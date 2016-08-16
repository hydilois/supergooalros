(function() {
    'use strict';

    angular
        .module('supergooalrosApp')
        .controller('RecetteJournaliereDeleteController',RecetteJournaliereDeleteController);

    RecetteJournaliereDeleteController.$inject = ['$uibModalInstance', 'entity', 'RecetteJournaliere'];

    function RecetteJournaliereDeleteController($uibModalInstance, entity, RecetteJournaliere) {
        var vm = this;

        vm.recetteJournaliere = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            RecetteJournaliere.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
