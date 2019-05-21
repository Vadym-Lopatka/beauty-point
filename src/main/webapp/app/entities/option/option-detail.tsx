import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
// tslint:disable-next-line:no-unused-variable
import { Translate, ICrudGetAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './option.reducer';
import { IOption } from 'app/shared/model/option.model';
// tslint:disable-next-line:no-unused-variable
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IOptionDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export class OptionDetail extends React.Component<IOptionDetailProps> {
  componentDidMount() {
    this.props.getEntity(this.props.match.params.id);
  }

  render() {
    const { optionEntity } = this.props;
    return (
      <Row>
        <Col md="8">
          <h2>
            <Translate contentKey="beautyPointApp.option.detail.title">Option</Translate> [<b>{optionEntity.id}</b>]
          </h2>
          <dl className="jh-entity-details">
            <dt>
              <span id="name">
                <Translate contentKey="beautyPointApp.option.name">Name</Translate>
              </span>
            </dt>
            <dd>{optionEntity.name}</dd>
            <dt>
              <span id="price">
                <Translate contentKey="beautyPointApp.option.price">Price</Translate>
              </span>
            </dt>
            <dd>{optionEntity.price}</dd>
            <dt>
              <span id="sessionTime">
                <Translate contentKey="beautyPointApp.option.sessionTime">Session Time</Translate>
              </span>
            </dt>
            <dd>{optionEntity.sessionTime}</dd>
            <dt>
              <span id="active">
                <Translate contentKey="beautyPointApp.option.active">Active</Translate>
              </span>
            </dt>
            <dd>{optionEntity.active ? 'true' : 'false'}</dd>
            <dt>
              <Translate contentKey="beautyPointApp.option.offer">Offer</Translate>
            </dt>
            <dd>{optionEntity.offer ? optionEntity.offer.id : ''}</dd>
          </dl>
          <Button tag={Link} to="/entity/option" replace color="info">
            <FontAwesomeIcon icon="arrow-left" />{' '}
            <span className="d-none d-md-inline">
              <Translate contentKey="entity.action.back">Back</Translate>
            </span>
          </Button>
          &nbsp;
          <Button tag={Link} to={`/entity/option/${optionEntity.id}/edit`} replace color="primary">
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

const mapStateToProps = ({ option }: IRootState) => ({
  optionEntity: option.entity
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(OptionDetail);
