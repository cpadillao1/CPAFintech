import React, { useState, useEffect } from 'react';
import api from '../../../api/axiosConfig';

const RoleCreate = () => {
  const [menuCompleto, setMenuCompleto] = useState([]);
  const [selectedPermissions, setSelectedPermissions] = useState([]);
  const [roleName, setRoleName] = useState(""); // Input de texto para el nuevo nombre
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    const fetchMenu = async () => {
      try {
        setLoading(true);
        const res = await api.get('/api/v1/menu/tree');
        setMenuCompleto(res.data || []);
      } catch (err) {
        console.error("Error cargando el menú");
      } finally {
        setLoading(false);
      }
    };
    fetchMenu();
  }, []);

  // Lógica de marcado (Ahora sin bloqueos de "selectedRole")
  const handleSubmoduleCheck = (subId, actions) => {
    const sId = String(subId);
    const actionIds = (actions || []).map(a => String(a.id)).filter(id => id);
    const allRelatedIds = [sId, ...actionIds];

    if (selectedPermissions.includes(sId)) {
      setSelectedPermissions(prev => prev.filter(id => !allRelatedIds.includes(id)));
    } else {
      setSelectedPermissions(prev => [...new Set([...prev, ...allRelatedIds])]);
    }
  };

  const handleActionCheck = (actionId) => {
    const aId = String(actionId);
    setSelectedPermissions(prev =>
      prev.includes(aId) ? prev.filter(id => id !== aId) : [...prev, aId]
    );
  };

  const saveNewRole = async () => {
    if (!roleName.trim()) return alert("Escribe un nombre para el nuevo rol.");
    if (selectedPermissions.length === 0) return alert("Selecciona al menos un permiso.");

    setSaving(true);
    try {
      // Usamos tu RoleRequestDTO: { name, functionalityIds }
      const payload = {
        name: roleName,
        functionalityIds: selectedPermissions
      };
      await api.post('/api/v1/roles/save', payload);
      alert("✅ ¡Nuevo rol creado exitosamente!");
      // Limpiamos tras guardar
      setRoleName("");
      setSelectedPermissions([]);
    } catch (err) {
      alert("❌ Error al crear rol: " + (err.response?.data || "Error de red"));
    } finally {
      setSaving(false);
    }
  };

  if (loading) return <div style={styles.loader}>Cargando Mapa de Funcionalidades...</div>;

  return (
    <div style={styles.container}>
      <header style={styles.header}>
        <div style={{ flex: 1 }}>
          <h2 style={styles.title}>Crear Nuevo Rol</h2>
          <p style={styles.subtitle}>Asigna un nombre y define sus accesos iniciales</p>
        </div>

        <div style={styles.actionsHeader}>
          <input
            type="text"
            placeholder="Nombre del Rol (Ej: ANALISTA)"
            style={styles.input}
            value={roleName}
            onChange={(e) => setRoleName(e.target.value.toUpperCase())}
          />

          <button
            onClick={saveNewRole}
            style={saving ? styles.saveBtnDisabled : styles.saveBtn}
            disabled={saving}
          >
            {saving ? "Guardando..." : "Crear Rol"}
          </button>
        </div>
      </header>

      <div style={styles.treeContainer}>
        {menuCompleto.map(modulo => (
          <div key={modulo.id} style={styles.moduleBox}>
            <div style={styles.moduleHeader}>
              <strong>{modulo.icon} {modulo.label}</strong>
              <span style={styles.badge}>Módulo</span>
            </div>

            <div style={styles.submoduleGrid}>
              {modulo.children?.map(sub => (
                <div key={sub.id} style={styles.subCard}>
                  <div style={styles.subTitle}>
                    <input
                      type="checkbox"
                      style={styles.checkbox}
                      onChange={() => handleSubmoduleCheck(sub.id, sub.actions)}
                      checked={selectedPermissions.includes(String(sub.id))}
                    />
                    <span>{sub.label}</span>
                  </div>

                  <div style={styles.actionsList}>
                    {sub.actions?.map(action => (
                      <label key={action.id} style={styles.actionItem}>
                        <input
                          type="checkbox"
                          style={styles.checkboxSmall}
                          onChange={() => handleActionCheck(action.id)}
                          checked={selectedPermissions.includes(String(action.id))}
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

// Estilos rápidos para el input
const styles = {
  container: { padding: '20px', backgroundColor: '#f8fafc', minHeight: '100vh' },
  header: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px', backgroundColor: 'white', padding: '15px 25px', borderRadius: '10px', border: '1px solid #e2e8f0' },
  title: { margin: 0, fontSize: '1.2rem', color: '#0f172a' },
  subtitle: { margin: 0, color: '#64748b', fontSize: '0.8rem' },
  actionsHeader: { display: 'flex', gap: '12px' },
  input: { padding: '10px', borderRadius: '6px', border: '1px solid #cbd5e1', minWidth: '250px' },
  saveBtn: { backgroundColor: '#10b981', color: 'white', border: 'none', padding: '10px 24px', borderRadius: '6px', cursor: 'pointer', fontWeight: 'bold' },
  saveBtnDisabled: { backgroundColor: '#94a3b8', color: 'white', border: 'none', padding: '10px 24px', borderRadius: '6px' },
  treeContainer: { maxWidth: '1400px', margin: '0 auto' },
  moduleBox: { marginBottom: '20px', backgroundColor: 'white', borderRadius: '10px', border: '1px solid #e2e8f0', overflow: 'hidden' },
  moduleHeader: { backgroundColor: '#334155', color: 'white', padding: '12px 20px', display: 'flex', justifyContent: 'space-between' },
  badge: { fontSize: '0.65rem', border: '1px solid #475569', padding: '2px 6px', borderRadius: '4px' },
  submoduleGrid: { padding: '20px', display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))', gap: '20px' },
  subCard: { border: '1px solid #f1f5f9', padding: '15px', borderRadius: '8px', backgroundColor: '#fff' },
  subTitle: { borderBottom: '1px solid #f1f5f9', marginBottom: '10px', paddingBottom: '6px', display: 'flex', gap: '10px', fontWeight: 'bold' },
  actionsList: { display: 'flex', flexDirection: 'column', gap: '8px' },
  actionItem: { fontSize: '0.8rem', display: 'flex', alignItems: 'center', gap: '10px', cursor: 'pointer' },
  checkbox: { width: '17px', height: '17px' },
  checkboxSmall: { width: '15px', height: '15px' },
  loader: { display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' }
};

export default RoleCreate;
