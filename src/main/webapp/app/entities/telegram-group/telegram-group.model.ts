import { ICustomerTelegram } from 'app/entities/customer-telegram/customer-telegram.model';

export interface ITelegramGroup {
  id: number;
  name?: string | null;
  chatId?: number | null;
  customerTelegrams?: Pick<ICustomerTelegram, 'id'>[] | null;
}

export type NewTelegramGroup = Omit<ITelegramGroup, 'id'> & { id: null };
