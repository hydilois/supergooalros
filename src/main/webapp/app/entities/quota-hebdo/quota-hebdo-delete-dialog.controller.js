(function() {
    'use strict';

    angular
        .module('supergooalrosApp')
        .controller('QuotaHebdoDeleteController',QuotaHebdoDeleteController);

    QuotaHebdoDeleteController.$inject = ['$uibModalInstance', 'entity', 'QuotaHebdo'];

    function QuotaHebdoDeleteController($uibModalInstance, entity, QuotaHebdo) {
        var vm = this;

        vm.quotaHebdo = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            QuotaHebdo.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
