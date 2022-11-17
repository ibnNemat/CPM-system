import { IOrganization } from 'app/entities/organization/organization.model';
import { ICustomers } from 'app/entities/customers/customers.model';

export interface IGroups {
  id: number;
  name?: string | null;
  groupOwnerName?: string | null;
  organization?: Pick<IOrganization, 'id'> | null;
  users?: Pick<ICustomers, 'id'>[] | null;
}

export type NewGroups = Omit<IGroups, 'id'> & { id: null };
