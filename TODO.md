Y3 goals
--------
[1] + Formal means to add rules to monitoring (including timing) through a high level language.
      ~ Rest Resource Interface
      ~ Test feature
[2] + Dynamic updates of rules (including timing) (plus propagation of rules)
      + Creation of Rule 
      + Request of active users (provided by heartbeat service)
      + Send the existence of new Rule with Reliable Multicast
      + Instances request the new Rule
      + Serialization of Rule object
      + Test feature
[3] + Automatic configuration of monitoring component
      + Distribution of configuration to active users (provided by the heartbeat service)
      + Test feature
[4] + Automatic deployment of monitoring component
[5] + Heartbeat function for all the instances
      ~ Simple Multicast heartbeat Client/Service 
      ~ Multithreaded Service
      + Test feature
[6] + Optimized Aggregation (how to achieve partial aggregation)
[7] + Replace vismo-config module with a service that knows which machines are running.
      + Replace based on heartbeat service
      + Test feature
[8] + Refactor MonitoringDriver to run multiple vismo instances so that we can test aggregation on multiple resources
      + Implementation
      + Test feature
[9] + Send log output to one dedicated machine
      + Implementation
      + Test feature
[10] + Free mem: free -o -m (total = used+free)
[11]+ Http ui
[12]+ Persistence
[13]+ Open source plan (what we will do)
      + Licenses
      + What we will provide
      + How we will provide
[14]+ Open source documentation/website
[15]+ Open source refactoring of code for modularity/usability

Symbols
-------
+ : to be done
- : done
~ : to revisit

Indicative Days
---------------
[1]  10 days
[2]  15 days
[3]  3  days
[4]  10 days
[5]  5  days
[6]  15 days
[7]  5  days
[8]  5  days
[9]  7?  days
[10] 3?  days
[11] 7  days
[12] 7  days
[13] 7  days
[14] 15 days
[15] 60 days

Sum
---  174 days
     /2  persons = 87 days
     /22 days per month
     = 4 months

Indicative Delivery for all features
-----------------------------------
     Mid of April

