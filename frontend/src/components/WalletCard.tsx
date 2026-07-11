import React from 'react';

interface WalletCardProps {
  walletAddress: string;
  usdcBalance: number;
  ethBalance: number;
  onSelect?: (walletAddress: string) => void;
}

const WalletCard: React.FC<WalletCardProps> = ({ walletAddress, usdcBalance, ethBalance, onSelect }) => {
  const shortAddress = `${walletAddress.slice(0, 6)}...${walletAddress.slice(-4)}`;

  return (
    <div
      onClick={() => onSelect?.(walletAddress)}
      className="bg-gradient-to-br from-blue-500 to-purple-600 rounded-lg shadow-lg p-6 text-white cursor-pointer hover:shadow-xl transition"
    >
      <div className="flex justify-between items-start mb-8">
        <div>
          <p className="text-sm opacity-80">Wallet Address</p>
          <p className="text-lg font-mono font-bold">{shortAddress}</p>
        </div>
        <span className="text-2xl">💼</span>
      </div>

      <div className="grid grid-cols-2 gap-4">
        <div>
          <p className="text-sm opacity-80">USDC Balance</p>
          <p className="text-2xl font-bold">{usdcBalance.toFixed(2)}</p>
          <p className="text-xs opacity-75">USDC</p>
        </div>
        <div>
          <p className="text-sm opacity-80">ETH Balance</p>
          <p className="text-2xl font-bold">{ethBalance.toFixed(4)}</p>
          <p className="text-xs opacity-75">ETH</p>
        </div>
      </div>
    </div>
  );
};

export default WalletCard;
