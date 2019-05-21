import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import TimeTable from './time-table';
import TimeTableDetail from './time-table-detail';
import TimeTableUpdate from './time-table-update';
import TimeTableDeleteDialog from './time-table-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={TimeTableUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={TimeTableUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={TimeTableDetail} />
      <ErrorBoundaryRoute path={match.url} component={TimeTable} />
    </Switch>
    <ErrorBoundaryRoute path={`${match.url}/:id/delete`} component={TimeTableDeleteDialog} />
  </>
);

export default Routes;
