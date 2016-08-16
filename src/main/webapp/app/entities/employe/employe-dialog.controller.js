(function() {
    'use strict';

    angular
        .module('supergooalrosApp')
        .controller('EmployeDialogController', EmployeDialogController);

    EmployeDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'DataUtils', 'entity', 'Employe', 'Shop', 'Prime', 'Absence', 'Work', 'Conge'];

    function EmployeDialogController ($timeout, $scope, $stateParams, $uibModalInstance, DataUtils, entity, Employe, Shop, Prime, Absence, Work, Conge) {
        var vm = this;

        vm.employe = entity;
        vm.clear = clear;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;
        vm.save = save;
        vm.shops = Shop.query();
        vm.primes = Prime.query();
        vm.absences = Absence.query();
        vm.works = Work.query();
        vm.conges = Conge.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.employe.id !== null) {
                Employe.update(vm.employe, onSaveSuccess, onSaveError);
            } else {
                Employe.save(vm.employe, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('supergooalrosApp:employeUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


        vm.setPhoto = function ($file, employe) {
            if ($file && $file.$error === 'pattern') {
                return;
            }
            if ($file) {
                DataUtils.toBase64($file, function(base64Data) {
                    $scope.$apply(function() {
                        employe.photo = base64Data;
                        employe.photoContentType = $file.type;
                    });
                });
            }
        };

    }
})();
