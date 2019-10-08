package net.thumbtack.school.elections.response;

import net.thumbtack.school.elections.model.Candidate;
import net.thumbtack.school.elections.model.Offer;

import java.util.Map;
import java.util.Set;

public class GetCandidatesAndProgramsDtoResponse {

    private Map<Candidate, Set<Offer>> candidatesAndPrograms;

    public GetCandidatesAndProgramsDtoResponse(Map<Candidate, Set<Offer>> candidatesAndPrograms) {
        this.candidatesAndPrograms = candidatesAndPrograms;
    }

    public GetCandidatesAndProgramsDtoResponse() {

    }

    public Map<Candidate, Set<Offer>> getCandidatesAndPrograms() {
        return candidatesAndPrograms;
    }

    public void setCandidatesAndPrograms(Map<Candidate, Set<Offer>> candidatesAndPrograms) {
        this.candidatesAndPrograms = candidatesAndPrograms;
    }
}
