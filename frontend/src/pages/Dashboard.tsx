import React, { useEffect, useState } from 'react';
import { useAppDispatch, useAppSelector } from '../hooks';
import { setWallets, setActiveWallet, setLoading } from '../store/slices/walletSlice';
import { walletAPI } from '../services/api';
import BuyUsdcModal from '../components/BuyUsdcModal';
import SellUsdcModal from '../components/SellUsdcModal';
import TransactionHistory from '../components/TransactionHistory';
import WalletCard from '../components/WalletCard';

const Dashboard: React.FC = () => {
  const dispatch = useAppDispatch();
  const { wallets, activeWallet, isLoading } = useAppSelector((state) => state.wallet);
  const [isBuyModalOpen, setIsBuyModalOpen] = useState(false);
  const [isSellModalOpen, setIsSellModalOpen] = useState(false);

  useEffect(() => {
    fetchWallets();
  }, []);

  const fetchWallets = async () => {
    dispatch(setLoading(true));
    try {
      const response = await walletAPI.getWallets();
      const walletList = response.data.content || response.data;
      dispatch(setWallets(walletList));
      if (walletList.length > 0) {
        dispatch(setActiveWallet(walletList[0]));
      }
    } catch (error) {
      console.error('Error fetching wallets:', error);
    } finally {
      dispatch(setLoading(false));
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-50 to-gray-100 p-8">
      {/* Header */}
      <div className="mb-8">
        <h1 className="text-4xl font-bold text-gray-800 mb-2">USDC Trading Dashboard</h1>
        <p className="text-gray-600">Buy, sell, and manage your USDC tokens</p>
      </div>

      {/* Wallet Section */}
      <div className="bg-white rounded-lg shadow-lg p-6 mb-8">
        <h2 className="text-2xl font-bold text-gray-800 mb-4">Your Wallets</h2>

        {isLoading ? (
          <div className="text-center py-8 text-gray-500">Loading wallets...</div>
        ) : wallets.length > 0 ? (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {wallets.map((wallet) => (
              <WalletCard
                key={wallet.id}
                walletAddress={wallet.walletAddress}
                usdcBalance={wallet.usdcBalance}
                ethBalance={wallet.ethBalance}
                onSelect={() => dispatch(setActiveWallet(wallet))}
              />
            ))}
          </div>
        ) : (
          <div className="text-center py-8 text-gray-500">
            No wallets found. Create a wallet to get started.
          </div>
        )}
      </div>

      {/* Quick Actions */}
      <div className="bg-white rounded-lg shadow-lg p-6 mb-8">
        <h2 className="text-2xl font-bold text-gray-800 mb-4">Quick Actions</h2>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <button
            onClick={() => setIsBuyModalOpen(true)}
            className="bg-blue-500 hover:bg-blue-600 text-white font-bold py-3 px-6 rounded-lg transition shadow-md hover:shadow-lg"
          >
            💳 Buy USDC
          </button>
          <button
            onClick={() => setIsSellModalOpen(true)}
            className="bg-green-500 hover:bg-green-600 text-white font-bold py-3 px-6 rounded-lg transition shadow-md hover:shadow-lg"
          >
            💰 Sell USDC
          </button>
          <button className="bg-purple-500 hover:bg-purple-600 text-white font-bold py-3 px-6 rounded-lg transition shadow-md hover:shadow-lg">
            ↔️ Transfer
          </button>
        </div>
      </div>

      {/* Stats Cards */}
      {activeWallet && (
        <div className="bg-white rounded-lg shadow-lg p-6 mb-8">
          <h2 className="text-2xl font-bold text-gray-800 mb-4">Active Wallet Summary</h2>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
            <div className="bg-blue-50 p-4 rounded-lg">
              <p className="text-gray-600 text-sm">USDC Balance</p>
              <p className="text-3xl font-bold text-blue-600">{activeWallet.usdcBalance.toFixed(2)}</p>
              <p className="text-xs text-gray-500 mt-1">≈ ${(activeWallet.usdcBalance * 1).toFixed(2)}</p>
            </div>
            <div className="bg-green-50 p-4 rounded-lg">
              <p className="text-gray-600 text-sm">ETH Balance</p>
              <p className="text-3xl font-bold text-green-600">{activeWallet.ethBalance.toFixed(4)}</p>
              <p className="text-xs text-gray-500 mt-1">≈ ${(activeWallet.ethBalance * 2500).toFixed(2)}</p>
            </div>
            <div className="bg-purple-50 p-4 rounded-lg">
              <p className="text-gray-600 text-sm">Total Value</p>
              <p className="text-3xl font-bold text-purple-600">
                ${((activeWallet.usdcBalance + activeWallet.ethBalance * 2500).toFixed(2))}
              </p>
              <p className="text-xs text-gray-500 mt-1">All assets</p>
            </div>
            <div className="bg-orange-50 p-4 rounded-lg">
              <p className="text-gray-600 text-sm">Transaction Fee</p>
              <p className="text-3xl font-bold text-orange-600">1%</p>
              <p className="text-xs text-gray-500 mt-1">Per trade</p>
            </div>
          </div>
        </div>
      )}

      {/* Transaction History */}
      <div className="bg-white rounded-lg shadow-lg p-6">
        <h2 className="text-2xl font-bold text-gray-800 mb-4">Recent Transactions</h2>
        <TransactionHistory />
      </div>

      {/* Modals */}
      <BuyUsdcModal isOpen={isBuyModalOpen} onClose={() => setIsBuyModalOpen(false)} />
      <SellUsdcModal isOpen={isSellModalOpen} onClose={() => setIsSellModalOpen(false)} />
    </div>
  );
};

export default Dashboard;
