import React from 'react';
import { useAppDispatch, useAppSelector } from '../hooks';
import { setSellFormField, setLoading, setError, setSuccess, resetSellForm } from '../store/slices/tradeSlice';
import { updateWalletBalance } from '../store/slices/walletSlice';
import { tradeAPI } from '../services/api';

const SellUsdcModal: React.FC<{ isOpen: boolean; onClose: () => void }> = ({ isOpen, onClose }) => {
  const dispatch = useAppDispatch();
  const { sellForm, isLoading, error, success } = useAppSelector((state) => state.trade);
  const { wallets } = useAppSelector((state) => state.wallet);

  if (!isOpen) return null;

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    dispatch(setSellFormField({ field: name as any, value }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!sellForm.walletId || !sellForm.amount || !sellForm.recipientAddress) {
      dispatch(setError('Please fill in all required fields'));
      return;
    }

    const selectedWallet = wallets.find((w) => w.id === sellForm.walletId);
    if (selectedWallet && selectedWallet.usdcBalance < parseFloat(sellForm.amount)) {
      dispatch(setError('Insufficient USDC balance'));
      return;
    }

    dispatch(setLoading(true));

    try {
      const response = await tradeAPI.sellUsdc({
        walletId: sellForm.walletId,
        amount: parseFloat(sellForm.amount),
        pricePerUnit: parseFloat(sellForm.pricePerUnit),
        recipientAddress: sellForm.recipientAddress,
        description: sellForm.description,
      });

      dispatch(setSuccess('Sell order created successfully!'));
      dispatch(resetSellForm());

      // Update wallet balance
      if (selectedWallet) {
        dispatch(
          updateWalletBalance({
            walletId: sellForm.walletId,
            usdcBalance: selectedWallet.usdcBalance - parseFloat(sellForm.amount),
          })
        );
      }

      setTimeout(() => {
        onClose();
      }, 2000);
    } catch (err: any) {
      dispatch(setError(err.response?.data?.message || 'Failed to create sell order'));
    } finally {
      dispatch(setLoading(false));
    }
  };

  const selectedWallet = wallets.find((w) => w.id === sellForm.walletId);
  const totalRevenue = sellForm.amount && sellForm.pricePerUnit
    ? (parseFloat(sellForm.amount) * parseFloat(sellForm.pricePerUnit)).toFixed(2)
    : '0.00';

  const fee = sellForm.amount ? (parseFloat(sellForm.amount) * 0.01).toFixed(2) : '0.00';
  const totalAfterFee = sellForm.amount && sellForm.pricePerUnit
    ? (parseFloat(totalRevenue) - parseFloat(fee)).toFixed(2)
    : '0.00';

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div className="bg-white rounded-lg shadow-xl p-8 w-full max-w-md">
        <div className="flex justify-between items-center mb-6">
          <h2 className="text-2xl font-bold text-gray-800">Sell USDC</h2>
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
              value={sellForm.walletId || ''}
              onChange={handleInputChange}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-green-500"
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

          {selectedWallet && (
            <div className="bg-blue-50 p-3 rounded">
              <p className="text-sm text-gray-600">Available Balance:</p>
              <p className="text-lg font-bold text-gray-800">{selectedWallet.usdcBalance} USDC</p>
            </div>
          )}

          <div>
            <label className="block text-gray-700 text-sm font-medium mb-2">Amount to Sell (USDC)</label>
            <input
              type="number"
              name="amount"
              value={sellForm.amount}
              onChange={handleInputChange}
              placeholder="Enter amount"
              step="0.01"
              min="0"
              max={selectedWallet?.usdcBalance || undefined}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-green-500"
              required
            />
          </div>

          <div>
            <label className="block text-gray-700 text-sm font-medium mb-2">Price per Unit (USD)</label>
            <input
              type="number"
              name="pricePerUnit"
              value={sellForm.pricePerUnit}
              onChange={handleInputChange}
              placeholder="1.0"
              step="0.01"
              min="0"
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-green-500"
              required
            />
          </div>

          <div>
            <label className="block text-gray-700 text-sm font-medium mb-2">Recipient Address</label>
            <input
              type="text"
              name="recipientAddress"
              value={sellForm.recipientAddress}
              onChange={handleInputChange}
              placeholder="0x..."
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-green-500"
              required
            />
          </div>

          <div>
            <label className="block text-gray-700 text-sm font-medium mb-2">Description (Optional)</label>
            <input
              type="text"
              name="description"
              value={sellForm.description}
              onChange={handleInputChange}
              placeholder="Add a note"
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-green-500"
            />
          </div>

          {/* Summary */}
          <div className="bg-gray-100 p-4 rounded-lg space-y-2">
            <div className="flex justify-between">
              <span className="text-gray-600">Revenue:</span>
              <span className="font-semibold">${totalRevenue}</span>
            </div>
            <div className="flex justify-between">
              <span className="text-gray-600">Fee (1%):</span>
              <span className="font-semibold">-${fee}</span>
            </div>
            <div className="border-t pt-2 flex justify-between">
              <span className="text-gray-800 font-semibold">You Receive:</span>
              <span className="font-bold text-lg text-green-600">${totalAfterFee}</span>
            </div>
          </div>

          <button
            type="submit"
            disabled={isLoading}
            className="w-full bg-green-500 hover:bg-green-600 disabled:bg-gray-400 text-white font-bold py-2 px-4 rounded-lg transition"
          >
            {isLoading ? 'Processing...' : 'Sell USDC'}
          </button>
        </form>
      </div>
    </div>
  );
};

export default SellUsdcModal;
