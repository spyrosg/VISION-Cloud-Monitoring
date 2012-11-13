
Y3 goals
===

- Formal means to add rules to monitoring (including timing) through a high level language.
- Dynamic updates of rules (including timing) (plus propagation of rules)
- Automatic deployment and configuration of monitoring component
- Heartbeat function for all the instances
- Optimized Aggregation (how to achieve partial aggregation)
- MAYBE: persistence

- replace vismo-config with a (distributed) service that knows which
  machines are running.

- refactor MonitoringDriver to run multiple vismo instances
  so that we can test aggregation on multiple resources
- send log output to one dedicated machine
- free mem: free -o -m (total = used+free)

- http ui

