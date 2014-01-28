/* global define */
/* jshint devel: true */


// the views used in the app
define(['dom', 'util', 'ctrls', 'canvasjs', 'bqueue'], function(dom, util, ctrls, CanvasJS, bqueue) {
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

    function mk_chart1(elem_name, title, data) {
        return new CanvasJS.Chart(elem_name, {
            title: { text: title },
            axisX: { title: 'time (sec)', valueFormatString: 'HH:mm:ss'},
            axisY: { interval: 1, minimum: 0, maximum: 4 },
            data: [{ type: 'line', dataPoints: data }]
        });
    }

    function mk_chart2(elem_name, title, data) {
        return new CanvasJS.Chart(elem_name, {
            title: { text: title },
            axisX: { title: 'time (sec)', valueFormatString: 'HH:mm:ss'},
            axisY: { title: '%', interval: 20, minimum: 0, maximum: 100 },
            data: [{ type: 'area', color: "rgba(255, 244, 32, 0.95)", dataPoints: data }]
        });
    }

    function mk_chart3(elem_name, title, data_in, data_out) {
        return new CanvasJS.Chart(elem_name, {
            title: { text: title },
            axisX: { title: 'time (sec)', valueFormatString: 'HH:mm:ss'},
            axisY: { title: 'KB' },
            data: [{ type: 'line', color: "rgb(12, 255, 32)", dataPoints: data_in }, { type: 'line', color: "rgb(255, 33, 45)", dataPoints: data_out }]
        });
    }

    var baseChart = {
        update_interval: 500, // in ms

        default_max_data_size: 20,

        setup: function(mk_chart) {
            this.data_queue = bqueue.new(this.default_max_data_size);
            this.chart = mk_chart(this.elem_name, this.title, this.data_queue.data);
            this.chart.render();

            var self = this;

            setInterval(function() { self.redraw(); }, this.update_interval);
        },

        redraw: function() {
            this.chart.render();
        },

        add_point: function(p) {
            this.data_queue.push(p);
        }
    };

    var cpuLoadChart = {
        elem_name: 'cpu-graph',

        title: 'cpu load',

        update: function(e) {
            var val = e['cpu-load'];

            this.add_point({ x: new Date(e.timestamp), y: val, 'originating-machine': e['originating-machine'] });
        }
    };

    extend(cpuLoadChart).with(baseChart);

    var memoryUsageChart = {
        update_interval: 620, // in ms

        elem_name: 'memory-graph',

        title: 'memory usage',

        update: function(e) {
            var val = parseFloat((100 * e['memory-used'] / e['memory-total']).toFixed(2));

            this.add_point({ x: new Date(e.timestamp), y: val, 'originating-machine': e['originating-machine'] });
        }
    };

    extend(memoryUsageChart).with(baseChart);

    var bandwidthUsageChart = {
        update_interval: 380, // in ms

        elem_name: 'bandwidth-graph',

        title: 'bandwidth',

        prev_inb: -1,
        prev_outb: -1,

        toKB: function(x, y) {
            return (y - x) / 1024;
        },

        add_inb: function(x) {
            this.data_queue_in.push(x);
        },

        add_outb: function(x) {
            this.data_queue_out.push(x);
        },

        update: function(e) {
            var inbound = e.inbound;
            var outbound = e.outbound;

            if (this.prev_inb !== -1) {
                this.add_inb({ x: new Date(e.timestamp), y: this.toKB(this.prev_inb, inbound) });
                this.add_outb({ x: new Date(e.timestamp), y: this.toKB(this.prev_outb, outbound) });
            } else {
                this.add_inb({ x: new Date(e.timestamp), y: 0 });
                this.add_outb({ x: new Date(e.timestamp), y: 0 });
            }

            this.prev_inb = inbound;
            this.prev_outb = outbound;
        }
    };

    extend(bandwidthUsageChart).with(baseChart);

    bandwidthUsageChart.setup = function(mk_chart) {
        this.data_queue_in = bqueue.new(this.default_max_data_size);
        this.data_queue_out = bqueue.new(this.default_max_data_size);
        this.chart = mk_chart(this.elem_name, this.title, this.data_queue_in.data, this.data_queue_out.data);
        this.chart.render();

        var self = this;

        setInterval(function() { self.redraw(); }, this.update_interval);
    };

    // this object decides which view gets to show which events
    var selectView = {
        originating_machine: null,

        setup: function(model) {
            this.model = model;
            cpuLoadChart.setup(mk_chart1);
            memoryUsageChart.setup(mk_chart2);
            bandwidthUsageChart.setup(mk_chart3);
            defaultView.setup();
        },

        update: function(/*args*/) {
            var topic = arguments[0],
                event = arguments[1];

            if (this.originating_machine === null) {
                this.originating_machine = e['originating-machine'];
            }
            if (this.originating_machine !== e['originating-machine']) {
                return; // ignore this event
            }

            if ('select_' + topic in this) {
                this['select_' + topic](event);
            } else {
                console.log('selectView#update');
                this.default_view(event);
            }
        },

        select_metrics: function(e) {
            cpuLoadChart.update(e);
            memoryUsageChart.update(e);
            bandwidthUsageChart.update(e);
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
