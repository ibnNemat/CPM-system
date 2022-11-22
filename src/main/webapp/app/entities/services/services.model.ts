import dayjs from 'dayjs/esm';
import { IGroups } from 'app/entities/groups/groups.model';
import { PeriodType } from 'app/entities/enumerations/period-type.model';

export interface IServices {
  id: number;
  name?: string | null;
  price?: number | null;
  startedPeriod?: dayjs.Dayjs | null;
  periodType?: PeriodType | null;
  countPeriod?: number | null;
  groups?: Pick<IGroups, 'id'>[] | null;
}

export type NewServices = Omit<IServices, 'id'> & { id: null };
