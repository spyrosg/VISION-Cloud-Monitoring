package gr.ntua.vision.monitoring.cluster;

import java.util.Set;

import org.apache.log4j.Logger;

import com.google.common.collect.Sets;


/**
 * This is responsible for scheduling all live probes and running them at the appropriate times.
 */
public class Scheduler implements Runnable {
    /** the logger. */
    @SuppressWarnings("all")
    private static final Logger log    = Logger.getLogger( Scheduler.class );
    /** the probes to be scheduled. */
    final Set<ProbeExecutor>    probes = Sets.newHashSet();


    /**
     * c/tor.
     */
    Scheduler() {
        // NOP
    }


    /**
     * remove the given executor.
     * 
     * @param pexc
     *            the executor to remove.
     */
    public void remove(final ProbeExecutor pexc) {
        synchronized( probes ) {
            probes.remove( pexc );
        }
    }


    /**
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
        log.info( "Start" );
        while( !Thread.interrupted() )
            try {
                Thread.sleep( 1000 );

                final Set<ProbeExecutor> executors = Sets.newHashSet();
                synchronized( probes ) {
                    executors.addAll( probes );
                }
                for( final ProbeExecutor x : executors )
                    try {
                        x.tick();
                    } catch( final Throwable err ) {
                        log.error( err );
                        log.warn( "ignoring last error" );
                    }
            } catch( final InterruptedException x ) {
                break;
            } catch( final Throwable x ) {
                // ignore
                x.printStackTrace();
            }

        log.info( "Stop" );
    }
}