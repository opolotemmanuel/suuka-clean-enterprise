export default function ClientFeaturesPage({ role }: { role: 'client' | 'cleaner' | 'admin' }) {
  const clientActivities = ['Booking', 'Schedule', 'Orders', 'Track', 'Manage', 'Reviews'];

  if (role !== 'client') {
    return (
      <div className="card">
        <h3>Client Page</h3>
        <p>Access denied.</p>
      </div>
    );
  }

  return (
    <section className="actions">
      <h2>Client Activities</h2>
      <ul>
        {clientActivities.map((activity) => (
          <li key={activity}>{activity}</li>
        ))}
      </ul>
    </section>
  );
}
