export interface IRole {
  id: number;
  name?: string | null;
}

export type NewRole = Omit<IRole, 'id'> & { id: null };
