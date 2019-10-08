package net.thumbtack.school.elections.request;

import net.thumbtack.school.elections.database.DataBase;
import net.thumbtack.school.elections.exception.VoterErrorCode;
import net.thumbtack.school.elections.exception.VoterException;
import net.thumbtack.school.elections.model.Voter;
import net.thumbtack.school.elections.response.RegisterVoterDtoResponse;
import net.thumbtack.school.elections.service.GenerateTokenService;
import org.junit.BeforeClass;
import org.junit.Test;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TestAllCandidatesDtoRequest {

    private static DataBase dataBase;

    @BeforeClass
    public static void setUpClass(){
        dataBase = DataBase.getDataBase();
    }

    @Test
    public void testAllCandidatesDtoRequest() throws VoterException {
        String token = GenerateTokenService.generateNewToken();
        AllCandidatesDtoRequest allCandidatesDtoRequest = new AllCandidatesDtoRequest(token);
        assertEquals(token, allCandidatesDtoRequest.getToken());

        String newToken = GenerateTokenService.generateNewToken();

        allCandidatesDtoRequest.setToken(newToken);
        assertEquals(newToken, allCandidatesDtoRequest.getToken());
    }

    @Test
    public void testValidateWrongToken() {
        dataBase.getTokensAndVoters().clear();
        try {
            AllCandidatesDtoRequest allCandidatesDtoRequest1 = new AllCandidatesDtoRequest(null);
            assertEquals(null, allCandidatesDtoRequest1.getToken());

            dataBase.getTokensAndVoters().put(allCandidatesDtoRequest1.getToken(), new Voter("ivan", "ivanov", "123456789"));
            allCandidatesDtoRequest1.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(1, allCandidatesDtoRequest1.getTokensAndVoters().size());

            allCandidatesDtoRequest1.validate();
            fail();
        } catch (VoterException ex) {
            assertEquals(VoterErrorCode.VOTER_WRONG_TOKEN, ex.getVoterErrorCode());
        }

        dataBase.getTokensAndVoters().clear();
        try {
            AllCandidatesDtoRequest allCandidatesDtoRequest1 = new AllCandidatesDtoRequest("");
            assertEquals("", allCandidatesDtoRequest1.getToken());

            dataBase.getTokensAndVoters().put(allCandidatesDtoRequest1.getToken(), new Voter("ivan", "ivanov", "123456789"));
            allCandidatesDtoRequest1.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(1, allCandidatesDtoRequest1.getTokensAndVoters().size());

            allCandidatesDtoRequest1.validate();
            fail();
        }  catch (VoterException ex) {
            assertEquals(VoterErrorCode.VOTER_WRONG_TOKEN, ex.getVoterErrorCode());
        }
    }

    @Test
    public void testValidateOfflineToken() {
        try {
            String token1 = GenerateTokenService.generateNewToken();

            AllCandidatesDtoRequest allCandidatesDtoRequest1 = new AllCandidatesDtoRequest(token1);
            assertEquals(token1, allCandidatesDtoRequest1.getToken());

            Map<String, Voter> tokensAndVoters = dataBase.getTokensAndVoters();
            allCandidatesDtoRequest1.setTokensAndVoters(tokensAndVoters);
            assertEquals(0, allCandidatesDtoRequest1.getTokensAndVoters().size());

            allCandidatesDtoRequest1.validate();
            fail();
        } catch (VoterException ex) {
            assertEquals(VoterErrorCode.OFFLINE_TOKEN, ex.getVoterErrorCode());
        }
    }
}
