import { IImage } from 'app/shared/model/image.model';
import { ITimeTable } from 'app/shared/model/time-table.model';
import { IOffer } from 'app/shared/model/offer.model';
import { IMaster } from 'app/shared/model/master.model';
import { ICategory } from 'app/shared/model/category.model';
import { IUser } from 'app/shared/model/user.model';

export const enum SalonStatusEnum {
  EXAMPLE = 'EXAMPLE',
  DRAFT = ' DRAFT',
  CREATED = 'CREATED',
  ACTIVATED = 'ACTIVATED',
  DEACTIVATED = 'DEACTIVATED',
  DELETED = 'DELETED',
  BANNED = 'BANNED'
}

export const enum SalonTypeEnum {
  STANDARD = 'STANDARD',
  PART_TIME_CUSTOM = 'PART_TIME_CUSTOM',
  FULL_TIME_CUSTOM = 'FULL_TIME_CUSTOM',
  SECOND_WORK_CUSTOM = 'SECOND_WORK_CUSTOM'
}

export interface ISalon {
  id?: number;
  name?: string;
  slogan?: string;
  location?: string;
  status?: SalonStatusEnum;
  systemComment?: string;
  type?: SalonTypeEnum;
  image?: IImage;
  timeTable?: ITimeTable;
  offers?: IOffer[];
  masters?: IMaster[];
  categories?: ICategory[];
  owner?: IUser;
}

export const defaultValue: Readonly<ISalon> = {};
