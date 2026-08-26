import React, { useState } from 'react';
import api from '../../../api/axiosConfig';
import Swal from 'sweetalert2';
import { Search, User, Lock, Unlock, AlertCircle, Calendar } from 'lucide-react';

const ReleaseHold = () => {
    const userData = JSON.parse(localStorage.getItem('user')) || {};

    const [accountNumber, setAccountNumber] = useState('');
    const [customerName, setCustomerName] = useState('');
    const [holds, setHolds] = useState([]);
    const [selectedHoldId, setSelectedHoldId] = useState(null);
    const [releaseObservations, setReleaseObservations] = useState('');

    const [loading, setLoading] = useState(false);
    const [searching, setSearching] = useState(false);

    const handleSearch = async () => {
        if (!accountNumber) {
            return Swal.fire({ icon: 'warning', title: 'Attention', text: 'Please enter an account number.', confirmButtonColor: '#334155' });
        }

        setSearching(true);
        setCustomerName('');
        setHolds([]);
        setSelectedHoldId(null);

        try {
            const accountRes = await api.get(`/api/v1/accounts/number/${accountNumber}`);
            setCustomerName(accountRes.data.customerName);

            const holdsRes = await api.get(`/api/v1/accounts/holds/query?accountNumber=${accountNumber}`);
            setHolds(holdsRes.data.content || []);

            if (holdsRes.data.content.length === 0) {
                Swal.fire({ icon: 'info', title: 'No Holds Found', text: 'This account has no active holds to release.', confirmButtonColor: '#334155' });
            }
        } catch (err) {
            console.error("Search error:", err);
            Swal.fire({ icon: 'error', title: 'Not Found', text: 'Account not found or service unavailable.', confirmButtonColor: '#334155' });
        } finally {
            setSearching(false);
        }
    };

    const handleRelease = async (e) => {
        if (e) e.preventDefault();

        if (!selectedHoldId) {
            return Swal.fire({ icon: 'warning', title: 'Selection Required', text: 'Please select a hold from the table first.', confirmButtonColor: '#334155' });
        }

        if (!releaseObservations.trim()) {
            return Swal.fire({ icon: 'warning', title: 'Missing Information', text: 'Please provide observations for the release.', confirmButtonColor: '#334155' });
        }

        setLoading(true);
        try {
            const releaseRequest = {
                observations: releaseObservations,
                releasedBy: userData.login || 'system'
            };

            await api.patch(`/api/v1/accounts/holds/${selectedHoldId}/release`, releaseRequest);

            await Swal.fire({ icon: 'success', title: 'Funds Released', text: 'The hold has been successfully released.', confirmButtonColor: '#334155' });

            setReleaseObservations('');
            handleSearch();
        } catch (err) {
            const errMsg = err.response?.data?.message || "Error processing release";
            Swal.fire({ icon: 'error', title: 'Action Failed', text: errMsg, confirmButtonColor: '#334155' });
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="ig-container" style={{ padding: '5px 20px', maxWidth: '1000px', margin: '0 auto' }}>
            <h3 className="ig-card-title text-center" style={{ marginBottom: '10px', fontSize: '1.15rem' }}>
                🔓 Release Account Funds (Hold Release)
            </h3>

            <div className="ig-card" style={{ padding: '15px 25px', maxWidth: '1000px', margin: '0 auto' }}>
                <div className="search-flex-container" style={{
                    marginBottom: '10px',
                    padding: '8px 15px',
                    backgroundColor: '#f8fafc',
                    borderRadius: '10px',
                    border: '1px solid #e2e8f0',
                    display: 'flex',
                    alignItems: 'flex-end',
                    gap: '15px'
                }}>
                    <div className="ig-field" style={{ flex: '0 0 200px', marginBottom: 0 }}>
                        <label className="ig-label" style={{ fontSize: '0.75rem' }}>Account Number <span className="text-danger">*</span></label>
                        <input
                            className="ig-input"
                            type="text"
                            placeholder="Search..."
                            value={accountNumber}
                            onChange={(e) => setAccountNumber(e.target.value)}
                            onKeyPress={(e) => e.key === 'Enter' && handleSearch()}
                            style={{ height: '38px' }}
                        />
                    </div>

                    <button
                        type="button"
                        className="ig-btn-navy"
                        style={{ width: 'auto', padding: '0 15px', height: '38px', flexShrink: 0 }}
                        onClick={handleSearch}
                        disabled={searching}
                    >
                        {searching ? '...' : <Search size={14} />}
                    </button>

                    {customerName && (
                        <div style={{
                            flex: 1,
                            height: '38px',
                            display: 'flex',
                            alignItems: 'center',
                            padding: '0 12px',
                            backgroundColor: '#fff',
                            border: '1px solid #cbd5e1',
                            borderRadius: '6px',
                            fontSize: '0.85rem',
                            overflow: 'hidden'
                        }}>
                            <User size={14} className="text-primary me-2" />
                            <span className="fw-bold text-slate-700 me-2" style={{ whiteSpace: 'nowrap' }}>Customer:</span>
                            <span className="text-primary fw-bold text-truncate" title={customerName}>{customerName}</span>
                        </div>
                    )}
                </div>

                <div className="ig-table-wrapper" style={{ marginTop: '5px' }}>
                    <label className="ig-label" style={{ fontSize: '0.65rem', display: 'flex', alignItems: 'center', gap: '4px', padding: '0 8px', marginBottom: '4px' }}>
                        <Lock size={12} /> Active Holds Selection
                    </label>
                    <div className="ig-table-action-wrapper" style={{ maxHeight: '400px', overflowY: 'auto' }}>
                        <table className="ig-table">
                            <thead>
                                <tr>
                                    <th className="ig-th" style={{ padding: '4px 8px' }}>Select</th>
                                    <th className="ig-th" style={{ padding: '4px 8px' }}>Type Hold</th>
                                    <th className="ig-th" style={{ padding: '4px 8px' }}>Start Date</th>
                                    <th className="ig-th" style={{ padding: '4px 8px' }}>Expiry Date</th>
                                    <th className="ig-th" style={{ padding: '4px 8px' }}>Amount</th>
                                </tr>
                            </thead>
                            <tbody>
                                {holds.length > 0 ? (
                                    holds.map((hold) => (
                                        <tr
                                            key={hold.id}
                                            className={`ig-row-clickable ${selectedHoldId === hold.id ? 'ig-row-selected' : ''}`}
                                            onClick={() => setSelectedHoldId(hold.id)}
                                        >
                                            <td className="ig-td text-center" style={{ padding: '4px 8px' }}>
                                                <input
                                                    type="radio"
                                                    name="holdSelection"
                                                    className="ig-radio-navy"
                                                    checked={selectedHoldId === hold.id}
                                                    onChange={() => setSelectedHoldId(hold.id)}
                                                />
                                            </td>
                                            <td className="ig-td" style={{ padding: '4px 8px' }}>
                                                <div className="fw-bold" style={{ fontSize: '0.8rem', color: '#1e293b' }}>
                                                    {hold.holdTypeDescription}
                                                </div>
                                            </td>
                                            <td className="ig-td" style={{ padding: '4px 8px', fontSize: '0.8rem' }}>
                                                <div className="d-flex align-items-center">
                                                    <Calendar size={12} className="me-1" />
                                                    {hold.startDate}
                                                </div>
                                            </td>
                                            <td className="ig-td" style={{ padding: '4px 8px', fontSize: '0.8rem' }}>
                                                <div className="d-flex align-items-center">
                                                    <Calendar size={12} className="me-1 text-muted" />
                                                    {hold.expiryDate}
                                                </div>
                                            </td>
                                            <td className="ig-td fw-bold text-danger" style={{ padding: '4px 8px', fontSize: '0.85rem' }}>
                                                $ {parseFloat(hold.amount).toFixed(2)}
                                            </td>
                                        </tr>
                                    ))
                                ) : (
                                    <tr>
                                        <td colSpan="5" className="ig-td text-center py-2 text-muted italic" style={{ fontSize: '0.8rem', padding: '8px' }}>
                                            No holds available.
                                        </td>
                                    </tr>
                                )}
                            </tbody>
                        </table>
                    </div>
                </div>

                <div className="ig-field" style={{ marginTop: '15px', marginBottom: '10px' }}>
                    <label className="ig-label" style={{ fontSize: '0.75rem' }}>Release Observations <span className="text-danger">*</span></label>
                    <input
                        className="ig-input"
                        type="text"
                        style={{ fontSize: '0.85rem', height: '38px' }}
                        placeholder="Explain why these funds are being released..."
                        value={releaseObservations}
                        onChange={(e) => setReleaseObservations(e.target.value)}
                        disabled={!selectedHoldId}
                    />
                </div>

                <div className="p-2 rounded d-flex align-items-start border bg-light" style={{ marginBottom: '10px' }}>
                    <AlertCircle size={14} className="text-warning me-2 mt-1" />
                    <small className="text-muted" style={{ fontSize: '0.7rem' }}>
                        <strong>Note:</strong> Releasing a hold immediately updates the <em>Available Balance</em>.
                    </small>
                </div>

                <button
                    type="button"
                    className="ig-btn-navy"
                    style={{ backgroundColor: '#334155', padding: '10px', width: '100%' }}
                    onClick={handleRelease}
                    disabled={loading || !selectedHoldId}
                >
                    {loading ? 'Processing...' : (
                        <span className="d-flex align-items-center justify-content-center">
                            <Unlock size={16} className="me-2"/> CONFIRM RELEASE
                        </span>
                    )}
                </button>
            </div>
        </div>
    );
};

export default ReleaseHold;
