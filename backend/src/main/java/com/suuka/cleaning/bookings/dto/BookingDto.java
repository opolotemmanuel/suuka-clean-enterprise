package com.suuka.cleaning.bookings.dto;

import com.suuka.cleaning.bookings.entity.Booking;
import com.suuka.cleaning.common.enums.BookingStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record BookingDto(
        UUID id,
        UUID clientId,
        UUID cleanerId,
        String serviceType,
        String propertyAddress,
        LocalDateTime scheduledAt,
        int durationHours,
        BookingStatus status,
        String completionNotes
) {
    public static BookingDto from(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getClientId(),
                booking.getCleanerId(),
                booking.getServiceType(),
                booking.getPropertyAddress(),
                booking.getScheduledAt(),
                booking.getDurationHours(),
                booking.getBookingStatus(),
                booking.getCompletionNotes()
        );
    }
}
