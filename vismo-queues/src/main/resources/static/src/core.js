/* global define */

// this is were the application is assembled,
// configured and started.
// Search for the ``main`` method.
define(['dom', 'ajax'], function(dom, ajax) {
    'use strict';

    var cdmi_queues = {
        root_server: '/api/queues',

        create: function(name, topic) {
            return ajax(this.root_server + '/' + name + '/' + topic, 'PUT', {
                'Accept': 'application/cdmi-queue',
                'Content-Type': 'application/cdmi-queue',
                'X-CDMI-Specification-Version': '1.0.2'
            }).then(JSON.parse);
        },
    };

    var eventList = {
        parent: null,
        el: null,

        setup: function(parent) {
            this.parent = parent;
            this.el = dom.creat('ul');
            this.parent.appendChild(this.el);
        },

        append: function(evt) {
            var el = dom.frag();
        },

        render: function() {
        },

        empty: function() {
            dom.empty(this.el);
        }
    };

    return {
        main: function() {
            console.debug('app started');

            cdmi_queues.create('foo', '*').then(function(data) {
                var container = dom.id('main');

                console.debug('queue "foo" created');
                eventList.setup(container);
            }).done();
        }
    };
});
