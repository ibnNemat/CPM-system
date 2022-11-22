import dayjs from 'dayjs/esm';

import { IPayment, NewPayment } from './payment.model';

export const sampleWithRequiredData: IPayment = {
  id: 47537,
  paidMoney: 72336,
  paymentForPeriod: 76971,
  isPayed: false,
  startedPeriod: dayjs('2022-11-22'),
};

export const sampleWithPartialData: IPayment = {
  id: 46452,
  paidMoney: 39655,
  paymentForPeriod: 35842,
  isPayed: false,
  startedPeriod: dayjs('2022-11-21'),
  finishedPeriod: dayjs('2022-11-21'),
};

export const sampleWithFullData: IPayment = {
  id: 44057,
  paidMoney: 89152,
  paymentForPeriod: 91729,
  isPayed: true,
  startedPeriod: dayjs('2022-11-21'),
  finishedPeriod: dayjs('2022-11-21'),
};

export const sampleWithNewData: NewPayment = {
  paidMoney: 12909,
  paymentForPeriod: 22359,
  isPayed: false,
  startedPeriod: dayjs('2022-11-22'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
