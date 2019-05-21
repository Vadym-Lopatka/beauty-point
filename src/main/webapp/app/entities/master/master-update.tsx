import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvFeedback, AvForm, AvGroup, AvInput } from 'availity-reactstrap-validation';
// tslint:disable-next-line:no-unused-variable
import { Translate, translate, ICrudGetAction, ICrudGetAllAction, ICrudPutAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { ISalon } from 'app/shared/model/salon.model';
import { getEntities as getSalons } from 'app/entities/salon/salon.reducer';
import { ICategory } from 'app/shared/model/category.model';
import { getEntities as getCategories } from 'app/entities/category/category.reducer';
import { IUser } from 'app/shared/model/user.model';
import { getUsers } from 'app/modules/administration/user-management/user-management.reducer';
import { getEntity, updateEntity, createEntity, reset } from './master.reducer';
import { IMaster } from 'app/shared/model/master.model';
// tslint:disable-next-line:no-unused-variable
import { convertDateTimeFromServer, convertDateTimeToServer } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface IMasterUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export interface IMasterUpdateState {
  isNew: boolean;
  idscategory: any[];
  salonId: string;
  userId: string;
}

export class MasterUpdate extends React.Component<IMasterUpdateProps, IMasterUpdateState> {
  constructor(props) {
    super(props);
    this.state = {
      idscategory: [],
      salonId: '0',
      userId: '0',
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
    this.props.getCategories();
    this.props.getUsers();
  }

  saveEntity = (event, errors, values) => {
    if (errors.length === 0) {
      const { masterEntity } = this.props;
      const entity = {
        ...masterEntity,
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
    this.props.history.push('/entity/master');
  };

  render() {
    const { masterEntity, salons, categories, users, loading, updating } = this.props;
    const { isNew } = this.state;

    return (
      <div>
        <Row className="justify-content-center">
          <Col md="8">
            <h2 id="beautyPointApp.master.home.createOrEditLabel">
              <Translate contentKey="beautyPointApp.master.home.createOrEditLabel">Create or edit a Master</Translate>
            </h2>
          </Col>
        </Row>
        <Row className="justify-content-center">
          <Col md="8">
            {loading ? (
              <p>Loading...</p>
            ) : (
              <AvForm model={isNew ? {} : masterEntity} onSubmit={this.saveEntity}>
                {!isNew ? (
                  <AvGroup>
                    <Label for="master-id">
                      <Translate contentKey="global.field.id">ID</Translate>
                    </Label>
                    <AvInput id="master-id" type="text" className="form-control" name="id" required readOnly />
                  </AvGroup>
                ) : null}
                <AvGroup>
                  <Label for="master-salon">
                    <Translate contentKey="beautyPointApp.master.salon">Salon</Translate>
                  </Label>
                  <AvInput id="master-salon" type="select" className="form-control" name="salon.id">
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
                  <Label for="master-category">
                    <Translate contentKey="beautyPointApp.master.category">Category</Translate>
                  </Label>
                  <AvInput
                    id="master-category"
                    type="select"
                    multiple
                    className="form-control"
                    name="categories"
                    value={masterEntity.categories && masterEntity.categories.map(e => e.id)}
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
                  <Label for="master-user">
                    <Translate contentKey="beautyPointApp.master.user">User</Translate>
                  </Label>
                  <AvInput
                    id="master-user"
                    type="select"
                    className="form-control"
                    name="user.id"
                    value={isNew ? users[0] && users[0].id : masterEntity.user.id}
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
                <Button tag={Link} id="cancel-save" to="/entity/master" replace color="info">
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
  categories: storeState.category.entities,
  users: storeState.userManagement.users,
  masterEntity: storeState.master.entity,
  loading: storeState.master.loading,
  updating: storeState.master.updating,
  updateSuccess: storeState.master.updateSuccess
});

const mapDispatchToProps = {
  getSalons,
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
)(MasterUpdate);
