import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvFeedback, AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
// tslint:disable-next-line:no-unused-variable
import { Translate, translate, ICrudGetAction, ICrudGetAllAction, ICrudPutAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { IUser } from 'app/shared/model/user.model';
import { getUsers } from 'app/modules/administration/user-management/user-management.reducer';
import { getEntity, updateEntity, createEntity, reset } from './image.reducer';
import { IImage } from 'app/shared/model/image.model';
// tslint:disable-next-line:no-unused-variable
import { convertDateTimeFromServer, convertDateTimeToServer } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface IImageUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export interface IImageUpdateState {
  isNew: boolean;
  ownerId: string;
}

export class ImageUpdate extends React.Component<IImageUpdateProps, IImageUpdateState> {
  constructor(props) {
    super(props);
    this.state = {
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

    this.props.getUsers();
  }

  saveEntity = (event, errors, values) => {
    if (errors.length === 0) {
      const { imageEntity } = this.props;
      const entity = {
        ...imageEntity,
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
    this.props.history.push('/entity/image');
  };

  render() {
    const { imageEntity, users, loading, updating } = this.props;
    const { isNew } = this.state;

    return (
      <div>
        <Row className="justify-content-center">
          <Col md="8">
            <h2 id="beautyPointApp.image.home.createOrEditLabel">
              <Translate contentKey="beautyPointApp.image.home.createOrEditLabel">Create or edit a Image</Translate>
            </h2>
          </Col>
        </Row>
        <Row className="justify-content-center">
          <Col md="8">
            {loading ? (
              <p>Loading...</p>
            ) : (
              <AvForm model={isNew ? {} : imageEntity} onSubmit={this.saveEntity}>
                {!isNew ? (
                  <AvGroup>
                    <Label for="image-id">
                      <Translate contentKey="global.field.id">ID</Translate>
                    </Label>
                    <AvInput id="image-id" type="text" className="form-control" name="id" required readOnly />
                  </AvGroup>
                ) : null}
                <AvGroup>
                  <Label id="sourceLabel" for="image-source">
                    <Translate contentKey="beautyPointApp.image.source">Source</Translate>
                  </Label>
                  <AvField
                    id="image-source"
                    type="text"
                    name="source"
                    validate={{
                      required: { value: true, errorMessage: translate('entity.validation.required') }
                    }}
                  />
                </AvGroup>
                <AvGroup>
                  <Label id="imageTypeLabel" for="image-imageType">
                    <Translate contentKey="beautyPointApp.image.imageType">Image Type</Translate>
                  </Label>
                  <AvInput
                    id="image-imageType"
                    type="select"
                    className="form-control"
                    name="imageType"
                    value={(!isNew && imageEntity.imageType) || 'PROFILE'}
                  >
                    <option value="PROFILE">
                      <Translate contentKey="beautyPointApp.ImageTypeEnum.PROFILE" />
                    </option>
                    <option value="SALON">
                      <Translate contentKey="beautyPointApp.ImageTypeEnum.SALON" />
                    </option>
                    <option value="OFFER">
                      <Translate contentKey="beautyPointApp.ImageTypeEnum.OFFER" />
                    </option>
                    <option value="VARIANT">
                      <Translate contentKey="beautyPointApp.ImageTypeEnum.VARIANT" />
                    </option>
                    <option value="CATEGORY">
                      <Translate contentKey="beautyPointApp.ImageTypeEnum.CATEGORY" />
                    </option>
                    <option value="SYSTEM">
                      <Translate contentKey="beautyPointApp.ImageTypeEnum.SYSTEM" />
                    </option>
                  </AvInput>
                </AvGroup>
                <AvGroup>
                  <Label for="image-owner">
                    <Translate contentKey="beautyPointApp.image.owner">Owner</Translate>
                  </Label>
                  <AvInput
                    id="image-owner"
                    type="select"
                    className="form-control"
                    name="owner.id"
                    value={isNew ? users[0] && users[0].id : imageEntity.owner.id}
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
                <Button tag={Link} id="cancel-save" to="/entity/image" replace color="info">
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
  users: storeState.userManagement.users,
  imageEntity: storeState.image.entity,
  loading: storeState.image.loading,
  updating: storeState.image.updating,
  updateSuccess: storeState.image.updateSuccess
});

const mapDispatchToProps = {
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
)(ImageUpdate);
