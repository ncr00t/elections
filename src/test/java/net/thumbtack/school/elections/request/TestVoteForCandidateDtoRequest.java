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

public class TestVoteForCandidateDtoRequest {

    private static DataBase dataBase;

    @BeforeClass
    public static void setUpClass(){
        dataBase = DataBase.getDataBase();
    }

    @Before
    public void setUp(){
        dataBase.getTokensAndVoters().clear();
    }

    @Test
    public void testVoteForCandidateDtoRequest() {
        String votingToken =  GenerateTokenService.generateNewToken();
        String candidateToken =  GenerateTokenService.generateNewToken();

        VoteForCandidateDtoRequest voteForCandidateDtoRequest = new VoteForCandidateDtoRequest(0, 0, votingToken, candidateToken);
        assertEquals(votingToken, voteForCandidateDtoRequest.getVotingToken());
        assertEquals(candidateToken, voteForCandidateDtoRequest.getCandidateToken());
        voteForCandidateDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
        assertEquals(0, voteForCandidateDtoRequest.getTokensAndVoters().size());

        String newVotingToken = GenerateTokenService.generateNewToken();
        voteForCandidateDtoRequest.setVotingToken(newVotingToken);
        assertEquals(newVotingToken, voteForCandidateDtoRequest.getVotingToken());

        String newCandidateToken = GenerateTokenService.generateNewToken();
        voteForCandidateDtoRequest.setCandidateToken(newCandidateToken);
        assertEquals(newCandidateToken, voteForCandidateDtoRequest.getCandidateToken());
    }

    @Test
    public void testValidateWrongCandidateToken() {
        String votingToken =  GenerateTokenService.generateNewToken();
        String candidateToken =  GenerateTokenService.generateNewToken();
        try {
            VoteForCandidateDtoRequest voteForCandidateDtoRequest = new VoteForCandidateDtoRequest(0, 0, votingToken, "");
            assertEquals("", voteForCandidateDtoRequest.getCandidateToken());
            voteForCandidateDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(0, voteForCandidateDtoRequest.getTokensAndVoters().size());

            voteForCandidateDtoRequest.validate();
            fail();
        } catch (VoterException ex) {
            assertEquals(VoterErrorCode.CANDIDATE_WRONG_TOKEN, ex.getVoterErrorCode());
        }

        try {
            VoteForCandidateDtoRequest voteForCandidateDtoRequest = new VoteForCandidateDtoRequest(0,0, votingToken, null);
            assertEquals(null, voteForCandidateDtoRequest.getCandidateToken());
            voteForCandidateDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(0, voteForCandidateDtoRequest.getTokensAndVoters().size());

            voteForCandidateDtoRequest.validate();
            fail();
        }  catch (VoterException ex) {
            assertEquals(VoterErrorCode.CANDIDATE_WRONG_TOKEN, ex.getVoterErrorCode());
        }

        try {
            VoteForCandidateDtoRequest voteForCandidateDtoRequest = new VoteForCandidateDtoRequest(0, 0, votingToken, candidateToken);
            voteForCandidateDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(0, voteForCandidateDtoRequest.getTokensAndVoters().size());
            voteForCandidateDtoRequest.setCandidateToken("");

            voteForCandidateDtoRequest.validate();
            fail();
        }  catch (VoterException ex) {
            assertEquals(VoterErrorCode.CANDIDATE_WRONG_TOKEN, ex.getVoterErrorCode());
        }

        try {
            VoteForCandidateDtoRequest voteForCandidateDtoRequest = new VoteForCandidateDtoRequest(0, 0, votingToken, candidateToken);
            voteForCandidateDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(0, voteForCandidateDtoRequest.getTokensAndVoters().size());
            voteForCandidateDtoRequest.setCandidateToken(null);

            voteForCandidateDtoRequest.validate();
            fail();
        }  catch (VoterException ex) {
            assertEquals(VoterErrorCode.CANDIDATE_WRONG_TOKEN, ex.getVoterErrorCode());
        }
    }

    @Test
    public void testValidateOfflineVotingToken() {
        String votingToken = GenerateTokenService.generateNewToken();
        String candidateToken = GenerateTokenService.generateNewToken();
        try {
            VoteForCandidateDtoRequest voteForCandidateDtoRequest = new VoteForCandidateDtoRequest(0,0, votingToken, candidateToken);

            Map<String, Voter> tokensAndVoters = dataBase.getTokensAndVoters();
            tokensAndVoters.put(votingToken, new Voter("ivan", "ivanov", "123456789"));
            voteForCandidateDtoRequest.setTokensAndVoters(tokensAndVoters);
            assertTrue(voteForCandidateDtoRequest.getTokensAndVoters().containsKey(votingToken));

            voteForCandidateDtoRequest.validate();
            fail();
        } catch (VoterException ex) {
            assertEquals(VoterErrorCode.OFFLINE_TOKEN, ex.getVoterErrorCode());
        }
    }

    @Test
    public void testValidateOfflineCandidateToken() {
        String votingToken =  GenerateTokenService.generateNewToken();
        String candidateToken = GenerateTokenService.generateNewToken();
        try {
            VoteForCandidateDtoRequest voteForCandidateDtoRequest = new VoteForCandidateDtoRequest(0,0, votingToken, candidateToken);

            Map<String, Voter> tokensAndVoters = dataBase.getTokensAndVoters();
            tokensAndVoters.put(candidateToken, new Candidate("ivan", "ivanov", "123456789"));
            voteForCandidateDtoRequest.setTokensAndVoters(tokensAndVoters);
            assertTrue(voteForCandidateDtoRequest.getTokensAndVoters().containsKey(candidateToken));

            voteForCandidateDtoRequest.validate();
            fail();
        } catch (VoterException ex) {
            assertEquals(VoterErrorCode.OFFLINE_TOKEN, ex.getVoterErrorCode());
        }
    }
}
