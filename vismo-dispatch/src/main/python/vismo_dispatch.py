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
        self.start_request = None
        self.start_response = None
        self.end_response = None

    def load_configuration(self):
        p = Properties()
        p.load(open(CONFIGURATION_PROPERTIES))
        self.producers_point = p['producers.point']
        # FIXME: auto acquire the name according to the machine's ip
        self.cluster_name = socket.gethostname().split('-')[0]
        self.ip = get_public_ip()

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

        self.add_timestamp(event)
        self.handle_event(event)

    def add_timestamp(self, event):
        event['timestamp'] = int(1000 * time())

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

        log('received: {0}'.format(event))
        tag = event['tag']

        if tag == 'start-request':
            self.start_request = event
        elif tag == 'start-response':
            self.start_response = event
        elif tag == 'end-response':
            self.end_response = event

        if self.start_request and self.start_response and self.end_response:
            try:
                if 'content_size' in self.end_response:
                    event = dict(self.end_response)
                    event['id'] = str(uuid4())
                    event['timestamp'] = int(1000 * time())
                    event['originating-machine'] = self.ip
                    event['originating-service'] = self.service_name
                    event['originating-cluster'] = self.cluster_name
                    event['transaction-latency'] = self.get_latency()
                    event['transaction-duration'] = self.get_transaction_duration()
                    event['content-size'] = float(self.end_response['content_size'])
                    event['object'] = self.end_response['obj']
                    event['transaction-throughput'] = self.get_throughput()
                    del event['content_size']
                    del event['obj']
                    del event['tag']
                    self._send(event)
                elif 'metadata_size' in self.end_response:
                    event = dict(self.end_response)
                    event['id'] = str(uuid4())
                    event['timestamp'] = int(1000 * time())
                    event['originating-machine'] = self.ip
                    event['originating-service'] = self.service_name
                    event['originating-cluster'] = self.cluster_name
                    event['metadata-size'] = float(self.end_response['metadata_size'])
                    event['object'] = self.end_response['obj']
                    del event['metadata_size']
                    del event['obj']
                    del event['tag']
                    self._send(event)
            finally:
                self.start_request, self.start_response, self.end_response = None, None, None

    def get_latency(self):
        """
            Latency here is defined as the time duration from the time the system received
            the start request event till the time the system is ready to start serving the
            response, as seen by the start response event.
        """

        t1 = float(self.start_request['timestamp'])
        t2 = float(self.start_response['timestamp'])

        return (t2 - t1) / 1000

    def get_transaction_duration(self):
        """
            Transaction duration is the time spent serving the request. More accurately, is the
            duration from the time the system received the start request event till the time the
            system received the end response event.
        """

        t1 = float(self.start_request['timestamp'])
        t2 = float(self.end_response['timestamp'])

        return (t2 - t1) / 1000

    def get_throughput(self):
        """
            Throughput is defined as the the number of bytes served in the unit of time.
            Transaction size is measured in bytes, transaction_time in seconds.
            Output value is in bytes per second.
        """

        size = float(self.end_response['content_size'])
        duration = self.get_transaction_duration()

        if abs(duration) <= 5e-9:
            return 0

        return size / duration


if __name__ == '__main__':
    from time import sleep

    mon = VismoEventDispatcher('foo')

    def mon_send_data(operation, tag, tenant, user, container, obj, content_size=0):
        mon.send(status='SUCCESS', tag=tag, operation=operation, tenant=tenant, user=user,
                container=container, obj=obj, content_size=content_size)

    def mon_send_metadata(operation, tag, tenant, user, container, obj, key=None, val=None):
        mon.send(status='SUCCESS', tag=tag, operation=operation, tenant=tenant, user=user,
                container=container, obj=obj, metadata_size=val)

    def send_put(tenant, user, container, obj):
        t = int(1000 * time())
        mon_send_data('PUT', 'start-request', tenant, user, container, obj)
        mon_send_data('PUT', 'start-response', tenant, user, container, obj)
        mon_send_data('PUT', 'end-response', tenant, user, container, obj, 100)

    def send_get(tenant, user, container, obj):
        t = int(1000 * time())
        mon_send_data('GET', 'start-request', tenant, user, container, obj)
        mon_send_data('GET', 'start-response', tenant, user, container, obj)
        mon_send_data('GET', 'end-response', tenant, user, container, obj, 200)

    def send_put_multi(tenant, user, container, obj):
        t = int(1000 * time())
        mon_send_data('PUT_MULTI', 'start-request', tenant, user, container, obj)
        mon_send_data('PUT_MULTI', 'start-response', tenant, user, container, obj)
        mon_send_data('PUT_MULTI', 'end-response', tenant, user, container, obj, 500)

    def send_put_metadata(tenant, user, container, obj):
        t = int(1000 * time())
        mon_send_metadata('PUT_METADATA', 'start-request', tenant, user, container, obj)
        mon_send_metadata('PUT_METADATA', 'start-response', tenant, user, container, obj)
        mon_send_metadata('PUT_METADATA', 'end-response', tenant, user, container, obj, 'foo', 500)

    def send_get_metadata(tenant, user, container, obj):
        t = int(1000 * time())
        mon_send_metadata('GET_METADATA', 'start-request', tenant, user, container, obj)
        mon_send_metadata('GET_METADATA', 'start-response', tenant, user, container, obj)
        mon_send_metadata('GET_METADATA', 'end-response', tenant, user, container, obj, 'bar', 567)

    if sys.argv[1] == 'multi':
        for i in range(int(sys.argv[2])):
            send_put_multi('ntua', 'vassilis', 'test-container', 'foo')
    elif sys.argv[1] == 'meta':
        for i in range(int(sys.argv[2]) / 2):
            send_put_metadata('ntua', 'vassilis', 'test-container-meta', 'foo')
            send_get_metadata('ntua', 'vassilis', 'test-container-meta', 'foo')
    else:
        for i in range(int(sys.argv[2]) / 2):
            send_put('ntua', 'vassilis', 'test-container', 'foo')
            send_get('ntua', 'vassilis', 'test-container', 'foo')
