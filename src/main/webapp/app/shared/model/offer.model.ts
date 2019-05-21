import { ISalon } from 'app/shared/model/salon.model';
import { IImage } from 'app/shared/model/image.model';
import { IVariant } from 'app/shared/model/variant.model';
import { IOption } from 'app/shared/model/option.model';
import { ICategory } from 'app/shared/model/category.model';

export const enum OfferStatusEnum {
  NORMAL = 'NORMAL',
  DELETED = ' DELETED',
  BANNED = ' BANNED'
}

export interface IOffer {
  id?: number;
  name?: string;
  description?: string;
  priceLow?: number;
  priceHigh?: number;
  active?: boolean;
  status?: OfferStatusEnum;
  salon?: ISalon;
  image?: IImage;
  variants?: IVariant[];
  options?: IOption[];
  category?: ICategory;
}

export const defaultValue: Readonly<IOffer> = {
  active: false
};
