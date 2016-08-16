(function() {
    'use strict';

    angular
        .module('supergooalrosApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('prime', {
            parent: 'entity',
            url: '/prime?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Primes'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/prime/primes.html',
                    controller: 'PrimeController',
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
        .state('prime-detail', {
            parent: 'entity',
            url: '/prime/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Prime'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/prime/prime-detail.html',
                    controller: 'PrimeDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'Prime', function($stateParams, Prime) {
                    return Prime.get({id : $stateParams.id}).$promise;
                }]
            }
        })
        .state('prime.new', {
            parent: 'prime',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/prime/prime-dialog.html',
                    controller: 'PrimeDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                montant: null,
                                dateFixation: null,
                                raison: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('prime', null, { reload: true });
                }, function() {
                    $state.go('prime');
                });
            }]
        })
        .state('prime.edit', {
            parent: 'prime',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/prime/prime-dialog.html',
                    controller: 'PrimeDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Prime', function(Prime) {
                            return Prime.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('prime', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('prime.delete', {
            parent: 'prime',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/prime/prime-delete-dialog.html',
                    controller: 'PrimeDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Prime', function(Prime) {
                            return Prime.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('prime', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
