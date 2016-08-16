'use strict';

describe('Controller Tests', function() {

    describe('Absence Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockAbsence, MockEmploye;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockAbsence = jasmine.createSpy('MockAbsence');
            MockEmploye = jasmine.createSpy('MockEmploye');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'Absence': MockAbsence,
                'Employe': MockEmploye
            };
            createController = function() {
                $injector.get('$controller')("AbsenceDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'supergooalrosApp:absenceUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
