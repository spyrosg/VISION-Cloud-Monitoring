
;"use strict";

function parse(s) { return JSON.parse(s); }

function date_to_human(dt) {
    var now = Date.now();
    var diff = (now - dt) / 1000;

    if (diff > 3600)
        return parseInt(diff / 3600, 10) + "hr ago";
    if (diff > 60)
        return parseInt(diff / 60, 10) + "min ago";
    if (diff > 1)
        return parseInt(diff, 10) + "sec ago";

    return "now";
}

function format(member) {
    return member.addr + ', last updated ' + date_to_human(member.lastUpdated);
}

function clear(dom_node) {
    dom_node.innerText = null;
}

function display_member_list(members) {
    var live_members = $('nodes');

    clear(live_members);

    members.forEach(function(member) {
        var m = new_elem('li');

        m.innerText = format(member);
        live_members.appendChild(m);
    });
}

function show_members_using(url) {
    get(url)
        .then(parse)
        .then(display_member_list, function(err) {
            console.log(err);
        });
}

main(function() {
    var UPDATING_EVERY = 5000; // 5 seconds

    show_members_using('/api/members');
    setInterval(function() { show_members_using('/api/members'); }, UPDATING_EVERY);
});
