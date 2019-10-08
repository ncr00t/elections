package net.thumbtack.school.elections.request;

import net.thumbtack.school.elections.database.DataBase;
import net.thumbtack.school.elections.exception.VoterErrorCode;
import net.thumbtack.school.elections.exception.VoterException;
import net.thumbtack.school.elections.model.Candidate;
import net.thumbtack.school.elections.model.Voter;
import net.thumbtack.school.elections.response.RegisterVoterDtoResponse;
import net.thumbtack.school.elections.service.GenerateTokenService;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import java.util.Map;

import static org.junit.Assert.*;

public class TestAddOfferDtoRequest {

    private static String token;
    private static DataBase dataBase;

    @BeforeClass
    public static void setUpClass(){
        dataBase = DataBase.getDataBase();
        token = GenerateTokenService.generateNewToken();
    }

    @Before
    public void setUp(){
        dataBase.getTokensAndVoters().clear();
    }

    @Test
    public void testAddOfferDtoRequest() {
        String voterToken = token;
        String candidateToken = GenerateTokenService.generateNewToken();

        AddOfferDtoRequest addOfferDtoRequest = new AddOfferDtoRequest(voterToken, 0, candidateToken, 0, "build a bridge across the river");
        addOfferDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
        assertEquals(voterToken, addOfferDtoRequest.getVoterToken());
        assertEquals(candidateToken, addOfferDtoRequest.getCandidateToken());
        assertEquals("build a bridge across the river", addOfferDtoRequest.getOfferDescription());
        assertEquals(0, addOfferDtoRequest.getTokensAndVoters().size());

        String newVoterToken = GenerateTokenService.generateNewToken();
        addOfferDtoRequest.setVoterToken(newVoterToken);
        assertEquals(newVoterToken, addOfferDtoRequest.getVoterToken());

        String newCandidateToken = GenerateTokenService.generateNewToken();
        addOfferDtoRequest.setCandidateToken(newCandidateToken);
        assertEquals(newCandidateToken, addOfferDtoRequest.getCandidateToken());

        String newOfferDescription = "repair the road";
        addOfferDtoRequest.setOfferDescription(newOfferDescription);
        assertEquals(newOfferDescription, addOfferDtoRequest.getOfferDescription());
    }

    @Test
    public void testValidateWrongVoterToken() {
        dataBase.getTokensAndVoters().clear();
        String candidateToken = GenerateTokenService.generateNewToken();
        String voterToken = token;
        try {
            AddOfferDtoRequest addOfferDtoRequest = new AddOfferDtoRequest("", 0, candidateToken, 0, "build a bridge across the river");
            addOfferDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(0, addOfferDtoRequest.getTokensAndVoters().size());

            addOfferDtoRequest.validate();
            fail();
        } catch (VoterException ex) {
            assertEquals(VoterErrorCode.VOTER_WRONG_TOKEN, ex.getVoterErrorCode());
        }

        try {
            AddOfferDtoRequest addOfferDtoRequest = new AddOfferDtoRequest(null, 0, candidateToken, 0, "build a bridge across the river");
            addOfferDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(0, addOfferDtoRequest.getTokensAndVoters().size());

            addOfferDtoRequest.validate();
            fail();
        }  catch (VoterException ex) {
            assertEquals(VoterErrorCode.VOTER_WRONG_TOKEN, ex.getVoterErrorCode());
        }

        try {
            AddOfferDtoRequest addOfferDtoRequest = new AddOfferDtoRequest(voterToken, 0, candidateToken, 0, "build a bridge across the river");
            addOfferDtoRequest.setVoterToken("");
            addOfferDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(0, addOfferDtoRequest.getTokensAndVoters().size());

            addOfferDtoRequest.validate();
            fail();
        }  catch (VoterException ex) {
            assertEquals(VoterErrorCode.VOTER_WRONG_TOKEN, ex.getVoterErrorCode());
        }

        try {
            AddOfferDtoRequest addOfferDtoRequest = new AddOfferDtoRequest(voterToken, 0, candidateToken, 0, "build a bridge across the river");
            addOfferDtoRequest.setVoterToken(null);
            addOfferDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(0, addOfferDtoRequest.getTokensAndVoters().size());

            addOfferDtoRequest.validate();
            fail();
        }  catch (VoterException ex) {
            assertEquals(VoterErrorCode.VOTER_WRONG_TOKEN, ex.getVoterErrorCode());
        }
    }

    @Test
    public void testValidateWrongCandidateToken() {
        dataBase.getTokensAndVoters().clear();
        String voterToken = token;
        try {
            AddOfferDtoRequest addOfferDtoRequest = new AddOfferDtoRequest(voterToken, 0, "", 0, "build a bridge across the river");
            dataBase.getTokensAndVoters().put(addOfferDtoRequest.getVoterToken(), new Voter("ivan", "ivanov", "123456789"));
            dataBase.getTokensAndVoters().put(addOfferDtoRequest.getCandidateToken(), new Candidate("sergei", "sergeev", "123456789"));
            addOfferDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(2, addOfferDtoRequest.getTokensAndVoters().size());

            addOfferDtoRequest.validate();
            fail();
        } catch (VoterException ex) {
            assertEquals(VoterErrorCode.CANDIDATE_WRONG_TOKEN, ex.getVoterErrorCode());
        }

        dataBase.getTokensAndVoters().clear();
        try {
            AddOfferDtoRequest addOfferDtoRequest = new AddOfferDtoRequest(voterToken, 0, null, 0, "build a bridge across the river");
            dataBase.getTokensAndVoters().put(addOfferDtoRequest.getVoterToken(), new Voter("ivan", "ivanov", "123456789"));
            dataBase.getTokensAndVoters().put(addOfferDtoRequest.getCandidateToken(), new Candidate("sergei", "sergeev", "123456789"));
            addOfferDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(2, addOfferDtoRequest.getTokensAndVoters().size());

            addOfferDtoRequest.validate();
            fail();
        }  catch (VoterException ex) {
            assertEquals(VoterErrorCode.CANDIDATE_WRONG_TOKEN, ex.getVoterErrorCode());
        }

        dataBase.getTokensAndVoters().clear();
        try {
            String candidateToken = GenerateTokenService.generateNewToken();
            AddOfferDtoRequest addOfferDtoRequest = new AddOfferDtoRequest(voterToken, 0, candidateToken, 0, "build a bridge across the river");
            addOfferDtoRequest.setCandidateToken("");
            dataBase.getTokensAndVoters().put(addOfferDtoRequest.getVoterToken(), new Voter("ivan", "ivanov", "123456789"));
            dataBase.getTokensAndVoters().put(addOfferDtoRequest.getCandidateToken(), new Candidate("sergei", "sergeev", "123456789"));
            addOfferDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(2, addOfferDtoRequest.getTokensAndVoters().size());

            addOfferDtoRequest.validate();
            fail();
        }  catch (VoterException ex) {
            assertEquals(VoterErrorCode.CANDIDATE_WRONG_TOKEN, ex.getVoterErrorCode());
        }

        dataBase.getTokensAndVoters().clear();
        try {
            String candidateToken = GenerateTokenService.generateNewToken();
            AddOfferDtoRequest addOfferDtoRequest = new AddOfferDtoRequest(voterToken, 0, candidateToken, 0, "build a bridge across the river");
            addOfferDtoRequest.setCandidateToken(null);
            dataBase.getTokensAndVoters().put(addOfferDtoRequest.getVoterToken(), new Voter("ivan", "ivanov", "123456789"));
            dataBase.getTokensAndVoters().put(addOfferDtoRequest.getCandidateToken(), new Candidate("sergei", "sergeev", "123456789"));
            addOfferDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(2, addOfferDtoRequest.getTokensAndVoters().size());

            addOfferDtoRequest.validate();
            fail();
        }  catch (VoterException ex) {
            assertEquals(VoterErrorCode.CANDIDATE_WRONG_TOKEN, ex.getVoterErrorCode());
        }
    }

    @Test
    public void testValidateWrongOfferDescription() {
        dataBase.getTokensAndVoters().clear();
        String candidateToken = GenerateTokenService.generateNewToken();
        String voterToken = token;
        try {
            AddOfferDtoRequest addOfferDtoRequest = new AddOfferDtoRequest(voterToken, 0, candidateToken, 0, "");
            dataBase.getTokensAndVoters().put(voterToken, new Voter("ivan", "ivanov", "123456789"));
            dataBase.getTokensAndVoters().put(candidateToken, new Candidate("sergei", "sergeev", "123456789"));
            addOfferDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(2, addOfferDtoRequest.getTokensAndVoters().size());

            addOfferDtoRequest.validate();
            fail();
        } catch (VoterException ex) {
            assertEquals(VoterErrorCode.OFFER_WRONG_DESCRIPTION, ex.getVoterErrorCode());
        }

        dataBase.getTokensAndVoters().clear();
        try {
            AddOfferDtoRequest addOfferDtoRequest = new AddOfferDtoRequest(voterToken, 0, candidateToken, 0, null);
            dataBase.getTokensAndVoters().put(voterToken, new Voter("ivan", "ivanov", "123456789"));
            dataBase.getTokensAndVoters().put(candidateToken, new Candidate("sergei", "sergeev", "123456789"));
            addOfferDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(2, addOfferDtoRequest.getTokensAndVoters().size());

            addOfferDtoRequest.validate();
            fail();
        }  catch (VoterException ex) {
            assertEquals(VoterErrorCode.OFFER_WRONG_DESCRIPTION, ex.getVoterErrorCode());
        }

        dataBase.getTokensAndVoters().clear();
        try {
            AddOfferDtoRequest addOfferDtoRequest = new AddOfferDtoRequest(voterToken, 0, candidateToken, 0, "build a bridge across the river");
            addOfferDtoRequest.setOfferDescription("");
            dataBase.getTokensAndVoters().put(voterToken, new Voter("ivan", "ivanov", "123456789"));
            dataBase.getTokensAndVoters().put(candidateToken, new Candidate("sergei", "sergeev", "123456789"));
            addOfferDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(2, addOfferDtoRequest.getTokensAndVoters().size());

            addOfferDtoRequest.validate();
            fail();
        }  catch (VoterException ex) {
            assertEquals(VoterErrorCode.OFFER_WRONG_DESCRIPTION, ex.getVoterErrorCode());
        }

        dataBase.getTokensAndVoters().clear();
        try {
            AddOfferDtoRequest addOfferDtoRequest = new AddOfferDtoRequest(voterToken, 0, candidateToken, 0, "build a bridge across the river");
            addOfferDtoRequest.setOfferDescription(null);
            dataBase.getTokensAndVoters().put(voterToken, new Voter("ivan", "ivanov", "123456789"));
            dataBase.getTokensAndVoters().put(candidateToken, new Candidate("sergei", "sergeev", "123456789"));
            addOfferDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(2, addOfferDtoRequest.getTokensAndVoters().size());

            addOfferDtoRequest.validate();
            fail();
        }  catch (VoterException ex) {
            assertEquals(VoterErrorCode.OFFER_WRONG_DESCRIPTION, ex.getVoterErrorCode());
        }
    }

    @Test
    public void testValidateOfflineToken() {
        try {
            String voterToken = token;
            String candidateToken = GenerateTokenService.generateNewToken();

            AddOfferDtoRequest addOfferDtoRequest = new AddOfferDtoRequest(voterToken, 0, candidateToken, 0, "build a bridge across the river");

            Map<String, Voter> tokensAndVoters = dataBase.getTokensAndVoters();
            tokensAndVoters.put(voterToken, new Voter("ivan", "ivanov", "123456789"));
            addOfferDtoRequest.setTokensAndVoters(tokensAndVoters);
            assertTrue(addOfferDtoRequest.getTokensAndVoters().containsKey(voterToken));

            addOfferDtoRequest.validate();
            fail();
        } catch (VoterException ex) {
            assertEquals(VoterErrorCode.OFFLINE_TOKEN, ex.getVoterErrorCode());
        }
    }
}
