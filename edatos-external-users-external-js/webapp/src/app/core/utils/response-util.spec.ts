import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { Category, Favorite, InternationalString } from '@app/shared';
import { convert, ResponseWrapper } from './response-utils';

describe('ResponseUtils', () => {
    let favoritePlainObject;
    let httpResponse: HttpResponse<Favorite>;
    let httpResponseList: HttpResponse<Favorite[]>;

    beforeEach(() => {
        favoritePlainObject = {
            id: 5,
            optLock: 1,
            createdDate: '2021-03-24T12:15:59Z',
            createdBy: 'asdasdasdasdsda',
            email: 'qwer@qwer.com',
            category: {
                id: 1,
                optLock: 1,
                createdDate: '2021-03-22T15:17:19Z',
                createdBy: 'system',
                code: 'sdfs',
                uri: 'sdf',
                urn: 'fsd',
                name: {
                    texts: [
                        {
                            label: 'ejemplo',
                            locale: 'es'
                        }
                    ]
                }
            }
        };

        httpResponse = new HttpResponse({
            body: favoritePlainObject,
            headers: new HttpHeaders({ 'Test-Header': 'OK' }),
            status: 200
        });

        httpResponseList = new HttpResponse({
            body: [favoritePlainObject, favoritePlainObject],
            headers: new HttpHeaders({ 'Test-Header': 'OK' }),
            status: 200
        });
    });

    it('should convert a plain json object into a class instance', () => {
        const instantiatedObject = convert(Favorite, favoritePlainObject);
        expect(instantiatedObject).toBeInstanceOf(Favorite);
        expect(instantiatedObject.category).toBeInstanceOf(Category);
        expect(instantiatedObject.category.name).toBeInstanceOf(InternationalString);
        expect(instantiatedObject.category.name.getLocalisedLabel).toBeInstanceOf(Function);
        expect(instantiatedObject.category.name.getLocalisedLabel('es')).toEqual('ejemplo');
    });

    it('should convert a http response json object into a response wrapper', () => {
        const instantiatedObject: ResponseWrapper<Favorite> = (convert(Favorite, httpResponse) as unknown) as ResponseWrapper<Favorite>;
        expect(instantiatedObject).toBeInstanceOf(ResponseWrapper);
        expect(instantiatedObject.body.category).toBeInstanceOf(Category);
        expect(instantiatedObject.body.category.name).toBeInstanceOf(InternationalString);
        expect(instantiatedObject.body.category.name.getLocalisedLabel).toBeInstanceOf(Function);
        expect(instantiatedObject.body.category.name.getLocalisedLabel('es')).toEqual('ejemplo');
        expect(instantiatedObject.headers.get('Test-Header')).toEqual('OK');
    });

    it('should convert a http response json list object into a list inside the response wrapper', () => {
        const instantiatedObject: ResponseWrapper<Favorite> = (convert(Favorite, httpResponseList) as unknown) as ResponseWrapper<Favorite>;
        expect(instantiatedObject).toBeInstanceOf(ResponseWrapper);
        expect(Array.isArray(instantiatedObject.body)).toBeTrue();
        expect(instantiatedObject.body[0].category).toBeInstanceOf(Category);
        expect(instantiatedObject.body[0].category.name).toBeInstanceOf(InternationalString);
        expect(instantiatedObject.body[0].category.name.getLocalisedLabel).toBeInstanceOf(Function);
        expect(instantiatedObject.body[0].category.name.getLocalisedLabel('es')).toEqual('ejemplo');
        expect(instantiatedObject.headers.get('Test-Header')).toEqual('OK');
    });
});
