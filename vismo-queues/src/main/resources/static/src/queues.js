/* global define */
/* jshint devel: true */


// this is were the application is assembled,
// configured and started.
// Search for the ``setup`` method.
define(['service', 'util'], function(service, util) {
    'use strict';

    var extend = util.extend,
        Observer = util.Observer,
        queues_service = service.queues_service,
        rules_service = service.rules_service;

    var cdmiQueuesModel = {
        metrics_rule_id: null,

        setup: function() {
            this.observables = []; // NOTE: required by Observer
            this.get_metrics_rule_id();
        },

        create_queue: function(name, topic) {
            var self = this;

            return queues_service.create(name, topic).then(function() {
                return name;
            }, function(req) {
                console.error('could not create queue:', req.statusText + ', ' + req.responseText);
            });
        },

        read_queue: function(name) {
            var self = this;

            return queues_service.read(name).then(function(eventList) {
                eventList.forEach(function(e) { self.notify(e.topic, e); });
                return eventList;
            });
        },

        delete_values_off: function(name) {
            queues_service.delete_queue(name + '?values:10000');
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
            return rules_service.update_rule_period(this.metrics_rule_id, period);
        },

        get_metrics_rule_id: function() {
            var self = this;

            rules_service.get_metrics_rule_id().then(function(id) {
                self.metrics_rule_id = id;
            });
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

    return cdmiQueuesModel;
});
