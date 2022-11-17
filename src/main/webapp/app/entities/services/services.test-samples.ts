import { ServiceType } from 'app/entities/enumerations/service-type.model';
import { PeriodType } from 'app/entities/enumerations/period-type.model';

import { IServices, NewServices } from './services.model';

export const sampleWithRequiredData: IServices = {
  id: 10413,
  serviceType: ServiceType['MEDICAL'],
  price: 50885,
  periodType: PeriodType['MONTH'],
  countPeriod: 95265,
};

export const sampleWithPartialData: IServices = {
  id: 18772,
  serviceType: ServiceType['FOOD'],
  price: 13790,
  periodType: PeriodType['YEAR'],
  countPeriod: 47844,
};

export const sampleWithFullData: IServices = {
  id: 43040,
  serviceType: ServiceType['ACCOMMODATION'],
  price: 25657,
  periodType: PeriodType['ONETIME'],
  countPeriod: 24136,
};

export const sampleWithNewData: NewServices = {
  serviceType: ServiceType['ACCOMMODATION'],
  price: 28337,
  periodType: PeriodType['MONTH'],
  countPeriod: 30159,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
