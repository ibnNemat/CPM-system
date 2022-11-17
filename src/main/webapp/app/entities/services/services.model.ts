import { IGroups } from 'app/entities/groups/groups.model';
import { ICustomers } from 'app/entities/customers/customers.model';
import { ServiceType } from 'app/entities/enumerations/service-type.model';
import { PeriodType } from 'app/entities/enumerations/period-type.model';

export interface IServices {
  id: number;
  serviceType?: ServiceType | null;
  price?: number | null;
  periodType?: PeriodType | null;
  countPeriod?: number | null;
  group?: Pick<IGroups, 'id'> | null;
  users?: Pick<ICustomers, 'id'>[] | null;
}

export type NewServices = Omit<IServices, 'id'> & { id: null };
