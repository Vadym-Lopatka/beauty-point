import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Image from './image';
import ImageDetail from './image-detail';
import ImageUpdate from './image-update';
import ImageDeleteDialog from './image-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={ImageUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={ImageUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={ImageDetail} />
      <ErrorBoundaryRoute path={match.url} component={Image} />
    </Switch>
    <ErrorBoundaryRoute path={`${match.url}/:id/delete`} component={ImageDeleteDialog} />
  </>
);

export default Routes;
