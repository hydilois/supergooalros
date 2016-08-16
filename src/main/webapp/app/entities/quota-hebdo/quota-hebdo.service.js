(function() {
    'use strict';
    angular
        .module('supergooalrosApp')
        .factory('QuotaHebdo', QuotaHebdo);

    QuotaHebdo.$inject = ['$resource'];

    function QuotaHebdo ($resource) {
        var resourceUrl =  'api/quota-hebdos/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
