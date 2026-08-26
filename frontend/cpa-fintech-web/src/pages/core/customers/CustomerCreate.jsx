import React, { useState, useEffect } from 'react';
import api from '../../../api/axiosConfig';
import { Card, Form, Button, Row, Col, Spinner, Table } from 'react-bootstrap';
import { User, MapPin, Phone, Mail, CheckCircle, Plus, Trash2, Shield } from 'lucide-react';
import Swal from 'sweetalert2';

const CustomerCreate = ({ onBack }) => {
    const [loading, setLoading] = useState(false);
    const [step, setStep] = useState(1);

    // Color constante para reusar en Swal y botones
    const PRIMARY_COLOR = '#334155';

    const currentUser = (() => {
        const savedUser = localStorage.getItem('user');
        try {
            return savedUser ? JSON.parse(savedUser).login : 'system_user';
        } catch { return 'system_user'; }
    })();

    const [catalogs, setCatalogs] = useState({
        documentTypes: [], genders: [], maritalStatuses: [], nationalities: [],
        addressTypes: [], phoneTypes: [], emailTypes: [], countries: []
    });

    const [customerData, setCustomerData] = useState({
        firstName: '', lastName: '', documentTypeId: '',
        documentNumber: '', birthDate: '', genderId: '',
        maritalStatusId: '', nationalityId: '',
        statusId: '', createdBy: currentUser,
        addresses: [],
        phones: [],
        emails: []
    });

    const [tempAddress, setTempAddress] = useState({
        addressLine: '', city: '', state: '', countryId: '',
        postalCode: '', addressTypeId: '', isPrimary: false
    });
    const [tempPhone, setTempPhone] = useState({
        phoneNumber: '', phoneTypeId: '', isPrimary: false
    });
    const [tempEmail, setTempEmail] = useState({
        email: '', emailTypeId: '', isPrimary: false
    });

    const resetForm = () => {
        setCustomerData({
            firstName: '', lastName: '', documentTypeId: '',
            documentNumber: '', birthDate: '', genderId: '',
            maritalStatusId: '', nationalityId: '',
            statusId: customerData.statusId,
            createdBy: currentUser,
            addresses: [],
            phones: [],
            emails: []
        });
        setTempAddress({ addressLine: '', city: '', state: '', countryId: '', postalCode: '', addressTypeId: '', isPrimary: false });
        setTempPhone({ phoneNumber: '', phoneTypeId: '', isPrimary: false });
        setTempEmail({ email: '', emailTypeId: '', isPrimary: false });
        setStep(1);
        setLoading(false);
    };

    useEffect(() => {
        const loadInitialData = async () => {
            try {
                const res = await api.get('/api/v1/catalogs/all');
                const data = res.data;
                setCatalogs({
                    documentTypes: data.filter(c => c.catalogName?.toUpperCase() === 'IDENTIFICATION_TYPE'),
                    genders: data.filter(c => c.catalogName?.toUpperCase() === 'GENDER'),
                    maritalStatuses: data.filter(c => c.catalogName?.toUpperCase() === 'MARITAL_STATUS'),
                    nationalities: data.filter(c => c.catalogName?.toUpperCase() === 'NATIONALITY'),
                    addressTypes: data.filter(c => c.catalogName?.toUpperCase() === 'ADDRESS_TYPE'),
                    phoneTypes: data.filter(c => c.catalogName?.toUpperCase() === 'PHONE_TYPE'),
                    emailTypes: data.filter(c => c.catalogName?.toUpperCase() === 'EMAIL_TYPE'),
                    countries: data.filter(c => c.catalogName?.toUpperCase() === 'COUNTRY'),
                });
                const activeStatus = data.find(c => c.catalogName?.toUpperCase() === 'CUSTOMER_STATUS' && (c.name?.toUpperCase() === 'ACTIVE' || c.name?.toUpperCase() === 'ACTIVO'));
                if (activeStatus) setCustomerData(prev => ({ ...prev, statusId: activeStatus.id }));
            } catch (err) { console.error("Error loading catalogs", err); }
        };
        loadInitialData();
    }, []);

    const handleNextStep = (e) => {
        e.preventDefault();
        const swalConfig = { confirmButtonColor: PRIMARY_COLOR };

        if (!customerData.firstName) return Swal.fire({...swalConfig, icon: 'warning', title: 'Field Required', text: 'Please enter the First Name'});
        if (!customerData.lastName) return Swal.fire({...swalConfig, icon: 'warning', title: 'Field Required', text: 'Please enter the Last Name'});
        if (!customerData.documentTypeId) return Swal.fire({...swalConfig, icon: 'warning', title: 'Field Required', text: 'Please select an ID Type'});
        if (!customerData.documentNumber) return Swal.fire({...swalConfig, icon: 'warning', title: 'Field Required', text: 'Please enter the ID Number'});
        if (!customerData.birthDate) return Swal.fire({...swalConfig, icon: 'warning', title: 'Field Required', text: 'Please select a Birth Date'});
        if (!customerData.genderId) return Swal.fire({...swalConfig, icon: 'warning', title: 'Field Required', text: 'Please select a Gender'});
        if (!customerData.maritalStatusId) return Swal.fire({...swalConfig, icon: 'warning', title: 'Field Required', text: 'Please select a Marital Status'});
        if (!customerData.nationalityId) return Swal.fire({...swalConfig, icon: 'warning', title: 'Field Required', text: 'Please select a Nationality'});
        setStep(2);
    };

    const handleFinalSave = async () => {
        const swalConfig = { confirmButtonColor: PRIMARY_COLOR };

        if (customerData.addresses.length === 0) return Swal.fire({...swalConfig, icon: 'warning', title: 'Action Required', text: 'Please register at least one Address.'});
        if (customerData.phones.length === 0) return Swal.fire({...swalConfig, icon: 'warning', title: 'Action Required', text: 'Please register at least one Phone Number.'});
        if (customerData.emails.length === 0) return Swal.fire({...swalConfig, icon: 'warning', title: 'Action Required', text: 'Please register at least one Email Address.'});

        setLoading(true);
        try {
            const response = await api.post('/api/v1/customers', customerData);
            if (response.status === 201 || response.status === 200) {
                await Swal.fire({ ...swalConfig, icon: 'success', title: 'Success!', text: 'The customer has been created successfully.', timer: 2000, showConfirmButton: false });
                resetForm();
            }
        } catch (err) {
            setLoading(false);
            Swal.fire({ ...swalConfig, icon: 'error', title: 'Save Error', text: err.response?.data?.message || 'Server communication error.' });
        }
    };

    return (
        <div className="container-fluid py-4" style={{ backgroundColor: '#f1f5f9', minHeight: '100vh' }}>

            <div className="d-flex justify-content-center mb-4 mt-2">
                <div className="text-center px-4 border-end">
                    <div className="rounded-circle d-flex align-items-center justify-content-center mx-auto mb-1 shadow-sm fw-bold"
                         style={{ width: '45px', height: '45px', backgroundColor: step === 1 ? PRIMARY_COLOR : '#10b981', color: 'white' }}>
                        {step === 1 ? '1' : <CheckCircle size={24} />}
                    </div>
                    <small className="fw-bold text-muted" style={{ fontSize: '0.75rem' }}>BASIC DATA</small>
                </div>
                <div className="text-center px-4">
                    <div className="rounded-circle d-flex align-items-center justify-content-center mx-auto mb-1 shadow-sm fw-bold"
                         style={{ width: '45px', height: '45px', backgroundColor: step === 2 ? PRIMARY_COLOR : '#e2e8f0', color: step === 2 ? 'white' : '#94a3b8' }}>
                        2
                    </div>
                    <small className="fw-bold text-muted" style={{ fontSize: '0.75rem' }}>CONTACT INFO</small>
                </div>
            </div>

            <Card className="shadow border-0 mx-auto" style={{ borderRadius: '15px', maxWidth: '1100px' }}>
                <Card.Header style={{ backgroundColor: PRIMARY_COLOR, color: 'white' }} className="py-3 px-4">
                    <h6 className="mb-0 fw-bold">CUSTOMER ONBOARDING SYSTEM</h6>
                </Card.Header>

                <Card.Body className="p-4">
                    {step === 1 ? (
                        <Form onSubmit={handleNextStep} noValidate>
                            <h6 className="text-primary mb-4 border-bottom pb-2 fw-bold d-flex align-items-center"><User size={18} className="me-2"/>Primary Profile</h6>
                            <Row className="g-3 mb-4">
                                <Col md={6}><Form.Label className="small fw-bold">First Name <span className="text-danger">*</span></Form.Label><Form.Control value={customerData.firstName} onChange={e => setCustomerData({...customerData, firstName: e.target.value.toUpperCase()})} required /></Col>
                                <Col md={6}><Form.Label className="small fw-bold">Last Name <span className="text-danger">*</span></Form.Label><Form.Control value={customerData.lastName} onChange={e => setCustomerData({...customerData, lastName: e.target.value.toUpperCase()})} required /></Col>
                                <Col md={4}><Form.Label className="small fw-bold">ID Type <span className="text-danger">*</span></Form.Label>
                                    <Form.Select value={customerData.documentTypeId} onChange={e => setCustomerData({...customerData, documentTypeId: e.target.value})} required>
                                        <option value="">Select...</option>{catalogs.documentTypes.map(d => <option key={d.id} value={d.id}>{d.name}</option>)}
                                    </Form.Select>
                                </Col>
                                <Col md={4}><Form.Label className="small fw-bold">ID Number <span className="text-danger">*</span></Form.Label><Form.Control value={customerData.documentNumber} onChange={e => setCustomerData({...customerData, documentNumber: e.target.value})} required /></Col>
                                <Col md={4}><Form.Label className="small fw-bold">Birth Date <span className="text-danger">*</span></Form.Label><Form.Control type="date" value={customerData.birthDate} onChange={e => setCustomerData({...customerData, birthDate: e.target.value})} required /></Col>
                                <Col md={4}><Form.Label className="small fw-bold">Gender <span className="text-danger">*</span></Form.Label>
                                    <Form.Select value={customerData.genderId} onChange={e => setCustomerData({...customerData, genderId: e.target.value})}>
                                        <option value="">Select...</option>{catalogs.genders.map(g => <option key={g.id} value={g.id}>{g.name}</option>)}
                                    </Form.Select>
                                </Col>
                                <Col md={4}><Form.Label className="small fw-bold">Marital Status <span className="text-danger">*</span></Form.Label>
                                    <Form.Select value={customerData.maritalStatusId} onChange={e => setCustomerData({...customerData, maritalStatusId: e.target.value})} required>
                                        <option value="">Select...</option>{catalogs.maritalStatuses.map(m => <option key={m.id} value={m.id}>{m.name}</option>)}
                                    </Form.Select>
                                </Col>
                                <Col md={4}><Form.Label className="small fw-bold">Nationality <span className="text-danger">*</span></Form.Label>
                                    <Form.Select value={customerData.nationalityId} onChange={e => setCustomerData({...customerData, nationalityId: e.target.value})}>
                                        <option value="">Select...</option>{catalogs.nationalities.map(n => <option key={n.id} value={n.id}>{n.name}</option>)}
                                    </Form.Select>
                                </Col>
                            </Row>
                            <div className="d-flex justify-content-end mt-4 pt-3 border-top">
                                <Button type="submit" style={{backgroundColor: PRIMARY_COLOR, border:'none'}} className="px-5 shadow text-white fw-bold">PROCEED TO CONTACT</Button>
                            </div>
                        </Form>
                    ) : (
                        <div className="animate-fade-in">
                            <h6 className="text-primary mb-3 border-bottom pb-1 fw-bold d-flex align-items-center small">
                                <Shield size={16} className="me-2"/> Contact Management: {customerData.firstName} {customerData.lastName}
                            </h6>

                            <Card className="mb-2 bg-light border-0 shadow-sm">
                                <Card.Body className="p-2">
                                    <h6 className="fw-bold mb-2 d-flex align-items-center" style={{fontSize: '0.75rem'}}><MapPin size={14} className="me-2 text-danger"/>ADDRESSES</h6>

                                    <Row className="g-1 mb-1">
                                        <Col md={5}><Form.Control size="sm" placeholder="Address Line" value={tempAddress.addressLine} onChange={e => setTempAddress({...tempAddress, addressLine: e.target.value.toUpperCase()})}/></Col>
                                        <Col md={2}><Form.Control size="sm" placeholder="City" value={tempAddress.city} onChange={e => setTempAddress({...tempAddress, city: e.target.value.toUpperCase()})}/></Col>
                                        <Col md={2}><Form.Control size="sm" placeholder="State/Prov" value={tempAddress.state} onChange={e => setTempAddress({...tempAddress, state: e.target.value.toUpperCase()})}/></Col>
                                        <Col md={3}><Form.Select size="sm" value={tempAddress.countryId} onChange={e => setTempAddress({...tempAddress, countryId: e.target.value})}><option value="">Country...</option>{catalogs.countries.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}</Form.Select></Col>
                                    </Row>

                                    <Row className="g-1 mb-2 align-items-center">
                                        <Col md={3}><Form.Select size="sm" value={tempAddress.addressTypeId} onChange={e => setTempAddress({...tempAddress, addressTypeId: e.target.value})}><option value="">Address Type...</option>{catalogs.addressTypes.map(t => <option key={t.id} value={t.id}>{t.name}</option>)}</Form.Select></Col>
                                        <Col md={6} className="ps-2">
                                            <Form.Check type="checkbox" label={<span style={{fontSize: '0.75rem'}}>Mark as Primary Address</span>} checked={tempAddress.isPrimary} onChange={e => setTempAddress({...tempAddress, isPrimary: e.target.checked})}/>
                                        </Col>
                                        <Col md={3}><Button variant="dark" size="sm" className="w-100 py-0 d-flex align-items-center justify-content-center fw-bold" onClick={() => {
                                                const countryObj = catalogs.countries.find(x => String(x.id) === String(tempAddress.countryId));
                                                if(tempAddress.addressLine && countryObj) {
                                                    setCustomerData({ ...customerData, addresses: [...customerData.addresses, {
                                                        addressLine: tempAddress.addressLine, city: tempAddress.city, state: tempAddress.state,
                                                        country: countryObj.name, addressTypeId: tempAddress.addressTypeId, isPrimary: tempAddress.isPrimary
                                                    }]});
                                                    setTempAddress({addressLine:'', city:'', state:'', countryId:'', postalCode:'', addressTypeId:'', isPrimary:false});
                                                } else { Swal.fire({confirmButtonColor: PRIMARY_COLOR, icon:'warning', title:'Attention', text:'Address and Country are required'}); }
                                            }}><Plus size={14} className="me-1"/> ADD ADDRESS</Button>
                                        </Col>
                                    </Row>

                                    <Table hover size="sm" className="bg-white mb-0 border" style={{fontSize: '0.75rem'}}>
                                        <thead className="table-light"><tr><th>Address</th><th>City</th><th>State</th><th>Country</th><th className="text-center">Action</th></tr></thead>
                                        <tbody>{customerData.addresses.length > 0 ? customerData.addresses.map((a, i) => <tr key={i}><td>{a.addressLine}</td><td>{a.city}</td><td>{a.state || 'N/A'}</td><td>{a.country}</td><td className="text-center"><Trash2 size={14} className="text-danger pointer" onClick={() => setCustomerData({...customerData, addresses: customerData.addresses.filter((_, idx)=>idx!==i)})}/></td></tr>) : <tr><td colSpan="5" className="text-center text-muted">No addresses added</td></tr>}</tbody>
                                    </Table>
                                </Card.Body>
                            </Card>

                            <Row className="g-2">
                                <Col lg={6}>
                                    <Card className="h-100 border-0 bg-light shadow-sm"><Card.Body className="p-2">
                                        <h6 className="fw-bold mb-2 d-flex align-items-center" style={{fontSize: '0.75rem'}}><Phone size={14} className="me-2 text-success"/>PHONES</h6>
                                        <div className="d-flex gap-1 mb-2">
                                            <Form.Control size="sm" placeholder="Number" value={tempPhone.phoneNumber} onChange={e => setTempPhone({...tempPhone, phoneNumber: e.target.value})}/><Form.Select size="sm" value={tempPhone.phoneTypeId} onChange={e => setTempPhone({...tempPhone, phoneTypeId: e.target.value})}><option value="">Type...</option>{catalogs.phoneTypes.map(t => <option key={t.id} value={t.id}>{t.name}</option>)}</Form.Select>
                                            <Button variant="success" size="sm" className="py-0 px-3 d-flex align-items-center fw-bold" onClick={() => {
                                                if(tempPhone.phoneNumber && tempPhone.phoneTypeId) {
                                                    setCustomerData({...customerData, phones: [...customerData.phones, {...tempPhone}]});
                                                    setTempPhone({phoneNumber:'', phoneTypeId:'', isPrimary:false});
                                                }
                                            }}><Plus size={14} className="me-1"/> ADD</Button>
                                        </div>
                                        <Table hover size="sm" className="bg-white mb-0 border" style={{fontSize: '0.75rem'}}>
                                            <thead className="table-light"><tr><th>Number</th><th>Type</th><th className="text-center">Action</th></tr></thead>
                                            <tbody>{customerData.phones.length > 0 ? customerData.phones.map((p, i) => (
                                                <tr key={i}><td>{p.phoneNumber}</td><td>{catalogs.phoneTypes.find(t => String(t.id) === String(p.phoneTypeId))?.name || 'N/A'}</td><td className="text-center"><Trash2 size={14} className="text-danger pointer" onClick={() => setCustomerData({...customerData, phones: customerData.phones.filter((_, idx)=>idx!==i)})}/></td></tr>
                                            )) : <tr><td colSpan="3" className="text-center text-muted">No phones added</td></tr>}</tbody>
                                        </Table>
                                    </Card.Body></Card>
                                </Col>
                                <Col lg={6}>
                                    <Card className="h-100 border-0 bg-light shadow-sm"><Card.Body className="p-2">
                                        <h6 className="fw-bold mb-2 d-flex align-items-center" style={{fontSize: '0.75rem'}}><Mail size={14} className="me-2 text-info"/>EMAILS</h6>
                                        <div className="d-flex gap-1 mb-2">
                                            <Form.Control size="sm" type="email" placeholder="Email" value={tempEmail.email} onChange={e => setTempEmail({...tempEmail, email: e.target.value.toLowerCase()})}/><Form.Select size="sm" value={tempEmail.emailTypeId} onChange={e => setTempEmail({...tempEmail, emailTypeId: e.target.value})}><option value="">Type...</option>{catalogs.emailTypes.map(t => <option key={t.id} value={t.id}>{t.name}</option>)}</Form.Select>
                                            <Button variant="info" size="sm" className="text-white py-0 px-3 d-flex align-items-center fw-bold" onClick={() => {
                                                if(tempEmail.email && tempEmail.emailTypeId) {
                                                    setCustomerData({...customerData, emails: [...customerData.emails, {...tempEmail}]});
                                                    setTempEmail({email:'', emailTypeId:'', isPrimary:false});
                                                }
                                            }}><Plus size={14} className="me-1"/> ADD</Button>
                                        </div>
                                        <Table hover size="sm" className="bg-white mb-0 border" style={{fontSize: '0.75rem'}}>
                                            <thead className="table-light"><tr><th>Email</th><th>Type</th><th className="text-center">Action</th></tr></thead>
                                            <tbody>{customerData.emails.length > 0 ? customerData.emails.map((m, i) => (
                                                <tr key={i}><td>{m.email}</td><td>{catalogs.emailTypes.find(t => String(t.id) === String(m.emailTypeId))?.name || 'N/A'}</td><td className="text-center"><Trash2 size={14} className="text-danger pointer" onClick={() => setCustomerData({...customerData, emails: customerData.emails.filter((_, idx)=>idx!==i)})}/></td></tr>
                                            )) : <tr><td colSpan="3" className="text-center text-muted">No emails added</td></tr>}</tbody>
                                        </Table>
                                    </Card.Body></Card>
                                </Col>
                            </Row>

                            <div className="d-flex justify-content-between mt-4 pt-2 border-top">
                                <Button variant="outline-dark" size="sm" onClick={() => setStep(1)} className="fw-bold px-4">EDIT PROFILE</Button>
                                <Button variant="success" size="sm" className="px-5 fw-bold shadow d-flex align-items-center" style={{backgroundColor: PRIMARY_COLOR, border: 'none'}} onClick={handleFinalSave} disabled={loading}>
                                    {loading ? <Spinner size="sm" className="me-2"/> : "SAVE CUSTOMER"}
                                    <CheckCircle size={18} className="ms-2"/>
                                </Button>
                            </div>
                        </div>
                    )}
                </Card.Body>
            </Card>
        </div>
    );
};

export default CustomerCreate;
