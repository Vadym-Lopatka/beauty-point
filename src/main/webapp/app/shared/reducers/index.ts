import { combineReducers } from 'redux';
import { loadingBarReducer as loadingBar } from 'react-redux-loading-bar';

import locale, { LocaleState } from './locale';
import authentication, { AuthenticationState } from './authentication';
import applicationProfile, { ApplicationProfileState } from './application-profile';

import administration, { AdministrationState } from 'app/modules/administration/administration.reducer';
import userManagement, { UserManagementState } from 'app/modules/administration/user-management/user-management.reducer';
import register, { RegisterState } from 'app/modules/account/register/register.reducer';
import activate, { ActivateState } from 'app/modules/account/activate/activate.reducer';
import password, { PasswordState } from 'app/modules/account/password/password.reducer';
import settings, { SettingsState } from 'app/modules/account/settings/settings.reducer';
import passwordReset, { PasswordResetState } from 'app/modules/account/password-reset/password-reset.reducer';
// prettier-ignore
import category, {
  CategoryState
} from 'app/entities/category/category.reducer';
// prettier-ignore
import image, {
  ImageState
} from 'app/entities/image/image.reducer';
// prettier-ignore
import master, {
  MasterState
} from 'app/entities/master/master.reducer';
// prettier-ignore
import offer, {
  OfferState
} from 'app/entities/offer/offer.reducer';
// prettier-ignore
import option, {
  OptionState
} from 'app/entities/option/option.reducer';
// prettier-ignore
import record, {
  RecordState
} from 'app/entities/record/record.reducer';
// prettier-ignore
import salon, {
  SalonState
} from 'app/entities/salon/salon.reducer';
// prettier-ignore
import subscriber, {
  SubscriberState
} from 'app/entities/subscriber/subscriber.reducer';
// prettier-ignore
import timeTable, {
  TimeTableState
} from 'app/entities/time-table/time-table.reducer';
// prettier-ignore
import variant, {
  VariantState
} from 'app/entities/variant/variant.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

export interface IRootState {
  readonly authentication: AuthenticationState;
  readonly locale: LocaleState;
  readonly applicationProfile: ApplicationProfileState;
  readonly administration: AdministrationState;
  readonly userManagement: UserManagementState;
  readonly register: RegisterState;
  readonly activate: ActivateState;
  readonly passwordReset: PasswordResetState;
  readonly password: PasswordState;
  readonly settings: SettingsState;
  readonly category: CategoryState;
  readonly image: ImageState;
  readonly master: MasterState;
  readonly offer: OfferState;
  readonly option: OptionState;
  readonly record: RecordState;
  readonly salon: SalonState;
  readonly subscriber: SubscriberState;
  readonly timeTable: TimeTableState;
  readonly variant: VariantState;
  /* jhipster-needle-add-reducer-type - JHipster will add reducer type here */
  readonly loadingBar: any;
}

const rootReducer = combineReducers<IRootState>({
  authentication,
  locale,
  applicationProfile,
  administration,
  userManagement,
  register,
  activate,
  passwordReset,
  password,
  settings,
  category,
  image,
  master,
  offer,
  option,
  record,
  salon,
  subscriber,
  timeTable,
  variant,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
  loadingBar
});

export default rootReducer;
