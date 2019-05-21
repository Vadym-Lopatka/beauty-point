import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
// tslint:disable-next-line:no-unused-variable
import { Translate, ICrudGetAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './image.reducer';
import { IImage } from 'app/shared/model/image.model';
// tslint:disable-next-line:no-unused-variable
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IImageDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export class ImageDetail extends React.Component<IImageDetailProps> {
  componentDidMount() {
    this.props.getEntity(this.props.match.params.id);
  }

  render() {
    const { imageEntity } = this.props;
    return (
      <Row>
        <Col md="8">
          <h2>
            <Translate contentKey="beautyPointApp.image.detail.title">Image</Translate> [<b>{imageEntity.id}</b>]
          </h2>
          <dl className="jh-entity-details">
            <dt>
              <span id="source">
                <Translate contentKey="beautyPointApp.image.source">Source</Translate>
              </span>
            </dt>
            <dd>{imageEntity.source}</dd>
            <dt>
              <span id="imageType">
                <Translate contentKey="beautyPointApp.image.imageType">Image Type</Translate>
              </span>
            </dt>
            <dd>{imageEntity.imageType}</dd>
            <dt>
              <Translate contentKey="beautyPointApp.image.owner">Owner</Translate>
            </dt>
            <dd>{imageEntity.owner ? imageEntity.owner.login : ''}</dd>
          </dl>
          <Button tag={Link} to="/entity/image" replace color="info">
            <FontAwesomeIcon icon="arrow-left" />{' '}
            <span className="d-none d-md-inline">
              <Translate contentKey="entity.action.back">Back</Translate>
            </span>
          </Button>
          &nbsp;
          <Button tag={Link} to={`/entity/image/${imageEntity.id}/edit`} replace color="primary">
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

const mapStateToProps = ({ image }: IRootState) => ({
  imageEntity: image.entity
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(ImageDetail);
