import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvFeedback, AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
// tslint:disable-next-line:no-unused-variable
import { Translate, translate, ICrudGetAction, ICrudGetAllAction, ICrudPutAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { IMaster } from 'app/shared/model/master.model';
import { getEntities as getMasters } from 'app/entities/master/master.reducer';
import { IVariant } from 'app/shared/model/variant.model';
import { getEntities as getVariants } from 'app/entities/variant/variant.reducer';
import { IOption } from 'app/shared/model/option.model';
import { getEntities as getOptions } from 'app/entities/option/option.reducer';
import { IUser } from 'app/shared/model/user.model';
import { getUsers } from 'app/modules/administration/user-management/user-management.reducer';
import { ISalon } from 'app/shared/model/salon.model';
import { getEntities as getSalons } from 'app/entities/salon/salon.reducer';
import { getEntity, updateEntity, createEntity, reset } from './record.reducer';
import { IRecord } from 'app/shared/model/record.model';
// tslint:disable-next-line:no-unused-variable
import { convertDateTimeFromServer, convertDateTimeToServer } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface IRecordUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export interface IRecordUpdateState {
  isNew: boolean;
  idsoption: any[];
  masterId: string;
  variantId: string;
  userId: string;
  salonId: string;
}

export class RecordUpdate extends React.Component<IRecordUpdateProps, IRecordUpdateState> {
  constructor(props) {
    super(props);
    this.state = {
      idsoption: [],
      masterId: '0',
      variantId: '0',
      userId: '0',
      salonId: '0',
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

    this.props.getMasters();
    this.props.getVariants();
    this.props.getOptions();
    this.props.getUsers();
    this.props.getSalons();
  }

  saveEntity = (event, errors, values) => {
    values.bookingTime = convertDateTimeToServer(values.bookingTime);

    if (errors.length === 0) {
      const { recordEntity } = this.props;
      const entity = {
        ...recordEntity,
        ...values,
        options: mapIdList(values.options)
      };

      if (this.state.isNew) {
        this.props.createEntity(entity);
      } else {
        this.props.updateEntity(entity);
      }
    }
  };

  handleClose = () => {
    this.props.history.push('/entity/record');
  };

  render() {
    const { recordEntity, masters, variants, options, users, salons, loading, updating } = this.props;
    const { isNew } = this.state;

    return (
      <div>
        <Row className="justify-content-center">
          <Col md="8">
            <h2 id="beautyPointApp.record.home.createOrEditLabel">
              <Translate contentKey="beautyPointApp.record.home.createOrEditLabel">Create or edit a Record</Translate>
            </h2>
          </Col>
        </Row>
        <Row className="justify-content-center">
          <Col md="8">
            {loading ? (
              <p>Loading...</p>
            ) : (
              <AvForm model={isNew ? {} : recordEntity} onSubmit={this.saveEntity}>
                {!isNew ? (
                  <AvGroup>
                    <Label for="record-id">
                      <Translate contentKey="global.field.id">ID</Translate>
                    </Label>
                    <AvInput id="record-id" type="text" className="form-control" name="id" required readOnly />
                  </AvGroup>
                ) : null}
                <AvGroup>
                  <Label id="bookingTimeLabel" for="record-bookingTime">
                    <Translate contentKey="beautyPointApp.record.bookingTime">Booking Time</Translate>
                  </Label>
                  <AvInput
                    id="record-bookingTime"
                    type="datetime-local"
                    className="form-control"
                    name="bookingTime"
                    placeholder={'YYYY-MM-DD HH:mm'}
                    value={isNew ? null : convertDateTimeFromServer(this.props.recordEntity.bookingTime)}
                    validate={{
                      required: { value: true, errorMessage: translate('entity.validation.required') }
                    }}
                  />
                </AvGroup>
                <AvGroup>
                  <Label id="durationLabel" for="record-duration">
                    <Translate contentKey="beautyPointApp.record.duration">Duration</Translate>
                  </Label>
                  <AvField
                    id="record-duration"
                    type="string"
                    className="form-control"
                    name="duration"
                    validate={{
                      required: { value: true, errorMessage: translate('entity.validation.required') },
                      number: { value: true, errorMessage: translate('entity.validation.number') }
                    }}
                  />
                </AvGroup>
                <AvGroup>
                  <Label id="totalPriceLabel" for="record-totalPrice">
                    <Translate contentKey="beautyPointApp.record.totalPrice">Total Price</Translate>
                  </Label>
                  <AvField
                    id="record-totalPrice"
                    type="text"
                    name="totalPrice"
                    validate={{
                      required: { value: true, errorMessage: translate('entity.validation.required') },
                      number: { value: true, errorMessage: translate('entity.validation.number') }
                    }}
                  />
                </AvGroup>
                <AvGroup>
                  <Label id="orderStatusLabel" for="record-orderStatus">
                    <Translate contentKey="beautyPointApp.record.orderStatus">Order Status</Translate>
                  </Label>
                  <AvInput
                    id="record-orderStatus"
                    type="select"
                    className="form-control"
                    name="orderStatus"
                    value={(!isNew && recordEntity.orderStatus) || 'CREATED_BY_SALON'}
                  >
                    <option value="CREATED_BY_SALON">
                      <Translate contentKey="beautyPointApp.OrderStatusEnum.CREATED_BY_SALON" />
                    </option>
                    <option value="CREATED_BY_CLIENT">
                      <Translate contentKey="beautyPointApp.OrderStatusEnum.CREATED_BY_CLIENT" />
                    </option>
                    <option value="INFORMED">
                      <Translate contentKey="beautyPointApp.OrderStatusEnum.INFORMED" />
                    </option>
                    <option value="PENDING">
                      <Translate contentKey="beautyPointApp.OrderStatusEnum.PENDING" />
                    </option>
                    <option value="IN_PROGRESS">
                      <Translate contentKey="beautyPointApp.OrderStatusEnum.IN_PROGRESS" />
                    </option>
                    <option value="DONE">
                      <Translate contentKey="beautyPointApp.OrderStatusEnum.DONE" />
                    </option>
                    <option value="CANCELED_BY_CLIENT">
                      <Translate contentKey="beautyPointApp.OrderStatusEnum.CANCELED_BY_CLIENT" />
                    </option>
                    <option value="CANCELED_BY_SALON">
                      <Translate contentKey="beautyPointApp.OrderStatusEnum.CANCELED_BY_SALON" />
                    </option>
                    <option value="PROBLEM">
                      <Translate contentKey="beautyPointApp.OrderStatusEnum.PROBLEM" />
                    </option>
                  </AvInput>
                </AvGroup>
                <AvGroup>
                  <Label id="commentLabel" for="record-comment">
                    <Translate contentKey="beautyPointApp.record.comment">Comment</Translate>
                  </Label>
                  <AvField id="record-comment" type="text" name="comment" />
                </AvGroup>
                <AvGroup>
                  <Label for="record-master">
                    <Translate contentKey="beautyPointApp.record.master">Master</Translate>
                  </Label>
                  <AvInput id="record-master" type="select" className="form-control" name="master.id">
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
                <AvGroup>
                  <Label for="record-variant">
                    <Translate contentKey="beautyPointApp.record.variant">Variant</Translate>
                  </Label>
                  <AvInput id="record-variant" type="select" className="form-control" name="variant.id">
                    <option value="" key="0" />
                    {variants
                      ? variants.map(otherEntity => (
                          <option value={otherEntity.id} key={otherEntity.id}>
                            {otherEntity.id}
                          </option>
                        ))
                      : null}
                  </AvInput>
                </AvGroup>
                <AvGroup>
                  <Label for="record-option">
                    <Translate contentKey="beautyPointApp.record.option">Option</Translate>
                  </Label>
                  <AvInput
                    id="record-option"
                    type="select"
                    multiple
                    className="form-control"
                    name="options"
                    value={recordEntity.options && recordEntity.options.map(e => e.id)}
                  >
                    <option value="" key="0" />
                    {options
                      ? options.map(otherEntity => (
                          <option value={otherEntity.id} key={otherEntity.id}>
                            {otherEntity.id}
                          </option>
                        ))
                      : null}
                  </AvInput>
                </AvGroup>
                <AvGroup>
                  <Label for="record-user">
                    <Translate contentKey="beautyPointApp.record.user">User</Translate>
                  </Label>
                  <AvInput
                    id="record-user"
                    type="select"
                    className="form-control"
                    name="user.id"
                    value={isNew ? users[0] && users[0].id : recordEntity.user.id}
                    required
                  >
                    {users
                      ? users.map(otherEntity => (
                          <option value={otherEntity.id} key={otherEntity.id}>
                            {otherEntity.login}
                          </option>
                        ))
                      : null}
                  </AvInput>
                  <AvFeedback>
                    <Translate contentKey="entity.validation.required">This field is required.</Translate>
                  </AvFeedback>
                </AvGroup>
                <AvGroup>
                  <Label for="record-salon">
                    <Translate contentKey="beautyPointApp.record.salon">Salon</Translate>
                  </Label>
                  <AvInput
                    id="record-salon"
                    type="select"
                    className="form-control"
                    name="salon.id"
                    value={isNew ? salons[0] && salons[0].id : recordEntity.salon.id}
                    required
                  >
                    {salons
                      ? salons.map(otherEntity => (
                          <option value={otherEntity.id} key={otherEntity.id}>
                            {otherEntity.id}
                          </option>
                        ))
                      : null}
                  </AvInput>
                  <AvFeedback>
                    <Translate contentKey="entity.validation.required">This field is required.</Translate>
                  </AvFeedback>
                </AvGroup>
                <Button tag={Link} id="cancel-save" to="/entity/record" replace color="info">
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
  masters: storeState.master.entities,
  variants: storeState.variant.entities,
  options: storeState.option.entities,
  users: storeState.userManagement.users,
  salons: storeState.salon.entities,
  recordEntity: storeState.record.entity,
  loading: storeState.record.loading,
  updating: storeState.record.updating,
  updateSuccess: storeState.record.updateSuccess
});

const mapDispatchToProps = {
  getMasters,
  getVariants,
  getOptions,
  getUsers,
  getSalons,
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
)(RecordUpdate);
