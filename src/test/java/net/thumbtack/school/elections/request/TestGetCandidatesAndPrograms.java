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

public class TestGetCandidatesAndPrograms {

    private static DataBase dataBase;

    @BeforeClass
    public static void setUpClass(){
        dataBase = DataBase.getDataBase();
    }

    @Test
    public void testGetCandidatesAndProgramsDtoRequest() {
        String token = GenerateTokenService.generateNewToken();
        GetCandidatesAndProgramsDtoRequest candidatesAndProgramsDtoRequest = new GetCandidatesAndProgramsDtoRequest(token);
        assertEquals(token, candidatesAndProgramsDtoRequest.getToken());

        String newToken = GenerateTokenService.generateNewToken();

        candidatesAndProgramsDtoRequest.setToken(newToken);
        assertEquals(newToken, candidatesAndProgramsDtoRequest.getToken());
    }

    @Test
    public void testValidateWrongToken() {
        dataBase.getTokensAndVoters().clear();
        try {
            GetCandidatesAndProgramsDtoRequest candidatesAndProgramsDtoRequest = new GetCandidatesAndProgramsDtoRequest(null);
            assertEquals(null, candidatesAndProgramsDtoRequest.getToken());
            dataBase.getTokensAndVoters().put(candidatesAndProgramsDtoRequest.getToken(), new Voter("ivan", "ivanov", "123456789"));
            candidatesAndProgramsDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(1, candidatesAndProgramsDtoRequest.getTokensAndVoters().size());

            candidatesAndProgramsDtoRequest.validate();
        } catch (VoterException ex) {
            assertEquals(VoterErrorCode.VOTER_WRONG_TOKEN, ex.getVoterErrorCode());
        }

        dataBase.getTokensAndVoters().clear();
        try {
            GetCandidatesAndProgramsDtoRequest candidatesAndProgramsDtoRequest = new GetCandidatesAndProgramsDtoRequest("");
            assertEquals("", candidatesAndProgramsDtoRequest.getToken());

            dataBase.getTokensAndVoters().put(candidatesAndProgramsDtoRequest.getToken(), new Voter("ivan", "ivanov", "123456789"));
            candidatesAndProgramsDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(1, candidatesAndProgramsDtoRequest.getTokensAndVoters().size());

            candidatesAndProgramsDtoRequest.validate();
        }  catch (VoterException ex) {
            assertEquals(VoterErrorCode.VOTER_WRONG_TOKEN, ex.getVoterErrorCode());
        }
    }

    @Test
    public void testValidateOfflineToken() {
        dataBase.getTokensAndVoters().clear();
        try {
            String token1 = GenerateTokenService.generateNewToken();

            GetCandidatesAndProgramsDtoRequest candidatesAndProgramsDtoRequest = new GetCandidatesAndProgramsDtoRequest(token1);
            assertEquals(token1, candidatesAndProgramsDtoRequest.getToken());

            Map<String, Voter> tokensAndVoters = dataBase.getTokensAndVoters();
            candidatesAndProgramsDtoRequest.setTokensAndVoters(tokensAndVoters);
            assertEquals(0, candidatesAndProgramsDtoRequest.getTokensAndVoters().size());

            candidatesAndProgramsDtoRequest.validate();
        } catch (VoterException ex) {
            assertEquals(VoterErrorCode.OFFLINE_TOKEN, ex.getVoterErrorCode());
        }
    }
}
