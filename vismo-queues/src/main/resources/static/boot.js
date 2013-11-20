/* global requirejs */
/* jshint devel: true */


requirejs.config({
    baseUrl: '.',
    urlArgs: "bust=" + (new Date()).getTime(),
    paths: {
        // external libraries
        when: 'lib/when',
        canvasjs: 'lib/canvasjs.min',

        // internal app libraries
        dom: 'src/dom',
        util: 'src/util',
        ajax: 'src/ajax',
        http: 'src/http',
        services: 'src/services',
        views: 'src/views',
        ctrls: 'src/ctrls',
        queues: 'src/queues',
    },
    shim: {
        canvasjs: {
            exports: 'CanvasJS'
        }
    }
});


// TODO: add a remove all queues button
// This is were the application is assembled, configured and started.
requirejs(['queues', 'views', 'ctrls'], function(cdmiQueuesModel, views, ctrls) {
    'use strict';

    function bind_views_to(model) {
        Object.getOwnPropertyNames(views).forEach(function(name) {
            var view = views[name];

            view.setup(model);
            model.add(view);
        });
    }

    function bind_controllers_to(model) {
        Object.getOwnPropertyNames(ctrls).forEach(function(name) {
            var ctrl = ctrls[name];

            ctrl.setup(model);
        });
    }

    function prepare_queue_using(model) {
        var queue_name = 'vismo-cdmi-demo-' + Date.now(),
            topic = '*', // we're interested in all kind of events
            update_interval = 1000; // 1000msec

        model.create_queue(queue_name, topic).then(function() {
            model.read_queue_with_interval(queue_name, update_interval);
        });
    }

    console.info('booting app');
    cdmiQueuesModel.setup();
    bind_views_to(cdmiQueuesModel);
    bind_controllers_to(cdmiQueuesModel);
    prepare_queue_using(cdmiQueuesModel);
    console.info('app running');
});
