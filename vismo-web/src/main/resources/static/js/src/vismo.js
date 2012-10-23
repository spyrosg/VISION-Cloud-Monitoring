
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
            var points3 = [[0, 0]];
            // Total number of events of our interest
            var sum1 = 0;
            var sum2 = 0;
            var sum3 = 0;
            var MAX_EVENT_LIMIT = 100;
            var index1 = 1,
                index2 = 2,
                index3 = 3;

            draw_barcharts(divid, points1, points2);

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
                    points1.push([index1, ++sum1]);
                }
                if (is_accounting_event(data)) {
                    console.log('have accounting event');
                    points2.push([index2, ++sum2]);
                }
                if (is_cto_event(data)) {
                    console.log('have cto event');
                    points3.push([index3, ++sum3]);
                }

                draw_barcharts(divid, points1, points2, points3);
                delete e;
                delete data;
            };
        },
    };
})();


function is_obs_service_event(ev) {
    return ev.indexOf('obs') >= 0;
}

function is_accounting_event(ev) {
    return ev.indexOf('Accounting') >= 0;
}

function is_cto_event(ev) {
    return ev.indexOf('cto-') >= 0;
}

// Draw the plot (points) in the div with the id given
function draw_figure(divid, points1, points2) {
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
                numberTicks:21
                }
        },
        seriesDefaults: {
            lineWidth:5,
            shadow: true,
            showMarker:false,
        }
    });
}

function draw_barcharts(div_id, s1, s2, s3) {
    $('#' + div_id).empty();

    var ticks = ['all events', 'accounting events', 'cto events'];

    var plot1 = $.jqplot(div_id, [s1, s2, s3], {
        // The "seriesDefaults" option is an options object that will
        // be applied to all series in the chart.
        seriesDefaults: {
            renderer:$.jqplot.BarRenderer,
            rendererOptions: { fillToZero: true }
        },
        // Custom labels for the series are specified with the "label"
        // option on the series option.  Here a series option object
        // is specified for each series.
        series:[
            { label: 'all events' },
            { label: 'accounting events' },
            { label: 'cto events' },
        ],
        // Show the legend and put it outside the grid, but inside the
        // plot container, shrinking the grid to accomodate the legend.
        // A value of "outside" would not shrink the grid and allow
        // the legend to overflow the container.
        legend: {
            show: true,
            placement: 'outsideGrid'
        },
        axes: {
             xaxis: {
                renderer: $.jqplot.CategoryAxisRenderer,
                ticks: ticks
            },
            // Pad the y axis just a little so bars can get close to, but
            // not touch, the grid boundaries.  1.2 is the default padding.
            yaxis: {
                pad: 1.05,
                tickOptions: { formatString: '%d' }
            }
        }
    });
}
