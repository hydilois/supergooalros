(function() {
    'use strict';

    angular
        .module('supergooalrosApp')
        .controller('ShopDetailController', ShopDetailController);

    ShopDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Shop', 'Employe', 'QuotaHebdo', 'RecetteJournaliere', 'User'];

    function ShopDetailController($scope, $rootScope, $stateParams, entity, Shop, Employe, QuotaHebdo, RecetteJournaliere, User) {
        var vm = this;

        vm.shop = entity;

        var unsubscribe = $rootScope.$on('supergooalrosApp:shopUpdate', function(event, result) {
            vm.shop = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
