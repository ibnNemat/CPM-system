import { IOrganization, NewOrganization } from './organization.model';

export const sampleWithRequiredData: IOrganization = {
  id: 12784,
  name: 'Automated Rial',
};

export const sampleWithPartialData: IOrganization = {
  id: 53520,
  name: 'salmon Home',
};

export const sampleWithFullData: IOrganization = {
  id: 57576,
  name: 'out-of-the-box',
};

export const sampleWithNewData: NewOrganization = {
  name: 'systems',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
