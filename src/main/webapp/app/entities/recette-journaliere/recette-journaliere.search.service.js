(function() {
    'use strict';

    angular
        .module('supergooalrosApp')
        .factory('RecetteJournaliereSearch', RecetteJournaliereSearch);

    RecetteJournaliereSearch.$inject = ['$resource'];

    function RecetteJournaliereSearch($resource) {
        var resourceUrl =  'api/_search/recette-journalieres/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
