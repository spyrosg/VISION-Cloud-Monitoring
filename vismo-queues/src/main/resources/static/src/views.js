/* global define */
/* jshint devel: true */


// the views used in the app
define(['dom', 'util', 'ctrls', 'canvasjs'], function(dom, util, ctrls, CanvasJS) {
    'use strict';

    var extend = util.extend,
        Observable = util.Observable;

    var versionView = {
        el: dom.$('#info .version'),

        is_set: false,

        setup: function(model) {
            this.model = model;
        },

        update: function(version) {
            if (!this.is_set) {
                this.is_set = true;

                try {
                    console.log('versionView#update');
                    this.el.textContent = version;
                } catch (e1) {
                } finally {
                    // this.model.remove(this);
                }
            }
        }
    };

    extend(versionView).with(Observable);

    var defaultView = {
        el: dom.$('#events ul'),

        setup: function() {
            this.insert_into_list.el = this.el;
            this.insert_into_list.method = this.insert_into_list.append;
        },

        update: function(e) {
            console.log('defaultView#update');
            this.render(e);
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

    var metricsView = {
        el: dom.id('cpu-graph'),

        update_interval: 500, // 1000ms = 1sec

        title: 'cpu load',

        data_queue: [],

        max_data_size: 20,

        mk_chart: function(elem_id) {
            return new CanvasJS.Chart('cpu-graph', {
                title: { text: this.title },
                axisX: { title: 'time (sec)', interval: 5, valueFormatString: 'HH:mm:ss'},
                axisY: { interval: 1, minimum: 0, maximum: 4 },
                data: [{ type: 'line', dataPoints: this.data_queue }]
            });
        },

        setup: function() {
            this.chart = this.mk_chart('cpu-graph');
            this.chart.render();

            var self = this;

            setInterval(function() { self.redraw(); }, this.update_interval);
        },

        redraw: function() {
            this.chart.render();
        },

        add_point: function(p) {
            this.chart.options.title.text = this.title + ' @ ' + p['originating-machine'];
            this.data_queue.push(p);

            if (this.data_queue.length >= this.max_data_size + 1) {
                this.data_queue.shift();
            }
        },

        update: function(e) {
            var dt = (e.timestamp - Date.now()) / 1000;
            var val = e['cpu-load'];

            this.add_point({ x: new Date(e.timestamp), y: val, 'originating-machine': e['originating-machine'] });
        }
    };

    // this object decides which view gets to show which events
    var selectView = {
        setup: function(model) {
            this.model = model;
            metricsView.setup();
            defaultView.setup();
        },

        update: function(/*args*/) {
            var topic = arguments[0],
                event = arguments[1];

            if ('select_' + topic in this) {
                this['select_' + topic](event);
            } else {
                console.log('selectView#update');
                this.default_view(event);
            }
        },

        select_metrics: function(e) {
            metricsView.update(e);
        },

        default_view: function(e) {
            defaultView.update(e);
        }
    };

    extend(selectView).with(Observable);

    return {
        versionView: versionView,

        selectView: selectView
    };
});
