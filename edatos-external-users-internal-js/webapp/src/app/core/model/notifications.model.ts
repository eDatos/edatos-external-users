export class Notification {
    public subject: string;
    public message: string;
    public receivers?: string[];
    public categoriesIds?: number[];
    public externalOperationsIds?: number[];
}
