import React, { useState, useEffect } from 'react';
import api from '../../../api/axiosConfig';
import Swal from 'sweetalert2';
import { UserPlus, Trash2, Users, MapPin } from 'lucide-react';
// Importamos las librerías necesarias para el PDF
import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';

const AccountCreate = () => {
    const userData = JSON.parse(localStorage.getItem('user')) || {};
    // Extraemos la fecha de negocio si existe, sino la del sistema
    const businessDate = userData.businessDate || new Date().toLocaleDateString();

    const [formData, setFormData] = useState({
        branchId: userData.branchId || '',
        customerId: '',
        productId: '',
        subproductId: '',
        currencyId: '',
        ownershipTypeId: '',
        createdBy: userData.login || 'system',
        holders: []
    });

    const [products, setProducts] = useState([]);
    const [subproducts, setSubproducts] = useState([]);
    const [catalogs, setCatalogs] = useState({
        currencies: [],
        ownershipTypes: [],
        holderRoles: []
    });

    const [mainCustomer, setMainCustomer] = useState(null);
    const [searchDoc, setSearchDoc] = useState('');
    const [loading, setLoading] = useState(false);

    const [showModal, setShowModal] = useState(false);
    const [holderSearchDoc, setHolderSearchDoc] = useState('');
    const [tempHolder, setTempHolder] = useState(null);
    const [selectedRole, setSelectedRole] = useState('');

    // --- FUNCIÓN PARA GENERAR EL CONTRATO PDF ---
    // --- FUNCIÓN PARA GENERAR EL CONTRATO PDF ---
    const generateContractPDF = (accountInfo) => {
        const doc = new jsPDF();
        const businessDate = userData.businessDate || new Date().toLocaleDateString();

        // 1. Cabecera y Título
        doc.setFontSize(18);
        doc.setTextColor(51, 65, 85);
        doc.text("ACCOUNT OPENING AGREEMENT", 14, 22);

        doc.setFontSize(10);
        doc.setTextColor(100);
        doc.text(`Account Number: ${accountInfo.accountNumber || 'N/A'}`, 14, 30);
        doc.text(`Business Date: ${businessDate}`, 150, 30);
        doc.line(14, 35, 196, 35);

        // 2. Tabla de Detalles del Producto
        autoTable(doc, {
            startY: 45,
            head: [['Account Details', 'Information']],
            body: [
                ["Branch", userData.branchName || 'N/A'],
                ["Product", products.find(p => String(p.id) === String(formData.productId))?.name || 'N/A'],
                ["Subproduct", subproducts.find(s => String(s.id) === String(formData.subproductId))?.name || 'N/A'],
                ["Currency", catalogs.currencies.find(c => String(c.id) === String(formData.currencyId))?.name || 'N/A']
            ],
            theme: 'striped',
            headStyles: { fillColor: [51, 65, 85] }
        });

        // 3. Tabla de Titulares
        autoTable(doc, {
            startY: doc.lastAutoTable.finalY + 10,
            head: [['Full Name', 'Account Role']],
            body: formData.holders.map(h => [h.fullName, h.roleName]),
            headStyles: { fillColor: [71, 85, 105] }
        });

        // --- BLOQUE DE FIRMAS LIMPIO ---
        let currentY = doc.lastAutoTable.finalY + 35;
        const margin = 14;
        const pageWidth = doc.internal.pageSize.getWidth();
        const signatureWidth = 75;

        doc.setFontSize(9);
        doc.setTextColor(0);

        formData.holders.forEach((holder, index) => {
            const isLeft = index % 2 === 0;
            const xPos = isLeft ? margin : pageWidth - margin - signatureWidth;

            if (index > 0 && index % 2 === 0) {
                currentY += 35;
            }

            // Línea de firma
            doc.setDrawColor(150);
            doc.line(xPos, currentY, xPos + signatureWidth, currentY);

            // Nombre del titular (Sin etiquetas "quemadas")
            doc.setFont("helvetica", "bold");
            doc.text(holder.fullName.toUpperCase(), xPos, currentY + 6);

            // Rol del titular justo debajo
            doc.setFont("helvetica", "normal");
            doc.setFontSize(8);
            doc.text(holder.roleName, xPos, currentY + 11);
            doc.setFontSize(9);
        });

        // 4. Footer Profesional (Inglés Fintech)
        const pageHeight = doc.internal.pageSize.getHeight();
        doc.setFontSize(8);
        doc.setFont("helvetica", "italic");
        doc.setTextColor(120);

        // Leyenda profesional mejorada
        const legalDisclaimer = "This document is a certified digital representation of the formal account opening agreement. All terms and conditions are subject to the bank's master service agreement and applicable financial regulations.";

        // Ajustar el texto al ancho de la página para que no se corte
        const splitDisclaimer = doc.splitTextToSize(legalDisclaimer, 180);
        doc.text(splitDisclaimer, margin, pageHeight - 15);

        // Guardar archivo
        doc.save(`Agreement_${accountInfo.accountNumber}.pdf`);
    };

    useEffect(() => {
        const loadInitialData = async () => {
            try {
                const [resProducts, resCatalogs] = await Promise.all([
                    api.get('/api/v1/products'),
                    api.get('/api/v1/catalogs/all')
                ]);
                const extractData = (res) => res.data.content || (Array.isArray(res.data) ? res.data : []);
                setProducts(extractData(resProducts));
                const catData = resCatalogs.data || [];
                setCatalogs({
                    currencies: catData.filter(c => c.catalogName?.toUpperCase() === 'CURRENCY'),
                    ownershipTypes: catData.filter(c => c.catalogName?.toUpperCase() === 'OWNERSHIP'),
                    holderRoles: catData.filter(c => c.catalogName?.toUpperCase() === 'ROLES_HOLDER')
                });
            } catch (err) {
                console.error("Error loading masters", err);
            }
        };
        loadInitialData();
    }, []);

    useEffect(() => {
        if (!formData.productId) { setSubproducts([]); return; }
        api.get(`/api/v1/management/subproducts/product/${formData.productId}`)
            .then(res => setSubproducts(res.data.content || res.data))
            .catch(err => console.error(err));
        setFormData(prev => ({ ...prev, subproductId: '' }));
    }, [formData.productId]);

    const closeAndResetModal = () => {
        setShowModal(false);
        setHolderSearchDoc('');
        setTempHolder(null);
        setSelectedRole('');
    };

    const resetForm = () => {
        setFormData({
            branchId: userData.branchId || '',
            customerId: '',
            productId: '',
            subproductId: '',
            currencyId: '',
            ownershipTypeId: '',
            createdBy: userData.login || 'system',
            holders: []
        });
        setMainCustomer(null);
        setSearchDoc('');
        setSubproducts([]);
    };

    const searchMainCustomer = async () => {
        if (!searchDoc.trim()) {
            Swal.fire({
                icon: 'warning',
                title: 'Attention',
                text: 'Identification number is mandatory for searching.',
                confirmButtonColor: '#334155'
            });
            return;
        }
        setLoading(true);
        try {
            const res = await api.get(`/api/v1/customers/document/${searchDoc}`);
            const fullName = `${res.data.firstName} ${res.data.lastName}`;
            setMainCustomer({ ...res.data, fullName });

            const ownerRole = catalogs.holderRoles.find(r => r.name?.toUpperCase() === 'OWNER');

            setFormData(prev => ({
                ...prev,
                customerId: res.data.id,
                holders: [{
                    customerId: res.data.id,
                    fullName,
                    roleId: ownerRole ? ownerRole.id : null,
                    roleName: 'OWNER',
                    isMain: true
                }]
            }));
        } catch (err) {
            Swal.fire('Error', 'Customer not found', 'error');
        } finally { setLoading(false); }
    };

    const searchAdditionalHolder = async () => {
        if (!holderSearchDoc.trim()) {
            Swal.fire({ icon: 'warning', title: 'Attention', text: 'Please enter an ID number.', confirmButtonColor: '#334155' });
            return;
        }
        try {
            const res = await api.get(`/api/v1/customers/document/${holderSearchDoc}`);
            setTempHolder({ ...res.data, fullName: `${res.data.firstName} ${res.data.lastName}` });
        } catch (err) {
            Swal.fire('Not Found', 'The additional customer does not exist', 'info');
        }
    };

    const addHolderToTable = () => {
        if (!tempHolder || !selectedRole) {
            Swal.fire('Attention', 'Please select a customer and a role', 'warning');
            return;
        }

        if (formData.holders.find(h => String(h.customerId) === String(tempHolder.id))) {
            Swal.fire('Error', 'This customer has already been added', 'error');
            return;
        }

        const roleObj = catalogs.holderRoles.find(r => String(r.id) === String(selectedRole));

        setFormData(prev => ({
            ...prev,
            holders: [...prev.holders, {
                customerId: tempHolder.id,
                fullName: tempHolder.fullName,
                roleId: selectedRole,
                roleName: roleObj ? roleObj.name : 'N/A'
            }]
        }));
        closeAndResetModal();
    };

    const removeHolder = (id) => {
        setFormData(prev => ({ ...prev, holders: prev.holders.filter(h => h.customerId !== id || h.isMain) }));
    };

    const isJoint = (() => {
        if (!formData.ownershipTypeId) return false;
        const typeSelected = catalogs.ownershipTypes.find(t => String(t.id) === String(formData.ownershipTypeId));
        const typeName = typeSelected?.name?.toUpperCase() || '';
        return typeName.includes('JOINT') || typeName.includes('CONJUNTA');
    })();

    const handleSaveAccount = async (e) => {
        if (e) e.preventDefault();

        // VALIDACIONES CAMPOS MANDATORIOS CON MENSAJES PERSONALIZADOS
        if (!formData.customerId) {
            Swal.fire({ icon: 'warning', title: 'Missing Main Holder', text: 'You must search for and select a main customer using their ID.', confirmButtonColor: '#334155' });
            return;
        }
        if (!formData.ownershipTypeId) {
            Swal.fire({ icon: 'warning', title: 'Missing Ownership Type', text: 'Please select an ownership type for the account.', confirmButtonColor: '#334155' });
            return;
        }
        if (!formData.currencyId) {
            Swal.fire({ icon: 'warning', title: 'Missing Currency', text: 'Please select the account currency.', confirmButtonColor: '#334155' });
            return;
        }
        if (!formData.productId) {
            Swal.fire({ icon: 'warning', title: 'Missing Product', text: 'Please select a financial product.', confirmButtonColor: '#334155' });
            return;
        }
        if (!formData.subproductId) {
            Swal.fire({ icon: 'warning', title: 'Missing Subproduct', text: 'Please select a subproduct.', confirmButtonColor: '#334155' });
            return;
        }

        // VALIDACIÓN CUENTA CONJUNTA (JOINT)
        if (isJoint) {
            const hasExtraHolder = formData.holders.some(h =>
                !h.isMain && (h.roleName?.toUpperCase() === 'SIGNATORY' || h.roleName?.toUpperCase() === 'CO-OWNER')
            );
            if (!hasExtraHolder) {
                Swal.fire({
                    icon: 'warning',
                    title: 'Joint Account Requirement',
                    text: 'Joint accounts require at least one additional Co-owner or Signatory.',
                    confirmButtonColor: '#334155'
                });
                return;
            }
        }

        setLoading(true);
        try {
            const requestBody = {
                branchId: parseInt(formData.branchId),
                customerId: formData.customerId,
                subproductId: parseInt(formData.subproductId),
                currencyId: parseInt(formData.currencyId),
                ownershipTypeId: parseInt(formData.ownershipTypeId),
                createdBy: formData.createdBy,
                holders: formData.holders.map(h => ({
                    customerId: h.customerId,
                    ownershipTypeCode: h.roleName // El BE espera el código del rol (ej: 'OWNER', 'SIGNATORY')
                }))
            };
            const response = await api.post('/api/v1/accounts', requestBody);
            if (response.status === 201 || response.status === 200) {
                const newAccountData = response.data;
                await Swal.fire({
                    icon: 'success',
                    title: '¡Account Created!',
                    text: `Account number has been created: ${newAccountData.accountNumber}`,
                    confirmButtonColor: '#334155'
                });

                // DISPARAMOS EL PDF JUSTO AQUÍ ANTES DE LIMPIAR EL FORMULARIO
                // Asumimos que response.data contiene el número de cuenta generado
                try {
                    generateContractPDF(newAccountData);
                } catch (pdfError) {
                    console.error("Error al generar el PDF:", pdfError);
                    Swal.fire({
                        icon: 'info',
                        title: 'Account created, but...',
                        text: 'The PDF could not be opened automatically. Please check that the libraries are installed',
                    });
                }

                resetForm();
            }
        } catch (err) {
            const detail = err.response?.data?.message || "Persistence error";
            Swal.fire('Core Error', detail, 'error');
        } finally {
            setLoading(false);
        }
    };

    const getBadgeClass = (holder) => {
        if (holder.isMain) return 'bg-primary';
        if (holder.roleName?.toUpperCase() === 'SIGNATORY') return 'bg-warning text-dark';
        return 'bg-info';
    };

    return (
        <div className="ig-container" style={{ maxWidth: '1200px', margin: '0 auto', padding: '10px 0' }}>
            <h3 className="ig-card-title" style={{ marginBottom: '10px', fontSize: '1.2rem' }}>🆕 Account Opening</h3>

            <div
                className="alert alert-info d-flex align-items-center mb-3 shadow-sm border-0"
                style={{
                    borderRadius: '10px',
                    maxWidth: '1200px',
                    margin: '0 auto 12px auto',
                    padding: '8px 15px',
                    boxSizing: 'border-box'
                }}
            >
                <MapPin size={18} className="me-2 text-primary" />
                <div>
                    <small className="d-block text-muted text-uppercase fw-bold" style={{ fontSize: '0.6rem' }}>Active Branch</small>
                    <span className="fw-bold" style={{ fontSize: '0.9rem' }}>{userData.branchName || 'Unknown Branch'}</span>
                </div>
            </div>

            <div className="ig-card" style={{ marginBottom: '12px', padding: '12px 20px', maxWidth: '1200px', margin: '0 auto 12px auto' }}>
                <label className="ig-label" style={{ marginBottom: '4px' }}>1. Main Holder Identification <span className="text-danger">*</span></label>
                <div style={{ display: 'flex', gap: '10px' }}>
                    <input className="ig-input" style={{ height: '38px' }} placeholder="ID Number..." value={searchDoc} onChange={e => setSearchDoc(e.target.value)} onKeyPress={e => e.key === 'Enter' && searchMainCustomer()} />
                    <button
                        type="button"
                        className="ig-btn-primary d-flex align-items-center justify-content-center"
                        style={{ backgroundColor: '#334155', width: '120px', height: '38px', lineHeight: '1' }}
                        onClick={searchMainCustomer}
                        disabled={loading}
                    >
                        Search
                    </button>
                </div>
                {mainCustomer && <div className="mt-1 text-success fw-bold" style={{ fontSize: '0.85rem' }}>Holder: {mainCustomer.fullName}</div>}
            </div>

            <form className="ig-card" style={{ padding: '15px 20px', maxWidth: '1200px', margin: '0 auto' }} onSubmit={handleSaveAccount} noValidate>
                <div className="ig-grid" style={{ gap: '10px 20px' }}>
                    <div className="ig-field">
                        <label className="ig-label" style={{ marginBottom: '4px' }}>Ownership Type <span className="text-danger">*</span></label>
                        <select className="ig-select" style={{ height: '38px', padding: '4px 10px', lineHeight: 'normal' }} value={formData.ownershipTypeId} onChange={e => setFormData({...formData, ownershipTypeId: e.target.value})}>
                            <option value="">Select...</option>
                            {catalogs.ownershipTypes.map(t => <option key={t.id} value={t.id}>{t.name}</option>)}
                        </select>
                    </div>

                    <div className="ig-field">
                        <label className="ig-label" style={{ marginBottom: '4px' }}>Currency <span className="text-danger">*</span></label>
                        <select className="ig-select" style={{ height: '38px', padding: '4px 10px', lineHeight: 'normal' }} value={formData.currencyId} onChange={e => setFormData({...formData, currencyId: e.target.value})}>
                            <option value="">Select...</option>
                            {catalogs.currencies.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}
                        </select>
                    </div>

                    <div className="ig-field">
                        <label className="ig-label" style={{ marginBottom: '4px' }}>Product <span className="text-danger">*</span></label>
                        <select className="ig-select" style={{ height: '38px', padding: '4px 10px', lineHeight: 'normal' }} value={formData.productId} onChange={e => setFormData({...formData, productId: e.target.value})}>
                            <option value="">Select...</option>
                            {products.map(p => <option key={p.id} value={p.id}>{p.name}</option>)}
                        </select>
                    </div>

                    <div className="ig-field">
                        <label className="ig-label" style={{ marginBottom: '4px' }}>Subproduct <span className="text-danger">*</span></label>
                        <select className="ig-select" style={{ height: '38px', padding: '4px 10px', lineHeight: 'normal' }} value={formData.subproductId} onChange={e => setFormData({...formData, subproductId: e.target.value})}>
                            <option value="">Select...</option>
                            {subproducts.map(s => <option key={s.id} value={s.id}>{s.name}</option>)}
                        </select>
                    </div>
                </div>

                <div style={{ marginTop: '12px', padding: '10px 15px', border: '1px solid #e2e8f0', borderRadius: '8px' }}>
                    <div className="d-flex justify-content-between align-items-center mb-2">
                        <h6 className="fw-bold mb-0 d-flex align-items-center" style={{ fontSize: '0.9rem' }}>
                            <Users size={18} className="me-2 text-secondary"/>
                            Account Holders
                        </h6>
                        {isJoint && (
                            <button
                                type="button"
                                className="ig-btn-primary d-flex align-items-center justify-content-center"
                                style={{ width: 'auto', padding: '5px 15px', fontSize: '0.85rem', backgroundColor: '#334155', height: '32px' }}
                                onClick={() => setShowModal(true)}
                            >
                                <UserPlus size={16} className="me-2"/> Add
                            </button>
                        )}
                    </div>
                    <table className="table table-sm custom-table mb-0">
                        <thead><tr><th>Name</th><th>Role</th><th>Action</th></tr></thead>
                        <tbody>
                            {formData.holders.map(h => (
                                <tr key={h.customerId}>
                                    <td>{h.fullName}</td>
                                    <td><span className={`badge ${getBadgeClass(h)}`} style={{ fontSize: '0.7rem' }}>{h.roleName}</span></td>
                                    <td>{!h.isMain && <Trash2 size={14} className="text-danger pointer" onClick={() => removeHolder(h.customerId)}/>}</td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>

                <button
                    type="submit"
                    className="ig-btn-success d-flex align-items-center justify-content-center"
                    style={{ marginTop: '15px', backgroundColor: '#334155', height: '42px', width: '100%' }}
                    disabled={loading}
                >
                    {loading ? 'Processing...' : 'OPEN ACCOUNT'}
                </button>
            </form>

            {showModal && (
                <div className="custom-modal-overlay">
                    <div className="custom-modal-content" style={{ padding: '20px', width: '400px' }}>
                        <h5 style={{ fontSize: '1.1rem' }}>Add Additional Holder</h5>
                        <hr style={{ margin: '10px 0' }} />
                        <div className="mb-2">
                            <label className="ig-label">Search by ID <span className="text-danger">*</span></label>
                            <div className="d-flex gap-2">
                                <input className="ig-input" style={{ height: '36px' }} value={holderSearchDoc} onChange={e => setHolderSearchDoc(e.target.value)} onKeyPress={e => e.key === 'Enter' && searchAdditionalHolder()} />
                                <button type="button" className="ig-btn-primary d-flex align-items-center justify-content-center" style={{ backgroundColor: '#334155', width: '45px', height: '36px' }} onClick={searchAdditionalHolder}>🔍</button>
                            </div>
                        </div>
                        {tempHolder && <div className="alert alert-secondary p-2 small mb-2" style={{ fontSize: '0.8rem' }}><strong>Customer:</strong> {tempHolder.fullName}</div>}
                        <div className="mb-3">
                            <label className="ig-label">Account Role <span className="text-danger">*</span></label>
                            <select className="ig-select" style={{ height: '36px' }} value={selectedRole} onChange={e => setSelectedRole(e.target.value)}>
                                <option value="">Select Role...</option>
                                {catalogs.holderRoles.map(r => <option key={r.id} value={r.id}>{r.name}</option>)}
                            </select>
                        </div>
                        <div className="d-flex justify-content-end gap-2">
                            <button type="button" className="btn btn-sm btn-light" onClick={closeAndResetModal}>Cancel</button>
                            <button type="button" className="btn btn-sm btn-dark" style={{ backgroundColor: '#334155', border: 'none' }} onClick={addHolderToTable}>Add</button>
                        </div>
                    </div>
                </div>
            )}
            <style>{`
                .custom-modal-overlay { position: fixed; top:0; left:0; width:100%; height:100%; background: rgba(0,0,0,0.5); display:flex; align-items:center; justify-content:center; z-index:1000; }
                .custom-modal-content { background: white; border-radius: 12px; }
                .pointer { cursor: pointer; }
                .custom-table { font-size: 0.8rem; }
                .custom-table th { padding: 8px 4px; }
                .custom-table td { padding: 6px 4px; vertical-align: middle; }

                .ig-select {
                    padding: 0 10px !important;
                    line-height: 1 !important;
                    display: flex;
                    align-items: center;
                }
            `}</style>
        </div>
    );
};

export default AccountCreate;
