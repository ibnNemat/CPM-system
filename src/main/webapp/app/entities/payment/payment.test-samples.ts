import dayjs from 'dayjs/esm';

import { IPayment, NewPayment } from './payment.model';

export const sampleWithRequiredData: IPayment = {
  id: 47537,
  payedMoney: 72336,
  paymentForPeriod: 74412,
  isPayed: false,
  startedPeriod: dayjs('2022-11-11'),
  finishedPeriod: dayjs('2022-11-11'),
};

export const sampleWithPartialData: IPayment = {
  id: 46452,
  payedMoney: 39655,
  paymentForPeriod: 28713,
  isPayed: false,
  startedPeriod: dayjs('2022-11-11'),
  finishedPeriod: dayjs('2022-11-11'),
};

export const sampleWithFullData: IPayment = {
  id: 44057,
  payedMoney: 89152,
  paymentForPeriod: 90810,
  isPayed: true,
  startedPeriod: dayjs('2022-11-11'),
  finishedPeriod: dayjs('2022-11-11'),
};

export const sampleWithNewData: NewPayment = {
  payedMoney: 12909,
  paymentForPeriod: 13733,
  isPayed: false,
  startedPeriod: dayjs('2022-11-12'),
  finishedPeriod: dayjs('2022-11-11'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
