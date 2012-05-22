#!/usr/bin/python2
# vim: set fileencoding=utf-8

from __future__ import print_function

from vismo_dispatch import MonitoringEventDispatcher
from time import sleep
import logging
from random import randint


### The following code emulates object service and its clients.

class FakeObjectService(object):
    """
        This is the fake object service, which is used
        by the clients to perform object requests.
    """

    def __init__(self, dispatcher):
        self.dispatcher = dispatcher


    def read(self, tenant, user, container, obj):
        self.dispatcher.send(event_type='read', tenant=tenant, user=user, container=container, obj=obj)
        return 'ok' # assume success for now


    def write(self, tenant, user, container, obj):
        self.dispatcher.send(event_type='write', tenant=tenant, user=user, container=container, obj=obj)
        return 'ok' # assume success for now



class FakeObjectServiceClient(object):
    # the maximum number of seconds to wait
    # before sending an event
    MAX_DELAY = 5


    def __init__(self, obs):
        self.obs = obs

    def run_requests(self):
        while True:
            n = randint(1, 100)

            if n % 3 == 0:
                self.obs.write('ntua', 'vassilis', 'foo', 'my-object')
            else:
                self.obs.read('ntua', 'vassilis', 'foo', 'my-object')

            sleep(randint(1, FakeObjectServiceClient.MAX_DELAY))


if __name__ == '__main__':
    logging.info('starting on localhost')

    # this will be our entry point to object service
    # which will be normally imported to the actual object
    # service code
    dispatcher = MonitoringEventDispatcher()

    fake_obj_service = FakeObjectService(dispatcher)
    fake_client = FakeObjectServiceClient(fake_obj_service)
    fake_client.run_requests()


