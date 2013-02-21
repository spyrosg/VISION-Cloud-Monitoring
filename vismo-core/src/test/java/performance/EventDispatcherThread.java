package performance;

import gr.ntua.vision.monitoring.dispatch.EventBuilder;
import gr.ntua.vision.monitoring.dispatch.EventDispatcher;
import integration.tests.FakeObjectService.Operation;
import integration.tests.FakeObjectService.Status;

import java.util.Random;
import java.util.concurrent.CountDownLatch;


/**
 * @author tmessini
 */
public class EventDispatcherThread implements Runnable {

    /** the max request/response duration (in seconds). */
    private static final int      MAX_DURATION = 100;
    /** the max request content size (in bytes). */
    private static final int      MAX_SIZE     = 1000000;
    /***/
    private static final String   SERVICE_NAME = "performance-test";
    /***/
    CountDownLatch                latch;
    /***/
    private final EventDispatcher dispatcher;
    /***/
    private final Random          rng;


    /**
     * @param dispatcher
     * @param rng
     * @param latch
     */
    public EventDispatcherThread(final EventDispatcher dispatcher, final Random rng, final CountDownLatch latch) {
        this.rng = rng;
        this.dispatcher = dispatcher;
        this.latch = latch;
    }


    @Override
    public void run() {
        while (latch.getCount() > 0) {
            final int randomNum = rand(EventDispatcherThread.MAX_DURATION);
            final EventBuilder builder = randomEvent(getWeightedRandomOp(randomNum), getWeightedRandomTenant(),
                                                     getWeightedRandomUser(randomNum), getWeightedRandomContainer(randomNum),
                                                     getWeightedRandomObject(), getWeightedRandomSt(randomNum));
            builder.send();
            latch.countDown();
        }

    }


    /**
     * returns a container name
     * 
     * @param random
     * @return string
     */
    private static String getWeightedRandomContainer(final int random) {
        if (random % 8 == 0)
            return "container1";
        if (random % 8 == 1)
            return "container2";
        if (random % 8 == 2)
            return "container3";
        if (random % 8 == 3)
            return "container4";
        if (random % 8 == 4)
            return "container5";
        if (random % 8 == 5)
            return "container6";
        if (random % 8 == 6)
            return "container7";
        if (random % 8 == 7)
            return "container8";
        return null;
    }


    /**
     * returns a random object.
     * 
     * @return string System.out.println("unblocked here!");
     */
    private static String getWeightedRandomObject() {
        return "bar-object";
    }


    /**
     * returns a weighted random operation.
     * 
     * @param random
     * @return {@link Operation}
     */
    private static Operation getWeightedRandomOp(final int random) {
        if (random % 3 == 0)
            return Operation.PUT;
        if (random % 3 == 1)
            return Operation.GET;
        if (random % 3 == 2)
            return Operation.DELETE;
        return null;
    }


    /**
     * returns a weighted status.
     * 
     * @param random
     * @return {@link Status}
     */
    private static Status getWeightedRandomSt(final int random) {
        if (random % 1 == 0)
            return Status.SUCCESS;
        if (random % 2 == 0)
            return Status.ERROR;
        return null;
    }


    /**
     * returns a random tenant.
     * 
     * @return string
     */
    private static String getWeightedRandomTenant() {
        return "ntua";
    }


    /**
     * returns a random user
     * 
     * @param random
     * @return string
     */
    private static String getWeightedRandomUser(final int random) {
        if (random % 4 == 0)
            return "theo";
        if (random % 4 == 1)
            return "bill";
        if (random % 4 == 2)
            return "niki";
        if (random % 4 == 3)
            return "spyros";
        return null;
    }


    /**
     * @param maxSize
     * @return int
     */
    private int rand(final int maxSize) {
        return rng.nextInt(maxSize + 1);
    }


    /**
     * @param op
     * @param tenant
     * @param user
     * @param container
     * @param object
     * @param st
     * @return an event
     */
    private EventBuilder randomEvent(final Operation op, final String tenant, final String user, final String container,
            final String object, final Status st) {
        final int contentSize = rand(EventDispatcherThread.MAX_SIZE);

        return dispatcher.newEvent().field("latch-id", latch.getCount()).field("originating-service", EventDispatcherThread.SERVICE_NAME)
                .field("originating-machine", "localhost").field("operation", op.toString()).field("tenant", tenant)
                .field("user", user).field("container", container).field("object", object).field("status", st.toString())
                .field("content-size", contentSize).field("timestamp", System.currentTimeMillis());
    }


    /**
     * returns the name of the thread.
     * 
     * @return the name of the thread
     */
    public static String getThreadName() {
        Thread t = Thread.currentThread();
        String name = t.getName();
        return name;
    }
}
