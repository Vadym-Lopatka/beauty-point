import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Offer from './offer';
import OfferDetail from './offer-detail';
import OfferUpdate from './offer-update';
import OfferDeleteDialog from './offer-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={OfferUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={OfferUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={OfferDetail} />
      <ErrorBoundaryRoute path={match.url} component={Offer} />
    </Switch>
    <ErrorBoundaryRoute path={`${match.url}/:id/delete`} component={OfferDeleteDialog} />
  </>
);

export default Routes;
