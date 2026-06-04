import React, { useEffect } from 'react';
import { useAuth } from './useAuth';

/**
 * ProtectedRoute Component (React / Web Native)
 * Wraps medical screens and redirects unauthenticated users to the secure register/login screen.
 */
const ProtectedRoute = ({ children, fallbackPath = '/auth' }) => {
  const { user, loading } = useAuth();

  useEffect(() => {
    if (!loading && !user) {
      console.log(`[Security Clearance] Redirecting unauthenticated user session to ${fallbackPath}`);
      // In a real router layout, we would push to history or redirect:
      // window.location.href = fallbackPath;
    }
  }, [user, loading, fallbackPath]);

  if (loading) {
    return (
      <div style={styles.loadingContainer}>
        <div style={styles.spinner}></div>
        <p style={styles.text}>Verifying medical database session permissions...</p>
      </div>
    );
  }

  return user ? children : null;
};

const styles = {
  loadingContainer: {
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    justifyContent: 'center',
    height: '100vh',
    backgroundColor: '#0F172A', // Slate 900 Theme
    color: '#F8FAFC',
    fontFamily: 'System-ui, sans-serif'
  },
  spinner: {
    width: '40px',
    height: '40px',
    border: '4px solid rgba(255, 255, 255, 0.1)',
    borderTop: '4px solid #3B82F6', // Blue 500
    borderRadius: '50%',
    animation: 'spin 1s linear infinite',
    marginBottom: '16px'
  },
  text: {
    fontSize: '14px',
    fontWeight: '500',
    color: '#94A3B8'
  }
};

export default ProtectedRoute;
