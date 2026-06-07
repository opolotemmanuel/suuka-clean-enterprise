import { useState, FormEvent } from 'react';

export default function CleanerSuppliesForm() {
  const [supplyName, setSupplyName] = useState('');
  const [quantity, setQuantity] = useState(1);
  const [issueDescription, setIssueDescription] = useState('');
  const [reported, setReported] = useState(false);

  function handleSubmit(e: FormEvent) {
    e.preventDefault();
    setReported(true);
  }

  return (
    <div>
      <h3>Supplies</h3>
      <form onSubmit={handleSubmit}>
        <div>
          <label>Supply Name</label>
          <input
            type="text"
            value={supplyName}
            onChange={(e) => setSupplyName(e.target.value)}
            placeholder=""
          />
        </div>

        <div style={{ marginTop: 12 }}>
          <label>Quantity</label>
          <input
            type="number"
            min={1}
            value={quantity}
            onChange={(e) => setQuantity(Number(e.target.value))}
          />
        </div>

        <div style={{ marginTop: 12 }}>
          <label>Issue Description</label>
          <textarea
            value={issueDescription}
            onChange={(e) => setIssueDescription(e.target.value)}
            rows={4}
            placeholder=""
          />
        </div>

        <div style={{ marginTop: 16 }}>
          <button type="submit">Report Supply Issue</button>
        </div>
      </form>

      {reported && (
        <div style={{ marginTop: 16 }}>
          <h4>Supply Issue Reported</h4>
          <p>Supply: <strong>{supplyName || 'N/A'}</strong></p>
          <p>Quantity: <strong>{quantity}</strong></p>
          <p>Description:</p>
          <pre>{issueDescription || 'No description provided.'}</pre>
        </div>
      )}
    </div>
  );
}
