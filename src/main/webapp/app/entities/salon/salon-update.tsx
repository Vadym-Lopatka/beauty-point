import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvFeedback, AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
// tslint:disable-next-line:no-unused-variable
import { Translate, translate, ICrudGetAction, ICrudGetAllAction, ICrudPutAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { IImage } from 'app/shared/model/image.model';
import { getEntities as getImages } from 'app/entities/image/image.reducer';
import { ITimeTable } from 'app/shared/model/time-table.model';
import { getEntities as getTimeTables } from 'app/entities/time-table/time-table.reducer';
import { ICategory } from 'app/shared/model/category.model';
import { getEntities as getCategories } from 'app/entities/category/category.reducer';
import { IUser } from 'app/shared/model/user.model';
import { getUsers } from 'app/modules/administration/user-management/user-management.reducer';
import { getEntity, updateEntity, createEntity, reset } from './salon.reducer';
import { ISalon } from 'app/shared/model/salon.model';
// tslint:disable-next-line:no-unused-variable
import { convertDateTimeFromServer, convertDateTimeToServer } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface ISalonUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export interface ISalonUpdateState {
  isNew: boolean;
  idscategory: any[];
  imageId: string;
  timeTableId: string;
  ownerId: string;
}

export class SalonUpdate extends React.Component<ISalonUpdateProps, ISalonUpdateState> {
  constructor(props) {
    super(props);
    this.state = {
      idscategory: [],
      imageId: '0',
      timeTableId: '0',
      ownerId: '0',
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

    this.props.getImages();
    this.props.getTimeTables();
    this.props.getCategories();
    this.props.getUsers();
  }

  saveEntity = (event, errors, values) => {
    if (errors.length === 0) {
      const { salonEntity } = this.props;
      const entity = {
        ...salonEntity,
        ...values,
        categories: mapIdList(values.categories)
      };

      if (this.state.isNew) {
        this.props.createEntity(entity);
      } else {
        this.props.updateEntity(entity);
      }
    }
  };

  handleClose = () => {
    this.props.history.push('/entity/salon');
  };

  render() {
    const { salonEntity, images, timeTables, categories, users, loading, updating } = this.props;
    const { isNew } = this.state;

    return (
      <div>
        <Row className="justify-content-center">
          <Col md="8">
            <h2 id="beautyPointApp.salon.home.createOrEditLabel">
              <Translate contentKey="beautyPointApp.salon.home.createOrEditLabel">Create or edit a Salon</Translate>
            </h2>
          </Col>
        </Row>
        <Row className="justify-content-center">
          <Col md="8">
            {loading ? (
              <p>Loading...</p>
            ) : (
              <AvForm model={isNew ? {} : salonEntity} onSubmit={this.saveEntity}>
                {!isNew ? (
                  <AvGroup>
                    <Label for="salon-id">
                      <Translate contentKey="global.field.id">ID</Translate>
                    </Label>
                    <AvInput id="salon-id" type="text" className="form-control" name="id" required readOnly />
                  </AvGroup>
                ) : null}
                <AvGroup>
                  <Label id="nameLabel" for="salon-name">
                    <Translate contentKey="beautyPointApp.salon.name">Name</Translate>
                  </Label>
                  <AvField
                    id="salon-name"
                    type="text"
                    name="name"
                    validate={{
                      required: { value: true, errorMessage: translate('entity.validation.required') }
                    }}
                  />
                </AvGroup>
                <AvGroup>
                  <Label id="sloganLabel" for="salon-slogan">
                    <Translate contentKey="beautyPointApp.salon.slogan">Slogan</Translate>
                  </Label>
                  <AvField id="salon-slogan" type="text" name="slogan" />
                </AvGroup>
                <AvGroup>
                  <Label id="locationLabel" for="salon-location">
                    <Translate contentKey="beautyPointApp.salon.location">Location</Translate>
                  </Label>
                  <AvField
                    id="salon-location"
                    type="text"
                    name="location"
                    validate={{
                      required: { value: true, errorMessage: translate('entity.validation.required') }
                    }}
                  />
                </AvGroup>
                <AvGroup>
                  <Label id="statusLabel" for="salon-status">
                    <Translate contentKey="beautyPointApp.salon.status">Status</Translate>
                  </Label>
                  <AvInput
                    id="salon-status"
                    type="select"
                    className="form-control"
                    name="status"
                    value={(!isNew && salonEntity.status) || 'EXAMPLE'}
                  >
                    <option value="EXAMPLE">
                      <Translate contentKey="beautyPointApp.SalonStatusEnum.EXAMPLE" />
                    </option>
                    <option value="DRAFT">
                      <Translate contentKey="beautyPointApp.SalonStatusEnum.DRAFT" />
                    </option>
                    <option value="CREATED">
                      <Translate contentKey="beautyPointApp.SalonStatusEnum.CREATED" />
                    </option>
                    <option value="ACTIVATED">
                      <Translate contentKey="beautyPointApp.SalonStatusEnum.ACTIVATED" />
                    </option>
                    <option value="DEACTIVATED">
                      <Translate contentKey="beautyPointApp.SalonStatusEnum.DEACTIVATED" />
                    </option>
                    <option value="DELETED">
                      <Translate contentKey="beautyPointApp.SalonStatusEnum.DELETED" />
                    </option>
                    <option value="BANNED">
                      <Translate contentKey="beautyPointApp.SalonStatusEnum.BANNED" />
                    </option>
                  </AvInput>
                </AvGroup>
                <AvGroup>
                  <Label id="systemCommentLabel" for="salon-systemComment">
                    <Translate contentKey="beautyPointApp.salon.systemComment">System Comment</Translate>
                  </Label>
                  <AvField id="salon-systemComment" type="text" name="systemComment" />
                </AvGroup>
                <AvGroup>
                  <Label id="typeLabel" for="salon-type">
                    <Translate contentKey="beautyPointApp.salon.type">Type</Translate>
                  </Label>
                  <AvInput
                    id="salon-type"
                    type="select"
                    className="form-control"
                    name="type"
                    value={(!isNew && salonEntity.type) || 'STANDARD'}
                  >
                    <option value="STANDARD">
                      <Translate contentKey="beautyPointApp.SalonTypeEnum.STANDARD" />
                    </option>
                    <option value="PART_TIME_CUSTOM">
                      <Translate contentKey="beautyPointApp.SalonTypeEnum.PART_TIME_CUSTOM" />
                    </option>
                    <option value="FULL_TIME_CUSTOM">
                      <Translate contentKey="beautyPointApp.SalonTypeEnum.FULL_TIME_CUSTOM" />
                    </option>
                    <option value="SECOND_WORK_CUSTOM">
                      <Translate contentKey="beautyPointApp.SalonTypeEnum.SECOND_WORK_CUSTOM" />
                    </option>
                  </AvInput>
                </AvGroup>
                <AvGroup>
                  <Label for="salon-image">
                    <Translate contentKey="beautyPointApp.salon.image">Image</Translate>
                  </Label>
                  <AvInput id="salon-image" type="select" className="form-control" name="image.id">
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
                  <Label for="salon-timeTable">
                    <Translate contentKey="beautyPointApp.salon.timeTable">Time Table</Translate>
                  </Label>
                  <AvInput id="salon-timeTable" type="select" className="form-control" name="timeTable.id">
                    <option value="" key="0" />
                    {timeTables
                      ? timeTables.map(otherEntity => (
                          <option value={otherEntity.id} key={otherEntity.id}>
                            {otherEntity.id}
                          </option>
                        ))
                      : null}
                  </AvInput>
                </AvGroup>
                <AvGroup>
                  <Label for="salon-category">
                    <Translate contentKey="beautyPointApp.salon.category">Category</Translate>
                  </Label>
                  <AvInput
                    id="salon-category"
                    type="select"
                    multiple
                    className="form-control"
                    name="categories"
                    value={salonEntity.categories && salonEntity.categories.map(e => e.id)}
                  >
                    <option value="" key="0" />
                    {categories
                      ? categories.map(otherEntity => (
                          <option value={otherEntity.id} key={otherEntity.id}>
                            {otherEntity.name}
                          </option>
                        ))
                      : null}
                  </AvInput>
                </AvGroup>
                <AvGroup>
                  <Label for="salon-owner">
                    <Translate contentKey="beautyPointApp.salon.owner">Owner</Translate>
                  </Label>
                  <AvInput
                    id="salon-owner"
                    type="select"
                    className="form-control"
                    name="owner.id"
                    value={isNew ? users[0] && users[0].id : salonEntity.owner}
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
                <Button tag={Link} id="cancel-save" to="/entity/salon" replace color="info">
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
  images: storeState.image.entities,
  timeTables: storeState.timeTable.entities,
  categories: storeState.category.entities,
  users: storeState.userManagement.users,
  salonEntity: storeState.salon.entity,
  loading: storeState.salon.loading,
  updating: storeState.salon.updating,
  updateSuccess: storeState.salon.updateSuccess
});

const mapDispatchToProps = {
  getImages,
  getTimeTables,
  getCategories,
  getUsers,
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
)(SalonUpdate);
