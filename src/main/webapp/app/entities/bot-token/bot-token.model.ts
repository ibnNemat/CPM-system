import { IUser } from 'app/entities/user/user.model';

export interface IBotToken {
  id: number;
  username?: string | null;
  telegramId?: number | null;
  token?: string | null;
  createdBy?: Pick<IUser, 'id'> | null;
}

export type NewBotToken = Omit<IBotToken, 'id'> & { id: null };
