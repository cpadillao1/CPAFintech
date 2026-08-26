import React, { useState } from 'react';
import api from '../../../api/axiosConfig';
import { Card, Form, Button, Row, Col, InputGroup, Spinner } from 'react-bootstrap';
import Swal from 'sweetalert2';

const BranchUpdate = () => {
    // Estados para la búsqueda
    const [searchCode, setSearchCode] = useState('');
    const [searching, setSearching] = useState(false);
    const [found, setFound] = useState(false);

    // Estados para los datos de la sucursal
    const [branchData, setBranchData] = useState({
        code: '',
        name: '',
        city: '',
        address: ''
    });

    const [updating, setUpdating] = useState(false);

    // 1. Función para buscar la sucursal por código
    const handleSearch = async (e) => {
        e.preventDefault();
        if (!searchCode.trim()) return;

        setSearching(true);
        setFound(false);
        try {
            const response = await api.get(`/api/v1/branches/${searchCode}`);
            setBranchData(response.data);
            setFound(true);
        } catch (error) {
            console.error("Error al buscar:", error);
            Swal.fire({
                icon: 'error',
                title: '<span style="font-size: 1.1rem">No encontrado</span>',
                text: `La sucursal con código ${searchCode} no existe.`,
                confirmButtonColor: '#0f172a',
                width: '320px'
            });
        } finally {
            setSearching(false);
        }
    };

    // 2. Función para manejar cambios en los inputs
    const handleChange = (e) => {
        const { name, value } = e.target;
        setBranchData({ ...branchData, [name]: value });
    };

    // 3. Función para enviar la actualización y LIMPIAR datos
    const handleUpdate = async (e) => {
        e.preventDefault();
        setUpdating(true);
        try {
            await api.put(`/api/v1/branches/${branchData.code}`, branchData);

            Swal.fire({
                icon: 'success',
                title: '<span style="font-size: 1.1rem">¡Éxito!</span>',
                text: 'Sucursal actualizada correctamente.',
                showConfirmButton: false,
                timer: 2000,
                width: '320px'
            });

            // ✨ LIMPIEZA DE CAMPOS DESPUÉS DEL ÉXITO
            setFound(false);
            setSearchCode('');
            setBranchData({ code: '', name: '', city: '', address: '' });

        } catch (error) {
            console.error("Error al actualizar:", error);
            Swal.fire({
                icon: 'error',
                title: '<span style="font-size: 1.1rem">Error</span>',
                text: 'Hubo un problema al procesar la actualización.',
                confirmButtonColor: '#0f172a',
                width: '320px'
            });
        } finally {
            setUpdating(false);
        }
    };

    return (
        <Card className="shadow-sm border-0" style={{ borderRadius: '15px', maxWidth: '800px', margin: '20px auto' }}>
            <Card.Header style={{ backgroundColor: '#0f172a', color: 'white' }} className="py-3 text-center">
                <h5 className="mb-0">Actualización de Sucursales</h5>
            </Card.Header>
            <Card.Body className="p-4">

                {/* SECCIÓN DE BÚSQUEDA */}
                <Form onSubmit={handleSearch} className="mb-4 pb-4 border-bottom">
                    <Form.Label className="fw-bold text-muted small text-uppercase">
                        Buscar por Código <span className="text-danger">*</span>
                    </Form.Label>
                    <Row className="align-items-center">
                        <Col md={8}>
                            <InputGroup className="shadow-sm">
                                <InputGroup.Text className="bg-light">🔍</InputGroup.Text>
                                <Form.Control
                                    placeholder="Ingrese código de sucursal (ej: 0001)"
                                    value={searchCode}
                                    onChange={(e) => setSearchCode(e.target.value)}
                                    disabled={searching}
                                    required
                                />
                            </InputGroup>
                        </Col>
                        <Col md={4}>
                            <Button variant="dark" type="submit" className="w-100 fw-bold" disabled={searching}>
                                {searching ? <Spinner size="sm" /> : "BUSCAR SUCURSAL"}
                            </Button>
                        </Col>
                    </Row>
                </Form>

                {/* FORMULARIO DE EDICIÓN */}
                {found && (
                    <Form onSubmit={handleUpdate} className="animate__animated animate__fadeIn">
                        <Row className="g-3">
                            <Col md={6}>
                                <Form.Group>
                                    <Form.Label className="small fw-bold text-muted">Código (No editable)</Form.Label>
                                    <Form.Control value={branchData.code} disabled className="bg-light fw-bold text-center" />
                                </Form.Group>
                            </Col>
                            <Col md={6}>
                                <Form.Group>
                                    <Form.Label className="small fw-bold text-primary">
                                        Nombre de Sucursal <span className="text-danger">*</span>
                                    </Form.Label>
                                    <Form.Control
                                        name="name"
                                        value={branchData.name}
                                        onChange={handleChange}
                                        required
                                    />
                                </Form.Group>
                            </Col>
                            <Col md={6}>
                                <Form.Group>
                                    <Form.Label className="small fw-bold text-primary">
                                        Ciudad <span className="text-danger">*</span>
                                    </Form.Label>
                                    <Form.Control
                                        name="city"
                                        value={branchData.city}
                                        onChange={handleChange}
                                        required
                                    />
                                </Form.Group>
                            </Col>
                            <Col md={6}>
                                <Form.Group>
                                    <Form.Label className="small fw-bold text-primary">
                                        Dirección <span className="text-danger">*</span>
                                    </Form.Label>
                                    <Form.Control
                                        name="address"
                                        value={branchData.address}
                                        onChange={handleChange}
                                        required
                                    />
                                </Form.Group>
                            </Col>
                        </Row>

                        <div className="mt-4 d-flex justify-content-end gap-2">
                            <Button
                                variant="outline-secondary"
                                onClick={() => {
                                    setFound(false);
                                    setSearchCode('');
                                }}
                            >
                                Cancelar
                            </Button>
                            <Button variant="primary" type="submit" className="px-5 fw-bold shadow-sm" disabled={updating}>
                                {updating ? <Spinner size="sm" /> : "GUARDAR CAMBIOS"}
                            </Button>
                        </div>
                    </Form>
                )}

                {!found && !searching && (
                    <div className="text-center py-4">
                        <p className="text-muted small">Por favor, localice primero la sucursal para habilitar la edición.</p>
                    </div>
                )}
            </Card.Body>
        </Card>
    );
};

export default BranchUpdate;
