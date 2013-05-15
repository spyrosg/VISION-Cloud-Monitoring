#!/usr/bin/env python2
# vim: set fileencoding=utf-8 :

from __future__ import print_function

import sys
from math import sqrt


class OnlineNormalEstimator(object):
    """
    This is copy/pasted from http://stackoverflow.com/a/1348615
    """

    def __init__(self):
        self.sample_size = 0.0
        self.running_mean = 0.0
        self.running_variance = 0.0
        self.running_min = float('+inf')
        self.running_max = float('-inf')

    def handle(self, x):
        if x > self.running_max: self.running_max = x
        if x < self.running_min: self.running_min = x
        self.sample_size += 1
        nextM = self.running_mean + (x - self.running_mean) / self.sample_size;
        self.running_variance += (x - self.running_mean) * (x - nextM);
        self.running_mean = nextM;

    def min(self):
        return self.running_min

    def max(self):
        return self.running_max

    def mean(self):
        return self.running_mean

    def variance(self):
        if self.sample_size > 1:
            return self.running_variance / self.sample_size
        else:
            return 0.0

    def std_dev(self):
        return sqrt(self.variance())


def read_csv(file_stream, est1, est2):
    for line in file_stream:
        if line.startswith('#'):
            continue

        fs = line[:-1].split(',')
        est1.handle(float(fs[1]))
        est2.handle(float(fs[3]))


if __name__ == '__main__':
    if len(sys.argv) == 2:
        latency_est = OnlineNormalEstimator()
        throughput_est = OnlineNormalEstimator()

        with open(sys.argv[1]) as inp:
            read_csv(inp, latency_est, throughput_est)

        print('latency: min={0}, mean={1}, std_dev={2}, max={3}'.format(latency_est.min(),
            latency_est.mean(), latency_est.std_dev(), latency_est.max()))
        print('throughput: min={0}, mean={1}, std_dev={2}, max={3}'.format(throughput_est.min(),
            throughput_est.mean(), throughput_est.std_dev(), throughput_est.max()))
