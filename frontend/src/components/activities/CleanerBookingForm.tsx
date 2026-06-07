import { useState, FormEvent } from 'react';

export default function CleanerBookingForm() {
  const [bookingRef, setBookingRef] = useState('');
  const [arrived, setArrived] = useState(false);
  const [started, setStarted] = useState(false);
  const [completed, setCompleted] = useState(false);
  const [submitted, setSubmitted] = useState(false);

  function handleSubmit(e: FormEvent) {
    e.preventDefault();
    setSubmitted(true);
  }

  return (
    <div>
      <h3>Booking</h3>
      <form onSubmit={handleSubmit}>
        <div>
          <label>Booking Reference</label>
          <input
            type="text"
            value={bookingRef}
            onChange={(e) => setBookingRef(e.target.value)}
            placeholder=""
          />
        </div>

        <div style={{ marginTop: 12 }}>
          <label>
            <input type="checkbox" checked={arrived} onChange={(e) => setArrived(e.target.checked)} />
            Mark arrival
          </label>
        </div>

        <div>
          <label>
            <input type="checkbox" checked={started} onChange={(e) => setStarted(e.target.checked)} />
            Mark service started
          </label>
        </div>

        <div>
          <label>
            <input type="checkbox" checked={completed} onChange={(e) => setCompleted(e.target.checked)} />
            Mark service completed
          </label>
        </div>

        <div style={{ marginTop: 16 }}>
          <button type="submit">Update Booking Status</button>
        </div>
      </form>

      {submitted && (
        <div style={{ marginTop: 16 }}>
          <h4>Booking Status Updated</h4>
          <p>Booking reference: <strong>{bookingRef || 'N/A'}</strong></p>
          <ul>
            <li>Arrived: {arrived ? 'Yes' : 'No'}</li>
            <li>Started: {started ? 'Yes' : 'No'}</li>
            <li>Completed: {completed ? 'Yes' : 'No'}</li>
          </ul>
        </div>
      )}
    </div>
  );
}
