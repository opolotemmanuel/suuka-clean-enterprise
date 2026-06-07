export default function DefaultSummary() {
  return (
    <section className="page-hero">
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', gap: 20 }}>
        <div>
          <p className="page-label">Dashboard</p>
          <h1>Suuka Cleaning Marketplace</h1>
        </div>

        <div style={{ display: 'flex', gap: 12 }}>
          <button className="primary-button">New Booking</button>
        </div>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: 16, marginTop: 20 }}>
        <div className="card">
          <h4>Open Bookings</h4>
          <p style={{ fontSize: 24, margin: '8px 0' }}>12</p>
        </div>

        <div className="card">
          <h4>Active Cleaners</h4>
          <p style={{ fontSize: 24, margin: '8px 0' }}>34</p>
        </div>

        <div className="card">
          <h4>Pending Supplies</h4>
          <p style={{ fontSize: 24, margin: '8px 0' }}>7</p>
        </div>
      </div>
    </section>
  );
}
