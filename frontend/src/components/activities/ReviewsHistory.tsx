import { useState } from 'react';

const sampleReviews = [
  { id: 1, cleaner: 'Alice', rating: 5, text: 'Great service!' },
  { id: 2, cleaner: 'Bob', rating: 4, text: 'On time and thorough.' },
];

export default function ReviewsHistory() {
  const [reviews, setReviews] = useState(sampleReviews);
  const [text, setText] = useState('');
  const [rating, setRating] = useState(5);

  function submit(e: React.FormEvent) {
    e.preventDefault();
    setReviews((r) => [{ id: Date.now(), cleaner: 'You', rating, text }, ...r]);
    setText('');
    setRating(5);
  }

  return (
    <div>
      <h3>Reviews & History</h3>
      <form onSubmit={submit}>
        <div>
          <label>Rating</label>
          <select value={rating} onChange={(e) => setRating(Number(e.target.value))}>
            {[5,4,3,2,1].map(n => <option key={n} value={n}>{n}</option>)}
          </select>
        </div>
        <div>
          <label>Review</label>
          <textarea value={text} onChange={(e) => setText(e.target.value)} />
        </div>
        <button type="submit">Submit Review</button>
      </form>

      <h4 style={{ marginTop: 12 }}>Past Reviews</h4>
      <ul>
        {reviews.map(r => (
          <li key={r.id}><strong>{r.cleaner}</strong> ({r.rating}/5): {r.text}</li>
        ))}
      </ul>
    </div>
  );
}
