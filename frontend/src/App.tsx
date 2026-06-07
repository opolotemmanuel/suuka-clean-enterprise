import { useEffect, useMemo, useState } from 'react';
import PageLayout from './components/PageLayout';
import { roleMenuItems, Role } from './data/roleActivities';
import {
  clearBackendSession,
  connectDemoBackendSession,
  getBackendSession,
  loadAIRecommendations,
  loadBackendData,
  loadDashboardSummary,
  type AIRecommendationRecord,
  type BackendUser,
  type DashboardAction,
  type DashboardSummary,
} from './api/backend';
import BookingForm from './components/activities/BookingForm';
import ClientProfileForm from './components/activities/ClientProfileForm';
import CleanerEarningsSummary from './components/activities/CleanerEarningsSummary';
import ManageBooking from './components/activities/ManageBooking';
import ReviewsHistory from './components/activities/ReviewsHistory';
import SettingsPage from './pages/SettingsPage';
import {
  ApplicationStatusPage,
  ForgotPasswordPage,
  LoginPage,
  RegisterPage,
  ResetPasswordPage,
  SimpleAuthPage,
  VerifyAccountPage,
  VerifyMfaPage,
  dashboardPathFor,
  usePathname,
} from './pages/AuthPages';

type Metric = { icon: string; label: string; value: string; trend?: string };
type AIRecommendation = {
  title: string;
  observation: string;
  recommendation: string;
  impact: string;
  status: 'Recommendation' | 'Needs review' | 'Queued';
};
type TableRow = Record<string, string>;
type BackendRecord = Record<string, unknown>;

const roleLabels: Record<Role, string> = {
  client: 'Client',
  cleaner: 'Cleaner',
  supervisor: 'Supervisor',
  operations: 'Operations Manager',
  'customer-success': 'Customer Success Manager',
  workforce: 'HR / Workforce Manager',
  finance: 'Finance Manager',
  'inventory-procurement': 'Inventory & Procurement Manager',
  admin: 'System Administrator',
  'executive-admin': 'Executive Administrator',
};

const backendRoleToUiRole: Record<string, Role> = {
  CLIENT: 'client',
  CLEANER: 'cleaner',
  SUPERVISOR: 'supervisor',
  OPERATIONS_MANAGER: 'operations',
  CUSTOMER_SUCCESS_MANAGER: 'customer-success',
  HR_MANAGER: 'workforce',
  FINANCE_MANAGER: 'finance',
  INVENTORY_MANAGER: 'inventory-procurement',
  SYSTEM_ADMIN: 'admin',
  EXECUTIVE_ADMIN: 'executive-admin',
};

const pathToRole: Record<string, Role> = {
  '/client/dashboard': 'client',
  '/cleaner/dashboard': 'cleaner',
  '/supervisor/dashboard': 'supervisor',
  '/operations/dashboard': 'operations',
  '/customer-success/dashboard': 'customer-success',
  '/hr/dashboard': 'workforce',
  '/finance/dashboard': 'finance',
  '/inventory-manager/dashboard': 'inventory-procurement',
  '/system-admin/dashboard': 'admin',
  '/executive/dashboard': 'executive-admin',
};

const roleDashboardApiPath: Record<Role, string> = {
  client: '/api/client/dashboard',
  cleaner: '/api/cleaner/dashboard',
  supervisor: '/api/supervisor/dashboard',
  operations: '/api/operations/dashboard',
  'customer-success': '/api/customer-success/dashboard',
  workforce: '/api/hr/dashboard',
  finance: '/api/finance/dashboard',
  'inventory-procurement': '/api/inventory-manager/dashboard',
  admin: '/api/system-admin/dashboard',
  'executive-admin': '/api/executive/dashboard',
};

const moduleApiPath: Partial<Record<Role, Record<string, string>>> = {
  cleaner: {
    jobs: '/api/client/bookings',
    schedule: '/api/cleaner/schedule',
    earnings: '/api/cleaner/earnings',
    'supply-requests': '/api/inventory-manager/supply-requests',
    'training-materials': '/api/cleaner/training',
  },
  supervisor: {
    'cleaner-monitoring': '/api/supervisor/team',
    'performance-reports': '/api/supervisor/team/performance',
    'complaint-monitoring': '/api/supervisor/complaints',
    'territory-management': '/api/supervisor/territory',
    'supply-approval': '/api/supervisor/supply-requests',
  },
  operations: {
    'bookings-operations': '/api/operations/dispatch-board',
    'cleaner-schedules': '/api/operations/schedules',
    'route-management': '/api/operations/routes',
    'territory-operations': '/api/operations/service-gaps',
    'delivery-tracking': '/api/operations/active-jobs',
  },
  'customer-success': {
    complaints: '/api/customer-success/complaints',
    'client-follow-ups': '/api/customer-success/follow-ups',
    'vip-clients': '/api/customer-success/crm',
    'at-risk-clients': '/api/customer-success/clients/at-risk',
    'campaign-performance': '/api/customer-success/campaigns',
  },
  workforce: {
    'cleaner-records': '/api/hr/cleaners',
    training: '/api/hr/training',
    attendance: '/api/hr/attendance',
    'leave-requests': '/api/hr/leave-requests',
    'performance-plans': '/api/platform/WORKFORCE/records',
    certifications: '/api/hr/certifications',
  },
  finance: {
    'finance-invoices': '/api/finance/invoices',
    payments: '/api/finance/payments',
    'refund-approvals': '/api/finance/refunds',
    'profit-loss': '/api/finance/profit-loss',
    'cash-flow': '/api/finance/cash-flow',
    expenses: '/api/finance/expenses',
    'payroll-summaries': '/api/finance/payroll',
  },
  'inventory-procurement': {
    inventory: '/api/inventory-manager/items',
    suppliers: '/api/inventory-manager/suppliers',
    procurement: '/api/inventory-manager/purchase-orders',
    'asset-management': '/api/platform/ASSETS/records',
    'vehicle-management': '/api/platform/VEHICLES/records',
    documents: '/api/platform/DOCUMENTS/records',
  },
  admin: {
    'ai-intelligence': '/api/admin/ai/recommendations',
    'service-catalog': '/api/platform/SERVICE_CATALOG/records',
    'pricing-engine': '/api/platform/PRICING/records',
    'sla-management': '/api/platform/SERVICE_CATALOG/records',
    documents: '/api/platform/DOCUMENTS/records',
    'knowledge-center': '/api/platform/DOCUMENTS/records',
    'admin-bookings': '/api/operations/dispatch-board',
    dispatch: '/api/operations/dispatch-board',
    clients: '/api/system-admin/users',
    'crm-center': '/api/customer-success/crm',
    cleaners: '/api/hr/cleaners',
    'workforce-management': '/api/platform/WORKFORCE/records',
    'quality-assurance': '/api/platform/QUALITY/records',
    'complaints-disputes': '/api/platform/COMPLAINTS/records',
    incidents: '/api/platform/INCIDENTS/records',
    inventory: '/api/inventory-manager/items',
    suppliers: '/api/inventory-manager/suppliers',
    procurement: '/api/inventory-manager/purchase-orders',
    'asset-management': '/api/platform/ASSETS/records',
    'vehicle-management': '/api/platform/VEHICLES/records',
    'finance-control': '/api/finance/summary',
    'marketing-center': '/api/platform/MARKETING/records',
    'territory-expansion': '/api/platform/TERRITORY/records',
    'corporate-accounts': '/api/platform/CORPORATE/records',
    notifications: '/api/notifications',
    'business-intelligence': '/api/executive/business-intelligence',
    'executive-copilot': '/api/executive/command-center',
    'reports-analytics': '/api/reports/bookings',
    'approval-center': '/api/approvals',
    'audit-logs': '/api/audit-logs',
    'security-center': '/api/system-admin/security-logs',
    'backup-recovery': '/api/system-admin/backup-status',
    'system-health': '/api/system-admin/system-health',
    'multi-city': '/api/platform/TERRITORY/records',
  },
  'executive-admin': {
    'executive-copilot': '/api/executive/command-center',
    'business-intelligence': '/api/executive/business-intelligence',
    'ai-intelligence': '/api/executive/ai-recommendations',
    'approval-center': '/api/executive/approval-queue',
    'corporate-accounts': '/api/platform/CORPORATE/records',
    'service-catalog': '/api/platform/SERVICE_CATALOG/records',
    'pricing-engine': '/api/platform/PRICING/records',
    'sla-management': '/api/platform/SERVICE_CATALOG/records',
    'quality-assurance': '/api/platform/QUALITY/records',
    incidents: '/api/platform/INCIDENTS/records',
    'territory-expansion': '/api/platform/TERRITORY/records',
    'system-health': '/api/system-admin/system-health',
    'audit-logs': '/api/executive/audit-logs',
  },
};

function apiPathFor(role: Role, selectedItem: string) {
  if (selectedItem === 'dashboard') return roleDashboardApiPath[role];
  return moduleApiPath[role]?.[selectedItem] ?? roleDashboardApiPath[role];
}

function toTitle(key: string) {
  return key.split('-').map((word) => word[0].toUpperCase() + word.slice(1)).join(' ');
}

const metricsByRole: Record<Role, Metric[]> = {
  client: [
    { icon: 'fa-calendar-day', label: 'Next Booking', value: 'Jun 12, 2:00 PM' },
    { icon: 'fa-book-open', label: 'Active Bookings', value: '2' },
    { icon: 'fa-wallet', label: 'Wallet Balance', value: '$86' },
    { icon: 'fa-star', label: 'Loyalty Points', value: '450' },
  ],
  cleaner: [
    { icon: 'fa-broom', label: "Today's Jobs", value: '3' },
    { icon: 'fa-route', label: 'Route Stops', value: '5' },
    { icon: 'fa-money-bill-wave', label: 'Weekly Earnings', value: '$240' },
    { icon: 'fa-star', label: 'Rating', value: '4.9' },
  ],
  supervisor: [
    { icon: 'fa-people-group', label: 'Active Teams', value: '8' },
    { icon: 'fa-clipboard-check', label: 'Jobs On Track', value: '92%' },
    { icon: 'fa-triangle-exclamation', label: 'Open Complaints', value: '4' },
    { icon: 'fa-box-open', label: 'Supply Requests', value: '11' },
  ],
  operations: [
    { icon: 'fa-calendar-check', label: 'Bookings In Motion', value: '64' },
    { icon: 'fa-route', label: 'Routes Active', value: '18' },
    { icon: 'fa-location-crosshairs', label: 'Tracked Services', value: '41' },
    { icon: 'fa-map-location-dot', label: 'Territories Covered', value: '12' },
  ],
  'customer-success': [
    { icon: 'fa-user-plus', label: 'New Leads', value: '28' },
    { icon: 'fa-triangle-exclamation', label: 'At-Risk Clients', value: '12' },
    { icon: 'fa-phone', label: 'Follow-Ups Due', value: '34' },
    { icon: 'fa-gem', label: 'VIP Clients', value: '19' },
  ],
  workforce: [
    { icon: 'fa-people-group', label: 'Active Cleaners', value: '34' },
    { icon: 'fa-person-walking-arrow-right', label: 'On Leave', value: '3' },
    { icon: 'fa-graduation-cap', label: 'Training Completion', value: '82%' },
    { icon: 'fa-certificate', label: 'Certifications Due', value: '6' },
  ],
  finance: [
    { icon: 'fa-file-invoice-dollar', label: 'Open Invoices', value: '$18,420' },
    { icon: 'fa-credit-card', label: 'Payments Today', value: '$3,240' },
    { icon: 'fa-rotate-left', label: 'Refund Approvals', value: '5' },
    { icon: 'fa-money-bill-trend-up', label: 'Cash Flow', value: '+12%' },
  ],
  'inventory-procurement': [
    { icon: 'fa-boxes-stacked', label: 'Inventory Alerts', value: '7' },
    { icon: 'fa-cart-shopping', label: 'Purchase Requests', value: '12' },
    { icon: 'fa-truck-field', label: 'Active Suppliers', value: '9' },
    { icon: 'fa-toolbox', label: 'Assets Due Service', value: '5' },
  ],
  admin: [
    { icon: 'fa-lock', label: 'Security Events', value: '4' },
    { icon: 'fa-database', label: 'Backup Status', value: 'Healthy' },
    { icon: 'fa-heart-pulse', label: 'System Uptime', value: '99.98%' },
    { icon: 'fa-users-gear', label: 'Role Changes', value: '3' },
  ],
  'executive-admin': [
    { icon: 'fa-sack-dollar', label: 'Revenue MTD', value: '$12,450' },
    { icon: 'fa-book-open', label: 'Open Bookings', value: '124' },
    { icon: 'fa-users', label: 'Active Clients', value: '1,248' },
    { icon: 'fa-people-group', label: 'Active Cleaners', value: '34' },
    { icon: 'fa-face-smile', label: 'Satisfaction', value: '96%' },
    { icon: 'fa-list-check', label: 'Pending Approvals', value: '17' },
  ],
};

const aiByRole: Record<Role, AIRecommendation[]> = {
  client: [
    {
      title: 'Booking Suggestion',
      observation: 'Your Friday bookings usually request kitchen deep-clean add-ons.',
      recommendation: 'Offer a repeat add-on when you schedule next week.',
      impact: 'Estimated 18% time savings',
      status: 'Recommendation',
    },
    {
      title: 'Cleaning Reminder',
      observation: 'The last upholstery service was 87 days ago.',
      recommendation: 'Schedule upholstery refresh before the next home clean.',
      impact: 'Improves service consistency',
      status: 'Queued',
    },
  ],
  cleaner: [
    {
      title: 'Route Optimization',
      observation: 'Two assigned stops are within the same district.',
      recommendation: 'Take the Garden Plaza job before Downtown Apartment.',
      impact: 'Saves 24 minutes travel time',
      status: 'Recommendation',
    },
    {
      title: 'Supply Suggestion',
      observation: 'Microfiber inventory is below your normal weekly usage.',
      recommendation: 'Request a refill before accepting Friday jobs.',
      impact: 'Prevents supply shortage',
      status: 'Needs review',
    },
  ],
  supervisor: [
    {
      title: 'Coverage Analysis',
      observation: 'Northside has three overlapping jobs and one available cleaner nearby.',
      recommendation: 'Review reassignment for the 3:00 PM office clean.',
      impact: 'Reduces late-arrival risk',
      status: 'Needs review',
    },
    {
      title: 'Training Recommendation',
      observation: 'Complaint tags show recurring handover issues for new cleaners.',
      recommendation: 'Schedule refresher training for shift closeout notes.',
      impact: 'Targets quality variance',
      status: 'Recommendation',
    },
  ],
  operations: [
    {
      title: 'Dispatch Bottleneck',
      observation: 'Central District has three jobs waiting for route confirmation.',
      recommendation: 'Review route balance before the 2:00 PM dispatch window.',
      impact: 'Protects on-time arrival',
      status: 'Needs review',
    },
    {
      title: 'Territory Coverage',
      observation: 'Weekend demand is outpacing cleaner availability in Northside.',
      recommendation: 'Invite two standby cleaners to open Saturday slots.',
      impact: 'Improves service coverage',
      status: 'Recommendation',
    },
  ],
  'customer-success': [
    {
      title: 'Client Churn Risk',
      observation: 'Premium clients with unresolved complaints show reduced bookings.',
      recommendation: 'Assign follow-up calls for the highest risk accounts.',
      impact: 'Targets retention',
      status: 'Needs review',
    },
    {
      title: 'VIP Opportunity',
      observation: 'Top-spend clients respond well to recurring plan reminders.',
      recommendation: 'Prepare a loyalty offer for manual approval.',
      impact: 'Increases repeat bookings',
      status: 'Queued',
    },
  ],
  workforce: [
    {
      title: 'Training Need',
      observation: 'New cleaners have repeated closeout-note corrections.',
      recommendation: 'Schedule documentation refresher training.',
      impact: 'Improves handover quality',
      status: 'Recommendation',
    },
    {
      title: 'Attendance Pattern',
      observation: 'Two cleaners have late check-ins on morning routes.',
      recommendation: 'Review attendance records before any HR action.',
      impact: 'Supports fair review',
      status: 'Needs review',
    },
  ],
  finance: [
    {
      title: 'Refund Review',
      observation: 'Five refund requests include complaint escalation tags.',
      recommendation: 'Route high-value refunds to finance approval.',
      impact: 'Maintains control',
      status: 'Needs review',
    },
    {
      title: 'Cash Flow Signal',
      observation: 'Corporate account payments are trending three days slower.',
      recommendation: 'Review follow-up schedule with account owners.',
      impact: 'Protects cash timing',
      status: 'Recommendation',
    },
  ],
  'inventory-procurement': [
    {
      title: 'Procurement Risk',
      observation: 'Disinfectant stock is below projected demand for Central District.',
      recommendation: 'Review supplier quote and submit purchase approval.',
      impact: 'Prevents supply disruption',
      status: 'Needs review',
    },
    {
      title: 'Asset Maintenance',
      observation: 'Two vacuum cleaners are nearing preventive maintenance windows.',
      recommendation: 'Schedule service before assigning them to corporate jobs.',
      impact: 'Reduces equipment failure risk',
      status: 'Recommendation',
    },
  ],
  admin: [
    {
      title: 'Security Control',
      observation: 'Three role changes were made in the last 24 hours.',
      recommendation: 'Review permissions and audit log entries.',
      impact: 'Strengthens governance',
      status: 'Needs review',
    },
    {
      title: 'System Health',
      observation: 'Notification service retries are above normal baseline.',
      recommendation: 'Inspect provider delivery reports before campaign approval.',
      impact: 'Protects communication reliability',
      status: 'Queued',
    },
  ],
  'executive-admin': [
    {
      title: 'Customers At Risk',
      observation: 'Nine high-value clients have not booked in 45 days.',
      recommendation: 'Approve a retention campaign for review by operations.',
      impact: 'Potential $4,800 retained revenue',
      status: 'Needs review',
    },
    {
      title: 'Inventory Depletion',
      observation: 'Glass cleaner stock will reach reorder threshold in 6 days.',
      recommendation: 'Approve purchase request after supplier quote review.',
      impact: 'Avoids service disruption',
      status: 'Queued',
    },
    {
      title: 'Demand Forecast',
      observation: 'Weekend demand is projected 21% higher in Central District.',
      recommendation: 'Schedule additional cleaner availability before Thursday.',
      impact: 'Improves dispatch coverage',
      status: 'Recommendation',
    },
  ],
};

const quickActionsByRole: Record<Role, string[]> = {
  client: ['Request Cleaning', 'Message Cleaner', 'Review Invoice'],
  cleaner: ['Update Availability', 'Request Supplies', 'Start Route'],
  supervisor: ['Review Coverage', 'Approve Supplies', 'Escalate Complaint'],
  operations: ['Open Dispatch Board', 'Rebalance Routes', 'Track Delivery'],
  'customer-success': ['Create Follow-Up', 'Review At-Risk Clients', 'Draft Campaign'],
  workforce: ['Review Attendance', 'Assign Training', 'Open Cleaner Record'],
  finance: ['Review Refunds', 'Export Payments', 'Approve Payroll Summary'],
  'inventory-procurement': ['Review Purchase Requests', 'Check Asset Maintenance', 'Open Supplier List'],
  admin: ['Review Security Logs', 'Check Backups', 'Manage Roles'],
  'executive-admin': ['Approve AI Recommendation', 'Open Executive Copilot', 'Review Forecasts'],
};

const activitiesByRole: Record<Role, string[]> = {
  client: ['Booking confirmed for Jun 12', 'Invoice INV-322 is due', 'Support replied to ticket SC-1042'],
  cleaner: ['Route updated for Downtown Apartment', 'Supply request submitted', 'Client note added to Garden Plaza'],
  supervisor: ['Complaint SC-917 moved to review', 'Supply request approved for Team 4', 'Coverage alert opened for Northside'],
  operations: ['Route batch updated', 'Late-arrival risk flagged', 'Territory board refreshed'],
  'customer-success': ['Follow-up assigned to VIP client', 'Complaint reply drafted', 'Retention list refreshed'],
  workforce: ['Training module assigned', 'Leave request reviewed', 'Certification reminder sent'],
  finance: ['Refund request queued', 'Payment batch reconciled', 'Payroll summary exported'],
  'inventory-procurement': ['Purchase request queued', 'Supplier agreement reviewed', 'Asset maintenance alert logged'],
  admin: ['Role permission reviewed', 'Daily backup completed', 'System health check passed'],
  'executive-admin': ['AI recommendation queued for approval', 'Inventory threshold audit recorded', 'Finance report exported'],
};

const pageCopy: Record<Role, { title: string; subtitle: string }> = {
  client: {
    title: 'Client Dashboard',
    subtitle: 'Request services, manage bookings, review payments, and stay in control of your cleaning plan.',
  },
  cleaner: {
    title: 'Cleaner Workspace',
    subtitle: 'Manage assigned jobs, routes, supplies, earnings, and customer handoffs.',
  },
  supervisor: {
    title: 'Supervisor Operations',
    subtitle: 'Monitor teams, complaints, supply approvals, territories, and field performance.',
  },
  operations: {
    title: 'Dispatch Center',
    subtitle: 'Coordinate bookings, cleaner schedules, routes, territories, and live service delivery.',
  },
  'customer-success': {
    title: 'CRM Center',
    subtitle: 'Manage complaints, follow-ups, VIP clients, at-risk accounts, and campaigns.',
  },
  workforce: {
    title: 'Workforce Dashboard',
    subtitle: 'Manage cleaner records, attendance, training, leave requests, performance plans, and certifications.',
  },
  finance: {
    title: 'Finance Control',
    subtitle: 'Review invoices, payments, refunds, cash flow, expenses, and payroll summaries.',
  },
  'inventory-procurement': {
    title: 'Supply Chain Center',
    subtitle: 'Manage inventory, suppliers, procurement, assets, vehicles, and document controls.',
  },
  admin: {
    title: 'System Administrator',
    subtitle: 'Manage security, roles, system configuration, backups, health, and compliance controls.',
  },
  'executive-admin': {
    title: 'Executive Command Center',
    subtitle: 'Lead enterprise operations with forecasts, governance, approvals, audit summary, and executive copilot.',
  },
};

function StatusBadge({ children }: { children: string }) {
  return <span className="status-badge">{children}</span>;
}

function EnterpriseTable({ title, rows }: { title: string; rows: TableRow[] }) {
  const [search, setSearch] = useState('');
  const [sortColumn, setSortColumn] = useState<string | null>(null);
  const [sortDirection, setSortDirection] = useState<'asc' | 'desc'>('asc');
  const [page, setPage] = useState(1);
  const pageSize = 8;
  const columns = Object.keys(rows[0] ?? {});
  const filteredRows = rows.filter((row) =>
    Object.values(row).some((value) => value.toLowerCase().includes(search.toLowerCase())),
  );
  const sortedRows = sortColumn
    ? [...filteredRows].sort((first, second) => {
        const result = first[sortColumn].localeCompare(second[sortColumn], undefined, { numeric: true });
        return sortDirection === 'asc' ? result : -result;
      })
    : filteredRows;
  const totalPages = Math.max(1, Math.ceil(sortedRows.length / pageSize));
  const visibleRows = sortedRows.slice((page - 1) * pageSize, page * pageSize);

  useEffect(() => {
    setPage(1);
  }, [search, rows]);

  function toggleSort(column: string) {
    if (sortColumn === column) {
      setSortDirection((value) => (value === 'asc' ? 'desc' : 'asc'));
    } else {
      setSortColumn(column);
      setSortDirection('asc');
    }
  }

  function exportCsv() {
    const csv = [
      columns.join(','),
      ...sortedRows.map((row) => columns.map((column) => JSON.stringify(row[column] ?? '')).join(',')),
    ].join('\n');
    const blob = new Blob([csv], { type: 'text/csv;charset=utf-8' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `${title.toLowerCase().replace(/[^a-z0-9]+/g, '-')}.csv`;
    link.click();
    URL.revokeObjectURL(url);
  }

  return (
    <section className="panel-card">
      <div className="panel-header">
        <h3>{title}</h3>
      </div>
      <div className="table-toolbar">
        <label className="table-search">
          <i className="fa-solid fa-magnifying-glass" aria-hidden="true" />
          <input type="search" placeholder="Search" value={search} onChange={(event) => setSearch(event.target.value)} />
        </label>
        <div className="table-actions">
          <button className="secondary-button small-button" type="button" onClick={() => setSearch('')}>
            Clear
          </button>
          <button className="secondary-button small-button" type="button" onClick={exportCsv} disabled={rows.length === 0}>
            CSV
          </button>
        </div>
      </div>
      <div className="table-scroll">
        <table>
          <thead>
            <tr>
              {columns.map((column) => (
                <th key={column}>
                  <button className="table-sort-button" type="button" onClick={() => toggleSort(column)}>
                    {column}
                    {sortColumn === column && <span>{sortDirection === 'asc' ? ' ↑' : ' ↓'}</span>}
                  </button>
                </th>
              ))}
            </tr>
          </thead>
          <tbody>
            {visibleRows.map((row, index) => (
              <tr key={Object.values(row).join('-')}>
                {columns.map((column) => (
                  <td key={`${column}-${index}`}>{row[column]}</td>
                ))}
              </tr>
            ))}
            {visibleRows.length === 0 && (
              <tr>
                <td colSpan={Math.max(columns.length, 1)}>No database records found for this view.</td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
      <div className="pagination-row">
        <span>
          Page {page} of {totalPages} · {filteredRows.length} records
        </span>
        <div>
          <button className="secondary-button small-button" type="button" disabled={page === 1} onClick={() => setPage((value) => Math.max(1, value - 1))}>
            Previous
          </button>
          <button className="secondary-button small-button" type="button" disabled={page === totalPages} onClick={() => setPage((value) => Math.min(totalPages, value + 1))}>
            Next
          </button>
        </div>
      </div>
    </section>
  );
}

function displayValue(value: unknown): string {
  if (value === null || value === undefined) return '';
  if (Array.isArray(value)) return value.map(displayValue).filter(Boolean).join(', ');
  if (typeof value === 'object') {
    const record = value as BackendRecord;
    return String(record.title ?? record.label ?? record.name ?? record.id ?? JSON.stringify(value));
  }
  return String(value);
}

function rowsFromBackendData(data: unknown): TableRow[] {
  const records: unknown[] = Array.isArray(data)
    ? data
    : [
        ...((data as BackendRecord | null)?.kpiCards as unknown[] | undefined ?? []),
        ...((data as BackendRecord | null)?.metrics as unknown[] | undefined ?? []),
        ...((data as BackendRecord | null)?.pendingTasks as unknown[] | undefined ?? []),
        ...((data as BackendRecord | null)?.notifications as unknown[] | undefined ?? []),
        ...((data as BackendRecord | null)?.recentActivity as unknown[] | undefined ?? []),
        ...((data as BackendRecord | null)?.quickActions as unknown[] | undefined ?? []),
      ];

  return records.map((item, index) => {
    if (!item || typeof item !== 'object') return { Record: String(index + 1), Value: displayValue(item) };
    const object = item as BackendRecord;
    return Object.fromEntries(
      Object.entries(object)
        .filter(([key]) => !['passwordHash', 'accessToken', 'refreshToken'].includes(key))
        .slice(0, 8)
        .map(([key, value]) => [toTitle(key), displayValue(value)]),
    );
  });
}

function BackendModulePanel({ role, selectedItem }: { role: Role; selectedItem: string }) {
  const [rows, setRows] = useState<TableRow[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const title = selectedItem === 'dashboard' ? `${roleLabels[role]} Database Records` : toTitle(selectedItem);
  const apiPath = apiPathFor(role, selectedItem);

  async function load() {
    setLoading(true);
    setError(null);
    try {
      const response = await loadBackendData(apiPath);
      setRows(rowsFromBackendData(response.data));
    } catch (requestError) {
      setRows([]);
      setError(requestError instanceof Error ? requestError.message : 'Unable to load database records');
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    void load();
  }, [apiPath]);

  if (loading) {
    return (
      <section className="panel-card data-state">
        <p>Loading {title.toLowerCase()} from backend...</p>
      </section>
    );
  }

  if (error) {
    return (
      <section className="panel-card data-state error">
        <p>{error}</p>
        <button className="secondary-button small-button" type="button" onClick={load}>
          Retry
        </button>
      </section>
    );
  }

  return <EnterpriseTable title={title} rows={rows} />;
}

function KPISection({ cards }: { cards: Metric[] }) {
  return (
    <section className="metrics-row" aria-label="KPI cards">
      {cards.map((card) => (
        <div key={card.label} className="metric-card">
          <div className="metric-icon">
            <i className={`fa-solid ${card.icon}`} aria-hidden="true" />
          </div>
          <div>
            <p className="metric-label">{card.label}</p>
            <p className="metric-value">{card.value}</p>
            {card.trend && <p className="metric-trend">{card.trend}</p>}
          </div>
        </div>
      ))}
    </section>
  );
}

function DashboardDataState({
  loading,
  error,
  emptyStates,
  onRetry,
}: {
  loading: boolean;
  error: string | null;
  emptyStates: string[];
  onRetry: () => void;
}) {
  if (loading) {
    return (
      <section className="panel-card data-state">
        <p>Loading dashboard data from backend...</p>
      </section>
    );
  }

  if (error) {
    return (
      <section className="panel-card data-state error">
        <p>Unable to load dashboard data.</p>
        <button className="secondary-button small-button" type="button" onClick={onRetry}>
          Retry
        </button>
      </section>
    );
  }

  if (emptyStates.length > 0) {
    return (
      <section className="panel-card data-state">
        {emptyStates.map((state) => (
          <p key={state}>{state}</p>
        ))}
      </section>
    );
  }

  return null;
}

function AIInsightsSection({ role, recommendations }: { role: Role; recommendations: AIRecommendationRecord[] }) {
  const heightClass = role === 'cleaner' ? 'ai-insights taller' : 'ai-insights';
  const backendRole = Object.entries(backendRoleToUiRole).find(([, uiRole]) => uiRole === role)?.[0];
  const visibleRecommendations = recommendations.filter((item) => !item.generatedForRole || item.generatedForRole === backendRole);

  return (
    <section className={heightClass} aria-label="AI insights">
      <div className="panel-header compact">
        <div>
          <p className="section-label">AI Assistant</p>
          <h3>{role === 'admin' ? 'AI Intelligence Summary' : `${roleLabels[role]} AI Insights`}</h3>
        </div>
        <StatusBadge>Human approval required</StatusBadge>
      </div>
      <div className="ai-grid">
        {visibleRecommendations.map((item) => (
          <article className="ai-card" key={item.id}>
            <div>
              <div className="ai-card-title-row">
                <h4>{item.recommendationTitle}</h4>
                <StatusBadge>{item.aiStatus}</StatusBadge>
              </div>
              <p>{item.insightCategory ?? 'Role-scoped recommendation'}</p>
              <strong>{item.recommendationBody}</strong>
            </div>
            <div className="ai-card-footer">
              <span>{item.riskLevel ?? 'Review required'}</span>
              <button
                className={role === 'admin' || role === 'executive-admin' ? 'primary-button small-button' : 'secondary-button small-button'}
                type="button"
                onClick={() => window.dispatchEvent(new CustomEvent('suuka-open-module', { detail: 'approval-center' }))}
              >
                Review
              </button>
            </div>
          </article>
        ))}
        {visibleRecommendations.length === 0 && (
          <article className="ai-card">
            <div>
              <div className="ai-card-title-row">
                <h4>No AI recommendations</h4>
                <StatusBadge>Database empty</StatusBadge>
              </div>
              <p>Recommendations will appear here after they are stored by the backend.</p>
            </div>
          </article>
        )}
      </div>
    </section>
  );
}

function QuickActions({
  backendActions,
  onAction,
}: {
  backendActions?: DashboardAction[];
  onAction?: (action: DashboardAction) => void;
}) {
  if (backendActions && backendActions.length > 0) {
    return (
      <section className="quick-actions" aria-label="Quick actions">
        {backendActions.map((action, index) => (
          <button
            key={`${action.targetRoute}-${action.label}`}
            className={index === 0 ? 'primary-button' : 'secondary-button'}
            type="button"
            onClick={() => onAction?.(action)}
          >
            {action.label}
          </button>
        ))}
      </section>
    );
  }

  return (
    <section className="panel-card data-state">
      <p>No backend quick actions are available for this role yet.</p>
    </section>
  );
}

function ActivityFeed({ activity }: { activity: string[] }) {
  return (
    <section className="panel-card activity-feed">
      <div className="panel-header">
        <h3>Activity Feed</h3>
      </div>
      <div className="activity-list">
        {activity.map((event) => (
          <div className="activity-item" key={event}>
            <span className="activity-dot" />
            <p>{event}</p>
            <time>Database</time>
          </div>
        ))}
        {activity.length === 0 && (
          <div className="activity-item">
            <span className="activity-dot" />
            <p>No backend activity has been recorded for this role yet.</p>
            <time>Empty</time>
          </div>
        )}
      </div>
    </section>
  );
}

function AuditTrail({ selectedItem }: { selectedItem: string }) {
  const adminSensitivePages = new Set([
    'ai-intelligence',
    'approval-center',
    'audit-logs',
    'finance-control',
    'refund-approvals',
    'system-settings',
    'inventory',
    'suppliers',
    'workforce-management',
    'service-catalog',
    'pricing-engine',
    'sla-management',
    'documents',
    'asset-management',
    'vehicle-management',
    'incidents',
    'notifications',
    'business-intelligence',
    'executive-copilot',
    'security-center',
    'backup-recovery',
    'system-health',
    'procurement',
  ]);

  if (!adminSensitivePages.has(selectedItem)) return null;

  return (
    <section className="panel-card audit-trail">
      <div className="panel-header">
        <h3>Audit Trail</h3>
        <StatusBadge>Admin-sensitive page</StatusBadge>
      </div>
      <div className="activity-list">
        {[
          'Permission check passed for current role',
          'Page data loaded with read-only AI recommendations',
          'Critical actions require human approval before execution',
        ].map((event) => (
          <div className="activity-item" key={event}>
            <span className="activity-dot" />
            <p>{event}</p>
            <time>Logged</time>
          </div>
        ))}
      </div>
    </section>
  );
}

function ClassicDashboardSection({ title, items }: { title: string; items: string[] }) {
  return (
    <section className="panel-card">
      <div className="panel-header">
        <h3>{title}</h3>
      </div>
      <div className="classic-grid">
        {items.map((item) => (
          <div className="classic-item" key={item}>
            <span className="classic-icon">
              <i className="fa-solid fa-check" aria-hidden="true" />
            </span>
            <p>{item}</p>
          </div>
        ))}
      </div>
    </section>
  );
}

function EnterprisePillars() {
  return (
    <section className="panel-card">
      <div className="panel-header">
        <h3>Enterprise Pillars</h3>
      </div>
      <div className="pillar-grid">
        {[
          'Operations Excellence',
          'Workforce Excellence',
          'Customer Excellence',
          'Financial Excellence',
          'AI-Assisted Intelligence',
          'Governance & Compliance',
        ].map((pillar) => (
          <div className="pillar-card" key={pillar}>
            <span className="classic-icon">
              <i className="fa-solid fa-layer-group" aria-hidden="true" />
            </span>
            <p>{pillar}</p>
          </div>
        ))}
      </div>
    </section>
  );
}

function ClientDashboard() {
  return (
    <div className="classic-dashboard">
      <EnterpriseTable
        title="Upcoming & Active Bookings"
        rows={[
          { Service: 'Standard Home Clean', Date: 'Jun 12, 10:00 AM', Cleaner: 'Mia W.', Status: 'Confirmed' },
          { Service: 'Office Deep Clean', Date: 'Jun 15, 2:00 PM', Cleaner: 'Samir P.', Status: 'Pending' },
        ]}
      />
      <ClassicDashboardSection
        title="Account & Payments"
        items={['Invoice INV-322 awaiting review', 'Wallet balance available for next booking', 'Referral credit ready after first invited booking']}
      />
      <ClassicDashboardSection
        title="Service Relationship"
        items={['Message cleaner before arrival', 'Review last service quality', 'Open support ticket SC-1042', 'Track loyalty progress']}
      />
    </div>
  );
}

function CleanerDashboard() {
  return (
    <div className="classic-dashboard">
      <EnterpriseTable
        title="Assigned Jobs In Order"
        rows={[
          { Priority: '1', Job: 'Lakeview Office', Time: 'Today, 9:00 AM', Status: 'Assigned' },
          { Priority: '2', Job: 'Downtown Apartment', Time: 'Today, 1:30 PM', Status: 'On route' },
          { Priority: '3', Job: 'Garden Plaza', Time: 'Today, 4:00 PM', Status: 'Scheduled' },
        ]}
      />
      <ClassicDashboardSection
        title="Route, Schedule & Supplies"
        items={['Start route from nearest confirmed stop', 'Update availability before accepting Friday work', 'Submit microfiber refill before stock runs low']}
      />
      <ClassicDashboardSection
        title="Earnings, Training & Profile"
        items={['Weekly earnings summary ready', 'Closeout-notes training recommended', 'Profile and certification records up to date']}
      />
    </div>
  );
}

function OverviewPanel({ role }: { role: Role }) {
  const items: Record<Role, string[]> = {
    client: ['Upcoming bookings', 'Invoices and wallet', 'Messages and support', 'Loyalty and referrals'],
    cleaner: ['Assigned jobs', 'Schedule and route planner', 'Earnings and supplies', 'Training and profile'],
    supervisor: ['Cleaner monitoring', 'Complaint review', 'Territory coverage'],
    operations: ['Dispatch center', 'Route management', 'Service delivery tracking'],
    'customer-success': ['CRM follow-ups', 'VIP clients', 'At-risk accounts'],
    workforce: ['Cleaner records', 'Training', 'Attendance'],
    finance: ['Invoices', 'Refund approvals', 'Payroll summaries'],
    'inventory-procurement': ['Inventory levels', 'Supplier performance', 'Assets and vehicles'],
    admin: ['Security controls', 'Backups', 'System health', 'RBAC'],
    'executive-admin': ['Operations board', 'Executive copilot', 'Forecasting', 'Approval queue'],
  };

  return (
    <section className="panel-card">
      <div className="panel-header">
        <h3>{roleLabels[role]} Overview</h3>
      </div>
      <div className="panel-body-grid">
        {items[role].map((item) => (
          <div key={item} className="info-card">
            {item}
          </div>
        ))}
      </div>
    </section>
  );
}

function OperationsBoard() {
  const columns = ['Pending', 'Assigned', 'Active', 'Completed', 'Cancelled'];
  return (
    <section className="panel-card">
      <div className="panel-header">
        <h3>Operations Board</h3>
      </div>
      <div className="kanban-board">
        {columns.map((column, index) => (
          <div className="kanban-column" key={column}>
            <h4>{column}</h4>
            <p>{[18, 42, 27, 96, 6][index]} jobs</p>
          </div>
        ))}
      </div>
    </section>
  );
}

function AICommandCenter() {
  return (
    <div className="stacked-content">
      <OverviewPanel role="admin" />
      <section className="panel-card recommendation-queue">
        <div className="panel-header">
          <h3>AI Recommendation Queue</h3>
          <StatusBadge>Scrollable approval workflow</StatusBadge>
        </div>
        <div className="recommendation-list">
          {aiByRole.admin.concat(aiByRole.supervisor).map((item) => (
            <article className="recommendation-item" key={`${item.title}-${item.impact}`}>
              <div>
                <h4>{item.title}</h4>
                <p>{item.recommendation}</p>
              </div>
              <div className="recommendation-actions">
                <button className="primary-button small-button">Approve</button>
                <button className="secondary-button small-button">Schedule</button>
                <button className="secondary-button small-button">Escalate</button>
                <button className="secondary-button small-button">Ignore</button>
              </div>
            </article>
          ))}
        </div>
      </section>
      <EnterpriseTable
        title="AI Audit Logs"
        rows={[
          { Event: 'Recommendation generated', Actor: 'AI Assistant', Action: 'Queued', Time: '09:12' },
          { Event: 'Inventory alert reviewed', Actor: 'Admin', Action: 'Approved', Time: '10:02' },
          { Event: 'Demand forecast exported', Actor: 'Admin', Action: 'Recorded', Time: '11:30' },
        ]}
      />
    </div>
  );
}

function ExecutiveCommandCenter() {
  return (
    <div className="stacked-content">
      <EnterprisePillars />
      <div className="approval-queue">
        <EnterpriseTable
          title="Approval Queue"
          rows={[
            { RequestType: 'Refund approval', RequestedBy: 'Finance', Priority: 'High', Date: 'Jun 5', Status: 'Review', Action: 'Approve / Reject' },
            { RequestType: 'Inventory purchase', RequestedBy: 'Operations', Priority: 'Medium', Date: 'Jun 5', Status: 'Queued', Action: 'Review' },
            { RequestType: 'Campaign send', RequestedBy: 'Customer Success', Priority: 'Medium', Date: 'Jun 6', Status: 'Draft', Action: 'Escalate' },
          ]}
        />
      </div>
      <OperationsBoard />
      <ClassicDashboardSection
        title="Business Forecasts"
        items={['Revenue trend', 'Demand forecast', 'Cleaner utilization', 'Customer retention']}
      />
    </div>
  );
}

function InventoryPanel() {
  return (
    <EnterpriseTable
      title="Inventory Management"
      rows={[
        { Item: 'Glass cleaner', Stock: '42 units', Threshold: '50 units', Supplier: 'Sparkle Supply', Status: 'Reorder review' },
        { Item: 'Microfiber cloths', Stock: '180 units', Threshold: '120 units', Supplier: 'CleanOps', Status: 'Healthy' },
        { Item: 'Disinfectant', Stock: '64 units', Threshold: '80 units', Supplier: 'BioSafe', Status: 'Forecast warning' },
      ]}
    />
  );
}

function ModulePanel({ title, rows }: { title: string; rows: TableRow[] }) {
  return <EnterpriseTable title={title} rows={rows} />;
}

function AnalyticsPanel() {
  return (
    <div className="stacked-content">
      <section className="panel-card">
        <div className="panel-header">
          <h3>Analytics & Forecasts</h3>
        </div>
        <div className="filter-tabs">
          {['7 Days', '30 Days', '90 Days', 'Custom'].map((filter) => (
            <button className="secondary-button small-button" key={filter}>
              {filter}
            </button>
          ))}
        </div>
        <div className="panel-body-grid">
          {['Revenue Analytics', 'Customer Analytics', 'Cleaner Analytics', 'Inventory Analytics', 'Operational Analytics', 'AI Forecasting'].map((item) => (
            <div key={item} className="info-card">
              {item}
            </div>
          ))}
        </div>
      </section>
    </div>
  );
}

function renderMainContent(role: Role, selectedItem: string) {
  if (selectedItem === 'new-booking') return <BookingForm />;
  if (selectedItem === 'my-bookings') return <ManageBooking />;
  if (selectedItem === 'profile') return <ClientProfileForm />;
  if (selectedItem === 'earnings') return <CleanerEarningsSummary />;
  if (selectedItem === 'system-settings') return <SettingsPage role={role} />;
      { Job: 'Downtown Apartment', Time: 'Today, 1:30 PM', Status: 'On route' },
    ],
    schedule: [
      { Day: 'Monday', Window: '8:00 AM - 2:00 PM', Jobs: '3', Status: 'Open' },
      { Day: 'Tuesday', Window: '10:00 AM - 5:00 PM', Jobs: '4', Status: 'Full' },
    ],
    'supply-requests': [
      { Request: 'Microfiber cloths', Quantity: '24', Status: 'Submitted', ETA: 'Jun 8' },
      { Request: 'Disinfectant', Quantity: '6', Status: 'Approved', ETA: 'Jun 7' },
    ],
    'training-materials': [
      { Module: 'Closeout Notes', Type: 'Checklist', Progress: 'Recommended', Status: 'Open' },
      { Module: 'Before / After Photos', Type: 'Guide', Progress: 'Complete', Status: 'Passed' },
    ],
    loyalty: [
      { Program: 'Home Care Rewards', Points: '450', NextReward: '$20 credit', Status: 'Active' },
      { Program: 'Recurring Booking Bonus', Points: '120', NextReward: 'Priority slot', Status: 'Eligible' },
    ],
    referrals: [
      { Invitee: 'Noor M.', Reward: '$15 credit', Date: 'Jun 3', Status: 'Pending first booking' },
      { Invitee: 'Jacob H.', Reward: '$15 credit', Date: 'May 22', Status: 'Earned' },
    ],
    'cleaner-monitoring': [
      { Cleaner: 'Mia W.', Territory: 'Northside', Jobs: '4', Status: 'On track' },
      { Cleaner: 'Samir P.', Territory: 'Central', Jobs: '3', Status: 'Needs check-in' },
    ],
    'performance-reports': [
      { Team: 'Team 1', Completion: '96%', Rating: '4.8', Status: 'Stable' },
      { Team: 'Team 4', Completion: '89%', Rating: '4.5', Status: 'Review' },
    ],
    'complaint-monitoring': [
      { Case: 'SC-917', Client: 'Amina K.', Severity: 'Medium', Status: 'Reviewing' },
      { Case: 'SC-921', Client: 'Noor M.', Severity: 'Low', Status: 'Assigned' },
    ],
    'territory-management': [
      { Territory: 'Central', Coverage: '88%', Demand: 'High', Status: 'Add capacity' },
      { Territory: 'Northside', Coverage: '96%', Demand: 'Normal', Status: 'Covered' },
    ],
    'supply-approval': [
      { Request: 'REQ-104', Team: 'Team 4', Amount: '$220', Status: 'Awaiting approval' },
      { Request: 'REQ-105', Team: 'Team 2', Amount: '$80', Status: 'Approved' },
    ],
    'user-management': [
      { Name: 'Amina K.', Role: 'Client', Status: 'Active', LastSeen: 'Today' },
      { Name: 'Mia W.', Role: 'Cleaner', Status: 'Available', LastSeen: '12 min ago' },
    ],
    'service-catalog': [
      { ServiceName: 'Home Cleaning', Category: 'Residential', Duration: '2 hrs', BasePrice: '$75', RequiredSkills: 'Standard clean', Zones: 'Central, Northside', Status: 'Active' },
      { ServiceName: 'Post Construction Cleaning', Category: 'Specialized', Duration: '6 hrs', BasePrice: '$280', RequiredSkills: 'Deep clean', Zones: 'Corporate zones', Status: 'Approval required' },
      { ServiceName: 'Carpet Cleaning', Category: 'Add-on', Duration: '90 mins', BasePrice: '$60', RequiredSkills: 'Machine handling', Zones: 'All active zones', Status: 'Active' },
    ],
    'pricing-engine': [
      { Rule: 'Peak Pricing', BasePrice: '$75', LocationMultiplier: '1.10', DemandMultiplier: '1.18', UrgencyMultiplier: '1.00', CorporateDiscount: '0%', Status: 'Admin approval required' },
      { Rule: 'Corporate Office Monthly', BasePrice: '$420', LocationMultiplier: '1.00', DemandMultiplier: '1.00', UrgencyMultiplier: '1.00', CorporateDiscount: '12%', Status: 'Approved' },
      { Rule: 'Emergency Same-Day', BasePrice: '$95', LocationMultiplier: '1.15', DemandMultiplier: '1.20', UrgencyMultiplier: '1.35', CorporateDiscount: '0%', Status: 'Review' },
    ],
    'sla-management': [
      { Account: 'Lakeview Office', ResponseTime: '2 hrs', ArrivalTime: '98%', CompletionTime: '96%', QualityScore: '94%', SLACompliance: '97%', Status: 'Healthy' },
      { Account: 'Garden Plaza', ResponseTime: '4 hrs', ArrivalTime: '91%', CompletionTime: '93%', QualityScore: '89%', SLACompliance: '90%', Status: 'At risk' },
    ],
    documents: [
      { Document: 'Corporate Contract', Owner: 'Lakeview Office', Version: 'v3', Expiry: 'Dec 31', Permission: 'Executive/Admin', Status: 'Valid' },
      { Document: 'Cleaner NIN Copy', Owner: 'Mia W.', Version: 'v1', Expiry: 'Aug 14', Permission: 'HR/Admin', Status: 'Expiry alert' },
      { Document: 'Supplier Agreement', Owner: 'Sparkle Supply', Version: 'v2', Expiry: 'Nov 10', Permission: 'Procurement/Admin', Status: 'Valid' },
    ],
    'knowledge-center': [
      { Category: 'Cleaning Procedures', Article: 'Deep Clean SOP', Version: '2.1', Bookmarks: '48', Status: 'Published' },
      { Category: 'Safety Procedures', Article: 'Chemical Handling', Version: '1.8', Bookmarks: '32', Status: 'Review due' },
      { Category: 'Customer Service', Article: 'Complaint Closeout', Version: '1.3', Bookmarks: '19', Status: 'Published' },
    ],
    clients: [
      { Client: 'Amina K.', Segment: 'VIP', LastBooking: 'Jun 1', Status: 'Active' },
      { Client: 'Jacob H.', Segment: 'Standard', LastBooking: 'May 21', Status: 'Follow-up due' },
    ],
    cleaners: [
      { Cleaner: 'Mia W.', Rating: '4.9', Jobs: '4 today', Status: 'Available' },
      { Cleaner: 'Samir P.', Rating: '4.7', Jobs: '3 today', Status: 'On route' },
    ],
    'admin-bookings': [
      { Booking: 'SC-201', Client: 'Amina K.', Cleaner: 'Mia W.', Status: 'Confirmed' },
      { Booking: 'SC-202', Client: 'Jacob H.', Cleaner: 'Samir P.', Status: 'Pending dispatch' },
    ],
    dispatch: [
      { Route: 'Central AM', Cleaner: 'Mia W.', Stops: '4', Status: 'Active' },
      { Route: 'Northside PM', Cleaner: 'Tariq S.', Stops: '3', Status: 'Needs coverage' },
    ],
    'crm-center': [
      { ClientName: 'Amina K.', Phone: '+1 555 0101', LastBooking: 'Jun 1', LastContact: 'Jun 3', RiskScore: 'Low', LifetimeValue: '$2,840', AssignedAgent: 'Nora', Action: 'View Profile' },
      { ClientName: 'Jacob H.', Phone: '+1 555 0102', LastBooking: 'Apr 22', LastContact: 'May 1', RiskScore: 'High', LifetimeValue: '$1,420', AssignedAgent: 'Iris', Action: 'Create Follow-Up' },
    ],
    'workforce-management': [
      { Cleaner: 'Mia W.', Attendance: '98%', Training: 'Complete', Certification: 'Valid', Status: 'Top performer' },
      { Cleaner: 'Tariq S.', Attendance: '89%', Training: 'Due', Certification: 'Renewal soon', Status: 'Review' },
    ],
    'quality-assurance': [
      { Case: 'QA-204', Job: 'SC-190', Score: '92%', Finding: 'Passed', Status: 'Recorded' },
      { Case: 'QA-205', Job: 'SC-191', Score: '68%', Finding: 'Photo mismatch', Status: 'Supervisor review' },
    ],
    'complaints-disputes': [
      { Case: 'SC-917', Type: 'Complaint', Priority: 'Medium', Owner: 'Customer Success', Status: 'Investigating' },
      { Case: 'RF-302', Type: 'Refund Request', Priority: 'High', Owner: 'Finance', Status: 'Approval required' },
    ],
    incidents: [
      { Incident: 'INC-118', Type: 'Property Damage', Severity: 'Medium', Evidence: 'Photos attached', Investigation: 'Supervisor review', Status: 'Open' },
      { Incident: 'INC-119', Type: 'Safety Violation', Severity: 'High', Evidence: 'Report pending', Investigation: 'Assigned', Status: 'Risk scored' },
    ],
    suppliers: [
      { Supplier: 'Sparkle Supply', Category: 'Chemicals', LeadTime: '3 days', Status: 'Active' },
      { Supplier: 'CleanOps', Category: 'Equipment', LeadTime: '5 days', Status: 'Backup' },
    ],
    procurement: [
      { Request: 'PR-204', Item: 'Disinfectant', Quantity: '80 units', Supplier: 'BioSafe', Amount: '$640', Approval: 'Required', Status: 'Review' },
      { Request: 'PR-205', Item: 'Uniforms', Quantity: '24', Supplier: 'CleanOps', Amount: '$720', Approval: 'Pending', Status: 'Queued' },
    ],
    'asset-management': [
      { AssetID: 'VAC-018', Category: 'Vacuum Cleaner', PurchaseDate: 'Jan 12', Condition: 'Good', AssignedUser: 'Mia W.', MaintenanceSchedule: 'Jun 20', Status: 'Active' },
      { AssetID: 'PHN-044', Category: 'Phone', PurchaseDate: 'Mar 4', Condition: 'Fair', AssignedUser: 'Samir P.', MaintenanceSchedule: 'Jul 2', Status: 'Inspect' },
      { AssetID: 'PRS-003', Category: 'Pressure Washer', PurchaseDate: 'Sep 18', Condition: 'Service due', AssignedUser: 'Warehouse', MaintenanceSchedule: 'Jun 8', Status: 'Maintenance forecast' },
    ],
    'vehicle-management': [
      { Vehicle: 'UG-204A', Driver: 'Tariq S.', FuelUsage: 'Normal', Maintenance: 'Jun 18', Insurance: 'Valid', Status: 'Active' },
      { Vehicle: 'UG-305B', Driver: 'Mia W.', FuelUsage: 'Above baseline', Maintenance: 'Jul 1', Insurance: 'Valid', Status: 'AI anomaly review' },
    ],
    'finance-control': [
      { Account: 'Client invoices', Value: '$18,420', Status: 'Healthy', Owner: 'Finance Admin' },
      { Account: 'Cleaner payouts', Value: '$7,840', Status: 'Pending review', Owner: 'Operations' },
    ],
    'marketing-center': [
      { Campaign: 'Inactive Clients Winback', Channel: 'Email', Audience: '142', Status: 'Needs approval' },
      { Campaign: 'Referral Boost', Channel: 'SMS', Audience: '83', Status: 'Draft' },
    ],
    'territory-expansion': [
      { Zone: 'Northside', Demand: 'High', CleanerCoverage: 'Medium', ExpansionScore: '82' },
      { Zone: 'West End', Demand: 'Medium', CleanerCoverage: 'High', ExpansionScore: '67' },
    ],
    'corporate-accounts': [
      { Account: 'Lakeview Office', Contract: 'Monthly', AccountManager: 'Nora', SLACompliance: '97%', Billing: 'Corporate invoice', RenewalForecast: 'Healthy', Status: 'Active' },
      { Account: 'Garden Plaza', Contract: 'Quarterly', AccountManager: 'Iris', SLACompliance: '90%', Billing: 'Corporate invoice', RenewalForecast: 'At risk', Status: 'Renewal due' },
    ],
    notifications: [
      { Template: 'Booking Reminder', Channel: 'WhatsApp', DeliveryRate: '98%', ReadRate: '82%', Retry: 'Enabled', Schedule: 'Automated' },
      { Template: 'Invoice Due', Channel: 'Email', DeliveryRate: '96%', ReadRate: '64%', Retry: 'Enabled', Schedule: 'Daily' },
    ],
    'business-intelligence': [
      { Submodule: 'Executive Dashboard', Metric: 'Revenue trend', Insight: '+12% MTD', Forecast: 'Stable', Status: 'Live' },
      { Submodule: 'Territory Analytics', Metric: 'Underserved zones', Insight: '2 high-demand areas', Forecast: 'Expansion opportunity', Status: 'Review' },
      { Submodule: 'AI Forecasting', Metric: 'Weekend demand', Insight: '+21%', Forecast: 'Needs capacity', Status: 'Live' },
    ],
    'executive-copilot': [
      { Question: 'Why is revenue down?', Evidence: 'Corporate payments delayed three days', Recommendation: 'Review AR follow-ups', ActionPolicy: 'Never takes action' },
      { Question: 'Which customers are at risk?', Evidence: 'Reduced bookings and open complaints', Recommendation: 'Assign retention review', ActionPolicy: 'Recommendation only' },
      { Question: 'What inventory needs restocking?', Evidence: 'Disinfectant below forecast', Recommendation: 'Review purchase request', ActionPolicy: 'Approval required' },
    ],
    'approval-center': [
      { RequestType: 'Refund approval', RequestedBy: 'Finance', Priority: 'High', Date: 'Jun 5', Status: 'Review', Action: 'Approve / Reject' },
      { RequestType: 'AI campaign recommendation', RequestedBy: 'AI Assistant', Priority: 'Medium', Date: 'Jun 5', Status: 'Queued', Action: 'Review' },
    ],
    'audit-logs': [
      { Timestamp: '08:42', User: 'System Admin', Role: 'Admin', Action: 'Settings viewed', PreviousValue: 'None', NewValue: 'Read access', IPAddress: '10.0.0.24', Status: 'Allowed' },
      { Timestamp: '10:02', User: 'Executive Admin', Role: 'Executive', Action: 'AI recommendation approved', PreviousValue: 'Queued', NewValue: 'Approved', IPAddress: '10.0.0.18', Status: 'Logged' },
      { Timestamp: '11:15', User: 'Finance Manager', Role: 'Finance', Action: 'Refund review opened', PreviousValue: 'Pending', NewValue: 'Under review', IPAddress: '10.0.0.44', Status: 'Recorded' },
    ],
    'security-center': [
      { Control: 'Authentication', Requirement: 'Email, password, MFA, Google Login', Status: 'Enabled', Owner: 'System Admin' },
      { Control: 'Permissions', Requirement: 'RBAC, feature, page, action-level checks', Status: 'Configured', Owner: 'System Admin' },
      { Control: 'Security Logs', Requirement: 'Required', Status: 'Active', Owner: 'System Admin' },
    ],
    'backup-recovery': [
      { Backup: 'Daily', LastRun: 'Jun 5, 02:00', Storage: 'S3-compatible', RecoveryTest: 'Passed', Status: 'Healthy' },
      { Backup: 'Weekly', LastRun: 'Jun 1, 03:00', Storage: 'S3-compatible', RecoveryTest: 'Passed', Status: 'Healthy' },
      { Backup: 'Monthly', LastRun: 'Jun 1, 04:00', Storage: 'S3-compatible', RecoveryTest: 'Scheduled', Status: 'Ready' },
    ],
    'system-health': [
      { Service: 'Server', Status: 'Healthy', Uptime: '99.98%', AIInsight: 'Normal baseline' },
      { Service: 'Database', Status: 'Healthy', Uptime: '99.99%', AIInsight: 'No anomaly' },
      { Service: 'Notification Services', Status: 'Watch', Uptime: '99.20%', AIInsight: 'Retry rate elevated' },
      { Service: 'Payment Services', Status: 'Healthy', Uptime: '99.95%', AIInsight: 'Stable' },
    ],
    'multi-city': [
      { Country: 'Uganda', Region: 'Central', District: 'Kampala', City: 'Kampala', Zone: 'Northside', Branch: 'Branch A', Status: 'Active' },
      { Country: 'Uganda', Region: 'Central', District: 'Wakiso', City: 'Entebbe', Zone: 'Airport Road', Branch: 'Branch B', Status: 'Expansion review' },
    ],
    messages: [
      { Thread: 'Booking SC-201', From: 'Operations', Updated: 'Today', Status: 'Open' },
      { Thread: 'Support SC-1042', From: 'Support', Updated: 'Yesterday', Status: 'Replied' },
    ],
    support: [
      { Ticket: 'SC-1042', Topic: 'Invoice question', Updated: 'Today', Status: 'Open' },
      { Ticket: 'SC-1038', Topic: 'Cleaner arrival', Updated: 'May 31', Status: 'Closed' },
    ],
    'route-planner': [
      { Stop: 'Lakeview Office', ETA: '9:00 AM', Distance: '2.1 mi', Status: 'Next' },
      { Stop: 'Downtown Apartment', ETA: '1:30 PM', Distance: '4.8 mi', Status: 'Queued' },
    ],
    'bookings-operations': [
      { Booking: 'SC-201', Window: '10:00 AM', Cleaner: 'Mia W.', Status: 'Confirmed' },
      { Booking: 'SC-202', Window: '2:00 PM', Cleaner: 'Unassigned', Status: 'Dispatch needed' },
    ],
    'cleaner-schedules': [
      { Cleaner: 'Mia W.', Shift: '8:00 AM - 3:00 PM', Jobs: '4', Status: 'Full' },
      { Cleaner: 'Samir P.', Shift: '11:00 AM - 6:00 PM', Jobs: '3', Status: 'Open capacity' },
    ],
    'route-management': [
      { Route: 'Central AM', Stops: '4', ETAHealth: 'On time', Status: 'Active' },
      { Route: 'Northside PM', Stops: '3', ETAHealth: 'Risk', Status: 'Review' },
    ],
    'territory-operations': [
      { Territory: 'Central', Bookings: '42', Coverage: '88%', Status: 'Add capacity' },
      { Territory: 'Northside', Bookings: '31', Coverage: '96%', Status: 'Covered' },
    ],
    'delivery-tracking': [
      { Service: 'SC-201', Cleaner: 'Mia W.', Stage: 'On site', Status: 'Active' },
      { Service: 'SC-202', Cleaner: 'Samir P.', Stage: 'En route', Status: 'On time' },
    ],
    complaints: [
      { Case: 'SC-917', Client: 'Amina K.', Owner: 'Nora', Status: 'Investigating' },
      { Case: 'SC-921', Client: 'Noor M.', Owner: 'Iris', Status: 'Reply drafted' },
    ],
    'client-follow-ups': [
      { Client: 'Jacob H.', Reason: 'Inactive 60 days', Agent: 'Iris', Status: 'Call due' },
      { Client: 'Noor M.', Reason: 'Low rating', Agent: 'Nora', Status: 'Email drafted' },
    ],
    'vip-clients': [
      { Client: 'Amina K.', LifetimeValue: '$2,840', LastContact: 'Jun 3', Status: 'Active' },
      { Client: 'Lakeview Office', LifetimeValue: '$9,200', LastContact: 'Jun 1', Status: 'Renewal watch' },
    ],
    'at-risk-clients': [
      { Client: 'Jacob H.', RiskScore: 'High', Trigger: 'Reduced bookings', Status: 'Follow-up assigned' },
      { Client: 'Noor M.', RiskScore: 'Medium', Trigger: 'Complaint open', Status: 'Monitor' },
    ],
    'campaign-performance': [
      { Campaign: 'Winback', Conversion: '12%', Revenue: '$1,240', Status: 'Active' },
      { Campaign: 'Referral Boost', Conversion: '8%', Revenue: '$840', Status: 'Draft' },
    ],
    'cleaner-records': [
      { Cleaner: 'Mia W.', Attendance: '98%', Rating: '4.9', Status: 'Active' },
      { Cleaner: 'Tariq S.', Attendance: '89%', Rating: '4.6', Status: 'Training due' },
    ],
    training: [
      { Module: 'Service Standards', Assigned: '34 cleaners', Completion: '82%', Status: 'In progress' },
      { Module: 'Photo Evidence', Assigned: '12 cleaners', Completion: '66%', Status: 'Due' },
    ],
    attendance: [
      { Cleaner: 'Mia W.', CheckIns: '22/22', Late: '0', Status: 'Excellent' },
      { Cleaner: 'Samir P.', CheckIns: '20/22', Late: '2', Status: 'Review' },
    ],
    'leave-requests': [
      { Cleaner: 'Tariq S.', Dates: 'Jun 10 - Jun 12', Coverage: 'Needed', Status: 'Pending' },
      { Cleaner: 'Mia W.', Dates: 'Jun 20', Coverage: 'Covered', Status: 'Approved' },
    ],
    'performance-plans': [
      { Cleaner: 'Samir P.', Focus: 'Arrival time', Owner: 'HR', Status: 'Active' },
      { Cleaner: 'Tariq S.', Focus: 'Closeout notes', Owner: 'Supervisor', Status: 'Draft' },
    ],
    certifications: [
      { Cleaner: 'Mia W.', Certification: 'Deep Clean', Expires: 'Sep 10', Status: 'Valid' },
      { Cleaner: 'Tariq S.', Certification: 'Chemical Safety', Expires: 'Jun 22', Status: 'Renew soon' },
    ],
    'finance-invoices': [
      { Invoice: 'INV-321', Client: 'Amina K.', Amount: '$120', Status: 'Paid' },
      { Invoice: 'INV-322', Client: 'Jacob H.', Amount: '$75', Status: 'Due' },
    ],
    payments: [
      { Payment: 'PAY-118', Source: 'Wallet', Amount: '$75', Status: 'Processed' },
      { Payment: 'PAY-119', Source: 'Card', Amount: '$120', Status: 'Settled' },
    ],
    'refund-approvals': [
      { Refund: 'RF-302', Client: 'Noor M.', Amount: '$45', Reason: 'Complaint', Status: 'Approval needed' },
      { Refund: 'RF-303', Client: 'Jacob H.', Amount: '$25', Reason: 'Late arrival', Status: 'Review' },
    ],
    'profit-loss': [
      { Category: 'Revenue', Month: 'June', Amount: '$12,450', Status: 'Tracking' },
      { Category: 'Operating Costs', Month: 'June', Amount: '$6,180', Status: 'Tracking' },
    ],
    'cash-flow': [
      { Account: 'Corporate AR', Trend: '3 days slower', Amount: '$4,800', Status: 'Follow-up' },
      { Account: 'Client Wallets', Trend: 'Stable', Amount: '$2,120', Status: 'Healthy' },
    ],
    expenses: [
      { Expense: 'Supplies', Amount: '$1,240', Owner: 'Operations', Status: 'Approved' },
      { Expense: 'Route fuel', Amount: '$620', Owner: 'Dispatch', Status: 'Review' },
    ],
    'payroll-summaries': [
      { Batch: 'June Week 1', Cleaners: '34', Amount: '$7,840', Status: 'Pending approval' },
      { Batch: 'May Week 4', Cleaners: '32', Amount: '$7,210', Status: 'Paid' },
    ],
  };

  return tableRows[selectedItem] ? <ModulePanel title={selectedItem.split('-').map((word) => word[0].toUpperCase() + word.slice(1)).join(' ')} rows={tableRows[selectedItem]} /> : <OverviewPanel role={role} />;
}

export default function App() {
  const pathname = usePathname();
  const initialUser = getBackendSession()?.user ?? null;
  const [role, setRole] = useState<Role>(backendRoleToUiRole[initialUser?.role ?? ''] ?? pathToRole[pathname] ?? 'client');
  const [selectedItem, setSelectedItem] = useState<string>(roleMenuItems.client[0].key);
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const [backendUser, setBackendUser] = useState<BackendUser | null>(() => initialUser);
  const [backendStatus, setBackendStatus] = useState<'offline' | 'connecting' | 'connected' | 'error'>(() =>
    getBackendSession() ? 'connected' : 'offline',
  );
  const [dashboardSummary, setDashboardSummary] = useState<DashboardSummary | null>(null);
  const [dashboardLoading, setDashboardLoading] = useState(false);
  const [dashboardError, setDashboardError] = useState<string | null>(null);

  const authRoutes = new Set([
    '/login',
    '/register',
    '/verify-account',
    '/verify-mfa',
    '/forgot-password',
    '/reset-password',
    '/account-locked',
    '/application-status',
    '/onboarding',
    '/session-expired',
    '/unauthorized',
  ]);

  useEffect(() => {
    const routeRole = pathToRole[pathname];
    if (!routeRole) return;
    const userRole = backendRoleToUiRole[backendUser?.role ?? ''];
    if (!backendUser) {
      window.history.replaceState({}, '', '/login');
      window.dispatchEvent(new PopStateEvent('popstate'));
      return;
    }
    if (userRole && routeRole !== userRole) {
      window.history.replaceState({}, '', '/unauthorized');
      window.dispatchEvent(new PopStateEvent('popstate'));
      return;
    }
    setRole(routeRole);
  }, [pathname, backendUser]);

  useEffect(() => {
    setSelectedItem(roleMenuItems[role][0].key);
    setSidebarOpen(false);
  }, [role]);

  const menuItems = useMemo(() => roleMenuItems[role], [role]);
  const activeItem = menuItems.find((item) => item.key === selectedItem) ?? menuItems[0];
  const copy = pageCopy[role];
  const connectedMetrics = dashboardSummary?.metrics.map((metric) => ({
    icon: metric.icon,
    label: metric.label,
    value: metric.value,
    trend: metric.trend ?? undefined,
  }));

  useEffect(() => {
    if (backendStatus !== 'connected') {
      setDashboardSummary(null);
      setDashboardError(null);
      setDashboardLoading(false);
      return;
    }
    void refreshDashboardSummary();
  }, [backendStatus]);

  async function refreshDashboardSummary() {
    setDashboardLoading(true);
    setDashboardError(null);
    try {
      const response = await loadDashboardSummary();
      setDashboardSummary(response.data);
    } catch (error) {
      setDashboardError(error instanceof Error ? error.message : 'Dashboard request failed');
    } finally {
      setDashboardLoading(false);
    }
  }

  async function connectBackend() {
    setBackendStatus('connecting');
    try {
      const session = await connectDemoBackendSession();
      setBackendUser(session.user);
      setBackendStatus('connected');
    } catch {
      clearBackendSession();
      setBackendUser(null);
      setBackendStatus('error');
    }
  }

  function disconnectBackend() {
    clearBackendSession();
    setBackendUser(null);
    setBackendStatus('offline');
    setDashboardSummary(null);
  }

  function handleAuthenticated(user: BackendUser) {
    setBackendUser(user);
    setBackendStatus('connected');
    setRole(backendRoleToUiRole[user.role] ?? 'client');
  }

  function handleDashboardAction(action: DashboardAction) {
    if (action.actionType === 'OPEN_ROUTE' && roleMenuItems[role].some((item) => item.key === action.targetRoute)) {
      setSelectedItem(action.targetRoute);
      setSidebarOpen(false);
    }
  }

  if (pathname === '/' && !backendUser) {
    window.history.replaceState({}, '', '/login');
  }

  if (authRoutes.has(pathname)) {
    if (pathname === '/login') return <LoginPage onAuthenticated={handleAuthenticated} />;
    if (pathname === '/register') return <RegisterPage />;
    if (pathname === '/verify-account') return <VerifyAccountPage />;
    if (pathname === '/verify-mfa') return <VerifyMfaPage onAuthenticated={handleAuthenticated} />;
    if (pathname === '/forgot-password') return <ForgotPasswordPage />;
    if (pathname === '/reset-password') return <ResetPasswordPage />;
    if (pathname === '/application-status') return <ApplicationStatusPage />;
    if (pathname === '/account-locked') return <SimpleAuthPage type="locked" />;
    if (pathname === '/session-expired') return <SimpleAuthPage type="expired" />;
    if (pathname === '/unauthorized') return <SimpleAuthPage type="unauthorized" />;
    if (pathname === '/onboarding') return <SimpleAuthPage type="onboarding" />;
  }

  const expectedPath = dashboardPathFor(backendUser);
  if (backendUser && pathname === '/') {
    window.history.replaceState({}, '', expectedPath);
  }

  return (
    <PageLayout
      menuItems={menuItems}
      activeItem={activeItem.key}
      onSelectItem={(key) => {
        setSelectedItem(key);
        setSidebarOpen(false);
      }}
      currentRole={role}
      onRoleChange={setRole}
      isSidebarOpen={sidebarOpen}
      toggleSidebar={() => setSidebarOpen((value) => !value)}
      closeSidebar={() => setSidebarOpen(false)}
      pageTitle={activeItem.key === 'dashboard' ? copy.title : activeItem.label}
      pageSubtitle={activeItem.key === 'dashboard' ? copy.subtitle : `${roleLabels[role]} access-controlled workspace`}
      backendUser={backendUser}
      backendStatus={backendStatus}
      onConnectBackend={connectBackend}
      onDisconnectBackend={disconnectBackend}
    >
      <DashboardDataState
        loading={dashboardLoading}
        error={dashboardError}
        emptyStates={dashboardSummary?.emptyStates ?? []}
        onRetry={refreshDashboardSummary}
      />
      <KPISection cards={connectedMetrics ?? metricsByRole[role]} />
      <AIInsightsSection role={role} />
      <QuickActions role={role} backendActions={dashboardSummary?.quickActions} onAction={handleDashboardAction} />
      <main className="main-work-area">{renderMainContent(role, activeItem.key)}</main>
      <ActivityFeed role={role} />
      <AuditTrail selectedItem={activeItem.key} />
    </PageLayout>
  );
}
