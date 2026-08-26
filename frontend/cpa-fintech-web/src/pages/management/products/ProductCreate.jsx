import React, { useState } from 'react';
import api from '../../../api/axiosConfig';
import { Card, Form, Button, Row, Col, Spinner } from 'react-bootstrap';
import Swal from 'sweetalert2';

const ProductCreate = () => {
    const [formData, setFormData] = useState({
        code: '',
        name: '',
        status: 'ACTIVO' // Valor inicial
    });
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);

        try {
            await api.post('/api/v1/products', formData);

            Swal.fire({
                icon: 'success',
                title: 'Producto Registrado',
                text: `El producto ${formData.name} ha sido creado con éxito.`,
                confirmButtonColor: '#0f172a'
            });

            setFormData({ code: '', name: '' }); // Limpiar campos
        } catch (error) {
            const msg = error.response?.data?.message || "Error al crear el producto.";
            Swal.fire('Error', msg, 'error');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="animate__animated animate__fadeIn" style={{ maxWidth: '700px', margin: '20px auto' }}>
            <Card className="shadow-lg border-0" style={{ borderRadius: '15px' }}>
                <Card.Header className="bg-dark text-white py-3" style={{ borderTopLeftRadius: '15px', borderTopRightRadius: '15px' }}>
                    <h5 className="mb-0 fw-bold text-center">Configuración de Nuevo Producto</h5>
                </Card.Header>
                <Card.Body className="p-4">
                    <Form onSubmit={handleSubmit}>
                        <Row>
                            <Col md={4} className="mb-3">
                                <Form.Label className="small fw-bold text-muted">CÓDIGO</Form.Label>
                                <Form.Control
                                    type="text"
                                    placeholder="EJ: AHOR-01"
                                    value={formData.code}
                                    onChange={(e) => setFormData({...formData, code: e.target.value.toUpperCase()})}
                                    required
                                    className="fw-bold"
                                    maxLength={10}
                                />
                            </Col>
                            <Col md={8} className="mb-3">
                                <Form.Label className="small fw-bold text-muted">NOMBRE DEL PRODUCTO</Form.Label>
                                <Form.Control
                                    type="text"
                                    placeholder="Nombre descriptivo"
                                    value={formData.name}
                                    onChange={(e) => setFormData({...formData, name: e.target.value})}
                                    required
                                />
                            </Col>
                            <Col md={4} className="mb-3">
                                <Form.Label className="small fw-bold text-muted">ESTADO</Form.Label>
                                <Form.Select
                                    value={formData.status}
                                    onChange={(e) => setFormData({...formData, status: e.target.value})}
                                >
                                    <option value="ACTIVE">ACTIVO</option>
                                    <option value="INACTIVE">INACTIVO</option>
                                    <option value="PENDING">PENDIENTE</option>
                                </Form.Select>
                            </Col>
                        </Row>

                        <div className="d-grid mt-4">
                            <Button
                                variant="primary"
                                type="submit"
                                className="py-2 fw-bold"
                                style={{ backgroundColor: '#0f172a', border: 'none' }}
                                disabled={loading}
                            >
                                {loading ? <Spinner size="sm" className="me-2" /> : "REGISTRAR PRODUCTO"}
                            </Button>
                        </div>
                    </Form>
                </Card.Body>
            </Card>
        </div>
    );
};

export default ProductCreate;