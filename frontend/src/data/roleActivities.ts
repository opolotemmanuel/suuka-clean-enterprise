export type Role =
  | 'client'
  | 'cleaner'
  | 'supervisor'
  | 'operations'
  | 'customer-success'
  | 'workforce'
  | 'finance'
  | 'inventory-procurement'
  | 'admin'
  | 'executive-admin';

export type MenuItem = {
  key: string;
  label: string;
  icon: string;
};

export const roleMenuItems: Record<Role, MenuItem[]> = {
  client: [
    { key: 'dashboard', label: 'Dashboard', icon: 'fa-house' },
    { key: 'new-booking', label: 'Bookings', icon: 'fa-calendar-plus' },
    { key: 'my-bookings', label: 'Booking History', icon: 'fa-book-open' },
    { key: 'invoices', label: 'Invoices', icon: 'fa-file-invoice-dollar' },
    { key: 'wallet', label: 'Wallet', icon: 'fa-wallet' },
    { key: 'reviews', label: 'Reviews', icon: 'fa-star' },
    { key: 'messages', label: 'Messages', icon: 'fa-comments' },
    { key: 'support', label: 'Support', icon: 'fa-headset' },
    { key: 'loyalty', label: 'Loyalty', icon: 'fa-award' },
    { key: 'referrals', label: 'Referrals', icon: 'fa-user-plus' },
    { key: 'profile', label: 'Profile', icon: 'fa-user' },
  ],
  cleaner: [
    { key: 'dashboard', label: 'Dashboard', icon: 'fa-house' },
    { key: 'jobs', label: 'Jobs', icon: 'fa-broom' },
    { key: 'schedule', label: 'Schedule', icon: 'fa-calendar-day' },
    { key: 'route-planner', label: 'Route Planner', icon: 'fa-route' },
    { key: 'earnings', label: 'Earnings', icon: 'fa-money-bill-wave' },
    { key: 'supply-requests', label: 'Supply Requests', icon: 'fa-box-open' },
    { key: 'messages', label: 'Messages', icon: 'fa-comments' },
    { key: 'training-materials', label: 'Training Materials', icon: 'fa-graduation-cap' },
    { key: 'profile', label: 'Profile', icon: 'fa-user' },
  ],
  supervisor: [
    { key: 'dashboard', label: 'Dashboard', icon: 'fa-house' },
    { key: 'cleaner-monitoring', label: 'Cleaner Monitoring', icon: 'fa-people-group' },
    { key: 'performance-reports', label: 'Performance Reports', icon: 'fa-chart-line' },
    { key: 'complaint-monitoring', label: 'Complaint Monitoring', icon: 'fa-triangle-exclamation' },
    { key: 'territory-management', label: 'Territory Management', icon: 'fa-map-location-dot' },
    { key: 'supply-approval', label: 'Supply Approval', icon: 'fa-clipboard-check' },
  ],
  operations: [
    { key: 'dashboard', label: 'Dispatch Center', icon: 'fa-tower-broadcast' },
    { key: 'bookings-operations', label: 'Bookings Operations', icon: 'fa-calendar-check' },
    { key: 'cleaner-schedules', label: 'Cleaner Schedules', icon: 'fa-calendar-days' },
    { key: 'route-management', label: 'Route Management', icon: 'fa-route' },
    { key: 'territory-operations', label: 'Territory Operations', icon: 'fa-map-location-dot' },
    { key: 'delivery-tracking', label: 'Service Delivery Tracking', icon: 'fa-location-crosshairs' },
  ],
  'customer-success': [
    { key: 'dashboard', label: 'CRM Center', icon: 'fa-address-book' },
    { key: 'complaints', label: 'Complaints', icon: 'fa-comments' },
    { key: 'client-follow-ups', label: 'Client Follow-Ups', icon: 'fa-phone' },
    { key: 'vip-clients', label: 'VIP Clients', icon: 'fa-gem' },
    { key: 'at-risk-clients', label: 'At-Risk Clients', icon: 'fa-triangle-exclamation' },
    { key: 'campaign-performance', label: 'Campaign Performance', icon: 'fa-bullhorn' },
  ],
  workforce: [
    { key: 'dashboard', label: 'Workforce Dashboard', icon: 'fa-users-viewfinder' },
    { key: 'cleaner-records', label: 'Cleaner Records', icon: 'fa-id-card' },
    { key: 'training', label: 'Training', icon: 'fa-graduation-cap' },
    { key: 'attendance', label: 'Attendance', icon: 'fa-clipboard-user' },
    { key: 'leave-requests', label: 'Leave Requests', icon: 'fa-person-walking-arrow-right' },
    { key: 'performance-plans', label: 'Performance Plans', icon: 'fa-chart-simple' },
    { key: 'certifications', label: 'Certifications', icon: 'fa-certificate' },
  ],
  finance: [
    { key: 'dashboard', label: 'Finance Control', icon: 'fa-sack-dollar' },
    { key: 'finance-invoices', label: 'Invoices', icon: 'fa-file-invoice-dollar' },
    { key: 'payments', label: 'Payments', icon: 'fa-credit-card' },
    { key: 'refund-approvals', label: 'Refund Approvals', icon: 'fa-rotate-left' },
    { key: 'profit-loss', label: 'Profit & Loss', icon: 'fa-chart-pie' },
    { key: 'cash-flow', label: 'Cash Flow', icon: 'fa-money-bill-trend-up' },
    { key: 'expenses', label: 'Expenses', icon: 'fa-receipt' },
    { key: 'payroll-summaries', label: 'Payroll Summaries', icon: 'fa-file-lines' },
  ],
  'inventory-procurement': [
    { key: 'dashboard', label: 'Supply Chain Center', icon: 'fa-boxes-stacked' },
    { key: 'inventory', label: 'Inventory', icon: 'fa-boxes-stacked' },
    { key: 'suppliers', label: 'Suppliers', icon: 'fa-truck-field' },
    { key: 'procurement', label: 'Procurement', icon: 'fa-cart-shopping' },
    { key: 'asset-management', label: 'Asset Management', icon: 'fa-toolbox' },
    { key: 'vehicle-management', label: 'Vehicle Management', icon: 'fa-truck' },
    { key: 'documents', label: 'Documents', icon: 'fa-folder-open' },
  ],
  admin: [
    { key: 'dashboard', label: 'System Administrator', icon: 'fa-house' },
    { key: 'ai-intelligence', label: 'AI Intelligence', icon: 'fa-brain' },
    { key: 'service-catalog', label: 'Service Catalog', icon: 'fa-list' },
    { key: 'pricing-engine', label: 'Pricing Engine', icon: 'fa-tags' },
    { key: 'sla-management', label: 'SLA Management', icon: 'fa-business-time' },
    { key: 'documents', label: 'Documents', icon: 'fa-folder-open' },
    { key: 'knowledge-center', label: 'SOP & Knowledge', icon: 'fa-book' },
    { key: 'admin-bookings', label: 'Bookings', icon: 'fa-calendar-check' },
    { key: 'dispatch', label: 'Dispatch', icon: 'fa-tower-broadcast' },
    { key: 'clients', label: 'Clients', icon: 'fa-users' },
    { key: 'crm-center', label: 'CRM Center', icon: 'fa-address-book' },
    { key: 'cleaners', label: 'Cleaners', icon: 'fa-people-group' },
    { key: 'workforce-management', label: 'Workforce Management', icon: 'fa-users-viewfinder' },
    { key: 'quality-assurance', label: 'Quality Assurance', icon: 'fa-clipboard-check' },
    { key: 'complaints-disputes', label: 'Complaints & Disputes', icon: 'fa-scale-balanced' },
    { key: 'incidents', label: 'Incidents', icon: 'fa-shield-halved' },
    { key: 'inventory', label: 'Inventory', icon: 'fa-boxes-stacked' },
    { key: 'suppliers', label: 'Suppliers', icon: 'fa-truck-field' },
    { key: 'procurement', label: 'Procurement', icon: 'fa-cart-shopping' },
    { key: 'asset-management', label: 'Asset Management', icon: 'fa-toolbox' },
    { key: 'vehicle-management', label: 'Vehicle Management', icon: 'fa-truck' },
    { key: 'finance-control', label: 'Finance Control', icon: 'fa-sack-dollar' },
    { key: 'marketing-center', label: 'Marketing Center', icon: 'fa-bullhorn' },
    { key: 'territory-expansion', label: 'Territory & Expansion', icon: 'fa-map-location-dot' },
    { key: 'corporate-accounts', label: 'Corporate Accounts', icon: 'fa-building' },
    { key: 'notifications', label: 'Notifications', icon: 'fa-bell' },
    { key: 'business-intelligence', label: 'Business Intelligence', icon: 'fa-chart-simple' },
    { key: 'executive-copilot', label: 'Executive Copilot', icon: 'fa-wand-magic-sparkles' },
    { key: 'reports-analytics', label: 'Reports & Analytics', icon: 'fa-chart-line' },
    { key: 'approval-center', label: 'Approval Center', icon: 'fa-list-check' },
    { key: 'audit-logs', label: 'Audit Logs', icon: 'fa-clipboard-list' },
    { key: 'security-center', label: 'Security Center', icon: 'fa-lock' },
    { key: 'backup-recovery', label: 'Backup & Recovery', icon: 'fa-database' },
    { key: 'system-health', label: 'System Health', icon: 'fa-heart-pulse' },
    { key: 'multi-city', label: 'Multi-City Operations', icon: 'fa-city' },
    { key: 'system-settings', label: 'System Settings', icon: 'fa-gear' },
  ],
  'executive-admin': [
    { key: 'dashboard', label: 'Executive Command Center', icon: 'fa-house' },
    { key: 'executive-copilot', label: 'Executive Copilot', icon: 'fa-wand-magic-sparkles' },
    { key: 'business-intelligence', label: 'Business Intelligence', icon: 'fa-chart-simple' },
    { key: 'ai-intelligence', label: 'AI Intelligence', icon: 'fa-brain' },
    { key: 'approval-center', label: 'Approval Center', icon: 'fa-list-check' },
    { key: 'corporate-accounts', label: 'Corporate Accounts', icon: 'fa-building' },
    { key: 'service-catalog', label: 'Service Catalog', icon: 'fa-list' },
    { key: 'pricing-engine', label: 'Pricing Engine', icon: 'fa-tags' },
    { key: 'sla-management', label: 'SLA Management', icon: 'fa-business-time' },
    { key: 'quality-assurance', label: 'Quality Assurance', icon: 'fa-clipboard-check' },
    { key: 'incidents', label: 'Incidents', icon: 'fa-shield-halved' },
    { key: 'territory-expansion', label: 'Territory & Expansion', icon: 'fa-map-location-dot' },
    { key: 'system-health', label: 'System Health', icon: 'fa-heart-pulse' },
    { key: 'audit-logs', label: 'Audit Logs', icon: 'fa-clipboard-list' },
    { key: 'system-settings', label: 'System Settings', icon: 'fa-gear' },
  ],
};

export const roleOptions: Array<{ value: Role; label: string }> = [
  { value: 'client', label: 'Client' },
  { value: 'cleaner', label: 'Cleaner' },
  { value: 'supervisor', label: 'Supervisor' },
  { value: 'operations', label: 'Operations Manager' },
  { value: 'customer-success', label: 'Customer Success' },
  { value: 'workforce', label: 'HR / Workforce' },
  { value: 'finance', label: 'Finance Manager' },
  { value: 'inventory-procurement', label: 'Inventory & Procurement' },
  { value: 'admin', label: 'System Administrator' },
  { value: 'executive-admin', label: 'Executive Administrator' },
];
