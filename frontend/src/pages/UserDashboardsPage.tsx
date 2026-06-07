import React, { useState, useEffect } from 'react';

type Role = 'client' | 'cleaner' | 'admin';

export default function UserDashboardsPage({ role }: { role: Role }) {
  const [activeRole, setActiveRole] = useState<Role>(role);
  // Sync with parent role selector
  useEffect(() => {
    setActiveRole(role);
  }, [role]);

  const labels: Record<Role, string[]> = {
    client: ['Bookings', 'Scheduling', 'Orders', 'Tracking', 'Manage', 'Reviews'],
    cleaner: ['Jobs', 'Booking', 'Availability', 'Supplies', 'Earnings', 'Feedback'],
    admin: ['Pipeline', 'Schedules', 'Inventory', 'Reports', 'Escalations', 'Users'],
  };

  return (
    <>
      <div className="role-tabs">
        <button
          className={activeRole === 'client' ? 'role-button active' : 'role-button'}
          onClick={() => setActiveRole('client')}
        >
          Client
        </button>
        <button
          className={activeRole === 'cleaner' ? 'role-button active' : 'role-button'}
          onClick={() => setActiveRole('cleaner')}
        >
          Cleaner
        </button>
        <button
          className={activeRole === 'admin' ? 'role-button active' : 'role-button'}
          onClick={() => setActiveRole('admin')}
        >
          Admin
        </button>
      </div>

      <section className="actions">
        <h2>{activeRole.charAt(0).toUpperCase() + activeRole.slice(1)} Dashboard</h2>
        <ul>
          {labels[activeRole].map((l) => (
            <li key={l}>{l}</li>
          ))}
        </ul>
      </section>
    </>
  );
}
