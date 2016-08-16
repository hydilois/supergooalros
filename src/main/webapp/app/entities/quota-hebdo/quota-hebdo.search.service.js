(function() {
    'use strict';

    angular
        .module('supergooalrosApp')
        .factory('QuotaHebdoSearch', QuotaHebdoSearch);

    QuotaHebdoSearch.$inject = ['$resource'];

    function QuotaHebdoSearch($resource) {
        var resourceUrl =  'api/_search/quota-hebdos/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
