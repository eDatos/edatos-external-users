import { BaseVersionedAndAuditingWithDeletionEntity } from 'arte-ng/model';
import { Role } from './rol.model';
import * as jwtDecode from 'jwt-decode';

export class User extends BaseVersionedAndAuditingWithDeletionEntity {
    public id?: any;
    public name?: string;
    public surname1?: string;
    public surname2?: string;
    public password?: string;
    public email?: string;
    public organism?: string;
    public language?: any[];
    public phoneNumber?: string;
    public emailNotificationsEnabled?: boolean;
    public roles?: any[];
    public accept_lopd?: boolean;

    constructor(
        id?: any,
        name?: string,
        surname1?: string,
        surname2?: string,
        email?: string,
        organism?: string,
        password?: string,

        language?: any[],
        phoneNumber?: string,
        emailNotificationsEnabled?: boolean,
        roles?: any[],
        accept_lopd?: boolean
    ) {
        super();
        this.id = id ? id : null;
        this.name = name ? name : null;
        this.surname1 = surname1 ? surname1 : null;
        this.surname2 = surname2 ? surname2 : null;
        this.email = email ? email : null;
        this.organism = organism ? organism : null;
        this.password = password ? password : null;
        this.phoneNumber = phoneNumber ? phoneNumber : null;
        this.roles = roles ? roles : null;
        this.language = language ? language : null;
        this.emailNotificationsEnabled = emailNotificationsEnabled ? emailNotificationsEnabled : null;
        this.accept_lopd = accept_lopd ? accept_lopd : false;
    }

    public getFullName(): string {
        return `${this.name} ${this.surname1} ${this.surname2}`;
    }

    public hasRole(rol: Role): boolean {
        return this.roles.some((userRol) => userRol.app == Role.USER && userRol.role == rol);
    }
}

export interface RoleUserAccount {
    app: string;
    role: string;
}
export class UserAccount {
    constructor(public login: string, public roles: RoleUserAccount[]) {}

    public static fromJwt(token: string) {
        const payload: { sub: string; auth: string; exp: string } = jwtDecode(token);
        const roles = payload.auth.split(',').map((appRole) => {
            const [app, role] = appRole.split('#', 2);
            return { app, role } as RoleUserAccount;
        });
        return new UserAccount(payload.sub, roles);
    }

    public hasRole(rol: Role): boolean {
        return this.roles.some((userRol) => userRol.role == rol);
    }
}

export class UserAccountChangePassword {
    constructor(public currentPassword: string, public newPassword: string) {}
}
