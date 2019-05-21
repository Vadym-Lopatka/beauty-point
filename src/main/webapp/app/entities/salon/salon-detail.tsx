import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
// tslint:disable-next-line:no-unused-variable
import { Translate, ICrudGetAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './salon.reducer';
import { ISalon } from 'app/shared/model/salon.model';
// tslint:disable-next-line:no-unused-variable
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface ISalonDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export class SalonDetail extends React.Component<ISalonDetailProps> {
  componentDidMount() {
    this.props.getEntity(this.props.match.params.id);
  }

  render() {
    const { salonEntity } = this.props;
    return (
      <Row>
        <Col md="8">
          <h2>
            <Translate contentKey="beautyPointApp.salon.detail.title">Salon</Translate> [<b>{salonEntity.id}</b>]
          </h2>
          <dl className="jh-entity-details">
            <dt>
              <span id="name">
                <Translate contentKey="beautyPointApp.salon.name">Name</Translate>
              </span>
            </dt>
            <dd>{salonEntity.name}</dd>
            <dt>
              <span id="slogan">
                <Translate contentKey="beautyPointApp.salon.slogan">Slogan</Translate>
              </span>
            </dt>
            <dd>{salonEntity.slogan}</dd>
            <dt>
              <span id="location">
                <Translate contentKey="beautyPointApp.salon.location">Location</Translate>
              </span>
            </dt>
            <dd>{salonEntity.location}</dd>
            <dt>
              <span id="status">
                <Translate contentKey="beautyPointApp.salon.status">Status</Translate>
              </span>
            </dt>
            <dd>{salonEntity.status}</dd>
            <dt>
              <span id="systemComment">
                <Translate contentKey="beautyPointApp.salon.systemComment">System Comment</Translate>
              </span>
            </dt>
            <dd>{salonEntity.systemComment}</dd>
            <dt>
              <span id="type">
                <Translate contentKey="beautyPointApp.salon.type">Type</Translate>
              </span>
            </dt>
            <dd>{salonEntity.type}</dd>
            <dt>
              <Translate contentKey="beautyPointApp.salon.image">Image</Translate>
            </dt>
            <dd>{salonEntity.image ? salonEntity.image.id : ''}</dd>
            <dt>
              <Translate contentKey="beautyPointApp.salon.timeTable">Time Table</Translate>
            </dt>
            <dd>{salonEntity.timeTable ? salonEntity.timeTable.id : ''}</dd>
            <dt>
              <Translate contentKey="beautyPointApp.salon.category">Category</Translate>
            </dt>
            <dd>
              {salonEntity.categories
                ? salonEntity.categories.map((val, i) => (
                    <span key={val.id}>
                      <a>{val.name}</a>
                      {i === salonEntity.categories.length - 1 ? '' : ', '}
                    </span>
                  ))
                : null}
            </dd>
            <dt>
              <Translate contentKey="beautyPointApp.salon.owner">Owner</Translate>
            </dt>
            <dd>{salonEntity.owner ? salonEntity.owner.login : ''}</dd>
          </dl>
          <Button tag={Link} to="/entity/salon" replace color="info">
            <FontAwesomeIcon icon="arrow-left" />{' '}
            <span className="d-none d-md-inline">
              <Translate contentKey="entity.action.back">Back</Translate>
            </span>
          </Button>
          &nbsp;
          <Button tag={Link} to={`/entity/salon/${salonEntity.id}/edit`} replace color="primary">
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

const mapStateToProps = ({ salon }: IRootState) => ({
  salonEntity: salon.entity
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(SalonDetail);
