import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
// tslint:disable-next-line:no-unused-variable
import { Translate, ICrudGetAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './variant.reducer';
import { IVariant } from 'app/shared/model/variant.model';
// tslint:disable-next-line:no-unused-variable
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IVariantDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export class VariantDetail extends React.Component<IVariantDetailProps> {
  componentDidMount() {
    this.props.getEntity(this.props.match.params.id);
  }

  render() {
    const { variantEntity } = this.props;
    return (
      <Row>
        <Col md="8">
          <h2>
            <Translate contentKey="beautyPointApp.variant.detail.title">Variant</Translate> [<b>{variantEntity.id}</b>]
          </h2>
          <dl className="jh-entity-details">
            <dt>
              <span id="name">
                <Translate contentKey="beautyPointApp.variant.name">Name</Translate>
              </span>
            </dt>
            <dd>{variantEntity.name}</dd>
            <dt>
              <span id="price">
                <Translate contentKey="beautyPointApp.variant.price">Price</Translate>
              </span>
            </dt>
            <dd>{variantEntity.price}</dd>
            <dt>
              <span id="sessionTime">
                <Translate contentKey="beautyPointApp.variant.sessionTime">Session Time</Translate>
              </span>
            </dt>
            <dd>{variantEntity.sessionTime}</dd>
            <dt>
              <span id="active">
                <Translate contentKey="beautyPointApp.variant.active">Active</Translate>
              </span>
            </dt>
            <dd>{variantEntity.active ? 'true' : 'false'}</dd>
            <dt>
              <Translate contentKey="beautyPointApp.variant.offer">Offer</Translate>
            </dt>
            <dd>{variantEntity.offer ? variantEntity.offer.id : ''}</dd>
            <dt>
              <Translate contentKey="beautyPointApp.variant.executor">Executor</Translate>
            </dt>
            <dd>
              {variantEntity.executors
                ? variantEntity.executors.map((val, i) => (
                    <span key={val.id}>
                      <a>{val.id}</a>
                      {i === variantEntity.executors.length - 1 ? '' : ', '}
                    </span>
                  ))
                : null}
            </dd>
          </dl>
          <Button tag={Link} to="/entity/variant" replace color="info">
            <FontAwesomeIcon icon="arrow-left" />{' '}
            <span className="d-none d-md-inline">
              <Translate contentKey="entity.action.back">Back</Translate>
            </span>
          </Button>
          &nbsp;
          <Button tag={Link} to={`/entity/variant/${variantEntity.id}/edit`} replace color="primary">
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

const mapStateToProps = ({ variant }: IRootState) => ({
  variantEntity: variant.entity
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(VariantDetail);
