import dayjs from 'dayjs/esm';
import { ICustomers } from 'app/entities/customers/customers.model';
import { IServices } from 'app/entities/services/services.model';

export interface IPayment {
  id: number;
  paymentForPeriod?: number | null;
  isPayed?: boolean | null;
  createdAt?: dayjs.Dayjs | null;
  user?: Pick<ICustomers, 'id'> | null;
  service?: Pick<IServices, 'id'> | null;
}

export type NewPayment = Omit<IPayment, 'id'> & { id: null };
