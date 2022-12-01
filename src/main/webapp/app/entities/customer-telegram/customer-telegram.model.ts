import { IUser } from 'app/entities/user/user.model';

export interface ICustomerTelegram {
  id: number;
  isBot?: boolean | null;
  firstname?: string | null;
  lastname?: string | null;
  username?: string | null;
  telegramId?: number | null;
  phoneNumber?: string | null;
  step?: number | null;
  canJoinGroups?: boolean | null;
  languageCode?: string | null;
  isActive?: boolean | null;
  user?: Pick<IUser, 'id'> | null;
}

export type NewCustomerTelegram = Omit<ICustomerTelegram, 'id'> & { id: null };
