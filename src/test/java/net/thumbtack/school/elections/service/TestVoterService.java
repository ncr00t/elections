package net.thumbtack.school.elections.service;

import com.google.gson.Gson;
import net.thumbtack.school.elections.database.DataBase;
import net.thumbtack.school.elections.exception.VoterErrorCode;
import net.thumbtack.school.elections.exception.VoterException;
import net.thumbtack.school.elections.model.Voter;
import net.thumbtack.school.elections.request.*;
import net.thumbtack.school.elections.response.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import java.io.IOException;
import java.util.Map;
import static org.junit.Assert.assertEquals;

public class TestVoterService {

    private static Gson gson;
    private static VoterService voterService;
    private static CandidateService candidateService;
    private static ElectionService electionService;
    private static DataBase dataBase;

    @BeforeClass
    public static void setUpClass(){
        gson = new Gson();
        voterService = new VoterService();
        candidateService = new CandidateService();
        electionService = new ElectionService();
        dataBase = DataBase.getDataBase();
    }

    @Before
    public void setUp(){
        dataBase.getTokensAndVoters().clear();
        dataBase.getVoters().clear();
        dataBase.getCandidates().clear();
    }

    @Test
    public void testRegisterVoterCorrectly() {
        RegisterVoterDtoRequest request = new RegisterVoterDtoRequest(0, "john","smith","123456789");
        assertEquals("john", request.getFirstName());
        assertEquals("smith", request.getLogin());
        assertEquals("123456789", request.getPassword());

        String jsonRequest = gson.toJson(request);
        String jsonResponse  = voterService.registerVoter(jsonRequest);
        RegisterVoterDtoResponse result = gson.fromJson(jsonResponse, RegisterVoterDtoResponse.class);
        String resultTokenJson = gson.toJson(result);
        assertEquals(resultTokenJson, jsonResponse);
    }

    @Test
    public void testRegisterVoterShortPassword() {
        RegisterVoterDtoRequest request = new RegisterVoterDtoRequest(0, "ivan","ivanov","123456");
        assertEquals("ivan", request.getFirstName());
        assertEquals("ivanov", request.getLogin());
        assertEquals("123456", request.getPassword());

        String jsonRequest = gson.toJson(request);
        String jsonResponse  = voterService.registerVoter(jsonRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonResponse);
    }

    @Test
    public void testRegisterVoterEmptyFirstname() {
        RegisterVoterDtoRequest request = new RegisterVoterDtoRequest(0, null,"ivanov","12345678");
        assertEquals(null, request.getFirstName());
        assertEquals("ivanov", request.getLogin());
        assertEquals("12345678", request.getPassword());

        String jsonRequest = gson.toJson(request);
        String jsonResponse  = voterService.registerVoter(jsonRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonResponse);
    }

    @Test
    public void testRegisterVoterEmptyLogin() {
        RegisterVoterDtoRequest request = new RegisterVoterDtoRequest(0, "ivan",null,"12345678");
        assertEquals("ivan", request.getFirstName());
        assertEquals(null, request.getLogin());
        assertEquals("12345678", request.getPassword());

        String jsonRequest = gson.toJson(request);
        String jsonResponse  = voterService.registerVoter(jsonRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonResponse);
    }

    @Test
    public void testRegisterVoterEmptyPassword() {
        RegisterVoterDtoRequest request = new RegisterVoterDtoRequest(0, "ivan","ivanov",null);
        assertEquals("ivan", request.getFirstName());
        assertEquals("ivanov", request.getLogin());
        assertEquals(null, request.getPassword());

        String jsonRequest = gson.toJson(request);
        String jsonResponse  = voterService.registerVoter(jsonRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonResponse);
    }

    @Ignore
    @Test
    public void testLogoutCorrectlyIfVoterNotCandidate() throws IOException, VoterException {
        RegisterVoterDtoRequest request = new RegisterVoterDtoRequest(0, "sergei","sergeev","12345678");
        assertEquals("sergei", request.getFirstName());
        assertEquals("sergeev", request.getLogin());
        assertEquals("12345678", request.getPassword());

        String jsonRegisterRequest = gson.toJson(request);
        String jsonRegisterResponse  = voterService.registerVoter(jsonRegisterRequest);

        RegisterVoterDtoResponse resultRegister = gson.fromJson(jsonRegisterResponse, RegisterVoterDtoResponse.class);
        String registerTokenJson = gson.toJson(resultRegister);
        assertEquals(registerTokenJson, jsonRegisterResponse);

        LogoutDtoRequest logoutDtoRequest = new LogoutDtoRequest(request.getLogin(), resultRegister.getToken());
        assertEquals("sergeev", logoutDtoRequest.getLogin());
        assertEquals(resultRegister.getToken(), logoutDtoRequest.getToken());

        String jsonLogoutRequest = gson.toJson(logoutDtoRequest);
        String jsonLogoutResponse = voterService.logout(jsonLogoutRequest);

        LogoutDtoResponse resultLogout = gson.fromJson(jsonLogoutResponse, LogoutDtoResponse.class);
        String offlineTokenJson = gson.toJson(resultLogout);
        assertEquals(offlineTokenJson, jsonLogoutResponse);
    }

    @Ignore
    @Test
    public void testLogoutCorrectlyIfVoterIsCandidate() throws IOException, VoterException {
        RegisterVoterDtoRequest registerVoterRequest = new RegisterVoterDtoRequest(0, "andrei","andreev","12345678");
        assertEquals("andrei", registerVoterRequest.getFirstName());
        assertEquals("andreev", registerVoterRequest.getLogin());
        assertEquals("12345678", registerVoterRequest.getPassword());

        String jsonRegisterRequest = gson.toJson(registerVoterRequest);
        String jsonRegisterResponse  = voterService.registerVoter(jsonRegisterRequest);

        RegisterVoterDtoResponse resultRegister = gson.fromJson(jsonRegisterResponse, RegisterVoterDtoResponse.class);
        String registerTokenJson = gson.toJson(resultRegister);
        assertEquals(registerTokenJson, jsonRegisterResponse);

        AddCandidateDtoRequest addCandidateRequest = new AddCandidateDtoRequest(registerVoterRequest.getId(), registerVoterRequest.getFirstName(), registerVoterRequest.getLogin(),
                registerVoterRequest.getPassword(), resultRegister.getToken());
        assertEquals(registerVoterRequest.getFirstName(), addCandidateRequest.getFirstName());
        assertEquals(registerVoterRequest.getLogin(), addCandidateRequest.getLogin());
        assertEquals(registerVoterRequest.getPassword(), addCandidateRequest.getPassword());
        assertEquals(resultRegister.getToken(), addCandidateRequest.getToken());

        String jsonAddCandidateRequest1 = gson.toJson(addCandidateRequest);
        String jsonAddCandidateResponse1  = candidateService.addCandidate(jsonAddCandidateRequest1);

        AddCandidateDtoResponse addCandidateDtoResponse1 = gson.fromJson(jsonAddCandidateResponse1, AddCandidateDtoResponse.class);
        String resultAddCandidateResponseJson1 = gson.toJson(addCandidateDtoResponse1);
        assertEquals(resultAddCandidateResponseJson1, jsonAddCandidateResponse1);

        LogoutDtoRequest logoutDtoRequest = new LogoutDtoRequest(addCandidateRequest.getLogin(), addCandidateDtoResponse1.getToken());
        assertEquals("andreev", logoutDtoRequest.getLogin());
        assertEquals(resultRegister.getToken(), logoutDtoRequest.getToken());

        String jsonLogoutRequest = gson.toJson(logoutDtoRequest);
        String jsonLogoutResponse = voterService.logout(jsonLogoutRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonLogoutResponse);
    }

    @Test
    public void testLogoutWrongToken1() throws IOException, VoterException {
        RegisterVoterDtoRequest request = new RegisterVoterDtoRequest(0, "sergei","sergeev","12345678");
        assertEquals("sergei", request.getFirstName());
        assertEquals("sergeev", request.getLogin());
        assertEquals("12345678", request.getPassword());

        LogoutDtoRequest logoutDtoRequest = new LogoutDtoRequest(request.getLogin(), null);
        assertEquals("sergeev", logoutDtoRequest.getLogin());
        assertEquals(null, logoutDtoRequest.getToken());

        String jsonLogoutRequest = gson.toJson(logoutDtoRequest);
        String jsonLogoutResponse = voterService.logout(jsonLogoutRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonLogoutResponse);
    }

    @Test
    public void testLogoutWrongToken2() throws IOException {
        RegisterVoterDtoRequest registerRequest = new RegisterVoterDtoRequest(0, "sergei","sergeev","12345678");
        assertEquals("sergei", registerRequest.getFirstName());
        assertEquals("sergeev", registerRequest.getLogin());
        assertEquals("12345678", registerRequest.getPassword());

        LogoutDtoRequest logoutDtoRequest = new LogoutDtoRequest(registerRequest.getLogin(), "");
        assertEquals("sergeev", logoutDtoRequest.getLogin());
        assertEquals("", logoutDtoRequest.getToken());

        String jsonLogoutRequest = gson.toJson(logoutDtoRequest);
        String jsonLogoutResponse = voterService.logout(jsonLogoutRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonLogoutResponse);
    }

    @Test
    public void testLogoutWrongLogin1() throws IOException {
        RegisterVoterDtoRequest registerVoterRequest = new RegisterVoterDtoRequest(0, "maksim","maksimov","12345678");
        assertEquals("maksim", registerVoterRequest.getFirstName());
        assertEquals("maksimov", registerVoterRequest.getLogin());
        assertEquals("12345678", registerVoterRequest.getPassword());

        String jsonRegisterRequest = gson.toJson(registerVoterRequest);
        String jsonRegisterResponse  = voterService.registerVoter(jsonRegisterRequest);

        RegisterVoterDtoResponse resultRegister = gson.fromJson(jsonRegisterResponse, RegisterVoterDtoResponse.class);
        String registerTokenJson = gson.toJson(resultRegister);
        assertEquals(registerTokenJson, jsonRegisterResponse);

        LogoutDtoRequest logoutDtoRequest = new LogoutDtoRequest(null, resultRegister.getToken());
        assertEquals(null, logoutDtoRequest.getLogin());
        assertEquals(resultRegister.getToken(), logoutDtoRequest.getToken());

        String jsonLogoutRequest = gson.toJson(logoutDtoRequest);
        String jsonLogoutResponse = voterService.logout(jsonLogoutRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonLogoutResponse);
    }

    @Test
    public void testLogoutWrongLogin2() throws IOException {
        RegisterVoterDtoRequest registerVoterRequest = new RegisterVoterDtoRequest(0, "alexei","alexeev", "12345678");
        assertEquals("alexei", registerVoterRequest.getFirstName());
        assertEquals("alexeev", registerVoterRequest.getLogin());
        assertEquals("12345678", registerVoterRequest.getPassword());

        String jsonRegisterRequest = gson.toJson(registerVoterRequest);
        String jsonRegisterResponse = voterService.registerVoter(jsonRegisterRequest);

        RegisterVoterDtoResponse resultRegister = gson.fromJson(jsonRegisterResponse, RegisterVoterDtoResponse.class);
        String registerTokenJson = gson.toJson(resultRegister);
        assertEquals(registerTokenJson, jsonRegisterResponse);

        LogoutDtoRequest logoutDtoRequest = new LogoutDtoRequest("", resultRegister.getToken());
        assertEquals("", logoutDtoRequest.getLogin());
        assertEquals(resultRegister.getToken(), logoutDtoRequest.getToken());

        String jsonLogoutRequest = gson.toJson(logoutDtoRequest);
        String jsonLogoutResponse = voterService.logout(jsonLogoutRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonLogoutResponse);
    }

    @Ignore
    @Test
    public void testLoginIfTokenIsOfflineToken() throws IOException, VoterException {
        RegisterVoterDtoRequest request = new RegisterVoterDtoRequest(0, "nikolai","nikolaev","12345678");
        assertEquals("nikolai", request.getFirstName());
        assertEquals("nikolaev", request.getLogin());
        assertEquals("12345678", request.getPassword());

        String jsonRegisterRequest1 = gson.toJson(request);
        String jsonRegisterResponse1  = voterService.registerVoter(jsonRegisterRequest1);

        RegisterVoterDtoResponse resultRegister = gson.fromJson(jsonRegisterResponse1, RegisterVoterDtoResponse.class);
        String registerTokenJson1 = gson.toJson(resultRegister);
        assertEquals(registerTokenJson1, jsonRegisterResponse1);

        LogoutDtoRequest logoutDtoRequest = new LogoutDtoRequest(request.getLogin(), resultRegister.getToken());
        assertEquals("nikolaev", logoutDtoRequest.getLogin());
        assertEquals(resultRegister.getToken(), logoutDtoRequest.getToken());

        String jsonLogoutRequest = gson.toJson(logoutDtoRequest);
        String jsonLogoutResponse = voterService.logout(jsonLogoutRequest);

        LogoutDtoResponse resultLogout = gson.fromJson(jsonLogoutResponse, LogoutDtoResponse.class);
        String offlineTokenJson = gson.toJson(resultLogout);
        assertEquals(offlineTokenJson, jsonLogoutResponse);

        LoginDtoRequest loginDtoRequest = new LoginDtoRequest();
        loginDtoRequest.setLogin(request.getLogin());
        assertEquals("nikolaev", loginDtoRequest.getLogin());
        loginDtoRequest.setPassword(request.getPassword());
        assertEquals("12345678", loginDtoRequest.getPassword());

        String jsonLoginRequest = gson.toJson(loginDtoRequest);
        String jsonLoginResponse = voterService.login(jsonLoginRequest);

        LoginDtoResponse resultLogin = gson.fromJson(jsonLoginResponse, LoginDtoResponse.class);
        String newTokenJson = gson.toJson(resultLogin);
        assertEquals(newTokenJson, jsonLoginResponse);
    }

    @Test
    public void testLoginIfTokenIsActiveToken() throws IOException, VoterException {
        RegisterVoterDtoRequest request = new RegisterVoterDtoRequest(0, "fedor","fedorov","12345678");
        assertEquals("fedor", request.getFirstName());
        assertEquals("fedorov", request.getLogin());
        assertEquals("12345678", request.getPassword());

        String jsonRegisterRequest1 = gson.toJson(request);
        String jsonRegisterResponse1  = voterService.registerVoter(jsonRegisterRequest1);

        RegisterVoterDtoResponse resultRegister = gson.fromJson(jsonRegisterResponse1, RegisterVoterDtoResponse.class);
        String registerTokenJson1 = gson.toJson(resultRegister);
        assertEquals(registerTokenJson1, jsonRegisterResponse1);

        LoginDtoRequest loginDtoRequest = new LoginDtoRequest();
        loginDtoRequest.setLogin(request.getLogin());
        assertEquals("fedorov", loginDtoRequest.getLogin());
        loginDtoRequest.setPassword(request.getPassword());
        assertEquals("12345678", loginDtoRequest.getPassword());

        String jsonLoginRequest = gson.toJson(loginDtoRequest);
        String jsonLoginResponse = voterService.login(jsonLoginRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonLoginResponse);
    }

    @Ignore
    @Test
    public void testLoginIfVoterNotFoundByLogin() throws IOException, VoterException {
        RegisterVoterDtoRequest request = new RegisterVoterDtoRequest(0, "boris","borisov","12345678");
        assertEquals("boris", request.getFirstName());
        assertEquals("borisov", request.getLogin());
        assertEquals("12345678", request.getPassword());

        String jsonRegisterRequest1 = gson.toJson(request);
        String jsonRegisterResponse1  = voterService.registerVoter(jsonRegisterRequest1);

        RegisterVoterDtoResponse resultRegister = gson.fromJson(jsonRegisterResponse1, RegisterVoterDtoResponse.class);
        String registerTokenJson1 = gson.toJson(resultRegister);
        assertEquals(registerTokenJson1, jsonRegisterResponse1);

        LogoutDtoRequest logoutDtoRequest = new LogoutDtoRequest(request.getLogin(), resultRegister.getToken());
        assertEquals("borisov", logoutDtoRequest.getLogin());
        assertEquals(resultRegister.getToken(), logoutDtoRequest.getToken());

        String jsonLogoutRequest = gson.toJson(logoutDtoRequest);
        String jsonLogoutResponse = voterService.logout(jsonLogoutRequest);

        LogoutDtoResponse resultLogout = gson.fromJson(jsonLogoutResponse, LogoutDtoResponse.class);
        String offlineTokenJson = gson.toJson(resultLogout);
        assertEquals(offlineTokenJson, jsonLogoutResponse);

        LoginDtoRequest loginDtoRequest = new LoginDtoRequest();
        loginDtoRequest.setLogin("andreev");
        assertEquals("andreev", loginDtoRequest.getLogin());
        loginDtoRequest.setPassword("12345678");
        assertEquals("12345678", loginDtoRequest.getPassword());

        String jsonLoginRequest = gson.toJson(loginDtoRequest);
        String jsonLoginResponse = voterService.login(jsonLoginRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonLoginResponse);
    }

    @Ignore
    @Test
    public void testLoginIfPasswordNotFound() throws IOException, VoterException {
        RegisterVoterDtoRequest request = new RegisterVoterDtoRequest(0, "vladimir","akimov","12345678");
        assertEquals("vladimir", request.getFirstName());
        assertEquals("akimov", request.getLogin());
        assertEquals("12345678", request.getPassword());

        String jsonRegisterRequest1 = gson.toJson(request);
        String jsonRegisterResponse1  = voterService.registerVoter(jsonRegisterRequest1);

        RegisterVoterDtoResponse resultRegister = gson.fromJson(jsonRegisterResponse1, RegisterVoterDtoResponse.class);
        String registerTokenJson1 = gson.toJson(resultRegister);
        assertEquals(registerTokenJson1, jsonRegisterResponse1);

        LogoutDtoRequest logoutDtoRequest = new LogoutDtoRequest(request.getLogin(), resultRegister.getToken());
        assertEquals("akimov", logoutDtoRequest.getLogin());
        assertEquals(resultRegister.getToken(), logoutDtoRequest.getToken());

        String jsonLogoutRequest = gson.toJson(logoutDtoRequest);
        String jsonLogoutResponse = voterService.logout(jsonLogoutRequest);

        LogoutDtoResponse resultLogout = gson.fromJson(jsonLogoutResponse, LogoutDtoResponse.class);
        String offlineTokenJson = gson.toJson(resultLogout);
        assertEquals(offlineTokenJson, jsonLogoutResponse);

        LoginDtoRequest loginDtoRequest = new LoginDtoRequest();
        loginDtoRequest.setLogin("akimov");
        assertEquals("akimov", loginDtoRequest.getLogin());

        loginDtoRequest.setPassword("123456789");
        assertEquals("123456789", loginDtoRequest.getPassword());

        String jsonLoginRequest = gson.toJson(loginDtoRequest);
        String jsonLoginResponse = voterService.login(jsonLoginRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonLoginResponse);
    }

    @Test
    public void testAddVotedVoterToken() {
        RegisterVoterDtoRequest registerVoterRequest = new RegisterVoterDtoRequest(0, "sergei","sidorov","123456789");
        assertEquals("sergei", registerVoterRequest.getFirstName());
        assertEquals("sidorov", registerVoterRequest.getLogin());
        assertEquals("123456789", registerVoterRequest.getPassword());

        String jsonRegisterRequest = gson.toJson(registerVoterRequest);
        String jsonRegisterResponse  = voterService.registerVoter(jsonRegisterRequest);
        RegisterVoterDtoResponse registerVoterDtoResponse = gson.fromJson(jsonRegisterResponse, RegisterVoterDtoResponse.class);
        String resultRegisterJson = gson.toJson(registerVoterDtoResponse);
        assertEquals(resultRegisterJson, jsonRegisterResponse);

        AddVotedVoterTokenDtoRequest addVotedVoterTokenDtoRequest = new AddVotedVoterTokenDtoRequest(registerVoterDtoResponse.getToken());
        assertEquals(registerVoterDtoResponse.getToken(), addVotedVoterTokenDtoRequest.getVoterToken());

        String jsonAddVotedVoterTokenRequest = gson.toJson(addVotedVoterTokenDtoRequest);
        String jsonAddVotedVoterTokenResponse = voterService.addVotedVoter(jsonAddVotedVoterTokenRequest);
        assertEquals(gson.toJson(""), jsonAddVotedVoterTokenResponse);
    }

    @Test
    public void testAddVotedCandidateTokenWrongToken1() {
        AddVotedVoterTokenDtoRequest addVotedVoterTokenDtoRequest = new AddVotedVoterTokenDtoRequest(null);
        assertEquals(null, addVotedVoterTokenDtoRequest.getVoterToken());

        String jsonAddVotedCandidateTokenRequest = gson.toJson(addVotedVoterTokenDtoRequest);
        String jsonAddVotedCandidateTokenResponse = voterService.addVotedVoter(jsonAddVotedCandidateTokenRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonAddVotedCandidateTokenResponse);
    }

    @Test
    public void testAddVotedCandidateTokenWrongToken2() {
        AddVotedVoterTokenDtoRequest addVotedVoterTokenDtoRequest = new AddVotedVoterTokenDtoRequest("");
        assertEquals("", addVotedVoterTokenDtoRequest.getVoterToken());

        String jsonAddVotedCandidateTokenRequest = gson.toJson(addVotedVoterTokenDtoRequest);
        String jsonAddVotedCandidateTokenResponse = voterService.addVotedVoter(jsonAddVotedCandidateTokenRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonAddVotedCandidateTokenResponse);
    }

    @Ignore
    @Test
    public void testGetVotedVoterTokens() {
        RegisterVoterDtoRequest registerVoterRequest = new RegisterVoterDtoRequest(0, "miron","mironov","12345678");
        assertEquals("miron", registerVoterRequest.getFirstName());
        assertEquals("mironov", registerVoterRequest.getLogin());
        assertEquals("12345678", registerVoterRequest.getPassword());

        String jsonRegisterRequest = gson.toJson(registerVoterRequest);
        String jsonRegisterResponse  = voterService.registerVoter(jsonRegisterRequest);

        RegisterVoterDtoResponse registerVoterDtoResponse = gson.fromJson(jsonRegisterResponse, RegisterVoterDtoResponse.class);
        String resultRegisterJson = gson.toJson(registerVoterDtoResponse);
        assertEquals(resultRegisterJson, jsonRegisterResponse);

        AddCandidateDtoRequest addCandidateRequest = new AddCandidateDtoRequest(registerVoterRequest.getId(), registerVoterRequest.getFirstName(), registerVoterRequest.getLogin(),
                                                                                registerVoterRequest.getPassword(), registerVoterDtoResponse.getToken());
        assertEquals(registerVoterRequest.getFirstName(), addCandidateRequest.getFirstName());
        assertEquals(registerVoterRequest.getLogin(), addCandidateRequest.getLogin());
        assertEquals(registerVoterRequest.getPassword(), addCandidateRequest.getPassword());
        assertEquals(registerVoterDtoResponse.getToken(), addCandidateRequest.getToken());

        String jsonAddCandidateRequest1 = gson.toJson(addCandidateRequest);
        String jsonAddCandidateResponse1  = candidateService.addCandidate(jsonAddCandidateRequest1);

        AddCandidateDtoResponse addCandidateDtoResponse1 = gson.fromJson(jsonAddCandidateResponse1, AddCandidateDtoResponse.class);
        String resultAddCandidateResponseJson1 = gson.toJson(addCandidateDtoResponse1);
        assertEquals(resultAddCandidateResponseJson1, jsonAddCandidateResponse1);

        AddVotedVoterTokenDtoRequest addVotedVoterTokenDtoRequest = new AddVotedVoterTokenDtoRequest(addCandidateRequest.getToken());
        assertEquals(addCandidateDtoResponse1.getToken(), addCandidateRequest.getToken());

        String jsonAddVotedVoterTokenRequest = gson.toJson(addVotedVoterTokenDtoRequest);
        String jsonAddVotedVoterTokenResponse = voterService.addVotedVoter(jsonAddVotedVoterTokenRequest);
        assertEquals(gson.toJson(""), jsonAddVotedVoterTokenResponse);

        GetVotedVotersDtoRequest getVotedVotersDtoRequest = new GetVotedVotersDtoRequest(addCandidateDtoResponse1.getToken());
        assertEquals(getVotedVotersDtoRequest.getCandidateToken(), registerVoterDtoResponse.getToken());
        String jsonRequest = gson.toJson(getVotedVotersDtoRequest);
        String jsonResponse = voterService.getVotedVoters(jsonRequest);

        GetVotedVotersDtoResponse getVotedVotersDtoResponse = gson.fromJson(jsonResponse, GetVotedVotersDtoResponse.class);
        String resultJson = gson.toJson(getVotedVotersDtoResponse);
        assertEquals(resultJson, jsonResponse);
    }

    @Test
    public void testGetVotedCandidateWrongToken1() {
        GetVotedVotersDtoRequest getVotedVotersDtoRequest = new GetVotedVotersDtoRequest(null);
        assertEquals(null, getVotedVotersDtoRequest.getCandidateToken());

        String jsonRequest = gson.toJson(getVotedVotersDtoRequest);
        String jsonResponse = voterService.getVotedVoters(jsonRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonResponse);
    }

    @Test
    public void testGetVotedCandidateWrongToken2() {
        GetVotedVotersDtoRequest getVotedVotersDtoRequest = new GetVotedVotersDtoRequest("");
        assertEquals("", getVotedVotersDtoRequest.getCandidateToken());

        String jsonRequest = gson.toJson(getVotedVotersDtoRequest);
        String jsonResponse = voterService.getVotedVoters(jsonRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonResponse);
    }

    @Ignore
    @Test
    public void testGetVotedCandidateOfflineToken() {
        RegisterVoterDtoRequest registerVoterRequest = new RegisterVoterDtoRequest(0, "anton","antonov","123456789");
        assertEquals("anton", registerVoterRequest.getFirstName());
        assertEquals("antonov", registerVoterRequest.getLogin());
        assertEquals("123456789", registerVoterRequest.getPassword());

        String jsonRegisterRequest = gson.toJson(registerVoterRequest);
        String jsonRegisterResponse  = voterService.registerVoter(jsonRegisterRequest);

        RegisterVoterDtoResponse registerVoterDtoResponse = gson.fromJson(jsonRegisterResponse, RegisterVoterDtoResponse.class);
        String resultRegisterJson = gson.toJson(registerVoterDtoResponse);
        assertEquals(resultRegisterJson, jsonRegisterResponse);

        GetVotedVotersDtoRequest getVotedVotersDtoRequest = new GetVotedVotersDtoRequest(registerVoterDtoResponse.getToken());
        assertEquals(getVotedVotersDtoRequest.getCandidateToken(), registerVoterDtoResponse.getToken());

        DataBase dataBase = DataBase.getDataBase();
        Map<String, Voter> tokensAndVoters = dataBase.getTokensAndVoters();
        tokensAndVoters.put(registerVoterDtoResponse.getToken(), new Voter("anton","antonov","123456789"));
        getVotedVotersDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(tokensAndVoters, getVotedVotersDtoRequest.getTokensAndVoters());

        String jsonRequest = gson.toJson(getVotedVotersDtoRequest);
        String jsonResponse = voterService.getVotedVoters(jsonRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonResponse);
    }
}
