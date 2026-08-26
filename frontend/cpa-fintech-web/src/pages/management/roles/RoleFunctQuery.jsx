import React, { useState, useEffect } from 'react';
import api from '../../../api/axiosConfig';
import Swal from 'sweetalert2';

/**
 * Super-Squeezed Tree Node for High-Density Data
 */
const FunctionalityNode = ({ item, isLast }) => {
  const [isOpen, setIsOpen] = useState(true);
  const hasChildren = item.children && item.children.length > 0;

  const typeStyles = {
    MODULE: "bg-blue-50 text-blue-700 border-blue-100",
    SUBMODULE: "bg-indigo-50 text-indigo-700 border-indigo-100",
    ACTION: "bg-emerald-50 text-emerald-700 border-emerald-100"
  };

  return (
    <div className="relative ml-2.5">
      {!isLast && <div className="absolute left-[-8px] top-0 bottom-0 w-px bg-slate-200"></div>}
      <div className="absolute left-[-8px] top-2.5 w-2 h-px bg-slate-200"></div>

      <div className="group flex items-center gap-1.5 py-0.5 px-1.5 bg-white border border-slate-100 rounded shadow-sm mb-0.5 hover:bg-slate-50 transition-colors">
        {hasChildren ? (
          <button
            onClick={() => setIsOpen(!isOpen)}
            className="text-[8px] w-3 h-3 flex items-center justify-center rounded border border-slate-300 bg-white text-slate-500 hover:border-indigo-500"
          >
            {isOpen ? '−' : '+'}
          </button>
        ) : (
          <div className="w-3"></div>
        )}

        <span className="text-xs leading-none">{item.icon || '📄'}</span>

        <div className="flex flex-col leading-none py-0.5">
          <span className="text-[12px] font-bold text-slate-700">{item.label}</span>
        </div>

        <div className={`ml-auto text-[7px] px-1 py-0 rounded border uppercase font-black tracking-tighter ${typeStyles[item.type] || 'bg-slate-100'}`}>
          {item.type}
        </div>
      </div>

      {hasChildren && isOpen && (
        <div className="ml-0 border-l border-slate-200">
          {item.children.map((child, index) => (
            <FunctionalityNode
              key={child.id}
              item={child}
              isLast={index === item.children.length - 1}
            />
          ))}
        </div>
      )}
    </div>
  );
};

const RoleFunctQuery = () => {
  const [roles, setRoles] = useState([]);
  const [selectedRoleId, setSelectedRoleId] = useState('');
  const [roleData, setRoleData] = useState(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const loadRoles = async () => {
      try {
        const response = await api.get('/api/v1/roles');
        setRoles(response.data);
      } catch (err) {
        console.error("Error loading roles:", err);
      }
    };
    loadRoles();
  }, []);

  const fetchRoleData = async (e) => {
    if (e) e.preventDefault();

    if (!selectedRoleId) {
      setRoleData(null);
      Swal.fire({
        title: 'Attention',
        text: 'Please select a system role to search.',
        icon: 'warning',
        confirmButtonColor: '#1e293b'
      });
      return;
    }

    setLoading(true);
    try {
      const response = await api.get(`/api/v1/roles/${selectedRoleId}/functionalities`);
      setRoleData(response.data);
    } catch (err) {
      Swal.fire('Error', 'Query failed', 'error');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-[#f8fafc] py-2 px-2 font-sans text-slate-900">
      <div className="max-w-xl mx-auto">

        {/* Compressed Header */}
        <div className="bg-white rounded-t-md border border-slate-200 p-2 shadow-sm border-b-0">
          <div className="flex items-center gap-2 mb-2">
            <div className="h-5 w-1 bg-indigo-600 rounded-full"></div>
            <h1 className="text-xs font-black text-slate-800 uppercase tracking-tight">Profile Audit</h1>
            <span className="text-[8px] text-slate-400 font-bold uppercase ml-auto tracking-widest">Core Access</span>
          </div>

          <form onSubmit={fetchRoleData} className="flex gap-1.5">
            <select
              value={selectedRoleId}
              onChange={(e) => setSelectedRoleId(e.target.value)}
              className="flex-1 text-[11px] p-1.5 bg-slate-50 border border-slate-200 rounded focus:ring-1 focus:ring-indigo-500 outline-none font-medium text-slate-700 cursor-pointer"
            >
              <option value="">Select role...</option>
              {roles.map((role) => (
                <option key={role.id || role.roleId} value={role.id || role.roleId}>
                  {role.name || role.roleName}
                </option>
              ))}
            </select>

            <button
              type="submit"
              disabled={loading}
              className={`px-3 py-1 rounded text-[9px] font-black uppercase tracking-widest transition-all ${
                loading ? 'bg-slate-300' : 'bg-slate-900 hover:bg-indigo-700 text-white active:scale-95'
              }`}
            >
              {loading ? '...' : 'Search'}
            </button>
          </form>
        </div>

        {roleData && (
          <div className="animate-in fade-in slide-in-from-top-1 duration-200">
            {/* Identity Bar - FINTECH text shifted left for visibility */}
            <div className="bg-slate-900 text-white py-1.5 px-3 border-x border-slate-900 relative overflow-hidden flex justify-between items-center">
              <div className="absolute right-[15px] top-[-2px] opacity-[0.05] text-4xl font-black italic pointer-events-none">
                FINTECH
              </div>
              <h2 className="text-sm font-black leading-none z-10">{roleData.roleName}</h2>
            </div>

            <div className="bg-white border border-slate-200 rounded-b-md p-1.5 shadow-lg">
              {roleData.description && (
                <div className="bg-indigo-50/30 rounded px-2 py-1 mb-2 border border-indigo-100/30">
                  <p className="text-[10px] text-slate-500 font-medium italic">"{roleData.description}"</p>
                </div>
              )}

              <div className="space-y-0">
                {roleData.functionalities?.length > 0 ? (
                  roleData.functionalities.map((func, index) => (
                    <FunctionalityNode
                      key={func.id}
                      item={func}
                      isLast={index === roleData.functionalities.length - 1}
                    />
                  ))
                ) : (
                  <div className="py-4 text-center text-[9px] font-bold text-slate-300 uppercase italic">Empty Profile</div>
                )}
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default RoleFunctQuery;
