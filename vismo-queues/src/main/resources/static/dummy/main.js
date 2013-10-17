/* global requirejs */
/* jshint devel: true */


requirejs.config({
    baseUrl: '.',
    urlArgs: "bust=" + (new Date()).getTime(),
    paths: {
        when: 'when',
        dom:  'dom',
        ajax: 'ajax'
    },
});


// Boot the application.
requirejs(['when', 'dom', 'ajax'], function(when, dom, ajax) {
    'use strict';

    var cdmiQueuesModel = {
        service: {
            root_server: 'http://5.255.144.199:9997/api/queues',

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
                return ajax(this.root_server, 'GET', this.headers).then(JSON.parse);
            }
        },

        set_service_root: function(ip) {
            this.service.root_server = 'http://' + ip + '/api/queues';
        },

        get_list_for: function(topic) {
            return this.service
                .list()
                .then(function(queue_list) {
                    console.log('there are', queue_list.length, 'queues');

                    var i, len = queue_list.length, queue;

                    for (i = 0; i < len; ++i) {
                        queue = queue_list[i];

                        console.log("topic", queue.metadata.topic);

                        if (queue.metadata.topic === topic) {
                            console.log('got queue', queue.objectName);
                            return queue.objectName;
                        }
                    }

                    return null;
                });
        },

        alert: function(msg) {
            dom.id('main').textContent = msg;
        },

        create_queue: function(name, topic) {
            return this.service.create(name, topic);
        },

        hash: function(s) {
            return Math.abs(Array.prototype.reduce.call(s, function(hash, y) {
                var c = y.charCodeAt(0);

                return (((hash << 5) - hash) + c) | 0;
            }, 0));
        },

        storlet_list: [],

        find_storlet_by: function(name) {
            var i, len = this.storlet_list.length, storlet;

            for (i = 0; i < len; ++i) {
                storlet = this.storlet_list[i];

                if (storlet.name === name)
                    return storlet.div;
            }

            return null;
        },

        render_storlet: function() {
            var div = dom.creat('div'),
                name = dom.creat('div'),
                progress = dom.creat('progress');

            // div.setAttribute('id', id);

            name.setAttribute('class', 'name');
            name.textContent = 'no storlets running';

            progress.setAttribute('class', 'count');
            progress.setAttribute('value', 0);
            progress.setAttribute('max', 100);

            div.appendChild(name);
            div.appendChild(progress);

            return div;
        },

        render: function(eventList) {
            var self = this,
                main = dom.id('main');

            eventList.forEach(function(e) {
                self.known_events[e.timestamp || e.id] = true;

                var count = parseInt(e.progress, 10),
                    name = e.tenantID + '.' + e.containerID + '.' + e.storlet_name,
                    storlet = self.find_storlet_by(name);

                if (storlet === null) {
                    storlet = self.render_storlet();
                    self.storlet_list.push({ name: name, div: storlet });

                    if (main.children.length === 0) {
                        main.appendChild(storlet);
                    } else {
                        main.insertBefore(storlet, main.firstChild);
                    }
                }

                storlet.getElementsByClassName('name')[0].textContent = name;
                storlet.getElementsByClassName('count')[0].setAttribute('value', count);
            });
        },

        prepare_read_queue: function(name) {
            var self = this;

            setInterval(function() {
                cdmiQueuesModel.service
                    .read(name)
                    .then(function(eventList) {
                        self.render(eventList.filter(function(e) { return !self.is_known_event(e); }));
                        console.log('no of events:', eventList.length);
                    });
            }, 500);
        },

        known_events: {},

        is_known_event: function(e) {
            if ('timestamp' in e && e.timestamp in this.known_events) {
                return true;
            }
            if ('id' in e && e.id in this.known_events) {
                return true;
            }

            return false;
        },
    };

    window.addEventListener('message', function(evt) {
        if (evt.origin !== window.location.origin) {
            return;
        }

        var TOPIC = 'storletProgress';

        console.log('got from parent', evt.data);
        cdmiQueuesModel.set_service_root(evt.data);
        cdmiQueuesModel
            .get_list_for(TOPIC)
            .then(function(queue_name) {
                if (queue_name !== null) {
                    console.log('queue already exists:', queue_name);
                    cdmiQueuesModel.prepare_read_queue(queue_name);
                } else {
                    var default_queue_name = 'x-ft-ntua-' + (new Date()).getTime();

                    cdmiQueuesModel
                        .create_queue(default_queue_name, TOPIC)
                        .then(function(ok) {
                            console.log('queue created:', default_queue_name);
                            cdmiQueuesModel.prepare_read_queue(default_queue_name);
                        },
                        function(req) {
                            cdmiQueuesModel.alert('functionality not supported');
                            console.error('error creating queue:', req.statusText + ', ' + req.responseText);
                        });
                }
            });
    }, false);

    window.parent.postMessage(null, window.location.origin);
});

