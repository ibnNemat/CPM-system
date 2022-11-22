import { IGroups, NewGroups } from './groups.model';

export const sampleWithRequiredData: IGroups = {
  id: 3292,
  name: 'modular Fish wireless',
};

export const sampleWithPartialData: IGroups = {
  id: 67596,
  name: 'Handmade Shoes Checking',
  groupOwnerName: 'Cotton',
};

export const sampleWithFullData: IGroups = {
  id: 51968,
  name: 'Home payment web-enabled',
  groupOwnerName: 'Gorgeous Quality Account',
};

export const sampleWithNewData: NewGroups = {
  name: 'alarm',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
