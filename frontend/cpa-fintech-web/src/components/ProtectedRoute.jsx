import React from 'react';
import { Navigate } from 'react-router-dom';

const ProtectedRoute = ({ children }) => {
    // 1. Revisamos si existe el token en el "bolsillo" del navegador
    const token = localStorage.getItem('token');

    // 2. Si NO hay token, lo mandamos de patitas a la calle (al Login)
    if (!token) {
        return <Navigate to="/login" replace />;
    }

    // 3. Si hay token, lo dejamos ver la página que pidió
    return children;
};

export default ProtectedRoute;
