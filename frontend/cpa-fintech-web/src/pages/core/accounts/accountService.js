// accountService.js

// Esta función se encarga de ir al BE, pedir los datos y traerlos de vuelta
export const searchAccounts = async (filters) => {

  // 1. Extraemos los valores del formulario que vienen en el objeto 'filters'
  const { productId, subproductId, accountNumber, documentNumber } = filters;

  // 2. Convertimos los filtros en "Query Params" para la URL
  // Esto transforma {productId: '123'} en "?productId=123"
  const params = new URLSearchParams();
  if (productId) params.append('productId', productId);
  if (subproductId) params.append('subproductId', subproductId);
  if (accountNumber) params.append('accountNumber', accountNumber);
  if (documentNumber) params.append('documentNumber', documentNumber);

  // 3. Hacemos la llamada al endpoint que acabamos de terminar en el BE
  const response = await fetch(`/api/accounts/search?${params.toString()}`, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
      // Aquí iría el Token si usas Spring Security: 'Authorization': `Bearer ${token}`
    }
  });

  // 4. Si el servidor responde con error (ej: 500 o 404), lanzamos una excepción
  if (!response.ok) {
    throw new Error('No se pudo conectar con el servidor de cuentas');
  }

  // 5. Devolvemos la lista de cuentas con sus titulares ya procesada
  return await response.json();
};