package net.thumbtack.school.elections.response;

import net.thumbtack.school.elections.model.Candidate;

public class GetElectionResultsDtoResponse {

    private Candidate selectedCandidate;

    public GetElectionResultsDtoResponse(Candidate selectedCandidate) {
        this.selectedCandidate = selectedCandidate;
    }

    public GetElectionResultsDtoResponse() {

    }

    public Candidate getSelectedCandidate() {
        return selectedCandidate;
    }

    public void setSelectedCandidate(Candidate selectedCandidate) {
        this.selectedCandidate = selectedCandidate;
    }
}
