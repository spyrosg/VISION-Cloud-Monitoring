/* global define */
/* jshint devel: true */


define(['ajax', 'util'], function(ajax, util) {
    'use strict';

    var extend = util.extend;

    return {
        root_server: '/api/queues',

        headers: {
            'Accept': 'application/cdmi-queue',
            'X-CDMI-Specification-Version': '1.0.2'
        },

        request: function(path, name, headers) {
            return ajax(this.root_server + path, name, extend(this.headers).with(headers)).then(JSON.parse);
        },

        put: function(path, headers) {
            return this.request(path, 'PUT', headers);
        },

        get: function(path, headers) {
            return this.request(path, 'GET', headers);
        },

        delete: function(path, headers) {
            return this.request(path, 'DELETE', headers);
        },

        create: function(name, topic) {
            return this.put('/' + name + '/' + topic, { 'Content-Type': 'application/cdmi-queue' });
        },

        read: function(name) {
            return this.get('/' + name).then(function(cdmi) { return cdmi.value; });
        },

        list: function() {
            return this.get('/');
        }
    };
});