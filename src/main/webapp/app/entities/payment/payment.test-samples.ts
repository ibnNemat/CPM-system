import dayjs from 'dayjs/esm';

import { IPayment, NewPayment } from './payment.model';

export const sampleWithRequiredData: IPayment = {
  id: 47537,
  paymentForPeriod: 72336,
  totalPrice: 74412,
  isPayed: false,
  createdAt: dayjs('2022-11-11'),
};

export const sampleWithPartialData: IPayment = {
  id: 88670,
  paymentForPeriod: 46452,
  totalPrice: 39655,
  isPayed: false,
  createdAt: dayjs('2022-11-12'),
};

export const sampleWithFullData: IPayment = {
  id: 53029,
  paymentForPeriod: 63797,
  totalPrice: 44057,
  isPayed: true,
  createdAt: dayjs('2022-11-11'),
};

export const sampleWithNewData: NewPayment = {
  paymentForPeriod: 60161,
  totalPrice: 49401,
  isPayed: true,
  createdAt: dayjs('2022-11-12'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
