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


@task(alias='reinstall')
@hosts('')
def reinstall_rpm():
    def get_rpm(dir):
        """Get the name of the rpm."""

        return filter(lambda f: f.startswith(RPM_NAME) and f.endswith('.rpm'), listdir(dir))[0]

    rpm_file = get_rpm('../../../target/rpm/vismo/RPMS/noarch')
    tmp_dir = '/tmp/vismo-tmp'

    run('mkdir -p {0}'.format(tmp_dir))
    put(rpm_file, tmp_dir)
    run('rpm -e {0}'.format(RPM_NAME))
    run('rpm -i {0}/{1}'.format(tmp_dir, RPM_NAME))
    run('rm -fr {0}'.format(tmp_dir))

