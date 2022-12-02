import { IBotToken, NewBotToken } from './bot-token.model';

export const sampleWithRequiredData: IBotToken = {
  id: 52331,
  username: 'Table Granite Music',
  telegramId: 37133,
  token: 'transmit invoice',
};

export const sampleWithPartialData: IBotToken = {
  id: 95991,
  username: 'HDD',
  telegramId: 34081,
  token: 'Missouri productivity one-to-one',
};

export const sampleWithFullData: IBotToken = {
  id: 48400,
  username: 'Wooden Agent',
  telegramId: 49995,
  token: 'grey microchip',
};

export const sampleWithNewData: NewBotToken = {
  username: 'Concrete white overriding',
  telegramId: 95479,
  token: 'Liaison',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
