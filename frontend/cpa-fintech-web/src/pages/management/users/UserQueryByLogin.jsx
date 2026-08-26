import React, { useState } from 'react';
import api from '../../../api/axiosConfig';
import { Card, Form, Button, Row, Col, InputGroup, Spinner, Badge } from 'react-bootstrap';
import Swal from 'sweetalert2';

const UserQueryByLogin = () => {
    const [loginValue, setLoginValue] = useState('');
    const [loading, setLoading] = useState(false);
    const [userData, setUserData] = useState(null);

    const handleSearch = async (e) => {
        e.preventDefault();
        if (!loginValue.trim()) return;

        setLoading(true);
        setUserData(null);
        try {
            const response = await api.get(`/api/v1/users/by-login/${loginValue}`);
            setUserData(response.data);
        } catch (error) {
            console.error("Error al buscar usuario:", error);
            Swal.fire({
                icon: 'error',
                title: 'No encontrado',
                text: `No existe un colaborador con el login: ${loginValue}`,
                confirmButtonColor: '#0f172a'
            });
        } finally {
            setLoading(false);
        }
    };

    const styles = {
        avatarSquare: {
            width: '70px',
            height: '70px',
            backgroundColor: '#0f172a',
            color: 'white',
            borderRadius: '12px',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            fontSize: '1.5rem',
            fontWeight: 'bold',
            boxShadow: '0 4px 10px rgba(0,0,0,0.1)'
        }
    };

    return (
        <Card className="shadow-sm border-0" style={{ borderRadius: '15px', maxWidth: '700px', margin: '20px auto' }}>
            <Card.Header style={{ backgroundColor: '#0f172a', color: 'white' }} className="py-3 text-center">
                <h5 className="mb-0 text-uppercase small fw-bold" style={{ letterSpacing: '1px' }}>Consulta de Usuario</h5>
            </Card.Header>
            <Card.Body className="p-4">

                <Form onSubmit={handleSearch} className="mb-4 pb-4 border-bottom">
                    <Form.Label className="fw-bold text-muted small">IDENTIFICADOR (LOGIN / EMAIL)</Form.Label>
                    <Row className="align-items-center">
                        <Col md={9}>
                            <InputGroup>
                                <InputGroup.Text className="bg-light">@</InputGroup.Text>
                                <Form.Control
                                    placeholder="Ej: christian.padilla"
                                    value={loginValue}
                                    onChange={(e) => setLoginValue(e.target.value)}
                                    required
                                />
                            </InputGroup>
                        </Col>
                        <Col md={3}>
                            <Button variant="primary" type="submit" className="w-100 fw-bold shadow-sm" disabled={loading}>
                                {loading ? <Spinner size="sm" /> : "BUSCAR"}
                            </Button>
                        </Col>
                    </Row>
                </Form>

                {userData && (
                    <div className="animate__animated animate__fadeIn">
                        {/* PERFIL PRINCIPAL */}
                        <div className="d-flex align-items-center mb-4 p-3 bg-white rounded-3 border">
                            <div style={styles.avatarSquare}>
                                {userData.firstName.charAt(0)}{userData.lastName.charAt(0)}
                            </div>
                            <div className="ms-3 flex-grow-1">
                                <div className="d-flex align-items-center justify-content-between">
                                    <h4 className="mb-0 fw-bold">{userData.firstName} {userData.lastName}</h4>
                                    <Badge bg={userData.active ? "success" : "danger"} pill style={{fontSize: '0.65rem'}}>
                                        {userData.active ? "ACTIVO" : "INACTIVO"}
                                    </Badge>
                                </div>
                                <div className="text-muted small">{userData.email}</div>
                                <div className="text-primary x-small fw-bold mt-1" style={{fontSize: '0.75rem'}}>ID: {userData.id?.substring(0, 13)}...</div>
                            </div>
                        </div>

                        {/* PANEL DE UBICACIÓN ÚNICO */}
                        <Card className="border-0 bg-light shadow-sm">
                            <Card.Body className="p-4 text-center">
                                <div className="mb-2" style={{fontSize: '2rem'}}>🏦</div>
                                <h6 className="fw-bold text-muted small text-uppercase mb-3">Sucursal Asignada</h6>
                                <h3 className="text-dark mb-1">{userData.branch?.name || "Sin Sucursal"}</h3>
                                <p className="text-muted">
                                    <strong>Ciudad:</strong> {userData.branch?.city || "N/A"}
                                    <span className="mx-2">|</span>
                                    <strong>Código:</strong> {userData.branch?.code || "S/C"}
                                </p>
                            </Card.Body>
                        </Card>

                        <div className="text-center mt-4">
                            <Button
                                variant="outline-secondary"
                                size="sm"
                                className="border-0"
                                onClick={() => { setUserData(null); setLoginValue(''); }}
                            >
                                ← Nueva consulta
                            </Button>
                        </div>
                    </div>
                )}

                {!userData && !loading && (
                    <div className="text-center py-5 text-muted opacity-50">
                        <p className="mb-0 small italic">Ingrese las credenciales para localizar al colaborador en la red de agencias.</p>
                    </div>
                )}
            </Card.Body>
        </Card>
    );
};

export default UserQueryByLogin;
