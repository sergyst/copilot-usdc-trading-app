import { createSlice, PayloadAction } from '@reduxjs/toolkit';

interface TradeState {
  buyForm: {
    walletId: number | null;
    amount: string;
    pricePerUnit: string;
    paymentMethod: string;
    description: string;
  };
  sellForm: {
    walletId: number | null;
    amount: string;
    pricePerUnit: string;
    recipientAddress: string;
    description: string;
  };
  isLoading: boolean;
  error: string | null;
  success: string | null;
}

const initialState: TradeState = {
  buyForm: {
    walletId: null,
    amount: '',
    pricePerUnit: '1.0',
    paymentMethod: 'CREDIT_CARD',
    description: '',
  },
  sellForm: {
    walletId: null,
    amount: '',
    pricePerUnit: '1.0',
    recipientAddress: '',
    description: '',
  },
  isLoading: false,
  error: null,
  success: null,
};

const tradeSlice = createSlice({
  name: 'trade',
  initialState,
  reducers: {
    setBuyFormField: (state, action: PayloadAction<{ field: keyof typeof state.buyForm; value: string | number }>) => {
      state.buyForm = {
        ...state.buyForm,
        [action.payload.field]: action.payload.value,
      };
    },
    setSellFormField: (state, action: PayloadAction<{ field: keyof typeof state.sellForm; value: string | number }>) => {
      state.sellForm = {
        ...state.sellForm,
        [action.payload.field]: action.payload.value,
      };
    },
    setLoading: (state, action: PayloadAction<boolean>) => {
      state.isLoading = action.payload;
    },
    setError: (state, action: PayloadAction<string>) => {
      state.error = action.payload;
      state.success = null;
    },
    setSuccess: (state, action: PayloadAction<string>) => {
      state.success = action.payload;
      state.error = null;
    },
    clearMessages: (state) => {
      state.error = null;
      state.success = null;
    },
    resetBuyForm: (state) => {
      state.buyForm = initialState.buyForm;
    },
    resetSellForm: (state) => {
      state.sellForm = initialState.sellForm;
    },
  },
});

export const {
  setBuyFormField,
  setSellFormField,
  setLoading,
  setError,
  setSuccess,
  clearMessages,
  resetBuyForm,
  resetSellForm,
} = tradeSlice.actions;
export default tradeSlice.reducer;
