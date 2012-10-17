#!/usr/bin/env python2

from __future__ import print_function
from fabric.api import task, run, env, hosts
from fabric.operations import put
from os import listdir
from time import sleep
import sys

RPM_NAME = 'vismo'
SERVICE_NAME = 'vision-vismo'


###
# utils
###


###
# commands
###

@task(alias='restart')
def restart_service():
    """Restart the service."""

    run('service {0} restart'.format(SERVICE_NAME))


@task(alias='status')
def status_service():
    """Report service's status."""

    run('service {0} status'.format(SERVICE_NAME))


@task(alias='status1')
def ps_status():
    """Report service's status."""

    run("""ps aux | awk '/vismo.jar/ && !/awk/'""")


@task(alias='start')
def start_service():
    """Start the service."""

    run('service {0} start'.format(SERVICE_NAME))


@task(alias='stop')
def stop_service():
    """Stop the service."""

    run('service {0} stop'.format(SERVICE_NAME))



@task(alias='dummy-start')
def start_dummy_server():
    """Start the dummy event server."""

    run('cd /; nohup python /srv/vismo/dummy-event-server.py </dev/null >&/dev/null &', pty=False)


@task(alias='dummy-status')
def status_dummy_server():
    """Check for a running dummy-event-server instance."""

    run("""ps aux | awk '/dummy-event-server/ && !/awk/ { print $2 }'""")


@task(alias='dummy-stop')
def stop_dummy_server():
    """Stop the dummy event server."""

    run("""ps aux | awk '/dummy-event-server/ && !/awk/ { print $2; system("kill " $2); }'""")


@task(alias='grep')
def grep_log(pattern):
    """Grep containers log with given pattern"""
    run("grep '{0}' /var/log/vismo* || echo".format(pattern))


@task(alias='up')
@hosts('10.0.3.212')
def upload_rpm_to_testbed(url, name):
    """Upload the rpm to the testbed."""

    run('rm -fr /tmp/vismo*.rpm')
    run("""wget -q '{0}' -O /tmp/{1}""".format(url, name))

    for host in env.hosts:
        run('scp /tmp/{0} {1}:/tmp/'.format(name, host))


@task(alias='install')
def install_rpm():
    """Install the latest rpm."""

    run('rpm -e vismo')
    run('rpm -i /tmp/vismo*.rpm')


@task(alias='config')
def print_config():
    run('cat /srv/vismo/config.properties')
