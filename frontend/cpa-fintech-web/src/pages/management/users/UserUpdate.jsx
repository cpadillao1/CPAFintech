import React, { useState, useEffect } from 'react';
import api from '../../../api/axiosConfig';
import { Card, Form, Button, Row, Col, Spinner, InputGroup } from 'react-bootstrap';
import Swal from 'sweetalert2';

const UserUpdate = () => {
    const [searchLogin, setSearchLogin] = useState('');
    const [branches, setBranches] = useState([]);
    const [loading, setLoading] = useState(false);
    const [saving, setSaving] = useState(false);
    const [showPassword, setShowPassword] = useState(false);

    const [formData, setFormData] = useState({
        id: '',
        firstName: '',
        lastName: '',
        email: '',
        branchCode: '',
        password: '',
        active: true
    });

    useEffect(() => {
        const fetchBranches = async () => {
            try {
                const res = await api.get('/api/v1/branches');
                const data = Array.isArray(res.data) ? res.data : (res.data?.content || []);
                setBranches(data);
            } catch (err) {
                console.error("Error cargando sucursales:", err);
                setBranches([]);
            }
        };
        fetchBranches();
    }, []);

    const handleSearch = async (e) => {
        e.preventDefault();
        if (!searchLogin.trim()) return;

        setLoading(true);
        try {
            const res = await api.get(`/api/v1/users/by-login/${searchLogin}`);
            setFormData({
                id: res.data.id,
                firstName: res.data.firstName,
                lastName: res.data.lastName,
                email: res.data.email,
                branchCode: res.data.branch?.code || '',
                password: '',
                active: res.data.active
            });
        } catch (error) {
            Swal.fire({
                icon: 'error',
                title: 'No encontrado',
                text: 'El usuario no existe.',
                confirmButtonColor: '#0f172a'
            });
        } finally {
            setLoading(false);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setSaving(true);

        try {
            // 1. Creamos una copia limpia de los datos
            const dataToSend = { ...formData };

            // 2. 💡 AJUSTE CLAVE: Eliminamos la propiedad password si está vacía
            // Al hacer 'delete', la propiedad desaparece del JSON y el Backend
            // no intentará validar el @NotBlank
            if (!dataToSend.password || dataToSend.password.trim() === "") {
                delete dataToSend.password;
            }

            await api.put(`/api/v1/users/${formData.id}`, dataToSend);

            await Swal.fire({
                icon: 'success',
                title: '¡Actualizado!',
                text: 'Los cambios se guardaron correctamente.',
                confirmButtonColor: '#0f172a',
                timer: 2000
            });

            // Reset total
            setSearchLogin('');
            setFormData({ id: '', firstName: '', lastName: '', email: '', branchCode: '', password: '', active: true });

        } catch (error) {
            // Si el backend responde con errores de validación (como el del password)
            // intentamos capturar el mensaje específico.
            console.error("Error completo del servidor:", error.response?.data);

            const backendError = error.response?.data?.message ||
                                 (error.response?.data && typeof error.response.data === 'string' ? error.response.data : "Error al actualizar los datos.");

            Swal.fire('Error', backendError, 'error');
        } finally {
            setSaving(false);
        }
    };

    return (
        <div style={{ maxWidth: '850px', margin: '20px auto' }}>
            <Card className="mb-4 shadow-sm border-0" style={{ borderRadius: '12px' }}>
                <Card.Body className="p-4">
                    <Form onSubmit={handleSearch}>
                        <Form.Label className="fw-bold small text-muted text-uppercase">Búsqueda de Colaborador</Form.Label>
                        <InputGroup>
                            <InputGroup.Text className="bg-light">@</InputGroup.Text>
                            <Form.Control
                                placeholder="Ingrese login o email"
                                value={searchLogin}
                                onChange={(e) => setSearchLogin(e.target.value)}
                            />
                            <Button variant="dark" type="submit" disabled={loading}>
                                {loading ? <Spinner size="sm" /> : "CARGAR"}
                            </Button>
                        </InputGroup>
                    </Form>
                </Card.Body>
            </Card>

            {formData.id && (
                <Card className="shadow border-0 animate__animated animate__fadeIn" style={{ borderRadius: '15px' }}>
                    <Card.Header className="bg-primary text-white py-3 border-0 text-center">
                        <h5 className="mb-0 fw-bold">Editor de Perfil</h5>
                    </Card.Header>
                    <Card.Body className="p-4">
                        <Form onSubmit={handleSubmit}>
                            <Row>
                                <Col md={6} className="mb-3">
                                    <Form.Label className="small fw-bold text-muted">NOMBRE</Form.Label>
                                    <Form.Control
                                        value={formData.firstName}
                                        onChange={(e) => setFormData({...formData, firstName: e.target.value})}
                                        required
                                    />
                                </Col>
                                <Col md={6} className="mb-3">
                                    <Form.Label className="small fw-bold text-muted">APELLIDO</Form.Label>
                                    <Form.Control
                                        value={formData.lastName}
                                        onChange={(e) => setFormData({...formData, lastName: e.target.value})}
                                        required
                                    />
                                </Col>
                            </Row>

                            <Form.Group className="mb-4">
                                <Form.Label className="small fw-bold text-muted">EMAIL</Form.Label>
                                <Form.Control
                                    type="email"
                                    value={formData.email}
                                    onChange={(e) => setFormData({...formData, email: e.target.value})}
                                    required
                                />
                            </Form.Group>

                            <Row className="mb-4 pt-3 border-top">
                                <Col md={6}>
                                    <Form.Label className="small fw-bold text-primary">SUCURSAL</Form.Label>
                                    <Form.Select
                                        value={formData.branchCode}
                                        onChange={(e) => setFormData({...formData, branchCode: e.target.value})}
                                        required
                                        className="border-primary"
                                    >
                                        <option value="">Seleccione...</option>
                                        {Array.isArray(branches) && branches.map(b => (
                                            <option key={b.id} value={b.code}>{b.name}</option>
                                        ))}
                                    </Form.Select>
                                </Col>

                                <Col md={6}>
                                    <Form.Label className="small fw-bold text-danger">RESET CLAVE (OPCIONAL)</Form.Label>
                                    <InputGroup>
                                        <Form.Control
                                            type={showPassword ? "text" : "password"}
                                            placeholder="Vacío para no cambiar"
                                            value={formData.password}
                                            onChange={(e) => setFormData({...formData, password: e.target.value})}
                                            className="border-danger"
                                            /* Sin required para que el navegador no bloquee */
                                        />
                                        <Button variant="outline-danger" onClick={() => setShowPassword(!showPassword)}>
                                            {showPassword ? "🙈" : "👁️"}
                                        </Button>
                                    </InputGroup>
                                    <Form.Text className="text-muted" style={{fontSize: '0.75rem'}}>
                                        Si se deja vacío, se mantendrá la clave actual.
                                    </Form.Text>
                                </Col>
                            </Row>

                            <div className="d-flex justify-content-between align-items-center mt-5">
                                <Button variant="link" className="text-muted text-decoration-none p-0" onClick={() => setFormData({id:''})}>
                                    Cancelar
                                </Button>
                                <Button variant="primary" type="submit" className="px-5 fw-bold" disabled={saving}>
                                    {saving ? <Spinner size="sm" className="me-2" /> : null}
                                    GUARDAR CAMBIOS
                                </Button>
                            </div>
                        </Form>
                    </Card.Body>
                </Card>
            )}
        </div>
    );
};

export default UserUpdate;
