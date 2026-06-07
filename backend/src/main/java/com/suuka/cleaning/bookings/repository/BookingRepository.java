package com.suuka.cleaning.bookings.repository;

import com.suuka.cleaning.bookings.entity.Booking;
import com.suuka.cleaning.common.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {
    List<Booking> findByClientId(UUID clientId);

    List<Booking> findByCleanerId(UUID cleanerId);

    long countByClientId(UUID clientId);

    long countByCleanerId(UUID cleanerId);

    long countByBookingStatus(BookingStatus bookingStatus);

    long countByClientIdAndBookingStatus(UUID clientId, BookingStatus bookingStatus);

    long countByCleanerIdAndBookingStatus(UUID cleanerId, BookingStatus bookingStatus);
}
