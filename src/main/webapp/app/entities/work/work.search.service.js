(function() {
    'use strict';

    angular
        .module('supergooalrosApp')
        .factory('WorkSearch', WorkSearch);

    WorkSearch.$inject = ['$resource'];

    function WorkSearch($resource) {
        var resourceUrl =  'api/_search/works/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
