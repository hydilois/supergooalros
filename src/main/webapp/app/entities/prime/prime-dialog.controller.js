(function() {
    'use strict';

    angular
        .module('supergooalrosApp')
        .controller('PrimeDialogController', PrimeDialogController);

    PrimeDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Prime', 'Employe'];

    function PrimeDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Prime, Employe) {
        var vm = this;

        vm.prime = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        vm.employes = Employe.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.prime.id !== null) {
                Prime.update(vm.prime, onSaveSuccess, onSaveError);
            } else {
                Prime.save(vm.prime, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('supergooalrosApp:primeUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.dateFixation = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
