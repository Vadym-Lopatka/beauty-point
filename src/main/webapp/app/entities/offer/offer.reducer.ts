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

import { IOffer, defaultValue } from 'app/shared/model/offer.model';

export const ACTION_TYPES = {
  SEARCH_OFFERS: 'offer/SEARCH_OFFERS',
  FETCH_OFFER_LIST: 'offer/FETCH_OFFER_LIST',
  FETCH_OFFER: 'offer/FETCH_OFFER',
  CREATE_OFFER: 'offer/CREATE_OFFER',
  UPDATE_OFFER: 'offer/UPDATE_OFFER',
  DELETE_OFFER: 'offer/DELETE_OFFER',
  RESET: 'offer/RESET'
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<IOffer>,
  entity: defaultValue,
  links: { next: 0 },
  updating: false,
  totalItems: 0,
  updateSuccess: false
};

export type OfferState = Readonly<typeof initialState>;

// Reducer

export default (state: OfferState = initialState, action): OfferState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.SEARCH_OFFERS):
    case REQUEST(ACTION_TYPES.FETCH_OFFER_LIST):
    case REQUEST(ACTION_TYPES.FETCH_OFFER):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true
      };
    case REQUEST(ACTION_TYPES.CREATE_OFFER):
    case REQUEST(ACTION_TYPES.UPDATE_OFFER):
    case REQUEST(ACTION_TYPES.DELETE_OFFER):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true
      };
    case FAILURE(ACTION_TYPES.SEARCH_OFFERS):
    case FAILURE(ACTION_TYPES.FETCH_OFFER_LIST):
    case FAILURE(ACTION_TYPES.FETCH_OFFER):
    case FAILURE(ACTION_TYPES.CREATE_OFFER):
    case FAILURE(ACTION_TYPES.UPDATE_OFFER):
    case FAILURE(ACTION_TYPES.DELETE_OFFER):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload
      };
    case SUCCESS(ACTION_TYPES.SEARCH_OFFERS):
    case SUCCESS(ACTION_TYPES.FETCH_OFFER_LIST):
      const links = parseHeaderForLinks(action.payload.headers.link);
      return {
        ...state,
        links,
        loading: false,
        totalItems: action.payload.headers['x-total-count'],
        entities: loadMoreDataWhenScrolled(state.entities, action.payload.data, links)
      };
    case SUCCESS(ACTION_TYPES.FETCH_OFFER):
      return {
        ...state,
        loading: false,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.CREATE_OFFER):
    case SUCCESS(ACTION_TYPES.UPDATE_OFFER):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.DELETE_OFFER):
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

const apiUrl = 'api/offers';
const apiSearchUrl = 'api/_search/offers';

// Actions

export const getSearchEntities: ICrudSearchAction<IOffer> = (query, page, size, sort) => ({
  type: ACTION_TYPES.SEARCH_OFFERS,
  payload: axios.get<IOffer>(`${apiSearchUrl}?query=${query}${sort ? `&page=${page}&size=${size}&sort=${sort}` : ''}`)
});

export const getEntities: ICrudGetAllAction<IOffer> = (page, size, sort) => {
  const requestUrl = `${apiUrl}${sort ? `?page=${page}&size=${size}&sort=${sort}` : ''}`;
  return {
    type: ACTION_TYPES.FETCH_OFFER_LIST,
    payload: axios.get<IOffer>(`${requestUrl}${sort ? '&' : '?'}cacheBuster=${new Date().getTime()}`)
  };
};

export const getEntity: ICrudGetAction<IOffer> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_OFFER,
    payload: axios.get<IOffer>(requestUrl)
  };
};

export const createEntity: ICrudPutAction<IOffer> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_OFFER,
    payload: axios.post(apiUrl, cleanEntity(entity))
  });
  return result;
};

export const updateEntity: ICrudPutAction<IOffer> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_OFFER,
    payload: axios.put(apiUrl, cleanEntity(entity))
  });
  return result;
};

export const deleteEntity: ICrudDeleteAction<IOffer> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_OFFER,
    payload: axios.delete(requestUrl)
  });
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET
});
