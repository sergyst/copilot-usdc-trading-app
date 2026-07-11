import { createSlice, PayloadAction } from '@reduxjs/toolkit';

interface Transaction {
  id: number;
  type: string;
  amount: number;
  pricePerUnit: number;
  totalValue: number;
  feeAmount: number;
  status: string;
  transactionHash: string;
  fromAddress: string;
  toAddress: string;
  description: string;
  createdAt: string;
  updatedAt: string;
}

interface TransactionState {
  transactions: Transaction[];
  isLoading: boolean;
  error: string | null;
}

const initialState: TransactionState = {
  transactions: [],
  isLoading: false,
  error: null,
};

const transactionSlice = createSlice({
  name: 'transaction',
  initialState,
  reducers: {
    setLoading: (state, action: PayloadAction<boolean>) => {
      state.isLoading = action.payload;
    },
    setTransactions: (state, action: PayloadAction<Transaction[]>) => {
      state.transactions = action.payload;
    },
    addTransaction: (state, action: PayloadAction<Transaction>) => {
      state.transactions.unshift(action.payload);
    },
    updateTransaction: (state, action: PayloadAction<Transaction>) => {
      const index = state.transactions.findIndex(t => t.id === action.payload.id);
      if (index !== -1) {
        state.transactions[index] = action.payload;
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

export const { setLoading, setTransactions, addTransaction, updateTransaction, setError, clearError } =
  transactionSlice.actions;
export default transactionSlice.reducer;
