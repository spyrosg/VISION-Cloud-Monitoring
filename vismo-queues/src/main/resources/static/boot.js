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
        service: 'src/service',
        views: 'src/views',
        ctrls: 'src/ctrls',

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
