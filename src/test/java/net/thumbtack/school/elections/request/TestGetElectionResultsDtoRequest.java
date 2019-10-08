package net.thumbtack.school.elections.request;

import net.thumbtack.school.elections.database.DataBase;
import net.thumbtack.school.elections.exception.VoterErrorCode;
import net.thumbtack.school.elections.exception.VoterException;
import net.thumbtack.school.elections.model.Voter;
import net.thumbtack.school.elections.response.RegisterVoterDtoResponse;
import net.thumbtack.school.elections.service.GenerateTokenService;
import org.junit.BeforeClass;
import org.junit.Test;


import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TestGetElectionResultsDtoRequest {

    private static DataBase dataBase;

    @BeforeClass
    public static void setUpClass(){
        dataBase = DataBase.getDataBase();
    }

    @Test
    public void testGetCandidatesAndProgramsDtoRequest() {
        String token = GenerateTokenService.generateNewToken();
        GetElectionResultsDtoRequest getElectionResultsDtoRequest = new GetElectionResultsDtoRequest(token);
        assertEquals(token, getElectionResultsDtoRequest.getToken());

        String newToken = GenerateTokenService.generateNewToken();

        getElectionResultsDtoRequest.setToken(newToken);
        assertEquals(newToken, getElectionResultsDtoRequest.getToken());
    }

    @Test
    public void testValidateWrongToken() {
        dataBase.getTokensAndVoters().clear();
        try {
            GetElectionResultsDtoRequest getElectionResultsDtoRequest = new GetElectionResultsDtoRequest(null);
            assertEquals(null, getElectionResultsDtoRequest.getToken());

            getElectionResultsDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(0, getElectionResultsDtoRequest.getTokensAndVoters().size());

            getElectionResultsDtoRequest.validate();
            fail();
        } catch (VoterException ex) {
            assertEquals(VoterErrorCode.VOTER_WRONG_TOKEN, ex.getVoterErrorCode());
        }

        try {
            GetElectionResultsDtoRequest getElectionResultsDtoRequest = new GetElectionResultsDtoRequest("");
            assertEquals("", getElectionResultsDtoRequest.getToken());

            getElectionResultsDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(0, getElectionResultsDtoRequest.getTokensAndVoters().size());

            getElectionResultsDtoRequest.validate();
            fail();
        }  catch (VoterException ex) {
            assertEquals(VoterErrorCode.VOTER_WRONG_TOKEN, ex.getVoterErrorCode());
        }

        String token = GenerateTokenService.generateNewToken();
        try {
            GetElectionResultsDtoRequest getElectionResultsDtoRequest = new GetElectionResultsDtoRequest(token);
            assertEquals(token, getElectionResultsDtoRequest.getToken());
            getElectionResultsDtoRequest.setToken(null);

            getElectionResultsDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(0, getElectionResultsDtoRequest.getTokensAndVoters().size());

            getElectionResultsDtoRequest.validate();
            fail();
        }  catch (VoterException ex) {
            assertEquals(VoterErrorCode.VOTER_WRONG_TOKEN, ex.getVoterErrorCode());
        }

        try {
            GetElectionResultsDtoRequest getElectionResultsDtoRequest = new GetElectionResultsDtoRequest(token);
            assertEquals(token, getElectionResultsDtoRequest.getToken());
            getElectionResultsDtoRequest.setToken("");

            getElectionResultsDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(0, getElectionResultsDtoRequest.getTokensAndVoters().size());

            getElectionResultsDtoRequest.validate();
            fail();
        }  catch (VoterException ex) {
            assertEquals(VoterErrorCode.VOTER_WRONG_TOKEN, ex.getVoterErrorCode());
        }
    }

    @Test
    public void testValidateOfflineToken() {
        dataBase.getTokensAndVoters().clear();
        try {
            String token1 = GenerateTokenService.generateNewToken();

            GetElectionResultsDtoRequest getElectionResultsDtoRequest = new GetElectionResultsDtoRequest(token1);
            assertEquals(token1, getElectionResultsDtoRequest.getToken());

            Map<String, Voter> tokensAndVoters = dataBase.getTokensAndVoters();
            getElectionResultsDtoRequest.setTokensAndVoters(tokensAndVoters);
            assertEquals(0, getElectionResultsDtoRequest.getTokensAndVoters().size());

            getElectionResultsDtoRequest.validate();
            fail();
        } catch (VoterException ex) {
            assertEquals(VoterErrorCode.OFFLINE_TOKEN, ex.getVoterErrorCode());
        }
    }
}
