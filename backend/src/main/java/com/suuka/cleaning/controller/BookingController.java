package com.suuka.cleaning.controller;

import com.suuka.cleaning.auth.security.SuukaPrincipal;
import com.suuka.cleaning.bookings.dto.AssignCleanerRequest;
import com.suuka.cleaning.bookings.dto.BookingDto;
import com.suuka.cleaning.bookings.dto.CompleteJobRequest;
import com.suuka.cleaning.bookings.dto.CreateBookingRequest;
import com.suuka.cleaning.bookings.service.BookingService;
import com.suuka.cleaning.common.enums.BookingStatus;
import com.suuka.cleaning.common.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/client/bookings")
    @PreAuthorize("hasAuthority('CREATE_BOOKING')")
    public ApiResponse<BookingDto> createBooking(@AuthenticationPrincipal SuukaPrincipal principal, @Valid @RequestBody CreateBookingRequest request) {
        return ApiResponse.success("Booking created", bookingService.create(principal.getId(), request));
    }

    @GetMapping("/client/bookings")
    @PreAuthorize("hasAuthority('VIEW_OWN_BOOKINGS')")
    public ApiResponse<List<BookingDto>> ownBookings(@AuthenticationPrincipal SuukaPrincipal principal) {
        return ApiResponse.success("Bookings loaded", bookingService.ownBookings(principal.getId()));
    }

    @GetMapping("/client/bookings/{bookingId}")
    @PreAuthorize("hasAuthority('VIEW_OWN_BOOKINGS')")
    public ApiResponse<BookingDto> ownBooking(@AuthenticationPrincipal SuukaPrincipal principal, @PathVariable UUID bookingId) {
        return ApiResponse.success("Booking loaded", bookingService.getOwnBooking(principal.getId(), bookingId));
    }

    @PostMapping("/client/bookings/{bookingId}/cancel")
    @PreAuthorize("hasAuthority('CANCEL_OWN_BOOKING')")
    public ApiResponse<BookingDto> cancelOwnBooking(@AuthenticationPrincipal SuukaPrincipal principal, @PathVariable UUID bookingId) {
        return ApiResponse.success("Booking cancelled", bookingService.cancelOwn(principal.getId(), bookingId));
    }

    @GetMapping("/cleaner/jobs")
    @PreAuthorize("hasAuthority('VIEW_ASSIGNED_JOBS')")
    public ApiResponse<List<BookingDto>> assignedJobs(@AuthenticationPrincipal SuukaPrincipal principal) {
        return ApiResponse.success("Assigned jobs loaded", bookingService.assignedJobs(principal.getId()));
    }

    @PostMapping("/cleaner/jobs/{bookingId}/accept")
    @PreAuthorize("hasAuthority('UPDATE_JOB_STATUS')")
    public ApiResponse<BookingDto> acceptJob(@AuthenticationPrincipal SuukaPrincipal principal, @PathVariable UUID bookingId) {
        return ApiResponse.success("Job accepted", bookingService.cleanerTransition(principal.getId(), bookingId, BookingStatus.ACCEPTED, null));
    }

    @PostMapping("/cleaner/jobs/{bookingId}/arrived")
    @PreAuthorize("hasAuthority('UPDATE_JOB_STATUS')")
    public ApiResponse<BookingDto> markArrived(@AuthenticationPrincipal SuukaPrincipal principal, @PathVariable UUID bookingId) {
        return ApiResponse.success("Arrival recorded", bookingService.cleanerTransition(principal.getId(), bookingId, BookingStatus.ARRIVED, null));
    }

    @PostMapping("/cleaner/jobs/{bookingId}/start")
    @PreAuthorize("hasAuthority('UPDATE_JOB_STATUS')")
    public ApiResponse<BookingDto> startJob(@AuthenticationPrincipal SuukaPrincipal principal, @PathVariable UUID bookingId) {
        return ApiResponse.success("Job started", bookingService.cleanerTransition(principal.getId(), bookingId, BookingStatus.IN_PROGRESS, null));
    }

    @PostMapping("/cleaner/jobs/{bookingId}/complete")
    @PreAuthorize("hasAuthority('UPDATE_JOB_STATUS')")
    public ApiResponse<BookingDto> completeJob(
            @AuthenticationPrincipal SuukaPrincipal principal,
            @PathVariable UUID bookingId,
            @Valid @RequestBody CompleteJobRequest request
    ) {
        return ApiResponse.success("Job completed", bookingService.cleanerTransition(principal.getId(), bookingId, BookingStatus.COMPLETED, request.completionNotes()));
    }

    @GetMapping("/admin/bookings")
    @PreAuthorize("hasAuthority('MANAGE_BOOKINGS')")
    public ApiResponse<List<BookingDto>> allBookings() {
        return ApiResponse.success("Bookings loaded", bookingService.allBookings());
    }

    @PostMapping("/admin/bookings/{bookingId}/assign-cleaner")
    @PreAuthorize("hasAuthority('MANAGE_BOOKINGS')")
    public ApiResponse<BookingDto> assignCleaner(@PathVariable UUID bookingId, @Valid @RequestBody AssignCleanerRequest request) {
        return ApiResponse.success("Cleaner assigned", bookingService.assignCleaner(bookingId, request.cleanerId()));
    }
}
