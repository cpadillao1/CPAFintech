import React, { useState } from 'react';
import api from '../../../api/axiosConfig'; // Ajusta la ruta si es necesario
import Swal from 'sweetalert2';

const BranchCreate = () => {
  const [branchData, setBranchData] = useState({
    code: '',
    name: '',
    address: '',
    city: '',
    country: 'Ecuador' // Valor por defecto
  });
  const [saving, setSaving] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setBranchData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const saveBranch = async (e) => {
    e.preventDefault();

    // Validaciones de integridad (Campos Mandatorios)
    const { code, name, address, city, country } = branchData;
    if (!code.trim() || !name.trim() || !address.trim() || !city.trim() || !country.trim()) {
      return Swal.fire({
        title: 'Campos Requeridos',
        text: 'Por favor, complete todos los campos marcados con asterisco.',
        icon: 'warning',
        confirmButtonColor: '#334155'
      });
    }

    setSaving(true);
    try {
      // Consumo del API según tu estructura
      await api.post('/api/v1/branches/save', branchData);

      Swal.fire({
        title: '¡Sucursal Registrada!',
        text: `La sucursal ${name} ha sido creada exitosamente.`,
        icon: 'success',
        confirmButtonColor: '#10b981'
      });

      // Limpiar formulario tras éxito
      setBranchData({ code: '', name: '', address: '', city: '', country: 'Ecuador' });
    } catch (err) {
      const mensajeError = err.response?.data?.message || err.message;
      Swal.fire({
        title: 'Error de Registro',
        text: typeof mensajeError === 'object' ? JSON.stringify(mensajeError) : mensajeError,
        icon: 'error',
        confirmButtonColor: '#ef4444'
      });
    } finally {
      setSaving(false);
    }
  };

  return (
    <div style={styles.container}>
      <header style={styles.header}>
        <div style={{ flex: 1 }}>
          <h2 style={styles.title}>Administración de Sucursales</h2>
          <p style={styles.subtitle}>Registro y apertura de nuevas agencias bancarias.</p>
        </div>
      </header>

      <div style={styles.formCard}>
        <form onSubmit={saveBranch}>
          <div style={styles.row}>
            <div style={styles.inputGroup}>
              <label style={styles.label}>Código Sucursal <span style={styles.required}>*</span></label>
              <input
                name="code"
                type="text"
                placeholder="Ej: 0002"
                style={styles.input}
                value={branchData.code}
                onChange={handleChange}
              />
            </div>
            <div style={styles.inputGroupFull}>
              <label style={styles.label}>Nombre de Agencia <span style={styles.required}>*</span></label>
              <input
                name="name"
                type="text"
                placeholder="Ej: Sucursal Sur"
                style={styles.inputFull}
                value={branchData.name}
                onChange={handleChange}
              />
            </div>
          </div>

          <div style={styles.inputGroupFull}>
            <label style={styles.label}>Dirección Completa <span style={styles.required}>*</span></label>
            <input
              name="address"
              type="text"
              placeholder="Ej: Av. Maldonado y Joaquin Gutierrez"
              style={styles.inputFull}
              value={branchData.address}
              onChange={handleChange}
            />
          </div>

          <div style={styles.row}>
            <div style={styles.inputGroup}>
              <label style={styles.label}>Ciudad <span style={styles.required}>*</span></label>
              <input
                name="city"
                type="text"
                placeholder="Ej: Quito"
                style={styles.input}
                value={branchData.city}
                onChange={handleChange}
              />
            </div>
            <div style={styles.inputGroup}>
              <label style={styles.label}>País <span style={styles.required}>*</span></label>
              <input
                name="country"
                type="text"
                placeholder="Ej: Ecuador"
                style={styles.input}
                value={branchData.country}
                onChange={handleChange}
              />
            </div>
          </div>

          <div style={styles.footer}>
            <button
              type="submit"
              style={saving ? styles.saveBtnDisabled : styles.saveBtn}
              disabled={saving}
            >
              {saving ? "Registrando..." : "Guardar Sucursal"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

const styles = {
  container: { padding: '20px', backgroundColor: '#f8fafc', minHeight: '100vh', fontFamily: 'sans-serif' },
  header: { display: 'flex', flexDirection: 'column', gap: '10px', marginBottom: '20px', backgroundColor: 'white', padding: '20px', borderRadius: '10px', border: '1px solid #e2e8f0', boxShadow: '0 1px 3px rgba(0,0,0,0.1)' },
  title: { margin: 0, fontSize: '1.25rem', color: '#1e293b', fontWeight: 'bold' },
  subtitle: { margin: 0, color: '#64748b', fontSize: '0.85rem' },
  formCard: { backgroundColor: 'white', padding: '30px', borderRadius: '10px', border: '1px solid #e2e8f0', boxShadow: '0 4px 6px -1px rgba(0,0,0,0.05)', maxWidth: '900px', margin: '0 auto' },
  row: { display: 'flex', gap: '20px', flexWrap: 'wrap', marginBottom: '20px' },
  inputGroup: { display: 'flex', flexDirection: 'column', gap: '6px', flex: 1, minWidth: '200px' },
  inputGroupFull: { display: 'flex', flexDirection: 'column', gap: '6px', flex: 2, minWidth: '100%', marginBottom: '20px' },
  label: { fontSize: '0.75rem', fontWeight: 'bold', color: '#475569', textTransform: 'uppercase' },
  required: { color: '#ef4444' },
  input: { padding: '12px', borderRadius: '6px', border: '1px solid #cbd5e1', outline: 'none', fontSize: '0.9rem' },
  inputFull: { padding: '12px', borderRadius: '6px', border: '1px solid #cbd5e1', outline: 'none', fontSize: '0.9rem' },
  footer: { display: 'flex', justifyContent: 'flex-end', marginTop: '10px' },
  saveBtn: { backgroundColor: '#10b981', color: 'white', border: 'none', padding: '12px 35px', borderRadius: '6px', cursor: 'pointer', fontWeight: 'bold', transition: 'background 0.2s' },
  saveBtnDisabled: { backgroundColor: '#94a3b8', color: 'white', border: 'none', padding: '12px 35px', borderRadius: '6px', cursor: 'not-allowed' },
  loader: { display: 'flex', justifyContent: 'center', alignItems: 'center', height: '80vh' }
};

export default BranchCreate;
