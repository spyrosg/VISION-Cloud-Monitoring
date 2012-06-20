#!/usr/bin/python2
# vim: set fileencoding=utf-8

import socket, struct, fcntl, sys
from os import getpid
from time import time
import logging
from logging.handlers import TimedRotatingFileHandler
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

    rolling_handler = TimedRotatingFileHandler('/var/log/vismo_dispatch.log', backupCount=10, when='midnight')
    rolling_handler.doRollover()
    # TRACE [2010-04-06 06:42:35,271] com.example.dw.Thing: Contemplating doing a thing.
    rolling_handler.setFormatter(logging.Formatter('%(levelname)s [%(asctime)-15s] %(clazz)s: %(message)s'))
    log = logging.getLogger('vismo')
    log.addHandler(rolling_handler)
    log.setLevel(logging.DEBUG)


    def __init__(self):
        self.iface = 'eth0'
        self.ip = get_public_ip(self.iface)
        self.info('dispatcher startup, with pid={0}, ip={1}'.format(getpid(), self.iface + '/' + self.ip))
        self.debug('connecting to endpoint={0}'.format(MonitoringEventDispatcher.EVENTS_ENDPOINT))
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
        event['originating-service'] = 'object-service'
        self.debug('=> {0}'.format(event))
        self.sock.send(json.dumps(event))



    def debug(self, msg):
        MonitoringEventDispatcher.log.debug(msg, extra={'clazz':self.cls_name()})

    def info(self, msg):
        MonitoringEventDispatcher.log.info(msg, extra={'clazz':self.cls_name()})

    def warn(self, msg):
        MonitoringEventDispatcher.log.warn(msg, extra={'clazz':self.cls_name()})

    def error(self, msg):
        MonitoringEventDispatcher.log.error(msg, extra={'clazz':self.cls_name()})

    def cls_name(self):
        return self.__module__+ '.' + self.__class__.__name__

