export interface IOrganization {
  id: number;
  name?: string | null;
}

export type NewOrganization = Omit<IOrganization, 'id'> & { id: null };
