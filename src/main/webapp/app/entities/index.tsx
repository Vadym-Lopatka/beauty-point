import React from 'react';
import { Switch } from 'react-router-dom';

// tslint:disable-next-line:no-unused-variable
import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Category from './category';
import Image from './image';
import Master from './master';
import Offer from './offer';
import Option from './option';
import Record from './record';
import Salon from './salon';
import Subscriber from './subscriber';
import TimeTable from './time-table';
import Variant from './variant';
/* jhipster-needle-add-route-import - JHipster will add routes here */

const Routes = ({ match }) => (
  <div>
    <Switch>
      {/* prettier-ignore */}
      <ErrorBoundaryRoute path={`${match.url}/category`} component={Category} />
      <ErrorBoundaryRoute path={`${match.url}/image`} component={Image} />
      <ErrorBoundaryRoute path={`${match.url}/master`} component={Master} />
      <ErrorBoundaryRoute path={`${match.url}/offer`} component={Offer} />
      <ErrorBoundaryRoute path={`${match.url}/option`} component={Option} />
      <ErrorBoundaryRoute path={`${match.url}/record`} component={Record} />
      <ErrorBoundaryRoute path={`${match.url}/salon`} component={Salon} />
      <ErrorBoundaryRoute path={`${match.url}/subscriber`} component={Subscriber} />
      <ErrorBoundaryRoute path={`${match.url}/time-table`} component={TimeTable} />
      <ErrorBoundaryRoute path={`${match.url}/variant`} component={Variant} />
      {/* jhipster-needle-add-route-path - JHipster will add routes here */}
    </Switch>
  </div>
);

export default Routes;
