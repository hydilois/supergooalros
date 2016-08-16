(function() {
    'use strict';

    angular
        .module('supergooalrosApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('conge', {
            parent: 'entity',
            url: '/conge?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Conges'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/conge/conges.html',
                    controller: 'CongeController',
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
        .state('conge-detail', {
            parent: 'entity',
            url: '/conge/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Conge'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/conge/conge-detail.html',
                    controller: 'CongeDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'Conge', function($stateParams, Conge) {
                    return Conge.get({id : $stateParams.id}).$promise;
                }]
            }
        })
        .state('conge.new', {
            parent: 'conge',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/conge/conge-dialog.html',
                    controller: 'CongeDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                dateDebut: null,
                                dateFin: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('conge', null, { reload: true });
                }, function() {
                    $state.go('conge');
                });
            }]
        })
        .state('conge.edit', {
            parent: 'conge',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/conge/conge-dialog.html',
                    controller: 'CongeDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Conge', function(Conge) {
                            return Conge.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('conge', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('conge.delete', {
            parent: 'conge',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/conge/conge-delete-dialog.html',
                    controller: 'CongeDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Conge', function(Conge) {
                            return Conge.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('conge', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
