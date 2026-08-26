import React, { useState, useEffect } from 'react';
import api from '../../../api/axiosConfig';
import Swal from 'sweetalert2';
import { Search, User, AlertCircle, ShieldCheck, Calendar } from 'lucide-react';

const RestrictionRelease = () => {
  const [loading, setLoading] = useState(false);
  const [searchingAccount, setSearchingAccount] = useState(false);
  const [accountOwner, setAccountOwner] = useState('');
  const [accountNumber, setAccountNumber] = useState('');

  const [restrictions, setRestrictions] = useState([]);
  const [selectedId, setSelectedId] = useState(null);
  const [observations, setObservations] = useState('');

  const [ids, setIds] = useState({
    activeStatus: null,
    releasedStatus: null,
    authorizer: 'system'
  });

  useEffect(() => {
    const loadData = async () => {
      try {
        const savedUser = localStorage.getItem('user');
        let login = "system";
        if (savedUser) {
          const parsed = JSON.parse(savedUser);
          login = parsed.login || "system";
        }

        const response = await api.get('/api/v1/catalogs/all');
        const catData = Array.isArray(response.data) ? response.data : [];

        const active = catData.find(c => c.catalogName?.toUpperCase() === 'HOLD_STATUS' && c.code?.toUpperCase() === 'ACTIVE');
        const expired = catData.find(c => c.catalogName?.toUpperCase() === 'HOLD_STATUS' && c.code?.toUpperCase() === 'EXPIRED');

        setIds({
          activeStatus: active?.id,
          releasedStatus: expired?.id,
          authorizer: login
        });
      } catch (err) {
        console.error("Error initializing data", err);
      }
    };
    loadData();
  }, []);

  const handleAccountBlur = async () => {
    if (!accountNumber) {
      setAccountOwner('');
      return;
    }
    setSearchingAccount(true);
    try {
      const res = await api.get(`/api/v1/accounts/number/${accountNumber}`);
      if (res.data && res.data.customerName) {
        setAccountOwner(res.data.customerName);
      } else {
        setAccountOwner('Account verified');
      }
    } catch (err) {
      setAccountOwner('');
    } finally {
      setSearchingAccount(false);
    }
  };

  const handleSearchRestrictions = async (e) => {
    if (e) e.preventDefault();
    if (!accountNumber.trim()) {
      return Swal.fire({
        icon: 'warning',
        title: 'Mandatory Field',
        text: 'Please enter an account number.',
        confirmButtonColor: '#0f172a'
      });
    }

    setLoading(true);
    setRestrictions([]);
    setSelectedId(null);

    try {
      const response = await api.get(`/api/v1/accounts/restrictions/active/${accountNumber}`, {
        params: { statusActiveId: ids.activeStatus }
      });
      const data = response.data || [];
      setRestrictions(data);

      if (data.length === 0) {
        Swal.fire({
          icon: 'info',
          title: 'No Active Restrictions',
          text: 'This account does not have active locks to release.',
          confirmButtonColor: '#0f172a'
        });
      }
    } catch (error) {
      Swal.fire('Error', 'Technical error querying restrictions.', 'error');
    } finally {
      setLoading(false);
    }
  };

  const handleRelease = async (e) => {
    if (e) e.preventDefault();

    if (!selectedId) {
      return Swal.fire({
        icon: 'warning',
        title: 'Required Selection',
        text: 'Please select a restriction from the table to continue.',
        confirmButtonColor: '#0f172a'
      });
    }

    if (!observations.trim()) {
      return Swal.fire({
        icon: 'warning',
        title: 'Mandatory Field',
        text: 'Please enter the technical observations for the release.',
        confirmButtonColor: '#0f172a'
      });
    }

    setLoading(true);
    try {
      const payload = {
        observations: observations,
        statusReleasedId: ids.releasedStatus,
        releaseAuthorizer: ids.authorizer
      };

      await api.patch(`/api/v1/accounts/restrictions/${selectedId}/release`, payload);
      Swal.fire({
        icon: 'success',
        title: 'Successful Process!',
        text: 'The restriction has been correctly released.',
        confirmButtonColor: '#0f172a'
      });

      setAccountNumber('');
      setAccountOwner('');
      setRestrictions([]);
      setObservations('');
      setSelectedId(null);
    } catch (error) {
      Swal.fire('Error', error.response?.data?.message || "Internal Core Error", 'error');
    } finally {
      setLoading(false);
    }
  };

  const formatDate = (dateString) => {
    if (!dateString) return '';
    const [year, month, day] = dateString.split('-');
    return `${day}-${month}-${year}`;
  };

  return (
    <div className="ig-container">
      <div style={{ maxWidth: '900px', margin: '0 auto', textAlign: 'center' }}>
        <h3 className="ig-card-title" style={{ marginBottom: '15px' }}>🛡️ Manual Restriction Release</h3>
      </div>

      <div className="ig-card" style={{ maxWidth: '900px' }}>
        {/* SEARCH SECTION */}
        <div className="search-flex-container" style={{ alignItems: 'flex-start' }}>
          <div className="ig-field" style={{ flex: 1, marginBottom: 0 }}>
            <label className="ig-label">Account Number <span className="text-danger">*</span></label>
            <input
              type="text"
              className="ig-input"
              value={accountNumber}
              onChange={(e) => setAccountNumber(e.target.value)}
              onBlur={handleAccountBlur}
              placeholder="e.g. 1000000001"
            />
            <div style={{ minHeight: '20px', marginTop: '4px' }}>
              {searchingAccount && <small style={{ color: '#3b82f6' }}>Checking...</small>}
              {accountOwner && (
                <div className="d-flex align-items-center text-success fw-bold" style={{ fontSize: '0.75rem' }}>
                  <User size={12} className="me-1" /> {accountOwner}
                </div>
              )}
            </div>
          </div>

          <div style={{ flex: 0.3, paddingTop: '22px' }}>
            <button
              className="ig-btn-navy"
              onClick={handleSearchRestrictions}
              disabled={loading}
              style={{ height: '42px' }}
            >
              {loading ? (
                <div className="loader-spinner" style={{ width: '18px', height: '18px' }}></div>
              ) : (
                <><Search size={18} /> SEARCH</>
              )}
            </button>
          </div>
        </div>

        {/* RESULTS SECTION */}
        {restrictions.length > 0 && (
          <div className="ig-table-action-wrapper">
            <table className="ig-table">
              <thead>
                <tr>
                  <th className="ig-th" style={{ width: '8%', textAlign: 'center' }}>SEL.</th>
                  <th className="ig-th">TYPE</th>
                  <th className="ig-th">REASON / MOTIVE</th>
                  <th className="ig-th">
                    <Calendar size={12} className="inline me-1" /> START DATE
                  </th>
                </tr>
              </thead>
              <tbody>
                {restrictions.map((res) => (
                  <tr
                    key={res.id}
                    onClick={() => setSelectedId(res.id)}
                    className={`ig-row-clickable ${selectedId === res.id ? 'ig-row-selected' : ''}`}
                  >
                    <td className="ig-td" style={{ textAlign: 'center' }}>
                      <input
                        type="radio"
                        className="ig-radio-navy"
                        checked={selectedId === res.id}
                        readOnly
                      />
                    </td>
                    <td className="ig-td">
                      <span className="ig-badge" style={{ fontSize: '0.65rem' }}>{res.restrictionType}</span>
                    </td>
                    <td className="ig-td" style={{ color: '#475569', fontSize: '0.85rem' }}>{res.reasonCode}</td>
                    <td className="ig-td" style={{ fontFamily: 'monospace', fontSize: '0.85rem' }}>
                        {formatDate(res.startDate)}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>

            <div style={{ padding: '20px', backgroundColor: '#ffffff' }}>
              <div className="ig-field">
                <label className="ig-label">Release Observations <span className="text-danger">*</span></label>
                <textarea
                  className="ig-textarea"
                  value={observations}
                  onChange={(e) => setObservations(e.target.value)}
                  placeholder="Justify the release process..."
                  style={{ minHeight: '80px' }}
                ></textarea>
              </div>

              <div style={{ marginTop: '10px' }}>
                <button
                  type="button"
                  className="ig-btn-navy"
                  onClick={handleRelease}
                  disabled={loading}
                >
                  {loading ? (
                    <div className="loader-spinner" style={{ width: '18px', height: '18px' }}></div>
                  ) : (
                    <><ShieldCheck size={18} /> PROCESS RELEASE</>
                  )}
                </button>
              </div>
            </div>
          </div>
        )}

        {/* EMPTY STATE */}
        {restrictions.length === 0 && !loading && (
          <div style={{ textAlign: 'center', padding: '40px', color: '#94a3b8' }}>
            <AlertCircle size={36} style={{ marginBottom: '12px', opacity: 0.5 }} />
            <p style={{ fontSize: '0.9rem' }}>Search for a valid account to manage its restrictions.</p>
          </div>
        )}
      </div>
    </div>
  );
};

export default RestrictionRelease;
