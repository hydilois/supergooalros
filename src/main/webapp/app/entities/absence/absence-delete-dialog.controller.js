(function() {
    'use strict';

    angular
        .module('supergooalrosApp')
        .controller('AbsenceDeleteController',AbsenceDeleteController);

    AbsenceDeleteController.$inject = ['$uibModalInstance', 'entity', 'Absence'];

    function AbsenceDeleteController($uibModalInstance, entity, Absence) {
        var vm = this;

        vm.absence = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Absence.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
