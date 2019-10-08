package net.thumbtack.school.elections.service;

import com.google.gson.Gson;
import net.thumbtack.school.elections.database.DataBase;
import net.thumbtack.school.elections.exception.VoterErrorCode;
import net.thumbtack.school.elections.exception.VoterException;
import net.thumbtack.school.elections.model.Candidate;
import net.thumbtack.school.elections.model.Voter;
import net.thumbtack.school.elections.request.*;
import net.thumbtack.school.elections.response.AddCandidateDtoResponse;
import net.thumbtack.school.elections.response.GetElectionResultsDtoResponse;
import net.thumbtack.school.elections.response.LogoutDtoResponse;
import net.thumbtack.school.elections.response.RegisterVoterDtoResponse;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import java.util.Map;

import static org.junit.Assert.*;

public class TestElectionService {

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
        electionService = new ElectionService();
        candidateService = new CandidateService();
        offerService = new OfferService();
        dataBase = DataBase.getDataBase();
    }

    @Before
    public void setUp(){
        dataBase.getVoters().clear();
        dataBase.getCandidates().clear();
        dataBase.getTokensAndVoters().clear();
        dataBase.getVotedCandidates().clear();
        dataBase.getVotedVoters().clear();
        dataBase.getVotedAgainstAll().clear();
    }

    @Ignore
    @Test
    public void testAddOffer() {
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

        String jsonAddCandidateRequest = gson.toJson(addCandidateRequest);
        String jsonAddCandidateResponse  = candidateService.addCandidate(jsonAddCandidateRequest);
        assertEquals(gson.toJson(""), jsonAddCandidateResponse);

        RegisterVoterDtoRequest registerRequest2 = new RegisterVoterDtoRequest(1, "boris","borisov","12345678");
        assertEquals("boris", registerRequest2.getFirstName());
        assertEquals("borisov", registerRequest2.getLogin());
        assertEquals("12345678", registerRequest2.getPassword());
        String jsonRegisterRequest2 = gson.toJson(registerRequest2);
        String jsonRegisterResponse2  = voterService.registerVoter(jsonRegisterRequest2);

        RegisterVoterDtoResponse registerVoterDtoResponse2 = gson.fromJson(jsonRegisterResponse2, RegisterVoterDtoResponse.class);
        String resultRegisterJson2 = gson.toJson(registerVoterDtoResponse2);
        assertEquals(resultRegisterJson2, jsonRegisterResponse2);

        String voterToken = registerVoterDtoResponse2.getToken();
        String candidateToken = addCandidateRequest.getToken();

        AddOfferDtoRequest addOfferDtoRequest = new AddOfferDtoRequest(voterToken,1, candidateToken,0, "build a bridge across the river");
        assertEquals(voterToken, addOfferDtoRequest.getVoterToken());
        assertEquals(candidateToken, addOfferDtoRequest.getCandidateToken());
        assertEquals("build a bridge across the river", addOfferDtoRequest.getOfferDescription());

        Map<String, Voter> tokensAndVoters = dataBase.getTokensAndVoters();
        addOfferDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(0, addOfferDtoRequest.getTokensAndVoters().size());

        String jsonAddOfferRequest = gson.toJson(addOfferDtoRequest);
        String jsonAddOfferResponse = offerService.addOffer(jsonAddOfferRequest);
        assertEquals(gson.toJson(""), jsonAddOfferResponse);
    }

    @Ignore
    @Test
    public void testAddOfferIfVoterTokenIsOffline() {
        RegisterVoterDtoRequest registerRequest1 = new RegisterVoterDtoRequest(0, "sergei","sergeev","12345678");
        assertEquals("sergei", registerRequest1.getFirstName());
        assertEquals("sergeev", registerRequest1.getLogin());
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

        RegisterVoterDtoRequest registerRequest2 = new RegisterVoterDtoRequest(1, "alex","alexandrov","12345678");
        assertEquals("alex", registerRequest2.getFirstName());
        assertEquals("alexandrov", registerRequest2.getLogin());
        assertEquals("12345678", registerRequest2.getPassword());

        String jsonRegisterRequest2 = gson.toJson(registerRequest2);
        String jsonRegisterResponse2  = voterService.registerVoter(jsonRegisterRequest2);

        RegisterVoterDtoResponse registerVoterDtoResponse2 = gson.fromJson(jsonRegisterResponse2, RegisterVoterDtoResponse.class);
        String resultRegisterJson2 = gson.toJson(registerVoterDtoResponse2);
        assertEquals(resultRegisterJson2, jsonRegisterResponse2);

        LogoutDtoRequest logoutDtoRequest = new LogoutDtoRequest(registerRequest2.getLogin(), registerVoterDtoResponse2.getToken());
        assertEquals("alexandrov", logoutDtoRequest.getLogin());
        assertEquals(registerVoterDtoResponse2.getToken(), logoutDtoRequest.getToken());

        String jsonLogoutRequest = gson.toJson(logoutDtoRequest);
        String jsonLogoutResponse = voterService.logout(jsonLogoutRequest);

        LogoutDtoResponse resultLogout = gson.fromJson(jsonLogoutResponse, LogoutDtoResponse.class);
        String offlineTokenJson = gson.toJson(resultLogout);
        assertEquals(offlineTokenJson, jsonLogoutResponse);

        String voterToken = registerVoterDtoResponse2.getToken();
        String candidateToken = addCandidateRequest.getToken();

        AddOfferDtoRequest addOfferDtoRequest = new AddOfferDtoRequest(voterToken,1, candidateToken,0, "build a bridge across the river");
        assertEquals(candidateToken, addOfferDtoRequest.getCandidateToken());

        Map<String, Voter> tokensAndVoters = dataBase.getTokensAndVoters();
        assertTrue(tokensAndVoters.containsKey(voterToken));

        addOfferDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(1, addOfferDtoRequest.getTokensAndVoters().size());

        String jsonAddOfferRequest = gson.toJson(addOfferDtoRequest);
        String jsonAddOfferResponse = offerService.addOffer(jsonAddOfferRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonAddOfferResponse);
    }

    @Ignore
    @Test
    public void testAddOfferIfCandidateTokenIsOffline() {
        RegisterVoterDtoRequest registerRequest1 = new RegisterVoterDtoRequest(0, "boris","borisov","12345678");
        assertEquals("boris", registerRequest1.getFirstName());
        assertEquals("borisov", registerRequest1.getLogin());
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

        RemoveCandidateDtoRequest removeCandidateDtoRequest = new RemoveCandidateDtoRequest(0, addCandidateRequest.getToken());
        assertEquals(addCandidateRequest.getLogin(), removeCandidateDtoRequest.getCandidateId());
        assertEquals(addCandidateRequest.getToken(), removeCandidateDtoRequest.getCandidateToken());

        String jsonRemoveCandidateRequest = gson.toJson(removeCandidateDtoRequest);
        String jsonRemoveCandidateResponse = candidateService.removeCandidate(jsonRemoveCandidateRequest);
        assertEquals(gson.toJson(""), jsonRemoveCandidateResponse);

        LogoutDtoRequest logoutDtoRequest = new LogoutDtoRequest(addCandidateRequest.getLogin(), addCandidateRequest.getToken());
        assertEquals("borisov", logoutDtoRequest.getLogin());
        assertEquals(registerVoterDtoResponse.getToken(), addCandidateRequest.getToken());

        String jsonLogoutRequest = gson.toJson(logoutDtoRequest);
        String jsonLogoutResponse = voterService.logout(jsonLogoutRequest);

        LogoutDtoResponse resultLogout = gson.fromJson(jsonLogoutResponse, LogoutDtoResponse.class);
        String offlineTokenJson = gson.toJson(resultLogout);
        assertEquals(offlineTokenJson, jsonLogoutResponse);

        RegisterVoterDtoRequest registerRequest2 = new RegisterVoterDtoRequest(1, "alex","alexandrov","12345678");
        assertEquals("alex", registerRequest2.getFirstName());
        assertEquals("alexandrov", registerRequest2.getLogin());
        assertEquals("12345678", registerRequest2.getPassword());

        String jsonRegisterRequest2 = gson.toJson(registerRequest2);
        String jsonRegisterResponse2  = voterService.registerVoter(jsonRegisterRequest2);

        RegisterVoterDtoResponse registerVoterDtoResponse2 = gson.fromJson(jsonRegisterResponse2, RegisterVoterDtoResponse.class);
        String resultRegisterJson2 = gson.toJson(registerVoterDtoResponse2);
        assertEquals(resultRegisterJson2, jsonRegisterResponse2);

        String voterToken = registerVoterDtoResponse2.getToken();
        String candidateToken = addCandidateRequest.getToken();

        AddOfferDtoRequest addOfferDtoRequest = new AddOfferDtoRequest(voterToken,1, candidateToken,0, "build a bridge across the river");
        assertEquals(candidateToken, addOfferDtoRequest.getCandidateToken());

        Map<String, Voter> tokensAndVoters = dataBase.getTokensAndVoters();
        assertTrue(tokensAndVoters.containsKey(candidateToken));

        addOfferDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(1, addOfferDtoRequest.getTokensAndVoters().size());

        String jsonAddOfferRequest = gson.toJson(addOfferDtoRequest);
        String jsonAddOfferResponse = offerService.addOffer(jsonAddOfferRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonAddOfferResponse);
    }

    @Ignore
    @Test
    public void testAddOfferWrongCandidateToken1() {
        RegisterVoterDtoRequest registerVoterRequest = new RegisterVoterDtoRequest(0, "ivan","ivanov","12345678");
        assertEquals("ivan", registerVoterRequest.getFirstName());
        assertEquals("ivanov", registerVoterRequest.getLogin());
        assertEquals("12345678", registerVoterRequest.getPassword());

        String jsonRegisterRequest = gson.toJson(registerVoterRequest);
        String jsonRegisterResponse  = voterService.registerVoter(jsonRegisterRequest);
        RegisterVoterDtoResponse registerVoterDtoResponse = gson.fromJson(jsonRegisterResponse, RegisterVoterDtoResponse.class);
        String resultRegisterJson = gson.toJson(registerVoterDtoResponse);
        assertEquals(resultRegisterJson, jsonRegisterResponse);

        String voterToken = registerVoterDtoResponse.getToken();
        AddOfferDtoRequest addOfferDtoRequest = new AddOfferDtoRequest(voterToken,0, "",0, "build a bridge across the river");
        assertEquals("", addOfferDtoRequest.getCandidateToken());

        Map<String,Voter> tokensAndVoters = dataBase.getTokensAndVoters();
        addOfferDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(0, addOfferDtoRequest.getTokensAndVoters().size());

        String jsonAddOfferRequest = gson.toJson(addOfferDtoRequest);
        String jsonAddOfferResponse = offerService.addOffer(jsonAddOfferRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonAddOfferResponse);
    }

    @Ignore
    @Test
    public void testAddOfferWrongCandidateToken2() {
        RegisterVoterDtoRequest registerVoterRequest = new RegisterVoterDtoRequest(0, "stepan","stepanov","12345678");
        assertEquals("stepan", registerVoterRequest.getFirstName());
        assertEquals("stepanov", registerVoterRequest.getLogin());
        assertEquals("12345678", registerVoterRequest.getPassword());
        String jsonRegisterRequest = gson.toJson(registerVoterRequest);
        String jsonRegisterResponse  = voterService.registerVoter(jsonRegisterRequest);

        RegisterVoterDtoResponse registerVoterDtoResponse = gson.fromJson(jsonRegisterResponse, RegisterVoterDtoResponse.class);
        String resultRegisterJson = gson.toJson(registerVoterDtoResponse);
        assertEquals(resultRegisterJson, jsonRegisterResponse);

        String voterToken = registerVoterDtoResponse.getToken();
        AddOfferDtoRequest addOfferDtoRequest = new AddOfferDtoRequest(voterToken, 0,null,0, "build a bridge across the river");
        assertEquals(null, addOfferDtoRequest.getCandidateToken());

        Map<String,Voter> tokensAndVoters = dataBase.getTokensAndVoters();
        addOfferDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(0, addOfferDtoRequest.getTokensAndVoters().size());

        String jsonAddOfferRequest = gson.toJson(addOfferDtoRequest);
        String jsonAddOfferResponse = offerService.addOffer(jsonAddOfferRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonAddOfferResponse);
    }

    @Ignore
    @Test
    public void testAddOfferWrongOfferDescription1() {
        RegisterVoterDtoRequest registerRequest1 = new RegisterVoterDtoRequest(0, "sergei","sergeev","12345678");
        assertEquals("sergei", registerRequest1.getFirstName());
        assertEquals("sergeev", registerRequest1.getLogin());
        assertEquals("12345678", registerRequest1.getPassword());

        String jsonRegisterRequest = gson.toJson(registerRequest1);
        String jsonRegisterResponse  = voterService.registerVoter(jsonRegisterRequest);
        RegisterVoterDtoResponse registerVoterDtoResponse = gson.fromJson(jsonRegisterResponse, RegisterVoterDtoResponse.class);
        String resultRegisterJson = gson.toJson(registerVoterDtoResponse);
        assertEquals(resultRegisterJson, jsonRegisterResponse);

        AddCandidateDtoRequest addCandidateRequest = new AddCandidateDtoRequest(0, registerRequest1.getFirstName(), registerRequest1.getLogin(),
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

        RegisterVoterDtoRequest registerRequest2 = new RegisterVoterDtoRequest(1, "alex","alexandrov","12345678");
        assertEquals("alex", registerRequest2.getFirstName());
        assertEquals("alexandrov", registerRequest2.getLogin());
        assertEquals("12345678", registerRequest2.getPassword());

        String jsonRegisterRequest2 = gson.toJson(registerRequest2);
        String jsonRegisterResponse2  = voterService.registerVoter(jsonRegisterRequest2);

        RegisterVoterDtoResponse registerVoterDtoResponse2 = gson.fromJson(jsonRegisterResponse2, RegisterVoterDtoResponse.class);
        String resultRegisterJson2 = gson.toJson(registerVoterDtoResponse2);
        assertEquals(resultRegisterJson2, jsonRegisterResponse2);

        String voterToken = registerVoterDtoResponse2.getToken();
        String candidateToken = addCandidateDtoResponse1.getToken();

        AddOfferDtoRequest addOfferDtoRequest = new AddOfferDtoRequest(voterToken, 1,candidateToken,0, "");
        assertEquals("", addOfferDtoRequest.getOfferDescription());

        Map<String,Voter> tokensAndVoters = dataBase.getTokensAndVoters();
        addOfferDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(0, addOfferDtoRequest.getTokensAndVoters().size());

        String jsonAddOfferRequest = gson.toJson(addOfferDtoRequest);
        String jsonAddOfferResponse = offerService.addOffer(jsonAddOfferRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonAddOfferResponse);
    }

    @Test
    public void testAddOfferWrongOfferDescription2() {
        RegisterVoterDtoRequest registerRequest1 = new RegisterVoterDtoRequest(0, "sergei","sergeev","12345678");
        assertEquals("sergei", registerRequest1.getFirstName());
        assertEquals("sergeev", registerRequest1.getLogin());
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

        RegisterVoterDtoRequest registerRequest2 = new RegisterVoterDtoRequest(1, "alex","alexandrov","12345678");
        assertEquals("alex", registerRequest2.getFirstName());
        assertEquals("alexandrov", registerRequest2.getLogin());
        assertEquals("12345678", registerRequest2.getPassword());

        String jsonRegisterRequest2 = gson.toJson(registerRequest2);
        String jsonRegisterResponse2  = voterService.registerVoter(jsonRegisterRequest2);

        RegisterVoterDtoResponse registerVoterDtoResponse2 = gson.fromJson(jsonRegisterResponse2, RegisterVoterDtoResponse.class);
        String resultRegisterJson2 = gson.toJson(registerVoterDtoResponse2);
        assertEquals(resultRegisterJson2, jsonRegisterResponse2);

        String voterToken = registerVoterDtoResponse2.getToken();
        String candidateToken = addCandidateDtoResponse1.getToken();

        AddOfferDtoRequest addOfferDtoRequest = new AddOfferDtoRequest(voterToken, 1,candidateToken,0, null);
        assertEquals(null, addOfferDtoRequest.getOfferDescription());

        Map<String,Voter> tokensAndVoters = dataBase.getTokensAndVoters();
        addOfferDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(2, addOfferDtoRequest.getTokensAndVoters().size());

        String jsonAddOfferRequest = gson.toJson(addOfferDtoRequest);
        String jsonAddOfferResponse = offerService.addOffer(jsonAddOfferRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonAddOfferResponse);
    }

    @Ignore
    @Test
    public void testVoteForCandidateIfVoterVote() throws VoterException {
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

        String jsonAddCandidateRequest = gson.toJson(addCandidateRequest);
        String jsonAddCandidateResponse  = candidateService.addCandidate(jsonAddCandidateRequest);
        assertEquals(gson.toJson(""), jsonAddCandidateResponse);

        RegisterVoterDtoRequest registerRequest2 = new RegisterVoterDtoRequest(1, "boris","borisov","12345678");
        assertEquals("boris", registerRequest2.getFirstName());
        assertEquals("borisov", registerRequest2.getLogin());
        assertEquals("12345678", registerRequest2.getPassword());
        String jsonRegisterRequest2 = gson.toJson(registerRequest2);
        String jsonRegisterResponse2  = voterService.registerVoter(jsonRegisterRequest2);

        RegisterVoterDtoResponse registerVoterDtoResponse2 = gson.fromJson(jsonRegisterResponse2, RegisterVoterDtoResponse.class);
        String resultRegisterJson2 = gson.toJson(registerVoterDtoResponse2);
        assertEquals(resultRegisterJson2, jsonRegisterResponse2);

        String voterToken = registerVoterDtoResponse2.getToken();
        String candidateToken = addCandidateRequest.getToken();

        AddOfferDtoRequest addOfferDtoRequest = new AddOfferDtoRequest(voterToken,1, candidateToken,0, "build a bridge across the river");
        assertEquals(voterToken, addOfferDtoRequest.getVoterToken());
        assertEquals(candidateToken, addOfferDtoRequest.getCandidateToken());
        assertEquals("build a bridge across the river", addOfferDtoRequest.getOfferDescription());

        Map<String,Voter> tokensAndVoters = dataBase.getTokensAndVoters();
        addOfferDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(0, addOfferDtoRequest.getTokensAndVoters().size());

        String jsonAddOfferRequest = gson.toJson(addOfferDtoRequest);
        String jsonAddOfferResponse = offerService.addOffer(jsonAddOfferRequest);
        assertEquals(gson.toJson(""), jsonAddOfferResponse);

        VoteForCandidateDtoRequest voteForCandidateDtoRequest = new VoteForCandidateDtoRequest(1,0,voterToken, candidateToken);
        assertEquals(voterToken, voteForCandidateDtoRequest.getVotingToken());
        assertEquals(candidateToken, voteForCandidateDtoRequest.getCandidateToken());
        voteForCandidateDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(0, voteForCandidateDtoRequest.getTokensAndVoters().size());

        String jsonVoterForCandidateRequest = gson.toJson(voteForCandidateDtoRequest);
        String jsonVoterForCandidateResponse = electionService.voteForCandidate(jsonVoterForCandidateRequest);
        assertEquals(gson.toJson(""), jsonVoterForCandidateResponse);

        assertEquals(0, dataBase.getVotedCandidates().size());
        assertEquals(1, dataBase.getVotedVoters().size());
        assertTrue(dataBase.getVotedVoters().contains(voterToken));

        Candidate candidate = dataBase.getCandidateByToken(candidateToken);
        assertEquals(1, candidate.getAmountVotes());
        assertEquals(1, dataBase.getRankedCandidates().size());
        assertTrue(dataBase.getRankedCandidates().contains(candidate));
    }

    @Test
    public void testVoteForCandidateIfVoterVoteTwice() throws VoterException {
        dataBase.getRankedCandidates().clear();

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

        RegisterVoterDtoRequest registerRequest2 = new RegisterVoterDtoRequest(1, "boris","borisov","12345678");
        assertEquals("boris", registerRequest2.getFirstName());
        assertEquals("borisov", registerRequest2.getLogin());
        assertEquals("12345678", registerRequest2.getPassword());
        String jsonRegisterRequest2 = gson.toJson(registerRequest2);
        String jsonRegisterResponse2  = voterService.registerVoter(jsonRegisterRequest2);

        RegisterVoterDtoResponse registerVoterDtoResponse2 = gson.fromJson(jsonRegisterResponse2, RegisterVoterDtoResponse.class);
        String resultRegisterJson2 = gson.toJson(registerVoterDtoResponse2);
        assertEquals(resultRegisterJson2, jsonRegisterResponse2);

        String voterToken = registerVoterDtoResponse2.getToken();
        String candidateToken = addCandidateDtoResponse1.getToken();

        AddOfferDtoRequest addOfferDtoRequest = new AddOfferDtoRequest(voterToken, 1,candidateToken,0, "build a bridge across the river");
        assertEquals(voterToken, addOfferDtoRequest.getVoterToken());
        assertEquals(candidateToken, addOfferDtoRequest.getCandidateToken());
        assertEquals("build a bridge across the river", addOfferDtoRequest.getOfferDescription());

        Map<String,Voter> tokensAndVoters = dataBase.getTokensAndVoters();
        addOfferDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(2, addOfferDtoRequest.getTokensAndVoters().size());

        String jsonAddOfferRequest = gson.toJson(addOfferDtoRequest);
        String jsonAddOfferResponse = offerService.addOffer(jsonAddOfferRequest);
        assertEquals(gson.toJson(""), jsonAddOfferResponse);

        VoteForCandidateDtoRequest voteForCandidateDtoRequest1 = new VoteForCandidateDtoRequest(1,0,voterToken, candidateToken);
        assertEquals(voterToken, voteForCandidateDtoRequest1.getVotingToken());
        assertEquals(candidateToken, voteForCandidateDtoRequest1.getCandidateToken());
        voteForCandidateDtoRequest1.setTokensAndVoters(tokensAndVoters);
        assertEquals(2, voteForCandidateDtoRequest1.getTokensAndVoters().size());

        String jsonVoterForCandidateRequest1 = gson.toJson(voteForCandidateDtoRequest1);
        String jsonVoterForCandidateResponse1 = electionService.voteForCandidate(jsonVoterForCandidateRequest1);
        assertEquals(gson.toJson(""), jsonVoterForCandidateResponse1);

        assertEquals(0, dataBase.getVotedCandidates().size());
        assertEquals(1, dataBase.getVotedVoters().size());

        assertEquals(1, dataBase.getRankedCandidates().size());
        Candidate candidate = dataBase.getCandidateById(addCandidateRequest.getId());
        assertEquals(1, candidate.getAmountVotes());
        assertEquals(1, dataBase.getRankedCandidates().size());
        assertTrue(dataBase.getRankedCandidates().contains(candidate));

        VoteForCandidateDtoRequest voteForCandidateDtoRequest2 = new VoteForCandidateDtoRequest(1,0,voterToken, candidateToken);
        assertEquals(voterToken, voteForCandidateDtoRequest2.getVotingToken());
        assertEquals(candidateToken, voteForCandidateDtoRequest2.getCandidateToken());
        voteForCandidateDtoRequest2.setTokensAndVoters(tokensAndVoters);
        assertEquals(2, voteForCandidateDtoRequest2.getTokensAndVoters().size());

        String jsonVoterForCandidateRequest2 = gson.toJson(voteForCandidateDtoRequest2);
        String jsonVoterForCandidateResponse2 = electionService.voteForCandidate(jsonVoterForCandidateRequest2);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonVoterForCandidateResponse2);

        assertEquals(0, dataBase.getVotedCandidates().size());
        assertEquals(1, dataBase.getVotedVoters().size());
        assertEquals(1, dataBase.getRankedCandidates().size());
        assertEquals(1, candidate.getAmountVotes());
    }

    @Ignore
    @Test
    public void testVoteForCandidateIfCandidateVote() throws VoterException {
        RegisterVoterDtoRequest registerRequest1 = new RegisterVoterDtoRequest(0, "petr","petrov","12345678");
        assertEquals("petr", registerRequest1.getFirstName());
        assertEquals("petrov", registerRequest1.getLogin());
        assertEquals("12345678", registerRequest1.getPassword());
        String jsonRegisterRequest1 = gson.toJson(registerRequest1);
        String jsonRegisterResponse1  = voterService.registerVoter(jsonRegisterRequest1);

        RegisterVoterDtoResponse registerVoterDtoResponse1 = gson.fromJson(jsonRegisterResponse1, RegisterVoterDtoResponse.class);
        String resultRegisterJson1 = gson.toJson(registerVoterDtoResponse1);
        assertEquals(resultRegisterJson1, jsonRegisterResponse1);

        AddCandidateDtoRequest addCandidateRequest1 = new AddCandidateDtoRequest(registerRequest1.getId(), registerRequest1.getFirstName(), registerRequest1.getLogin(),
                                                                                registerRequest1.getPassword(), registerVoterDtoResponse1.getToken());
        assertEquals(registerRequest1.getFirstName(), addCandidateRequest1.getFirstName());
        assertEquals(registerRequest1.getLogin(), addCandidateRequest1.getLogin());
        assertEquals(registerRequest1.getPassword(), addCandidateRequest1.getPassword());
        assertEquals(registerVoterDtoResponse1.getToken(), addCandidateRequest1.getToken());

        String jsonAddCandidateRequest = gson.toJson(addCandidateRequest1);
        String jsonAddCandidateResponse  = candidateService.addCandidate(jsonAddCandidateRequest);
        assertEquals(gson.toJson(""), jsonAddCandidateResponse);

        RegisterVoterDtoRequest registerRequest2 = new RegisterVoterDtoRequest(1, "boris","borisov","12345678");
        assertEquals("boris", registerRequest2.getFirstName());
        assertEquals("borisov", registerRequest2.getLogin());
        assertEquals("12345678", registerRequest2.getPassword());
        String jsonRegisterRequest2 = gson.toJson(registerRequest2);
        String jsonRegisterResponse2  = voterService.registerVoter(jsonRegisterRequest2);

        RegisterVoterDtoResponse registerVoterDtoResponse2 = gson.fromJson(jsonRegisterResponse2, RegisterVoterDtoResponse.class);
        String resultRegisterJson2 = gson.toJson(registerVoterDtoResponse2);
        assertEquals(resultRegisterJson2, jsonRegisterResponse2);

        AddCandidateDtoRequest addCandidateRequest2 = new AddCandidateDtoRequest(registerRequest2.getId(), registerRequest2.getFirstName(), registerRequest2.getLogin(),
                                                                                registerRequest2.getPassword(), registerVoterDtoResponse2.getToken());
        assertEquals(registerRequest2.getFirstName(), addCandidateRequest2.getFirstName());
        assertEquals(registerRequest2.getLogin(), addCandidateRequest2.getLogin());
        assertEquals(registerRequest2.getPassword(), addCandidateRequest2.getPassword());
        assertEquals(registerVoterDtoResponse2.getToken(), addCandidateRequest2.getToken());

        String jsonAddCandidateRequest2 = gson.toJson(addCandidateRequest2);
        String jsonAddCandidateResponse2  = candidateService.addCandidate(jsonAddCandidateRequest2);
        assertEquals(gson.toJson(""), jsonAddCandidateResponse2);

        String votingCandidateToken = addCandidateRequest1.getToken();
        Candidate votingCandidate = dataBase.getCandidateByToken(votingCandidateToken);
        String candidateToken = addCandidateRequest2.getToken();
        Candidate candidate = dataBase.getCandidateByToken(candidateToken);
        assertNotEquals(votingCandidateToken, candidateToken);

        AddOfferToElectionProgramDtoRequest addOfferToElectionProgramDtoRequest1 = new AddOfferToElectionProgramDtoRequest(votingCandidateToken, 0, "repair the road");
        assertEquals(votingCandidateToken, addOfferToElectionProgramDtoRequest1.getCandidateToken());
        assertEquals("repair the road", addOfferToElectionProgramDtoRequest1.getOfferDescription());

        Map<String,Voter> tokensAndVoters = dataBase.getTokensAndVoters();
        addOfferToElectionProgramDtoRequest1.setTokensAndVoters(tokensAndVoters);
        assertEquals(0, addOfferToElectionProgramDtoRequest1.getTokensAndVoters().size());

        String jsonAddOfferRequest = gson.toJson(addOfferToElectionProgramDtoRequest1);
        String jsonAddOfferResponse = offerService.addOfferToElectionProgram(jsonAddOfferRequest);
        assertEquals(gson.toJson(""), jsonAddOfferResponse);
        assertEquals(1, votingCandidate.getElectionProgram().size());

        AddOfferToElectionProgramDtoRequest addOfferToElectionProgramDtoRequest2 = new AddOfferToElectionProgramDtoRequest(candidateToken, 1, "build a bridge across the river");
        assertEquals(candidateToken, addOfferToElectionProgramDtoRequest2.getCandidateToken());
        assertEquals("build a bridge across the river", addOfferToElectionProgramDtoRequest2.getOfferDescription());

        addOfferToElectionProgramDtoRequest2.setTokensAndVoters(tokensAndVoters);
        assertEquals(0, addOfferToElectionProgramDtoRequest2.getTokensAndVoters().size());

        String jsonAddOfferRequest2 = gson.toJson(addOfferToElectionProgramDtoRequest2);
        String jsonAddOfferResponse2 = offerService.addOfferToElectionProgram(jsonAddOfferRequest2);
        assertEquals(gson.toJson(""), jsonAddOfferResponse2);
        assertEquals(1, candidate.getElectionProgram().size());

        VoteForCandidateDtoRequest voteForCandidateDtoRequest = new VoteForCandidateDtoRequest(0,1,votingCandidateToken, candidateToken);
        assertEquals(votingCandidateToken, voteForCandidateDtoRequest.getVotingToken());
        assertEquals(candidateToken, voteForCandidateDtoRequest.getCandidateToken());
        voteForCandidateDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(0, voteForCandidateDtoRequest.getTokensAndVoters().size());

        String jsonVoterForCandidateRequest = gson.toJson(voteForCandidateDtoRequest);
        String jsonVoterForCandidateResponse = electionService.voteForCandidate(jsonVoterForCandidateRequest);
        assertEquals(gson.toJson(""), jsonVoterForCandidateResponse);

        assertEquals(0, dataBase.getVotedVoters().size());
        assertEquals(1, dataBase.getVotedCandidates().size());
        assertTrue(dataBase.getVotedCandidates().contains(votingCandidateToken));

        assertEquals(1, candidate.getAmountVotes());
        assertEquals(1, dataBase.getRankedCandidates().size());
        assertTrue(dataBase.getRankedCandidates().contains(candidate));
    }

    @Ignore
    @Test
    public void testVoteForCandidateIfCandidateVoteTwice() throws VoterException {
        dataBase.getRankedCandidates().clear();

        RegisterVoterDtoRequest registerRequest1 = new RegisterVoterDtoRequest(0, "petr","petrov","12345678");
        assertEquals("petr", registerRequest1.getFirstName());
        assertEquals("petrov", registerRequest1.getLogin());
        assertEquals("12345678", registerRequest1.getPassword());
        String jsonRegisterRequest1 = gson.toJson(registerRequest1);
        String jsonRegisterResponse1  = voterService.registerVoter(jsonRegisterRequest1);

        RegisterVoterDtoResponse registerVoterDtoResponse1 = gson.fromJson(jsonRegisterResponse1, RegisterVoterDtoResponse.class);
        String resultRegisterJson1 = gson.toJson(registerVoterDtoResponse1);
        assertEquals(resultRegisterJson1, jsonRegisterResponse1);

        AddCandidateDtoRequest addCandidateRequest1 = new AddCandidateDtoRequest(registerRequest1.getId(), registerRequest1.getFirstName(), registerRequest1.getLogin(),
                                                                                 registerRequest1.getPassword(), registerVoterDtoResponse1.getToken());
        assertEquals(registerRequest1.getFirstName(), addCandidateRequest1.getFirstName());
        assertEquals(registerRequest1.getLogin(), addCandidateRequest1.getLogin());
        assertEquals(registerRequest1.getPassword(), addCandidateRequest1.getPassword());
        assertEquals(registerVoterDtoResponse1.getToken(), addCandidateRequest1.getToken());

        String jsonAddCandidateRequest = gson.toJson(addCandidateRequest1);
        String jsonAddCandidateResponse  = candidateService.addCandidate(jsonAddCandidateRequest);
        assertEquals(gson.toJson(""), jsonAddCandidateResponse);

        RegisterVoterDtoRequest registerRequest2 = new RegisterVoterDtoRequest(1, "boris","borisov","12345678");
        assertEquals("boris", registerRequest2.getFirstName());
        assertEquals("borisov", registerRequest2.getLogin());
        assertEquals("12345678", registerRequest2.getPassword());
        String jsonRegisterRequest2 = gson.toJson(registerRequest2);
        String jsonRegisterResponse2  = voterService.registerVoter(jsonRegisterRequest2);

        RegisterVoterDtoResponse registerVoterDtoResponse2 = gson.fromJson(jsonRegisterResponse2, RegisterVoterDtoResponse.class);
        String resultRegisterJson2 = gson.toJson(registerVoterDtoResponse2);
        assertEquals(resultRegisterJson2, jsonRegisterResponse2);

        AddCandidateDtoRequest addCandidateRequest2 = new AddCandidateDtoRequest(registerRequest2.getId(), registerRequest2.getFirstName(), registerRequest2.getLogin(),
                                                                                 registerRequest2.getPassword(), registerVoterDtoResponse2.getToken());
        assertEquals(registerRequest2.getFirstName(), addCandidateRequest2.getFirstName());
        assertEquals(registerRequest2.getLogin(), addCandidateRequest2.getLogin());
        assertEquals(registerRequest2.getPassword(), addCandidateRequest2.getPassword());
        assertEquals(registerVoterDtoResponse2.getToken(), addCandidateRequest2.getToken());

        String jsonAddCandidateRequest2 = gson.toJson(addCandidateRequest2);
        String jsonAddCandidateResponse2  = candidateService.addCandidate(jsonAddCandidateRequest2);
        assertEquals(gson.toJson(""), jsonAddCandidateResponse2);

        String votingCandidateToken = addCandidateRequest1.getToken();
        Candidate votingCandidate = dataBase.getCandidateByToken(votingCandidateToken);
        String candidateToken = addCandidateRequest2.getToken();
        Candidate candidate = dataBase.getCandidateByToken(candidateToken);
        assertNotEquals(votingCandidateToken, candidateToken);

        AddOfferToElectionProgramDtoRequest addOfferToElectionProgramDtoRequest1 = new AddOfferToElectionProgramDtoRequest(votingCandidateToken, 0, "repair the road");
        assertEquals(votingCandidateToken, addOfferToElectionProgramDtoRequest1.getCandidateToken());
        assertEquals("repair the road", addOfferToElectionProgramDtoRequest1.getOfferDescription());

        Map<String,Voter> tokensAndVoters = dataBase.getTokensAndVoters();
        addOfferToElectionProgramDtoRequest1.setTokensAndVoters(tokensAndVoters);
        assertEquals(0, addOfferToElectionProgramDtoRequest1.getTokensAndVoters().size());

        String jsonAddOfferRequest = gson.toJson(addOfferToElectionProgramDtoRequest1);
        String jsonAddOfferResponse = offerService.addOfferToElectionProgram(jsonAddOfferRequest);
        assertEquals(gson.toJson(""), jsonAddOfferResponse);
        assertEquals(1, votingCandidate.getElectionProgram().size());

        AddOfferToElectionProgramDtoRequest addOfferToElectionProgramDtoRequest2 = new AddOfferToElectionProgramDtoRequest(candidateToken, 1,"build a bridge across the river");
        assertEquals(candidateToken, addOfferToElectionProgramDtoRequest2.getCandidateToken());
        assertEquals("build a bridge across the river", addOfferToElectionProgramDtoRequest2.getOfferDescription());

        addOfferToElectionProgramDtoRequest2.setTokensAndVoters(tokensAndVoters);
        assertEquals(0, addOfferToElectionProgramDtoRequest2.getTokensAndVoters().size());

        String jsonAddOfferRequest2 = gson.toJson(addOfferToElectionProgramDtoRequest2);
        String jsonAddOfferResponse2 = offerService.addOfferToElectionProgram(jsonAddOfferRequest2);
        assertEquals(gson.toJson(""), jsonAddOfferResponse2);
        assertEquals(1, candidate.getElectionProgram().size());

        VoteForCandidateDtoRequest voteForCandidateDtoRequest1 = new VoteForCandidateDtoRequest(0,1,votingCandidateToken, candidateToken);
        assertEquals(votingCandidateToken, voteForCandidateDtoRequest1.getVotingToken());
        assertEquals(candidateToken, voteForCandidateDtoRequest1.getCandidateToken());
        voteForCandidateDtoRequest1.setTokensAndVoters(tokensAndVoters);
        assertEquals(0, voteForCandidateDtoRequest1.getTokensAndVoters().size());

        String jsonVoterForCandidateRequest1 = gson.toJson(voteForCandidateDtoRequest1);
        String jsonVoterForCandidateResponse1 = electionService.voteForCandidate(jsonVoterForCandidateRequest1);
        assertEquals(gson.toJson(""), jsonVoterForCandidateResponse1);

        assertEquals(0, dataBase.getVotedVoters().size());
        assertEquals(1, dataBase.getVotedCandidates().size());
        assertTrue(dataBase.getVotedCandidates().contains(votingCandidateToken));

        assertEquals(1, candidate.getAmountVotes());
        assertEquals(1, dataBase.getRankedCandidates().size());
        assertTrue(dataBase.getRankedCandidates().contains(candidate));

        VoteForCandidateDtoRequest voteForCandidateDtoRequest2 = new VoteForCandidateDtoRequest(0,0,votingCandidateToken, candidateToken);
        assertEquals(votingCandidateToken, voteForCandidateDtoRequest2.getVotingToken());
        assertEquals(candidateToken, voteForCandidateDtoRequest2.getCandidateToken());
        voteForCandidateDtoRequest2.setTokensAndVoters(tokensAndVoters);
        assertEquals(0, voteForCandidateDtoRequest1.getTokensAndVoters().size());

        String jsonVoterForCandidateRequest2 = gson.toJson(voteForCandidateDtoRequest2);
        String jsonVoterForCandidateResponse2 = electionService.voteForCandidate(jsonVoterForCandidateRequest2);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonVoterForCandidateResponse2);

        assertEquals(0, dataBase.getVotedVoters().size());
        assertEquals(1, dataBase.getVotedCandidates().size());
        assertTrue(dataBase.getVotedCandidates().contains(votingCandidateToken));

        assertEquals(1, candidate.getAmountVotes());
        assertEquals(1, dataBase.getRankedCandidates().size());
    }

    @Ignore
    @Test
    public void testVoteForCandidateIfCandidateWithoutElectionProgram() throws VoterException {
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

        String jsonAddCandidateRequest = gson.toJson(addCandidateRequest);
        String jsonAddCandidateResponse  = candidateService.addCandidate(jsonAddCandidateRequest);
        assertEquals(gson.toJson(""), jsonAddCandidateResponse);

        RegisterVoterDtoRequest registerRequest2 = new RegisterVoterDtoRequest(1, "boris","borisov","12345678");
        assertEquals("boris", registerRequest2.getFirstName());
        assertEquals("borisov", registerRequest2.getLogin());
        assertEquals("12345678", registerRequest2.getPassword());
        String jsonRegisterRequest2 = gson.toJson(registerRequest2);
        String jsonRegisterResponse2  = voterService.registerVoter(jsonRegisterRequest2);

        RegisterVoterDtoResponse registerVoterDtoResponse2 = gson.fromJson(jsonRegisterResponse2, RegisterVoterDtoResponse.class);
        String resultRegisterJson2 = gson.toJson(registerVoterDtoResponse2);
        assertEquals(resultRegisterJson2, jsonRegisterResponse2);

        String voterToken = registerVoterDtoResponse2.getToken();
        String candidateToken = addCandidateRequest.getToken();

        Candidate candidate = dataBase.getCandidateByToken(candidateToken);
        assertTrue(candidate.getElectionProgram().isEmpty());
        assertEquals(0, candidate.getElectionProgram().size());

        VoteForCandidateDtoRequest voteForCandidateDtoRequest = new VoteForCandidateDtoRequest(1,0,voterToken, candidateToken);
        assertEquals(voterToken, voteForCandidateDtoRequest.getVotingToken());
        assertEquals(candidateToken, voteForCandidateDtoRequest.getCandidateToken());

        Map<String,Voter> tokensAndVoters = dataBase.getTokensAndVoters();
        voteForCandidateDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(0, voteForCandidateDtoRequest.getTokensAndVoters().size());

        String jsonVoterForCandidateRequest = gson.toJson(voteForCandidateDtoRequest);
        String jsonVoterForCandidateResponse = electionService.voteForCandidate(jsonVoterForCandidateRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonVoterForCandidateResponse);
        assertEquals(0, candidate.getAmountVotes());
        assertTrue(dataBase.getOffersAndAverageRatings().isEmpty());
        assertTrue(dataBase.getVotedVoters().isEmpty());
        assertTrue(dataBase.getVotedCandidates().isEmpty());
    }

    @Ignore
    @Test
    public void testVoteForCandidateIfVotingCandidateWithoutElectionProgram() throws VoterException {
        RegisterVoterDtoRequest registerRequest1 = new RegisterVoterDtoRequest(0, "petr","petrov","12345678");
        assertEquals("petr", registerRequest1.getFirstName());
        assertEquals("petrov", registerRequest1.getLogin());
        assertEquals("12345678", registerRequest1.getPassword());
        String jsonRegisterRequest1 = gson.toJson(registerRequest1);
        String jsonRegisterResponse1  = voterService.registerVoter(jsonRegisterRequest1);

        RegisterVoterDtoResponse registerVoterDtoResponse1 = gson.fromJson(jsonRegisterResponse1, RegisterVoterDtoResponse.class);
        String resultRegisterJson1 = gson.toJson(registerVoterDtoResponse1);
        assertEquals(resultRegisterJson1, jsonRegisterResponse1);

        AddCandidateDtoRequest addCandidateRequest1 = new AddCandidateDtoRequest(registerRequest1.getId(), registerRequest1.getFirstName(), registerRequest1.getLogin(),
                                                                                 registerRequest1.getPassword(), registerVoterDtoResponse1.getToken());
        assertEquals(registerRequest1.getFirstName(), addCandidateRequest1.getFirstName());
        assertEquals(registerRequest1.getLogin(), addCandidateRequest1.getLogin());
        assertEquals(registerRequest1.getPassword(), addCandidateRequest1.getPassword());
        assertEquals(registerVoterDtoResponse1.getToken(), addCandidateRequest1.getToken());

        String jsonAddCandidateRequest1 = gson.toJson(addCandidateRequest1);
        String jsonAddCandidateResponse1  = candidateService.addCandidate(jsonAddCandidateRequest1);
        assertEquals(gson.toJson(""), jsonAddCandidateResponse1);

        RegisterVoterDtoRequest registerRequest2 = new RegisterVoterDtoRequest(1, "boris","borisov","12345678");
        assertEquals("boris", registerRequest2.getFirstName());
        assertEquals("borisov", registerRequest2.getLogin());
        assertEquals("12345678", registerRequest2.getPassword());
        String jsonRegisterRequest2 = gson.toJson(registerRequest2);
        String jsonRegisterResponse2  = voterService.registerVoter(jsonRegisterRequest2);

        RegisterVoterDtoResponse registerVoterDtoResponse2 = gson.fromJson(jsonRegisterResponse2, RegisterVoterDtoResponse.class);
        String resultRegisterJson2 = gson.toJson(registerVoterDtoResponse2);
        assertEquals(resultRegisterJson2, jsonRegisterResponse2);

        AddCandidateDtoRequest addCandidateRequest2 = new AddCandidateDtoRequest(registerRequest2.getId(), registerRequest2.getFirstName(), registerRequest2.getLogin(),
                                                                                 registerRequest2.getPassword(), registerVoterDtoResponse2.getToken());
        assertEquals(registerRequest2.getFirstName(), addCandidateRequest2.getFirstName());
        assertEquals(registerRequest2.getLogin(), addCandidateRequest2.getLogin());
        assertEquals(registerRequest2.getPassword(), addCandidateRequest2.getPassword());
        assertEquals(registerVoterDtoResponse2.getToken(), addCandidateRequest2.getToken());

        String jsonAddCandidateRequest2 = gson.toJson(addCandidateRequest2);
        String jsonAddCandidateResponse2  = candidateService.addCandidate(jsonAddCandidateRequest2);
        assertEquals(gson.toJson(""), jsonAddCandidateResponse2);

        String candidateToken = addCandidateRequest1.getToken();
        String votingCandidateToken = addCandidateRequest2.getToken();
        Candidate candidate = dataBase.getCandidateByToken(candidateToken);
        Candidate votingCandidate = dataBase.getCandidateByToken(votingCandidateToken);

        AddOfferToElectionProgramDtoRequest addOfferToElectionProgramDtoRequest = new AddOfferToElectionProgramDtoRequest(candidateToken, 0,"repair the road");
        assertEquals(candidateToken, addOfferToElectionProgramDtoRequest.getCandidateToken());
        assertEquals("repair the road", addOfferToElectionProgramDtoRequest.getOfferDescription());

        Map<String,Voter> tokensAndVoters = dataBase.getTokensAndVoters();
        addOfferToElectionProgramDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(0, addOfferToElectionProgramDtoRequest.getTokensAndVoters().size());

        String jsonAddOfferRequest = gson.toJson(addOfferToElectionProgramDtoRequest);
        String jsonAddOfferResponse = offerService.addOfferToElectionProgram(jsonAddOfferRequest);
        assertEquals(gson.toJson(""), jsonAddOfferResponse);

        assertEquals(1, candidate.getElectionProgram().size());
        assertEquals(0, votingCandidate.getElectionProgram().size());

        VoteForCandidateDtoRequest voteForCandidateDtoRequest = new VoteForCandidateDtoRequest(1,0,votingCandidateToken, candidateToken);
        assertEquals(votingCandidateToken, voteForCandidateDtoRequest.getVotingToken());
        assertEquals(candidateToken, voteForCandidateDtoRequest.getCandidateToken());

        voteForCandidateDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(0, voteForCandidateDtoRequest.getTokensAndVoters().size());

        String jsonVoterForCandidateRequest = gson.toJson(voteForCandidateDtoRequest);
        String jsonVoterForCandidateResponse = electionService.voteForCandidate(jsonVoterForCandidateRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonVoterForCandidateResponse);

        assertEquals(0, candidate.getAmountVotes());
        assertTrue(dataBase.getOffersAndAverageRatings().isEmpty());
        assertTrue(dataBase.getVotedVoters().isEmpty());
        assertTrue(dataBase.getVotedCandidates().isEmpty());
    }

    @Ignore
    @Test
    public void testVoteForCandidateIfCandidateVoteForYourself() throws VoterException {
        RegisterVoterDtoRequest registerRequest1 = new RegisterVoterDtoRequest(0, "petr","petrov","12345678");
        assertEquals("petr", registerRequest1.getFirstName());
        assertEquals("petrov", registerRequest1.getLogin());
        assertEquals("12345678", registerRequest1.getPassword());
        String jsonRegisterRequest1 = gson.toJson(registerRequest1);
        String jsonRegisterResponse1  = voterService.registerVoter(jsonRegisterRequest1);

        RegisterVoterDtoResponse registerVoterDtoResponse1 = gson.fromJson(jsonRegisterResponse1, RegisterVoterDtoResponse.class);
        String resultRegisterJson1 = gson.toJson(registerVoterDtoResponse1);
        assertEquals(resultRegisterJson1, jsonRegisterResponse1);

        AddCandidateDtoRequest addCandidateRequest1 = new AddCandidateDtoRequest(registerRequest1.getId(), registerRequest1.getFirstName(), registerRequest1.getLogin(),
                                                                                 registerRequest1.getPassword(), registerVoterDtoResponse1.getToken());
        assertEquals(registerRequest1.getFirstName(), addCandidateRequest1.getFirstName());
        assertEquals(registerRequest1.getLogin(), addCandidateRequest1.getLogin());
        assertEquals(registerRequest1.getPassword(), addCandidateRequest1.getPassword());
        assertEquals(registerVoterDtoResponse1.getToken(), addCandidateRequest1.getToken());

        String jsonAddCandidateRequest = gson.toJson(addCandidateRequest1);
        String jsonAddCandidateResponse  = candidateService.addCandidate(jsonAddCandidateRequest);
        assertEquals(gson.toJson(""), jsonAddCandidateResponse);

        String votingCandidateToken = addCandidateRequest1.getToken();
        Candidate votingCandidate = dataBase.getCandidateByToken(votingCandidateToken);

        AddOfferToElectionProgramDtoRequest addOfferToElectionProgramDtoRequest1 = new AddOfferToElectionProgramDtoRequest(votingCandidateToken, 0,"repair the road");
        assertEquals(votingCandidateToken, addOfferToElectionProgramDtoRequest1.getCandidateToken());
        assertEquals("repair the road", addOfferToElectionProgramDtoRequest1.getOfferDescription());

        Map<String,Voter> tokensAndVoters = dataBase.getTokensAndVoters();
        addOfferToElectionProgramDtoRequest1.setTokensAndVoters(tokensAndVoters);
        assertEquals(0, addOfferToElectionProgramDtoRequest1.getTokensAndVoters().size());

        String jsonAddOfferRequest = gson.toJson(addOfferToElectionProgramDtoRequest1);
        String jsonAddOfferResponse = offerService.addOfferToElectionProgram(jsonAddOfferRequest);
        assertEquals(gson.toJson(""), jsonAddOfferResponse);
        assertFalse(votingCandidate.getElectionProgram().isEmpty());
        assertEquals(1, votingCandidate.getElectionProgram().size());

        VoteForCandidateDtoRequest voteForCandidateDtoRequest = new VoteForCandidateDtoRequest(0,0,votingCandidateToken, votingCandidateToken);
        assertEquals(votingCandidateToken, voteForCandidateDtoRequest.getVotingToken());
        assertEquals(votingCandidateToken, voteForCandidateDtoRequest.getCandidateToken());
        voteForCandidateDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(0, voteForCandidateDtoRequest.getTokensAndVoters().size());

        String jsonVoterForCandidateRequest = gson.toJson(voteForCandidateDtoRequest);
        String jsonVoterForCandidateResponse = electionService.voteForCandidate(jsonVoterForCandidateRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonVoterForCandidateResponse);

        assertEquals(0, votingCandidate.getAmountVotes());
        assertTrue(dataBase.getOffersAndAverageRatings().isEmpty());
        assertTrue(dataBase.getVotedVoters().isEmpty());
        assertTrue(dataBase.getVotedCandidates().isEmpty());
    }

    @Ignore
    @Test
    public void testVoteAgainstAllIfVoterVote() {
        RegisterVoterDtoRequest registerRequest = new RegisterVoterDtoRequest(0, "boris","borisov","12345678");
        assertEquals("boris", registerRequest.getFirstName());
        assertEquals("borisov", registerRequest.getLogin());
        assertEquals("12345678", registerRequest.getPassword());
        String jsonRegisterRequest2 = gson.toJson(registerRequest);
        String jsonRegisterResponse2  = voterService.registerVoter(jsonRegisterRequest2);

        RegisterVoterDtoResponse registerVoterDtoResponse = gson.fromJson(jsonRegisterResponse2, RegisterVoterDtoResponse.class);
        String resultRegisterJson2 = gson.toJson(registerVoterDtoResponse);
        assertEquals(resultRegisterJson2, jsonRegisterResponse2);

        String voterToken = registerVoterDtoResponse.getToken();
        Voter voter = dataBase.getVoterById(registerRequest.getId());
        VoteAgainstAllDtoRequest voteAgainstAllDtoRequest = new VoteAgainstAllDtoRequest(voterToken, voter);
        assertEquals(voterToken, voteAgainstAllDtoRequest.getVotingToken());
        voteAgainstAllDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
        assertEquals(1, voteAgainstAllDtoRequest.getTokensAndVoters().size());

        String jsonVoteAgainstAllRequest = gson.toJson(voteAgainstAllDtoRequest);
        String jsonVoteAgainstAllResponse = electionService.voteAgainstAll(jsonVoteAgainstAllRequest);
        assertEquals(gson.toJson(""), jsonVoteAgainstAllResponse);

        assertEquals(0, dataBase.getVotedCandidates().size());
        assertEquals(1, dataBase.getVotedVoters().size());
        assertTrue(dataBase.getVotedVoters().contains(voterToken));

        assertEquals(1, dataBase.getVotedAgainstAll().size());
        assertTrue(dataBase.getVotedAgainstAll().contains(voterToken));
    }

    @Test
    public void testVoteAgainstAllIfVoterVoteTwice1() {
        RegisterVoterDtoRequest registerRequest = new RegisterVoterDtoRequest(0, "boris","borisov","12345678");
        assertEquals("boris", registerRequest.getFirstName());
        assertEquals("borisov", registerRequest.getLogin());
        assertEquals("12345678", registerRequest.getPassword());
        String jsonRegisterRequest2 = gson.toJson(registerRequest);
        String jsonRegisterResponse2  = voterService.registerVoter(jsonRegisterRequest2);

        RegisterVoterDtoResponse registerVoterDtoResponse = gson.fromJson(jsonRegisterResponse2, RegisterVoterDtoResponse.class);
        String resultRegisterJson2 = gson.toJson(registerVoterDtoResponse);
        assertEquals(resultRegisterJson2, jsonRegisterResponse2);

        String voterToken = registerVoterDtoResponse.getToken();
        Voter voter = dataBase.getVoterById(registerRequest.getId());
        VoteAgainstAllDtoRequest voteAgainstAllDtoRequest1 = new VoteAgainstAllDtoRequest(voterToken,voter);
        assertEquals(voterToken, voteAgainstAllDtoRequest1.getVotingToken());
        voteAgainstAllDtoRequest1.setTokensAndVoters(dataBase.getTokensAndVoters());
        assertEquals(1, voteAgainstAllDtoRequest1.getTokensAndVoters().size());

        String jsonVoteAgainstAllRequest1 = gson.toJson(voteAgainstAllDtoRequest1);
        String jsonVoteAgainstAllResponse1 = electionService.voteAgainstAll(jsonVoteAgainstAllRequest1);
        assertEquals(gson.toJson(""), jsonVoteAgainstAllResponse1);

        assertEquals(0, dataBase.getVotedCandidates().size());
        assertEquals(0, dataBase.getVotedVoters().size());

        assertEquals(1, dataBase.getVotedAgainstAll().size());

        VoteAgainstAllDtoRequest voteAgainstAllDtoRequest2 = new VoteAgainstAllDtoRequest(voterToken,voter);
        assertEquals(voterToken, voteAgainstAllDtoRequest2.getVotingToken());
        voteAgainstAllDtoRequest2.setTokensAndVoters(dataBase.getTokensAndVoters());
        assertEquals(1, voteAgainstAllDtoRequest2.getTokensAndVoters().size());

        String jsonVoteAgainstAllRequest2 = gson.toJson(voteAgainstAllDtoRequest2);
        String jsonVoteAgainstAllResponse2 = electionService.voteAgainstAll(jsonVoteAgainstAllRequest2);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonVoteAgainstAllResponse2);

        assertEquals(0, dataBase.getVotedCandidates().size());
        assertEquals(0, dataBase.getVotedVoters().size());

        assertEquals(1, dataBase.getVotedAgainstAll().size());
    }

    @Test
    public void testVoteAgainstAllIfVoterVoteTwice2() throws VoterException {
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

        String jsonAddCandidateRequest = gson.toJson(addCandidateRequest);
        String jsonAddCandidateResponse  = candidateService.addCandidate(jsonAddCandidateRequest);

        AddCandidateDtoResponse addCandidateDtoResponse1 = gson.fromJson(jsonAddCandidateResponse, AddCandidateDtoResponse.class);
        String resultAddCandidateResponseJson1 = gson.toJson(addCandidateDtoResponse1);
        assertEquals(resultAddCandidateResponseJson1, jsonAddCandidateResponse);

        RegisterVoterDtoRequest registerRequest2 = new RegisterVoterDtoRequest(1, "boris","borisov","12345678");
        assertEquals("boris", registerRequest2.getFirstName());
        assertEquals("borisov", registerRequest2.getLogin());
        assertEquals("12345678", registerRequest2.getPassword());
        String jsonRegisterRequest2 = gson.toJson(registerRequest2);
        String jsonRegisterResponse2  = voterService.registerVoter(jsonRegisterRequest2);

        RegisterVoterDtoResponse registerVoterDtoResponse2 = gson.fromJson(jsonRegisterResponse2, RegisterVoterDtoResponse.class);
        String resultRegisterJson2 = gson.toJson(registerVoterDtoResponse2);
        assertEquals(resultRegisterJson2, jsonRegisterResponse2);

        String voterToken = registerVoterDtoResponse2.getToken();
        String candidateToken = dataBase.getTokenByCandidateLogin(addCandidateRequest.getLogin());

        AddOfferDtoRequest addOfferDtoRequest = new AddOfferDtoRequest(voterToken,1, candidateToken,0, "build a bridge across the river");
        assertEquals(voterToken, addOfferDtoRequest.getVoterToken());
        assertEquals(candidateToken, addOfferDtoRequest.getCandidateToken());
        assertEquals("build a bridge across the river", addOfferDtoRequest.getOfferDescription());

        Map<String,Voter> tokensAndVoters = dataBase.getTokensAndVoters();
        addOfferDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(2, addOfferDtoRequest.getTokensAndVoters().size());

        String jsonAddOfferRequest = gson.toJson(addOfferDtoRequest);
        String jsonAddOfferResponse = offerService.addOffer(jsonAddOfferRequest);
        assertEquals(gson.toJson(""), jsonAddOfferResponse);

        VoteForCandidateDtoRequest voteForCandidateDtoRequest = new VoteForCandidateDtoRequest(1,0,voterToken, candidateToken);
        assertEquals(voterToken, voteForCandidateDtoRequest.getVotingToken());
        assertEquals(candidateToken, voteForCandidateDtoRequest.getCandidateToken());
        voteForCandidateDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(2, voteForCandidateDtoRequest.getTokensAndVoters().size());

        String jsonVoterForCandidateRequest = gson.toJson(voteForCandidateDtoRequest);
        String jsonVoterForCandidateResponse = electionService.voteForCandidate(jsonVoterForCandidateRequest);
        assertEquals(gson.toJson(""), jsonVoterForCandidateResponse);

        assertEquals(0, dataBase.getVotedCandidates().size());
        assertEquals(1, dataBase.getVotedVoters().size());

        Candidate candidate = dataBase.getCandidateById(addCandidateRequest.getId());
        assertEquals(1, candidate.getAmountVotes());
        assertEquals(1, dataBase.getRankedCandidates().size());
        assertTrue(dataBase.getRankedCandidates().contains(candidate));

        Voter voter = dataBase.getVoterById(registerRequest2.getId());
        VoteAgainstAllDtoRequest voteAgainstAllDtoRequest = new VoteAgainstAllDtoRequest(voterToken,voter);
        assertEquals(voterToken, voteAgainstAllDtoRequest.getVotingToken());
        voteAgainstAllDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
        assertEquals(2, voteAgainstAllDtoRequest.getTokensAndVoters().size());

        String jsonVoteAgainstAllRequest2 = gson.toJson(voteAgainstAllDtoRequest);
        String jsonVoteAgainstAllResponse2 = electionService.voteAgainstAll(jsonVoteAgainstAllRequest2);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonVoteAgainstAllResponse2);

        assertTrue(dataBase.getVotedAgainstAll().isEmpty());
        assertEquals(0, dataBase.getVotedCandidates().size());
        assertEquals(1, dataBase.getVotedVoters().size());

        assertEquals(1, candidate.getAmountVotes());
        assertEquals(1, dataBase.getRankedCandidates().size());
        assertTrue(dataBase.getRankedCandidates().contains(candidate));
    }

    @Ignore
    @Test
    public void testVoteAgainstAllIfCandidateVote() throws VoterException {
        RegisterVoterDtoRequest registerRequest1 = new RegisterVoterDtoRequest(0, "petr","petrov","12345678");
        assertEquals("petr", registerRequest1.getFirstName());
        assertEquals("petrov", registerRequest1.getLogin());
        assertEquals("12345678", registerRequest1.getPassword());
        String jsonRegisterRequest1 = gson.toJson(registerRequest1);
        String jsonRegisterResponse1  = voterService.registerVoter(jsonRegisterRequest1);

        RegisterVoterDtoResponse registerVoterDtoResponse1 = gson.fromJson(jsonRegisterResponse1, RegisterVoterDtoResponse.class);
        String resultRegisterJson1 = gson.toJson(registerVoterDtoResponse1);
        assertEquals(resultRegisterJson1, jsonRegisterResponse1);

        AddCandidateDtoRequest addCandidateRequest1 = new AddCandidateDtoRequest(registerRequest1.getId(), registerRequest1.getFirstName(), registerRequest1.getLogin(),
                                                                                 registerRequest1.getPassword(), registerVoterDtoResponse1.getToken());
        assertEquals(registerRequest1.getFirstName(), addCandidateRequest1.getFirstName());
        assertEquals(registerRequest1.getLogin(), addCandidateRequest1.getLogin());
        assertEquals(registerRequest1.getPassword(), addCandidateRequest1.getPassword());
        assertEquals(registerVoterDtoResponse1.getToken(), addCandidateRequest1.getToken());

        String jsonAddCandidateRequest = gson.toJson(addCandidateRequest1);
        String jsonAddCandidateResponse  = candidateService.addCandidate(jsonAddCandidateRequest);

        AddCandidateDtoResponse addCandidateDtoResponse1 = gson.fromJson(jsonAddCandidateResponse, AddCandidateDtoResponse.class);
        String resultAddCandidateResponseJson1 = gson.toJson(addCandidateDtoResponse1);
        assertEquals(resultAddCandidateResponseJson1, jsonAddCandidateResponse);

        String votingCandidateToken = addCandidateDtoResponse1.getToken();
        Candidate votingCandidate = dataBase.getCandidateById(addCandidateRequest1.getId());

        AddOfferToElectionProgramDtoRequest addOfferToElectionProgramDtoRequest1 = new AddOfferToElectionProgramDtoRequest(votingCandidateToken, 0,"repair the road");
        assertEquals(votingCandidateToken, addOfferToElectionProgramDtoRequest1.getCandidateToken());
        assertEquals("repair the road", addOfferToElectionProgramDtoRequest1.getOfferDescription());

        Map<String,Voter> tokensAndVoters = dataBase.getTokensAndVoters();
        addOfferToElectionProgramDtoRequest1.setTokensAndVoters(tokensAndVoters);
        assertEquals(0, addOfferToElectionProgramDtoRequest1.getTokensAndVoters().size());

        String jsonAddOfferRequest = gson.toJson(addOfferToElectionProgramDtoRequest1);
        String jsonAddOfferResponse = offerService.addOfferToElectionProgram(jsonAddOfferRequest);
        assertEquals(gson.toJson(""), jsonAddOfferResponse);
        assertEquals(1, votingCandidate.getElectionProgram().size());

        VoteAgainstAllDtoRequest voteAgainstAllDtoRequest = new VoteAgainstAllDtoRequest(votingCandidateToken, votingCandidate);
        assertEquals(votingCandidateToken, voteAgainstAllDtoRequest.getVotingToken());
        voteAgainstAllDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
        assertEquals(0, voteAgainstAllDtoRequest.getTokensAndVoters().size());

        String jsonVoteAgainstAllRequest = gson.toJson(voteAgainstAllDtoRequest);
        String jsonVoteAgainstAllResponse = electionService.voteAgainstAll(jsonVoteAgainstAllRequest);
        assertEquals(gson.toJson(""), jsonVoteAgainstAllResponse);

        assertEquals(0, dataBase.getVotedVoters().size());
        assertEquals(1, dataBase.getVotedCandidates().size());
        assertTrue(dataBase.getVotedCandidates().contains(votingCandidateToken));

        assertEquals(1, dataBase.getVotedAgainstAll().size());
        assertTrue(dataBase.getVotedAgainstAll().contains(votingCandidateToken));
    }

    @Ignore
    @Test
    public void testVoteAgainstAllIfCandidateVoteTwice1() throws VoterException {
        RegisterVoterDtoRequest registerRequest1 = new RegisterVoterDtoRequest(0,"petr","petrov","12345678");
        assertEquals("petr", registerRequest1.getFirstName());
        assertEquals("petrov", registerRequest1.getLogin());
        assertEquals("12345678", registerRequest1.getPassword());
        String jsonRegisterRequest1 = gson.toJson(registerRequest1);
        String jsonRegisterResponse1  = voterService.registerVoter(jsonRegisterRequest1);

        RegisterVoterDtoResponse registerVoterDtoResponse1 = gson.fromJson(jsonRegisterResponse1, RegisterVoterDtoResponse.class);
        String resultRegisterJson1 = gson.toJson(registerVoterDtoResponse1);
        assertEquals(resultRegisterJson1, jsonRegisterResponse1);

        AddCandidateDtoRequest addCandidateRequest1 = new AddCandidateDtoRequest(registerRequest1.getId(), registerRequest1.getFirstName(), registerRequest1.getLogin(),
                                                                                 registerRequest1.getPassword(), registerVoterDtoResponse1.getToken());
        assertEquals(registerRequest1.getFirstName(), addCandidateRequest1.getFirstName());
        assertEquals(registerRequest1.getLogin(), addCandidateRequest1.getLogin());
        assertEquals(registerRequest1.getPassword(), addCandidateRequest1.getPassword());
        assertEquals(registerVoterDtoResponse1.getToken(), addCandidateRequest1.getToken());

        String jsonAddCandidateRequest = gson.toJson(addCandidateRequest1);
        String jsonAddCandidateResponse  = candidateService.addCandidate(jsonAddCandidateRequest);

        AddCandidateDtoResponse addCandidateDtoResponse1 = gson.fromJson(jsonAddCandidateResponse, AddCandidateDtoResponse.class);
        String resultAddCandidateResponseJson1 = gson.toJson(addCandidateDtoResponse1);
        assertEquals(resultAddCandidateResponseJson1, jsonAddCandidateResponse);

        String votingCandidateToken = addCandidateRequest1.getToken();
        Candidate votingCandidate = dataBase.getCandidateById(addCandidateRequest1.getId());

        AddOfferToElectionProgramDtoRequest addOfferToElectionProgramDtoRequest1 = new AddOfferToElectionProgramDtoRequest(votingCandidateToken, 0,"repair the road");
        assertEquals(votingCandidateToken, addOfferToElectionProgramDtoRequest1.getCandidateToken());
        assertEquals("repair the road", addOfferToElectionProgramDtoRequest1.getOfferDescription());

        Map<String,Voter> tokensAndVoters = dataBase.getTokensAndVoters();
        addOfferToElectionProgramDtoRequest1.setTokensAndVoters(tokensAndVoters);
        assertEquals(1, addOfferToElectionProgramDtoRequest1.getTokensAndVoters().size());

        String jsonAddOfferRequest = gson.toJson(addOfferToElectionProgramDtoRequest1);
        String jsonAddOfferResponse = offerService.addOfferToElectionProgram(jsonAddOfferRequest);
        assertEquals(gson.toJson(""), jsonAddOfferResponse);
        assertEquals(1, votingCandidate.getElectionProgram().size());

        VoteAgainstAllDtoRequest voteAgainstAllDtoRequest1 = new VoteAgainstAllDtoRequest(votingCandidateToken, votingCandidate);
        assertEquals(votingCandidateToken, voteAgainstAllDtoRequest1.getVotingToken());
        voteAgainstAllDtoRequest1.setTokensAndVoters(dataBase.getTokensAndVoters());
        assertEquals(1, voteAgainstAllDtoRequest1.getTokensAndVoters().size());

        String jsonVoteAgainstAllRequest1 = gson.toJson(voteAgainstAllDtoRequest1);
        String jsonVoteAgainstAllResponse1 = electionService.voteAgainstAll(jsonVoteAgainstAllRequest1);
        assertEquals(gson.toJson(""), jsonVoteAgainstAllResponse1);

        assertEquals(0, dataBase.getVotedVoters().size());
        assertEquals(0, dataBase.getVotedCandidates().size());
        assertEquals(1, dataBase.getVotedAgainstAll().size());
        assertTrue(dataBase.getVotedAgainstAll().contains(votingCandidate));

        VoteAgainstAllDtoRequest voteAgainstAllDtoRequest2 = new VoteAgainstAllDtoRequest(votingCandidateToken, votingCandidate);
        assertEquals(votingCandidateToken, voteAgainstAllDtoRequest2.getVotingToken());
        voteAgainstAllDtoRequest2.setTokensAndVoters(dataBase.getTokensAndVoters());
        assertEquals(1, voteAgainstAllDtoRequest2.getTokensAndVoters().size());

        String jsonVoteAgainstAllRequest2 = gson.toJson(voteAgainstAllDtoRequest2);
        String jsonVoteAgainstAllResponse2 = electionService.voteAgainstAll(jsonVoteAgainstAllRequest2);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonVoteAgainstAllResponse2);

        assertEquals(0, dataBase.getVotedVoters().size());
        assertEquals(0, dataBase.getVotedCandidates().size());
        assertEquals(1, dataBase.getVotedAgainstAll().size());
    }

    @Ignore
    @Test
    public void testVoteAgainstAllIfCandidateVoteTwice2() throws VoterException {
        dataBase.getRankedCandidates().clear();

        RegisterVoterDtoRequest registerRequest1 = new RegisterVoterDtoRequest(0, "petr","petrov","12345678");
        assertEquals("petr", registerRequest1.getFirstName());
        assertEquals("petrov", registerRequest1.getLogin());
        assertEquals("12345678", registerRequest1.getPassword());
        String jsonRegisterRequest1 = gson.toJson(registerRequest1);
        String jsonRegisterResponse1  = voterService.registerVoter(jsonRegisterRequest1);

        RegisterVoterDtoResponse registerVoterDtoResponse1 = gson.fromJson(jsonRegisterResponse1, RegisterVoterDtoResponse.class);
        String resultRegisterJson1 = gson.toJson(registerVoterDtoResponse1);
        assertEquals(resultRegisterJson1, jsonRegisterResponse1);

        AddCandidateDtoRequest addCandidateRequest1 = new AddCandidateDtoRequest(registerRequest1.getId(), registerRequest1.getFirstName(), registerRequest1.getLogin(),
                                                                                 registerRequest1.getPassword(), registerVoterDtoResponse1.getToken());
        assertEquals(registerRequest1.getFirstName(), addCandidateRequest1.getFirstName());
        assertEquals(registerRequest1.getLogin(), addCandidateRequest1.getLogin());
        assertEquals(registerRequest1.getPassword(), addCandidateRequest1.getPassword());
        assertEquals(registerVoterDtoResponse1.getToken(), addCandidateRequest1.getToken());

        String jsonAddCandidateRequest = gson.toJson(addCandidateRequest1);
        String jsonAddCandidateResponse  = candidateService.addCandidate(jsonAddCandidateRequest);

        AddCandidateDtoResponse addCandidateDtoResponse1 = gson.fromJson(jsonAddCandidateResponse, AddCandidateDtoResponse.class);
        String resultAddCandidateResponseJson1 = gson.toJson(addCandidateDtoResponse1);
        assertEquals(resultAddCandidateResponseJson1, jsonAddCandidateResponse);

        RegisterVoterDtoRequest registerRequest2 = new RegisterVoterDtoRequest(1, "boris","borisov","12345678");
        assertEquals("boris", registerRequest2.getFirstName());
        assertEquals("borisov", registerRequest2.getLogin());
        assertEquals("12345678", registerRequest2.getPassword());
        String jsonRegisterRequest2 = gson.toJson(registerRequest2);
        String jsonRegisterResponse2  = voterService.registerVoter(jsonRegisterRequest2);

        RegisterVoterDtoResponse registerVoterDtoResponse2 = gson.fromJson(jsonRegisterResponse2, RegisterVoterDtoResponse.class);
        String resultRegisterJson2 = gson.toJson(registerVoterDtoResponse2);
        assertEquals(resultRegisterJson2, jsonRegisterResponse2);

        AddCandidateDtoRequest addCandidateRequest2 = new AddCandidateDtoRequest(registerRequest1.getId(), registerRequest2.getFirstName(), registerRequest2.getLogin(),
                                                                                 registerRequest2.getPassword(), registerVoterDtoResponse2.getToken());
        assertEquals(registerRequest2.getFirstName(), addCandidateRequest2.getFirstName());
        assertEquals(registerRequest2.getLogin(), addCandidateRequest2.getLogin());
        assertEquals(registerRequest2.getPassword(), addCandidateRequest2.getPassword());
        assertEquals(registerVoterDtoResponse2.getToken(), addCandidateRequest2.getToken());

        String jsonAddCandidateRequest2 = gson.toJson(addCandidateRequest2);
        String jsonAddCandidateResponse2  = candidateService.addCandidate(jsonAddCandidateRequest2);

        AddCandidateDtoResponse addCandidateDtoResponse2 = gson.fromJson(jsonAddCandidateResponse2, AddCandidateDtoResponse.class);
        String resultAddCandidateResponseJson2 = gson.toJson(addCandidateDtoResponse2);
        assertEquals(resultAddCandidateResponseJson2, jsonAddCandidateResponse);

        String votingCandidateToken = addCandidateDtoResponse1.getToken();
        Candidate votingCandidate = dataBase.getCandidateById(addCandidateRequest1.getId());
        String candidateToken = addCandidateDtoResponse2.getToken();
        Candidate candidate = dataBase.getCandidateById(addCandidateRequest2.getId());
        assertNotEquals(votingCandidateToken, candidateToken);

        AddOfferToElectionProgramDtoRequest addOfferToElectionProgramDtoRequest1 = new AddOfferToElectionProgramDtoRequest(votingCandidateToken, 0, "repair the road");
        assertEquals(votingCandidateToken, addOfferToElectionProgramDtoRequest1.getCandidateToken());
        assertEquals("repair the road", addOfferToElectionProgramDtoRequest1.getOfferDescription());

        Map<String,Voter> tokensAndVoters = dataBase.getTokensAndVoters();
        addOfferToElectionProgramDtoRequest1.setTokensAndVoters(tokensAndVoters);
        assertEquals(2, addOfferToElectionProgramDtoRequest1.getTokensAndVoters().size());

        String jsonAddOfferRequest = gson.toJson(addOfferToElectionProgramDtoRequest1);
        String jsonAddOfferResponse = offerService.addOfferToElectionProgram(jsonAddOfferRequest);
        assertEquals(gson.toJson(""), jsonAddOfferResponse);
        assertEquals(1, votingCandidate.getElectionProgram().size());

        AddOfferToElectionProgramDtoRequest addOfferToElectionProgramDtoRequest2 = new AddOfferToElectionProgramDtoRequest(candidateToken, 0, "build a bridge across the river");
        assertEquals(candidateToken, addOfferToElectionProgramDtoRequest2.getCandidateToken());
        assertEquals("build a bridge across the river", addOfferToElectionProgramDtoRequest2.getOfferDescription());

        addOfferToElectionProgramDtoRequest2.setTokensAndVoters(tokensAndVoters);
        assertEquals(2, addOfferToElectionProgramDtoRequest2.getTokensAndVoters().size());

        String jsonAddOfferRequest2 = gson.toJson(addOfferToElectionProgramDtoRequest2);
        String jsonAddOfferResponse2 = offerService.addOfferToElectionProgram(jsonAddOfferRequest2);
        assertEquals(gson.toJson(""), jsonAddOfferResponse2);
        assertEquals(1, candidate.getElectionProgram().size());

        VoteForCandidateDtoRequest voteForCandidateDtoRequest1 = new VoteForCandidateDtoRequest(1,0,votingCandidateToken, candidateToken);
        assertEquals(votingCandidateToken, voteForCandidateDtoRequest1.getVotingToken());
        assertEquals(candidateToken, voteForCandidateDtoRequest1.getCandidateToken());
        voteForCandidateDtoRequest1.setTokensAndVoters(tokensAndVoters);
        assertEquals(2, voteForCandidateDtoRequest1.getTokensAndVoters().size());

        String jsonVoterForCandidateRequest1 = gson.toJson(voteForCandidateDtoRequest1);
        String jsonVoterForCandidateResponse1 = electionService.voteForCandidate(jsonVoterForCandidateRequest1);
        assertEquals(gson.toJson(""), jsonVoterForCandidateResponse1);

        assertEquals(0, dataBase.getVotedVoters().size());
        assertEquals(1, dataBase.getVotedCandidates().size());
        assertTrue(dataBase.getVotedCandidates().contains(votingCandidateToken));

        assertEquals(1, candidate.getAmountVotes());
        assertEquals(1, dataBase.getRankedCandidates().size());
        assertTrue(dataBase.getRankedCandidates().contains(candidate));

        VoteAgainstAllDtoRequest voteAgainstAllDtoRequest1 = new VoteAgainstAllDtoRequest(votingCandidateToken,votingCandidate);
        assertEquals(votingCandidateToken, voteAgainstAllDtoRequest1.getVotingToken());
        voteAgainstAllDtoRequest1.setTokensAndVoters(dataBase.getTokensAndVoters());
        assertEquals(2, voteAgainstAllDtoRequest1.getTokensAndVoters().size());

        String jsonVoteAgainstAllRequest1 = gson.toJson(voteAgainstAllDtoRequest1);
        String jsonVoteAgainstAllResponse1 = electionService.voteAgainstAll(jsonVoteAgainstAllRequest1);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonVoteAgainstAllResponse1);

        assertEquals(0, dataBase.getVotedVoters().size());
        assertEquals(1, dataBase.getVotedCandidates().size());
        assertTrue(dataBase.getVotedCandidates().contains(votingCandidateToken));

        assertEquals(1, candidate.getAmountVotes());
        assertEquals(1, dataBase.getRankedCandidates().size());
        assertTrue(dataBase.getRankedCandidates().contains(candidate));

        VoteAgainstAllDtoRequest voteAgainstAllDtoRequest2 = new VoteAgainstAllDtoRequest(votingCandidateToken,votingCandidate);
        assertEquals(votingCandidateToken, voteAgainstAllDtoRequest2.getVotingToken());
        voteAgainstAllDtoRequest2.setTokensAndVoters(dataBase.getTokensAndVoters());
        assertEquals(2, voteAgainstAllDtoRequest2.getTokensAndVoters().size());

        String jsonVoteAgainstAllRequest2 = gson.toJson(voteAgainstAllDtoRequest2);
        String jsonVoteAgainstAllResponse2 = electionService.voteAgainstAll(jsonVoteAgainstAllRequest2);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonVoteAgainstAllResponse2);

        assertTrue(dataBase.getVotedAgainstAll().isEmpty());
        assertEquals(0, dataBase.getVotedVoters().size());
        assertEquals(1, dataBase.getVotedCandidates().size());
        assertTrue(dataBase.getVotedCandidates().contains(votingCandidateToken));

        assertEquals(1, candidate.getAmountVotes());
        assertEquals(1, dataBase.getRankedCandidates().size());
        assertTrue(dataBase.getRankedCandidates().contains(candidate));
    }

    @Test
    public void testVoteAgainstAllIfVotingCandidateVoteWithoutElectionProgram() throws VoterException {
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

        String jsonAddCandidateRequest = gson.toJson(addCandidateRequest);
        String jsonAddCandidateResponse  = candidateService.addCandidate(jsonAddCandidateRequest);

        AddCandidateDtoResponse addCandidateDtoResponse1 = gson.fromJson(jsonAddCandidateResponse, AddCandidateDtoResponse.class);
        String resultAddCandidateResponseJson1 = gson.toJson(addCandidateDtoResponse1);
        assertEquals(resultAddCandidateResponseJson1, jsonAddCandidateResponse);

        String votingCandidateToken = addCandidateDtoResponse1.getToken();
        Candidate votingCandidate = dataBase.getCandidateById(addCandidateRequest.getId());
        assertTrue(votingCandidate.getElectionProgram().isEmpty());
        assertEquals(0, votingCandidate.getElectionProgram().size());

        VoteAgainstAllDtoRequest voteAgainstAllDtoRequest = new VoteAgainstAllDtoRequest(votingCandidateToken,votingCandidate);
        assertEquals(votingCandidateToken, voteAgainstAllDtoRequest.getVotingToken());

        Map<String,Voter> tokensAndVoters = dataBase.getTokensAndVoters();
        voteAgainstAllDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(1, voteAgainstAllDtoRequest.getTokensAndVoters().size());

        String jsonVoterForCandidateRequest = gson.toJson(voteAgainstAllDtoRequest);
        String jsonVoterForCandidateResponse = electionService.voteForCandidate(jsonVoterForCandidateRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonVoterForCandidateResponse);
        assertTrue(dataBase.getVotedAgainstAll().isEmpty());
        assertTrue(dataBase.getVotedVoters().isEmpty());
        assertTrue(dataBase.getVotedCandidates().isEmpty());
    }

    @Ignore
    @Test
    public void testGetElectionResultsIfCandidateScoredMostAmountVotes() throws VoterException {
        dataBase.getRankedCandidates().clear();

        RegisterVoterDtoRequest registerRequest1 = new RegisterVoterDtoRequest(0, "petr1","petrov1","12345678");
        assertEquals("petr1", registerRequest1.getFirstName());
        assertEquals("petrov1", registerRequest1.getLogin());
        assertEquals("12345678", registerRequest1.getPassword());
        String jsonRegisterRequest1 = gson.toJson(registerRequest1);
        String jsonRegisterResponse1  = voterService.registerVoter(jsonRegisterRequest1);

        RegisterVoterDtoResponse registerVoterDtoResponse1 = gson.fromJson(jsonRegisterResponse1, RegisterVoterDtoResponse.class);
        String resultRegisterJson1 = gson.toJson(registerVoterDtoResponse1);
        assertEquals(resultRegisterJson1, jsonRegisterResponse1);

        AddCandidateDtoRequest addCandidateRequest1 = new AddCandidateDtoRequest(registerRequest1.getId(), registerRequest1.getFirstName(), registerRequest1.getLogin(),
                                                                                 registerRequest1.getPassword(), registerVoterDtoResponse1.getToken());
        assertEquals(registerRequest1.getFirstName(), addCandidateRequest1.getFirstName());
        assertEquals(registerRequest1.getLogin(), addCandidateRequest1.getLogin());
        assertEquals(registerRequest1.getPassword(), addCandidateRequest1.getPassword());
        assertEquals(registerVoterDtoResponse1.getToken(), addCandidateRequest1.getToken());

        String jsonAddCandidateRequest1 = gson.toJson(addCandidateRequest1);
        String jsonAddCandidateResponse1  = candidateService.addCandidate(jsonAddCandidateRequest1);

        AddCandidateDtoResponse addCandidateDtoResponse1 = gson.fromJson(jsonAddCandidateResponse1, AddCandidateDtoResponse.class);
        String resultAddCandidateResponseJson1 = gson.toJson(addCandidateDtoResponse1);
        assertEquals(resultAddCandidateResponseJson1, jsonAddCandidateResponse1);

        RegisterVoterDtoRequest registerRequest2 = new RegisterVoterDtoRequest(1, "petr2","petrov2","123456789");
        assertEquals("petr2", registerRequest2.getFirstName());
        assertEquals("petrov2", registerRequest2.getLogin());
        assertEquals("123456789", registerRequest2.getPassword());
        String jsonRegisterRequest2 = gson.toJson(registerRequest2);
        String jsonRegisterResponse2  = voterService.registerVoter(jsonRegisterRequest2);

        RegisterVoterDtoResponse registerVoterDtoResponse2 = gson.fromJson(jsonRegisterResponse2, RegisterVoterDtoResponse.class);
        String resultRegisterJson2 = gson.toJson(registerVoterDtoResponse2);
        assertEquals(resultRegisterJson2, jsonRegisterResponse2);

        AddCandidateDtoRequest addCandidateRequest2 = new AddCandidateDtoRequest(registerRequest2.getId(), registerRequest2.getFirstName(), registerRequest2.getLogin(),
                                                                                 registerRequest2.getPassword(), registerVoterDtoResponse2.getToken());
        assertEquals(registerRequest2.getFirstName(), addCandidateRequest2.getFirstName());
        assertEquals(registerRequest2.getLogin(), addCandidateRequest2.getLogin());
        assertEquals(registerRequest2.getPassword(), addCandidateRequest2.getPassword());
        assertEquals(registerVoterDtoResponse2.getToken(), addCandidateRequest2.getToken());

        String jsonAddCandidateRequest2 = gson.toJson(addCandidateRequest2);
        String jsonAddCandidateResponse2  = candidateService.addCandidate(jsonAddCandidateRequest2);

        AddCandidateDtoResponse addCandidateDtoResponse2 = gson.fromJson(jsonAddCandidateResponse2, AddCandidateDtoResponse.class);
        String resultAddCandidateResponseJson2 = gson.toJson(addCandidateDtoResponse2);
        assertEquals(resultAddCandidateResponseJson2, jsonAddCandidateResponse2);

        RegisterVoterDtoRequest registerRequest3 = new RegisterVoterDtoRequest(2, "petr3","petrov3","12345678");
        assertEquals("petr3", registerRequest3.getFirstName());
        assertEquals("petrov3", registerRequest3.getLogin());
        assertEquals("12345678", registerRequest3.getPassword());
        String jsonRegisterRequest3 = gson.toJson(registerRequest3);
        String jsonRegisterResponse3  = voterService.registerVoter(jsonRegisterRequest3);

        RegisterVoterDtoResponse registerVoterDtoResponse3 = gson.fromJson(jsonRegisterResponse3, RegisterVoterDtoResponse.class);
        String resultRegisterJson3 = gson.toJson(registerVoterDtoResponse3);
        assertEquals(resultRegisterJson3, jsonRegisterResponse3);

        AddCandidateDtoRequest addCandidateRequest3 = new AddCandidateDtoRequest(registerRequest3.getId(), registerRequest3.getFirstName(), registerRequest3.getLogin(),
                                                                                 registerRequest3.getPassword(), registerVoterDtoResponse3.getToken());
        assertEquals(registerRequest3.getFirstName(), addCandidateRequest3.getFirstName());
        assertEquals(registerRequest3.getLogin(), addCandidateRequest3.getLogin());
        assertEquals(registerRequest3.getPassword(), addCandidateRequest3.getPassword());
        assertEquals(registerVoterDtoResponse3.getToken(), addCandidateRequest3.getToken());

        String jsonAddCandidateRequest3 = gson.toJson(addCandidateRequest3);
        String jsonAddCandidateResponse3  = candidateService.addCandidate(jsonAddCandidateRequest3);

        AddCandidateDtoResponse addCandidateDtoResponse3 = gson.fromJson(jsonAddCandidateResponse3, AddCandidateDtoResponse.class);
        String resultAddCandidateResponseJson3 = gson.toJson(addCandidateDtoResponse3);
        assertEquals(resultAddCandidateResponseJson3, jsonAddCandidateResponse3);

        RegisterVoterDtoRequest registerRequest4 = new RegisterVoterDtoRequest(3, "boris1","borisov1","12345678");
        assertEquals("boris1", registerRequest4.getFirstName());
        assertEquals("borisov1", registerRequest4.getLogin());
        assertEquals("12345678", registerRequest4.getPassword());
        String jsonRegisterRequest4 = gson.toJson(registerRequest4);
        String jsonRegisterResponse4  = voterService.registerVoter(jsonRegisterRequest4);

        RegisterVoterDtoResponse registerVoterDtoResponse4 = gson.fromJson(jsonRegisterResponse4, RegisterVoterDtoResponse.class);
        String resultRegisterJson4 = gson.toJson(registerVoterDtoResponse4);
        assertEquals(resultRegisterJson4, jsonRegisterResponse4);

        RegisterVoterDtoRequest registerRequest5 = new RegisterVoterDtoRequest(4, "boris2","borisov2","12345678");
        assertEquals("boris2", registerRequest5.getFirstName());
        assertEquals("borisov2", registerRequest5.getLogin());
        assertEquals("12345678", registerRequest5.getPassword());
        String jsonRegisterRequest5 = gson.toJson(registerRequest5);
        String jsonRegisterResponse5  = voterService.registerVoter(jsonRegisterRequest5);

        RegisterVoterDtoResponse registerVoterDtoResponse5 = gson.fromJson(jsonRegisterResponse5, RegisterVoterDtoResponse.class);
        String resultRegisterJson5 = gson.toJson(registerVoterDtoResponse5);
        assertEquals(resultRegisterJson5, jsonRegisterResponse5);

        RegisterVoterDtoRequest registerRequest6 = new RegisterVoterDtoRequest(5, "boris3","borisov3","12345678");
        assertEquals("boris3", registerRequest6.getFirstName());
        assertEquals("borisov3", registerRequest6.getLogin());
        assertEquals("12345678", registerRequest6.getPassword());
        String jsonRegisterRequest6 = gson.toJson(registerRequest6);
        String jsonRegisterResponse6  = voterService.registerVoter(jsonRegisterRequest6);

        RegisterVoterDtoResponse registerVoterDtoResponse6 = gson.fromJson(jsonRegisterResponse6, RegisterVoterDtoResponse.class);
        String resultRegisterJson6 = gson.toJson(registerVoterDtoResponse6);
        assertEquals(resultRegisterJson6, jsonRegisterResponse6);

        RegisterVoterDtoRequest registerRequest7 = new RegisterVoterDtoRequest(6, "boris4","borisov4","12345678");
        assertEquals("boris4", registerRequest7.getFirstName());
        assertEquals("borisov4", registerRequest7.getLogin());
        assertEquals("12345678", registerRequest7.getPassword());
        String jsonRegisterRequest7 = gson.toJson(registerRequest7);
        String jsonRegisterResponse7  = voterService.registerVoter(jsonRegisterRequest7);

        RegisterVoterDtoResponse registerVoterDtoResponse7 = gson.fromJson(jsonRegisterResponse7, RegisterVoterDtoResponse.class);
        String resultRegisterJson7 = gson.toJson(registerVoterDtoResponse7);
        assertEquals(resultRegisterJson7, jsonRegisterResponse7);

        RegisterVoterDtoRequest registerRequest8 = new RegisterVoterDtoRequest(7, "boris5","borisov5","12345678");
        assertEquals("boris5", registerRequest8.getFirstName());
        assertEquals("borisov5", registerRequest8.getLogin());
        assertEquals("12345678", registerRequest8.getPassword());
        String jsonRegisterRequest8 = gson.toJson(registerRequest8);
        String jsonRegisterResponse8  = voterService.registerVoter(jsonRegisterRequest8);

        RegisterVoterDtoResponse registerVoterDtoResponse8 = gson.fromJson(jsonRegisterResponse8, RegisterVoterDtoResponse.class);
        String resultRegisterJson8 = gson.toJson(registerVoterDtoResponse8);
        assertEquals(resultRegisterJson8, jsonRegisterResponse8);

        String candidateToken1 = addCandidateDtoResponse1.getToken();
        String candidateToken2 = addCandidateDtoResponse2.getToken();
        String candidateToken3 = addCandidateDtoResponse3.getToken();

        String voterToken1 = registerVoterDtoResponse4.getToken();
        String voterToken2 = registerVoterDtoResponse5.getToken();
        String voterToken3 = registerVoterDtoResponse6.getToken();
        String voterToken4 = registerVoterDtoResponse7.getToken();
        String voterToken5 = registerVoterDtoResponse8.getToken();

        AddOfferDtoRequest addOfferDtoRequest1 = new AddOfferDtoRequest(voterToken1, 3,candidateToken1,0, "build a bridge across the river");
        assertEquals(voterToken1, addOfferDtoRequest1.getVoterToken());
        assertEquals(candidateToken1, addOfferDtoRequest1.getCandidateToken());
        assertEquals("build a bridge across the river", addOfferDtoRequest1.getOfferDescription());

        Map<String, Voter> tokensAndVoters = dataBase.getTokensAndVoters();
        addOfferDtoRequest1.setTokensAndVoters(tokensAndVoters);
        assertEquals(8, addOfferDtoRequest1.getTokensAndVoters().size());

        String jsonAddOfferRequest1 = gson.toJson(addOfferDtoRequest1);
        String jsonAddOfferResponse1 = offerService.addOffer(jsonAddOfferRequest1);
        assertEquals(gson.toJson(""), jsonAddOfferResponse1);

        AddOfferDtoRequest addOfferDtoRequest2 = new AddOfferDtoRequest(voterToken2,4, candidateToken2,1, "repair the road");
        assertEquals(voterToken2, addOfferDtoRequest2.getVoterToken());
        assertEquals(candidateToken2, addOfferDtoRequest2.getCandidateToken());
        assertEquals("repair the road", addOfferDtoRequest2.getOfferDescription());

        addOfferDtoRequest2.setTokensAndVoters(tokensAndVoters);
        assertEquals(8, addOfferDtoRequest2.getTokensAndVoters().size());

        String jsonAddOfferRequest2 = gson.toJson(addOfferDtoRequest2);
        String jsonAddOfferResponse2 = offerService.addOffer(jsonAddOfferRequest2);
        assertEquals(gson.toJson(""), jsonAddOfferResponse2);

        AddOfferDtoRequest addOfferDtoRequest3 = new AddOfferDtoRequest(voterToken3, 5,candidateToken3,2, "increase the minimum salary");
        assertEquals(voterToken3, addOfferDtoRequest3.getVoterToken());
        assertEquals(candidateToken3, addOfferDtoRequest3.getCandidateToken());
        assertEquals("increase the minimum salary", addOfferDtoRequest3.getOfferDescription());

        addOfferDtoRequest3.setTokensAndVoters(tokensAndVoters);
        assertEquals(8, addOfferDtoRequest3.getTokensAndVoters().size());

        String jsonAddOfferRequest3 = gson.toJson(addOfferDtoRequest3);
        String jsonAddOfferResponse3 = offerService.addOffer(jsonAddOfferRequest3);
        assertEquals(gson.toJson(""), jsonAddOfferResponse3);

        VoteForCandidateDtoRequest voteForCandidateDtoRequest1 = new VoteForCandidateDtoRequest(3,0,voterToken1, candidateToken1);
        assertEquals(voterToken1, voteForCandidateDtoRequest1.getVotingToken());
        assertEquals(candidateToken1, voteForCandidateDtoRequest1.getCandidateToken());
        voteForCandidateDtoRequest1.setTokensAndVoters(tokensAndVoters);
        assertEquals(8, voteForCandidateDtoRequest1.getTokensAndVoters().size());

        String jsonVoterForCandidateRequest1 = gson.toJson(voteForCandidateDtoRequest1);
        String jsonVoterForCandidateResponse1 = electionService.voteForCandidate(jsonVoterForCandidateRequest1);
        assertEquals(gson.toJson(""), jsonVoterForCandidateResponse1);

        VoteForCandidateDtoRequest voteForCandidateDtoRequest2 = new VoteForCandidateDtoRequest(4,1,voterToken2, candidateToken2);
        assertEquals(voterToken2, voteForCandidateDtoRequest2.getVotingToken());
        assertEquals(candidateToken2, voteForCandidateDtoRequest2.getCandidateToken());
        voteForCandidateDtoRequest2.setTokensAndVoters(tokensAndVoters);
        assertEquals(8, voteForCandidateDtoRequest2.getTokensAndVoters().size());

        String jsonVoterForCandidateRequest2 = gson.toJson(voteForCandidateDtoRequest2);
        String jsonVoterForCandidateResponse2 = electionService.voteForCandidate(jsonVoterForCandidateRequest2);
        assertEquals(gson.toJson(""), jsonVoterForCandidateResponse2);

        VoteForCandidateDtoRequest voteForCandidateDtoRequest3 = new VoteForCandidateDtoRequest(5,2,voterToken3, candidateToken3);
        assertEquals(voterToken3, voteForCandidateDtoRequest3.getVotingToken());
        assertEquals(candidateToken3, voteForCandidateDtoRequest3.getCandidateToken());
        voteForCandidateDtoRequest3.setTokensAndVoters(tokensAndVoters);
        assertEquals(8, voteForCandidateDtoRequest3.getTokensAndVoters().size());

        String jsonVoterForCandidateRequest3 = gson.toJson(voteForCandidateDtoRequest3);
        String jsonVoterForCandidateResponse3 = electionService.voteForCandidate(jsonVoterForCandidateRequest3);
        assertEquals(gson.toJson(""), jsonVoterForCandidateResponse3);

        VoteForCandidateDtoRequest voteForCandidateDtoRequest4 = new VoteForCandidateDtoRequest(6,2,voterToken4, candidateToken3);
        assertEquals(voterToken4, voteForCandidateDtoRequest4.getVotingToken());
        assertEquals(candidateToken3, voteForCandidateDtoRequest4.getCandidateToken());
        voteForCandidateDtoRequest4.setTokensAndVoters(tokensAndVoters);
        assertEquals(8, voteForCandidateDtoRequest4.getTokensAndVoters().size());

        String jsonVoterForCandidateRequest4 = gson.toJson(voteForCandidateDtoRequest4);
        String jsonVoterForCandidateResponse4 = electionService.voteForCandidate(jsonVoterForCandidateRequest4);
        assertEquals(gson.toJson(""), jsonVoterForCandidateResponse4);

        VoteForCandidateDtoRequest voteForCandidateDtoRequest5 = new VoteForCandidateDtoRequest(7,2,voterToken5, candidateToken3);
        assertEquals(voterToken5, voteForCandidateDtoRequest5.getVotingToken());
        assertEquals(candidateToken3, voteForCandidateDtoRequest5.getCandidateToken());
        voteForCandidateDtoRequest5.setTokensAndVoters(tokensAndVoters);
        assertEquals(8, voteForCandidateDtoRequest5.getTokensAndVoters().size());

        String jsonVoterForCandidateRequest5 = gson.toJson(voteForCandidateDtoRequest5);
        String jsonVoterForCandidateResponse5 = electionService.voteForCandidate(jsonVoterForCandidateRequest5);
        assertEquals(gson.toJson(""), jsonVoterForCandidateResponse5);

        VoteForCandidateDtoRequest voteForCandidateDtoRequest6 = new VoteForCandidateDtoRequest(0,2,candidateToken1, candidateToken3);
        assertEquals(candidateToken1, voteForCandidateDtoRequest6.getVotingToken());
        assertEquals(candidateToken3, voteForCandidateDtoRequest6.getCandidateToken());
        voteForCandidateDtoRequest6.setTokensAndVoters(tokensAndVoters);
        assertEquals(8, voteForCandidateDtoRequest6.getTokensAndVoters().size());

        String jsonVoterForCandidateRequest6 = gson.toJson(voteForCandidateDtoRequest6);
        String jsonVoterForCandidateResponse6 = electionService.voteForCandidate(jsonVoterForCandidateRequest6);
        assertEquals(gson.toJson(""), jsonVoterForCandidateResponse6);

        GetElectionResultsDtoRequest getElectionResultsDtoRequest = new GetElectionResultsDtoRequest(voterToken1);
        assertEquals(voterToken1, getElectionResultsDtoRequest.getToken());
        getElectionResultsDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(8, getElectionResultsDtoRequest.getTokensAndVoters().size());

        String jsonGetElectionResultsRequest = gson.toJson(getElectionResultsDtoRequest);
        String jsonGetElectionResultsResponse = electionService.getElectionResults(jsonGetElectionResultsRequest);

        GetElectionResultsDtoResponse getElectionResultsDtoResponse = gson.fromJson(jsonGetElectionResultsResponse, GetElectionResultsDtoResponse.class);
        String result = gson.toJson(getElectionResultsDtoResponse);
        assertEquals(result, jsonGetElectionResultsResponse);

        Candidate candidate1 = dataBase.getCandidateById(addCandidateRequest1.getId());
        Candidate candidate2 = dataBase.getCandidateById(addCandidateRequest2.getId());
        Candidate candidate3 = dataBase.getCandidateById(addCandidateRequest3.getId());
        assertEquals(1, candidate1.getAmountVotes());
        assertEquals(1, candidate2.getAmountVotes());
        assertEquals(4, candidate3.getAmountVotes());
        assertTrue(candidate3.getAmountVotes() > candidate1.getAmountVotes());
        assertTrue(candidate3.getAmountVotes() > candidate2.getAmountVotes());
        assertEquals(candidate3.getAmountVotes(), getElectionResultsDtoResponse.getSelectedCandidate().getAmountVotes());
        assertEquals(candidate3.getLogin(), getElectionResultsDtoResponse.getSelectedCandidate().getLogin());
        assertTrue(dataBase.getVotedAgainstAll().isEmpty());
    }

    @Test
    public void testGetElectionResultsIfTwoCandidatesScoredMostAmountVotes() throws VoterException {
        dataBase.getRankedCandidates().clear();

        RegisterVoterDtoRequest registerRequest1 = new RegisterVoterDtoRequest(0, "petr1","petrov1","12345678");
        assertEquals("petr1", registerRequest1.getFirstName());
        assertEquals("petrov1", registerRequest1.getLogin());
        assertEquals("12345678", registerRequest1.getPassword());
        String jsonRegisterRequest1 = gson.toJson(registerRequest1);
        String jsonRegisterResponse1  = voterService.registerVoter(jsonRegisterRequest1);

        RegisterVoterDtoResponse registerVoterDtoResponse1 = gson.fromJson(jsonRegisterResponse1, RegisterVoterDtoResponse.class);
        String resultRegisterJson1 = gson.toJson(registerVoterDtoResponse1);
        assertEquals(resultRegisterJson1, jsonRegisterResponse1);

        AddCandidateDtoRequest addCandidateRequest1 = new AddCandidateDtoRequest(registerRequest1.getId(), registerRequest1.getFirstName(), registerRequest1.getLogin(),
                                                                                  registerRequest1.getPassword(), registerVoterDtoResponse1.getToken());
        assertEquals(registerRequest1.getFirstName(), addCandidateRequest1.getFirstName());
        assertEquals(registerRequest1.getLogin(), addCandidateRequest1.getLogin());
        assertEquals(registerRequest1.getPassword(), addCandidateRequest1.getPassword());
        assertEquals(registerVoterDtoResponse1.getToken(), addCandidateRequest1.getToken());

        String jsonAddCandidateRequest1 = gson.toJson(addCandidateRequest1);
        String jsonAddCandidateResponse1  = candidateService.addCandidate(jsonAddCandidateRequest1);

        AddCandidateDtoResponse addCandidateDtoResponse1 = gson.fromJson(jsonAddCandidateResponse1, AddCandidateDtoResponse.class);
        String resultAddCandidateResponseJson1 = gson.toJson(addCandidateDtoResponse1);
        assertEquals(resultAddCandidateResponseJson1, jsonAddCandidateResponse1);

        RegisterVoterDtoRequest registerRequest2 = new RegisterVoterDtoRequest(1, "petr2","petrov2","12345678");
        assertEquals("petr2", registerRequest2.getFirstName());
        assertEquals("petrov2", registerRequest2.getLogin());
        assertEquals("12345678", registerRequest2.getPassword());
        String jsonRegisterRequest2 = gson.toJson(registerRequest2);
        String jsonRegisterResponse2  = voterService.registerVoter(jsonRegisterRequest2);

        RegisterVoterDtoResponse registerVoterDtoResponse2 = gson.fromJson(jsonRegisterResponse2, RegisterVoterDtoResponse.class);
        String resultRegisterJson2 = gson.toJson(registerVoterDtoResponse2);
        assertEquals(resultRegisterJson2, jsonRegisterResponse2);

        AddCandidateDtoRequest addCandidateRequest2 = new AddCandidateDtoRequest(registerRequest2.getId(), registerRequest2.getFirstName(), registerRequest2.getLogin(),
                                                                                 registerRequest2.getPassword(), registerVoterDtoResponse2.getToken());
        assertEquals(registerRequest2.getFirstName(), addCandidateRequest2.getFirstName());
        assertEquals(registerRequest2.getLogin(), addCandidateRequest2.getLogin());
        assertEquals(registerRequest2.getPassword(), addCandidateRequest2.getPassword());
        assertEquals(registerVoterDtoResponse2.getToken(), addCandidateRequest2.getToken());

        String jsonAddCandidateRequest2 = gson.toJson(addCandidateRequest2);
        String jsonAddCandidateResponse2  = candidateService.addCandidate(jsonAddCandidateRequest2);

        AddCandidateDtoResponse addCandidateDtoResponse2 = gson.fromJson(jsonAddCandidateResponse2, AddCandidateDtoResponse.class);
        String resultAddCandidateResponseJson2 = gson.toJson(addCandidateDtoResponse2);
        assertEquals(resultAddCandidateResponseJson2, jsonAddCandidateResponse2);

        RegisterVoterDtoRequest registerRequest3 = new RegisterVoterDtoRequest(2, "petr3","petrov3","12345678");
        assertEquals("petr3", registerRequest3.getFirstName());
        assertEquals("petrov3", registerRequest3.getLogin());
        assertEquals("12345678", registerRequest3.getPassword());
        String jsonRegisterRequest3 = gson.toJson(registerRequest3);
        String jsonRegisterResponse3  = voterService.registerVoter(jsonRegisterRequest3);

        RegisterVoterDtoResponse registerVoterDtoResponse3 = gson.fromJson(jsonRegisterResponse3, RegisterVoterDtoResponse.class);
        String resultRegisterJson3 = gson.toJson(registerVoterDtoResponse3);
        assertEquals(resultRegisterJson3, jsonRegisterResponse3);

        AddCandidateDtoRequest addCandidateRequest3 = new AddCandidateDtoRequest(registerRequest3.getId(), registerRequest3.getFirstName(), registerRequest3.getLogin(),
                                                                                 registerRequest3.getPassword(), registerVoterDtoResponse3.getToken());
        assertEquals(registerRequest3.getFirstName(), addCandidateRequest3.getFirstName());
        assertEquals(registerRequest3.getLogin(), addCandidateRequest3.getLogin());
        assertEquals(registerRequest3.getPassword(), addCandidateRequest3.getPassword());
        assertEquals(registerVoterDtoResponse3.getToken(), addCandidateRequest3.getToken());

        String jsonAddCandidateRequest3 = gson.toJson(addCandidateRequest3);
        String jsonAddCandidateResponse3  = candidateService.addCandidate(jsonAddCandidateRequest3);

        AddCandidateDtoResponse addCandidateDtoResponse3 = gson.fromJson(jsonAddCandidateResponse3, AddCandidateDtoResponse.class);
        String resultAddCandidateResponseJson3 = gson.toJson(addCandidateDtoResponse3);
        assertEquals(resultAddCandidateResponseJson3, jsonAddCandidateResponse3);

        RegisterVoterDtoRequest registerRequest4 = new RegisterVoterDtoRequest(3, "boris1","borisov1","12345678");
        assertEquals("boris1", registerRequest4.getFirstName());
        assertEquals("borisov1", registerRequest4.getLogin());
        assertEquals("12345678", registerRequest4.getPassword());
        String jsonRegisterRequest4 = gson.toJson(registerRequest4);
        String jsonRegisterResponse4  = voterService.registerVoter(jsonRegisterRequest4);

        RegisterVoterDtoResponse registerVoterDtoResponse4 = gson.fromJson(jsonRegisterResponse4, RegisterVoterDtoResponse.class);
        String resultRegisterJson4 = gson.toJson(registerVoterDtoResponse4);
        assertEquals(resultRegisterJson4, jsonRegisterResponse4);

        RegisterVoterDtoRequest registerRequest5 = new RegisterVoterDtoRequest(4, "boris2","borisov2","12345678");
        assertEquals("boris2", registerRequest5.getFirstName());
        assertEquals("borisov2", registerRequest5.getLogin());
        assertEquals("12345678", registerRequest5.getPassword());
        String jsonRegisterRequest5 = gson.toJson(registerRequest5);
        String jsonRegisterResponse5  = voterService.registerVoter(jsonRegisterRequest5);

        RegisterVoterDtoResponse registerVoterDtoResponse5 = gson.fromJson(jsonRegisterResponse5, RegisterVoterDtoResponse.class);
        String resultRegisterJson5 = gson.toJson(registerVoterDtoResponse5);
        assertEquals(resultRegisterJson5, jsonRegisterResponse5);

        RegisterVoterDtoRequest registerRequest6 = new RegisterVoterDtoRequest(5, "boris3","borisov3","12345678");
        assertEquals("boris3", registerRequest6.getFirstName());
        assertEquals("borisov3", registerRequest6.getLogin());
        assertEquals("12345678", registerRequest6.getPassword());
        String jsonRegisterRequest6 = gson.toJson(registerRequest6);
        String jsonRegisterResponse6  = voterService.registerVoter(jsonRegisterRequest6);

        RegisterVoterDtoResponse registerVoterDtoResponse6 = gson.fromJson(jsonRegisterResponse6, RegisterVoterDtoResponse.class);
        String resultRegisterJson6 = gson.toJson(registerVoterDtoResponse6);
        assertEquals(resultRegisterJson6, jsonRegisterResponse6);

        RegisterVoterDtoRequest registerRequest7 = new RegisterVoterDtoRequest(6, "boris4","borisov4","12345678");
        assertEquals("boris4", registerRequest7.getFirstName());
        assertEquals("borisov4", registerRequest7.getLogin());
        assertEquals("12345678", registerRequest7.getPassword());
        String jsonRegisterRequest7 = gson.toJson(registerRequest7);
        String jsonRegisterResponse7  = voterService.registerVoter(jsonRegisterRequest7);

        RegisterVoterDtoResponse registerVoterDtoResponse7 = gson.fromJson(jsonRegisterResponse7, RegisterVoterDtoResponse.class);
        String resultRegisterJson7 = gson.toJson(registerVoterDtoResponse7);
        assertEquals(resultRegisterJson7, jsonRegisterResponse7);

        RegisterVoterDtoRequest registerRequest8 = new RegisterVoterDtoRequest(7, "boris5","borisov5","12345678");
        assertEquals("boris5", registerRequest8.getFirstName());
        assertEquals("borisov5", registerRequest8.getLogin());
        assertEquals("12345678", registerRequest8.getPassword());
        String jsonRegisterRequest8 = gson.toJson(registerRequest8);
        String jsonRegisterResponse8  = voterService.registerVoter(jsonRegisterRequest8);

        RegisterVoterDtoResponse registerVoterDtoResponse8 = gson.fromJson(jsonRegisterResponse8, RegisterVoterDtoResponse.class);
        String resultRegisterJson8 = gson.toJson(registerVoterDtoResponse8);
        assertEquals(resultRegisterJson8, jsonRegisterResponse8);

        String candidateToken1 = addCandidateDtoResponse1.getToken();
        String candidateToken2 = addCandidateDtoResponse2.getToken();
        String candidateToken3 = addCandidateDtoResponse3.getToken();

        String voterToken1 = registerVoterDtoResponse4.getToken();
        String voterToken2 = registerVoterDtoResponse5.getToken();
        String voterToken3 = registerVoterDtoResponse6.getToken();
        String voterToken4 = registerVoterDtoResponse7.getToken();
        String voterToken5 = registerVoterDtoResponse8.getToken();

        AddOfferDtoRequest addOfferDtoRequest1 = new AddOfferDtoRequest(voterToken1,3, candidateToken1,0, "build a bridge across the river");
        assertEquals(voterToken1, addOfferDtoRequest1.getVoterToken());
        assertEquals(candidateToken1, addOfferDtoRequest1.getCandidateToken());
        assertEquals("build a bridge across the river", addOfferDtoRequest1.getOfferDescription());

        Map<String, Voter> tokensAndVoters = dataBase.getTokensAndVoters();
        addOfferDtoRequest1.setTokensAndVoters(tokensAndVoters);
        assertEquals(8, addOfferDtoRequest1.getTokensAndVoters().size());

        String jsonAddOfferRequest1 = gson.toJson(addOfferDtoRequest1);
        String jsonAddOfferResponse1 = offerService.addOffer(jsonAddOfferRequest1);
        assertEquals(gson.toJson(""), jsonAddOfferResponse1);

        AddOfferDtoRequest addOfferDtoRequest2 = new AddOfferDtoRequest(voterToken2,4, candidateToken2,1, "repair the road");
        assertEquals(voterToken2, addOfferDtoRequest2.getVoterToken());
        assertEquals(candidateToken2, addOfferDtoRequest2.getCandidateToken());
        assertEquals("repair the road", addOfferDtoRequest2.getOfferDescription());

        addOfferDtoRequest2.setTokensAndVoters(tokensAndVoters);
        assertEquals(8, addOfferDtoRequest2.getTokensAndVoters().size());

        String jsonAddOfferRequest2 = gson.toJson(addOfferDtoRequest2);
        String jsonAddOfferResponse2 = offerService.addOffer(jsonAddOfferRequest2);
        assertEquals(gson.toJson(""), jsonAddOfferResponse2);

        AddOfferDtoRequest addOfferDtoRequest3 = new AddOfferDtoRequest(voterToken3,5, candidateToken3,2, "increase the minimum salary");
        assertEquals(voterToken3, addOfferDtoRequest3.getVoterToken());
        assertEquals(candidateToken3, addOfferDtoRequest3.getCandidateToken());
        assertEquals("increase the minimum salary", addOfferDtoRequest3.getOfferDescription());

        addOfferDtoRequest3.setTokensAndVoters(tokensAndVoters);
        assertEquals(8, addOfferDtoRequest3.getTokensAndVoters().size());

        String jsonAddOfferRequest3 = gson.toJson(addOfferDtoRequest3);
        String jsonAddOfferResponse3 = offerService.addOffer(jsonAddOfferRequest3);
        assertEquals(gson.toJson(""), jsonAddOfferResponse3);

        VoteForCandidateDtoRequest voteForCandidateDtoRequest1 = new VoteForCandidateDtoRequest(3,0,voterToken1, candidateToken1);
        assertEquals(voterToken1, voteForCandidateDtoRequest1.getVotingToken());
        assertEquals(candidateToken1, voteForCandidateDtoRequest1.getCandidateToken());
        voteForCandidateDtoRequest1.setTokensAndVoters(tokensAndVoters);
        assertEquals(8, voteForCandidateDtoRequest1.getTokensAndVoters().size());

        String jsonVoterForCandidateRequest1 = gson.toJson(voteForCandidateDtoRequest1);
        String jsonVoterForCandidateResponse1 = electionService.voteForCandidate(jsonVoterForCandidateRequest1);
        assertEquals(gson.toJson(""), jsonVoterForCandidateResponse1);

        VoteForCandidateDtoRequest voteForCandidateDtoRequest2 = new VoteForCandidateDtoRequest(4,0,voterToken2, candidateToken1);
        assertEquals(voterToken2, voteForCandidateDtoRequest2.getVotingToken());
        assertEquals(candidateToken1, voteForCandidateDtoRequest2.getCandidateToken());
        voteForCandidateDtoRequest2.setTokensAndVoters(tokensAndVoters);
        assertEquals(8, voteForCandidateDtoRequest2.getTokensAndVoters().size());

        String jsonVoterForCandidateRequest2 = gson.toJson(voteForCandidateDtoRequest2);
        String jsonVoterForCandidateResponse2 = electionService.voteForCandidate(jsonVoterForCandidateRequest2);
        assertEquals(gson.toJson(""), jsonVoterForCandidateResponse2);

        VoteForCandidateDtoRequest voteForCandidateDtoRequest3 = new VoteForCandidateDtoRequest(5,2,voterToken3, candidateToken3);
        assertEquals(voterToken3, voteForCandidateDtoRequest3.getVotingToken());
        assertEquals(candidateToken3, voteForCandidateDtoRequest3.getCandidateToken());
        voteForCandidateDtoRequest3.setTokensAndVoters(tokensAndVoters);
        assertEquals(8, voteForCandidateDtoRequest3.getTokensAndVoters().size());

        String jsonVoterForCandidateRequest3 = gson.toJson(voteForCandidateDtoRequest3);
        String jsonVoterForCandidateResponse3 = electionService.voteForCandidate(jsonVoterForCandidateRequest3);
        assertEquals(gson.toJson(""), jsonVoterForCandidateResponse3);

        VoteForCandidateDtoRequest voteForCandidateDtoRequest4 = new VoteForCandidateDtoRequest(6,2,voterToken4, candidateToken3);
        assertEquals(voterToken4, voteForCandidateDtoRequest4.getVotingToken());
        assertEquals(candidateToken3, voteForCandidateDtoRequest4.getCandidateToken());
        voteForCandidateDtoRequest4.setTokensAndVoters(tokensAndVoters);
        assertEquals(8, voteForCandidateDtoRequest4.getTokensAndVoters().size());

        String jsonVoterForCandidateRequest4 = gson.toJson(voteForCandidateDtoRequest4);
        String jsonVoterForCandidateResponse4 = electionService.voteForCandidate(jsonVoterForCandidateRequest4);
        assertEquals(gson.toJson(""), jsonVoterForCandidateResponse4);

        VoteForCandidateDtoRequest voteForCandidateDtoRequest5 = new VoteForCandidateDtoRequest(7,1,voterToken5, candidateToken2);
        assertEquals(voterToken5, voteForCandidateDtoRequest5.getVotingToken());
        assertEquals(candidateToken2, voteForCandidateDtoRequest5.getCandidateToken());
        voteForCandidateDtoRequest5.setTokensAndVoters(tokensAndVoters);
        assertEquals(8, voteForCandidateDtoRequest5.getTokensAndVoters().size());

        String jsonVoterForCandidateRequest5 = gson.toJson(voteForCandidateDtoRequest5);
        String jsonVoterForCandidateResponse5 = electionService.voteForCandidate(jsonVoterForCandidateRequest5);
        assertEquals(gson.toJson(""), jsonVoterForCandidateResponse5);

        GetElectionResultsDtoRequest getElectionResultsDtoRequest = new GetElectionResultsDtoRequest(voterToken1);
        assertEquals(voterToken1, getElectionResultsDtoRequest.getToken());
        getElectionResultsDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(8, getElectionResultsDtoRequest.getTokensAndVoters().size());

        String jsonGetElectionResultsRequest = gson.toJson(getElectionResultsDtoRequest);
        String jsonGetElectionResultsResponse = electionService.getElectionResults(jsonGetElectionResultsRequest);

        GetElectionResultsDtoResponse getElectionResultsDtoResponse = gson.fromJson(jsonGetElectionResultsResponse, GetElectionResultsDtoResponse.class);
        String result = gson.toJson(getElectionResultsDtoResponse);
        assertEquals(result, jsonGetElectionResultsResponse);

        Candidate candidate1 = dataBase.getCandidateById(addCandidateRequest1.getId());
        Candidate candidate2 = dataBase.getCandidateById(addCandidateRequest2.getId());
        Candidate candidate3 = dataBase.getCandidateById(addCandidateRequest3.getId());
        assertEquals(2, candidate1.getAmountVotes());
        assertEquals(1, candidate2.getAmountVotes());
        assertEquals(2, candidate3.getAmountVotes());
        assertTrue(candidate3.getAmountVotes() == candidate1.getAmountVotes());
        assertEquals(candidate1.getAmountVotes(), getElectionResultsDtoResponse.getSelectedCandidate().getAmountVotes());
        assertEquals(candidate1.getLogin(), getElectionResultsDtoResponse.getSelectedCandidate().getLogin());
        assertTrue(dataBase.getVotedAgainstAll().isEmpty());
    }

    @Test
    public void testGetElectionResultsIfAmountVotesAgainstAllMoreThanVotesCandidate() throws VoterException {
        dataBase.getRankedCandidates().clear();
        RegisterVoterDtoRequest registerRequest1 = new RegisterVoterDtoRequest(0, "petr1","petrov1","12345678");
        assertEquals("petr1", registerRequest1.getFirstName());
        assertEquals("petrov1", registerRequest1.getLogin());
        assertEquals("12345678", registerRequest1.getPassword());
        String jsonRegisterRequest1 = gson.toJson(registerRequest1);
        String jsonRegisterResponse1  = voterService.registerVoter(jsonRegisterRequest1);

        RegisterVoterDtoResponse registerVoterDtoResponse1 = gson.fromJson(jsonRegisterResponse1, RegisterVoterDtoResponse.class);
        String resultRegisterJson1 = gson.toJson(registerVoterDtoResponse1);
        assertEquals(resultRegisterJson1, jsonRegisterResponse1);

        AddCandidateDtoRequest addCandidateRequest1 = new AddCandidateDtoRequest(registerRequest1.getId(), registerRequest1.getFirstName(), registerRequest1.getLogin(),
                                                                                 registerRequest1.getPassword(), registerVoterDtoResponse1.getToken());
        assertEquals(registerRequest1.getFirstName(), addCandidateRequest1.getFirstName());
        assertEquals(registerRequest1.getLogin(), addCandidateRequest1.getLogin());
        assertEquals(registerRequest1.getPassword(), addCandidateRequest1.getPassword());
        assertEquals(registerVoterDtoResponse1.getToken(), addCandidateRequest1.getToken());

        String jsonAddCandidateRequest1 = gson.toJson(addCandidateRequest1);
        String jsonAddCandidateResponse1  = candidateService.addCandidate(jsonAddCandidateRequest1);

        AddCandidateDtoResponse addCandidateDtoResponse1 = gson.fromJson(jsonAddCandidateResponse1, AddCandidateDtoResponse.class);
        String resultAddCandidateResponseJson1 = gson.toJson(addCandidateDtoResponse1);
        assertEquals(resultAddCandidateResponseJson1, jsonAddCandidateResponse1);

        RegisterVoterDtoRequest registerRequest2 = new RegisterVoterDtoRequest(1, "petr2","petrov2","12345678");
        assertEquals("petr2", registerRequest2.getFirstName());
        assertEquals("petrov2", registerRequest2.getLogin());
        assertEquals("12345678", registerRequest2.getPassword());
        String jsonRegisterRequest2 = gson.toJson(registerRequest2);
        String jsonRegisterResponse2  = voterService.registerVoter(jsonRegisterRequest2);

        RegisterVoterDtoResponse registerVoterDtoResponse2 = gson.fromJson(jsonRegisterResponse2, RegisterVoterDtoResponse.class);
        String resultRegisterJson2 = gson.toJson(registerVoterDtoResponse2);
        assertEquals(resultRegisterJson2, jsonRegisterResponse2);

        AddCandidateDtoRequest addCandidateRequest2 = new AddCandidateDtoRequest(registerRequest2.getId(), registerRequest2.getFirstName(), registerRequest2.getLogin(),
                                                                                 registerRequest2.getPassword(), registerVoterDtoResponse2.getToken());
        assertEquals(registerRequest2.getFirstName(), addCandidateRequest2.getFirstName());
        assertEquals(registerRequest2.getLogin(), addCandidateRequest2.getLogin());
        assertEquals(registerRequest2.getPassword(), addCandidateRequest2.getPassword());
        assertEquals(registerVoterDtoResponse2.getToken(), addCandidateRequest2.getToken());

        String jsonAddCandidateRequest2 = gson.toJson(addCandidateRequest2);
        String jsonAddCandidateResponse2  = candidateService.addCandidate(jsonAddCandidateRequest2);

        AddCandidateDtoResponse addCandidateDtoResponse2 = gson.fromJson(jsonAddCandidateResponse2, AddCandidateDtoResponse.class);
        String resultAddCandidateResponseJson2 = gson.toJson(addCandidateDtoResponse2);
        assertEquals(resultAddCandidateResponseJson2, jsonAddCandidateResponse2);

        RegisterVoterDtoRequest registerRequest3 = new RegisterVoterDtoRequest(2, "petr3","petrov3","12345678");
        assertEquals("petr3", registerRequest3.getFirstName());
        assertEquals("petrov3", registerRequest3.getLogin());
        assertEquals("12345678", registerRequest3.getPassword());
        String jsonRegisterRequest3 = gson.toJson(registerRequest3);
        String jsonRegisterResponse3  = voterService.registerVoter(jsonRegisterRequest3);

        RegisterVoterDtoResponse registerVoterDtoResponse3 = gson.fromJson(jsonRegisterResponse3, RegisterVoterDtoResponse.class);
        String resultRegisterJson3 = gson.toJson(registerVoterDtoResponse3);
        assertEquals(resultRegisterJson3, jsonRegisterResponse3);

        AddCandidateDtoRequest addCandidateRequest3 = new AddCandidateDtoRequest(registerRequest3.getId(), registerRequest3.getFirstName(), registerRequest3.getLogin(),
                                                                                 registerRequest3.getPassword(), registerVoterDtoResponse3.getToken());
        assertEquals(registerRequest3.getFirstName(), addCandidateRequest3.getFirstName());
        assertEquals(registerRequest3.getLogin(), addCandidateRequest3.getLogin());
        assertEquals(registerRequest3.getPassword(), addCandidateRequest3.getPassword());
        assertEquals(registerVoterDtoResponse3.getToken(), addCandidateRequest3.getToken());

        String jsonAddCandidateRequest3 = gson.toJson(addCandidateRequest3);
        String jsonAddCandidateResponse3  = candidateService.addCandidate(jsonAddCandidateRequest3);

        AddCandidateDtoResponse addCandidateDtoResponse3 = gson.fromJson(jsonAddCandidateResponse3, AddCandidateDtoResponse.class);
        String resultAddCandidateResponseJson3 = gson.toJson(addCandidateDtoResponse3);
        assertEquals(resultAddCandidateResponseJson3, jsonAddCandidateResponse3);

        RegisterVoterDtoRequest registerRequest4 = new RegisterVoterDtoRequest(3, "boris1","borisov1","12345678");
        assertEquals("boris1", registerRequest4.getFirstName());
        assertEquals("borisov1", registerRequest4.getLogin());
        assertEquals("12345678", registerRequest4.getPassword());
        String jsonRegisterRequest4 = gson.toJson(registerRequest4);
        String jsonRegisterResponse4  = voterService.registerVoter(jsonRegisterRequest4);

        RegisterVoterDtoResponse registerVoterDtoResponse4 = gson.fromJson(jsonRegisterResponse4, RegisterVoterDtoResponse.class);
        String resultRegisterJson4 = gson.toJson(registerVoterDtoResponse4);
        assertEquals(resultRegisterJson4, jsonRegisterResponse4);

        RegisterVoterDtoRequest registerRequest5 = new RegisterVoterDtoRequest(4, "boris2","borisov2","12345678");
        assertEquals("boris2", registerRequest5.getFirstName());
        assertEquals("borisov2", registerRequest5.getLogin());
        assertEquals("12345678", registerRequest5.getPassword());
        String jsonRegisterRequest5 = gson.toJson(registerRequest5);
        String jsonRegisterResponse5  = voterService.registerVoter(jsonRegisterRequest5);

        RegisterVoterDtoResponse registerVoterDtoResponse5 = gson.fromJson(jsonRegisterResponse5, RegisterVoterDtoResponse.class);
        String resultRegisterJson5 = gson.toJson(registerVoterDtoResponse5);
        assertEquals(resultRegisterJson5, jsonRegisterResponse5);

        RegisterVoterDtoRequest registerRequest6 = new RegisterVoterDtoRequest(5, "boris3","borisov3","12345678");
        assertEquals("boris3", registerRequest6.getFirstName());
        assertEquals("borisov3", registerRequest6.getLogin());
        assertEquals("12345678", registerRequest6.getPassword());
        String jsonRegisterRequest6 = gson.toJson(registerRequest6);
        String jsonRegisterResponse6  = voterService.registerVoter(jsonRegisterRequest6);

        RegisterVoterDtoResponse registerVoterDtoResponse6 = gson.fromJson(jsonRegisterResponse6, RegisterVoterDtoResponse.class);
        String resultRegisterJson6 = gson.toJson(registerVoterDtoResponse6);
        assertEquals(resultRegisterJson6, jsonRegisterResponse6);

        RegisterVoterDtoRequest registerRequest7 = new RegisterVoterDtoRequest(6, "boris4","borisov4","12345678");
        assertEquals("boris4", registerRequest7.getFirstName());
        assertEquals("borisov4", registerRequest7.getLogin());
        assertEquals("12345678", registerRequest7.getPassword());
        String jsonRegisterRequest7 = gson.toJson(registerRequest7);
        String jsonRegisterResponse7  = voterService.registerVoter(jsonRegisterRequest7);

        RegisterVoterDtoResponse registerVoterDtoResponse7 = gson.fromJson(jsonRegisterResponse7, RegisterVoterDtoResponse.class);
        String resultRegisterJson7 = gson.toJson(registerVoterDtoResponse7);
        assertEquals(resultRegisterJson7, jsonRegisterResponse7);

        RegisterVoterDtoRequest registerRequest8 = new RegisterVoterDtoRequest(7, "boris5","borisov5","12345678");
        assertEquals("boris5", registerRequest8.getFirstName());
        assertEquals("borisov5", registerRequest8.getLogin());
        assertEquals("12345678", registerRequest8.getPassword());
        String jsonRegisterRequest8 = gson.toJson(registerRequest8);
        String jsonRegisterResponse8  = voterService.registerVoter(jsonRegisterRequest8);

        RegisterVoterDtoResponse registerVoterDtoResponse8 = gson.fromJson(jsonRegisterResponse8, RegisterVoterDtoResponse.class);
        String resultRegisterJson8 = gson.toJson(registerVoterDtoResponse8);
        assertEquals(resultRegisterJson8, jsonRegisterResponse8);

        String candidateToken1 = addCandidateDtoResponse1.getToken();
        String candidateToken2 = addCandidateDtoResponse2.getToken();
        String candidateToken3 = addCandidateDtoResponse3.getToken();

        String voterToken1 = registerVoterDtoResponse4.getToken();
        String voterToken2 = registerVoterDtoResponse5.getToken();
        String voterToken3 = registerVoterDtoResponse6.getToken();
        String voterToken4 = registerVoterDtoResponse7.getToken();
        String voterToken5 = registerVoterDtoResponse8.getToken();

        AddOfferDtoRequest addOfferDtoRequest1 = new AddOfferDtoRequest(voterToken1,3, candidateToken1,0, "build a bridge across the river");
        assertEquals(voterToken1, addOfferDtoRequest1.getVoterToken());
        assertEquals(candidateToken1, addOfferDtoRequest1.getCandidateToken());
        assertEquals("build a bridge across the river", addOfferDtoRequest1.getOfferDescription());

        Map<String,Voter> tokensAndVoters = dataBase.getTokensAndVoters();
        addOfferDtoRequest1.setTokensAndVoters(tokensAndVoters);
        assertEquals(8, addOfferDtoRequest1.getTokensAndVoters().size());

        String jsonAddOfferRequest1 = gson.toJson(addOfferDtoRequest1);
        String jsonAddOfferResponse1 = offerService.addOffer(jsonAddOfferRequest1);
        assertEquals(gson.toJson(""), jsonAddOfferResponse1);

        AddOfferDtoRequest addOfferDtoRequest2 = new AddOfferDtoRequest(voterToken2,4, candidateToken2,1, "repair the road");
        assertEquals(voterToken2, addOfferDtoRequest2.getVoterToken());
        assertEquals(candidateToken2, addOfferDtoRequest2.getCandidateToken());
        assertEquals("repair the road", addOfferDtoRequest2.getOfferDescription());

        addOfferDtoRequest2.setTokensAndVoters(tokensAndVoters);
        assertEquals(8, addOfferDtoRequest2.getTokensAndVoters().size());

        String jsonAddOfferRequest2 = gson.toJson(addOfferDtoRequest2);
        String jsonAddOfferResponse2 = offerService.addOffer(jsonAddOfferRequest2);
        assertEquals(gson.toJson(""), jsonAddOfferResponse2);

        AddOfferDtoRequest addOfferDtoRequest3 = new AddOfferDtoRequest(voterToken3,5, candidateToken3,2, "increase the minimum salary");
        assertEquals(voterToken3, addOfferDtoRequest3.getVoterToken());
        assertEquals(candidateToken3, addOfferDtoRequest3.getCandidateToken());
        assertEquals("increase the minimum salary", addOfferDtoRequest3.getOfferDescription());

        addOfferDtoRequest3.setTokensAndVoters(tokensAndVoters);
        assertEquals(8, addOfferDtoRequest3.getTokensAndVoters().size());

        String jsonAddOfferRequest3 = gson.toJson(addOfferDtoRequest3);
        String jsonAddOfferResponse3 = offerService.addOffer(jsonAddOfferRequest3);
        assertEquals(gson.toJson(""), jsonAddOfferResponse3);

        VoteForCandidateDtoRequest voteForCandidateDtoRequest1 = new VoteForCandidateDtoRequest(3,0,voterToken1, candidateToken1);
        assertEquals(voterToken1, voteForCandidateDtoRequest1.getVotingToken());
        assertEquals(candidateToken1, voteForCandidateDtoRequest1.getCandidateToken());
        voteForCandidateDtoRequest1.setTokensAndVoters(tokensAndVoters);
        assertEquals(8, voteForCandidateDtoRequest1.getTokensAndVoters().size());

        String jsonVoterForCandidateRequest1 = gson.toJson(voteForCandidateDtoRequest1);
        String jsonVoterForCandidateResponse1 = electionService.voteForCandidate(jsonVoterForCandidateRequest1);
        assertEquals(gson.toJson(""), jsonVoterForCandidateResponse1);

        VoteForCandidateDtoRequest voteForCandidateDtoRequest2 = new VoteForCandidateDtoRequest(4,1,voterToken2, candidateToken2);
        assertEquals(voterToken2, voteForCandidateDtoRequest2.getVotingToken());
        assertEquals(candidateToken2, voteForCandidateDtoRequest2.getCandidateToken());
        voteForCandidateDtoRequest2.setTokensAndVoters(tokensAndVoters);
        assertEquals(8, voteForCandidateDtoRequest2.getTokensAndVoters().size());

        String jsonVoterForCandidateRequest2 = gson.toJson(voteForCandidateDtoRequest2);
        String jsonVoterForCandidateResponse2 = electionService.voteForCandidate(jsonVoterForCandidateRequest2);
        assertEquals(gson.toJson(""), jsonVoterForCandidateResponse2);

        VoteForCandidateDtoRequest voteForCandidateDtoRequest3 = new VoteForCandidateDtoRequest(5,2,voterToken3, candidateToken3);
        assertEquals(voterToken3, voteForCandidateDtoRequest3.getVotingToken());
        assertEquals(candidateToken3, voteForCandidateDtoRequest3.getCandidateToken());
        voteForCandidateDtoRequest3.setTokensAndVoters(tokensAndVoters);
        assertEquals(8, voteForCandidateDtoRequest3.getTokensAndVoters().size());

        String jsonVoterForCandidateRequest3 = gson.toJson(voteForCandidateDtoRequest3);
        String jsonVoterForCandidateResponse3 = electionService.voteForCandidate(jsonVoterForCandidateRequest3);
        assertEquals(gson.toJson(""), jsonVoterForCandidateResponse3);

        VoteForCandidateDtoRequest voteForCandidateDtoRequest4 = new VoteForCandidateDtoRequest(6,2,voterToken4, candidateToken3);
        assertEquals(voterToken4, voteForCandidateDtoRequest4.getVotingToken());
        assertEquals(candidateToken3, voteForCandidateDtoRequest4.getCandidateToken());
        voteForCandidateDtoRequest4.setTokensAndVoters(tokensAndVoters);
        assertEquals(8, voteForCandidateDtoRequest4.getTokensAndVoters().size());

        String jsonVoterForCandidateRequest4 = gson.toJson(voteForCandidateDtoRequest4);
        String jsonVoterForCandidateResponse4 = electionService.voteForCandidate(jsonVoterForCandidateRequest4);
        assertEquals(gson.toJson(""), jsonVoterForCandidateResponse4);

        Voter voter5 = dataBase.getVoterById(registerRequest8.getId());
        VoteAgainstAllDtoRequest voteAgainstAllDtoRequest5 = new VoteAgainstAllDtoRequest(voterToken5, voter5);
        assertEquals(voterToken5, voteAgainstAllDtoRequest5.getVotingToken());
        voteAgainstAllDtoRequest5.setTokensAndVoters(dataBase.getTokensAndVoters());
        assertEquals(8, voteAgainstAllDtoRequest5.getTokensAndVoters().size());

        String jsonVoteAgainstAllRequest5 = gson.toJson(voteAgainstAllDtoRequest5);
        String jsonVoteAgainstAllResponse5 = electionService.voteAgainstAll(jsonVoteAgainstAllRequest5);
        assertEquals(gson.toJson(""), jsonVoteAgainstAllResponse5);

        Candidate candidate1 = dataBase.getCandidateById(addCandidateRequest1.getId());
        VoteAgainstAllDtoRequest voteAgainstAllDtoRequest6 = new VoteAgainstAllDtoRequest(candidateToken1, candidate1);
        assertEquals(candidateToken1, voteAgainstAllDtoRequest6.getVotingToken());
        voteAgainstAllDtoRequest6.setTokensAndVoters(dataBase.getTokensAndVoters());
        assertEquals(8, voteAgainstAllDtoRequest6.getTokensAndVoters().size());

        String jsonVoteAgainstAllRequest6 = gson.toJson(voteAgainstAllDtoRequest6);
        String jsonVoteAgainstAllResponse6 = electionService.voteAgainstAll(jsonVoteAgainstAllRequest6);
        assertEquals(gson.toJson(""), jsonVoteAgainstAllResponse6);

        Candidate candidate2 = dataBase.getCandidateById(addCandidateRequest2.getId());
        VoteAgainstAllDtoRequest voteAgainstAllDtoRequest7 = new VoteAgainstAllDtoRequest(candidateToken2, candidate2);
        assertEquals(candidateToken2, voteAgainstAllDtoRequest7.getVotingToken());
        voteAgainstAllDtoRequest7.setTokensAndVoters(dataBase.getTokensAndVoters());
        assertEquals(8, voteAgainstAllDtoRequest7.getTokensAndVoters().size());

        String jsonVoteAgainstAllRequest7 = gson.toJson(voteAgainstAllDtoRequest7);
        String jsonVoteAgainstAllResponse7 = electionService.voteAgainstAll(jsonVoteAgainstAllRequest7);
        assertEquals(gson.toJson(""), jsonVoteAgainstAllResponse7);

        Candidate candidate3 = dataBase.getCandidateById(addCandidateRequest3.getId());
        VoteAgainstAllDtoRequest voteAgainstAllDtoRequest8 = new VoteAgainstAllDtoRequest(candidateToken3,candidate3);
        assertEquals(candidateToken3, voteAgainstAllDtoRequest8.getVotingToken());
        voteAgainstAllDtoRequest8.setTokensAndVoters(dataBase.getTokensAndVoters());
        assertEquals(8, voteAgainstAllDtoRequest8.getTokensAndVoters().size());

        String jsonVoteAgainstAllRequest8 = gson.toJson(voteAgainstAllDtoRequest8);
        String jsonVoteAgainstAllResponse8 = electionService.voteAgainstAll(jsonVoteAgainstAllRequest8);
        assertEquals(gson.toJson(""), jsonVoteAgainstAllResponse8);

        GetElectionResultsDtoRequest getElectionResultsDtoRequest = new GetElectionResultsDtoRequest(voterToken1);
        assertEquals(voterToken1, getElectionResultsDtoRequest.getToken());
        getElectionResultsDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(8, getElectionResultsDtoRequest.getTokensAndVoters().size());

        String jsonGetElectionResultsRequest = gson.toJson(getElectionResultsDtoRequest);
        String jsonGetElectionResultsResponse = electionService.getElectionResults(jsonGetElectionResultsRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonGetElectionResultsResponse);

        assertEquals(1, candidate1.getAmountVotes());
        assertEquals(1, candidate2.getAmountVotes());
        assertEquals(2, candidate3.getAmountVotes());
        assertEquals(4, dataBase.getVotedAgainstAll().size());
        assertTrue(candidate3.getAmountVotes() > candidate1.getAmountVotes());
        assertTrue(candidate3.getAmountVotes() > candidate2.getAmountVotes());
        assertTrue(dataBase.getVotedAgainstAll().size() > candidate3.getAmountVotes());
    }

    @Test
    public void testGetElectionResultsIfEveryoneVotedAgainstAll() throws VoterException {
        dataBase.getRankedCandidates().clear();

        RegisterVoterDtoRequest registerRequest1 = new RegisterVoterDtoRequest(0,"petr1","petrov1","12345678");
        assertEquals("petr1", registerRequest1.getFirstName());
        assertEquals("petrov1", registerRequest1.getLogin());
        assertEquals("12345678", registerRequest1.getPassword());
        String jsonRegisterRequest1 = gson.toJson(registerRequest1);
        String jsonRegisterResponse1  = voterService.registerVoter(jsonRegisterRequest1);

        RegisterVoterDtoResponse registerVoterDtoResponse1 = gson.fromJson(jsonRegisterResponse1, RegisterVoterDtoResponse.class);
        String resultRegisterJson1 = gson.toJson(registerVoterDtoResponse1);
        assertEquals(resultRegisterJson1, jsonRegisterResponse1);

        AddCandidateDtoRequest addCandidateRequest1 = new AddCandidateDtoRequest(registerRequest1.getId(), registerRequest1.getFirstName(), registerRequest1.getLogin(),
                registerRequest1.getPassword(), registerVoterDtoResponse1.getToken());
        assertEquals(registerRequest1.getFirstName(), addCandidateRequest1.getFirstName());
        assertEquals(registerRequest1.getLogin(), addCandidateRequest1.getLogin());
        assertEquals(registerRequest1.getPassword(), addCandidateRequest1.getPassword());
        assertEquals(registerVoterDtoResponse1.getToken(), addCandidateRequest1.getToken());

        String jsonAddCandidateRequest1 = gson.toJson(addCandidateRequest1);
        String jsonAddCandidateResponse1  = candidateService.addCandidate(jsonAddCandidateRequest1);

        AddCandidateDtoResponse addCandidateDtoResponse1 = gson.fromJson(jsonAddCandidateResponse1, AddCandidateDtoResponse.class);
        String resultAddCandidateResponseJson1 = gson.toJson(addCandidateDtoResponse1);
        assertEquals(resultAddCandidateResponseJson1, jsonAddCandidateResponse1);

        RegisterVoterDtoRequest registerRequest2 = new RegisterVoterDtoRequest(1, "petr2","petrov2","12345678");
        assertEquals("petr2", registerRequest2.getFirstName());
        assertEquals("petrov2", registerRequest2.getLogin());
        assertEquals("12345678", registerRequest2.getPassword());
        String jsonRegisterRequest2 = gson.toJson(registerRequest2);
        String jsonRegisterResponse2  = voterService.registerVoter(jsonRegisterRequest2);

        RegisterVoterDtoResponse registerVoterDtoResponse2 = gson.fromJson(jsonRegisterResponse2, RegisterVoterDtoResponse.class);
        String resultRegisterJson2 = gson.toJson(registerVoterDtoResponse2);
        assertEquals(resultRegisterJson2, jsonRegisterResponse2);

        AddCandidateDtoRequest addCandidateRequest2 = new AddCandidateDtoRequest(registerRequest2.getId(), registerRequest2.getFirstName(), registerRequest2.getLogin(),
                registerRequest2.getPassword(), registerVoterDtoResponse2.getToken());
        assertEquals(registerRequest2.getFirstName(), addCandidateRequest2.getFirstName());
        assertEquals(registerRequest2.getLogin(), addCandidateRequest2.getLogin());
        assertEquals(registerRequest2.getPassword(), addCandidateRequest2.getPassword());
        assertEquals(registerVoterDtoResponse2.getToken(), addCandidateRequest2.getToken());

        String jsonAddCandidateRequest2 = gson.toJson(addCandidateRequest2);
        String jsonAddCandidateResponse2  = candidateService.addCandidate(jsonAddCandidateRequest2);

        AddCandidateDtoResponse addCandidateDtoResponse2 = gson.fromJson(jsonAddCandidateResponse2, AddCandidateDtoResponse.class);
        String resultAddCandidateResponseJson2 = gson.toJson(addCandidateDtoResponse2);
        assertEquals(resultAddCandidateResponseJson2, jsonAddCandidateResponse2);

        RegisterVoterDtoRequest registerRequest3 = new RegisterVoterDtoRequest(2, "petr3","petrov3","12345678");
        assertEquals("petr3", registerRequest3.getFirstName());
        assertEquals("petrov3", registerRequest3.getLogin());
        assertEquals("12345678", registerRequest3.getPassword());
        String jsonRegisterRequest3 = gson.toJson(registerRequest3);
        String jsonRegisterResponse3  = voterService.registerVoter(jsonRegisterRequest3);

        RegisterVoterDtoResponse registerVoterDtoResponse3 = gson.fromJson(jsonRegisterResponse3, RegisterVoterDtoResponse.class);
        String resultRegisterJson3 = gson.toJson(registerVoterDtoResponse3);
        assertEquals(resultRegisterJson3, jsonRegisterResponse3);

        AddCandidateDtoRequest addCandidateRequest3 = new AddCandidateDtoRequest(registerRequest3.getId(), registerRequest3.getFirstName(), registerRequest3.getLogin(),
                registerRequest3.getPassword(), registerVoterDtoResponse3.getToken());
        assertEquals(registerRequest3.getFirstName(), addCandidateRequest3.getFirstName());
        assertEquals(registerRequest3.getLogin(), addCandidateRequest3.getLogin());
        assertEquals(registerRequest3.getPassword(), addCandidateRequest3.getPassword());
        assertEquals(registerVoterDtoResponse3.getToken(), addCandidateRequest3.getToken());

        String jsonAddCandidateRequest3 = gson.toJson(addCandidateRequest3);
        String jsonAddCandidateResponse3  = candidateService.addCandidate(jsonAddCandidateRequest3);

        AddCandidateDtoResponse addCandidateDtoResponse3 = gson.fromJson(jsonAddCandidateResponse3, AddCandidateDtoResponse.class);
        String resultAddCandidateResponseJson3 = gson.toJson(addCandidateDtoResponse3);
        assertEquals(resultAddCandidateResponseJson3, jsonAddCandidateResponse3);

        RegisterVoterDtoRequest registerRequest4 = new RegisterVoterDtoRequest(3, "boris1","borisov1","12345678");
        assertEquals("boris1", registerRequest4.getFirstName());
        assertEquals("borisov1", registerRequest4.getLogin());
        assertEquals("12345678", registerRequest4.getPassword());
        String jsonRegisterRequest4 = gson.toJson(registerRequest4);
        String jsonRegisterResponse4  = voterService.registerVoter(jsonRegisterRequest4);

        RegisterVoterDtoResponse registerVoterDtoResponse4 = gson.fromJson(jsonRegisterResponse4, RegisterVoterDtoResponse.class);
        String resultRegisterJson4 = gson.toJson(registerVoterDtoResponse4);
        assertEquals(resultRegisterJson4, jsonRegisterResponse4);

        RegisterVoterDtoRequest registerRequest5 = new RegisterVoterDtoRequest(4, "boris2","borisov2","12345678");
        assertEquals("boris2", registerRequest5.getFirstName());
        assertEquals("borisov2", registerRequest5.getLogin());
        assertEquals("12345678", registerRequest5.getPassword());
        String jsonRegisterRequest5 = gson.toJson(registerRequest5);
        String jsonRegisterResponse5  = voterService.registerVoter(jsonRegisterRequest5);

        RegisterVoterDtoResponse registerVoterDtoResponse5 = gson.fromJson(jsonRegisterResponse5, RegisterVoterDtoResponse.class);
        String resultRegisterJson5 = gson.toJson(registerVoterDtoResponse5);
        assertEquals(resultRegisterJson5, jsonRegisterResponse5);

        RegisterVoterDtoRequest registerRequest6 = new RegisterVoterDtoRequest(5, "boris3","borisov3","12345678");
        assertEquals("boris3", registerRequest6.getFirstName());
        assertEquals("borisov3", registerRequest6.getLogin());
        assertEquals("12345678", registerRequest6.getPassword());
        String jsonRegisterRequest6 = gson.toJson(registerRequest6);
        String jsonRegisterResponse6  = voterService.registerVoter(jsonRegisterRequest6);

        RegisterVoterDtoResponse registerVoterDtoResponse6 = gson.fromJson(jsonRegisterResponse6, RegisterVoterDtoResponse.class);
        String resultRegisterJson6 = gson.toJson(registerVoterDtoResponse6);
        assertEquals(resultRegisterJson6, jsonRegisterResponse6);

        RegisterVoterDtoRequest registerRequest7 = new RegisterVoterDtoRequest(6, "boris4","borisov4","12345678");
        assertEquals("boris4", registerRequest7.getFirstName());
        assertEquals("borisov4", registerRequest7.getLogin());
        assertEquals("12345678", registerRequest7.getPassword());
        String jsonRegisterRequest7 = gson.toJson(registerRequest7);
        String jsonRegisterResponse7  = voterService.registerVoter(jsonRegisterRequest7);

        RegisterVoterDtoResponse registerVoterDtoResponse7 = gson.fromJson(jsonRegisterResponse7, RegisterVoterDtoResponse.class);
        String resultRegisterJson7 = gson.toJson(registerVoterDtoResponse7);
        assertEquals(resultRegisterJson7, jsonRegisterResponse7);

        RegisterVoterDtoRequest registerRequest8 = new RegisterVoterDtoRequest(7, "boris5","borisov5","12345678");
        assertEquals("boris5", registerRequest8.getFirstName());
        assertEquals("borisov5", registerRequest8.getLogin());
        assertEquals("12345678", registerRequest8.getPassword());
        String jsonRegisterRequest8 = gson.toJson(registerRequest8);
        String jsonRegisterResponse8  = voterService.registerVoter(jsonRegisterRequest8);

        RegisterVoterDtoResponse registerVoterDtoResponse8 = gson.fromJson(jsonRegisterResponse8, RegisterVoterDtoResponse.class);
        String resultRegisterJson8 = gson.toJson(registerVoterDtoResponse8);
        assertEquals(resultRegisterJson8, jsonRegisterResponse8);

        String candidateToken1 = addCandidateDtoResponse1.getToken();
        String candidateToken2 = addCandidateDtoResponse2.getToken();
        String candidateToken3 = addCandidateDtoResponse3.getToken();

        String voterToken1 = registerVoterDtoResponse4.getToken();
        String voterToken2 = registerVoterDtoResponse5.getToken();
        String voterToken3 = registerVoterDtoResponse6.getToken();
        String voterToken4 = registerVoterDtoResponse7.getToken();
        String voterToken5 = registerVoterDtoResponse8.getToken();

        AddOfferDtoRequest addOfferDtoRequest1 = new AddOfferDtoRequest(voterToken1, 3, candidateToken1,0, "build a bridge across the river");
        assertEquals(voterToken1, addOfferDtoRequest1.getVoterToken());
        assertEquals(candidateToken1, addOfferDtoRequest1.getCandidateToken());
        assertEquals("build a bridge across the river", addOfferDtoRequest1.getOfferDescription());

        Map<String, Voter> tokensAndVoters = dataBase.getTokensAndVoters();
        addOfferDtoRequest1.setTokensAndVoters(tokensAndVoters);
        assertEquals(8, addOfferDtoRequest1.getTokensAndVoters().size());

        String jsonAddOfferRequest1 = gson.toJson(addOfferDtoRequest1);
        String jsonAddOfferResponse1 = offerService.addOffer(jsonAddOfferRequest1);
        assertEquals(gson.toJson(""), jsonAddOfferResponse1);

        AddOfferDtoRequest addOfferDtoRequest2 = new AddOfferDtoRequest(voterToken2, 4,candidateToken2,1, "repair the road");
        assertEquals(voterToken2, addOfferDtoRequest2.getVoterToken());
        assertEquals(candidateToken2, addOfferDtoRequest2.getCandidateToken());
        assertEquals("repair the road", addOfferDtoRequest2.getOfferDescription());

        addOfferDtoRequest2.setTokensAndVoters(tokensAndVoters);
        assertEquals(8, addOfferDtoRequest2.getTokensAndVoters().size());

        String jsonAddOfferRequest2 = gson.toJson(addOfferDtoRequest2);
        String jsonAddOfferResponse2 = offerService.addOffer(jsonAddOfferRequest2);
        assertEquals(gson.toJson(""), jsonAddOfferResponse2);

        AddOfferDtoRequest addOfferDtoRequest3 = new AddOfferDtoRequest(voterToken2,4, candidateToken3,2, "increase the minimum salary");
        assertEquals(voterToken2, addOfferDtoRequest3.getVoterToken());
        assertEquals(candidateToken3, addOfferDtoRequest3.getCandidateToken());
        assertEquals("increase the minimum salary", addOfferDtoRequest3.getOfferDescription());

        addOfferDtoRequest3.setTokensAndVoters(tokensAndVoters);
        assertEquals(8, addOfferDtoRequest3.getTokensAndVoters().size());

        String jsonAddOfferRequest3 = gson.toJson(addOfferDtoRequest3);
        String jsonAddOfferResponse3 = offerService.addOffer(jsonAddOfferRequest3);
        assertEquals(gson.toJson(""), jsonAddOfferResponse3);

        Voter voter1 = dataBase.getVoterById(registerRequest4.getId());
        VoteAgainstAllDtoRequest voteAgainstAllDtoRequest1 = new VoteAgainstAllDtoRequest(voterToken1, voter1);
        assertEquals(voterToken1, voteAgainstAllDtoRequest1.getVotingToken());
        voteAgainstAllDtoRequest1.setTokensAndVoters(dataBase.getTokensAndVoters());
        assertEquals(8, voteAgainstAllDtoRequest1.getTokensAndVoters().size());

        String jsonVoteAgainstAllRequest1 = gson.toJson(voteAgainstAllDtoRequest1);
        String jsonVoteAgainstAllResponse1 = electionService.voteAgainstAll(jsonVoteAgainstAllRequest1);
        assertEquals(gson.toJson(""), jsonVoteAgainstAllResponse1);

        Voter voter2 = dataBase.getVoterById(registerRequest5.getId());
        VoteAgainstAllDtoRequest voteAgainstAllDtoRequest2 = new VoteAgainstAllDtoRequest(voterToken2,voter2);
        assertEquals(voterToken2, voteAgainstAllDtoRequest2.getVotingToken());
        voteAgainstAllDtoRequest2.setTokensAndVoters(dataBase.getTokensAndVoters());
        assertEquals(8, voteAgainstAllDtoRequest2.getTokensAndVoters().size());

        String jsonVoteAgainstAllRequest2 = gson.toJson(voteAgainstAllDtoRequest2);
        String jsonVoteAgainstAllResponse2 = electionService.voteAgainstAll(jsonVoteAgainstAllRequest2);
        assertEquals(gson.toJson(""), jsonVoteAgainstAllResponse2);

        Voter voter3 = dataBase.getVoterById(registerRequest6.getId());
        VoteAgainstAllDtoRequest voteAgainstAllDtoRequest3 = new VoteAgainstAllDtoRequest(voterToken3,voter3);
        assertEquals(voterToken3, voteAgainstAllDtoRequest3.getVotingToken());
        voteAgainstAllDtoRequest3.setTokensAndVoters(dataBase.getTokensAndVoters());
        assertEquals(8, voteAgainstAllDtoRequest2.getTokensAndVoters().size());

        String jsonVoteAgainstAllRequest3 = gson.toJson(voteAgainstAllDtoRequest3);
        String jsonVoteAgainstAllResponse3 = electionService.voteAgainstAll(jsonVoteAgainstAllRequest3);
        assertEquals(gson.toJson(""), jsonVoteAgainstAllResponse3);

        Voter voter4 = dataBase.getVoterById(registerRequest7.getId());
        VoteAgainstAllDtoRequest voteAgainstAllDtoRequest4 = new VoteAgainstAllDtoRequest(voterToken4,voter4);
        assertEquals(voterToken4, voteAgainstAllDtoRequest4.getVotingToken());
        voteAgainstAllDtoRequest4.setTokensAndVoters(dataBase.getTokensAndVoters());
        assertEquals(8, voteAgainstAllDtoRequest4.getTokensAndVoters().size());

        String jsonVoteAgainstAllRequest4 = gson.toJson(voteAgainstAllDtoRequest4);
        String jsonVoteAgainstAllResponse4 = electionService.voteAgainstAll(jsonVoteAgainstAllRequest4);
        assertEquals(gson.toJson(""), jsonVoteAgainstAllResponse4);

        Voter voter5 = dataBase.getVoterById(registerRequest8.getId());
        VoteAgainstAllDtoRequest voteAgainstAllDtoRequest5 = new VoteAgainstAllDtoRequest(voterToken5,voter5);
        assertEquals(voterToken5, voteAgainstAllDtoRequest5.getVotingToken());
        voteAgainstAllDtoRequest5.setTokensAndVoters(dataBase.getTokensAndVoters());
        assertEquals(8, voteAgainstAllDtoRequest5.getTokensAndVoters().size());

        String jsonVoteAgainstAllRequest5 = gson.toJson(voteAgainstAllDtoRequest5);
        String jsonVoteAgainstAllResponse5 = electionService.voteAgainstAll(jsonVoteAgainstAllRequest5);
        assertEquals(gson.toJson(""), jsonVoteAgainstAllResponse5);

        Candidate candidate1 = dataBase.getCandidateById(registerRequest1.getId());
        VoteAgainstAllDtoRequest voteAgainstAllDtoRequest6 = new VoteAgainstAllDtoRequest(candidateToken1, candidate1);
        assertEquals(candidateToken1, voteAgainstAllDtoRequest6.getVotingToken());
        voteAgainstAllDtoRequest6.setTokensAndVoters(dataBase.getTokensAndVoters());
        assertEquals(8, voteAgainstAllDtoRequest6.getTokensAndVoters().size());

        String jsonVoteAgainstAllRequest6 = gson.toJson(voteAgainstAllDtoRequest6);
        String jsonVoteAgainstAllResponse6 = electionService.voteAgainstAll(jsonVoteAgainstAllRequest6);
        assertEquals(gson.toJson(""), jsonVoteAgainstAllResponse6);

        Candidate candidate2 = dataBase.getCandidateById(registerRequest2.getId());
        VoteAgainstAllDtoRequest voteAgainstAllDtoRequest7 = new VoteAgainstAllDtoRequest(candidateToken2,candidate2);
        assertEquals(candidateToken2, voteAgainstAllDtoRequest7.getVotingToken());
        voteAgainstAllDtoRequest7.setTokensAndVoters(dataBase.getTokensAndVoters());
        assertEquals(8, voteAgainstAllDtoRequest7.getTokensAndVoters().size());

        String jsonVoteAgainstAllRequest7 = gson.toJson(voteAgainstAllDtoRequest7);
        String jsonVoteAgainstAllResponse7 = electionService.voteAgainstAll(jsonVoteAgainstAllRequest7);
        assertEquals(gson.toJson(""), jsonVoteAgainstAllResponse7);

        Candidate candidate3 = dataBase.getCandidateById(registerRequest3.getId());
        VoteAgainstAllDtoRequest voteAgainstAllDtoRequest8 = new VoteAgainstAllDtoRequest(candidateToken3,candidate3);
        assertEquals(candidateToken3, voteAgainstAllDtoRequest8.getVotingToken());
        voteAgainstAllDtoRequest8.setTokensAndVoters(dataBase.getTokensAndVoters());
        assertEquals(8, voteAgainstAllDtoRequest8.getTokensAndVoters().size());

        String jsonVoteAgainstAllRequest8 = gson.toJson(voteAgainstAllDtoRequest8);
        String jsonVoteAgainstAllResponse8 = electionService.voteAgainstAll(jsonVoteAgainstAllRequest8);
        assertEquals(gson.toJson(""), jsonVoteAgainstAllResponse8);

        GetElectionResultsDtoRequest getElectionResultsDtoRequest = new GetElectionResultsDtoRequest(voterToken1);
        assertEquals(voterToken1, getElectionResultsDtoRequest.getToken());
        getElectionResultsDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(8, getElectionResultsDtoRequest.getTokensAndVoters().size());

        String jsonGetElectionResultsRequest = gson.toJson(getElectionResultsDtoRequest);
        String jsonGetElectionResultsResponse = electionService.getElectionResults(jsonGetElectionResultsRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonGetElectionResultsResponse);

        assertEquals(0, candidate1.getAmountVotes());
        assertEquals(0, candidate2.getAmountVotes());
        assertEquals(0, candidate3.getAmountVotes());
        assertEquals(8, dataBase.getVotedAgainstAll().size());
        assertTrue(dataBase.getRankedCandidates().isEmpty());
    }

    @Test
    public void testGetElectionResultsIfVotingHasNotStarted() {
        dataBase.getRankedCandidates().clear();

        RegisterVoterDtoRequest registerRequest1 = new RegisterVoterDtoRequest(0, "petr1","petrov1","12345678");
        assertEquals("petr1", registerRequest1.getFirstName());
        assertEquals("petrov1", registerRequest1.getLogin());
        assertEquals("12345678", registerRequest1.getPassword());
        String jsonRegisterRequest1 = gson.toJson(registerRequest1);
        String jsonRegisterResponse1  = voterService.registerVoter(jsonRegisterRequest1);

        RegisterVoterDtoResponse registerVoterDtoResponse1 = gson.fromJson(jsonRegisterResponse1, RegisterVoterDtoResponse.class);
        String resultRegisterJson1 = gson.toJson(registerVoterDtoResponse1);
        assertEquals(resultRegisterJson1, jsonRegisterResponse1);

        String voterToken1 = registerVoterDtoResponse1.getToken();

        GetElectionResultsDtoRequest getElectionResultsDtoRequest = new GetElectionResultsDtoRequest(voterToken1);
        assertEquals(voterToken1, getElectionResultsDtoRequest.getToken());
        getElectionResultsDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
        assertEquals(1, getElectionResultsDtoRequest.getTokensAndVoters().size());

        String jsonGetElectionResultsRequest = gson.toJson(getElectionResultsDtoRequest);
        String jsonGetElectionResultsResponse = electionService.getElectionResults(jsonGetElectionResultsRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonGetElectionResultsResponse);

        assertTrue(dataBase.getVotedAgainstAll().isEmpty());
        assertTrue(dataBase.getRankedCandidates().isEmpty());
    }
}
