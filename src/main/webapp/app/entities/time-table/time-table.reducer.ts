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

import { ITimeTable, defaultValue } from 'app/shared/model/time-table.model';

export const ACTION_TYPES = {
  SEARCH_TIMETABLES: 'timeTable/SEARCH_TIMETABLES',
  FETCH_TIMETABLE_LIST: 'timeTable/FETCH_TIMETABLE_LIST',
  FETCH_TIMETABLE: 'timeTable/FETCH_TIMETABLE',
  CREATE_TIMETABLE: 'timeTable/CREATE_TIMETABLE',
  UPDATE_TIMETABLE: 'timeTable/UPDATE_TIMETABLE',
  DELETE_TIMETABLE: 'timeTable/DELETE_TIMETABLE',
  RESET: 'timeTable/RESET'
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<ITimeTable>,
  entity: defaultValue,
  links: { next: 0 },
  updating: false,
  totalItems: 0,
  updateSuccess: false
};

export type TimeTableState = Readonly<typeof initialState>;

// Reducer

export default (state: TimeTableState = initialState, action): TimeTableState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.SEARCH_TIMETABLES):
    case REQUEST(ACTION_TYPES.FETCH_TIMETABLE_LIST):
    case REQUEST(ACTION_TYPES.FETCH_TIMETABLE):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true
      };
    case REQUEST(ACTION_TYPES.CREATE_TIMETABLE):
    case REQUEST(ACTION_TYPES.UPDATE_TIMETABLE):
    case REQUEST(ACTION_TYPES.DELETE_TIMETABLE):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true
      };
    case FAILURE(ACTION_TYPES.SEARCH_TIMETABLES):
    case FAILURE(ACTION_TYPES.FETCH_TIMETABLE_LIST):
    case FAILURE(ACTION_TYPES.FETCH_TIMETABLE):
    case FAILURE(ACTION_TYPES.CREATE_TIMETABLE):
    case FAILURE(ACTION_TYPES.UPDATE_TIMETABLE):
    case FAILURE(ACTION_TYPES.DELETE_TIMETABLE):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload
      };
    case SUCCESS(ACTION_TYPES.SEARCH_TIMETABLES):
    case SUCCESS(ACTION_TYPES.FETCH_TIMETABLE_LIST):
      const links = parseHeaderForLinks(action.payload.headers.link);
      return {
        ...state,
        links,
        loading: false,
        totalItems: action.payload.headers['x-total-count'],
        entities: loadMoreDataWhenScrolled(state.entities, action.payload.data, links)
      };
    case SUCCESS(ACTION_TYPES.FETCH_TIMETABLE):
      return {
        ...state,
        loading: false,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.CREATE_TIMETABLE):
    case SUCCESS(ACTION_TYPES.UPDATE_TIMETABLE):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.DELETE_TIMETABLE):
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

const apiUrl = 'api/time-tables';
const apiSearchUrl = 'api/_search/time-tables';

// Actions

export const getSearchEntities: ICrudSearchAction<ITimeTable> = (query, page, size, sort) => ({
  type: ACTION_TYPES.SEARCH_TIMETABLES,
  payload: axios.get<ITimeTable>(`${apiSearchUrl}?query=${query}${sort ? `&page=${page}&size=${size}&sort=${sort}` : ''}`)
});

export const getEntities: ICrudGetAllAction<ITimeTable> = (page, size, sort) => {
  const requestUrl = `${apiUrl}${sort ? `?page=${page}&size=${size}&sort=${sort}` : ''}`;
  return {
    type: ACTION_TYPES.FETCH_TIMETABLE_LIST,
    payload: axios.get<ITimeTable>(`${requestUrl}${sort ? '&' : '?'}cacheBuster=${new Date().getTime()}`)
  };
};

export const getEntity: ICrudGetAction<ITimeTable> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_TIMETABLE,
    payload: axios.get<ITimeTable>(requestUrl)
  };
};

export const createEntity: ICrudPutAction<ITimeTable> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_TIMETABLE,
    payload: axios.post(apiUrl, cleanEntity(entity))
  });
  return result;
};

export const updateEntity: ICrudPutAction<ITimeTable> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_TIMETABLE,
    payload: axios.put(apiUrl, cleanEntity(entity))
  });
  return result;
};

export const deleteEntity: ICrudDeleteAction<ITimeTable> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_TIMETABLE,
    payload: axios.delete(requestUrl)
  });
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET
});
