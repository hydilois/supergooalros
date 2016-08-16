(function() {
    'use strict';

    angular
        .module('supergooalrosApp')
        .controller('EmployeDetailController', EmployeDetailController);

    EmployeDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'DataUtils', 'entity', 'Employe', 'Shop', 'Prime', 'Absence', 'Work', 'Conge'];

    function EmployeDetailController($scope, $rootScope, $stateParams, DataUtils, entity, Employe, Shop, Prime, Absence, Work, Conge) {
        var vm = this;

        vm.employe = entity;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;

        var unsubscribe = $rootScope.$on('supergooalrosApp:employeUpdate', function(event, result) {
            vm.employe = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
