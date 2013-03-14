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

import org.junit.Before;
import org.junit.Test;
import org.zeromq.ZContext;


/**
 * 
 */
public class PythonDispatchTest {
    /***/
    private static class NoEventsSourceListener implements EventSourceListener {
        /***/
        private final int noExpectedEvents;
        /***/
        private int       noReceivedEvents = 0;


        /**
         * @param noExpectedEvents
         */
        public NoEventsSourceListener(final int noExpectedEvents) {
            this.noExpectedEvents = noExpectedEvents;
        }


        /**
         * 
         */
        public void haveExpectedNoEvents() {
            assertEquals(noExpectedEvents, noReceivedEvents);
        }


        /**
         * @see gr.ntua.vision.monitoring.sources.EventSourceListener#receive(gr.ntua.vision.monitoring.events.MonitoringEvent)
         */
        @Override
        public void receive(final MonitoringEvent e) {
            if (e != null)
                ++noReceivedEvents;
        }
    }
    /***/
    private static final int       NO_EVENTS_TO_SEND = 10;
    /***/
    private static final String    PY_DISPATCH       = "../vismo-dispatch/src/main/python/vismo_dispatch.py";
    /***/
    private static final String    PYTHON            = "/usr/local/bin/python";
    /***/
    private static final String    VISMO_CONFIG_FILE = "src/test/resources/vismo-config.properties";
    /***/
    private VismoConfiguration     conf;
    /***/
    private final ZMQFactory       factory           = new ZMQFactory(new ZContext());
    /***/
    private NoEventsSourceListener listener;

    /***/
    private VismoEventSource       source;


    /**
     * @throws IOException
     * @throws InterruptedException
     */
    @Before
    public void setUp() throws IOException, InterruptedException {
        requirePythonVersion("2.6");
        conf = new VismoConfiguration(VISMO_CONFIG_FILE);
        source = new VismoEventSource(factory.newBoundPullSocket(conf.getProducersPoint()), factory.newConnectedPushSocket(conf
                .getProducersPoint()));
        listener = new NoEventsSourceListener(NO_EVENTS_TO_SEND);
        source.add(listener);
    }


    /**
     * @throws InterruptedException
     * @throws IOException
     */
    @Test
    public void sourceReceivesEventsFromPyDispatch() throws IOException, InterruptedException {
        source.start();
        runPythonVismoDispatch();
        Thread.sleep(300);
        listener.haveExpectedNoEvents();
    }


    /**
     * @param version
     * @throws IOException
     * @throws InterruptedException
     */
    private static void requirePythonVersion(final String version) throws IOException, InterruptedException {
        final double requiredVersion = Double.valueOf(version.substring(0, 3));
        final ProcessBuilder builder = new ProcessBuilder(PYTHON, "--version");

        builder.redirectErrorStream(true);

        final Process proc = builder.start();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        final OutputStreamWriter writer = new OutputStreamWriter(proc.getOutputStream());

        writer.flush();

        final String[] fs = reader.readLine().split(" ");
        final double actualVersion = Double.valueOf(fs[1].substring(0, 3));

        System.err.println("python version: " + fs[1]);
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
     * @throws IOException
     * @throws InterruptedException
     */
    private static void runPythonVismoDispatch() throws IOException, InterruptedException {
        final ProcessBuilder builder = new ProcessBuilder(PYTHON, PY_DISPATCH, String.valueOf(NO_EVENTS_TO_SEND));

        builder.environment().put("VISMO_CONFIG", VISMO_CONFIG_FILE);
        builder.redirectErrorStream(true);

        final Process proc = builder.start();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        final OutputStreamWriter writer = new OutputStreamWriter(proc.getOutputStream());

        writer.flush();

        for (String s = reader.readLine(); s != null; s = reader.readLine())
            System.err.println(proc + " >> " + s);

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
