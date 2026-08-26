import React, { useState, useEffect, useCallback } from 'react';
import api from '../../../api/axiosConfig';
import Swal from 'sweetalert2';
import { Search, FileText, TrendingUp, X, User, Calendar, CreditCard, Clock, ShieldCheck, HardDrive, ArrowDownLeft, ArrowUpRight } from 'lucide-react';

const AccountQuery = () => {
  const [products, setProducts] = useState([]);
  const [subproducts, setSubproducts] = useState([]);
  const [filters, setFilters] = useState({ productId: '', subproductId: '', accountNumber: '' });
  const [results, setResults] = useState([]);
  const [loading, setLoading] = useState(false);

  // Modals state
  const [showModal, setShowModal] = useState(false);
  const [showSnapModal, setShowSnapModal] = useState(false);
  const [selectedAccount, setSelectedAccount] = useState(null);

  // Data for Statement & Snapshots
  const [statement, setStatement] = useState([]);
  const [snapshots, setSnapshots] = useState([]);
  const [stmtLoading, setStmtLoading] = useState(false);
  const [snapLoading, setSnapLoading] = useState(false);

  // Pagination states
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [snapPage, setSnapPage] = useState(0);
  const [snapTotalPages, setSnapTotalPages] = useState(0);
  const [snapTotalElements, setSnapTotalElements] = useState(0);

  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');

  // Initial Data Load
  useEffect(() => {
    api.get('/api/v1/products')
      .then(res => setProducts(res.data))
      .catch(err => console.error("Error loading products", err));
  }, []);

  // --- NUEVA LÓGICA DE LIMPIEZA ---
  // Si el usuario borra el número de cuenta o cambia filtros, limpiamos los resultados previos
  useEffect(() => {
    if (!filters.productId || !filters.subproductId || !filters.accountNumber.trim()) {
      setResults([]);
    }
  }, [filters.productId, filters.subproductId, filters.accountNumber]);

  useEffect(() => {
    if (!filters.productId) { setSubproducts([]); return; }
    api.get(`/api/v1/management/subproducts/product/${filters.productId}`)
      .then(res => setSubproducts(res.data))
      .catch(err => console.error("Error loading subproducts", err));
    setFilters(prev => ({ ...prev, subproductId: '' }));
  }, [filters.productId]);

  const setupDates = () => {
    const savedUser = localStorage.getItem('user');
    let businessDate = new Date().toISOString().split('T')[0];
    if (savedUser) {
      try {
        const parsed = JSON.parse(savedUser);
        const rawDate = parsed.businessDate;
        if (rawDate && rawDate.includes('/')) {
          const [day, month, year] = rawDate.split('/');
          businessDate = `${year}-${month}-${day}`;
        } else { businessDate = rawDate; }
      } catch (e) { console.error("Error parsing businessDate", e); }
    }
    setStartDate(businessDate);
    setEndDate(businessDate);
    return businessDate;
  };

  const handleSearch = async (e) => {
    if(e) e.preventDefault();

    if (!filters.productId) {
      Swal.fire({ icon: 'warning', title: 'Missing Data', text: 'Please select a Product.', confirmButtonColor: '#334155' });
      return;
    }
    if (!filters.subproductId) {
      Swal.fire({ icon: 'warning', title: 'Missing Data', text: 'Please select a Subproduct.', confirmButtonColor: '#334155' });
      return;
    }
    if (!filters.accountNumber.trim()) {
      Swal.fire({ icon: 'warning', title: 'Missing Data', text: 'Please enter an Account Number.', confirmButtonColor: '#334155' });
      return;
    }

    setLoading(true);
    try {
      const params = {
        productId: Number(filters.productId),
        subproductId: Number(filters.subproductId),
        accountNumber: filters.accountNumber.trim()
      };
      const response = await api.get('/api/v1/accounts/search', { params });
      setResults(response.data);
      if (!response.data || response.data.length === 0) {
        Swal.fire({ icon: 'info', title: 'No results', text: 'No accounts found.', timer: 2000 });
      }
    } catch (error) {
      Swal.fire({ icon: 'error', title: 'Search failed', text: error.response?.data?.message || "Error" });
    } finally {
      setLoading(false);
    }
  };

  const fetchStatement = useCallback(async (page = 0) => {
    const formatDateForBE = (dateStr) => {
      if(!dateStr) return "";
      const [year, month, day] = dateStr.split('-');
      return `${day}/${month}/${year}`;
    };
    if (!selectedAccount) return;
    setStmtLoading(true);
    try {
      const response = await api.get(`/api/v1/accounts/${selectedAccount.id}/statement`, {
        params: { startDate: formatDateForBE(startDate), endDate: formatDateForBE(endDate), page, size: 10 }
      });
      setStatement(response.data.content);
      setTotalPages(response.data.totalPages);
      setTotalElements(response.data.totalElements);
      setCurrentPage(response.data.number);
    } catch (error) {
        setStatement([]);
        Swal.fire({ icon: 'error', title: 'Inquiry Error', text: "Could not fetch statement." });
    } finally { setStmtLoading(false); }
  }, [selectedAccount, startDate, endDate]);

  const fetchSnapshots = useCallback(async (page = 0) => {
    if (!selectedAccount) return;
    setSnapLoading(true);
    try {
      const response = await api.get(`/api/v1/accounts/snapshots/${selectedAccount.accountNumber}`, {
        params: { startDate, endDate, page, size: 10 }
      });
      setSnapshots(response.data.content);
      setSnapTotalPages(response.data.totalPages);
      setSnapTotalElements(response.data.totalElements);
      setSnapPage(response.data.number);
    } catch (error) {
      setSnapshots([]);
      Swal.fire({ icon: 'error', title: 'Inquiry Error', text: "History not available." });
    } finally { setSnapLoading(false); }
  }, [selectedAccount, startDate, endDate]);

  const handleOpenStatement = (acc) => {
    setupDates();
    setStatement([]);
    setSelectedAccount(acc);
    setShowModal(true);
  };

  const handleOpenSnapshots = (acc) => {
    setupDates();
    setSnapshots([]);
    setSelectedAccount(acc);
    setShowSnapModal(true);
  };

  const fCur = (val) => new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(val || 0);
  const fNum = (val, dec = 2) => (val || 0).toLocaleString('en-US', { minimumFractionDigits: dec, maximumFractionDigits: dec });

  const getHolderBadgeStyle = (type) => {
    const t = type?.toUpperCase();
    if (t === 'TITULAR' || t === 'PRINCIPAL' || t === 'OWNER') return { background: '#eff6ff', color: '#1e40af', border: '1px solid #bfdbfe' };
    if (t === 'MANCOMUNADO' || t === 'JOINT') return { background: '#f5f3ff', color: '#5b21b6', border: '1px solid #ddd6fe' };
    if (t === 'SIGNATORY' || t === 'FIRMANTE') return { background: '#fffbeb', color: '#b45309', border: '1px solid #fde68a' };
    return { background: '#f8fafc', color: '#475569', border: '1px solid #e2e8f0' };
  };

  return (
    <div className="ig-container">
      <h3 className="ig-card-title">🔍 Account Inquiry</h3>

      <form onSubmit={handleSearch} className="ig-card search-inline" style={{ marginBottom: '25px', padding: '15px 20px' }}>
        <div className="ig-grid" style={{ gridTemplateColumns: '1fr 1fr 1fr auto', alignItems: 'end', gap: '15px' }}>
          <div className="ig-field" style={{ marginBottom: 0 }}>
            <label className="ig-label">Product <span style={{color: '#ef4444'}}>*</span></label>
            <select className="ig-select" value={filters.productId} onChange={(e) => setFilters({...filters, productId: e.target.value})}>
              <option value="">Select...</option>
              {products.map(p => <option key={p.id} value={p.id}>{p.name}</option>)}
            </select>
          </div>
          <div className="ig-field" style={{ marginBottom: 0 }}>
            <label className="ig-label">Subproduct <span style={{color: '#ef4444'}}>*</span></label>
            <select className="ig-select" value={filters.subproductId} disabled={!filters.productId} onChange={(e) => setFilters({...filters, subproductId: e.target.value})}>
              <option value="">Select...</option>
              {subproducts.map(s => <option key={s.id} value={s.id}>{s.name}</option>)}
            </select>
          </div>
          <div className="ig-field" style={{ marginBottom: 0 }}>
            <label className="ig-label">Account Number <span style={{color: '#ef4444'}}>*</span></label>
            <input className="ig-input" type="text" value={filters.accountNumber} onChange={(e) => setFilters({...filters, accountNumber: e.target.value})} placeholder="Ex: 1000000001" />
          </div>
          <button type="submit" className="ig-btn-primary" style={{ height: '42px', backgroundColor: '#334155', display: 'flex', alignItems: 'center', justifyContent: 'center', textAlign: 'center', minWidth: '120px' }} disabled={loading}>
            {loading ? '...' : <><Search size={16} style={{marginRight: '8px'}} /> Search</>}
          </button>
        </div>
      </form>

      <div className="results-list">
        {results.map(acc => (
          <div key={acc.id} className="account-card">
            <div className="card-header">
              <div>
                <span className="account-main-number">{acc.accountNumber}</span>
                <span className="product-badge">{acc.productName}</span>
                <span className="badge-role" style={{background: '#3b82f6', color: 'white'}}>{acc.subproductName}</span>
              </div>
              <span className={`status-pill ${acc.status?.toLowerCase()}`}>{acc.status}</span>
            </div>

            <div className="card-body-grid">
              {/* SECCIÓN 1: GENERAL */}
              <div className="info-section">
                <h4 className="luxury-title"><Calendar size={15} /> General Information</h4>
                <div className="data-row-luxury">
                    <Calendar size={12} className="text-blue" />
                    <span>Opening Date:</span>
                    <strong className="val-opening">{acc.openingDate}</strong>
                </div>
                <div className="data-row-luxury">
                    <User size={12} className="text-slate" />
                    <span>Created By:</span> <strong className="val-created">{acc.createdBy}</strong>
                </div>
                <div className="data-row-luxury">
                    <User size={12} className="text-amber" />
                    <span>Last Processed:</span>
                    <strong className="val-processed">{acc.lastProcessedDate}</strong>
                </div>

                <div className="holders-box" style={{marginTop: '15px'}}>
                  <label className="ig-label label-holders-compact"><ShieldCheck size={14}/> Owners / Holders</label>
                  {acc.holders?.map(h => (
                    <div key={h.customerId} className="holder-item-luxury">
                      <span className="holder-name"><User size={11} /> {h.fullName}</span>
                      <span className="badge-role" style={getHolderBadgeStyle(h.ownershipType)}>
                        {h.ownershipType}
                      </span>
                    </div>
                  ))}
                </div>
              </div>

              {/* SECCIÓN 2: BALANCES */}
              <div className="info-section highlight luxury-border">
                <h4 className="luxury-title"><CreditCard size={15} /> Financial Balances</h4>
                <div className="main-balance-box">
                  <small className="ig-label">Available Balance</small>
                  <div className="available-amount">{fCur(acc.availableBalance)}</div>
                </div>

                <div className="balance-item-luxury today">
                    <span className="lbl">Balance Today:</span>
                    <span className="val">{fCur(acc.balanceToday)}</span>
                </div>
                <div className="balance-item-luxury yesterday">
                    <span className="lbl">Balance Yesterday:</span>
                    <span className="val">{fCur(acc.balanceYesterday)}</span>
                </div>
                <div className="balance-item-luxury held">
                    <span className="lbl">Hold Amount:</span>
                    <span className="val">{fCur(acc.amountHold)}</span>
                </div>
                <div className="balance-item-luxury interest">
                    <span className="lbl">Accrued Int.:</span>
                    <span className="val">{fCur(acc.accruedInterestMonth)}</span>
                </div>

                <button onClick={() => handleOpenStatement(acc)} className="ig-btn-secondary luxury-btn-compact">
                  <FileText size={14} /> View Movements
                </button>
              </div>

              {/* SECCIÓN 3: ACTIVITY */}
              <div className="info-section">
                <h4 className="luxury-title"><HardDrive size={15} /> Activity Summary</h4>
                <div className="activity-grid-luxury">
                   <div className="act-box green">
                        <small>NC Today</small>
                        <p>{fCur(acc.amountNcToday)}</p>
                   </div>
                   <div className="act-box red">
                        <small>ND Today</small>
                        <p>{fCur(acc.amountNdToday)}</p>
                   </div>
                   <div className="act-box green dimmed">
                        <small>NC Yesterday</small>
                        <p>{fCur(acc.amountNcYesterday)}</p>
                   </div>
                   <div className="act-box red dimmed">
                        <small>ND Yesterday</small>
                        <p>{fCur(acc.amountNdYesterday)}</p>
                   </div>
                </div>
                <div className="date-footer-luxury">
                    <div className="f-row-dazzle nc">
                        <div className="lbl-box"><ArrowDownLeft size={12}/> <span>Last NC:</span></div>
                        <strong className="val-dazzle">{acc.lastDateNc || 'N/A'}</strong>
                    </div>
                    <div className="f-row-dazzle nd">
                        <div className="lbl-box"><ArrowUpRight size={12}/> <span>Last ND:</span></div>
                        <strong className="val-dazzle">{acc.lastDateNd || 'N/A'}</strong>
                    </div>
                </div>
                <button onClick={() => handleOpenSnapshots(acc)} className="luxury-btn-compact"
                    style={{ backgroundColor: 'white', border: '2px solid #0ea5e9', color: '#0ea5e9', marginTop: '40px' }}>
                  <TrendingUp size={14} /> Daily Interests
                </button>
              </div>
            </div>
          </div>
        ))}
      </div>

      {/* --- MODALES Y ESTILOS SE MANTIENEN IGUAL --- */}
      {showModal && (
        <div className="ig-modal-overlay">
          <div className="ig-card" style={{ maxWidth: '1000px', width: '95%', padding: '25px', position: 'relative' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '20px' }}>
              <h3 className="ig-card-title">Statement: {selectedAccount?.accountNumber}</h3>
              <button onClick={() => setShowModal(false)} className="btn-del" style={{width:'30px', height:'30px', borderRadius:'50%'}}><X size={18}/></button>
            </div>
            <div className="ig-range-form" style={{ padding: '15px', marginBottom: '20px', display:'grid', gridTemplateColumns: '1fr 1fr auto', gap:'10px' }}>
              <div className="ig-field" style={{marginBottom:0}}><label className="ig-label">From</label><input className="ig-input" type="date" value={startDate} onChange={(e) => setStartDate(e.target.value)} /></div>
              <div className="ig-field" style={{marginBottom:0}}><label className="ig-label">To</label><input className="ig-input" type="date" value={endDate} onChange={(e) => setEndDate(e.target.value)} /></div>
              <button onClick={() => fetchStatement(0)} className="modal-query-btn" disabled={stmtLoading}>{stmtLoading ? '...' : 'Query'}</button>
            </div>
            <div className="ig-table-wrapper" style={{ maxHeight: '400px', overflowY: 'auto' }}>
              <table className="ig-table">
                <thead>
                  <tr style={{background: '#f8fafc'}}><th className="ig-th">Date</th><th className="ig-th">Type</th><th className="ig-th" style={{textAlign: 'right'}}>Amount</th><th className="ig-th" style={{textAlign: 'right'}}>Balance</th><th className="ig-th">Reference</th></tr>
                </thead>
                <tbody>
                  {statement.map((mov) => (
                    <tr key={mov.id}>
                      <td className="ig-td">{mov.businessDate}</td>
                      <td className="ig-td"><span className={`ig-rate-tag`} style={{background: mov.typeTran === 'CR' ? '#dcfce7' : '#fee2e2', color: mov.typeTran === 'CR' ? '#16a34a' : '#ef4444'}}>{mov.typeTran}</span></td>
                      <td className="ig-td" style={{textAlign: 'right', fontWeight: 'bold'}}>{fCur(mov.amount)}</td>
                      <td className="ig-td" style={{textAlign: 'right'}}>{fCur(mov.newBalance)}</td>
                      <td className="ig-td" style={{fontSize: '0.75rem'}}>{mov.reference}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
            <div style={{display:'flex', justifyContent:'space-between', marginTop:'15px', alignItems:'center'}}>
               <small className="ig-label">Records: {totalElements}</small>
               <div style={{display:'flex', gap:'5px'}}>
                  <button className="btn-action" style={{width:'80px'}} disabled={currentPage === 0} onClick={() => fetchStatement(currentPage - 1)}>Prev</button>
                  <span className="ig-badge">{currentPage + 1} / {totalPages}</span>
                  <button className="btn-action" style={{width:'80px'}} disabled={currentPage + 1 >= totalPages} onClick={() => fetchStatement(currentPage + 1)}>Next</button>
               </div>
            </div>
          </div>
        </div>
      )}

      {showSnapModal && (
        <div className="ig-modal-overlay">
          <div className="ig-card" style={{ maxWidth: '1200px', width: '98%', padding: '25px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '20px' }}>
              <h3 className="ig-card-title">Daily Interest History: {selectedAccount?.accountNumber}</h3>
              <button onClick={() => setShowSnapModal(false)} className="btn-del" style={{width:'30px', height:'30px', borderRadius:'50%'}}><X size={18}/></button>
            </div>
            <div className="ig-range-form" style={{ padding: '15px', display:'grid', gridTemplateColumns: '1fr 1fr auto', gap:'10px' }}>
              <div className="ig-field" style={{marginBottom:0}}><label className="ig-label">From</label><input className="ig-input" type="date" value={startDate} onChange={(e) => setStartDate(e.target.value)} /></div>
              <div className="ig-field" style={{marginBottom:0}}><label className="ig-label">To</label><input className="ig-input" type="date" value={endDate} onChange={(e) => setEndDate(e.target.value)} /></div>
              <button onClick={() => fetchSnapshots(0)} className="modal-query-btn" disabled={snapLoading}>{snapLoading ? '...' : 'Query'}</button>
            </div>
            <div className="ig-table-wrapper" style={{ maxHeight: '400px', overflowY: 'auto', marginTop:'15px' }}>
              <table className="ig-table">
                <thead>
                  <tr style={{background: '#f8fafc'}}>
                      <th className="ig-th">Date</th>
                      <th className="ig-th" style={{textAlign: 'right'}}>Rate %</th>
                      <th className="ig-th" style={{textAlign: 'right'}}>Interest Day</th>
                      <th className="ig-th" style={{textAlign: 'right'}}>Remainder</th>
                      <th className="ig-th" style={{textAlign: 'right'}}>Gross Interest</th>
                      <th className="ig-th" style={{textAlign: 'right'}}>Accrued</th>
                      <th className="ig-th" style={{textAlign: 'right'}}>Balance Today</th>
                      <th className="ig-th" style={{textAlign: 'right'}}>NC Today</th>
                  </tr>
                </thead>
                <tbody>
                  {snapshots.map((s) => (
                    <tr key={s.id}>
                      <td className="ig-td" style={{fontWeight:'bold'}}>{s.snapshotDate}</td>
                      <td className="ig-td" style={{textAlign: 'right'}}>{(s.appliedRate * 100).toFixed(2)}%</td>
                      <td className="ig-td" style={{textAlign: 'right', color: '#0369a1'}}>{fNum(s.interestDay, 10)}</td>
                      <td className="ig-td" style={{textAlign: 'right'}}>{fNum(s.remainderAfter, 10)}</td>
                      <td className="ig-td" style={{textAlign: 'right'}}>{fNum(s.grossInterest, 10)}</td>
                      <td className="ig-td" style={{textAlign: 'right'}}>{fNum(s.accruedMonthToDate, 2)}</td>
                      <td className="ig-td" style={{textAlign: 'right', fontWeight:'bold'}}>{s.balanceToday}</td>
                      <td className="ig-td" style={{textAlign: 'right', color:'#10b981'}}>{s.totalNcDay}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
            <div style={{display:'flex', justifyContent:'flex-end', marginTop:'15px'}}>
               <div style={{display:'flex', gap:'5px'}}>
                  <button className="btn-action" style={{width:'80px'}} disabled={snapPage === 0} onClick={() => fetchSnapshots(snapPage - 1)}>Prev</button>
                  <span className="ig-badge">{snapPage + 1} / {snapTotalPages}</span>
                  <button className="btn-action" style={{width:'80px'}} disabled={snapPage + 1 >= snapTotalPages} onClick={() => fetchSnapshots(snapPage + 1)}>Next</button>
               </div>
            </div>
          </div>
        </div>
      )}

      <style>{`
        /* ... Todos los estilos se mantienen igual que la versión anterior ... */
        .ig-modal-overlay { position: fixed; top:0; left:0; width:100%; height:100%; background: rgba(15, 23, 42, 0.7); display:flex; justify-content:center; align-items:center; z-index:2000; backdrop-filter: blur(4px); }
        .account-card { border: 1px solid #e2e8f0; border-radius: 16px; overflow: hidden; background: white; margin-bottom: 25px; box-shadow: 0 10px 15px -3px rgba(0,0,0,0.1); border-top: 4px solid #3b82f6; }
        .card-header { background: #0f172a; color: white; padding: 18px 25px; display: flex; justify-content: space-between; align-items: center; }
        .account-main-number { font-size: 1.4rem; font-weight: 800; color: #60a5fa; }
        .card-body-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 20px; padding: 25px; align-items: stretch; }
        .info-section { display: flex; flex-direction: column; border: 1px solid #f1f5f9; padding: 15px; border-radius: 12px; background: #fff; height: 100%; }
        .luxury-btn-compact {
          height: 34px; padding: 0 12px; font-size: 0.8rem; font-weight: 700;
          display: flex; align-items: center; justify-content: center; gap: 8px; border-radius: 8px; cursor: pointer; transition: all 0.2s ease; }

        .luxury-btn-compact:hover { opacity: 0.8; transform: translateY(-1px); }
        .luxury-title { display: flex; align-items: center; gap: 10px; background: #f1f5f9; padding: 10px 15px; border-radius: 8px; border-left: 4px solid #3b82f6; color: #1e293b !important; font-weight: 800 !important; font-size: 0.85rem !important; margin-bottom: 15px !important; }
        .modal-query-btn { height: 42px; margin-top: 20px; width: 120px; background-color: #334155; color: white; border: none; border-radius: 8px; font-weight: 600; cursor: pointer; display: flex; align-items: center; justify-content: center; text-align: center; transition: background 0.2s; }
        .modal-query-btn:hover { background-color: #1e293b; }
        .modal-query-btn:disabled { opacity: 0.6; cursor: not-allowed; }
        .label-holders-compact { display: flex; align-items: center; gap: 5px; color: #475569; font-size: 0.75rem !important; font-weight: 700; text-transform: uppercase; letter-spacing: 0.5px; margin-bottom: 8px; }
        .data-row-luxury { display: flex; align-items: center; gap: 10px; padding: 8px 0; border-bottom: 1px solid #f8fafc; font-size: 0.9rem; }
        .data-row-luxury span { color: #64748b; min-width: 110px; }
        .val-opening { color: #2563eb; background: #eff6ff; padding: 2px 8px; border-radius: 4px; font-weight: 600; }
        .val-processed { color: #d97706; background: #fffbeb; padding: 2px 8px; border-radius: 4px; font-weight: 600; }
        .val-created { color: #334155; background: #f1f5f9; padding: 2px 8px; border-radius: 4px; border: 1px solid #e2e8f0; font-weight: 600; }
        .holder-item-luxury { display: flex; justify-content: space-between; padding: 6px 10px; background: #f8fafc; border-radius: 8px; margin-bottom: 6px; border: 1px solid #f1f5f9; align-items: center; }
        .holder-name { font-weight: 600; font-size: 0.7rem; color: #334155; display: flex; align-items: center; gap: 6px; }
        .available-amount { font-size: 1.8rem; font-weight: 900; color: #0f172a; letter-spacing: -1px; margin-bottom: 10px; }
        .balance-item-luxury { display: flex; justify-content: space-between; padding: 8px 12px; border-radius: 10px; margin-bottom: 8px; font-size: 0.85rem; }
        .balance-item-luxury.today { background: #f0f9ff; border: 1px solid #bae6fd; color: #0369a1; }
        .balance-item-luxury.yesterday { background: #fdfdfd; border: 1px solid #f1f5f9; color: #64748b; }
        .balance-item-luxury.held { background: #fff1f2; border: 1px solid #fecdd3; color: #be123c; }
        .balance-item-luxury.interest { background: #f0fdf4; border: 1px solid #bbf7d0; color: #15803d; }
        .activity-grid-luxury { display: grid; grid-template-columns: 1fr 1fr; gap: 8px; }
        .act-box { padding: 10px; border-radius: 12px; border-top: 3px solid #cbd5e1; }
        .act-box.green { background: #f0fdf4; border-top-color: #22c55e; color: #166534; }
        .act-box.red { background: #fef2f2; border-top-color: #ef4444; color: #991b1b; }
        .act-box.green.dimmed { background: #fafdfb; border-top-color: #d1fae5; color: #a1a1aa; }
        .act-box.red.dimmed { background: #fffcfc; border-top-color: #fee2e2; color: #a1a1aa; }
        .act-box.dimmed p { color: #71717a; font-weight: 500; }
        .date-footer-luxury { margin-top: 15px; padding: 8px; background: #f8fafc; border-radius: 12px; display: flex; flex-direction: column; gap: 4px; border: 1px solid #f1f5f9; }
        .f-row-dazzle { display: flex; justify-content: space-between; align-items: center; padding: 6px 10px; border-radius: 8px; }
        .f-row-dazzle.nc { background: #f0fdf4 !important; color: #166534; }
        .f-row-dazzle.nd { background: #fef2f2 !important; color: #991b1b; }
        .lbl-box { display: flex; align-items: center; gap: 6px; font-size: 0.75rem; font-weight: 600; text-transform: uppercase; }
        .val-dazzle { font-size: 0.85rem; font-weight: 800; }
        .status-pill { padding: 4px 12px; border-radius: 99px; font-size: 0.7rem; font-weight: 900; text-transform: uppercase; border: 1px solid rgba(255,255,255,0.2); }
        .status-pill.activo { background: #dcfce7; color: #15803d; }
        @media (max-width: 1100px) { .card-body-grid { grid-template-columns: 1fr; } }
      `}</style>
    </div>
  );
};

export default AccountQuery;
