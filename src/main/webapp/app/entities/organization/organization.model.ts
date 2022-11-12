import { ICustomers } from 'app/entities/customers/customers.model';

export interface IOrganization {
  id: number;
  name?: string | null;
  orgOwner?: Pick<ICustomers, 'id'> | null;
}

export type NewOrganization = Omit<IOrganization, 'id'> & { id: null };
