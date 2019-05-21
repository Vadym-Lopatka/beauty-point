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

import { IOption, defaultValue } from 'app/shared/model/option.model';

export const ACTION_TYPES = {
  SEARCH_OPTIONS: 'option/SEARCH_OPTIONS',
  FETCH_OPTION_LIST: 'option/FETCH_OPTION_LIST',
  FETCH_OPTION: 'option/FETCH_OPTION',
  CREATE_OPTION: 'option/CREATE_OPTION',
  UPDATE_OPTION: 'option/UPDATE_OPTION',
  DELETE_OPTION: 'option/DELETE_OPTION',
  RESET: 'option/RESET'
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<IOption>,
  entity: defaultValue,
  links: { next: 0 },
  updating: false,
  totalItems: 0,
  updateSuccess: false
};

export type OptionState = Readonly<typeof initialState>;

// Reducer

export default (state: OptionState = initialState, action): OptionState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.SEARCH_OPTIONS):
    case REQUEST(ACTION_TYPES.FETCH_OPTION_LIST):
    case REQUEST(ACTION_TYPES.FETCH_OPTION):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true
      };
    case REQUEST(ACTION_TYPES.CREATE_OPTION):
    case REQUEST(ACTION_TYPES.UPDATE_OPTION):
    case REQUEST(ACTION_TYPES.DELETE_OPTION):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true
      };
    case FAILURE(ACTION_TYPES.SEARCH_OPTIONS):
    case FAILURE(ACTION_TYPES.FETCH_OPTION_LIST):
    case FAILURE(ACTION_TYPES.FETCH_OPTION):
    case FAILURE(ACTION_TYPES.CREATE_OPTION):
    case FAILURE(ACTION_TYPES.UPDATE_OPTION):
    case FAILURE(ACTION_TYPES.DELETE_OPTION):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload
      };
    case SUCCESS(ACTION_TYPES.SEARCH_OPTIONS):
    case SUCCESS(ACTION_TYPES.FETCH_OPTION_LIST):
      const links = parseHeaderForLinks(action.payload.headers.link);
      return {
        ...state,
        links,
        loading: false,
        totalItems: action.payload.headers['x-total-count'],
        entities: loadMoreDataWhenScrolled(state.entities, action.payload.data, links)
      };
    case SUCCESS(ACTION_TYPES.FETCH_OPTION):
      return {
        ...state,
        loading: false,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.CREATE_OPTION):
    case SUCCESS(ACTION_TYPES.UPDATE_OPTION):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.DELETE_OPTION):
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

const apiUrl = 'api/options';
const apiSearchUrl = 'api/_search/options';

// Actions

export const getSearchEntities: ICrudSearchAction<IOption> = (query, page, size, sort) => ({
  type: ACTION_TYPES.SEARCH_OPTIONS,
  payload: axios.get<IOption>(`${apiSearchUrl}?query=${query}${sort ? `&page=${page}&size=${size}&sort=${sort}` : ''}`)
});

export const getEntities: ICrudGetAllAction<IOption> = (page, size, sort) => {
  const requestUrl = `${apiUrl}${sort ? `?page=${page}&size=${size}&sort=${sort}` : ''}`;
  return {
    type: ACTION_TYPES.FETCH_OPTION_LIST,
    payload: axios.get<IOption>(`${requestUrl}${sort ? '&' : '?'}cacheBuster=${new Date().getTime()}`)
  };
};

export const getEntity: ICrudGetAction<IOption> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_OPTION,
    payload: axios.get<IOption>(requestUrl)
  };
};

export const createEntity: ICrudPutAction<IOption> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_OPTION,
    payload: axios.post(apiUrl, cleanEntity(entity))
  });
  return result;
};

export const updateEntity: ICrudPutAction<IOption> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_OPTION,
    payload: axios.put(apiUrl, cleanEntity(entity))
  });
  return result;
};

export const deleteEntity: ICrudDeleteAction<IOption> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_OPTION,
    payload: axios.delete(requestUrl)
  });
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET
});
