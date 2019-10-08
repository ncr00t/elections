package net.thumbtack.school.elections.response;

import net.thumbtack.school.elections.model.Candidate;
import java.util.Set;

public class GetVotedCandidatesDtoResponse {

    private Set<Candidate> votedCandidates;

    public GetVotedCandidatesDtoResponse(Set<Candidate> votedCandidates) {
        this.votedCandidates = votedCandidates;
    }

    public GetVotedCandidatesDtoResponse() {

    }

    public Set<Candidate> getVotedCandidates() {
        return votedCandidates;
    }

    public void setVotedCandidates(Set<Candidate> votedCandidates) {
        this.votedCandidates = votedCandidates;
    }
}
