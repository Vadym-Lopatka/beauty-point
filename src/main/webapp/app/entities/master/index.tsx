import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Master from './master';
import MasterDetail from './master-detail';
import MasterUpdate from './master-update';
import MasterDeleteDialog from './master-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={MasterUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={MasterUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={MasterDetail} />
      <ErrorBoundaryRoute path={match.url} component={Master} />
    </Switch>
    <ErrorBoundaryRoute path={`${match.url}/:id/delete`} component={MasterDeleteDialog} />
  </>
);

export default Routes;
