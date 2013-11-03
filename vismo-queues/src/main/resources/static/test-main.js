/* global requirejs */

requirejs.config({
    // Karma serves files from '/base'
    baseUrl: '/base/src',

    paths: {
        // external libraries used
        q: 'lib/q.min',

        // internal app libraries
        ajax: 'ajax',
        dom: 'dom',
        util: 'util',
        views: 'views',
        ctrls: 'ctrls',

        core: 'core'
    },

    // ask Require.js to load these files (all our tests)
    deps: Object.getOwnPropertyNames(window.__karma__.files).filter(function(name) {
        'use strict';

        return (/-spec\.js$/).test(name);
    }),

    // start test run, once Require.js is done
    callback: window.__karma__.start
});
