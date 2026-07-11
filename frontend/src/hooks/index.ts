import { useDispatch, useSelector } from 'react-redux';
import type { RootState, AppDispatch } from '../store/store';

export const useAppDispatch = () => useDispatch<AppDispatch>();
export const useAppSelector = useSelector.bind(null) as (<Selected = unknown>(
  selector: (state: RootState) => Selected,
) => Selected);
