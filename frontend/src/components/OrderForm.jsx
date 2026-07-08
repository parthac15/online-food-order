import React, { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { ShoppingCart, Loader2, CheckCircle2 } from 'lucide-react';

const MENU_ITEMS = [
  { name: 'Margherita Pizza', price: 12.99 },
  { name: 'Chicken Biryani', price: 14.99 },
  { name: 'Veg Burger', price: 8.99 },
  { name: 'Pasta Alfredo', price: 11.49 },
  { name: 'Paneer Tikka', price: 10.99 },
  { name: 'Fish & Chips', price: 13.49 },
  { name: 'Caesar Salad', price: 9.99 },
  { name: 'Butter Chicken', price: 15.99 },
];

function OrderForm({ onSubmit, loading }) {
  const [customerName, setCustomerName] = useState('');
  const [selectedItem, setSelectedItem] = useState('');
  const [showSuccess, setShowSuccess] = useState(false);

  const selectedMenu = MENU_ITEMS.find((m) => m.name === selectedItem);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!customerName.trim() || !selectedItem) return;

    const success = await onSubmit({
      customerName: customerName.trim(),
      item: selectedItem,
      amount: selectedMenu.price,
    });

    if (success) {
      setShowSuccess(true);
      setCustomerName('');
      setSelectedItem('');
      setTimeout(() => setShowSuccess(false), 3000);
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <div className="input-group">
        <label className="input-label">Customer Name</label>
        <input
          className="premium-input"
          type="text"
          placeholder="e.g., John Doe"
          value={customerName}
          onChange={(e) => setCustomerName(e.target.value)}
          required
        />
      </div>

      <div className="input-group">
        <label className="input-label">Select Dish</label>
        <select
          className="premium-input premium-select"
          value={selectedItem}
          onChange={(e) => setSelectedItem(e.target.value)}
          required
        >
          <option value="">-- Choose from menu --</option>
          {MENU_ITEMS.map((item) => (
            <option key={item.name} value={item.name}>
              {item.name}
            </option>
          ))}
        </select>
      </div>

      <AnimatePresence>
        {selectedMenu && (
          <motion.div 
            className="price-display"
            initial={{ opacity: 0, height: 0, marginBottom: 0 }}
            animate={{ opacity: 1, height: 'auto', marginBottom: 32 }}
            exit={{ opacity: 0, height: 0, marginBottom: 0 }}
            transition={{ type: 'spring', bounce: 0.4 }}
          >
            <span className="price-label">Total Amount</span>
            <span className="price-value">${selectedMenu.price.toFixed(2)}</span>
          </motion.div>
        )}
      </AnimatePresence>

      <motion.button 
        type="submit" 
        className="btn-primary"
        disabled={loading || !customerName || !selectedItem}
        whileHover={{ scale: 1.02 }}
        whileTap={{ scale: 0.98 }}
      >
        {loading ? (
          <>
            <Loader2 className="animate-spin" size={20} />
            Processing...
          </>
        ) : (
          <>
            <ShoppingCart size={20} />
            Place Order
          </>
        )}
      </motion.button>

      <AnimatePresence>
        {showSuccess && (
          <motion.div 
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -10 }}
            style={{
              marginTop: '16px',
              padding: '16px',
              background: 'rgba(16, 185, 129, 0.15)',
              border: '1px solid rgba(16, 185, 129, 0.3)',
              borderRadius: '12px',
              color: '#10B981',
              display: 'flex',
              alignItems: 'center',
              gap: '12px',
              fontWeight: 600
            }}
          >
            <CheckCircle2 size={20} />
            Order transmitted to Camunda Engine!
          </motion.div>
        )}
      </AnimatePresence>
    </form>
  );
}

export default OrderForm;
