#!/usr/bin/python2
# vim: set fileencoding=utf-8

from __future__ import print_function

import socket, struct, fcntl, sys
from os import getpid, getenv
from time import time
from pyjavaproperties import Properties
from uuid import uuid4
import zmq
import json


## this is the file that holds the lib's configuration
CONFIGURATION_PROPERTIES = getenv('VISMO_CONFIG', '/etc/visioncloud_vismo.conf')

##
## Utilities
##

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

##
## core
##

class EventDispatcher(object):
    """
        An event dispatcher is used to pass events down a socket.
        An event is just a collection of key/value pairs (a python dict).
    """

    def __init__(self, service_name, sock):
        self.service_name = service_name
        self.sock = sock

    def _send(self, event):
        """Actually dispatch the event."""

        log('sending: {0}'.format(event))
        self.sock.send(json.dumps(event))


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
        self.events_received = []

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

    def send(self, **event):
        """
            This is the single public entry point to the object.
        """

        self.cleanup_event(event)
        self.add_basic_fields(event)
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

    def handle_event(self, event):
        """
            This does most of the work. It accumulates the interesting events,
            and upon arrival of all events, makes some request calculations and
            sends the event to the main monitoring instance.
        """

        # has the event anything to do with request/response?
        if 'tag' not in event:
            self._send(event)
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

            self._send(event)
            self._send(main_event)
        else:
            # send anyway
            self._send(event)

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
    from time import sleep

    mon = VismoEventDispatcher('foo')

    def mon_send(operation, tag, tenant, user, container, obj):
        mon.send(status='SUCCESS', tag=tag, operation=operation, tenant=tenant, user=user, container=container, obj=obj)

    def send_put(tenant, user, container, obj):
        mon_send('PUT', 'start-request', tenant, user, container, obj)
        mon_send('PUT', 'start-response', tenant, user, container, obj)
        mon_send('PUT', 'end-response', tenant, user, container, obj)

    def send_get():
        mon_send('GET', 'start-request', tenant, user, container, obj)
        mon_send('GET', 'start-response', tenant, user, container, obj)
        mon_send('GET', 'end-response', tenant, user, container, obj)

    ctr = 0

    for i in range(int(sys.argv[1])):
        send_put('ntua', 'vassilis', 'test-container', 'foo')

#{"status": "SUCCESS", "container": "events", "timestamp": 1361897370880, "object": "event-1361897364933", "transaction-duration": 5.4139999999999997, "content-size": 733, "originating-machine": "10.0.1.101", "user": "admin", "transaction-latency": 5.399, "transaction-throughput": 135.38973032877726, "originating-cluster": "test", "operation": "PUT", "type": "write", "id": "53638f2f-3c0a-4711-8ab1-01b836337f2c", "originating-service": "object_service", "tenant": "analytics"}
