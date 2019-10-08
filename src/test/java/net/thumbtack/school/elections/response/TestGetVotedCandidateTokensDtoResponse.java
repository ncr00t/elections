package net.thumbtack.school.elections.response;

import net.thumbtack.school.elections.database.DataBase;
import net.thumbtack.school.elections.model.Candidate;
import org.junit.Before;
import org.junit.Test;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class TestGetVotedCandidateTokensDtoResponse {

    private DataBase dataBase;

    @Before
    public void setUp(){
        dataBase = DataBase.getDataBase();
        dataBase.getVotedCandidates().clear();
    }

    @Test
    public void testGetVotedCandidateTokensDtoResponse() {
        GetVotedCandidatesDtoResponse getVotedCandidatesDtoResponse = new GetVotedCandidatesDtoResponse();
        Set<Candidate> votedCandidates = dataBase.getVotedCandidates();
        getVotedCandidatesDtoResponse.setVotedCandidates(votedCandidates);
        assertEquals(0, getVotedCandidatesDtoResponse.getVotedCandidates().size());
    }
}
