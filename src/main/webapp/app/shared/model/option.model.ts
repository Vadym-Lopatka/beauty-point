import { IOffer } from 'app/shared/model/offer.model';

export interface IOption {
  id?: number;
  name?: string;
  price?: number;
  sessionTime?: number;
  active?: boolean;
  offer?: IOffer;
}

export const defaultValue: Readonly<IOption> = {
  active: false
};
