import { IRole } from 'app/entities/role/role.model';
import { IGroups } from 'app/entities/groups/groups.model';
import { IServices } from 'app/entities/services/services.model';

export interface ICustomers {
  id: number;
  fullName?: string | null;
  username?: string | null;
  password?: string | null;
  phoneNumber?: string | null;
  email?: string | null;
  account?: number | null;
  role?: Pick<IRole, 'id'> | null;
  groups?: Pick<IGroups, 'id'>[] | null;
  services?: Pick<IServices, 'id'>[] | null;
}

export type NewCustomers = Omit<ICustomers, 'id'> & { id: null };
