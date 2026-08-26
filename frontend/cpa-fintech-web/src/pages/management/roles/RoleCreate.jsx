import React, { useState, useEffect } from 'react';
import api from '../../../api/axiosConfig';
import Swal from 'sweetalert2';

const RoleCreate = () => {
  const [menuCompleto, setMenuCompleto] = useState([]);
  const [selectedPermissions, setSelectedPermissions] = useState([]);
  const [roleName, setRoleName] = useState("");
  const [roleDescription, setRoleDescription] = useState("");
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    const fetchMenu = async () => {
      try {
        setLoading(true);
        const res = await api.get('/api/v1/menu/tree');
        setMenuCompleto(res.data || []);
      } catch (err) {
        console.error("Error loading the menu");
      } finally {
        setLoading(false);
      }
    };
    fetchMenu();
  }, []);

  // --- LÓGICA DE SELECCIÓN JERÁRQUICA ---
  const handleModuleCheck = (e, modulo) => {
    e.stopPropagation();
    const moduloId = modulo.id;
    const subIds = (modulo.children || []).map(s => s.id);
    const actionIds = (modulo.children || []).flatMap(s => (s.actions || []).map(a => a.id));
    const allModuleRelatedIds = [moduloId, ...subIds, ...actionIds];

    if (selectedPermissions.includes(moduloId)) {
      setSelectedPermissions(prev => prev.filter(id => !allModuleRelatedIds.includes(id)));
    } else {
      setSelectedPermissions(prev => [...new Set([...prev, moduloId])]);
    }
  };

  const handleSubmoduleCheck = (e, subId, actions, moduloId) => {
    e.stopPropagation();
    if (!subId) return;
    const actionIds = (actions || []).map(a => a.id).filter(id => id);
    const allRelated = [subId, ...actionIds];
    if (selectedPermissions.includes(subId)) {
      setSelectedPermissions(prev => prev.filter(id => !allRelated.includes(id)));
    } else {
      setSelectedPermissions(prev => [...new Set([...prev, ...allRelated, moduloId])]);
    }
  };

  const handleActionCheck = (e, actionId, subId, moduloId) => {
    e.stopPropagation();
    if (!actionId) return;
    setSelectedPermissions(prev => {
      if (prev.includes(actionId)) {
        return prev.filter(id => id !== actionId);
      } else {
        return [...new Set([...prev, actionId, subId, moduloId])];
      }
    });
  };

  // --- VALIDACIÓN DE INTEGRIDAD Y GUARDADO ---
  const saveNewRole = async () => {
    if (!roleName.trim()) {
      return Swal.fire({
        title: 'Required Field',
        text: 'The role name is required.',
        icon: 'warning',
        confirmButtonColor: '#334155'
      });
    }

    if (!roleDescription.trim()) {
      return Swal.fire({
        title: 'Required Field',
        text: 'You must enter a description for the role.',
        icon: 'warning',
        confirmButtonColor: '#334155'
      });
    }

    if (selectedPermissions.length === 0) {
      return Swal.fire({
        title: 'No items selected',
        text: 'You must select at least one feature to create the profile',
        icon: 'info',
        confirmButtonColor: '#334155'
      });
    }

    let erroresIntegridad = [];

    menuCompleto.forEach(modulo => {
      if (selectedPermissions.includes(modulo.id)) {
        const subsDelModulo = modulo.children || [];
        const tieneSubMarcado = subsDelModulo.some(s => selectedPermissions.includes(s.id));
        if (!tieneSubMarcado) {
          erroresIntegridad.push(`• Module <b>${modulo.label}</b> must contain submodules.`);
        }
      }
    });

    const todosLosSubmodulos = menuCompleto.flatMap(m => m.children || []);
    todosLosSubmodulos.forEach(sub => {
      if (selectedPermissions.includes(sub.id)) {
        const accionesDelSub = sub.actions || [];
        const tieneAccionMarcada = accionesDelSub.some(a => selectedPermissions.includes(a.id));
        if (!tieneAccionMarcada) {
          erroresIntegridad.push(`• Submodule <b>${sub.label}</b> must contain actions.`);
        }
      }
    });

    if (erroresIntegridad.length > 0) {
      return Swal.fire({
        title: 'Data Inconsistency',
        html: `<div style="text-align: left; font-size: 0.9rem;">You cannot save an empty branch:<br><br>${erroresIntegridad.join('<br>')}</div>`,
        icon: 'error',
        confirmButtonColor: '#ef4444'
      });
    }

    setSaving(true);
    try {
      const payload = {
        name: roleName.toUpperCase(),
        description: roleDescription,
        functionalityIds: selectedPermissions
      };

      await api.post('/api/v1/roles/save', payload);

      Swal.fire({
        title: '¡Successful Operation!',
        text: 'The new role has been created with all its hierarchies.',
        icon: 'success',
        confirmButtonColor: '#10b981'
      });

      setRoleName("");
      setRoleDescription("");
      setSelectedPermissions([]);
    } catch (err) {
        const mensajeError = err.response?.data?.message || err.response?.data || err.message;
        Swal.fire({
          title: 'Error processing',
          text: typeof mensajeError === 'object' ? JSON.stringify(mensajeError) : mensajeError,
          icon: 'error',
          confirmButtonColor: '#ef4444'
        });
    } finally {
      setSaving(false);
    }
  };

  if (loading) return <div style={styles.loader}>Loading permission structure...</div>;

  return (
    <div style={styles.container}>
      <header style={styles.header}>
        <div style={{ marginBottom: '10px' }}>
          <h2 style={styles.title}>Profile Settings</h2>
          <p style={styles.subtitle}>Technical hierarchy: Module &gt; Submodule &gt; Action.</p>
        </div>

        <div style={styles.formInputs}>
          <div style={styles.inputGroup}>
            <label style={styles.label}>Rol Name <span style={styles.required}>*</span></label>
            <input
              type="text"
              placeholder="EX: Compliance_Officer"
              style={styles.input}
              value={roleName}
              onChange={(e) => setRoleName(e.target.value)}
            />
          </div>

          <div style={styles.inputGroupLarge}>
            <label style={styles.label}>Description <span style={styles.required}>*</span></label>
            <input
              type="text"
              placeholder="Describe the purpose of this profile"
              style={styles.inputFull}
              value={roleDescription}
              onChange={(e) => setRoleDescription(e.target.value)}
            />
          </div>

          <div style={styles.buttonContainer}>
             <button
              onClick={saveNewRole}
              style={saving ? styles.saveBtnDisabled : styles.saveBtn}
              disabled={saving}
            >
              {saving ? "Processing..." : "Save Profile"}
            </button>
          </div>
        </div>
      </header>

      <div style={styles.treeContainer}>
        <div style={{ marginBottom: '15px', color: '#64748b', fontSize: '0.85rem' }}>
           <span style={styles.required}>*</span> The required fields and the action structure are mandatory.
        </div>
        {menuCompleto.map(modulo => (
          <div key={modulo.id} style={styles.moduleBox}>
            <div
              style={styles.moduleHeader}
              onClick={(e) => handleModuleCheck(e, modulo)}
            >
              <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                <input
                   type="checkbox"
                   checked={selectedPermissions.includes(modulo.id)}
                   onChange={(e) => handleModuleCheck(e, modulo)}
                   style={styles.checkboxMain}
                />
                <strong>{modulo.icon} {modulo.label}</strong>
              </div>
              <small style={{opacity: 0.7}}>{modulo.code}</small>
            </div>

            <div style={styles.submoduleGrid}>
              {modulo.children?.map(sub => (
                <div key={sub.id} style={styles.subCard}>
                  <div style={styles.subTitle}>
                    <input
                      type="checkbox"
                      style={styles.checkbox}
                      onChange={(e) => handleSubmoduleCheck(e, sub.id, sub.actions, modulo.id)}
                      checked={selectedPermissions.includes(sub.id)}
                    />
                    <span>{sub.label}</span>
                  </div>

                  <div style={styles.actionsList}>
                    {sub.actions?.map(action => (
                      <label key={action.id} style={styles.actionItem}>
                        <input
                          type="checkbox"
                          style={styles.checkboxSmall}
                          onChange={(e) => handleActionCheck(e, action.id, sub.id, modulo.id)}
                          checked={selectedPermissions.includes(action.id)}
                        />
                        <span>{action.i} {action.n}</span>
                      </label>
                    ))}
                  </div>
                </div>
              ))}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

const styles = {
  container: { padding: '20px', backgroundColor: '#f8fafc', minHeight: '100vh', fontFamily: 'sans-serif' },
  header: {
    maxWidth: '1200px',
    margin: '0 auto 20px auto',
    display: 'flex',
    flexDirection: 'column',
    gap: '10px',
    backgroundColor: 'white',
    padding: '12px 20px',
    borderRadius: '10px',
    border: '1px solid #e2e8f0',
    boxShadow: '0 1px 3px rgba(0,0,0,0.1)'
  },
  title: { margin: 0, fontSize: '1.25rem', color: '#1e293b', fontWeight: 'bold' },
  subtitle: { margin: 0, color: '#64748b', fontSize: '0.85rem' },
  formInputs: { display: 'flex', gap: '20px', flexWrap: 'wrap', alignItems: 'flex-end' },
  inputGroup: { display: 'flex', flexDirection: 'column', gap: '5px' },
  inputGroupLarge: { display: 'flex', flexDirection: 'column', gap: '5px', flex: 1, minWidth: '300px' },
  label: { fontSize: '0.75rem', fontWeight: 'bold', color: '#475569', textTransform: 'uppercase' },
  required: { color: '#ef4444', marginLeft: '2px' },
  input: { padding: '10px 14px', borderRadius: '6px', border: '1px solid #cbd5e1', width: '220px', outline: 'none', fontSize: '0.9rem' },
  inputFull: { padding: '10px 14px', borderRadius: '6px', border: '1px solid #cbd5e1', width: '100%', outline: 'none', fontSize: '0.9rem' },
  buttonContainer: { paddingBottom: '2px' },
  // CAMBIADO AL COLOR DE CABECERA (#334155)
  saveBtn: { backgroundColor: '#334155', color: 'white', border: 'none', padding: '10px 28px', borderRadius: '6px', cursor: 'pointer', fontWeight: 'bold', transition: 'background 0.2s', height: '42px' },
  saveBtnDisabled: { backgroundColor: '#94a3b8', color: 'white', border: 'none', padding: '10px 28px', borderRadius: '6px', cursor: 'not-allowed', height: '42px' },
  treeContainer: {
    maxWidth: '1200px',
    margin: '0 auto'
  },
  moduleBox: { marginBottom: '20px', backgroundColor: 'white', borderRadius: '10px', border: '1px solid #e2e8f0', overflow: 'hidden', boxShadow: '0 4px 6px -1px rgba(0,0,0,0.05)' },
  moduleHeader: { backgroundColor: '#334155', color: 'white', padding: '14px 22px', display: 'flex', justifyContent: 'space-between', alignItems: 'center', cursor: 'pointer' },
  submoduleGrid: { padding: '20px', display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))', gap: '20px' },
  subCard: { border: '2px solid #f1f5f9', padding: '15px', borderRadius: '10px', backgroundColor: '#ffffff' },
  subTitle: { borderBottom: '2px solid #e2e8f0', marginBottom: '10px', paddingBottom: '8px', display: 'flex', gap: '12px', fontWeight: 'bold', color: '#334155' },
  actionsList: { display: 'flex', flexDirection: 'column', gap: '8px' },
  actionItem: { fontSize: '0.82rem', display: 'flex', alignItems: 'center', gap: '12px', cursor: 'pointer', color: '#475569' },
  checkboxMain: { cursor: 'pointer', width: '18px', height: '18px', accentColor: '#475569' },
  checkbox: { width: '19px', height: '19px', cursor: 'pointer', accentColor: '#475569' },
  checkboxSmall: { width: '17px', height: '17px', cursor: 'pointer', accentColor: '#475569' },
  loader: { display: 'flex', justifyContent: 'center', alignItems: 'center', height: '80vh', fontSize: '1.1rem', color: '#64748b' }
};

export default RoleCreate;
