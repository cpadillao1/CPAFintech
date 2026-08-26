import React, { useState, useEffect } from 'react';
import api from '../../../api/axiosConfig';
import Swal from 'sweetalert2';

const InterestGroupCreate = ({ onGroupCreated }) => {
  const MAX_LIMIT = 999999999999.99;

  const [groupId, setGroupId] = useState(null);
  const [formData, setFormData] = useState({
    code: '',
    name: '',
    description: '',
    status: 'ACTIVE'
  });

  const [currentRange, setCurrentRange] = useState({
    rangeName: '',
    rateValue: '',
    rateType: 'EA',
    minAmount: 0,
    maxAmount: '',
    status: 'ACTIVE'
  });

  const [displayMaxAmount, setDisplayMaxAmount] = useState('');
  const [rangesList, setRangesList] = useState([]);

  useEffect(() => {
    if (rangesList.length === 0) {
      setCurrentRange(prev => ({ ...prev, minAmount: 0 }));
    } else {
      const lastMax = parseFloat(rangesList[rangesList.length - 1].maxAmount);
      const nextMin = (lastMax + 0.01).toFixed(2);
      setCurrentRange(prev => ({ ...prev, minAmount: parseFloat(nextMin) }));
    }
  }, [rangesList]);

  const handleMaxAmountChange = (e) => {
    const rawValue = e.target.value.replace(/[^0-9.]/g, '');
    const parts = rawValue.split('.');
    if (parts.length > 2) return;

    setCurrentRange({ ...currentRange, maxAmount: rawValue });

    if (rawValue === '') {
      setDisplayMaxAmount('');
    } else {
      const num = parseFloat(parts[0]);
      if (isNaN(num)) return;
      const formattedInt = num.toLocaleString('en-US');
      const formattedValue = parts.length > 1 ? `${formattedInt}.${parts[1].slice(0, 2)}` : formattedInt;
      setDisplayMaxAmount(formattedValue);
    }
  };

  const handleSaveHeader = async () => {
    const missingFields = [];
    if (!formData.code) missingFields.push('Strategy Code');
    if (!formData.name) missingFields.push('Strategy Name');

    if (missingFields.length > 0) {
      return Swal.fire('Attention', `The following fields are mandatory: ${missingFields.join(', ')}`, 'warning');
    }

    try {
      const res = await api.post('/api/v1/interest-groups', { ...formData, ranges: [] });
      setGroupId(res.data.id);
      Swal.fire({
        title: 'Step 1 Successful!',
        text: 'Header created.',
        icon: 'success',
        confirmButtonColor: '#3b82f6'
      });
    } catch (err) {
      Swal.fire('Error', err.response?.data?.message || 'Error saving header', 'error');
    }
  };

  const addRangeToList = () => {
    const { rangeName, rateValue, minAmount, maxAmount } = currentRange;
    const numMax = parseFloat(maxAmount);
    const numMin = parseFloat(minAmount);

    const missingRangeFields = [];
    if (!rangeName) missingRangeFields.push('Range Label');
    if (!rateValue) missingRangeFields.push('Rate Value');
    if (maxAmount === '') missingRangeFields.push('To Amount');

    if (missingRangeFields.length > 0) {
      return Swal.fire('Incomplete Fields', `Required: ${missingRangeFields.join(', ')}`, 'warning');
    }

    if (numMax <= numMin) {
      return Swal.fire('Logic Error', `Max must be > ${numMin}`, 'error');
    }

    setRangesList([...rangesList, { ...currentRange, maxAmount: numMax, minAmount: numMin }]);
    setCurrentRange({ ...currentRange, rangeName: '', rateValue: '', maxAmount: '' });
    setDisplayMaxAmount('');
  };

  const removeRange = (index) => {
    if (index !== rangesList.length - 1) {
      return Swal.fire('Info', 'Remove ranges from last to first.', 'info');
    }
    setRangesList(rangesList.filter((_, i) => i !== index));
  };

  const handleFinalize = async () => {
    if (rangesList.length === 0) return Swal.fire('No Ranges', 'Add at least one range.', 'info');

    const lastRange = rangesList[rangesList.length - 1];
    if (parseFloat(lastRange.maxAmount) !== MAX_LIMIT) {
      return Swal.fire('Incomplete', `Last range must end at ${MAX_LIMIT.toLocaleString()}`, 'warning');
    }

    try {
      const finalData = {
        id: groupId,
        ...formData,
        ranges: rangesList.map(r => ({
          ...r,
          rateValue: parseFloat(r.rateValue),
          minAmount: parseFloat(r.minAmount),
          maxAmount: parseFloat(r.maxAmount)
        }))
      };

      await api.post('/api/v1/interest-groups', finalData);
      await Swal.fire('Saved!', 'Strategy configured correctly.', 'success');

      setGroupId(null);
      setRangesList([]);
      setFormData({ code: '', name: '', description: '', status: 'ACTIVE' });
      if (onGroupCreated) onGroupCreated();
    } catch (err) {
      Swal.fire('Error', 'Could not save strategy.', 'error');
    }
  };

  return (
    <div className="ig-container">
      <div className="ig-stepper">
        <div className="ig-step" style={{ color: !groupId ? '#3b82f6' : '#10b981' }}>
          <span className="ig-step-num" style={{ backgroundColor: !groupId ? '#3b82f6' : '#10b981' }}>1</span>
          Header
        </div>
        <div className="ig-step-line" style={{ backgroundColor: groupId ? '#10b981' : '#e2e8f0' }} />
        <div className="ig-step" style={{ color: groupId ? '#3b82f6' : '#94a3b8' }}>
          <span className="ig-step-num" style={{ backgroundColor: groupId ? '#3b82f6' : '#94a3b8' }}>2</span>
          Ranges & Rates
        </div>
      </div>

      <div className="ig-card">
        {!groupId ? (
          <div className="animate-fade-in">
            <h3 className="ig-card-title">📦 General Strategy Data</h3>
            <div className="ig-grid">
              <div className="ig-field">
                <label className="ig-label">Unique Code</label>
                <input
                  className="ig-input"
                  placeholder="Ex: CONSUMO_NORMAL"
                  value={formData.code}
                  onChange={(e) => setFormData({...formData, code: e.target.value.toUpperCase()})}
                />
              </div>
              <div className="ig-field">
                <label className="ig-label">Public Name</label>
                <input
                  className="ig-input"
                  placeholder="Ex: Consumer Rate"
                  value={formData.name}
                  onChange={(e) => setFormData({...formData, name: e.target.value})}
                />
              </div>
            </div>
            <div className="ig-field">
              <label className="ig-label">Product Description</label>
              <textarea
                className="ig-textarea"
                placeholder="Purpose of this rate..."
                value={formData.description}
                onChange={(e) => setFormData({...formData, description: e.target.value})}
              />
            </div>
            <button onClick={handleSaveHeader} className="ig-btn-primary">
              Continue to Ranges ➡
            </button>
          </div>
        ) : (
          <div className="animate-fade-in">
            <div className="ig-header-info">
              <h3 className="ig-card-title" style={{ marginBottom: '5px' }}>📈 Rate Configuration</h3>
              <span className="ig-badge">Editing: {formData.code}</span>
            </div>

            <div className="ig-range-form">
              <div className="ig-field" style={{ gridColumn: 'span 2' }}>
                <label className="ig-label">Range Label</label>
                <input className="ig-input" value={currentRange.rangeName} onChange={(e) => setCurrentRange({...currentRange, rangeName: e.target.value})}/>
              </div>

              <div className="ig-field">
                <label className="ig-label">Rate Value</label>
                <input className="ig-input" type="number" step="0.0001" value={currentRange.rateValue} onChange={(e) => setCurrentRange({...currentRange, rateValue: e.target.value})}/>
              </div>

              <div className="ig-field">
                <label className="ig-label">Periodicity</label>
                <select className="ig-select" value={currentRange.rateType} onChange={(e) => setCurrentRange({...currentRange, rateType: e.target.value})}>
                  <option value="EA">Effective Annual (EA)</option>
                  <option value="NOM">Nominal (NOM)</option>
                </select>
              </div>

              <div className="ig-field">
                <label className="ig-label">From Amount</label>
                <input className="ig-input" style={{ backgroundColor: '#f1f5f9' }} value={currentRange.minAmount.toLocaleString(undefined, {minimumFractionDigits: 2})} readOnly />
              </div>

              <div className="ig-field">
                <label className="ig-label">To Amount</label>
                <input className="ig-input" type="text" value={displayMaxAmount} onChange={handleMaxAmountChange} />
              </div>
            </div>

            <button onClick={addRangeToList} className="ig-btn-secondary">
              + Add Range to List
            </button>

            {rangesList.length > 0 && (
              <div className="ig-table-wrapper">
                <table className="ig-table">
                  <thead>
                    <tr>
                      <th className="ig-th">Name</th>
                      <th className="ig-th">From</th>
                      <th className="ig-th">To</th>
                      <th className="ig-th">Rate</th>
                      <th className="ig-th"></th>
                    </tr>
                  </thead>
                  <tbody>
                    {rangesList.map((r, i) => (
                      <tr key={i}>
                        <td className="ig-td"><strong>{r.rangeName}</strong></td>
                        <td className="ig-td">${parseFloat(r.minAmount).toLocaleString(undefined, {minimumFractionDigits: 2})}</td>
                        <td className="ig-td">${parseFloat(r.maxAmount).toLocaleString(undefined, {minimumFractionDigits: 2})}</td>
                        <td className="ig-td"><span className="ig-rate-tag">{(parseFloat(r.rateValue) * 100).toFixed(2)}% {r.rateType}</span></td>
                        <td className="ig-td">
                          <button onClick={() => removeRange(i)} className="ig-btn-del">✕</button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
                <div className="ig-footer-actions">
                   <button onClick={handleFinalize} className="ig-btn-success">
                    Finalize & Save Strategy ✅
                  </button>
                </div>
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
};

export default InterestGroupCreate;
