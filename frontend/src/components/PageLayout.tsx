import Sidebar from './Sidebar';
import RoleBasedChatbot from './RoleBasedChatbot';
import { roleOptions, type MenuItem, type Role } from '../data/roleActivities';
import { loadNotifications, markNotificationRead, type BackendNotification, type BackendUser } from '../api/backend';
import { useEffect, useState, type ReactNode } from 'react';

type Props = {
  menuItems: MenuItem[];
  activeItem: string;
  onSelectItem: (key: string) => void;
  currentRole: Role;
  onRoleChange: (role: Role) => void;
  isSidebarOpen: boolean;
  toggleSidebar: () => void;
  closeSidebar: () => void;
  pageTitle: string;
  pageSubtitle: string;
  backendUser: BackendUser | null;
  backendStatus: 'offline' | 'connecting' | 'connected' | 'error';
  onConnectBackend: () => void;
  onDisconnectBackend: () => void;
  children: ReactNode;
};

export default function PageLayout({
  menuItems,
  activeItem,
  onSelectItem,
  currentRole,
  onRoleChange,
  isSidebarOpen,
  toggleSidebar,
  closeSidebar,
  pageTitle,
  pageSubtitle,
  backendUser,
  backendStatus,
  onConnectBackend,
  onDisconnectBackend,
  children,
}: Props) {
  const isConnected = backendStatus === 'connected' && backendUser;
  const [notifications, setNotifications] = useState<BackendNotification[]>([]);
  const [selectedNotification, setSelectedNotification] = useState<BackendNotification | null>(null);
  const [notificationError, setNotificationError] = useState<string | null>(null);
  const unreadCount = notifications.filter((notification) => !notification.read).length;

  useEffect(() => {
    if (!isConnected) {
      setNotifications([]);
      setSelectedNotification(null);
      return;
    }
    void refreshNotifications();
  }, [isConnected]);

  async function refreshNotifications() {
    setNotificationError(null);
    try {
      const response = await loadNotifications();
      setNotifications(response.data);
    } catch (error) {
      setNotificationError(error instanceof Error ? error.message : 'Unable to load notifications');
    }
  }

  async function markSelectedRead() {
    if (!selectedNotification) return;
    const response = await markNotificationRead(selectedNotification.id);
    setSelectedNotification(response.data);
    await refreshNotifications();
  }

  return (
    <div className={`page-layout ${isSidebarOpen ? 'sidebar-visible' : ''}`}>
      <Sidebar
        menuItems={menuItems}
        activeItem={activeItem}
        currentRole={currentRole}
        onSelectItem={onSelectItem}
        isOpen={isSidebarOpen}
        onClose={closeSidebar}
      />
      <div className="content-shell">
        <header className="top-bar">
          <div className="top-left">
            <button className="mobile-menu-button" onClick={toggleSidebar} aria-label="Toggle menu">
              <i className="fa-solid fa-bars" aria-hidden="true" />
            </button>
            <div className="brand-block">
              <span className="brand-mark" />
              <div>
                <p className="brand-title">Suuka Clean</p>
                <p className="brand-note">Cleaning marketplace</p>
              </div>
            </div>
          </div>

          <div className="top-center">
            {currentRole === 'client' ? (
              <div className="location-picker">
                <i className="fa-solid fa-location-dot" aria-hidden="true" />
                <select aria-label="Select location">
                  <option>Current location</option>
                  <option>Central District</option>
                  <option>Northside</option>
                </select>
              </div>
            ) : (
              <div className="top-search">
                <i className="fa-solid fa-magnifying-glass" aria-hidden="true" />
                <input type="search" placeholder="Search clients, jobs, or reports" />
              </div>
            )}
          </div>

          <div className="top-right">
            <div className={`backend-chip ${backendStatus}`}>
              <span className="backend-dot" />
              <span>{isConnected ? `API: ${backendUser.role}` : backendStatus === 'connecting' ? 'API: Connecting' : 'API: Offline'}</span>
              <button
                className="backend-chip-button"
                onClick={isConnected ? onDisconnectBackend : onConnectBackend}
                type="button"
              >
                {isConnected ? 'Disconnect' : 'Connect'}
              </button>
            </div>
            <button className="icon-button" aria-label="Notifications" type="button" onClick={() => setSelectedNotification(notifications[0] ?? null)}>
              <i className="fa-solid fa-bell" aria-hidden="true" />
              {unreadCount > 0 && <span className="badge">{unreadCount}</span>}
            </button>
            <button className="icon-button" aria-label="Messages" type="button" onClick={() => onSelectItem('messages')}>
              <i className="fa-solid fa-comments" aria-hidden="true" />
            </button>
            <div className="profile-chip">
              <span className="profile-avatar">SC</span>
              <span className="profile-name">Suuka</span>
              <i className="fa-solid fa-caret-down" aria-hidden="true" />
            </div>
            <select className="role-select" value={currentRole} onChange={(e) => onRoleChange(e.target.value as Role)}>
              {roleOptions.map((option) => (
                <option value={option.value} key={option.value}>
                  {option.label}
                </option>
              ))}
            </select>
          </div>
        </header>

        <main className="content-area">
          <section className="page-header-section">
            <div>
              <p className="page-label">Dashboard</p>
              <h1>{pageTitle}</h1>
              <p className="page-description">{pageSubtitle}</p>
            </div>
            <div className="header-actions">
              <button className="secondary-button">Filter</button>
              <button className="secondary-button">Export</button>
            </div>
          </section>

          {children}
        </main>

        <nav className="bottom-nav" aria-label="Mobile navigation">
          {menuItems.slice(0, 5).map((item) => (
            <button
              key={item.key}
              className={activeItem === item.key ? 'bottom-nav-item active' : 'bottom-nav-item'}
              onClick={() => onSelectItem(item.key)}
              title={item.label}
            >
              <i className={`fa-solid ${item.icon}`} aria-hidden="true" />
              <span>{item.label}</span>
            </button>
          ))}
        </nav>

        <RoleBasedChatbot currentRole={currentRole} />
        {selectedNotification && (
          <aside className="notification-drawer" aria-label="Notification action drawer">
            <header className="notification-drawer-header">
              <div>
                <p className="section-label">{selectedNotification.type}</p>
                <h2>{selectedNotification.title}</h2>
              </div>
              <button className="chatbot-icon-button" type="button" onClick={() => setSelectedNotification(null)} aria-label="Close notification">
                <i className="fa-solid fa-xmark" aria-hidden="true" />
              </button>
            </header>
            <div className="notification-drawer-body">
              <p>{selectedNotification.message}</p>
              <dl className="notification-detail-list">
                <div>
                  <dt>Related module</dt>
                  <dd>{selectedNotification.relatedModule}</dd>
                </div>
                <div>
                  <dt>Related entity</dt>
                  <dd>{selectedNotification.relatedEntityId ?? 'None'}</dd>
                </div>
                <div>
                  <dt>Status</dt>
                  <dd>{selectedNotification.read ? 'Read' : 'Unread'}</dd>
                </div>
              </dl>
              <div className="notification-actions">
                {selectedNotification.availableActions.length === 0 ? (
                  <p className="empty-note">No permitted actions available for this notification.</p>
                ) : (
                  selectedNotification.availableActions.map((action) => (
                    <button className="secondary-button small-button" type="button" key={action} onClick={() => onSelectItem(selectedNotification.relatedModule.toLowerCase())}>
                      {action.replaceAll('_', ' ')}
                    </button>
                  ))
                )}
              </div>
              <div className="notification-timeline">
                <h3>Activity Timeline</h3>
                <div className="activity-item">
                  <span className="activity-dot" />
                  <p>Notification created from backend record</p>
                  <time>{selectedNotification.createdAt ?? 'Recorded'}</time>
                </div>
              </div>
              {notificationError && <p className="error-text">{notificationError}</p>}
            </div>
            <footer className="notification-drawer-footer">
              <button className="secondary-button small-button" type="button" onClick={() => setSelectedNotification(null)}>
                Dismiss
              </button>
              <button className="primary-button small-button" type="button" onClick={markSelectedRead}>
                Mark as read
              </button>
            </footer>
          </aside>
        )}
      </div>
    </div>
  );
}
