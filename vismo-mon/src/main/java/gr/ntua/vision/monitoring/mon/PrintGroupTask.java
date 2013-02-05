package gr.ntua.vision.monitoring.mon;

import java.util.Date;
import java.util.TimerTask;


/**
 * This is used to periodically report the active member list.
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
        System.out.println(new Date() + " printing group members:");

        mship.forEach(new GroupProc() {
            @Override
            public void applyTo(final GroupElement member) {
                System.out.println(member);
            }
        });

        System.out.println("***\n");
    }
}
