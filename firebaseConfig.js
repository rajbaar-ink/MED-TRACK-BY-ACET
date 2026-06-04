/**
 * Firebase Configuration for the 'acet-medtrack' project.
 * Package Context: com.medtrack.health
 * 
 * Generated for authentication state managers and SDK interfaces.
 */
import { initializeApp } from 'firebase/app';
import { getAuth } from 'firebase/auth';

const firebaseConfig = {
  apiKey: "AIzaSyCd0Q6ohw72UW41alpa2DyHOeommE1tdFA",
  authDomain: "acet-medtrack.firebaseapp.com",
  projectId: "acet-medtrack",
  storageBucket: "acet-medtrack.firebasestorage.app",
  messagingSenderId: "366794442070",
  appId: "1:366794442070:web:6eef350ca9fcf781d4a0de"
};

// Initialize Firebase Instance
const app = initializeApp(firebaseConfig);

// Initialize Firebase Authentication Setup
export const auth = getAuth(app);

export default app;
