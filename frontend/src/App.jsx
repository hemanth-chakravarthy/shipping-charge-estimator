import React, { useEffect, useRef, useState } from 'react';
import { Package, Truck, MapPin, Search, Calculator, ShieldCheck, Info, BarChart3, Globe, Plus, Minus, Trash2, ShoppingCart } from 'lucide-react';
import { ResponsiveContainer, AreaChart, Area, XAxis, YAxis, Tooltip, CartesianGrid } from 'recharts';
import useStore from './store';
import './App.css';

const API_BASE = import.meta.env.VITE_API_BASE || 'http://localhost:8082/api';

function App() {
  const { 
    data, setData, 
    cart, addToCart, updateQuantity, removeFromCart, 
    form, setForm, 
    calculation, setCalculation, 
    history, setHistory 
  } = useStore();

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [calculating, setCalculating] = useState(false);
  
  const mapRef = useRef(null);
  const leafletMap = useRef(null);
  const routeLayer = useRef(null);
  const markersLayer = useRef(null);

  useEffect(() => {
    fetchData();
  }, []);

  useEffect(() => {
    if (!loading && window.L && mapRef.current && !leafletMap.current) {
      setTimeout(() => {
        if (!mapRef.current) return;
        leafletMap.current = window.L.map(mapRef.current).setView([20.5937, 78.9629], 5);
        window.L.tileLayer('https://{s}.basemaps.cartocdn.com/light_all/{z}/{x}/{y}{r}.png', {
          attribution: '&copy; CARTO'
        }).addTo(leafletMap.current);
        markersLayer.current = window.L.layerGroup().addTo(leafletMap.current);
        routeLayer.current = window.L.layerGroup().addTo(leafletMap.current);
      }, 200);
    }
  }, [loading]);

  const updateRoute = async (warehouse, customer) => {
    if (!leafletMap.current || !warehouse || !customer) return;
    markersLayer.current.clearLayers();
    routeLayer.current.clearLayers();
    const wPos = [warehouse.latitude, warehouse.longitude];
    const cPos = [customer.latitude, customer.longitude];
    window.L.marker(wPos).addTo(markersLayer.current);
    window.L.marker(cPos).addTo(markersLayer.current);
    try {
      const res = await fetch(`https://router.project-osrm.org/route/v1/driving/${warehouse.longitude},${warehouse.latitude};${customer.longitude},${customer.latitude}?overview=full&geometries=geojson`);
      const routeData = await res.json();
      if (routeData.code === 'Ok') {
        const coordinates = routeData.routes[0].geometry.coordinates.map(coord => [coord[1], coord[0]]);
        window.L.polyline(coordinates, { color: '#000', weight: 4 }).addTo(routeLayer.current);
        leafletMap.current.fitBounds(window.L.polyline(coordinates).getBounds(), { padding: [50, 50] });
      }
    } catch (e) {}
  };

  const fetchData = async () => {
    try {
      const res = await fetch(`${API_BASE}/v1/data`);
      if (!res.ok) throw new Error('Backend Offline');
      const jsonData = await res.json();
      setData(jsonData);
      if (jsonData.sellers.length > 0) {
        setForm({
          sellerId: jsonData.sellers[0].id,
          customerId: jsonData.customers[0]?.id || '',
          productId: jsonData.products.filter(p => p.sellerId === jsonData.sellers[0].id)[0]?.id || '',
        });
      }
    } catch (err) {
      setError('System connection lost. Retrying...');
      setTimeout(fetchData, 5000);
    } finally {
      setLoading(false);
    }
  };

  const handleCalculate = async () => {
    if (cart.length === 0 || !form.customerId) return;
    try {
      setCalculating(true);
      setError(null);
      const itemsMap = {};
      cart.forEach(item => itemsMap[item.productId] = item.quantity);

      const nearestRes = await fetch(`${API_BASE}/v1/warehouse/nearest?sellerId=${form.sellerId}&productId=${cart[0].productId}`);
      if (!nearestRes.ok) throw new Error('Warehouse service failed.');
      const nearestData = await nearestRes.json();

      const res = await fetch(`${API_BASE}/v2/shipping-charge/bulk?warehouseId=${nearestData.warehouseId}&customerId=${form.customerId}&deliverySpeed=${form.deliverySpeed}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(itemsMap)
      });
      
      if (!res.ok) throw new Error('Shipping Engine failed calculation.');
      const result = await res.json();
      
      const finalResult = {
        ...result,
        warehouse: data.warehouses.find(w => w.id === nearestData.warehouseId),
        customer: data.customers.find(c => c.id === form.customerId),
        totalItems: cart.reduce((acc, item) => acc + item.quantity, 0)
      };
      
      setCalculation(finalResult);
      if (finalResult.warehouse && finalResult.customer) {
        updateRoute(finalResult.warehouse, finalResult.customer);
      }
      setHistory([...history, { name: `Order #${history.length + 1}`, cost: result.shippingCharge }].slice(-10));
    } catch (err) {
      setError(err.message);
    } finally {
      setCalculating(false);
    }
  };

  const totalWeight = cart.reduce((acc, item) => acc + (item.product.weightKg * item.quantity), 0);

  if (loading) return <div className="loading">CONNECTING TO ENTERPRISE ENGINE...</div>;

  return (
    <div className="container">
      <header>
        <h1>Logistics Hub </h1>
        <p>Enterprise Ready • Zustand State • Hardened Backend</p>
      </header>

      {error && <div className="error-message" style={{ background: '#ff000011', border: '1px solid #ff0000', color: '#ff0000', padding: '1rem', borderRadius: '4px', marginBottom: '1.5rem', fontWeight: 700 }}>{error}</div>}

      <div className="grid">
        <div className="card">
          <div className="card-title"><Package size={16} /> Order Entry</div>
          
          <div className="form-group">
            <label>Business Partner (Seller)</label>
            <select value={form.sellerId} onChange={(e) => setForm({ sellerId: e.target.value })}>
              {data.sellers.map(s => <option key={s.id} value={s.id}>{s.name}</option>)}
            </select>
          </div>

          <div style={{ display: 'flex', gap: '1rem', alignItems: 'flex-end' }}>
            <div className="form-group" style={{ flex: 3 }}>
              <label>SKU Selection</label>
              <select value={form.productId} onChange={(e) => setForm({ productId: e.target.value })}>
                {data.products.filter(p => p.sellerId === form.sellerId).map(p => (
                  <option key={p.id} value={p.id}>{p.name} (₹{p.price?.toLocaleString()})</option>
                ))}
              </select>
            </div>
            <div className="form-group" style={{ flex: 1 }}>
              <label>Qty</label>
              <input 
                type="number" 
                min="1" 
                value={form.quantity} 
                onChange={(e) => setForm({ quantity: parseInt(e.target.value) })}
                style={{ width: '100%', padding: '0.8rem', border: '1px solid #000000ff', background: '#000000' }}
              />
            </div>
            <button className="btn" onClick={addToCart} style={{ flex: 1, marginBottom: '1.5rem', background: '#000', color: '#fff' }}>
              <Plus size={18} />
            </button>
          </div>

          <div className="card-title" style={{ marginTop: '2rem' }}><ShoppingCart size={16} /> Current Order</div>
          {cart.length === 0 ? (
            <p style={{ textAlign: 'center', color: '#888', padding: '2rem', border: '1px dashed #ccc' }}>No items added yet</p>
          ) : (
            <div className="cart-list">
              {cart.map(item => (
                <div key={item.productId} className="result-item" style={{ padding: '1rem 0', borderBottom: '1px solid #eee' }}>
                  <div style={{ flex: 2 }}>
                    <div style={{ fontWeight: 700 }}>{item.product.name}</div>
                    <div style={{ fontSize: '0.7rem', color: '#888' }}>
                      ₹{item.product.price?.toLocaleString()} unit • {(item.product.weightKg * item.quantity).toFixed(1)} kg total
                    </div>
                  </div>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', flex: 1 }}>
                    <button onClick={() => updateQuantity(item.productId, -1)} className="qty-btn"><Minus size={12}/></button>
                    <span style={{ fontWeight: 800, minWidth: '30px', textAlign: 'center' }}>{item.quantity}</span>
                    <button onClick={() => updateQuantity(item.productId, 1)} className="qty-btn"><Plus size={12}/></button>
                  </div>
                  <button onClick={() => removeFromCart(item.productId)} style={{ border: 'none', background: 'none', color: '#ff4444', cursor: 'pointer' }}><Trash2 size={16}/></button>
                </div>
              ))}
              <div style={{ marginTop: '1.5rem', borderTop: '2px solid #000', paddingTop: '1.5rem' }}>
                <div className="form-group">
                  <label>Deliver To (Customer)</label>
                  <select value={form.customerId} onChange={(e) => setForm({ customerId: e.target.value })}>
                    <option value="">Select Destination...</option>
                    {data.customers.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}
                  </select>
                </div>
                <div className="form-group">
                  <label>Service Level</label>
                  <select value={form.deliverySpeed} onChange={(e) => setForm({ deliverySpeed: e.target.value })}>
                    <option value="standard">Standard Road Logistics</option>
                    <option value="express">Express Priority Air/Road</option>
                  </select>
                </div>
                <button className="btn" onClick={handleCalculate} disabled={calculating || cart.length === 0}>
                  {calculating ? 'PROCESSING FINANCIALS...' : 'GENERATE COMPLETE QUOTE'}
                </button>
              </div>
            </div>
          )}

          {calculation && !calculating && (
            <div className="results">
              <div className="result-label" style={{ textAlign: 'center', marginBottom: '0.5rem' }}>SHIPPING TOTAL</div>
              <div className="price">₹{calculation.shippingCharge?.toLocaleString()}</div>
              
              <div className="result-item" style={{ borderBottom: '1px solid #eee', paddingBottom: '0.5rem', marginBottom: '0.5rem' }}>
                <span className="result-label">ESTIMATED DISTANCE</span>
                <span className="result-value">{calculation.distanceKm} KM</span>
              </div>

              <div className="result-item">
                <span className="result-label">BASE SHIPPING</span>
                <span className="result-value">₹{calculation.baseCharge?.toLocaleString()}</span>
              </div>
              <div className="result-item">
                <span className="result-label">FUEL SURCHARGE (5%)</span>
                <span className="result-value">₹{calculation.fuelSurcharge?.toLocaleString()}</span>
              </div>
              <div className="result-item" style={{ borderBottom: '1px solid #eee', paddingBottom: '0.5rem', marginBottom: '0.5rem' }}>
                <span className="result-label">HANDLING & TECH FEE</span>
                <span className="result-value">₹{calculation.handlingFee?.toLocaleString()}</span>
              </div>

              <div className="result-item">
                <span className="result-label">PRODUCT SUBTOTAL</span>
                <span className="result-value">₹{calculation.productSubtotal?.toLocaleString()}</span>
              </div>

              <div className="result-item" style={{ marginTop: '1rem', paddingTop: '1rem', borderTop: '2px solid #000' }}>
                <span className="result-label" style={{ fontWeight: 900 }}>GRAND TOTAL</span>
                <span className="result-value" style={{ fontWeight: 900, fontSize: '1.2rem' }}>₹{calculation.grandTotal?.toLocaleString()}</span>
              </div>
              
              <button 
                className="btn" 
                style={{ marginTop: '1.5rem', backgroundColor: '#000' }}
                onClick={async () => {
                  const itemsMap = {};
                  cart.forEach(item => itemsMap[item.productId] = item.quantity);
                  const res = await fetch(`${API_BASE}/v2/shipping-charge/pdf-invoice-bulk?warehouseId=${calculation.warehouse?.id}&customerId=${calculation.customer?.id}&deliverySpeed=${form.deliverySpeed}`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(itemsMap)
                  });
                  const blob = await res.blob();
                  const url = window.URL.createObjectURL(blob);
                  const a = document.createElement('a');
                  a.href = url;
                  a.download = `invoice_${Date.now()}.pdf`;
                  a.click();
                }}
              >
                DOWNLOAD PDF INVOICE
              </button>
            </div>
          )}
        </div>

        <div className="card">
          <div className="card-title"><BarChart3 size={16} /> Cost Analysis</div>
          <div style={{ width: '100%', height: 300 }}>
            <ResponsiveContainer width="100%" height="100%" minWidth={0} debounce={1} aspect={1.5}>
              <AreaChart data={history}>
                <defs>
                  <linearGradient id="colorCost" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%" stopColor="#000" stopOpacity={0.2}/>
                    <stop offset="95%" stopColor="#000" stopOpacity={0}/>
                  </linearGradient>
                </defs>
                <CartesianGrid strokeDasharray="3 3" stroke="#f0f0f0" vertical={false} />
                <XAxis dataKey="name" stroke="#000" fontSize={10} fontWeight="bold" />
                <YAxis stroke="#000" fontSize={10} fontWeight="bold" />
                <Tooltip contentStyle={{ backgroundColor: '#000', color: '#fff' }}/>
                <Area type="monotone" dataKey="cost" stroke="#000" strokeWidth={3} fill="url(#colorCost)" />
              </AreaChart>
            </ResponsiveContainer>
          </div>
          
          <div className="card-title" style={{ marginTop: '2rem' }}><Globe size={16} /> Real-Time OSRM Path</div>
          <div ref={mapRef} style={{ height: '350px', width: '100%', border: '1px solid #eee' }}></div>
        </div>
      </div>

      <footer style={{ marginTop: '4rem', textAlign: 'center', color: '#ccc', fontSize: '0.7rem' }}>
        SYSTEM • ENTERPRISE LOGISTICS ENGINE • ZUSTAND STORE
      </footer>
    </div>
  );
}

export default App;
