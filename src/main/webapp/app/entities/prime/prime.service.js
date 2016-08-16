(function() {
    'use strict';
    angular
        .module('supergooalrosApp')
        .factory('Prime', Prime);

    Prime.$inject = ['$resource', 'DateUtils'];

    function Prime ($resource, DateUtils) {
        var resourceUrl =  'api/primes/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.dateFixation = DateUtils.convertLocalDateFromServer(data.dateFixation);
                    }
                    return data;
                }
            },
            'update': {
                method: 'PUT',
                transformRequest: function (data) {
                    data.dateFixation = DateUtils.convertLocalDateToServer(data.dateFixation);
                    return angular.toJson(data);
                }
            },
            'save': {
                method: 'POST',
                transformRequest: function (data) {
                    data.dateFixation = DateUtils.convertLocalDateToServer(data.dateFixation);
                    return angular.toJson(data);
                }
            }
        });
    }
})();
