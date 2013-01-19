package gr.ntua.monitoring.mon;

import java.util.Date;
import java.util.TimerTask;


/**
 * 
 */
public class PrintGroupTask extends TimerTask {
    /***/
    private final GroupMembership mship;


    /**
     * Constructor.
     * 
     * @param mship
     */
    public PrintGroupTask(final GroupMembership mship) {
        this.mship = mship;
    }


    /**
     * @see java.util.TimerTask#run()
     */
    @Override
    public void run() {
        System.out.println(new Date() + " group members:");

        mship.forEach(new GroupProc() {
            @Override
            public void performWith(final GroupElement member) {
                System.out.println("\t" + member);
            }
        });

        System.out.print("\n\n");
    }
}
