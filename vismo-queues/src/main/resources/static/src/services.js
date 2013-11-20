/* global define */
/* jshint devel: true */


define(['util', 'http'], function(util, http) {
    'use strict';

    var extend = util.extend;

    var queues_service = {
        cdmi_headers: {
            'Accept': 'application/cdmi-queue',
            'X-CDMI-Specification-Version': '1.0.2'
        },

        server_root: function() {
            return '/api/queues';
        },

        headers: function() {
            return this.cdmi_headers;
        },

        create: function(name, topic) {
            return this.put('/' + name + '/' + topic, { 'Content-Type': 'application/cdmi-queue' });
        },

        read: function(name) {
            return this.get('/' + name).then(function(cdmi) { return cdmi.value; });
        },

        delete_queue: function(path) {
            return this.delete('/' + path);
        }
    };

    extend(queues_service).with(http);

    var rules_service = {
        json_content: {
            'Accept': 'application/json',
        },

        server_root: function() {
            // return 'http://10.0.1.101:9996';
            return window.location.protocol + '//' + window.location.hostname + ':9996';
        },

        headers: function() {
            return this.json_content;
        },

        update_rule_period: function(rule_id, period) {
            return this.put('/rules/' + rule_id + '/period/' + period);
        },

        get_metrics_rule_id: function() {
            return this.get('/rules').then(function(rules) {
                return rules.filter(function(rule) {
                    return rule['class'] === "MetricsRule";
                })[0].id;
            });
        },
    };

    extend(rules_service).with(http);

    return {
        queues_service: queues_service,

        rules_service: rules_service
    };
});
