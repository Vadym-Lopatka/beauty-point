export interface ITimeTable {
  id?: number;
  everyDayEqual?: boolean;
  mo?: number;
  tu?: number;
  we?: number;
  th?: number;
  fr?: number;
  sa?: number;
  su?: number;
}

export const defaultValue: Readonly<ITimeTable> = {
  everyDayEqual: false
};
