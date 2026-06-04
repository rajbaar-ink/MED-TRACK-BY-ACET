import React, { createContext, useContext, useState } from 'react';

const ToastContext = createContext(null);

export const ToastProvider = ({ children }) => {
  const [toasts, setToasts] = useState([]);

  const showToast = (message, type = 'success') => {
    const id = Date.now();
    setToasts((prev) => [...prev, { id, message, type }]);
    
    // Automatically dismiss after 4 seconds
    setTimeout(() => {
      setToasts((prev) => prev.filter((toast) => toast.id !== id));
    }, 4000);
  };

  const removeToast = (id) => {
    setToasts((prev) => prev.filter((toast) => toast.id !== id));
  };

  return (
    <ToastContext.Provider value={{ showToast }}>
      {children}
      <div style={styles.toastContainer}>
        {toasts.map((toast) => (
          <div key={toast.id} style={{ ...styles.toast, ...styles[toast.type] }}>
            <span style={styles.message}>{toast.message}</span>
            <button style={styles.closeBtn} onClick={() => removeToast(toast.id)}>×</button>
          </div>
        ))}
      </div>
    </ToastContext.Provider>
  );
};

export const useToast = () => {
  const context = useContext(ToastContext);
  if (!context) {
    throw new Error('useToast must be used within a ToastProvider');
  }
  return context;
};

const styles = {
  toastContainer: {
    position: 'fixed',
    bottom: '24px',
    left: '24px',
    display: 'flex',
    flexDirection: 'column',
    gap: '10px',
    zIndex: 9999,
  },
  toast: {
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'space-between',
    padding: '12px 20px',
    borderRadius: '8px',
    boxShadow: '0 10px 15px -3px rgba(0, 0, 0, 0.3)',
    color: 'white',
    fontSize: '14px',
    fontWeight: '500',
    minWidth: '280px',
    maxWidth: '400px',
    transition: 'all 0.3s ease',
  },
  success: {
    backgroundColor: '#059669', // Emerald 600
    borderLeft: '4px solid #34D399',
  },
  error: {
    backgroundColor: '#DC2626', // Red 600
    borderLeft: '4px solid #F87171',
  },
  info: {
    backgroundColor: '#2563EB', // Blue 600
    borderLeft: '4px solid #60A5FA',
  },
  message: {
    flex: 1,
    marginRight: '12px',
  },
  closeBtn: {
    background: 'none',
    border: 'none',
    color: 'rgba(255, 255, 255, 0.7)',
    fontSize: '18px',
    cursor: 'pointer',
    padding: '0 4px',
  }
};
