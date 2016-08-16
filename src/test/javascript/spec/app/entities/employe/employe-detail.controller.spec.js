'use strict';

describe('Controller Tests', function() {

    describe('Employe Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockEmploye, MockShop, MockPrime, MockAbsence, MockWork, MockConge;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockEmploye = jasmine.createSpy('MockEmploye');
            MockShop = jasmine.createSpy('MockShop');
            MockPrime = jasmine.createSpy('MockPrime');
            MockAbsence = jasmine.createSpy('MockAbsence');
            MockWork = jasmine.createSpy('MockWork');
            MockConge = jasmine.createSpy('MockConge');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'Employe': MockEmploye,
                'Shop': MockShop,
                'Prime': MockPrime,
                'Absence': MockAbsence,
                'Work': MockWork,
                'Conge': MockConge
            };
            createController = function() {
                $injector.get('$controller')("EmployeDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'supergooalrosApp:employeUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
