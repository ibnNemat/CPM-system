import { IGroups, NewGroups } from './groups.model';

export const sampleWithRequiredData: IGroups = {
  id: 3292,
  name: 'modular Fish wireless',
  groupOwnerName: 'Bolivar navigate matrix',
};

export const sampleWithPartialData: IGroups = {
  id: 1818,
  name: 'Movies',
  groupOwnerName: 'navigating Supervisor',
};

export const sampleWithFullData: IGroups = {
  id: 62164,
  name: 'deposit Communications',
  groupOwnerName: 'Multi-layered',
};

export const sampleWithNewData: NewGroups = {
  name: 'Communications Direct withdrawal',
  groupOwnerName: 'plum AI generate',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
