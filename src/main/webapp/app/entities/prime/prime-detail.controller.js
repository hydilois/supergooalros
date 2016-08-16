(function() {
    'use strict';

    angular
        .module('supergooalrosApp')
        .controller('PrimeDetailController', PrimeDetailController);

    PrimeDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Prime', 'Employe'];

    function PrimeDetailController($scope, $rootScope, $stateParams, entity, Prime, Employe) {
        var vm = this;

        vm.prime = entity;

        var unsubscribe = $rootScope.$on('supergooalrosApp:primeUpdate', function(event, result) {
            vm.prime = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
