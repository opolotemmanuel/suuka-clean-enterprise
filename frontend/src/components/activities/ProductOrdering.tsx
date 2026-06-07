import { useEffect, useState } from 'react';
import { backendFetch } from '../../api/backend';

type InventoryItem = {
  id: string;
  name: string;
  quantity: number;
  reorderLevel: number;
  unit?: string;
};

export default function ProductOrdering() {
  const [cart, setCart] = useState<string[]>([]);
  const [products, setProducts] = useState<InventoryItem[]>([]);
  const [message, setMessage] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    backendFetch<InventoryItem[]>('/api/inventory-manager/items')
      .then((response) => setProducts(response.data))
      .catch((requestError) => setError(requestError instanceof Error ? requestError.message : 'Unable to load products'));
  }, []);

  function add(id: string) {
    setCart((c) => [...c, id]);
  }

  async function checkout() {
    setError(null);
    setMessage(null);
    try {
      await backendFetch('/api/inventory-manager/purchase-orders/request-approval', {
        method: 'POST',
        body: JSON.stringify({
          title: 'Product order approval',
          items: cart,
        }),
      });
      setCart([]);
      setMessage('Purchase approval request submitted.');
    } catch (requestError) {
      setError(requestError instanceof Error ? requestError.message : 'Unable to submit purchase approval');
    }
  }

  return (
    <div>
      <h3>Order Products & Consultations</h3>
      {error && <p className="auth-message error">{error}</p>}
      {message && <p className="auth-message">{message}</p>}
      <ul>
        {products.map((p) => (
          <li key={p.id} style={{ marginBottom: 8 }}>
            <strong>{p.name}</strong> — {p.quantity} {p.unit ?? 'units'} available{' '}
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
