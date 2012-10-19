#!/usr/bin/python2
# vim: set fileencoding=utf-8

from __future__ import print_function

import socket, struct, fcntl, sys
from os import getpid
from time import time
from pyjavaproperties import Properties
from uuid import uuid4
import zmq
import json


# this is the file that holds the lib's configuration
CONFIGURATION_PROPERTIES = '/srv/vismo/config.properties'
#CONFIGURATION_PROPERTIES = 'config.properties'


###
##
## Utilities
##
###

def get_public_ip(iface='eth0'):
    """
        Return a tuple whose first element contains the first non loopback interface
        and the second elements is the first not inet6 address of that interface.
    """

    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    sockfd = sock.fileno()
    SIOCGIFADDR = 0x8915
    ifreq = struct.pack('16sH14s', iface, socket.AF_INET, '\x00'*14)

    try:
        res = fcntl.ioctl(sockfd, SIOCGIFADDR, ifreq)
    except:
        return None
    finally:
        sock.close()

    ip = struct.unpack('16sH2x4s8x', res)[2]

    return socket.inet_ntoa(ip)

def is_effectively_zero(n):
    return abs(n) <= 5e-9

def log(msg):
    print('vismo-local-event-dispatcher: {0}'.format(msg), file=sys.stderr)

def log_and_raise(msg, excp):
    print('vismo-local-event-dispatcher: {0}'.format(msg), file=sys.stderr)
    raise excp(msg)

_MAX_LENGTH = 80
def safe_repr(obj, short=False):
    try:
        result = repr(obj)
    except Exception:
        result = object.__repr__(obj)
    if not short or len(result) < _MAX_LENGTH:
        return result
    return result[:_MAX_LENGTH] + ' [truncated]...'



## core

class EventDispatcher(object):
    """
        An event dispatcher is used to pass around events to
        interested parties.
    """

    def __init__(self, service_name, sock):
        self.service_name = service_name
        self.sock = sock


    def send(self, **event):
        """Actually dispatch the event."""

        self.sock.send(event)


    def calculate_event_time_difference(self, e1, e2):
        """
            Calculates the time difference of the two events. It is assumed
            that e1 ``happens before'' e2, so that a positive difference is
            returned.

            It is also assumed that the event timestamps are in millis and
            the returned value is in seconds.
        """

        return (float(e2['timestamp']) - float(e1['timestamp'])) / 1000.0


    def calculate_mean_value_per_time_unit(self, val, time_diff):
        if is_effectively_zero(time_diff):
            return 0.0

        return float(val) / float(time_diff)


class VismoEventDispatcher(EventDispatcher):
    """
        This the bridge that handles the event generation code (the caller
        of this library) with the event distribution code (the main
        monitoring instance). This instance talks directly to the locally
        running vismo instance.

        The services of this library are called by the Object Service on
        each request/response cycle. Since each request in object service
        is handled by a different process, we know that each event belongs
        to the same request/response.
    """


    def __init__(self, service_name):
        self.load_configuration()
        sock = self.create_push_socket(self.producers_point)
        super(VismoEventDispatcher, self).__init__(service_name, sock)
        self.start_request_event = None
        self.start_response_event = None
        self.end_response_event = None

    def load_configuration(self):
        p = Properties()
        p.load(open(CONFIGURATION_PROPERTIES))
        self.producers_point = p['producers.point']
        # FIXME: auto acquire the name according to the machine's ip
        self.cluster_name = 'test'
        self.iface = 'eth0'
        self.ip = get_public_ip(self.iface)

    def create_push_socket(self, end_point):
        ctx = zmq.Context()
        sock = ctx.socket(zmq.PUSH)
        sock.setsockopt(zmq.LINGER, 0)
        sock.connect(end_point)
        log('connecting to endpoint={0}'.format(end_point))

        return sock


    def send(self, **event):
        """
            This is the single public entry point to the object.
        """

        self.cleanup_event(event)
        self.add_basic_fields(event)
        log('sending: {0}'.format(event))
        self.handle_event(event)


    def cleanup_event(self, event):
        """
            For consistency reasons, use dashes and full nouns.
        """

        def object_service_topic_to_http_op(topic):
            if topic == 'reads':
                return 'GET'
            elif topic == 'writes':
                return 'PUT'
            elif topic == 'deletes':
                return 'DELETE'
            else:
                log('got unknown topic: {0}'.format(topic))
                return topic

        if 'content_size' in event:
            content_size = event['content_size']
            del event['content_size']

            if content_size is None:
                content_size = 0
            if isinstance(content_size, basestring):
                content_size = long(content_size)

            event['content-size'] = content_size
        if 'obj' in event:
            event['object'] = event['obj']
            del event['obj']
        if 'topic' in event:
            event['operation'] = object_service_topic_to_http_op(event['topic'])
            del event['topic']


    def add_basic_fields(self, event):
        event['timestamp'] = int(1000 * time())
        event['originating-machine'] = self.ip
        event['originating-service'] = self.service_name
        event['originating-cluster'] = self.cluster_name
        event['id'] = str(uuid4())

    def _sock_send(self, event):
        self.sock.send(json.dumps(event))


    def handle_event(self, event):
        """
            This does most of the work. It accumulates the interesting events,
            and upon arrival of all events, makes some request calculations and
            sends the event to the main monitoring instance.
        """

        # has the event anything to do with request/response?
        if 'tag' not in event:
            self._sock_send(event)
            return

        tag = event['tag']

        if tag == 'start-request':
            self.start_request_event = event
        elif tag == 'start-response':
            self.start_response_event = event
        elif tag == 'end-response':
            self.end_response_event = event
        else:
            log_and_raise('handed event with incomprehensible tag: {0}, event: {1}'.format(tag, event), ValueError)

        ## TODO: handle object services failures => availability
        # if we have all the events
        if self.start_request_event and self.start_response_event and self.end_response_event:
            main_event = dict(self.end_response_event)
            main_event['id'] = str(uuid4())

            main_event['transaction-latency'] = self.calculate_latency()
            main_event['transaction-duration'] = self.calculate_transaction_duration()

            if 'content-size' not in main_event or main_event['content-size'] is None:
                main_event['transaction-throughput'] = 0
            else:
                main_event['transaction-throughput'] = self.calculate_throughput(main_event['content-size'], main_event['transaction-duration'])
            # TODO: Calculate availability and other Niki's required stuff

            # not needed
            del main_event['tag']

            self._sock_send(event)
            self._sock_send(main_event)
        else:
            # send anyway
            self._sock_send(event)


    def calculate_latency(self):
        """
            Latency here is defined as the time duration from the time the system received
            the start request event till the time the system is ready to start serving the
            response, as seen by the start response event.
        """

        return self.calculate_event_time_difference(self.start_request_event, self.start_response_event)


    def calculate_transaction_duration(self):
        """
            Transaction duration is the time spent serving the request. More accurately, is the
            duration from the time the system received the start request event till the time the
            system received the end response event.
        """

        return self.calculate_event_time_difference(self.start_request_event, self.end_response_event)


    def calculate_throughput(self, content_size, transaction_time):
        """
            Throughput is defined as the the number of bytes served in the unit of time.
            Transaction size is measured in bytes, transaction_time in seconds.
            Output value is in bytes per second.
        """

        return self.calculate_mean_value_per_time_unit(content_size, transaction_time)





if __name__ == '__main__':
    import unittest
    from time import sleep

    class MyTestCase(unittest.TestCase):
        """
            This is used to reproduce to methods missing from
            python 2.6.6
        """

        def assertGreater(self, a, b, msg=None):
            """Just like self.assertTrue(a > b), but with a nicer default message."""
            if not a > b:
                standardMsg = '%s not greater than %s' % (safe_repr(a), safe_repr(b))
                self.fail(self._formatMessage(msg, standardMsg))

        def assertAlmostEqual(self, first, second, places=None, msg=None, delta=None):
            """Fail if the two objects are unequal as determined by their
               difference rounded to the given number of decimal places
               (default 7) and comparing to zero, or by comparing that the
               between the two objects is more than the given delta.

               Note that decimal places (from zero) are usually not the same
               as significant digits (measured from the most signficant digit).

               If the two objects compare equal then they will automatically
               compare almost equal.
            """
            if first == second:
                # shortcut
                return
            if delta is not None and places is not None:
                raise TypeError("specify delta or places not both")

            if delta is not None:
                if abs(first - second) <= delta:
                    return

                standardMsg = '%s != %s within %s delta' % (safe_repr(first),
                                                            safe_repr(second),
                                                            safe_repr(delta))
            else:
                if places is None:
                    places = 7

                if round(abs(second-first), places) == 0:
                    return

                standardMsg = '%s != %s within %r places' % (safe_repr(first),
                                                              safe_repr(second),
                                                              places)
            msg = self._formatMessage(msg, standardMsg)
            raise self.failureException(msg)


    class EventDispatcherTest(MyTestCase):
        def setUp(self):
            self.content_size = 1000 # in bytes
            self.obj = 'ofdesire'
            self.success_status = 'SUCCESS'
            self.fail_status = 'FAIL'
            self.time_till_start_of_response = 0.1 # in seconds
            self.time_till_end_of_response = 0.1 # in seconds
            self.delta = 0.005
            self.sent_events = []
            self.sock = object()
            def sock(): pass
            sock.send = lambda e: self.sent_events.append(e)
            self.dispatcher = EventDispatcher('foo', sock)

        def send_event(self, **args):
            args['timestamp'] = int(1000 * time())
            args['content-size'] = args['content_size']
            del args['content_size']
            self.dispatcher.send(**args)

        def send_start_request(self):
            self.send_event(tag='start-request', content_size=self.content_size, obj=self.obj, status=self.success_status)

        def send_start_response(self):
            self.send_event(tag='start-response', content_size=self.content_size, obj=self.obj, status=self.success_status)

        def send_end_response(self):
            self.send_event(tag='end-response', content_size=self.content_size, obj=self.obj, status=self.success_status)

        def perform_full_request_response_event_generation(self):
            self.send_start_request()
            sleep(self.time_till_start_of_response)
            self.send_start_response()
            sleep(self.time_till_end_of_response)
            self.send_end_response()


        def test_that_event_was_sent(self):
            self.send_event(tag='start-request', content_size=1000, obj='ofdesire', status=1)
            self.assertEquals(self.content_size, self.sent_events[0]['content-size'])


        def test_event_latency(self):
            self.perform_full_request_response_event_generation()

            latency = self.dispatcher.calculate_event_time_difference(self.sent_events[0], self.sent_events[1])
            self.assertGreater(latency, 0)
            self.assertAlmostEqual(self.time_till_start_of_response, latency, delta=self.delta)


        def test_event_throughput(self):
            self.perform_full_request_response_event_generation()

            throughput = self.dispatcher.calculate_mean_value_per_time_unit(self.sent_events[0]['content-size'], self.time_till_start_of_response + self.time_till_end_of_response)
            self.assertAlmostEqual(self.content_size / (self.time_till_start_of_response + self.time_till_end_of_response), throughput, delta=self.delta)


        def test_event_transaction_time(self):
            self.perform_full_request_response_event_generation()

            transaction_time = self.dispatcher.calculate_event_time_difference(self.sent_events[0], self.sent_events[2])
            self.assertGreater(transaction_time, 0)
            self.assertAlmostEqual(self.time_till_start_of_response + self.time_till_end_of_response, transaction_time, delta=self.delta)


    unittest.main()

    mon = VismoEventDispatcher('foo')
    mon.send(tag='start-request', obj='ofdesire', status=1, operation='GET')
    sleep(1)
    mon.send(tag='start-response', obj='ofdesire', status=2, operation='GET')
    sleep(1)
    mon.send(tag='end-response', content_size=1000, obj='ofdesire', status=3, operation='GET')

