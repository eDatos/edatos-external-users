import { Language } from '@app/core/model/language.model';
import { BaseAuditingEntity } from 'arte-ng/model';
import * as jwtDecode from 'jwt-decode';
import { Role } from './rol.model';

export class User extends BaseAuditingEntity {
    constructor(
        public id?: any,
        public login?: string,
        public nombre?: string,
        public apellido1?: string,
        public apellido2?: string,
        public email?: string,
        public language?: Language,
        public organization?: string,
        public phoneNumber?: string,
        public deletionDate?: Date,
        public deletedBy?: string,
        public roles?: any[]
    ) {
        super();
    }
}

export interface RolCAS {
    app: string;
    role: string;
}

export class UserCAS {
    private readonly ACL_APP_NAME: string = 'GESTOR_USUARIOS_EXTERNOS';
    constructor(public login: string, public roles: RolCAS[]) {}

    public static fromJwt(token: string) {
        const payload: { sub: string; auth: string; exp: string } = jwtDecode(token);
        const rolesCas = payload.auth.split(',').map((appRole) => {
            const [app, role] = appRole.split('#', 2);
            return { app, role } as RolCAS;
        });
        return new UserCAS(payload.sub, rolesCas);
    }

    public hasRole(rol: Role): boolean {
        return this.roles.some((userRol) => userRol.app == this.ACL_APP_NAME && userRol.role == rol);
    }
}
