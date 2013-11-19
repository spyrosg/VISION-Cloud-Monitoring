/* global define */
/* jshint devel: true */


// this is were the application is assembled, configured and started.
define(['views', 'ctrls', 'queues'], function(views, ctrls, cdmiQueuesModel) {
    'use strict';

    return {
        setup: function() {
            console.info('app setup');
            cdmiQueuesModel.setup();

            Object.getOwnPropertyNames(views).forEach(function(name) {
                var view = views[name];

                view.setup(cdmiQueuesModel);
                cdmiQueuesModel.add(view);
            });
            Object.getOwnPropertyNames(ctrls).forEach(function(name) {
                var ctrl = ctrls[name];

                ctrl.setup(cdmiQueuesModel);
            });

            this.prepare_queue();
            console.info('app started');
        },

        // TODO: add a remove all queues button
        prepare_queue: function() {
            var queue_name = 'vismo-cdmi-demo-' + Date.now(),
                topic = '*', // we're interested in all kind of events
                update_interval = 1000; // 1000msec

            cdmiQueuesModel.create_queue(queue_name, topic).then(function() {
                cdmiQueuesModel.read_queue_with_interval(queue_name, update_interval);
            });
        }
    };
});
