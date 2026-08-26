import React, { useState, useEffect } from 'react';
import api from '../../../api/axiosConfig';
import Swal from 'sweetalert2';

const InterestRangeCreate = () => {
  const [groups, setGroups] = useState([]);
  const [selectedGroupId, setSelectedGroupId] = useState('');
  const [ranges, setRanges] = useState([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);

  const [newRange, setNewRange] = useState({
    minAmount: '',
    maxAmount: '',
    rateValue: ''
  });

  useEffect(() => {
    fetchGroups();
  }, []);

  const fetchGroups = async () => {
    try {
      const res = await api.get('/api/v1/interest-groups/active');
      setGroups(res.data || []);
    } catch (err) {
      console.error("Error loading groups:", err);
    }
  };

  const fetchRanges = async (groupId) => {
    if (!groupId) return;
    setLoading(true);
    try {
      const res = await api.get(`/api/v1/interest-groups/${groupId}/ranges`);
      setRanges(res.data || []);
    } catch (err) {
      console.error("Error loading ranges:", err);
    } finally {
      setLoading(false);
    }
  };

  const handleGroupChange = (e) => {
    const id = e.target.value;
    setSelectedGroupId(id);
    fetchRanges(id);
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setNewRange(prev => ({ ...prev, [name]: value }));
  };

  const handleSaveRange = async () => {
    if (!selectedGroupId) {
      return Swal.fire({ title: 'Atención', text: 'Por favor selecciona una Estrategia primero.', icon: 'info' });
    }

    // Convertimos explícitamente a número para el Backend
    const payload = {
      minAmount: parseFloat(newRange.minAmount),
      maxAmount: parseFloat(newRange.maxAmount),
      rateValue: parseFloat(newRange.rateValue)
    };

    // Validaciones básicas
    if (isNaN(payload.minAmount) || isNaN(payload.maxAmount) || isNaN(payload.rateValue)) {
      return Swal.fire({ title: 'Error', text: 'Todos los campos deben ser valores numéricos.', icon: 'warning' });
    }

    if (payload.minAmount >= payload.maxAmount) {
      return Swal.fire({ title: 'Lógica Inválida', text: 'El monto mínimo no puede ser mayor o igual al máximo.', icon: 'error' });
    }

    setSaving(true);
    try {
      // POST al endpoint del detalle vinculado al ID del maestro
      await api.post(`/api/v1/interest-groups/${selectedGroupId}/ranges`, payload);

      Swal.fire({
        title: '¡Guardado!',
        text: 'El rango se ha vinculado correctamente.',
        icon: 'success',
        timer: 1500,
        showConfirmButton: false
      });

      setNewRange({ minAmount: '', maxAmount: '', rateValue: '' });
      fetchRanges(selectedGroupId);
    } catch (err) {
      console.error("Error al guardar:", err.response?.data);
      const errorMsg = err.response?.data?.message || 'Error de persistencia en el servidor';

      Swal.fire({
        title: 'Error al Guardar',
        text: errorMsg,
        icon: 'error'
      });
    } finally {
      setSaving(false);
    }
  };

  return (
    <div style={styles.container}>
      <header style={styles.header}>
        <div>
          <h2 style={styles.title}>Matriz de Tasas de Interés</h2>
          <p style={styles.subtitle}>Configuración de rangos de saldo y tasas anuales progresivas.</p>
        </div>

        <div style={styles.groupSelector}>
          <label style={styles.label}>Estrategia Maestra (Header)</label>
          <select
            style={styles.select}
            value={selectedGroupId}
            onChange={handleGroupChange}
          >
            <option value="">-- Seleccionar Estrategia --</option>
            {groups.map(g => (
              <option key={g.id} value={g.id}>{g.name} ({g.code})</option>
            ))}
          </select>
        </div>
      </header>

      {selectedGroupId && (
        <div style={styles.mainContent}>
          <div style={styles.quickForm}>
            <div style={styles.inputGroup}>
              <label style={styles.label}>Monto Mínimo</label>
              <input
                type="number" name="minAmount" placeholder="0.00"
                value={newRange.minAmount} onChange={handleInputChange} style={styles.input}
              />
            </div>
            <div style={styles.inputGroup}>
              <label style={styles.label}>Monto Máximo</label>
              <input
                type="number" name="maxAmount" placeholder="999.99"
                value={newRange.maxAmount} onChange={handleInputChange} style={styles.input}
              />
            </div>
            <div style={styles.inputGroup}>
              <label style={styles.label}>Tasa Anual (%)</label>
              <input
                type="number" name="rateValue" placeholder="5.25"
                value={newRange.rateValue} onChange={handleInputChange} style={styles.input}
              />
            </div>
            <button
              onClick={handleSaveRange}
              style={saving ? styles.addBtnDisabled : styles.addBtn}
              disabled={saving}
            >
              {saving ? "Cargando..." : "+ Agregar Rango"}
            </button>
          </div>

          <div style={styles.tableCard}>
            {loading ? (
              <div style={{padding: '40px', textAlign: 'center'}}>Actualizando matriz...</div>
            ) : (
              <table style={styles.table}>
                <thead>
                  <tr style={styles.thead}>
                    <th style={styles.th}>Saldo Mínimo</th>
                    <th style={styles.th}>Saldo Máximo</th>
                    <th style={styles.th}>Tasa Aplicada</th>
                    <th style={styles.th}>Estado</th>
                  </tr>
                </thead>
                <tbody>
                  {ranges.length === 0 ? (
                    <tr><td colSpan="4" style={styles.tdEmpty}>No hay rangos definidos para esta estrategia.</td></tr>
                  ) : (
                    ranges.map((r, i) => (
                      <tr key={r.id} style={i % 2 === 0 ? {} : styles.trAlternate}>
                        <td style={styles.td}>${Number(r.minAmount).toLocaleString(undefined, {minimumFractionDigits: 2})}</td>
                        <td style={styles.td}>${Number(r.maxAmount).toLocaleString(undefined, {minimumFractionDigits: 2})}</td>
                        <td style={styles.td}><strong>{r.rateValue}% Anual</strong></td>
                        <td style={styles.td}>
                          <span style={styles.badgeActive}>ACTIVO</span>
                        </td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            )}
          </div>
        </div>
      )}
    </div>
  );
};

const styles = {
  container: { padding: '20px', backgroundColor: '#f8fafc', minHeight: '100vh', fontFamily: 'sans-serif' },
  header: { display: 'flex', justifyContent: 'space-between', alignItems: 'flex-end', backgroundColor: 'white', padding: '25px', borderRadius: '12px', border: '1px solid #e2e8f0', marginBottom: '20px' },
  title: { margin: 0, fontSize: '1.4rem', color: '#1e293b' },
  subtitle: { margin: 0, color: '#64748b', fontSize: '0.9rem' },
  groupSelector: { width: '350px' },
  label: { fontSize: '0.7rem', fontWeight: 'bold', color: '#475569', textTransform: 'uppercase', marginBottom: '5px', display: 'block' },
  select: { width: '100%', padding: '12px', borderRadius: '8px', border: '2px solid #3b82f6', outline: 'none', backgroundColor: '#f0f9ff' },
  mainContent: { animation: 'fadeIn 0.3s ease-in' },
  quickForm: { display: 'flex', gap: '20px', backgroundColor: '#334155', padding: '20px', borderRadius: '10px', marginBottom: '20px', alignItems: 'flex-end' },
  inputGroup: { flex: 1 },
  input: { width: '100%', padding: '10px', borderRadius: '6px', border: 'none', fontSize: '0.9rem', outline: 'none' },
  addBtn: { backgroundColor: '#10b981', color: 'white', border: 'none', padding: '10px 25px', borderRadius: '6px', cursor: 'pointer', fontWeight: 'bold', height: '40px' },
  addBtnDisabled: { backgroundColor: '#94a3b8', color: 'white', border: 'none', padding: '10px 25px', borderRadius: '6px', cursor: 'not-allowed', height: '40px' },
  tableCard: { backgroundColor: 'white', borderRadius: '12px', border: '1px solid #e2e8f0', overflow: 'hidden', boxShadow: '0 4px 6px -1px rgba(0,0,0,0.05)' },
  table: { width: '100%', borderCollapse: 'collapse' },
  thead: { backgroundColor: '#f8fafc', borderBottom: '2px solid #e2e8f0' },
  th: { textAlign: 'left', padding: '15px', fontSize: '0.8rem', color: '#475569', textTransform: 'uppercase' },
  td: { padding: '15px', borderBottom: '1px solid #f1f5f9', color: '#1e293b', fontSize: '0.95rem' },
  trAlternate: { backgroundColor: '#fdfdfd' },
  tdEmpty: { padding: '40px', textAlign: 'center', color: '#94a3b8' },
  badgeActive: { backgroundColor: '#dcfce7', color: '#166534', padding: '4px 10px', borderRadius: '20px', fontSize: '0.75rem', fontWeight: 'bold' }
};

export default InterestRangeCreate;
