import { IServices, NewServices } from './services.model';

export const sampleWithRequiredData: IServices = {
  id: 10413,
  name: 'Buckinghamshire maroon indexing',
  price: 70316,
  period: 'Computers',
  countPeriod: 28337,
};

export const sampleWithPartialData: IServices = {
  id: 60542,
  name: 'Rico',
  price: 84393,
  period: 'Tunisia panel Avon',
  countPeriod: 72759,
};

export const sampleWithFullData: IServices = {
  id: 31841,
  name: 'Internal Account Consultant',
  price: 49208,
  period: 'Games',
  countPeriod: 80324,
};

export const sampleWithNewData: NewServices = {
  name: 'Egyptian RAM',
  price: 50252,
  period: 'revolutionary Handcrafted Borders',
  countPeriod: 9217,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
