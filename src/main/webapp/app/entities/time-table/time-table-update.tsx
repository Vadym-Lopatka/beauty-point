import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvFeedback, AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
// tslint:disable-next-line:no-unused-variable
import { Translate, translate, ICrudGetAction, ICrudGetAllAction, ICrudPutAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { getEntity, updateEntity, createEntity, reset } from './time-table.reducer';
import { ITimeTable } from 'app/shared/model/time-table.model';
// tslint:disable-next-line:no-unused-variable
import { convertDateTimeFromServer, convertDateTimeToServer } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface ITimeTableUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export interface ITimeTableUpdateState {
  isNew: boolean;
}

export class TimeTableUpdate extends React.Component<ITimeTableUpdateProps, ITimeTableUpdateState> {
  constructor(props) {
    super(props);
    this.state = {
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
  }

  saveEntity = (event, errors, values) => {
    if (errors.length === 0) {
      const { timeTableEntity } = this.props;
      const entity = {
        ...timeTableEntity,
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
    this.props.history.push('/entity/time-table');
  };

  render() {
    const { timeTableEntity, loading, updating } = this.props;
    const { isNew } = this.state;

    return (
      <div>
        <Row className="justify-content-center">
          <Col md="8">
            <h2 id="beautyPointApp.timeTable.home.createOrEditLabel">
              <Translate contentKey="beautyPointApp.timeTable.home.createOrEditLabel">Create or edit a TimeTable</Translate>
            </h2>
          </Col>
        </Row>
        <Row className="justify-content-center">
          <Col md="8">
            {loading ? (
              <p>Loading...</p>
            ) : (
              <AvForm model={isNew ? {} : timeTableEntity} onSubmit={this.saveEntity}>
                {!isNew ? (
                  <AvGroup>
                    <Label for="time-table-id">
                      <Translate contentKey="global.field.id">ID</Translate>
                    </Label>
                    <AvInput id="time-table-id" type="text" className="form-control" name="id" required readOnly />
                  </AvGroup>
                ) : null}
                <AvGroup>
                  <Label id="everyDayEqualLabel" check>
                    <AvInput id="time-table-everyDayEqual" type="checkbox" className="form-control" name="everyDayEqual" />
                    <Translate contentKey="beautyPointApp.timeTable.everyDayEqual">Every Day Equal</Translate>
                  </Label>
                </AvGroup>
                <AvGroup>
                  <Label id="moLabel" for="time-table-mo">
                    <Translate contentKey="beautyPointApp.timeTable.mo">Mo</Translate>
                  </Label>
                  <AvField
                    id="time-table-mo"
                    type="string"
                    className="form-control"
                    name="mo"
                    validate={{
                      required: { value: true, errorMessage: translate('entity.validation.required') },
                      number: { value: true, errorMessage: translate('entity.validation.number') }
                    }}
                  />
                </AvGroup>
                <AvGroup>
                  <Label id="tuLabel" for="time-table-tu">
                    <Translate contentKey="beautyPointApp.timeTable.tu">Tu</Translate>
                  </Label>
                  <AvField
                    id="time-table-tu"
                    type="string"
                    className="form-control"
                    name="tu"
                    validate={{
                      required: { value: true, errorMessage: translate('entity.validation.required') },
                      number: { value: true, errorMessage: translate('entity.validation.number') }
                    }}
                  />
                </AvGroup>
                <AvGroup>
                  <Label id="weLabel" for="time-table-we">
                    <Translate contentKey="beautyPointApp.timeTable.we">We</Translate>
                  </Label>
                  <AvField
                    id="time-table-we"
                    type="string"
                    className="form-control"
                    name="we"
                    validate={{
                      required: { value: true, errorMessage: translate('entity.validation.required') },
                      number: { value: true, errorMessage: translate('entity.validation.number') }
                    }}
                  />
                </AvGroup>
                <AvGroup>
                  <Label id="thLabel" for="time-table-th">
                    <Translate contentKey="beautyPointApp.timeTable.th">Th</Translate>
                  </Label>
                  <AvField
                    id="time-table-th"
                    type="string"
                    className="form-control"
                    name="th"
                    validate={{
                      required: { value: true, errorMessage: translate('entity.validation.required') },
                      number: { value: true, errorMessage: translate('entity.validation.number') }
                    }}
                  />
                </AvGroup>
                <AvGroup>
                  <Label id="frLabel" for="time-table-fr">
                    <Translate contentKey="beautyPointApp.timeTable.fr">Fr</Translate>
                  </Label>
                  <AvField
                    id="time-table-fr"
                    type="string"
                    className="form-control"
                    name="fr"
                    validate={{
                      required: { value: true, errorMessage: translate('entity.validation.required') },
                      number: { value: true, errorMessage: translate('entity.validation.number') }
                    }}
                  />
                </AvGroup>
                <AvGroup>
                  <Label id="saLabel" for="time-table-sa">
                    <Translate contentKey="beautyPointApp.timeTable.sa">Sa</Translate>
                  </Label>
                  <AvField
                    id="time-table-sa"
                    type="string"
                    className="form-control"
                    name="sa"
                    validate={{
                      required: { value: true, errorMessage: translate('entity.validation.required') },
                      number: { value: true, errorMessage: translate('entity.validation.number') }
                    }}
                  />
                </AvGroup>
                <AvGroup>
                  <Label id="suLabel" for="time-table-su">
                    <Translate contentKey="beautyPointApp.timeTable.su">Su</Translate>
                  </Label>
                  <AvField
                    id="time-table-su"
                    type="string"
                    className="form-control"
                    name="su"
                    validate={{
                      required: { value: true, errorMessage: translate('entity.validation.required') },
                      number: { value: true, errorMessage: translate('entity.validation.number') }
                    }}
                  />
                </AvGroup>
                <Button tag={Link} id="cancel-save" to="/entity/time-table" replace color="info">
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
  timeTableEntity: storeState.timeTable.entity,
  loading: storeState.timeTable.loading,
  updating: storeState.timeTable.updating,
  updateSuccess: storeState.timeTable.updateSuccess
});

const mapDispatchToProps = {
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
)(TimeTableUpdate);
