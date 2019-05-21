import { Moment } from 'moment';
import { IMaster } from 'app/shared/model/master.model';
import { IVariant } from 'app/shared/model/variant.model';
import { IOption } from 'app/shared/model/option.model';
import { IUser } from 'app/shared/model/user.model';
import { ISalon } from 'app/shared/model/salon.model';

export const enum OrderStatusEnum {
  CREATED_BY_SALON = 'CREATED_BY_SALON',
  CREATED_BY_CLIENT = ' CREATED_BY_CLIENT',
  INFORMED = ' INFORMED',
  PENDING = 'PENDING',
  IN_PROGRESS = 'IN_PROGRESS',
  DONE = 'DONE',
  CANCELED_BY_CLIENT = 'CANCELED_BY_CLIENT',
  CANCELED_BY_SALON = 'CANCELED_BY_SALON',
  PROBLEM = 'PROBLEM'
}

export interface IRecord {
  id?: number;
  bookingTime?: Moment;
  duration?: number;
  totalPrice?: number;
  orderStatus?: OrderStatusEnum;
  comment?: string;
  master?: IMaster;
  variant?: IVariant;
  options?: IOption[];
  user?: IUser;
  salon?: ISalon;
}

export const defaultValue: Readonly<IRecord> = {};
