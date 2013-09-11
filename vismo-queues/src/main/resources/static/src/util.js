/* global define */
/* jshint devel: true */

define([], function() {
    'use strict';

    var Observer = {
        add: function(observable) {
            this.observables.push(observable);
        },

        remove: function(observable) {
            throw 'niy';
        },

        notify: function(/*args*/) {
            var args = Array.slice(arguments);

            console.debug('notifying', args);

            this.observables.forEach(function(obs) {
                obs.update.apply(obs, args);
            });
        }
    };

    var Observable = {
        update: function(/*args*/) {
            throw 'implement me';
        }
    };

    return {
        Observer: Observer,

        Observable: Observable,

        extend: function(dest) {
            return {
                'with': function(src) {
                    Object
                        .getOwnPropertyNames(src)
                        .filter(function(name) {
                            return !(name in dest);
                        }).
                        forEach(function(name) {
                            dest[name] = src[name];
                        });

                    return dest;
                }
            };
        }
    };
});
