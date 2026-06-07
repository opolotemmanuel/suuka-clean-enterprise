import { useEffect, useState } from 'react';
import { cancelOwnBooking, loadOwnBookings, type BookingDto } from '../../api/backend';

export default function ManageBooking() {
  const [bookings, setBookings] = useState<BookingDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    void refreshBookings();
  }, []);

  async function refreshBookings() {
    setLoading(true);
    setError(null);
    try {
      const response = await loadOwnBookings();
      setBookings(response.data);
    } catch (requestError) {
      setError(requestError instanceof Error ? requestError.message : 'Unable to load bookings');
    } finally {
      setLoading(false);
    }
  }

  async function cancel(id: string) {
    setError(null);
    try {
      await cancelOwnBooking(id);
      await refreshBookings();
    } catch (requestError) {
      setError(requestError instanceof Error ? requestError.message : 'Unable to cancel booking');
    }
  }

  return (
    <div>
      <h3>Manage Bookings</h3>
      {loading && <p>Loading bookings...</p>}
      {error && (
        <div>
          <p>{error}</p>
          <button type="button" onClick={refreshBookings}>Retry</button>
        </div>
      )}
      {!loading && !error && bookings.length === 0 && (
        <div>
          <p>No bookings found.</p>
        </div>
      )}
      <ul>
        {bookings.map((b) => (
          <li key={b.id} style={{ marginBottom: 12 }}>
            <div><strong>{b.propertyAddress}</strong> - {new Date(b.scheduledAt).toLocaleString()}</div>
            <div>Status: {b.status}</div>
            {b.status === 'PENDING' && (
              <button type="button" onClick={() => cancel(b.id)} style={{ marginTop: 6 }}>Cancel</button>
            )}
          </li>
        ))}
      </ul>
    </div>
  );
}
