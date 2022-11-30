import { ITelegramEntity, NewTelegramEntity } from './telegram-entity.model';

export const sampleWithRequiredData: ITelegramEntity = {
  id: 49138,
};

export const sampleWithPartialData: ITelegramEntity = {
  id: 64540,
  firstname: 'copying',
  username: 'Delaware',
  canJoinGroups: false,
};

export const sampleWithFullData: ITelegramEntity = {
  id: 9253,
  isBot: false,
  firstname: 'GB Granite 24/365',
  lastname: 'International Lead',
  username: 'payment',
  telegramId: 10563,
  canJoinGroups: false,
  languageCode: 'calculating',
  isActive: false,
};

export const sampleWithNewData: NewTelegramEntity = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
