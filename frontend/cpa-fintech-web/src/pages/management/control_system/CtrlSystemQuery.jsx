import React, { useState, useEffect } from 'react';
import api from '../../../api/axiosConfig';

const CtrlSystemQuery = () => {
  const [config, setConfig] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchConfig = async () => {
      try {
        const response = await api.get('/api/v1/system/config');
        setConfig(response.data);
      } catch (err) {
        console.error("Error fetching system control:", err);
        setError("Could not load system configuration.");
      } finally {
        setLoading(false);
      }
    };
    fetchConfig();
  }, []);

  if (loading) return <div style={styles.loader}>Loading System Configuration...</div>;
  if (error) return <div style={styles.error}>{error}</div>;
  if (!config) return null;

  return (
    <div style={styles.container}>
      <div style={styles.header}>
        <h2 style={styles.title}>🖥️ System Control Center</h2>
        <div style={{
          ...styles.statusBadge,
          // Mapeado a 'status' de tu DB
          backgroundColor: config.status === 'ACTIVE' ? '#22c55e' : '#f59e0b'
        }}>
          {config.status}
        </div>
      </div>

      <div style={styles.grid}>
        {/* Card: Fechas de Negocio */}
        <div style={styles.infoCard}>
          <h4 style={styles.cardTitle}>📅 Business Calendar</h4>
          <div style={styles.dataRow}>
            <span style={styles.label}>Previous Date:</span>
            <span style={styles.value}>{config.beforeBusinessDate}</span>
          </div>
          <div style={styles.dataRow}>
            <span style={styles.label}>Business Date:</span>
            <span style={styles.value}>{config.businessDate}</span>
          </div>
          <div style={styles.dataRow}>
            <span style={styles.label}>Next Date:</span>
            <span style={styles.value}>{config.afterBusinessDate}</span>
          </div>
        </div>

        {/* Card: Estado del Proceso */}
        <div style={styles.infoCard}>
          <h4 style={styles.cardTitle}>⚙️ Process Info</h4>
          <div style={styles.dataRow}>
            <span style={styles.label}>System Status:</span>
            <span style={{...styles.value, color: config.status === 'IN_CLOSING' ? '#ef4444' : '#3b82f6'}}>
              {config.status}
            </span>
          </div>
          <div style={styles.dataRow}>
            <span style={styles.label}>Last Update:</span>
            <span style={styles.value}>{config.lastUpdate}</span>
          </div>
          <div style={styles.dataRow}>
            <span style={styles.label}>Version:</span>
            <span style={styles.value}>v_{config.version}</span>
          </div>
        </div>
      </div>

      {/* Footer Informativo */}
      <div style={styles.footer}>
        <p><strong>Note:</strong> Business dates are synchronized with the core accounting engine. Version control is active for audit purposes.</p>
      </div>
    </div>
  );
};

const styles = {
    container: { padding: '20px', backgroundColor: '#ffffff', borderRadius: '12px', boxShadow: '0 4px 6px -1px rgba(0, 0, 0, 0.1)', border: '1px solid #e2e8f0'},
    header: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '25px', borderBottom: '2px solid #f1f5f9', paddingBottom: '15px'},
    title: { margin: 0, fontSize: '1.5rem', color: '#0f172a' },
    statusBadge: { padding: '6px 15px', borderRadius: '20px', color: 'white', fontSize: '0.8rem', fontWeight: 'bold', textTransform: 'uppercase'},
    grid: { display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(300px, 1fr))', gap: '20px'},
    infoCard: { padding: '15px', backgroundColor: '#f8fafc', borderRadius: '8px', border: '1px solid #e2e8f0'},
    cardTitle: { margin: '0 0 15px 0', fontSize: '1rem', color: '#475569', borderBottom: '1px solid #cbd5e1', paddingBottom: '5px'},
    dataRow: { display: 'flex', justifyContent: 'space-between', marginBottom: '10px', fontSize: '0.9rem'},
    label: { color: '#64748b', fontWeight: '500' },
    value: { color: '#1e293b', fontWeight: 'bold', fontFamily: 'monospace' },
    footer: { marginTop: '30px', padding: '15px', backgroundColor: '#fff7ed', border: '1px solid #ffedd5', borderRadius: '8px', color: '#9a3412', fontSize: '0.85rem'},
    loader: { textAlign: 'center', padding: '50px', color: '#3b82f6', fontWeight: 'bold' },
    error: { textAlign: 'center', padding: '50px', color: '#ef4444', fontWeight: 'bold' }
};

export default CtrlSystemQuery;