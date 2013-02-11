
;"use strict";

function parse(s) { return JSON.parse(s); }

function date_to_human(dt) {
    var now = Date.now();
    var diff = (now - dt) / 1000;

    if (diff > 3600)
        return parseInt(diff / 3600, 10) + " hr ago";
    if (diff > 60)
        return parseInt(diff / 60, 10) +  "min ago";
    if (diff > 1)
        return parseInt(diff, 10) + " sec ago";

    return "now";
}

function format(member) {
    return member.addr + ', last updated ' + date_to_human(member.lastUpdated);
}

function clear_table(tab) {
    var trs = tab.getElementsByTagName('tr');

    // remove all but first row
    for (var i = trs.length - 1; i > 0; --i)
        tab.removeChild(trs[i]);
}

function display_member_list(members) {
    var live_members = $('nodes');

    clear_table(live_members);

    members.forEach(function(member) {
        var row = new_elem('tr');
        var ip = new_elem('td', member.addr);
        var last_updated = new_elem('td', date_to_human(member.lastUpdated));
        var version = new_elem('td', '#TODO');

        row.appendChild(ip);
        row.appendChild(last_updated);
        row.appendChild(version);
        live_members.appendChild(row);
    });
}

function show_members_using(url) {
    get(url)
        .then(parse)
        .then(display_member_list, function(err) {
            console.log('my error: ' + err);
        });
}

main(function() {
    var UPDATING_EVERY = 5000; // 5 seconds

    show_members_using('/api/members');
    setInterval(function() { show_members_using('/api/members'); }, UPDATING_EVERY);
});
