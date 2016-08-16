(function() {
    'use strict';

    angular
        .module('supergooalrosApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('quota-hebdo', {
            parent: 'entity',
            url: '/quota-hebdo?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'QuotaHebdos'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/quota-hebdo/quota-hebdos.html',
                    controller: 'QuotaHebdoController',
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
        .state('quota-hebdo-detail', {
            parent: 'entity',
            url: '/quota-hebdo/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'QuotaHebdo'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/quota-hebdo/quota-hebdo-detail.html',
                    controller: 'QuotaHebdoDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'QuotaHebdo', function($stateParams, QuotaHebdo) {
                    return QuotaHebdo.get({id : $stateParams.id}).$promise;
                }]
            }
        })
        .state('quota-hebdo.new', {
            parent: 'quota-hebdo',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/quota-hebdo/quota-hebdo-dialog.html',
                    controller: 'QuotaHebdoDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                montant: null,
                                primeHebdo: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('quota-hebdo', null, { reload: true });
                }, function() {
                    $state.go('quota-hebdo');
                });
            }]
        })
        .state('quota-hebdo.edit', {
            parent: 'quota-hebdo',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/quota-hebdo/quota-hebdo-dialog.html',
                    controller: 'QuotaHebdoDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['QuotaHebdo', function(QuotaHebdo) {
                            return QuotaHebdo.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('quota-hebdo', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('quota-hebdo.delete', {
            parent: 'quota-hebdo',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/quota-hebdo/quota-hebdo-delete-dialog.html',
                    controller: 'QuotaHebdoDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['QuotaHebdo', function(QuotaHebdo) {
                            return QuotaHebdo.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('quota-hebdo', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
