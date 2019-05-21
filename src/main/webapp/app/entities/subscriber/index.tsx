import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Subscriber from './subscriber';
import SubscriberDetail from './subscriber-detail';
import SubscriberUpdate from './subscriber-update';
import SubscriberDeleteDialog from './subscriber-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={SubscriberUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={SubscriberUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={SubscriberDetail} />
      <ErrorBoundaryRoute path={match.url} component={Subscriber} />
    </Switch>
    <ErrorBoundaryRoute path={`${match.url}/:id/delete`} component={SubscriberDeleteDialog} />
  </>
);

export default Routes;
