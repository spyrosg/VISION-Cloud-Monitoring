#!/usr/bin/python2
# vim: set fileencoding=utf-8

from __future__ import print_function

from vismo_dispatch import MonitoringEventDispatcher
from time import sleep, time
from random import randint, choice, random, sample


class AggregatedEventsServer(object):
    """
        Generate aggregrated events for Accounting/Billing/CTO.
    """

    tenants = ['RAI', 'ntua', 'SAP', 'IBM', 'FT', 'tid', 'SIE']
    users = ['marco', 'mmontagnuolo', 'vassilis', 'peter', 'MichelD', 'Michael', 'jose']
    containers = ['test', 'foo', 'bar', 'prova2', 'prova3', 'schemas', 'mediabox',
            'test', 'prova4', 'prova', 'media_uc_container', 'docs', 'test4', 'dwdatatest', 'test5',
            'TIDtrailers3', 'TIDtrailers2', 'f1rst', 'sec0nd', 'TIDtrailers', 'dns1', 'dns2', 'dns3',
            '-star', 'f1rst', 'my-bar', 'my-foo']
    objects = ['object1', 'object2', 'objectn', 'foo', 'bar', 'spam', 'eggs']


    def __init__(self, dispatcher):
        self.dispatcher = dispatcher


    def send_billing_events(self):
        tend = int(1000 * time())
        tstart = tend - 60 * 1000 # an hour ago
        self.dispatcher.send(topic='Billing', aggregation='no of objects read', units='', users=self.user_list_for_objects_accessed(), tstart=tstart, tend=tend)
        self.dispatcher.send(topic='Billing', aggregation='no of objects written', units='', users=self.user_list_for_objects_accessed(), tstart=tstart, tend=tend)


    def send_cto_events(self):
        tend = int(1000 * time())
        tstart = tend - 3 * 1000 # 3 mings ago
        self.dispatcher.send(topic='CTO', aggregation='mean think time per read', units='seconds', users=self.user_list_for_think_time(), tstart=tstart, tend=tend)
        self.dispatcher.send(topic='CTO', aggregation='mean think time per write', units='seconds', users=self.user_list_for_think_time(), tstart=tstart, tend=tend)


    def send_analysis_events(self):
        tend = int(1000 * time())
        tstart = tend - 5 * 1000 # 5 mings ago
        self.dispatcher.send(users=self.user_list_for_bytes(), aggregation='inbound traffic', topic='Analysis', units='bytes per second', tstart=tstart, tend=tend)
        self.dispatcher.send(users=self.user_list_for_bytes(), aggregation='outbound traffic', topic='Analysis', units='bytes per second', tstart=tstart, tend=tend)
        self.dispatcher.send(containers=self.throughput_per_container_list(), aggregation='inbound throughput per container', topic='Analysis', units='bytes per second', tstart=tstart, tend=tend)
        self.dispatcher.send(containers=self.throughput_per_container_list(), aggregation='outbound throughput per container', topic='Analysis', units='bytes per second', tstart=tstart, tend=tend)


    def send_sla_events(self):
        tend = int(1000 * time())
        tstart = tend - 10 * 1000 # 10 mings ago
        self.dispatcher.send(containers=self.throughput_per_container_list(), aggregation='inbound throughput per container', topic='SLA', units='bytes per second', tstart=tstart, tend=tend)
        self.dispatcher.send(containers=self.throughput_per_container_list(), aggregation='outbound throughput per container', topic='SLA', units='bytes per second', tstart=tstart, tend=tend)
        self.dispatcher.send(objects=self.throughput_per_request(), aggregation='inbound throughput per request', topic='SLA', units='bytes per second', tstart=tstart, tend=tend)
        self.dispatcher.send(objects=self.throughput_per_request(), aggregation='outbound throughput per request', topic='SLA', units='bytes per second', tstart=tstart, tend=tend)


    def user_list_for_objects_accessed(self):
        return [{
            "tenant": choice(AggregatedEventsServer.tenants),
            "container": choice(AggregatedEventsServer.containers),
            "object": choice(AggregatedEventsServer.objects),
            "user": u,
            "count": randint(1, 500)
        } for u in AggregatedEventsServer.users]


    def user_list_for_think_time(self):
        return [{
            "user": u,
            "tenant": choice(AggregatedEventsServer.tenants),
            "containers": [
                {
                    "container": c,
                    "think-mean-time": 20 * random(),
                } for c in sample(AggregatedEventsServer.containers, 5)
            ],
        } for u in AggregatedEventsServer.users]


    def user_list_for_bytes(self):
        return [{
            "tenant": choice(AggregatedEventsServer.tenants),
            "objects": [
                {
                    "count": randint(int(1e6), 500 * int(1e6)),
                    "object": o,
                    "container": choice(AggregatedEventsServer.containers),
                } for o in sample(AggregatedEventsServer.objects, 7)],
            "user": u,
        } for u in AggregatedEventsServer.users]


    def throughput_per_container_list(self):
        return [{
            "container": c,
            "user": choice(AggregatedEventsServer.users),
            "tenant": choice(AggregatedEventsServer.tenants),
            "count": randint(int(1e6), int(1e7)),
        } for c in AggregatedEventsServer.containers]


    def throughput_per_request(self):
        return [{
            "object": o,
            "container": choice(AggregatedEventsServer.containers),
            "user": choice(AggregatedEventsServer.users),
            "tenant": choice(AggregatedEventsServer.tenants),
            "count": randint(int(1e6), int(1e7)),
        } for o in AggregatedEventsServer.objects]


if __name__ == '__main__':
    dispatcher = MonitoringEventDispatcher('AggregatedEventsServer')
    server = AggregatedEventsServer(dispatcher)

    while True:
        server.send_billing_events()
        server.send_cto_events()
        server.send_analysis_events()
        server.send_sla_events()
        sleep(2.0)

