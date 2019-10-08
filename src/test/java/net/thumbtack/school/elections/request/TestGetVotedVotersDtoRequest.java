package net.thumbtack.school.elections.request;

import net.thumbtack.school.elections.database.DataBase;
import net.thumbtack.school.elections.exception.VoterErrorCode;
import net.thumbtack.school.elections.exception.VoterException;
import net.thumbtack.school.elections.model.Candidate;
import net.thumbtack.school.elections.model.Voter;
import net.thumbtack.school.elections.response.RegisterVoterDtoResponse;
import net.thumbtack.school.elections.service.GenerateTokenService;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import java.util.Map;

import static org.junit.Assert.*;

public class TestGetVotedVotersDtoRequest {

    private static String voterToken;
    private static DataBase dataBase;

    @BeforeClass
    public static void setUpClass(){
        dataBase = DataBase.getDataBase();
        voterToken = GenerateTokenService.generateNewToken();
    }

    @Before
    public void setUp(){
        dataBase.getTokensAndVoters().clear();
        dataBase.getVotedVoters().clear();
    }

    @Test
    public void testGetVotedVoterTokensDtoRequest() {
        GetVotedVotersDtoRequest getVotedVotersDtoRequest = new GetVotedVotersDtoRequest(voterToken);
        assertEquals(voterToken, getVotedVotersDtoRequest.getCandidateToken());
        getVotedVotersDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
        assertEquals(0, getVotedVotersDtoRequest.getTokensAndVoters().size());

        String newVoterToken = GenerateTokenService.generateNewToken();
        getVotedVotersDtoRequest.setCandidateToken(newVoterToken);
        assertEquals(newVoterToken, getVotedVotersDtoRequest.getCandidateToken());
    }

    @Test
    public void testValidateWrongVoterToken() {
        dataBase.getTokensAndVoters().clear();
        try {
            GetVotedVotersDtoRequest getVotedVotersDtoRequest = new GetVotedVotersDtoRequest("");
            assertEquals("", getVotedVotersDtoRequest.getCandidateToken());
            dataBase.getTokensAndVoters().put(getVotedVotersDtoRequest.getCandidateToken(), new Candidate("ivan", "ivanov", "123456789"));
            getVotedVotersDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(1, getVotedVotersDtoRequest.getTokensAndVoters().size());

            getVotedVotersDtoRequest.validate();
            fail();
        } catch (VoterException ex) {
            assertEquals(VoterErrorCode.VOTER_WRONG_TOKEN, ex.getVoterErrorCode());
        }

        dataBase.getTokensAndVoters().clear();
        try {
            GetVotedVotersDtoRequest getVotedVotersDtoRequest = new GetVotedVotersDtoRequest(null);
            assertEquals(null, getVotedVotersDtoRequest.getCandidateToken());
            dataBase.getTokensAndVoters().put(getVotedVotersDtoRequest.getCandidateToken(), new Candidate("ivan", "ivanov", "123456789"));
            getVotedVotersDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(1, getVotedVotersDtoRequest.getTokensAndVoters().size());

            getVotedVotersDtoRequest.validate();
            fail();
        }  catch (VoterException ex) {
            assertEquals(VoterErrorCode.VOTER_WRONG_TOKEN, ex.getVoterErrorCode());
        }

        dataBase.getTokensAndVoters().clear();
        try {
            GetVotedVotersDtoRequest getVotedVotersDtoRequest = new GetVotedVotersDtoRequest(voterToken);
            getVotedVotersDtoRequest.setCandidateToken("");
            dataBase.getTokensAndVoters().put(getVotedVotersDtoRequest.getCandidateToken(), new Candidate("ivan", "ivanov", "123456789"));
            getVotedVotersDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(1, getVotedVotersDtoRequest.getTokensAndVoters().size());

            getVotedVotersDtoRequest.validate();
            fail();
        }  catch (VoterException ex) {
            assertEquals(VoterErrorCode.VOTER_WRONG_TOKEN, ex.getVoterErrorCode());
        }

        dataBase.getTokensAndVoters().clear();
        try {
            GetVotedVotersDtoRequest getVotedVotersDtoRequest = new GetVotedVotersDtoRequest(voterToken);
            getVotedVotersDtoRequest.setCandidateToken(null);
            dataBase.getTokensAndVoters().put(getVotedVotersDtoRequest.getCandidateToken(), new Candidate("ivan", "ivanov", "123456789"));
            getVotedVotersDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(1, getVotedVotersDtoRequest.getTokensAndVoters().size());

            getVotedVotersDtoRequest.validate();
            fail();
        }  catch (VoterException ex) {
            assertEquals(VoterErrorCode.VOTER_WRONG_TOKEN, ex.getVoterErrorCode());
        }
    }

    @Test
    public void testValidateOfflineVoterToken() {
        try {
            GetVotedVotersDtoRequest getVotedVotersDtoRequest = new GetVotedVotersDtoRequest(voterToken);

            Map<String, Voter> tokensAndVoters = dataBase.getTokensAndVoters();
            getVotedVotersDtoRequest.setTokensAndVoters(tokensAndVoters);
            assertEquals(0, getVotedVotersDtoRequest.getTokensAndVoters().size());

            getVotedVotersDtoRequest.validate();
            fail();
        } catch (VoterException ex) {
            assertEquals(VoterErrorCode.OFFLINE_TOKEN, ex.getVoterErrorCode());
        }
    }
}
