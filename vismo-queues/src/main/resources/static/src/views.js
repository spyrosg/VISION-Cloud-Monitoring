/* global define */
/* jshint devel: true */


// the views used in the app
define(['dom', 'util', 'ctrls', 'canvasjs'], function(dom, util, ctrls, CanvasJS) {
    'use strict';

    var extend = util.extend,
        Observable = util.Observable;

    var eventsView = {
        el: dom.$('#events ul'),

        setup: function(model) {
            this.model = model;
            this.insert_into_list.el = this.el;
            this.insert_into_list.method = this.insert_into_list.append;
        },

        update: function(/*args*/) {
            if (arguments[0] !== 'events') {
                return;
            }

            var self = this,
                e = arguments[1];

            self.render(e);
        },

        render: function(e) {
            var li = dom.creat('li'),
                div = dom.creat('div'),
                time = dom.creat('span'),
                from = dom.creat('span'),
                pre = dom.creat('pre');

            time.textContent = this.calc_human_time(e.timestamp);
            time.classList.add('e');
            from.textContent = ', from: ' + e['originating-machine'] + '/' + e['originating-service'];
            from.classList.add('e');

            delete e['originating-machine'];
            delete e['originating-service'];
            // delete e['timestamp'];
            pre.textContent = JSON.stringify(e);

            div.appendChild(pre);
            div.appendChild(time);
            div.appendChild(from);
            li.appendChild(div);

            this.insert_into_list.method(li);
        },

        calc_human_time: function(t) {
            var dur = (Date.now() - t) / 1000;

            if (dur > 3600) {
                return parseInt((dur) / 3600, 10) + ' hours';
            } else if (dur > 60) {
                return parseInt((dur) / 60, 10) + ' minutes ago';
            } else {
                return parseInt(dur, 10) + ' seconds ago';
            }
        },

        insert_into_list: {
            el: null,

            method: null,

            prepend: function(elem) {
                this.el.insertBefore(elem, this.el.firstChild);
            },

            append: function(elem) {
                this.el.appendChild(elem);
                this.method = this.prepend;
            }
        }
    };

    extend(eventsView).with(Observable);

    var graphView = {
        el: dom.id('cpu-graph'),

        update_interval: 500, // 1000ms = 1sec

        title: "cpu load",

        data_queue: [],

        max_data_size: 20,

        mk_chart: function(elem_id) {
            return new CanvasJS.Chart("cpu-graph", {
                title: { text: this.title },
                axisX: { title: "time (sec)", interval: 5, },
                axisY: { title: "%", interval: 20, minimum: 0, maximum: 100 },
                data: [{
                    type: "line",
                    dataPoints: this.data_queue
                }]
            });
        },

        setup: function(model) {
            this.model = model;
            this.chart = this.mk_chart('cpu-graph');
            this.chart.render();

            var self = this;

            setInterval(function() { self.redraw(); }, this.update_interval);
        },

        redraw: function() {
            this.chart.render();
        },

        add_point: function(p) {
            // this.chart.options.title.text = this.title + '(' + e['originating-machine'] + ')';
            this.data_queue.push(p);

            if (this.data_queue.length >= this.max_data_size) {
                this.data_queue.shift();
            }
        },

        update: function(/*args*/) {
            if (arguments[0] !== 'metrics') {
                return;
            }

            var e = arguments[1];

            this.add_point({ x: (e.timestamp - Date.now()) / 1000, y: e.jvm['cpu-load'], 'originating-machine': e['originating-machine'] });
        }
    };

    extend(graphView).with(Observable);

    return {
        eventsView: eventsView,

        graphView: graphView
    };
});
