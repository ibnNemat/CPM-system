import { IGroups, NewGroups } from './groups.model';

export const sampleWithRequiredData: IGroups = {
  id: 3292,
  name: 'modular Fish wireless',
};

export const sampleWithPartialData: IGroups = {
  id: 86991,
  name: 'SDD clicks-and-mortar Orchestrator',
};

export const sampleWithFullData: IGroups = {
  id: 12945,
  name: 'Cotton',
};

export const sampleWithNewData: NewGroups = {
  name: 'Supervisor Card',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
