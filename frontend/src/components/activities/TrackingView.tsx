export default function TrackingView() {
  const events = [
    { t: '2026-06-03T09:00:00', text: 'Cleaner assigned' },
    { t: '2026-06-03T09:45:00', text: 'Cleaner en route' },
    { t: '2026-06-03T10:05:00', text: 'Cleaner arrived' },
    { t: '2026-06-03T11:30:00', text: 'Service completed' },
  ];

  return (
    <div>
      <h3>Service Tracking</h3>
      <ol>
        {events.map((e) => (
          <li key={e.t}>
            <strong>{new Date(e.t).toLocaleString()}</strong> — {e.text}
          </li>
        ))}
      </ol>
    </div>
  );
}
