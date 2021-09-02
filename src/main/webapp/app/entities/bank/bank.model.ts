export interface IBank {
  id?: number;
  name?: string | null;
  address?: string | null;
}

export class Bank implements IBank {
  constructor(public id?: number, public name?: string | null, public address?: string | null) {}
}

export function getBankIdentifier(bank: IBank): number | undefined {
  return bank.id;
}
