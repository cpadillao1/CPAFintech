import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Login from './pages/Login';
import ProtectedRoute from './components/ProtectedRoute';
import RoleCreate from './pages/management/roles/RoleCreate';
import RoleEdit from './pages/management/roles/RoleEdit';
import RoleFunctQuery from './pages/management/roles/RoleFunctQuery';
import BranchCreate from './pages/management/branches/BranchCreate';
import BranchQuery from './pages/management/branches/BranchQuery';
import BranchUpdate from './pages/management/branches/BranchUpdate';
import UserCreate from './pages/management/users/UserCreate';
import UserQueryByLogin from './pages/management/users/UserQueryByLogin';
import UserUpdate from './pages/management/users/UserUpdate';
import ProductCreate from './pages/management/products/ProductCreate';
import ProductQuery from './pages/management/products/ProductQuery';
import SubproductQuery from './pages/management/subproducts/SubproductQuery';
import CtrlSystemQuery from './pages/management/control_system/CtrlSystemQuery';
import SubproductCreate from './pages/management/products/SubproductCreate';
import InterestGroupCreate from './pages/management/interest_range/InterestGroupCreate';
import CustomerCreate from './pages/core/customers/CustomerCreate';
import AccountQuery from './pages/core/accounts/AccountQuery';
import AccountCreate from './pages/core/accounts/AccountCreate';
import NcNdCreate from './pages/core/transactions/NcNdCreate';
import TransferCreate from './pages/core/transactions/TransferCreate';
import HoldQuery from './pages/core/accounts/HoldQuery';
import CreateHold from './pages/core/accounts/CreateHold';
import ReleaseHold from './pages/core/accounts/ReleaseHold';
import RestrictionCreate from './pages/core/accounts/RestrictionCreate';
import RestrictionQuery from './pages/core/accounts/RestrictionQuery';
import RestrictionRelease from './pages/core/accounts/RestrictionRelease';
import AccountReport from './pages/core/accounts/AccountReport';
import api from './api/axiosConfig';

const Dashboard = () => {
  const [user, setUser] = useState(() => {
    const saved = localStorage.getItem('user');
    if (!saved) return { name: 'Usuario', access: [], businessDate: '', systemStatus: '' };
    try {
      const parsed = JSON.parse(saved);
      return {
        name: parsed.firstName || parsed.nombre || 'Usuario',
        access: parsed.roles || parsed.permisos || [],
        businessDate: parsed.businessDate,
        systemStatus: parsed.systemStatus
      };
    } catch (e) {
      return { name: 'Usuario', access: [], businessDate: '', systemStatus: '' };
    }
  });

  const [menuDinamico, setMenuDinamico] = useState([]);
  const [loading, setLoading] = useState(true);
  const [currentView, setCurrentView] = useState('grid');

  useEffect(() => {
    const fetchMenu = async () => {
      try {
        const respuesta = await api.get('/api/v1/menu/tree');
        setMenuDinamico(respuesta.data);
      } catch (error) {
        console.error("Error loading the menu:", error);
      } finally {
        setLoading(false);
      }
    };
    fetchMenu();
  }, []);

  const handleLogout = () => {
    localStorage.clear();
    window.location.href = '/login';
  };

  const tieneAcceso = (code) => code && user.access.includes(code);

  const renderView = (ViewComponent) => (
    <div>
      <button onClick={() => setCurrentView('grid')} className="btn-back">⬅ Back to Dashboard</button>
      <ViewComponent />
    </div>
  );

  if (loading) return (
    <div className="loader-container">
      <div className="loader-spinner"></div>
      <p>Loading CPA Fintech...</p>
    </div>
  );

  return (
    <div className="dash-container">
      <nav className="dash-navbar">
        <h2 onClick={() => setCurrentView('grid')} className="dash-logo">CPA Fintech</h2>

        <div className="date-badge">
          <span className="calendar-icon">📅</span>
          <span className="date-label">BUSINESS DATE:</span>
          <span className="date-value">{user.businessDate}</span>
          <div
            className="status-dot"
            style={{
                backgroundColor: user.systemStatus === 'ACTIVE' ? '#22c55e' : '#f59e0b',
                boxShadow: '0 0 5px currentColor'
            }}
          ></div>
        </div>

        <div className="user-section">
          <div className="user-pill">{user.access.length} Permits</div>
          <span>Hello, <strong>{user.name}</strong></span>
          <button onClick={handleLogout} className="btn-logout">Log out</button>
        </div>
      </nav>

      <div className="dash-content">
        {/* Router de Vistas Internas */}
        {currentView === 'crear_rol' && renderView(RoleCreate)}
        {currentView === 'editar_rol' && renderView(RoleEdit)}
        {currentView === 'crear_sucursal' && renderView(BranchCreate)}
        {currentView === 'consultar_sucursal' && renderView(BranchQuery)}
        {currentView === 'actualizar_sucursal' && renderView(BranchUpdate)}
        {currentView === 'crear_usuario' && renderView(UserCreate)}
        {currentView === 'consultar_usuario_login' && renderView(UserQueryByLogin)}
        {currentView === 'actualizar_usuario' && renderView(UserUpdate)}
        {currentView === 'crear_producto' && renderView(ProductCreate)}
        {currentView === 'consultar_producto' && renderView(ProductQuery)}
        {currentView === 'query_subproducts' && renderView(SubproductQuery)}
        {currentView === 'query_control' && renderView(CtrlSystemQuery)}
        {currentView === 'create_subproduct' && renderView(SubproductCreate)}
        {currentView === 'create_interest' && renderView(InterestGroupCreate)}
        {currentView === 'create_customer' && renderView(CustomerCreate)}
        {currentView === 'query_account' && renderView(AccountQuery)}
        {currentView === 'create_account' && renderView(AccountCreate)}
        {currentView === 'create_ncnd' && renderView(NcNdCreate)}
        {currentView === 'create_transfer' && renderView(TransferCreate)}
        {currentView === 'query_hold' && renderView(HoldQuery)}
        {currentView === 'create_hold' && renderView(CreateHold)}
        {currentView === 'release_hold' && renderView(ReleaseHold)}
        {currentView === 'query_rolfunct' && renderView(RoleFunctQuery)}
        {currentView === 'create_restriction' && renderView(RestrictionCreate)}
        {currentView === 'query_restriction' && renderView(RestrictionQuery)}
        {currentView === 'release_restriction' && renderView(RestrictionRelease)}
        {currentView === 'report_account' && renderView(AccountReport)}

        {currentView === 'grid' && (
          <>
            {menuDinamico.length === 0 && <p>No modules are available.</p>}

            {menuDinamico.map((modulo) => (
              tieneAcceso(modulo.code) && (
                <div key={modulo.code} className="dash-section">
                  <h3 className="dash-section-title">{modulo.icon} {modulo.label}</h3>
                  <div className="dash-grid">

                    {modulo.children && modulo.children.map((sub) => (
                      tieneAcceso(sub.code) && (
                        <div key={sub.code} className="dash-card-wrapper">
                          <div className="dash-card">
                            <span className="dash-card-icon">{sub.icon}</span>
                            <span className="dash-card-name">{sub.label}</span>
                          </div>

                          <div className="actions-container">
                            {sub.actions && sub.actions.length > 0 ? (
                              sub.actions.map(action => (
                                tieneAcceso(action.c) && (
                                  <button
                                    key={action.c}
                                    className="btn-action"
                                    onClick={(e) => {
                                      e.stopPropagation();

                                      if (action.c === 'FUNC_CREATE_PERM') setCurrentView('crear_rol');
                                      else if (action.c === 'FUNC_EDIT_PERM') setCurrentView('editar_rol');
                                      else if (action.c === 'BRAN_CREATE') setCurrentView('crear_sucursal');
                                      else if (action.c === 'BRAN_QUERY') setCurrentView('consultar_sucursal');
                                      else if (action.c === 'BRAN_UPDATE') setCurrentView('actualizar_sucursal');
                                      else if (action.c === 'USER_CREATE') setCurrentView('crear_usuario');
                                      else if (action.c === 'USER_QUERY') setCurrentView('consultar_usuario_login');
                                      else if (action.c === 'USER_UPDATE') setCurrentView('actualizar_usuario');
                                      else if (action.c === 'PROD_CREATE') setCurrentView('crear_producto');
                                      else if (action.c === 'PROD_QUERY') setCurrentView('consultar_producto');
                                      else if (action.c === 'SPRO_QUERY') setCurrentView('query_subproducts');
                                      else if (action.c === 'CTRL_QUERY') setCurrentView('query_control');
                                      else if (action.c === 'SPRO_CREATE') setCurrentView('create_subproduct');
                                      else if (action.c === 'SPRO_CREATE_INT') setCurrentView('create_interest');
                                      else if (action.c === 'CUST_CREATE') setCurrentView('create_customer');
                                      else if (action.c === 'ACCO_QUERY') setCurrentView('query_account');
                                      else if (action.c === 'ACCO_CREATE') setCurrentView('create_account');
                                      else if (action.c === 'TRANS_CREATE') setCurrentView('create_ncnd');
                                      else if (action.c === 'TRANS_CREATETRACC') setCurrentView('create_transfer');
                                      else if (action.c === 'ACCO_QUERY_HOLD') setCurrentView('query_hold');
                                      else if (action.c === 'ACCO_CREATE_HOLD') setCurrentView('create_hold');
                                      else if (action.c === 'ACCO_RELEASE_HOLD') setCurrentView('release_hold');
                                      else if (action.c === 'FUNC_QUERY_PERM') setCurrentView('query_rolfunct');
                                      else if (action.c === 'ACCO_CREATE_RESTRICTION') setCurrentView('create_restriction');
                                      else if (action.c === 'ACCO_QUERY_RESTRICTION') setCurrentView('query_restriction');
                                      else if (action.c === 'ACCO_RELEASE_RESTRICTION') setCurrentView('release_restriction');
                                      else if (action.c === 'ACCO_REPORT_ACCOUNT') setCurrentView('report_account');
                                      else {
                                          alert(`Starting: ${action.n}`);
                                        }
                                    }}
                                  >
                                    <span style={{ marginRight: '6px' }}>
                                      {action.i || '⚡'}
                                    </span>
                                    {action.n}
                                  </button>
                                )
                              ))
                            ) : (
                              <span style={{textAlign: 'center', fontSize: '0.7rem', padding: '10px', color: '#94a3b8'}}>
                                No actions available
                              </span>
                            )}
                          </div>
                        </div>
                      )
                    ))}
                  </div>
                </div>
              )
            ))}
          </>
        )}
      </div>
    </div>
  );
};

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/dashboard" element={<ProtectedRoute><Dashboard /></ProtectedRoute>} />
        <Route path="/" element={<Navigate to="/login" replace />} />
      </Routes>
    </Router>
  );
}

export default App;
