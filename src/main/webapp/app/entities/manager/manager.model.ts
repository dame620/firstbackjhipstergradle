import { IUser } from 'app/entities/user/user.model';
import { ICompany } from 'app/entities/company/company.model';

export interface IManager {
  id?: number;
  registrationNumber?: string | null;
  department?: string | null;
  user?: IUser | null;
  company?: ICompany | null;
}

export class Manager implements IManager {
  constructor(
    public id?: number,
    public registrationNumber?: string | null,
    public department?: string | null,
    public user?: IUser | null,
    public company?: ICompany | null
  ) {}
}

export function getManagerIdentifier(manager: IManager): number | undefined {
  return manager.id;
}
