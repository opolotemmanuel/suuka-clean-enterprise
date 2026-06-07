import { useState } from 'react';

export default function ClientProfileForm() {
  const [firstName, setFirstName] = useState('');
  const [lastName, setLastName] = useState('');
  const [email, setEmail] = useState('');
  const [phone, setPhone] = useState('');
  const [address, setAddress] = useState('');
  const [submitted, setSubmitted] = useState<any>(null);

  function submit(e: React.FormEvent) {
    e.preventDefault();
    setSubmitted({ firstName, lastName, email, phone, address });
  }

  return (
    <div>
      <h3>Client Details</h3>
      <form onSubmit={submit}>
        <div>
          <label>First Name</label>
          <input value={firstName} onChange={(e) => setFirstName(e.target.value)} />
        </div>
        <div>
          <label>Last Name</label>
          <input value={lastName} onChange={(e) => setLastName(e.target.value)} />
        </div>
        <div>
          <label>Email</label>
          <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} />
        </div>
        <div>
          <label>Phone</label>
          <input type="tel" value={phone} onChange={(e) => setPhone(e.target.value)} />
        </div>
        <div>
          <label>Address</label>
          <input value={address} onChange={(e) => setAddress(e.target.value)} />
        </div>
        <div style={{ marginTop: 12 }}>
          <button type="submit">Save Client</button>
        </div>
      </form>

      {submitted && (
        <div style={{ marginTop: 12 }}>
          <h4>Client Payload</h4>
          <pre>{JSON.stringify(submitted, null, 2)}</pre>
        </div>
      )}
    </div>
  );
}
