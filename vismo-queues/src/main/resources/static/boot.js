/* global requirejs */
/* jshint devel: true */


requirejs.config({
    baseUrl: '.',
    paths: {
        // external libraries used
        'q': 'lib/q.min',

        // internal app libraries
        'ajax': 'src/ajax',
        'dom': 'src/dom',
        'util': 'src/util',
        'views': 'src/views',
        'ctrls': 'src/ctrls',

        'core': 'src/core'
    }
});


// Boot the application.
requirejs(['core'], function(core) {
    'use strict';

    console.info('booting');
    core.setup();
});
