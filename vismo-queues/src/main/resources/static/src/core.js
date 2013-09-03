/* global define */

// this is were the application is assembled,
// configured and started.
// Search for the ``main`` method.
define(['dom', 'ajax'], function(dom, ajax) {
    'use strict';


    var eventList = {
        parent: null,
        el: null,

        setup: function(parent) {
            this.parent = parent;
            this.el = dom.creat('ul');
            this.parent.appendChild(el);
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
            var container = dom.id('main');

            eventList.setup(container);
            console.debug('app started');
        }
    };
});
