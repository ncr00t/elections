package net.thumbtack.school.elections.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.thumbtack.school.elections.database.DataBase;
import net.thumbtack.school.elections.exception.VoterErrorCode;
import net.thumbtack.school.elections.exception.VoterException;
import net.thumbtack.school.elections.model.Candidate;
import net.thumbtack.school.elections.model.Voter;
import net.thumbtack.school.elections.request.*;
import net.thumbtack.school.elections.response.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import java.io.IOException;
import java.util.*;
import static org.junit.Assert.assertEquals;

public class TestCandidateService {

    private static Gson gson;
    private static CandidateService candidateService;
    private static VoterService voterService;
    private static DataBase dataBase;

    @BeforeClass
    public static void setUpClass(){
        gson = new Gson();
        candidateService = new CandidateService();
        voterService = new VoterService();
        dataBase = DataBase.getDataBase();
    }

    @Before
    public void setUp(){
        dataBase.getTokensAndVoters().clear();
        dataBase.getCandidates().clear();
        dataBase.getVoters().clear();
    }

    @Test
    public void testAddCandidate() {
        RegisterVoterDtoRequest registerVoterRequest = new RegisterVoterDtoRequest(0, "sergei","sidorov","123456789");
        assertEquals("sergei", registerVoterRequest.getFirstName());
        assertEquals("sidorov", registerVoterRequest.getLogin());
        assertEquals("123456789", registerVoterRequest.getPassword());

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
    }

    @Test
    public void testAddCandidateWrongToken() {
        RegisterVoterDtoRequest registerVoterRequest = new RegisterVoterDtoRequest(0, null,"ivanov","12345678");
        assertEquals(null, registerVoterRequest.getFirstName());
        assertEquals("ivanov", registerVoterRequest.getLogin());
        assertEquals("12345678", registerVoterRequest.getPassword());

        String jsonRegisterRequest = gson.toJson(registerVoterRequest);
        String jsonRegisterResponse  = voterService.registerVoter(jsonRegisterRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonRegisterResponse);

        AddCandidateDtoRequest addCandidateRequest = new AddCandidateDtoRequest(registerVoterRequest.getId(), registerVoterRequest.getFirstName(), registerVoterRequest.getLogin(),
                                                                                 registerVoterRequest.getPassword(), null);
        assertEquals(registerVoterRequest.getFirstName(), addCandidateRequest.getFirstName());
        assertEquals(registerVoterRequest.getLogin(), addCandidateRequest.getLogin());
        assertEquals(registerVoterRequest.getPassword(), addCandidateRequest.getPassword());
        assertEquals(null, addCandidateRequest.getToken());

        String jsonAddCandidateRequest = gson.toJson(addCandidateRequest);
        String jsonAddCandidateResponse  = candidateService.addCandidate(jsonAddCandidateRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonAddCandidateResponse);
    }

    @Test
    public void testAddVotedCandidate() {
        RegisterVoterDtoRequest registerVoterRequest = new RegisterVoterDtoRequest(0, "sergei","sidorov","123456789");
        assertEquals("sergei", registerVoterRequest.getFirstName());
        assertEquals("sidorov", registerVoterRequest.getLogin());
        assertEquals("123456789", registerVoterRequest.getPassword());

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

        AddVotedCandidateDtoRequest addVotedCandidateDtoRequest = new AddVotedCandidateDtoRequest(0, addCandidateDtoResponse1.getToken());
        assertEquals(addCandidateDtoResponse1.getToken(), addVotedCandidateDtoRequest.getCandidateToken());

        String jsonAddVotedCandidateTokenRequest = gson.toJson(addVotedCandidateDtoRequest);
        String jsonAddVotedCandidateTokenResponse = candidateService.addVotedCandidate(jsonAddVotedCandidateTokenRequest);
        assertEquals(gson.toJson(""), jsonAddVotedCandidateTokenResponse);
    }

    @Test
    public void testAddVotedCandidateWrongToken1() {
        AddVotedCandidateDtoRequest addVotedCandidateDtoRequest = new AddVotedCandidateDtoRequest(0, null);
        assertEquals(null, addVotedCandidateDtoRequest.getCandidateToken());

        String jsonAddVotedCandidateTokenRequest = gson.toJson(addVotedCandidateDtoRequest);
        String jsonAddVotedCandidateTokenResponse = candidateService.addVotedCandidate(jsonAddVotedCandidateTokenRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonAddVotedCandidateTokenResponse);
    }

    @Test
    public void testAddVotedCandidateTokenWrongToken2() {
        AddVotedCandidateDtoRequest addVotedCandidateDtoRequest = new AddVotedCandidateDtoRequest(0,"");
        assertEquals("", addVotedCandidateDtoRequest.getCandidateToken());

        String jsonAddVotedCandidateTokenRequest = gson.toJson(addVotedCandidateDtoRequest);
        String jsonAddVotedCandidateTokenResponse = candidateService.addVotedCandidate(jsonAddVotedCandidateTokenRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonAddVotedCandidateTokenResponse);
    }

    @Ignore
    @Test
    public void testGetCandidates() {
        RegisterVoterDtoRequest registerVoterRequest = new RegisterVoterDtoRequest(0,"miron","mironov","12345678");
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

        String jsonAddCandidateRequest = gson.toJson(addCandidateRequest);
        String jsonAddCandidateResponse  = candidateService.addCandidate(jsonAddCandidateRequest);
        String emptyJson = gson.toJson("");
        assertEquals(emptyJson, jsonAddCandidateResponse);

        AllCandidatesDtoRequest allCandidatesDtoRequest = new AllCandidatesDtoRequest(registerVoterDtoResponse.getToken());
        assertEquals(allCandidatesDtoRequest.getToken(), registerVoterDtoResponse.getToken());
        String jsonRequest = gson.toJson(allCandidatesDtoRequest);
        String jsonResponse = candidateService.getCandidates(jsonRequest);

        AllCandidatesDtoResponse allCandidatesDtoResponse = gson.fromJson(jsonResponse, AllCandidatesDtoResponse.class);
        String resultJson = gson.toJson(allCandidatesDtoResponse);
        assertEquals(resultJson, jsonResponse);
    }

    @Ignore
    @Test
    public void testGetCandidatesWrongToken1() {
        AllCandidatesDtoRequest allCandidatesDtoRequest = new AllCandidatesDtoRequest(null);
        assertEquals(null, allCandidatesDtoRequest.getToken());

        String jsonRequest = gson.toJson(allCandidatesDtoRequest);
        String jsonResponse = candidateService.getCandidates(jsonRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonResponse);
    }

    @Ignore
    @Test
    public void testGetCandidatesWrongToken2() {
        AllCandidatesDtoRequest allCandidatesDtoRequest = new AllCandidatesDtoRequest("");
        assertEquals("", allCandidatesDtoRequest.getToken());

        String jsonRequest = gson.toJson(allCandidatesDtoRequest);
        String jsonResponse = candidateService.getCandidates(jsonRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonResponse);
    }

    @Ignore
    @Test
    public void testGetCandidatesOfflineToken() {
        RegisterVoterDtoRequest registerVoterRequest = new RegisterVoterDtoRequest(0,"anton","antonov","123456789");
        assertEquals("anton", registerVoterRequest.getFirstName());
        assertEquals("antonov", registerVoterRequest.getLogin());
        assertEquals("123456789", registerVoterRequest.getPassword());

        String jsonRegisterRequest = gson.toJson(registerVoterRequest);
        String jsonRegisterResponse  = voterService.registerVoter(jsonRegisterRequest);
        RegisterVoterDtoResponse registerVoterDtoResponse = gson.fromJson(jsonRegisterResponse, RegisterVoterDtoResponse.class);
        String resultRegisterJson = gson.toJson(registerVoterDtoResponse);
        assertEquals(resultRegisterJson, jsonRegisterResponse);

        AllCandidatesDtoRequest allCandidatesDtoRequest = new AllCandidatesDtoRequest(registerVoterDtoResponse.getToken());
        assertEquals(allCandidatesDtoRequest.getToken(), registerVoterDtoResponse.getToken());

        DataBase dataBase = DataBase.getDataBase();
        Map<String, Voter> tokensAndVoters = dataBase.getTokensAndVoters();
        tokensAndVoters.put(registerVoterDtoResponse.getToken(), new Candidate("anton","antonov","123456789"));
        allCandidatesDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(tokensAndVoters, allCandidatesDtoRequest.getTokensAndVoters());

        String jsonRequest = new GsonBuilder().create().toJson(allCandidatesDtoRequest);
        String jsonResponse = candidateService.getCandidates(jsonRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonResponse);
    }

    @Test
    public void testGetCandidatesAndPrograms() {
        RegisterVoterDtoRequest registerVoterRequest = new RegisterVoterDtoRequest(0, "efim","efimov","12345678");
        assertEquals("efim", registerVoterRequest.getFirstName());
        assertEquals("efimov", registerVoterRequest.getLogin());
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

        GetCandidatesAndProgramsDtoRequest candidatesAndProgramsDtoRequest = new GetCandidatesAndProgramsDtoRequest(addCandidateDtoResponse1.getToken());
        assertEquals(candidatesAndProgramsDtoRequest.getToken(), addCandidateDtoResponse1.getToken());
        String jsonRequest = gson.toJson(candidatesAndProgramsDtoRequest);
        String jsonResponse = candidateService.getCandidatesAndPrograms(jsonRequest);

        GetCandidatesAndProgramsDtoResponse getCandidatesAndProgramsDtoResponse = gson.fromJson(jsonResponse, GetCandidatesAndProgramsDtoResponse.class);
        String resultJson = new GsonBuilder().enableComplexMapKeySerialization()
                                             .create()
                                             .toJson(getCandidatesAndProgramsDtoResponse);
        assertEquals(resultJson, jsonResponse);
    }

    @Test
    public void testGetCandidatesAndProgramsWrongToken1() {
        GetCandidatesAndProgramsDtoRequest candidatesAndProgramsDtoRequest = new GetCandidatesAndProgramsDtoRequest(null);
        assertEquals(null, candidatesAndProgramsDtoRequest.getToken());

        String jsonRequest = gson.toJson(candidatesAndProgramsDtoRequest);
        String jsonResponse = candidateService.getCandidatesAndPrograms(jsonRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonResponse);
    }

    @Test
    public void testGetCandidatesAndProgramsWrongToken2() {
        GetCandidatesAndProgramsDtoRequest candidatesAndProgramsDtoRequest = new GetCandidatesAndProgramsDtoRequest("");
        assertEquals("", candidatesAndProgramsDtoRequest.getToken());

        String jsonRequest = gson.toJson(candidatesAndProgramsDtoRequest);
        String jsonResponse = candidateService.getCandidatesAndPrograms(jsonRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonResponse);
    }

    @Ignore
    @Test
    public void testGetCandidatesAndProgramsOfflineToken() {
        RegisterVoterDtoRequest registerVoterRequest = new RegisterVoterDtoRequest(0, "abram","abramov","123456789");
        assertEquals("abram", registerVoterRequest.getFirstName());
        assertEquals("abramov", registerVoterRequest.getLogin());
        assertEquals("123456789", registerVoterRequest.getPassword());

        String jsonRegisterRequest = gson.toJson(registerVoterRequest);
        String jsonRegisterResponse  = voterService.registerVoter(jsonRegisterRequest);
        RegisterVoterDtoResponse registerVoterDtoResponse = gson.fromJson(jsonRegisterResponse, RegisterVoterDtoResponse.class);
        String resultRegisterJson = gson.toJson(registerVoterDtoResponse);
        assertEquals(resultRegisterJson, jsonRegisterResponse);

        GetCandidatesAndProgramsDtoRequest candidatesAndProgramsDtoRequest = new GetCandidatesAndProgramsDtoRequest(registerVoterDtoResponse.getToken());
        assertEquals(candidatesAndProgramsDtoRequest.getToken(), registerVoterDtoResponse.getToken());

        DataBase dataBase = DataBase.getDataBase();
        Map<String, Voter> tokensAndVoters = dataBase.getTokensAndVoters();
        tokensAndVoters.put(registerVoterDtoResponse.getToken(), new Candidate("abram","abramov","123456789"));
        candidatesAndProgramsDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(tokensAndVoters, candidatesAndProgramsDtoRequest.getTokensAndVoters());

        String jsonRequest = gson.toJson(candidatesAndProgramsDtoRequest);
        String jsonResponse = candidateService.getCandidatesAndPrograms(jsonRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonResponse);
    }

    @Test
    public void testGetVotedCandidates() {
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

        AddVotedCandidateDtoRequest addVotedCandidateDtoRequest = new AddVotedCandidateDtoRequest(0, addCandidateDtoResponse1.getToken());
        assertEquals(addCandidateDtoResponse1.getToken(), addVotedCandidateDtoRequest.getCandidateToken());

        String jsonAddVotedCandidateTokenRequest = gson.toJson(addVotedCandidateDtoRequest);
        String jsonAddVotedCandidateTokenResponse = candidateService.addVotedCandidate(jsonAddVotedCandidateTokenRequest);
        assertEquals(gson.toJson(""), jsonAddVotedCandidateTokenResponse);

        GetVotedCandidatesDtoRequest getVotedCandidatesDtoRequest = new GetVotedCandidatesDtoRequest(addCandidateDtoResponse1.getToken());
        assertEquals(getVotedCandidatesDtoRequest.getCandidateToken(), addCandidateDtoResponse1.getToken());
        String jsonRequest = gson.toJson(getVotedCandidatesDtoRequest);
        String jsonResponse = candidateService.getVotedCandidates(jsonRequest);

        GetVotedCandidatesDtoResponse getVotedCandidatesDtoResponse = gson.fromJson(jsonResponse, GetVotedCandidatesDtoResponse.class);
        String resultJson = gson.toJson(getVotedCandidatesDtoResponse);
        assertEquals(resultJson, jsonResponse);
    }

    @Test
    public void testGetVotedCandidateWrongToken1() {
        GetVotedCandidatesDtoRequest getVotedCandidatesDtoRequest = new GetVotedCandidatesDtoRequest(null);
        assertEquals(null, getVotedCandidatesDtoRequest.getCandidateToken());

        String jsonRequest = gson.toJson(getVotedCandidatesDtoRequest);
        String jsonResponse = candidateService.getVotedCandidates(jsonRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonResponse);
    }

    @Test
    public void testGetVotedCandidateWrongToken2() {
        GetVotedCandidatesDtoRequest getVotedCandidatesDtoRequest = new GetVotedCandidatesDtoRequest("");
        assertEquals("", getVotedCandidatesDtoRequest.getCandidateToken());

        String jsonRequest = gson.toJson(getVotedCandidatesDtoRequest);
        String jsonResponse = candidateService.getVotedCandidates(jsonRequest);
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

        GetVotedCandidatesDtoRequest getVotedCandidatesDtoRequest = new GetVotedCandidatesDtoRequest(registerVoterDtoResponse.getToken());
        assertEquals(getVotedCandidatesDtoRequest.getCandidateToken(), registerVoterDtoResponse.getToken());

        DataBase dataBase = DataBase.getDataBase();
        Map<String, Voter> tokensAndVoters = dataBase.getTokensAndVoters();
        tokensAndVoters.put(registerVoterDtoResponse.getToken(), new Candidate("anton","antonov","123456789"));
        getVotedCandidatesDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(tokensAndVoters, getVotedCandidatesDtoRequest.getTokensAndVoters());

        String jsonRequest = gson.toJson(getVotedCandidatesDtoRequest);
        String jsonResponse = candidateService.getVotedCandidates(jsonRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonResponse);
    }

    @Ignore
    @Test
    public void testRemoveCandidateCorrectly() {
        RegisterVoterDtoRequest registerVoterRequest = new RegisterVoterDtoRequest(0, "kiril","kirilov","123456789");
        assertEquals("kiril", registerVoterRequest.getFirstName());
        assertEquals("kirilov", registerVoterRequest.getLogin());
        assertEquals("123456789", registerVoterRequest.getPassword());

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

        RemoveCandidateDtoRequest removeCandidateDtoRequest = new RemoveCandidateDtoRequest(0, addCandidateDtoResponse1.getToken());
        assertEquals(addCandidateRequest.getLogin(), removeCandidateDtoRequest.getCandidateId());
        assertEquals(addCandidateDtoResponse1.getToken(), removeCandidateDtoRequest.getCandidateToken());

        String jsonRemoveCandidateRequest = gson.toJson(removeCandidateDtoRequest);
        String jsonRemoveCandidateResponse = candidateService.removeCandidate(jsonRemoveCandidateRequest);
        assertEquals(gson.toJson(""), jsonRemoveCandidateResponse);
    }

//    @Test
//    public void testRemoveCandidateWrongLogin1() {
//        RegisterVoterDtoRequest registerVoterRequest = new RegisterVoterDtoRequest("roman","romanov","123456789");
//        assertEquals("roman", registerVoterRequest.getFirstName());
//        assertEquals("romanov", registerVoterRequest.getLogin());
//        assertEquals("123456789", registerVoterRequest.getPassword());
//
//        String jsonRegisterRequest = gson.toJson(registerVoterRequest);
//        String jsonRegisterResponse  = voterService.registerVoter(jsonRegisterRequest);
//        RegisterVoterDtoResponse registerVoterDtoResponse = gson.fromJson(jsonRegisterResponse, RegisterVoterDtoResponse.class);
//        String resultRegisterJson = gson.toJson(registerVoterDtoResponse);
//        assertEquals(resultRegisterJson, jsonRegisterResponse);
//
//        AddCandidateDtoRequest addCandidateRequest = new AddCandidateDtoRequest(registerVoterRequest.getFirstName(), registerVoterRequest.getLogin(),
//                registerVoterRequest.getPassword(), registerVoterDtoResponse.getToken());
//        assertEquals(registerVoterRequest.getFirstName(), addCandidateRequest.getFirstName());
//        assertEquals(registerVoterRequest.getLogin(), addCandidateRequest.getLogin());
//        assertEquals(registerVoterRequest.getPassword(), addCandidateRequest.getPassword());
//        assertEquals(registerVoterDtoResponse.getToken(), addCandidateRequest.getToken());
//
//        String jsonAddCandidateRequest = gson.toJson(addCandidateRequest);
//        String jsonAddCandidateResponse  = candidateService.addCandidate(jsonAddCandidateRequest);
//        String emptyJson = gson.toJson("");
//        assertEquals(emptyJson, jsonAddCandidateResponse);
//
//        RemoveCandidateDtoRequest removeCandidateDtoRequest = new RemoveCandidateDtoRequest(null, addCandidateRequest.getToken());
//        assertEquals(null, removeCandidateDtoRequest.getCandidateId());
//        assertEquals(addCandidateRequest.getToken(), removeCandidateDtoRequest.getCandidateToken());
//
//        String jsonRemoveCandidateRequest = gson.toJson(removeCandidateDtoRequest);
//        String jsonRemoveCandidateResponse = candidateService.removeCandidate(jsonRemoveCandidateRequest);
//        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonRemoveCandidateResponse);
//    }

//    @Test
//    public void testRemoveCandidateWrongLogin2() {
//        RegisterVoterDtoRequest registerVoterRequest = new RegisterVoterDtoRequest("vasya","pupkin","123456789");
//        assertEquals("vasya", registerVoterRequest.getFirstName());
//        assertEquals("pupkin", registerVoterRequest.getLogin());
//        assertEquals("123456789", registerVoterRequest.getPassword());
//
//        String jsonRegisterRequest = gson.toJson(registerVoterRequest);
//        String jsonRegisterResponse  = voterService.registerVoter(jsonRegisterRequest);
//        RegisterVoterDtoResponse registerVoterDtoResponse = gson.fromJson(jsonRegisterResponse, RegisterVoterDtoResponse.class);
//        String resultRegisterJson = gson.toJson(registerVoterDtoResponse);
//        assertEquals(resultRegisterJson, jsonRegisterResponse);
//
//        AddCandidateDtoRequest addCandidateRequest = new AddCandidateDtoRequest(registerVoterRequest.getFirstName(), registerVoterRequest.getLogin(),
//                registerVoterRequest.getPassword(), registerVoterDtoResponse.getToken());
//        assertEquals(registerVoterRequest.getFirstName(), addCandidateRequest.getFirstName());
//        assertEquals(registerVoterRequest.getLogin(), addCandidateRequest.getLogin());
//        assertEquals(registerVoterRequest.getPassword(), addCandidateRequest.getPassword());
//        assertEquals(registerVoterDtoResponse.getToken(), addCandidateRequest.getToken());
//
//        String jsonAddCandidateRequest = gson.toJson(addCandidateRequest);
//        String jsonAddCandidateResponse  = candidateService.addCandidate(jsonAddCandidateRequest);
//        String emptyJson = gson.toJson("");
//        assertEquals(emptyJson, jsonAddCandidateResponse);
//
//        RemoveCandidateDtoRequest removeCandidateDtoRequest = new RemoveCandidateDtoRequest("", addCandidateRequest.getToken());
//        assertEquals("", removeCandidateDtoRequest.getCandidateId());
//        assertEquals(addCandidateRequest.getToken(), removeCandidateDtoRequest.getCandidateToken());
//
//        String jsonRemoveCandidateRequest = gson.toJson(removeCandidateDtoRequest);
//        String jsonRemoveCandidateResponse = candidateService.removeCandidate(jsonRemoveCandidateRequest);
//        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonRemoveCandidateResponse);
//    }

    @Ignore
    @Test
    public void testRemoveCandidateIfTokenIsOfflineToken() throws IOException, VoterException {
        RegisterVoterDtoRequest request = new RegisterVoterDtoRequest(0, "ignat","ignatov","12345678");
        assertEquals("ignat", request.getFirstName());
        assertEquals("ignatov", request.getLogin());
        assertEquals("12345678", request.getPassword());

        String jsonRegisterRequest1 = gson.toJson(request);
        String jsonRegisterResponse1  = voterService.registerVoter(jsonRegisterRequest1);

        RegisterVoterDtoResponse resultRegister = gson.fromJson(jsonRegisterResponse1, RegisterVoterDtoResponse.class);
        String registerTokenJson1 = gson.toJson(resultRegister);
        assertEquals(registerTokenJson1, jsonRegisterResponse1);

        LogoutDtoRequest logoutDtoRequest = new LogoutDtoRequest(request.getLogin(), resultRegister.getToken());
        assertEquals("ignatov", logoutDtoRequest.getLogin());
        assertEquals(resultRegister.getToken(), logoutDtoRequest.getToken());

        String jsonLogoutRequest = gson.toJson(logoutDtoRequest);
        String jsonLogoutResponse = voterService.logout(jsonLogoutRequest);

        LogoutDtoResponse resultLogout = gson.fromJson(jsonLogoutResponse, LogoutDtoResponse.class);
        String offlineTokenJson = gson.toJson(resultLogout);
        assertEquals(offlineTokenJson, jsonLogoutResponse);

        RemoveCandidateDtoRequest removeCandidateDtoRequest = new RemoveCandidateDtoRequest(0, resultLogout.getToken());
        assertEquals("ignatov", removeCandidateDtoRequest.getCandidateId());
        assertEquals( resultLogout.getToken(), removeCandidateDtoRequest.getCandidateToken());

        String jsonRemoveCandidateRequest = gson.toJson(removeCandidateDtoRequest);
        String jsonRemoveCandidateResponse = candidateService.removeCandidate(jsonRemoveCandidateRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonRemoveCandidateResponse);
    }

    @Test
    public void testRemoveCandidateIfVoterIsNotCandidate() throws IOException, VoterException {
        RegisterVoterDtoRequest request = new RegisterVoterDtoRequest(0, "georgi","georgiev","12345678");
        assertEquals("georgi", request.getFirstName());
        assertEquals("georgiev", request.getLogin());
        assertEquals("12345678", request.getPassword());

        String jsonRegisterRequest1 = gson.toJson(request);
        String jsonRegisterResponse1  = voterService.registerVoter(jsonRegisterRequest1);

        RegisterVoterDtoResponse resultRegister = gson.fromJson(jsonRegisterResponse1, RegisterVoterDtoResponse.class);
        String registerTokenJson1 = gson.toJson(resultRegister);
        assertEquals(registerTokenJson1, jsonRegisterResponse1);

        RemoveCandidateDtoRequest removeCandidateDtoRequest = new RemoveCandidateDtoRequest(0, resultRegister.getToken());
        assertEquals(0, removeCandidateDtoRequest.getCandidateId());
        assertEquals( resultRegister.getToken(), removeCandidateDtoRequest.getCandidateToken());

        String jsonRemoveCandidateRequest = gson.toJson(removeCandidateDtoRequest);
        String jsonRemoveCandidateResponse = candidateService.removeCandidate(jsonRemoveCandidateRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonRemoveCandidateResponse);
    }
}
