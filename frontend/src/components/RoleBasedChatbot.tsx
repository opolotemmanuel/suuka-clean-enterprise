import { FormEvent, useEffect, useMemo, useState } from 'react';
import { backendFetch, getBackendSession } from '../api/backend';
import { roleOptions, type Role } from '../data/roleActivities';

type ChatMessage = {
  id: number;
  sender: 'assistant' | 'user';
  text: string;
  responseType?: 'allowed' | 'denied' | 'approval_required';
};

type ChatAuditLog = {
  date: string;
  user: string;
  role: Role;
  question: string;
  dataAccessed: string;
  responseType: string;
  permissionResult: string;
  actionRequested: string;
  status: string;
};

type BackendChatbotResponse = {
  answer: string;
  responseType: 'allowed' | 'denied' | 'approval_required';
  permissionResult: string;
  dataAccessed: string;
  actionRequested: string;
  status: string;
};

type RolePolicy = {
  scope: string[];
  prompts: string[];
  allowedTopics: string[];
  deniedTopics: string[];
};

type Props = {
  currentRole: Role;
};

const criticalActionKeywords = [
  'approve refund',
  'approve refunds',
  'process refund',
  'suspend',
  'delete',
  'change price',
  'change prices',
  'send campaign',
  'process payment',
  'purchase inventory',
  'modify permissions',
  'change system settings',
  'terminate',
  'pay cleaner',
];

const rolePolicies: Record<Role, RolePolicy> = {
  client: {
    scope: ['own_bookings', 'own_invoices', 'own_payments', 'own_reviews', 'own_loyalty', 'support'],
    prompts: ['Show my next booking', 'Recommend a cleaning plan', 'Check my invoice', 'Contact support'],
    allowedTopics: ['booking', 'invoice', 'payment', 'review', 'loyalty', 'cleaning plan', 'support'],
    deniedTopics: ['cleaner earning', 'other client', 'admin report', 'company revenue', 'inventory', 'ai intelligence', 'audit log', 'system setting'],
  },
  cleaner: {
    scope: ['assigned_jobs', 'own_earnings', 'own_schedule', 'own_route', 'supply_requests', 'training'],
    prompts: ['Show today\'s jobs', 'Suggest best route', 'Check my earnings', 'Request supplies'],
    allowedTopics: ['job', 'earning', 'schedule', 'route', 'supply', 'performance tip', 'training'],
    deniedTopics: ['client financial', 'other cleaner', 'admin analytics', 'company revenue', 'inventory purchase', 'audit log', 'system setting'],
  },
  supervisor: {
    scope: ['assigned_teams', 'cleaner_performance_summaries', 'quality_checks', 'assigned_complaints', 'territory_coverage'],
    prompts: ['Show team issues', 'Show quality alerts', 'Check assigned complaints', 'Recommend training'],
    allowedTopics: ['team', 'performance summary', 'quality', 'assigned complaint', 'territory', 'training'],
    deniedTopics: ['payroll', 'finance report', 'system setting', 'ai configuration', 'audit log'],
  },
  operations: {
    scope: ['dispatch', 'bookings_operations', 'cleaner_schedules', 'routes', 'territory_operations'],
    prompts: ['Show dispatch risks', 'Rebalance routes', 'Check late jobs', 'Show territory coverage'],
    allowedTopics: ['dispatch', 'booking operation', 'schedule', 'route', 'territory', 'delivery'],
    deniedTopics: ['payroll', 'full financial', 'system setting', 'ai configuration', 'audit log'],
  },
  'customer-success': {
    scope: ['crm', 'complaints', 'client_followups', 'vip_clients', 'at_risk_clients', 'campaign_performance'],
    prompts: ['Show customers at risk', 'Check open complaints', 'Create follow-up notes', 'Show VIP clients'],
    allowedTopics: ['crm', 'complaint', 'follow-up', 'vip', 'at-risk', 'campaign performance', 'customer health'],
    deniedTopics: ['cleaner payroll', 'inventory purchase', 'system setting', 'audit log'],
  },
  workforce: {
    scope: ['cleaner_records', 'training', 'attendance', 'leave_requests', 'certifications', 'performance_plans'],
    prompts: ['Show attendance issues', 'Recommend training', 'Check certifications', 'Review leave requests'],
    allowedTopics: ['cleaner record', 'training', 'attendance', 'leave', 'certification', 'performance plan'],
    deniedTopics: ['client financial', 'revenue analytics', 'system setting', 'audit log'],
  },
  finance: {
    scope: ['invoices', 'payments', 'refund_approvals', 'profit_loss', 'cash_flow', 'expenses', 'payroll_summaries'],
    prompts: ['Show refund queue', 'Check cash flow', 'Summarize invoices', 'Review payroll summaries'],
    allowedTopics: ['invoice', 'payment', 'refund', 'profit', 'loss', 'cash flow', 'expense', 'payroll'],
    deniedTopics: ['ai system setting', 'user deletion', 'cleaner suspension', 'system setting'],
  },
  'inventory-procurement': {
    scope: ['inventory', 'suppliers', 'procurement', 'assets', 'vehicles', 'documents'],
    prompts: ['Show inventory alerts', 'Check purchase requests', 'Show supplier status', 'Check asset maintenance'],
    allowedTopics: ['inventory', 'supplier', 'procurement', 'asset', 'vehicle', 'document', 'stock'],
    deniedTopics: ['payroll', 'client financial', 'user deletion', 'system setting', 'audit log'],
  },
  admin: {
    scope: ['security', 'roles', 'system_health', 'backups', 'settings', 'audit_summaries', 'ai_recommendations'],
    prompts: ['Show security events', 'Check backup status', 'Show system health', 'Summarize audit activity'],
    allowedTopics: ['security', 'role', 'system health', 'backup', 'setting', 'audit summary', 'ai recommendation', 'approval queue'],
    deniedTopics: [],
  },
  'executive-admin': {
    scope: ['executive_summary', 'client_intelligence', 'cleaner_intelligence', 'revenue_trends', 'inventory_alerts', 'service_gaps', 'audit_summaries', 'ai_recommendations', 'approval_queue'],
    prompts: ['Summarize today\'s operations', 'Show customers at risk', 'Show inventory alerts', 'Show pending approvals', 'Forecast revenue'],
    allowedTopics: ['executive', 'client intelligence', 'cleaner intelligence', 'revenue', 'inventory', 'service gap', 'audit summary', 'ai recommendation', 'approval queue', 'forecast'],
    deniedTopics: [],
  },
};

const roleLabelByValue = Object.fromEntries(roleOptions.map((role) => [role.value, role.label])) as Record<Role, string>;

function includesAny(source: string, keywords: string[]) {
  return keywords.some((keyword) => source.includes(keyword));
}

function detectAction(question: string) {
  const normalized = question.toLowerCase();
  return criticalActionKeywords.find((keyword) => normalized.includes(keyword)) ?? 'information_request';
}

function evaluateQuestion(role: Role, question: string) {
  const normalized = question.toLowerCase();
  const policy = rolePolicies[role];
  const actionRequested = detectAction(normalized);

  if (actionRequested !== 'information_request') {
    return {
      allowed: false,
      responseType: 'approval_required' as const,
      actionRequested,
      dataAccessed: 'critical_action',
      answer: 'This action requires authorized human approval. I can prepare a recommendation for review.',
    };
  }

  if (includesAny(normalized, policy.deniedTopics)) {
    return {
      allowed: false,
      responseType: 'denied' as const,
      actionRequested,
      dataAccessed: 'restricted_topic',
      answer: 'You do not have permission to access this information.',
    };
  }

  const matchedTopic = policy.allowedTopics.find((topic) => normalized.includes(topic));
  const dataAccessed = matchedTopic ?? policy.scope[0];

  return {
    allowed: true,
    responseType: 'allowed' as const,
    actionRequested,
    dataAccessed,
    answer: buildRoleAnswer(role, matchedTopic ?? 'overview'),
  };
}

function buildRoleAnswer(role: Role, topic: string) {
  const answers: Record<Role, string> = {
    client: 'I can help with your bookings, invoices, payments, reviews, loyalty points, cleaning recommendations, and support requests. Your next booking is Jun 12 at 2:00 PM.',
    cleaner: 'I can help with your assigned jobs, earnings, schedule, route, supply request status, performance tips, and training materials. Today you have 3 assigned jobs.',
    supervisor: 'I can summarize assigned team issues, cleaner performance summaries, quality checks, assigned complaints, territory coverage, and training recommendations.',
    operations: 'I can help with dispatch, bookings operations, cleaner schedules, routes, territory operations, and service delivery tracking.',
    'customer-success': 'I can help with CRM follow-ups, complaints, VIP clients, at-risk customers, campaign performance, and customer health signals.',
    workforce: 'I can help with cleaner records, training, attendance, leave requests, certifications, and performance plans.',
    finance: 'I can help with invoices, payments, refund queues, profit and loss, cash flow, expenses, and payroll summaries. Critical finance actions still require approval.',
    'inventory-procurement': 'I can help with inventory alerts, suppliers, procurement requests, assets, vehicles, and document expiry tracking. Purchases require approval.',
    admin: 'I can summarize security events, role activity, system health, backups, settings, audit summaries, and AI recommendations. Critical changes require confirmation.',
    'executive-admin': 'I can summarize operations, client intelligence, cleaner intelligence, revenue trends, inventory alerts, service gaps, audit summaries, AI recommendations, approval queues, and forecasts.',
  };

  return `${answers[role]} Requested scope: ${topic}.`;
}

function createAuditLog(role: Role, question: string, result: ReturnType<typeof evaluateQuestion>): ChatAuditLog {
  return {
    date: new Date().toISOString(),
    user: 'demo-user-001',
    role,
    question,
    dataAccessed: result.dataAccessed,
    responseType: result.responseType,
    permissionResult: result.allowed ? 'allowed' : 'blocked',
    actionRequested: result.actionRequested,
    status: 'recorded',
  };
}

async function sendBackendChatRequest(role: Role, question: string, result: ReturnType<typeof evaluateQuestion>, scope: string[]) {
  const session = getBackendSession();
  if (!session) return null;

  const payload = {
    userId: session.user.id,
    role,
    permissionScope: scope,
    tenant: 'suuka-clean',
    branch: session.user.branch ?? 'central-branch',
    sessionId: 'demo-session-001',
    question,
    requestedAction: result.actionRequested,
  };

  const response = await backendFetch<BackendChatbotResponse>('/api/chatbot/message', {
    method: 'POST',
    body: JSON.stringify(payload),
  });
  return response.data;
}

export default function RoleBasedChatbot({ currentRole }: Props) {
  const [isOpen, setIsOpen] = useState(false);
  const [inputValue, setInputValue] = useState('');
  const [auditLogs, setAuditLogs] = useState<ChatAuditLog[]>([]);
  const policy = rolePolicies[currentRole];
  const initialMessages = useMemo<ChatMessage[]>(
    () => [
      {
        id: 1,
        sender: 'assistant',
        text: `I am Suuka AI Assistant. I can only answer within your ${roleLabelByValue[currentRole]} permissions.`,
        responseType: 'allowed',
      },
    ],
    [currentRole],
  );
  const [messages, setMessages] = useState<ChatMessage[]>(initialMessages);

  useEffect(() => {
    setMessages(initialMessages);
    setInputValue('');
  }, [initialMessages]);

  function resetForRole() {
    setMessages(initialMessages);
    setInputValue('');
  }

  async function sendQuestion(question: string) {
    const trimmed = question.trim();
    if (!trimmed) return;

    const result = evaluateQuestion(currentRole, trimmed);
    let answer = result.answer;
    let responseType = result.responseType;
    let auditLog = createAuditLog(currentRole, trimmed, result);

    try {
      const backendResponse = await sendBackendChatRequest(currentRole, trimmed, result, policy.scope);
      if (backendResponse) {
        answer = backendResponse.answer;
        responseType = backendResponse.responseType;
        auditLog = {
          date: new Date().toISOString(),
          user: getBackendSession()?.user.email ?? 'backend-user',
          role: currentRole,
          question: trimmed,
          dataAccessed: backendResponse.dataAccessed,
          responseType: backendResponse.responseType,
          permissionResult: backendResponse.permissionResult,
          actionRequested: backendResponse.actionRequested,
          status: backendResponse.status,
        };
      }
    } catch {
      answer = `${result.answer} Backend unavailable or permission denied; using local policy fallback.`;
    }

    const userMessage: ChatMessage = {
      id: Date.now(),
      sender: 'user',
      text: trimmed,
    };
    const assistantMessage: ChatMessage = {
      id: Date.now() + 1,
      sender: 'assistant',
      text: answer,
      responseType,
    };

    setMessages((current) => [...current, userMessage, assistantMessage]);
    setAuditLogs((current) => [auditLog, ...current].slice(0, 6));
    setInputValue('');
  }

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    sendQuestion(inputValue);
  }

  function handleToggle() {
    setIsOpen((value) => !value);
    if (!isOpen) resetForRole();
  }

  return (
    <aside className={`chatbot-shell ${isOpen ? 'open' : ''}`} aria-label="Suuka AI Assistant">
      {isOpen && (
        <section className="chatbot-panel">
          <header className="chatbot-header">
            <div>
              <h2>Suuka AI Assistant</h2>
              <p>Role-aware system assistant</p>
            </div>
            <button className="chatbot-icon-button" onClick={() => setIsOpen(false)} aria-label="Close assistant">
              <i className="fa-solid fa-xmark" aria-hidden="true" />
            </button>
          </header>

          <div className="chatbot-body">
            <div className="chatbot-permission-strip">
              <i className="fa-solid fa-lock" aria-hidden="true" />
              <span>{roleLabelByValue[currentRole]} scope active</span>
            </div>

            <div className="chatbot-messages">
              {messages.map((message) => (
                <div className={`chatbot-message ${message.sender} ${message.responseType ?? ''}`} key={message.id}>
                  <p>{message.text}</p>
                </div>
              ))}
            </div>

            <div className="chatbot-prompts" aria-label="Quick prompts">
              {policy.prompts.map((prompt) => (
                <button type="button" className="chatbot-prompt" key={prompt} onClick={() => sendQuestion(prompt)}>
                  {prompt}
                </button>
              ))}
            </div>

            {auditLogs[0] && (
              <div className="chatbot-audit-note">
                Last audit: {auditLogs[0].permissionResult} / {auditLogs[0].responseType}
              </div>
            )}
          </div>

          <form className="chatbot-input-row" onSubmit={handleSubmit}>
            <input
              value={inputValue}
              onChange={(event) => setInputValue(event.target.value)}
              placeholder="Ask within your role permissions"
              aria-label="Ask Suuka AI"
            />
            <button className="primary-button small-button" type="submit">
              <i className="fa-solid fa-paper-plane" aria-hidden="true" />
              <span>Send</span>
            </button>
          </form>
        </section>
      )}

      <button className="chatbot-toggle" onClick={handleToggle} aria-expanded={isOpen} aria-label="Open Suuka AI Assistant">
        <i className="fa-solid fa-robot" aria-hidden="true" />
      </button>
    </aside>
  );
}
