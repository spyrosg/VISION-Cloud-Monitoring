/* global define */

// Some dom helpers.
define([], function() {
    'use strict';

    return {
        $: function(sel) { return document.querySelector(sel); },
        id: function(sel) { return document.getElementById(sel); },
        on: function(type, el, fn) { el.addEventListener(type, fn, false); },
        off: function(type, el, fn) { el.removeEventListener(type, fn, false); },
        creat: function(el) { return document.createElement(el); },
        text: function(str) { return document.createTextNode(str); },
        frag: function() { return document.createDocumentFragment(); },
        empty: function(el) { el.innerHTML = ''; }
    };
});
