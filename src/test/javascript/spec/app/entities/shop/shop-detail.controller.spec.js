'use strict';

describe('Controller Tests', function() {

    describe('Shop Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockShop, MockEmploye, MockQuotaHebdo, MockRecetteJournaliere, MockUser;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockShop = jasmine.createSpy('MockShop');
            MockEmploye = jasmine.createSpy('MockEmploye');
            MockQuotaHebdo = jasmine.createSpy('MockQuotaHebdo');
            MockRecetteJournaliere = jasmine.createSpy('MockRecetteJournaliere');
            MockUser = jasmine.createSpy('MockUser');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'Shop': MockShop,
                'Employe': MockEmploye,
                'QuotaHebdo': MockQuotaHebdo,
                'RecetteJournaliere': MockRecetteJournaliere,
                'User': MockUser
            };
            createController = function() {
                $injector.get('$controller')("ShopDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'supergooalrosApp:shopUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
