import { useState } from 'react';

const earningsData = {
  total: 2150,
  paid: 1920,
  pending: 230,
};

const completedJobs = [
  { id: 'B-1042', client: 'Jessica', amount: 160, date: '2026-06-01' },
  { id: 'B-1043', client: 'Rahul', amount: 120, date: '2026-06-02' },
  { id: 'B-1044', client: 'Sofia', amount: 200, date: '2026-06-03' },
];

const feedbackItems = [
  { id: 1, client: 'Jessica', rating: 5, text: 'Very thorough and professional.' },
  { id: 2, client: 'Rahul', rating: 4, text: 'Arrived on time and did a great job.' },
];

export default function CleanerEarningsSummary() {
  const [feedback, setFeedback] = useState(feedbackItems);
  const [newFeedback, setNewFeedback] = useState('');

  function addFeedback() {
    if (!newFeedback.trim()) return;
    setFeedback((prev) => [
      { id: Date.now(), client: 'Anonymous', rating: 5, text: newFeedback.trim() },
      ...prev,
    ]);
    setNewFeedback('');
  }

  return (
    <div>
      <h3>Earnings</h3>
      <section style={{ marginBottom: 18 }}>
        <h4>Earnings Summary</h4>
        <p>Total earnings: <strong>${earningsData.total.toFixed(2)}</strong></p>
        <p>Paid: <strong>${earningsData.paid.toFixed(2)}</strong></p>
        <p>Pending: <strong>${earningsData.pending.toFixed(2)}</strong></p>
      </section>

      <section style={{ marginBottom: 18 }}>
        <h4>Completed Jobs</h4>
        <ul>
          {completedJobs.map((job) => (
            <li key={job.id} style={{ marginBottom: 8 }}>
              <strong>{job.id}</strong> - {job.client} - ${job.amount} on {job.date}
            </li>
          ))}
        </ul>
      </section>

      <section style={{ marginBottom: 18 }}>
        <h4>Client Feedback</h4>
        <ul>
          {feedback.map((item) => (
            <li key={item.id} style={{ marginBottom: 8 }}>
              <strong>{item.client}</strong> ({item.rating}/5): {item.text}
            </li>
          ))}
        </ul>
      </section>

      <section>
        <h4>Leave an internal note</h4>
        <textarea
          rows={3}
          value={newFeedback}
          onChange={(e) => setNewFeedback(e.target.value)}
          placeholder=""
          style={{ width: '100%', padding: 10, borderRadius: 10, border: '1px solid #e2e8f0' }}
        />
        <button type="button" onClick={addFeedback} style={{ marginTop: 12 }}>
          Add Note
        </button>
      </section>
    </div>
  );
}
