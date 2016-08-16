(function() {
    'use strict';

    angular
        .module('supergooalrosApp')
        .controller('QuotaHebdoDetailController', QuotaHebdoDetailController);

    QuotaHebdoDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'QuotaHebdo', 'Shop'];

    function QuotaHebdoDetailController($scope, $rootScope, $stateParams, entity, QuotaHebdo, Shop) {
        var vm = this;

        vm.quotaHebdo = entity;

        var unsubscribe = $rootScope.$on('supergooalrosApp:quotaHebdoUpdate', function(event, result) {
            vm.quotaHebdo = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
