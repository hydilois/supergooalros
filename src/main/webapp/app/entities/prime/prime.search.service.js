(function() {
    'use strict';

    angular
        .module('supergooalrosApp')
        .factory('PrimeSearch', PrimeSearch);

    PrimeSearch.$inject = ['$resource'];

    function PrimeSearch($resource) {
        var resourceUrl =  'api/_search/primes/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
