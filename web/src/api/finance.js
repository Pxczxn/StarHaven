import request from './request';

export const getFinanceSummary = (params) => request.get('/finance/summary', { params });

export const getFinanceRevenue = (params) => request.get('/finance/revenue', { params });

export const getPaymentMethods = (params) => request.get('/finance/payment-methods', { params });

export const getUnpaidOrders = () => request.get('/finance/unpaid-orders');

export const getRefundOrders = () => request.get('/finance/refund-orders');

export const exportFinanceData = (params) => request.get('/finance/export', { params });
