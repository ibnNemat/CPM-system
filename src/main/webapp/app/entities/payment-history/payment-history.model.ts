import dayjs from 'dayjs/esm';

export interface IPaymentHistory {
  id: number;
  organizationName?: string | null;
  serviceName?: string | null;
  groupName?: string | null;
  sum?: number | null;
  createdAt?: dayjs.Dayjs | null;
}

export type NewPaymentHistory = Omit<IPaymentHistory, 'id'> & { id: null };
