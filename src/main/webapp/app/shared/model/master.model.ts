import { ISalon } from 'app/shared/model/salon.model';
import { IRecord } from 'app/shared/model/record.model';
import { ICategory } from 'app/shared/model/category.model';
import { IUser } from 'app/shared/model/user.model';

export interface IMaster {
  id?: number;
  salon?: ISalon;
  records?: IRecord[];
  categories?: ICategory[];
  user?: IUser;
}

export const defaultValue: Readonly<IMaster> = {};
