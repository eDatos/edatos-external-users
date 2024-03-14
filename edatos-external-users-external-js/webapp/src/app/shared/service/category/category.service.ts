import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { convert } from '@app/core/utils/response-utils';

import { Category } from '@app/shared/model';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({
    providedIn: 'root',
})
export class CategoryService {
    public readonly resourceUrl = 'api/categories';

    constructor(private http: HttpClient) {}

    public get(id: number): Observable<Category> {
        return this.http.get<Category>(`${this.resourceUrl}/${id}`).pipe(map((category) => convert(Category, category)));
    }

    public findAll(): Observable<Category[]> {
        return this.http.get<Category[]>(`${this.resourceUrl}`).pipe(map((category) => convert(Category, category)));
    }

    public getTree(): Observable<Category[]> {
        return this.http
            .get<Category[]>(`${this.resourceUrl}/tree`, { params: { sort: 'id' } })
            .pipe(map((tree) => convert(Category, tree)));
    }

    public updateTree(tree: Category[]): Observable<Category[]> {
        return this.http.put<Category[]>(`${this.resourceUrl}/tree`, tree).pipe(map((categories) => convert(Category, categories)));
    }
}
