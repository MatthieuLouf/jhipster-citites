import { Moment } from 'moment';
import { IUser } from 'app/core/user/user.model';

export interface ICity {
  id?: number;
  name?: string;
  inhabitants?: number;
  postal_code?: number;
  created_date?: Moment;
  user?: IUser;
}

export class City implements ICity {
  constructor(
    public id?: number,
    public name?: string,
    public inhabitants?: number,
    public postal_code?: number,
    public created_date?: Moment,
    public user?: IUser
  ) {}
}
