/* global requirejs */

requirejs.config({
    baseUrl: '.',
    paths: {
        'login': 'lib/login'
    }
});

requirejs(['login'], function(login) {
    'use strict';

    var submit = document.getElementById('submit');
    var username = document.getElementById('username');
    var password = document.getElementById('password');

    submit.addEventListener('click', function(e) {
        console.log('name', username.value);
        e.preventDefault();
    }, false);
});
