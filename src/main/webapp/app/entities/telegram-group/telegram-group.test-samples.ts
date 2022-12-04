import { ITelegramGroup, NewTelegramGroup } from './telegram-group.model';

export const sampleWithRequiredData: ITelegramGroup = {
  id: 25789,
};

export const sampleWithPartialData: ITelegramGroup = {
  id: 93633,
  chatId: 10101,
};

export const sampleWithFullData: ITelegramGroup = {
  id: 69783,
  name: 'Specialist Dynamic archive',
  chatId: 66799,
};

export const sampleWithNewData: NewTelegramGroup = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
