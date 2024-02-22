import { InternationalString } from './international-string.model';
import { Type } from 'class-transformer';

export class Notification {
    @Type(() => InternationalString)
    public subject: InternationalString = new InternationalString();

    @Type(() => InternationalString)
    public message: InternationalString = new InternationalString();

    public receivers?: string[];
    public categoriesIds?: number[];
    public externalOperationsIds?: number[];
}
