import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
// tslint:disable-next-line:no-unused-variable
import { Translate, ICrudGetAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './time-table.reducer';
import { ITimeTable } from 'app/shared/model/time-table.model';
// tslint:disable-next-line:no-unused-variable
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface ITimeTableDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export class TimeTableDetail extends React.Component<ITimeTableDetailProps> {
  componentDidMount() {
    this.props.getEntity(this.props.match.params.id);
  }

  render() {
    const { timeTableEntity } = this.props;
    return (
      <Row>
        <Col md="8">
          <h2>
            <Translate contentKey="beautyPointApp.timeTable.detail.title">TimeTable</Translate> [<b>{timeTableEntity.id}</b>]
          </h2>
          <dl className="jh-entity-details">
            <dt>
              <span id="everyDayEqual">
                <Translate contentKey="beautyPointApp.timeTable.everyDayEqual">Every Day Equal</Translate>
              </span>
            </dt>
            <dd>{timeTableEntity.everyDayEqual ? 'true' : 'false'}</dd>
            <dt>
              <span id="mo">
                <Translate contentKey="beautyPointApp.timeTable.mo">Mo</Translate>
              </span>
            </dt>
            <dd>{timeTableEntity.mo}</dd>
            <dt>
              <span id="tu">
                <Translate contentKey="beautyPointApp.timeTable.tu">Tu</Translate>
              </span>
            </dt>
            <dd>{timeTableEntity.tu}</dd>
            <dt>
              <span id="we">
                <Translate contentKey="beautyPointApp.timeTable.we">We</Translate>
              </span>
            </dt>
            <dd>{timeTableEntity.we}</dd>
            <dt>
              <span id="th">
                <Translate contentKey="beautyPointApp.timeTable.th">Th</Translate>
              </span>
            </dt>
            <dd>{timeTableEntity.th}</dd>
            <dt>
              <span id="fr">
                <Translate contentKey="beautyPointApp.timeTable.fr">Fr</Translate>
              </span>
            </dt>
            <dd>{timeTableEntity.fr}</dd>
            <dt>
              <span id="sa">
                <Translate contentKey="beautyPointApp.timeTable.sa">Sa</Translate>
              </span>
            </dt>
            <dd>{timeTableEntity.sa}</dd>
            <dt>
              <span id="su">
                <Translate contentKey="beautyPointApp.timeTable.su">Su</Translate>
              </span>
            </dt>
            <dd>{timeTableEntity.su}</dd>
          </dl>
          <Button tag={Link} to="/entity/time-table" replace color="info">
            <FontAwesomeIcon icon="arrow-left" />{' '}
            <span className="d-none d-md-inline">
              <Translate contentKey="entity.action.back">Back</Translate>
            </span>
          </Button>
          &nbsp;
          <Button tag={Link} to={`/entity/time-table/${timeTableEntity.id}/edit`} replace color="primary">
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

const mapStateToProps = ({ timeTable }: IRootState) => ({
  timeTableEntity: timeTable.entity
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(TimeTableDetail);
