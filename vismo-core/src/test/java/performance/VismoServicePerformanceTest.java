package performance;

import gr.ntua.vision.monitoring.VismoConfiguration;
import gr.ntua.vision.monitoring.VismoVMInfo;
import gr.ntua.vision.monitoring.dispatch.VismoEventDispatcher;
import gr.ntua.vision.monitoring.notify.VismoEventRegistry;
import gr.ntua.vision.monitoring.rules.AccountingRule;
import gr.ntua.vision.monitoring.rules.CTORule;
import gr.ntua.vision.monitoring.rules.PassThroughRule;
import gr.ntua.vision.monitoring.rules.RulesStore;
import gr.ntua.vision.monitoring.rules.VismoRulesEngine;
import gr.ntua.vision.monitoring.service.ClusterHeadNodeFactory;
import gr.ntua.vision.monitoring.service.Service;
import gr.ntua.vision.monitoring.zmq.ZMQFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZContext;

import com.carrotsearch.junitbenchmarks.AbstractBenchmark;
import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.BenchmarkRule;


/**
 * @author tmessini
 */
public class VismoServicePerformanceTest extends AbstractBenchmark {

    /** the log target. */
    private static final Logger              log                 = LoggerFactory.getLogger(VismoServicePerformanceTest.class);
    /***/
    private final static Properties          props               = new Properties() {
                                                                     /***/
                                                                     private static final long serialVersionUID = 1L;
                                                                     {
                                                                         setProperty("cloud.heads", "127.0.0.1, 127.0.0.2");
                                                                         setProperty("cloud.name", "visioncloud.eu");
                                                                         setProperty("cluster.name", "vision-1");
                                                                         setProperty("cluster.head", "127.0.0.1");
                                                                         setProperty("producers.point", "tcp://127.0.0.1:56429");
                                                                         setProperty("consumers.port", "56430");
                                                                         setProperty("udp.port", "56431");
                                                                         setProperty("cluster.head.port", "56432");
                                                                         setProperty("cloud.head.port", "56433");
                                                                         setProperty("mon.group.addr", "228.5.6.7");
                                                                         setProperty("mon.group.port", "12345");
                                                                         setProperty("mon.ping.period", "60000");
                                                                     }
                                                                 };
    /***/
    private final static int                 TOTAL_NUM_OF_EVENTS = 200000;
    /***/
    private final static int                 TOTAL_THREAD_NUM    = 10;
    /***/
    private final VismoConfiguration         conf                = new VismoConfiguration(VismoServicePerformanceTest.props);
    /***/
    private final Random                     rng                 = new Random();
    /***/
    private Service                          service;
    /***/
    private ZMQFactory                       socketFactory;
    /***/
    private ArrayList<SimpleConsumerHandler> consumers           = new ArrayList<SimpleConsumerHandler>();
    /***/
    private final static int                 NUM_OF_CONSUMERS    = 1;


    /***/
    enum LoadedRulesSet {
        /***/
        PASSTHROUGH,
        /***/
        SMALL_SET,
        /***/
        MEDIUM_SET,
        /***/
        INTENSIVE_SET
    }


    /**
     * this test creates a number of threads that consume events and we measure the performance of the system.
     * 
     * @throws InterruptedException
     */
    @Ignore
    @Test
    public void enginePerformanceFewEventsTest() throws InterruptedException {

        createEventConsumers(NUM_OF_CONSUMERS);

        final CountDownLatch eventCountDownLatch = new CountDownLatch(TOTAL_NUM_OF_EVENTS);
        final ExecutorService threadExecutor = Executors.newFixedThreadPool(VismoServicePerformanceTest.TOTAL_THREAD_NUM);

        final long start = System.currentTimeMillis();

        startDispatcherThreads(threadExecutor, eventCountDownLatch);
        waitForEventsToBeSent(eventCountDownLatch);
        SimpleConsumerHandler probingConsumer = consumers.get(getRandomConsumerNum(consumers));
        waitForEventsToReachConsumer(probingConsumer);
        threadExecutor.shutdown();

        final double dur = (System.currentTimeMillis() - start) / 1000.0;

        printPerformanceMessage(probingConsumer, dur);
    }

    /**
     * 
     */
    @Rule
    public BenchmarkRule benchmarkRun1 = new BenchmarkRule();


    /**
     * @param threadExecutor
     * @param eventCountDownLatch
     */
    @BenchmarkOptions(callgc = false, benchmarkRounds = 2, warmupRounds = 0)
    /**
     * the method that starts the dispatcher threads
     * @param threadExecutor
     * @param eventCountDownLatch
     */
    private void startDispatcherThreads(ExecutorService threadExecutor, CountDownLatch eventCountDownLatch) {
        for (int i = 0; i < VismoServicePerformanceTest.TOTAL_THREAD_NUM; i++)
            threadExecutor.execute(new EventDispatcherThread(new VismoEventDispatcher(socketFactory, conf, "performance-test"),
                    rng, eventCountDownLatch));

    }


    /**
     * log a message reporting performance
     * 
     * @param probingConsumer
     * @param duration
     */
    private static void printPerformanceMessage(SimpleConsumerHandler probingConsumer, double duration) {
        log.debug("{} events received by the consumer in {} sec ({} events/sec) with maximum latency {} secs", new Object[] {
                probingConsumer.getNoReceivedEvents(), duration, probingConsumer.getNoReceivedEvents() / duration,
                probingConsumer.getMaxLatencyInSecs() });

    }


    /**
     * wait for events to be sent
     * 
     * @param eventCountDownLatch
     */
    private static void waitForEventsToBeSent(CountDownLatch eventCountDownLatch) {
        try {
            eventCountDownLatch.await();
        } catch (final InterruptedException exc) {
            System.out.println(exc);
        }
    }


    /**
     * wait to receive the events in the consumer side
     * 
     * @param probingConsumer
     * @throws InterruptedException
     */
    private static void waitForEventsToReachConsumer(SimpleConsumerHandler probingConsumer) throws InterruptedException {
        while (probingConsumer.getNoReceivedEvents() < TOTAL_NUM_OF_EVENTS) {
            waitTime(1000);
        }

    }


    /**
     * creates the event handlers consumers and registers them
     * 
     * @param consumerNumber
     */
    private void createEventConsumers(int consumerNumber) {
        final VismoEventRegistry reg = new VismoEventRegistry(socketFactory, "tcp://127.0.0.1:" + conf.getConsumersPort());
        if (consumerNumber > 0)
            for (int j = 0; j < consumerNumber; j++) {
                final SimpleConsumerHandler consumer = new SimpleConsumerHandler();
                consumers.add(consumer);
                reg.registerToAll(consumer);
            }
    }


    /**
     * setup the test infrastructure.
     * 
     * @throws IOException
     * @throws InterruptedException
     */
    @Before
    public void setUp() throws IOException, InterruptedException {
        socketFactory = new ZMQFactory(new ZContext());
        final VismoRulesEngine engine = new VismoRulesEngine(new RulesStore());
        submitRulesToEngine(engine, LoadedRulesSet.PASSTHROUGH);
        final ClusterHeadNodeFactory serviceFactory = new ClusterHeadNodeFactory(conf, socketFactory, engine);
        service = serviceFactory.build(new VismoVMInfo());
        service.start();
    }


    /**
     * tear down the infrastructure.
     * 
     * @throws InterruptedException
     */
    @After
    public void tearDown() throws InterruptedException {
        if (service != null)
            service.halt();
        try {
            Thread.sleep(1000);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
        socketFactory.destroy();
    }


    /**
     * process the events latency
     * 
     * @param eventsStore
     */
    @SuppressWarnings("unused")
    private static void statisticallyProcessEvents(ConcurrentHashMap<Long, Long> eventsStore) {
        SortedSet<Long> keys = new TreeSet<Long>(eventsStore.keySet());
        for (Long key : keys) {
            Long value = eventsStore.get(key);
        }
    }


    /**
     * waits till events to be received
     * 
     * @param waitTime
     * @throws InterruptedException
     */
    private static void waitTime(final int waitTime) throws InterruptedException {
        Thread.sleep(waitTime);
    }


    /**
     * loads the rules engine with different load in terms of rules.
     * 
     * @param engine
     * @param type
     */
    private static void submitRulesToEngine(VismoRulesEngine engine, LoadedRulesSet type) {
        switch(type){
            case PASSTHROUGH:
                new PassThroughRule(engine).submit();
                break;
            case SMALL_SET:
                for (int j = 1; j < 10; j++) {
                    new CTORule(engine, "cto" + j, 10).submit();
                    new AccountingRule(engine, j + 1).submit();
                    new AccountingRule(engine, j + 2).submit();
                    new AccountingRule(engine, j + 3).submit();
                    new CTORule(engine, "cto*" + j, 1).submit();
                }
                break;
            case MEDIUM_SET:
                for (int j = 1; j < 100; j++) {
                    new CTORule(engine, "cto" + j, 10).submit();
                    new AccountingRule(engine, j + 1).submit();
                    new AccountingRule(engine, j + 2).submit();
                    new AccountingRule(engine, j + 3).submit();
                    new CTORule(engine, "cto*" + j, 1).submit();
                }
                break;
            case INTENSIVE_SET:
                for (int j = 1; j < 1000; j++) {
                    new CTORule(engine, "cto" + j, 10).submit();
                    new AccountingRule(engine, j + 1).submit();
                    new AccountingRule(engine, j + 2).submit();
                    new AccountingRule(engine, j + 3).submit();
                    new CTORule(engine, "cto*" + j, 1).submit();
                }
            default:
                new PassThroughRule(engine).submit();
                break;
        }

    }


    /**
     * get a random consumer id
     * 
     * @param consumers
     * @return an int
     */
    private static int getRandomConsumerNum(ArrayList<SimpleConsumerHandler> consumers) {
        if (consumers.size() > 0) {
            return ((int) Math.floor((consumers.size() - 1) * Math.random()));
        }
        return 0;
    }

}
