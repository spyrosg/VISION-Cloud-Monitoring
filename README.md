
vismo
=====

vismo is the monitoring infrastructure of the [VISION Cloud](http://www.visioncloud.eu/)
european project.


Building
---

vismo is a multi-module maven project, and as such, can be build with:

    mvn clean package

Running
---

After building, you can run:

    java -jar vismo-core/target/vismo-core-${project.version}.jar vismo-config/src/main/resources/config.properties start

and vismo will start receiving, processing and publising events.
There's also the generated javadoc you can read for getting up to speed.

