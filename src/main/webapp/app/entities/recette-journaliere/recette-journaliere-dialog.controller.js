(function() {
    'use strict';

    angular
        .module('supergooalrosApp')
        .controller('RecetteJournaliereDialogController', RecetteJournaliereDialogController);

    RecetteJournaliereDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'RecetteJournaliere', 'Shop'];

    function RecetteJournaliereDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, RecetteJournaliere, Shop) {
        var vm = this;

        vm.recetteJournaliere = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        vm.shops = Shop.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.recetteJournaliere.id !== null) {
                RecetteJournaliere.update(vm.recetteJournaliere, onSaveSuccess, onSaveError);
            } else {
                RecetteJournaliere.save(vm.recetteJournaliere, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('supergooalrosApp:recetteJournaliereUpdate', result);
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
