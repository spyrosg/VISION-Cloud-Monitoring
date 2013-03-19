package integration.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import gr.ntua.vision.monitoring.VismoConfiguration;
import gr.ntua.vision.monitoring.events.MonitoringEvent;
import gr.ntua.vision.monitoring.sources.EventSourceListener;
import gr.ntua.vision.monitoring.sources.VismoEventSource;
import gr.ntua.vision.monitoring.zmq.ZMQFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZContext;


/**
 * 
 */
public class PythonDispatchTest {
    /***/
    public static abstract class VerifyingEventsListener implements EventSourceListener {
        /***/
        protected final ArrayList<MonitoringEvent> events           = new ArrayList<MonitoringEvent>();
        /***/
        private final int                          noExpectedEvents;
        /***/
        private int                                noReceivedEvents = 0;


        /**
         * Constructor.
         * 
         * @param noExpectedEvents
         */
        public VerifyingEventsListener(final int noExpectedEvents) {
            this.noExpectedEvents = noExpectedEvents;
        }


        /**
         * @see gr.ntua.vision.monitoring.sources.EventSourceListener#receive(gr.ntua.vision.monitoring.events.MonitoringEvent)
         */
        @Override
        public void receive(final MonitoringEvent e) {
            collect(e);
            ++noReceivedEvents;
        }


        /***/
        public void verifyEvents() {
            haveExpectedNoEvents();
            verifyEventsHelper();
        }


        /***/
        public abstract void verifyEventsHelper();


        /**
         * @param e
         */
        private void collect(final MonitoringEvent e) {
            events.add(e);
        }


        /***/
        private void haveExpectedNoEvents() {
            assertEquals(noExpectedEvents, noReceivedEvents);
        }
    }


    /***/
    private static class MetadataEventsListener extends VerifyingEventsListener {
        /***/
        private static final String GET            = "GET_METADATA";
        /***/
        private static final String PUT            = "PUT_METADATA";
        /***/
        private static final String REQUIRED_FIELD = "metadata-size";


        /**
         * Constructor.
         * 
         * @param noExpectedEvents
         */
        public MetadataEventsListener(final int noExpectedEvents) {
            super(noExpectedEvents);
        }


        /**
         * @see integration.tests.PythonDispatchTest.VerifyingEventsListener#verifyEventsHelper()
         */
        @Override
        public void verifyEventsHelper() {
            for (final MonitoringEvent e : events) {
                if (!(PUT.equals(e.get("operation")) || GET.equals(e.get("operation"))))
                    throw new AssertionError("received unexpected event: " + e + " which is not about metadata");
                if (e.get(REQUIRED_FIELD) == null)
                    throw new AssertionError("received unexpected event: " + e + " with no field '" + REQUIRED_FIELD + "'");
            }
        }
    }


    /***/
    private static class MultiUploadEvents extends VerifyingEventsListener {
        /***/
        private static final String MULTI_PUT_OPERATION = "PUT_MULTI";


        /**
         * Constructor.
         * 
         * @param noExpectedEvents
         */
        public MultiUploadEvents(final int noExpectedEvents) {
            super(noExpectedEvents);
        }


        /**
         * @see integration.tests.PythonDispatchTest.VerifyingEventsListener#verifyEventsHelper()
         */
        @Override
        public void verifyEventsHelper() {
            for (final MonitoringEvent e : events) {
                if (!MULTI_PUT_OPERATION.equals(e.get("operation")))
                    throw new AssertionError("received unexpected event: " + e + " which is not multi upload");

                if (e.get("transaction-throughput") == null)
                    throw new AssertionError("received unexpected event: " + e + " with no field 'transaction-throughput'");
            }
        }
    }


    /***/
    private static class PlainEventsListener extends VerifyingEventsListener {
        /**
         * Constructor.
         * 
         * @param noExpectedEvents
         */
        public PlainEventsListener(final int noExpectedEvents) {
            super(noExpectedEvents);
        }


        /***/
        public void haveExpectedTypeEvents() {
            for (final MonitoringEvent e : events) {
                if (e.get("tag") != null)
                    throw new AssertionError("received unexpected event: " + e + " with field 'tag'");

                if (e.get("transaction-throughput") == null)
                    throw new AssertionError("received unexpected event: " + e + " with no field 'transaction-throughput'");
            }
        }


        /**
         * @see integration.tests.PythonDispatchTest.VerifyingEventsListener#verifyEventsHelper()
         */
        @Override
        public void verifyEventsHelper() {
            haveExpectedTypeEvents();
        }
    }

    /***/
    private static final Logger     log                    = LoggerFactory.getLogger(PythonDispatchTest.class);
    /** this is the flag for updating/getting metadata events. */
    private static final String     META                   = "meta";
    /***/
    private static final String     MINIMUM_PYTHON_VERSION = "2.6";
    /** this is the flag used to push multi upload events. */
    private static final String     MULTI                  = "multi";
    /***/
    private static final int        NO_EVENTS_TO_SEND      = 100;
    /** this is the flag for plain put/get events. */
    private static final String     PLAIN                  = "plain";
    /***/
    private static final String     PY_DISPATCH            = "../vismo-dispatch/src/main/python/vismo_dispatch.py";
    /***/
    private static final String     PYTHON                 = "/usr/bin/python2";
    /***/
    private static final String     VISMO_CONFIG_FILE      = "src/test/resources/vismo-config.properties";
    /***/
    private VismoConfiguration      conf;
    /***/
    private final ZMQFactory        factory                = new ZMQFactory(new ZContext());
    /***/
    private VerifyingEventsListener listener;
    /***/
    private VismoEventSource        source;


    /**
     * @throws IOException
     */
    @Before
    public void setUp() throws IOException {
        conf = new VismoConfiguration(VISMO_CONFIG_FILE);
        source = new VismoEventSource(factory.newBoundPullSocket(conf.getProducersPoint()), factory.newConnectedPushSocket(conf
                .getProducersPoint()));
        source.start();
    }


    /**
     * @throws InterruptedException
     * @throws IOException
     */
    @Test
    public void shouldReceivedMetadataEvents() throws IOException, InterruptedException {
        listener = new MetadataEventsListener(NO_EVENTS_TO_SEND);
        source.add(listener);

        runPythonVismoDispatch(META);
        Thread.sleep(1000);
        listener.verifyEvents();
    }


    /**
     * @throws InterruptedException
     * @throws IOException
     */
    @Test
    public void shouldReceiveMultiUploadEvents() throws IOException, InterruptedException {
        listener = new MultiUploadEvents(NO_EVENTS_TO_SEND);
        source.add(listener);

        runPythonVismoDispatch(MULTI);
        Thread.sleep(1000);
        listener.verifyEvents();
    }


    /**
     * This is the base case feature.
     * 
     * @throws InterruptedException
     * @throws IOException
     */
    @Test
    public void sourceReceivesEventsFromPyDispatch() throws IOException, InterruptedException {
        listener = new PlainEventsListener(NO_EVENTS_TO_SEND);
        source.add(listener);

        runPythonVismoDispatch(PLAIN);
        Thread.sleep(1000);
        listener.verifyEvents();
    }


    /**
     * @throws InterruptedException
     */
    @After
    public void tearDown() throws InterruptedException {
        source.halt();
        Thread.sleep(50); // wait for source sockets to die off.
    }


    /**
     * @throws IOException
     * @throws InterruptedException
     */
    @BeforeClass
    public static void assertEnvironmentHasUsablePython() throws IOException, InterruptedException {
        log.debug("checking minimum python version");
        requirePython(MINIMUM_PYTHON_VERSION);
    }


    /**
     * @param version
     * @throws IOException
     * @throws InterruptedException
     */
    private static void requirePython(final String version) throws IOException, InterruptedException {
        final double requiredVersion = Double.valueOf(version.substring(0, 3));
        final ProcessBuilder builder = new ProcessBuilder(PYTHON, "--version");

        builder.redirectErrorStream(true);

        final Process proc = builder.start();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        final OutputStreamWriter writer = new OutputStreamWriter(proc.getOutputStream());

        writer.flush();

        final String[] fs = reader.readLine().split(" ");
        final double actualVersion = Double.valueOf(fs[1].substring(0, 3));

        log.debug("found python version: " + fs[1]);
        assertTrue("this test should be run against at least python version " + version, actualVersion >= requiredVersion);

        try {
            proc.waitFor();
        } catch (final InterruptedException e) {
            throw e;
        } finally {
            writer.close();
            reader.close();
        }
    }


    /**
     * @param flag
     *            the command line flag that controls which events will be sent.
     * @throws IOException
     * @throws InterruptedException
     */
    private static void runPythonVismoDispatch(final String flag) throws IOException, InterruptedException {
        final ProcessBuilder builder = new ProcessBuilder(PYTHON, PY_DISPATCH, flag, String.valueOf(NO_EVENTS_TO_SEND));

        builder.environment().put("VISMO_CONFIG", VISMO_CONFIG_FILE);
        builder.redirectErrorStream(true);

        final Process proc = builder.start();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        final OutputStreamWriter writer = new OutputStreamWriter(proc.getOutputStream());

        writer.flush();

        for (String s = reader.readLine(); s != null; s = reader.readLine())
            log.debug("{} >> {}", proc, s);

        try {
            final int ret = proc.waitFor();
            assertEquals("process didn't exit normally", 0, ret);
        } catch (final InterruptedException e) {
            throw e;
        } finally {
            reader.close();
            writer.close();
        }
    }
}
