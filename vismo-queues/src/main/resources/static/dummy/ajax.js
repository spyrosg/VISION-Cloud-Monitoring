/* global define */

// An promises based ajax request.
define(['when'], function(when) {
    'use strict';

    // Determine if an XMLHttpRequest was successful
    // Some versions of WebKit return 0 for successful file:// URLs
    function xhrSuccess(req) {
        return (req.status <= 307 || (req.status === 0 && req.responseText));
    }

    // Due to crazy variabile availability of new and old XHR APIs across
    // platforms, this implementation registers every known name for the event
    // listeners. The promise library ascertains that the returned promise
    // is resolved only by the first event.
    // http://dl.dropbox.com/u/131998/yui/misc/get/browser-capabilities.html
    return function(url, method, headers, data) {
        if (url.match(/^file:.*/i)) {
            throw new Error("XHR does not function for file: protocol");
        }

        if (!method) {
            method = 'GET';
        }

        var request = new XMLHttpRequest();
        var response = when.defer();

        function onload() {
            if (xhrSuccess(request)) {
                response.resolve(request.responseText);
            } else {
                onerror();
            }
        }

        function onerror() {
            response.reject(request);
        }

        try {
            request.open(method, url, true);

            Object.getOwnPropertyNames(headers).forEach(function(name) {
                request.setRequestHeader(name, headers[name]);
            });

            if (request.overrideMimeType) {
                request.overrideMimeType('application/json');
            }
            request.onreadystatechange = function () {
                if (request.readyState === 4) {
                    onload();
                }
            };
            request.onload = request.load = onload;
            request.onerror = request.error = onerror;
        } catch (exception) {
            response.reject(exception.message, exception);
        }

        if (data) {
            request.send(data);
        } else {
            request.send();
        }

        return response.promise;
    };
});
