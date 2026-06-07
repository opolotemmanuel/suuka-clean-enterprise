package com.suuka.cleaning.bookings.service;

import com.suuka.cleaning.bookings.dto.*;
import com.suuka.cleaning.bookings.entity.Booking;
import com.suuka.cleaning.bookings.repository.BookingRepository;
import com.suuka.cleaning.common.enums.BookingStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;

    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Transactional
    public BookingDto create(UUID clientId, CreateBookingRequest request) {
        Booking booking = new Booking();
        booking.setClientId(clientId);
        booking.setServiceType(request.serviceType());
        booking.setPropertyAddress(request.propertyAddress());
        booking.setLatitude(request.latitude());
        booking.setLongitude(request.longitude());
        booking.setScheduledAt(request.scheduledAt());
        booking.setDurationHours(request.durationHours());
        booking.setPaymentMethod(request.paymentMethod());
        booking.setBookingStatus(BookingStatus.PENDING);
        return BookingDto.from(bookingRepository.save(booking));
    }

    public BookingDto getOwnBooking(UUID clientId, UUID bookingId) {
        Booking booking = booking(bookingId);
        requireOwner(booking.getClientId(), clientId, "Booking does not belong to this client");
        return BookingDto.from(booking);
    }

    public List<BookingDto> ownBookings(UUID clientId) {
        return bookingRepository.findByClientId(clientId).stream().map(BookingDto::from).toList();
    }

    public List<BookingDto> assignedJobs(UUID cleanerId) {
        return bookingRepository.findByCleanerId(cleanerId).stream().map(BookingDto::from).toList();
    }

    public BookingDto assignedJob(UUID cleanerId, UUID bookingId) {
        Booking booking = booking(bookingId);
        requireOwner(booking.getCleanerId(), cleanerId, "Job is not assigned to this cleaner");
        return BookingDto.from(booking);
    }

    public List<BookingDto> allBookings() {
        return bookingRepository.findAll().stream().map(BookingDto::from).toList();
    }

    @Transactional
    public BookingDto cancelOwn(UUID clientId, UUID bookingId) {
        Booking booking = booking(bookingId);
        requireOwner(booking.getClientId(), clientId, "Booking does not belong to this client");
        if (booking.getScheduledAt() != null && booking.getScheduledAt().minusHours(2).isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Booking can no longer be cancelled by the client");
        }
        booking.setBookingStatus(BookingStatus.CANCELLED);
        return BookingDto.from(booking);
    }

    @Transactional
    public BookingDto assignCleaner(UUID bookingId, UUID cleanerId) {
        Booking booking = booking(bookingId);
        booking.setCleanerId(cleanerId);
        booking.setBookingStatus(BookingStatus.ASSIGNED);
        return BookingDto.from(booking);
    }

    @Transactional
    public BookingDto cleanerTransition(UUID cleanerId, UUID bookingId, BookingStatus targetStatus, String notes) {
        Booking booking = booking(bookingId);
        requireOwner(booking.getCleanerId(), cleanerId, "Job is not assigned to this cleaner");
        booking.setBookingStatus(targetStatus);
        if (notes != null) {
            booking.setCompletionNotes(notes);
        }
        return BookingDto.from(booking);
    }

    private Booking booking(UUID id) {
        return bookingRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Booking not found"));
    }

    private void requireOwner(UUID actual, UUID expected, String message) {
        if (actual == null || !actual.equals(expected)) {
            throw new IllegalArgumentException(message);
        }
    }
}
