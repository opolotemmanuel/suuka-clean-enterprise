import { useState } from 'react';

export default function RecurringScheduler() {
  const [frequency, setFrequency] = useState('Weekly');
  const [startDate, setStartDate] = useState('');
  const [submitted, setSubmitted] = useState<any>(null);

  function submit(e: React.FormEvent) {
    e.preventDefault();
    setSubmitted({ frequency, startDate });
  }

  return (
    <div>
      <h3>Schedule Recurring Cleanings</h3>
      <form onSubmit={submit}>
        <div>
          <label>Frequency</label>
          <select value={frequency} onChange={(e) => setFrequency(e.target.value)}>
            <option>Weekly</option>
            <option>Biweekly</option>
            <option>Monthly</option>
          </select>
        </div>
        <div>
          <label>Start Date</label>
          <input type="date" value={startDate} onChange={(e) => setStartDate(e.target.value)} />
        </div>
        <div style={{ marginTop: 12 }}>
          <button type="submit">Save Schedule</button>
        </div>
      </form>

      {submitted && (
        <div style={{ marginTop: 12 }}>
          <h4>Schedule Saved</h4>
          <pre>{JSON.stringify(submitted, null, 2)}</pre>
        </div>
      )}
    </div>
  );
}
