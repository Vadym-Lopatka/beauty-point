import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
// tslint:disable-next-line:no-unused-variable
import { Translate, ICrudGetAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './offer.reducer';
import { IOffer } from 'app/shared/model/offer.model';
// tslint:disable-next-line:no-unused-variable
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IOfferDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export class OfferDetail extends React.Component<IOfferDetailProps> {
  componentDidMount() {
    this.props.getEntity(this.props.match.params.id);
  }

  render() {
    const { offerEntity } = this.props;
    return (
      <Row>
        <Col md="8">
          <h2>
            <Translate contentKey="beautyPointApp.offer.detail.title">Offer</Translate> [<b>{offerEntity.id}</b>]
          </h2>
          <dl className="jh-entity-details">
            <dt>
              <span id="name">
                <Translate contentKey="beautyPointApp.offer.name">Name</Translate>
              </span>
            </dt>
            <dd>{offerEntity.name}</dd>
            <dt>
              <span id="description">
                <Translate contentKey="beautyPointApp.offer.description">Description</Translate>
              </span>
            </dt>
            <dd>{offerEntity.description}</dd>
            <dt>
              <span id="priceLow">
                <Translate contentKey="beautyPointApp.offer.priceLow">Price Low</Translate>
              </span>
            </dt>
            <dd>{offerEntity.priceLow}</dd>
            <dt>
              <span id="priceHigh">
                <Translate contentKey="beautyPointApp.offer.priceHigh">Price High</Translate>
              </span>
            </dt>
            <dd>{offerEntity.priceHigh}</dd>
            <dt>
              <span id="active">
                <Translate contentKey="beautyPointApp.offer.active">Active</Translate>
              </span>
            </dt>
            <dd>{offerEntity.active ? 'true' : 'false'}</dd>
            <dt>
              <span id="status">
                <Translate contentKey="beautyPointApp.offer.status">Status</Translate>
              </span>
            </dt>
            <dd>{offerEntity.status}</dd>
            <dt>
              <Translate contentKey="beautyPointApp.offer.salon">Salon</Translate>
            </dt>
            <dd>{offerEntity.salon ? offerEntity.salon.id : ''}</dd>
            <dt>
              <Translate contentKey="beautyPointApp.offer.image">Image</Translate>
            </dt>
            <dd>{offerEntity.image ? offerEntity.image.id : ''}</dd>
            <dt>
              <Translate contentKey="beautyPointApp.offer.category">Category</Translate>
            </dt>
            <dd>{offerEntity.category ? offerEntity.category.id : ''}</dd>
          </dl>
          <Button tag={Link} to="/entity/offer" replace color="info">
            <FontAwesomeIcon icon="arrow-left" />{' '}
            <span className="d-none d-md-inline">
              <Translate contentKey="entity.action.back">Back</Translate>
            </span>
          </Button>
          &nbsp;
          <Button tag={Link} to={`/entity/offer/${offerEntity.id}/edit`} replace color="primary">
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

const mapStateToProps = ({ offer }: IRootState) => ({
  offerEntity: offer.entity
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(OfferDetail);
