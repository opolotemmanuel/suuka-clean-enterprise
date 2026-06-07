package com.suuka.cleaning.dashboard.service;

import com.suuka.cleaning.approvals.repository.ApprovalRequestRepository;
import com.suuka.cleaning.auth.security.SuukaPrincipal;
import com.suuka.cleaning.bookings.repository.BookingRepository;
import com.suuka.cleaning.common.enums.ApprovalStatus;
import com.suuka.cleaning.common.enums.BookingStatus;
import com.suuka.cleaning.common.enums.Role;
import com.suuka.cleaning.dashboard.dto.DashboardAction;
import com.suuka.cleaning.dashboard.dto.DashboardMetric;
import com.suuka.cleaning.dashboard.dto.DashboardSummary;
import com.suuka.cleaning.inventory.repository.InventoryItemRepository;
import com.suuka.cleaning.messages.repository.MessageRepository;
import com.suuka.cleaning.notifications.repository.NotificationRepository;
import com.suuka.cleaning.users.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DashboardService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final ApprovalRequestRepository approvalRequestRepository;
    private final NotificationRepository notificationRepository;
    private final MessageRepository messageRepository;

    public DashboardService(
            BookingRepository bookingRepository,
            UserRepository userRepository,
            InventoryItemRepository inventoryItemRepository,
            ApprovalRequestRepository approvalRequestRepository,
            NotificationRepository notificationRepository,
            MessageRepository messageRepository
    ) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.inventoryItemRepository = inventoryItemRepository;
        this.approvalRequestRepository = approvalRequestRepository;
        this.notificationRepository = notificationRepository;
        this.messageRepository = messageRepository;
    }

    public DashboardSummary summary(SuukaPrincipal principal) {
        Role role = principal.getRole();
        if (role == Role.CLIENT) {
            return clientSummary(principal);
        }
        if (role == Role.CLEANER) {
            return cleanerSummary(principal);
        }
        return adminSummary(role);
    }

    private DashboardSummary clientSummary(SuukaPrincipal principal) {
        long bookings = bookingRepository.countByClientId(principal.getId());
        long pending = bookingRepository.countByClientIdAndBookingStatus(principal.getId(), BookingStatus.PENDING);
        long completed = bookingRepository.countByClientIdAndBookingStatus(principal.getId(), BookingStatus.COMPLETED);
        long unreadNotifications = notificationRepository.countByUserIdAndReadFalse(principal.getId());
        long unreadMessages = messageRepository.countByRecipientIdAndReadFalse(principal.getId());
        List<String> emptyStates = new ArrayList<>();
        if (bookings == 0) {
            emptyStates.add("No bookings found. Create a booking to populate this dashboard.");
        }

        return new DashboardSummary(
                "CLIENT",
                List.of(
                        metric("activeBookings", "Active Bookings", bookings - completed, "fa-book-open"),
                        metric("pendingBookings", "Pending Bookings", pending, "fa-calendar-day"),
                        metric("completedBookings", "Completed Bookings", completed, "fa-circle-check"),
                        metric("notifications", "Unread Notifications", unreadNotifications, "fa-bell"),
                        metric("messages", "Unread Messages", unreadMessages, "fa-comments")
                ),
                List.of(
                        action("Create Booking", "OPEN_ROUTE", "BOOKINGS", "new-booking", "CREATE_BOOKING"),
                        action("View Booking History", "OPEN_ROUTE", "BOOKINGS", "my-bookings", "VIEW_OWN_BOOKINGS"),
                        action("Message Support", "OPEN_ROUTE", "MESSAGES", "support", "USE_CHATBOT")
                ),
                List.of(),
                emptyStates
        );
    }

    private DashboardSummary cleanerSummary(SuukaPrincipal principal) {
        long assigned = bookingRepository.countByCleanerId(principal.getId());
        long accepted = bookingRepository.countByCleanerIdAndBookingStatus(principal.getId(), BookingStatus.ACCEPTED);
        long active = bookingRepository.countByCleanerIdAndBookingStatus(principal.getId(), BookingStatus.IN_PROGRESS);
        long unreadNotifications = notificationRepository.countByUserIdAndReadFalse(principal.getId());
        long unreadMessages = messageRepository.countByRecipientIdAndReadFalse(principal.getId());
        List<String> emptyStates = new ArrayList<>();
        if (assigned == 0) {
            emptyStates.add("No assigned jobs found.");
        }

        return new DashboardSummary(
                "CLEANER",
                List.of(
                        metric("assignedJobs", "Assigned Jobs", assigned, "fa-broom"),
                        metric("acceptedJobs", "Accepted Jobs", accepted, "fa-calendar-check"),
                        metric("activeJobs", "Jobs In Progress", active, "fa-person-digging"),
                        metric("notifications", "Unread Notifications", unreadNotifications, "fa-bell"),
                        metric("messages", "Unread Messages", unreadMessages, "fa-comments")
                ),
                List.of(
                        action("View Jobs", "OPEN_ROUTE", "BOOKINGS", "jobs", "VIEW_ASSIGNED_JOBS"),
                        action("Request Supplies", "OPEN_ROUTE", "INVENTORY", "supply-requests", "VIEW_ASSIGNED_JOBS"),
                        action("Open Schedule", "OPEN_ROUTE", "WORKFORCE", "schedule", "VIEW_ASSIGNED_JOBS")
                ),
                List.of(),
                emptyStates
        );
    }

    private DashboardSummary adminSummary(Role role) {
        long activeClients = userRepository.countByRole(Role.CLIENT);
        long activeCleaners = userRepository.countByRole(Role.CLEANER);
        long openBookings = bookingRepository.countByBookingStatus(BookingStatus.PENDING)
                + bookingRepository.countByBookingStatus(BookingStatus.ASSIGNED)
                + bookingRepository.countByBookingStatus(BookingStatus.ACCEPTED)
                + bookingRepository.countByBookingStatus(BookingStatus.ARRIVED)
                + bookingRepository.countByBookingStatus(BookingStatus.IN_PROGRESS);
        long lowStock = inventoryItemRepository.findAll().stream()
                .filter(item -> item.getQuantity() <= item.getReorderLevel())
                .count();
        long approvals = approvalRequestRepository.findAll().stream()
                .filter(item -> item.getApprovalStatus() == ApprovalStatus.PENDING)
                .count();

        return new DashboardSummary(
                role.name(),
                List.of(
                        metric("activeClients", "Active Clients", activeClients, "fa-users"),
                        metric("activeCleaners", "Active Cleaners", activeCleaners, "fa-people-group"),
                        metric("openBookings", "Open Bookings", openBookings, "fa-calendar-check"),
                        metric("lowStock", "Low Stock Items", lowStock, "fa-boxes-stacked"),
                        metric("pendingApprovals", "Pending Approvals", approvals, "fa-list-check")
                ),
                List.of(
                        action("Review Bookings", "OPEN_ROUTE", "BOOKINGS", "admin-bookings", "MANAGE_BOOKINGS"),
                        action("Open Inventory", "OPEN_ROUTE", "INVENTORY", "inventory", "MANAGE_INVENTORY"),
                        action("Review Approvals", "OPEN_ROUTE", "APPROVALS", "approval-center", "REVIEW_APPROVAL_REQUESTS")
                ),
                List.of(),
                List.of()
        );
    }

    private DashboardMetric metric(String key, String label, long value, String icon) {
        return new DashboardMetric(key, label, String.valueOf(value), icon, null);
    }

    private DashboardAction action(String label, String actionType, String targetModule, String targetRoute, String requiredPermission) {
        return new DashboardAction(label, actionType, targetModule, targetRoute, requiredPermission);
    }
}
