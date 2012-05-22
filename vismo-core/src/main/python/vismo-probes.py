#!/usr/bin/python2
# vim: set fileencoding=utf-8

from __future__ import print_function

from vismo_dispatch import MonitoringEventDispatcher
from time import sleep
from subprocess import Popen, PIPE
import logging


class EventProducer(object):
    """All event producers should extend this class."""

    def __init__(self, name, dispatcher):
        self.dispatcher = dispatcher

    def send(self, **kwargs):
        kwargs['originating-service'] = self.name
        self.dispatcher.send(kwargs)


class MemoryConsumptionEventProducer(EventProducer):
    """Memory consumption producer event."""

    def __init__(self, dispatcher):
        super(MemoryConsumptionEventProducer, self).__init__('memory-consumption', dispatcher)


    def collect(self):
        self.pipe = Popen('free -o', shell=True, bufsize=1024, stdout=PIPE).stdout

        for line in self.pipe:
            if line.startswith('Mem:'):
                fs = line.split()
                total, free = fs[1], fs[3]
                print('{} / {}'.format(int(free), int(total)))
                self.dispatcher.send(free_mem_perc=float(free)/int(total))
                self.dispatcher.send(free_memory=free, unit='bytes')

        self.pipe.close()


def proc_load():
    pass

def get_disk_usage():
    pass


if __name__ == '__main__':
    logging.info('starting vismo probes')
    dispatcher = MonitoringEventDispatcher()
    mem_prod = MemoryConsumptionEventProducer(dispatcher)

    while True:
        mem = mem_prod.collect()
        #cpu = get_cpu_load()
        #disk = get_disk_usage()
        #dispatcher
        sleep(1)
