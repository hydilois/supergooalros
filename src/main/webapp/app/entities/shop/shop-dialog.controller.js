(function() {
    'use strict';

    angular
        .module('supergooalrosApp')
        .controller('ShopDialogController', ShopDialogController);

    ShopDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Shop', 'Employe', 'QuotaHebdo', 'RecetteJournaliere', 'User'];

    function ShopDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Shop, Employe, QuotaHebdo, RecetteJournaliere, User) {
        var vm = this;

        vm.shop = entity;
        vm.clear = clear;
        vm.save = save;
        vm.employes = Employe.query();
        vm.quotahebdos = QuotaHebdo.query();
        vm.recettejournalieres = RecetteJournaliere.query();
        vm.users = User.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.shop.id !== null) {
                Shop.update(vm.shop, onSaveSuccess, onSaveError);
            } else {
                Shop.save(vm.shop, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('supergooalrosApp:shopUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
