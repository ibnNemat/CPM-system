import { IOrganization, NewOrganization } from './organization.model';

export const sampleWithRequiredData: IOrganization = {
  id: 12784,
  name: 'Automated Rial',
};

export const sampleWithPartialData: IOrganization = {
  id: 57434,
  name: 'Utah',
  orgOwnerName: 'generating',
};

export const sampleWithFullData: IOrganization = {
  id: 35747,
  name: 'Market invoice withdrawal',
  orgOwnerName: 'quantifying digital',
};

export const sampleWithNewData: NewOrganization = {
  name: 'payment circuit',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
