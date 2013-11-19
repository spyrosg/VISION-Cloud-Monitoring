/* global define */
/* jshint devel: true */

// the views used in the app
define(['dom'], function(dom) {
    'use strict';

    var updateButtonController = {
        el: dom.id('update-period'),

        setup: function(model) {
            this.model = model;
            this.register_on_click();
        },

        get_period: function() {
            return this.el['period'].value;
        },

        register_on_click: function() {
            var self = this;

            dom.on('submit', this.el, function(evt) {
                evt.preventDefault();
                self.model.update_rule_period_to(self.get_period());
            });
        }
    };

    return {
        updateButtonController: updateButtonController
    };
});
