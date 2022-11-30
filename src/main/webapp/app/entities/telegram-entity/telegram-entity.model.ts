import { IUser } from 'app/entities/user/user.model';

export interface ITelegramEntity {
  id: number;
  isBot?: boolean | null;
  firstname?: string | null;
  lastname?: string | null;
  username?: string | null;
  telegramId?: number | null;
  canJoinGroups?: boolean | null;
  languageCode?: string | null;
  isActive?: boolean | null;
  user?: Pick<IUser, 'id'> | null;
}

export type NewTelegramEntity = Omit<ITelegramEntity, 'id'> & { id: null };
