import React, { useState, useEffect } from 'react';
import { createClient } from '@supabase/supabase-api'; // Assumes Supabase SDK context

const supabaseUrl = 'https://acet-medtrack.supabase.co';
const supabaseKey = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.dummy_supabase_secret_key';
const supabase = createClient(supabaseUrl, supabaseKey);

/**
 * MedicationList React Component
 * Reads prescriptions in real-time from Supabase database table and triggers a manual creation drawer.
 */
export default function MedicationList() {
  const [medicines, setMedicines] = useState([]);
  const [loading, setLoading] = useState(true);
  const [toastMessage, setToastMessage] = useState(null);
  const [showAddModal, setShowAddModal] = useState(false);

  // Form Fields
  const [name, setName] = useState('');
  const [dosage, setDosage] = useState('');
  const [times, setTimes] = useState('');
  const [frequency, setFrequency] = useState('Daily');
  const [instructions, setInstructions] = useState('After meal');
  const [critical, setCritical] = useState(false);

  useEffect(() => {
    fetchMedicines();

    // Subscribe to real-time additions/modifications using Supabase Realtime
    const subscription = supabase
      .channel('public:medicines')
      .on('postgres_changes', { event: '*', scheme: 'public', table: 'medicines' }, (payload) => {
        console.log('[Supabase Realtime] Change detected:', payload);
        fetchMedicines(); // Refresh on changes
        triggerToast('Database updated in real-time');
      })
      .subscribe();

    return () => {
      supabase.removeChannel(subscription);
    };
  }, []);

  const fetchMedicines = async () => {
    try {
      setLoading(true);
      const { data, error } = await supabase
        .from('medicines')
        .select('*')
        .order('id', { ascending: false });

      if (error) throw error;
      setMedicines(data || []);
    } catch (err) {
      console.error('Error fetching prescriptions:', err.message);
    } finally {
      setLoading(false);
    }
  };

  const triggerToast = (msg) => {
    setToastMessage(msg);
    setTimeout(() => setToastMessage(null), 3000);
  };

  const handleAddMedication = async (e) => {
    e.preventDefault();
    if (!name) return;

    try {
      const { data, error } = await supabase
        .from('medicines')
        .insert([
          {
            name,
            dosage,
            timesString: times,
            frequency,
            instructions,
            is_critical: critical,
            qtyRemaining: 30,
            qtyNeeded: 5,
            type: 'Pill'
          }
        ]);

      if (error) throw error;
      
      triggerToast(`Prescription saved: ${name}`);
      setShowAddModal(false);
      setName('');
      setDosage('');
      setTimes('');
      fetchMedicines();
    } catch (err) {
      triggerToast(`Error: ${err.message}`);
    }
  };

  return (
    <div style={styles.container}>
      {toastMessage && (
        <div style={styles.globalToast}>
          <span style={styles.toastIcon}>🔔</span>
          {toastMessage}
        </div>
      )}

      <header style={styles.header}>
        <h2>Your Prescriptions</h2>
        <p>Real-time database records synchronized via Supabase Console</p>
      </header>

      {loading ? (
        <p style={styles.loading}>Connecting to secure tables...</p>
      ) : medicines.length === 0 ? (
        <div style={styles.emptyState}>No medicines configured. Press the floating action button below.</div>
      ) : (
        <div style={styles.grid}>
          {medicines.map((med) => (
            <div key={med.id} style={styles.card}>
              <div style={styles.cardHeader}>
                <h3 style={styles.medName}>
                  {med.name} {med.is_critical && <span style={styles.criticalBadge}>CRITICAL</span>}
                </h3>
                <span style={styles.dosageBadge}>{med.dosage}</span>
              </div>
              <p style={styles.details}><strong>Frequency:</strong> {med.frequency}</p>
              <p style={styles.details}><strong>Times:</strong> {med.timesString || 'Ad-Hoc'}</p>
              <p style={styles.details}><strong>Instructions:</strong> {med.instructions}</p>
            </div>
          ))}
        </div>
      )}

      {/* Floating Action Button */}
      <button style={styles.fab} onClick={() => setShowAddModal(true)}>
        + Add Medicine
      </button>

      {/* Add Medication Modal */}
      {showAddModal && (
        <div style={styles.modalOverlay}>
          <div style={styles.modalContent}>
            <h3>Add New Medication</h3>
            <form onSubmit={handleAddMedication} style={styles.form}>
              <label style={styles.label}>Medicine Name</label>
              <input 
                type="text" 
                style={styles.input} 
                value={name} 
                onChange={(e) => setName(e.target.value)} 
                required 
                placeholder="e.g. Paracetamol"
              />

              <label style={styles.label}>Dosage Amount</label>
              <input 
                type="text" 
                style={styles.input} 
                value={dosage} 
                onChange={(e) => setDosage(e.target.value)} 
                placeholder="e.g. 500mg"
              />

              <label style={styles.label}>Daily Times (comma separated)</label>
              <input 
                type="text" 
                style={styles.input} 
                value={times} 
                onChange={(e) => setTimes(e.target.value)} 
                placeholder="e.g. 08:00,20:00"
              />

              <div style={styles.row}>
                <label style={styles.checkboxLabel}>
                  <input 
                    type="checkbox" 
                    checked={critical} 
                    onChange={(e) => setCritical(e.target.checked)} 
                  />
                  Mark as High Priority (Hospital Grade Reminder)
                </label>
              </div>

              <div style={styles.actions}>
                <button type="button" style={styles.cancelBtn} onClick={() => setShowAddModal(false)}>Cancel</button>
                <button type="submit" style={styles.submitBtn}>Save prescription</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

const styles = {
  container: {
    padding: '24px',
    backgroundColor: '#0F172A',
    minHeight: '100vh',
    color: '#E2E8F0',
    fontFamily: 'system-ui, sans-serif',
    position: 'relative'
  },
  header: {
    marginBottom: '24px',
    borderBottom: '1px solid #334155',
    paddingBottom: '12px'
  },
  globalToast: {
    position: 'fixed',
    top: '20px',
    left: '50%',
    transform: 'translateX(-50%)',
    backgroundColor: '#1E293B',
    color: '#10B981',
    padding: '12px 24px',
    borderRadius: '8px',
    boxShadow: '0 4px 12px rgba(0,0,0,0.5)',
    zIndex: 1000,
    fontWeight: 'bold',
    display: 'flex',
    alignItems: 'center',
    gap: '8px',
    borderLeft: '4px solid #10B981',
  },
  toastIcon: {
    fontSize: '16px'
  },
  loading: {
    color: '#94A3B8'
  },
  emptyState: {
    textAlign: 'center',
    color: '#64748B',
    padding: '48px 0'
  },
  grid: {
    display: 'grid',
    gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))',
    gap: '16px'
  },
  card: {
    backgroundColor: '#1E293B',
    border: '1px solid #334155',
    borderRadius: '12px',
    padding: '16px',
    transition: 'transform 0.2s'
  },
  cardHeader: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: '12px'
  },
  medName: {
    fontSize: '18px',
    fontWeight: '600',
    margin: 0,
    color: '#F8FAFC'
  },
  criticalBadge: {
    backgroundColor: '#EF4444',
    color: '#FFF',
    fontSize: '9px',
    fontWeight: 'bold',
    padding: '2px 6px',
    borderRadius: '4px',
    marginLeft: '8px',
    verticalAlign: 'middle'
  },
  dosageBadge: {
    backgroundColor: '#3B82F6',
    color: '#FFF',
    fontSize: '12px',
    fontWeight: '500',
    padding: '2px 8px',
    borderRadius: '12px'
  },
  details: {
    fontSize: '14px',
    color: '#94A3B8',
    margin: '4px 0'
  },
  fab: {
    position: 'fixed',
    bottom: '30px',
    right: '30px',
    backgroundColor: '#3B82F6',
    color: 'white',
    border: 'none',
    padding: '16px 24px',
    borderRadius: '30px',
    fontSize: '16px',
    fontWeight: 'bold',
    cursor: 'pointer',
    boxShadow: '0 4px 14px rgba(59, 130, 246, 0.4)'
  },
  modalOverlay: {
    position: 'fixed',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    backgroundColor: 'rgba(15, 23, 42, 0.8)',
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    zIndex: 100
  },
  modalContent: {
    backgroundColor: '#1E293B',
    padding: '24px',
    borderRadius: '16px',
    width: '100%',
    maxWidth: '450px',
    border: '1px solid #475569'
  },
  form: {
    display: 'flex',
    flexDirection: 'column',
    gap: '12px'
  },
  label: {
    fontSize: '12px',
    fontWeight: '600',
    color: '#94A3B8'
  },
  input: {
    backgroundColor: '#0F172A',
    border: '1px solid #475569',
    borderRadius: '6px',
    padding: '10px',
    color: 'white',
    outline: 'none'
  },
  row: {
    display: 'flex',
    alignItems: 'center',
    marginTop: '6px'
  },
  checkboxLabel: {
    fontSize: '13px',
    display: 'flex',
    alignItems: 'center',
    gap: '8px',
    cursor: 'pointer'
  },
  actions: {
    display: 'flex',
    justifyContent: 'flex-end',
    gap: '12px',
    marginTop: '16px'
  },
  cancelBtn: {
    backgroundColor: '#475569',
    color: 'white',
    border: 'none',
    padding: '10px 16px',
    borderRadius: '6px',
    cursor: 'pointer'
  },
  submitBtn: {
    backgroundColor: '#10B981',
    color: 'white',
    border: 'none',
    padding: '10px 16px',
    borderRadius: '6px',
    cursor: 'pointer'
  }
};
