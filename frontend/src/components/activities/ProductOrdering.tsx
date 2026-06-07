import { useState } from 'react';

const sampleProducts = [
  { id: 'p1', name: 'All-purpose Cleaner', price: 6.5 },
  { id: 'p2', name: 'Microfiber Cloths (5)', price: 8.0 },
  { id: 'p3', name: 'Disinfectant Spray', price: 5.25 },
];

export default function ProductOrdering() {
  const [cart, setCart] = useState<string[]>([]);

  function add(id: string) {
    setCart((c) => [...c, id]);
  }

  function checkout() {
    alert('Checkout simulated: ' + JSON.stringify(cart));
    setCart([]);
  }

  return (
    <div>
      <h3>Order Products & Consultations</h3>
      <ul>
        {sampleProducts.map((p) => (
          <li key={p.id} style={{ marginBottom: 8 }}>
            <strong>{p.name}</strong> — ${p.price.toFixed(2)}{' '}
            <button onClick={() => add(p.id)} style={{ marginLeft: 8 }}>Add</button>
          </li>
        ))}
      </ul>

      <div style={{ marginTop: 12 }}>
        <h4>Cart</h4>
        <pre>{JSON.stringify(cart, null, 2)}</pre>
        <button onClick={checkout} disabled={cart.length === 0}>Checkout</button>
      </div>
    </div>
  );
}
