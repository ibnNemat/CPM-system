import { IRole, NewRole } from './role.model';

export const sampleWithRequiredData: IRole = {
  id: 56879,
  name: 'Knolls Loan portals',
};

export const sampleWithPartialData: IRole = {
  id: 73835,
  name: 'Fords impactful Mouse',
};

export const sampleWithFullData: IRole = {
  id: 4555,
  name: 'multi-byte indexing methodical',
};

export const sampleWithNewData: NewRole = {
  name: 'Chicken',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
