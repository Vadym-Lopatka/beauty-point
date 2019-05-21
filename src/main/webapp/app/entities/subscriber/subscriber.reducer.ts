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

import { ISubscriber, defaultValue } from 'app/shared/model/subscriber.model';

export const ACTION_TYPES = {
  SEARCH_SUBSCRIBERS: 'subscriber/SEARCH_SUBSCRIBERS',
  FETCH_SUBSCRIBER_LIST: 'subscriber/FETCH_SUBSCRIBER_LIST',
  FETCH_SUBSCRIBER: 'subscriber/FETCH_SUBSCRIBER',
  CREATE_SUBSCRIBER: 'subscriber/CREATE_SUBSCRIBER',
  UPDATE_SUBSCRIBER: 'subscriber/UPDATE_SUBSCRIBER',
  DELETE_SUBSCRIBER: 'subscriber/DELETE_SUBSCRIBER',
  RESET: 'subscriber/RESET'
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<ISubscriber>,
  entity: defaultValue,
  links: { next: 0 },
  updating: false,
  totalItems: 0,
  updateSuccess: false
};

export type SubscriberState = Readonly<typeof initialState>;

// Reducer

export default (state: SubscriberState = initialState, action): SubscriberState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.SEARCH_SUBSCRIBERS):
    case REQUEST(ACTION_TYPES.FETCH_SUBSCRIBER_LIST):
    case REQUEST(ACTION_TYPES.FETCH_SUBSCRIBER):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true
      };
    case REQUEST(ACTION_TYPES.CREATE_SUBSCRIBER):
    case REQUEST(ACTION_TYPES.UPDATE_SUBSCRIBER):
    case REQUEST(ACTION_TYPES.DELETE_SUBSCRIBER):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true
      };
    case FAILURE(ACTION_TYPES.SEARCH_SUBSCRIBERS):
    case FAILURE(ACTION_TYPES.FETCH_SUBSCRIBER_LIST):
    case FAILURE(ACTION_TYPES.FETCH_SUBSCRIBER):
    case FAILURE(ACTION_TYPES.CREATE_SUBSCRIBER):
    case FAILURE(ACTION_TYPES.UPDATE_SUBSCRIBER):
    case FAILURE(ACTION_TYPES.DELETE_SUBSCRIBER):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload
      };
    case SUCCESS(ACTION_TYPES.SEARCH_SUBSCRIBERS):
    case SUCCESS(ACTION_TYPES.FETCH_SUBSCRIBER_LIST):
      const links = parseHeaderForLinks(action.payload.headers.link);
      return {
        ...state,
        links,
        loading: false,
        totalItems: action.payload.headers['x-total-count'],
        entities: loadMoreDataWhenScrolled(state.entities, action.payload.data, links)
      };
    case SUCCESS(ACTION_TYPES.FETCH_SUBSCRIBER):
      return {
        ...state,
        loading: false,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.CREATE_SUBSCRIBER):
    case SUCCESS(ACTION_TYPES.UPDATE_SUBSCRIBER):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.DELETE_SUBSCRIBER):
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

const apiUrl = 'api/subscribers';
const apiSearchUrl = 'api/_search/subscribers';

// Actions

export const getSearchEntities: ICrudSearchAction<ISubscriber> = (query, page, size, sort) => ({
  type: ACTION_TYPES.SEARCH_SUBSCRIBERS,
  payload: axios.get<ISubscriber>(`${apiSearchUrl}?query=${query}${sort ? `&page=${page}&size=${size}&sort=${sort}` : ''}`)
});

export const getEntities: ICrudGetAllAction<ISubscriber> = (page, size, sort) => {
  const requestUrl = `${apiUrl}${sort ? `?page=${page}&size=${size}&sort=${sort}` : ''}`;
  return {
    type: ACTION_TYPES.FETCH_SUBSCRIBER_LIST,
    payload: axios.get<ISubscriber>(`${requestUrl}${sort ? '&' : '?'}cacheBuster=${new Date().getTime()}`)
  };
};

export const getEntity: ICrudGetAction<ISubscriber> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_SUBSCRIBER,
    payload: axios.get<ISubscriber>(requestUrl)
  };
};

export const createEntity: ICrudPutAction<ISubscriber> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_SUBSCRIBER,
    payload: axios.post(apiUrl, cleanEntity(entity))
  });
  return result;
};

export const updateEntity: ICrudPutAction<ISubscriber> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_SUBSCRIBER,
    payload: axios.put(apiUrl, cleanEntity(entity))
  });
  return result;
};

export const deleteEntity: ICrudDeleteAction<ISubscriber> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_SUBSCRIBER,
    payload: axios.delete(requestUrl)
  });
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET
});
