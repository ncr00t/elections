package net.thumbtack.school.elections.response;

import net.thumbtack.school.elections.model.Candidate;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestGetElectionResultsDtoResponse {

    @Test
    public void testGetCandidatesAndProgramsDtoResponse() {
        GetElectionResultsDtoResponse getElectionResultsDtoResponse = new GetElectionResultsDtoResponse();
        Candidate selectedCandidate = new Candidate("ivan", "ivanov", "12345678");
        getElectionResultsDtoResponse.setSelectedCandidate(selectedCandidate);
        assertEquals(selectedCandidate, getElectionResultsDtoResponse.getSelectedCandidate());
    }
}
