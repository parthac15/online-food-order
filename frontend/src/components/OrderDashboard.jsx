import React from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { 
  FileText, CreditCard, ChefHat, Car, CheckCircle2, XCircle, Loader2,
  User, DollarSign, Hash, Utensils
} from 'lucide-react';

const STATUS_CONFIG = {
  PLACED: { label: 'Placed', icon: FileText },
  PAYMENT_PROCESSING: { label: 'Payment', icon: CreditCard },
  KITCHEN_PREP: { label: 'Kitchen', icon: ChefHat },
  OUT_FOR_DELIVERY: { label: 'Delivery', icon: Car },
  DELIVERED: { label: 'Delivered', icon: CheckCircle2 },
  CANCELLED: { label: 'Cancelled', icon: XCircle },
};

function OrderDashboard({ orders }) {
  // Compute stats
  const stats = {
    total: orders.length,
    processing: orders.filter((o) =>
      ['PLACED', 'PAYMENT_PROCESSING', 'KITCHEN_PREP', 'OUT_FOR_DELIVERY'].includes(o.status)
    ).length,
    delivered: orders.filter((o) => o.status === 'DELIVERED').length,
    cancelled: orders.filter((o) => o.status === 'CANCELLED').length,
  };

  return (
    <div>
      {/* Stats Grid */}
      <div className="stats-grid">
        <motion.div className="stat-card" whileHover={{ y: -5 }}>
          <span className="stat-value" style={{ color: '#fff' }}>{stats.total}</span>
          <span className="stat-label">Total Orders</span>
        </motion.div>
        <motion.div className="stat-card" whileHover={{ y: -5 }}>
          <span className="stat-value" style={{ color: '#F59E0B' }}>{stats.processing}</span>
          <span className="stat-label">In Progress</span>
        </motion.div>
        <motion.div className="stat-card" whileHover={{ y: -5 }}>
          <span className="stat-value" style={{ color: '#10B981' }}>{stats.delivered}</span>
          <span className="stat-label">Completed</span>
        </motion.div>
        <motion.div className="stat-card" whileHover={{ y: -5 }}>
          <span className="stat-value" style={{ color: '#EF4444' }}>{stats.cancelled}</span>
          <span className="stat-label">Cancelled</span>
        </motion.div>
      </div>

      {/* Order List with AnimatePresence */}
      <div className="order-list">
        <AnimatePresence mode="popLayout">
          {orders.length === 0 ? (
            <motion.div 
              className="empty-state"
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              exit={{ opacity: 0 }}
            >
              <Utensils size={48} />
              <p>Awaiting incoming orders...</p>
            </motion.div>
          ) : (
            orders.map((order) => {
              const config = STATUS_CONFIG[order.status] || STATUS_CONFIG.PLACED;
              const StatusIcon = config.icon;
              
              return (
                <motion.div 
                  layout
                  initial={{ opacity: 0, scale: 0.9, y: 20 }}
                  animate={{ opacity: 1, scale: 1, y: 0 }}
                  exit={{ opacity: 0, scale: 0.9, transition: { duration: 0.2 } }}
                  transition={{ type: 'spring', bounce: 0.3, duration: 0.6 }}
                  className="order-card" 
                  key={order.id}
                >
                  <div className="order-main-info">
                    <div className="order-icon-box">
                      <Utensils size={24} />
                    </div>
                    <div className="order-details">
                      <h3>{order.item}</h3>
                      <div className="order-meta">
                        <span><User size={14}/> {order.customerName}</span>
                        <span><DollarSign size={14}/> {Number(order.amount).toFixed(2)}</span>
                        <span><Hash size={14}/> {order.id}</span>
                      </div>
                    </div>
                  </div>
                  <div className={`status-pill status-${order.status}`}>
                    {['PAYMENT_PROCESSING', 'KITCHEN_PREP', 'OUT_FOR_DELIVERY'].includes(order.status) ? (
                      <Loader2 size={16} className="animate-spin" />
                    ) : (
                      <StatusIcon size={16} />
                    )}
                    {config.label}
                  </div>
                </motion.div>
              );
            })
          )}
        </AnimatePresence>
      </div>
    </div>
  );
}

export default OrderDashboard;
