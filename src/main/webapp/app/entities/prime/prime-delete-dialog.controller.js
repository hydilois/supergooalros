(function() {
    'use strict';

    angular
        .module('supergooalrosApp')
        .controller('PrimeDeleteController',PrimeDeleteController);

    PrimeDeleteController.$inject = ['$uibModalInstance', 'entity', 'Prime'];

    function PrimeDeleteController($uibModalInstance, entity, Prime) {
        var vm = this;

        vm.prime = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Prime.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
