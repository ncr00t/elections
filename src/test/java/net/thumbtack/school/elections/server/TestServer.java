package net.thumbtack.school.elections.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.thumbtack.school.elections.database.DataBase;
import net.thumbtack.school.elections.exception.VoterErrorCode;
import net.thumbtack.school.elections.exception.VoterException;
import net.thumbtack.school.elections.model.Candidate;
import net.thumbtack.school.elections.model.Voter;
import net.thumbtack.school.elections.request.*;
import net.thumbtack.school.elections.response.*;
import net.thumbtack.school.elections.service.GenerateTokenService;
import org.apache.commons.cli.ParseException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import java.io.IOException;
import java.util.Map;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.*;

public class TestServer {

    private static Gson gson;
    private static DataBase dataBase;

    @BeforeClass
    public static void setUpClass(){
        gson = new Gson();
        dataBase = DataBase.getDataBase();
    }

    @Before
    public void setUp(){
        dataBase.getRankedCandidates().clear();
        dataBase.getVotedCandidates().clear();
        dataBase.getVotedVoters().clear();
        dataBase.getVotedAgainstAll().clear();
        dataBase.getVoters().clear();
        dataBase.getCandidates().clear();
    }

    @Test
    public void testStartServerEmptyDataBase() throws IOException {
        DataBase dataBase = Server.startServer(null);
        assertNotEquals(null, dataBase);
        assertEquals(0, dataBase.getCandidates().size());
        assertEquals(0, dataBase.getVoters().size());
        assertEquals(0, dataBase.getTokensAndVoters().size());
    }

    @Test(expected = IOException.class)
    public void testStartServerFileNotFound() throws IOException {
        Server.startServer("wrongFile.txt");
    }

    @Test
    public void testStopServerWriteToFile() throws IOException, VoterException {
        DataBase dataBase = Server.startServer(null);
        assertNotEquals(null, dataBase);
        assertEquals(0, dataBase.getCandidates().size());
        assertEquals(0, dataBase.getVoters().size());
        assertEquals(0, dataBase.getTokensAndVoters().size());
        Server.stopServer("newFile.txt");
    }

    @Test
    public void testStartServerReadFromFile() throws IOException, VoterException {
        DataBase dataBase1 = Server.startServer(null);
        assertNotEquals(null, dataBase1);
        assertEquals(0, dataBase1.getCandidates().size());
        assertEquals(0, dataBase1.getVoters().size());
        assertEquals(0, dataBase1.getTokensAndVoters().size());

        Server.stopServer("newFile.txt");

        DataBase dataBase2 = Server.startServer("newFile.txt");
        assertNotEquals(null, dataBase2);
        assertEquals(0, dataBase2.getCandidates().size());
        assertEquals(0, dataBase2.getVoters().size());
        assertEquals(0, dataBase2.getTokensAndVoters().size());
    }

    @Test
    public void testParseCommandLine1() throws IOException, ParseException, VoterException {
        String[] args1 = {"-s file.txt"};
        Server.parseCommandLine(args1);
    }

    @Test
    public void testParseCommandLine2() throws IOException, ParseException, VoterException {
        String[] args1 = {"-s file.txt"};
        Server.parseCommandLine(args1);

        String[] args2 = {"-l file.txt"};
        Server.parseCommandLine(args2);
    }

    @Test
    public void testParseCommandLine3() throws IOException, ParseException, VoterException {
        String[] args1 = {"-s file.txt"};
        Server.parseCommandLine(args1);

        String[] args2 = {"-l file.txt", "-s file.txt"};
        Server.parseCommandLine(args2);
    }

    @Test
    public void testParseCommandLine4() throws IOException, ParseException, VoterException {
        String[] args1 = {""};
        Server.parseCommandLine(args1);

        DataBase dataBase1 = Server.startServer(null);
        assertNotEquals(null, dataBase1);
        assertEquals(0, dataBase1.getCandidates().size());
        assertEquals(0, dataBase1.getVoters().size());
        assertEquals(0, dataBase1.getTokensAndVoters().size());
    }

    @Test(expected = IOException.class)
    public void testParseCommandLineFileNotFound() throws IOException, ParseException, VoterException {
        String[] args = {"-l wrongFile.txt", "-s file.txt"};
        Server.parseCommandLine(args);
    }

    @Test(expected = ParseException.class)
    public void testParseCommandLineWrongArgument() throws IOException, ParseException, VoterException {
        String[] args1 = {"-wrongArgument file.txt"};
        Server.parseCommandLine(args1);
    }

    @Test
    public void testRegisterVoterCorrectly() throws IOException, VoterException, VoterException {
        Server server = new Server();
        server.startServer(null);
        RegisterVoterDtoRequest request = new RegisterVoterDtoRequest(0, "sergei","sergeev","12345678");
        assertEquals("sergei", request.getFirstName());
        assertEquals("sergeev", request.getLogin());
        assertEquals("12345678", request.getPassword());
        String jsonRequest = gson.toJson(request);
        String jsonResponse  = server.registerVoter(jsonRequest);
        RegisterVoterDtoResponse result = gson.fromJson(jsonResponse, RegisterVoterDtoResponse.class);
        String resultTokenJson = gson.toJson(result);
        assertEquals(resultTokenJson, jsonResponse);
        server.stopServer(null);
    }

    @Test
    public void testRegisterVoterShortPassword() throws IOException, VoterException {
        Server server = new Server();
        server.startServer(null);
        RegisterVoterDtoRequest request = new RegisterVoterDtoRequest(0, "ivan","ivanov","123456");
        assertEquals("ivan", request.getFirstName());
        assertEquals("ivanov", request.getLogin());
        assertEquals("123456", request.getPassword());
        String jsonRequest = gson.toJson(request);
        String jsonResponse  = server.registerVoter(jsonRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonResponse);
        server.stopServer(null);
    }

    @Test
    public void testRegisterVoterEmptyFirstname() throws IOException, VoterException {
        Server server = new Server();
        server.startServer(null);
        RegisterVoterDtoRequest request = new RegisterVoterDtoRequest(0, null,"ivanov","12345678");
        assertEquals(null, request.getFirstName());
        assertEquals("ivanov", request.getLogin());
        assertEquals("12345678", request.getPassword());
        String jsonRequest = gson.toJson(request);
        String jsonResponse  = server.registerVoter(jsonRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonResponse);
        server.stopServer(null);
    }

    @Test
    public void testRegisterVoterEmptyLogin() throws IOException, VoterException {
        Server server = new Server();
        server.startServer(null);
        RegisterVoterDtoRequest request = new RegisterVoterDtoRequest(0, "ivan",null,"12345678");
        assertEquals("ivan", request.getFirstName());
        assertEquals(null, request.getLogin());
        assertEquals("12345678", request.getPassword());
        String jsonRequest = gson.toJson(request);
        String jsonResponse  = server.registerVoter(jsonRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonResponse);
        server.stopServer(null);
    }

    @Test
    public void testRegisterVoterEmptyPassword() throws IOException, VoterException {
        Server server = new Server();
        server.startServer(null);
        RegisterVoterDtoRequest request = new RegisterVoterDtoRequest(0, "ivan","ivanov",null);
        assertEquals("ivan", request.getFirstName());
        assertEquals("ivanov", request.getLogin());
        assertEquals(null, request.getPassword());
        String jsonRequest = gson.toJson(request);
        String jsonResponse  = server.registerVoter(jsonRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonResponse);
        server.stopServer(null);
    }

    @Test
    public void testAddCandidateCorrectly() throws IOException, VoterException {
        Server server = new Server();
        server.startServer(null);

        RegisterVoterDtoRequest registerVoterRequest = new RegisterVoterDtoRequest(0, "alex","alexandrov","12345678");
        assertEquals("alex", registerVoterRequest.getFirstName());
        assertEquals("alexandrov", registerVoterRequest.getLogin());
        assertEquals("12345678", registerVoterRequest.getPassword());

        String jsonRegisterRequest = gson.toJson(registerVoterRequest);
        String jsonRegisterResponse  = server.registerVoter(jsonRegisterRequest);
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
        String jsonAddCandidateResponse1  = server.addCandidate(jsonAddCandidateRequest1);

        AddCandidateDtoResponse addCandidateDtoResponse1 = gson.fromJson(jsonAddCandidateResponse1, AddCandidateDtoResponse.class);
        String resultAddCandidateResponseJson1 = gson.toJson(addCandidateDtoResponse1);
        assertEquals(resultAddCandidateResponseJson1, jsonAddCandidateResponse1);

        server.stopServer(null);
    }

    @Ignore
    @Test
    public void testGetCandidates() throws IOException, VoterException {
        Server server = new Server();
        server.startServer(null);

        RegisterVoterDtoRequest registerVoterRequest = new RegisterVoterDtoRequest(0,"miron","mironov","12345678");
        assertEquals("miron", registerVoterRequest.getFirstName());
        assertEquals("mironov", registerVoterRequest.getLogin());
        assertEquals("12345678", registerVoterRequest.getPassword());

        String jsonRegisterRequest = gson.toJson(registerVoterRequest);
        String jsonRegisterResponse  = server.registerVoter(jsonRegisterRequest);
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
        String jsonAddCandidateResponse1  = server.addCandidate(jsonAddCandidateRequest1);
        String emptyJson = gson.toJson("");

        AddCandidateDtoResponse addCandidateDtoResponse1 = gson.fromJson(jsonAddCandidateResponse1, AddCandidateDtoResponse.class);
        String resultAddCandidateResponseJson1 = gson.toJson(addCandidateDtoResponse1);
        assertEquals(resultAddCandidateResponseJson1, jsonAddCandidateResponse1);

        AllCandidatesDtoRequest allCandidatesDtoRequest = new AllCandidatesDtoRequest(addCandidateDtoResponse1.getToken());
        assertEquals(allCandidatesDtoRequest.getToken(), registerVoterDtoResponse.getToken());
        String jsonRequest = gson.toJson(allCandidatesDtoRequest);
        String jsonResponse = server.getCandidates(jsonRequest);

        AllCandidatesDtoResponse allCandidatesDtoResponse = gson.fromJson(jsonResponse, AllCandidatesDtoResponse.class);
        String resultJson = gson.toJson(allCandidatesDtoResponse);
        assertEquals(resultJson, jsonResponse);

        server.stopServer(null);
    }

    @Ignore
    @Test
    public void testGetCandidatesWrongToken1() throws IOException, VoterException {
        Server server = new Server();
        server.startServer(null);

        AllCandidatesDtoRequest allCandidatesDtoRequest = new AllCandidatesDtoRequest(null);
        assertEquals(null, allCandidatesDtoRequest.getToken());

        String jsonRequest = gson.toJson(allCandidatesDtoRequest);
        String jsonResponse = server.getCandidates(jsonRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonResponse);

        server.stopServer(null);
    }

    @Ignore
    @Test
    public void testGetCandidatesWrongToken2() throws IOException, VoterException {
        Server server = new Server();
        server.startServer(null);

        AllCandidatesDtoRequest allCandidatesDtoRequest = new AllCandidatesDtoRequest("");
        assertEquals("", allCandidatesDtoRequest.getToken());

        String jsonRequest = gson.toJson(allCandidatesDtoRequest);
        String jsonResponse = server.getCandidates(jsonRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonResponse);

        server.stopServer(null);
    }

    @Ignore
    @Test
    public void testGetCandidatesOfflineToken() throws IOException, VoterException {
        Server server = new Server();
        server.startServer(null);

        RegisterVoterDtoRequest registerVoterRequest = new RegisterVoterDtoRequest(0, "alexei","alexeev","12345678");
        assertEquals("alexei", registerVoterRequest.getFirstName());
        assertEquals("alexeev", registerVoterRequest.getLogin());
        assertEquals("12345678", registerVoterRequest.getPassword());

        String jsonRegisterRequest = gson.toJson(registerVoterRequest);
        String jsonRegisterResponse  = server.registerVoter(jsonRegisterRequest);
        RegisterVoterDtoResponse registerVoterDtoResponse = gson.fromJson(jsonRegisterResponse, RegisterVoterDtoResponse.class);
        String resultRegisterJson = gson.toJson(registerVoterDtoResponse);
        assertEquals(resultRegisterJson, jsonRegisterResponse);

        AllCandidatesDtoRequest allCandidatesDtoRequest = new AllCandidatesDtoRequest(registerVoterDtoResponse.getToken());
        assertEquals(allCandidatesDtoRequest.getToken(), registerVoterDtoResponse.getToken());

        DataBase dataBase = DataBase.getDataBase();
        Map<String, Voter> tokensAndVoter = dataBase.getTokensAndVoters();
        tokensAndVoter.put(registerVoterDtoResponse.getToken(), new Voter("alexei","alexeev","12345678"));
        allCandidatesDtoRequest.setTokensAndVoters(tokensAndVoter);
        assertEquals(tokensAndVoter, allCandidatesDtoRequest.getTokensAndVoters());

        String jsonRequest = gson.toJson(allCandidatesDtoRequest);
        String jsonResponse = server.getCandidates(jsonRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonResponse);

        server.stopServer(null);
    }

    @Ignore
    @Test
    public void testGetCandidatesAndPrograms() throws IOException, VoterException {
        Server server = new Server();
        server.startServer(null);

        RegisterVoterDtoRequest registerVoterRequest = new RegisterVoterDtoRequest(0, "efim","efimov","12345678");
        assertEquals("efim", registerVoterRequest.getFirstName());
        assertEquals("efimov", registerVoterRequest.getLogin());
        assertEquals("12345678", registerVoterRequest.getPassword());

        String jsonRegisterRequest = gson.toJson(registerVoterRequest);
        String jsonRegisterResponse  = server.registerVoter(jsonRegisterRequest);
        RegisterVoterDtoResponse registerVoterDtoResponse = gson.fromJson(jsonRegisterResponse, RegisterVoterDtoResponse.class);
        String resultRegisterJson = gson.toJson(registerVoterDtoResponse);
        assertEquals(resultRegisterJson, jsonRegisterResponse);

        AddCandidateDtoRequest addCandidateRequest = new AddCandidateDtoRequest(registerVoterRequest.getId(),registerVoterRequest.getFirstName(), registerVoterRequest.getLogin(),
                                                                                registerVoterRequest.getPassword(), registerVoterDtoResponse.getToken());
        assertEquals(registerVoterRequest.getFirstName(), addCandidateRequest.getFirstName());
        assertEquals(registerVoterRequest.getLogin(), addCandidateRequest.getLogin());
        assertEquals(registerVoterRequest.getPassword(), addCandidateRequest.getPassword());
        assertEquals(registerVoterDtoResponse.getToken(), addCandidateRequest.getToken());

        String jsonAddCandidateRequest1 = gson.toJson(addCandidateRequest);
        String jsonAddCandidateResponse1  = server.addCandidate(jsonAddCandidateRequest1);

        AddCandidateDtoResponse addCandidateDtoResponse1 = gson.fromJson(jsonAddCandidateResponse1, AddCandidateDtoResponse.class);
        String resultAddCandidateResponseJson1 = gson.toJson(addCandidateDtoResponse1);
        assertEquals(resultAddCandidateResponseJson1, jsonAddCandidateResponse1);

        GetCandidatesAndProgramsDtoRequest candidatesAndProgramsDtoRequest = new GetCandidatesAndProgramsDtoRequest(addCandidateDtoResponse1.getToken());
        assertEquals(candidatesAndProgramsDtoRequest.getToken(), registerVoterDtoResponse.getToken());
        String jsonRequest = gson.toJson(candidatesAndProgramsDtoRequest);
        String jsonResponse = server.getCandidatesAndPrograms(jsonRequest);

        GetCandidatesAndProgramsDtoResponse getCandidatesAndProgramsDtoResponse = gson.fromJson(jsonResponse, GetCandidatesAndProgramsDtoResponse.class);
        String resultJson = new GsonBuilder().enableComplexMapKeySerialization()
                                             .create()
                                             .toJson(getCandidatesAndProgramsDtoResponse);
        assertEquals(resultJson, jsonResponse);

        server.stopServer(null);
    }

    @Test
    public void testGetCandidatesAndProgramsWrongToken1() throws IOException, VoterException {
        Server server = new Server();
        server.startServer(null);

        GetCandidatesAndProgramsDtoRequest candidatesAndProgramsDtoRequest = new GetCandidatesAndProgramsDtoRequest(null);
        assertEquals(null, candidatesAndProgramsDtoRequest.getToken());

        String jsonRequest = gson.toJson(candidatesAndProgramsDtoRequest);
        String jsonResponse = server.getCandidatesAndPrograms(jsonRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonResponse);

        server.stopServer(null);
    }

    @Test
    public void testGetCandidatesAndProgramsWrongToken2() throws IOException, VoterException {
        Server server = new Server();
        server.startServer(null);

        GetCandidatesAndProgramsDtoRequest candidatesAndProgramsDtoRequest = new GetCandidatesAndProgramsDtoRequest("");
        assertEquals("", candidatesAndProgramsDtoRequest.getToken());

        String jsonRequest = gson.toJson(candidatesAndProgramsDtoRequest);
        String jsonResponse = server.getCandidatesAndPrograms(jsonRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonResponse);

        server.stopServer(null);
    }

    @Ignore
    @Test
    public void testGetCandidatesAndProgramsOfflineToken() throws IOException, VoterException {
        Server server = new Server();
        server.startServer(null);

        RegisterVoterDtoRequest registerVoterRequest = new RegisterVoterDtoRequest(0, "alexei","alexeev","12345678");
        assertEquals("alexei", registerVoterRequest.getFirstName());
        assertEquals("alexeev", registerVoterRequest.getLogin());
        assertEquals("12345678", registerVoterRequest.getPassword());

        String jsonRegisterRequest = gson.toJson(registerVoterRequest);
        String jsonRegisterResponse  = server.registerVoter(jsonRegisterRequest);
        RegisterVoterDtoResponse registerVoterDtoResponse = gson.fromJson(jsonRegisterResponse, RegisterVoterDtoResponse.class);
        String resultRegisterJson = gson.toJson(registerVoterDtoResponse);
        assertEquals(resultRegisterJson, jsonRegisterResponse);

        GetCandidatesAndProgramsDtoRequest candidatesAndProgramsDtoRequest = new GetCandidatesAndProgramsDtoRequest(registerVoterDtoResponse.getToken());
        assertEquals(candidatesAndProgramsDtoRequest.getToken(), registerVoterDtoResponse.getToken());

        DataBase dataBase = DataBase.getDataBase();
        Map<String, Voter> tokensAndVoters = dataBase.getTokensAndVoters();
        tokensAndVoters.put(registerVoterDtoResponse.getToken(), new Voter("alexei","alexeev","12345678"));
        candidatesAndProgramsDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(tokensAndVoters, candidatesAndProgramsDtoRequest.getTokensAndVoters());

        String jsonRequest = gson.toJson(candidatesAndProgramsDtoRequest);
        String jsonResponse = server.getCandidates(jsonRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonResponse);

        server.stopServer(null);
    }

    @Ignore
    @Test
    public void testRemoveCandidateCorrectly() throws IOException, VoterException {
        Server server = new Server();
        server.startServer(null);

        RegisterVoterDtoRequest registerVoterRequest = new RegisterVoterDtoRequest(0, "kiril","kirilov","123456789");
        assertEquals("kiril", registerVoterRequest.getFirstName());
        assertEquals("kirilov", registerVoterRequest.getLogin());
        assertEquals("123456789", registerVoterRequest.getPassword());

        String jsonRegisterRequest = gson.toJson(registerVoterRequest);
        String jsonRegisterResponse  = server.registerVoter(jsonRegisterRequest);
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
        String jsonAddCandidateResponse1  = server.addCandidate(jsonAddCandidateRequest1);

        AddCandidateDtoResponse addCandidateDtoResponse1 = gson.fromJson(jsonAddCandidateResponse1, AddCandidateDtoResponse.class);
        String resultAddCandidateResponseJson1 = gson.toJson(addCandidateDtoResponse1);
        assertEquals(resultAddCandidateResponseJson1, jsonAddCandidateResponse1);

        RemoveCandidateDtoRequest removeCandidateDtoRequest = new RemoveCandidateDtoRequest(0, addCandidateDtoResponse1.getToken());
        assertEquals(addCandidateRequest.getId(), removeCandidateDtoRequest.getCandidateId());
        assertEquals(addCandidateDtoResponse1.getToken(), removeCandidateDtoRequest.getCandidateToken());

        String jsonRemoveCandidateRequest = gson.toJson(removeCandidateDtoRequest);
        String jsonRemoveCandidateResponse = server.removeCandidate(jsonRemoveCandidateRequest);
        assertEquals(gson.toJson(""), jsonRemoveCandidateResponse);

        server.stopServer(null);
    }

    @Test
    public void testRemoveCandidateWrongLogin1() throws IOException, VoterException {
        Server server = new Server();
        server.startServer(null);

        RegisterVoterDtoRequest registerVoterRequest = new RegisterVoterDtoRequest(0, "kiril","kirilov","123456789");
        assertEquals("kiril", registerVoterRequest.getFirstName());
        assertEquals("kirilov", registerVoterRequest.getLogin());
        assertEquals("123456789", registerVoterRequest.getPassword());

        String jsonRegisterRequest = gson.toJson(registerVoterRequest);
        String jsonRegisterResponse  = server.registerVoter(jsonRegisterRequest);
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
        String jsonAddCandidateResponse1  = server.addCandidate(jsonAddCandidateRequest1);
        String emptyJson = gson.toJson("");

        AddCandidateDtoResponse addCandidateDtoResponse1 = gson.fromJson(jsonAddCandidateResponse1, AddCandidateDtoResponse.class);
        String resultAddCandidateResponseJson1 = gson.toJson(addCandidateDtoResponse1);
        assertEquals(resultAddCandidateResponseJson1, jsonAddCandidateResponse1);

        RemoveCandidateDtoRequest removeCandidateDtoRequest = new RemoveCandidateDtoRequest(0, null);
        assertEquals(null, removeCandidateDtoRequest.getCandidateToken());

        String jsonRemoveCandidateRequest = gson.toJson(removeCandidateDtoRequest);
        String jsonRemoveCandidateResponse = server.removeCandidate(jsonRemoveCandidateRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonRemoveCandidateResponse);

        server.stopServer(null);
    }

    @Test
    public void testRemoveCandidateWrongLogin2() throws IOException, VoterException {
        Server server = new Server();
        server.startServer(null);

        RegisterVoterDtoRequest registerVoterRequest = new RegisterVoterDtoRequest(0, "kiril","kirilov","123456789");
        assertEquals("kiril", registerVoterRequest.getFirstName());
        assertEquals("kirilov", registerVoterRequest.getLogin());
        assertEquals("123456789", registerVoterRequest.getPassword());

        String jsonRegisterRequest = gson.toJson(registerVoterRequest);
        String jsonRegisterResponse  = server.registerVoter(jsonRegisterRequest);
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
        String jsonAddCandidateResponse1  = server.addCandidate(jsonAddCandidateRequest1);
        String emptyJson = gson.toJson("");

        AddCandidateDtoResponse addCandidateDtoResponse1 = gson.fromJson(jsonAddCandidateResponse1, AddCandidateDtoResponse.class);
        String resultAddCandidateResponseJson1 = gson.toJson(addCandidateDtoResponse1);
        assertEquals(resultAddCandidateResponseJson1, jsonAddCandidateResponse1);

        RemoveCandidateDtoRequest removeCandidateDtoRequest = new RemoveCandidateDtoRequest(0, "");
        assertEquals("", removeCandidateDtoRequest.getCandidateToken());

        String jsonRemoveCandidateRequest = gson.toJson(removeCandidateDtoRequest);
        String jsonRemoveCandidateResponse = server.removeCandidate(jsonRemoveCandidateRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonRemoveCandidateResponse);

        server.stopServer(null);
    }

    @Ignore
    @Test
    public void testRemoveCandidateIfTokenIsOfflineToken() throws IOException, VoterException {
        Server server = new Server();
        server.startServer(null);

        RegisterVoterDtoRequest request = new RegisterVoterDtoRequest(0, "ignat","ignatov","12345678");
        assertEquals("ignat", request.getFirstName());
        assertEquals("ignatov", request.getLogin());
        assertEquals("12345678", request.getPassword());

        String jsonRegisterRequest1 = gson.toJson(request);
        String jsonRegisterResponse1  = server.registerVoter(jsonRegisterRequest1);

        RegisterVoterDtoResponse resultRegister = gson.fromJson(jsonRegisterResponse1, RegisterVoterDtoResponse.class);
        String registerTokenJson1 = gson.toJson(resultRegister);
        assertEquals(registerTokenJson1, jsonRegisterResponse1);

        LogoutDtoRequest logoutDtoRequest = new LogoutDtoRequest(request.getLogin(), resultRegister.getToken());
        assertEquals("ignatov", logoutDtoRequest.getLogin());
        assertEquals(resultRegister.getToken(), logoutDtoRequest.getToken());

        String jsonLogoutRequest = gson.toJson(logoutDtoRequest);
        String jsonLogoutResponse = server.logout(jsonLogoutRequest);

        LogoutDtoResponse resultLogout = gson.fromJson(jsonLogoutResponse, LogoutDtoResponse.class);
        String offlineTokenJson = gson.toJson(resultLogout);
        assertEquals(offlineTokenJson, jsonLogoutResponse);

        RemoveCandidateDtoRequest removeCandidateDtoRequest = new RemoveCandidateDtoRequest(0, resultLogout.getToken());
        assertEquals("ignatov", removeCandidateDtoRequest.getCandidateId());
        assertEquals( resultLogout.getToken(), removeCandidateDtoRequest.getCandidateToken());

        String jsonRemoveCandidateRequest = gson.toJson(removeCandidateDtoRequest);
        String jsonRemoveCandidateResponse = server.removeCandidate(jsonRemoveCandidateRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonRemoveCandidateResponse);

        server.stopServer(null);
    }

    @Test
    public void testRemoveCandidateIfVoterIsNotCandidate() throws IOException, VoterException {
        Server server = new Server();
        server.startServer(null);

        RegisterVoterDtoRequest request = new RegisterVoterDtoRequest(0, "georgi","georgiev","12345678");
        assertEquals("georgi", request.getFirstName());
        assertEquals("georgiev", request.getLogin());
        assertEquals("12345678", request.getPassword());

        String jsonRegisterRequest1 = gson.toJson(request);
        String jsonRegisterResponse1  = server.registerVoter(jsonRegisterRequest1);

        RegisterVoterDtoResponse resultRegister = gson.fromJson(jsonRegisterResponse1, RegisterVoterDtoResponse.class);
        String registerTokenJson1 = gson.toJson(resultRegister);
        assertEquals(registerTokenJson1, jsonRegisterResponse1);

        RemoveCandidateDtoRequest removeCandidateDtoRequest = new RemoveCandidateDtoRequest(0, resultRegister.getToken());
        assertEquals(0, removeCandidateDtoRequest.getCandidateId());
        assertEquals( resultRegister.getToken(), removeCandidateDtoRequest.getCandidateToken());

        String jsonRemoveCandidateRequest = gson.toJson(removeCandidateDtoRequest);
        String jsonRemoveCandidateResponse = server.removeCandidate(jsonRemoveCandidateRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonRemoveCandidateResponse);

        server.stopServer(null);
    }

    @Ignore
    @Test
    public void testLogoutCorrectlyIfVoterNotCandidate() throws IOException, VoterException {
        Server server = new Server();
        server.startServer(null);

        RegisterVoterDtoRequest request = new RegisterVoterDtoRequest(0, "sergei","sergeev","12345678");
        assertEquals("sergei", request.getFirstName());
        assertEquals("sergeev", request.getLogin());
        assertEquals("12345678", request.getPassword());

        String jsonRegisterRequest = gson.toJson(request);
        String jsonRegisterResponse  = server.registerVoter(jsonRegisterRequest);

        RegisterVoterDtoResponse resultRegister = gson.fromJson(jsonRegisterResponse, RegisterVoterDtoResponse.class);
        String registerTokenJson = gson.toJson(resultRegister);
        assertEquals(registerTokenJson, jsonRegisterResponse);

        LogoutDtoRequest logoutDtoRequest = new LogoutDtoRequest(request.getLogin(), resultRegister.getToken());
        assertEquals("sergeev", logoutDtoRequest.getLogin());
        assertEquals(resultRegister.getToken(), logoutDtoRequest.getToken());

        String jsonLogoutRequest = gson.toJson(logoutDtoRequest);
        String jsonLogoutResponse = server.logout(jsonLogoutRequest);

        LogoutDtoResponse resultLogout = gson.fromJson(jsonLogoutResponse, LogoutDtoResponse.class);
        String offlineTokenJson = gson.toJson(resultLogout);
        assertEquals(offlineTokenJson, jsonLogoutResponse);

        server.stopServer(null);
    }

    @Ignore
    @Test
    public void testLogoutCorrectlyIfVoterIsCandidate() throws IOException, VoterException {
        Server server = new Server();
        server.startServer(null);

        RegisterVoterDtoRequest registerVoterRequest = new RegisterVoterDtoRequest(0, "sergei","sergeev","12345678");
        assertEquals("sergei", registerVoterRequest.getFirstName());
        assertEquals("sergeev", registerVoterRequest.getLogin());
        assertEquals("12345678", registerVoterRequest.getPassword());

        String jsonRegisterRequest = gson.toJson(registerVoterRequest);
        String jsonRegisterResponse  = server.registerVoter(jsonRegisterRequest);

        RegisterVoterDtoResponse resultRegister = gson.fromJson(jsonRegisterResponse, RegisterVoterDtoResponse.class);
        String registerTokenJson = gson.toJson(resultRegister);
        assertEquals(registerTokenJson, jsonRegisterResponse);

        AddCandidateDtoRequest addCandidateRequest = new AddCandidateDtoRequest(registerVoterRequest.getId(), registerVoterRequest.getFirstName(), registerVoterRequest.getLogin(),
                                                                                registerVoterRequest.getPassword(), resultRegister.getToken());
        assertEquals(registerVoterRequest.getFirstName(), addCandidateRequest.getFirstName());
        assertEquals(registerVoterRequest.getLogin(), addCandidateRequest.getLogin());
        assertEquals(registerVoterRequest.getPassword(), addCandidateRequest.getPassword());
        assertEquals(resultRegister.getToken(), addCandidateRequest.getToken());

        String jsonAddCandidateRequest = gson.toJson(addCandidateRequest);
        String jsonAddCandidateResponse  = server.addCandidate(jsonAddCandidateRequest);
        String emptyJson = gson.toJson("");
        assertEquals(emptyJson, jsonAddCandidateResponse);

        LogoutDtoRequest logoutDtoRequest = new LogoutDtoRequest(addCandidateRequest.getLogin(), resultRegister.getToken());
        assertEquals("sergeev", logoutDtoRequest.getLogin());
        assertEquals(resultRegister.getToken(), logoutDtoRequest.getToken());

        String jsonLogoutRequest = gson.toJson(logoutDtoRequest);
        String jsonLogoutResponse = server.logout(jsonLogoutRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonLogoutResponse);

        server.stopServer(null);
    }

    @Test
    public void testLogoutWrongToken1() throws IOException, VoterException {
        Server server = new Server();
        server.startServer(null);

        RegisterVoterDtoRequest request = new RegisterVoterDtoRequest(0, "sergei","sergeev","12345678");
        assertEquals("sergei", request.getFirstName());
        assertEquals("sergeev", request.getLogin());
        assertEquals("12345678", request.getPassword());

        LogoutDtoRequest logoutDtoRequest = new LogoutDtoRequest(request.getLogin(), null);
        assertEquals("sergeev", logoutDtoRequest.getLogin());
        assertEquals(null, logoutDtoRequest.getToken());

        String jsonLogoutRequest = gson.toJson(logoutDtoRequest);
        String jsonLogoutResponse = server.logout(jsonLogoutRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonLogoutResponse);

        server.stopServer(null);
    }

    @Test
    public void testLogoutWrongToken2() throws IOException, VoterException {
        Server server = new Server();
        server.startServer(null);

        RegisterVoterDtoRequest registerRequest = new RegisterVoterDtoRequest(0, "sergei","sergeev","12345678");
        assertEquals("sergei", registerRequest.getFirstName());
        assertEquals("sergeev", registerRequest.getLogin());
        assertEquals("12345678", registerRequest.getPassword());

        LogoutDtoRequest logoutDtoRequest = new LogoutDtoRequest(registerRequest.getLogin(), "");
        assertEquals("sergeev", logoutDtoRequest.getLogin());
        assertEquals("", logoutDtoRequest.getToken());

        String jsonLogoutRequest = gson.toJson(logoutDtoRequest);
        String jsonLogoutResponse = server.logout(jsonLogoutRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonLogoutResponse);

        server.stopServer(null);
    }

    @Test
    public void testLogoutWrongLogin1() throws IOException, VoterException {
        Server server = new Server();
        server.startServer(null);

        RegisterVoterDtoRequest registerVoterRequest = new RegisterVoterDtoRequest(0, "sergei","sergeev","12345678");
        assertEquals("sergei", registerVoterRequest.getFirstName());
        assertEquals("sergeev", registerVoterRequest.getLogin());
        assertEquals("12345678", registerVoterRequest.getPassword());

        String jsonRegisterRequest = gson.toJson(registerVoterRequest);
        String jsonRegisterResponse  = server.registerVoter(jsonRegisterRequest);

        RegisterVoterDtoResponse resultRegister = gson.fromJson(jsonRegisterResponse, RegisterVoterDtoResponse.class);
        String registerTokenJson = gson.toJson(resultRegister);
        assertEquals(registerTokenJson, jsonRegisterResponse);

        LogoutDtoRequest logoutDtoRequest = new LogoutDtoRequest(null, resultRegister.getToken());
        assertEquals(null, logoutDtoRequest.getLogin());
        assertEquals(resultRegister.getToken(), logoutDtoRequest.getToken());

        String jsonLogoutRequest = gson.toJson(logoutDtoRequest);
        String jsonLogoutResponse = server.logout(jsonLogoutRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonLogoutResponse);

        server.stopServer(null);
    }

    @Test
    public void testLogoutWrongLogin2() throws IOException, VoterException {
        Server server = new Server();
        server.startServer(null);

        RegisterVoterDtoRequest registerVoterRequest = new RegisterVoterDtoRequest(0, "sergei","sergeev","12345678");
        assertEquals("sergei", registerVoterRequest.getFirstName());
        assertEquals("sergeev", registerVoterRequest.getLogin());
        assertEquals("12345678", registerVoterRequest.getPassword());

        String jsonRegisterRequest = gson.toJson(registerVoterRequest);
        String jsonRegisterResponse  = server.registerVoter(jsonRegisterRequest);

        RegisterVoterDtoResponse resultRegister = gson.fromJson(jsonRegisterResponse, RegisterVoterDtoResponse.class);
        String registerTokenJson = gson.toJson(resultRegister);
        assertEquals(registerTokenJson, jsonRegisterResponse);

        LogoutDtoRequest logoutDtoRequest = new LogoutDtoRequest("", resultRegister.getToken());
        assertEquals("", logoutDtoRequest.getLogin());
        assertEquals(resultRegister.getToken(), logoutDtoRequest.getToken());

        String jsonLogoutRequest = gson.toJson(logoutDtoRequest);
        String jsonLogoutResponse = server.logout(jsonLogoutRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonLogoutResponse);

        server.stopServer(null);
    }

    @Ignore
    @Test
    public void testLoginIfTokenIsOfflineToken() throws IOException, VoterException {
        Server server = new Server();
        server.startServer(null);

        RegisterVoterDtoRequest request = new RegisterVoterDtoRequest(0, "georgi","georgiev","12345678");
        assertEquals("georgi", request.getFirstName());
        assertEquals("georgiev", request.getLogin());
        assertEquals("12345678", request.getPassword());

        String jsonRegisterRequest1 = gson.toJson(request);
        String jsonRegisterResponse1  = server.registerVoter(jsonRegisterRequest1);

        RegisterVoterDtoResponse resultRegister = gson.fromJson(jsonRegisterResponse1, RegisterVoterDtoResponse.class);
        String registerTokenJson1 = gson.toJson(resultRegister);
        assertEquals(registerTokenJson1, jsonRegisterResponse1);

        LogoutDtoRequest logoutDtoRequest = new LogoutDtoRequest(request.getLogin(), resultRegister.getToken());
        assertEquals("georgiev", logoutDtoRequest.getLogin());
        assertEquals(resultRegister.getToken(), logoutDtoRequest.getToken());

        String jsonLogoutRequest = gson.toJson(logoutDtoRequest);
        String jsonLogoutResponse = server.logout(jsonLogoutRequest);

        LogoutDtoResponse resultLogout = gson.fromJson(jsonLogoutResponse, LogoutDtoResponse.class);
        String offlineTokenJson = gson.toJson(resultLogout);
        assertEquals(offlineTokenJson, jsonLogoutResponse);

        LoginDtoRequest loginDtoRequest = new LoginDtoRequest();
        loginDtoRequest.setLogin(request.getLogin());
        assertEquals("georgiev", loginDtoRequest.getLogin());
        loginDtoRequest.setPassword(request.getPassword());
        assertEquals("12345678", loginDtoRequest.getPassword());

        String jsonLoginRequest = gson.toJson(loginDtoRequest);
        String jsonLoginResponse = server.login(jsonLoginRequest);

        LoginDtoResponse resultLogin = gson.fromJson(jsonLoginResponse, LoginDtoResponse.class);
        String newTokenJson = gson.toJson(resultLogin);
        assertEquals(newTokenJson, jsonLoginResponse);

        server.stopServer(null);
    }

    @Test
    public void testLoginIfTokenIsActiveToken() throws IOException, VoterException {
        Server server = new Server();
        server.startServer(null);

        RegisterVoterDtoRequest request = new RegisterVoterDtoRequest(0, "fedor","fedorov","12345678");
        assertEquals("fedor", request.getFirstName());
        assertEquals("fedorov", request.getLogin());
        assertEquals("12345678", request.getPassword());

        String jsonRegisterRequest1 = gson.toJson(request);
        String jsonRegisterResponse1  = server.registerVoter(jsonRegisterRequest1);

        RegisterVoterDtoResponse resultRegister = gson.fromJson(jsonRegisterResponse1, RegisterVoterDtoResponse.class);
        String registerTokenJson1 = gson.toJson(resultRegister);
        assertEquals(registerTokenJson1, jsonRegisterResponse1);

        LoginDtoRequest loginDtoRequest = new LoginDtoRequest();
        loginDtoRequest.setLogin(request.getLogin());
        assertEquals("fedorov", loginDtoRequest.getLogin());
        loginDtoRequest.setPassword(request.getPassword());
        assertEquals("12345678", loginDtoRequest.getPassword());

        String jsonLoginRequest = gson.toJson(loginDtoRequest);
        String jsonLoginResponse = server.login(jsonLoginRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonLoginResponse);

        server.stopServer(null);
    }

    @Ignore
    @Test
    public void testLoginIfVoterNotFoundByLogin() throws IOException, VoterException {
        Server server = new Server();
        server.startServer(null);

        RegisterVoterDtoRequest request = new RegisterVoterDtoRequest(0, "boris","borisov","12345678");
        assertEquals("boris", request.getFirstName());
        assertEquals("borisov", request.getLogin());
        assertEquals("12345678", request.getPassword());

        String jsonRegisterRequest1 = gson.toJson(request);
        String jsonRegisterResponse1  = server.registerVoter(jsonRegisterRequest1);

        RegisterVoterDtoResponse resultRegister = gson.fromJson(jsonRegisterResponse1, RegisterVoterDtoResponse.class);
        String registerTokenJson1 = gson.toJson(resultRegister);
        assertEquals(registerTokenJson1, jsonRegisterResponse1);

        LogoutDtoRequest logoutDtoRequest = new LogoutDtoRequest(request.getLogin(), resultRegister.getToken());
        assertEquals("borisov", logoutDtoRequest.getLogin());
        assertEquals(resultRegister.getToken(), logoutDtoRequest.getToken());

        String jsonLogoutRequest = gson.toJson(logoutDtoRequest);
        String jsonLogoutResponse = server.logout(jsonLogoutRequest);

        LogoutDtoResponse resultLogout = gson.fromJson(jsonLogoutResponse, LogoutDtoResponse.class);
        String offlineTokenJson = gson.toJson(resultLogout);
        assertEquals(offlineTokenJson, jsonLogoutResponse);

        LoginDtoRequest loginDtoRequest = new LoginDtoRequest();
        loginDtoRequest.setLogin("andreev");
        assertEquals("andreev", loginDtoRequest.getLogin());
        loginDtoRequest.setPassword("12345678");
        assertEquals("12345678", loginDtoRequest.getPassword());

        String jsonLoginRequest = gson.toJson(loginDtoRequest);
        String jsonLoginResponse = server.login(jsonLoginRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonLoginResponse);

        server.stopServer(null);
    }

    @Ignore
    @Test
    public void testLoginIfPasswordNotFound() throws IOException, VoterException {
        Server server = new Server();
        server.startServer(null);

        RegisterVoterDtoRequest request = new RegisterVoterDtoRequest(0, "vladimir","akimov","12345678");
        assertEquals("vladimir", request.getFirstName());
        assertEquals("akimov", request.getLogin());
        assertEquals("12345678", request.getPassword());

        String jsonRegisterRequest1 = gson.toJson(request);
        String jsonRegisterResponse1  = server.registerVoter(jsonRegisterRequest1);

        RegisterVoterDtoResponse resultRegister = gson.fromJson(jsonRegisterResponse1, RegisterVoterDtoResponse.class);
        String registerTokenJson1 = gson.toJson(resultRegister);
        assertEquals(registerTokenJson1, jsonRegisterResponse1);

        LogoutDtoRequest logoutDtoRequest = new LogoutDtoRequest(request.getLogin(), resultRegister.getToken());
        assertEquals("akimov", logoutDtoRequest.getLogin());
        assertEquals(resultRegister.getToken(), logoutDtoRequest.getToken());

        String jsonLogoutRequest = gson.toJson(logoutDtoRequest);
        String jsonLogoutResponse = server.logout(jsonLogoutRequest);

        LogoutDtoResponse resultLogout = gson.fromJson(jsonLogoutResponse, LogoutDtoResponse.class);
        String offlineTokenJson = gson.toJson(resultLogout);
        assertEquals(offlineTokenJson, jsonLogoutResponse);

        LoginDtoRequest loginDtoRequest = new LoginDtoRequest();
        loginDtoRequest.setLogin("akimov");
        assertEquals("akimov", loginDtoRequest.getLogin());

        loginDtoRequest.setPassword("123456789");
        assertEquals("123456789", loginDtoRequest.getPassword());

        String jsonLoginRequest = gson.toJson(loginDtoRequest);
        String jsonLoginResponse = server.login(jsonLoginRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonLoginResponse);

        server.stopServer(null);
    }

    @Test
    public void testAddOffer() throws IOException, VoterException {
        Server server = new Server();
        server.startServer(null);

        RegisterVoterDtoRequest registerRequest1 = new RegisterVoterDtoRequest(0, "petr","petrov","12345678");
        assertEquals("petr", registerRequest1.getFirstName());
        assertEquals("petrov", registerRequest1.getLogin());
        assertEquals("12345678", registerRequest1.getPassword());
        String jsonRegisterRequest1 = gson.toJson(registerRequest1);
        String jsonRegisterResponse1  = server.registerVoter(jsonRegisterRequest1);

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
        String jsonAddCandidateResponse1  = server.addCandidate(jsonAddCandidateRequest1);

        AddCandidateDtoResponse addCandidateDtoResponse1 = gson.fromJson(jsonAddCandidateResponse1, AddCandidateDtoResponse.class);
        String resultAddCandidateResponseJson1 = gson.toJson(addCandidateDtoResponse1);
        assertEquals(resultAddCandidateResponseJson1, jsonAddCandidateResponse1);

        RegisterVoterDtoRequest registerRequest2 = new RegisterVoterDtoRequest(1, "boris","borisov","12345678");
        assertEquals("boris", registerRequest2.getFirstName());
        assertEquals("borisov", registerRequest2.getLogin());
        assertEquals("12345678", registerRequest2.getPassword());
        String jsonRegisterRequest2 = gson.toJson(registerRequest2);
        String jsonRegisterResponse2  = server.registerVoter(jsonRegisterRequest2);

        RegisterVoterDtoResponse registerVoterDtoResponse2 = gson.fromJson(jsonRegisterResponse2, RegisterVoterDtoResponse.class);
        String resultRegisterJson2 = gson.toJson(registerVoterDtoResponse2);
        assertEquals(resultRegisterJson2, jsonRegisterResponse2);

        String voterToken = registerVoterDtoResponse2.getToken();
        String candidateToken = addCandidateDtoResponse1.getToken();

        AddOfferDtoRequest addOfferDtoRequest = new AddOfferDtoRequest(voterToken,1, candidateToken, 0,"build a bridge across the river");
        assertEquals(voterToken, addOfferDtoRequest.getVoterToken());
        assertEquals(candidateToken, addOfferDtoRequest.getCandidateToken());
        assertEquals("build a bridge across the river", addOfferDtoRequest.getOfferDescription());

        Map<String,Voter> tokensAndVoters = dataBase.getTokensAndVoters();
        addOfferDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(2, addOfferDtoRequest.getTokensAndVoters().size());

        String jsonAddOfferRequest = gson.toJson(addOfferDtoRequest);
        String jsonAddOfferResponse = server.addOffer(jsonAddOfferRequest);
        assertEquals(gson.toJson(""), jsonAddOfferResponse);

        server.stopServer(null);
    }

    @Ignore
    @Test
    public void testAddOfferIfVoterTokenIsOffline() throws IOException, VoterException {
        Server server = new Server();
        server.startServer(null);

        RegisterVoterDtoRequest registerRequest1 = new RegisterVoterDtoRequest(0, "sergei","sergeev","12345678");
        assertEquals("sergei", registerRequest1.getFirstName());
        assertEquals("sergeev", registerRequest1.getLogin());
        assertEquals("12345678", registerRequest1.getPassword());

        String jsonRegisterRequest = gson.toJson(registerRequest1);
        String jsonRegisterResponse  = server.registerVoter(jsonRegisterRequest);
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
        String jsonAddCandidateResponse1  = server.addCandidate(jsonAddCandidateRequest1);

        AddCandidateDtoResponse addCandidateDtoResponse1 = gson.fromJson(jsonAddCandidateResponse1, AddCandidateDtoResponse.class);
        String resultAddCandidateResponseJson1 = gson.toJson(addCandidateDtoResponse1);
        assertEquals(resultAddCandidateResponseJson1, jsonAddCandidateResponse1);

        RegisterVoterDtoRequest registerRequest2 = new RegisterVoterDtoRequest(1, "alex","alexandrov","12345678");
        assertEquals("alex", registerRequest2.getFirstName());
        assertEquals("alexandrov", registerRequest2.getLogin());
        assertEquals("12345678", registerRequest2.getPassword());

        String jsonRegisterRequest2 = gson.toJson(registerRequest2);
        String jsonRegisterResponse2  = server.registerVoter(jsonRegisterRequest2);

        RegisterVoterDtoResponse registerVoterDtoResponse2 = gson.fromJson(jsonRegisterResponse2, RegisterVoterDtoResponse.class);
        String resultRegisterJson2 = gson.toJson(registerVoterDtoResponse2);
        assertEquals(resultRegisterJson2, jsonRegisterResponse2);

        LogoutDtoRequest logoutDtoRequest = new LogoutDtoRequest(registerRequest2.getLogin(), registerVoterDtoResponse2.getToken());
        assertEquals("alexandrov", logoutDtoRequest.getLogin());
        assertEquals(registerVoterDtoResponse2.getToken(), logoutDtoRequest.getToken());

        String jsonLogoutRequest = gson.toJson(logoutDtoRequest);
        String jsonLogoutResponse = server.logout(jsonLogoutRequest);

        LogoutDtoResponse resultLogout = gson.fromJson(jsonLogoutResponse, LogoutDtoResponse.class);
        String offlineTokenJson = gson.toJson(resultLogout);
        assertEquals(offlineTokenJson, jsonLogoutResponse);

        String voterToken = registerVoterDtoResponse2.getToken();
        String candidateToken = addCandidateDtoResponse1.getToken();

        AddOfferDtoRequest addOfferDtoRequest = new AddOfferDtoRequest(voterToken, 1,candidateToken,0, "build a bridge across the river");
        assertEquals(candidateToken, addOfferDtoRequest.getCandidateToken());

        Map<String,Voter> tokensAndVoters = dataBase.getTokensAndVoters();
        assertTrue(tokensAndVoters.containsKey(voterToken));

        addOfferDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(2, addOfferDtoRequest.getTokensAndVoters().size());

        String jsonAddOfferRequest = gson.toJson(addOfferDtoRequest);
        String jsonAddOfferResponse = server.addOffer(jsonAddOfferRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonAddOfferResponse);

        server.stopServer(null);
    }

    @Test
    public void testAddOfferWrongCandidateToken1() throws IOException, VoterException {
        Server server = new Server();
        server.startServer(null);

        RegisterVoterDtoRequest registerVoterRequest = new RegisterVoterDtoRequest(0, "ivan","ivanov","12345678");
        assertEquals("ivan", registerVoterRequest.getFirstName());
        assertEquals("ivanov", registerVoterRequest.getLogin());
        assertEquals("12345678", registerVoterRequest.getPassword());

        String jsonRegisterRequest = gson.toJson(registerVoterRequest);
        String jsonRegisterResponse  = server.registerVoter(jsonRegisterRequest);
        RegisterVoterDtoResponse registerVoterDtoResponse = gson.fromJson(jsonRegisterResponse, RegisterVoterDtoResponse.class);
        String resultRegisterJson = gson.toJson(registerVoterDtoResponse);
        assertEquals(resultRegisterJson, jsonRegisterResponse);

        String voterToken = registerVoterDtoResponse.getToken();
        AddOfferDtoRequest addOfferDtoRequest = new AddOfferDtoRequest(voterToken,0, "",0, "build a bridge across the river");
        assertEquals("", addOfferDtoRequest.getCandidateToken());

        Map<String,Voter> tokensAndVoters = dataBase.getTokensAndVoters();
        addOfferDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(1, addOfferDtoRequest.getTokensAndVoters().size());

        String jsonAddOfferRequest = gson.toJson(addOfferDtoRequest);
        String jsonAddOfferResponse = server.addOffer(jsonAddOfferRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonAddOfferResponse);

        server.stopServer(null);
    }

    @Test
    public void testAddOfferWrongCandidateToken2() throws IOException, VoterException {
        Server server = new Server();
        server.startServer(null);

        RegisterVoterDtoRequest registerVoterRequest = new RegisterVoterDtoRequest(0, "stepan","stepanov","12345678");
        assertEquals("stepan", registerVoterRequest.getFirstName());
        assertEquals("stepanov", registerVoterRequest.getLogin());
        assertEquals("12345678", registerVoterRequest.getPassword());
        String jsonRegisterRequest = gson.toJson(registerVoterRequest);
        String jsonRegisterResponse  = server.registerVoter(jsonRegisterRequest);

        RegisterVoterDtoResponse registerVoterDtoResponse = gson.fromJson(jsonRegisterResponse, RegisterVoterDtoResponse.class);
        String resultRegisterJson = gson.toJson(registerVoterDtoResponse);
        assertEquals(resultRegisterJson, jsonRegisterResponse);

        String voterToken = registerVoterDtoResponse.getToken();
        AddOfferDtoRequest addOfferDtoRequest = new AddOfferDtoRequest(voterToken, 0,null, 0, "build a bridge across the river");
        assertEquals(null, addOfferDtoRequest.getCandidateToken());

        Map<String,Voter> tokensAndVoters = dataBase.getTokensAndVoters();
        addOfferDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(1, addOfferDtoRequest.getTokensAndVoters().size());

        String jsonAddOfferRequest = gson.toJson(addOfferDtoRequest);
        String jsonAddOfferResponse = server.addOffer(jsonAddOfferRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonAddOfferResponse);

        server.stopServer(null);
    }

    @Test
    public void testAddOfferWrongOfferDescription1() throws IOException, VoterException {
        Server server = new Server();
        server.startServer(null);

        RegisterVoterDtoRequest registerRequest1 = new RegisterVoterDtoRequest(0,"sergei","sergeev","12345678");
        assertEquals("sergei", registerRequest1.getFirstName());
        assertEquals("sergeev", registerRequest1.getLogin());
        assertEquals("12345678", registerRequest1.getPassword());

        String jsonRegisterRequest = gson.toJson(registerRequest1);
        String jsonRegisterResponse  = server.registerVoter(jsonRegisterRequest);
        RegisterVoterDtoResponse registerVoterDtoResponse = gson.fromJson(jsonRegisterResponse, RegisterVoterDtoResponse.class);
        String resultRegisterJson = gson.toJson(registerVoterDtoResponse);
        assertEquals(resultRegisterJson, jsonRegisterResponse);

        AddCandidateDtoRequest addCandidateRequest = new AddCandidateDtoRequest(registerRequest1.getId(),registerRequest1.getFirstName(), registerRequest1.getLogin(),
                                                                                registerRequest1.getPassword(), registerVoterDtoResponse.getToken());
        assertEquals(registerRequest1.getFirstName(), addCandidateRequest.getFirstName());
        assertEquals(registerRequest1.getLogin(), addCandidateRequest.getLogin());
        assertEquals(registerRequest1.getPassword(), addCandidateRequest.getPassword());
        assertEquals(registerVoterDtoResponse.getToken(), addCandidateRequest.getToken());

        String jsonAddCandidateRequest1 = gson.toJson(addCandidateRequest);
        String jsonAddCandidateResponse1  = server.addCandidate(jsonAddCandidateRequest1);
        String emptyJson = gson.toJson("");

        AddCandidateDtoResponse addCandidateDtoResponse1 = gson.fromJson(jsonAddCandidateResponse1, AddCandidateDtoResponse.class);
        String resultAddCandidateResponseJson1 = gson.toJson(addCandidateDtoResponse1);
        assertEquals(resultAddCandidateResponseJson1, jsonAddCandidateResponse1);

        RegisterVoterDtoRequest registerRequest2 = new RegisterVoterDtoRequest(1, "alex","alexandrov","12345678");
        assertEquals("alex", registerRequest2.getFirstName());
        assertEquals("alexandrov", registerRequest2.getLogin());
        assertEquals("12345678", registerRequest2.getPassword());

        String jsonRegisterRequest2 = gson.toJson(registerRequest2);
        String jsonRegisterResponse2  = server.registerVoter(jsonRegisterRequest2);

        RegisterVoterDtoResponse registerVoterDtoResponse2 = gson.fromJson(jsonRegisterResponse2, RegisterVoterDtoResponse.class);
        String resultRegisterJson2 = gson.toJson(registerVoterDtoResponse2);
        assertEquals(resultRegisterJson2, jsonRegisterResponse2);

        String voterToken = registerVoterDtoResponse2.getToken();
        String candidateToken = addCandidateDtoResponse1.getToken();

        AddOfferDtoRequest addOfferDtoRequest = new AddOfferDtoRequest(voterToken,1, candidateToken, 0,"");
        assertEquals("", addOfferDtoRequest.getOfferDescription());

        Map<String,Voter> tokensAndVoters = dataBase.getTokensAndVoters();
        addOfferDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(2, addOfferDtoRequest.getTokensAndVoters().size());

        String jsonAddOfferRequest = gson.toJson(addOfferDtoRequest);
        String jsonAddOfferResponse = server.addOffer(jsonAddOfferRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonAddOfferResponse);

        server.stopServer(null);
    }

    @Test
    public void testAddOfferWrongOfferDescription2() throws IOException, VoterException {
        Server server = new Server();
        server.startServer(null);

        RegisterVoterDtoRequest registerRequest1 = new RegisterVoterDtoRequest(0,"sergei","sergeev","12345678");
        assertEquals("sergei", registerRequest1.getFirstName());
        assertEquals("sergeev", registerRequest1.getLogin());
        assertEquals("12345678", registerRequest1.getPassword());

        String jsonRegisterRequest = gson.toJson(registerRequest1);
        String jsonRegisterResponse  = server.registerVoter(jsonRegisterRequest);
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
        String jsonAddCandidateResponse1  = server.addCandidate(jsonAddCandidateRequest1);
        String emptyJson = gson.toJson("");

        AddCandidateDtoResponse addCandidateDtoResponse1 = gson.fromJson(jsonAddCandidateResponse1, AddCandidateDtoResponse.class);
        String resultAddCandidateResponseJson1 = gson.toJson(addCandidateDtoResponse1);
        assertEquals(resultAddCandidateResponseJson1, jsonAddCandidateResponse1);

        RegisterVoterDtoRequest registerRequest2 = new RegisterVoterDtoRequest(1, "alex","alexandrov","12345678");
        assertEquals("alex", registerRequest2.getFirstName());
        assertEquals("alexandrov", registerRequest2.getLogin());
        assertEquals("12345678", registerRequest2.getPassword());

        String jsonRegisterRequest2 = gson.toJson(registerRequest2);
        String jsonRegisterResponse2  = server.registerVoter(jsonRegisterRequest2);

        RegisterVoterDtoResponse registerVoterDtoResponse2 = gson.fromJson(jsonRegisterResponse2, RegisterVoterDtoResponse.class);
        String resultRegisterJson2 = gson.toJson(registerVoterDtoResponse2);
        assertEquals(resultRegisterJson2, jsonRegisterResponse2);

        String voterToken = registerVoterDtoResponse2.getToken();
        String candidateToken = addCandidateDtoResponse1.getToken();

        AddOfferDtoRequest addOfferDtoRequest = new AddOfferDtoRequest(voterToken,0, candidateToken, 0,null);
        assertEquals(null, addOfferDtoRequest.getOfferDescription());

        Map<String, Voter> tokensAndVoters = dataBase.getTokensAndVoters();
        addOfferDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(2, addOfferDtoRequest.getTokensAndVoters().size());

        String jsonAddOfferRequest = gson.toJson(addOfferDtoRequest);
        String jsonAddOfferResponse = server.addOffer(jsonAddOfferRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonAddOfferResponse);

        server.stopServer(null);
    }

    @Test
    public void testAddOfferToElectionProgram() throws IOException, VoterException {
        Server server = new Server();
        server.startServer(null);

        RegisterVoterDtoRequest registerRequest1 = new RegisterVoterDtoRequest(0, "petr","petrov","12345678");
        assertEquals("petr", registerRequest1.getFirstName());
        assertEquals("petrov", registerRequest1.getLogin());
        assertEquals("12345678", registerRequest1.getPassword());
        String jsonRegisterRequest1 = gson.toJson(registerRequest1);
        String jsonRegisterResponse1  = server.registerVoter(jsonRegisterRequest1);

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
        String jsonAddCandidateResponse  = server.addCandidate(jsonAddCandidateRequest);

        AddCandidateDtoResponse addCandidateDtoResponse1 = gson.fromJson(jsonAddCandidateResponse, AddCandidateDtoResponse.class);
        String resultAddCandidateResponseJson1 = gson.toJson(addCandidateDtoResponse1);
        assertEquals(resultAddCandidateResponseJson1, jsonAddCandidateResponse);

        String candidateToken = addCandidateDtoResponse1.getToken();

        AddOfferToElectionProgramDtoRequest addOfferToElectionProgramDtoRequest = new AddOfferToElectionProgramDtoRequest(candidateToken, 0, "build a bridge across the river");
        assertEquals(candidateToken, addOfferToElectionProgramDtoRequest.getCandidateToken());
        assertEquals("build a bridge across the river", addOfferToElectionProgramDtoRequest.getOfferDescription());

        Map<String, Voter> tokensAndVoters = dataBase.getTokensAndVoters();
        addOfferToElectionProgramDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(1, addOfferToElectionProgramDtoRequest.getTokensAndVoters().size());

        String jsonAddOfferRequest = gson.toJson(addOfferToElectionProgramDtoRequest);
        String jsonAddOfferResponse = server.addOfferToElectionProgram(jsonAddOfferRequest);
        assertEquals(gson.toJson(""), jsonAddOfferResponse);

        server.stopServer(null);
    }

    @Test
    public void testAddOfferToElectionProgramWrongOfferDescription1() throws IOException, VoterException {
        Server server = new Server();
        server.startServer(null);

        RegisterVoterDtoRequest registerRequest1 = new RegisterVoterDtoRequest(0, "sergei","sergeev","12345678");
        assertEquals("sergei", registerRequest1.getFirstName());
        assertEquals("sergeev", registerRequest1.getLogin());
        assertEquals("12345678", registerRequest1.getPassword());

        String jsonRegisterRequest = gson.toJson(registerRequest1);
        String jsonRegisterResponse  = server.registerVoter(jsonRegisterRequest);
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
        String jsonAddCandidateResponse1  = server.addCandidate(jsonAddCandidateRequest1);

        AddCandidateDtoResponse addCandidateDtoResponse1 = gson.fromJson(jsonAddCandidateResponse1, AddCandidateDtoResponse.class);
        String resultAddCandidateResponseJson1 = gson.toJson(addCandidateDtoResponse1);
        assertEquals(resultAddCandidateResponseJson1, jsonAddCandidateResponse1);

        String candidateToken = addCandidateDtoResponse1.getToken();

        AddOfferToElectionProgramDtoRequest addOfferToElectionProgramDtoRequest = new AddOfferToElectionProgramDtoRequest(candidateToken, 0,"");
        assertEquals("", addOfferToElectionProgramDtoRequest.getOfferDescription());

        Map<String,Voter> tokensAndVoters = dataBase.getTokensAndVoters();
        addOfferToElectionProgramDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(1, addOfferToElectionProgramDtoRequest.getTokensAndVoters().size());

        String jsonAddOfferRequest = gson.toJson(addOfferToElectionProgramDtoRequest);
        String jsonAddOfferResponse = server.addOfferToElectionProgram(jsonAddOfferRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonAddOfferResponse);

        server.stopServer(null);
    }

    @Test
    public void testAddOfferToElectionProgramWrongOfferDescription2() throws IOException, VoterException {
        Server server = new Server();
        server.startServer(null);

        RegisterVoterDtoRequest registerRequest1 = new RegisterVoterDtoRequest(0, "konstantin","konstantinov","12345678");
        assertEquals("konstantin", registerRequest1.getFirstName());
        assertEquals("konstantinov", registerRequest1.getLogin());
        assertEquals("12345678", registerRequest1.getPassword());

        String jsonRegisterRequest = gson.toJson(registerRequest1);
        String jsonRegisterResponse  = server.registerVoter(jsonRegisterRequest);

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
        String jsonAddCandidateResponse1  = server.addCandidate(jsonAddCandidateRequest1);
        String emptyJson = gson.toJson("");

        AddCandidateDtoResponse addCandidateDtoResponse1 = gson.fromJson(jsonAddCandidateResponse1, AddCandidateDtoResponse.class);
        String resultAddCandidateResponseJson1 = gson.toJson(addCandidateDtoResponse1);
        assertEquals(resultAddCandidateResponseJson1, jsonAddCandidateResponse1);

        String candidateToken = addCandidateDtoResponse1.getToken();

        AddOfferToElectionProgramDtoRequest addOfferToElectionProgramDtoRequest = new AddOfferToElectionProgramDtoRequest(candidateToken, 0,null);
        assertEquals(null, addOfferToElectionProgramDtoRequest.getOfferDescription());

        Map<String,Voter> tokensAndVoters = dataBase.getTokensAndVoters();
        addOfferToElectionProgramDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(1, addOfferToElectionProgramDtoRequest.getTokensAndVoters().size());

        String jsonAddOfferRequest = gson.toJson(addOfferToElectionProgramDtoRequest);
        String jsonAddOfferResponse = server.addOfferToElectionProgram(jsonAddOfferRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonAddOfferResponse);

        server.stopServer(null);
    }

    @Ignore
    @Test
    public void testAddOfferToElectionProgramIfCandidateTokenIsOffline() throws IOException, VoterException {
        Server server = new Server();
        server.startServer(null);

        RegisterVoterDtoRequest registerRequest1 = new RegisterVoterDtoRequest(0, "boris","borisov","12345678");
        assertEquals("boris", registerRequest1.getFirstName());
        assertEquals("borisov", registerRequest1.getLogin());
        assertEquals("12345678", registerRequest1.getPassword());

        String jsonRegisterRequest = gson.toJson(registerRequest1);
        String jsonRegisterResponse  = server.registerVoter(jsonRegisterRequest);

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
        String jsonAddCandidateResponse1  = server.addCandidate(jsonAddCandidateRequest1);

        AddCandidateDtoResponse addCandidateDtoResponse1 = gson.fromJson(jsonAddCandidateResponse1, AddCandidateDtoResponse.class);
        String resultAddCandidateResponseJson1 = gson.toJson(addCandidateDtoResponse1);
        assertEquals(resultAddCandidateResponseJson1, jsonAddCandidateResponse1);

        RemoveCandidateDtoRequest removeCandidateDtoRequest = new RemoveCandidateDtoRequest(0, addCandidateDtoResponse1.getToken());
        assertEquals(addCandidateRequest.getLogin(), removeCandidateDtoRequest.getCandidateId());
        assertEquals(addCandidateRequest.getToken(), removeCandidateDtoRequest.getCandidateToken());

        String jsonRemoveCandidateRequest = gson.toJson(removeCandidateDtoRequest);
        String jsonRemoveCandidateResponse = server.removeCandidate(jsonRemoveCandidateRequest);
        assertEquals(gson.toJson(""), jsonRemoveCandidateResponse);

        LogoutDtoRequest logoutDtoRequest = new LogoutDtoRequest(addCandidateRequest.getLogin(), addCandidateDtoResponse1.getToken());
        assertEquals("borisov", logoutDtoRequest.getLogin());
        assertEquals(registerVoterDtoResponse.getToken(), addCandidateRequest.getToken());

        String jsonLogoutRequest = gson.toJson(logoutDtoRequest);
        String jsonLogoutResponse = server.logout(jsonLogoutRequest);

        LogoutDtoResponse resultLogout = gson.fromJson(jsonLogoutResponse, LogoutDtoResponse.class);
        String offlineTokenJson = gson.toJson(resultLogout);
        assertEquals(offlineTokenJson, jsonLogoutResponse);

        RegisterVoterDtoRequest registerRequest2 = new RegisterVoterDtoRequest(1, "alex","alexandrov","12345678");
        assertEquals("alex", registerRequest2.getFirstName());
        assertEquals("alexandrov", registerRequest2.getLogin());
        assertEquals("12345678", registerRequest2.getPassword());

        String jsonRegisterRequest2 = gson.toJson(registerRequest2);
        String jsonRegisterResponse2  = server.registerVoter(jsonRegisterRequest2);

        RegisterVoterDtoResponse registerVoterDtoResponse2 = gson.fromJson(jsonRegisterResponse2, RegisterVoterDtoResponse.class);
        String resultRegisterJson2 = gson.toJson(registerVoterDtoResponse2);
        assertEquals(resultRegisterJson2, jsonRegisterResponse2);

        String voterToken = registerVoterDtoResponse2.getToken();
        String candidateToken = addCandidateDtoResponse1.getToken();

        AddOfferDtoRequest addOfferDtoRequest = new AddOfferDtoRequest(voterToken, 1,candidateToken,0, "build a bridge across the river");
        assertEquals(candidateToken, addOfferDtoRequest.getCandidateToken());

        Map<String, Voter> tokensAndVoters = dataBase.getTokensAndVoters();
        assertTrue(tokensAndVoters.containsKey(candidateToken));

        addOfferDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(2, addOfferDtoRequest.getTokensAndVoters().size());

        String jsonAddOfferRequest = gson.toJson(addOfferDtoRequest);
        String jsonAddOfferResponse = server.addOfferToElectionProgram(jsonAddOfferRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonAddOfferResponse);

        server.stopServer(null);
    }

    @Ignore
    @Test
    public void testRemoveOfferFromElectionProgram() throws IOException, VoterException {
        Server server = new Server();
        server.startServer(null);

        RegisterVoterDtoRequest registerRequest1 = new RegisterVoterDtoRequest(0, "petr1","petrov1","12345678");
        assertEquals("petr1", registerRequest1.getFirstName());
        assertEquals("petrov1", registerRequest1.getLogin());
        assertEquals("12345678", registerRequest1.getPassword());
        String jsonRegisterRequest1 = gson.toJson(registerRequest1);
        String jsonRegisterResponse1  = server.registerVoter(jsonRegisterRequest1);

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
        String jsonAddCandidateResponse1  = server.addCandidate(jsonAddCandidateRequest1);

        AddCandidateDtoResponse addCandidateDtoResponse1 = gson.fromJson(jsonAddCandidateResponse1, AddCandidateDtoResponse.class);
        String resultAddCandidateResponseJson1 = gson.toJson(addCandidateDtoResponse1);
        assertEquals(resultAddCandidateResponseJson1, jsonAddCandidateResponse1);
        String candidateToken = addCandidateRequest.getToken();

        AddOfferToElectionProgramDtoRequest addOfferToElectionProgramDtoRequest = new AddOfferToElectionProgramDtoRequest(candidateToken, 0, "build a bridge across the river");
        assertEquals(candidateToken, addOfferToElectionProgramDtoRequest.getCandidateToken());
        assertEquals("build a bridge across the river", addOfferToElectionProgramDtoRequest.getOfferDescription());

        Map<String, Voter> tokensAndVoters = dataBase.getTokensAndVoters();
        addOfferToElectionProgramDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(1, addOfferToElectionProgramDtoRequest.getTokensAndVoters().size());

        String jsonAddOfferRequest = gson.toJson(addOfferToElectionProgramDtoRequest);
        String jsonAddOfferResponse = server.addOfferToElectionProgram(jsonAddOfferRequest);
        assertEquals(gson.toJson(""), jsonAddOfferResponse);

        RemoveOfferFromElectionProgramDtoRequest removeOfferFromElectionProgramDtoRequest = new RemoveOfferFromElectionProgramDtoRequest(candidateToken, 0, "build a bridge across the river");
        assertEquals(candidateToken, removeOfferFromElectionProgramDtoRequest.getCandidateToken());
        assertEquals("build a bridge across the river", removeOfferFromElectionProgramDtoRequest.getOfferDescription());

        removeOfferFromElectionProgramDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(1, removeOfferFromElectionProgramDtoRequest.getTokensAndVoters().size());

        String jsonRemoveOfferRequest = gson.toJson(removeOfferFromElectionProgramDtoRequest);
        String jsonRemoveOfferResponse = server.removeOfferFromElectionProgram(jsonRemoveOfferRequest);
        assertEquals(gson.toJson(""), jsonRemoveOfferResponse);

        server.stopServer(null);
    }

    @Test
    public void testRemoveOfferFromElectionProgramWrongOfferDescription1() throws IOException, VoterException {
        Server server = new Server();
        server.startServer(null);

        RegisterVoterDtoRequest registerRequest1 = new RegisterVoterDtoRequest(0, "semen1","semenov1","12345678");
        assertEquals("semen1", registerRequest1.getFirstName());
        assertEquals("semenov1", registerRequest1.getLogin());
        assertEquals("12345678", registerRequest1.getPassword());

        String jsonRegisterRequest = gson.toJson(registerRequest1);
        String jsonRegisterResponse  = server.registerVoter(jsonRegisterRequest);
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
        String jsonAddCandidateResponse1  = server.addCandidate(jsonAddCandidateRequest1);
        String emptyJson = gson.toJson("");

        AddCandidateDtoResponse addCandidateDtoResponse1 = gson.fromJson(jsonAddCandidateResponse1, AddCandidateDtoResponse.class);
        String resultAddCandidateResponseJson1 = gson.toJson(addCandidateDtoResponse1);
        assertEquals(resultAddCandidateResponseJson1, jsonAddCandidateResponse1);

        String candidateToken = addCandidateDtoResponse1.getToken();

        AddOfferToElectionProgramDtoRequest addOfferToElectionProgramDtoRequest = new AddOfferToElectionProgramDtoRequest(candidateToken, 0,"build a bridge across the river");
        assertEquals(candidateToken, addOfferToElectionProgramDtoRequest.getCandidateToken());
        assertEquals("build a bridge across the river", addOfferToElectionProgramDtoRequest.getOfferDescription());

        Map<String, Voter> tokensAndVoters = dataBase.getTokensAndVoters();
        addOfferToElectionProgramDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(1, addOfferToElectionProgramDtoRequest.getTokensAndVoters().size());

        String jsonAddOfferRequest = gson.toJson(addOfferToElectionProgramDtoRequest);
        String jsonAddOfferResponse = server.addOfferToElectionProgram(jsonAddOfferRequest);
        assertEquals(gson.toJson(""), jsonAddOfferResponse);

        RemoveOfferFromElectionProgramDtoRequest removeOfferFromElectionProgramDtoRequest = new RemoveOfferFromElectionProgramDtoRequest(candidateToken, 0, "");
        assertEquals(candidateToken, removeOfferFromElectionProgramDtoRequest.getCandidateToken());
        assertEquals("", removeOfferFromElectionProgramDtoRequest.getOfferDescription());

        removeOfferFromElectionProgramDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(1, removeOfferFromElectionProgramDtoRequest.getTokensAndVoters().size());

        String jsonRemoveOfferRequest = gson.toJson(removeOfferFromElectionProgramDtoRequest);
        String jsonRemoveOfferResponse = server.removeOfferFromElectionProgram(jsonRemoveOfferRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonRemoveOfferResponse);

        server.stopServer(null);
    }

    @Test
    public void testRemoveOfferFromElectionProgramWrongOfferDescription2() throws IOException, VoterException {
        Server server = new Server();
        server.startServer(null);

        RegisterVoterDtoRequest registerRequest1 = new RegisterVoterDtoRequest(0, "semen2","semenov2","12345678");
        assertEquals("semen2", registerRequest1.getFirstName());
        assertEquals("semenov2", registerRequest1.getLogin());
        assertEquals("12345678", registerRequest1.getPassword());

        String jsonRegisterRequest = gson.toJson(registerRequest1);
        String jsonRegisterResponse  = server.registerVoter(jsonRegisterRequest);
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
        String jsonAddCandidateResponse1  = server.addCandidate(jsonAddCandidateRequest1);

        AddCandidateDtoResponse addCandidateDtoResponse1 = gson.fromJson(jsonAddCandidateResponse1, AddCandidateDtoResponse.class);
        String resultAddCandidateResponseJson1 = gson.toJson(addCandidateDtoResponse1);
        assertEquals(resultAddCandidateResponseJson1, jsonAddCandidateResponse1);

        String candidateToken = addCandidateDtoResponse1.getToken();

        AddOfferToElectionProgramDtoRequest addOfferToElectionProgramDtoRequest = new AddOfferToElectionProgramDtoRequest(candidateToken, 0,"build a bridge across the river");
        assertEquals(candidateToken, addOfferToElectionProgramDtoRequest.getCandidateToken());
        assertEquals("build a bridge across the river", addOfferToElectionProgramDtoRequest.getOfferDescription());

        Map<String, Voter> tokensAndVoters = dataBase.getTokensAndVoters();
        addOfferToElectionProgramDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(1, addOfferToElectionProgramDtoRequest.getTokensAndVoters().size());

        String jsonAddOfferRequest = gson.toJson(addOfferToElectionProgramDtoRequest);
        String jsonAddOfferResponse = server.addOfferToElectionProgram(jsonAddOfferRequest);
        assertEquals(gson.toJson(""), jsonAddOfferResponse);

        RemoveOfferFromElectionProgramDtoRequest removeOfferFromElectionProgramDtoRequest = new RemoveOfferFromElectionProgramDtoRequest(candidateToken, 0, null);
        assertEquals(candidateToken, removeOfferFromElectionProgramDtoRequest.getCandidateToken());
        assertEquals(null, removeOfferFromElectionProgramDtoRequest.getOfferDescription());

        removeOfferFromElectionProgramDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(1, removeOfferFromElectionProgramDtoRequest.getTokensAndVoters().size());

        String jsonRemoveOfferRequest = gson.toJson(removeOfferFromElectionProgramDtoRequest);
        String jsonRemoveOfferResponse = server.removeOfferFromElectionProgram(jsonRemoveOfferRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonRemoveOfferResponse);

        server.stopServer(null);
    }

    @Ignore
    @Test
    public void testRemoveOfferFromElectionProgramIfCandidateTokenIsOffline() throws IOException, VoterException {
        Server server = new Server();
        server.startServer(null);

        RegisterVoterDtoRequest registerRequest1 = new RegisterVoterDtoRequest(0, "petr2","petrov2","12345678");
        assertEquals("petr2", registerRequest1.getFirstName());
        assertEquals("petrov2", registerRequest1.getLogin());
        assertEquals("12345678", registerRequest1.getPassword());
        String jsonRegisterRequest1 = gson.toJson(registerRequest1);
        String jsonRegisterResponse1  = server.registerVoter(jsonRegisterRequest1);

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
        String jsonAddCandidateResponse1  = server.addCandidate(jsonAddCandidateRequest1);

        AddCandidateDtoResponse addCandidateDtoResponse1 = gson.fromJson(jsonAddCandidateResponse1, AddCandidateDtoResponse.class);
        String resultAddCandidateResponseJson1 = gson.toJson(addCandidateDtoResponse1);
        assertEquals(resultAddCandidateResponseJson1, jsonAddCandidateResponse1);

        String candidateToken = addCandidateDtoResponse1.getToken();

        AddOfferToElectionProgramDtoRequest addOfferToElectionProgramDtoRequest = new AddOfferToElectionProgramDtoRequest(candidateToken, 0,"build a bridge across the river");
        assertEquals(candidateToken, addOfferToElectionProgramDtoRequest.getCandidateToken());
        assertEquals("build a bridge across the river", addOfferToElectionProgramDtoRequest.getOfferDescription());

        Map<String, Voter> tokensAndVoters = dataBase.getTokensAndVoters();
        addOfferToElectionProgramDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(1, addOfferToElectionProgramDtoRequest.getTokensAndVoters().size());

        String jsonAddOfferRequest = gson.toJson(addOfferToElectionProgramDtoRequest);
        String jsonAddOfferResponse = server.addOfferToElectionProgram(jsonAddOfferRequest);
        assertEquals(gson.toJson(""), jsonAddOfferResponse);

        RemoveOfferFromElectionProgramDtoRequest removeOfferFromElectionProgramDtoRequest = new RemoveOfferFromElectionProgramDtoRequest(candidateToken, 0, "build a bridge across the river");
        assertEquals(candidateToken, removeOfferFromElectionProgramDtoRequest.getCandidateToken());
        assertEquals("build a bridge across the river", removeOfferFromElectionProgramDtoRequest.getOfferDescription());

        tokensAndVoters.put(candidateToken, new Candidate("petr2","petrov2","12345678"));
        assertTrue(tokensAndVoters.containsKey(candidateToken));
        removeOfferFromElectionProgramDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(1, removeOfferFromElectionProgramDtoRequest.getTokensAndVoters().size());

        String jsonRemoveOfferRequest = gson.toJson(removeOfferFromElectionProgramDtoRequest);
        String jsonRemoveOfferResponse = server.removeOfferFromElectionProgram(jsonRemoveOfferRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonRemoveOfferResponse);

        server.stopServer(null);
    }

    @Test
    public void testChangeOfferRating() throws IOException, VoterException {
        Server server = new Server();
        server.startServer(null);

        RegisterVoterDtoRequest registerRequest1 = new RegisterVoterDtoRequest(0, "petr","petrov","12345678");
        assertEquals("petr", registerRequest1.getFirstName());
        assertEquals("petrov", registerRequest1.getLogin());
        assertEquals("12345678", registerRequest1.getPassword());
        String jsonRegisterRequest1 = gson.toJson(registerRequest1);
        String jsonRegisterResponse1  = server.registerVoter(jsonRegisterRequest1);

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
        String jsonAddCandidateResponse1  = server.addCandidate(jsonAddCandidateRequest1);

        AddCandidateDtoResponse addCandidateDtoResponse1 = gson.fromJson(jsonAddCandidateResponse1, AddCandidateDtoResponse.class);
        String resultAddCandidateResponseJson1 = gson.toJson(addCandidateDtoResponse1);
        assertEquals(resultAddCandidateResponseJson1, jsonAddCandidateResponse1);

        RegisterVoterDtoRequest registerRequest2 = new RegisterVoterDtoRequest(1, "boris1","borisov1","12345678");
        assertEquals("boris1", registerRequest2.getFirstName());
        assertEquals("borisov1", registerRequest2.getLogin());
        assertEquals("12345678", registerRequest2.getPassword());
        String jsonRegisterRequest2 = gson.toJson(registerRequest2);
        String jsonRegisterResponse2  = server.registerVoter(jsonRegisterRequest2);

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
        String jsonAddOfferResponse = server.addOffer(jsonAddOfferRequest);
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
        String jsonChangeRatingResponse = server.changeOfferRating(jsonChangeRatingRequest);
        assertEquals(gson.toJson(""), jsonChangeRatingResponse);

        server.stopServer(null);
    }

    @Test
    public void testChangeRatingWrongVoterToken1() throws IOException, VoterException {
        Server server = new Server();
        server.startServer(null);

        String authorToken = GenerateTokenService.generateNewToken();

        ChangeOfferRatingDtoRequest changeOfferRatingDtoRequest = new ChangeOfferRatingDtoRequest("",new Voter("ivan", "ivanov", "123456789"), authorToken,new Voter("petr", "petrov", "123456789"), 1);
        assertEquals("", changeOfferRatingDtoRequest.getVoterToken());

        Map<String,Voter> tokensAndVoters = dataBase.getTokensAndVoters();
        changeOfferRatingDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(0, changeOfferRatingDtoRequest.getTokensAndVoters().size());

        String jsonChangeRatingRequest = gson.toJson(changeOfferRatingDtoRequest);
        String jsonChangeRatingResponse = server.changeOfferRating(jsonChangeRatingRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonChangeRatingResponse);

        server.stopServer(null);
    }

    @Test
    public void testChangeRatingWrongVoterToken2() throws IOException, VoterException {
        Server server = new Server();
        server.startServer(null);

        String authorToken = GenerateTokenService.generateNewToken();

        ChangeOfferRatingDtoRequest changeOfferRatingDtoRequest = new ChangeOfferRatingDtoRequest(null,new Voter("ivan", "ivanov", "123456789"), authorToken,new Voter("petr", "petrov", "12345678"), 1);
        assertEquals(null, changeOfferRatingDtoRequest.getVoterToken());

        Map<String, Voter> tokensAndVoters = dataBase.getTokensAndVoters();
        changeOfferRatingDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(0, changeOfferRatingDtoRequest.getTokensAndVoters().size());

        String jsonChangeRatingRequest = gson.toJson(changeOfferRatingDtoRequest);
        String jsonChangeRatingResponse = server.changeOfferRating(jsonChangeRatingRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonChangeRatingResponse);

        server.stopServer(null);
    }

    @Test
    public void testChangeRatingWrongAuthorToken1() throws IOException, VoterException {
        Server server = new Server();
        server.startServer(null);

        String voterToken = GenerateTokenService.generateNewToken();

        ChangeOfferRatingDtoRequest changeOfferRatingDtoRequest = new ChangeOfferRatingDtoRequest(voterToken,new Voter("ivan", "ivanov", "123456789"), "",new Voter("petr", "petrov", "123456789"), 1);
        assertEquals("", changeOfferRatingDtoRequest.getAuthorToken());

        Map<String, Voter> tokensAndVoters = dataBase.getTokensAndVoters();
        changeOfferRatingDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(0, changeOfferRatingDtoRequest.getTokensAndVoters().size());

        String jsonChangeRatingRequest = gson.toJson(changeOfferRatingDtoRequest);
        String jsonChangeRatingResponse = server.changeOfferRating(jsonChangeRatingRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonChangeRatingResponse);

        server.stopServer(null);
    }

    @Test
    public void testChangeRatingWrongAuthorToken2() throws IOException, VoterException {
        Server server = new Server();
        server.startServer(null);

        String voterToken = GenerateTokenService.generateNewToken();

        ChangeOfferRatingDtoRequest changeOfferRatingDtoRequest = new ChangeOfferRatingDtoRequest(voterToken,new Voter("ivan", "ivanov", "123456789"), null, new Voter("petr", "petrov", "123456789"),1);
        assertEquals(null, changeOfferRatingDtoRequest.getAuthorToken());

        Map<String, Voter> tokensAndVoters = dataBase.getTokensAndVoters();
        changeOfferRatingDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(0, changeOfferRatingDtoRequest.getTokensAndVoters().size());

        String jsonChangeRatingRequest = gson.toJson(changeOfferRatingDtoRequest);
        String jsonChangeRatingResponse = server.changeOfferRating(jsonChangeRatingRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonChangeRatingResponse);

        server.stopServer(null);
    }

    @Test
    public void testChangeRatingWrongRating1() throws IOException, VoterException {
        Server server = new Server();
        server.startServer(null);

        String voterToken = GenerateTokenService.generateNewToken();
        String authorToken = GenerateTokenService.generateNewToken();

        ChangeOfferRatingDtoRequest changeOfferRatingDtoRequest = new ChangeOfferRatingDtoRequest(voterToken,new Voter("ivan", "ivanov", "123456789"), authorToken,new Voter("petr", "petrov", "123456789"), 0);
        assertEquals(0, changeOfferRatingDtoRequest.getRating());

        Map<String, Voter> tokensAndVoters = dataBase.getTokensAndVoters();
        changeOfferRatingDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(0, changeOfferRatingDtoRequest.getTokensAndVoters().size());

        String jsonChangeRatingRequest = gson.toJson(changeOfferRatingDtoRequest);
        String jsonChangeRatingResponse = server.changeOfferRating(jsonChangeRatingRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonChangeRatingResponse);

        server.stopServer(null);
    }

    @Test
    public void testChangeRatingWrongRating2() throws IOException, VoterException {
        Server server = new Server();
        server.startServer(null);

        String voterToken = GenerateTokenService.generateNewToken();
        String authorToken = GenerateTokenService.generateNewToken();

        ChangeOfferRatingDtoRequest changeOfferRatingDtoRequest = new ChangeOfferRatingDtoRequest(voterToken,new Voter("ivan", "ivanov", "123456789"), authorToken,new Voter("petr", "petrov", "123456789"), 6);
        assertEquals(6, changeOfferRatingDtoRequest.getRating());

        Map<String, Voter> tokensAndVoters = dataBase.getTokensAndVoters();
        changeOfferRatingDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(0, changeOfferRatingDtoRequest.getTokensAndVoters().size());

        String jsonChangeRatingRequest = gson.toJson(changeOfferRatingDtoRequest);
        String jsonChangeRatingResponse = server.changeOfferRating(jsonChangeRatingRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonChangeRatingResponse);

        server.stopServer(null);
    }

    @Test
    public void testRemoveOfferRating() throws IOException, VoterException {
        Server server = new Server();
        server.startServer(null);

        RegisterVoterDtoRequest registerRequest1 = new RegisterVoterDtoRequest(0, "sergei5","sergeev5","12345678");
        assertEquals("sergei5", registerRequest1.getFirstName());
        assertEquals("sergeev5", registerRequest1.getLogin());
        assertEquals("12345678", registerRequest1.getPassword());
        String jsonRegisterRequest1 = gson.toJson(registerRequest1);
        String jsonRegisterResponse1  = server.registerVoter(jsonRegisterRequest1);

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
        String jsonAddCandidateResponse1  = server.addCandidate(jsonAddCandidateRequest1);

        AddCandidateDtoResponse addCandidateDtoResponse1 = gson.fromJson(jsonAddCandidateResponse1, AddCandidateDtoResponse.class);
        String resultAddCandidateResponseJson1 = gson.toJson(addCandidateDtoResponse1);
        assertEquals(resultAddCandidateResponseJson1, jsonAddCandidateResponse1);

        RegisterVoterDtoRequest registerRequest2 = new RegisterVoterDtoRequest(1, "boris5","borisov5","12345678");
        assertEquals("boris5", registerRequest2.getFirstName());
        assertEquals("borisov5", registerRequest2.getLogin());
        assertEquals("12345678", registerRequest2.getPassword());
        String jsonRegisterRequest2 = gson.toJson(registerRequest2);
        String jsonRegisterResponse2  = server.registerVoter(jsonRegisterRequest2);

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
        String jsonAddOfferResponse = server.addOffer(jsonAddOfferRequest);
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
        String jsonRemoveRatingResponse = server.removeOfferRating(jsonRemoveRatingRequest);
        assertEquals(gson.toJson(""), jsonRemoveRatingResponse);

        server.stopServer(null);
    }

    @Test
    public void testRemoveRatingWrongVoterToken1() throws IOException, VoterException {
        Server server = new Server();
        server.startServer(null);

        String authorToken = GenerateTokenService.generateNewToken();

        Voter voter = new Voter("ivan", "ivanov", "123456789");
        Voter author = new Candidate("sergei", "sergeev", "12345678");

        RemoveOfferRatingDtoRequest removeOfferRatingDtoRequest = new RemoveOfferRatingDtoRequest("", authorToken, voter, author);
        assertEquals("", removeOfferRatingDtoRequest.getVoterToken());

        dataBase.getTokensAndVoters().put(removeOfferRatingDtoRequest.getVoterToken(), voter);
        removeOfferRatingDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
        assertEquals(1, removeOfferRatingDtoRequest.getTokensAndVoters().size());

        String jsonChangeRatingRequest = gson.toJson(removeOfferRatingDtoRequest);
        String jsonChangeRatingResponse = server.removeOfferRating(jsonChangeRatingRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonChangeRatingResponse);

        server.stopServer(null);
    }

    @Test
    public void testRemoveRatingWrongVoterToken2() throws IOException, VoterException {
        Server server = new Server();
        server.startServer(null);

        String authorToken = GenerateTokenService.generateNewToken();

        Voter voter = new Voter("ivan", "ivanov", "123456789");
        Voter author = new Candidate("sergei", "sergeev", "12345678");

        RemoveOfferRatingDtoRequest removeOfferRatingDtoRequest = new RemoveOfferRatingDtoRequest(null, authorToken, voter, author);
        assertEquals(null, removeOfferRatingDtoRequest.getVoterToken());

        dataBase.getTokensAndVoters().put(removeOfferRatingDtoRequest.getVoterToken(), voter);
        removeOfferRatingDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
        assertEquals(1, removeOfferRatingDtoRequest.getTokensAndVoters().size());

        String jsonChangeRatingRequest = gson.toJson(removeOfferRatingDtoRequest);
        String jsonChangeRatingResponse = server.removeOfferRating(jsonChangeRatingRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonChangeRatingResponse);

        server.stopServer(null);
    }

    @Test
    public void testRemoveRatingWrongAuthorToken1() throws IOException, VoterException {
        Server server = new Server();
        server.startServer(null);

        String voterToken = GenerateTokenService.generateNewToken();

        Voter voter = new Voter("ivan", "ivanov", "123456789");
        Voter author = new Candidate("sergei", "sergeev", "12345678");

        RemoveOfferRatingDtoRequest removeOfferRatingDtoRequest = new RemoveOfferRatingDtoRequest( voterToken, "", voter, author);
        assertEquals("", removeOfferRatingDtoRequest.getAuthorToken());

        dataBase.getTokensAndVoters().put(removeOfferRatingDtoRequest.getVoterToken(), voter);
        removeOfferRatingDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
        assertEquals(1, removeOfferRatingDtoRequest.getTokensAndVoters().size());

        String jsonChangeRatingRequest = gson.toJson(removeOfferRatingDtoRequest);
        String jsonChangeRatingResponse = server.changeOfferRating(jsonChangeRatingRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonChangeRatingResponse);

        server.stopServer(null);
    }

    @Test
    public void testRemoveRatingWrongAuthorToken2() throws IOException, VoterException {
        Server server = new Server();
        server.startServer(null);

        String voterToken = GenerateTokenService.generateNewToken();

        Voter voter = new Voter("ivan", "ivanov", "123456789");
        Voter author = new Candidate("sergei", "sergeev", "12345678");

        RemoveOfferRatingDtoRequest removeOfferRatingDtoRequest = new RemoveOfferRatingDtoRequest( voterToken, null, voter, author);
        assertEquals(null, removeOfferRatingDtoRequest.getAuthorToken());

        dataBase.getTokensAndVoters().put(removeOfferRatingDtoRequest.getVoterToken(), voter);
        removeOfferRatingDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
        assertEquals(1, removeOfferRatingDtoRequest.getTokensAndVoters().size());

        String jsonChangeRatingRequest = gson.toJson(removeOfferRatingDtoRequest);
        String jsonChangeRatingResponse = server.changeOfferRating(jsonChangeRatingRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonChangeRatingResponse);

        server.stopServer(null);
    }

    @Test
    public void testGetOffersAndAverageRatings() throws IOException, VoterException {
        Server server = new Server();
        server.startServer(null);

        RegisterVoterDtoRequest registerRequest1 = new RegisterVoterDtoRequest(0, "miron","mironov","12345678");
        assertEquals("miron", registerRequest1.getFirstName());
        assertEquals("mironov", registerRequest1.getLogin());
        assertEquals("12345678", registerRequest1.getPassword());
        String jsonRegisterRequest1 = gson.toJson(registerRequest1);
        String jsonRegisterResponse1  = server.registerVoter(jsonRegisterRequest1);

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
        String jsonAddCandidateResponse1  = server.addCandidate(jsonAddCandidateRequest1);

        AddCandidateDtoResponse addCandidateDtoResponse1 = gson.fromJson(jsonAddCandidateResponse1, AddCandidateDtoResponse.class);
        String resultAddCandidateResponseJson1 = gson.toJson(addCandidateDtoResponse1);
        assertEquals(resultAddCandidateResponseJson1, jsonAddCandidateResponse1);

        RegisterVoterDtoRequest registerRequest2 = new RegisterVoterDtoRequest(1, "egor","egorov","12345678");
        assertEquals("egor", registerRequest2.getFirstName());
        assertEquals("egorov", registerRequest2.getLogin());
        assertEquals("12345678", registerRequest2.getPassword());
        String jsonRegisterRequest2 = gson.toJson(registerRequest2);
        String jsonRegisterResponse2  = server.registerVoter(jsonRegisterRequest2);

        RegisterVoterDtoResponse registerVoterDtoResponse2 = gson.fromJson(jsonRegisterResponse2, RegisterVoterDtoResponse.class);
        String resultRegisterJson2 = gson.toJson(registerVoterDtoResponse2);
        assertEquals(resultRegisterJson2, jsonRegisterResponse2);

        String voterToken1 = registerVoterDtoResponse2.getToken();
        String voterToken2 = addCandidateDtoResponse1.getToken();
        String candidateToken = addCandidateDtoResponse1.getToken();

        AddOfferDtoRequest addOfferDtoRequest1 = new AddOfferDtoRequest(voterToken1,1, candidateToken,0, "build a bridge across the river");
        assertEquals(voterToken1, addOfferDtoRequest1.getVoterToken());
        assertEquals(candidateToken, addOfferDtoRequest1.getCandidateToken());
        assertEquals("build a bridge across the river", addOfferDtoRequest1.getOfferDescription());

        Map<String, Voter> tokensAndVoters = dataBase.getTokensAndVoters();
        addOfferDtoRequest1.setTokensAndVoters(tokensAndVoters);
        assertEquals(2, addOfferDtoRequest1.getTokensAndVoters().size());

        String jsonAddOfferRequest = gson.toJson(addOfferDtoRequest1);
        String jsonAddOfferResponse = server.addOffer(jsonAddOfferRequest);
        assertEquals(gson.toJson(""), jsonAddOfferResponse);

        AddOfferDtoRequest addOfferDtoRequest2 = new AddOfferDtoRequest(voterToken2,2, candidateToken,0, "repair the road");
        assertEquals(voterToken2, addOfferDtoRequest2.getVoterToken());
        assertEquals(candidateToken, addOfferDtoRequest2.getCandidateToken());
        assertEquals("repair the road", addOfferDtoRequest2.getOfferDescription());

        addOfferDtoRequest2.setTokensAndVoters(tokensAndVoters);
        assertEquals(2, addOfferDtoRequest2.getTokensAndVoters().size());

        String jsonAddOfferRequest2 = gson.toJson(addOfferDtoRequest2);
        String jsonAddOfferResponse2 = server.addOffer(jsonAddOfferRequest2);
        assertEquals(gson.toJson(""), jsonAddOfferResponse2);

        Voter voter = dataBase.getVoterByLogin(registerRequest2.getLogin());
        Voter author = dataBase.getCandidateByLogin(addCandidateRequest.getLogin());
        ChangeOfferRatingDtoRequest changeOfferRatingDtoRequest = new ChangeOfferRatingDtoRequest(voterToken1,voter, voterToken2,author, 1);
        assertEquals(voterToken1, changeOfferRatingDtoRequest.getVoterToken());
        assertEquals(1, changeOfferRatingDtoRequest.getRating());

        changeOfferRatingDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(2, changeOfferRatingDtoRequest.getTokensAndVoters().size());

        String jsonChangeRatingRequest = gson.toJson(changeOfferRatingDtoRequest);
        String jsonChangeRatingResponse = server.changeOfferRating(jsonChangeRatingRequest);
        assertEquals(gson.toJson(""), jsonChangeRatingResponse);

        GetOffersAndAverageRatingsDtoRequest OffersAndAverageRatingsDtoRequest = new GetOffersAndAverageRatingsDtoRequest(voterToken1);
        assertEquals(voterToken1, OffersAndAverageRatingsDtoRequest.getToken());
        String jsonGetOffersSortedByAverageRatingsRequest = gson.toJson(OffersAndAverageRatingsDtoRequest);
        String jsonGetOffersSortedByAverageRatingsResponse = server.getOffersAndAverageRatings(jsonGetOffersSortedByAverageRatingsRequest);

        GetOffersAndAverageRatingsDtoResponse OffersAndAverageRatingsDtoResponse = gson.fromJson(jsonGetOffersSortedByAverageRatingsResponse, GetOffersAndAverageRatingsDtoResponse.class);
        String resultGetOffersSortedByAverageRatings = new GsonBuilder().enableComplexMapKeySerialization()
                .create()
                .toJson(OffersAndAverageRatingsDtoResponse);
        assertEquals(resultGetOffersSortedByAverageRatings, jsonGetOffersSortedByAverageRatingsResponse);
        assertThat(OffersAndAverageRatingsDtoResponse.getOffersSortedByAverageRatings().values(), contains(5, 1));

        server.stopServer(null);
    }

    @Test
    public void testGetOffersAndAverageRatingsIfOfferWithoutRatings() throws IOException, VoterException {
        Server server = new Server();
        server.startServer(null);

        RegisterVoterDtoRequest registerRequest1 = new RegisterVoterDtoRequest(0, "miron","mironov","12345678");
        assertEquals("miron", registerRequest1.getFirstName());
        assertEquals("mironov", registerRequest1.getLogin());
        assertEquals("12345678", registerRequest1.getPassword());
        String jsonRegisterRequest1 = gson.toJson(registerRequest1);
        String jsonRegisterResponse1  = server.registerVoter(jsonRegisterRequest1);

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
        String jsonAddCandidateResponse1  = server.addCandidate(jsonAddCandidateRequest1);

        AddCandidateDtoResponse addCandidateDtoResponse1 = gson.fromJson(jsonAddCandidateResponse1, AddCandidateDtoResponse.class);
        String resultAddCandidateResponseJson1 = gson.toJson(addCandidateDtoResponse1);
        assertEquals(resultAddCandidateResponseJson1, jsonAddCandidateResponse1);

        RegisterVoterDtoRequest registerRequest2 = new RegisterVoterDtoRequest(1, "egor","egorov","12345678");
        assertEquals("egor", registerRequest2.getFirstName());
        assertEquals("egorov", registerRequest2.getLogin());
        assertEquals("12345678", registerRequest2.getPassword());
        String jsonRegisterRequest2 = gson.toJson(registerRequest2);
        String jsonRegisterResponse2  = server.registerVoter(jsonRegisterRequest2);

        RegisterVoterDtoResponse registerVoterDtoResponse2 = gson.fromJson(jsonRegisterResponse2, RegisterVoterDtoResponse.class);
        String resultRegisterJson2 = gson.toJson(registerVoterDtoResponse2);
        assertEquals(resultRegisterJson2, jsonRegisterResponse2);

        RegisterVoterDtoRequest registerRequest3 = new RegisterVoterDtoRequest(2, "efim","efimov","12345678");
        assertEquals("efim", registerRequest3.getFirstName());
        assertEquals("efimov", registerRequest3.getLogin());
        assertEquals("12345678", registerRequest3.getPassword());
        String jsonRegisterRequest3 = gson.toJson(registerRequest3);
        String jsonRegisterResponse3  = server.registerVoter(jsonRegisterRequest3);

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
        String jsonAddOfferResponse = server.addOffer(jsonAddOfferRequest);
        assertEquals(gson.toJson(""), jsonAddOfferResponse);

        AddOfferDtoRequest addOfferDtoRequest2 = new AddOfferDtoRequest(voterToken2,2, candidateToken,0, "repair the road");
        assertEquals(voterToken2, addOfferDtoRequest2.getVoterToken());
        assertEquals(candidateToken, addOfferDtoRequest2.getCandidateToken());
        assertEquals("repair the road", addOfferDtoRequest2.getOfferDescription());

        addOfferDtoRequest2.setTokensAndVoters(tokensAndVoters);
        assertEquals(3, addOfferDtoRequest2.getTokensAndVoters().size());

        String jsonAddOfferRequest2 = gson.toJson(addOfferDtoRequest2);
        String jsonAddOfferResponse2 = server.addOffer(jsonAddOfferRequest2);
        assertEquals(gson.toJson(""), jsonAddOfferResponse2);

        Voter voter = dataBase.getVoterById(registerRequest2.getId());
        Voter author = dataBase.getCandidateById(addCandidateRequest.getId());

        RemoveOfferRatingDtoRequest removeOfferRatingDtoRequest = new RemoveOfferRatingDtoRequest(voterToken1, candidateToken, voter, author);
        assertEquals(voterToken1, removeOfferRatingDtoRequest.getVoterToken());
        assertEquals(candidateToken, removeOfferRatingDtoRequest.getAuthorToken());

        removeOfferRatingDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(3, removeOfferRatingDtoRequest.getTokensAndVoters().size());

        String jsonRemoveRatingRequest = gson.toJson(removeOfferRatingDtoRequest);
        String jsonRemoveRatingResponse = server.removeOfferRating(jsonRemoveRatingRequest);
        assertEquals(gson.toJson(""), jsonRemoveRatingResponse);

        server.stopServer(null);
    }

    @Test
    public void testGetOffersAndAverageRatingsWrongToken1() throws IOException, VoterException {
        Server server = new Server();
        server.startServer(null);

        GetOffersAndAverageRatingsDtoRequest OffersAndAverageRatingsDtoRequest = new GetOffersAndAverageRatingsDtoRequest(null);
        assertEquals(null, OffersAndAverageRatingsDtoRequest.getToken());

        String jsonRequest = gson.toJson(OffersAndAverageRatingsDtoRequest);
        String jsonResponse = server.getOffersAndAverageRatings(jsonRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonResponse);

        server.stopServer(null);
    }

    @Test
    public void testGetOffersAndAverageRatingsWrongToken2() throws IOException, VoterException {
        Server server = new Server();
        server.startServer(null);

        GetOffersAndAverageRatingsDtoRequest OffersAndAverageRatingsDtoRequest = new GetOffersAndAverageRatingsDtoRequest("");
        assertEquals("", OffersAndAverageRatingsDtoRequest.getToken());

        String jsonRequest = gson.toJson(OffersAndAverageRatingsDtoRequest);
        String jsonResponse = server.getOffersAndAverageRatings(jsonRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonResponse);

        server.stopServer(null);
    }

    @Test
    public void testGetOffersAndAverageRatingsOfflineToken() throws IOException, VoterException {
        Server server = new Server();
        server.startServer(null);

        RegisterVoterDtoRequest registerVoterRequest = new RegisterVoterDtoRequest(0, "abram","abramov","123456789");
        assertEquals("abram", registerVoterRequest.getFirstName());
        assertEquals("abramov", registerVoterRequest.getLogin());
        assertEquals("123456789", registerVoterRequest.getPassword());

        String jsonRegisterRequest = gson.toJson(registerVoterRequest);
        String jsonRegisterResponse  = server.registerVoter(jsonRegisterRequest);
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
        String jsonResponse = server.getOffersAndAverageRatings(jsonRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonResponse);

        server.stopServer(null);
    }

    @Ignore
    @Test
    public void testGetOffersSortedByAverageRatings() throws IOException, VoterException {
        Server server = new Server();
        server.startServer(null);

        RegisterVoterDtoRequest registerRequest1 = new RegisterVoterDtoRequest(0, "miron","mironov","12345678");
        assertEquals("miron", registerRequest1.getFirstName());
        assertEquals("mironov", registerRequest1.getLogin());
        assertEquals("12345678", registerRequest1.getPassword());
        String jsonRegisterRequest1 = gson.toJson(registerRequest1);
        String jsonRegisterResponse1  = server.registerVoter(jsonRegisterRequest1);

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
        String jsonAddCandidateResponse1  = server.addCandidate(jsonAddCandidateRequest1);

        AddCandidateDtoResponse addCandidateDtoResponse1 = gson.fromJson(jsonAddCandidateResponse1, AddCandidateDtoResponse.class);
        String resultAddCandidateResponseJson1 = gson.toJson(addCandidateDtoResponse1);
        assertEquals(resultAddCandidateResponseJson1, jsonAddCandidateResponse1);

        RegisterVoterDtoRequest registerRequest2 = new RegisterVoterDtoRequest(1, "egor","egorov","12345678");
        assertEquals("egor", registerRequest2.getFirstName());
        assertEquals("egorov", registerRequest2.getLogin());
        assertEquals("12345678", registerRequest2.getPassword());
        String jsonRegisterRequest2 = gson.toJson(registerRequest2);
        String jsonRegisterResponse2  = server.registerVoter(jsonRegisterRequest2);

        RegisterVoterDtoResponse registerVoterDtoResponse2 = gson.fromJson(jsonRegisterResponse2, RegisterVoterDtoResponse.class);
        String resultRegisterJson2 = gson.toJson(registerVoterDtoResponse2);
        assertEquals(resultRegisterJson2, jsonRegisterResponse2);

        RegisterVoterDtoRequest registerRequest3 = new RegisterVoterDtoRequest(2, "efim","efimov","12345678");
        assertEquals("efim", registerRequest3.getFirstName());
        assertEquals("efimov", registerRequest3.getLogin());
        assertEquals("12345678", registerRequest3.getPassword());
        String jsonRegisterRequest3 = gson.toJson(registerRequest3);
        String jsonRegisterResponse3  = server.registerVoter(jsonRegisterRequest3);

        RegisterVoterDtoResponse registerVoterDtoResponse3 = gson.fromJson(jsonRegisterResponse3, RegisterVoterDtoResponse.class);
        String resultRegisterJson3 = gson.toJson(registerVoterDtoResponse3);
        assertEquals(resultRegisterJson3, jsonRegisterResponse3);

        String voterToken1 = registerVoterDtoResponse2.getToken();
        String voterToken2 = registerVoterDtoResponse3.getToken();
        String candidateToken = addCandidateDtoResponse1.getToken();

        AddOfferDtoRequest addOfferDtoRequest1 = new AddOfferDtoRequest(voterToken1,1, candidateToken, 0,"build a bridge across the river");
        assertEquals(voterToken1, addOfferDtoRequest1.getVoterToken());
        assertEquals(candidateToken, addOfferDtoRequest1.getCandidateToken());
        assertEquals("build a bridge across the river", addOfferDtoRequest1.getOfferDescription());

        Map<String, Voter> tokensAndVoters = dataBase.getTokensAndVoters();
        addOfferDtoRequest1.setTokensAndVoters(tokensAndVoters);
        assertEquals(3, addOfferDtoRequest1.getTokensAndVoters().size());

        String jsonAddOfferRequest = gson.toJson(addOfferDtoRequest1);
        String jsonAddOfferResponse = server.addOffer(jsonAddOfferRequest);
        assertEquals(gson.toJson(""), jsonAddOfferResponse);

        AddOfferDtoRequest addOfferDtoRequest2 = new AddOfferDtoRequest(voterToken2,2, candidateToken,0, "repair the road");
        assertEquals(voterToken2, addOfferDtoRequest2.getVoterToken());
        assertEquals(candidateToken, addOfferDtoRequest2.getCandidateToken());
        assertEquals("repair the road", addOfferDtoRequest2.getOfferDescription());

        addOfferDtoRequest2.setTokensAndVoters(tokensAndVoters);
        assertEquals(3, addOfferDtoRequest2.getTokensAndVoters().size());

        String jsonAddOfferRequest2 = gson.toJson(addOfferDtoRequest2);
        String jsonAddOfferResponse2 = server.addOffer(jsonAddOfferRequest2);
        assertEquals(gson.toJson(""), jsonAddOfferResponse2);

        Voter voter = dataBase.getVoterByLogin(registerRequest2.getLogin());
        Voter author = dataBase.getVoterByLogin(registerRequest3.getLogin());
        ChangeOfferRatingDtoRequest changeOfferRatingDtoRequest = new ChangeOfferRatingDtoRequest(voterToken2, voter, voterToken1, author,1);
        assertEquals(voterToken2, changeOfferRatingDtoRequest.getVoterToken());
        assertEquals(1, changeOfferRatingDtoRequest.getRating());

        changeOfferRatingDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(3, changeOfferRatingDtoRequest.getTokensAndVoters().size());

        String jsonChangeRatingRequest = gson.toJson(changeOfferRatingDtoRequest);
        String jsonChangeRatingResponse = server.changeOfferRating(jsonChangeRatingRequest);
        assertEquals(gson.toJson(""), jsonChangeRatingResponse);

        GetOffersSortedByAverageRatingsDtoRequest offersSortedByAverageRatingsDtoRequest = new GetOffersSortedByAverageRatingsDtoRequest(voterToken1);
        assertEquals(voterToken1, offersSortedByAverageRatingsDtoRequest.getToken());
        String jsonGetOffersSortedByAverageRatingsRequest = gson.toJson(offersSortedByAverageRatingsDtoRequest);
        String jsonGetOffersSortedByAverageRatingsResponse = server.getOffersSortedByAverageRatings(jsonGetOffersSortedByAverageRatingsRequest);

        GetOffersSortedByAverageRatingsDtoResponse OffersSortedByAverageRatingsDtoResponse = gson.fromJson(jsonGetOffersSortedByAverageRatingsResponse, GetOffersSortedByAverageRatingsDtoResponse.class);
        String resultGetOffersSortedByAverageRatings = gson.toJson(OffersSortedByAverageRatingsDtoResponse);
        assertEquals(resultGetOffersSortedByAverageRatings, jsonGetOffersSortedByAverageRatingsResponse);

        server.stopServer(null);
    }

    @Test
    public void testGetOffersSortedByAverageRatingsWrongToken1() throws IOException, VoterException {
        Server server = new Server();
        server.startServer(null);

        GetOffersSortedByAverageRatingsDtoRequest offersSortedByAverageRatingsDtoRequest = new GetOffersSortedByAverageRatingsDtoRequest(null);
        assertEquals(null, offersSortedByAverageRatingsDtoRequest.getToken());

        String jsonRequest = gson.toJson(offersSortedByAverageRatingsDtoRequest);
        String jsonResponse = server.getOffersAndAverageRatings(jsonRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonResponse);

        server.stopServer(null);
    }

    @Test
    public void testGetOffersSortedByAverageRatingsWrongToken2() throws IOException, VoterException {
        Server server = new Server();
        server.startServer(null);

        GetOffersSortedByAverageRatingsDtoRequest offersSortedByAverageRatingsDtoRequest = new GetOffersSortedByAverageRatingsDtoRequest("");
        assertEquals("", offersSortedByAverageRatingsDtoRequest.getToken());

        String jsonRequest = gson.toJson(offersSortedByAverageRatingsDtoRequest);
        String jsonResponse = server.getOffersAndAverageRatings(jsonRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonResponse);

        server.stopServer(null);
    }

    @Ignore
    @Test
    public void testGetOffersSortedByAverageRatingsOfflineToken() throws IOException, VoterException {
        Server server = new Server();
        server.startServer(null);

        RegisterVoterDtoRequest registerVoterRequest = new RegisterVoterDtoRequest(0, "abram","abramov","123456789");
        assertEquals("abram", registerVoterRequest.getFirstName());
        assertEquals("abramov", registerVoterRequest.getLogin());
        assertEquals("123456789", registerVoterRequest.getPassword());

        String jsonRegisterRequest = gson.toJson(registerVoterRequest);
        String jsonRegisterResponse  = server.registerVoter(jsonRegisterRequest);

        RegisterVoterDtoResponse registerVoterDtoResponse = gson.fromJson(jsonRegisterResponse, RegisterVoterDtoResponse.class);
        String resultRegisterJson = gson.toJson(registerVoterDtoResponse);
        assertEquals(resultRegisterJson, jsonRegisterResponse);

        GetOffersSortedByAverageRatingsDtoRequest offersSortedByAverageRatingsDtoRequest = new GetOffersSortedByAverageRatingsDtoRequest(registerVoterDtoResponse.getToken());
        assertEquals(offersSortedByAverageRatingsDtoRequest.getToken(), registerVoterDtoResponse.getToken());

        DataBase dataBase = DataBase.getDataBase();
        Map<String, Voter> tokensAndVoters = dataBase.getTokensAndVoters();
        tokensAndVoters.put(registerVoterDtoResponse.getToken(), new Voter("abram","abramov","123456789"));
        offersSortedByAverageRatingsDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(tokensAndVoters, offersSortedByAverageRatingsDtoRequest.getTokensAndVoters());

        String jsonRequest = gson.toJson(offersSortedByAverageRatingsDtoRequest);
        String jsonResponse = server.getOffersAndAverageRatings(jsonRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonResponse);

        server.stopServer(null);
    }

    @Ignore
    @Test
    public void testVoteForCandidateIfVoterVote() throws IOException, VoterException {
        Server server = new Server();
        server.startServer(null);

        RegisterVoterDtoRequest registerRequest1 = new RegisterVoterDtoRequest(0, "petr","petrov","12345678");
        assertEquals("petr", registerRequest1.getFirstName());
        assertEquals("petrov", registerRequest1.getLogin());
        assertEquals("12345678", registerRequest1.getPassword());
        String jsonRegisterRequest1 = gson.toJson(registerRequest1);
        String jsonRegisterResponse1  = server.registerVoter(jsonRegisterRequest1);

        RegisterVoterDtoResponse registerVoterDtoResponse1 = gson.fromJson(jsonRegisterResponse1, RegisterVoterDtoResponse.class);
        String resultRegisterJson1 = gson.toJson(registerVoterDtoResponse1);
        assertEquals(resultRegisterJson1, jsonRegisterResponse1);

        AddCandidateDtoRequest addCandidateRequest = new AddCandidateDtoRequest(registerRequest1.getId(), registerRequest1.getFirstName(), registerRequest1.getLogin(),
                                                                                registerRequest1.getPassword(), registerVoterDtoResponse1.getToken());
        assertEquals(registerRequest1.getFirstName(), addCandidateRequest.getFirstName());
        assertEquals(registerRequest1.getLogin(), addCandidateRequest.getLogin());
        assertEquals(registerRequest1.getPassword(), addCandidateRequest.getPassword());

        String jsonAddCandidateRequest1 = gson.toJson(addCandidateRequest);
        String jsonAddCandidateResponse1  = server.addCandidate(jsonAddCandidateRequest1);

        AddCandidateDtoResponse addCandidateDtoResponse1 = gson.fromJson(jsonAddCandidateResponse1, AddCandidateDtoResponse.class);
        String resultAddCandidateResponseJson1 = gson.toJson(addCandidateDtoResponse1);
        assertEquals(resultAddCandidateResponseJson1, jsonAddCandidateResponse1);

        RegisterVoterDtoRequest registerRequest2 = new RegisterVoterDtoRequest(1, "boris","borisov","12345678");
        assertEquals("boris", registerRequest2.getFirstName());
        assertEquals("borisov", registerRequest2.getLogin());
        assertEquals("12345678", registerRequest2.getPassword());
        String jsonRegisterRequest2 = gson.toJson(registerRequest2);
        String jsonRegisterResponse2  = server.registerVoter(jsonRegisterRequest2);

        RegisterVoterDtoResponse registerVoterDtoResponse2 = gson.fromJson(jsonRegisterResponse2, RegisterVoterDtoResponse.class);
        String resultRegisterJson2 = gson.toJson(registerVoterDtoResponse2);
        assertEquals(resultRegisterJson2, jsonRegisterResponse2);

        String voterToken = registerVoterDtoResponse2.getToken();
        String candidateToken = addCandidateDtoResponse1.getToken();

        AddOfferDtoRequest addOfferDtoRequest = new AddOfferDtoRequest(voterToken,1, candidateToken,0, "build a bridge across the river");
        assertEquals(voterToken, addOfferDtoRequest.getVoterToken());
        assertEquals(candidateToken, addOfferDtoRequest.getCandidateToken());
        assertEquals("build a bridge across the river", addOfferDtoRequest.getOfferDescription());

        Map<String,Voter> tokensAndVoters = dataBase.getTokensAndVoters();
        addOfferDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(2, addOfferDtoRequest.getTokensAndVoters().size());

        String jsonAddOfferRequest = gson.toJson(addOfferDtoRequest);
        String jsonAddOfferResponse = server.addOffer(jsonAddOfferRequest);
        assertEquals(gson.toJson(""), jsonAddOfferResponse);

        VoteForCandidateDtoRequest voteForCandidateDtoRequest = new VoteForCandidateDtoRequest(1,0,voterToken, candidateToken);
        assertEquals(voterToken, voteForCandidateDtoRequest.getVotingToken());
        assertEquals(candidateToken, voteForCandidateDtoRequest.getCandidateToken());
        voteForCandidateDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(2, voteForCandidateDtoRequest.getTokensAndVoters().size());

        String jsonVoterForCandidateRequest = gson.toJson(voteForCandidateDtoRequest);
        String jsonVoterForCandidateResponse = server.voteForCandidate(jsonVoterForCandidateRequest);
        assertEquals(gson.toJson(""), jsonVoterForCandidateResponse);

        assertEquals(0, dataBase.getVotedCandidates().size());
        assertEquals(1, dataBase.getVotedVoters().size());
        assertTrue(dataBase.getVotedVoters().contains(voterToken));

        Candidate candidate = dataBase.getCandidateByToken(candidateToken);
        assertEquals(1, candidate.getAmountVotes());
        assertEquals(1, dataBase.getRankedCandidates().size());
        assertTrue(dataBase.getRankedCandidates().contains(candidate));

        server.stopServer(null);
    }

    @Test
    public void testVoteForCandidateIfVoterVoteTwice() throws IOException, VoterException {
        Server server = new Server();
        server.startServer(null);

        RegisterVoterDtoRequest registerRequest1 = new RegisterVoterDtoRequest(0, "petr","petrov","12345678");
        assertEquals("petr", registerRequest1.getFirstName());
        assertEquals("petrov", registerRequest1.getLogin());
        assertEquals("12345678", registerRequest1.getPassword());
        String jsonRegisterRequest1 = gson.toJson(registerRequest1);
        String jsonRegisterResponse1  = server.registerVoter(jsonRegisterRequest1);

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
        String jsonAddCandidateResponse  = server.addCandidate(jsonAddCandidateRequest);

        AddCandidateDtoResponse addCandidateDtoResponse1 = gson.fromJson(jsonAddCandidateResponse, AddCandidateDtoResponse.class);
        String resultAddCandidateResponseJson1 = gson.toJson(addCandidateDtoResponse1);
        assertEquals(resultAddCandidateResponseJson1, jsonAddCandidateResponse);

        RegisterVoterDtoRequest registerRequest2 = new RegisterVoterDtoRequest(1, "boris","borisov","12345678");
        assertEquals("boris", registerRequest2.getFirstName());
        assertEquals("borisov", registerRequest2.getLogin());
        assertEquals("12345678", registerRequest2.getPassword());
        String jsonRegisterRequest2 = gson.toJson(registerRequest2);
        String jsonRegisterResponse2  = server.registerVoter(jsonRegisterRequest2);

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
        String jsonAddOfferResponse = server.addOffer(jsonAddOfferRequest);
        assertEquals(gson.toJson(""), jsonAddOfferResponse);

        VoteForCandidateDtoRequest voteForCandidateDtoRequest1 = new VoteForCandidateDtoRequest(1,0,voterToken, candidateToken);
        assertEquals(voterToken, voteForCandidateDtoRequest1.getVotingToken());
        assertEquals(candidateToken, voteForCandidateDtoRequest1.getCandidateToken());
        voteForCandidateDtoRequest1.setTokensAndVoters(tokensAndVoters);
        assertEquals(2, voteForCandidateDtoRequest1.getTokensAndVoters().size());

        String jsonVoterForCandidateRequest1 = gson.toJson(voteForCandidateDtoRequest1);
        String jsonVoterForCandidateResponse1 = server.voteForCandidate(jsonVoterForCandidateRequest1);
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
        String jsonVoterForCandidateResponse2 = server.voteForCandidate(jsonVoterForCandidateRequest2);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonVoterForCandidateResponse2);

        assertEquals(0, dataBase.getVotedCandidates().size());
        assertEquals(1, dataBase.getVotedVoters().size());

        assertEquals(1, dataBase.getRankedCandidates().size());
        assertEquals(1, candidate.getAmountVotes());

        server.stopServer(null);
    }

    @Ignore
    @Test
    public void testVoteForCandidateIfCandidateVote() throws IOException, VoterException {
        Server server = new Server();
        server.startServer(null);

        RegisterVoterDtoRequest registerRequest1 = new RegisterVoterDtoRequest(0, "petr","petrov","12345678");
        assertEquals("petr", registerRequest1.getFirstName());
        assertEquals("petrov", registerRequest1.getLogin());
        assertEquals("12345678", registerRequest1.getPassword());
        String jsonRegisterRequest1 = gson.toJson(registerRequest1);
        String jsonRegisterResponse1  = server.registerVoter(jsonRegisterRequest1);

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
        String jsonAddCandidateResponse  = server.addCandidate(jsonAddCandidateRequest);
        assertEquals(gson.toJson(""), jsonAddCandidateResponse);

        RegisterVoterDtoRequest registerRequest2 = new RegisterVoterDtoRequest(1, "boris","borisov","12345678");
        assertEquals("boris", registerRequest2.getFirstName());
        assertEquals("borisov", registerRequest2.getLogin());
        assertEquals("12345678", registerRequest2.getPassword());
        String jsonRegisterRequest2 = gson.toJson(registerRequest2);
        String jsonRegisterResponse2  = server.registerVoter(jsonRegisterRequest2);

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
        String jsonAddCandidateResponse2  = server.addCandidate(jsonAddCandidateRequest2);
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
        String jsonAddOfferResponse = server.addOfferToElectionProgram(jsonAddOfferRequest);
        assertEquals(gson.toJson(""), jsonAddOfferResponse);
        assertEquals(1, votingCandidate.getElectionProgram().size());

        AddOfferToElectionProgramDtoRequest addOfferToElectionProgramDtoRequest2 = new AddOfferToElectionProgramDtoRequest(candidateToken, 1, "build a bridge across the river");
        assertEquals(candidateToken, addOfferToElectionProgramDtoRequest2.getCandidateToken());
        assertEquals("build a bridge across the river", addOfferToElectionProgramDtoRequest2.getOfferDescription());

        addOfferToElectionProgramDtoRequest2.setTokensAndVoters(tokensAndVoters);
        assertEquals(0, addOfferToElectionProgramDtoRequest2.getTokensAndVoters().size());

        String jsonAddOfferRequest2 = gson.toJson(addOfferToElectionProgramDtoRequest2);
        String jsonAddOfferResponse2 = server.addOfferToElectionProgram(jsonAddOfferRequest2);
        assertEquals(gson.toJson(""), jsonAddOfferResponse2);
        assertEquals(1, candidate.getElectionProgram().size());

        VoteForCandidateDtoRequest voteForCandidateDtoRequest = new VoteForCandidateDtoRequest(0,1,votingCandidateToken, candidateToken);
        assertEquals(votingCandidateToken, voteForCandidateDtoRequest.getVotingToken());
        assertEquals(candidateToken, voteForCandidateDtoRequest.getCandidateToken());
        voteForCandidateDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(0, voteForCandidateDtoRequest.getTokensAndVoters().size());

        String jsonVoterForCandidateRequest = gson.toJson(voteForCandidateDtoRequest);
        String jsonVoterForCandidateResponse = server.voteForCandidate(jsonVoterForCandidateRequest);
        assertEquals(gson.toJson(""), jsonVoterForCandidateResponse);

        assertEquals(0, dataBase.getVotedVoters().size());
        assertEquals(1, dataBase.getVotedCandidates().size());
        assertTrue(dataBase.getVotedCandidates().contains(votingCandidateToken));

        assertEquals(1, candidate.getAmountVotes());
        assertEquals(1, dataBase.getRankedCandidates().size());
        assertTrue(dataBase.getRankedCandidates().contains(candidate));

        server.stopServer(null);
    }

    @Ignore
    @Test
    public void testVoteForCandidateIfCandidateVoteTwice() throws IOException, VoterException {
        Server server = new Server();
        server.startServer(null);

        RegisterVoterDtoRequest registerRequest1 = new RegisterVoterDtoRequest(0, "petr","petrov","12345678");
        assertEquals("petr", registerRequest1.getFirstName());
        assertEquals("petrov", registerRequest1.getLogin());
        assertEquals("12345678", registerRequest1.getPassword());
        String jsonRegisterRequest1 = gson.toJson(registerRequest1);
        String jsonRegisterResponse1  = server.registerVoter(jsonRegisterRequest1);

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
        String jsonAddCandidateResponse1  = server.addCandidate(jsonAddCandidateRequest1);

        AddCandidateDtoResponse addCandidateDtoResponse1 = gson.fromJson(jsonAddCandidateResponse1, AddCandidateDtoResponse.class);
        String resultAddCandidateResponseJson1 = gson.toJson(addCandidateDtoResponse1);
        assertEquals(resultAddCandidateResponseJson1, jsonAddCandidateResponse1);

        RegisterVoterDtoRequest registerRequest2 = new RegisterVoterDtoRequest(1, "boris","borisov","12345678");
        assertEquals("boris", registerRequest2.getFirstName());
        assertEquals("borisov", registerRequest2.getLogin());
        assertEquals("12345678", registerRequest2.getPassword());
        String jsonRegisterRequest2 = gson.toJson(registerRequest2);
        String jsonRegisterResponse2  = server.registerVoter(jsonRegisterRequest2);

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
        String jsonAddCandidateResponse2  = server.addCandidate(jsonAddCandidateRequest2);

        AddCandidateDtoResponse addCandidateDtoResponse2 = gson.fromJson(jsonAddCandidateResponse2, AddCandidateDtoResponse.class);
        String resultAddCandidateResponseJson2 = gson.toJson(addCandidateDtoResponse2);
        assertEquals(resultAddCandidateResponseJson2, jsonAddCandidateResponse2);

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
        String jsonAddOfferResponse = server.addOfferToElectionProgram(jsonAddOfferRequest);
        assertEquals(gson.toJson(""), jsonAddOfferResponse);
        assertEquals(1, votingCandidate.getElectionProgram().size());

        AddOfferToElectionProgramDtoRequest addOfferToElectionProgramDtoRequest2 = new AddOfferToElectionProgramDtoRequest(candidateToken, 1,"build a bridge across the river");
        assertEquals(candidateToken, addOfferToElectionProgramDtoRequest2.getCandidateToken());
        assertEquals("build a bridge across the river", addOfferToElectionProgramDtoRequest2.getOfferDescription());

        addOfferToElectionProgramDtoRequest2.setTokensAndVoters(tokensAndVoters);
        assertEquals(2, addOfferToElectionProgramDtoRequest2.getTokensAndVoters().size());

        String jsonAddOfferRequest2 = gson.toJson(addOfferToElectionProgramDtoRequest2);
        String jsonAddOfferResponse2 = server.addOfferToElectionProgram(jsonAddOfferRequest2);
        assertEquals(gson.toJson(""), jsonAddOfferResponse2);
        assertEquals(1, candidate.getElectionProgram().size());

        VoteForCandidateDtoRequest voteForCandidateDtoRequest1 = new VoteForCandidateDtoRequest(0,1,votingCandidateToken, candidateToken);
        assertEquals(votingCandidateToken, voteForCandidateDtoRequest1.getVotingToken());
        assertEquals(candidateToken, voteForCandidateDtoRequest1.getCandidateToken());
        voteForCandidateDtoRequest1.setTokensAndVoters(tokensAndVoters);
        assertEquals(2, voteForCandidateDtoRequest1.getTokensAndVoters().size());

        String jsonVoterForCandidateRequest1 = gson.toJson(voteForCandidateDtoRequest1);
        String jsonVoterForCandidateResponse1 = server.voteForCandidate(jsonVoterForCandidateRequest1);
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
        assertEquals(2, voteForCandidateDtoRequest1.getTokensAndVoters().size());

        String jsonVoterForCandidateRequest2 = gson.toJson(voteForCandidateDtoRequest2);
        String jsonVoterForCandidateResponse2 = server.voteForCandidate(jsonVoterForCandidateRequest2);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonVoterForCandidateResponse2);

        assertEquals(0, dataBase.getVotedVoters().size());
        assertEquals(1, dataBase.getVotedCandidates().size());
        assertTrue(dataBase.getVotedCandidates().contains(votingCandidateToken));

        assertEquals(1, candidate.getAmountVotes());
        assertEquals(1, dataBase.getRankedCandidates().size());

        server.stopServer(null);
    }

    @Test
    public void testVoteForCandidateIfCandidateWithoutElectionProgram() throws IOException, VoterException {
        Server server = new Server();
        server.startServer(null);

        RegisterVoterDtoRequest registerRequest1 = new RegisterVoterDtoRequest(0, "petr","petrov","12345678");
        assertEquals("petr", registerRequest1.getFirstName());
        assertEquals("petrov", registerRequest1.getLogin());
        assertEquals("12345678", registerRequest1.getPassword());
        String jsonRegisterRequest1 = gson.toJson(registerRequest1);
        String jsonRegisterResponse1  = server.registerVoter(jsonRegisterRequest1);

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
        String jsonAddCandidateResponse1  = server.addCandidate(jsonAddCandidateRequest1);

        AddCandidateDtoResponse addCandidateDtoResponse1 = gson.fromJson(jsonAddCandidateResponse1, AddCandidateDtoResponse.class);
        String resultAddCandidateResponseJson1 = gson.toJson(addCandidateDtoResponse1);
        assertEquals(resultAddCandidateResponseJson1, jsonAddCandidateResponse1);

        RegisterVoterDtoRequest registerRequest2 = new RegisterVoterDtoRequest(1, "boris","borisov","12345678");
        assertEquals("boris", registerRequest2.getFirstName());
        assertEquals("borisov", registerRequest2.getLogin());
        assertEquals("12345678", registerRequest2.getPassword());
        String jsonRegisterRequest2 = gson.toJson(registerRequest2);
        String jsonRegisterResponse2  = server.registerVoter(jsonRegisterRequest2);

        RegisterVoterDtoResponse registerVoterDtoResponse2 = gson.fromJson(jsonRegisterResponse2, RegisterVoterDtoResponse.class);
        String resultRegisterJson2 = gson.toJson(registerVoterDtoResponse2);
        assertEquals(resultRegisterJson2, jsonRegisterResponse2);

        String voterToken = registerVoterDtoResponse2.getToken();
        String candidateToken = addCandidateDtoResponse1.getToken();

        Candidate candidate = dataBase.getCandidateById(addCandidateRequest.getId());
        assertTrue(candidate.getElectionProgram().isEmpty());
        assertEquals(0, candidate.getElectionProgram().size());

        VoteForCandidateDtoRequest voteForCandidateDtoRequest = new VoteForCandidateDtoRequest(1,0,voterToken, candidateToken);
        assertEquals(voterToken, voteForCandidateDtoRequest.getVotingToken());
        assertEquals(candidateToken, voteForCandidateDtoRequest.getCandidateToken());

        Map<String,Voter> tokensAndVoters = dataBase.getTokensAndVoters();
        voteForCandidateDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(2, voteForCandidateDtoRequest.getTokensAndVoters().size());

        String jsonVoterForCandidateRequest = gson.toJson(voteForCandidateDtoRequest);
        String jsonVoterForCandidateResponse = server.voteForCandidate(jsonVoterForCandidateRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonVoterForCandidateResponse);
        assertEquals(0, candidate.getAmountVotes());
        assertTrue(dataBase.getOffersAndAverageRatings().isEmpty());
        assertTrue(dataBase.getVotedVoters().isEmpty());
        assertTrue(dataBase.getVotedCandidates().isEmpty());

        server.stopServer(null);
    }

    @Test
    public void testVoteForCandidateIfVotingCandidateWithoutElectionProgram() throws IOException, VoterException {
        Server server = new Server();
        server.startServer(null);

        RegisterVoterDtoRequest registerRequest1 = new RegisterVoterDtoRequest(0, "petr","petrov","12345678");
        assertEquals("petr", registerRequest1.getFirstName());
        assertEquals("petrov", registerRequest1.getLogin());
        assertEquals("12345678", registerRequest1.getPassword());
        String jsonRegisterRequest1 = gson.toJson(registerRequest1);
        String jsonRegisterResponse1  = server.registerVoter(jsonRegisterRequest1);

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
        String jsonAddCandidateResponse1  = server.addCandidate(jsonAddCandidateRequest1);

        AddCandidateDtoResponse addCandidateDtoResponse1 = gson.fromJson(jsonAddCandidateResponse1, AddCandidateDtoResponse.class);
        String resultAddCandidateResponseJson1 = gson.toJson(addCandidateDtoResponse1);
        assertEquals(resultAddCandidateResponseJson1, jsonAddCandidateResponse1);

        RegisterVoterDtoRequest registerRequest2 = new RegisterVoterDtoRequest(1, "boris","borisov","12345678");
        assertEquals("boris", registerRequest2.getFirstName());
        assertEquals("borisov", registerRequest2.getLogin());
        assertEquals("12345678", registerRequest2.getPassword());
        String jsonRegisterRequest2 = gson.toJson(registerRequest2);
        String jsonRegisterResponse2  = server.registerVoter(jsonRegisterRequest2);

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
        String jsonAddCandidateResponse2  = server.addCandidate(jsonAddCandidateRequest2);

        AddCandidateDtoResponse addCandidateDtoResponse2 = gson.fromJson(jsonAddCandidateResponse2, AddCandidateDtoResponse.class);
        String resultAddCandidateResponseJson2 = gson.toJson(addCandidateDtoResponse2);
        assertEquals(resultAddCandidateResponseJson2, jsonAddCandidateResponse2);

        String candidateToken = addCandidateDtoResponse1.getToken();
        String votingCandidateToken = addCandidateDtoResponse2.getToken();
        Candidate candidate = dataBase.getCandidateById(addCandidateRequest1.getId());
        Candidate votingCandidate = dataBase.getCandidateById(addCandidateRequest2.getId());

        AddOfferToElectionProgramDtoRequest addOfferToElectionProgramDtoRequest = new AddOfferToElectionProgramDtoRequest(candidateToken, 0,"repair the road");
        assertEquals(candidateToken, addOfferToElectionProgramDtoRequest.getCandidateToken());
        assertEquals("repair the road", addOfferToElectionProgramDtoRequest.getOfferDescription());

        Map<String,Voter> tokensAndVoters = dataBase.getTokensAndVoters();
        addOfferToElectionProgramDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(2, addOfferToElectionProgramDtoRequest.getTokensAndVoters().size());

        String jsonAddOfferRequest = gson.toJson(addOfferToElectionProgramDtoRequest);
        String jsonAddOfferResponse = server.addOfferToElectionProgram(jsonAddOfferRequest);
        assertEquals(gson.toJson(""), jsonAddOfferResponse);

        assertEquals(1, candidate.getElectionProgram().size());
        assertEquals(0, votingCandidate.getElectionProgram().size());

        VoteForCandidateDtoRequest voteForCandidateDtoRequest = new VoteForCandidateDtoRequest(1,0,votingCandidateToken, candidateToken);
        assertEquals(votingCandidateToken, voteForCandidateDtoRequest.getVotingToken());
        assertEquals(candidateToken, voteForCandidateDtoRequest.getCandidateToken());

        voteForCandidateDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(2, voteForCandidateDtoRequest.getTokensAndVoters().size());

        String jsonVoterForCandidateRequest = gson.toJson(voteForCandidateDtoRequest);
        String jsonVoterForCandidateResponse = server.voteForCandidate(jsonVoterForCandidateRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonVoterForCandidateResponse);

        assertEquals(0, candidate.getAmountVotes());
        assertTrue(dataBase.getOffersAndAverageRatings().isEmpty());
        assertTrue(dataBase.getVotedVoters().isEmpty());
        assertTrue(dataBase.getVotedCandidates().isEmpty());

        server.stopServer(null);
    }

    @Ignore
    @Test
    public void testVoteForCandidateIfCandidateVoteForYourself() throws IOException, VoterException {
        Server server = new Server();
        server.startServer(null);

        RegisterVoterDtoRequest registerRequest1 = new RegisterVoterDtoRequest(0, "petr","petrov","12345678");
        assertEquals("petr", registerRequest1.getFirstName());
        assertEquals("petrov", registerRequest1.getLogin());
        assertEquals("12345678", registerRequest1.getPassword());
        String jsonRegisterRequest1 = gson.toJson(registerRequest1);
        String jsonRegisterResponse1  = server.registerVoter(jsonRegisterRequest1);

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
        String jsonAddCandidateResponse1  = server.addCandidate(jsonAddCandidateRequest1);

        AddCandidateDtoResponse addCandidateDtoResponse1 = gson.fromJson(jsonAddCandidateResponse1, AddCandidateDtoResponse.class);
        String resultAddCandidateResponseJson1 = gson.toJson(addCandidateDtoResponse1);
        assertEquals(resultAddCandidateResponseJson1, jsonAddCandidateResponse1);

        String votingCandidateToken = addCandidateDtoResponse1.getToken();
        Candidate votingCandidate = dataBase.getCandidateById(addCandidateRequest1.getId());

        AddOfferToElectionProgramDtoRequest addOfferToElectionProgramDtoRequest1 = new AddOfferToElectionProgramDtoRequest(votingCandidateToken, 0,"repair the road");
        assertEquals(votingCandidateToken, addOfferToElectionProgramDtoRequest1.getCandidateToken());
        assertEquals("repair the road", addOfferToElectionProgramDtoRequest1.getOfferDescription());

        Map<String,Voter> tokensAndVoters = dataBase.getTokensAndVoters();
        addOfferToElectionProgramDtoRequest1.setTokensAndVoters(tokensAndVoters);
        assertEquals(1, addOfferToElectionProgramDtoRequest1.getTokensAndVoters().size());

        String jsonAddOfferRequest = gson.toJson(addOfferToElectionProgramDtoRequest1);
        String jsonAddOfferResponse = server.addOfferToElectionProgram(jsonAddOfferRequest);
        assertEquals(gson.toJson(""), jsonAddOfferResponse);
        assertFalse(votingCandidate.getElectionProgram().isEmpty());
        assertEquals(1, votingCandidate.getElectionProgram().size());

        VoteForCandidateDtoRequest voteForCandidateDtoRequest = new VoteForCandidateDtoRequest(0,0,votingCandidateToken, votingCandidateToken);
        assertEquals(votingCandidateToken, voteForCandidateDtoRequest.getVotingToken());
        assertEquals(votingCandidateToken, voteForCandidateDtoRequest.getCandidateToken());
        voteForCandidateDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(1, voteForCandidateDtoRequest.getTokensAndVoters().size());

        String jsonVoterForCandidateRequest = gson.toJson(voteForCandidateDtoRequest);
        String jsonVoterForCandidateResponse = server.voteForCandidate(jsonVoterForCandidateRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonVoterForCandidateResponse);

        assertEquals(0, votingCandidate.getAmountVotes());
        assertTrue(dataBase.getOffersAndAverageRatings().isEmpty());
        assertTrue(dataBase.getVotedVoters().isEmpty());
        assertTrue(dataBase.getVotedCandidates().isEmpty());

        server.stopServer(null);
    }

    @Test
    public void testVoteAgainstAllIfVoterVote() throws IOException,VoterException {
        Server server = new Server();
        server.startServer(null);

        RegisterVoterDtoRequest registerRequest = new RegisterVoterDtoRequest(0, "boris","borisov","12345678");
        assertEquals("boris", registerRequest.getFirstName());
        assertEquals("borisov", registerRequest.getLogin());
        assertEquals("12345678", registerRequest.getPassword());
        String jsonRegisterRequest2 = gson.toJson(registerRequest);
        String jsonRegisterResponse2  = server.registerVoter(jsonRegisterRequest2);

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
        String jsonVoteAgainstAllResponse = server.voteAgainstAll(jsonVoteAgainstAllRequest);
        assertEquals(gson.toJson(""), jsonVoteAgainstAllResponse);

        assertEquals(0, dataBase.getVotedCandidates().size());
        assertEquals(0, dataBase.getVotedVoters().size());
        assertEquals(1, dataBase.getVotedAgainstAll().size());
        assertTrue(dataBase.getVotedAgainstAll().contains(voter));

        server.stopServer(null);
    }

    @Test
    public void testVoteAgainstAllIfVoterVoteTwice1() throws IOException, VoterException {
        Server server = new Server();
        server.startServer(null);

        RegisterVoterDtoRequest registerRequest = new RegisterVoterDtoRequest(0, "boris","borisov","12345678");
        assertEquals("boris", registerRequest.getFirstName());
        assertEquals("borisov", registerRequest.getLogin());
        assertEquals("12345678", registerRequest.getPassword());
        String jsonRegisterRequest2 = gson.toJson(registerRequest);
        String jsonRegisterResponse2  = server.registerVoter(jsonRegisterRequest2);

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
        String jsonVoteAgainstAllResponse1 = server.voteAgainstAll(jsonVoteAgainstAllRequest1);
        assertEquals(gson.toJson(""), jsonVoteAgainstAllResponse1);

        assertEquals(0, dataBase.getVotedCandidates().size());
        assertEquals(0, dataBase.getVotedVoters().size());

        assertEquals(1, dataBase.getVotedAgainstAll().size());

        VoteAgainstAllDtoRequest voteAgainstAllDtoRequest2 = new VoteAgainstAllDtoRequest(voterToken,voter);
        assertEquals(voterToken, voteAgainstAllDtoRequest2.getVotingToken());
        voteAgainstAllDtoRequest2.setTokensAndVoters(dataBase.getTokensAndVoters());
        assertEquals(1, voteAgainstAllDtoRequest2.getTokensAndVoters().size());

        String jsonVoteAgainstAllRequest2 = gson.toJson(voteAgainstAllDtoRequest2);
        String jsonVoteAgainstAllResponse2 = server.voteAgainstAll(jsonVoteAgainstAllRequest2);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonVoteAgainstAllResponse2);

        assertEquals(0, dataBase.getVotedCandidates().size());
        assertEquals(0, dataBase.getVotedVoters().size());

        assertEquals(1, dataBase.getVotedAgainstAll().size());

        server.stopServer(null);
    }

    @Test
    public void testVoteAgainstAllIfVoterVoteTwice2() throws IOException, VoterException {
        Server server = new Server();
        server.startServer(null);

        RegisterVoterDtoRequest registerRequest1 = new RegisterVoterDtoRequest(0, "petr","petrov","12345678");
        assertEquals("petr", registerRequest1.getFirstName());
        assertEquals("petrov", registerRequest1.getLogin());
        assertEquals("12345678", registerRequest1.getPassword());
        String jsonRegisterRequest1 = gson.toJson(registerRequest1);
        String jsonRegisterResponse1  = server.registerVoter(jsonRegisterRequest1);

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
        String jsonAddCandidateResponse  = server.addCandidate(jsonAddCandidateRequest);

        AddCandidateDtoResponse addCandidateDtoResponse1 = gson.fromJson(jsonAddCandidateResponse, AddCandidateDtoResponse.class);
        String resultAddCandidateResponseJson1 = gson.toJson(addCandidateDtoResponse1);
        assertEquals(resultAddCandidateResponseJson1, jsonAddCandidateResponse);

        RegisterVoterDtoRequest registerRequest2 = new RegisterVoterDtoRequest(1, "boris","borisov","12345678");
        assertEquals("boris", registerRequest2.getFirstName());
        assertEquals("borisov", registerRequest2.getLogin());
        assertEquals("12345678", registerRequest2.getPassword());
        String jsonRegisterRequest2 = gson.toJson(registerRequest2);
        String jsonRegisterResponse2  = server.registerVoter(jsonRegisterRequest2);

        RegisterVoterDtoResponse registerVoterDtoResponse2 = gson.fromJson(jsonRegisterResponse2, RegisterVoterDtoResponse.class);
        String resultRegisterJson2 = gson.toJson(registerVoterDtoResponse2);
        assertEquals(resultRegisterJson2, jsonRegisterResponse2);

        String voterToken = registerVoterDtoResponse2.getToken();
        String candidateToken = addCandidateDtoResponse1.getToken();

        AddOfferDtoRequest addOfferDtoRequest = new AddOfferDtoRequest(voterToken,1, candidateToken,0, "build a bridge across the river");
        assertEquals(voterToken, addOfferDtoRequest.getVoterToken());
        assertEquals(candidateToken, addOfferDtoRequest.getCandidateToken());
        assertEquals("build a bridge across the river", addOfferDtoRequest.getOfferDescription());

        Map<String,Voter> tokensAndVoters = dataBase.getTokensAndVoters();
        addOfferDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(2, addOfferDtoRequest.getTokensAndVoters().size());

        String jsonAddOfferRequest = gson.toJson(addOfferDtoRequest);
        String jsonAddOfferResponse = server.addOffer(jsonAddOfferRequest);
        assertEquals(gson.toJson(""), jsonAddOfferResponse);

        VoteForCandidateDtoRequest voteForCandidateDtoRequest = new VoteForCandidateDtoRequest(1,0,voterToken, candidateToken);
        assertEquals(voterToken, voteForCandidateDtoRequest.getVotingToken());
        assertEquals(candidateToken, voteForCandidateDtoRequest.getCandidateToken());
        voteForCandidateDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(2, voteForCandidateDtoRequest.getTokensAndVoters().size());

        String jsonVoterForCandidateRequest = gson.toJson(voteForCandidateDtoRequest);
        String jsonVoterForCandidateResponse = server.voteForCandidate(jsonVoterForCandidateRequest);
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
        String jsonVoteAgainstAllResponse2 = server.voteAgainstAll(jsonVoteAgainstAllRequest2);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonVoteAgainstAllResponse2);

        assertTrue(dataBase.getVotedAgainstAll().isEmpty());
        assertEquals(0, dataBase.getVotedCandidates().size());
        assertEquals(1, dataBase.getVotedVoters().size());

        assertEquals(1, candidate.getAmountVotes());
        assertEquals(1, dataBase.getRankedCandidates().size());
        assertTrue(dataBase.getRankedCandidates().contains(candidate));

        server.stopServer(null);
    }

    @Test
    public void testVoteAgainstAllIfCandidateVote() throws IOException, VoterException {
        Server server = new Server();
        server.startServer(null);

        RegisterVoterDtoRequest registerRequest1 = new RegisterVoterDtoRequest(0, "petr","petrov","12345678");
        assertEquals("petr", registerRequest1.getFirstName());
        assertEquals("petrov", registerRequest1.getLogin());
        assertEquals("12345678", registerRequest1.getPassword());
        String jsonRegisterRequest1 = gson.toJson(registerRequest1);
        String jsonRegisterResponse1  = server.registerVoter(jsonRegisterRequest1);

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
        String jsonAddCandidateResponse  = server.addCandidate(jsonAddCandidateRequest);

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
        assertEquals(1, addOfferToElectionProgramDtoRequest1.getTokensAndVoters().size());

        String jsonAddOfferRequest = gson.toJson(addOfferToElectionProgramDtoRequest1);
        String jsonAddOfferResponse = server.addOfferToElectionProgram(jsonAddOfferRequest);
        assertEquals(gson.toJson(""), jsonAddOfferResponse);
        assertEquals(1, votingCandidate.getElectionProgram().size());

        VoteAgainstAllDtoRequest voteAgainstAllDtoRequest = new VoteAgainstAllDtoRequest(votingCandidateToken, votingCandidate);
        assertEquals(votingCandidateToken, voteAgainstAllDtoRequest.getVotingToken());
        voteAgainstAllDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
        assertEquals(1, voteAgainstAllDtoRequest.getTokensAndVoters().size());

        String jsonVoteAgainstAllRequest = gson.toJson(voteAgainstAllDtoRequest);
        String jsonVoteAgainstAllResponse = server.voteAgainstAll(jsonVoteAgainstAllRequest);
        assertEquals(gson.toJson(""), jsonVoteAgainstAllResponse);

        assertEquals(0, dataBase.getVotedVoters().size());
        assertEquals(0, dataBase.getVotedCandidates().size());
        assertEquals(1, dataBase.getVotedAgainstAll().size());
        assertTrue(dataBase.getVotedAgainstAll().contains(votingCandidate));

        server.stopServer(null);
    }

    @Test
    public void testVoteAgainstAllIfCandidateVoteTwice1() throws IOException, VoterException {
        Server server = new Server();
        server.startServer(null);

        RegisterVoterDtoRequest registerRequest1 = new RegisterVoterDtoRequest(0,"petr","petrov","12345678");
        assertEquals("petr", registerRequest1.getFirstName());
        assertEquals("petrov", registerRequest1.getLogin());
        assertEquals("12345678", registerRequest1.getPassword());
        String jsonRegisterRequest1 = gson.toJson(registerRequest1);
        String jsonRegisterResponse1  = server.registerVoter(jsonRegisterRequest1);

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
        String jsonAddCandidateResponse  = server.addCandidate(jsonAddCandidateRequest);

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
        assertEquals(1, addOfferToElectionProgramDtoRequest1.getTokensAndVoters().size());

        String jsonAddOfferRequest = gson.toJson(addOfferToElectionProgramDtoRequest1);
        String jsonAddOfferResponse = server.addOfferToElectionProgram(jsonAddOfferRequest);
        assertEquals(gson.toJson(""), jsonAddOfferResponse);
        assertEquals(1, votingCandidate.getElectionProgram().size());

        VoteAgainstAllDtoRequest voteAgainstAllDtoRequest1 = new VoteAgainstAllDtoRequest(votingCandidateToken, votingCandidate);
        assertEquals(votingCandidateToken, voteAgainstAllDtoRequest1.getVotingToken());
        voteAgainstAllDtoRequest1.setTokensAndVoters(dataBase.getTokensAndVoters());
        assertEquals(1, voteAgainstAllDtoRequest1.getTokensAndVoters().size());

        String jsonVoteAgainstAllRequest1 = gson.toJson(voteAgainstAllDtoRequest1);
        String jsonVoteAgainstAllResponse1 = server.voteAgainstAll(jsonVoteAgainstAllRequest1);
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
        String jsonVoteAgainstAllResponse2 = server.voteAgainstAll(jsonVoteAgainstAllRequest2);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonVoteAgainstAllResponse2);

        assertEquals(0, dataBase.getVotedVoters().size());
        assertEquals(0, dataBase.getVotedCandidates().size());
        assertEquals(1, dataBase.getVotedAgainstAll().size());
        assertTrue(dataBase.getVotedAgainstAll().contains(votingCandidate));

        server.stopServer(null);
    }

    @Ignore
    @Test
    public void testVoteAgainstAllIfCandidateVoteTwice2() throws IOException, VoterException {
        Server server = new Server();
        server.startServer(null);

        RegisterVoterDtoRequest registerRequest1 = new RegisterVoterDtoRequest(0, "petr","petrov","12345678");
        assertEquals("petr", registerRequest1.getFirstName());
        assertEquals("petrov", registerRequest1.getLogin());
        assertEquals("12345678", registerRequest1.getPassword());
        String jsonRegisterRequest1 = gson.toJson(registerRequest1);
        String jsonRegisterResponse1  = server.registerVoter(jsonRegisterRequest1);

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
        String jsonAddCandidateResponse  = server.addCandidate(jsonAddCandidateRequest);

        AddCandidateDtoResponse addCandidateDtoResponse1 = gson.fromJson(jsonAddCandidateResponse, AddCandidateDtoResponse.class);
        String resultAddCandidateResponseJson1 = gson.toJson(addCandidateDtoResponse1);
        assertEquals(resultAddCandidateResponseJson1, jsonAddCandidateResponse);

        RegisterVoterDtoRequest registerRequest2 = new RegisterVoterDtoRequest(1, "boris","borisov","12345678");
        assertEquals("boris", registerRequest2.getFirstName());
        assertEquals("borisov", registerRequest2.getLogin());
        assertEquals("12345678", registerRequest2.getPassword());
        String jsonRegisterRequest2 = gson.toJson(registerRequest2);
        String jsonRegisterResponse2  = server.registerVoter(jsonRegisterRequest2);

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
        String jsonAddCandidateResponse2  = server.addCandidate(jsonAddCandidateRequest2);

        AddCandidateDtoResponse addCandidateDtoResponse2 = gson.fromJson(jsonAddCandidateResponse2, AddCandidateDtoResponse.class);
        String resultAddCandidateResponseJson2 = gson.toJson(addCandidateDtoResponse2);
        assertEquals(resultAddCandidateResponseJson2, jsonAddCandidateResponse2);

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
        String jsonAddOfferResponse = server.addOfferToElectionProgram(jsonAddOfferRequest);
        assertEquals(gson.toJson(""), jsonAddOfferResponse);
        assertEquals(1, votingCandidate.getElectionProgram().size());

        AddOfferToElectionProgramDtoRequest addOfferToElectionProgramDtoRequest2 = new AddOfferToElectionProgramDtoRequest(candidateToken, 0, "build a bridge across the river");
        assertEquals(candidateToken, addOfferToElectionProgramDtoRequest2.getCandidateToken());
        assertEquals("build a bridge across the river", addOfferToElectionProgramDtoRequest2.getOfferDescription());

        addOfferToElectionProgramDtoRequest2.setTokensAndVoters(tokensAndVoters);
        assertEquals(2, addOfferToElectionProgramDtoRequest2.getTokensAndVoters().size());

        String jsonAddOfferRequest2 = gson.toJson(addOfferToElectionProgramDtoRequest2);
        String jsonAddOfferResponse2 = server.addOfferToElectionProgram(jsonAddOfferRequest2);
        assertEquals(gson.toJson(""), jsonAddOfferResponse2);
        assertEquals(2, candidate.getElectionProgram().size());

        VoteForCandidateDtoRequest voteForCandidateDtoRequest1 = new VoteForCandidateDtoRequest(1,0,votingCandidateToken, candidateToken);
        assertEquals(votingCandidateToken, voteForCandidateDtoRequest1.getVotingToken());
        assertEquals(candidateToken, voteForCandidateDtoRequest1.getCandidateToken());
        voteForCandidateDtoRequest1.setTokensAndVoters(tokensAndVoters);
        assertEquals(2, voteForCandidateDtoRequest1.getTokensAndVoters().size());

        String jsonVoterForCandidateRequest1 = gson.toJson(voteForCandidateDtoRequest1);
        String jsonVoterForCandidateResponse1 = server.voteForCandidate(jsonVoterForCandidateRequest1);
        assertEquals(gson.toJson(""), jsonVoterForCandidateResponse1);

        assertEquals(0, dataBase.getVotedVoters().size());
        assertEquals(1, dataBase.getVotedCandidates().size());
        assertTrue(dataBase.getVotedCandidates().contains(votingCandidate));

        assertEquals(1, candidate.getAmountVotes());
        assertEquals(1, dataBase.getRankedCandidates().size());
        assertTrue(dataBase.getRankedCandidates().contains(candidate));

        VoteAgainstAllDtoRequest voteAgainstAllDtoRequest1 = new VoteAgainstAllDtoRequest(votingCandidateToken,votingCandidate);
        assertEquals(votingCandidateToken, voteAgainstAllDtoRequest1.getVotingToken());
        voteAgainstAllDtoRequest1.setTokensAndVoters(dataBase.getTokensAndVoters());
        assertEquals(2, voteAgainstAllDtoRequest1.getTokensAndVoters().size());

        String jsonVoteAgainstAllRequest1 = gson.toJson(voteAgainstAllDtoRequest1);
        String jsonVoteAgainstAllResponse1 = server.voteAgainstAll(jsonVoteAgainstAllRequest1);
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
        String jsonVoteAgainstAllResponse2 = server.voteAgainstAll(jsonVoteAgainstAllRequest2);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonVoteAgainstAllResponse2);

        assertTrue(dataBase.getVotedAgainstAll().isEmpty());
        assertEquals(0, dataBase.getVotedVoters().size());
        assertEquals(1, dataBase.getVotedCandidates().size());
        assertTrue(dataBase.getVotedCandidates().contains(votingCandidateToken));

        assertEquals(1, candidate.getAmountVotes());
        assertEquals(1, dataBase.getRankedCandidates().size());
        assertTrue(dataBase.getRankedCandidates().contains(candidate));

        server.stopServer(null);
    }

    @Test
    public void testVoteAgainstAllIfVotingCandidateVoteWithoutElectionProgram() throws IOException, VoterException {
        Server server = new Server();
        server.startServer(null);

        RegisterVoterDtoRequest registerRequest1 = new RegisterVoterDtoRequest(0, "petr","petrov","12345678");
        assertEquals("petr", registerRequest1.getFirstName());
        assertEquals("petrov", registerRequest1.getLogin());
        assertEquals("12345678", registerRequest1.getPassword());
        String jsonRegisterRequest1 = gson.toJson(registerRequest1);
        String jsonRegisterResponse1  = server.registerVoter(jsonRegisterRequest1);

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
        String jsonAddCandidateResponse  = server.addCandidate(jsonAddCandidateRequest);

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
        String jsonVoterForCandidateResponse = server.voteForCandidate(jsonVoterForCandidateRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonVoterForCandidateResponse);
        assertTrue(dataBase.getVotedAgainstAll().isEmpty());
        assertTrue(dataBase.getVotedVoters().isEmpty());
        assertTrue(dataBase.getVotedCandidates().isEmpty());

        server.stopServer(null);
    }

    @Ignore
    @Test
    public void testGetElectionResultsIfCandidateScoredMostAmountVotes() throws IOException, VoterException {
        Server server = new Server();
        server.startServer(null);

        dataBase.getRankedCandidates().clear();

        RegisterVoterDtoRequest registerRequest1 = new RegisterVoterDtoRequest(0, "petr1","petrov1","12345678");
        assertEquals("petr1", registerRequest1.getFirstName());
        assertEquals("petrov1", registerRequest1.getLogin());
        assertEquals("12345678", registerRequest1.getPassword());
        String jsonRegisterRequest1 = gson.toJson(registerRequest1);
        String jsonRegisterResponse1  = server.registerVoter(jsonRegisterRequest1);

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
        String jsonAddCandidateResponse1  = server.addCandidate(jsonAddCandidateRequest1);

        AddCandidateDtoResponse addCandidateDtoResponse1 = gson.fromJson(jsonAddCandidateResponse1, AddCandidateDtoResponse.class);
        String resultAddCandidateResponseJson1 = gson.toJson(addCandidateDtoResponse1);
        assertEquals(resultAddCandidateResponseJson1, jsonAddCandidateResponse1);

        RegisterVoterDtoRequest registerRequest2 = new RegisterVoterDtoRequest(1, "petr2","petrov2","123456789");
        assertEquals("petr2", registerRequest2.getFirstName());
        assertEquals("petrov2", registerRequest2.getLogin());
        assertEquals("123456789", registerRequest2.getPassword());
        String jsonRegisterRequest2 = gson.toJson(registerRequest2);
        String jsonRegisterResponse2  = server.registerVoter(jsonRegisterRequest2);

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
        String jsonAddCandidateResponse2  = server.addCandidate(jsonAddCandidateRequest2);

        AddCandidateDtoResponse addCandidateDtoResponse2 = gson.fromJson(jsonAddCandidateResponse2, AddCandidateDtoResponse.class);
        String resultAddCandidateResponseJson2 = gson.toJson(addCandidateDtoResponse2);
        assertEquals(resultAddCandidateResponseJson2, jsonAddCandidateResponse2);

        RegisterVoterDtoRequest registerRequest3 = new RegisterVoterDtoRequest(2, "petr3","petrov3","12345678");
        assertEquals("petr3", registerRequest3.getFirstName());
        assertEquals("petrov3", registerRequest3.getLogin());
        assertEquals("12345678", registerRequest3.getPassword());
        String jsonRegisterRequest3 = gson.toJson(registerRequest3);
        String jsonRegisterResponse3  = server.registerVoter(jsonRegisterRequest3);

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
        String jsonAddCandidateResponse3  = server.addCandidate(jsonAddCandidateRequest3);

        AddCandidateDtoResponse addCandidateDtoResponse3 = gson.fromJson(jsonAddCandidateResponse3, AddCandidateDtoResponse.class);
        String resultAddCandidateResponseJson3 = gson.toJson(addCandidateDtoResponse3);
        assertEquals(resultAddCandidateResponseJson3, jsonAddCandidateResponse3);

        RegisterVoterDtoRequest registerRequest4 = new RegisterVoterDtoRequest(3, "boris1","borisov1","12345678");
        assertEquals("boris1", registerRequest4.getFirstName());
        assertEquals("borisov1", registerRequest4.getLogin());
        assertEquals("12345678", registerRequest4.getPassword());
        String jsonRegisterRequest4 = gson.toJson(registerRequest4);
        String jsonRegisterResponse4  = server.registerVoter(jsonRegisterRequest4);

        RegisterVoterDtoResponse registerVoterDtoResponse4 = gson.fromJson(jsonRegisterResponse4, RegisterVoterDtoResponse.class);
        String resultRegisterJson4 = gson.toJson(registerVoterDtoResponse4);
        assertEquals(resultRegisterJson4, jsonRegisterResponse4);

        RegisterVoterDtoRequest registerRequest5 = new RegisterVoterDtoRequest(4, "boris2","borisov2","12345678");
        assertEquals("boris2", registerRequest5.getFirstName());
        assertEquals("borisov2", registerRequest5.getLogin());
        assertEquals("12345678", registerRequest5.getPassword());
        String jsonRegisterRequest5 = gson.toJson(registerRequest5);
        String jsonRegisterResponse5  = server.registerVoter(jsonRegisterRequest5);

        RegisterVoterDtoResponse registerVoterDtoResponse5 = gson.fromJson(jsonRegisterResponse5, RegisterVoterDtoResponse.class);
        String resultRegisterJson5 = gson.toJson(registerVoterDtoResponse5);
        assertEquals(resultRegisterJson5, jsonRegisterResponse5);

        RegisterVoterDtoRequest registerRequest6 = new RegisterVoterDtoRequest(5, "boris3","borisov3","12345678");
        assertEquals("boris3", registerRequest6.getFirstName());
        assertEquals("borisov3", registerRequest6.getLogin());
        assertEquals("12345678", registerRequest6.getPassword());
        String jsonRegisterRequest6 = gson.toJson(registerRequest6);
        String jsonRegisterResponse6  = server.registerVoter(jsonRegisterRequest6);

        RegisterVoterDtoResponse registerVoterDtoResponse6 = gson.fromJson(jsonRegisterResponse6, RegisterVoterDtoResponse.class);
        String resultRegisterJson6 = gson.toJson(registerVoterDtoResponse6);
        assertEquals(resultRegisterJson6, jsonRegisterResponse6);

        RegisterVoterDtoRequest registerRequest7 = new RegisterVoterDtoRequest(6, "boris4","borisov4","12345678");
        assertEquals("boris4", registerRequest7.getFirstName());
        assertEquals("borisov4", registerRequest7.getLogin());
        assertEquals("12345678", registerRequest7.getPassword());
        String jsonRegisterRequest7 = gson.toJson(registerRequest7);
        String jsonRegisterResponse7  = server.registerVoter(jsonRegisterRequest7);

        RegisterVoterDtoResponse registerVoterDtoResponse7 = gson.fromJson(jsonRegisterResponse7, RegisterVoterDtoResponse.class);
        String resultRegisterJson7 = gson.toJson(registerVoterDtoResponse7);
        assertEquals(resultRegisterJson7, jsonRegisterResponse7);

        RegisterVoterDtoRequest registerRequest8 = new RegisterVoterDtoRequest(7, "boris5","borisov5","12345678");
        assertEquals("boris5", registerRequest8.getFirstName());
        assertEquals("borisov5", registerRequest8.getLogin());
        assertEquals("12345678", registerRequest8.getPassword());
        String jsonRegisterRequest8 = gson.toJson(registerRequest8);
        String jsonRegisterResponse8  = server.registerVoter(jsonRegisterRequest8);

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
        String jsonAddOfferResponse1 = server.addOffer(jsonAddOfferRequest1);
        assertEquals(gson.toJson(""), jsonAddOfferResponse1);

        AddOfferDtoRequest addOfferDtoRequest2 = new AddOfferDtoRequest(voterToken2,4, candidateToken2,1, "repair the road");
        assertEquals(voterToken2, addOfferDtoRequest2.getVoterToken());
        assertEquals(candidateToken2, addOfferDtoRequest2.getCandidateToken());
        assertEquals("repair the road", addOfferDtoRequest2.getOfferDescription());

        addOfferDtoRequest2.setTokensAndVoters(tokensAndVoters);
        assertEquals(8, addOfferDtoRequest2.getTokensAndVoters().size());

        String jsonAddOfferRequest2 = gson.toJson(addOfferDtoRequest2);
        String jsonAddOfferResponse2 = server.addOffer(jsonAddOfferRequest2);
        assertEquals(gson.toJson(""), jsonAddOfferResponse2);

        AddOfferDtoRequest addOfferDtoRequest3 = new AddOfferDtoRequest(voterToken3, 5,candidateToken3,2, "increase the minimum salary");
        assertEquals(voterToken3, addOfferDtoRequest3.getVoterToken());
        assertEquals(candidateToken3, addOfferDtoRequest3.getCandidateToken());
        assertEquals("increase the minimum salary", addOfferDtoRequest3.getOfferDescription());

        addOfferDtoRequest3.setTokensAndVoters(tokensAndVoters);
        assertEquals(8, addOfferDtoRequest3.getTokensAndVoters().size());

        String jsonAddOfferRequest3 = gson.toJson(addOfferDtoRequest3);
        String jsonAddOfferResponse3 = server.addOffer(jsonAddOfferRequest3);
        assertEquals(gson.toJson(""), jsonAddOfferResponse3);

        VoteForCandidateDtoRequest voteForCandidateDtoRequest1 = new VoteForCandidateDtoRequest(3,0,voterToken1, candidateToken1);
        assertEquals(voterToken1, voteForCandidateDtoRequest1.getVotingToken());
        assertEquals(candidateToken1, voteForCandidateDtoRequest1.getCandidateToken());
        voteForCandidateDtoRequest1.setTokensAndVoters(tokensAndVoters);
        assertEquals(8, voteForCandidateDtoRequest1.getTokensAndVoters().size());

        String jsonVoterForCandidateRequest1 = gson.toJson(voteForCandidateDtoRequest1);
        String jsonVoterForCandidateResponse1 = server.voteForCandidate(jsonVoterForCandidateRequest1);
        assertEquals(gson.toJson(""), jsonVoterForCandidateResponse1);

        VoteForCandidateDtoRequest voteForCandidateDtoRequest2 = new VoteForCandidateDtoRequest(4,1,voterToken2, candidateToken2);
        assertEquals(voterToken2, voteForCandidateDtoRequest2.getVotingToken());
        assertEquals(candidateToken2, voteForCandidateDtoRequest2.getCandidateToken());
        voteForCandidateDtoRequest2.setTokensAndVoters(tokensAndVoters);
        assertEquals(8, voteForCandidateDtoRequest2.getTokensAndVoters().size());

        String jsonVoterForCandidateRequest2 = gson.toJson(voteForCandidateDtoRequest2);
        String jsonVoterForCandidateResponse2 = server.voteForCandidate(jsonVoterForCandidateRequest2);
        assertEquals(gson.toJson(""), jsonVoterForCandidateResponse2);

        VoteForCandidateDtoRequest voteForCandidateDtoRequest3 = new VoteForCandidateDtoRequest(5,2,voterToken3, candidateToken3);
        assertEquals(voterToken3, voteForCandidateDtoRequest3.getVotingToken());
        assertEquals(candidateToken3, voteForCandidateDtoRequest3.getCandidateToken());
        voteForCandidateDtoRequest3.setTokensAndVoters(tokensAndVoters);
        assertEquals(8, voteForCandidateDtoRequest3.getTokensAndVoters().size());

        String jsonVoterForCandidateRequest3 = gson.toJson(voteForCandidateDtoRequest3);
        String jsonVoterForCandidateResponse3 = server.voteForCandidate(jsonVoterForCandidateRequest3);
        assertEquals(gson.toJson(""), jsonVoterForCandidateResponse3);

        VoteForCandidateDtoRequest voteForCandidateDtoRequest4 = new VoteForCandidateDtoRequest(6,2,voterToken4, candidateToken3);
        assertEquals(voterToken4, voteForCandidateDtoRequest4.getVotingToken());
        assertEquals(candidateToken3, voteForCandidateDtoRequest4.getCandidateToken());
        voteForCandidateDtoRequest4.setTokensAndVoters(tokensAndVoters);
        assertEquals(8, voteForCandidateDtoRequest4.getTokensAndVoters().size());

        String jsonVoterForCandidateRequest4 = gson.toJson(voteForCandidateDtoRequest4);
        String jsonVoterForCandidateResponse4 = server.voteForCandidate(jsonVoterForCandidateRequest4);
        assertEquals(gson.toJson(""), jsonVoterForCandidateResponse4);

        VoteForCandidateDtoRequest voteForCandidateDtoRequest5 = new VoteForCandidateDtoRequest(7,2,voterToken5, candidateToken3);
        assertEquals(voterToken5, voteForCandidateDtoRequest5.getVotingToken());
        assertEquals(candidateToken3, voteForCandidateDtoRequest5.getCandidateToken());
        voteForCandidateDtoRequest5.setTokensAndVoters(tokensAndVoters);
        assertEquals(8, voteForCandidateDtoRequest5.getTokensAndVoters().size());

        String jsonVoterForCandidateRequest5 = gson.toJson(voteForCandidateDtoRequest5);
        String jsonVoterForCandidateResponse5 = server.voteForCandidate(jsonVoterForCandidateRequest5);
        assertEquals(gson.toJson(""), jsonVoterForCandidateResponse5);

        VoteForCandidateDtoRequest voteForCandidateDtoRequest6 = new VoteForCandidateDtoRequest(0,2,candidateToken1, candidateToken3);
        assertEquals(candidateToken1, voteForCandidateDtoRequest6.getVotingToken());
        assertEquals(candidateToken3, voteForCandidateDtoRequest6.getCandidateToken());
        voteForCandidateDtoRequest6.setTokensAndVoters(tokensAndVoters);
        assertEquals(8, voteForCandidateDtoRequest6.getTokensAndVoters().size());

        String jsonVoterForCandidateRequest6 = gson.toJson(voteForCandidateDtoRequest6);
        String jsonVoterForCandidateResponse6 = server.voteForCandidate(jsonVoterForCandidateRequest6);
        assertEquals(gson.toJson(""), jsonVoterForCandidateResponse6);

        GetElectionResultsDtoRequest getElectionResultsDtoRequest = new GetElectionResultsDtoRequest(voterToken1);
        assertEquals(voterToken1, getElectionResultsDtoRequest.getToken());
        getElectionResultsDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(8, getElectionResultsDtoRequest.getTokensAndVoters().size());

        String jsonGetElectionResultsRequest = gson.toJson(getElectionResultsDtoRequest);
        String jsonGetElectionResultsResponse = server.getElectionResults(jsonGetElectionResultsRequest);

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

        server.stopServer(null);
    }

    @Test
    public void testGetElectionResultsIfTwoCandidatesScoredMostAmountVotes() throws IOException, VoterException {
        Server server = new Server();
        server.startServer(null);

        dataBase.getRankedCandidates().clear();

        RegisterVoterDtoRequest registerRequest1 = new RegisterVoterDtoRequest(0, "petr1","petrov1","12345678");
        assertEquals("petr1", registerRequest1.getFirstName());
        assertEquals("petrov1", registerRequest1.getLogin());
        assertEquals("12345678", registerRequest1.getPassword());
        String jsonRegisterRequest1 = gson.toJson(registerRequest1);
        String jsonRegisterResponse1  = server.registerVoter(jsonRegisterRequest1);

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
        String jsonAddCandidateResponse1  = server.addCandidate(jsonAddCandidateRequest1);

        AddCandidateDtoResponse addCandidateDtoResponse1 = gson.fromJson(jsonAddCandidateResponse1, AddCandidateDtoResponse.class);
        String resultAddCandidateResponseJson1 = gson.toJson(addCandidateDtoResponse1);
        assertEquals(resultAddCandidateResponseJson1, jsonAddCandidateResponse1);

        RegisterVoterDtoRequest registerRequest2 = new RegisterVoterDtoRequest(1, "petr2","petrov2","12345678");
        assertEquals("petr2", registerRequest2.getFirstName());
        assertEquals("petrov2", registerRequest2.getLogin());
        assertEquals("12345678", registerRequest2.getPassword());
        String jsonRegisterRequest2 = gson.toJson(registerRequest2);
        String jsonRegisterResponse2  = server.registerVoter(jsonRegisterRequest2);

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
        String jsonAddCandidateResponse2  = server.addCandidate(jsonAddCandidateRequest2);

        AddCandidateDtoResponse addCandidateDtoResponse2 = gson.fromJson(jsonAddCandidateResponse2, AddCandidateDtoResponse.class);
        String resultAddCandidateResponseJson2 = gson.toJson(addCandidateDtoResponse2);
        assertEquals(resultAddCandidateResponseJson2, jsonAddCandidateResponse2);

        RegisterVoterDtoRequest registerRequest3 = new RegisterVoterDtoRequest(2, "petr3","petrov3","12345678");
        assertEquals("petr3", registerRequest3.getFirstName());
        assertEquals("petrov3", registerRequest3.getLogin());
        assertEquals("12345678", registerRequest3.getPassword());
        String jsonRegisterRequest3 = gson.toJson(registerRequest3);
        String jsonRegisterResponse3  = server.registerVoter(jsonRegisterRequest3);

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
        String jsonAddCandidateResponse3  = server.addCandidate(jsonAddCandidateRequest3);

        AddCandidateDtoResponse addCandidateDtoResponse3 = gson.fromJson(jsonAddCandidateResponse3, AddCandidateDtoResponse.class);
        String resultAddCandidateResponseJson3 = gson.toJson(addCandidateDtoResponse3);
        assertEquals(resultAddCandidateResponseJson3, jsonAddCandidateResponse3);

        RegisterVoterDtoRequest registerRequest4 = new RegisterVoterDtoRequest(3, "boris1","borisov1","12345678");
        assertEquals("boris1", registerRequest4.getFirstName());
        assertEquals("borisov1", registerRequest4.getLogin());
        assertEquals("12345678", registerRequest4.getPassword());
        String jsonRegisterRequest4 = gson.toJson(registerRequest4);
        String jsonRegisterResponse4  = server.registerVoter(jsonRegisterRequest4);

        RegisterVoterDtoResponse registerVoterDtoResponse4 = gson.fromJson(jsonRegisterResponse4, RegisterVoterDtoResponse.class);
        String resultRegisterJson4 = gson.toJson(registerVoterDtoResponse4);
        assertEquals(resultRegisterJson4, jsonRegisterResponse4);

        RegisterVoterDtoRequest registerRequest5 = new RegisterVoterDtoRequest(4, "boris2","borisov2","12345678");
        assertEquals("boris2", registerRequest5.getFirstName());
        assertEquals("borisov2", registerRequest5.getLogin());
        assertEquals("12345678", registerRequest5.getPassword());
        String jsonRegisterRequest5 = gson.toJson(registerRequest5);
        String jsonRegisterResponse5  = server.registerVoter(jsonRegisterRequest5);

        RegisterVoterDtoResponse registerVoterDtoResponse5 = gson.fromJson(jsonRegisterResponse5, RegisterVoterDtoResponse.class);
        String resultRegisterJson5 = gson.toJson(registerVoterDtoResponse5);
        assertEquals(resultRegisterJson5, jsonRegisterResponse5);

        RegisterVoterDtoRequest registerRequest6 = new RegisterVoterDtoRequest(5, "boris3","borisov3","12345678");
        assertEquals("boris3", registerRequest6.getFirstName());
        assertEquals("borisov3", registerRequest6.getLogin());
        assertEquals("12345678", registerRequest6.getPassword());
        String jsonRegisterRequest6 = gson.toJson(registerRequest6);
        String jsonRegisterResponse6  = server.registerVoter(jsonRegisterRequest6);

        RegisterVoterDtoResponse registerVoterDtoResponse6 = gson.fromJson(jsonRegisterResponse6, RegisterVoterDtoResponse.class);
        String resultRegisterJson6 = gson.toJson(registerVoterDtoResponse6);
        assertEquals(resultRegisterJson6, jsonRegisterResponse6);

        RegisterVoterDtoRequest registerRequest7 = new RegisterVoterDtoRequest(6, "boris4","borisov4","12345678");
        assertEquals("boris4", registerRequest7.getFirstName());
        assertEquals("borisov4", registerRequest7.getLogin());
        assertEquals("12345678", registerRequest7.getPassword());
        String jsonRegisterRequest7 = gson.toJson(registerRequest7);
        String jsonRegisterResponse7  = server.registerVoter(jsonRegisterRequest7);

        RegisterVoterDtoResponse registerVoterDtoResponse7 = gson.fromJson(jsonRegisterResponse7, RegisterVoterDtoResponse.class);
        String resultRegisterJson7 = gson.toJson(registerVoterDtoResponse7);
        assertEquals(resultRegisterJson7, jsonRegisterResponse7);

        RegisterVoterDtoRequest registerRequest8 = new RegisterVoterDtoRequest(7, "boris5","borisov5","12345678");
        assertEquals("boris5", registerRequest8.getFirstName());
        assertEquals("borisov5", registerRequest8.getLogin());
        assertEquals("12345678", registerRequest8.getPassword());
        String jsonRegisterRequest8 = gson.toJson(registerRequest8);
        String jsonRegisterResponse8  = server.registerVoter(jsonRegisterRequest8);

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
        String jsonAddOfferResponse1 = server.addOffer(jsonAddOfferRequest1);
        assertEquals(gson.toJson(""), jsonAddOfferResponse1);

        AddOfferDtoRequest addOfferDtoRequest2 = new AddOfferDtoRequest(voterToken2,4, candidateToken2,1, "repair the road");
        assertEquals(voterToken2, addOfferDtoRequest2.getVoterToken());
        assertEquals(candidateToken2, addOfferDtoRequest2.getCandidateToken());
        assertEquals("repair the road", addOfferDtoRequest2.getOfferDescription());

        addOfferDtoRequest2.setTokensAndVoters(tokensAndVoters);
        assertEquals(8, addOfferDtoRequest2.getTokensAndVoters().size());

        String jsonAddOfferRequest2 = gson.toJson(addOfferDtoRequest2);
        String jsonAddOfferResponse2 = server.addOffer(jsonAddOfferRequest2);
        assertEquals(gson.toJson(""), jsonAddOfferResponse2);

        AddOfferDtoRequest addOfferDtoRequest3 = new AddOfferDtoRequest(voterToken3,5, candidateToken3,2, "increase the minimum salary");
        assertEquals(voterToken3, addOfferDtoRequest3.getVoterToken());
        assertEquals(candidateToken3, addOfferDtoRequest3.getCandidateToken());
        assertEquals("increase the minimum salary", addOfferDtoRequest3.getOfferDescription());

        addOfferDtoRequest3.setTokensAndVoters(tokensAndVoters);
        assertEquals(8, addOfferDtoRequest3.getTokensAndVoters().size());

        String jsonAddOfferRequest3 = gson.toJson(addOfferDtoRequest3);
        String jsonAddOfferResponse3 = server.addOffer(jsonAddOfferRequest3);
        assertEquals(gson.toJson(""), jsonAddOfferResponse3);

        VoteForCandidateDtoRequest voteForCandidateDtoRequest1 = new VoteForCandidateDtoRequest(3,0,voterToken1, candidateToken1);
        assertEquals(voterToken1, voteForCandidateDtoRequest1.getVotingToken());
        assertEquals(candidateToken1, voteForCandidateDtoRequest1.getCandidateToken());
        voteForCandidateDtoRequest1.setTokensAndVoters(tokensAndVoters);
        assertEquals(8, voteForCandidateDtoRequest1.getTokensAndVoters().size());

        String jsonVoterForCandidateRequest1 = gson.toJson(voteForCandidateDtoRequest1);
        String jsonVoterForCandidateResponse1 = server.voteForCandidate(jsonVoterForCandidateRequest1);
        assertEquals(gson.toJson(""), jsonVoterForCandidateResponse1);

        VoteForCandidateDtoRequest voteForCandidateDtoRequest2 = new VoteForCandidateDtoRequest(4,0,voterToken2, candidateToken1);
        assertEquals(voterToken2, voteForCandidateDtoRequest2.getVotingToken());
        assertEquals(candidateToken1, voteForCandidateDtoRequest2.getCandidateToken());
        voteForCandidateDtoRequest2.setTokensAndVoters(tokensAndVoters);
        assertEquals(8, voteForCandidateDtoRequest2.getTokensAndVoters().size());

        String jsonVoterForCandidateRequest2 = gson.toJson(voteForCandidateDtoRequest2);
        String jsonVoterForCandidateResponse2 = server.voteForCandidate(jsonVoterForCandidateRequest2);
        assertEquals(gson.toJson(""), jsonVoterForCandidateResponse2);

        VoteForCandidateDtoRequest voteForCandidateDtoRequest3 = new VoteForCandidateDtoRequest(5,2,voterToken3, candidateToken3);
        assertEquals(voterToken3, voteForCandidateDtoRequest3.getVotingToken());
        assertEquals(candidateToken3, voteForCandidateDtoRequest3.getCandidateToken());
        voteForCandidateDtoRequest3.setTokensAndVoters(tokensAndVoters);
        assertEquals(8, voteForCandidateDtoRequest3.getTokensAndVoters().size());

        String jsonVoterForCandidateRequest3 = gson.toJson(voteForCandidateDtoRequest3);
        String jsonVoterForCandidateResponse3 = server.voteForCandidate(jsonVoterForCandidateRequest3);
        assertEquals(gson.toJson(""), jsonVoterForCandidateResponse3);

        VoteForCandidateDtoRequest voteForCandidateDtoRequest4 = new VoteForCandidateDtoRequest(6,2,voterToken4, candidateToken3);
        assertEquals(voterToken4, voteForCandidateDtoRequest4.getVotingToken());
        assertEquals(candidateToken3, voteForCandidateDtoRequest4.getCandidateToken());
        voteForCandidateDtoRequest4.setTokensAndVoters(tokensAndVoters);
        assertEquals(8, voteForCandidateDtoRequest4.getTokensAndVoters().size());

        String jsonVoterForCandidateRequest4 = gson.toJson(voteForCandidateDtoRequest4);
        String jsonVoterForCandidateResponse4 = server.voteForCandidate(jsonVoterForCandidateRequest4);
        assertEquals(gson.toJson(""), jsonVoterForCandidateResponse4);

        VoteForCandidateDtoRequest voteForCandidateDtoRequest5 = new VoteForCandidateDtoRequest(7,1,voterToken5, candidateToken2);
        assertEquals(voterToken5, voteForCandidateDtoRequest5.getVotingToken());
        assertEquals(candidateToken2, voteForCandidateDtoRequest5.getCandidateToken());
        voteForCandidateDtoRequest5.setTokensAndVoters(tokensAndVoters);
        assertEquals(8, voteForCandidateDtoRequest5.getTokensAndVoters().size());

        String jsonVoterForCandidateRequest5 = gson.toJson(voteForCandidateDtoRequest5);
        String jsonVoterForCandidateResponse5 = server.voteForCandidate(jsonVoterForCandidateRequest5);
        assertEquals(gson.toJson(""), jsonVoterForCandidateResponse5);

        GetElectionResultsDtoRequest getElectionResultsDtoRequest = new GetElectionResultsDtoRequest(voterToken1);
        assertEquals(voterToken1, getElectionResultsDtoRequest.getToken());
        getElectionResultsDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(8, getElectionResultsDtoRequest.getTokensAndVoters().size());

        String jsonGetElectionResultsRequest = gson.toJson(getElectionResultsDtoRequest);
        String jsonGetElectionResultsResponse = server.getElectionResults(jsonGetElectionResultsRequest);

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

        server.stopServer(null);
    }

    @Test
    public void testGetElectionResultsIfAmountVotesAgainstAllMoreThanVotesCandidate() throws IOException, VoterException {
        Server server = new Server();
        server.startServer(null);

        dataBase.getRankedCandidates().clear();

        RegisterVoterDtoRequest registerRequest1 = new RegisterVoterDtoRequest(0, "petr1","petrov1","12345678");
        assertEquals("petr1", registerRequest1.getFirstName());
        assertEquals("petrov1", registerRequest1.getLogin());
        assertEquals("12345678", registerRequest1.getPassword());
        String jsonRegisterRequest1 = gson.toJson(registerRequest1);
        String jsonRegisterResponse1  = server.registerVoter(jsonRegisterRequest1);

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
        String jsonAddCandidateResponse1  = server.addCandidate(jsonAddCandidateRequest1);

        AddCandidateDtoResponse addCandidateDtoResponse1 = gson.fromJson(jsonAddCandidateResponse1, AddCandidateDtoResponse.class);
        String resultAddCandidateResponseJson1 = gson.toJson(addCandidateDtoResponse1);
        assertEquals(resultAddCandidateResponseJson1, jsonAddCandidateResponse1);

        RegisterVoterDtoRequest registerRequest2 = new RegisterVoterDtoRequest(1, "petr2","petrov2","12345678");
        assertEquals("petr2", registerRequest2.getFirstName());
        assertEquals("petrov2", registerRequest2.getLogin());
        assertEquals("12345678", registerRequest2.getPassword());
        String jsonRegisterRequest2 = gson.toJson(registerRequest2);
        String jsonRegisterResponse2  = server.registerVoter(jsonRegisterRequest2);

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
        String jsonAddCandidateResponse2  = server.addCandidate(jsonAddCandidateRequest2);

        AddCandidateDtoResponse addCandidateDtoResponse2 = gson.fromJson(jsonAddCandidateResponse2, AddCandidateDtoResponse.class);
        String resultAddCandidateResponseJson2 = gson.toJson(addCandidateDtoResponse2);
        assertEquals(resultAddCandidateResponseJson2, jsonAddCandidateResponse2);

        RegisterVoterDtoRequest registerRequest3 = new RegisterVoterDtoRequest(2, "petr3","petrov3","12345678");
        assertEquals("petr3", registerRequest3.getFirstName());
        assertEquals("petrov3", registerRequest3.getLogin());
        assertEquals("12345678", registerRequest3.getPassword());
        String jsonRegisterRequest3 = gson.toJson(registerRequest3);
        String jsonRegisterResponse3  = server.registerVoter(jsonRegisterRequest3);

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
        String jsonAddCandidateResponse3  = server.addCandidate(jsonAddCandidateRequest3);

        AddCandidateDtoResponse addCandidateDtoResponse3 = gson.fromJson(jsonAddCandidateResponse3, AddCandidateDtoResponse.class);
        String resultAddCandidateResponseJson3 = gson.toJson(addCandidateDtoResponse3);
        assertEquals(resultAddCandidateResponseJson3, jsonAddCandidateResponse3);

        RegisterVoterDtoRequest registerRequest4 = new RegisterVoterDtoRequest(3, "boris1","borisov1","12345678");
        assertEquals("boris1", registerRequest4.getFirstName());
        assertEquals("borisov1", registerRequest4.getLogin());
        assertEquals("12345678", registerRequest4.getPassword());
        String jsonRegisterRequest4 = gson.toJson(registerRequest4);
        String jsonRegisterResponse4  = server.registerVoter(jsonRegisterRequest4);

        RegisterVoterDtoResponse registerVoterDtoResponse4 = gson.fromJson(jsonRegisterResponse4, RegisterVoterDtoResponse.class);
        String resultRegisterJson4 = gson.toJson(registerVoterDtoResponse4);
        assertEquals(resultRegisterJson4, jsonRegisterResponse4);

        RegisterVoterDtoRequest registerRequest5 = new RegisterVoterDtoRequest(4, "boris2","borisov2","12345678");
        assertEquals("boris2", registerRequest5.getFirstName());
        assertEquals("borisov2", registerRequest5.getLogin());
        assertEquals("12345678", registerRequest5.getPassword());
        String jsonRegisterRequest5 = gson.toJson(registerRequest5);
        String jsonRegisterResponse5  = server.registerVoter(jsonRegisterRequest5);

        RegisterVoterDtoResponse registerVoterDtoResponse5 = gson.fromJson(jsonRegisterResponse5, RegisterVoterDtoResponse.class);
        String resultRegisterJson5 = gson.toJson(registerVoterDtoResponse5);
        assertEquals(resultRegisterJson5, jsonRegisterResponse5);

        RegisterVoterDtoRequest registerRequest6 = new RegisterVoterDtoRequest(5, "boris3","borisov3","12345678");
        assertEquals("boris3", registerRequest6.getFirstName());
        assertEquals("borisov3", registerRequest6.getLogin());
        assertEquals("12345678", registerRequest6.getPassword());
        String jsonRegisterRequest6 = gson.toJson(registerRequest6);
        String jsonRegisterResponse6  = server.registerVoter(jsonRegisterRequest6);

        RegisterVoterDtoResponse registerVoterDtoResponse6 = gson.fromJson(jsonRegisterResponse6, RegisterVoterDtoResponse.class);
        String resultRegisterJson6 = gson.toJson(registerVoterDtoResponse6);
        assertEquals(resultRegisterJson6, jsonRegisterResponse6);

        RegisterVoterDtoRequest registerRequest7 = new RegisterVoterDtoRequest(6, "boris4","borisov4","12345678");
        assertEquals("boris4", registerRequest7.getFirstName());
        assertEquals("borisov4", registerRequest7.getLogin());
        assertEquals("12345678", registerRequest7.getPassword());
        String jsonRegisterRequest7 = gson.toJson(registerRequest7);
        String jsonRegisterResponse7  = server.registerVoter(jsonRegisterRequest7);

        RegisterVoterDtoResponse registerVoterDtoResponse7 = gson.fromJson(jsonRegisterResponse7, RegisterVoterDtoResponse.class);
        String resultRegisterJson7 = gson.toJson(registerVoterDtoResponse7);
        assertEquals(resultRegisterJson7, jsonRegisterResponse7);

        RegisterVoterDtoRequest registerRequest8 = new RegisterVoterDtoRequest(7, "boris5","borisov5","12345678");
        assertEquals("boris5", registerRequest8.getFirstName());
        assertEquals("borisov5", registerRequest8.getLogin());
        assertEquals("12345678", registerRequest8.getPassword());
        String jsonRegisterRequest8 = gson.toJson(registerRequest8);
        String jsonRegisterResponse8  = server.registerVoter(jsonRegisterRequest8);

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
        String jsonAddOfferResponse1 = server.addOffer(jsonAddOfferRequest1);
        assertEquals(gson.toJson(""), jsonAddOfferResponse1);

        AddOfferDtoRequest addOfferDtoRequest2 = new AddOfferDtoRequest(voterToken2,4, candidateToken2,1, "repair the road");
        assertEquals(voterToken2, addOfferDtoRequest2.getVoterToken());
        assertEquals(candidateToken2, addOfferDtoRequest2.getCandidateToken());
        assertEquals("repair the road", addOfferDtoRequest2.getOfferDescription());

        addOfferDtoRequest2.setTokensAndVoters(tokensAndVoters);
        assertEquals(8, addOfferDtoRequest2.getTokensAndVoters().size());

        String jsonAddOfferRequest2 = gson.toJson(addOfferDtoRequest2);
        String jsonAddOfferResponse2 = server.addOffer(jsonAddOfferRequest2);
        assertEquals(gson.toJson(""), jsonAddOfferResponse2);

        AddOfferDtoRequest addOfferDtoRequest3 = new AddOfferDtoRequest(voterToken3,5, candidateToken3,2, "increase the minimum salary");
        assertEquals(voterToken3, addOfferDtoRequest3.getVoterToken());
        assertEquals(candidateToken3, addOfferDtoRequest3.getCandidateToken());
        assertEquals("increase the minimum salary", addOfferDtoRequest3.getOfferDescription());

        addOfferDtoRequest3.setTokensAndVoters(tokensAndVoters);
        assertEquals(8, addOfferDtoRequest3.getTokensAndVoters().size());

        String jsonAddOfferRequest3 = gson.toJson(addOfferDtoRequest3);
        String jsonAddOfferResponse3 = server.addOffer(jsonAddOfferRequest3);
        assertEquals(gson.toJson(""), jsonAddOfferResponse3);

        VoteForCandidateDtoRequest voteForCandidateDtoRequest1 = new VoteForCandidateDtoRequest(3,0,voterToken1, candidateToken1);
        assertEquals(voterToken1, voteForCandidateDtoRequest1.getVotingToken());
        assertEquals(candidateToken1, voteForCandidateDtoRequest1.getCandidateToken());
        voteForCandidateDtoRequest1.setTokensAndVoters(tokensAndVoters);
        assertEquals(8, voteForCandidateDtoRequest1.getTokensAndVoters().size());

        String jsonVoterForCandidateRequest1 = gson.toJson(voteForCandidateDtoRequest1);
        String jsonVoterForCandidateResponse1 = server.voteForCandidate(jsonVoterForCandidateRequest1);
        assertEquals(gson.toJson(""), jsonVoterForCandidateResponse1);

        VoteForCandidateDtoRequest voteForCandidateDtoRequest2 = new VoteForCandidateDtoRequest(4,1,voterToken2, candidateToken2);
        assertEquals(voterToken2, voteForCandidateDtoRequest2.getVotingToken());
        assertEquals(candidateToken2, voteForCandidateDtoRequest2.getCandidateToken());
        voteForCandidateDtoRequest2.setTokensAndVoters(tokensAndVoters);
        assertEquals(8, voteForCandidateDtoRequest2.getTokensAndVoters().size());

        String jsonVoterForCandidateRequest2 = gson.toJson(voteForCandidateDtoRequest2);
        String jsonVoterForCandidateResponse2 = server.voteForCandidate(jsonVoterForCandidateRequest2);
        assertEquals(gson.toJson(""), jsonVoterForCandidateResponse2);

        VoteForCandidateDtoRequest voteForCandidateDtoRequest3 = new VoteForCandidateDtoRequest(5,2,voterToken3, candidateToken3);
        assertEquals(voterToken3, voteForCandidateDtoRequest3.getVotingToken());
        assertEquals(candidateToken3, voteForCandidateDtoRequest3.getCandidateToken());
        voteForCandidateDtoRequest3.setTokensAndVoters(tokensAndVoters);
        assertEquals(8, voteForCandidateDtoRequest3.getTokensAndVoters().size());

        String jsonVoterForCandidateRequest3 = gson.toJson(voteForCandidateDtoRequest3);
        String jsonVoterForCandidateResponse3 = server.voteForCandidate(jsonVoterForCandidateRequest3);
        assertEquals(gson.toJson(""), jsonVoterForCandidateResponse3);

        VoteForCandidateDtoRequest voteForCandidateDtoRequest4 = new VoteForCandidateDtoRequest(6,2,voterToken4, candidateToken3);
        assertEquals(voterToken4, voteForCandidateDtoRequest4.getVotingToken());
        assertEquals(candidateToken3, voteForCandidateDtoRequest4.getCandidateToken());
        voteForCandidateDtoRequest4.setTokensAndVoters(tokensAndVoters);
        assertEquals(8, voteForCandidateDtoRequest4.getTokensAndVoters().size());

        String jsonVoterForCandidateRequest4 = gson.toJson(voteForCandidateDtoRequest4);
        String jsonVoterForCandidateResponse4 = server.voteForCandidate(jsonVoterForCandidateRequest4);
        assertEquals(gson.toJson(""), jsonVoterForCandidateResponse4);

        Voter voter5 = dataBase.getVoterById(registerRequest8.getId());
        VoteAgainstAllDtoRequest voteAgainstAllDtoRequest5 = new VoteAgainstAllDtoRequest(voterToken5, voter5);
        assertEquals(voterToken5, voteAgainstAllDtoRequest5.getVotingToken());
        voteAgainstAllDtoRequest5.setTokensAndVoters(dataBase.getTokensAndVoters());
        assertEquals(8, voteAgainstAllDtoRequest5.getTokensAndVoters().size());

        String jsonVoteAgainstAllRequest5 = gson.toJson(voteAgainstAllDtoRequest5);
        String jsonVoteAgainstAllResponse5 = server.voteAgainstAll(jsonVoteAgainstAllRequest5);
        assertEquals(gson.toJson(""), jsonVoteAgainstAllResponse5);

        Candidate candidate1 = dataBase.getCandidateById(addCandidateRequest1.getId());
        VoteAgainstAllDtoRequest voteAgainstAllDtoRequest6 = new VoteAgainstAllDtoRequest(candidateToken1, candidate1);
        assertEquals(candidateToken1, voteAgainstAllDtoRequest6.getVotingToken());
        voteAgainstAllDtoRequest6.setTokensAndVoters(dataBase.getTokensAndVoters());
        assertEquals(8, voteAgainstAllDtoRequest6.getTokensAndVoters().size());

        String jsonVoteAgainstAllRequest6 = gson.toJson(voteAgainstAllDtoRequest6);
        String jsonVoteAgainstAllResponse6 = server.voteAgainstAll(jsonVoteAgainstAllRequest6);
        assertEquals(gson.toJson(""), jsonVoteAgainstAllResponse6);

        Candidate candidate2 = dataBase.getCandidateById(addCandidateRequest2.getId());
        VoteAgainstAllDtoRequest voteAgainstAllDtoRequest7 = new VoteAgainstAllDtoRequest(candidateToken2, candidate2);
        assertEquals(candidateToken2, voteAgainstAllDtoRequest7.getVotingToken());
        voteAgainstAllDtoRequest7.setTokensAndVoters(dataBase.getTokensAndVoters());
        assertEquals(8, voteAgainstAllDtoRequest7.getTokensAndVoters().size());

        String jsonVoteAgainstAllRequest7 = gson.toJson(voteAgainstAllDtoRequest7);
        String jsonVoteAgainstAllResponse7 = server.voteAgainstAll(jsonVoteAgainstAllRequest7);
        assertEquals(gson.toJson(""), jsonVoteAgainstAllResponse7);

        Candidate candidate3 = dataBase.getCandidateById(addCandidateRequest3.getId());
        VoteAgainstAllDtoRequest voteAgainstAllDtoRequest8 = new VoteAgainstAllDtoRequest(candidateToken3,candidate3);
        assertEquals(candidateToken3, voteAgainstAllDtoRequest8.getVotingToken());
        voteAgainstAllDtoRequest8.setTokensAndVoters(dataBase.getTokensAndVoters());
        assertEquals(8, voteAgainstAllDtoRequest8.getTokensAndVoters().size());

        String jsonVoteAgainstAllRequest8 = gson.toJson(voteAgainstAllDtoRequest8);
        String jsonVoteAgainstAllResponse8 = server.voteAgainstAll(jsonVoteAgainstAllRequest8);
        assertEquals(gson.toJson(""), jsonVoteAgainstAllResponse8);

        GetElectionResultsDtoRequest getElectionResultsDtoRequest = new GetElectionResultsDtoRequest(voterToken1);
        assertEquals(voterToken1, getElectionResultsDtoRequest.getToken());
        getElectionResultsDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(8, getElectionResultsDtoRequest.getTokensAndVoters().size());

        String jsonGetElectionResultsRequest = gson.toJson(getElectionResultsDtoRequest);
        String jsonGetElectionResultsResponse = server.getElectionResults(jsonGetElectionResultsRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonGetElectionResultsResponse);

        assertEquals(1, candidate1.getAmountVotes());
        assertEquals(1, candidate2.getAmountVotes());
        assertEquals(2, candidate3.getAmountVotes());
        assertEquals(4, dataBase.getVotedAgainstAll().size());
        assertTrue(candidate3.getAmountVotes() > candidate1.getAmountVotes());
        assertTrue(candidate3.getAmountVotes() > candidate2.getAmountVotes());
        assertTrue(dataBase.getVotedAgainstAll().size() > candidate3.getAmountVotes());

        server.stopServer(null);
    }

    @Test
    public void testGetElectionResultsIfEveryoneVotedAgainstAll() throws IOException, VoterException {
        Server server = new Server();
        server.startServer(null);

        dataBase.getRankedCandidates().clear();

        RegisterVoterDtoRequest registerRequest1 = new RegisterVoterDtoRequest(0,"petr1","petrov1","12345678");
        assertEquals("petr1", registerRequest1.getFirstName());
        assertEquals("petrov1", registerRequest1.getLogin());
        assertEquals("12345678", registerRequest1.getPassword());
        String jsonRegisterRequest1 = gson.toJson(registerRequest1);
        String jsonRegisterResponse1  = server.registerVoter(jsonRegisterRequest1);

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
        String jsonAddCandidateResponse1  = server.addCandidate(jsonAddCandidateRequest1);

        AddCandidateDtoResponse addCandidateDtoResponse1 = gson.fromJson(jsonAddCandidateResponse1, AddCandidateDtoResponse.class);
        String resultAddCandidateResponseJson1 = gson.toJson(addCandidateDtoResponse1);
        assertEquals(resultAddCandidateResponseJson1, jsonAddCandidateResponse1);

        RegisterVoterDtoRequest registerRequest2 = new RegisterVoterDtoRequest(1, "petr2","petrov2","12345678");
        assertEquals("petr2", registerRequest2.getFirstName());
        assertEquals("petrov2", registerRequest2.getLogin());
        assertEquals("12345678", registerRequest2.getPassword());
        String jsonRegisterRequest2 = gson.toJson(registerRequest2);
        String jsonRegisterResponse2  = server.registerVoter(jsonRegisterRequest2);

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
        String jsonAddCandidateResponse2  = server.addCandidate(jsonAddCandidateRequest2);

        AddCandidateDtoResponse addCandidateDtoResponse2 = gson.fromJson(jsonAddCandidateResponse2, AddCandidateDtoResponse.class);
        String resultAddCandidateResponseJson2 = gson.toJson(addCandidateDtoResponse2);
        assertEquals(resultAddCandidateResponseJson2, jsonAddCandidateResponse2);

        RegisterVoterDtoRequest registerRequest3 = new RegisterVoterDtoRequest(2, "petr3","petrov3","12345678");
        assertEquals("petr3", registerRequest3.getFirstName());
        assertEquals("petrov3", registerRequest3.getLogin());
        assertEquals("12345678", registerRequest3.getPassword());
        String jsonRegisterRequest3 = gson.toJson(registerRequest3);
        String jsonRegisterResponse3  = server.registerVoter(jsonRegisterRequest3);

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
        String jsonAddCandidateResponse3  = server.addCandidate(jsonAddCandidateRequest3);

        AddCandidateDtoResponse addCandidateDtoResponse3 = gson.fromJson(jsonAddCandidateResponse3, AddCandidateDtoResponse.class);
        String resultAddCandidateResponseJson3 = gson.toJson(addCandidateDtoResponse3);
        assertEquals(resultAddCandidateResponseJson3, jsonAddCandidateResponse3);

        RegisterVoterDtoRequest registerRequest4 = new RegisterVoterDtoRequest(3, "boris1","borisov1","12345678");
        assertEquals("boris1", registerRequest4.getFirstName());
        assertEquals("borisov1", registerRequest4.getLogin());
        assertEquals("12345678", registerRequest4.getPassword());
        String jsonRegisterRequest4 = gson.toJson(registerRequest4);
        String jsonRegisterResponse4  = server.registerVoter(jsonRegisterRequest4);

        RegisterVoterDtoResponse registerVoterDtoResponse4 = gson.fromJson(jsonRegisterResponse4, RegisterVoterDtoResponse.class);
        String resultRegisterJson4 = gson.toJson(registerVoterDtoResponse4);
        assertEquals(resultRegisterJson4, jsonRegisterResponse4);

        RegisterVoterDtoRequest registerRequest5 = new RegisterVoterDtoRequest(4, "boris2","borisov2","12345678");
        assertEquals("boris2", registerRequest5.getFirstName());
        assertEquals("borisov2", registerRequest5.getLogin());
        assertEquals("12345678", registerRequest5.getPassword());
        String jsonRegisterRequest5 = gson.toJson(registerRequest5);
        String jsonRegisterResponse5  = server.registerVoter(jsonRegisterRequest5);

        RegisterVoterDtoResponse registerVoterDtoResponse5 = gson.fromJson(jsonRegisterResponse5, RegisterVoterDtoResponse.class);
        String resultRegisterJson5 = gson.toJson(registerVoterDtoResponse5);
        assertEquals(resultRegisterJson5, jsonRegisterResponse5);

        RegisterVoterDtoRequest registerRequest6 = new RegisterVoterDtoRequest(5, "boris3","borisov3","12345678");
        assertEquals("boris3", registerRequest6.getFirstName());
        assertEquals("borisov3", registerRequest6.getLogin());
        assertEquals("12345678", registerRequest6.getPassword());
        String jsonRegisterRequest6 = gson.toJson(registerRequest6);
        String jsonRegisterResponse6  = server.registerVoter(jsonRegisterRequest6);

        RegisterVoterDtoResponse registerVoterDtoResponse6 = gson.fromJson(jsonRegisterResponse6, RegisterVoterDtoResponse.class);
        String resultRegisterJson6 = gson.toJson(registerVoterDtoResponse6);
        assertEquals(resultRegisterJson6, jsonRegisterResponse6);

        RegisterVoterDtoRequest registerRequest7 = new RegisterVoterDtoRequest(6, "boris4","borisov4","12345678");
        assertEquals("boris4", registerRequest7.getFirstName());
        assertEquals("borisov4", registerRequest7.getLogin());
        assertEquals("12345678", registerRequest7.getPassword());
        String jsonRegisterRequest7 = gson.toJson(registerRequest7);
        String jsonRegisterResponse7  = server.registerVoter(jsonRegisterRequest7);

        RegisterVoterDtoResponse registerVoterDtoResponse7 = gson.fromJson(jsonRegisterResponse7, RegisterVoterDtoResponse.class);
        String resultRegisterJson7 = gson.toJson(registerVoterDtoResponse7);
        assertEquals(resultRegisterJson7, jsonRegisterResponse7);

        RegisterVoterDtoRequest registerRequest8 = new RegisterVoterDtoRequest(7, "boris5","borisov5","12345678");
        assertEquals("boris5", registerRequest8.getFirstName());
        assertEquals("borisov5", registerRequest8.getLogin());
        assertEquals("12345678", registerRequest8.getPassword());
        String jsonRegisterRequest8 = gson.toJson(registerRequest8);
        String jsonRegisterResponse8  = server.registerVoter(jsonRegisterRequest8);

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
        String jsonAddOfferResponse1 = server.addOffer(jsonAddOfferRequest1);
        assertEquals(gson.toJson(""), jsonAddOfferResponse1);

        AddOfferDtoRequest addOfferDtoRequest2 = new AddOfferDtoRequest(voterToken2, 4,candidateToken2,1, "repair the road");
        assertEquals(voterToken2, addOfferDtoRequest2.getVoterToken());
        assertEquals(candidateToken2, addOfferDtoRequest2.getCandidateToken());
        assertEquals("repair the road", addOfferDtoRequest2.getOfferDescription());

        addOfferDtoRequest2.setTokensAndVoters(tokensAndVoters);
        assertEquals(8, addOfferDtoRequest2.getTokensAndVoters().size());

        String jsonAddOfferRequest2 = gson.toJson(addOfferDtoRequest2);
        String jsonAddOfferResponse2 = server.addOffer(jsonAddOfferRequest2);
        assertEquals(gson.toJson(""), jsonAddOfferResponse2);

        AddOfferDtoRequest addOfferDtoRequest3 = new AddOfferDtoRequest(voterToken2,4, candidateToken3,2, "increase the minimum salary");
        assertEquals(voterToken2, addOfferDtoRequest3.getVoterToken());
        assertEquals(candidateToken3, addOfferDtoRequest3.getCandidateToken());
        assertEquals("increase the minimum salary", addOfferDtoRequest3.getOfferDescription());

        addOfferDtoRequest3.setTokensAndVoters(tokensAndVoters);
        assertEquals(8, addOfferDtoRequest3.getTokensAndVoters().size());

        String jsonAddOfferRequest3 = gson.toJson(addOfferDtoRequest3);
        String jsonAddOfferResponse3 = server.addOffer(jsonAddOfferRequest3);
        assertEquals(gson.toJson(""), jsonAddOfferResponse3);

        Voter voter1 = dataBase.getVoterById(registerRequest4.getId());
        VoteAgainstAllDtoRequest voteAgainstAllDtoRequest1 = new VoteAgainstAllDtoRequest(voterToken1, voter1);
        assertEquals(voterToken1, voteAgainstAllDtoRequest1.getVotingToken());
        voteAgainstAllDtoRequest1.setTokensAndVoters(dataBase.getTokensAndVoters());
        assertEquals(8, voteAgainstAllDtoRequest1.getTokensAndVoters().size());

        String jsonVoteAgainstAllRequest1 = gson.toJson(voteAgainstAllDtoRequest1);
        String jsonVoteAgainstAllResponse1 = server.voteAgainstAll(jsonVoteAgainstAllRequest1);
        assertEquals(gson.toJson(""), jsonVoteAgainstAllResponse1);

        Voter voter2 = dataBase.getVoterById(registerRequest5.getId());
        VoteAgainstAllDtoRequest voteAgainstAllDtoRequest2 = new VoteAgainstAllDtoRequest(voterToken2,voter2);
        assertEquals(voterToken2, voteAgainstAllDtoRequest2.getVotingToken());
        voteAgainstAllDtoRequest2.setTokensAndVoters(dataBase.getTokensAndVoters());
        assertEquals(8, voteAgainstAllDtoRequest2.getTokensAndVoters().size());

        String jsonVoteAgainstAllRequest2 = gson.toJson(voteAgainstAllDtoRequest2);
        String jsonVoteAgainstAllResponse2 = server.voteAgainstAll(jsonVoteAgainstAllRequest2);
        assertEquals(gson.toJson(""), jsonVoteAgainstAllResponse2);

        Voter voter3 = dataBase.getVoterById(registerRequest6.getId());
        VoteAgainstAllDtoRequest voteAgainstAllDtoRequest3 = new VoteAgainstAllDtoRequest(voterToken3,voter3);
        assertEquals(voterToken3, voteAgainstAllDtoRequest3.getVotingToken());
        voteAgainstAllDtoRequest3.setTokensAndVoters(dataBase.getTokensAndVoters());
        assertEquals(8, voteAgainstAllDtoRequest2.getTokensAndVoters().size());

        String jsonVoteAgainstAllRequest3 = gson.toJson(voteAgainstAllDtoRequest3);
        String jsonVoteAgainstAllResponse3 = server.voteAgainstAll(jsonVoteAgainstAllRequest3);
        assertEquals(gson.toJson(""), jsonVoteAgainstAllResponse3);

        Voter voter4 = dataBase.getVoterById(registerRequest7.getId());
        VoteAgainstAllDtoRequest voteAgainstAllDtoRequest4 = new VoteAgainstAllDtoRequest(voterToken4,voter4);
        assertEquals(voterToken4, voteAgainstAllDtoRequest4.getVotingToken());
        voteAgainstAllDtoRequest4.setTokensAndVoters(dataBase.getTokensAndVoters());
        assertEquals(8, voteAgainstAllDtoRequest4.getTokensAndVoters().size());

        String jsonVoteAgainstAllRequest4 = gson.toJson(voteAgainstAllDtoRequest4);
        String jsonVoteAgainstAllResponse4 = server.voteAgainstAll(jsonVoteAgainstAllRequest4);
        assertEquals(gson.toJson(""), jsonVoteAgainstAllResponse4);

        Voter voter5 = dataBase.getVoterById(registerRequest8.getId());
        VoteAgainstAllDtoRequest voteAgainstAllDtoRequest5 = new VoteAgainstAllDtoRequest(voterToken5,voter5);
        assertEquals(voterToken5, voteAgainstAllDtoRequest5.getVotingToken());
        voteAgainstAllDtoRequest5.setTokensAndVoters(dataBase.getTokensAndVoters());
        assertEquals(8, voteAgainstAllDtoRequest5.getTokensAndVoters().size());

        String jsonVoteAgainstAllRequest5 = gson.toJson(voteAgainstAllDtoRequest5);
        String jsonVoteAgainstAllResponse5 = server.voteAgainstAll(jsonVoteAgainstAllRequest5);
        assertEquals(gson.toJson(""), jsonVoteAgainstAllResponse5);

        Candidate candidate1 = dataBase.getCandidateById(registerRequest1.getId());
        VoteAgainstAllDtoRequest voteAgainstAllDtoRequest6 = new VoteAgainstAllDtoRequest(candidateToken1, candidate1);
        assertEquals(candidateToken1, voteAgainstAllDtoRequest6.getVotingToken());
        voteAgainstAllDtoRequest6.setTokensAndVoters(dataBase.getTokensAndVoters());
        assertEquals(8, voteAgainstAllDtoRequest6.getTokensAndVoters().size());

        String jsonVoteAgainstAllRequest6 = gson.toJson(voteAgainstAllDtoRequest6);
        String jsonVoteAgainstAllResponse6 = server.voteAgainstAll(jsonVoteAgainstAllRequest6);
        assertEquals(gson.toJson(""), jsonVoteAgainstAllResponse6);

        Candidate candidate2 = dataBase.getCandidateById(registerRequest2.getId());
        VoteAgainstAllDtoRequest voteAgainstAllDtoRequest7 = new VoteAgainstAllDtoRequest(candidateToken2,candidate2);
        assertEquals(candidateToken2, voteAgainstAllDtoRequest7.getVotingToken());
        voteAgainstAllDtoRequest7.setTokensAndVoters(dataBase.getTokensAndVoters());
        assertEquals(8, voteAgainstAllDtoRequest7.getTokensAndVoters().size());

        String jsonVoteAgainstAllRequest7 = gson.toJson(voteAgainstAllDtoRequest7);
        String jsonVoteAgainstAllResponse7 = server.voteAgainstAll(jsonVoteAgainstAllRequest7);
        assertEquals(gson.toJson(""), jsonVoteAgainstAllResponse7);

        Candidate candidate3 = dataBase.getCandidateById(registerRequest3.getId());
        VoteAgainstAllDtoRequest voteAgainstAllDtoRequest8 = new VoteAgainstAllDtoRequest(candidateToken3,candidate3);
        assertEquals(candidateToken3, voteAgainstAllDtoRequest8.getVotingToken());
        voteAgainstAllDtoRequest8.setTokensAndVoters(dataBase.getTokensAndVoters());
        assertEquals(8, voteAgainstAllDtoRequest8.getTokensAndVoters().size());

        String jsonVoteAgainstAllRequest8 = gson.toJson(voteAgainstAllDtoRequest8);
        String jsonVoteAgainstAllResponse8 = server.voteAgainstAll(jsonVoteAgainstAllRequest8);
        assertEquals(gson.toJson(""), jsonVoteAgainstAllResponse8);

        GetElectionResultsDtoRequest getElectionResultsDtoRequest = new GetElectionResultsDtoRequest(voterToken1);
        assertEquals(voterToken1, getElectionResultsDtoRequest.getToken());
        getElectionResultsDtoRequest.setTokensAndVoters(tokensAndVoters);
        assertEquals(8, getElectionResultsDtoRequest.getTokensAndVoters().size());

        String jsonGetElectionResultsRequest = gson.toJson(getElectionResultsDtoRequest);
        String jsonGetElectionResultsResponse = server.getElectionResults(jsonGetElectionResultsRequest);
        assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonGetElectionResultsResponse);

        assertEquals(0, candidate1.getAmountVotes());
        assertEquals(0, candidate2.getAmountVotes());
        assertEquals(0, candidate3.getAmountVotes());
        assertEquals(8, dataBase.getVotedAgainstAll().size());
        assertTrue(dataBase.getRankedCandidates().isEmpty());

        server.stopServer(null);
    }

    @Test
    public void testGetElectionResultsIfVotingHasNotStarted2() throws IOException, VoterException {
        Server server = new Server();
        server.startServer(null);

        dataBase.getRankedCandidates().clear();
        try {
            RegisterVoterDtoRequest registerRequest1 = new RegisterVoterDtoRequest(0,"petr1", "petrov1", "12345678");
            assertEquals("petr1", registerRequest1.getFirstName());
            assertEquals("petrov1", registerRequest1.getLogin());
            assertEquals("12345678", registerRequest1.getPassword());
            String jsonRegisterRequest1 = gson.toJson(registerRequest1);
            String jsonRegisterResponse1 = server.registerVoter(jsonRegisterRequest1);

            RegisterVoterDtoResponse registerVoterDtoResponse1 = gson.fromJson(jsonRegisterResponse1, RegisterVoterDtoResponse.class);
            String resultRegisterJson1 = gson.toJson(registerVoterDtoResponse1);
            assertEquals(resultRegisterJson1, jsonRegisterResponse1);

            String voterToken1 = registerVoterDtoResponse1.getToken();

            GetElectionResultsDtoRequest getElectionResultsDtoRequest = new GetElectionResultsDtoRequest(voterToken1);
            assertEquals(voterToken1, getElectionResultsDtoRequest.getToken());
            getElectionResultsDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(1, getElectionResultsDtoRequest.getTokensAndVoters().size());

            String jsonGetElectionResultsRequest = gson.toJson(getElectionResultsDtoRequest);
            String jsonGetElectionResultsResponse = server.getElectionResults(jsonGetElectionResultsRequest);
            assertEquals(VoterErrorCode.ERROR_RESPONSE.getErrorString(), jsonGetElectionResultsResponse);
            fail();
        }catch(VoterException ve){
            assertEquals(VoterErrorCode.VOTING_HAS_NOT_STARTED, ve.getVoterErrorCode());
            assertTrue(dataBase.getVotedAgainstAll().isEmpty());
            assertTrue(dataBase.getRankedCandidates().isEmpty());
            server.stopServer(null);
        }
    }
}
