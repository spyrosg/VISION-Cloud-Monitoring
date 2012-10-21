
function log1(level, msg) {
    console.log(level + ' [' + new Date().toISOString() + ']: ' + msg);
}

['trace', 'debug', 'warn', 'error'].forEach(function(level) {
    window[level] = function(msg) {
        log1(level.toUpperCase(), msg);
    };
});

var vismo = (function() {
    var divid = 'event-plot';

    return {
        main: function() {
            var vismo_events_source = new EventSource('/api/events');
            var points1 = [[0, 0]];
            var points2 = [[0, 0]];
            // Total number of events of our interest
            var sum1 = 0;
            var sum2 = 0;
            var MAX_EVENT_LIMIT = 100;
            // Time
            var index1 = 1,
                index2 = 1;

            drawFigure(divid, points1, points2);

            vismo_events_source.onopen = function () {
                debug('opened stream, receiving events');
            };

            vismo_events_source.onmessage = function(e) {
                var data = e.data;

                if (index1 >= MAX_EVENT_LIMIT || index2 >= MAX_EVENT_LIMIT) {
                    delete e;
                    return;
                }

                if (is_obs_service_event(data)) {
                    console.log('have obs event');
                    points1.push([++index1, ++sum1]);
                }
                if (is_accounting_event(data)) {
                    console.log('have accounting event');
                    points2.push([++index2, ++sum2]);
                }

                drawFigure(divid, points1, points2);
                delete e;
            };
        },
    };
})();


function is_obs_service_event(ev) {
    return ev.indexOf('transaction-throughput') >= 0;
}

function is_accounting_event(ev) {
    return ev.indexOf('Accounting') >= 0;
}

function setup_header() {
    $.get('/api/ip', function(ip) {
        $('h2').append(' ').append('(' + ip + ')');
    });
}

// Draw the plot (points) in the div with the id given
function drawFigure(divid, points1, points2) {
    $('#' + divid).empty();
    var plot = $.jqplot (divid, [points1,points2], {
        title: 'Graphical representation of Events',
        axesDefaults: {
            labelRenderer: $.jqplot.CanvasAxisLabelRenderer
        },
        axes: {
            xaxis: {
                label: "Time",
                min:0,
                //max:20,
                numberTicks:21
            },
            yaxis: {
                label: "Count",
                min:0,
                //max:30
                numberTicks:31
            }
        },
        seriesDefaults: {
            lineWidth:5,
            shadow: true,
            showMarker:false,
        }
    });
}

