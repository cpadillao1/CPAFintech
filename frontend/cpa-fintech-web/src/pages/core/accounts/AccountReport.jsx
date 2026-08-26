import React, { useState } from 'react';
import { PDFDownloadLink } from '@react-pdf/renderer';
import api from '../../../api/axiosConfig';
import ReportPDF from './ReportPDF';

const AccountReport = () => {
    const [date, setDate] = useState(new Date().toISOString().split('T')[0]);
    const [report, setReport] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    const fetchReport = async () => {
        setLoading(true);
        setError(null);
        try {
            const response = await api.get('/api/v1/reports/summary', {
                params: { date: date }
            });
            setReport(response.data);
        } catch (err) {
            setError('Unable to retrieve the report. Please try again later.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="ig-container max-w-4xl mx-auto">
            {/* Form Section - Ajustado con max-w-2xl para evitar que se estire demasiado */}
            <div className="ig-card max-w-xl mx-auto p-3 mb-6">
                <h2 className="ig-card-title mb-4">Account Consolidated Report</h2>

                {/* Contenedor flex para alinear input y botón */}
                <div className="flex items-end gap-3">
                    {/* Input Date - Flex 1 para que tome el espacio necesario */}
                    <div className="flex-1">
                        <label className="ig-label block mb-1">Select Date</label>
                        <input
                            type="date"
                            value={date}
                            onChange={(e) => setDate(e.target.value)}
                            className="ig-input w-full"
                        />
                    </div>

                    {/* Botón - Flex none para que no se estire */}
                    <div className="flex-none">
                        <button
                            onClick={fetchReport}
                            disabled={loading}
                            className="ig-btn-navy btn-standard whitespace-nowrap"
                        >
                            {loading ? 'Processing...' : 'Generate Report'}
                        </button>
                    </div>
                </div>
            </div>


            {/* Error Handling */}
            {error && (
                <div className="ig-card mb-6 max-w-2xl mx-auto border-red-200 bg-red-50 text-red-700">
                    {error}
                </div>
            )}

            {/* Results Section */}
            {report && (
                <>
                    <div className="ig-card">
                        <div className="flex justify-between items-center mb-6 border-b pb-4">
                            <h2 className="ig-card-title m-0">Report Summary</h2>
                            <span className="ig-badge">Date: {report.reportDate}</span>
                        </div>

                        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                            <MetricCard label="Total Available" value={report.totalAvailable} color="border-indigo-600" textColor="text-indigo-700" />
                            <MetricCard label="Total Balance" value={report.totalBalance} color="border-slate-700" textColor="text-slate-800" />
                            <MetricCard label="Total Holds" value={report.totalHolds} color="border-amber-500" textColor="text-amber-700" />
                            <MetricCard label="Accrued Interest" value={report.totalAccruedInterest} color="border-blue-500" textColor="text-blue-700" />
                            <MetricCard label="Total Debits (ND)" value={report.totalNd} color="border-red-500" textColor="text-red-700" />
                            <MetricCard label="Total Credits (NC)" value={report.totalNc} color="border-emerald-500" textColor="text-emerald-700" />
                        </div>
                    </div>

                    <div className="mt-6 flex justify-end">
                        <PDFDownloadLink
                            document={<ReportPDF data={report} logoUrl="/logo.png" />}
                            fileName={`AccountReport_${report.reportDate}.pdf`}
                            className="ig-btn-navy"
                            style={{ textDecoration: 'none', width: '200px' }}
                        >
                            {({ loading }) => (loading ? 'Preparing Document...' : 'Download PDF Report')}
                        </PDFDownloadLink>
                    </div>
                </>
            )}
        </div>
    );
};

const MetricCard = ({ label, value, color, textColor }) => (
    <div className={`p-3 bg-slate-50 border-l-4 ${color} rounded-r-lg shadow-sm hover:shadow-md transition-shadow`}>
        <p className="text-[0.7rem] font-bold text-slate-500 uppercase tracking-widest">{label}</p>
        <p className={`text-2xl font-bold ${textColor} mt-1`}>
            {new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(value || 0)}
        </p>
    </div>
);

export default AccountReport;
