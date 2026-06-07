import Sidebar from './Sidebar';
import RoleBasedChatbot from './RoleBasedChatbot';
import { type MenuItem, type Role } from '../data/roleActivities';
import {
  loadConversation,
  loadConversations,
  loadNotifications,
  loadUnreadMessageCount,
  markAllNotificationsRead,
  markConversationRead,
  markNotificationRead,
  performNotificationAction,
  sendConversationMessage,
  updateProfilePicture,
  type BackendConversation,
  type BackendMessage,
  type BackendNotification,
  type BackendUser,
} from '../api/backend';
import { FormEvent, useEffect, useMemo, useState, type ReactNode } from 'react';

type Props = {
  menuItems: MenuItem[];
  activeItem: string;
  onSelectItem: (key: string) => void;
  currentRole: Role;
  isSidebarOpen: boolean;
  toggleSidebar: () => void;
  closeSidebar: () => void;
  pageTitle: string;
  pageSubtitle: string;
  backendUser: BackendUser | null;
  backendStatus: 'offline' | 'connecting' | 'connected' | 'error';
  onUserUpdated: (user: BackendUser) => void;
  onDisconnectBackend: () => void;
  children: ReactNode;
};

export default function PageLayout({
  menuItems,
  activeItem,
  onSelectItem,
  currentRole,
  isSidebarOpen,
  toggleSidebar,
  closeSidebar,
  pageTitle,
  pageSubtitle,
  backendUser,
  backendStatus,
  onUserUpdated,
  onDisconnectBackend,
  children,
}: Props) {
  const isConnected = backendStatus === 'connected' && backendUser;
  const [notifications, setNotifications] = useState<BackendNotification[]>([]);
  const [notificationFilter, setNotificationFilter] = useState<'all' | 'unread'>('unread');
  const [selectedNotification, setSelectedNotification] = useState<BackendNotification | null>(null);
  const [notificationError, setNotificationError] = useState<string | null>(null);
  const [notificationDrawerOpen, setNotificationDrawerOpen] = useState(false);
  const [profileOpen, setProfileOpen] = useState(false);
  const [uploadOpen, setUploadOpen] = useState(false);
  const [uploadError, setUploadError] = useState<string | null>(null);
  const [uploadPreview, setUploadPreview] = useState<string | null>(null);
  const [uploadFile, setUploadFile] = useState<File | null>(null);
  const [messagesOpen, setMessagesOpen] = useState(false);
  const [conversations, setConversations] = useState<BackendConversation[]>([]);
  const [selectedConversationId, setSelectedConversationId] = useState<string | null>(null);
  const [conversationMessages, setConversationMessages] = useState<BackendMessage[]>([]);
  const [conversationSearch, setConversationSearch] = useState('');
  const [messageDraft, setMessageDraft] = useState('');
  const [messageError, setMessageError] = useState<string | null>(null);
  const [unreadMessageCount, setUnreadMessageCount] = useState(0);
  const unreadCount = notifications.filter((notification) => !notification.read).length;
  const visibleNotifications = notifications.filter((notification) => notificationFilter === 'all' || !notification.read);
  const visibleConversations = useMemo(() => conversations.filter((conversation) => {
    const text = [conversation.relatedModule, conversation.relatedEntityId, conversation.id].filter(Boolean).join(' ').toLowerCase();
    return text.includes(conversationSearch.toLowerCase());
  }), [conversationSearch, conversations]);
  const displayName = backendUser?.fullName ?? backendUser?.email ?? 'Signed-in user';
  const displayRole = backendUser?.role ? backendUser.role.replaceAll('_', ' ') : 'Authenticated';
  const initials = displayName
    .split(/[\s@.]+/)
    .filter(Boolean)
    .slice(0, 2)
    .map((part) => part[0]?.toUpperCase())
    .join('') || 'SU';

  useEffect(() => {
    if (!isConnected) {
      setNotifications([]);
      setSelectedNotification(null);
      setConversations([]);
      setConversationMessages([]);
      setUnreadMessageCount(0);
      return;
    }
    void refreshNotifications();
    void refreshConversations();
    void refreshUnreadMessageCount();
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

  async function markAllRead() {
    await markAllNotificationsRead();
    setSelectedNotification(null);
    await refreshNotifications();
  }

  async function runNotificationAction(action: string) {
    if (!selectedNotification) return;
    await performNotificationAction(selectedNotification.id, action);
    setSelectedNotification((current) => current ? { ...current, read: true } : current);
    await refreshNotifications();
    const target = selectedNotification.relatedModule.toLowerCase().replaceAll('_', '-');
    onSelectItem(target);
  }

  async function refreshConversations() {
    setMessageError(null);
    try {
      const response = await loadConversations();
      setConversations(response.data);
      const firstConversationId = response.data[0]?.id ?? null;
      if (firstConversationId) {
        await openConversation(firstConversationId);
      }
    } catch (error) {
      setMessageError(error instanceof Error ? error.message : 'Unable to load conversations');
    }
  }

  async function refreshUnreadMessageCount() {
    try {
      const response = await loadUnreadMessageCount();
      setUnreadMessageCount(response.data);
    } catch {
      setUnreadMessageCount(0);
    }
  }

  async function openConversation(id: string) {
    setSelectedConversationId(id);
    const response = await loadConversation(id);
    setConversationMessages(response.data.messages);
    await markConversationRead(id);
    setConversationMessages((messages) => messages.map((message) => message.recipientId === backendUser?.id ? { ...message, read: true } : message));
    await refreshUnreadMessageCount();
    await refreshNotifications();
  }

  async function submitMessage(event: FormEvent) {
    event.preventDefault();
    if (!selectedConversationId || !messageDraft.trim()) return;
    try {
      const response = await sendConversationMessage(selectedConversationId, messageDraft.trim());
      setConversationMessages((current) => [...current, response.data]);
      setMessageDraft('');
      await refreshUnreadMessageCount();
    } catch (error) {
      setMessageError(error instanceof Error ? error.message : 'Unable to send message');
    }
  }

  function selectUploadFile(file: File | null) {
    setUploadError(null);
    setUploadFile(file);
    if (!file) {
      setUploadPreview(null);
      return;
    }
    if (!['image/jpeg', 'image/png', 'image/webp'].includes(file.type)) {
      setUploadError('Only JPG, PNG, and WEBP images are allowed.');
      setUploadFile(null);
      return;
    }
    if (file.size > 5 * 1024 * 1024) {
      setUploadError('Profile picture must be 5MB or smaller.');
      setUploadFile(null);
      return;
    }
    setUploadPreview(URL.createObjectURL(file));
  }

  async function saveProfilePicture() {
    if (!uploadFile) return;
    setUploadError(null);
    try {
      const response = await updateProfilePicture(uploadFile);
      onUserUpdated(response.data);
      setUploadOpen(false);
      setUploadFile(null);
      setUploadPreview(null);
    } catch (error) {
      setUploadError(error instanceof Error ? error.message : 'Unable to update profile picture');
    }
  }

  return (
    <div className={`page-layout ${isSidebarOpen ? 'sidebar-visible' : ''}`}>
      <Sidebar
        menuItems={menuItems}
        activeItem={activeItem}
        currentRole={currentRole}
        backendUser={backendUser}
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
              <span>{isConnected ? 'API: Connected' : backendStatus === 'connecting' ? 'API: Connecting' : 'API: Offline'}</span>
              {isConnected && (
                <button
                  className="backend-chip-button"
                  onClick={onDisconnectBackend}
                  type="button"
                >
                  Sign out
                </button>
              )}
            </div>
            <button className="icon-button" aria-label="Notifications" type="button" onClick={() => setNotificationDrawerOpen(true)}>
              <i className="fa-solid fa-bell" aria-hidden="true" />
              {unreadCount > 0 && <span className="badge">{unreadCount}</span>}
            </button>
            <button className="icon-button" aria-label="Messages" type="button" onClick={() => setMessagesOpen(true)}>
              <i className="fa-solid fa-comments" aria-hidden="true" />
              {unreadMessageCount > 0 && <span className="badge">{unreadMessageCount}</span>}
            </button>
            <button className="profile-chip" aria-label="Logged-in user profile" type="button" onClick={() => setProfileOpen((value) => !value)}>
              {backendUser?.profilePictureUrl ? (
                <img className="profile-avatar" src={backendUser.profilePictureUrl} alt="" />
              ) : (
                <span className="profile-avatar">{initials}</span>
              )}
              <span className="profile-copy">
                <span className="profile-name">{displayName}</span>
                <span className="profile-role">{displayRole}</span>
              </span>
              <span className="profile-online" aria-label="Online" />
            </button>
          </div>
        </header>
        {profileOpen && (
          <aside className="profile-panel" aria-label="Profile menu">
            <div className="profile-panel-header">
              {backendUser?.profilePictureUrl ? <img className="profile-panel-avatar" src={backendUser.profilePictureUrl} alt="" /> : <span className="profile-panel-avatar">{initials}</span>}
              <div>
                <h2>{displayName}</h2>
                <p>{displayRole}</p>
              </div>
            </div>
            <dl className="profile-detail-list">
              <div><dt>Email</dt><dd>{backendUser?.email ?? 'Unavailable'}</dd></div>
              <div><dt>Phone</dt><dd>{backendUser?.phoneNumber ?? 'Not added'}</dd></div>
              <div><dt>Status</dt><dd>{backendUser?.accountStatus ?? 'ACTIVE'}</dd></div>
            </dl>
            <button className="secondary-button small-button" type="button" onClick={() => { setProfileOpen(false); onSelectItem('profile'); }}>View Profile</button>
            <button className="secondary-button small-button" type="button" onClick={() => { setProfileOpen(false); setUploadOpen(true); }}>Update Profile Picture</button>
            <button className="secondary-button small-button" type="button" onClick={() => { setProfileOpen(false); onSelectItem('system-settings'); }}>Account Settings</button>
            <button className="danger-button small-button" type="button" onClick={onDisconnectBackend}>Logout</button>
          </aside>
        )}

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
        {messagesOpen && (
          <aside className="header-drawer" aria-label="Messages drawer">
            <header className="notification-drawer-header">
              <div>
                <p className="section-label">Messages</p>
                <h2>Conversations</h2>
              </div>
              <button className="chatbot-icon-button" type="button" onClick={() => setMessagesOpen(false)} aria-label="Close messages">
                <i className="fa-solid fa-xmark" aria-hidden="true" />
              </button>
            </header>
            <div className="drawer-search">
              <input type="search" value={conversationSearch} onChange={(event) => setConversationSearch(event.target.value)} placeholder="Search conversations" />
            </div>
            <div className="messages-drawer-body">
              <div className="conversation-list">
                {visibleConversations.map((conversation) => (
                  <button key={conversation.id} className={conversation.id === selectedConversationId ? 'conversation-row active' : 'conversation-row'} type="button" onClick={() => openConversation(conversation.id)}>
                    <strong>{conversation.relatedModule ?? 'Conversation'}</strong>
                    <span>{conversation.relatedEntityId ?? conversation.id}</span>
                  </button>
                ))}
                {visibleConversations.length === 0 && <p className="empty-note">No conversations found.</p>}
              </div>
              <div className="message-thread">
                {conversationMessages.map((message) => (
                  <article className={message.senderId === backendUser?.id ? 'message-bubble mine' : 'message-bubble'} key={message.id}>
                    <p>{message.body}</p>
                    <time>{message.createdAt ?? 'Sent'}</time>
                  </article>
                ))}
              </div>
              {messageError && <p className="error-text">{messageError}</p>}
            </div>
            <form className="message-compose" onSubmit={submitMessage}>
              <input value={messageDraft} onChange={(event) => setMessageDraft(event.target.value)} placeholder="Write a message" />
              <button className="primary-button small-button" type="submit" disabled={!selectedConversationId || !messageDraft.trim()}>Send</button>
            </form>
            <footer className="notification-drawer-footer">
              <button className="secondary-button small-button" type="button" onClick={() => { setMessagesOpen(false); onSelectItem('messages'); }}>Open full chat page</button>
            </footer>
          </aside>
        )}
        {notificationDrawerOpen && (
          <aside className="notification-drawer" aria-label="Notification action drawer">
            <header className="notification-drawer-header">
              <div>
                <p className="section-label">Notifications</p>
                <h2>{selectedNotification?.title ?? 'Notification Center'}</h2>
              </div>
              <button className="chatbot-icon-button" type="button" onClick={() => setNotificationDrawerOpen(false)} aria-label="Close notification">
                <i className="fa-solid fa-xmark" aria-hidden="true" />
              </button>
            </header>
            <div className="notification-drawer-body">
              <div className="drawer-tabs">
                <button className={notificationFilter === 'unread' ? 'active' : ''} type="button" onClick={() => setNotificationFilter('unread')}>Unread</button>
                <button className={notificationFilter === 'all' ? 'active' : ''} type="button" onClick={() => setNotificationFilter('all')}>All</button>
              </div>
              <div className="notification-list">
                {visibleNotifications.map((notification) => (
                  <button key={notification.id} type="button" className={notification.id === selectedNotification?.id ? 'notification-row active' : 'notification-row'} onClick={() => setSelectedNotification(notification)}>
                    <strong>{notification.title}</strong>
                    <span>{notification.message}</span>
                    {!notification.read && <em>Unread</em>}
                  </button>
                ))}
                {visibleNotifications.length === 0 && <p className="empty-note">No notifications found.</p>}
              </div>
              {selectedNotification && (
                <>
                  <p>{selectedNotification.message}</p>
                  <dl className="notification-detail-list">
                    <div><dt>Related module</dt><dd>{selectedNotification.relatedModule}</dd></div>
                    <div><dt>Related entity</dt><dd>{selectedNotification.relatedEntityId ?? 'None'}</dd></div>
                    <div><dt>Status</dt><dd>{selectedNotification.read ? 'Read' : 'Unread'}</dd></div>
                  </dl>
                  <div className="notification-actions">
                    {selectedNotification.availableActions.length === 0 ? <p className="empty-note">No permitted actions available for this notification.</p> : selectedNotification.availableActions.map((action) => (
                      <button className="secondary-button small-button" type="button" key={action} onClick={() => runNotificationAction(action)}>
                        {action.replaceAll('_', ' ')}
                      </button>
                    ))}
                  </div>
                  <div className="notification-timeline">
                    <h3>Activity Timeline</h3>
                    <div className="activity-item">
                      <span className="activity-dot" />
                      <p>Notification created from backend record</p>
                      <time>{selectedNotification.createdAt ?? 'Recorded'}</time>
                    </div>
                  </div>
                </>
              )}
              {notificationError && <p className="error-text">{notificationError}</p>}
            </div>
            <footer className="notification-drawer-footer">
              <button className="secondary-button small-button" type="button" onClick={() => setNotificationDrawerOpen(false)}>
                Dismiss
              </button>
              <button className="secondary-button small-button" type="button" onClick={markAllRead}>
                Mark all as read
              </button>
              <button className="primary-button small-button" type="button" onClick={markSelectedRead} disabled={!selectedNotification}>
                Mark as read
              </button>
            </footer>
          </aside>
        )}
        {uploadOpen && (
          <div className="upload-modal-backdrop" role="presentation">
            <section className="upload-modal" aria-label="Update profile picture">
              <header className="panel-header">
                <h3>Update Profile Picture</h3>
              </header>
              <div className="upload-preview-row">
                <div>
                  <p className="section-label">Current</p>
                  {backendUser?.profilePictureUrl ? <img className="upload-preview" src={backendUser.profilePictureUrl} alt="" /> : <span className="upload-preview">{initials}</span>}
                </div>
                <div>
                  <p className="section-label">New</p>
                  {uploadPreview ? <img className="upload-preview" src={uploadPreview} alt="" /> : <span className="upload-preview empty">Preview</span>}
                </div>
              </div>
              <input type="file" accept=".jpg,.jpeg,.png,.webp,image/jpeg,image/png,image/webp" onChange={(event) => selectUploadFile(event.target.files?.[0] ?? null)} />
              {uploadError && <p className="error-text">{uploadError}</p>}
              <footer className="notification-drawer-footer">
                <button className="secondary-button small-button" type="button" onClick={() => setUploadOpen(false)}>Cancel</button>
                <button className="primary-button small-button" type="button" onClick={saveProfilePicture} disabled={!uploadFile}>Save</button>
              </footer>
            </section>
          </div>
        )}
      </div>
    </div>
  );
}
