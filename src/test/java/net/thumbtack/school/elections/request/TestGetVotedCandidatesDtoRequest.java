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

public class TestGetVotedCandidatesDtoRequest {

    private static String candidateToken;
    private static DataBase dataBase;

    @BeforeClass
    public static void setUpClass(){
        dataBase = DataBase.getDataBase();
        candidateToken = GenerateTokenService.generateNewToken();
    }

    @Before
    public void setUp(){
        dataBase.getTokensAndVoters().clear();
        dataBase.getVotedCandidates().clear();
    }

    @Test
    public void testGetVotedCandidateTokensDtoRequest() {
        GetVotedCandidatesDtoRequest getVotedCandidatesDtoRequest = new GetVotedCandidatesDtoRequest(candidateToken);
        assertEquals(candidateToken, getVotedCandidatesDtoRequest.getCandidateToken());
        getVotedCandidatesDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
        assertEquals(0, getVotedCandidatesDtoRequest.getTokensAndVoters().size());

        String newCandidateToken = GenerateTokenService.generateNewToken();
        getVotedCandidatesDtoRequest.setCandidateToken(newCandidateToken);
        assertEquals(newCandidateToken, getVotedCandidatesDtoRequest.getCandidateToken());
    }

    @Test
    public void testValidateWrongCandidateToken() {
        dataBase.getTokensAndVoters().clear();
        try {
            GetVotedCandidatesDtoRequest getVotedCandidatesDtoRequest = new GetVotedCandidatesDtoRequest("");
            assertEquals("", getVotedCandidatesDtoRequest.getCandidateToken());
            dataBase.getTokensAndVoters().put(getVotedCandidatesDtoRequest.getCandidateToken(), new Candidate("ivan", "ivanov", "123456789"));
            getVotedCandidatesDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(1, getVotedCandidatesDtoRequest.getTokensAndVoters().size());

            getVotedCandidatesDtoRequest.validate();
            fail();
        } catch (VoterException ex) {
            assertEquals(VoterErrorCode.CANDIDATE_WRONG_TOKEN, ex.getVoterErrorCode());
        }

        dataBase.getTokensAndVoters().clear();
        try {
            GetVotedCandidatesDtoRequest getVotedCandidatesDtoRequest = new GetVotedCandidatesDtoRequest(null);
            assertEquals(null, getVotedCandidatesDtoRequest.getCandidateToken());
            dataBase.getTokensAndVoters().put(getVotedCandidatesDtoRequest.getCandidateToken(), new Candidate("ivan", "ivanov", "123456789"));
            getVotedCandidatesDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(1, getVotedCandidatesDtoRequest.getTokensAndVoters().size());

            getVotedCandidatesDtoRequest.validate();
            fail();
        }  catch (VoterException ex) {
            assertEquals(VoterErrorCode.CANDIDATE_WRONG_TOKEN, ex.getVoterErrorCode());
        }

        dataBase.getTokensAndVoters().clear();
        try {
            GetVotedCandidatesDtoRequest getVotedCandidatesDtoRequest = new GetVotedCandidatesDtoRequest(candidateToken);
            getVotedCandidatesDtoRequest.setCandidateToken("");
            dataBase.getTokensAndVoters().put(getVotedCandidatesDtoRequest.getCandidateToken(), new Candidate("ivan", "ivanov", "123456789"));
            getVotedCandidatesDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(1, getVotedCandidatesDtoRequest.getTokensAndVoters().size());

            getVotedCandidatesDtoRequest.validate();
            fail();
        }  catch (VoterException ex) {
            assertEquals(VoterErrorCode.CANDIDATE_WRONG_TOKEN, ex.getVoterErrorCode());
        }

        dataBase.getTokensAndVoters().clear();
        try {
            GetVotedCandidatesDtoRequest getVotedCandidatesDtoRequest = new GetVotedCandidatesDtoRequest(candidateToken);
            getVotedCandidatesDtoRequest.setCandidateToken(null);
            dataBase.getTokensAndVoters().put(getVotedCandidatesDtoRequest.getCandidateToken(), new Candidate("ivan", "ivanov", "123456789"));
            getVotedCandidatesDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(1, getVotedCandidatesDtoRequest.getTokensAndVoters().size());

            getVotedCandidatesDtoRequest.validate();
            fail();
        }  catch (VoterException ex) {
            assertEquals(VoterErrorCode.CANDIDATE_WRONG_TOKEN, ex.getVoterErrorCode());
        }
    }

    @Test
    public void testValidateOfflineCandidateToken() {
        dataBase.getTokensAndVoters().clear();
        try {
            GetVotedCandidatesDtoRequest getVotedCandidatesDtoRequest = new GetVotedCandidatesDtoRequest(candidateToken);

            Map<String, Voter> tokensAndVoters = dataBase.getTokensAndVoters();
            getVotedCandidatesDtoRequest.setTokensAndVoters(tokensAndVoters);
            assertEquals(0, getVotedCandidatesDtoRequest.getTokensAndVoters().size());

            getVotedCandidatesDtoRequest.validate();
            fail();
        } catch (VoterException ex) {
            assertEquals(VoterErrorCode.OFFLINE_TOKEN, ex.getVoterErrorCode());
        }
    }
}
