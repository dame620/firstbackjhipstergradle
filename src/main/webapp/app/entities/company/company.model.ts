export interface ICompany {
  id?: number;
  name?: string | null;
  ninea?: string | null;
  rc?: string | null;
  address?: string | null;
}

export class Company implements ICompany {
  constructor(
    public id?: number,
    public name?: string | null,
    public ninea?: string | null,
    public rc?: string | null,
    public address?: string | null
  ) {}
}

export function getCompanyIdentifier(company: ICompany): number | undefined {
  return company.id;
}
