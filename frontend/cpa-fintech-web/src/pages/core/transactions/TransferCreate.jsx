import React, { useState } from 'react';
import api from '../../../api/axiosConfig';
import Swal from 'sweetalert2';
import { ArrowRightLeft, User, AlertCircle } from 'lucide-react';
// Importamos jsPDF para el ticket
import jsPDF from 'jspdf';

const TransferCreate = () => {
    const userData = JSON.parse(localStorage.getItem('user')) || {};
    // Extraemos la fecha de negocio si existe
    const businessDate = userData.businessDate || new Date().toLocaleDateString();

    const [formData, setFormData] = useState({
        sourceAccountId: '',
        targetAccountId: '',
        amount: '',
        sourceConfigId: 20, // INTERNAL TRANSFER DEBIT
        targetConfigId: 21, // INTERNAL TRANSFER CREDIT
        description: '',
        transactionReference: `TRF-${Date.now()}`,
        createdBy: userData.login || 'system',
        terminalIp: '127.0.0.1',
        originCode: 'ONLINE'
    });

    const [owners, setOwners] = useState({ source: '', target: '' });
    const [loading, setLoading] = useState(false);
    const [searching, setSearching] = useState({ source: false, target: false });

    // --- FUNCIÓN PARA GENERAR EL TICKET DE TRANSFERENCIA (FORMATO ETIQUETERA) ---
    const generateTransferReceiptPDF = (trxData) => {
        const doc = new jsPDF({
            unit: "mm",
            format: [80, 150] // Tamaño estándar térmico
        });

        const margin = 5;
        let currentY = 10;

        // Título del Ticket
        doc.setFontSize(12);
        doc.setFont("helvetica", "bold");
        doc.text("TRANSFER RECEIPT", 40, currentY, { align: "center" });

        currentY += 8;
        doc.setFontSize(8);
        doc.setFont("helvetica", "normal");
        doc.text("--------------------------------------------------", 40, currentY, { align: "center" });

        currentY += 5;

        // Detalles de la operación
        const details = [
            { label: "Reference:", value: trxData.transactionReference, isOwner: false },
            { label: "Date:", value: businessDate, isOwner: false },
            { label: "Source Acc:", value: formData.sourceAccountId, isOwner: false },
            { label: "Source Owner:", value: owners.source, isOwner: true },
            { label: "Target Acc:", value: formData.targetAccountId, isOwner: false },
            { label: "Target Owner:", value: owners.target, isOwner: true },
        ];

        details.forEach((item) => {
            doc.setFontSize(8);
            doc.setFont("helvetica", "bold");
            doc.text(item.label, margin, currentY);

            doc.setFont("helvetica", "normal");

            if (item.isOwner) {
                // Ajustes para propietarios: Fuente más pequeña y multilínea
                doc.setFontSize(7);
                const splitName = doc.splitTextToSize(String(item.value || 'N/A'), 45);
                doc.text(splitName, 28, currentY);
                // Ajustar Y basado en cuántas líneas ocupó el nombre
                currentY += (splitName.length * 3.5);
            } else {
                doc.setFontSize(8);
                doc.text(String(item.value || 'N/A'), 28, currentY);
                currentY += 5;
            }
        });

        currentY += 2;
        doc.setFontSize(11);
        doc.setFont("helvetica", "bold");
        doc.text("AMOUNT:", margin, currentY);
        doc.text(`$ ${parseFloat(formData.amount).toFixed(2)}`, 28, currentY);

        currentY += 8;
        doc.setFontSize(8);
        doc.setFont("helvetica", "normal");
        doc.text(`User: ${formData.createdBy}`, margin, currentY);
        currentY += 5;
        doc.text(`Origin: ${formData.originCode}`, margin, currentY);

        currentY += 10;
        doc.setFont("helvetica", "italic");
        doc.text("Internal Transfer Successful", 40, currentY, { align: "center" });

        doc.save(`Transfer_${trxData.transactionReference}.pdf`);
    };

    const validateAccount = async (accountNumber, type) => {
        if (!accountNumber) {
            setOwners(prev => ({ ...prev, [type]: '' }));
            return;
        }

        setSearching(prev => ({ ...prev, [type]: true }));
        try {
            const res = await api.get(`/api/v1/accounts/number/${accountNumber}`);
            if (res.data && res.data.customerName) {
                setOwners(prev => ({ ...prev, [type]: res.data.customerName }));
            }
        } catch (err) {
            console.error("Account not found", err);
            setOwners(prev => ({ ...prev, [type]: '' }));
            const Toast = Swal.mixin({ toast: true, position: 'top-end', showConfirmButton: false, timer: 3000 });
            Toast.fire({ icon: 'error', title: `Account ${accountNumber} not found` });
        } finally {
            setSearching(prev => ({ ...prev, [type]: false }));
        }
    };

    const resetForm = () => {
        setFormData({
            ...formData,
            sourceAccountId: '',
            targetAccountId: '',
            amount: '',
            description: '',
            transactionReference: `TRF-${Date.now()}`
        });
        setOwners({ source: '', target: '' });
    };

    const handleTransfer = async (e) => {
        if (e) e.preventDefault();

        // --- VALIDACIONES PERSONALIZADAS CON SWAL ---
        if (!formData.sourceAccountId) {
            return Swal.fire({ icon: 'warning', title: 'Attention', text: 'Please enter the Source Account (Debit).', confirmButtonColor: '#334155' });
        }
        if (!formData.targetAccountId) {
            return Swal.fire({ icon: 'warning', title: 'Attention', text: 'Please enter the Target Account (Credit).', confirmButtonColor: '#334155' });
        }
        if (formData.sourceAccountId === formData.targetAccountId) {
            return Swal.fire({ icon: 'error', title: 'Validation Error', text: 'Source and Target accounts cannot be the same.', confirmButtonColor: '#334155' });
        }
        if (!owners.source || !owners.target) {
            return Swal.fire({ icon: 'warning', title: 'Validation Error', text: 'Both account numbers must be valid and verified.', confirmButtonColor: '#334155' });
        }
        if (!formData.amount || formData.amount <= 0) {
            return Swal.fire({ icon: 'warning', title: 'Attention', text: 'Please enter a valid amount greater than zero.', confirmButtonColor: '#334155' });
        }

        setLoading(true);
        try {
            const response = await api.post('/api/v1/transactions/transfer', formData);

            if (response.status === 200 || response.status === 201) {
                const trxResult = response.data;
                await Swal.fire({
                    icon: 'success',
                    title: 'Transfer Successful',
                    text: `Reference: ${trxResult.transactionReference}`,
                    confirmButtonColor: '#334155'
                });
                // --- DISPARO DEL TICKET PDF ---
                try {
                    generateTransferReceiptPDF(trxResult);
                } catch (pdfErr) {
                    console.error("Error generating receipt:", pdfErr);
                }
                resetForm();
            }
        } catch (err) {
            const errorMessage = err.response?.data?.message || "Internal Service Error";
            Swal.fire({
                icon: 'error',
                title: 'Transfer Failed',
                text: errorMessage,
                confirmButtonColor: '#334155'
            });
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="ig-container">
            {/* Título centrado al nivel del div principal */}
            <h3 className="ig-card-title text-center" style={{ marginBottom: '20px' }}>
                🔄 Internal Account Transfer
            </h3>

            <form className="ig-card" onSubmit={handleTransfer} noValidate>
                <div className="ig-grid">

                    {/* Source Account */}
                    <div className="ig-field">
                        <label className="ig-label">Source Account (Debit) <span className="text-danger">*</span></label>
                        <input
                            className="ig-input"
                            type="number"
                            placeholder="Debit from..."
                            value={formData.sourceAccountId}
                            onChange={e => setFormData({...formData, sourceAccountId: e.target.value})}
                            onBlur={() => validateAccount(formData.sourceAccountId, 'source')}
                        />
                        {searching.source && <small className="text-muted">Validating...</small>}
                        {owners.source && (
                            <div className="mt-1 text-primary fw-bold" style={{ fontSize: '0.85rem' }}>
                                <User size={14} className="me-1" /> {owners.source}
                            </div>
                        )}
                    </div>

                    {/* Target Account */}
                    <div className="ig-field">
                        <label className="ig-label">Target Account (Credit) <span className="text-danger">*</span></label>
                        <input
                            className="ig-input"
                            type="number"
                            placeholder="Credit to..."
                            value={formData.targetAccountId}
                            onChange={e => setFormData({...formData, targetAccountId: e.target.value})}
                            onBlur={() => validateAccount(formData.targetAccountId, 'target')}
                        />
                        {searching.target && <small className="text-muted">Validating...</small>}
                        {owners.target && (
                            <div className="mt-1 text-success fw-bold" style={{ fontSize: '0.85rem' }}>
                                <User size={14} className="me-1" /> {owners.target}
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
                        <label className="ig-label">Amount to Transfer ($) <span className="text-danger">*</span></label>
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
                    <label className="ig-label">Description / Concept</label>
                    <textarea
                        className="ig-input"
                        rows="2"
                        placeholder="Reason for transfer..."
                        value={formData.description}
                        onChange={e => setFormData({...formData, description: e.target.value})}
                    ></textarea>
                </div>

                <div className="mt-4 p-3 rounded d-flex align-items-start border bg-light">
                    <AlertCircle size={18} className="text-warning me-2 mt-1" />
                    <small className="text-muted">
                        <strong>Security Note:</strong> This operation is atomic. If the target account is inactive or
                        the source account has insufficient funds, no movements will be recorded.
                    </small>
                </div>

                {/* Botón con color azul uniforme */}
                <button
                    type="submit"
                    className="ig-btn-primary"
                    style={{ marginTop: '20px', backgroundColor: '#334155', width: '100%' }}
                    disabled={loading}
                >
                    {loading ? 'Processing Transfer...' : (
                        <span className="d-flex align-items-center justify-content-center">
                            <ArrowRightLeft size={18} className="me-2"/> CONFIRM TRANSFER
                        </span>
                    )}
                </button>
            </form>
        </div>
    );
};

export default TransferCreate;
