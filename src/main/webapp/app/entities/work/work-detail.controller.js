(function() {
    'use strict';

    angular
        .module('supergooalrosApp')
        .controller('WorkDetailController', WorkDetailController);

    WorkDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Work', 'Employe'];

    function WorkDetailController($scope, $rootScope, $stateParams, entity, Work, Employe) {
        var vm = this;

        vm.work = entity;

        var unsubscribe = $rootScope.$on('supergooalrosApp:workUpdate', function(event, result) {
            vm.work = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
