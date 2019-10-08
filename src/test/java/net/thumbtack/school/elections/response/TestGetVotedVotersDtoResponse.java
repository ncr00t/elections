package net.thumbtack.school.elections.response;

import net.thumbtack.school.elections.database.DataBase;
import net.thumbtack.school.elections.model.Voter;
import org.junit.Before;
import org.junit.Test;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class TestGetVotedVotersDtoResponse {

    private DataBase dataBase;

    @Before
    public void setUp(){
        dataBase = DataBase.getDataBase();
        dataBase.getVotedVoters().clear();
    }

    @Test
    public void testGetVotedVoterTokensDtoResponseDtoResponse() {
        GetVotedVotersDtoResponse getVotedVotersDtoResponse = new GetVotedVotersDtoResponse();
        Set<Voter> votedVoters = dataBase.getVotedVoters();
        getVotedVotersDtoResponse.setTokensAndVoters(votedVoters);
        assertEquals(0, getVotedVotersDtoResponse.getVotedVoters().size());
    }
}
