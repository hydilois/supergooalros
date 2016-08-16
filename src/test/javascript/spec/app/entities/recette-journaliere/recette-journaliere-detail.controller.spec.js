'use strict';

describe('Controller Tests', function() {

    describe('RecetteJournaliere Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockRecetteJournaliere, MockShop;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockRecetteJournaliere = jasmine.createSpy('MockRecetteJournaliere');
            MockShop = jasmine.createSpy('MockShop');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'RecetteJournaliere': MockRecetteJournaliere,
                'Shop': MockShop
            };
            createController = function() {
                $injector.get('$controller')("RecetteJournaliereDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'supergooalrosApp:recetteJournaliereUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
