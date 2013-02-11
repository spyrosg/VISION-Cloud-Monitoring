
;"use strict";

function $(id) {
    return document.getElementById(id);
}

function log(s) {
    console.log('INFO ' + '[' + new Date().toISOString() + '] ' + s);
}

function main(fn) {
    document.addEventListener('DOMContentLoaded', fn, false);
}

function new_elem(tag, text) {
    var e = document.createElement(tag);

    if (text)
        e.innerText = text;

    return e;
}

function ajax(method, url, type, body) {
    var deferred = Q.defer();
    var req = new XMLHttpRequest();

    req.onreadystatechange = function() {
        if (req.readyState == 4)
            if (req.status == 200)
                deferred.resolve(req.responseText);
            else
                deferred.reject(new Error('ajax call failed, reason: ' + req.statusText));
    };
    req.open(method, url, true);
    req.setRequestHeader('Content-Type', type || 'application/json');
    req.send(body || null);

    return deferred.promise;
}

['GET', 'PUT', 'POST', 'DELETE'].forEach(function(method) {
    window[method.toLowerCase()] = function(url, type, body) {
        return ajax(method, url, type, body);
    };
});

