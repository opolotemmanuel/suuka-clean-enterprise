package com.suuka.cleaning.roles.controller;

import com.suuka.cleaning.ai.repository.AIRecommendationRepository;
import com.suuka.cleaning.approvals.dto.CreateApprovalRequest;
import com.suuka.cleaning.approvals.entity.ApprovalRequest;
import com.suuka.cleaning.approvals.service.ApprovalService;
import com.suuka.cleaning.audit.service.AuditService;
import com.suuka.cleaning.auth.security.SuukaPrincipal;
import com.suuka.cleaning.bookings.dto.AssignCleanerRequest;
import com.suuka.cleaning.bookings.dto.BookingDto;
import com.suuka.cleaning.bookings.dto.CreateBookingRequest;
import com.suuka.cleaning.bookings.service.BookingService;
import com.suuka.cleaning.common.enums.ApprovalStatus;
import com.suuka.cleaning.common.enums.ApprovalType;
import com.suuka.cleaning.common.enums.BookingStatus;
import com.suuka.cleaning.common.enums.Role;
import com.suuka.cleaning.common.response.ApiResponse;
import com.suuka.cleaning.inventory.dto.InventoryItemRequest;
import com.suuka.cleaning.inventory.dto.SupplyRequestDto;
import com.suuka.cleaning.inventory.entity.InventoryItem;
import com.suuka.cleaning.inventory.entity.SupplyRequest;
import com.suuka.cleaning.inventory.repository.InventoryItemRepository;
import com.suuka.cleaning.inventory.repository.SupplyRequestRepository;
import com.suuka.cleaning.notifications.repository.NotificationRepository;
import com.suuka.cleaning.platform.entity.BusinessRecord;
import com.suuka.cleaning.platform.entity.TaskRecord;
import com.suuka.cleaning.platform.enums.PlatformModule;
import com.suuka.cleaning.platform.repository.ActivityTimelineEventRepository;
import com.suuka.cleaning.platform.repository.BusinessRecordRepository;
import com.suuka.cleaning.platform.repository.TaskRecordRepository;
import com.suuka.cleaning.suppliers.entity.PurchaseOrder;
import com.suuka.cleaning.suppliers.entity.Supplier;
import com.suuka.cleaning.suppliers.repository.PurchaseOrderRepository;
import com.suuka.cleaning.suppliers.repository.SupplierRepository;
import com.suuka.cleaning.users.dto.UserSummary;
import com.suuka.cleaning.users.entity.User;
import com.suuka.cleaning.users.repository.UserRepository;
import com.suuka.cleaning.users.service.PermissionService;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api")
public class RoleApiController {
    private final BookingService bookingService;
    private final UserRepository userRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final SupplyRequestRepository supplyRequestRepository;
    private final SupplierRepository supplierRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final BusinessRecordRepository businessRecordRepository;
    private final TaskRecordRepository taskRecordRepository;
    private final ActivityTimelineEventRepository timelineRepository;
    private final NotificationRepository notificationRepository;
    private final AIRecommendationRepository aiRecommendationRepository;
    private final ApprovalService approvalService;
    private final AuditService auditService;
    private final PermissionService permissionService;
    private final PasswordEncoder passwordEncoder;

    public RoleApiController(
            BookingService bookingService,
            UserRepository userRepository,
            InventoryItemRepository inventoryItemRepository,
            SupplyRequestRepository supplyRequestRepository,
            SupplierRepository supplierRepository,
            PurchaseOrderRepository purchaseOrderRepository,
            BusinessRecordRepository businessRecordRepository,
            TaskRecordRepository taskRecordRepository,
            ActivityTimelineEventRepository timelineRepository,
            NotificationRepository notificationRepository,
            AIRecommendationRepository aiRecommendationRepository,
            ApprovalService approvalService,
            AuditService auditService,
            PermissionService permissionService,
            PasswordEncoder passwordEncoder
    ) {
        this.bookingService = bookingService;
        this.userRepository = userRepository;
        this.inventoryItemRepository = inventoryItemRepository;
        this.supplyRequestRepository = supplyRequestRepository;
        this.supplierRepository = supplierRepository;
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.businessRecordRepository = businessRecordRepository;
        this.taskRecordRepository = taskRecordRepository;
        this.timelineRepository = timelineRepository;
        this.notificationRepository = notificationRepository;
        this.aiRecommendationRepository = aiRecommendationRepository;
        this.approvalService = approvalService;
        this.auditService = auditService;
        this.permissionService = permissionService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/client/dashboard")
    @PreAuthorize("hasAuthority('VIEW_OWN_BOOKINGS')")
    public ApiResponse<Map<String, Object>> clientDashboard(@AuthenticationPrincipal SuukaPrincipal principal) {
        return dashboard(principal, "Client dashboard loaded", List.of("Book Cleaning", "View Bookings", "Message Support"));
    }

    @GetMapping("/cleaner/dashboard")
    @PreAuthorize("hasAuthority('VIEW_ASSIGNED_JOBS')")
    public ApiResponse<Map<String, Object>> cleanerDashboard(@AuthenticationPrincipal SuukaPrincipal principal) {
        return dashboard(principal, "Cleaner dashboard loaded", List.of("View Jobs", "Open Schedule", "Request Supplies"));
    }

    @GetMapping("/supervisor/dashboard")
    @PreAuthorize("hasAuthority('MANAGE_QUALITY')")
    public ApiResponse<Map<String, Object>> supervisorDashboard(@AuthenticationPrincipal SuukaPrincipal principal) {
        return dashboard(principal, "Supervisor dashboard loaded", List.of("Team Performance", "Quality Checks", "Review Complaints"));
    }

    @GetMapping("/operations/dashboard")
    @PreAuthorize("hasAuthority('MANAGE_BOOKINGS')")
    public ApiResponse<Map<String, Object>> operationsDashboard(@AuthenticationPrincipal SuukaPrincipal principal) {
        return dashboard(principal, "Operations dashboard loaded", List.of("Dispatch Board", "Assign Cleaner", "Manual Booking"));
    }

    @GetMapping("/customer-success/dashboard")
    @PreAuthorize("hasAuthority('MANAGE_CRM')")
    public ApiResponse<Map<String, Object>> customerSuccessDashboard(@AuthenticationPrincipal SuukaPrincipal principal) {
        return dashboard(principal, "Customer success dashboard loaded", List.of("CRM", "At-risk Clients", "Follow Ups"));
    }

    @GetMapping("/hr/dashboard")
    @PreAuthorize("hasAuthority('MANAGE_WORKFORCE')")
    public ApiResponse<Map<String, Object>> hrDashboard(@AuthenticationPrincipal SuukaPrincipal principal) {
        return dashboard(principal, "HR dashboard loaded", List.of("Cleaners", "Attendance", "Training"));
    }

    @GetMapping("/finance/dashboard")
    @PreAuthorize("hasAuthority('VIEW_FINANCE')")
    public ApiResponse<Map<String, Object>> financeDashboard(@AuthenticationPrincipal SuukaPrincipal principal) {
        return dashboard(principal, "Finance dashboard loaded", List.of("Revenue", "Invoices", "Refunds"));
    }

    @GetMapping("/inventory-manager/dashboard")
    @PreAuthorize("hasAuthority('MANAGE_INVENTORY')")
    public ApiResponse<Map<String, Object>> inventoryDashboard(@AuthenticationPrincipal SuukaPrincipal principal) {
        return dashboard(principal, "Inventory manager dashboard loaded", List.of("Items", "Low Stock", "Purchase Orders"));
    }

    @GetMapping("/system-admin/dashboard")
    @PreAuthorize("hasAuthority('MANAGE_SYSTEM_SETTINGS')")
    public ApiResponse<Map<String, Object>> systemAdminDashboard(@AuthenticationPrincipal SuukaPrincipal principal) {
        return dashboard(principal, "System admin dashboard loaded", List.of("Users", "Roles", "Security Logs"));
    }

    @GetMapping("/executive/dashboard")
    @PreAuthorize("hasAuthority('VIEW_EXECUTIVE_BI')")
    public ApiResponse<Map<String, Object>> executiveDashboard(@AuthenticationPrincipal SuukaPrincipal principal) {
        return dashboard(principal, "Executive dashboard loaded", List.of("Command Center", "Business Intelligence", "Approval Queue"));
    }

    @GetMapping("/cleaner/jobs/{id}")
    @PreAuthorize("hasAuthority('VIEW_ASSIGNED_JOBS')")
    public ApiResponse<BookingDto> cleanerJob(@AuthenticationPrincipal SuukaPrincipal principal, @PathVariable UUID id) {
        return ApiResponse.success("Job loaded", bookingService.assignedJob(principal.getId(), id));
    }

    @PostMapping("/cleaner/jobs/{id}/upload-proof")
    @PreAuthorize("hasAuthority('UPDATE_JOB_STATUS')")
    public ApiResponse<BusinessRecord> uploadProof(@AuthenticationPrincipal SuukaPrincipal principal, @PathVariable UUID id, @RequestBody Map<String, Object> request) {
        return ApiResponse.success("Proof uploaded", record(principal, PlatformModule.QUALITY, "Job proof " + id, stringify(request), id.toString()));
    }

    @GetMapping("/cleaner/schedule")
    @PreAuthorize("hasAuthority('VIEW_ASSIGNED_JOBS')")
    public ApiResponse<List<BookingDto>> cleanerSchedule(@AuthenticationPrincipal SuukaPrincipal principal) {
        return ApiResponse.success("Schedule loaded", bookingService.assignedJobs(principal.getId()));
    }

    @GetMapping("/cleaner/earnings")
    @PreAuthorize("hasAuthority('VIEW_ASSIGNED_JOBS')")
    public ApiResponse<Map<String, Object>> cleanerEarnings(@AuthenticationPrincipal SuukaPrincipal principal) {
        long completed = bookingService.assignedJobs(principal.getId()).stream().filter(job -> job.status() == BookingStatus.COMPLETED).count();
        return ApiResponse.success("Earnings loaded", Map.of("completedJobs", completed, "estimatedEarnings", completed * 25000));
    }

    @PostMapping("/cleaner/supply-requests")
    @PreAuthorize("hasAuthority('VIEW_ASSIGNED_JOBS')")
    public ApiResponse<SupplyRequest> cleanerSupplyRequest(@AuthenticationPrincipal SuukaPrincipal principal, @Valid @RequestBody SupplyRequestDto request) {
        SupplyRequest supplyRequest = new SupplyRequest();
        supplyRequest.setCleanerId(principal.getId());
        supplyRequest.setInventoryItemId(request.inventoryItemId());
        supplyRequest.setQuantity(request.quantity());
        supplyRequest.setReason(request.reason());
        audit(principal, "inventory", "SUPPLY_REQUEST_CREATED", request.reason());
        return ApiResponse.success("Supply request created", supplyRequestRepository.save(supplyRequest));
    }

    @GetMapping("/cleaner/training")
    @PreAuthorize("hasAuthority('VIEW_ASSIGNED_JOBS')")
    public ApiResponse<List<BusinessRecord>> cleanerTraining() {
        return records(PlatformModule.WORKFORCE, "Training loaded");
    }

    @GetMapping("/cleaner/performance")
    @PreAuthorize("hasAuthority('VIEW_ASSIGNED_JOBS')")
    public ApiResponse<List<BusinessRecord>> cleanerPerformance() {
        return records(PlatformModule.QUALITY, "Performance loaded");
    }

    @GetMapping("/supervisor/team")
    @PreAuthorize("hasAuthority('MANAGE_QUALITY')")
    public ApiResponse<List<UserSummary>> supervisorTeam() {
        return ApiResponse.success("Team loaded", userRepository.findAll().stream().filter(user -> user.getRole() == Role.CLEANER).map(UserSummary::from).toList());
    }

    @GetMapping("/supervisor/team/performance")
    @PreAuthorize("hasAuthority('MANAGE_QUALITY')")
    public ApiResponse<List<BusinessRecord>> supervisorTeamPerformance() {
        return records(PlatformModule.QUALITY, "Team performance loaded");
    }

    @GetMapping("/supervisor/quality-checks")
    @PreAuthorize("hasAuthority('MANAGE_QUALITY')")
    public ApiResponse<List<BusinessRecord>> qualityChecks() {
        return records(PlatformModule.QUALITY, "Quality checks loaded");
    }

    @PostMapping("/supervisor/quality-checks")
    @PreAuthorize("hasAuthority('MANAGE_QUALITY')")
    public ApiResponse<BusinessRecord> createQualityCheck(@AuthenticationPrincipal SuukaPrincipal principal, @RequestBody Map<String, Object> request) {
        return ApiResponse.success("Quality check created", record(principal, PlatformModule.QUALITY, title(request, "Quality check"), stringify(request), relatedId(request)));
    }

    @GetMapping("/supervisor/complaints")
    @PreAuthorize("hasAuthority('MANAGE_COMPLAINTS')")
    public ApiResponse<List<BusinessRecord>> supervisorComplaints() {
        return records(PlatformModule.COMPLAINTS, "Complaints loaded");
    }

    @PostMapping("/supervisor/complaints/{id}/investigate")
    @PreAuthorize("hasAuthority('MANAGE_COMPLAINTS')")
    public ApiResponse<BusinessRecord> investigateComplaint(@AuthenticationPrincipal SuukaPrincipal principal, @PathVariable String id, @RequestBody(required = false) Map<String, Object> request) {
        return ApiResponse.success("Complaint investigation recorded", record(principal, PlatformModule.COMPLAINTS, "Complaint investigation", stringify(request), id));
    }

    @GetMapping("/supervisor/supply-requests")
    @PreAuthorize("hasAuthority('MANAGE_QUALITY')")
    public ApiResponse<List<SupplyRequest>> supervisorSupplyRequests() {
        return ApiResponse.success("Supply requests loaded", supplyRequestRepository.findAll());
    }

    @PostMapping("/supervisor/supply-requests/{id}/review")
    @PreAuthorize("hasAuthority('MANAGE_QUALITY')")
    public ApiResponse<SupplyRequest> supervisorReviewSupply(@AuthenticationPrincipal SuukaPrincipal principal, @PathVariable UUID id, @RequestBody(required = false) Map<String, Object> request) {
        SupplyRequest supplyRequest = supplyRequestRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Supply request not found"));
        supplyRequest.setApproved(Boolean.parseBoolean(String.valueOf(value(request, "approved", true))));
        audit(principal, "inventory", "SUPPLY_REQUEST_REVIEWED", id.toString());
        return ApiResponse.success("Supply request reviewed", supplyRequestRepository.save(supplyRequest));
    }

    @GetMapping("/supervisor/territory")
    @PreAuthorize("hasAuthority('MANAGE_QUALITY')")
    public ApiResponse<List<BusinessRecord>> supervisorTerritory() {
        return records(PlatformModule.TERRITORY, "Territory loaded");
    }

    @GetMapping("/operations/dispatch-board")
    @PreAuthorize("hasAuthority('MANAGE_BOOKINGS')")
    public ApiResponse<List<BookingDto>> dispatchBoard() {
        return ApiResponse.success("Dispatch board loaded", bookingService.allBookings());
    }

    @PostMapping({"/operations/bookings/{id}/assign-cleaner", "/operations/bookings/{id}/reassign-cleaner"})
    @PreAuthorize("hasAuthority('MANAGE_BOOKINGS')")
    public ApiResponse<BookingDto> operationsAssignCleaner(@AuthenticationPrincipal SuukaPrincipal principal, @PathVariable UUID id, @Valid @RequestBody AssignCleanerRequest request) {
        audit(principal, "bookings", "CLEANER_ASSIGNED", id.toString());
        return ApiResponse.success("Cleaner assigned", bookingService.assignCleaner(id, request.cleanerId()));
    }

    @GetMapping("/operations/schedules")
    @PreAuthorize("hasAuthority('MANAGE_BOOKINGS')")
    public ApiResponse<List<BookingDto>> operationsSchedules() {
        return ApiResponse.success("Schedules loaded", bookingService.allBookings());
    }

    @GetMapping("/operations/routes")
    @PreAuthorize("hasAuthority('MANAGE_BOOKINGS')")
    public ApiResponse<List<BusinessRecord>> operationsRoutes() {
        return records(PlatformModule.TERRITORY, "Routes loaded");
    }

    @GetMapping("/operations/active-jobs")
    @PreAuthorize("hasAuthority('MANAGE_BOOKINGS')")
    public ApiResponse<List<BookingDto>> activeJobs() {
        return ApiResponse.success("Active jobs loaded", bookingService.allBookings().stream().filter(job -> job.status() != BookingStatus.COMPLETED && job.status() != BookingStatus.CANCELLED).toList());
    }

    @GetMapping("/operations/service-gaps")
    @PreAuthorize("hasAuthority('MANAGE_BOOKINGS')")
    public ApiResponse<List<BusinessRecord>> serviceGaps() {
        return records(PlatformModule.TERRITORY, "Service gaps loaded");
    }

    @PostMapping("/operations/manual-bookings")
    @PreAuthorize("hasAuthority('MANAGE_BOOKINGS')")
    public ApiResponse<BookingDto> manualBooking(@RequestBody CreateBookingRequest request) {
        UUID clientId = userRepository.findAll().stream().filter(user -> user.getRole() == Role.CLIENT).findFirst().map(User::getId).orElseThrow(() -> new IllegalStateException("No client exists for manual booking"));
        return ApiResponse.success("Manual booking created", bookingService.create(clientId, request));
    }

    @GetMapping("/customer-success/crm")
    @PreAuthorize("hasAuthority('MANAGE_CRM')")
    public ApiResponse<List<BusinessRecord>> crm() {
        return records(PlatformModule.CRM, "CRM loaded");
    }

    @GetMapping("/customer-success/clients/at-risk")
    @PreAuthorize("hasAuthority('MANAGE_CRM')")
    public ApiResponse<List<BusinessRecord>> atRiskClients() {
        return records(PlatformModule.CLIENTS, "At-risk clients loaded");
    }

    @GetMapping("/customer-success/clients/inactive")
    @PreAuthorize("hasAuthority('MANAGE_CRM')")
    public ApiResponse<List<BusinessRecord>> inactiveClients() {
        return records(PlatformModule.CLIENTS, "Inactive clients loaded");
    }

    @GetMapping("/customer-success/follow-ups")
    @PreAuthorize("hasAuthority('MANAGE_CRM')")
    public ApiResponse<List<TaskRecord>> followUps() {
        return ApiResponse.success("Follow-ups loaded", taskRecordRepository.findAll().stream().filter(task -> task.getRelatedModule() == PlatformModule.CRM).toList());
    }

    @PostMapping("/customer-success/follow-ups")
    @PreAuthorize("hasAuthority('MANAGE_CRM')")
    public ApiResponse<BusinessRecord> createFollowUp(@AuthenticationPrincipal SuukaPrincipal principal, @RequestBody Map<String, Object> request) {
        return ApiResponse.success("Follow-up created", record(principal, PlatformModule.CRM, title(request, "Follow-up"), stringify(request), relatedId(request)));
    }

    @GetMapping("/customer-success/complaints")
    @PreAuthorize("hasAuthority('MANAGE_COMPLAINTS')")
    public ApiResponse<List<BusinessRecord>> customerComplaints() {
        return records(PlatformModule.COMPLAINTS, "Complaints loaded");
    }

    @PostMapping("/customer-success/complaints/{id}/respond")
    @PreAuthorize("hasAuthority('MANAGE_COMPLAINTS')")
    public ApiResponse<BusinessRecord> respondComplaint(@AuthenticationPrincipal SuukaPrincipal principal, @PathVariable String id, @RequestBody(required = false) Map<String, Object> request) {
        return ApiResponse.success("Complaint response recorded", record(principal, PlatformModule.COMPLAINTS, "Complaint response", stringify(request), id));
    }

    @GetMapping("/customer-success/campaigns")
    @PreAuthorize("hasAuthority('MANAGE_CRM')")
    public ApiResponse<List<BusinessRecord>> campaigns() {
        return records(PlatformModule.MARKETING, "Campaigns loaded");
    }

    @PostMapping("/customer-success/campaigns/request-approval")
    @PreAuthorize("hasAuthority('MANAGE_CRM')")
    public ApiResponse<ApprovalRequest> campaignApproval(@AuthenticationPrincipal SuukaPrincipal principal, @RequestBody Map<String, Object> request) {
        return createApproval(principal, ApprovalType.CAMPAIGN_APPROVAL, title(request, "Campaign approval"), stringify(request));
    }

    @GetMapping("/hr/cleaners")
    @PreAuthorize("hasAuthority('MANAGE_WORKFORCE')")
    public ApiResponse<List<UserSummary>> hrCleaners() {
        return ApiResponse.success("Cleaners loaded", userRepository.findAll().stream().filter(user -> user.getRole() == Role.CLEANER).map(UserSummary::from).toList());
    }

    @GetMapping("/hr/cleaners/{id}")
    @PreAuthorize("hasAuthority('MANAGE_WORKFORCE')")
    public ApiResponse<UserSummary> hrCleaner(@PathVariable UUID id) {
        return ApiResponse.success("Cleaner loaded", UserSummary.from(userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Cleaner not found"))));
    }

    @GetMapping("/hr/attendance")
    @PreAuthorize("hasAuthority('MANAGE_WORKFORCE')")
    public ApiResponse<List<BusinessRecord>> attendance() {
        return records(PlatformModule.WORKFORCE, "Attendance loaded");
    }

    @PostMapping("/hr/attendance")
    @PreAuthorize("hasAuthority('MANAGE_WORKFORCE')")
    public ApiResponse<BusinessRecord> createAttendance(@AuthenticationPrincipal SuukaPrincipal principal, @RequestBody Map<String, Object> request) {
        return ApiResponse.success("Attendance recorded", record(principal, PlatformModule.WORKFORCE, title(request, "Attendance"), stringify(request), relatedId(request)));
    }

    @GetMapping("/hr/leave-requests")
    @PreAuthorize("hasAuthority('MANAGE_WORKFORCE')")
    public ApiResponse<List<BusinessRecord>> leaveRequests() {
        return records(PlatformModule.WORKFORCE, "Leave requests loaded");
    }

    @PostMapping({"/hr/leave-requests/{id}/approve", "/hr/leave-requests/{id}/reject"})
    @PreAuthorize("hasAuthority('MANAGE_WORKFORCE')")
    public ApiResponse<BusinessRecord> reviewLeave(@AuthenticationPrincipal SuukaPrincipal principal, @PathVariable String id) {
        return ApiResponse.success("Leave request reviewed", record(principal, PlatformModule.WORKFORCE, "Leave review", "Reviewed by HR", id));
    }

    @GetMapping("/hr/training")
    @PreAuthorize("hasAuthority('MANAGE_WORKFORCE')")
    public ApiResponse<List<BusinessRecord>> hrTraining() {
        return records(PlatformModule.WORKFORCE, "Training loaded");
    }

    @PostMapping("/hr/training")
    @PreAuthorize("hasAuthority('MANAGE_WORKFORCE')")
    public ApiResponse<BusinessRecord> createTraining(@AuthenticationPrincipal SuukaPrincipal principal, @RequestBody Map<String, Object> request) {
        return ApiResponse.success("Training created", record(principal, PlatformModule.WORKFORCE, title(request, "Training"), stringify(request), relatedId(request)));
    }

    @GetMapping("/hr/certifications")
    @PreAuthorize("hasAuthority('MANAGE_WORKFORCE')")
    public ApiResponse<List<BusinessRecord>> certifications() {
        return records(PlatformModule.DOCUMENTS, "Certifications loaded");
    }

    @PostMapping("/hr/performance-plans")
    @PreAuthorize("hasAuthority('MANAGE_WORKFORCE')")
    public ApiResponse<BusinessRecord> performancePlan(@AuthenticationPrincipal SuukaPrincipal principal, @RequestBody Map<String, Object> request) {
        return ApiResponse.success("Performance plan created", record(principal, PlatformModule.WORKFORCE, title(request, "Performance plan"), stringify(request), relatedId(request)));
    }

    @PostMapping("/hr/promotions/request-approval")
    @PreAuthorize("hasAuthority('MANAGE_WORKFORCE')")
    public ApiResponse<ApprovalRequest> promotionApproval(@AuthenticationPrincipal SuukaPrincipal principal, @RequestBody Map<String, Object> request) {
        return createApproval(principal, ApprovalType.PAYROLL_APPROVAL, title(request, "Promotion approval"), stringify(request));
    }

    @GetMapping("/finance/summary")
    @PreAuthorize("hasAuthority('VIEW_FINANCE')")
    public ApiResponse<Map<String, Object>> financeSummary() {
        return ApiResponse.success("Finance summary loaded", Map.of("invoices", businessRecordRepository.findByModuleOrderByCreatedAtDesc(PlatformModule.INVOICES).size(), "payments", businessRecordRepository.findByModuleOrderByCreatedAtDesc(PlatformModule.PAYMENTS).size(), "refunds", businessRecordRepository.findByModuleOrderByCreatedAtDesc(PlatformModule.WALLET).size()));
    }

    @GetMapping({"/finance/revenue", "/finance/invoices", "/finance/payments", "/finance/refunds", "/finance/expenses", "/finance/payroll", "/finance/profit-loss", "/finance/cash-flow"})
    @PreAuthorize("hasAuthority('VIEW_FINANCE')")
    public ApiResponse<List<BusinessRecord>> financeRecords(HttpServletRequest request) {
        return records(financeModule(request.getRequestURI()), "Finance records loaded");
    }

    @PostMapping("/finance/refunds/{id}/review")
    @PreAuthorize("hasAuthority('APPROVE_REFUNDS')")
    public ApiResponse<BusinessRecord> reviewRefund(@AuthenticationPrincipal SuukaPrincipal principal, @PathVariable String id, @RequestBody(required = false) Map<String, Object> request) {
        return ApiResponse.success("Refund reviewed", record(principal, PlatformModule.WALLET, "Refund review", stringify(request), id));
    }

    @PostMapping("/finance/expenses")
    @PreAuthorize("hasAuthority('VIEW_FINANCE')")
    public ApiResponse<BusinessRecord> createExpense(@AuthenticationPrincipal SuukaPrincipal principal, @RequestBody Map<String, Object> request) {
        return ApiResponse.success("Expense created", record(principal, PlatformModule.PAYMENTS, title(request, "Expense"), stringify(request), relatedId(request)));
    }

    @PostMapping("/finance/payroll/request-approval")
    @PreAuthorize("hasAuthority('VIEW_FINANCE')")
    public ApiResponse<ApprovalRequest> payrollApproval(@AuthenticationPrincipal SuukaPrincipal principal, @RequestBody Map<String, Object> request) {
        return createApproval(principal, ApprovalType.PAYROLL_APPROVAL, title(request, "Payroll approval"), stringify(request));
    }

    @GetMapping("/inventory-manager/summary")
    @PreAuthorize("hasAuthority('MANAGE_INVENTORY')")
    public ApiResponse<Map<String, Object>> inventorySummary() {
        long lowStock = inventoryItemRepository.findAll().stream().filter(item -> item.getQuantity() <= item.getReorderLevel()).count();
        return ApiResponse.success("Inventory summary loaded", Map.of("items", inventoryItemRepository.count(), "lowStock", lowStock, "supplyRequests", supplyRequestRepository.count(), "suppliers", supplierRepository.count()));
    }

    @GetMapping("/inventory-manager/items")
    @PreAuthorize("hasAuthority('MANAGE_INVENTORY')")
    public ApiResponse<List<InventoryItem>> inventoryItems() {
        return ApiResponse.success("Inventory items loaded", inventoryItemRepository.findAll());
    }

    @PostMapping("/inventory-manager/items")
    @PreAuthorize("hasAuthority('MANAGE_INVENTORY')")
    public ApiResponse<InventoryItem> createInventoryItem(@AuthenticationPrincipal SuukaPrincipal principal, @Valid @RequestBody InventoryItemRequest request) {
        InventoryItem item = inventoryItem(request, new InventoryItem());
        audit(principal, "inventory", "ITEM_CREATED", item.getName());
        return ApiResponse.success("Inventory item created", inventoryItemRepository.save(item));
    }

    @PutMapping("/inventory-manager/items/{id}")
    @PreAuthorize("hasAuthority('MANAGE_INVENTORY')")
    public ApiResponse<InventoryItem> updateInventoryItem(@AuthenticationPrincipal SuukaPrincipal principal, @PathVariable UUID id, @Valid @RequestBody InventoryItemRequest request) {
        InventoryItem item = inventoryItemRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Inventory item not found"));
        audit(principal, "inventory", "ITEM_UPDATED", id.toString());
        return ApiResponse.success("Inventory item updated", inventoryItemRepository.save(inventoryItem(request, item)));
    }

    @GetMapping("/inventory-manager/low-stock")
    @PreAuthorize("hasAuthority('MANAGE_INVENTORY')")
    public ApiResponse<List<InventoryItem>> lowStock() {
        return ApiResponse.success("Low stock loaded", inventoryItemRepository.findAll().stream().filter(item -> item.getQuantity() <= item.getReorderLevel()).toList());
    }

    @GetMapping("/inventory-manager/supply-requests")
    @PreAuthorize("hasAuthority('MANAGE_INVENTORY')")
    public ApiResponse<List<SupplyRequest>> inventorySupplyRequests() {
        return ApiResponse.success("Supply requests loaded", supplyRequestRepository.findAll());
    }

    @PostMapping("/inventory-manager/supply-requests/{id}/approve")
    @PreAuthorize("hasAuthority('APPROVE_PURCHASES')")
    public ApiResponse<SupplyRequest> approveInventorySupply(@AuthenticationPrincipal SuukaPrincipal principal, @PathVariable UUID id) {
        SupplyRequest request = supplyRequestRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Supply request not found"));
        request.setApproved(true);
        audit(principal, "inventory", "SUPPLY_REQUEST_APPROVED", id.toString());
        return ApiResponse.success("Supply request approved", supplyRequestRepository.save(request));
    }

    @GetMapping("/inventory-manager/purchase-orders")
    @PreAuthorize("hasAuthority('APPROVE_PURCHASES')")
    public ApiResponse<List<PurchaseOrder>> purchaseOrders() {
        return ApiResponse.success("Purchase orders loaded", purchaseOrderRepository.findAll());
    }

    @PostMapping("/inventory-manager/purchase-orders/request-approval")
    @PreAuthorize("hasAuthority('APPROVE_PURCHASES')")
    public ApiResponse<ApprovalRequest> purchaseOrderApproval(@AuthenticationPrincipal SuukaPrincipal principal, @RequestBody Map<String, Object> request) {
        return createApproval(principal, ApprovalType.PURCHASE_APPROVAL, title(request, "Purchase order approval"), stringify(request));
    }

    @GetMapping("/inventory-manager/suppliers")
    @PreAuthorize("hasAuthority('MANAGE_SUPPLIERS')")
    public ApiResponse<List<Supplier>> inventorySuppliers() {
        return ApiResponse.success("Suppliers loaded", supplierRepository.findAll());
    }

    @PostMapping("/inventory-manager/suppliers")
    @PreAuthorize("hasAuthority('MANAGE_SUPPLIERS')")
    public ApiResponse<Supplier> createInventorySupplier(@AuthenticationPrincipal SuukaPrincipal principal, @RequestBody Map<String, Object> request) {
        Supplier supplier = new Supplier();
        supplier.setName(String.valueOf(value(request, "name", "Supplier")));
        supplier.setContactEmail(String.valueOf(value(request, "contactEmail", "")));
        supplier.setPhone(String.valueOf(value(request, "phone", "")));
        supplier.setRating(Integer.parseInt(String.valueOf(value(request, "rating", 0))));
        audit(principal, "suppliers", "SUPPLIER_CREATED", supplier.getName());
        return ApiResponse.success("Supplier created", supplierRepository.save(supplier));
    }

    @GetMapping("/system-admin/users")
    @PreAuthorize("hasAuthority('MANAGE_SYSTEM_SETTINGS')")
    public ApiResponse<List<UserSummary>> systemUsers() {
        return ApiResponse.success("Users loaded", userRepository.findAll().stream().map(UserSummary::from).toList());
    }

    @PostMapping("/system-admin/users")
    @PreAuthorize("hasAuthority('MANAGE_SYSTEM_SETTINGS')")
    @Transactional
    public ApiResponse<UserSummary> createSystemUser(@AuthenticationPrincipal SuukaPrincipal principal, @RequestBody Map<String, Object> request) {
        String email = String.valueOf(value(request, "email", "")).toLowerCase();
        if (email.isBlank() || userRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("A unique email is required");
        }
        Role role = Role.valueOf(String.valueOf(value(request, "role", Role.CLIENT.name())));
        User user = new User();
        user.setFullName(String.valueOf(value(request, "fullName", "New User")));
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(String.valueOf(value(request, "password", "Password123!"))));
        user.setRole(role);
        user.setPermissions(permissionService.permissionsFor(role));
        user.setBranch(String.valueOf(value(request, "branch", "")));
        user.setZone(String.valueOf(value(request, "zone", "")));
        audit(principal, "users", "USER_CREATED", email);
        return ApiResponse.success("User created", UserSummary.from(userRepository.save(user)));
    }

    @PutMapping("/system-admin/users/{id}")
    @PreAuthorize("hasAuthority('MANAGE_SYSTEM_SETTINGS')")
    @Transactional
    public ApiResponse<UserSummary> updateSystemUser(@AuthenticationPrincipal SuukaPrincipal principal, @PathVariable UUID id, @RequestBody Map<String, Object> request) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (request.containsKey("fullName")) user.setFullName(String.valueOf(request.get("fullName")));
        if (request.containsKey("branch")) user.setBranch(String.valueOf(request.get("branch")));
        if (request.containsKey("zone")) user.setZone(String.valueOf(request.get("zone")));
        if (request.containsKey("role")) {
            Role role = Role.valueOf(String.valueOf(request.get("role")));
            user.setRole(role);
            user.setPermissions(permissionService.permissionsFor(role));
        }
        audit(principal, "users", "USER_UPDATED", id.toString());
        return ApiResponse.success("User updated", UserSummary.from(user));
    }

    @PostMapping("/system-admin/users/{id}/disable")
    @PreAuthorize("hasAuthority('MANAGE_SYSTEM_SETTINGS')")
    public ApiResponse<BusinessRecord> disableUser(@AuthenticationPrincipal SuukaPrincipal principal, @PathVariable UUID id) {
        return ApiResponse.success("User disabled", record(principal, PlatformModule.CLIENTS, "User disabled", "Disabled user " + id, id.toString()));
    }

    @GetMapping("/system-admin/roles")
    @PreAuthorize("hasAuthority('MANAGE_SYSTEM_SETTINGS')")
    public ApiResponse<List<Map<String, Object>>> roles() {
        return ApiResponse.success("Roles loaded", Arrays.stream(Role.values()).map(role -> Map.<String, Object>of("role", role, "permissions", permissionService.permissionsFor(role))).toList());
    }

    @PostMapping("/system-admin/roles")
    @PreAuthorize("hasAuthority('MANAGE_SYSTEM_SETTINGS')")
    public ApiResponse<List<Map<String, Object>>> createRoleCatalogEntry() {
        return roles();
    }

    @PutMapping("/system-admin/roles/{id}/permissions")
    @PreAuthorize("hasAuthority('MANAGE_SYSTEM_SETTINGS')")
    public ApiResponse<BusinessRecord> updateRolePermissions(@AuthenticationPrincipal SuukaPrincipal principal, @PathVariable String id, @RequestBody Map<String, Object> request) {
        return ApiResponse.success("Role permission change requested", record(principal, PlatformModule.DOCUMENTS, "Role permission update", stringify(request), id));
    }

    @GetMapping("/system-admin/system-health")
    @PreAuthorize("hasAuthority('MANAGE_SYSTEM_SETTINGS')")
    public ApiResponse<Map<String, Object>> systemHealth() {
        return ApiResponse.success("System health loaded", Map.of("status", "UP", "database", "UP", "checkedAt", LocalDateTime.now()));
    }

    @GetMapping("/system-admin/backup-status")
    @PreAuthorize("hasAuthority('MANAGE_SYSTEM_SETTINGS')")
    public ApiResponse<List<BusinessRecord>> backupStatus() {
        return records(PlatformModule.DOCUMENTS, "Backup status loaded");
    }

    @GetMapping("/system-admin/security-logs")
    @PreAuthorize("hasAuthority('VIEW_AUDIT_LOGS')")
    public ApiResponse<?> securityLogs() {
        return ApiResponse.success("Security logs loaded", auditService.latest());
    }

    @GetMapping("/system-admin/settings")
    @PreAuthorize("hasAuthority('MANAGE_SYSTEM_SETTINGS')")
    public ApiResponse<List<BusinessRecord>> settings() {
        return records(PlatformModule.DOCUMENTS, "Settings loaded");
    }

    @PostMapping("/system-admin/settings/request-change")
    @PreAuthorize("hasAuthority('MANAGE_SYSTEM_SETTINGS')")
    public ApiResponse<ApprovalRequest> settingsApproval(@AuthenticationPrincipal SuukaPrincipal principal, @RequestBody Map<String, Object> request) {
        return createApproval(principal, ApprovalType.SYSTEM_SETTING_CHANGE, title(request, "System setting change"), stringify(request));
    }

    @GetMapping("/executive/command-center")
    @PreAuthorize("hasAuthority('VIEW_EXECUTIVE_BI')")
    public ApiResponse<Map<String, Object>> commandCenter() {
        return ApiResponse.success("Command center loaded", Map.of("clients", userRepository.countByRole(Role.CLIENT), "cleaners", userRepository.countByRole(Role.CLEANER), "approvals", approvalService.all().size()));
    }

    @GetMapping({"/executive/business-intelligence", "/executive/revenue-forecast", "/executive/customer-growth", "/executive/territory-performance"})
    @PreAuthorize("hasAuthority('VIEW_EXECUTIVE_BI')")
    public ApiResponse<List<BusinessRecord>> executiveRecords() {
        return records(PlatformModule.BUSINESS_INTELLIGENCE, "Executive records loaded");
    }

    @GetMapping("/executive/ai-recommendations")
    @PreAuthorize("hasAuthority('VIEW_EXECUTIVE_BI')")
    public ApiResponse<?> executiveAiRecommendations() {
        return ApiResponse.success("AI recommendations loaded", aiRecommendationRepository.findAll());
    }

    @GetMapping("/executive/approval-queue")
    @PreAuthorize("hasAuthority('VIEW_EXECUTIVE_BI')")
    public ApiResponse<List<ApprovalRequest>> executiveApprovalQueue() {
        return ApiResponse.success("Approval queue loaded", approvalService.all());
    }

    @PostMapping("/executive/approvals/{id}/approve")
    @PreAuthorize("hasAuthority('VIEW_EXECUTIVE_BI')")
    public ApiResponse<ApprovalRequest> executiveApprove(@AuthenticationPrincipal SuukaPrincipal principal, @PathVariable UUID id) {
        return ApiResponse.success("Approval approved", approvalService.review(principal.getId().toString(), id, ApprovalStatus.APPROVED));
    }

    @PostMapping("/executive/approvals/{id}/reject")
    @PreAuthorize("hasAuthority('VIEW_EXECUTIVE_BI')")
    public ApiResponse<ApprovalRequest> executiveReject(@AuthenticationPrincipal SuukaPrincipal principal, @PathVariable UUID id) {
        return ApiResponse.success("Approval rejected", approvalService.review(principal.getId().toString(), id, ApprovalStatus.REJECTED));
    }

    @GetMapping("/executive/audit-logs")
    @PreAuthorize("hasAuthority('VIEW_EXECUTIVE_BI')")
    public ApiResponse<?> executiveAuditLogs() {
        return ApiResponse.success("Audit logs loaded", auditService.latest());
    }

    private ApiResponse<Map<String, Object>> dashboard(SuukaPrincipal principal, String message, List<String> quickActions) {
        List<TaskRecord> tasks = taskRecordRepository.findByAssignedToOrderByCreatedAtDesc(principal.getId().toString());
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("role", principal.getRole());
        body.put("kpiCards", kpis());
        body.put("aiInsights", aiRecommendationRepository.findAll().stream().filter(item -> item.getGeneratedForRole() == null || item.getGeneratedForRole() == principal.getRole()).toList());
        body.put("pendingTasks", tasks);
        body.put("notifications", notificationRepository.findByUserIdOrderByCreatedAtDesc(principal.getId()));
        body.put("recentActivity", timelineRepository.findAll().stream().sorted(Comparator.comparing(item -> item.getCreatedAt() == null ? LocalDateTime.MIN : item.getCreatedAt(), Comparator.reverseOrder())).limit(10).toList());
        body.put("quickActions", quickActions);
        return ApiResponse.success(message, body);
    }

    private List<Map<String, Object>> kpis() {
        return List.of(
                Map.of("key", "clients", "label", "Clients", "value", userRepository.countByRole(Role.CLIENT)),
                Map.of("key", "cleaners", "label", "Cleaners", "value", userRepository.countByRole(Role.CLEANER)),
                Map.of("key", "inventoryItems", "label", "Inventory Items", "value", inventoryItemRepository.count()),
                Map.of("key", "pendingApprovals", "label", "Pending Approvals", "value", approvalService.all().stream().filter(item -> item.getApprovalStatus() == ApprovalStatus.PENDING).count())
        );
    }

    private ApiResponse<List<BusinessRecord>> records(PlatformModule module, String message) {
        return ApiResponse.success(message, businessRecordRepository.findByModuleOrderByCreatedAtDesc(module));
    }

    private BusinessRecord record(SuukaPrincipal principal, PlatformModule module, String title, String description, String relatedEntityId) {
        BusinessRecord record = new BusinessRecord();
        record.setModule(module);
        record.setTitle(title);
        record.setDescription(description);
        record.setRelatedEntityId(relatedEntityId);
        record.setOwnerId(principal.getId().toString());
        BusinessRecord saved = businessRecordRepository.save(record);
        audit(principal, module.name(), "RECORD_CREATED", title);
        return saved;
    }

    private ApiResponse<ApprovalRequest> createApproval(SuukaPrincipal principal, ApprovalType type, String title, String reason) {
        return ApiResponse.success("Approval request created", approvalService.create(principal.getId().toString(), new CreateApprovalRequest(type, title, reason)));
    }

    private void audit(SuukaPrincipal principal, String module, String action, String details) {
        auditService.record(principal.getId().toString(), module, action, details);
    }

    private InventoryItem inventoryItem(InventoryItemRequest request, InventoryItem item) {
        item.setName(request.name());
        item.setSku(request.sku());
        item.setQuantity(request.quantity());
        item.setReorderLevel(request.reorderLevel());
        item.setUnit(request.unit());
        return item;
    }

    private String title(Map<String, Object> request, String fallback) {
        return String.valueOf(value(request, "title", value(request, "name", fallback)));
    }

    private String relatedId(Map<String, Object> request) {
        Object value = value(request, "relatedEntityId", value(request, "entityId", null));
        return value == null ? null : String.valueOf(value);
    }

    private String stringify(Map<String, Object> request) {
        return request == null ? "" : request.toString();
    }

    private Object value(Map<String, Object> request, String key, Object fallback) {
        if (request == null) {
            return fallback;
        }
        return request.getOrDefault(key, fallback);
    }

    private PlatformModule financeModule(String path) {
        if (path.contains("invoices")) return PlatformModule.INVOICES;
        if (path.contains("payments")) return PlatformModule.PAYMENTS;
        if (path.contains("refunds")) return PlatformModule.WALLET;
        return PlatformModule.PAYMENTS;
    }
}
