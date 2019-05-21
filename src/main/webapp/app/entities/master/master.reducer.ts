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

import { IMaster, defaultValue } from 'app/shared/model/master.model';

export const ACTION_TYPES = {
  SEARCH_MASTERS: 'master/SEARCH_MASTERS',
  FETCH_MASTER_LIST: 'master/FETCH_MASTER_LIST',
  FETCH_MASTER: 'master/FETCH_MASTER',
  CREATE_MASTER: 'master/CREATE_MASTER',
  UPDATE_MASTER: 'master/UPDATE_MASTER',
  DELETE_MASTER: 'master/DELETE_MASTER',
  RESET: 'master/RESET'
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<IMaster>,
  entity: defaultValue,
  links: { next: 0 },
  updating: false,
  totalItems: 0,
  updateSuccess: false
};

export type MasterState = Readonly<typeof initialState>;

// Reducer

export default (state: MasterState = initialState, action): MasterState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.SEARCH_MASTERS):
    case REQUEST(ACTION_TYPES.FETCH_MASTER_LIST):
    case REQUEST(ACTION_TYPES.FETCH_MASTER):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true
      };
    case REQUEST(ACTION_TYPES.CREATE_MASTER):
    case REQUEST(ACTION_TYPES.UPDATE_MASTER):
    case REQUEST(ACTION_TYPES.DELETE_MASTER):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true
      };
    case FAILURE(ACTION_TYPES.SEARCH_MASTERS):
    case FAILURE(ACTION_TYPES.FETCH_MASTER_LIST):
    case FAILURE(ACTION_TYPES.FETCH_MASTER):
    case FAILURE(ACTION_TYPES.CREATE_MASTER):
    case FAILURE(ACTION_TYPES.UPDATE_MASTER):
    case FAILURE(ACTION_TYPES.DELETE_MASTER):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload
      };
    case SUCCESS(ACTION_TYPES.SEARCH_MASTERS):
    case SUCCESS(ACTION_TYPES.FETCH_MASTER_LIST):
      const links = parseHeaderForLinks(action.payload.headers.link);
      return {
        ...state,
        links,
        loading: false,
        totalItems: action.payload.headers['x-total-count'],
        entities: loadMoreDataWhenScrolled(state.entities, action.payload.data, links)
      };
    case SUCCESS(ACTION_TYPES.FETCH_MASTER):
      return {
        ...state,
        loading: false,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.CREATE_MASTER):
    case SUCCESS(ACTION_TYPES.UPDATE_MASTER):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.DELETE_MASTER):
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

const apiUrl = 'api/masters';
const apiSearchUrl = 'api/_search/masters';

// Actions

export const getSearchEntities: ICrudSearchAction<IMaster> = (query, page, size, sort) => ({
  type: ACTION_TYPES.SEARCH_MASTERS,
  payload: axios.get<IMaster>(`${apiSearchUrl}?query=${query}${sort ? `&page=${page}&size=${size}&sort=${sort}` : ''}`)
});

export const getEntities: ICrudGetAllAction<IMaster> = (page, size, sort) => {
  const requestUrl = `${apiUrl}${sort ? `?page=${page}&size=${size}&sort=${sort}` : ''}`;
  return {
    type: ACTION_TYPES.FETCH_MASTER_LIST,
    payload: axios.get<IMaster>(`${requestUrl}${sort ? '&' : '?'}cacheBuster=${new Date().getTime()}`)
  };
};

export const getEntity: ICrudGetAction<IMaster> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_MASTER,
    payload: axios.get<IMaster>(requestUrl)
  };
};

export const createEntity: ICrudPutAction<IMaster> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_MASTER,
    payload: axios.post(apiUrl, cleanEntity(entity))
  });
  return result;
};

export const updateEntity: ICrudPutAction<IMaster> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_MASTER,
    payload: axios.put(apiUrl, cleanEntity(entity))
  });
  return result;
};

export const deleteEntity: ICrudDeleteAction<IMaster> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_MASTER,
    payload: axios.delete(requestUrl)
  });
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET
});
