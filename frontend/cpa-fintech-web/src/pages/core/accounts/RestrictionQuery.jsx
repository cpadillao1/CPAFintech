import React, { useState, useEffect } from 'react';
import api from '../../../api/axiosConfig';
import Swal from 'sweetalert2';
import { Search, UserCheck, ChevronLeft, ChevronRight, Calendar, Hash, ShieldCheck } from 'lucide-react';

const RestrictionQuery = () => {
  const [loading, setLoading] = useState(false);
  const [restrictions, setRestrictions] = useState([]);
  const [statusList, setStatusList] = useState([]);
  const [filterStatus, setFilterStatus] = useState('');

  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const pageSize = 10;

  useEffect(() => {
    const loadStatuses = async () => {
      try {
        const response = await api.get('/api/v1/catalogs/all');
        const catData = Array.isArray(response.data) ? response.data : [];
        const filtered = catData.filter(c => c.catalogName?.toUpperCase() === 'HOLD_STATUS');
        setStatusList(filtered);

        const defaultStatus = filtered.find(s => s.code?.toUpperCase() === 'ACTIVE');
        if (defaultStatus) setFilterStatus(defaultStatus.id);
      } catch (err) {
        console.error("Error loading statuses", err);
      }
    };
    loadStatuses();
  }, []);

  const handleSearch = async (e, page = 0) => {
    if (e) e.preventDefault();
    if (!filterStatus) return Swal.fire('Attention', 'Please select a status.', 'warning');

    setLoading(true);
    setCurrentPage(page);

    try {
      const response = await api.get(`/api/v1/accounts/restrictions/status/${filterStatus}`, {
        params: { page, size: pageSize }
      });

      const data = response.data.content || [];
      setRestrictions(data);
      setTotalPages(response.data.totalPages || 0);

      if (data.length === 0 && page === 0) {
        Swal.fire({ icon: 'info', title: 'No results', confirmButtonColor: '#0f172a' });
      }
    } catch (error) {
      Swal.fire('Error', 'Technical inquiry error.', 'error');
    } finally {
      setLoading(false);
    }
  };

  const formatDate = (dateString) => {
    if (!dateString) return '-';
    const [year, month, day] = dateString.split('-');
    return `${day}-${month}-${year}`;
  };

  return (
    <div className="ig-container">
      <div style={{ maxWidth: '1000px', margin: '0 auto', textAlign: 'center' }}>
        <h3 className="ig-card-title" style={{ marginBottom: '10px' }}>🔍 Restriction Inquiry</h3>
      </div>

      <div className="ig-card" style={{ maxWidth: '1000px', padding: '15px 25px', marginBottom: '15px' }}>
        <form onSubmit={(e) => handleSearch(e, 0)} className="search-flex-container">
          <div className="ig-field" style={{ flex: 2, marginBottom: 0 }}>
            <label className="ig-label">Filter by Status</label>
            <select
              className="ig-select"
              value={filterStatus}
              onChange={(e) => setFilterStatus(e.target.value)}
            >
              <option value="">Select status...</option>
              {statusList.map(s => (
                <option key={s.id} value={s.id}>{s.name}</option>
              ))}
            </select>
          </div>
          <div style={{ flex: 1 }}>
            <button type="submit" className="ig-btn-navy btn-standard" disabled={loading}>
              {loading ? <div className="loader-spinner" style={{ width: '16px', height: '16px' }}></div> : <><Search size={16} /> SEARCH</>}
            </button>
          </div>
        </form>
      </div>

      {restrictions.length > 0 && (
        <div className="ig-card" style={{ maxWidth: '1000px', padding: '15px' }}>
          <div className="ig-table-action-wrapper" style={{ marginTop: 0 }}>
            <table className="ig-table">
              <thead>
                <tr>
                  <th className="ig-th" style={{ width: '18%' }}><Hash size={12} className="inline me-1" /> ACCOUNT</th>
                  <th className="ig-th" style={{ width: '14%' }}>TYPE</th>
                  <th className="ig-th" style={{ width: '20%' }}>REASON</th>
                  <th className="ig-th" style={{ width: '16%' }}><Calendar size={12} className="inline me-1" /> START DATE</th>
                  <th className="ig-th" style={{ width: '16%' }}>AUTH. CREATION</th>
                  <th className="ig-th" style={{ width: '16%' }}>AUTH. RELEASE</th>
                </tr>
              </thead>
              <tbody>
                {restrictions.map((item) => (
                  <tr key={item.id} className="ig-row-hover">
                    <td className="ig-td fw-bold" style={{ color: '#0f172a' }}>{item.accountNumber}</td>
                    <td className="ig-td">
                      <span className="ig-badge" style={{ fontSize: '0.65rem' }}>{item.restrictionType}</span>
                    </td>
                    <td className="ig-td" style={{ color: '#475569', fontSize: '0.8rem' }}>{item.reasonCode}</td>
                    <td className="ig-td" style={{ fontFamily: 'monospace', fontWeight: '500', fontSize: '0.8rem' }}>
                      {formatDate(item.startDate)}
                    </td>
                    <td className="ig-td">
                      <div className="d-flex align-items-center" style={{ fontSize: '0.75rem' }}>
                        <UserCheck size={12} className="me-1" style={{ color: '#64748b' }} />
                        {item.authorizer}
                      </div>
                    </td>
                    <td className="ig-td">
                      <div className="d-flex align-items-center" style={{ fontSize: '0.75rem' }}>
                        {item.releaseAuthorizer ? (
                          <>
                            <ShieldCheck size={12} className="me-1" style={{ color: '#10b981' }} />
                            {item.releaseAuthorizer}
                          </>
                        ) : (
                          <span style={{ color: '#cbd5e1' }}>—</span>
                        )}
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          {totalPages > 1 && (
            <div className="ig-pagination-container">
              <button
                className="ig-btn-navy ig-btn-pagination"
                disabled={currentPage === 0 || loading}
                onClick={() => handleSearch(null, currentPage - 1)}
              >
                <ChevronLeft size={16} />
              </button>

              <div className="ig-pagination-info">
                {currentPage + 1} / {totalPages}
              </div>

              <button
                className="ig-btn-navy ig-btn-pagination"
                disabled={currentPage === totalPages - 1 || loading}
                onClick={() => handleSearch(null, currentPage + 1)}
              >
                <ChevronRight size={16} />
              </button>
            </div>
          )}
        </div>
      )}
    </div>
  );
};

export default RestrictionQuery;
