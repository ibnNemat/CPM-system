import { ICustomers } from 'app/entities/customers/customers.model';
import { IOrganization } from 'app/entities/organization/organization.model';

export interface IGroups {
  id: number;
  name?: string | null;
  groupOwnerName?: string | null;
  parentId?: number | null;
  customers?: Pick<ICustomers, 'id'>[] | null;
  organization?: Pick<IOrganization, 'id'> | null;
}

export type NewGroups = Omit<IGroups, 'id'> & { id: null };
