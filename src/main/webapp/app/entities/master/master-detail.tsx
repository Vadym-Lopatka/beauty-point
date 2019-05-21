import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
// tslint:disable-next-line:no-unused-variable
import { Translate, ICrudGetAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './master.reducer';
import { IMaster } from 'app/shared/model/master.model';
// tslint:disable-next-line:no-unused-variable
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IMasterDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export class MasterDetail extends React.Component<IMasterDetailProps> {
  componentDidMount() {
    this.props.getEntity(this.props.match.params.id);
  }

  render() {
    const { masterEntity } = this.props;
    return (
      <Row>
        <Col md="8">
          <h2>
            <Translate contentKey="beautyPointApp.master.detail.title">Master</Translate> [<b>{masterEntity.id}</b>]
          </h2>
          <dl className="jh-entity-details">
            <dt>
              <Translate contentKey="beautyPointApp.master.salon">Salon</Translate>
            </dt>
            <dd>{masterEntity.salon ? masterEntity.salon.id : ''}</dd>
            <dt>
              <Translate contentKey="beautyPointApp.master.category">Category</Translate>
            </dt>
            <dd>
              {masterEntity.categories
                ? masterEntity.categories.map((val, i) => (
                    <span key={val.id}>
                      <a>{val.name}</a>
                      {i === masterEntity.categories.length - 1 ? '' : ', '}
                    </span>
                  ))
                : null}
            </dd>
            <dt>
              <Translate contentKey="beautyPointApp.master.user">User</Translate>
            </dt>
            <dd>{masterEntity.user ? masterEntity.user.login : ''}</dd>
          </dl>
          <Button tag={Link} to="/entity/master" replace color="info">
            <FontAwesomeIcon icon="arrow-left" />{' '}
            <span className="d-none d-md-inline">
              <Translate contentKey="entity.action.back">Back</Translate>
            </span>
          </Button>
          &nbsp;
          <Button tag={Link} to={`/entity/master/${masterEntity.id}/edit`} replace color="primary">
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

const mapStateToProps = ({ master }: IRootState) => ({
  masterEntity: master.entity
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(MasterDetail);
