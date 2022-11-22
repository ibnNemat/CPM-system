import { IUser } from 'app/entities/user/user.model';
import { IGroups } from 'app/entities/groups/groups.model';

export interface ICustomers {
  id: number;
  username?: string | null;
  password?: string | null;
  phoneNumber?: string | null;
  account?: number | null;
  user?: Pick<IUser, 'id'> | null;
  groups?: Pick<IGroups, 'id'>[] | null;
}

export type NewCustomers = Omit<ICustomers, 'id'> & { id: null };
