(function() {
    'use strict';

    angular
        .module('supergooalrosApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('work', {
            parent: 'entity',
            url: '/work?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Works'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/work/works.html',
                    controller: 'WorkController',
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
        .state('work-detail', {
            parent: 'entity',
            url: '/work/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Work'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/work/work-detail.html',
                    controller: 'WorkDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'Work', function($stateParams, Work) {
                    return Work.get({id : $stateParams.id}).$promise;
                }]
            }
        })
        .state('work.new', {
            parent: 'work',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/work/work-dialog.html',
                    controller: 'WorkDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                date: null,
                                heuredebut: null,
                                heureFin: null,
                                nombreTickets: null,
                                sommeEncaissee: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('work', null, { reload: true });
                }, function() {
                    $state.go('work');
                });
            }]
        })
        .state('work.edit', {
            parent: 'work',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/work/work-dialog.html',
                    controller: 'WorkDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Work', function(Work) {
                            return Work.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('work', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('work.delete', {
            parent: 'work',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/work/work-delete-dialog.html',
                    controller: 'WorkDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Work', function(Work) {
                            return Work.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('work', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
