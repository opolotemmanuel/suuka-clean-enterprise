type Role = 'client' | 'cleaner' | 'admin';

export default function UserDashboardsPage({ role }: { role: Role }) {
  const labels: Record<Role, string[]> = {
    client: ['Bookings', 'Scheduling', 'Orders', 'Tracking', 'Manage', 'Reviews'],
    cleaner: ['Jobs', 'Booking', 'Availability', 'Supplies', 'Earnings', 'Feedback'],
    admin: ['Pipeline', 'Schedules', 'Inventory', 'Reports', 'Escalations', 'Users'],
  };

  return (
    <>
      <section className="actions">
        <h2>{role.charAt(0).toUpperCase() + role.slice(1)} Dashboard</h2>
        <ul>
          {labels[role].map((l) => (
            <li key={l}>{l}</li>
          ))}
        </ul>
      </section>
    </>
  );
}
