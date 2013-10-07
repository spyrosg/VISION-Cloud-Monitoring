/* global define */
/* jshint devel: true */


// this is were the application is assembled,
// configured and started.
// Search for the ``setup`` method.
define(['ajax', 'util', 'views', 'ctrls'], function(ajax, util, views, ctrls) {
    'use strict';

    var extend = util.extend,
        Observer = util.Observer;

    var cdmiQueuesModel = {
        setup: function() {
            this.observables = []; // NOTE: required by Observer
        },

        service: {
            root_server: '/api/queues',

            headers: {
                'Accept': 'application/cdmi-queue',
                'X-CDMI-Specification-Version': '1.0.2'
            },

            create: function(name, topic) {
                return ajax(this.root_server + '/' + name + '/' + topic, 'PUT', {
                        'Content-Type': 'application/cdmi-queue',
                        'Accept': 'application/cdmi-queue',
                        'X-CDMI-Specification-Version': '1.0.2'
                    }).then(JSON.parse);
            },

            read: function(name) {
                return ajax(this.root_server + '/' + name, 'GET', this.headers)
                    .then(JSON.parse)
                    .then(function(cdmi) { return cdmi.value; });
            },

            list: function() {
                return ajax(this.root_server, 'GET', this.headers)
                    .then(JSON.parse);
            }
        },

        create_queue: function(name, topic) {
            var self = this;

            this
                .service.create(name, topic)
                .then(
                    function() {
                        self.notify('queue', name);
                        self.notify('new:queue', name);
                        self.reset_read_events_timer(name);
                    },
                    function(req) {
                        console.error('error creating queue:', req.statusText + ', ' + req.responseText);
                    })
                .done();
        },

        interval_fn_id: null,

        reset_read_events_timer: function(name) {
            var self = this;

            if (this.interval_fn_id !== null) {
                clearInterval(this.interval_fn_id);
            }

            this.interval_fn_id = setInterval(function() {
                self.read_queue(name);
            }, 1000);
        },

        read_queue: function(name) {
            var self = this;

            this
                .service.read(name)
                .then(function(eventList) {
                    self.notify('events', eventList);

                    eventList.forEach(function(e) {
                        if (e.topic === 'storletProgress') {
                            self.notify('storlets', e['originating-service'], e.progress);
                        }
                    });
                })
                .done();
        },

        render_queues: function() {
            var self = this;

            this.service.list()
                .then(function(list) {
                    list.forEach(function(q) {
                        self.notify('queue', q.objectName);
                    });
                })
                .done();
        },

        render_events: function() {
            var self = this;

            this.service.list()
                .then(function(list) {
                    if (list.length > 0) {
                        var q = list[list.length - 1];

                        self.reset_read_events_timer(q.objectName);
                    }
                })
                .done();
        }
    };

    extend(cdmiQueuesModel).with(Observer);

    return {
        setup: function() {
            cdmiQueuesModel.setup();

            Object.getOwnPropertyNames(views).forEach(function(name) {
                var view = views[name];

                view.setup(cdmiQueuesModel);
                cdmiQueuesModel.add(view);
            });

            ctrls.updateButtonController.setup(cdmiQueuesModel);

            cdmiQueuesModel.render_queues();
            cdmiQueuesModel.render_events();
            console.info('app started');
        }
    };
});
