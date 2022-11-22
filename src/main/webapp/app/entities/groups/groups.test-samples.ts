import { IGroups, NewGroups } from './groups.model';

export const sampleWithRequiredData: IGroups = {
  id: 3292,
  name: 'modular Fish wireless',
};

export const sampleWithPartialData: IGroups = {
  id: 85345,
  name: 'navigate',
  groupOwnerName: 'Orchestrator Steel index',
  parentId: 40751,
};

export const sampleWithFullData: IGroups = {
  id: 51968,
  name: 'Home payment web-enabled',
  groupOwnerName: 'Gorgeous Quality Account',
  parentId: 12710,
};

export const sampleWithNewData: NewGroups = {
  name: 'withdrawal Dynamic invoice',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
