import React, { useState, useEffect } from 'react';
import api from '../../../api/axiosConfig';
import { Card, Table, Form, InputGroup, Badge, Spinner, Button } from 'react-bootstrap';
import Swal from 'sweetalert2';

const ProductQuery = () => {
    const [products, setProducts] = useState([]);
    const [searchTerm, setSearchTerm] = useState('');
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        fetchProducts();
    }, []);

    const fetchProducts = async () => {
        setLoading(true);
        try {
            const response = await api.get('/api/v1/products');
            setProducts(response.data);
        } catch (error) {
            Swal.fire({
                icon: 'error',
                title: 'Error de Red',
                text: 'No se pudo obtener la lista de productos.',
                confirmButtonColor: '#0f172a'
            });
        } finally {
            setLoading(false);
        }
    };

    // Función para dar color a los estados
    const getStatusBadge = (status) => {
        switch (status) {
            case 'ACTIVE': return <Badge bg="success">ACTIVO</Badge>;
            case 'INACTIVE': return <Badge bg="danger">INACTIVO</Badge>;
            case 'PENDING': return <Badge bg="warning" text="dark">PENDIENTE</Badge>;
            default: return <Badge bg="secondary">{status}</Badge>;
        }
    };

    // Filtro en tiempo real por Código o Nombre
    const filteredProducts = products.filter(p =>
        p.code.toLowerCase().includes(searchTerm.toLowerCase()) ||
        p.name.toLowerCase().includes(searchTerm.toLowerCase())
    );

    return (
        <div className="animate__animated animate__fadeIn">
            <Card className="shadow-sm border-0" style={{ borderRadius: '12px' }}>
                <Card.Body className="p-4">
                    <div className="d-flex justify-content-between align-items-center mb-4">
                        <div>
                            <h4 className="fw-bold mb-0" style={{ color: '#0f172a' }}>Catálogo de Productos</h4>
                            <p className="text-muted small mb-0">Gestión de productos y servicios financieros configurados.</p>
                        </div>
                    </div>

                    {loading ? (
                        <div className="text-center py-5">
                            <Spinner animation="border" variant="primary" />
                            <p className="mt-2 text-muted">Consultando Core...</p>
                        </div>
                    ) : (
                        <div className="table-responsive">
                            <Table hover className="align-middle">
                                <thead className="bg-light">
                                    <tr style={{ fontSize: '0.75rem', color: '#64748b', textTransform: 'uppercase', letterSpacing: '1px' }}>
                                        <th className="border-0">Código</th>
                                        <th className="border-0">Nombre del Producto</th>
                                        <th className="border-0 text-center">Estado</th>
                                        <th className="border-0">Fecha Registro</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {filteredProducts.length > 0 ? (
                                        filteredProducts.map((p) => (
                                            <tr key={p.id} style={{ fontSize: '0.9rem' }}>
                                                <td>
                                                    <span className="fw-bold font-monospace" style={{ color: '#3b82f6' }}>{p.code}</span>
                                                </td>
                                                <td className="fw-medium text-dark">{p.name}</td>
                                                <td className="text-center">{getStatusBadge(p.status)}</td>
                                                <td className="text-muted">
                                                    {new Date(p.createdAt).toLocaleDateString('es-ES', {
                                                        day: '2-digit',
                                                        month: '2-digit',
                                                        year: 'numeric'
                                                    })}
                                                </td>
                                            </tr>
                                        ))
                                    ) : (
                                        <tr>
                                            <td colSpan="5" className="text-center py-4 text-muted small">
                                                No se encontraron productos que coincidan con la búsqueda.
                                            </td>
                                        </tr>
                                    )}
                                </tbody>
                            </Table>
                        </div>
                    )}
                </Card.Body>
            </Card>
        </div>
    );
};

export default ProductQuery;
