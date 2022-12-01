import { ICustomerTelegram, NewCustomerTelegram } from './customer-telegram.model';

export const sampleWithRequiredData: ICustomerTelegram = {
  id: 42682,
};

export const sampleWithPartialData: ICustomerTelegram = {
  id: 96868,
  isBot: false,
  lastname: 'Maine alarm',
  username: 'paradigms Texas JBOD',
  telegramId: 5036,
  phoneNumber: 'Vermont Shoes',
  step: 24893,
  languageCode: 'mobile protocol',
};

export const sampleWithFullData: ICustomerTelegram = {
  id: 66877,
  isBot: true,
  firstname: 'Salad',
  lastname: 'cross-platform user',
  username: 'Cyprus JSON',
  telegramId: 83556,
  phoneNumber: 'strategize Persevering encryption',
  step: 46760,
  canJoinGroups: true,
  languageCode: 'hack transmit',
  isActive: false,
};

export const sampleWithNewData: NewCustomerTelegram = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
