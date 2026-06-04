import { useState, useEffect, useContext, createContext } from 'react';
import { 
  signInWithEmailAndPassword, 
  createUserWithEmailAndPassword, 
  signOut, 
  onAuthStateChanged 
} from 'firebase/auth';
import { auth } from './firebaseConfig';

// Standardized translation utility for web client
const translateFirebaseError = (errorCode) => {
  switch (errorCode) {
    case 'auth/invalid-email':
      return 'The email address is improperly formatted. Please use a valid email (e.g., name@domain.com).';
    case 'auth/user-disabled':
      return 'This medical account has been disabled. Please contact the administrator.';
    case 'auth/user-not-found':
      return 'No corresponding medical account found for this email. Please register.';
    case 'auth/wrong-password':
      return 'Incorrect credentials. Please verify your password and try again.';
    case 'auth/email-already-in-use':
      return 'This email address is already registered to another patient profile.';
    case 'auth/weak-password':
      return 'The password is too weak. It must consist of at least 6 characters.';
    case 'auth/operation-not-allowed':
      return 'Email/Password authentication is disabled in the Firebase Console.';
    default:
      return 'An authorization failure occurred. Verify your medical config parameters.';
  }
};

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const unsubscribe = onAuthStateChanged(auth, (firebaseUser) => {
      setUser(firebaseUser);
      setLoading(false);
    });
    return unsubscribe;
  }, []);

  const register = async (email, password) => {
    setLoading(true);
    setError(null);
    try {
      const userCredential = await createUserWithEmailAndPassword(auth, email, password);
      setLoading(false);
      return { success: true, user: userCredential.user };
    } catch (err) {
      const friendlyMsg = translateFirebaseError(err.code);
      setError(friendlyMsg);
      setLoading(false);
      return { success: false, error: friendlyMsg };
    }
  };

  const login = async (email, password) => {
    setLoading(true);
    setError(null);
    try {
      const userCredential = await signInWithEmailAndPassword(auth, email, password);
      setLoading(false);
      return { success: true, user: userCredential.user };
    } catch (err) {
      const friendlyMsg = translateFirebaseError(err.code);
      setError(friendlyMsg);
      setLoading(false);
      return { success: false, error: friendlyMsg };
    }
  };

  const logout = async () => {
    setLoading(true);
    setError(null);
    try {
      await signOut(auth);
      setLoading(false);
      return { success: true };
    } catch (err) {
      const friendlyMsg = translateFirebaseError(err.code);
      setError(friendlyMsg);
      setLoading(false);
      return { success: false, error: friendlyMsg };
    }
  };

  const value = {
    user,
    loading,
    error,
    login,
    register,
    logout,
    clearError: () => setError(null)
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider component');
  }
  return context;
};
