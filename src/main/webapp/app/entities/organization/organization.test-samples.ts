import { IOrganization, NewOrganization } from './organization.model';

export const sampleWithRequiredData: IOrganization = {
  id: 12784,
  name: 'Automated Rial',
  orgOwnerName: 'Hawaii TCP',
};

export const sampleWithPartialData: IOrganization = {
  id: 85104,
  name: 'Fantastic systems',
  orgOwnerName: 'withdrawal invoice invoice',
};

export const sampleWithFullData: IOrganization = {
  id: 96369,
  name: 'payment circuit',
  orgOwnerName: 'Enhanced payment Electronics',
};

export const sampleWithNewData: NewOrganization = {
  name: 'global',
  orgOwnerName: 'auxiliary Program',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
