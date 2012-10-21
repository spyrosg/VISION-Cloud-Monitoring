// VISION - events
// Author: Efthymios Chondrogiannis
// Date: 16/10/2012

// A List with pair of elements
// The first element depict the time (e.g. 1, 2, etc) while the second one the total number of events (from the beginning)
var points1 = [];
var points2 = [];

// We assume that the system is running and we regularly count the requests received in a particular period of time
// finding those of oyr interest
function processEvents(divid) {
    // Initially draw the plot
    points1.push([0, 0]);
    points2.push([0, 0]);
    drawFigure(divid, points1, points2);
    // Test Data Index
    var testDataArrayIndex1 = 0;
    var testDataArrayIndex2 = 0;
    // Time
    var index = 1;
    // Total number of events of our interest
    var sum1 = 0;
    var sum2 = 0;
    var vismo_events_source = new EventSource('/events');
    var MAX_EVENT_LIMIT = 100;

    vismo_events_source.onopen = function () {
        console.log('opened stream, receiving events');
    };

    vismo_events_source.onmessage = function(e) {
        var data = e.data;

        console.log('current # events: ' + index);
        if (index >= MAX_EVENT_LIMIT) {
            delete e;
            return;
        }

        if (is_obs_service_event(data))
            points1.push([index, ++sum1]);
        if (is_accounting_event(data))
            points2.push([index, ++sum2]);

        ++index;
        drawFigure(divid, points1, points2);
        delete e;
        //console.log('received: ' + data);
    };
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
    } );
}

function is_obs_service_event(ev) {
    return ev.indexOf('transaction-throughput') >= 0;
}

function is_accounting_event(ev) {
    return ev.indexOf('Accounting') >= 0;
}
