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
import { getEntity, updateEntity, createEntity, reset } from './option.reducer';
import { IOption } from 'app/shared/model/option.model';
// tslint:disable-next-line:no-unused-variable
import { convertDateTimeFromServer, convertDateTimeToServer } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface IOptionUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export interface IOptionUpdateState {
  isNew: boolean;
  offerId: string;
}

export class OptionUpdate extends React.Component<IOptionUpdateProps, IOptionUpdateState> {
  constructor(props) {
    super(props);
    this.state = {
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
  }

  saveEntity = (event, errors, values) => {
    if (errors.length === 0) {
      const { optionEntity } = this.props;
      const entity = {
        ...optionEntity,
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
    this.props.history.push('/entity/option');
  };

  render() {
    const { optionEntity, offers, loading, updating } = this.props;
    const { isNew } = this.state;

    return (
      <div>
        <Row className="justify-content-center">
          <Col md="8">
            <h2 id="beautyPointApp.option.home.createOrEditLabel">
              <Translate contentKey="beautyPointApp.option.home.createOrEditLabel">Create or edit a Option</Translate>
            </h2>
          </Col>
        </Row>
        <Row className="justify-content-center">
          <Col md="8">
            {loading ? (
              <p>Loading...</p>
            ) : (
              <AvForm model={isNew ? {} : optionEntity} onSubmit={this.saveEntity}>
                {!isNew ? (
                  <AvGroup>
                    <Label for="option-id">
                      <Translate contentKey="global.field.id">ID</Translate>
                    </Label>
                    <AvInput id="option-id" type="text" className="form-control" name="id" required readOnly />
                  </AvGroup>
                ) : null}
                <AvGroup>
                  <Label id="nameLabel" for="option-name">
                    <Translate contentKey="beautyPointApp.option.name">Name</Translate>
                  </Label>
                  <AvField
                    id="option-name"
                    type="text"
                    name="name"
                    validate={{
                      required: { value: true, errorMessage: translate('entity.validation.required') }
                    }}
                  />
                </AvGroup>
                <AvGroup>
                  <Label id="priceLabel" for="option-price">
                    <Translate contentKey="beautyPointApp.option.price">Price</Translate>
                  </Label>
                  <AvField
                    id="option-price"
                    type="text"
                    name="price"
                    validate={{
                      required: { value: true, errorMessage: translate('entity.validation.required') },
                      number: { value: true, errorMessage: translate('entity.validation.number') }
                    }}
                  />
                </AvGroup>
                <AvGroup>
                  <Label id="sessionTimeLabel" for="option-sessionTime">
                    <Translate contentKey="beautyPointApp.option.sessionTime">Session Time</Translate>
                  </Label>
                  <AvField
                    id="option-sessionTime"
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
                    <AvInput id="option-active" type="checkbox" className="form-control" name="active" />
                    <Translate contentKey="beautyPointApp.option.active">Active</Translate>
                  </Label>
                </AvGroup>
                <AvGroup>
                  <Label for="option-offer">
                    <Translate contentKey="beautyPointApp.option.offer">Offer</Translate>
                  </Label>
                  <AvInput id="option-offer" type="select" className="form-control" name="offer.id">
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
                <Button tag={Link} id="cancel-save" to="/entity/option" replace color="info">
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
  optionEntity: storeState.option.entity,
  loading: storeState.option.loading,
  updating: storeState.option.updating,
  updateSuccess: storeState.option.updateSuccess
});

const mapDispatchToProps = {
  getOffers,
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
)(OptionUpdate);
