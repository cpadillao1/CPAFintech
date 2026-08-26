import React, { useState, useEffect } from 'react';
import api from '../../../api/axiosConfig';
import Swal from 'sweetalert2';
import { ArrowRightLeft, User, Info } from 'lucide-react';
// Importamos las librerías necesarias para el PDF
import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';

const NcNdCreate = () => {
    const userData = JSON.parse(localStorage.getItem('user')) || {};

    const [formData, setFormData] = useState({
        accountId: '',
        typeId: '',
        configId: '',
        originId: 59,
        amount: '',
        description: '',
        transactionReference: `TRX-${Date.now()}`,
        createdBy: userData.login || 'system',
        originCode: 'ONLINE'
    });

    const [accountOwner, setAccountOwner] = useState('');
    const [transactionTypes, setTransactionTypes] = useState([]);
    const [configs, setConfigs] = useState([]);
    const [loading, setLoading] = useState(false);
    const [searchingAccount, setSearchingAccount] = useState(false);

    const generateReceiptPDF = (trxData) => {
            // Configuramos jsPDF para 80mm de ancho y 150mm de alto (formato ticket)
            const doc = new jsPDF({
                unit: "mm",
                format: [80, 150]
            });
            const businessDate = userData.businessDate || new Date().toLocaleDateString();
            const margin = 5;
            const width = 70; // Ancho utilizable
            let currentY = 10;

            // Estilo de Título
            doc.setFontSize(12);
            doc.setFont("helvetica", "bold");
            doc.text("TRANSACTION RECEIPT", 40, currentY, { align: "center" });

            currentY += 8;
            doc.setFontSize(8);
            doc.setFont("helvetica", "normal");
            doc.text("--------------------------------------------------", 40, currentY, { align: "center" });

            currentY += 5;

            // Información de la Transacción
            const details = [
                ["Reference:", trxData.transactionReference],
                ["Date:", businessDate],
                ["Account:", formData.accountId],
                ["Type:", transactionTypes.find(t => String(t.typeId) === String(formData.typeId))?.name || 'N/A'],
                ["Reason:", configs.find(c => String(c.configId) === String(formData.configId))?.reasonName || 'N/A'],
            ];

            details.forEach(([label, value]) => {
                doc.setFont("helvetica", "bold");
                doc.text(label, margin, currentY);
                doc.setFont("helvetica", "normal");
                doc.text(String(value), 30, currentY);
                currentY += 5;
            });

            currentY += 2;
            doc.setFontSize(11);
            doc.setFont("helvetica", "bold");
            doc.text("AMOUNT:", margin, currentY);
            doc.text(`$ ${parseFloat(formData.amount).toFixed(2)}`, 30, currentY);

            currentY += 8;
            doc.setFontSize(8);
            doc.setFont("helvetica", "normal");
            doc.text(`User: ${formData.createdBy}`, margin, currentY);
            currentY += 5;
            doc.text(`Origin: ${formData.originCode}`, margin, currentY);

            currentY += 10;
            doc.setFont("helvetica", "italic");
            doc.text("Thank you for using our services.", 40, currentY, { align: "center" });

            // Descarga automática del ticket
            doc.save(`Ticket_${trxData.transactionReference}.pdf`);
        };

    useEffect(() => {
        const loadTypes = async () => {
            try {
                const res = await api.get('/api/v1/transaction-configs/types');
                setTransactionTypes(res.data || []);
            } catch (err) {
                console.error("Error loading transaction types", err);
            }
        };
        loadTypes();
    }, []);

    useEffect(() => {
        if (!formData.typeId) {
            setConfigs([]);
            return;
        }
        setLoading(true);
        api.get(`/api/v1/transaction-configs/type/${formData.typeId}`)
            .then(res => setConfigs(res.data || []))
            .catch(err => {
                console.error("Error loading configs", err);
                setConfigs([]);
            })
            .finally(() => setLoading(false));

        setFormData(prev => ({ ...prev, configId: '' }));
    }, [formData.typeId]);

    const handleAccountBlur = async () => {
        if (!formData.accountId) {
            setAccountOwner('');
            return;
        }
        setSearchingAccount(true);
        try {
            const res = await api.get(`/api/v1/accounts/number/${formData.accountId}`);
            if (res.data && res.data.customerName) {
                setAccountOwner(res.data.customerName);
            } else {
                setAccountOwner('Account verified');
            }
        } catch (err) {
            console.error("Account validation error", err);
            setAccountOwner('');
            Swal.fire({
                icon: 'error',
                title: 'Invalid Account',
                text: 'The account number entered does not exist.',
                confirmButtonColor: '#334155'
            });
        } finally {
            setSearchingAccount(false);
        }
    };

    const resetForm = () => {
        setFormData({
            ...formData,
            accountId: '',
            typeId: '',
            configId: '',
            amount: '',
            description: '',
            transactionReference: `TRX-${Date.now()}`
        });
        setConfigs([]);
        setAccountOwner('');
    };

    const handleSaveTransaction = async (e) => {
        if (e) e.preventDefault();

        // VALIDACIONES INDIVIDUALES CON MENSAJES PERSONALIZADOS
        if (!formData.accountId) {
            Swal.fire({ icon: 'warning', title: 'Attention', text: 'Please enter a valid Account Number.', confirmButtonColor: '#334155' });
            return;
        }
        if (!formData.typeId) {
            Swal.fire({ icon: 'warning', title: 'Attention', text: 'You must select a Movement Type.', confirmButtonColor: '#334155' });
            return;
        }
        if (!formData.configId) {
            Swal.fire({ icon: 'warning', title: 'Attention', text: 'Please select a Reason or Purpose for this transaction.', confirmButtonColor: '#334155' });
            return;
        }
        if (!formData.amount || formData.amount <= 0) {
            Swal.fire({ icon: 'warning', title: 'Attention', text: 'A valid amount greater than zero is required.', confirmButtonColor: '#334155' });
            return;
        }

        setLoading(true);
        try {
            const requestBody = {
                ...formData,
                accountId: formData.accountId,
                configId: parseInt(formData.configId),
                typeId: parseInt(formData.typeId),
                amount: parseFloat(formData.amount)
            };

            const response = await api.post('/api/v1/transactions', requestBody);

            if (response.status === 201 || response.status === 200) {
                const trxResult = response.data;
                Swal.fire({
                    icon: 'success',
                    title: 'Successful!',
                    text: `Reference: ${trxResult.transactionReference}`,
                    confirmButtonColor: '#334155'
                });
                // --- GENERACIÓN DEL TICKET ---
                try {
                    generateReceiptPDF(trxResult);
                } catch (pdfErr) {
                    console.error("Error generating ticket:", pdfErr);
                }
                resetForm();
            }
        } catch (err) {
            const detail = err.response?.data?.message || "Internal Core Error";
            Swal.fire('Error', detail, 'error');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="ig-container">
            <h3 className="ig-card-title text-center" style={{ marginBottom: '20px' }}>
                💸 Debit & Credit Management
            </h3>

            <form className="ig-card" onSubmit={handleSaveTransaction} noValidate>
                <div className="ig-grid">
                    <div className="ig-field">
                        <label className="ig-label">Account Number <span className="text-danger">*</span></label>
                        <input
                            className="ig-input"
                            type="number"
                            placeholder="e.g., 1000000001"
                            value={formData.accountId}
                            onChange={e => setFormData({...formData, accountId: e.target.value})}
                            onBlur={handleAccountBlur}
                        />
                        {searchingAccount && <small className="text-muted">Searching...</small>}
                        {accountOwner && (
                            <div className="mt-1 d-flex align-items-center text-success fw-bold" style={{ fontSize: '0.85rem' }}>
                                <User size={14} className="me-1" /> {accountOwner}
                            </div>
                        )}
                    </div>

                    <div className="ig-field">
                        <label className="ig-label">Reference</label>
                        <input
                            className="ig-input"
                            style={{ backgroundColor: '#f8fafc', color: '#64748b' }}
                            value={formData.transactionReference}
                            readOnly
                        />
                    </div>

                    <div className="ig-field">
                        <label className="ig-label">Movement Type <span className="text-danger">*</span></label>
                        <select
                            className="ig-select"
                            value={formData.typeId}
                            onChange={e => setFormData({...formData, typeId: e.target.value})}
                        >
                            <option value="">Select type...</option>
                            {transactionTypes.map(t => (
                                <option key={t.typeId} value={t.typeId}>{t.name}</option>
                            ))}
                        </select>
                    </div>

                    <div className="ig-field">
                        <label className="ig-label">Reason / Purpose <span className="text-danger">*</span></label>
                        <select
                            className="ig-select"
                            value={formData.configId}
                            onChange={e => setFormData({...formData, configId: e.target.value})}
                            disabled={!formData.typeId || loading}
                        >
                            <option value="">
                                {loading ? 'Loading...' : formData.typeId ? 'Select reason...' : 'Choose type first'}
                            </option>
                            {configs.map(c => (
                                <option key={c.configId} value={c.configId}>
                                    {c.mnemonic} - {c.reasonName}
                                </option>
                            ))}
                        </select>
                    </div>

                    <div className="ig-field">
                        <label className="ig-label">Amount ($) <span className="text-danger">*</span></label>
                        <input
                            className="ig-input"
                            type="number"
                            step="0.01"
                            placeholder="0.00"
                            style={{ fontWeight: 'bold', color: '#2563eb' }}
                            value={formData.amount}
                            onChange={e => setFormData({...formData, amount: e.target.value})}
                        />
                    </div>
                </div>

                <div className="ig-field" style={{ marginTop: '15px' }}>
                    <label className="ig-label">Transaction Details</label>
                    <textarea
                        className="ig-input"
                        rows="3"
                        placeholder="Internal notes or concept..."
                        value={formData.description}
                        onChange={e => setFormData({...formData, description: e.target.value})}
                    ></textarea>
                </div>

                <div className="mt-4 p-3 bg-light rounded d-flex align-items-start border">
                    <Info size={18} className="text-secondary me-2 mt-1" />
                    <small className="text-muted">
                        Confirm all data before applying. Debits deduct from the available balance,
                        and credits add to the current balance.
                    </small>
                </div>

                <button
                    type="submit"
                    className="ig-btn-primary"
                    style={{ marginTop: '20px', backgroundColor: '#334155', width: '100%' }}
                    disabled={loading || searchingAccount}
                >
                    {loading ? 'Processing...' : (
                        <span className="d-flex align-items-center justify-content-center">
                            <ArrowRightLeft size={18} className="me-2"/> APPLY TRANSACTION
                        </span>
                    )}
                </button>
            </form>
        </div>
    );
};

export default NcNdCreate;
