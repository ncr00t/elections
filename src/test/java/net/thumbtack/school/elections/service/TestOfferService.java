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
import java.util.Map;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.*;

public class TestOfferService {

    private static Gson gson;
    private static VoterService voterService;
    private static CandidateService candidateService;
    private static ElectionService electionService;
    private static OfferService offerService;
    private static DataBase dataBase;

    @BeforeClass
    public static void setUpClass(){
        gson = new Gson();
        voterService = new VoterService();
        candidateService = new CandidateService();
        electionService = new ElectionService();
        offerService = new OfferService();
        dataBase = DataBase.getDataBase();
    }

    @Before
    public void setUp(){
        dataBase.getTokensAndVoters().clear();
        dataBase.getVoters().clear();
        dataBase.getCandidates().clear();
    }

    @Ignore
    @Test
    public void testAddOfferToElectionProgram() {
        RegisterVoterDtoRequest registerRequest1 = new RegisterVoterDtoRequest(0, "petr2","petrov2","12345678");
        assertEquals("petr2", registerRequest1.getFirstName());
        assertEquals("petrov2", registerRequest1.getLogin());
        assertEquals("12345678", registerRequest1.getPassword());
        String jsonRegisterRequest1 = gson.toJson(registerRequest1);
        String jsonRegisterResponse1  = voterService.registerVoter(jsonRegisterRequest1);

        RegisterVoterDtoResponse registerVoterDtoResponse1 = gson.fromJson(jsonRegisterResponse1, RegisterVoterDtoResponse.class);
        String resultRegisterJson1 = gson.toJson(registerVoterDtoResponse1);
        assertEquals(resultRegisterJson1, jsonRegisterResponse1);

        AddCandidateDtoRequest addCandidateRequest = new AddCandidateDtoRequest(registerRequest1.getId(), registerRequest1.getFirstName(), registerRequest1.getLogin(),
                                                                                registerRequest1.getPassword(), registerVoterDtoResponse1.getToken());
        assertEquals(registerRequest1.getFirstName(), addCandidateRequest.getFirstName());
        assertEquals(registerRequest1.getLogin(), addCandidateRequest.getLogin());
        assertEquals(registerRequest1.getPassword(), addCandidateRequest.getPassword());
        assertEquals(registerVoterDtoResponse1.getToken(), addCandidateRequest.getToken());

        String jsonAddCandidateRequest = gson.toJson(addCandidateRequest);
        String jsonAddCandidateResponse  = candidateService.addCandidate(jsonAddCandidateRequest);
        assertEquals(gson.toJson(""), jsonAddCandidateResponse);

        String candidateToken = addCandidateRequest.getToken();

        AddOfferToElectionProgramDtoRequest addOfferToElectionProgramDtoRequest = new AddOfferToElectionProgramDtoRequest(candidateToken, 0, "build a bridge across the river");
        assertEquals(candidateToken, addOfferToElectionProgramDtoRequest.getCandidateToken());
        assertEquals("build a bridge across the river", addOfferToElectionProgramDtoRequest.getOfferDescription());

        Map<String, Voter> tokensAndVoters = dataBase.getTokensAndVoters();
        addOfferToElectionProgramDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(0, addOfferToElectionProgramDtoRequest.getTokensAndVoters().size());

        String jsonAddOfferRequest = gson.toJson(addOfferToElectionProgramDtoRequest);
        String jsonAddOfferResponse = offerService.addOfferToElectionProgram(jsonAddOfferRequest);
        assertEquals(gson.toJson(""), jsonAddOfferResponse);
    }

    @Ignore
    @Test
    public void testAddOfferToElectionProgramWrongOfferDescription1() {
        RegisterVoterDtoRequest registerRequest1 = new RegisterVoterDtoRequest(0, "semen","semenov","12345678");
        assertEquals("semen", registerRequest1.getFirstName());
        assertEquals("semenov", registerRequest1.getLogin());
        assertEquals("12345678", registerRequest1.getPassword());

        String jsonRegisterRequest = gson.toJson(registerRequest1);
        String jsonRegisterResponse  = voterService.registerVoter(jsonRegisterRequest);
        RegisterVoterDtoResponse registerVoterDtoResponse = gson.fromJson(jsonRegisterResponse, RegisterVoterDtoResponse.class);
        String resultRegisterJson = gson.toJson(registerVoterDtoResponse);
        assertEquals(resultRegisterJson, jsonRegisterResponse);

        AddCandidateDtoRequest addCandidateRequest = new AddCandidateDtoRequest(registerRequest1.getId(), registerRequest1.getFirstName(), registerRequest1.getLogin(),
                                                                                registerRequest1.getPassword(), registerVoterDtoResponse.getToken());
        assertEquals(registerRequest1.getFirstName(), addCandidateRequest.getFirstName());
        assertEquals(registerRequest1.getLogin(), addCandidateRequest.getLogin());
        assertEquals(registerRequest1.getPassword(), addCandidateRequest.getPassword());
        assertEquals(registerVoterDtoResponse.getToken(), addCandidateRequest.getToken());

        String jsonAddCandidateRequest = gson.toJson(addCandidateRequest);
        String jsonAddCandidateResponse  = candidateService.addCandidate(jsonAddCandidateRequest);
        String emptyJson = gson.toJson("");
        assertEquals(emptyJson, jsonAddCandidateResponse);

        String candidateToken = addCandidateRequest.getToken();

        AddOfferToElectionProgramDtoRequest addOfferToElectionProgramDtoRequest = new AddOfferToElectionProgramDtoRequest(candidateToken, 0, "");
        assertEquals("", addOfferToElectionProgramDtoRequest.getOfferDescription());

        Map<String, Voter> tokensAndVoters = dataBase.getTokensAndVoters();
        addOfferToElectionProgramDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(0, addOfferToElectionProgramDtoRequest.getTokensAndVoters().size());

        String jsonAddOfferRequest = gson.toJson(addOfferToElectionProgramDtoRequest);
        String jsonAddOfferResponse = offerService.addOfferToElectionProgram(jsonAddOfferRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonAddOfferResponse);
    }

    @Ignore
    @Test
    public void testAddOfferToElectionProgramWrongOfferDescription2() {
        RegisterVoterDtoRequest registerRequest1 = new RegisterVoterDtoRequest(0, "konstantin","konstantinov","12345678");
        assertEquals("konstantin", registerRequest1.getFirstName());
        assertEquals("konstantinov", registerRequest1.getLogin());
        assertEquals("12345678", registerRequest1.getPassword());

        String jsonRegisterRequest = gson.toJson(registerRequest1);
        String jsonRegisterResponse  = voterService.registerVoter(jsonRegisterRequest);

        RegisterVoterDtoResponse registerVoterDtoResponse = gson.fromJson(jsonRegisterResponse, RegisterVoterDtoResponse.class);
        String resultRegisterJson = gson.toJson(registerVoterDtoResponse);
        assertEquals(resultRegisterJson, jsonRegisterResponse);

        AddCandidateDtoRequest addCandidateRequest = new AddCandidateDtoRequest(registerRequest1.getId(), registerRequest1.getFirstName(), registerRequest1.getLogin(),
                                                                                registerRequest1.getPassword(), registerVoterDtoResponse.getToken());
        assertEquals(registerRequest1.getFirstName(), addCandidateRequest.getFirstName());
        assertEquals(registerRequest1.getLogin(), addCandidateRequest.getLogin());
        assertEquals(registerRequest1.getPassword(), addCandidateRequest.getPassword());
        assertEquals(registerVoterDtoResponse.getToken(), addCandidateRequest.getToken());

        String jsonAddCandidateRequest = gson.toJson(addCandidateRequest);
        String jsonAddCandidateResponse  = candidateService.addCandidate(jsonAddCandidateRequest);
        String emptyJson = gson.toJson("");
        assertEquals(emptyJson, jsonAddCandidateResponse);

        String candidateToken = addCandidateRequest.getToken();

        AddOfferToElectionProgramDtoRequest addOfferToElectionProgramDtoRequest = new AddOfferToElectionProgramDtoRequest(candidateToken, 0,null);
        assertEquals(null, addOfferToElectionProgramDtoRequest.getOfferDescription());

        Map<String, Voter> tokensAndVoters = dataBase.getTokensAndVoters();
        addOfferToElectionProgramDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(0, addOfferToElectionProgramDtoRequest.getTokensAndVoters().size());

        String jsonAddOfferRequest = gson.toJson(addOfferToElectionProgramDtoRequest);
        String jsonAddOfferResponse = offerService.addOfferToElectionProgram(jsonAddOfferRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonAddOfferResponse);
    }

    @Ignore
    @Test
    public void testAddOfferToElectionProgramIfCandidateTokenIsOffline() {
        RegisterVoterDtoRequest registerRequest1 = new RegisterVoterDtoRequest(0, "potap2","potapov2","12345678");
        assertEquals("potap2", registerRequest1.getFirstName());
        assertEquals("potapov2", registerRequest1.getLogin());
        assertEquals("12345678", registerRequest1.getPassword());

        String jsonRegisterRequest = gson.toJson(registerRequest1);
        String jsonRegisterResponse  = voterService.registerVoter(jsonRegisterRequest);

        RegisterVoterDtoResponse registerVoterDtoResponse = gson.fromJson(jsonRegisterResponse, RegisterVoterDtoResponse.class);
        String resultRegisterJson = gson.toJson(registerVoterDtoResponse);
        assertEquals(resultRegisterJson, jsonRegisterResponse);

        AddCandidateDtoRequest addCandidateRequest = new AddCandidateDtoRequest(registerRequest1.getId(), registerRequest1.getFirstName(), registerRequest1.getLogin(),
                                                                                registerRequest1.getPassword(), registerVoterDtoResponse.getToken());
        assertEquals(registerRequest1.getFirstName(), addCandidateRequest.getFirstName());
        assertEquals(registerRequest1.getLogin(), addCandidateRequest.getLogin());
        assertEquals(registerRequest1.getPassword(), addCandidateRequest.getPassword());
        assertEquals(registerVoterDtoResponse.getToken(), addCandidateRequest.getToken());

        String jsonAddCandidateRequest1 = gson.toJson(addCandidateRequest);
        String jsonAddCandidateResponse1  = candidateService.addCandidate(jsonAddCandidateRequest1);

        AddCandidateDtoResponse addCandidateDtoResponse1 = gson.fromJson(jsonAddCandidateResponse1, AddCandidateDtoResponse.class);
        String resultAddCandidateResponseJson1 = gson.toJson(addCandidateDtoResponse1);
        assertEquals(resultAddCandidateResponseJson1, jsonAddCandidateResponse1);

        RemoveCandidateDtoRequest removeCandidateDtoRequest = new RemoveCandidateDtoRequest(addCandidateRequest.getId(), addCandidateDtoResponse1.getToken());
        assertEquals(addCandidateRequest.getLogin(), removeCandidateDtoRequest.getCandidateId());
        assertEquals(addCandidateRequest.getToken(), removeCandidateDtoRequest.getCandidateToken());

        String jsonRemoveCandidateRequest = gson.toJson(removeCandidateDtoRequest);
        String jsonRemoveCandidateResponse = candidateService.removeCandidate(jsonRemoveCandidateRequest);
        assertEquals(gson.toJson(""), jsonRemoveCandidateResponse);

        LogoutDtoRequest logoutDtoRequest = new LogoutDtoRequest(addCandidateRequest.getLogin(), addCandidateDtoResponse1.getToken());
        assertEquals("potapov2", logoutDtoRequest.getLogin());
        assertEquals(registerVoterDtoResponse.getToken(), addCandidateDtoResponse1.getToken());

        String jsonLogoutRequest = gson.toJson(logoutDtoRequest);
        String jsonLogoutResponse = voterService.logout(jsonLogoutRequest);

        LogoutDtoResponse resultLogout = gson.fromJson(jsonLogoutResponse, LogoutDtoResponse.class);
        String offlineTokenJson = gson.toJson(resultLogout);
        assertEquals(offlineTokenJson, jsonLogoutResponse);

        RegisterVoterDtoRequest registerRequest2 = new RegisterVoterDtoRequest(1, "alex2","alexandrov2","12345678");
        assertEquals("alex2", registerRequest2.getFirstName());
        assertEquals("alexandrov2", registerRequest2.getLogin());
        assertEquals("12345678", registerRequest2.getPassword());

        String jsonRegisterRequest2 = gson.toJson(registerRequest2);
        String jsonRegisterResponse2  = voterService.registerVoter(jsonRegisterRequest2);

        RegisterVoterDtoResponse registerVoterDtoResponse2 = gson.fromJson(jsonRegisterResponse2, RegisterVoterDtoResponse.class);
        String resultRegisterJson2 = gson.toJson(registerVoterDtoResponse2);
        assertEquals(resultRegisterJson2, jsonRegisterResponse2);

        String voterToken = registerVoterDtoResponse2.getToken();
        String candidateToken = addCandidateDtoResponse1.getToken();

        AddOfferDtoRequest addOfferDtoRequest = new AddOfferDtoRequest(voterToken, 1, candidateToken, 0,  "build a bridge across the river");
        assertEquals(candidateToken, addOfferDtoRequest.getCandidateToken());

        Map<String, Voter> tokensAndVoters = dataBase.getTokensAndVoters();
        assertTrue(tokensAndVoters.containsKey(candidateToken));

        addOfferDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(2, addOfferDtoRequest.getTokensAndVoters().size());

        String jsonAddOfferRequest = gson.toJson(addOfferDtoRequest);
        String jsonAddOfferResponse = offerService.addOfferToElectionProgram(jsonAddOfferRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonAddOfferResponse);
    }

    @Ignore
    @Test
    public void testRemoveOfferFromElectionProgram() {
        RegisterVoterDtoRequest registerRequest1 = new RegisterVoterDtoRequest(0, "petr1","petrov1","12345678");
        assertEquals("petr1", registerRequest1.getFirstName());
        assertEquals("petrov1", registerRequest1.getLogin());
        assertEquals("12345678", registerRequest1.getPassword());
        String jsonRegisterRequest1 = gson.toJson(registerRequest1);
        String jsonRegisterResponse1  = voterService.registerVoter(jsonRegisterRequest1);

        RegisterVoterDtoResponse registerVoterDtoResponse1 = gson.fromJson(jsonRegisterResponse1, RegisterVoterDtoResponse.class);
        String resultRegisterJson1 = gson.toJson(registerVoterDtoResponse1);
        assertEquals(resultRegisterJson1, jsonRegisterResponse1);

        AddCandidateDtoRequest addCandidateRequest = new AddCandidateDtoRequest(registerRequest1.getId(), registerRequest1.getFirstName(), registerRequest1.getLogin(),
                                                                                registerRequest1.getPassword(), registerVoterDtoResponse1.getToken());
        assertEquals(registerRequest1.getFirstName(), addCandidateRequest.getFirstName());
        assertEquals(registerRequest1.getLogin(), addCandidateRequest.getLogin());
        assertEquals(registerRequest1.getPassword(), addCandidateRequest.getPassword());
        assertEquals(registerVoterDtoResponse1.getToken(), addCandidateRequest.getToken());

        String jsonAddCandidateRequest = gson.toJson(addCandidateRequest);
        String jsonAddCandidateResponse  = candidateService.addCandidate(jsonAddCandidateRequest);
        assertEquals(gson.toJson(""), jsonAddCandidateResponse);

        String candidateToken = addCandidateRequest.getToken();

        AddOfferToElectionProgramDtoRequest addOfferToElectionProgramDtoRequest = new AddOfferToElectionProgramDtoRequest(candidateToken, 0,"build a bridge across the river");
        assertEquals(candidateToken, addOfferToElectionProgramDtoRequest.getCandidateToken());
        assertEquals("build a bridge across the river", addOfferToElectionProgramDtoRequest.getOfferDescription());

        Map<String, Voter> tokensAndVoters = dataBase.getTokensAndVoters();
        addOfferToElectionProgramDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(0, addOfferToElectionProgramDtoRequest.getTokensAndVoters().size());

        String jsonAddOfferRequest = gson.toJson(addOfferToElectionProgramDtoRequest);
        String jsonAddOfferResponse = offerService.addOfferToElectionProgram(jsonAddOfferRequest);
        assertEquals(gson.toJson(""), jsonAddOfferResponse);

        RemoveOfferFromElectionProgramDtoRequest removeOfferFromElectionProgramDtoRequest = new RemoveOfferFromElectionProgramDtoRequest(candidateToken, 0, "build a bridge across the river");
        assertEquals(candidateToken, removeOfferFromElectionProgramDtoRequest.getCandidateToken());
        assertEquals("build a bridge across the river", removeOfferFromElectionProgramDtoRequest.getOfferDescription());

        removeOfferFromElectionProgramDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(0, removeOfferFromElectionProgramDtoRequest.getTokensAndVoters().size());

        String jsonRemoveOfferRequest = gson.toJson(removeOfferFromElectionProgramDtoRequest);
        String jsonRemoveOfferResponse = offerService.removeOfferFromElectionProgram(jsonRemoveOfferRequest);
        assertEquals(gson.toJson(""), jsonRemoveOfferResponse);
    }

    @Ignore
    @Test
    public void testRemoveOfferFromElectionProgramWrongOfferDescription1() {
        RegisterVoterDtoRequest registerRequest1 = new RegisterVoterDtoRequest(0, "semen1","semenov1","12345678");
        assertEquals("semen1", registerRequest1.getFirstName());
        assertEquals("semenov1", registerRequest1.getLogin());
        assertEquals("12345678", registerRequest1.getPassword());

        String jsonRegisterRequest = gson.toJson(registerRequest1);
        String jsonRegisterResponse  = voterService.registerVoter(jsonRegisterRequest);
        RegisterVoterDtoResponse registerVoterDtoResponse = gson.fromJson(jsonRegisterResponse, RegisterVoterDtoResponse.class);
        String resultRegisterJson = gson.toJson(registerVoterDtoResponse);
        assertEquals(resultRegisterJson, jsonRegisterResponse);

        AddCandidateDtoRequest addCandidateRequest = new AddCandidateDtoRequest(registerRequest1.getId(), registerRequest1.getFirstName(), registerRequest1.getLogin(),
                                                                                registerRequest1.getPassword(), registerVoterDtoResponse.getToken());
        assertEquals(registerRequest1.getFirstName(), addCandidateRequest.getFirstName());
        assertEquals(registerRequest1.getLogin(), addCandidateRequest.getLogin());
        assertEquals(registerRequest1.getPassword(), addCandidateRequest.getPassword());
        assertEquals(registerVoterDtoResponse.getToken(), addCandidateRequest.getToken());

        String jsonAddCandidateRequest = gson.toJson(addCandidateRequest);
        String jsonAddCandidateResponse  = candidateService.addCandidate(jsonAddCandidateRequest);
        String emptyJson = gson.toJson("");
        assertEquals(emptyJson, jsonAddCandidateResponse);

        String candidateToken = addCandidateRequest.getToken();

        AddOfferToElectionProgramDtoRequest addOfferToElectionProgramDtoRequest = new AddOfferToElectionProgramDtoRequest(candidateToken, 0, "build a bridge across the river");
        assertEquals(candidateToken, addOfferToElectionProgramDtoRequest.getCandidateToken());
        assertEquals("build a bridge across the river", addOfferToElectionProgramDtoRequest.getOfferDescription());

        Map<String, Voter> tokensAndVoters = dataBase.getTokensAndVoters();
        addOfferToElectionProgramDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(0, addOfferToElectionProgramDtoRequest.getTokensAndVoters().size());

        String jsonAddOfferRequest = gson.toJson(addOfferToElectionProgramDtoRequest);
        String jsonAddOfferResponse = offerService.addOfferToElectionProgram(jsonAddOfferRequest);
        assertEquals(gson.toJson(""), jsonAddOfferResponse);

        RemoveOfferFromElectionProgramDtoRequest removeOfferFromElectionProgramDtoRequest = new RemoveOfferFromElectionProgramDtoRequest(candidateToken, 0, "");
        assertEquals(candidateToken, removeOfferFromElectionProgramDtoRequest.getCandidateToken());
        assertEquals("", removeOfferFromElectionProgramDtoRequest.getOfferDescription());

        removeOfferFromElectionProgramDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(0, removeOfferFromElectionProgramDtoRequest.getTokensAndVoters().size());

        String jsonRemoveOfferRequest = gson.toJson(removeOfferFromElectionProgramDtoRequest);
        String jsonRemoveOfferResponse = offerService.removeOfferFromElectionProgram(jsonRemoveOfferRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonRemoveOfferResponse);
    }

    @Ignore
    @Test
    public void testRemoveOfferFromElectionProgramWrongOfferDescription2() {
        RegisterVoterDtoRequest registerRequest1 = new RegisterVoterDtoRequest(0, "semen2","semenov2","12345678");
        assertEquals("semen2", registerRequest1.getFirstName());
        assertEquals("semenov2", registerRequest1.getLogin());
        assertEquals("12345678", registerRequest1.getPassword());

        String jsonRegisterRequest = gson.toJson(registerRequest1);
        String jsonRegisterResponse  = voterService.registerVoter(jsonRegisterRequest);
        RegisterVoterDtoResponse registerVoterDtoResponse = gson.fromJson(jsonRegisterResponse, RegisterVoterDtoResponse.class);
        String resultRegisterJson = gson.toJson(registerVoterDtoResponse);
        assertEquals(resultRegisterJson, jsonRegisterResponse);

        AddCandidateDtoRequest addCandidateRequest = new AddCandidateDtoRequest(registerRequest1.getId(), registerRequest1.getFirstName(), registerRequest1.getLogin(),
                                                                                registerRequest1.getPassword(), registerVoterDtoResponse.getToken());
        assertEquals(registerRequest1.getFirstName(), addCandidateRequest.getFirstName());
        assertEquals(registerRequest1.getLogin(), addCandidateRequest.getLogin());
        assertEquals(registerRequest1.getPassword(), addCandidateRequest.getPassword());
        assertEquals(registerVoterDtoResponse.getToken(), addCandidateRequest.getToken());

        String jsonAddCandidateRequest = gson.toJson(addCandidateRequest);
        String jsonAddCandidateResponse  = candidateService.addCandidate(jsonAddCandidateRequest);
        String emptyJson = gson.toJson("");
        assertEquals(emptyJson, jsonAddCandidateResponse);

        String candidateToken = addCandidateRequest.getToken();

        AddOfferToElectionProgramDtoRequest addOfferToElectionProgramDtoRequest = new AddOfferToElectionProgramDtoRequest(candidateToken, 0,"build a bridge across the river");
        assertEquals(candidateToken, addOfferToElectionProgramDtoRequest.getCandidateToken());
        assertEquals("build a bridge across the river", addOfferToElectionProgramDtoRequest.getOfferDescription());

        Map<String, Voter> tokensAndVoters = dataBase.getTokensAndVoters();
        addOfferToElectionProgramDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(0, addOfferToElectionProgramDtoRequest.getTokensAndVoters().size());

        String jsonAddOfferRequest = gson.toJson(addOfferToElectionProgramDtoRequest);
        String jsonAddOfferResponse = offerService.addOfferToElectionProgram(jsonAddOfferRequest);
        assertEquals(gson.toJson(""), jsonAddOfferResponse);

        RemoveOfferFromElectionProgramDtoRequest removeOfferFromElectionProgramDtoRequest = new RemoveOfferFromElectionProgramDtoRequest(candidateToken, 0, null);
        assertEquals(candidateToken, removeOfferFromElectionProgramDtoRequest.getCandidateToken());
        assertEquals(null, removeOfferFromElectionProgramDtoRequest.getOfferDescription());

        removeOfferFromElectionProgramDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(0, removeOfferFromElectionProgramDtoRequest.getTokensAndVoters().size());

        String jsonRemoveOfferRequest = gson.toJson(removeOfferFromElectionProgramDtoRequest);
        String jsonRemoveOfferResponse = offerService.removeOfferFromElectionProgram(jsonRemoveOfferRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonRemoveOfferResponse);
    }

    @Ignore
    @Test
    public void testRemoveOfferFromElectionProgramIfCandidateTokenIsOffline() {
        RegisterVoterDtoRequest registerRequest1 = new RegisterVoterDtoRequest(0, "petr3","petrov3","12345678");
        assertEquals("petr3", registerRequest1.getFirstName());
        assertEquals("petrov3", registerRequest1.getLogin());
        assertEquals("12345678", registerRequest1.getPassword());
        String jsonRegisterRequest1 = gson.toJson(registerRequest1);
        String jsonRegisterResponse1  = voterService.registerVoter(jsonRegisterRequest1);

        RegisterVoterDtoResponse registerVoterDtoResponse1 = gson.fromJson(jsonRegisterResponse1, RegisterVoterDtoResponse.class);
        String resultRegisterJson1 = gson.toJson(registerVoterDtoResponse1);
        assertEquals(resultRegisterJson1, jsonRegisterResponse1);

        AddCandidateDtoRequest addCandidateRequest = new AddCandidateDtoRequest(registerRequest1.getId(), registerRequest1.getFirstName(), registerRequest1.getLogin(),
                                                                                registerRequest1.getPassword(), registerVoterDtoResponse1.getToken());
        assertEquals(registerRequest1.getFirstName(), addCandidateRequest.getFirstName());
        assertEquals(registerRequest1.getLogin(), addCandidateRequest.getLogin());
        assertEquals(registerRequest1.getPassword(), addCandidateRequest.getPassword());
        assertEquals(registerVoterDtoResponse1.getToken(), addCandidateRequest.getToken());

        String jsonAddCandidateRequest = gson.toJson(addCandidateRequest);
        String jsonAddCandidateResponse  = candidateService.addCandidate(jsonAddCandidateRequest);
        assertEquals(gson.toJson(""), jsonAddCandidateResponse);

        String candidateToken = addCandidateRequest.getToken();

        AddOfferToElectionProgramDtoRequest addOfferToElectionProgramDtoRequest = new AddOfferToElectionProgramDtoRequest(candidateToken, 0,"build a bridge across the river");
        assertEquals(candidateToken, addOfferToElectionProgramDtoRequest.getCandidateToken());
        assertEquals("build a bridge across the river", addOfferToElectionProgramDtoRequest.getOfferDescription());

        Map<String, Voter> tokensAndVoters = dataBase.getTokensAndVoters();
        addOfferToElectionProgramDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(0, addOfferToElectionProgramDtoRequest.getTokensAndVoters().size());

        String jsonAddOfferRequest = gson.toJson(addOfferToElectionProgramDtoRequest);
        String jsonAddOfferResponse = offerService.addOfferToElectionProgram(jsonAddOfferRequest);
        assertEquals(gson.toJson(""), jsonAddOfferResponse);

        RemoveOfferFromElectionProgramDtoRequest removeOfferFromElectionProgramDtoRequest = new RemoveOfferFromElectionProgramDtoRequest(candidateToken, 0,"build a bridge across the river");
        assertEquals(candidateToken, removeOfferFromElectionProgramDtoRequest.getCandidateToken());
        assertEquals("build a bridge across the river", removeOfferFromElectionProgramDtoRequest.getOfferDescription());

        tokensAndVoters.put(candidateToken, new Candidate("ivan", "ivanov", "123456789"));
        assertTrue(tokensAndVoters.containsKey(candidateToken));
        removeOfferFromElectionProgramDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(1, removeOfferFromElectionProgramDtoRequest.getTokensAndVoters().size());

        String jsonRemoveOfferRequest = gson.toJson(removeOfferFromElectionProgramDtoRequest);
        String jsonRemoveOfferResponse = offerService.removeOfferFromElectionProgram(jsonRemoveOfferRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonRemoveOfferResponse);
    }

    @Test
    public void testChangeOfferRating() throws VoterException {
        RegisterVoterDtoRequest registerRequest1 = new RegisterVoterDtoRequest(0, "petr","petrov","12345678");
        assertEquals("petr", registerRequest1.getFirstName());
        assertEquals("petrov", registerRequest1.getLogin());
        assertEquals("12345678", registerRequest1.getPassword());
        String jsonRegisterRequest1 = gson.toJson(registerRequest1);
        String jsonRegisterResponse1  = voterService.registerVoter(jsonRegisterRequest1);

        RegisterVoterDtoResponse registerVoterDtoResponse1 = gson.fromJson(jsonRegisterResponse1, RegisterVoterDtoResponse.class);
        String resultRegisterJson1 = gson.toJson(registerVoterDtoResponse1);
        assertEquals(resultRegisterJson1, jsonRegisterResponse1);

        AddCandidateDtoRequest addCandidateRequest = new AddCandidateDtoRequest(registerRequest1.getId(), registerRequest1.getFirstName(), registerRequest1.getLogin(),
                registerRequest1.getPassword(), registerVoterDtoResponse1.getToken());
        assertEquals(registerRequest1.getFirstName(), addCandidateRequest.getFirstName());
        assertEquals(registerRequest1.getLogin(), addCandidateRequest.getLogin());
        assertEquals(registerRequest1.getPassword(), addCandidateRequest.getPassword());
        assertEquals(registerVoterDtoResponse1.getToken(), addCandidateRequest.getToken());

        String jsonAddCandidateRequest1 = gson.toJson(addCandidateRequest);
        String jsonAddCandidateResponse1  = candidateService.addCandidate(jsonAddCandidateRequest1);

        AddCandidateDtoResponse addCandidateDtoResponse1 = gson.fromJson(jsonAddCandidateResponse1, AddCandidateDtoResponse.class);
        String resultAddCandidateResponseJson1 = gson.toJson(addCandidateDtoResponse1);
        assertEquals(resultAddCandidateResponseJson1, jsonAddCandidateResponse1);

        RegisterVoterDtoRequest registerRequest2 = new RegisterVoterDtoRequest(1, "boris1","borisov1","12345678");
        assertEquals("boris1", registerRequest2.getFirstName());
        assertEquals("borisov1", registerRequest2.getLogin());
        assertEquals("12345678", registerRequest2.getPassword());
        String jsonRegisterRequest2 = gson.toJson(registerRequest2);
        String jsonRegisterResponse2  = voterService.registerVoter(jsonRegisterRequest2);

        RegisterVoterDtoResponse registerVoterDtoResponse2 = gson.fromJson(jsonRegisterResponse2, RegisterVoterDtoResponse.class);
        String resultRegisterJson2 = gson.toJson(registerVoterDtoResponse2);
        assertEquals(resultRegisterJson2, jsonRegisterResponse2);

        String authorToken = registerVoterDtoResponse2.getToken();
        String candidateToken = addCandidateDtoResponse1.getToken();

        AddOfferDtoRequest addOfferDtoRequest = new AddOfferDtoRequest(authorToken,1, candidateToken,0, "build a bridge across the river");
        assertEquals(authorToken, addOfferDtoRequest.getVoterToken());
        assertEquals(candidateToken, addOfferDtoRequest.getCandidateToken());
        assertEquals("build a bridge across the river", addOfferDtoRequest.getOfferDescription());

        Map<String,Voter> tokensAndVoters = dataBase.getTokensAndVoters();
        addOfferDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(2, addOfferDtoRequest.getTokensAndVoters().size());

        String jsonAddOfferRequest = gson.toJson(addOfferDtoRequest);
        String jsonAddOfferResponse = offerService.addOffer(jsonAddOfferRequest);
        assertEquals(gson.toJson(""), jsonAddOfferResponse);

        String voterToken = registerVoterDtoResponse2.getToken();
        Voter voter = dataBase.getVoterByLogin(registerRequest2.getLogin());
        Voter author = dataBase.getCandidateByLogin(registerRequest1.getLogin());
        ChangeOfferRatingDtoRequest changeOfferRatingDtoRequest = new ChangeOfferRatingDtoRequest(voterToken,voter, authorToken,author, 1);
        assertEquals(voterToken, changeOfferRatingDtoRequest.getVoterToken());
        assertEquals(1, changeOfferRatingDtoRequest.getRating());

        changeOfferRatingDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(2, changeOfferRatingDtoRequest.getTokensAndVoters().size());

        String jsonChangeRatingRequest = gson.toJson(changeOfferRatingDtoRequest);
        String jsonChangeRatingResponse = offerService.changeOfferRating(jsonChangeRatingRequest);
        assertEquals(gson.toJson(""), jsonChangeRatingResponse);
    }

    @Test
    public void testChangeRatingWrongVoterToken1() {
        String authorToken = GenerateTokenService.generateNewToken();

        ChangeOfferRatingDtoRequest changeOfferRatingDtoRequest = new ChangeOfferRatingDtoRequest("", new Voter("ivan", "ivanov", "123456789"), authorToken,new Voter("petr", "petrov", "123456789"), 1);
        assertEquals("", changeOfferRatingDtoRequest.getVoterToken());

        Map<String, Voter> tokensAndVoters = dataBase.getTokensAndVoters();
        changeOfferRatingDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(0, changeOfferRatingDtoRequest.getTokensAndVoters().size());

        String jsonChangeRatingRequest = gson.toJson(changeOfferRatingDtoRequest);
        String jsonChangeRatingResponse = offerService.changeOfferRating(jsonChangeRatingRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonChangeRatingResponse);
    }

    @Test
    public void testChangeRatingWrongVoterToken2() {
        String authorToken = GenerateTokenService.generateNewToken();

        ChangeOfferRatingDtoRequest changeOfferRatingDtoRequest = new ChangeOfferRatingDtoRequest(null, new Voter("ivan", "ivanov", "123456789"), authorToken, new Voter("petr", "petrov", "123456789"),1);
        assertEquals(null, changeOfferRatingDtoRequest.getVoterToken());

        Map<String, Voter> tokensAndVoters = dataBase.getTokensAndVoters();
        changeOfferRatingDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(0, changeOfferRatingDtoRequest.getTokensAndVoters().size());

        String jsonChangeRatingRequest = gson.toJson(changeOfferRatingDtoRequest);
        String jsonChangeRatingResponse = offerService.changeOfferRating(jsonChangeRatingRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonChangeRatingResponse);
    }

    @Test
    public void testChangeRatingWrongAuthorToken1() {
        String voterToken = GenerateTokenService.generateNewToken();

        ChangeOfferRatingDtoRequest changeOfferRatingDtoRequest = new ChangeOfferRatingDtoRequest( voterToken, new Voter("ivan", "ivanov", "123456789"), "", new Voter("petr", "petrov", "123456789"), 1);
        assertEquals("", changeOfferRatingDtoRequest.getAuthorToken());

        Map<String, Voter> tokensAndVoters = dataBase.getTokensAndVoters();
        changeOfferRatingDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(0, changeOfferRatingDtoRequest.getTokensAndVoters().size());

        String jsonChangeRatingRequest = gson.toJson(changeOfferRatingDtoRequest);
        String jsonChangeRatingResponse = offerService.changeOfferRating(jsonChangeRatingRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonChangeRatingResponse);
    }

    @Test
    public void testChangeRatingWrongAuthorToken2() {
        String voterToken = GenerateTokenService.generateNewToken();

        ChangeOfferRatingDtoRequest changeOfferRatingDtoRequest = new ChangeOfferRatingDtoRequest(voterToken,new Voter("ivan", "ivanov", "123456789"), null, new Voter("petr", "petrov", "123456789"), 1);
        assertEquals(null, changeOfferRatingDtoRequest.getAuthorToken());

        Map<String, Voter> tokensAndVoters = dataBase.getTokensAndVoters();
        changeOfferRatingDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(0, changeOfferRatingDtoRequest.getTokensAndVoters().size());

        String jsonChangeRatingRequest = gson.toJson(changeOfferRatingDtoRequest);
        String jsonChangeRatingResponse = offerService.changeOfferRating(jsonChangeRatingRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonChangeRatingResponse);
    }

    @Test
    public void testChangeRatingWrongRating1() {
        String voterToken = GenerateTokenService.generateNewToken();
        String authorToken = GenerateTokenService.generateNewToken();

        ChangeOfferRatingDtoRequest changeOfferRatingDtoRequest = new ChangeOfferRatingDtoRequest(voterToken, new Voter("ivan", "ivanov", "123456789"), authorToken,new Voter("petr", "petrov", "123456789"), 0);
        assertEquals(0, changeOfferRatingDtoRequest.getRating());

        Map<String, Voter> tokensAndVoters = dataBase.getTokensAndVoters();
        changeOfferRatingDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(0, changeOfferRatingDtoRequest.getTokensAndVoters().size());

        String jsonChangeRatingRequest = gson.toJson(changeOfferRatingDtoRequest);
        String jsonChangeRatingResponse = offerService.changeOfferRating(jsonChangeRatingRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonChangeRatingResponse);
    }

    @Test
    public void testChangeRatingWrongRating2() {
        String voterToken = GenerateTokenService.generateNewToken();
        String authorToken = GenerateTokenService.generateNewToken();

        ChangeOfferRatingDtoRequest changeOfferRatingDtoRequest = new ChangeOfferRatingDtoRequest(voterToken, new Voter("ivan", "ivanov", "123456789"), authorToken, new Voter("petr", "petrov", "123456789"),6);
        assertEquals(6, changeOfferRatingDtoRequest.getRating());

        Map<String, Voter> tokensAndVoters = dataBase.getTokensAndVoters();
        changeOfferRatingDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(0, changeOfferRatingDtoRequest.getTokensAndVoters().size());

        String jsonChangeRatingRequest = gson.toJson(changeOfferRatingDtoRequest);
        String jsonChangeRatingResponse = offerService.changeOfferRating(jsonChangeRatingRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonChangeRatingResponse);
    }

    @Test
    public void testRemoveOfferRating() {
        RegisterVoterDtoRequest registerRequest1 = new RegisterVoterDtoRequest(0, "sergei5","sergeev5","12345678");
        assertEquals("sergei5", registerRequest1.getFirstName());
        assertEquals("sergeev5", registerRequest1.getLogin());
        assertEquals("12345678", registerRequest1.getPassword());
        String jsonRegisterRequest1 = gson.toJson(registerRequest1);
        String jsonRegisterResponse1  = voterService.registerVoter(jsonRegisterRequest1);

        RegisterVoterDtoResponse registerVoterDtoResponse1 = gson.fromJson(jsonRegisterResponse1, RegisterVoterDtoResponse.class);
        String resultRegisterJson1 = gson.toJson(registerVoterDtoResponse1);
        assertEquals(resultRegisterJson1, jsonRegisterResponse1);

        AddCandidateDtoRequest addCandidateRequest = new AddCandidateDtoRequest(registerRequest1.getId(), registerRequest1.getFirstName(), registerRequest1.getLogin(),
                registerRequest1.getPassword(), registerVoterDtoResponse1.getToken());
        assertEquals(registerRequest1.getFirstName(), addCandidateRequest.getFirstName());
        assertEquals(registerRequest1.getLogin(), addCandidateRequest.getLogin());
        assertEquals(registerRequest1.getPassword(), addCandidateRequest.getPassword());
        assertEquals(registerVoterDtoResponse1.getToken(), addCandidateRequest.getToken());

        String jsonAddCandidateRequest1 = gson.toJson(addCandidateRequest);
        String jsonAddCandidateResponse1  = candidateService.addCandidate(jsonAddCandidateRequest1);

        AddCandidateDtoResponse addCandidateDtoResponse1 = gson.fromJson(jsonAddCandidateResponse1, AddCandidateDtoResponse.class);
        String resultAddCandidateResponseJson1 = gson.toJson(addCandidateDtoResponse1);
        assertEquals(resultAddCandidateResponseJson1, jsonAddCandidateResponse1);

        RegisterVoterDtoRequest registerRequest2 = new RegisterVoterDtoRequest(1, "boris5","borisov5","12345678");
        assertEquals("boris5", registerRequest2.getFirstName());
        assertEquals("borisov5", registerRequest2.getLogin());
        assertEquals("12345678", registerRequest2.getPassword());
        String jsonRegisterRequest2 = gson.toJson(registerRequest2);
        String jsonRegisterResponse2  = voterService.registerVoter(jsonRegisterRequest2);

        RegisterVoterDtoResponse registerVoterDtoResponse2 = gson.fromJson(jsonRegisterResponse2, RegisterVoterDtoResponse.class);
        String resultRegisterJson2 = gson.toJson(registerVoterDtoResponse2);
        assertEquals(resultRegisterJson2, jsonRegisterResponse2);

        String authorToken = registerVoterDtoResponse2.getToken();
        String candidateToken = addCandidateDtoResponse1.getToken();

        AddOfferDtoRequest addOfferDtoRequest = new AddOfferDtoRequest(authorToken, 1,candidateToken,0, "build a bridge across the river");
        assertEquals(authorToken, addOfferDtoRequest.getVoterToken());
        assertEquals(candidateToken, addOfferDtoRequest.getCandidateToken());
        assertEquals("build a bridge across the river", addOfferDtoRequest.getOfferDescription());

        Map<String,Voter> tokensAndVoters = dataBase.getTokensAndVoters();
        addOfferDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(2, addOfferDtoRequest.getTokensAndVoters().size());

        String jsonAddOfferRequest = gson.toJson(addOfferDtoRequest);
        String jsonAddOfferResponse = offerService.addOffer(jsonAddOfferRequest);
        assertEquals(gson.toJson(""), jsonAddOfferResponse);

        String voterToken = registerVoterDtoResponse2.getToken();

        Voter voter = dataBase.getVoterById(registerRequest2.getId());
        Voter author = dataBase.getCandidateById(addCandidateRequest.getId());

        RemoveOfferRatingDtoRequest removeOfferRatingDtoRequest = new RemoveOfferRatingDtoRequest(voterToken, authorToken, voter, author);
        assertEquals(voterToken, removeOfferRatingDtoRequest.getVoterToken());
        assertEquals(authorToken, removeOfferRatingDtoRequest.getAuthorToken());

        removeOfferRatingDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(2, removeOfferRatingDtoRequest.getTokensAndVoters().size());

        String jsonRemoveRatingRequest = gson.toJson(removeOfferRatingDtoRequest);
        String jsonRemoveRatingResponse = offerService.removeOfferRating(jsonRemoveRatingRequest);
        assertEquals(gson.toJson(""), jsonRemoveRatingResponse);
    }

    @Test
    public void testRemoveRatingWrongVoterToken1() {
        String authorToken = GenerateTokenService.generateNewToken();

        Voter voter = new Voter("ivan", "ivanov", "123456789");
        Voter author = new Candidate("sergei", "sergeev", "12345678");

        RemoveOfferRatingDtoRequest removeOfferRatingDtoRequest = new RemoveOfferRatingDtoRequest("", authorToken,voter, author);
        assertEquals("", removeOfferRatingDtoRequest.getVoterToken());

        dataBase.getTokensAndVoters().put(removeOfferRatingDtoRequest.getVoterToken(), voter);
        removeOfferRatingDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
        assertEquals(1, removeOfferRatingDtoRequest.getTokensAndVoters().size());

        String jsonChangeRatingRequest = gson.toJson(removeOfferRatingDtoRequest);
        String jsonChangeRatingResponse = offerService.removeOfferRating(jsonChangeRatingRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonChangeRatingResponse);
    }

    @Test
    public void testRemoveRatingWrongVoterToken2() {
        String authorToken = GenerateTokenService.generateNewToken();

        Voter voter = new Voter("ivan", "ivanov", "123456789");
        Voter author = new Candidate("sergei", "sergeev", "12345678");

        RemoveOfferRatingDtoRequest removeOfferRatingDtoRequest = new RemoveOfferRatingDtoRequest(null, authorToken, voter, author);
        assertEquals(null, removeOfferRatingDtoRequest.getVoterToken());

        dataBase.getTokensAndVoters().put(removeOfferRatingDtoRequest.getVoterToken(), voter);
        removeOfferRatingDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
        assertEquals(1, removeOfferRatingDtoRequest.getTokensAndVoters().size());

        String jsonChangeRatingRequest = gson.toJson(removeOfferRatingDtoRequest);
        String jsonChangeRatingResponse = offerService.removeOfferRating(jsonChangeRatingRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonChangeRatingResponse);
    }

    @Test
    public void testRemoveRatingWrongAuthorToken1() {
        String voterToken = GenerateTokenService.generateNewToken();

        Voter voter = new Voter("ivan", "ivanov", "123456789");
        Voter author = new Candidate("sergei", "sergeev", "12345678");

        RemoveOfferRatingDtoRequest removeOfferRatingDtoRequest = new RemoveOfferRatingDtoRequest( voterToken, "", voter, author);
        assertEquals("", removeOfferRatingDtoRequest.getAuthorToken());

        dataBase.getTokensAndVoters().put(removeOfferRatingDtoRequest.getVoterToken(), voter);
        removeOfferRatingDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
        assertEquals(1, removeOfferRatingDtoRequest.getTokensAndVoters().size());

        String jsonChangeRatingRequest = gson.toJson(removeOfferRatingDtoRequest);
        String jsonChangeRatingResponse = offerService.changeOfferRating(jsonChangeRatingRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonChangeRatingResponse);
    }

    @Test
    public void testRemoveRatingWrongAuthorToken2() {
        String voterToken = GenerateTokenService.generateNewToken();

        Voter voter = new Voter("ivan", "ivanov", "123456789");
        Voter author = new Candidate("sergei", "sergeev", "12345678");

        RemoveOfferRatingDtoRequest removeOfferRatingDtoRequest = new RemoveOfferRatingDtoRequest( voterToken, null, voter, author);
        assertEquals(null, removeOfferRatingDtoRequest.getAuthorToken());

        dataBase.getTokensAndVoters().put(removeOfferRatingDtoRequest.getVoterToken(), voter);
        removeOfferRatingDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
        assertEquals(1, removeOfferRatingDtoRequest.getTokensAndVoters().size());

        String jsonChangeRatingRequest = gson.toJson(removeOfferRatingDtoRequest);
        String jsonChangeRatingResponse = offerService.changeOfferRating(jsonChangeRatingRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonChangeRatingResponse);
    }

    @Ignore
    @Test
    public void testGetOffersAndAverageRatings() {
        RegisterVoterDtoRequest registerRequest1 = new RegisterVoterDtoRequest(0, "miron","mironov","12345678");
        assertEquals("miron", registerRequest1.getFirstName());
        assertEquals("mironov", registerRequest1.getLogin());
        assertEquals("12345678", registerRequest1.getPassword());
        String jsonRegisterRequest1 = gson.toJson(registerRequest1);
        String jsonRegisterResponse1  = voterService.registerVoter(jsonRegisterRequest1);

        RegisterVoterDtoResponse registerVoterDtoResponse1 = gson.fromJson(jsonRegisterResponse1, RegisterVoterDtoResponse.class);
        String resultRegisterJson1 = gson.toJson(registerVoterDtoResponse1);
        assertEquals(resultRegisterJson1, jsonRegisterResponse1);

        AddCandidateDtoRequest addCandidateRequest = new AddCandidateDtoRequest(registerRequest1.getId(), registerRequest1.getFirstName(), registerRequest1.getLogin(),
                                                                                registerRequest1.getPassword(), registerVoterDtoResponse1.getToken());
        assertEquals(registerRequest1.getFirstName(), addCandidateRequest.getFirstName());
        assertEquals(registerRequest1.getLogin(), addCandidateRequest.getLogin());
        assertEquals(registerRequest1.getPassword(), addCandidateRequest.getPassword());
        assertEquals(registerVoterDtoResponse1.getToken(), addCandidateRequest.getToken());

        String jsonAddCandidateRequest1 = gson.toJson(addCandidateRequest);
        String jsonAddCandidateResponse1  = candidateService.addCandidate(jsonAddCandidateRequest1);

        AddCandidateDtoResponse addCandidateDtoResponse1 = gson.fromJson(jsonAddCandidateResponse1, AddCandidateDtoResponse.class);
        String resultAddCandidateResponseJson1 = gson.toJson(addCandidateDtoResponse1);
        assertEquals(resultAddCandidateResponseJson1, jsonAddCandidateResponse1);

        RegisterVoterDtoRequest registerRequest2 = new RegisterVoterDtoRequest(1, "egor","egorov","12345678");
        assertEquals("egor", registerRequest2.getFirstName());
        assertEquals("egorov", registerRequest2.getLogin());
        assertEquals("12345678", registerRequest2.getPassword());
        String jsonRegisterRequest2 = gson.toJson(registerRequest2);
        String jsonRegisterResponse2  = voterService.registerVoter(jsonRegisterRequest2);

        RegisterVoterDtoResponse registerVoterDtoResponse2 = gson.fromJson(jsonRegisterResponse2, RegisterVoterDtoResponse.class);
        String resultRegisterJson2 = gson.toJson(registerVoterDtoResponse2);
        assertEquals(resultRegisterJson2, jsonRegisterResponse2);

        RegisterVoterDtoRequest registerRequest3 = new RegisterVoterDtoRequest(2, "efim","efimov","12345678");
        assertEquals("efim", registerRequest3.getFirstName());
        assertEquals("efimov", registerRequest3.getLogin());
        assertEquals("12345678", registerRequest3.getPassword());
        String jsonRegisterRequest3 = gson.toJson(registerRequest3);
        String jsonRegisterResponse3  = voterService.registerVoter(jsonRegisterRequest3);

        RegisterVoterDtoResponse registerVoterDtoResponse3 = gson.fromJson(jsonRegisterResponse3, RegisterVoterDtoResponse.class);
        String resultRegisterJson3 = gson.toJson(registerVoterDtoResponse3);
        assertEquals(resultRegisterJson3, jsonRegisterResponse3);

        String voterToken1 = registerVoterDtoResponse2.getToken();
        String voterToken2 = registerVoterDtoResponse3.getToken();
        String candidateToken = addCandidateDtoResponse1.getToken();

        AddOfferDtoRequest addOfferDtoRequest1 = new AddOfferDtoRequest(voterToken1,1, candidateToken,0, "build a bridge across the river");
        assertEquals(voterToken1, addOfferDtoRequest1.getVoterToken());
        assertEquals(candidateToken, addOfferDtoRequest1.getCandidateToken());
        assertEquals("build a bridge across the river", addOfferDtoRequest1.getOfferDescription());

        Map<String, Voter> tokensAndVoters = dataBase.getTokensAndVoters();
        addOfferDtoRequest1.setTokensAndVoters(tokensAndVoters);
        assertEquals(3, addOfferDtoRequest1.getTokensAndVoters().size());

        String jsonAddOfferRequest = gson.toJson(addOfferDtoRequest1);
        String jsonAddOfferResponse = offerService.addOffer(jsonAddOfferRequest);
        assertEquals(gson.toJson(""), jsonAddOfferResponse);

        AddOfferDtoRequest addOfferDtoRequest2 = new AddOfferDtoRequest(voterToken2,0, candidateToken,0, "repair the road");
        assertEquals(voterToken2, addOfferDtoRequest2.getVoterToken());
        assertEquals(candidateToken, addOfferDtoRequest2.getCandidateToken());
        assertEquals("repair the road", addOfferDtoRequest2.getOfferDescription());

        addOfferDtoRequest2.setTokensAndVoters(tokensAndVoters);
        assertEquals(3, addOfferDtoRequest2.getTokensAndVoters().size());

        String jsonAddOfferRequest2 = gson.toJson(addOfferDtoRequest2);
        String jsonAddOfferResponse2 = offerService.addOffer(jsonAddOfferRequest2);
        assertEquals(gson.toJson(""), jsonAddOfferResponse2);

        ChangeOfferRatingDtoRequest changeOfferRatingDtoRequest = new ChangeOfferRatingDtoRequest(voterToken2, new Voter("ivan", "ivanov", "123456789"),voterToken1,new Voter("petr", "petrov", "123456789"), 1);
        assertEquals(voterToken2, changeOfferRatingDtoRequest.getVoterToken());
        assertEquals(1, changeOfferRatingDtoRequest.getRating());

        changeOfferRatingDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(3, changeOfferRatingDtoRequest.getTokensAndVoters().size());

        String jsonChangeRatingRequest = gson.toJson(changeOfferRatingDtoRequest);
        String jsonChangeRatingResponse = offerService.changeOfferRating(jsonChangeRatingRequest);
        assertEquals(gson.toJson(""), jsonChangeRatingResponse);

        GetOffersAndAverageRatingsDtoRequest OffersAndAverageRatingsDtoRequest = new GetOffersAndAverageRatingsDtoRequest(voterToken1);
        assertEquals(voterToken1, OffersAndAverageRatingsDtoRequest.getToken());
        String jsonGetOffersSortedByAverageRatingsRequest = gson.toJson(OffersAndAverageRatingsDtoRequest);
        String jsonGetOffersSortedByAverageRatingsResponse = offerService.getOffersAndAverageRatings(jsonGetOffersSortedByAverageRatingsRequest);

        GetOffersAndAverageRatingsDtoResponse OffersAndAverageRatingsDtoResponse = gson.fromJson(jsonGetOffersSortedByAverageRatingsResponse, GetOffersAndAverageRatingsDtoResponse.class);
        String resultGetOffersSortedByAverageRatings = new GsonBuilder().enableComplexMapKeySerialization()
                .create()
                .toJson(OffersAndAverageRatingsDtoResponse);
        assertEquals(resultGetOffersSortedByAverageRatings, jsonGetOffersSortedByAverageRatingsResponse);
        assertThat(OffersAndAverageRatingsDtoResponse.getOffersSortedByAverageRatings().values(), contains(5, 1));
    }

    @Ignore
    @Test
    public void testGetOffersAndAverageRatingsIfOfferWithoutRatings() {
        RegisterVoterDtoRequest registerRequest1 = new RegisterVoterDtoRequest(0, "miron","mironov","12345678");
        assertEquals("miron", registerRequest1.getFirstName());
        assertEquals("mironov", registerRequest1.getLogin());
        assertEquals("12345678", registerRequest1.getPassword());
        String jsonRegisterRequest1 = gson.toJson(registerRequest1);
        String jsonRegisterResponse1  = voterService.registerVoter(jsonRegisterRequest1);

        RegisterVoterDtoResponse registerVoterDtoResponse1 = gson.fromJson(jsonRegisterResponse1, RegisterVoterDtoResponse.class);
        String resultRegisterJson1 = gson.toJson(registerVoterDtoResponse1);
        assertEquals(resultRegisterJson1, jsonRegisterResponse1);

        AddCandidateDtoRequest addCandidateRequest = new AddCandidateDtoRequest(registerRequest1.getId(), registerRequest1.getFirstName(), registerRequest1.getLogin(),
                                                                                registerRequest1.getPassword(), registerVoterDtoResponse1.getToken());
        assertEquals(registerRequest1.getFirstName(), addCandidateRequest.getFirstName());
        assertEquals(registerRequest1.getLogin(), addCandidateRequest.getLogin());
        assertEquals(registerRequest1.getPassword(), addCandidateRequest.getPassword());
        assertEquals(registerVoterDtoResponse1.getToken(), addCandidateRequest.getToken());

        String jsonAddCandidateRequest = gson.toJson(addCandidateRequest);
        String jsonAddCandidateResponse  = candidateService.addCandidate(jsonAddCandidateRequest);
        assertEquals(gson.toJson(""), jsonAddCandidateResponse);

        RegisterVoterDtoRequest registerRequest2 = new RegisterVoterDtoRequest(1, "egor","egorov","12345678");
        assertEquals("egor", registerRequest2.getFirstName());
        assertEquals("egorov", registerRequest2.getLogin());
        assertEquals("12345678", registerRequest2.getPassword());
        String jsonRegisterRequest2 = gson.toJson(registerRequest2);
        String jsonRegisterResponse2  = voterService.registerVoter(jsonRegisterRequest2);

        RegisterVoterDtoResponse registerVoterDtoResponse2 = gson.fromJson(jsonRegisterResponse2, RegisterVoterDtoResponse.class);
        String resultRegisterJson2 = gson.toJson(registerVoterDtoResponse2);
        assertEquals(resultRegisterJson2, jsonRegisterResponse2);

        RegisterVoterDtoRequest registerRequest3 = new RegisterVoterDtoRequest(2, "efim","efimov","12345678");
        assertEquals("efim", registerRequest3.getFirstName());
        assertEquals("efimov", registerRequest3.getLogin());
        assertEquals("12345678", registerRequest3.getPassword());
        String jsonRegisterRequest3 = gson.toJson(registerRequest3);
        String jsonRegisterResponse3  = voterService.registerVoter(jsonRegisterRequest3);

        RegisterVoterDtoResponse registerVoterDtoResponse3 = gson.fromJson(jsonRegisterResponse3, RegisterVoterDtoResponse.class);
        String resultRegisterJson3 = gson.toJson(registerVoterDtoResponse3);
        assertEquals(resultRegisterJson3, jsonRegisterResponse3);

        String voterToken1 = registerVoterDtoResponse2.getToken();
        String voterToken2 = registerVoterDtoResponse3.getToken();
        String candidateToken = addCandidateRequest.getToken();

        AddOfferDtoRequest addOfferDtoRequest1 = new AddOfferDtoRequest(voterToken1, 1,candidateToken,0, "build a bridge across the river");
        assertEquals(voterToken1, addOfferDtoRequest1.getVoterToken());
        assertEquals(candidateToken, addOfferDtoRequest1.getCandidateToken());
        assertEquals("build a bridge across the river", addOfferDtoRequest1.getOfferDescription());

        Map<String, Voter> tokensAndVoters = dataBase.getTokensAndVoters();
        addOfferDtoRequest1.setTokensAndVoters(tokensAndVoters);
        assertEquals(3, addOfferDtoRequest1.getTokensAndVoters().size());

        String jsonAddOfferRequest = gson.toJson(addOfferDtoRequest1);
        String jsonAddOfferResponse = offerService.addOffer(jsonAddOfferRequest);
        assertEquals(gson.toJson(""), jsonAddOfferResponse);

        AddOfferDtoRequest addOfferDtoRequest2 = new AddOfferDtoRequest(voterToken2,2, candidateToken,0, "repair the road");
        assertEquals(voterToken2, addOfferDtoRequest2.getVoterToken());
        assertEquals(candidateToken, addOfferDtoRequest2.getCandidateToken());
        assertEquals("repair the road", addOfferDtoRequest2.getOfferDescription());

        addOfferDtoRequest2.setTokensAndVoters(tokensAndVoters);
        assertEquals(3, addOfferDtoRequest2.getTokensAndVoters().size());

        String jsonAddOfferRequest2 = gson.toJson(addOfferDtoRequest2);
        String jsonAddOfferResponse2 = offerService.addOffer(jsonAddOfferRequest2);
        assertEquals(gson.toJson(""), jsonAddOfferResponse2);

        Voter voter1 = dataBase.getVoterById(registerRequest2.getId());
        Voter voter2 = dataBase.getVoterById(registerRequest3.getId());

        RemoveOfferRatingDtoRequest removeOfferRatingDtoRequest = new RemoveOfferRatingDtoRequest(voterToken1, voterToken2, voter1, voter2);
        assertEquals(voterToken1, removeOfferRatingDtoRequest.getVoterToken());
        assertEquals(voterToken2, removeOfferRatingDtoRequest.getAuthorToken());

        removeOfferRatingDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(3, removeOfferRatingDtoRequest.getTokensAndVoters().size());

        String jsonRemoveRatingRequest = gson.toJson(removeOfferRatingDtoRequest);
        String jsonRemoveRatingResponse = offerService.removeOfferRating(jsonRemoveRatingRequest);
        assertEquals(gson.toJson(""), jsonRemoveRatingResponse);

        GetOffersAndAverageRatingsDtoRequest OffersAndAverageRatingsDtoRequest = new GetOffersAndAverageRatingsDtoRequest(voterToken1);
        assertEquals(voterToken1, OffersAndAverageRatingsDtoRequest.getToken());
        String jsonGetOffersSortedByAverageRatingsRequest = gson.toJson(OffersAndAverageRatingsDtoRequest);
        String jsonGetOffersSortedByAverageRatingsResponse = offerService.getOffersAndAverageRatings(jsonGetOffersSortedByAverageRatingsRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonGetOffersSortedByAverageRatingsResponse);
    }

    @Test
    public void testGetOffersAndAverageRatingsWrongToken1() {
        GetOffersAndAverageRatingsDtoRequest OffersAndAverageRatingsDtoRequest = new GetOffersAndAverageRatingsDtoRequest(null);
        assertEquals(null, OffersAndAverageRatingsDtoRequest.getToken());

        String jsonRequest = gson.toJson(OffersAndAverageRatingsDtoRequest);
        String jsonResponse = offerService.getOffersAndAverageRatings(jsonRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonResponse);
    }

    @Test
    public void testGetOffersAndAverageRatingsWrongToken2() {
        GetOffersAndAverageRatingsDtoRequest OffersAndAverageRatingsDtoRequest = new GetOffersAndAverageRatingsDtoRequest("");
        assertEquals("", OffersAndAverageRatingsDtoRequest.getToken());

        String jsonRequest = gson.toJson(OffersAndAverageRatingsDtoRequest);
        String jsonResponse = offerService.getOffersAndAverageRatings(jsonRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonResponse);
    }

    @Ignore
    @Test
    public void testGetOffersAndAverageRatingsOfflineToken() {
        RegisterVoterDtoRequest registerVoterRequest = new RegisterVoterDtoRequest(0, "abram","abramov","123456789");
        assertEquals("abram", registerVoterRequest.getFirstName());
        assertEquals("abramov", registerVoterRequest.getLogin());
        assertEquals("123456789", registerVoterRequest.getPassword());

        String jsonRegisterRequest = gson.toJson(registerVoterRequest);
        String jsonRegisterResponse  = voterService.registerVoter(jsonRegisterRequest);
        RegisterVoterDtoResponse registerVoterDtoResponse = gson.fromJson(jsonRegisterResponse, RegisterVoterDtoResponse.class);
        String resultRegisterJson = gson.toJson(registerVoterDtoResponse);
        assertEquals(resultRegisterJson, jsonRegisterResponse);

        GetOffersAndAverageRatingsDtoRequest OffersAndAverageRatingsDtoRequest = new GetOffersAndAverageRatingsDtoRequest(registerVoterDtoResponse.getToken());
        assertEquals(OffersAndAverageRatingsDtoRequest.getToken(), registerVoterDtoResponse.getToken());

        DataBase dataBase = DataBase.getDataBase();
        Map<String, Voter> tokensAndVoters = dataBase.getTokensAndVoters();
        tokensAndVoters.put(registerVoterDtoResponse.getToken(), new Voter("abram","abramov","123456789"));
        OffersAndAverageRatingsDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(tokensAndVoters, OffersAndAverageRatingsDtoRequest.getTokensAndVoters());

        String jsonRequest = gson.toJson(OffersAndAverageRatingsDtoRequest);
        String jsonResponse = offerService.getOffersAndAverageRatings(jsonRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonResponse);
    }

    @Ignore
    @Test
    public void testGetOffersSortedByAverageRatings() throws VoterException {
        RegisterVoterDtoRequest registerRequest1 = new RegisterVoterDtoRequest(0, "miron","mironov","12345678");
        assertEquals("miron", registerRequest1.getFirstName());
        assertEquals("mironov", registerRequest1.getLogin());
        assertEquals("12345678", registerRequest1.getPassword());
        String jsonRegisterRequest1 = gson.toJson(registerRequest1);
        String jsonRegisterResponse1  = voterService.registerVoter(jsonRegisterRequest1);

        RegisterVoterDtoResponse registerVoterDtoResponse1 = gson.fromJson(jsonRegisterResponse1, RegisterVoterDtoResponse.class);
        String resultRegisterJson1 = gson.toJson(registerVoterDtoResponse1);
        assertEquals(resultRegisterJson1, jsonRegisterResponse1);

        AddCandidateDtoRequest addCandidateRequest = new AddCandidateDtoRequest(registerRequest1.getId(), registerRequest1.getFirstName(), registerRequest1.getLogin(),
                                                                                registerRequest1.getPassword(), registerVoterDtoResponse1.getToken());
        assertEquals(registerRequest1.getFirstName(), addCandidateRequest.getFirstName());
        assertEquals(registerRequest1.getLogin(), addCandidateRequest.getLogin());
        assertEquals(registerRequest1.getPassword(), addCandidateRequest.getPassword());
        assertEquals(registerVoterDtoResponse1.getToken(), addCandidateRequest.getToken());

        String jsonAddCandidateRequest = gson.toJson(addCandidateRequest);
        String jsonAddCandidateResponse  = candidateService.addCandidate(jsonAddCandidateRequest);
        assertEquals(gson.toJson(""), jsonAddCandidateResponse);

        RegisterVoterDtoRequest registerRequest2 = new RegisterVoterDtoRequest(1, "egor","egorov","12345678");
        assertEquals("egor", registerRequest2.getFirstName());
        assertEquals("egorov", registerRequest2.getLogin());
        assertEquals("12345678", registerRequest2.getPassword());
        String jsonRegisterRequest2 = gson.toJson(registerRequest2);
        String jsonRegisterResponse2  = voterService.registerVoter(jsonRegisterRequest2);

        RegisterVoterDtoResponse registerVoterDtoResponse2 = gson.fromJson(jsonRegisterResponse2, RegisterVoterDtoResponse.class);
        String resultRegisterJson2 = gson.toJson(registerVoterDtoResponse2);
        assertEquals(resultRegisterJson2, jsonRegisterResponse2);

        RegisterVoterDtoRequest registerRequest3 = new RegisterVoterDtoRequest(2, "efim","efimov","12345678");
        assertEquals("efim", registerRequest3.getFirstName());
        assertEquals("efimov", registerRequest3.getLogin());
        assertEquals("12345678", registerRequest3.getPassword());
        String jsonRegisterRequest3 = gson.toJson(registerRequest3);
        String jsonRegisterResponse3  = voterService.registerVoter(jsonRegisterRequest3);

        RegisterVoterDtoResponse registerVoterDtoResponse3 = gson.fromJson(jsonRegisterResponse3, RegisterVoterDtoResponse.class);
        String resultRegisterJson3 = gson.toJson(registerVoterDtoResponse3);
        assertEquals(resultRegisterJson3, jsonRegisterResponse3);

        String voterToken1 = registerVoterDtoResponse2.getToken();
        String voterToken2 = registerVoterDtoResponse3.getToken();
        String candidateToken = addCandidateRequest.getToken();

        AddOfferDtoRequest addOfferDtoRequest1 = new AddOfferDtoRequest(voterToken1,1,candidateToken,0, "build a bridge across the river");
        assertEquals(voterToken1, addOfferDtoRequest1.getVoterToken());
        assertEquals(candidateToken, addOfferDtoRequest1.getCandidateToken());
        assertEquals("build a bridge across the river", addOfferDtoRequest1.getOfferDescription());

        Map<String, Voter> tokensAndVoters = dataBase.getTokensAndVoters();
        addOfferDtoRequest1.setTokensAndVoters(tokensAndVoters);
        assertEquals(0, addOfferDtoRequest1.getTokensAndVoters().size());

        String jsonAddOfferRequest = gson.toJson(addOfferDtoRequest1);
        String jsonAddOfferResponse = offerService.addOffer(jsonAddOfferRequest);
        assertEquals(gson.toJson(""), jsonAddOfferResponse);

        AddOfferDtoRequest addOfferDtoRequest2 = new AddOfferDtoRequest(voterToken2,2,  candidateToken, 0, "repair the road");
        assertEquals(voterToken2, addOfferDtoRequest2.getVoterToken());
        assertEquals(candidateToken, addOfferDtoRequest2.getCandidateToken());
        assertEquals("repair the road", addOfferDtoRequest2.getOfferDescription());

        addOfferDtoRequest2.setTokensAndVoters(tokensAndVoters);
        assertEquals(0, addOfferDtoRequest2.getTokensAndVoters().size());

        String jsonAddOfferRequest2 = gson.toJson(addOfferDtoRequest2);
        String jsonAddOfferResponse2 = offerService.addOffer(jsonAddOfferRequest2);
        assertEquals(gson.toJson(""), jsonAddOfferResponse2);

        Voter voter = dataBase.getVoterByLogin(registerRequest3.getLogin());
        Voter author = dataBase.getCandidateByLogin(addCandidateRequest.getLogin());
        ChangeOfferRatingDtoRequest changeOfferRatingDtoRequest = new ChangeOfferRatingDtoRequest(voterToken2, voter, voterToken1,author, 1);
        assertEquals(voterToken2, changeOfferRatingDtoRequest.getVoterToken());
        assertEquals(1, changeOfferRatingDtoRequest.getRating());

        changeOfferRatingDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(0, changeOfferRatingDtoRequest.getTokensAndVoters().size());

        String jsonChangeRatingRequest = gson.toJson(changeOfferRatingDtoRequest);
        String jsonChangeRatingResponse = offerService.changeOfferRating(jsonChangeRatingRequest);
        assertEquals(gson.toJson(""), jsonChangeRatingResponse);

        GetOffersSortedByAverageRatingsDtoRequest offersSortedByAverageRatingsDtoRequest = new GetOffersSortedByAverageRatingsDtoRequest(voterToken1);
        assertEquals(voterToken1, offersSortedByAverageRatingsDtoRequest.getToken());
        String jsonGetOffersSortedByAverageRatingsRequest = gson.toJson(offersSortedByAverageRatingsDtoRequest);
        String jsonGetOffersSortedByAverageRatingsResponse = offerService.getOffersSortedByAverageRatings(jsonGetOffersSortedByAverageRatingsRequest);

        GetOffersSortedByAverageRatingsDtoResponse OffersSortedByAverageRatingsDtoResponse = gson.fromJson(jsonGetOffersSortedByAverageRatingsResponse, GetOffersSortedByAverageRatingsDtoResponse.class);
        String resultGetOffersSortedByAverageRatings = gson.toJson(OffersSortedByAverageRatingsDtoResponse);
        assertEquals(resultGetOffersSortedByAverageRatings, jsonGetOffersSortedByAverageRatingsResponse);
    }

    @Test
    public void testGetOffersSortedByAverageRatingsWrongToken1() {
        GetOffersSortedByAverageRatingsDtoRequest offersSortedByAverageRatingsDtoRequest = new GetOffersSortedByAverageRatingsDtoRequest(null);
        assertEquals(null, offersSortedByAverageRatingsDtoRequest.getToken());

        String jsonRequest = gson.toJson(offersSortedByAverageRatingsDtoRequest);
        String jsonResponse = offerService.getOffersAndAverageRatings(jsonRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonResponse);
    }

    @Test
    public void testGetOffersSortedByAverageRatingsWrongToken2() {
        GetOffersSortedByAverageRatingsDtoRequest offersSortedByAverageRatingsDtoRequest = new GetOffersSortedByAverageRatingsDtoRequest("");
        assertEquals("", offersSortedByAverageRatingsDtoRequest.getToken());

        String jsonRequest = gson.toJson(offersSortedByAverageRatingsDtoRequest);
        String jsonResponse = offerService.getOffersAndAverageRatings(jsonRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonResponse);
    }

    @Ignore
    @Test
    public void testGetOffersSortedByAverageRatingsOfflineToken() {
        RegisterVoterDtoRequest registerVoterRequest = new RegisterVoterDtoRequest(0, "abram","abramov","123456789");
        assertEquals("abram", registerVoterRequest.getFirstName());
        assertEquals("abramov", registerVoterRequest.getLogin());
        assertEquals("123456789", registerVoterRequest.getPassword());

        String jsonRegisterRequest = gson.toJson(registerVoterRequest);
        String jsonRegisterResponse  = voterService.registerVoter(jsonRegisterRequest);
        RegisterVoterDtoResponse registerVoterDtoResponse = gson.fromJson(jsonRegisterResponse, RegisterVoterDtoResponse.class);
        String resultRegisterJson = gson.toJson(registerVoterDtoResponse);
        assertEquals(resultRegisterJson, jsonRegisterResponse);

        GetOffersSortedByAverageRatingsDtoRequest offersSortedByAverageRatingsDtoRequest = new GetOffersSortedByAverageRatingsDtoRequest(registerVoterDtoResponse.getToken());
        assertEquals(offersSortedByAverageRatingsDtoRequest.getToken(), registerVoterDtoResponse.getToken());

        DataBase dataBase = DataBase.getDataBase();
        Map<String,Voter> tokensAndVoters = dataBase.getTokensAndVoters();
        tokensAndVoters.put(registerVoterDtoResponse.getToken(), new Voter("abram","abramov","123456789"));
        offersSortedByAverageRatingsDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(tokensAndVoters, offersSortedByAverageRatingsDtoRequest.getTokensAndVoters());

        String jsonRequest = gson.toJson(offersSortedByAverageRatingsDtoRequest);
        String jsonResponse = offerService.getOffersAndAverageRatings(jsonRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonResponse);
    }
}
