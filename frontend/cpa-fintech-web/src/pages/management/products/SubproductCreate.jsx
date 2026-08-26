import React, { useState, useEffect } from 'react';
import api from '../../../api/axiosConfig';
import Swal from 'sweetalert2';

const SubproductCreate = () => {
  const [products, setProducts] = useState([]);
  const [interestGroups, setInterestGroups] = useState([]);
  const [selectedGroupDetails, setSelectedGroupDetails] = useState(null);
  const [showModal, setShowModal] = useState(false);

  const [formData, setFormData] = useState({
    productId: '',
    interestGroupId: '',
    code: '',
    name: '',
    description: '',
    generatesInterest: false,
    statementFrequency: 'MONTHLY',
    status: 'ACTIVE'
  });

  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    const loadData = async () => {
      try {
        setLoading(true);
        const [prodRes, groupRes] = await Promise.all([
          api.get('/api/v1/products'),
          api.get('/api/v1/interest-groups/active')
        ]);
        setProducts(prodRes.data || []);
        setInterestGroups(groupRes.data || []);
      } catch (err) {
        console.error("Error loading data:", err);
      } finally {
        setLoading(false);
      }
    };
    loadData();
  }, []);

  const canGenerateInterest = () => {
      // Usamos doble igual (==) para comparar String con Number
      const selectedProduct = products.find(p => String(p.id) === String(formData.productId));
      if (!selectedProduct) return false;

      const code = selectedProduct.code.toUpperCase();
      return code.includes('SAV') || code.includes('DPF');
  };

  const handleChange = (e) => {
      const { name, value, type, checked } = e.target;

      setFormData(prev => {
        const newValue = type === 'checkbox' ? checked : value;
        const nextState = { ...prev, [name]: newValue };

        if (name === 'productId') {
          const product = products.find(p => String(p.id) === String(value));
          const code = product?.code.toUpperCase() || '';
          if (!(code.includes('SAV') || code.includes('DPF'))) {
            nextState.generatesInterest = false;
            nextState.interestGroupId = '';
          }
        }

        if (name === 'generatesInterest' && !checked) {
          nextState.interestGroupId = '';
        }

        return nextState;
      });

      // CORRECCIÓN PARA EL MODAL:
      if (name === 'interestGroupId') {
        if (!value) {
          setSelectedGroupDetails(null);
        } else {
          // Forzamos la comparación de strings para que Map find no falle con los INTs
          const details = interestGroups.find(g => String(g.id) === String(value));
          console.log("Grupo seleccionado:", details); // Debug para ver si lo encuentra
          setSelectedGroupDetails(details || null);
        }
      }
    };

  const handleSave = async () => {
    // Basic field validations
    if (!formData.productId) {
      return Swal.fire({ title: 'Required Field', text: 'Parent product is mandatory.', icon: 'warning', confirmButtonColor: '#334155' });
    }
    if (!formData.code.trim()) {
      return Swal.fire({ title: 'Required Field', text: 'Subproduct code is required.', icon: 'warning', confirmButtonColor: '#334155' });
    }
    if (!formData.name.trim()) {
      return Swal.fire({ title: 'Required Field', text: 'Subproduct name is required.', icon: 'warning', confirmButtonColor: '#334155' });
    }

    // NUEVA VALIDACIÓN: Si es SAV o DPF, el interés es obligatorio
    if (canGenerateInterest()) {
        if (!formData.generatesInterest) {
            return Swal.fire({
                title: 'Business Rule',
                text: 'Savings (SAV) or Fixed Term (DPF) products must generate interest. Please check the box.',
                icon: 'info',
                confirmButtonColor: '#334155'
            });
        }
        if (!formData.interestGroupId) {
            return Swal.fire({
                title: 'Required Field',
                text: 'You must select an interest strategy for this type of product.',
                icon: 'warning',
                confirmButtonColor: '#334155'
            });
        }
    }

    setSaving(true);
    try {
      await api.post(`/api/v1/management/subproducts/product/${formData.productId}`, formData);

      Swal.fire({
        title: '¡Successful Operation!',
        text: 'The subproduct has been created correctly.',
        icon: 'success',
        confirmButtonColor: '#10b981'
      });

      setFormData({
        productId: '', interestGroupId: '', code: '', name: '',
        description: '', generatesInterest: false, statementFrequency: 'MONTHLY', status: 'ACTIVE'
      });
    } catch (err) {
      const msg = err.response?.data?.message || err.message;
      Swal.fire({
        title: 'Error processing',
        text: typeof msg === 'object' ? JSON.stringify(msg) : msg,
        icon: 'error',
        confirmButtonColor: '#ef4444'
      });
    } finally {
      setSaving(false);
    }
  };

  if (loading) return <div style={styles.loader}>Loading subproduct structure...</div>;

  return (
    <div style={styles.container}>
      <header style={styles.header}>
        <div style={{ marginBottom: '10px' }}>
          <h2 style={styles.title}>Subproduct Configuration</h2>
          <p style={styles.subtitle}>Define commercial rules for a specific financial Subproduct.</p>
        </div>

        <div style={styles.formInputs}>
          <div style={styles.inputGroup}>
            <label style={styles.label}>Parent Product <span style={styles.required}>*</span></label>
            <select name="productId" value={formData.productId} onChange={handleChange} style={styles.input}>
                <option value="">-- Select --</option>
                {products.map(p => <option key={p.id} value={p.id}>{p.name} ({p.code})</option>)}
            </select>
          </div>

          <div style={styles.inputGroup}>
            <label style={styles.label}>Code <span style={styles.required}>*</span></label>
            <input name="code" value={formData.code} onChange={handleChange} style={styles.inputSmall} placeholder="SAV-001" />
          </div>

          <div style={styles.inputGroup}>
            <label style={styles.label}>Name <span style={styles.required}>*</span></label>
            <input name="name" value={formData.name} onChange={handleChange} style={styles.inputTxt} placeholder="Junior Savings" />
          </div>

          <div style={styles.inputGroup}>
            <label style={styles.label}>Frequency <span style={styles.required}>*</span></label>
            <select name="statementFrequency" value={formData.statementFrequency} onChange={handleChange} style={styles.inputSmall}>
                <option value="MONTHLY">MONTHLY</option>
                <option value="BIMESTRAL">BIWEEKLY</option>
                <option value="QUARTERLY">QUARTERLY</option>
                <option value="SEMIANNUAL">ANNUAL</option>
                <option value="ANNUAL">ANNUAL</option>
            </select>
          </div>

          <div style={styles.buttonContainer}>
             <button onClick={handleSave} style={saving ? styles.saveBtnDisabled : styles.saveBtn} disabled={saving}>
              {saving ? "Processing..." : "Save Subproduct"}
            </button>
          </div>
        </div>

        <div style={{marginTop: '15px'}}>
            <label style={styles.label}>Description</label>
            <input name="description" value={formData.description} onChange={handleChange} style={styles.inputFull} placeholder="Describe commercial purpose..." />
        </div>
      </header>

      <div style={styles.treeContainer}>
        <div style={canGenerateInterest() ? styles.moduleBox : styles.moduleBoxDisabled}>
          <div style={styles.moduleHeader}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
              <input
                 type="checkbox"
                 name="generatesInterest"
                 checked={formData.generatesInterest}
                 onChange={handleChange}
                 disabled={!canGenerateInterest()}
                 style={{ cursor: 'pointer', width: '18px', height: '18px' }}
              />
              <strong>💰 FINANCIAL INTEREST RULES</strong>
            </div>
            {!canGenerateInterest() && <small>Available only for Savings/DPF</small>}
          </div>

          {formData.generatesInterest && (
            <div style={styles.submoduleGrid}>
              <div style={styles.subCard}>
                <div style={styles.subTitle}>Select Interest Strategy</div>
                <div style={{display: 'flex', gap: '10px'}}>
                    <select name="interestGroupId" value={formData.interestGroupId} onChange={handleChange} style={styles.selectFull}>
                        <option value="">-- Choose Strategy --</option>
                        {interestGroups.map(g => <option key={g.id} value={g.id}>{g.name}</option>)}
                    </select>
                    {formData.interestGroupId && (
                        <button onClick={() => setShowModal(true)} style={styles.btnView}>👁️ View Ranges</button>
                    )}
                </div>
              </div>
            </div>
          )}
        </div>
      </div>

      {/* MODAL DE RANGOS - ANCHO AJUSTADO */}
      {showModal && selectedGroupDetails && (
        <div style={styles.modalOverlay}>
          <div style={styles.modalContent}>
            <h3 style={{margin: '0 0 10px 0'}}>📊 {selectedGroupDetails.name}</h3>
            <p style={{fontSize: '0.8rem', color: '#64748b', marginBottom: '15px'}}>Configured rate table for the selected strategy.</p>
            <table style={styles.table}>
              <thead>
                <tr style={styles.tr}>
                  <th style={styles.th}>Min Amount</th>
                  <th style={styles.th}>Max Amount</th>
                  <th style={styles.th}>Rate (%)</th>
                </tr>
              </thead>
              <tbody>
                {selectedGroupDetails.ranges?.map((r, i) => (
                  <tr key={i}>
                    <td style={styles.td}>${Number(r.minAmount).toLocaleString(undefined, {minimumFractionDigits: 2})}</td>
                    <td style={styles.td}>${Number(r.maxAmount).toLocaleString(undefined, {minimumFractionDigits: 2})}</td>
                    <td style={styles.td}>{Number(r.rateValue).toFixed(2)}%</td>
                  </tr>
                ))}
              </tbody>
            </table>
            <button onClick={() => setShowModal(false)} style={styles.btnModalClose}>Close</button>
          </div>
        </div>
      )}
    </div>
  );
};

const styles = {
  container: { padding: '20px', backgroundColor: '#f8fafc', minHeight: '100vh', fontFamily: 'sans-serif' },
  header: { display: 'flex', flexDirection: 'column', gap: '10px', marginBottom: '20px', backgroundColor: 'white', padding: '20px', borderRadius: '10px', border: '1px solid #e2e8f0', boxShadow: '0 1px 3px rgba(0,0,0,0.1)' },
  title: { margin: 0, fontSize: '1.25rem', color: '#1e293b', fontWeight: 'bold' },
  subtitle: { margin: 0, color: '#64748b', fontSize: '0.85rem' },
  formInputs: { display: 'flex', gap: '15px', flexWrap: 'wrap', alignItems: 'flex-end' },
  inputGroup: { display: 'flex', flexDirection: 'column', gap: '5px' },
  label: { fontSize: '0.75rem', fontWeight: 'bold', color: '#475569', textTransform: 'uppercase' },
  required: { color: '#ef4444', marginLeft: '2px' },
  input: { padding: '10px 14px', borderRadius: '6px', border: '1px solid #cbd5e1', width: '340px', outline: 'none', fontSize: '0.9rem' },
  inputTxt: { padding: '10px 14px', borderRadius: '6px', border: '1px solid #cbd5e1', width: '400px', outline: 'none', fontSize: '0.9rem' },
  inputSmall: { padding: '10px 14px', borderRadius: '6px', border: '1px solid #cbd5e1', width: '120px', outline: 'none', fontSize: '0.9rem' },
  inputFull: { padding: '10px 14px', borderRadius: '6px', border: '1px solid #cbd5e1', width: '100%', outline: 'none', fontSize: '0.9rem', boxSizing: 'border-box' },
  buttonContainer: { paddingBottom: '2px' },
  saveBtn: { backgroundColor: '#10b981', color: 'white', border: 'none', padding: '0 25px', borderRadius: '6px', cursor: 'pointer', fontWeight: 'bold', height: '42px' },
  saveBtnDisabled: { backgroundColor: '#94a3b8', color: 'white', border: 'none', padding: '0 25px', borderRadius: '6px', cursor: 'not-allowed', height: '42px' },
  treeContainer: { maxWidth: '1440px', margin: '0 auto' },
  moduleBox: { marginBottom: '20px', backgroundColor: 'white', borderRadius: '10px', border: '1px solid #e2e8f0', overflow: 'hidden' },
  moduleBoxDisabled: { marginBottom: '20px', backgroundColor: '#f1f5f9', borderRadius: '10px', border: '1px solid #e2e8f0', opacity: 0.6 },
  moduleHeader: { backgroundColor: '#334155', color: 'white', padding: '14px 22px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' },
  submoduleGrid: { padding: '20px' },
  subCard: { border: '2px solid #f1f5f9', padding: '18px', borderRadius: '10px', backgroundColor: '#ffffff', maxWidth: '700px' },
  subTitle: { borderBottom: '2px solid #e2e8f0', marginBottom: '12px', paddingBottom: '8px', fontWeight: 'bold', color: '#334155' },
  selectFull: { flex: 1, padding: '10px', borderRadius: '6px', border: '1px solid #cbd5e1' },
  btnView: { padding: '0 15px', backgroundColor: '#6366f1', color: 'white', border: 'none', borderRadius: '6px', cursor: 'pointer' },
  loader: { display: 'flex', justifyContent: 'center', alignItems: 'center', height: '80vh', color: '#64748b' },
  modalOverlay: { position: 'fixed', top: 0, left: 0, right: 0, bottom: 0, backgroundColor: 'rgba(0,0,0,0.5)', display: 'flex', alignItems: 'center', justifyContent: 'center', zIndex: 1000 },
  modalContent: { backgroundColor: 'white', padding: '25px', borderRadius: '12px', width: '550px', boxShadow: '0 20px 25px -5px rgba(0,0,0,0.2)' },
  table: { width: '100%', borderCollapse: 'collapse' },
  th: { textAlign: 'left', padding: '12px', borderBottom: '2px solid #e2e8f0', fontSize: '0.85rem', color: '#475569' },
  td: { padding: '12px', borderBottom: '1px solid #f1f5f9', fontSize: '0.9rem', color: '#1e293b' },
  btnModalClose: { marginTop: '20px', width: '100%', padding: '12px', backgroundColor: '#ef4444', color: 'white', border: 'none', borderRadius: '6px', cursor: 'pointer', fontWeight: 'bold' }
};

export default SubproductCreate;