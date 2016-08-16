(function() {
    'use strict';

    angular
        .module('supergooalrosApp')
        .controller('AbsenceDialogController', AbsenceDialogController);

    AbsenceDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Absence', 'Employe'];

    function AbsenceDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Absence, Employe) {
        var vm = this;

        vm.absence = entity;
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
            if (vm.absence.id !== null) {
                Absence.update(vm.absence, onSaveSuccess, onSaveError);
            } else {
                Absence.save(vm.absence, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('supergooalrosApp:absenceUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.date = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
