#!/usr/bin/python2
# vim: set fileencoding=utf-8

from __future__ import print_function

import socket, struct, fcntl, sys
from os import getpid
from time import time, sleep
from pyjavaproperties import Properties
import zmq
import json


# this is the file that holds the lib's configuration
#CONFIGURATION_PROPERTIES = '/srv/vismo/config.properties'
CONFIGURATION_PROPERTIES = 'config.properties'


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


class MonitoringEventDispatcher(object):
    """
        This is used as the bridge that handles the event generation code
        with the event distribution code. This instance talks directly
        to the locally running vismo instance.

        The services of this library are called by the Object Service on
        each request/response cycle. Since each request in object service
        is handled by a different process, we know that each event belongs
        to the same request/response.
    """


    def __init__(self, service_name):
        self.load_configuration()
        self.service_name = service_name
        self.iface = 'eth0'
        self.ip = get_public_ip(self.iface)
        self.sock = self.create_push_socket(self.producers_point)
        self.start_request_event = None
        self.start_response_event = None
        self.end_response_event = None


    def collect_event(self, event):
        """
        """

        # is the event of interest to monitoring?
        if 'tag' not in event:
            self._sock_send(event)
            return

        tag = event['tag']

        if tag == 'start_request_event':
            self.start_request_event = event
        elif tag == 'start_response_event':
            self.start_response_event = event
        elif tag == 'end_response_event':
            self.end_response_event = event
        else:
            log_and_raise('handed event with incomprehensible tag: ' +  tag + ', event: ' + str(event), ValueError)

        # if we have all the events of interest
        if self.start_request_event and self.start_response_event and self.end_response_event:
            main_event = self.start_request_event

            main_event['latency'] = self.calculate_latency()
            main_event['transaction-time'] = self.calculate_transaction_time()
            main_event['throughput'] = self.calculate_throughput()
            # not needed
            del main_event['status']
            del main_event['tag']

            self._sock_send(main_event)


    def calculate_latency(self):
        """
            Latency is defined as the time it took from receiving
            the request till the time that we're ready to start sending
            the response. Input units are in millis, output in seconds.
        """

        return (float(self.start_response_event['timestamp']) - float(self.start_request_event['timestamp'])) / 1000.0


    def calculate_throughput(self):
        """
            Throughput is defined as the the number of bytes served in the unit of time.
            Input units are in bytes and millis, output in bytes/second.
        """

        transaction_time = self.calculate_transaction_time()

        if self.is_effectively_zero(transaction_time):
            return 0.0

        return self.start_request_event['content-size'] / transaction_time


    def calculate_transaction_time(self):
        """
            Transaction time is defined as the time it took from receiving
            the request till the time that the response was completely
            handed off to the user. Input units are in millis, output in seconds.

        """

        return (float(self.end_response_event['timestamp']) - float(self.start_request_event['timestamp'])) / 1000.0



    def add_basic_properties(self, event):
        event['timestamp'] = int(1000 * time())
        event['originating-machine'] = self.ip
        event['originating-service'] = self.service_name
        event['cluster'] = self.cluster_name


    def cleanup_event(self, event):
        """
            For consistency reasons, use dashes and full nouns.
        """

        if 'content_size' in event:
            event['content-size'] = event['content_size']
            del event['content_size']
        if 'obj' in event:
            event['object'] = event['obj']
            del event['obj']


    def send(self, **event):
        self.cleanup_event(event)
        self.add_basic_properties(event)
        self.log('{0}: {1}'.format(event['timestamp'], event['tag']))
        self.collect_event(event)


    def _sock_send(self, event):
        self.sock.send(json.dumps(event))


    def load_configuration(self):
        p = Properties()
        p.load(open(CONFIGURATION_PROPERTIES))
        self.producers_point = p['producers.point']
        # FIXME: auto acquire the name according to the machine's ip
        self.cluster_name = p['cluster1Name']


    def create_push_socket(self, end_point):
        ctx = zmq.Context()
        sock = ctx.socket(zmq.PUSH)
        sock.setsockopt(zmq.LINGER, 0)
        sock.connect(end_point)

        return sock

## Utilities

    def log(self, msg):
        print('vismo-local-event-dispatcher:' + msg, file=sys.stderr)

    def log_and_raise(msg, excp):
        print('vismo-local-event-dispatcher:' + msg, file=sys.stderr)
        raise excp(msg)

    def is_effectively_zero(self, n):
        return abs(n) <= 1e-9



if __name__ == '__main__':
    mon = MonitoringEventDispatcher('foo')
    mon.send(topic='off-course', tag='start_request_event', content_size=1000, obj='ofdesire', status=1)
    sleep(1)
    mon.send(topic='off-course', tag='start_response_event', obj='ofdesire', status=1)
    sleep(1)
    mon.send(topic='off-course', tag='end_response_event', obj='ofdesire', status=1)

# TRACE [2012-08-23 11:21:51,396] gr.ntua.vision.monitoring.LocalEventCollector: received: {"status": "SUCCESS", "originating-machine": "10.0.1.101", "container": "2-vassilis-tmp", "timestamp": 1345720911396, "topic": "reads", "tag": "start-request", "user": "unauthorized", "originating-service": "object-service", "content_size": 0, "obj": "1", "type": "read", "tenant": "ntua"}
# TRACE [2012-08-23 11:21:51,771] gr.ntua.vision.monitoring.LocalEventCollector: received: {"status": "SUCCESS", "originating-machine": "10.0.1.101", "container": "2-vassilis-tmp", "timestamp": 1345720911770, "topic": "reads", "tag": "start-response", "user": "unauthorized", "originating-service": "object-service", "content_size": 0, "obj": "1", "type": "read", "tenant": "ntua"}
# TRACE [2012-08-23 11:21:51,787] gr.ntua.vision.monitoring.LocalEventCollector: received: {"status": "SUCCESS", "originating-machine": "10.0.1.101", "container": "2-vassilis-tmp", "timestamp": 1345720911786, "topic": "reads", "tag": "end-response", "user": "unauthorized", "originating-service": "object-service", "content_size": 0, "obj": "1", "type": "read", "tenant": "ntua"}

