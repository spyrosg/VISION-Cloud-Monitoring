/* global requirejs */
/* jshint devel: true */


requirejs.config({
    baseUrl: '.',
    urlArgs: "bust=" + (new Date()).getTime(),
    paths: {
        // external libraries used
        when: 'lib/when',
        canvasjs: 'lib/canvasjs.min',

        // internal app libraries
        ajax: 'src/ajax',
        dom: 'src/dom',
        util: 'src/util',
        http: 'src/http',
        services: 'src/services',
        views: 'src/views',
        ctrls: 'src/ctrls',
        queues: 'src/queues',

        core: 'src/core'
    },
    shim: {
        canvasjs: {
            exports: 'CanvasJS'
        }
    }
});


// Boot the application.
requirejs(['core'], function(core) {
    'use strict';

    console.info('booting');
    core.setup();
});
