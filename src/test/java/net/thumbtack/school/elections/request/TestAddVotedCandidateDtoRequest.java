package net.thumbtack.school.elections.request;

import net.thumbtack.school.elections.database.DataBase;
import net.thumbtack.school.elections.exception.VoterErrorCode;
import net.thumbtack.school.elections.exception.VoterException;
import net.thumbtack.school.elections.model.Candidate;
import net.thumbtack.school.elections.model.Voter;
import net.thumbtack.school.elections.response.RegisterVoterDtoResponse;
import net.thumbtack.school.elections.service.GenerateTokenService;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import java.util.Map;

import static org.junit.Assert.*;

public class TestAddVotedCandidateDtoRequest {

    private static String candidateToken;
    private static DataBase dataBase;

    @BeforeClass
    public static void setUpClass(){
        dataBase = DataBase.getDataBase();
        candidateToken = GenerateTokenService.generateNewToken();
    }

    @Test
    public void testAddVotedCandidateDtoRequest() {
        dataBase.getTokensAndVoters().clear();
        AddVotedCandidateDtoRequest addVotedCandidateDtoRequest = new AddVotedCandidateDtoRequest(1, candidateToken);
        assertEquals(candidateToken, addVotedCandidateDtoRequest.getCandidateToken());
        addVotedCandidateDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
        assertEquals(0, addVotedCandidateDtoRequest.getTokensAndVoters().size());

        String newCandidateToken = GenerateTokenService.generateNewToken();
        addVotedCandidateDtoRequest.setCandidateToken(newCandidateToken);
        assertEquals(newCandidateToken, addVotedCandidateDtoRequest.getCandidateToken());
    }

    @Test
    public void testValidateWrongCandidateToken() {
        dataBase.getVotedCandidates().clear();
        dataBase.getTokensAndVoters().clear();
        try {
            AddVotedCandidateDtoRequest addVotedCandidateDtoRequest = new AddVotedCandidateDtoRequest(1, "");
            assertEquals("", addVotedCandidateDtoRequest.getCandidateToken());
            dataBase.getTokensAndVoters().put(addVotedCandidateDtoRequest.getCandidateToken(), new Candidate("ivan","ivanov", "123456789"));
            addVotedCandidateDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(1, addVotedCandidateDtoRequest.getTokensAndVoters().size());

            addVotedCandidateDtoRequest.validate();
            fail();
        } catch (VoterException ex) {
            assertEquals(VoterErrorCode.CANDIDATE_WRONG_TOKEN, ex.getVoterErrorCode());
        }

        dataBase.getVotedCandidates().clear();
        dataBase.getTokensAndVoters().clear();
        try {
            AddVotedCandidateDtoRequest addVotedCandidateDtoRequest = new AddVotedCandidateDtoRequest(1, null);
            assertEquals(null, addVotedCandidateDtoRequest.getCandidateToken());
            dataBase.getTokensAndVoters().put(addVotedCandidateDtoRequest.getCandidateToken(), new Candidate("ivan","ivanov", "123456789"));
            addVotedCandidateDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(1, addVotedCandidateDtoRequest.getTokensAndVoters().size());

            addVotedCandidateDtoRequest.validate();
            fail();
        }  catch (VoterException ex) {
            assertEquals(VoterErrorCode.CANDIDATE_WRONG_TOKEN, ex.getVoterErrorCode());
        }

        dataBase.getVotedCandidates().clear();
        dataBase.getTokensAndVoters().clear();
        try {
            AddVotedCandidateDtoRequest addVotedCandidateDtoRequest = new AddVotedCandidateDtoRequest(1, candidateToken);
            addVotedCandidateDtoRequest.setCandidateToken("");
            dataBase.getTokensAndVoters().put(addVotedCandidateDtoRequest.getCandidateToken(), new Candidate("ivan","ivanov", "123456789"));
            addVotedCandidateDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(1, addVotedCandidateDtoRequest.getTokensAndVoters().size());

            addVotedCandidateDtoRequest.validate();
            fail();
        }  catch (VoterException ex) {
            assertEquals(VoterErrorCode.CANDIDATE_WRONG_TOKEN, ex.getVoterErrorCode());
        }

        dataBase.getVotedCandidates().clear();dataBase.getVotedCandidates().clear();
        dataBase.getTokensAndVoters().clear();
        try {
            AddVotedCandidateDtoRequest addVotedCandidateDtoRequest = new AddVotedCandidateDtoRequest(1, candidateToken);
            addVotedCandidateDtoRequest.setCandidateToken(null);
            dataBase.getTokensAndVoters().put(addVotedCandidateDtoRequest.getCandidateToken(), new Candidate("ivan","ivanov", "123456789"));
            addVotedCandidateDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(1, addVotedCandidateDtoRequest.getTokensAndVoters().size());

            addVotedCandidateDtoRequest.validate();
            fail();
        }  catch (VoterException ex) {
            assertEquals(VoterErrorCode.CANDIDATE_WRONG_TOKEN, ex.getVoterErrorCode());
        }
    }

    @Test
    public void testValidateOfflineCandidateToken() {
        try {
            Candidate candidate = new Candidate("ivan", "ivanov", "12345678");
            AddVotedCandidateDtoRequest addVotedCandidateDtoRequest = new AddVotedCandidateDtoRequest(candidate.getId(), candidateToken);

            Map<String, Voter> tokensAndVoters = dataBase.getTokensAndVoters();
            addVotedCandidateDtoRequest.setTokensAndVoters(tokensAndVoters);
            assertEquals(0, addVotedCandidateDtoRequest.getTokensAndVoters().size());

            addVotedCandidateDtoRequest.validate();
            fail();
        } catch (VoterException ex) {
            assertEquals(VoterErrorCode.OFFLINE_TOKEN, ex.getVoterErrorCode());
        }
    }
}
