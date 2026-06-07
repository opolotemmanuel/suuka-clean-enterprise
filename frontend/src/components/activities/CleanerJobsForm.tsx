import { useState, FormEvent } from 'react';

export default function CleanerJobsForm() {
  const [decision, setDecision] = useState<'accept' | 'decline'>('accept');
  const [bookingRef, setBookingRef] = useState('');
  const [instructions, setInstructions] = useState('');
  const [submitted, setSubmitted] = useState(false);

  function handleSubmit(e: FormEvent) {
    e.preventDefault();
    setSubmitted(true);
  }

  return (
    <div>
      <h3>Jobs</h3>
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
          <span>Decision</span>
          <div>
            <label>
              <input
                type="radio"
                name="decision"
                value="accept"
                checked={decision === 'accept'}
                onChange={() => setDecision('accept')}
              />
              Accept
            </label>
            <label style={{ marginLeft: 16 }}>
              <input
                type="radio"
                name="decision"
                value="decline"
                checked={decision === 'decline'}
                onChange={() => setDecision('decline')}
              />
              Decline
            </label>
          </div>
        </div>

        <div style={{ marginTop: 12 }}>
          <label>Client Instructions</label>
          <textarea
            value={instructions}
            onChange={(e) => setInstructions(e.target.value)}
            rows={4}
            placeholder=""
          />
        </div>

        <div style={{ marginTop: 16 }}>
          <button type="submit">Submit Job Decision</button>
        </div>
      </form>

      {submitted && (
        <div style={{ marginTop: 16 }}>
          <h4>Job Decision Saved</h4>
          <p>
            {decision === 'accept' ? 'Accepted' : 'Declined'} booking{' '}
            <strong>{bookingRef || 'N/A'}</strong>.
          </p>
          <p>Client instructions noted:</p>
          <pre>{instructions || 'No instructions provided.'}</pre>
        </div>
      )}
    </div>
  );
}
