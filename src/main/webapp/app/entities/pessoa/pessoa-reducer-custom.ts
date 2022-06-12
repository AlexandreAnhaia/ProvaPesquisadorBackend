import { createAsyncThunk, createSlice, isPending } from '@reduxjs/toolkit';
import axios from 'axios';

import { createEntitySlice } from 'app/shared/reducers/reducer.utils';

const initialState = {
  loading: false,
  exists: false,
};

export type checkExists = Readonly<typeof initialState>;

const apiCheckExists = 'api/check-exists/';

// Actions

export const checkExists = createAsyncThunk('pessoa/fetch_check_exists', async (cpf: string) => {
  const requestUrl = `${apiCheckExists}${cpf}`;
  return axios.get<boolean>(requestUrl);
});

// slice

export const slice = createSlice({
  name: 'checkExists',
  initialState,
  reducers: {},
  extraReducers(builder) {
    builder
      .addCase(checkExists.fulfilled, (state, action) => {
        state.loading = false;
        state.exists = action.payload.data;
      })
      .addMatcher(isPending(checkExists), state => {
        state.loading = true;
      });
  },
});

// Reducer
export default slice.reducer;
