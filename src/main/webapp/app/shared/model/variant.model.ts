import { IOffer } from 'app/shared/model/offer.model';
import { IMaster } from 'app/shared/model/master.model';

export interface IVariant {
  id?: number;
  name?: string;
  price?: number;
  sessionTime?: number;
  active?: boolean;
  offer?: IOffer;
  executors?: IMaster[];
}

export const defaultValue: Readonly<IVariant> = {
  active: false
};
