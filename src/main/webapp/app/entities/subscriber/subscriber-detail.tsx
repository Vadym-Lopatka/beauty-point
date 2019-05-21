import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
// tslint:disable-next-line:no-unused-variable
import { Translate, ICrudGetAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './subscriber.reducer';
import { ISubscriber } from 'app/shared/model/subscriber.model';
// tslint:disable-next-line:no-unused-variable
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface ISubscriberDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export class SubscriberDetail extends React.Component<ISubscriberDetailProps> {
  componentDidMount() {
    this.props.getEntity(this.props.match.params.id);
  }

  render() {
    const { subscriberEntity } = this.props;
    return (
      <Row>
        <Col md="8">
          <h2>
            <Translate contentKey="beautyPointApp.subscriber.detail.title">Subscriber</Translate> [<b>{subscriberEntity.id}</b>]
          </h2>
          <dl className="jh-entity-details">
            <dt>
              <span id="firsName">
                <Translate contentKey="beautyPointApp.subscriber.firsName">Firs Name</Translate>
              </span>
            </dt>
            <dd>{subscriberEntity.firsName}</dd>
            <dt>
              <span id="email">
                <Translate contentKey="beautyPointApp.subscriber.email">Email</Translate>
              </span>
            </dt>
            <dd>{subscriberEntity.email}</dd>
          </dl>
          <Button tag={Link} to="/entity/subscriber" replace color="info">
            <FontAwesomeIcon icon="arrow-left" />{' '}
            <span className="d-none d-md-inline">
              <Translate contentKey="entity.action.back">Back</Translate>
            </span>
          </Button>
          &nbsp;
          <Button tag={Link} to={`/entity/subscriber/${subscriberEntity.id}/edit`} replace color="primary">
            <FontAwesomeIcon icon="pencil-alt" />{' '}
            <span className="d-none d-md-inline">
              <Translate contentKey="entity.action.edit">Edit</Translate>
            </span>
          </Button>
        </Col>
      </Row>
    );
  }
}

const mapStateToProps = ({ subscriber }: IRootState) => ({
  subscriberEntity: subscriber.entity
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(SubscriberDetail);
