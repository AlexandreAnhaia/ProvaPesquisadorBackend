import { createAsyncThunk, isFulfilled, isPending } from '@reduxjs/toolkit';
import axios from 'axios';

import { defaultValue, IPessoa } from 'app/shared/model/pessoa.model';
import { createEntitySlice, EntityState, IQueryParams, serializeAxiosError } from 'app/shared/reducers/reducer.utils';
import { cleanEntity } from 'app/shared/util/entity-utils';

const initialState: EntityState<IPessoa> = {
  loading: false,
  errorMessage: null,
  entities: [],
  entity: defaultValue,
  updating: false,
  totalItems: 0,
  updateSuccess: false,
};

const apiUrl = 'api/pessoas';
const apiUrlSearchByName = 'api/pessoas-search-name/';
const apiUrlSearchByCpf = 'api/pessoas-search-cpf/';
const apiUrlSearchByEmail = 'api/pessoas-search-email/';
const apiUrlSearchByDataNascimento = 'api/pessoas-search-birthdate/';

// Actions

export const getEntities = createAsyncThunk('pessoa/fetch_entity_list', async ({ page, size, sort }: IQueryParams) => {
  const requestUrl = `${apiUrl}${sort ? `?page=${page}&size=${size}&sort=${sort}&` : '?'}cacheBuster=${new Date().getTime()}`;
  return axios.get<IPessoa[]>(requestUrl);
});

export const getSearchEntitiesByName = createAsyncThunk('pessoa/fetch_entity_list', async (name: string) => {
  const requestUrl = `${apiUrlSearchByName}${name ? `?name=${name}` : ''}`;
  return axios.get<IPessoa[]>(requestUrl);
});

export const getSearchEntitiesByCpf = createAsyncThunk('pessoa/fetch_entity_list', async (cpf: string) => {
  const requestUrl = `${apiUrlSearchByCpf}${cpf ? `?cpf=${cpf}` : ''}`;
  return axios.get<IPessoa[]>(requestUrl);
});

export const getSearchEntitiesByEmail = createAsyncThunk('pessoa/fetch_entity_list', async (email: string) => {
  const requestUrl = `${apiUrlSearchByEmail}${email ? `?email=${email}` : ''}`;
  return axios.get<IPessoa[]>(requestUrl);
});

export const getSearchEntitiesByDataNascimneto = createAsyncThunk('pessoa/fetch_entity_list', async (birthdate: string) => {
  const requestUrl = `${apiUrlSearchByDataNascimento}${birthdate ? `?birthdate=${birthdate}` : ''}`;
  return axios.get<IPessoa[]>(requestUrl);
});

export const getEntity = createAsyncThunk(
  'pessoa/fetch_entity',
  async (id: string | number) => {
    const requestUrl = `${apiUrl}/${id}`;
    return axios.get<IPessoa>(requestUrl);
  },
  { serializeError: serializeAxiosError }
);

export const createEntity = createAsyncThunk(
  'pessoa/create_entity',
  async (entity: IPessoa, thunkAPI) => {
    const result = await axios.post<IPessoa>(apiUrl, cleanEntity(entity));
    thunkAPI.dispatch(getEntities({}));
    return result;
  },
  { serializeError: serializeAxiosError }
);

export const updateEntity = createAsyncThunk(
  'pessoa/update_entity',
  async (entity: IPessoa, thunkAPI) => {
    const result = await axios.put<IPessoa>(`${apiUrl}/${entity.id}`, cleanEntity(entity));
    thunkAPI.dispatch(getEntities({}));
    return result;
  },
  { serializeError: serializeAxiosError }
);

export const partialUpdateEntity = createAsyncThunk(
  'pessoa/partial_update_entity',
  async (entity: IPessoa, thunkAPI) => {
    const result = await axios.patch<IPessoa>(`${apiUrl}/${entity.id}`, cleanEntity(entity));
    thunkAPI.dispatch(getEntities({}));
    return result;
  },
  { serializeError: serializeAxiosError }
);

export const deleteEntity = createAsyncThunk(
  'pessoa/delete_entity',
  async (id: string | number, thunkAPI) => {
    const requestUrl = `${apiUrl}/${id}`;
    const result = await axios.delete<IPessoa>(requestUrl);
    thunkAPI.dispatch(getEntities({}));
    return result;
  },
  { serializeError: serializeAxiosError }
);

// slice

export const PessoaSlice = createEntitySlice({
  name: 'pessoa',
  initialState,
  extraReducers(builder) {
    builder
      .addCase(getEntity.fulfilled, (state, action) => {
        state.loading = false;
        state.entity = action.payload.data;
      })
      .addCase(deleteEntity.fulfilled, state => {
        state.updating = false;
        state.updateSuccess = true;
        state.entity = {};
      })
      .addMatcher(isFulfilled(getEntities), (state, action) => {
        const { data, headers } = action.payload;

        return {
          ...state,
          loading: false,
          entities: data,
          totalItems: parseInt(headers['x-total-count'], 10),
        };
      })
      .addMatcher(isFulfilled(createEntity, updateEntity, partialUpdateEntity), (state, action) => {
        state.updating = false;
        state.loading = false;
        state.updateSuccess = true;
        state.entity = action.payload.data;
      })
      .addMatcher(isPending(getEntities, getEntity), state => {
        state.errorMessage = null;
        state.updateSuccess = false;
        state.loading = true;
      })
      .addMatcher(isPending(createEntity, updateEntity, partialUpdateEntity, deleteEntity), state => {
        state.errorMessage = null;
        state.updateSuccess = false;
        state.updating = true;
      });
  },
});

export const { reset } = PessoaSlice.actions;

// Reducer
export default PessoaSlice.reducer;
