(function() {
    'use strict';

    angular
        .module('supergooalrosApp')
        .controller('WorkDeleteController',WorkDeleteController);

    WorkDeleteController.$inject = ['$uibModalInstance', 'entity', 'Work'];

    function WorkDeleteController($uibModalInstance, entity, Work) {
        var vm = this;

        vm.work = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Work.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
