(function() {
    'use strict';

    angular
        .module('supergooalrosApp')
        .controller('RecetteJournaliereDetailController', RecetteJournaliereDetailController);

    RecetteJournaliereDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'RecetteJournaliere', 'Shop'];

    function RecetteJournaliereDetailController($scope, $rootScope, $stateParams, entity, RecetteJournaliere, Shop) {
        var vm = this;

        vm.recetteJournaliere = entity;

        var unsubscribe = $rootScope.$on('supergooalrosApp:recetteJournaliereUpdate', function(event, result) {
            vm.recetteJournaliere = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
