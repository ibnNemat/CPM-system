import dayjs from 'dayjs/esm';
import { ICustomers } from 'app/entities/customers/customers.model';
import { IServices } from 'app/entities/services/services.model';
import { IGroups } from 'app/entities/groups/groups.model';

export interface IPayment {
  id: number;
  payedMoney?: number | null;
  paymentForPeriod?: number | null;
  isPayed?: boolean | null;
  startedPeriod?: dayjs.Dayjs | null;
  finishedPeriod?: dayjs.Dayjs | null;
  customer?: Pick<ICustomers, 'id'> | null;
  service?: Pick<IServices, 'id'> | null;
  group?: Pick<IGroups, 'id'> | null;
}

export type NewPayment = Omit<IPayment, 'id'> & { id: null };
