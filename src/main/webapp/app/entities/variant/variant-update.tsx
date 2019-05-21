import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvFeedback, AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
// tslint:disable-next-line:no-unused-variable
import { Translate, translate, ICrudGetAction, ICrudGetAllAction, ICrudPutAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { IOffer } from 'app/shared/model/offer.model';
import { getEntities as getOffers } from 'app/entities/offer/offer.reducer';
import { IMaster } from 'app/shared/model/master.model';
import { getEntities as getMasters } from 'app/entities/master/master.reducer';
import { getEntity, updateEntity, createEntity, reset } from './variant.reducer';
import { IVariant } from 'app/shared/model/variant.model';
// tslint:disable-next-line:no-unused-variable
import { convertDateTimeFromServer, convertDateTimeToServer } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface IVariantUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export interface IVariantUpdateState {
  isNew: boolean;
  idsexecutor: any[];
  offerId: string;
}

export class VariantUpdate extends React.Component<IVariantUpdateProps, IVariantUpdateState> {
  constructor(props) {
    super(props);
    this.state = {
      idsexecutor: [],
      offerId: '0',
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

    this.props.getOffers();
    this.props.getMasters();
  }

  saveEntity = (event, errors, values) => {
    if (errors.length === 0) {
      const { variantEntity } = this.props;
      const entity = {
        ...variantEntity,
        ...values,
        executors: mapIdList(values.executors)
      };

      if (this.state.isNew) {
        this.props.createEntity(entity);
      } else {
        this.props.updateEntity(entity);
      }
    }
  };

  handleClose = () => {
    this.props.history.push('/entity/variant');
  };

  render() {
    const { variantEntity, offers, masters, loading, updating } = this.props;
    const { isNew } = this.state;

    return (
      <div>
        <Row className="justify-content-center">
          <Col md="8">
            <h2 id="beautyPointApp.variant.home.createOrEditLabel">
              <Translate contentKey="beautyPointApp.variant.home.createOrEditLabel">Create or edit a Variant</Translate>
            </h2>
          </Col>
        </Row>
        <Row className="justify-content-center">
          <Col md="8">
            {loading ? (
              <p>Loading...</p>
            ) : (
              <AvForm model={isNew ? {} : variantEntity} onSubmit={this.saveEntity}>
                {!isNew ? (
                  <AvGroup>
                    <Label for="variant-id">
                      <Translate contentKey="global.field.id">ID</Translate>
                    </Label>
                    <AvInput id="variant-id" type="text" className="form-control" name="id" required readOnly />
                  </AvGroup>
                ) : null}
                <AvGroup>
                  <Label id="nameLabel" for="variant-name">
                    <Translate contentKey="beautyPointApp.variant.name">Name</Translate>
                  </Label>
                  <AvField
                    id="variant-name"
                    type="text"
                    name="name"
                    validate={{
                      required: { value: true, errorMessage: translate('entity.validation.required') }
                    }}
                  />
                </AvGroup>
                <AvGroup>
                  <Label id="priceLabel" for="variant-price">
                    <Translate contentKey="beautyPointApp.variant.price">Price</Translate>
                  </Label>
                  <AvField
                    id="variant-price"
                    type="text"
                    name="price"
                    validate={{
                      required: { value: true, errorMessage: translate('entity.validation.required') },
                      number: { value: true, errorMessage: translate('entity.validation.number') }
                    }}
                  />
                </AvGroup>
                <AvGroup>
                  <Label id="sessionTimeLabel" for="variant-sessionTime">
                    <Translate contentKey="beautyPointApp.variant.sessionTime">Session Time</Translate>
                  </Label>
                  <AvField
                    id="variant-sessionTime"
                    type="string"
                    className="form-control"
                    name="sessionTime"
                    validate={{
                      required: { value: true, errorMessage: translate('entity.validation.required') },
                      number: { value: true, errorMessage: translate('entity.validation.number') }
                    }}
                  />
                </AvGroup>
                <AvGroup>
                  <Label id="activeLabel" check>
                    <AvInput id="variant-active" type="checkbox" className="form-control" name="active" />
                    <Translate contentKey="beautyPointApp.variant.active">Active</Translate>
                  </Label>
                </AvGroup>
                <AvGroup>
                  <Label for="variant-offer">
                    <Translate contentKey="beautyPointApp.variant.offer">Offer</Translate>
                  </Label>
                  <AvInput id="variant-offer" type="select" className="form-control" name="offer.id">
                    <option value="" key="0" />
                    {offers
                      ? offers.map(otherEntity => (
                          <option value={otherEntity.id} key={otherEntity.id}>
                            {otherEntity.id}
                          </option>
                        ))
                      : null}
                  </AvInput>
                </AvGroup>
                <AvGroup>
                  <Label for="variant-executor">
                    <Translate contentKey="beautyPointApp.variant.executor">Executor</Translate>
                  </Label>
                  <AvInput
                    id="variant-executor"
                    type="select"
                    multiple
                    className="form-control"
                    name="executors"
                    value={variantEntity.executors && variantEntity.executors.map(e => e.id)}
                  >
                    <option value="" key="0" />
                    {masters
                      ? masters.map(otherEntity => (
                          <option value={otherEntity.id} key={otherEntity.id}>
                            {otherEntity.id}
                          </option>
                        ))
                      : null}
                  </AvInput>
                </AvGroup>
                <Button tag={Link} id="cancel-save" to="/entity/variant" replace color="info">
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
  offers: storeState.offer.entities,
  masters: storeState.master.entities,
  variantEntity: storeState.variant.entity,
  loading: storeState.variant.loading,
  updating: storeState.variant.updating,
  updateSuccess: storeState.variant.updateSuccess
});

const mapDispatchToProps = {
  getOffers,
  getMasters,
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
)(VariantUpdate);
