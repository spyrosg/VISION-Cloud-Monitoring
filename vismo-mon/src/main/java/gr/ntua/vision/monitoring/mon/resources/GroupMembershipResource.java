package gr.ntua.vision.monitoring.mon.resources;

import gr.ntua.vision.monitoring.mon.GroupElement;
import gr.ntua.vision.monitoring.mon.GroupMembership;
import gr.ntua.vision.monitoring.mon.GroupProc;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


/**
 *
 */
@Path("/members")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GroupMembershipResource {
    /***/
    private final GroupMembership mship;


    /**
     * Constructor.
     * 
     * @param mship
     */
    public GroupMembershipResource(final GroupMembership mship) {
        this.mship = mship;
    }


    /**
     * @return the list of members in the group.
     */
    @GET
    public List<GroupElement> getMembers() {
        final ArrayList<GroupElement> members = new ArrayList<GroupElement>();

        mship.forEach(new GroupProc() {
            @Override
            public void applyTo(final GroupElement member) {
                members.add(member);
            }
        });

        return members;
    }
}
