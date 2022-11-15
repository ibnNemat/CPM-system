import { IServices } from 'app/entities/services/services.model';
import { IOrganization } from 'app/entities/organization/organization.model';
import { ICustomers } from 'app/entities/customers/customers.model';

export interface IGroups {
  id: number;
  groupManagerId?: number | null;
  name?: string | null;
  groupOwnerName?: string | null;
  services?: Pick<IServices, 'id'>[] | null;
  organization?: Pick<IOrganization, 'id'> | null;
  users?: Pick<ICustomers, 'id'>[] | null;
}

export type NewGroups = Omit<IGroups, 'id'> & { id: null };
