/* global define */
/* jshint devel: true */


// this is were the application is assembled,
// configured and started.
// Search for the ``setup`` method.
define(['service', 'util', 'views', 'ctrls', 'dom'], function(service, util, views, ctrls, dom) {
    'use strict';

    var extend = util.extend,
        Observer = util.Observer;

    var cdmiQueuesModel = {
        setup: function() {
            this.observables = []; // NOTE: required by Observer
        },

        create_queue: function(name, topic) {
            var self = this;

            service.create(name, topic).then(function() {
                self.render_queues();
            }, function(req) {
                console.error('could not create queue:', req.statusText + ', ' + req.responseText);
            });
        },

        read_queue: function(name) {
            var self = this;

            service.read(name).then(function(eventList) {
                eventList.forEach(function(e) { self.notify(e.topic, e); });
            });
        }
    };

    extend(cdmiQueuesModel).with(Observer);

    function draw_canvas_background_on(canvas) {
        var ctx = canvas.getContext('2d');

        ctx.rect(0, 0, canvas.width, canvas.height);
        ctx.fillStyle = '#fff';
        ctx.fill();
        ctx.strokeStyle = '#eee';

        for (var x = 0.5; x < canvas.width; x += 10) {
            ctx.moveTo(x, 0);
            ctx.lineTo(x, canvas.height);
        }

        for (var y = 0.5; y < canvas.height; y += 10) {
            ctx.moveTo(0, y);
            ctx.lineTo(canvas.width, y);
            ctx.stroke();
        }
    }

    return {
        setup: function() {
            cdmiQueuesModel.setup();

            Object.getOwnPropertyNames(views).forEach(function(name) {
                var view = views[name];

                view.setup(cdmiQueuesModel);
                cdmiQueuesModel.add(view);
            });

            draw_canvas_background_on(dom.id('cpu-graph'));
            console.info('app started');
        }
    };
});
