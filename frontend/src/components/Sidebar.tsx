import { roleOptions, type MenuItem, type Role } from '../data/roleActivities';

type Props = {
  activeItem: string;
  menuItems: MenuItem[];
  currentRole: Role;
  onSelectItem: (key: string) => void;
  isOpen: boolean;
  onClose: () => void;
};

const roleLabelByValue = Object.fromEntries(roleOptions.map((role) => [role.value, role.label])) as Record<Role, string>;

export default function Sidebar({ activeItem, menuItems, currentRole, onSelectItem, isOpen, onClose }: Props) {
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
          <div className="sidebar-user-avatar" aria-hidden="true">
            SC
          </div>
          <div className="sidebar-user-details">
            <p className="sidebar-user-name">Suuka User</p>
            <p className="sidebar-user-role">{roleLabelByValue[currentRole]}</p>
          </div>
        </div>
      </div>
    </aside>
  );
}
