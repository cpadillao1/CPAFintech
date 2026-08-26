import React, { useState, useEffect } from 'react';
import api from '../../../api/axiosConfig';
import { Card, Form, Button, Row, Col, InputGroup, Spinner } from 'react-bootstrap';
import Swal from 'sweetalert2';

const UserCreate = () => {
    const [loading, setLoading] = useState(false);
    const [roles, setRoles] = useState([]);
    const [branches, setBranches] = useState([]);

    const [formData, setFormData] = useState({
        firstName: '',
        lastName: '',
        email: '',
        password: '',
        roleNames: [], // Array según tu JSON de Postman
        branchCode: ''
    });

    // Cargar Roles y Sucursales al iniciar para los Selects
    useEffect(() => {
        const loadFormData = async () => {
            try {
                const [resRoles, resBranches] = await Promise.all([
                    api.get('/api/v1/roles'), // Ajusta estas rutas según tu API
                    api.get('/api/v1/branches?size=100')
                ]);
                setRoles(resRoles.data);
                // Si tu API de ramas devuelve un Page object, usa resBranches.data.content
                setBranches(resBranches.data.content || resBranches.data);
            } catch (error) {
                console.error("Error cargando catálogos:", error);
            }
        };
        loadFormData();
    }, []);

    const handleChange = (e) => {
        const { name, value } = e.target;
        if (name === "roleNames") {
            // Manejo para multiselect si fuera necesario, por ahora uno simple en array
            setFormData({ ...formData, [name]: [value] });
        } else {
            setFormData({ ...formData, [name]: value });
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);

        try {
            await api.post('/api/v1/users/register', formData);

            Swal.fire({
                icon: 'success',
                title: '<span style="font-size: 1.1rem">Usuario Creado</span>',
                text: 'El registro se completó exitosamente.',
                showConfirmButton: false,
                timer: 2000,
                width: '350px'
            });

            // Limpiar formulario
            setFormData({
                firstName: '', lastName: '', email: '',
                password: '', roleNames: [], branchCode: ''
            });

        } catch (error) {
            console.error("Error al registrar:", error);
            Swal.fire({
                icon: 'error',
                title: 'Error de Registro',
                text: error.response?.data?.message || 'No se pudo crear el usuario.',
                confirmButtonColor: '#0f172a'
            });
        } finally {
            setLoading(false);
        }
    };

    return (
        <Card className="shadow-sm border-0" style={{ borderRadius: '15px', maxWidth: '900px', margin: '20px auto' }}>
            <Card.Header style={{ backgroundColor: '#0f172a', color: 'white' }} className="py-3 text-center">
                <h5 className="mb-0">Registro de Nuevo Usuario</h5>
            </Card.Header>
            <Card.Body className="p-4">
                <Form onSubmit={handleSubmit}>
                    <Row className="g-3">
                        <Col md={6}>
                            <Form.Group>
                                <Form.Label className="small fw-bold">Nombre <span className="text-danger">*</span></Form.Label>
                                <Form.Control
                                    name="firstName"
                                    placeholder="Ej: Lorena"
                                    value={formData.firstName}
                                    onChange={handleChange}
                                    required
                                />
                            </Form.Group>
                        </Col>
                        <Col md={6}>
                            <Form.Group>
                                <Form.Label className="small fw-bold">Apellido <span className="text-danger">*</span></Form.Label>
                                <Form.Control
                                    name="lastName"
                                    placeholder="Ej: Padilla"
                                    value={formData.lastName}
                                    onChange={handleChange}
                                    required
                                />
                            </Form.Group>
                        </Col>
                        <Col md={6}>
                            <Form.Group>
                                <Form.Label className="small fw-bold">Email Corporativo <span className="text-danger">*</span></Form.Label>
                                <InputGroup>
                                    <InputGroup.Text>@</InputGroup.Text>
                                    <Form.Control
                                        type="email"
                                        name="email"
                                        placeholder="usuario@fintech.com"
                                        value={formData.email}
                                        onChange={handleChange}
                                        required
                                    />
                                </InputGroup>
                            </Form.Group>
                        </Col>
                        <Col md={6}>
                            <Form.Group>
                                <Form.Label className="small fw-bold">Contraseña Temporal <span className="text-danger">*</span></Form.Label>
                                <Form.Control
                                    type="password"
                                    name="password"
                                    placeholder="••••••••"
                                    value={formData.password}
                                    onChange={handleChange}
                                    required
                                />
                            </Form.Group>
                        </Col>

                        <Col md={6}>
                            <Form.Group>
                                <Form.Label className="small fw-bold text-primary">Rol Asignado <span className="text-danger">*</span></Form.Label>
                                <Form.Select
                                    name="roleNames"
                                    onChange={handleChange}
                                    required
                                    value={formData.roleNames[0] || ''}
                                >
                                    <option value="">Seleccione un rol...</option>
                                    {roles.map(role => (
                                        <option key={role.id} value={role.name}>{role.name}</option>
                                    ))}
                                    {/* Fallback manual por si tu API de roles no está lista */}
                                    <option value="ADMIN">ADMINISTRADOR</option>
                                    <option value="CONSULTOR">CONSULTOR</option>
                                </Form.Select>
                            </Form.Group>
                        </Col>

                        <Col md={6}>
                            <Form.Group>
                                <Form.Label className="small fw-bold text-primary">Sucursal de Origen <span className="text-danger">*</span></Form.Label>
                                <Form.Select
                                    name="branchCode"
                                    value={formData.branchCode}
                                    onChange={handleChange}
                                    required
                                >
                                    <option value="">Seleccione sucursal...</option>
                                    {branches.map(branch => (
                                        <option key={branch.code} value={branch.code}>
                                            {branch.code} - {branch.name}
                                        </option>
                                    ))}
                                </Form.Select>
                            </Form.Group>
                        </Col>
                    </Row>

                    <div className="mt-5 d-flex justify-content-end gap-3">
                        <Button variant="outline-secondary" type="button" onClick={() => window.history.back()}>
                            Cancelar
                        </Button>
                        <Button variant="primary" type="submit" className="px-5 fw-bold shadow-sm" disabled={loading}>
                            {loading ? <Spinner size="sm" /> : "REGISTRAR USUARIO"}
                        </Button>
                    </div>
                </Form>
            </Card.Body>
        </Card>
    );
};

export default UserCreate;
