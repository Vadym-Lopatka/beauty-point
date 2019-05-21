import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Variant from './variant';
import VariantDetail from './variant-detail';
import VariantUpdate from './variant-update';
import VariantDeleteDialog from './variant-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={VariantUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={VariantUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={VariantDetail} />
      <ErrorBoundaryRoute path={match.url} component={Variant} />
    </Switch>
    <ErrorBoundaryRoute path={`${match.url}/:id/delete`} component={VariantDeleteDialog} />
  </>
);

export default Routes;
