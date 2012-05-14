#!/usr/bin/python2
# vim: set fileencoding=utf-8

from __future__ import print_function

from sys import stderr
from time import sleep, time
import logging
import zmq
import json



### This is the monitoring dispatch lib code.

EVENTS_ENDPOINT= "tcp://127.0.0.1:67891"


class MonitoringEventDispatcher(object):
    """
        This is used as the bridge that handles the event generation code
        with the event distribution code.
    """

    def __init__(self):
        logging.info('monitoring dispatcher startup')
        self.ctx = zmq.Context()
        self.events_end_point = EVENTS_ENDPOINT
        self.sock = self.ctx.socket(zmq.ROUTER)
        self.sock.setsockopt(zmq.LINGER, 0)
        logging.debug('binding to endpoint=%s', self.events_end_point)
        self.sock.bind(self.events_end_point)

    def send(self, **event):
        event['timestamp'] = int(time())
        logging.debug("sending event: %s", event)
        self.sock.send(json.dumps(event))



### The following code emulates object service and its clients.

class FakeObjectService(object):
    """
        This is the fake object service, which is used
        to put object requests to by the clients.
    """

    def __init__(self, dispatcher):
        self.dispatcher = dispatcher


    def read(self, tenant, user, container, obj):
        self.dispatcher.send(event_type='read', tenant=tenant, user=user, container=container, obj=obj)
        return 'ok' # assume success for now


    def write(self, tenant, user, container, obj):
        self.dispatcher.send(event_type='write', tenant=tenant, user=user, container=container, obj=obj)
        return 'ok' # assume success for now



ONE_SEC_DELAY = 1
NO_EVENTS = 1000

class FakeObjectServiceClient(object):
    def __init__(self, obs):
        self.obs = obs

    def run_requests(self):
        for i in range(NO_EVENTS):
            if i % 2 == 0:
                self.obs.read('ntua', 'vassilis', 'foo', 'my-object')
            else:
                self.obs.write('ntua', 'vassilis', 'foo', 'my-object')

            sleep(ONE_SEC_DELAY)



if __name__ == '__main__':
    logging.getLogger().setLevel(logging.DEBUG)
    logging.info('starting on localhost')

    # this will be our entry point to object service
    # which will be normally imported to the actual object
    # service code
    dispatcher = MonitoringEventDispatcher()

    fake_obj_service = FakeObjectService(dispatcher)
    fake_client = FakeObjectServiceClient(fake_obj_service)
    fake_client.run_requests()


