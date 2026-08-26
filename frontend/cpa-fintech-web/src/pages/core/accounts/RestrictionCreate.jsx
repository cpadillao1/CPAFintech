import React, { useState, useEffect } from 'react';
import api from '../../../api/axiosConfig';
import Swal from 'sweetalert2';
import { User, ShieldAlert } from 'lucide-react';

const RestrictionCreate = ({ onClear }) => {
  const [loading, setLoading] = useState(false);
  const [searchingAccount, setSearchingAccount] = useState(false);
  const [accountOwner, setAccountOwner] = useState('');

  const [catalogs, setCatalogs] = useState({
    types: [],
    reasons: []
  });

  const [formData, setFormData] = useState({
    accountNumber: '',
    restrictionTypeCatId: '',
    reasonCodeCatId: '',
    statusCatId: '',
    startDate: '',
    authorizer: '',
    observations: ''
  });

  useEffect(() => {
    const savedUser = localStorage.getItem('user');
    let bDate = "";
    let login = "system";

    if (savedUser) {
      try {
        const parsed = JSON.parse(savedUser);
        login = parsed.login || "system";
        const rawDate = parsed.businessDate;

        if (rawDate && rawDate.includes('/')) {
          const [day, month, year] = rawDate.split('/');
          bDate = `${year}-${month}-${day}`;
        } else {
          bDate = rawDate;
        }
      } catch (e) {
        console.error("Error parseando businessDate", e);
      }
    }

    if (!bDate) bDate = new Date().toISOString().split('T')[0];

    setFormData(prev => ({
      ...prev,
      startDate: bDate,
      authorizer: login
    }));
  }, []);

  useEffect(() => {
    const loadInitialData = async () => {
      try {
        const response = await api.get('/api/v1/catalogs/all');
        const catData = Array.isArray(response.data) ? response.data : [];

        setCatalogs({
          types: catData.filter(c => c.catalogName?.toUpperCase() === 'RESTRICTION_TYPE'),
          reasons: catData.filter(c => c.catalogName?.toUpperCase() === 'RESTRICTION_CAT')
        });

        const activeStatus = catData.find(c =>
          c.catalogName?.toUpperCase() === 'HOLD_STATUS' &&
          c.code?.toUpperCase() === 'ACTIVE'
        );

        if (activeStatus) {
          setFormData(prev => ({ ...prev, statusCatId: activeStatus.id }));
        }
      } catch (err) {
        console.error("Error loading masters", err);
      }
    };
    loadInitialData();
  }, []);

  const handleAccountBlur = async () => {
    if (!formData.accountNumber) {
      setAccountOwner('');
      return;
    }
    setSearchingAccount(true);
    try {
      const res = await api.get(`/api/v1/accounts/number/${formData.accountNumber}`);
      if (res.data && res.data.customerName) {
        setAccountOwner(res.data.customerName);
      } else {
        setAccountOwner('Account verified');
      }
    } catch (err) {
      setAccountOwner('');
      Swal.fire({
        icon: 'error',
        title: 'Invalid Account',
        text: 'The account number entered does not exist.',
        confirmButtonColor: '#0f172a'
      });
    } finally {
      setSearchingAccount(false);
    }
  };

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!formData.accountNumber) {
      return Swal.fire('Required Field', 'Please enter your account number', 'warning');
    }
    if (!accountOwner) {
      return Swal.fire('Cuenta no verificada', 'You must enter a valid account before continuing', 'warning');
    }
    if (!formData.restrictionTypeCatId) {
      return Swal.fire('Required Field', 'Please select a restriction type', 'warning');
    }
    if (!formData.reasonCodeCatId) {
      return Swal.fire('Required Field', 'Please select the reason for the restriction', 'warning');
    }
    if (!formData.startDate) {
      return Swal.fire('Required Field', 'Please select a start date', 'warning');
    }
    if (!formData.observations) {
      return Swal.fire('Required Field', 'Please enter your technical comments', 'warning');
    }

    setLoading(true);
    try {
      await api.post('/api/v1/accounts/restrictions', formData);
      Swal.fire({
        icon: 'success',
        title: '¡Successful Process!',
        text: 'The restriction has been correctly applied.',
        confirmButtonColor: '#0f172a'
      });
      setAccountOwner('');
      setFormData(prev => ({
        ...prev,
        accountNumber: '',
        restrictionTypeCatId: '',
        reasonCodeCatId: '',
        observations: ''
      }));
      if (onClear) onClear();
    } catch (error) {
      const detail = error.response?.data?.message || "Internal Core Error";
      Swal.fire('Error', detail, 'error');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="ig-container" style={{ paddingTop: '5px' }}>
      <div style={{ maxWidth: '850px', margin: '0 auto', textAlign: 'center' }}>
        <h3 className="ig-card-title" style={{ marginBottom: '15px' }}>🛡️ Account Restriction Management</h3>
      </div>

      <form className="ig-card" onSubmit={handleSubmit} noValidate style={{ padding: '20px 25px' }}>
        <div className="ig-grid" style={{ gap: '15px' }}>

          <div className="ig-field">
            <label className="ig-label">Account Number <span className="text-danger">*</span></label>
            <input
              className="ig-input"
              type="text"
              name="accountNumber"
              placeholder="e.g., 1000000001"
              value={formData.accountNumber}
              onChange={handleChange}
              onBlur={handleAccountBlur}
            />
            {searchingAccount && <small style={{ color: '#3b82f6' }}>Checking...</small>}
            {accountOwner && (
              <div className="mt-1 d-flex align-items-center text-success fw-bold" style={{ fontSize: '0.75rem' }}>
                <User size={12} className="me-1" /> {accountOwner}
              </div>
            )}
          </div>

          <div className="ig-field">
            <label className="ig-label">Restriction Type <span className="text-danger">*</span></label>
            <select
              className="ig-select"
              name="restrictionTypeCatId"
              value={formData.restrictionTypeCatId}
              onChange={handleChange}
            >
              <option value="">Select type...</option>
              {catalogs.types.map(t => (
                <option key={t.id} value={t.id}>{t.name}</option>
              ))}
            </select>
          </div>

          <div className="ig-field">
            <label className="ig-label">Reason / Motive <span className="text-danger">*</span></label>
            <select
              className="ig-select"
              name="reasonCodeCatId"
              value={formData.reasonCodeCatId}
              onChange={handleChange}
            >
              <option value="">Select reason...</option>
              {catalogs.reasons.map(r => (
                <option key={r.id} value={r.id}>{r.name}</option>
              ))}
            </select>
          </div>

          <div className="ig-field">
            <label className="ig-label">Start Date <span className="text-danger">*</span></label>
            <input
              className="ig-input"
              type="date"
              name="startDate"
              value={formData.startDate}
              onChange={handleChange}
            />
          </div>
        </div>

        <div className="ig-field" style={{ marginTop: '5px', marginBottom: '0' }}>
          <label className="ig-label">Observations <span className="text-danger">*</span></label>
          <textarea
            className="ig-textarea"
            name="observations"
            placeholder="Please explain the reason for this restriction..."
            style={{ minHeight: '60px' }}
            value={formData.observations}
            onChange={handleChange}
          ></textarea>
        </div>

        <div style={{ marginTop: '15px' }}>
          <button
            type="submit"
            className="ig-btn-navy"
            disabled={loading}
          >
            {loading ? (
              <div className="loader-spinner" style={{ width: '18px', height: '18px', borderTopColor: 'white' }}></div>
            ) : (
              <>
                <ShieldAlert size={18} />
                APPLY RESTRICTION
              </>
            )}
          </button>
        </div>
      </form>
    </div>
  );
};

export default RestrictionCreate;
