package net.thumbtack.school.elections.response;

import net.thumbtack.school.elections.model.Candidate;
import java.util.Map;

public class AllCandidatesDtoResponse {

    private Map<Integer, Candidate> allCandidates;

    public AllCandidatesDtoResponse(Map<Integer, Candidate> allCandidates) {
        this.allCandidates = allCandidates;
    }

    public AllCandidatesDtoResponse() {

    }

    public void setAllCandidates(Map<Integer, Candidate> allCandidates) {
        this.allCandidates = allCandidates;
    }

    public Map<Integer, Candidate> getAllCandidates() {
        return allCandidates;
    }
}
