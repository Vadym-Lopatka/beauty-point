import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
// tslint:disable-next-line:no-unused-variable
import { Translate, ICrudGetAction, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './record.reducer';
import { IRecord } from 'app/shared/model/record.model';
// tslint:disable-next-line:no-unused-variable
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IRecordDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export class RecordDetail extends React.Component<IRecordDetailProps> {
  componentDidMount() {
    this.props.getEntity(this.props.match.params.id);
  }

  render() {
    const { recordEntity } = this.props;
    return (
      <Row>
        <Col md="8">
          <h2>
            <Translate contentKey="beautyPointApp.record.detail.title">Record</Translate> [<b>{recordEntity.id}</b>]
          </h2>
          <dl className="jh-entity-details">
            <dt>
              <span id="bookingTime">
                <Translate contentKey="beautyPointApp.record.bookingTime">Booking Time</Translate>
              </span>
            </dt>
            <dd>
              <TextFormat value={recordEntity.bookingTime} type="date" format={APP_DATE_FORMAT} />
            </dd>
            <dt>
              <span id="duration">
                <Translate contentKey="beautyPointApp.record.duration">Duration</Translate>
              </span>
            </dt>
            <dd>{recordEntity.duration}</dd>
            <dt>
              <span id="totalPrice">
                <Translate contentKey="beautyPointApp.record.totalPrice">Total Price</Translate>
              </span>
            </dt>
            <dd>{recordEntity.totalPrice}</dd>
            <dt>
              <span id="orderStatus">
                <Translate contentKey="beautyPointApp.record.orderStatus">Order Status</Translate>
              </span>
            </dt>
            <dd>{recordEntity.orderStatus}</dd>
            <dt>
              <span id="comment">
                <Translate contentKey="beautyPointApp.record.comment">Comment</Translate>
              </span>
            </dt>
            <dd>{recordEntity.comment}</dd>
            <dt>
              <Translate contentKey="beautyPointApp.record.master">Master</Translate>
            </dt>
            <dd>{recordEntity.master ? recordEntity.master.id : ''}</dd>
            <dt>
              <Translate contentKey="beautyPointApp.record.variant">Variant</Translate>
            </dt>
            <dd>{recordEntity.variant ? recordEntity.variant.id : ''}</dd>
            <dt>
              <Translate contentKey="beautyPointApp.record.option">Option</Translate>
            </dt>
            <dd>
              {recordEntity.options
                ? recordEntity.options.map((val, i) => (
                    <span key={val.id}>
                      <a>{val.id}</a>
                      {i === recordEntity.options.length - 1 ? '' : ', '}
                    </span>
                  ))
                : null}
            </dd>
            <dt>
              <Translate contentKey="beautyPointApp.record.user">User</Translate>
            </dt>
            <dd>{recordEntity.user ? recordEntity.user.login : ''}</dd>
            <dt>
              <Translate contentKey="beautyPointApp.record.salon">Salon</Translate>
            </dt>
            <dd>{recordEntity.salon ? recordEntity.salon.id : ''}</dd>
          </dl>
          <Button tag={Link} to="/entity/record" replace color="info">
            <FontAwesomeIcon icon="arrow-left" />{' '}
            <span className="d-none d-md-inline">
              <Translate contentKey="entity.action.back">Back</Translate>
            </span>
          </Button>
          &nbsp;
          <Button tag={Link} to={`/entity/record/${recordEntity.id}/edit`} replace color="primary">
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

const mapStateToProps = ({ record }: IRootState) => ({
  recordEntity: record.entity
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(RecordDetail);
