import axios from 'axios';
import {
  ICrudSearchAction,
  parseHeaderForLinks,
  loadMoreDataWhenScrolled,
  ICrudGetAction,
  ICrudGetAllAction,
  ICrudPutAction,
  ICrudDeleteAction
} from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';

import { ISalon, defaultValue } from 'app/shared/model/salon.model';

export const ACTION_TYPES = {
  SEARCH_SALONS: 'salon/SEARCH_SALONS',
  FETCH_SALON_LIST: 'salon/FETCH_SALON_LIST',
  FETCH_SALON: 'salon/FETCH_SALON',
  CREATE_SALON: 'salon/CREATE_SALON',
  UPDATE_SALON: 'salon/UPDATE_SALON',
  DELETE_SALON: 'salon/DELETE_SALON',
  RESET: 'salon/RESET'
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<ISalon>,
  entity: defaultValue,
  links: { next: 0 },
  updating: false,
  totalItems: 0,
  updateSuccess: false
};

export type SalonState = Readonly<typeof initialState>;

// Reducer

export default (state: SalonState = initialState, action): SalonState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.SEARCH_SALONS):
    case REQUEST(ACTION_TYPES.FETCH_SALON_LIST):
    case REQUEST(ACTION_TYPES.FETCH_SALON):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true
      };
    case REQUEST(ACTION_TYPES.CREATE_SALON):
    case REQUEST(ACTION_TYPES.UPDATE_SALON):
    case REQUEST(ACTION_TYPES.DELETE_SALON):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true
      };
    case FAILURE(ACTION_TYPES.SEARCH_SALONS):
    case FAILURE(ACTION_TYPES.FETCH_SALON_LIST):
    case FAILURE(ACTION_TYPES.FETCH_SALON):
    case FAILURE(ACTION_TYPES.CREATE_SALON):
    case FAILURE(ACTION_TYPES.UPDATE_SALON):
    case FAILURE(ACTION_TYPES.DELETE_SALON):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload
      };
    case SUCCESS(ACTION_TYPES.SEARCH_SALONS):
    case SUCCESS(ACTION_TYPES.FETCH_SALON_LIST):
      const links = parseHeaderForLinks(action.payload.headers.link);
      return {
        ...state,
        links,
        loading: false,
        totalItems: action.payload.headers['x-total-count'],
        entities: loadMoreDataWhenScrolled(state.entities, action.payload.data, links)
      };
    case SUCCESS(ACTION_TYPES.FETCH_SALON):
      return {
        ...state,
        loading: false,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.CREATE_SALON):
    case SUCCESS(ACTION_TYPES.UPDATE_SALON):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.DELETE_SALON):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: {}
      };
    case ACTION_TYPES.RESET:
      return {
        ...initialState
      };
    default:
      return state;
  }
};

const apiUrl = 'api/salons';
const apiSearchUrl = 'api/_search/salons';

// Actions

export const getSearchEntities: ICrudSearchAction<ISalon> = (query, page, size, sort) => ({
  type: ACTION_TYPES.SEARCH_SALONS,
  payload: axios.get<ISalon>(`${apiSearchUrl}?query=${query}${sort ? `&page=${page}&size=${size}&sort=${sort}` : ''}`)
});

export const getEntities: ICrudGetAllAction<ISalon> = (page, size, sort) => {
  const requestUrl = `${apiUrl}${sort ? `?page=${page}&size=${size}&sort=${sort}` : ''}`;
  return {
    type: ACTION_TYPES.FETCH_SALON_LIST,
    payload: axios.get<ISalon>(`${requestUrl}${sort ? '&' : '?'}cacheBuster=${new Date().getTime()}`)
  };
};

export const getEntity: ICrudGetAction<ISalon> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_SALON,
    payload: axios.get<ISalon>(requestUrl)
  };
};

export const createEntity: ICrudPutAction<ISalon> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_SALON,
    payload: axios.post(apiUrl, cleanEntity(entity))
  });
  return result;
};

export const updateEntity: ICrudPutAction<ISalon> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_SALON,
    payload: axios.put(apiUrl, cleanEntity(entity))
  });
  return result;
};

export const deleteEntity: ICrudDeleteAction<ISalon> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_SALON,
    payload: axios.delete(requestUrl)
  });
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET
});
