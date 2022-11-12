import { ICustomers } from 'app/entities/customers/customers.model';

export interface IServices {
  id: number;
  name?: string | null;
  price?: number | null;
  period?: string | null;
  countPeriod?: number | null;
  users?: Pick<ICustomers, 'id'>[] | null;
}

export type NewServices = Omit<IServices, 'id'> & { id: null };
