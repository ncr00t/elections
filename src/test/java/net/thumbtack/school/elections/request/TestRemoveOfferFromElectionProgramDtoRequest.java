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
import static org.junit.Assert.fail;

public class TestRemoveOfferFromElectionProgramDtoRequest {

    private static String candidateToken;
    private static DataBase dataBase;

    @BeforeClass
    public static void setUpClass(){
        dataBase = DataBase.getDataBase();
        candidateToken = GenerateTokenService.generateNewToken();
    }

    @Test
    public void testRemoveOfferFromElectionProgramDtoRequest() {
        RemoveOfferFromElectionProgramDtoRequest removeOfferFromElectionProgramDtoRequest = new RemoveOfferFromElectionProgramDtoRequest(candidateToken, 0, "build a bridge across the river");
        assertEquals(candidateToken, removeOfferFromElectionProgramDtoRequest.getCandidateToken());
        assertEquals("build a bridge across the river", removeOfferFromElectionProgramDtoRequest.getOfferDescription());
        removeOfferFromElectionProgramDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
        assertEquals(0, removeOfferFromElectionProgramDtoRequest.getTokensAndVoters().size());

        String newCandidateToken = GenerateTokenService.generateNewToken();
        removeOfferFromElectionProgramDtoRequest.setCandidateToken(newCandidateToken);
        assertEquals(newCandidateToken, removeOfferFromElectionProgramDtoRequest.getCandidateToken());

        String newOfferDescription = "repair the road";
        removeOfferFromElectionProgramDtoRequest.setOfferDescription(newOfferDescription);
        assertEquals(newOfferDescription, removeOfferFromElectionProgramDtoRequest.getOfferDescription());
    }

    @Test
    public void testValidateWrongCandidateToken() {
        dataBase.getTokensAndVoters().clear();
        try {
            RemoveOfferFromElectionProgramDtoRequest removeOfferFromElectionProgramDtoRequest = new RemoveOfferFromElectionProgramDtoRequest("", 0, "build a bridge across the river");
            removeOfferFromElectionProgramDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(0, removeOfferFromElectionProgramDtoRequest.getTokensAndVoters().size());

            removeOfferFromElectionProgramDtoRequest.validate();
            fail();
        } catch (VoterException ex) {
            assertEquals(VoterErrorCode.CANDIDATE_WRONG_TOKEN, ex.getVoterErrorCode());
        }

        try {
            RemoveOfferFromElectionProgramDtoRequest removeOfferFromElectionProgramDtoRequest = new RemoveOfferFromElectionProgramDtoRequest(null, 0,"build a bridge across the river");
            removeOfferFromElectionProgramDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(0, removeOfferFromElectionProgramDtoRequest.getTokensAndVoters().size());

            removeOfferFromElectionProgramDtoRequest.validate();
            fail();
        }  catch (VoterException ex) {
            assertEquals(VoterErrorCode.CANDIDATE_WRONG_TOKEN, ex.getVoterErrorCode());
        }

        try {
            RemoveOfferFromElectionProgramDtoRequest removeOfferFromElectionProgramDtoRequest = new RemoveOfferFromElectionProgramDtoRequest(candidateToken, 0,"build a bridge across the river");
            removeOfferFromElectionProgramDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(0, removeOfferFromElectionProgramDtoRequest.getTokensAndVoters().size());
            removeOfferFromElectionProgramDtoRequest.setCandidateToken("");

            removeOfferFromElectionProgramDtoRequest.validate();
            fail();
        }  catch (VoterException ex) {
            assertEquals(VoterErrorCode.CANDIDATE_WRONG_TOKEN, ex.getVoterErrorCode());
        }

        try {
            RemoveOfferFromElectionProgramDtoRequest removeOfferFromElectionProgramDtoRequest = new RemoveOfferFromElectionProgramDtoRequest(candidateToken, 0,"build a bridge across the river");
            removeOfferFromElectionProgramDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(0, removeOfferFromElectionProgramDtoRequest.getTokensAndVoters().size());
            removeOfferFromElectionProgramDtoRequest.setCandidateToken(null);

            removeOfferFromElectionProgramDtoRequest.validate();
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
            RemoveOfferFromElectionProgramDtoRequest removeOfferFromElectionProgramDtoRequest = new RemoveOfferFromElectionProgramDtoRequest(candidateToken, 0,"");
            removeOfferFromElectionProgramDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(0, removeOfferFromElectionProgramDtoRequest.getTokensAndVoters().size());

            removeOfferFromElectionProgramDtoRequest.validate();
            fail();
        } catch (VoterException ex) {
            assertEquals(VoterErrorCode.OFFER_WRONG_DESCRIPTION, ex.getVoterErrorCode());
        }

        try {
            RemoveOfferFromElectionProgramDtoRequest removeOfferFromElectionProgramDtoRequest = new RemoveOfferFromElectionProgramDtoRequest(candidateToken, 0,null);
            removeOfferFromElectionProgramDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(0, removeOfferFromElectionProgramDtoRequest.getTokensAndVoters().size());

            removeOfferFromElectionProgramDtoRequest.validate();
            fail();
        }  catch (VoterException ex) {
            assertEquals(VoterErrorCode.OFFER_WRONG_DESCRIPTION, ex.getVoterErrorCode());
        }

        try {
            RemoveOfferFromElectionProgramDtoRequest removeOfferFromElectionProgramDtoRequest = new RemoveOfferFromElectionProgramDtoRequest(candidateToken, 0,"build a bridge across the river");
            removeOfferFromElectionProgramDtoRequest.setOfferDescription("");
            removeOfferFromElectionProgramDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(0, removeOfferFromElectionProgramDtoRequest.getTokensAndVoters().size());

            removeOfferFromElectionProgramDtoRequest.validate();
            fail();
        }  catch (VoterException ex) {
            assertEquals(VoterErrorCode.OFFER_WRONG_DESCRIPTION, ex.getVoterErrorCode());
        }

        try {
            RemoveOfferFromElectionProgramDtoRequest removeOfferFromElectionProgramDtoRequest = new RemoveOfferFromElectionProgramDtoRequest(candidateToken, 0,"build a bridge across the river");
            removeOfferFromElectionProgramDtoRequest.setOfferDescription(null);
            removeOfferFromElectionProgramDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(0, removeOfferFromElectionProgramDtoRequest.getTokensAndVoters().size());

            removeOfferFromElectionProgramDtoRequest.validate();
            fail();
        }  catch (VoterException ex) {
            assertEquals(VoterErrorCode.OFFER_WRONG_DESCRIPTION, ex.getVoterErrorCode());
        }
    }

    @Test
    public void testValidateOfflineCandidateToken() {
        try {
            RemoveOfferFromElectionProgramDtoRequest removeOfferFromElectionProgramDtoRequest = new RemoveOfferFromElectionProgramDtoRequest(candidateToken, 0, "build a bridge across the river");

            Map<String, Voter> tokensAndVoters = dataBase.getTokensAndVoters();
            removeOfferFromElectionProgramDtoRequest.setTokensAndVoters(tokensAndVoters);
            assertEquals(0, removeOfferFromElectionProgramDtoRequest.getTokensAndVoters().size());

            removeOfferFromElectionProgramDtoRequest.validate();
            fail();
        } catch (VoterException ex) {
            assertEquals(VoterErrorCode.OFFLINE_TOKEN, ex.getVoterErrorCode());
        }
    }
}
