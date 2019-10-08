package net.thumbtack.school.elections.request;

import net.thumbtack.school.elections.database.DataBase;
import net.thumbtack.school.elections.exception.VoterErrorCode;
import net.thumbtack.school.elections.exception.VoterException;
import net.thumbtack.school.elections.model.Candidate;
import net.thumbtack.school.elections.model.Voter;
import net.thumbtack.school.elections.response.RegisterVoterDtoResponse;
import net.thumbtack.school.elections.service.GenerateTokenService;
import org.junit.BeforeClass;
import org.junit.Test;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TestAddCandidateDtoRequest {

    private static String token;
    private static DataBase dataBase;

    @BeforeClass
    public static void setUpClass(){
        dataBase = DataBase.getDataBase();
        token = GenerateTokenService.generateNewToken();
    }

    @Test
    public void testAddCandidateDtoRequest() throws VoterException {
        AddCandidateDtoRequest addCandidateDtoRequest  = new AddCandidateDtoRequest(0,"ivan", "ivanov",
                                                                                    "12345678", token);
        assertEquals("ivan", addCandidateDtoRequest.getFirstName());
        assertEquals("ivanov", addCandidateDtoRequest.getLogin());
        assertEquals("12345678", addCandidateDtoRequest.getPassword());

        addCandidateDtoRequest.setFirstName("petr");
        assertEquals("petr", addCandidateDtoRequest.getFirstName());

        addCandidateDtoRequest.setLogin("petrov");
        assertEquals("petrov", addCandidateDtoRequest.getLogin());

        addCandidateDtoRequest.setPassword("123456789");
        assertEquals("123456789", addCandidateDtoRequest.getPassword());

        assertEquals(token, addCandidateDtoRequest.getToken());
    }

    @Test
    public void testValidateWrongToken() {
        dataBase.getTokensAndVoters().clear();
        try {
            AddCandidateDtoRequest addCandidateDtoRequest1  = new AddCandidateDtoRequest(0, "ivan", "ivanov",
                                                                                        "12345678", null);
            dataBase.getTokensAndVoters().put(addCandidateDtoRequest1.getToken(), new Candidate("ivan", "ivanov","12345678"));
            addCandidateDtoRequest1.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(1, addCandidateDtoRequest1.getTokensAndVoters().size());

            addCandidateDtoRequest1.validate();
            fail();
        } catch (VoterException ex) {
            assertEquals(VoterErrorCode.VOTER_WRONG_TOKEN, ex.getVoterErrorCode());
        }

        dataBase.getTokensAndVoters().clear();
        try {
            AddCandidateDtoRequest addCandidateDtoRequest2  = new AddCandidateDtoRequest(1, "ivan", "ivanov",
                                                                                         "12345678", "");
            dataBase.getTokensAndVoters().put(addCandidateDtoRequest2.getToken(), new Candidate("ivan", "ivanov","12345678"));
            addCandidateDtoRequest2.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(1, addCandidateDtoRequest2.getTokensAndVoters().size());

            addCandidateDtoRequest2.validate();
            fail();
        }  catch (VoterException ex) {
            assertEquals(VoterErrorCode.VOTER_WRONG_TOKEN, ex.getVoterErrorCode());
        }

        dataBase.getTokensAndVoters().clear();
        try {
            AddCandidateDtoRequest addCandidateDtoRequest3  = new AddCandidateDtoRequest(2, "ivan", "ivanov",
                                                                                         "12345678", token);
            addCandidateDtoRequest3.setToken(null);
            dataBase.getTokensAndVoters().put(addCandidateDtoRequest3.getToken(), new Candidate("ivan", "ivanov","12345678"));
            addCandidateDtoRequest3.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(1, addCandidateDtoRequest3.getTokensAndVoters().size());

            addCandidateDtoRequest3.validate();
            fail();
        }  catch (VoterException ex) {
            assertEquals(VoterErrorCode.VOTER_WRONG_TOKEN, ex.getVoterErrorCode());
        }

        dataBase.getTokensAndVoters().clear();
        try {
            AddCandidateDtoRequest addCandidateDtoRequest4  = new AddCandidateDtoRequest(3, "ivan", "ivanov",
                                                                                         "12345678", token);
            addCandidateDtoRequest4.setToken("");
            dataBase.getTokensAndVoters().put(addCandidateDtoRequest4.getToken(), new Candidate("ivan", "ivanov","12345678"));
            addCandidateDtoRequest4.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(1, addCandidateDtoRequest4.getTokensAndVoters().size());

            addCandidateDtoRequest4.validate();
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

            AddCandidateDtoRequest addCandidateDtoRequest  = new AddCandidateDtoRequest(0, "ivan", "ivanov",
                                                                                         "12345678", token1);
            Map<String, Voter> tokensAndVoters = dataBase.getTokensAndVoters();
            addCandidateDtoRequest.setTokensAndVoters(tokensAndVoters);
            assertEquals(0, addCandidateDtoRequest.getTokensAndVoters().size());

            addCandidateDtoRequest.validate();
            fail();
        } catch (VoterException ex) {
            assertEquals(VoterErrorCode.OFFLINE_TOKEN, ex.getVoterErrorCode());
        }
    }
}
