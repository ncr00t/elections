package net.thumbtack.school.elections.request;

import net.thumbtack.school.elections.database.DataBase;
import net.thumbtack.school.elections.exception.VoterErrorCode;
import net.thumbtack.school.elections.exception.VoterException;
import net.thumbtack.school.elections.model.Voter;
import net.thumbtack.school.elections.response.LoginDtoResponse;
import net.thumbtack.school.elections.response.RegisterVoterDtoResponse;
import net.thumbtack.school.elections.service.GenerateTokenService;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import java.util.Map;

import static org.junit.Assert.*;

public class TestLoginDtoRequest {

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
        dataBase.getVoters().clear();
    }

    @Test
    public void testLoginDtoRequest() throws VoterException {
        Voter voter = new Voter("boris","borisov","12345678");
        assertEquals("boris", voter.getFirstName());
        assertEquals("borisov", voter.getLogin());
        assertEquals("12345678", voter.getPassword());

        LoginDtoRequest loginDtoRequest = new LoginDtoRequest();
        dataBase.getTokensAndVoters().put(token, voter);
        loginDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
        assertEquals(1, loginDtoRequest.getTokensAndVoters().size());

        loginDtoRequest.setLogin(voter.getLogin());
        assertEquals(voter.getLogin(), loginDtoRequest.getLogin());

        loginDtoRequest.setPassword(voter.getPassword());
        assertEquals(voter.getPassword(), loginDtoRequest.getPassword());

        loginDtoRequest.setToken(token);
        assertEquals(token, loginDtoRequest.getToken());

        loginDtoRequest.setVoter(voter);
        assertEquals(voter, loginDtoRequest.getVoter());
    }

    @Test
    public void testSetTokenIfTokenNotFound() {
        try {
            Voter voter = new Voter("boris","borisov","12345678");
            assertEquals("boris", voter.getFirstName());
            assertEquals("borisov", voter.getLogin());
            assertEquals("12345678", voter.getPassword());
            dataBase.insertVoter(voter);
            dataBase.getTokensAndVoters().put(token, voter);

            LoginDtoRequest loginDtoRequest = new LoginDtoRequest();
            loginDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(1, loginDtoRequest.getTokensAndVoters().size());

            loginDtoRequest.setLogin("andreev");
            assertEquals("andreev", loginDtoRequest.getLogin());

            loginDtoRequest.setPassword(voter.getPassword());
            assertEquals("12345678", loginDtoRequest.getPassword());

            String tokenByLogin = dataBase.getTokenByLogin("andreev", dataBase.getTokensAndVoters());
            loginDtoRequest.setToken(tokenByLogin);
            assertEquals(null, loginDtoRequest.getToken());

            fail();
        } catch (VoterException ex) {
            assertEquals(VoterErrorCode.TOKEN_NOT_FOUND_BY_LOGIN, ex.getVoterErrorCode());
        }
    }

    @Test
    public void testSetVoterIfVoterNotFound() {
        try {
            Voter voter = new Voter("boris","borisov","12345678");
            assertEquals("boris", voter.getFirstName());
            assertEquals("borisov", voter.getLogin());
            assertEquals("12345678", voter.getPassword());
            dataBase.insertVoter(voter);
            dataBase.getTokensAndVoters().put(token, voter);

            LoginDtoRequest loginDtoRequest = new LoginDtoRequest();
            loginDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(1, loginDtoRequest.getTokensAndVoters().size());

            loginDtoRequest.setLogin("andreev");
            assertEquals("andreev", loginDtoRequest.getLogin());

            loginDtoRequest.setPassword(voter.getPassword());
            assertEquals("12345678", loginDtoRequest.getPassword());

            String tokenByLogin = dataBase.getTokenByLogin("andreev", dataBase.getTokensAndVoters());
            assertEquals(null, loginDtoRequest.getToken());

            loginDtoRequest.setVoter(dataBase.getVoters().get(tokenByLogin));
            assertEquals(null, loginDtoRequest.getVoter());

            fail();
        } catch (VoterException ex) {
            assertEquals(VoterErrorCode.VOTER_NOT_FOUND_BY_TOKEN, ex.getVoterErrorCode());
        }
    }

    @Test
    public void testSetLoginIfEmptyLogin() {
        try {
            LoginDtoRequest loginDtoRequest = new LoginDtoRequest();
            loginDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(0, loginDtoRequest.getTokensAndVoters().size());

            loginDtoRequest.setLogin(null);
            assertEquals(null, loginDtoRequest.getLogin());
            fail();
        } catch (VoterException ex) {
            assertEquals(VoterErrorCode.VOTER_WRONG_LOGIN, ex.getVoterErrorCode());
        }

        try {
            LoginDtoRequest loginDtoRequest = new LoginDtoRequest();
            loginDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(0, loginDtoRequest.getTokensAndVoters().size());

            loginDtoRequest.setLogin("");
            assertEquals("", loginDtoRequest.getLogin());
            fail();
        } catch (VoterException ex) {
            assertEquals(VoterErrorCode.VOTER_WRONG_LOGIN, ex.getVoterErrorCode());
        }
    }

    @Test
    public void testSetPasswordIfEmptyPassword() {
        try {
            LoginDtoRequest loginDtoRequest = new LoginDtoRequest();
            loginDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(0, loginDtoRequest.getTokensAndVoters().size());

            loginDtoRequest.setPassword(null);
            assertEquals(null, loginDtoRequest.getPassword());
            fail();
        } catch (VoterException ex) {
            assertEquals(VoterErrorCode.VOTER_WRONG_PASSWORD, ex.getVoterErrorCode());
        }

        try {
            LoginDtoRequest loginDtoRequest = new LoginDtoRequest();
            loginDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(0, loginDtoRequest.getTokensAndVoters().size());

            loginDtoRequest.setPassword("");
            assertEquals("", loginDtoRequest.getPassword());
            fail();
        } catch (VoterException ex) {
            assertEquals(VoterErrorCode.VOTER_WRONG_PASSWORD, ex.getVoterErrorCode());
        }
    }

    @Test
    public void testValidateIfPasswordNotFound() {
        dataBase.getTokensAndVoters().clear();
        try {
            Voter voter = new Voter("boris","borisov","12345678");
            assertEquals("boris", voter.getFirstName());
            assertEquals("borisov", voter.getLogin());
            assertEquals("12345678", voter.getPassword());

            LoginDtoRequest loginDtoRequest = new LoginDtoRequest();
            loginDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
            dataBase.getTokensAndVoters().put(token, voter);
            assertEquals(1, loginDtoRequest.getTokensAndVoters().size());

            loginDtoRequest.setLogin(voter.getLogin());
            assertEquals(voter.getLogin(), loginDtoRequest.getLogin());

            loginDtoRequest.setPassword("123456789");
            assertNotEquals("123456789", voter.getPassword());

            loginDtoRequest.setToken(token);
            assertEquals(token, loginDtoRequest.getToken());

            loginDtoRequest.setVoter(voter);
            assertEquals(voter, loginDtoRequest.getVoter());

            loginDtoRequest.validate();
            fail();
        } catch (VoterException ex) {
            assertEquals(VoterErrorCode.PASSWORD_NOT_FOUND, ex.getVoterErrorCode());
        }
    }

    @Test
    public void testValidateIfTokenIsNotOfflineToken() {
        try {
            Voter voter = new Voter("boris","borisov","12345678");
            assertEquals("boris", voter.getFirstName());
            assertEquals("borisov", voter.getLogin());
            assertEquals("12345678", voter.getPassword());

            LoginDtoRequest loginDtoRequest = new LoginDtoRequest();
            loginDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
            dataBase.getTokensAndVoters().put(token, voter);
            assertEquals(1, loginDtoRequest.getTokensAndVoters().size());

            loginDtoRequest.setLogin(voter.getLogin());
            assertEquals(voter.getLogin(), loginDtoRequest.getLogin());

            loginDtoRequest.setPassword("12345678");
            assertEquals("12345678", loginDtoRequest.getPassword());

            loginDtoRequest.setToken(token);
            assertEquals(token, loginDtoRequest.getToken());

            loginDtoRequest.setVoter(voter);
            assertEquals(voter, loginDtoRequest.getVoter());

            loginDtoRequest.validate();
            fail();
        } catch (VoterException ex) {
            assertEquals(VoterErrorCode.ACTIVE_TOKEN, ex.getVoterErrorCode());
        }
    }
}
