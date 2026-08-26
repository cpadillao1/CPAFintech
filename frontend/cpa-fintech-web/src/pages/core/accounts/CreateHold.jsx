import React, { useState, useEffect } from 'react';
import api from '../../../api/axiosConfig';
import Swal from 'sweetalert2';
import { Save, User, DollarSign, AlertCircle } from 'lucide-react';

const CreateHold = () => {
  const [loading, setLoading] = useState(false);
  const [searchingAccount, setSearchingAccount] = useState(false);
  const [accountOwner, setAccountOwner] = useState('');
  const [holdTypes, setHoldTypes] = useState([]);

  const [formData, setFormData] = useState({
    accountNumber: '',
    amount: '',
    holdTypeId: '',
    referenceNumber: '',
    description: '',
    startDate: '',
    expiryDate: '',
    createdBy: ''
  });

  // 1. Initial Setup: Session Data & Automatic Reference Generation
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
        // Silent catch
      }
    }

    if (!bDate) bDate = new Date().toISOString().split('T')[0];

    // Reference generation: HLD + Current Timestamp (Same logic as TRX)
    const autoRef = `HLD-${Date.now()}`;

    setFormData(prev => ({
      ...prev,
      startDate: bDate,
      createdBy: login,
      referenceNumber: autoRef
    }));
  }, []);

  // 2. Fetch Catalogs
  useEffect(() => {
    const fetchHoldTypes = async () => {
      try {
        const response = await api.get('/api/v1/catalogs/all');
        const data = Array.isArray(response.data) ? response.data : [];
        setHoldTypes(data.filter(c => c.catalogName?.toUpperCase() === 'HOLD_TYPE'));
      } catch (err) {
        // Fail silently
      }
    };
    fetchHoldTypes();
  }, []);

  // 3. Core Account Validation
  const handleAccountBlur = async () => {
    const accNum = formData.accountNumber ? formData.accountNumber.trim() : '';
    if (!accNum) {
      setAccountOwner('');
      return;
    }

    setSearchingAccount(true);
    try {
      const res = await api.get(`/api/v1/accounts/number/${accNum}`);
      if (res.data && res.data.customerName) {
        setAccountOwner(res.data.customerName);
      }
    } catch (err) {
      setAccountOwner('');
      Swal.fire({
        icon: 'error',
        title: 'Account Not Found',
        text: 'The entered account does not exist in our records.',
        confirmButtonColor: '#0f172a'
      });
    } finally {
      setSearchingAccount(false);
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  // 4. Strict Validation & Submission
  const handleSubmit = async (e) => {
    e.preventDefault();

    // Custom Mandatory Field Validations
    if (!formData.accountNumber) return Swal.fire('Missing Data', 'Account Number is mandatory', 'warning');
    if (!accountOwner) return Swal.fire('Validation Error', 'A valid account must be verified before proceeding', 'warning');
    if (!formData.amount || parseFloat(formData.amount) <= 0) return Swal.fire('Missing Data', 'Please enter a valid amount greater than zero', 'warning');
    if (!formData.holdTypeId) return Swal.fire('Missing Data', 'Selecting a Hold Type is mandatory', 'warning');
    if (!formData.expiryDate) return Swal.fire('Missing Data', 'Expiry Date is required for core auditing', 'warning');
    if (!formData.description || formData.description.length < 10) return Swal.fire('Missing Data', 'Please provide a descriptive observation (min. 10 chars)', 'warning');

    setLoading(true);
    try {
      const payload = {
        ...formData,
        amount: parseFloat(formData.amount)
      };

      await api.post('/api/v1/accounts/holds', payload);

      await Swal.fire({
        icon: 'success',
        title: 'Hold Applied',
        text: 'The restriction has been successfully registered.',
        confirmButtonColor: '#0f172a'
      });

      // Reset form with new sequence
      setAccountOwner('');
      setFormData(prev => ({
        ...prev,
        accountNumber: '',
        amount: '',
        holdTypeId: '',
        referenceNumber: `HLD-${Date.now()}`,
        description: '',
        expiryDate: ''
      }));

    } catch (error) {
      const msg = error.response?.data?.message || "Internal server error applying hold";
      Swal.fire('Process Failed', msg, 'error');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="ig-container" style={{ paddingTop: '10px' }}>
      <div style={{ maxWidth: '850px', margin: '0 auto', textAlign: 'center' }}>
        <h3 className="ig-card-title" style={{ marginBottom: '15px' }}>🔐 Manual Account Hold</h3>
      </div>

      <form className="ig-card" onSubmit={handleSubmit} noValidate style={{ padding: '20px 25px' }}>
        <div className="ig-grid" style={{ gap: '15px' }}>

          {/* Account Number */}
          <div className="ig-field">
            <label className="ig-label">Account Number <span className="text-danger">*</span></label>
            <input
              className="ig-input"
              type="text"
              name="accountNumber"
              placeholder="Enter account number..."
              value={formData.accountNumber}
              onChange={handleChange}
              onBlur={handleAccountBlur}
              autoComplete="off"
            />
            <div style={{ minHeight: '22px', marginTop: '4px' }}>
              {searchingAccount && <small style={{ color: '#3b82f6' }}>Verifying account status...</small>}
              {accountOwner && (
                <div className="d-flex align-items-center text-success fw-bold" style={{ fontSize: '0.75rem' }}>
                  <User size={12} className="me-1" /> {accountOwner}
                </div>
              )}
            </div>
          </div>

          {/* Amount */}
          <div className="ig-field">
            <label className="ig-label">Hold Amount <span className="text-danger">*</span></label>
            <div style={{ position: 'relative' }}>
              <input
                className="ig-input"
                type="number"
                step="0.01"
                name="amount"
                placeholder="0.00"
                value={formData.amount}
                onChange={handleChange}
              />
              <DollarSign size={14} style={{ position: 'absolute', right: '10px', top: '14px', color: '#94a3b8' }} />
            </div>
          </div>

          {/* Hold Type */}
          <div className="ig-field">
            <label className="ig-label">Hold Reason / Type <span className="text-danger">*</span></label>
            <select
              className="ig-select"
              name="holdTypeId"
              value={formData.holdTypeId}
              onChange={handleChange}
            >
              <option value="">Choose a type...</option>
              {holdTypes.map(t => (
                <option key={t.id} value={t.id}>{t.name}</option>
              ))}
            </select>
          </div>

          {/* Reference Number (Auto-generated) */}
          <div className="ig-field">
            <label className="ig-label">Reference Number <span className="text-danger">*</span></label>
            <input
              className="ig-input"
              type="text"
              name="referenceNumber"
              style={{ backgroundColor: '#f1f5f9', fontWeight: 'bold' }}
              value={formData.referenceNumber}
              readOnly
            />
          </div>

          {/* Start Date */}
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

          {/* Expiry Date */}
          <div className="ig-field">
            <label className="ig-label">Expiry Date <span className="text-danger">*</span></label>
            <input
              className="ig-input"
              type="date"
              name="expiryDate"
              value={formData.expiryDate}
              onChange={handleChange}
            />
          </div>
        </div>

        {/* Observations */}
        <div className="ig-field" style={{ marginTop: '10px' }}>
          <label className="ig-label">Hold Observations <span className="text-danger">*</span></label>
          <textarea
            className="ig-textarea"
            name="description"
            placeholder="Detailed reason for the fund restriction..."
            style={{ minHeight: '80px' }}
            value={formData.description}
            onChange={handleChange}
          ></textarea>
        </div>

        {/* Action Button */}
        <div style={{ marginTop: '20px' }}>
          <button type="submit" className="ig-btn-navy" disabled={loading}>
            {loading ? (
              <div className="loader-spinner" style={{ width: '18px', height: '18px', borderTopColor: 'white' }}></div>
            ) : (
              <><Save size={18} /> CONFIRM OPERATION</>
            )}
          </button>
          <div className="ig-helper-text" style={{ justifyContent: 'center', marginTop: '12px' }}>
            <AlertCircle size={14} className="me-1" />
            Locked funds will be reflected in the account's held balance immediately.
          </div>
        </div>
      </form>
    </div>
  );
};

export default CreateHold;
