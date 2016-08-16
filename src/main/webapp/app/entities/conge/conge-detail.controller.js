(function() {
    'use strict';

    angular
        .module('supergooalrosApp')
        .controller('CongeDetailController', CongeDetailController);

    CongeDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Conge', 'Employe'];

    function CongeDetailController($scope, $rootScope, $stateParams, entity, Conge, Employe) {
        var vm = this;

        vm.conge = entity;

        var unsubscribe = $rootScope.$on('supergooalrosApp:congeUpdate', function(event, result) {
            vm.conge = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
