#!/usr/bin/python2 # vim: set fileencoding=utf-8

from __future__ import print_function

from vismo_dispatch import MonitoringEventDispatcher
from time import sleep
import logging
from random import randint, choice


### The following code emulates object service and its clients.

class FakeObjectService(object):
    """
        This is the fake object service, which is used
        by the clients to perform object requests.
    """

    def __init__(self, dispatcher):
        self.dispatcher = dispatcher


    def read(self, tenant, user, container, obj):
        self.dispatcher.send(topic='reads', type='read', tenant=tenant, user=user, container=container, obj=obj)
        return 'ok' # assume success for now


    def write(self, tenant, user, container, obj):
        self.dispatcher.send(topic='writes', type='write', tenant=tenant, user=user, container=container, obj=obj)
        return 'ok' # assume success for now



class FakeObjectServiceClient(object):
    # the maximum number of seconds to wait
    # before sending an event
    MAX_DELAY = 3
    tenants = ['RAI', 'ntua', 'SAP', 'IBM', 'FT', 'tid', 'SIE']
    users = ['marco', 'mmontagnuolo', 'vassilis', 'peter', 'MichelD', 'Michael', 'jose']
    containers = ['test', 'foo', 'bar', 'prova2', 'prova3', 'schemas', 'mediabox',
        'test', 'prova4', 'prova', 'media_uc_container', 'docs', 'test4', 'dwdatatest', 'test5',
        'TIDtrailers3', 'TIDtrailers2', 'f1rst', 'sec0nd', 'TIDtrailers', 'dns1', 'dns2', 'dns3',
        '-star', 'f1rst', 'my-bar', 'my-foo']
    objects = ['object1', 'object2', 'objectn', 'foo', 'bar', 'spam', 'eggs']


    def __init__(self, obs):
        self.obs = obs

    def run_requests(self):
        while True:
            n = randint(1, 100)

            if n % 3 == 0:
                self.obs.write(self.tent(), self.usr(), self.cont(), self.obj())
            else:
                self.obs.write(self.tent(), self.usr(), self.cont(), self.obj())

            sleep(randint(1, FakeObjectServiceClient.MAX_DELAY))

    def obj(self):
        return choice(FakeObjectServiceClient.objects)

    def cont(self):
        return choice(FakeObjectServiceClient.containers)

    def tent(self):
        return choice(FakeObjectServiceClient.tenants)

    def usr(self):
        return choice(FakeObjectServiceClient.users)


if __name__ == '__main__':
    logging.info('starting on localhost')

    # this will be our entry point to object service
    # which will be normally imported to the actual object
    # service code
    dispatcher = MonitoringEventDispatcher()

    fake_obj_service = FakeObjectService(dispatcher)
    fake_client = FakeObjectServiceClient(fake_obj_service)
    fake_client.run_requests()


