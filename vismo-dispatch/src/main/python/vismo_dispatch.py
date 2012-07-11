#!/usr/bin/python2
# vim: set fileencoding=utf-8

import socket, struct, fcntl, sys
from os import getpid
from time import time
import zmq
import json


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
        with the event distribution code.
    """

    EVENTS_ENDPOINT = 'tcp://127.0.0.1:26891'


    def __init__(self, service_name):
        self.service_name = service_name
        self.iface = 'eth0'
        self.ip = get_public_ip(self.iface)
        self.sock = self.create_push_socket(MonitoringEventDispatcher.EVENTS_ENDPOINT)


    def create_push_socket(self, end_point):
        ctx = zmq.Context()
        sock = ctx.socket(zmq.PUSH)
        sock.setsockopt(zmq.LINGER, 0)
        sock.connect(end_point)

        return sock


    def send(self, **event):
        event['timestamp'] = int(1000 * time())
        event['originating-machine'] = self.ip
        event['originating-service'] = self.service_name
        event['cluster'] = 'test'
        self.sock.send(json.dumps(event))

