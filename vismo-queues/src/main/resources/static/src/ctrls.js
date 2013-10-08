/* global define */
/* jshint devel: true */

// the views used in the app
define(['dom'], function(dom) {
    'use strict';

    var updateButtonController = {
        el: dom.id('queue-entry'),

        setup: function(model) {
            this.model = model;
            this.register_on_click();
        },

        get_name: function() {
            return this.el['queue'].value;
        },

        get_topic: function() {
            return this.el['topic'].value;
        },

        register_on_click: function() {
            var self = this;

            dom.on('submit', this.el, function(evt) {
                evt.preventDefault();
                self.model.create_queue(self.get_name(), self.get_topic());
            });
        }
    };

    return {
        updateButtonController: updateButtonController
    };
});
