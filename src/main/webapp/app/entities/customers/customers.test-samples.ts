import { ICustomers, NewCustomers } from './customers.model';

export const sampleWithRequiredData: ICustomers = {
  id: 77988,
  fullName: 'Money',
  username: 'Chair transmit HDD',
  password: 'lime',
  phoneNumber: 'Sum Table',
  email: 'Wilma.Jakubowski79@hotmail.com',
  account: 12922,
};

export const sampleWithPartialData: ICustomers = {
  id: 58117,
  fullName: 'application Quality-focused sensor',
  username: 'grey',
  password: 'Bedfordshire Table',
  phoneNumber: 'reboot content-based',
  email: 'Marina77@yahoo.com',
  account: 66419,
};

export const sampleWithFullData: ICustomers = {
  id: 56934,
  fullName: 'visualize',
  username: 'integrated',
  password: 'Accounts Re-contextualized',
  phoneNumber: 'up',
  email: 'Daryl_Herzog7@gmail.com',
  account: 53558,
};

export const sampleWithNewData: NewCustomers = {
  fullName: 'Facilitator',
  username: 'Orchestrator Chair Fantastic',
  password: 'bi-directional',
  phoneNumber: 'SAS Cheese',
  email: 'Destin68@hotmail.com',
  account: 22897,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
