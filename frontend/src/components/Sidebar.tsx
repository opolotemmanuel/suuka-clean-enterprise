import { type MenuItem, type Role } from '../data/roleActivities';
import { type BackendUser } from '../api/backend';

type Props = {
  activeItem: string;
  menuItems: MenuItem[];
  currentRole: Role;
  backendUser: BackendUser | null;
  onSelectItem: (key: string) => void;
  isOpen: boolean;
  onClose: () => void;
};

export default function Sidebar({ activeItem, menuItems, currentRole, backendUser, onSelectItem, isOpen, onClose }: Props) {
  const displayName = backendUser?.fullName ?? backendUser?.email ?? 'Signed-in user';
  const displayRole = backendUser?.role ? backendUser.role.replaceAll('_', ' ') : currentRole.replaceAll('-', ' ');
  const initials = displayName
    .split(/[\s@.]+/)
    .filter(Boolean)
    .slice(0, 2)
    .map((part) => part[0]?.toUpperCase())
    .join('') || 'SU';

  return (
    <aside className={`sidebar ${isOpen ? 'sidebar-open' : ''}`}>
      <div className="sidebar-body">
        <div className="sidebar-top">
          <div className="sidebar-brand">Suuka Clean</div>
          <button className="sidebar-close" onClick={onClose} aria-label="Close menu">
            <i className="fa-solid fa-xmark" aria-hidden="true" />
          </button>
        </div>

        <nav className="sidebar-nav">
          {menuItems.map((item) => {
            const isSettings = item.key === 'system-settings';
            const className = `${activeItem === item.key ? 'sidebar-link active' : 'sidebar-link'}${isSettings ? ' settings' : ''}`;
            return (
              <button
                key={item.key}
                className={className}
                onClick={() => onSelectItem(item.key)}
                title={item.label}
              >
                <i className={`fa-solid ${item.icon} sidebar-icon`} aria-hidden="true" />
                <span className="sidebar-label">{item.label}</span>
              </button>
            );
          })}
        </nav>

        <div className="sidebar-user-card" aria-label="Signed-in user">
          {backendUser?.profilePictureUrl ? (
            <img className="sidebar-user-avatar" src={backendUser.profilePictureUrl} alt="" />
          ) : (
            <div className="sidebar-user-avatar" aria-hidden="true">
              {initials}
            </div>
          )}
          <div className="sidebar-user-details">
            <p className="sidebar-user-name">{displayName}</p>
            <p className="sidebar-user-role">{displayRole}</p>
          </div>
        </div>
      </div>
    </aside>
  );
}
