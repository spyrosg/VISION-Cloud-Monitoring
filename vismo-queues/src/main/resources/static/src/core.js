/* global define */
/* jshint devel: true */


// this is were the application is assembled,
// configured and started.
// Search for the ``setup`` method.
define(['service', 'util', 'views', 'ctrls', 'dom'], function(service, util, views, ctrls, dom) {
    'use strict';

    var extend = util.extend,
        Observer = util.Observer;

    var cdmiQueuesModel = {
        setup: function() {
            this.observables = []; // NOTE: required by Observer
        },

        create_queue: function(name, topic) {
            var self = this;

            service.create(name, topic).then(function() {
                self.render_queues();
            }, function(req) {
                console.error('could not create queue:', req.statusText + ', ' + req.responseText);
            });
        },

        read_queue: function(name) {
            var self = this;

            return service.read(name).then(function(eventList) {
                eventList.forEach(function(e) { self.notify(e.topic, e); });

                return [];
            });
        },

        delete_values_off: function(name) {
            service.delete(name + '?values:10000');
        },
    };

    extend(cdmiQueuesModel).with(Observer);

    return {
        setup: function() {
            cdmiQueuesModel.setup();

            Object.getOwnPropertyNames(views).forEach(function(name) {
                var view = views[name];

                view.setup(cdmiQueuesModel);
                cdmiQueuesModel.add(view);
            });

            console.info('app started');

            setInterval(function() {
                cdmiQueuesModel.read_queue('m1').then(function() {
                    console.debug('before deleting');
                    cdmiQueuesModel.delete_values_off('m1');
                    console.debug('after deleting');
                });
            }, 1000);
        }
    };
});
