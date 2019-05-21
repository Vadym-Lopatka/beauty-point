import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvFeedback, AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
// tslint:disable-next-line:no-unused-variable
import { Translate, translate, ICrudGetAction, ICrudGetAllAction, ICrudPutAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { ISalon } from 'app/shared/model/salon.model';
import { getEntities as getSalons } from 'app/entities/salon/salon.reducer';
import { IImage } from 'app/shared/model/image.model';
import { getEntities as getImages } from 'app/entities/image/image.reducer';
import { ICategory } from 'app/shared/model/category.model';
import { getEntities as getCategories } from 'app/entities/category/category.reducer';
import { getEntity, updateEntity, createEntity, reset } from './offer.reducer';
import { IOffer } from 'app/shared/model/offer.model';
// tslint:disable-next-line:no-unused-variable
import { convertDateTimeFromServer, convertDateTimeToServer } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface IOfferUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export interface IOfferUpdateState {
  isNew: boolean;
  salonId: string;
  imageId: string;
  categoryId: string;
}

export class OfferUpdate extends React.Component<IOfferUpdateProps, IOfferUpdateState> {
  constructor(props) {
    super(props);
    this.state = {
      salonId: '0',
      imageId: '0',
      categoryId: '0',
      isNew: !this.props.match.params || !this.props.match.params.id
    };
  }

  componentWillUpdate(nextProps, nextState) {
    if (nextProps.updateSuccess !== this.props.updateSuccess && nextProps.updateSuccess) {
      this.handleClose();
    }
  }

  componentDidMount() {
    if (!this.state.isNew) {
      this.props.getEntity(this.props.match.params.id);
    }

    this.props.getSalons();
    this.props.getImages();
    this.props.getCategories();
  }

  saveEntity = (event, errors, values) => {
    if (errors.length === 0) {
      const { offerEntity } = this.props;
      const entity = {
        ...offerEntity,
        ...values
      };

      if (this.state.isNew) {
        this.props.createEntity(entity);
      } else {
        this.props.updateEntity(entity);
      }
    }
  };

  handleClose = () => {
    this.props.history.push('/entity/offer');
  };

  render() {
    const { offerEntity, salons, images, categories, loading, updating } = this.props;
    const { isNew } = this.state;

    return (
      <div>
        <Row className="justify-content-center">
          <Col md="8">
            <h2 id="beautyPointApp.offer.home.createOrEditLabel">
              <Translate contentKey="beautyPointApp.offer.home.createOrEditLabel">Create or edit a Offer</Translate>
            </h2>
          </Col>
        </Row>
        <Row className="justify-content-center">
          <Col md="8">
            {loading ? (
              <p>Loading...</p>
            ) : (
              <AvForm model={isNew ? {} : offerEntity} onSubmit={this.saveEntity}>
                {!isNew ? (
                  <AvGroup>
                    <Label for="offer-id">
                      <Translate contentKey="global.field.id">ID</Translate>
                    </Label>
                    <AvInput id="offer-id" type="text" className="form-control" name="id" required readOnly />
                  </AvGroup>
                ) : null}
                <AvGroup>
                  <Label id="nameLabel" for="offer-name">
                    <Translate contentKey="beautyPointApp.offer.name">Name</Translate>
                  </Label>
                  <AvField
                    id="offer-name"
                    type="text"
                    name="name"
                    validate={{
                      required: { value: true, errorMessage: translate('entity.validation.required') }
                    }}
                  />
                </AvGroup>
                <AvGroup>
                  <Label id="descriptionLabel" for="offer-description">
                    <Translate contentKey="beautyPointApp.offer.description">Description</Translate>
                  </Label>
                  <AvField
                    id="offer-description"
                    type="text"
                    name="description"
                    validate={{
                      required: { value: true, errorMessage: translate('entity.validation.required') }
                    }}
                  />
                </AvGroup>
                <AvGroup>
                  <Label id="priceLowLabel" for="offer-priceLow">
                    <Translate contentKey="beautyPointApp.offer.priceLow">Price Low</Translate>
                  </Label>
                  <AvField
                    id="offer-priceLow"
                    type="text"
                    name="priceLow"
                    validate={{
                      required: { value: true, errorMessage: translate('entity.validation.required') },
                      number: { value: true, errorMessage: translate('entity.validation.number') }
                    }}
                  />
                </AvGroup>
                <AvGroup>
                  <Label id="priceHighLabel" for="offer-priceHigh">
                    <Translate contentKey="beautyPointApp.offer.priceHigh">Price High</Translate>
                  </Label>
                  <AvField
                    id="offer-priceHigh"
                    type="text"
                    name="priceHigh"
                    validate={{
                      required: { value: true, errorMessage: translate('entity.validation.required') },
                      number: { value: true, errorMessage: translate('entity.validation.number') }
                    }}
                  />
                </AvGroup>
                <AvGroup>
                  <Label id="activeLabel" check>
                    <AvInput id="offer-active" type="checkbox" className="form-control" name="active" />
                    <Translate contentKey="beautyPointApp.offer.active">Active</Translate>
                  </Label>
                </AvGroup>
                <AvGroup>
                  <Label id="statusLabel" for="offer-status">
                    <Translate contentKey="beautyPointApp.offer.status">Status</Translate>
                  </Label>
                  <AvInput
                    id="offer-status"
                    type="select"
                    className="form-control"
                    name="status"
                    value={(!isNew && offerEntity.status) || 'NORMAL'}
                  >
                    <option value="NORMAL">
                      <Translate contentKey="beautyPointApp.OfferStatusEnum.NORMAL" />
                    </option>
                    <option value="DELETED">
                      <Translate contentKey="beautyPointApp.OfferStatusEnum.DELETED" />
                    </option>
                    <option value="BANNED">
                      <Translate contentKey="beautyPointApp.OfferStatusEnum.BANNED" />
                    </option>
                  </AvInput>
                </AvGroup>
                <AvGroup>
                  <Label for="offer-salon">
                    <Translate contentKey="beautyPointApp.offer.salon">Salon</Translate>
                  </Label>
                  <AvInput id="offer-salon" type="select" className="form-control" name="salon.id">
                    <option value="" key="0" />
                    {salons
                      ? salons.map(otherEntity => (
                          <option value={otherEntity.id} key={otherEntity.id}>
                            {otherEntity.id}
                          </option>
                        ))
                      : null}
                  </AvInput>
                </AvGroup>
                <AvGroup>
                  <Label for="offer-image">
                    <Translate contentKey="beautyPointApp.offer.image">Image</Translate>
                  </Label>
                  <AvInput id="offer-image" type="select" className="form-control" name="image.id">
                    <option value="" key="0" />
                    {images
                      ? images.map(otherEntity => (
                          <option value={otherEntity.id} key={otherEntity.id}>
                            {otherEntity.id}
                          </option>
                        ))
                      : null}
                  </AvInput>
                </AvGroup>
                <AvGroup>
                  <Label for="offer-category">
                    <Translate contentKey="beautyPointApp.offer.category">Category</Translate>
                  </Label>
                  <AvInput id="offer-category" type="select" className="form-control" name="category.id">
                    <option value="" key="0" />
                    {categories
                      ? categories.map(otherEntity => (
                          <option value={otherEntity.id} key={otherEntity.id}>
                            {otherEntity.id}
                          </option>
                        ))
                      : null}
                  </AvInput>
                </AvGroup>
                <Button tag={Link} id="cancel-save" to="/entity/offer" replace color="info">
                  <FontAwesomeIcon icon="arrow-left" />
                  &nbsp;
                  <span className="d-none d-md-inline">
                    <Translate contentKey="entity.action.back">Back</Translate>
                  </span>
                </Button>
                &nbsp;
                <Button color="primary" id="save-entity" type="submit" disabled={updating}>
                  <FontAwesomeIcon icon="save" />
                  &nbsp;
                  <Translate contentKey="entity.action.save">Save</Translate>
                </Button>
              </AvForm>
            )}
          </Col>
        </Row>
      </div>
    );
  }
}

const mapStateToProps = (storeState: IRootState) => ({
  salons: storeState.salon.entities,
  images: storeState.image.entities,
  categories: storeState.category.entities,
  offerEntity: storeState.offer.entity,
  loading: storeState.offer.loading,
  updating: storeState.offer.updating,
  updateSuccess: storeState.offer.updateSuccess
});

const mapDispatchToProps = {
  getSalons,
  getImages,
  getCategories,
  getEntity,
  updateEntity,
  createEntity,
  reset
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(OfferUpdate);
