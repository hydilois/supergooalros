(function() {
    'use strict';

    angular
        .module('supergooalrosApp')
        .factory('AbsenceSearch', AbsenceSearch);

    AbsenceSearch.$inject = ['$resource'];

    function AbsenceSearch($resource) {
        var resourceUrl =  'api/_search/absences/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
