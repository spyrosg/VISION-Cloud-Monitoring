/* global requirejs */

requirejs.config({
    baseUrl: '.',
    paths: {
        // external libraries used
        'q': 'lib/q.min',
        'crel': 'lib/crel',

        // internal app libraries
        'ajax': 'src/ajax',
        'dom': 'src/dom',

        'core': 'src/core'
    }
});


// This is used to boot the application.
requirejs(['core'], function(core) {
    'use strict';

    core.main();
});
