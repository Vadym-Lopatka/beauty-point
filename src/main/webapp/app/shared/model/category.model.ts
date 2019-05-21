import { ICategory } from 'app/shared/model/category.model';
import { ISalon } from 'app/shared/model/salon.model';
import { IMaster } from 'app/shared/model/master.model';

export interface ICategory {
  id?: number;
  name?: string;
  main?: boolean;
  parent?: ICategory;
  salons?: ISalon[];
  masters?: IMaster[];
}

export const defaultValue: Readonly<ICategory> = {
  main: false
};
