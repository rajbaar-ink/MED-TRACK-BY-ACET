import React from 'react';
import { AuthProvider, useAuth } from './useAuth';

/**
 * AuthProvider component wrapper to handle Firebase auth states.
 * Connects directly to the 'acet-medtrack' Firebase app context.
 */
const AppAuthBootstrap = ({ children }) => {
  return (
    <AuthProvider>
      {children}
    </AuthProvider>
  );
};

export { useAuth };
export default AppAuthBootstrap;
