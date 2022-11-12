import dayjs from 'dayjs/esm';

import { IPayment, NewPayment } from './payment.model';

export const sampleWithRequiredData: IPayment = {
  id: 47537,
  paymentForPeriod: 72336,
  isPayed: true,
  createdAt: dayjs('2022-11-11'),
};

export const sampleWithPartialData: IPayment = {
  id: 34993,
  paymentForPeriod: 88670,
  isPayed: false,
  createdAt: dayjs('2022-11-11'),
};

export const sampleWithFullData: IPayment = {
  id: 28713,
  paymentForPeriod: 32065,
  isPayed: true,
  createdAt: dayjs('2022-11-11'),
};

export const sampleWithNewData: NewPayment = {
  paymentForPeriod: 44057,
  isPayed: true,
  createdAt: dayjs('2022-11-11'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
