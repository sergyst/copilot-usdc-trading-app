import React from 'react';
import { useAppDispatch, useAppSelector } from '../hooks';
import { setBuyFormField, setLoading, setError, setSuccess, resetBuyForm } from '../store/slices/tradeSlice';
import { updateWalletBalance } from '../store/slices/walletSlice';
import { tradeAPI } from '../services/api';

const BuyUsdcModal: React.FC<{ isOpen: boolean; onClose: () => void }> = ({ isOpen, onClose }) => {
  const dispatch = useAppDispatch();
  const { buyForm, isLoading, error, success } = useAppSelector((state) => state.trade);
  const { wallets } = useAppSelector((state) => state.wallet);

  if (!isOpen) return null;

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    dispatch(setBuyFormField({ field: name as any, value }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!buyForm.walletId || !buyForm.amount) {
      dispatch(setError('Please fill in all required fields'));
      return;
    }

    dispatch(setLoading(true));

    try {
      const response = await tradeAPI.buyUsdc({
        walletId: buyForm.walletId,
        amount: parseFloat(buyForm.amount),
        pricePerUnit: parseFloat(buyForm.pricePerUnit),
        paymentMethod: buyForm.paymentMethod,
        description: buyForm.description,
      });

      dispatch(setSuccess('Buy order created successfully!'));
      dispatch(resetBuyForm());

      // Update wallet balance
      const selectedWallet = wallets.find((w) => w.id === buyForm.walletId);
      if (selectedWallet) {
        dispatch(
          updateWalletBalance({
            walletId: buyForm.walletId,
            usdcBalance: selectedWallet.usdcBalance + parseFloat(buyForm.amount),
          })
        );
      }

      setTimeout(() => {
        onClose();
      }, 2000);
    } catch (err: any) {
      dispatch(setError(err.response?.data?.message || 'Failed to create buy order'));
    } finally {
      dispatch(setLoading(false));
    }
  };

  const totalCost = buyForm.amount && buyForm.pricePerUnit
    ? (parseFloat(buyForm.amount) * parseFloat(buyForm.pricePerUnit)).toFixed(2)
    : '0.00';

  const fee = buyForm.amount ? (parseFloat(buyForm.amount) * 0.01).toFixed(2) : '0.00';
  const totalWithFee = buyForm.amount && buyForm.pricePerUnit
    ? (parseFloat(totalCost) + parseFloat(fee)).toFixed(2)
    : '0.00';

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div className="bg-white rounded-lg shadow-xl p-8 w-full max-w-md">
        <div className="flex justify-between items-center mb-6">
          <h2 className="text-2xl font-bold text-gray-800">Buy USDC</h2>
          <button
            onClick={onClose}
            className="text-gray-500 hover:text-gray-700 text-2xl leading-none"
          >
            ×
          </button>
        </div>

        {error && (
          <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
            {error}
          </div>
        )}

        {success && (
          <div className="bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded mb-4">
            {success}
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-gray-700 text-sm font-medium mb-2">Select Wallet</label>
            <select
              name="walletId"
              value={buyForm.walletId || ''}
              onChange={handleInputChange}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              required
            >
              <option value="">Choose a wallet...</option>
              {wallets.map((wallet) => (
                <option key={wallet.id} value={wallet.id}>
                  {wallet.walletAddress.slice(0, 10)}... ({wallet.usdcBalance} USDC)
                </option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-gray-700 text-sm font-medium mb-2">Amount (USDC)</label>
            <input
              type="number"
              name="amount"
              value={buyForm.amount}
              onChange={handleInputChange}
              placeholder="Enter amount"
              step="0.01"
              min="0"
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              required
            />
          </div>

          <div>
            <label className="block text-gray-700 text-sm font-medium mb-2">Price per Unit (USD)</label>
            <input
              type="number"
              name="pricePerUnit"
              value={buyForm.pricePerUnit}
              onChange={handleInputChange}
              placeholder="1.0"
              step="0.01"
              min="0"
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              required
            />
          </div>

          <div>
            <label className="block text-gray-700 text-sm font-medium mb-2">Payment Method</label>
            <select
              name="paymentMethod"
              value={buyForm.paymentMethod}
              onChange={handleInputChange}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              <option value="CREDIT_CARD">Credit Card</option>
              <option value="BANK_TRANSFER">Bank Transfer</option>
              <option value="ACH">ACH</option>
            </select>
          </div>

          <div>
            <label className="block text-gray-700 text-sm font-medium mb-2">Description (Optional)</label>
            <input
              type="text"
              name="description"
              value={buyForm.description}
              onChange={handleInputChange}
              placeholder="Add a note"
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>

          {/* Summary */}
          <div className="bg-gray-100 p-4 rounded-lg space-y-2">
            <div className="flex justify-between">
              <span className="text-gray-600">Subtotal:</span>
              <span className="font-semibold">${totalCost}</span>
            </div>
            <div className="flex justify-between">
              <span className="text-gray-600">Fee (1%):</span>
              <span className="font-semibold">${fee}</span>
            </div>
            <div className="border-t pt-2 flex justify-between">
              <span className="text-gray-800 font-semibold">Total:</span>
              <span className="font-bold text-lg text-blue-600">${totalWithFee}</span>
            </div>
          </div>

          <button
            type="submit"
            disabled={isLoading}
            className="w-full bg-blue-500 hover:bg-blue-600 disabled:bg-gray-400 text-white font-bold py-2 px-4 rounded-lg transition"
          >
            {isLoading ? 'Processing...' : 'Buy USDC'}
          </button>
        </form>
      </div>
    </div>
  );
};

export default BuyUsdcModal;
