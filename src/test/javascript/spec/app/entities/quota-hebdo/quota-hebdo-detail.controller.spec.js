'use strict';

describe('Controller Tests', function() {

    describe('QuotaHebdo Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockQuotaHebdo, MockShop;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockQuotaHebdo = jasmine.createSpy('MockQuotaHebdo');
            MockShop = jasmine.createSpy('MockShop');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'QuotaHebdo': MockQuotaHebdo,
                'Shop': MockShop
            };
            createController = function() {
                $injector.get('$controller')("QuotaHebdoDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'supergooalrosApp:quotaHebdoUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
