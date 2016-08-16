(function() {
    'use strict';

    angular
        .module('supergooalrosApp')
        .controller('QuotaHebdoDialogController', QuotaHebdoDialogController);

    QuotaHebdoDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'QuotaHebdo', 'Shop'];

    function QuotaHebdoDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, QuotaHebdo, Shop) {
        var vm = this;

        vm.quotaHebdo = entity;
        vm.clear = clear;
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
            if (vm.quotaHebdo.id !== null) {
                QuotaHebdo.update(vm.quotaHebdo, onSaveSuccess, onSaveError);
            } else {
                QuotaHebdo.save(vm.quotaHebdo, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('supergooalrosApp:quotaHebdoUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
