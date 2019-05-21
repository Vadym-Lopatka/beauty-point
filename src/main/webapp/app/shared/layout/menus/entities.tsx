import React from 'react';
import MenuItem from 'app/shared/layout/menus/menu-item';
import { DropdownItem } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { Translate, translate } from 'react-jhipster';
import { NavLink as Link } from 'react-router-dom';
import { NavDropdown } from './menu-components';

export const EntitiesMenu = props => (
  // tslint:disable-next-line:jsx-self-close
  <NavDropdown icon="th-list" name={translate('global.menu.entities.main')} id="entity-menu">
    <MenuItem icon="asterisk" to="/entity/category">
      <Translate contentKey="global.menu.entities.category" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/entity/image">
      <Translate contentKey="global.menu.entities.image" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/entity/master">
      <Translate contentKey="global.menu.entities.master" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/entity/offer">
      <Translate contentKey="global.menu.entities.offer" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/entity/option">
      <Translate contentKey="global.menu.entities.option" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/entity/record">
      <Translate contentKey="global.menu.entities.record" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/entity/salon">
      <Translate contentKey="global.menu.entities.salon" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/entity/subscriber">
      <Translate contentKey="global.menu.entities.subscriber" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/entity/time-table">
      <Translate contentKey="global.menu.entities.timeTable" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/entity/variant">
      <Translate contentKey="global.menu.entities.variant" />
    </MenuItem>
    {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
  </NavDropdown>
);
