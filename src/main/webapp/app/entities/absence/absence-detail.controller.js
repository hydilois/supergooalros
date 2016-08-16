(function() {
    'use strict';

    angular
        .module('supergooalrosApp')
        .controller('AbsenceDetailController', AbsenceDetailController);

    AbsenceDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Absence', 'Employe'];

    function AbsenceDetailController($scope, $rootScope, $stateParams, entity, Absence, Employe) {
        var vm = this;

        vm.absence = entity;

        var unsubscribe = $rootScope.$on('supergooalrosApp:absenceUpdate', function(event, result) {
            vm.absence = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
