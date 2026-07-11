import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import { BigNumberish } from 'ethers';

interface Wallet {
  id: number;
  walletAddress: string;
  usdcBalance: number;
  ethBalance: number;
  walletType: string;
  isDefault: boolean;
  createdAt: string;
  updatedAt: string;
}

interface WalletState {
  wallets: Wallet[];
  activeWallet: Wallet | null;
  isLoading: boolean;
  error: string | null;
}

const initialState: WalletState = {
  wallets: [],
  activeWallet: null,
  isLoading: false,
  error: null,
};

const walletSlice = createSlice({
  name: 'wallet',
  initialState,
  reducers: {
    setLoading: (state, action: PayloadAction<boolean>) => {
      state.isLoading = action.payload;
    },
    setWallets: (state, action: PayloadAction<Wallet[]>) => {
      state.wallets = action.payload;
    },
    setActiveWallet: (state, action: PayloadAction<Wallet>) => {
      state.activeWallet = action.payload;
    },
    updateWalletBalance: (state, action: PayloadAction<{ walletId: number; usdcBalance: number }>) => {
      const wallet = state.wallets.find(w => w.id === action.payload.walletId);
      if (wallet) {
        wallet.usdcBalance = action.payload.usdcBalance;
      }
      if (state.activeWallet?.id === action.payload.walletId) {
        state.activeWallet.usdcBalance = action.payload.usdcBalance;
      }
    },
    setError: (state, action: PayloadAction<string>) => {
      state.error = action.payload;
    },
    clearError: (state) => {
      state.error = null;
    },
  },
});

export const { setLoading, setWallets, setActiveWallet, updateWalletBalance, setError, clearError } = walletSlice.actions;
export default walletSlice.reducer;
