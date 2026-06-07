import { useState, FormEvent } from 'react';

const daysOfWeek = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'];

export default function CleanerAvailabilityForm() {
  const [status, setStatus] = useState<'available' | 'unavailable'>('available');
  const [routeName, setRouteName] = useState('');
  const [selectedDays, setSelectedDays] = useState<string[]>([]);
  const [submitted, setSubmitted] = useState(false);

  function toggleDay(day: string) {
    setSelectedDays((prev) =>
      prev.includes(day) ? prev.filter((d) => d !== day) : [...prev, day]
    );
  }

  function handleSubmit(e: FormEvent) {
    e.preventDefault();
    setSubmitted(true);
  }

  return (
    <div>
      <h3>Availability</h3>
      <form onSubmit={handleSubmit}>
        <div>
          <span>Status</span>
          <div>
            <label>
              <input
                type="radio"
                name="availability"
                value="available"
                checked={status === 'available'}
                onChange={() => setStatus('available')}
              />
              Available
            </label>
            <label style={{ marginLeft: 16 }}>
              <input
                type="radio"
                name="availability"
                value="unavailable"
                checked={status === 'unavailable'}
                onChange={() => setStatus('unavailable')}
              />
              Unavailable
            </label>
          </div>
        </div>

        <div style={{ marginTop: 12 }}>
          <label>Recurring Route Name</label>
          <input
            type="text"
            value={routeName}
            onChange={(e) => setRouteName(e.target.value)}
            placeholder=""
          />
        </div>

        <div style={{ marginTop: 12 }}>
          <span>Recurring Days</span>
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(2, minmax(120px, 1fr))', gap: '8px', marginTop: 8 }}>
            {daysOfWeek.map((day) => (
              <label key={day} style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                <input
                  type="checkbox"
                  checked={selectedDays.includes(day)}
                  onChange={() => toggleDay(day)}
                />
                {day}
              </label>
            ))}
          </div>
        </div>

        <div style={{ marginTop: 16 }}>
          <button type="submit">Save Availability</button>
        </div>
      </form>

      {submitted && (
        <div style={{ marginTop: 16 }}>
          <h4>Availability Updated</h4>
          <p>Status: <strong>{status}</strong></p>
          <p>Route: <strong>{routeName || 'None'}</strong></p>
          <p>Recurring days: <strong>{selectedDays.join(', ') || 'None'}</strong></p>
        </div>
      )}
    </div>
  );
}
