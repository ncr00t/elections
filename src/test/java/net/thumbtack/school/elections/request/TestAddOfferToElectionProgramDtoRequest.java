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

import static org.junit.Assert.*;

public class TestAddOfferToElectionProgramDtoRequest {

    private static String candidateToken;
    private static DataBase dataBase;

    @BeforeClass
    public static void setUpClass(){
        dataBase = DataBase.getDataBase();
        candidateToken = GenerateTokenService.generateNewToken();
    }

    @Test
    public void testAddOfferToElectionProgramDtoRequest() {
        dataBase.getTokensAndVoters().clear();
        AddOfferToElectionProgramDtoRequest addOfferToElectionProgramDtoRequest = new AddOfferToElectionProgramDtoRequest(candidateToken, 0,"build a bridge across the river");
        assertEquals(candidateToken, addOfferToElectionProgramDtoRequest.getCandidateToken());
        assertEquals("build a bridge across the river", addOfferToElectionProgramDtoRequest.getOfferDescription());
        addOfferToElectionProgramDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
        assertEquals(0, addOfferToElectionProgramDtoRequest.getTokensAndVoters().size());

        String newCandidateToken = GenerateTokenService.generateNewToken();
        addOfferToElectionProgramDtoRequest.setCandidateToken(newCandidateToken);
        assertEquals(newCandidateToken, addOfferToElectionProgramDtoRequest.getCandidateToken());

        String newOfferDescription = "repair the road";
        addOfferToElectionProgramDtoRequest.setOfferDescription(newOfferDescription);
        assertEquals(newOfferDescription, addOfferToElectionProgramDtoRequest.getOfferDescription());
    }


    @Test
    public void testValidateWrongCandidateToken() {
        dataBase.getTokensAndVoters().clear();
        try {
            AddOfferToElectionProgramDtoRequest addOfferToElectionProgramDtoRequest = new AddOfferToElectionProgramDtoRequest("", 0,"build a bridge across the river");
            dataBase.getTokensAndVoters().put(addOfferToElectionProgramDtoRequest.getCandidateToken(), new Candidate("ivan", "ivanov", "123456789"));
            addOfferToElectionProgramDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(1, addOfferToElectionProgramDtoRequest.getTokensAndVoters().size());

            addOfferToElectionProgramDtoRequest.validate();
            fail();
        } catch (VoterException ex) {
            assertEquals(VoterErrorCode.CANDIDATE_WRONG_TOKEN, ex.getVoterErrorCode());
        }

        dataBase.getTokensAndVoters().clear();
        try {
            AddOfferToElectionProgramDtoRequest addOfferToElectionProgramDtoRequest = new AddOfferToElectionProgramDtoRequest(null, 0, "build a bridge across the river");
            dataBase.getTokensAndVoters().put(addOfferToElectionProgramDtoRequest.getCandidateToken(), new Candidate("ivan", "ivanov", "123456789"));
            addOfferToElectionProgramDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(1, addOfferToElectionProgramDtoRequest.getTokensAndVoters().size());

            addOfferToElectionProgramDtoRequest.validate();
            fail();
        }  catch (VoterException ex) {
            assertEquals(VoterErrorCode.CANDIDATE_WRONG_TOKEN, ex.getVoterErrorCode());
        }

        dataBase.getTokensAndVoters().clear();
        try {
            AddOfferToElectionProgramDtoRequest addOfferToElectionProgramDtoRequest = new AddOfferToElectionProgramDtoRequest(candidateToken, 0, "build a bridge across the river");
            addOfferToElectionProgramDtoRequest.setCandidateToken("");
            dataBase.getTokensAndVoters().put(addOfferToElectionProgramDtoRequest.getCandidateToken(), new Candidate("ivan", "ivanov", "123456789"));
            addOfferToElectionProgramDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(1, addOfferToElectionProgramDtoRequest.getTokensAndVoters().size());

            addOfferToElectionProgramDtoRequest.validate();
            fail();
        }  catch (VoterException ex) {
            assertEquals(VoterErrorCode.CANDIDATE_WRONG_TOKEN, ex.getVoterErrorCode());
        }

        dataBase.getTokensAndVoters().clear();
        try {
            AddOfferToElectionProgramDtoRequest addOfferToElectionProgramDtoRequest = new AddOfferToElectionProgramDtoRequest(candidateToken, 0, "build a bridge across the river");
            addOfferToElectionProgramDtoRequest.setCandidateToken(null);
            dataBase.getTokensAndVoters().put(addOfferToElectionProgramDtoRequest.getCandidateToken(), new Candidate("ivan", "ivanov", "123456789"));
            addOfferToElectionProgramDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(1, addOfferToElectionProgramDtoRequest.getTokensAndVoters().size());

            addOfferToElectionProgramDtoRequest.validate();
            fail();
        }  catch (VoterException ex) {
            assertEquals(VoterErrorCode.CANDIDATE_WRONG_TOKEN, ex.getVoterErrorCode());
        }
    }

    @Test
    public void testValidateWrongOfferDescription() {
        dataBase.getTokensAndVoters().clear();
        String candidateToken = GenerateTokenService.generateNewToken();
        try {
            AddOfferToElectionProgramDtoRequest addOfferToElectionProgramDtoRequest = new AddOfferToElectionProgramDtoRequest(candidateToken, 0,"");
            addOfferToElectionProgramDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(0, addOfferToElectionProgramDtoRequest.getTokensAndVoters().size());

            addOfferToElectionProgramDtoRequest.validate();
            fail();
        } catch (VoterException ex) {
            assertEquals(VoterErrorCode.OFFER_WRONG_DESCRIPTION, ex.getVoterErrorCode());
        }

        try {
            AddOfferToElectionProgramDtoRequest addOfferToElectionProgramDtoRequest = new AddOfferToElectionProgramDtoRequest(candidateToken, 0, null);
            addOfferToElectionProgramDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(0, addOfferToElectionProgramDtoRequest.getTokensAndVoters().size());

            addOfferToElectionProgramDtoRequest.validate();
            fail();
        }  catch (VoterException ex) {
            assertEquals(VoterErrorCode.OFFER_WRONG_DESCRIPTION, ex.getVoterErrorCode());
        }

        try {
            AddOfferToElectionProgramDtoRequest addOfferToElectionProgramDtoRequest = new AddOfferToElectionProgramDtoRequest(candidateToken, 0,"build a bridge across the river");
            addOfferToElectionProgramDtoRequest.setOfferDescription("");
            addOfferToElectionProgramDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(0, addOfferToElectionProgramDtoRequest.getTokensAndVoters().size());

            addOfferToElectionProgramDtoRequest.validate();
            fail();
        }  catch (VoterException ex) {
            assertEquals(VoterErrorCode.OFFER_WRONG_DESCRIPTION, ex.getVoterErrorCode());
        }

        try {
            AddOfferToElectionProgramDtoRequest addOfferToElectionProgramDtoRequest = new AddOfferToElectionProgramDtoRequest(candidateToken, 0, "build a bridge across the river");
            addOfferToElectionProgramDtoRequest.setOfferDescription(null);
            addOfferToElectionProgramDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(0, addOfferToElectionProgramDtoRequest.getTokensAndVoters().size());

            addOfferToElectionProgramDtoRequest.validate();
            fail();
        }  catch (VoterException ex) {
            assertEquals(VoterErrorCode.OFFER_WRONG_DESCRIPTION, ex.getVoterErrorCode());
        }
    }

    @Test
    public void testValidateOfflineCandidateToken() {
        try {
            AddOfferToElectionProgramDtoRequest addOfferToElectionProgramDtoRequest = new AddOfferToElectionProgramDtoRequest(candidateToken, 0,"build a bridge across the river");
            Map<String, Voter> tokensAndVoters = dataBase.getTokensAndVoters();
            addOfferToElectionProgramDtoRequest.setTokensAndVoters(tokensAndVoters);

            addOfferToElectionProgramDtoRequest.validate();
            fail();
        } catch (VoterException ex) {
            assertEquals(VoterErrorCode.OFFLINE_TOKEN, ex.getVoterErrorCode());
        }
    }
}
