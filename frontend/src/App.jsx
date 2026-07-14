import React, { useState, useEffect, useCallback } from 'react';
import { motion } from 'framer-motion';
import { UtensilsCrossed, Activity } from 'lucide-react';
import OrderForm from './components/OrderForm';
import OrderDashboard from './components/OrderDashboard';

const API_BASE = 'https://order-service-1s49.onrender.com/api/orders';

function App() {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(false);

  const fetchOrders = useCallback(async () => {
    try {
      const res = await fetch(API_BASE);
      if (res.ok) {
        const data = await res.json();
        setOrders(data.reverse()); // newest first
      }
    } catch (err) {
      console.error('Failed to fetch orders:', err);
    }
  }, []);

  // Poll every 2 seconds
  useEffect(() => {
    fetchOrders();
    const interval = setInterval(fetchOrders, 2000);
    return () => clearInterval(interval);
  }, [fetchOrders]);

  const handlePlaceOrder = async (orderData) => {
    setLoading(true);
    try {
      const res = await fetch(API_BASE, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(orderData),
      });
      if (res.ok) {
        await fetchOrders();
        return true;
      }
      return false;
    } catch (err) {
      console.error('Failed to place order:', err);
      return false;
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
      {/* Animated Background */}
      <div className="aurora-bg">
        <div className="aurora-orb orb-1"></div>
        <div className="aurora-orb orb-2"></div>
        <div className="aurora-orb orb-3"></div>
      </div>

      <div className="app-container">
        {/* Premium Navbar */}
        <motion.nav 
          className="navbar"
          initial={{ y: -50, opacity: 0 }}
          animate={{ y: 0, opacity: 1 }}
          transition={{ duration: 0.6, ease: [0.16, 1, 0.3, 1] }}
        >
          <div className="brand-section">
            <div className="brand-icon">
              <UtensilsCrossed size={24} strokeWidth={2.5} />
            </div>
            <div className="brand-text">
              <h1>FoodFlow</h1>
              <p>Event-Driven Architecture</p>
            </div>
          </div>
          <div className="status-indicator">
            <div className="status-pulse"></div>
            System Online
          </div>
        </motion.nav>

        {/* Main Grid Layout */}
        <div className="main-grid">
          {/* Form Panel */}
          <motion.div 
            className="glass-panel"
            initial={{ x: -30, opacity: 0 }}
            animate={{ x: 0, opacity: 1 }}
            transition={{ duration: 0.6, delay: 0.1, ease: [0.16, 1, 0.3, 1] }}
          >
            <div className="section-header">
              <h2>New Order</h2>
              <p>Submit an order to start the Camunda workflow.</p>
            </div>
            <OrderForm onSubmit={handlePlaceOrder} loading={loading} />
          </motion.div>

          {/* Dashboard Panel */}
          <motion.div 
            className="glass-panel"
            initial={{ x: 30, opacity: 0 }}
            animate={{ x: 0, opacity: 1 }}
            transition={{ duration: 0.6, delay: 0.2, ease: [0.16, 1, 0.3, 1] }}
          >
            <div className="section-header">
              <h2>
                <Activity size={28} className="text-accent" />
                Live Command Center
              </h2>
              <p>Real-time orchestration monitoring via ActiveMQ & React polling.</p>
            </div>
            <OrderDashboard orders={orders} />
          </motion.div>
        </div>
      </div>
    </>
  );
}

export default App;
