#!/usr/bin/python2
# vim: set fileencoding=utf-8

from subprocess import Popen, PIPE
from os import getpid
from time import time
import logging
from logging.handlers import TimedRotatingFileHandler
import zmq
import json




def get_iface_ip():
    """
        Return a tuple whose first element contains the first non loopback interface
        and the second elements is the first not inet6 address of that interface.
    """

    pipe = Popen('ip addr', shell=True, bufsize=1024, stdout=PIPE).stdout
    iface, ip = None, None

    for line in pipe:
        if line[0].isdigit():
            fs = line.split()
            iface = fs[1][:-1]

        if iface == 'lo':
            continue
        if 'inet6' in line:
            continue

        if 'inet' in line:
            fs = line.split()
            ind = fs[1].index('/')

            if ind >= 0:
                ip = fs[1][0:ind]
            else:
                ip = fs[1]

            pipe.close()
            return iface, ip

    pipe.close()



class MonitoringEventDispatcher(object):
    """
        This is used as the bridge that handles the event generation code
        with the event distribution code.
    """

    EVENTS_ENDPOINT = 'ipc:///tmp/vision.root.events'

    rolling_handler = TimedRotatingFileHandler('/var/log/vismo_dispatch.log', backupCount=10, when='midnight')
    rolling_handler.doRollover()
    # TRACE [2010-04-06 06:42:35,271] com.example.dw.Thing: Contemplating doing a thing.
    rolling_handler.setFormatter(logging.Formatter('%(levelname)s [%(asctime)-15s] %(clazz)s: %(message)s'))
    log = logging.getLogger('vismo')
    log.addHandler(rolling_handler)
    log.setLevel(logging.DEBUG)


    def __init__(self):
        (iface, ip) = get_iface_ip()
        self.ip = ip
        self.info('dispatcher startup, with pid={0}, ip={1}'.format(getpid(), iface + '/' + ip))
        self.debug('connecting to endpoint={0}'.format(MonitoringEventDispatcher.EVENTS_ENDPOINT))
        self.sock = self.create_push_socket(MonitoringEventDispatcher.EVENTS_ENDPOINT)


    def create_push_socket(self, end_point):
        ctx = zmq.Context()
        sock = ctx.socket(zmq.PUSH)
        sock.setsockopt(zmq.LINGER, 0)
        sock.connect(end_point)

        return sock


    def send(self, **event):
        event['timestamp'] = int(time())
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

