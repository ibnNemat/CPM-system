import dayjs from 'dayjs/esm';
import { ICustomers } from 'app/entities/customers/customers.model';

export interface IPaymentHistory {
  id: number;
  organizationName?: string | null;
  groupName?: string | null;
  serviceName?: string | null;
  sum?: number | null;
  createdAt?: dayjs.Dayjs | null;
  customer?: Pick<ICustomers, 'id'> | null;
}

export type NewPaymentHistory = Omit<IPaymentHistory, 'id'> & { id: null };
