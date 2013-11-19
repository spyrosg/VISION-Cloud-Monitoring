/* global define */
/* jshint devel: true */


// this is were the application is assembled,
// configured and started.
// Search for the ``setup`` method.
define(['service', 'util', 'views', 'ctrls', 'ajax'], function(service, util, views, ctrls, ajax) {
    'use strict';

    var extend = util.extend,
        Observer = util.Observer;

    var cdmiQueuesModel = {
        setup: function() {
            this.observables = []; // NOTE: required by Observer
        },

        create_queue: function(name, topic) {
            var self = this;

            return service.create(name, topic).then(function() {
                return name;
            }, function(req) {
                console.error('could not create queue:', req.statusText + ', ' + req.responseText);
            });
        },

        read_queue: function(name) {
            var self = this;

            return service.read(name).then(function(eventList) {
                eventList.forEach(function(e) { self.notify(e.topic, e); });
                return eventList;
            });
        },

        delete_values_off: function(name) {
            service.delete(name + '?values:10000');
        },

        read_repeatedly: function(name) {
            var self = this;

            self.read_queue(name).then(function() {
                self.delete_values_off(name);
            });
        },

        read_queue_with_interval: function(name, update_interval) {
            var self = this;

            setInterval(function() { self.read_repeatedly(name); }, update_interval);
        },

        update_vismo_rule: function(period) {
            return ajax('http://10.0.1.101:9996/rules/0c42c9f5-6c99-4b63-8c03-fce9911cc7f5/period/' + period, 'PUT');
        },

        update_rule_period_to: function(period) {
            if (Number.isNaN(Number(period))) {
                return;
            }
            if (Number(period) <= 0) {
                return;
            }

            period = Number(period) * 1000;

            return this.update_vismo_rule(period).then(function() {
                console.log("rule updated succeded");
            }, function(req) {
                console.error('rule update failed:', req.statusText + ', ' + req.responseText);
            });
        }
    };

    extend(cdmiQueuesModel).with(Observer);

    return {
        setup: function() {
            console.info('app setup');
            cdmiQueuesModel.setup();

            Object.getOwnPropertyNames(views).forEach(function(name) {
                var view = views[name];

                view.setup(cdmiQueuesModel);
                cdmiQueuesModel.add(view);
            });
            Object.getOwnPropertyNames(ctrls).forEach(function(name) {
                var ctrl = ctrls[name];

                ctrl.setup(cdmiQueuesModel);
            });

            this.setup_queue();
            console.info('app started');
        },

        // TODO: add a remove all queues button
        setup_queue: function() {
            var queue_name = 'vismo-cdmi-demo-' + Date.now(),
                topic = '*', // we're interested in all kind of events
                update_interval = 1000; // 1000msec

            cdmiQueuesModel.create_queue(queue_name, topic).then(function() {
                cdmiQueuesModel.read_queue_with_interval(queue_name, update_interval);
            });
        }
    };
});
