import dayjs from 'dayjs/esm';

import { PeriodType } from 'app/entities/enumerations/period-type.model';

import { IServices, NewServices } from './services.model';

export const sampleWithRequiredData: IServices = {
  id: 10413,
  name: 'Buckinghamshire maroon indexing',
  price: 73284,
  startedPeriod: dayjs('2022-11-12'),
  periodType: PeriodType['ONETIME'],
  countPeriod: 24137,
};

export const sampleWithPartialData: IServices = {
  id: 63479,
  name: 'Market',
  price: 74814,
  startedPeriod: dayjs('2022-11-11'),
  periodType: PeriodType['YEAR'],
  countPeriod: 88632,
};

export const sampleWithFullData: IServices = {
  id: 53209,
  name: 'Handmade relationships Handmade',
  price: 85968,
  startedPeriod: dayjs('2022-11-11'),
  periodType: PeriodType['YEAR'],
  countPeriod: 15695,
};

export const sampleWithNewData: NewServices = {
  name: 'program bypass',
  price: 37181,
  startedPeriod: dayjs('2022-11-12'),
  periodType: PeriodType['ONETIME'],
  countPeriod: 99479,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
