import React, { useEffect, useState } from 'react';
import api from '../../../api/axiosConfig';
import { Table, Card, Spinner, Pagination, Badge } from 'react-bootstrap';

const BranchQuery = () => {
    const [branches, setBranches] = useState([]);
    const [loading, setLoading] = useState(true);
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [totalElements, setTotalElements] = useState(0);

    const pageSize = 10;

    useEffect(() => {
        fetchBranches(currentPage);
    }, [currentPage]);

    const fetchBranches = async (page) => {
        setLoading(true);
        try {
            // El Backend ahora retorna ordenado por código gracias al Sort.by("code")
            const response = await api.get(`/api/v1/branches?page=${page}&size=${pageSize}`);

            setBranches(response.data.content);
            setTotalPages(response.data.totalPages);
            setTotalElements(response.data.totalElements);
        } catch (error) {
            console.error("Error al obtener sucursales:", error);
        } finally {
            setLoading(false);
        }
    };

    const handlePageChange = (newPage) => {
        if (newPage >= 0 && newPage < totalPages) {
            setCurrentPage(newPage);
        }
    };

    return (
        <Card className="shadow-sm border-0" style={{ borderRadius: '12px', marginTop: '10px' }}>
            {/* Header con el color oscuro de tu Navbar para mantener consistencia */}
            <Card.Header style={{ backgroundColor: '#0f172a', color: 'white' }} className="d-flex justify-content-between align-items-center py-3">
                <h5 className="mb-0">Consulta de Sucursales</h5>
                <Badge bg="info" text="dark">Total Registros: {totalElements}</Badge>
            </Card.Header>

            <Card.Body>
                {loading ? (
                    <div className="text-center p-5">
                        <Spinner animation="border" variant="primary" />
                        <p className="mt-2 text-muted">Cargando información...</p>
                    </div>
                ) : (
                    <>
                        <Table striped hover responsive className="align-middle">
                            <thead className="table-dark">
                                <tr>
                                    <th style={{ width: '15%' }}>Código</th>
                                    <th style={{ width: '30%' }}>Nombre</th>
                                    <th style={{ width: '20%' }}>Ciudad</th>
                                    <th style={{ width: '35%' }}>Dirección</th>
                                </tr>
                            </thead>
                            <tbody>
                                {branches.length > 0 ? (
                                    branches.map((branch) => (
                                        <tr key={branch.code}>
                                            <td>
                                                <span className="fw-bold text-secondary">{branch.code}</span>
                                            </td>
                                            <td className="fw-bold" style={{ color: '#0f172a' }}>
                                                {branch.name}
                                            </td>
                                            <td>{branch.city}</td>
                                            <td className="text-muted small">
                                                {branch.address}
                                            </td>
                                        </tr>
                                    ))
                                ) : (
                                    <tr>
                                        <td colSpan="4" className="text-center p-4 text-muted">
                                            No se encontraron sucursales registradas.
                                        </td>
                                    </tr>
                                )}
                            </tbody>
                        </Table>

                        {/* Paginación solo si hay más de una página */}
                        {totalPages > 1 && (
                            <div className="d-flex justify-content-center mt-4">
                                <Pagination>
                                    <Pagination.First onClick={() => handlePageChange(0)} disabled={currentPage === 0} />
                                    <Pagination.Prev
                                        onClick={() => handlePageChange(currentPage - 1)}
                                        disabled={currentPage === 0}
                                    />
                                    {[...Array(totalPages).keys()].map((number) => (
                                        <Pagination.Item
                                            key={number}
                                            active={number === currentPage}
                                            onClick={() => handlePageChange(number)}
                                        >
                                            {number + 1}
                                        </Pagination.Item>
                                    ))}
                                    <Pagination.Next
                                        onClick={() => handlePageChange(currentPage + 1)}
                                        disabled={currentPage === totalPages - 1}
                                    />
                                    <Pagination.Last onClick={() => handlePageChange(totalPages - 1)} disabled={currentPage === totalPages - 1} />
                                </Pagination>
                            </div>
                        )}
                    </>
                )}
            </Card.Body>
        </Card>
    );
};

export default BranchQuery;
