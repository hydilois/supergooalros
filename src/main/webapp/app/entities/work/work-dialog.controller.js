(function() {
    'use strict';

    angular
        .module('supergooalrosApp')
        .controller('WorkDialogController', WorkDialogController);

    WorkDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Work', 'Employe'];

    function WorkDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Work, Employe) {
        var vm = this;

        vm.work = entity;
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
            if (vm.work.id !== null) {
                Work.update(vm.work, onSaveSuccess, onSaveError);
            } else {
                Work.save(vm.work, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('supergooalrosApp:workUpdate', result);
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
