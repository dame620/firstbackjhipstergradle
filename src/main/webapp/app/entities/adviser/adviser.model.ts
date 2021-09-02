import { IUser } from 'app/entities/user/user.model';
import { IBank } from 'app/entities/bank/bank.model';

export interface IAdviser {
  id?: number;
  registrationNumber?: string | null;
  company?: string | null;
  department?: string | null;
  user?: IUser | null;
  bank?: IBank | null;
}

export class Adviser implements IAdviser {
  constructor(
    public id?: number,
    public registrationNumber?: string | null,
    public company?: string | null,
    public department?: string | null,
    public user?: IUser | null,
    public bank?: IBank | null
  ) {}
}

export function getAdviserIdentifier(adviser: IAdviser): number | undefined {
  return adviser.id;
}
