import React, { useState, useEffect } from 'react';
import api from '../../../api/axiosConfig';
import { Modal, Spinner } from 'react-bootstrap';
import { Layers, Percent, Info, RefreshCw } from 'lucide-react';
import Swal from 'sweetalert2';

const SubproductQuery = () => {
    const [subproducts, setSubproducts] = useState([]);
    const [loading, setLoading] = useState(true);

    // Modal States
    const [showModal, setShowModal] = useState(false);
    const [selectedSubproduct, setSelectedSubproduct] = useState(null);
    const [interestData, setInterestData] = useState(null);
    const [loadingRates, setLoadingRates] = useState(false);

    const PRIMARY_COLOR = '#334155';

    useEffect(() => {
        fetchSubproducts();
    }, []);

    const fetchSubproducts = async () => {
        setLoading(true);
        try {
            const response = await api.get('/api/v1/management/subproducts');
            const sortedData = (response.data || []).sort((a, b) => a.id - b.id);
            setSubproducts(sortedData);
        } catch (error) {
            Swal.fire({
                icon: 'error',
                title: 'Connection Error',
                text: 'Could not retrieve subproducts.',
                confirmButtonColor: PRIMARY_COLOR
            });
        } finally {
            setLoading(false);
        }
    };

    const handleViewRates = async (sub) => {
        if (!sub.interestGroupId) {
            Swal.fire({
                icon: 'info',
                title: 'No Parametrization',
                text: 'This subproduct does not have an interest group assigned.',
                confirmButtonColor: PRIMARY_COLOR
            });
            return;
        }

        setSelectedSubproduct(sub);
        setShowModal(true);
        setLoadingRates(true);

        try {
            const response = await api.get(`/api/v1/interest-groups/${sub.interestGroupId}`);
            setInterestData(response.data);
        } catch (error) {
            setShowModal(false);
            Swal.fire({
                icon: 'error',
                title: 'Error',
                text: 'Could not load interest range configuration.',
                confirmButtonColor: PRIMARY_COLOR
            });
        } finally {
            setLoadingRates(false);
        }
    };

    const formatCurrency = (value) => {
        return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(value);
    };

    const formatPercent = (value) => {
        return `${(value * 100).toFixed(2)}%`;
    };

    return (
        <div className="ig-container">
            {/* Ajuste de ancho a 1200px */}
            <div className="ig-card" style={{ maxWidth: '1200px', margin: '0 auto' }}>
                <div className="ig-header-info">
                    <div>
                        <h2 className="ig-card-title">
                            <Layers size={22} className="me-2" style={{ verticalAlign: 'middle' }} />
                            Subproduct Query
                        </h2>
                        <p className="text-muted small">Financial product catalog and interest configuration</p>
                    </div>
                    <button
                        className="ig-btn-navy btn-standard"
                        onClick={fetchSubproducts}
                        disabled={loading}
                        style={{ width: 'auto' }}
                    >
                        <RefreshCw size={16} className={loading ? 'spin' : ''} />
                        {loading ? 'Loading...' : 'Refresh List'}
                    </button>
                </div>

                <div className="ig-table-wrapper">
                    <table className="ig-table">
                        <thead>
                            <tr>
                                <th className="ig-th">Product</th>
                                <th className="ig-th">Code</th>
                                <th className="ig-th">Name</th>
                                <th className="ig-th">Description</th>
                                <th className="ig-th">Frequency</th>
                                <th className="ig-th">Status</th>
                                <th className="ig-th text-center">Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            {loading ? (
                                <tr>
                                    <td colSpan="7" className="text-center py-5">
                                        <div className="loader-spinner" style={{ margin: '0 auto' }}></div>
                                    </td>
                                </tr>
                            ) : subproducts.length > 0 ? (
                                subproducts.map((sub) => (
                                    <tr key={sub.id} className="ig-row-clickable">
                                        <td className="ig-td fw-bold" style={{ color: '#2563eb' }}>{sub.productName || 'N/A'}</td>
                                        <td className="ig-td fw-bold">{sub.code}</td>
                                        <td className="ig-td">{sub.name}</td>
                                        <td className="ig-td text-muted">{sub.description}</td>
                                        <td className="ig-td"><span className="ig-badge">{sub.statementFrequency}</span></td>
                                        <td className="ig-td">
                                            <span className={`status-pill ${sub.status === 'ACTIVE' ? 'activo' : 'inactivo'}`}>
                                                {sub.status}
                                            </span>
                                        </td>
                                        <td className="ig-td text-center">
                                            <button
                                                className="ig-btn-navy"
                                                style={{ padding: '6px 12px', fontSize: '0.75rem', height: 'auto', width: 'auto', display: 'inline-flex' }}
                                                onClick={() => handleViewRates(sub)}
                                            >
                                                <Percent size={14} className="me-1" /> View Rates
                                            </button>
                                        </td>
                                    </tr>
                                ))
                            ) : (
                                <tr>
                                    <td colSpan="7" className="text-center py-4 text-muted">No records found.</td>
                                </tr>
                            )}
                        </tbody>
                    </table>
                </div>
            </div>

            {/* MODAL: INTEREST RANGE CONFIGURATION */}
            <Modal show={showModal} onHide={() => setShowModal(false)} size="lg" centered>
                <Modal.Header closeButton className="bg-light">
                    <Modal.Title className="h6 fw-bold text-navy">
                        <Percent size={18} className="me-2" />
                        Interest Configuration: {selectedSubproduct?.name}
                    </Modal.Title>
                </Modal.Header>
                <Modal.Body className="p-0">
                    {loadingRates ? (
                        <div className="text-center py-5">
                            <Spinner animation="border" size="sm" variant="primary" />
                            <p className="mt-2 text-muted small">Querying interest engine...</p>
                        </div>
                    ) : interestData ? (
                        <div className="p-4">
                            <div className="ig-range-form" style={{ gridTemplateColumns: '1fr', marginBottom: '20px', padding: '15px' }}>
                                <div className="d-flex align-items-center gap-3">
                                    <div className="p-2 bg-white rounded border">
                                        <Info size={24} color="#3b82f6" />
                                    </div>
                                    <div>
                                        <h6 className="mb-0 fw-bold">{interestData.name} ({interestData.code})</h6>
                                        <p className="mb-0 text-muted small">{interestData.description}</p>
                                    </div>
                                </div>
                            </div>

                            <table className="ig-table border">
                                <thead>
                                    <tr style={{ backgroundColor: '#f8fafc' }}>
                                        <th className="ig-th">Range Name</th>
                                        <th className="ig-th text-end">Min Amount</th>
                                        <th className="ig-th text-end">Max Amount</th>
                                        <th className="ig-th text-center">Rate (EA)</th>
                                        <th className="ig-th text-center">Status</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {interestData.ranges.map((r) => (
                                        <tr key={r.id}>
                                            <td className="ig-td fw-semibold">{r.rangeName}</td>
                                            <td className="ig-td text-end font-monospace">{formatCurrency(r.minAmount)}</td>
                                            {/* Ajuste: Se carga el valor directo de la base de datos formateado */}
                                            <td className="ig-td text-end font-monospace">
                                                {formatCurrency(r.maxAmount)}
                                            </td>
                                            <td className="ig-td text-center">
                                                <span className="ig-rate-tag">
                                                    {formatPercent(r.rateValue)}
                                                </span>
                                            </td>
                                            <td className="ig-td text-center">
                                                <small className={r.status === 'ACTIVE' ? 'status-activo' : 'text-danger'}>
                                                    {r.status}
                                                </small>
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>
                    ) : null}
                </Modal.Body>
                <Modal.Footer className="bg-light">
                    <button className="btn-back" onClick={() => setShowModal(false)} style={{ margin: 0 }}>
                        Close
                    </button>
                </Modal.Footer>
            </Modal>
        </div>
    );
};

export default SubproductQuery;