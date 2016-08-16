(function() {
    'use strict';

    angular
        .module('supergooalrosApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('absence', {
            parent: 'entity',
            url: '/absence?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Absences'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/absence/absences.html',
                    controller: 'AbsenceController',
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
        .state('absence-detail', {
            parent: 'entity',
            url: '/absence/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Absence'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/absence/absence-detail.html',
                    controller: 'AbsenceDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'Absence', function($stateParams, Absence) {
                    return Absence.get({id : $stateParams.id}).$promise;
                }]
            }
        })
        .state('absence.new', {
            parent: 'absence',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/absence/absence-dialog.html',
                    controller: 'AbsenceDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                date: null,
                                justifie: null,
                                justification: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('absence', null, { reload: true });
                }, function() {
                    $state.go('absence');
                });
            }]
        })
        .state('absence.edit', {
            parent: 'absence',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/absence/absence-dialog.html',
                    controller: 'AbsenceDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Absence', function(Absence) {
                            return Absence.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('absence', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('absence.delete', {
            parent: 'absence',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/absence/absence-delete-dialog.html',
                    controller: 'AbsenceDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Absence', function(Absence) {
                            return Absence.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('absence', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
