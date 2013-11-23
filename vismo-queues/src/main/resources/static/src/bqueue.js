/* global define */
/* jshint devel: true */


// a bounded queue data structure: drops the oldest element
// when max size is exceeded.
define([], function() {
    'use strict';

    return {
        max_data_size: -1,
        data: null,

        setup: function(max_data_size) {
            this.max_data_size = max_data_size;
            this.data = [];
        },

        push: function(e) {
            this.data.push(e);

            if (this.data.length >= this.max_data_size + 1) {
                this.data.shift();
            }
        },

        new: function(max_data_size) {
            var q = Object.create(this);

            q.max_data_size = max_data_size;
            q.data = [];

            return q;
        }
    };
});
