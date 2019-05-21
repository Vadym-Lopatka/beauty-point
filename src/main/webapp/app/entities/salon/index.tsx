import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Salon from './salon';
import SalonDetail from './salon-detail';
import SalonUpdate from './salon-update';
import SalonDeleteDialog from './salon-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={SalonUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={SalonUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={SalonDetail} />
      <ErrorBoundaryRoute path={match.url} component={Salon} />
    </Switch>
    <ErrorBoundaryRoute path={`${match.url}/:id/delete`} component={SalonDeleteDialog} />
  </>
);

export default Routes;
