import { IGroups, NewGroups } from './groups.model';

export const sampleWithRequiredData: IGroups = {
  id: 3292,
  groupManagerId: 73459,
  name: 'Credit',
  groupOwnerName: 'Senegal program SDD',
};

export const sampleWithPartialData: IGroups = {
  id: 34064,
  groupManagerId: 1490,
  name: 'Orchestrator Steel index',
  groupOwnerName: 'Western middleware',
};

export const sampleWithFullData: IGroups = {
  id: 64663,
  groupManagerId: 14890,
  name: 'Communications',
  groupOwnerName: 'Multi-layered',
};

export const sampleWithNewData: NewGroups = {
  groupManagerId: 69018,
  name: 'Account Metal Cambridgeshire',
  groupOwnerName: 'Dakota',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
