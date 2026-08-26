import React, { useState, useEffect } from 'react';
import axios from 'axios';
import api from '../api/axiosConfig';
import { useNavigate } from 'react-router-dom';
import { Container, Row, Col, Form, Button, InputGroup, Spinner } from 'react-bootstrap';
import Swal from 'sweetalert2';

const Login = () => {
    const [login, setLogin] = useState('');
    const [password, setPassword] = useState('');
    const [branchId, setBranchId] = useState('');
    const [branches, setBranches] = useState([]);
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();

    useEffect(() => {
        const loadBranches = async () => {
            try {
                const baseURL = api.defaults.baseURL || 'http://localhost:8080';
                const finalURL = `${baseURL}/api/v1/branches/branches`;
                const res = await axios.get(finalURL);
                const data = res.data.content || (Array.isArray(res.data) ? res.data : []);
                setBranches(data);
            } catch (err) {
                console.error("Error loading branches:", err);
            }
        };
        loadBranches();
    }, []);

    const manejarEnvio = async (e) => {
        e.preventDefault();

        if (!login.trim()) {
            Swal.fire({
                icon: 'warning',
                title: '<span style="font-size: 1.1rem">User Required</span>',
                text: 'Please enter your User ID to continue.',
                confirmButtonColor: '#0f172a',
                width: '320px',
                customClass: { popup: 'rounded-4 shadow' }
            });
            return;
        }

        if (!password.trim()) {
            Swal.fire({
                icon: 'warning',
                title: '<span style="font-size: 1.1rem">Password Required</span>',
                text: 'Please enter your password.',
                confirmButtonColor: '#0f172a',
                width: '320px',
                customClass: { popup: 'rounded-4 shadow' }
            });
            return;
        }

        if (!branchId) {
            Swal.fire({
                icon: 'warning',
                title: '<span style="font-size: 1.1rem">Branch Required</span>',
                text: 'Please select a branch office.',
                confirmButtonColor: '#0f172a',
                width: '320px',
                customClass: { popup: 'rounded-4 shadow' }
            });
            return;
        }

        setLoading(true);
        try {
            const respuesta = await api.post('/api/v1/users/login', { login, password, branchId });
            const { token, firstName, lastName, roles, businessDate, systemStatus, branchName } = respuesta.data;

            localStorage.setItem('token', token);
            localStorage.setItem('user', JSON.stringify({
                nombre: `${firstName} ${lastName}`,
                permisos: roles,
                businessDate,
                systemStatus,
                login,
                branchId,
                branchName
            }));

            Swal.fire({ icon: 'success', title: 'Welcome', text: `Access granted: ${firstName}`, showConfirmButton: false, timer: 1200 });
            setTimeout(() => { navigate('/dashboard'); }, 1500);
        } catch (error) {
            const msg = error.response?.data?.message || "Login failed.";
            Swal.fire({ icon: 'error', title: 'Attention', text: msg, confirmButtonColor: '#0f172a' });
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="login-main-wrapper">
            <Container>
                <Row className="justify-content-center align-items-center" style={{ minHeight: '100vh' }}>
                    <Col md={10} lg={9} className="d-flex shadow-lg p-0" style={{ borderRadius: '24px', overflow: 'hidden', border: '1px solid rgba(255,255,255,0.1)' }}>

                        {/* SECCIÓN IZQUIERDA: BRANDING */}
                        <Col md={6} className="d-none d-md-flex flex-column justify-content-center align-items-center text-white p-5 branding-section">
                            <div className="visual-element">
                                <div className="glow-ring"></div>
                                <img src="https://cdn-icons-png.flaticon.com/512/2092/2092663.png" alt="Logo" style={{ width: '90px', filter: 'brightness(0) invert(1)', zIndex: 2 }} />
                            </div>
                            <div className="text-center" style={{ zIndex: 1 }}>
                                <h1 className="display-4 fw-bold mb-0" style={{ color: '#60a5fa' }}>CPA</h1>
                                <p className="h6 mb-3 text-uppercase fw-bold" style={{ letterSpacing: '5px', color: '#94a3b8' }}>Fintech Systems</p>
                                <div className="blue-separator"></div>
                                <p className="px-4 mt-3 branding-legend">
                                    Comprehensive banking management platform and highly available financial solutions for the global market.
                                </p>
                            </div>
                            <div className="dot-grid"></div>
                        </Col>

                        {/* SECCIÓN DERECHA: FORMULARIO */}
                        <Col xs={12} md={6} className="bg-white p-5 d-flex flex-column justify-content-center">
                            <div className="mb-4 text-center">
                                <h2 className="fw-bold mb-1" style={{ color: '#0f172a' }}>Sign In</h2>
                                <p className="text-muted small">Access your management console</p>
                            </div>

                            <Form onSubmit={manejarEnvio} noValidate>
                                <Form.Group className="mb-3">
                                    <Form.Label className="small fw-bold text-uppercase" style={{ color: '#64748b' }}>User ID</Form.Label>
                                    <InputGroup className="border rounded-3 overflow-hidden">
                                        <InputGroup.Text className="bg-white border-0 text-muted">👤</InputGroup.Text>
                                        <Form.Control
                                            type="text"
                                            placeholder="Enter your user"
                                            className="border-0 py-2 shadow-none"
                                            value={login}
                                            onChange={(e) => setLogin(e.target.value)}
                                        />
                                    </InputGroup>
                                </Form.Group>

                                <Form.Group className="mb-3">
                                    <Form.Label className="small fw-bold text-uppercase" style={{ color: '#64748b' }}>Password</Form.Label>
                                    <InputGroup className="border rounded-3 overflow-hidden">
                                        <InputGroup.Text className="bg-white border-0 text-muted">🔒</InputGroup.Text>
                                        <Form.Control
                                            type="password"
                                            placeholder="••••••••"
                                            className="border-0 py-2 shadow-none"
                                            value={password}
                                            onChange={(e) => setPassword(e.target.value)}
                                        />
                                    </InputGroup>
                                </Form.Group>

                                <Form.Group className="mb-4">
                                    <Form.Label className="small fw-bold text-uppercase" style={{ color: '#64748b' }}>Branch Office</Form.Label>
                                    <InputGroup className="border rounded-3 overflow-hidden">
                                        <InputGroup.Text className="bg-white border-0 text-muted">🏢</InputGroup.Text>
                                        <Form.Select
                                            className="border-0 py-2 shadow-none"
                                            value={branchId}
                                            onChange={(e) => setBranchId(e.target.value)}
                                        >
                                            <option value="">Select Branch...</option>
                                            {branches.map(b => (
                                                <option key={b.id} value={b.id}>{b.code ? `${b.code} - ` : ''}{b.name}</option>
                                            ))}
                                        </Form.Select>
                                    </InputGroup>
                                </Form.Group>

                                <Button variant="dark" type="submit" className="w-100 py-2 fw-bold shadow-sm"
                                        style={{ backgroundColor: '#0f172a', borderRadius: '10px' }}
                                        disabled={loading}>
                                    {loading ? <Spinner animation="border" size="sm" /> : "LOG IN"}
                                </Button>
                            </Form>

                            <div className="mt-5 text-center">
                                <div className="fw-bold small" style={{ borderBottom: '2px solid #60a5fa', display: 'inline-block', color: '#0f172a' }}>
                                    Christian Padilla Ochoa
                                </div>
                                <div className="mt-2 text-muted" style={{ fontSize: '0.65rem' }}>
                                    © 2026 CPA GLOBAL FINTECH v1.2.5
                                </div>
                            </div>
                        </Col>
                    </Col>
                </Row>
            </Container>
        </div>
    );
};

export default Login;
