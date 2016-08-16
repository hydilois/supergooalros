(function() {
    'use strict';

    angular
        .module('supergooalrosApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('recette-journaliere', {
            parent: 'entity',
            url: '/recette-journaliere?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'RecetteJournalieres'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/recette-journaliere/recette-journalieres.html',
                    controller: 'RecetteJournaliereController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'id,asc',
                    squash: true
                },
                search: null
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        search: $stateParams.search
                    };
                }],
            }
        })
        .state('recette-journaliere-detail', {
            parent: 'entity',
            url: '/recette-journaliere/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'RecetteJournaliere'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/recette-journaliere/recette-journaliere-detail.html',
                    controller: 'RecetteJournaliereDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'RecetteJournaliere', function($stateParams, RecetteJournaliere) {
                    return RecetteJournaliere.get({id : $stateParams.id}).$promise;
                }]
            }
        })
        .state('recette-journaliere.new', {
            parent: 'recette-journaliere',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/recette-journaliere/recette-journaliere-dialog.html',
                    controller: 'RecetteJournaliereDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                date: null,
                                montant: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('recette-journaliere', null, { reload: true });
                }, function() {
                    $state.go('recette-journaliere');
                });
            }]
        })
        .state('recette-journaliere.edit', {
            parent: 'recette-journaliere',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/recette-journaliere/recette-journaliere-dialog.html',
                    controller: 'RecetteJournaliereDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['RecetteJournaliere', function(RecetteJournaliere) {
                            return RecetteJournaliere.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('recette-journaliere', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('recette-journaliere.delete', {
            parent: 'recette-journaliere',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/recette-journaliere/recette-journaliere-delete-dialog.html',
                    controller: 'RecetteJournaliereDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['RecetteJournaliere', function(RecetteJournaliere) {
                            return RecetteJournaliere.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('recette-journaliere', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
