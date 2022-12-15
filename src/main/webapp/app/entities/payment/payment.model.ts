import dayjs from 'dayjs/esm';
import { ICustomers } from 'app/entities/customers/customers.model';
import { IServices } from 'app/entities/services/services.model';

export interface IPayment {
  id: number;
  paidMoney?: number | null;
  paymentForPeriod?: number | null;
  isPaid?: boolean | null;
  startedPeriod?: dayjs.Dayjs | null;
  finishedPeriod?: dayjs.Dayjs | null;
  customer?: Pick<ICustomers, 'id'> | null;
  service?: Pick<IServices, 'id'> | null;
}

export type NewPayment = Omit<IPayment, 'id'> & { id: null };
