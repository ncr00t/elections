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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TestRemoveCandidateDtoRequest {
    private static String token;
    private static DataBase dataBase;

    @BeforeClass
    public static void setUpClass(){
        dataBase = DataBase.getDataBase();
    }

    @Before
    public void setUp(){
        dataBase.getTokensAndVoters().clear();
        dataBase.getCandidates().clear();
        token = GenerateTokenService.generateNewToken();
    }

    @Test
    public void testLogoutDtoRequest() throws VoterException {
        RemoveCandidateDtoRequest removeCandidateDtoRequest = new RemoveCandidateDtoRequest(0, token);
        assertEquals(token, removeCandidateDtoRequest.getCandidateToken());
        assertEquals(0, removeCandidateDtoRequest.getCandidateId());

        String newToken =  GenerateTokenService.generateNewToken();
        removeCandidateDtoRequest.setCandidateToken(newToken);
        assertEquals(newToken, removeCandidateDtoRequest.getCandidateToken());

        removeCandidateDtoRequest.setCandidateId(1);
        assertEquals(1, removeCandidateDtoRequest.getCandidateId());

        removeCandidateDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
        assertEquals(dataBase.getTokensAndVoters(), removeCandidateDtoRequest.getTokensAndVoters());

        removeCandidateDtoRequest.setCandidates(dataBase.getCandidates());
        assertEquals(dataBase.getCandidates(), removeCandidateDtoRequest.getCandidates());
    }

    @Test
    public void testValidateIfTokenIsOfflineToken() {
        dataBase.getTokensAndVoters().clear();
        try {
            RemoveCandidateDtoRequest removeCandidateDtoRequest1 = new RemoveCandidateDtoRequest(0, token);
            assertEquals(token, removeCandidateDtoRequest1.getCandidateToken());
            assertEquals(0, removeCandidateDtoRequest1.getCandidateId());

            Candidate candidate = new Candidate("alex", "alexandrov", "123456789");
            dataBase.insertCandidate(candidate);
            removeCandidateDtoRequest1.setCandidates(dataBase.getCandidates());

            removeCandidateDtoRequest1.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(0, removeCandidateDtoRequest1.getTokensAndVoters().size());

            removeCandidateDtoRequest1.validate();
            fail();
        } catch (VoterException ex) {
            assertEquals(VoterErrorCode.OFFLINE_TOKEN, ex.getVoterErrorCode());
        }
    }

    @Test
    public void testValidateIfCandidateNotContainsInCandidates() {
        try {
            RemoveCandidateDtoRequest removeCandidateDtoRequest1 = new RemoveCandidateDtoRequest(0, token);
            assertEquals(0, removeCandidateDtoRequest1.getCandidateId());
            assertEquals(token, removeCandidateDtoRequest1.getCandidateToken());

            dataBase.getTokensAndVoters().put(token, new Candidate("sergei","sergeev", "123456789"));
            removeCandidateDtoRequest1.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(1, removeCandidateDtoRequest1.getTokensAndVoters().size());

            removeCandidateDtoRequest1.setCandidates(dataBase.getCandidates());

            removeCandidateDtoRequest1.validate();
            fail();
        } catch (VoterException ex) {
            assertEquals(VoterErrorCode.CANDIDATE_NOT_FOUND_BY_TOKEN, ex.getVoterErrorCode());
        }
    }
}
