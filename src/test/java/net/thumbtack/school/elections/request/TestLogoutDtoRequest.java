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
import org.junit.Ignore;
import org.junit.Test;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TestLogoutDtoRequest {

    private static String token;
    private static DataBase dataBase;

    @BeforeClass
    public static void setUpClass(){
        token = GenerateTokenService.generateNewToken();
        dataBase = DataBase.getDataBase();
    }

    @Before
    public void setUp(){
        dataBase.getTokensAndVoters().clear();
        dataBase.getCandidates().clear();
    }

    @Test
    public void testLogoutDtoRequest() throws VoterException {
        LogoutDtoRequest logoutDtoRequest = new LogoutDtoRequest("ivan", token);
        assertEquals("ivan", logoutDtoRequest.getLogin());
        assertEquals(token, logoutDtoRequest.getToken());

        String newToken = GenerateTokenService.generateNewToken();

        logoutDtoRequest.setToken(newToken);
        assertEquals(newToken, logoutDtoRequest.getToken());
    }

    @Test
    public void testValidateWrongVoterToken() {
        dataBase.getTokensAndVoters().clear();
        try {
            LogoutDtoRequest logoutDtoRequest1 = new LogoutDtoRequest("ivan", null);
            dataBase.getTokensAndVoters().put(logoutDtoRequest1.getToken(), new Voter("ivan", "ivanov", "123456789"));
            logoutDtoRequest1.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(1, logoutDtoRequest1.getTokensAndVoters().size());

            logoutDtoRequest1.validate();
            fail();
        } catch (VoterException ex) {
            assertEquals(VoterErrorCode.VOTER_WRONG_TOKEN, ex.getVoterErrorCode());
        }

        dataBase.getTokensAndVoters().clear();
        try {
            LogoutDtoRequest logoutDtoRequest2 = new LogoutDtoRequest("ivan", "");
            dataBase.getTokensAndVoters().put(logoutDtoRequest2.getToken(), new Voter("ivan", "ivanov", "123456789"));
            logoutDtoRequest2.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(1, logoutDtoRequest2.getTokensAndVoters().size());

            logoutDtoRequest2.validate();
            fail();
        }  catch (VoterException ex) {
            assertEquals(VoterErrorCode.VOTER_WRONG_TOKEN, ex.getVoterErrorCode());
        }

        dataBase.getTokensAndVoters().clear();
        try {
            LogoutDtoRequest logoutDtoRequest2 = new LogoutDtoRequest("ivan", token);
            logoutDtoRequest2.setToken(null);
            dataBase.getTokensAndVoters().put(logoutDtoRequest2.getToken(), new Voter("ivan", "ivanov", "123456789"));
            logoutDtoRequest2.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(1, logoutDtoRequest2.getTokensAndVoters().size());

            logoutDtoRequest2.validate();
            fail();
        }  catch (VoterException ex) {
            assertEquals(VoterErrorCode.VOTER_WRONG_TOKEN, ex.getVoterErrorCode());
        }

        dataBase.getTokensAndVoters().clear();
        try {
            LogoutDtoRequest logoutDtoRequest2 = new LogoutDtoRequest("ivan", token);
            logoutDtoRequest2.setToken("");
            dataBase.getTokensAndVoters().put(logoutDtoRequest2.getToken(), new Voter("ivan", "ivanov", "123456789"));
            logoutDtoRequest2.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(1, logoutDtoRequest2.getTokensAndVoters().size());

            logoutDtoRequest2.validate();
            fail();
        }  catch (VoterException ex) {
            assertEquals(VoterErrorCode.VOTER_WRONG_TOKEN, ex.getVoterErrorCode());
        }
    }

    @Test
    public void testValidateWrongLogin() {
        try {
            LogoutDtoRequest logoutDtoRequest1 = new LogoutDtoRequest(null, token);
            logoutDtoRequest1.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(0, logoutDtoRequest1.getTokensAndVoters().size());

            logoutDtoRequest1.validate();
            fail();
        } catch (VoterException ex) {
            assertEquals(VoterErrorCode.VOTER_WRONG_LOGIN, ex.getVoterErrorCode());
        }

        try {
            LogoutDtoRequest logoutDtoRequest2 = new LogoutDtoRequest("", token);
            logoutDtoRequest2.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(0, logoutDtoRequest2.getTokensAndVoters().size());

            logoutDtoRequest2.validate();
            fail();
        }  catch (VoterException ex) {
            assertEquals(VoterErrorCode.VOTER_WRONG_LOGIN, ex.getVoterErrorCode());
        }

        try {
            LogoutDtoRequest logoutDtoRequest3 = new LogoutDtoRequest("ivan", token);
            logoutDtoRequest3.setLogin(null);
            logoutDtoRequest3.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(0, logoutDtoRequest3.getTokensAndVoters().size());

            logoutDtoRequest3.validate();
            fail();
        }  catch (VoterException ex) {
            assertEquals(VoterErrorCode.VOTER_WRONG_LOGIN, ex.getVoterErrorCode());
        }

        try {
            LogoutDtoRequest logoutDtoRequest3 = new LogoutDtoRequest("ivan", token);
            logoutDtoRequest3.setLogin("");
            logoutDtoRequest3.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(0, logoutDtoRequest3.getTokensAndVoters().size());

            logoutDtoRequest3.validate();
            fail();
        }  catch (VoterException ex) {
            assertEquals(VoterErrorCode.VOTER_WRONG_LOGIN, ex.getVoterErrorCode());
        }
    }

    @Test
    public void testValidateOfflineToken() {
        dataBase.getTokensAndVoters().clear();
        try {
            LogoutDtoRequest logoutDtoRequest = new LogoutDtoRequest("ivan", token);
            logoutDtoRequest.setToken(token);
            logoutDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(0, logoutDtoRequest.getTokensAndVoters().size());

            logoutDtoRequest.validate();
            fail();
        } catch (VoterException ex) {
            assertEquals(VoterErrorCode.OFFLINE_TOKEN, ex.getVoterErrorCode());
        }
    }

    @Test
    public void testValidateWrongCandidateToken() {
        try {
            Candidate candidate = new Candidate("denis","denisov","12345678");
            assertEquals("denis", candidate.getFirstName());
            assertEquals("denisov", candidate.getLogin());
            assertEquals("12345678", candidate.getPassword());

            dataBase.insertCandidate(candidate);
            assertEquals(1, dataBase.getCandidates().size());

            LogoutDtoRequest logoutDtoRequest1 = new LogoutDtoRequest("denisov", token);
            dataBase.getTokensAndVoters().put(token, candidate);
            logoutDtoRequest1.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(1, logoutDtoRequest1.getTokensAndVoters().size());

            String candidateToken = dataBase.getTokenByLogin(logoutDtoRequest1.getLogin(), dataBase.getTokensAndVoters());
            logoutDtoRequest1.setCandidateToken(candidateToken);
            assertEquals(candidateToken, logoutDtoRequest1.getCandidateToken());

            logoutDtoRequest1.validate();
            fail();
        } catch (VoterException ex) {
            assertEquals(VoterErrorCode.VOTER_IS_CANDIDATE, ex.getVoterErrorCode());
        }
    }
}
