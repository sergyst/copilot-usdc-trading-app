import React, { useEffect } from 'react';
import { useAppDispatch, useAppSelector } from '../hooks';
import { setLoading, setTransactions } from '../store/slices/transactionSlice';
import { tradeAPI } from '../services/api';

const TransactionHistory: React.FC = () => {
  const dispatch = useAppDispatch();
  const { transactions, isLoading } = useAppSelector((state) => state.transaction);

  useEffect(() => {
    fetchTransactions();
  }, []);

  const fetchTransactions = async () => {
    dispatch(setLoading(true));
    try {
      const response = await tradeAPI.getTransactions(0, 20);
      dispatch(setTransactions(response.data.content || response.data));
    } catch (error) {
      console.error('Error fetching transactions:', error);
    } finally {
      dispatch(setLoading(false));
    }
  };

  const getStatusBadgeColor = (status: string) => {
    switch (status?.toUpperCase()) {
      case 'COMPLETED':
        return 'bg-green-100 text-green-800';
      case 'PROCESSING':
        return 'bg-blue-100 text-blue-800';
      case 'PENDING':
        return 'bg-yellow-100 text-yellow-800';
      case 'FAILED':
        return 'bg-red-100 text-red-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  const getTypeIcon = (type: string) => {
    switch (type?.toUpperCase()) {
      case 'BUY':
        return '🔵';
      case 'SELL':
        return '🟢';
      case 'TRANSFER':
        return '↔️';
      default:
        return '💱';
    }
  };

  if (isLoading) {
    return <div className="text-center py-8">Loading transactions...</div>;
  }

  if (transactions.length === 0) {
    return (
      <div className="text-center py-8 text-gray-500">
        No transactions yet. Start trading to see your history here.
      </div>
    );
  }

  return (
    <div className="overflow-x-auto">
      <table className="w-full text-sm">
        <thead className="bg-gray-200 border-b-2 border-gray-300">
          <tr>
            <th className="px-4 py-2 text-left">Type</th>
            <th className="px-4 py-2 text-left">Amount</th>
            <th className="px-4 py-2 text-left">Price/Unit</th>
            <th className="px-4 py-2 text-left">Total Value</th>
            <th className="px-4 py-2 text-left">Fee</th>
            <th className="px-4 py-2 text-left">Status</th>
            <th className="px-4 py-2 text-left">Date</th>
          </tr>
        </thead>
        <tbody>
          {transactions.map((tx) => (
            <tr key={tx.id} className="border-b border-gray-200 hover:bg-gray-50">
              <td className="px-4 py-3">
                <div className="flex items-center gap-2">
                  <span>{getTypeIcon(tx.type)}</span>
                  <span className="font-semibold">{tx.type.toUpperCase()}</span>
                </div>
              </td>
              <td className="px-4 py-3 font-semibold">{tx.amount.toFixed(2)} USDC</td>
              <td className="px-4 py-3">${tx.pricePerUnit.toFixed(2)}</td>
              <td className="px-4 py-3 font-semibold">${tx.totalValue.toFixed(2)}</td>
              <td className="px-4 py-3 text-red-600">${tx.feeAmount.toFixed(4)}</td>
              <td className="px-4 py-3">
                <span className={`px-3 py-1 rounded-full text-xs font-semibold ${getStatusBadgeColor(tx.status)}`}>
                  {tx.status}
                </span>
              </td>
              <td className="px-4 py-3 text-xs text-gray-500">
                {new Date(tx.createdAt).toLocaleDateString()}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default TransactionHistory;
