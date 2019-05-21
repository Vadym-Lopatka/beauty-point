import { IUser } from 'app/shared/model/user.model';

export const enum ImageTypeEnum {
  PROFILE = 'PROFILE',
  SALON = 'SALON',
  OFFER = 'OFFER',
  VARIANT = 'VARIANT',
  CATEGORY = 'CATEGORY',
  SYSTEM = 'SYSTEM'
}

export interface IImage {
  id?: number;
  source?: string;
  imageType?: ImageTypeEnum;
  owner?: IUser;
}

export const defaultValue: Readonly<IImage> = {};
