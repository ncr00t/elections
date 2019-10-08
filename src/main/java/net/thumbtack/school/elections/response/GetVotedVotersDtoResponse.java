package net.thumbtack.school.elections.response;

import net.thumbtack.school.elections.model.Voter;
import java.util.Set;

public class GetVotedVotersDtoResponse {

    private Set<Voter> votedVoters;

    public GetVotedVotersDtoResponse(Set<Voter> votedVoters) {
        this.votedVoters = votedVoters;
    }

    public GetVotedVotersDtoResponse() {

    }

    public Set<Voter>  getVotedVoters() {
        return votedVoters;
    }

    public void setTokensAndVoters(Set<Voter> votedVoters) {
        this.votedVoters = votedVoters;
    }
}
