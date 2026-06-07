import { useState } from 'react';
import { createBooking, type BookingDto } from '../../api/backend';

export default function BookingForm() {
  const [address, setAddress] = useState('');
  const [date, setDate] = useState('');
  const [service, setService] = useState('Standard Clean');
  const [createdBooking, setCreatedBooking] = useState<BookingDto | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  async function submit(e: React.FormEvent) {
    e.preventDefault();
    setIsSubmitting(true);
    setError(null);
    setCreatedBooking(null);

    try {
      const response = await createBooking({
        serviceType: service,
        propertyAddress: address,
        latitude: 0,
        longitude: 0,
        scheduledAt: date,
        durationHours: 2,
        paymentMethod: 'WALLET',
      });
      setCreatedBooking(response.data);
      setAddress('');
      setDate('');
    } catch (requestError) {
      setError(requestError instanceof Error ? requestError.message : 'Unable to create booking');
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <div>
      <h3>Create a Booking</h3>
      <form onSubmit={submit}>
        <div>
          <label>Address</label>
          <input value={address} onChange={(e) => setAddress(e.target.value)} />
        </div>
        <div>
          <label>Date & Time</label>
          <input type="datetime-local" value={date} onChange={(e) => setDate(e.target.value)} />
        </div>
        <div>
          <label>Service</label>
          <select value={service} onChange={(e) => setService(e.target.value)}>
            <option>Standard Clean</option>
            <option>Deep Clean</option>
            <option>Move-out Clean</option>
          </select>
        </div>
        <div style={{ marginTop: 12 }}>
          <button type="submit" disabled={isSubmitting || !address || !date}>
            {isSubmitting ? 'Creating...' : 'Create Booking'}
          </button>
        </div>
      </form>

      {error && (
        <div style={{ marginTop: 12 }}>
          <strong>Unable to create booking.</strong>
          <p>{error}</p>
        </div>
      )}

      {createdBooking && (
        <div style={{ marginTop: 12 }}>
          <h4>Booking Created</h4>
          <p>Status: <strong>{createdBooking.status}</strong></p>
          <p>Booking ID: <strong>{createdBooking.id}</strong></p>
        </div>
      )}
    </div>
  );
}
