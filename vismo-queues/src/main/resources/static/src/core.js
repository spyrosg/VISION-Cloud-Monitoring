/* global define */
/* jshint devel: true */


// this is were the application is assembled,
// configured and started.
// Search for the ``setup`` method.
define(['service', 'util', 'views', 'ctrls'], function(service, util, views, ctrls) {
    'use strict';

    var extend = util.extend,
        Observer = util.Observer;

    var cdmiQueuesModel = {
        setup: function() {
            this.observables = []; // NOTE: required by Observer
        },

        available_queues: [],

        current_queue: null,

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

            service.read(name).then(function(eventList) {
                eventList.forEach(function(e) { self.notify(e.topic, e); });
            });
        },

        render_queues: function() {
            var self = this;

            service.list().then(function(list) {
                list.forEach(function(q) {
                    self.notify('queue', q.objectName);
                });
            });
        }
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

            ctrls.updateButtonController.setup(cdmiQueuesModel);

            cdmiQueuesModel.render_queues();
            console.info('app started');
        }
    };
});
