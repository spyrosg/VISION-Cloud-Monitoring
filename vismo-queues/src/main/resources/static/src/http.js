/* global define */
/* jshint devel: true */


define(['ajax', 'util'], function(ajax, util) {
    'use strict';

    var extend = util.extend;

    return {
        request: function(path, name, headers) {
            return ajax(this.server_root() + path, name, extend(this.headers()).with(headers)).then(JSON.parse);
        },

        put: function(path, headers) {
            return this.request(path, 'PUT', headers);
        },

        post: function(path, headers) {
            return this.request(path, 'POST', headers);
        },

        get: function(path, headers) {
            return this.request(path, 'GET', headers);
        },

        delete: function(path, headers) {
            return this.request(path, 'DELETE', headers);
        }
    };
});
