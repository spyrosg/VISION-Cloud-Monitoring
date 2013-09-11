/* global define */
/* jshint devel: true */


// the views used in the app
define(['dom', 'util'], function(dom, util) {
    'use strict';

    var extend = util.extend,
        Observable = util.Observable;

    var queuesView = {
        el: dom.$('#queues'),

        setup: function(domain) {
            this.domain = domain;
        },

        update: function(/*args*/) {
            if (arguments[0] !== 'queue') {
                return;
            }

            this.add(arguments[1]);
        },

        add: function(name) {
            var span = dom.creat('span');

            if (this.el.children.length === 0) {
                span.textContent = name;
            } else {
                span.textContent = ', ' + name;
            }

            this.el.appendChild(span);
        }
    };

    extend(queuesView).with(Observable);

    var eventsView = {
        el: dom.$('#events ul'),

        known_events: {},

        setup: function(domain) {
            this.domain = domain;
            this.insert_into_list.el = this.el;
            this.insert_into_list.method = this.insert_into_list.append;
        },

        update: function(/*args*/) {
            if (arguments[0] !== 'events') {
                return;
            }

            var self = this,
                eventList = arguments[1];

            console.log('list with', eventList.length, 'events');

            if (eventList.length > 0) {
                eventList.
                    filter(function(e) {
                        return ('timestamp' in e) || ('id' in e);
                    }).
                    filter(function(e) {
                        if ('timestamp' in e) {
                            return !(e.timestamp in self.known_events);
                        }

                        return !(e.id in self.known_events);
                    }).
                    forEach(function(e) {
                        self.render(e);
                    });
            }
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

            this.known_events[e.timestamp || e.id] = true;
            delete e['originating-machine'];
            delete e['originating-service'];
            delete e['timestamp'];
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

    return {
        queuesView: queuesView,
        eventsView: eventsView
    };
});
