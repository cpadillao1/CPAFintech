import React, { useState, useCallback } from 'react';
import api from '../../../api/axiosConfig';
import Swal from 'sweetalert2';
import { Search, ChevronLeft, ChevronRight, Calendar, Hash, User, Clock } from 'lucide-react';

const HoldQuery = () => {
    const [accountNumber, setAccountNumber] = useState('');
    const [holds, setHolds] = useState([]);
    const [accountOwner, setAccountOwner] = useState('');
    const [loading, setLoading] = useState(false);
    const [searchingAccount, setSearchingAccount] = useState(false);

    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [totalElements, setTotalElements] = useState(0);
    const pageSize = 10;

    const handleAccountBlur = async () => {
        if (!accountNumber.trim()) {
            setAccountOwner('');
            return;
        }
        setSearchingAccount(true);
        try {
            const res = await api.get(`/api/v1/accounts/number/${accountNumber.trim()}`);
            if (res.data && res.data.customerName) {
                setAccountOwner(res.data.customerName);
            } else {
                setAccountOwner('Verified Account');
            }
        } catch (err) {
            setAccountOwner('');
        } finally {
            setSearchingAccount(false);
        }
    };

    const fetchHolds = useCallback(async (page = 0) => {
        if (!accountNumber.trim()) {
            Swal.fire({ icon: 'warning', title: 'Attention', text: 'Please enter an account number.', confirmButtonColor: '#0f172a' });
            return;
        }
        setLoading(true);
        setCurrentPage(page);
        try {
            const response = await api.get('/api/v1/accounts/holds/query', {
                params: { accountNumber: accountNumber.trim(), page, size: pageSize }
            });
            const { content, totalPages: totalP, totalElements: totalE } = response.data;
            setHolds(content || []);
            setTotalPages(totalP || 0);
            setTotalElements(totalE || 0);
            if (totalE === 0) {
                Swal.fire({ icon: 'info', title: 'No Results', confirmButtonColor: '#0f172a' });
            }
        } catch (error) {
            Swal.fire('Error', 'Service unavailable.', 'error');
        } finally {
            setLoading(false);
        }
    }, [accountNumber]);

    const fNum = (val) => new Intl.NumberFormat('en-US', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    }).format(val || 0);

    // Ajustado para reconocer "ACTIVE" (como viene del JSON)
    const renderStatus = (status) => {
        const s = status?.toUpperCase() || '';
        const styles = {
            'ACTIVE': 'active',
            'ACTIVO': 'active',
            'RELEASED': 'inactive',
            'LIBERADO': 'inactive',
            'EXPIRED': 'inactive'
        };
        return (
            <span className={`status-pill ${styles[s] || 'inactive'}`} style={{ padding: '2px 8px', fontSize: '0.7rem' }}>
                {s}
            </span>
        );
    };

    return (
        <div className="ig-container">
            <div style={{ maxWidth: '1000px', margin: '0 auto', textAlign: 'center' }}>
                <h3 className="ig-card-title" style={{ marginBottom: '10px' }}>🔍 Hold Inquiry</h3>
            </div>

            {/* BUSCADOR */}
            <div className="ig-card" style={{ maxWidth: '1000px', padding: '20px 25px', marginBottom: '15px' }}>
                <form onSubmit={(e) => { e.preventDefault(); fetchHolds(0); }} className="search-flex-container" style={{ alignItems: 'flex-start' }}>
                    <div className="ig-field" style={{ flex: 2, marginBottom: 0 }}>
                        <label className="ig-label" style={{ fontSize: '0.75rem' }}>Account Number</label>
                        <div style={{ position: 'relative' }}>
                            <Hash size={14} style={{ position: 'absolute', left: '10px', top: '50%', transform: 'translateY(-50%)', color: '#94a3b8' }} />
                            <input
                                className="ig-input"
                                style={{ paddingLeft: '30px', height: '36px', fontSize: '0.85rem' }}
                                type="text"
                                value={accountNumber}
                                onChange={(e) => setAccountNumber(e.target.value)}
                                onBlur={handleAccountBlur}
                                placeholder="Ex: 1000..."
                            />
                        </div>
                        <div style={{ minHeight: '20px', marginTop: '4px' }}>
                            {searchingAccount && <small style={{ color: '#3b82f6', fontSize: '0.65rem' }}>Checking...</small>}
                            {accountOwner && (
                                <div className="text-success fw-bold" style={{ fontSize: '0.75rem', display: 'flex', alignItems: 'center' }}>
                                    <User size={12} className="me-1" /> {accountOwner}
                                </div>
                            )}
                        </div>
                    </div>

                    <div style={{ flex: 1, paddingTop: '24px' }}>
                        <button type="submit" className="ig-btn-navy btn-standard" disabled={loading} style={{ height: '36px', fontSize: '0.8rem', width: '100%' }}>
                            {loading ? <div className="loader-spinner" style={{ width: '14px', height: '14px' }}></div> : <><Search size={14} className="me-1" /> SEARCH</>}
                        </button>
                    </div>
                </form>
            </div>

            {/* TABLA Y PAGINACIÓN */}
            {totalElements > 0 && (
                <div className="ig-card" style={{ maxWidth: '1000px', padding: '10px 15px' }}>
                    <div className="ig-table-action-wrapper" style={{ marginTop: 0 }}>
                        <table className="ig-table">
                            <thead>
                                <tr>
                                    <th className="ig-th" style={{ padding: '10px 8px' }}>START</th>
                                    <th className="ig-th" style={{ padding: '10px 8px' }}>EXPIRY</th>
                                    <th className="ig-th" style={{ padding: '10px 8px' }}>REFERENCE</th>
                                    <th className="ig-th" style={{ padding: '10px 8px' }}>TYPE</th>
                                    <th className="ig-th" style={{ padding: '10px 8px', textAlign: 'right' }}>AMOUNT</th>
                                    <th className="ig-th" style={{ padding: '10px 8px' }}>USER</th>
                                    <th className="ig-th" style={{ padding: '10px 8px' }}>STATUS</th>
                                </tr>
                            </thead>
                            <tbody>
                                {holds.map((hold, index) => (
                                    <tr key={index} className="ig-row-hover">
                                        <td className="ig-td" style={{ padding: '8px', fontWeight: '600', fontSize: '0.8rem' }}>{hold.startDate}</td>
                                        <td className="ig-td" style={{ padding: '8px', fontSize: '0.8rem' }}>{hold.expiryDate || '—'}</td>
                                        <td className="ig-td" style={{ padding: '8px', fontFamily: 'monospace', fontSize: '0.8rem', color: '#334155' }}>
                                            {hold.referenceNumber || '—'}
                                        </td>
                                        <td className="ig-td" style={{ padding: '8px' }}>
                                            <span className="ig-badge" style={{ fontSize: '0.6rem' }}>
                                                {/* Priorizamos la descripción que viene del catálogo */}
                                                {hold.holdTypeDescription || hold.holdType || 'GENERAL'}
                                            </span>
                                        </td>
                                        <td className="ig-td" style={{ padding: '8px', textAlign: 'right', fontWeight: 'bold', color: '#ef4444', fontSize: '0.8rem' }}>
                                            {fNum(hold.amount)}
                                        </td>
                                        <td className="ig-td" style={{ padding: '8px', fontSize: '0.8rem', color: '#475569' }}>
                                            {hold.createdBy || 'SYSTEM'}
                                        </td>
                                        <td className="ig-td" style={{ padding: '8px' }}>{renderStatus(hold.status)}</td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>

                    {totalPages > 1 && (
                        <div className="ig-pagination-container" style={{ marginTop: '10px' }}>
                            <button
                                className="ig-btn-navy ig-btn-pagination"
                                disabled={currentPage === 0 || loading}
                                onClick={() => fetchHolds(currentPage - 1)}
                            >
                                <ChevronLeft size={16} />
                            </button>
                            <div className="ig-pagination-info">
                                {currentPage + 1} / {totalPages}
                            </div>
                            <button
                                className="ig-btn-navy ig-btn-pagination"
                                disabled={currentPage + 1 >= totalPages || loading}
                                onClick={() => fetchHolds(currentPage + 1)}
                            >
                                <ChevronRight size={16} />
                            </button>
                        </div>
                    )}
                </div>
            )}
        </div>
    );
};

export default HoldQuery;
