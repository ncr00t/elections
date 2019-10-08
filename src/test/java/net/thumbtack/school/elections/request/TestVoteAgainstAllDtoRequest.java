package net.thumbtack.school.elections.request;

import net.thumbtack.school.elections.database.DataBase;
import net.thumbtack.school.elections.exception.VoterErrorCode;
import net.thumbtack.school.elections.exception.VoterException;
import net.thumbtack.school.elections.model.Voter;
import net.thumbtack.school.elections.response.RegisterVoterDtoResponse;
import net.thumbtack.school.elections.service.GenerateTokenService;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import java.util.Map;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class TestVoteAgainstAllDtoRequest {

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
    public void testVoteAgainstAllDtoRequest() {
        String votingToken = GenerateTokenService.generateNewToken();
        Voter voter = new Voter("ivan", "ivanov", "1234566789");

        VoteAgainstAllDtoRequest voteAgainstAllDtoRequest = new VoteAgainstAllDtoRequest(votingToken, voter);
        assertEquals(votingToken, voteAgainstAllDtoRequest.getVotingToken());
        assertEquals(voter, voteAgainstAllDtoRequest.getVoter());
        voteAgainstAllDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
        assertEquals(0, voteAgainstAllDtoRequest.getTokensAndVoters().size());

        String newVotingToken = GenerateTokenService.generateNewToken();
        voteAgainstAllDtoRequest.setVotingToken(newVotingToken);
        assertEquals(newVotingToken, voteAgainstAllDtoRequest.getVotingToken());
    }

    @Test
    public void testValidateWrongVotingToken() {
        String votingToken =  GenerateTokenService.generateNewToken();
        Voter voter = new Voter("ivan", "ivanov", "1234566789");
        try {
            VoteAgainstAllDtoRequest voteAgainstAllDtoRequest = new VoteAgainstAllDtoRequest("", voter);
            assertEquals("", voteAgainstAllDtoRequest.getVotingToken());
            assertEquals(voter, voteAgainstAllDtoRequest.getVoter());
            voteAgainstAllDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(0, voteAgainstAllDtoRequest.getTokensAndVoters().size());

            voteAgainstAllDtoRequest.validate();
            fail();
        } catch (VoterException ex) {
            assertEquals(VoterErrorCode.VOTER_WRONG_TOKEN, ex.getVoterErrorCode());
        }

        try {
            VoteAgainstAllDtoRequest voteAgainstAllDtoRequest = new VoteAgainstAllDtoRequest(null, voter);
            assertEquals(null, voteAgainstAllDtoRequest.getVotingToken());
            assertEquals(voter, voteAgainstAllDtoRequest.getVoter());
            voteAgainstAllDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(0, voteAgainstAllDtoRequest.getTokensAndVoters().size());

            voteAgainstAllDtoRequest.validate();
            fail();
        } catch (VoterException ex) {
            assertEquals(VoterErrorCode.VOTER_WRONG_TOKEN, ex.getVoterErrorCode());
        }

        try {
            VoteAgainstAllDtoRequest voteAgainstAllDtoRequest = new VoteAgainstAllDtoRequest(votingToken, voter);
            voteAgainstAllDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(0, voteAgainstAllDtoRequest.getTokensAndVoters().size());
            assertEquals(voter, voteAgainstAllDtoRequest.getVoter());
            voteAgainstAllDtoRequest.setVotingToken("");

            voteAgainstAllDtoRequest.validate();
            fail();
        }  catch (VoterException ex) {
            assertEquals(VoterErrorCode.VOTER_WRONG_TOKEN, ex.getVoterErrorCode());
        }

        try {
            VoteAgainstAllDtoRequest voteAgainstAllDtoRequest = new VoteAgainstAllDtoRequest(votingToken, voter);
            voteAgainstAllDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(0, voteAgainstAllDtoRequest.getTokensAndVoters().size());
            voteAgainstAllDtoRequest.setVotingToken(null);
            assertEquals(voter, voteAgainstAllDtoRequest.getVoter());

            voteAgainstAllDtoRequest.validate();
            fail();
        }  catch (VoterException ex) {
            assertEquals(VoterErrorCode.VOTER_WRONG_TOKEN, ex.getVoterErrorCode());
        }
    }

    @Test
    public void testValidateOfflineVotingToken() {
        String votingToken = GenerateTokenService.generateNewToken();
        Voter voter = new Voter("ivan", "ivanov", "1234566789");

        try {
            VoteAgainstAllDtoRequest voteAgainstAllDtoRequest = new VoteAgainstAllDtoRequest(votingToken, voter);
            assertEquals(voter, voteAgainstAllDtoRequest.getVoter());

            Map<String, Voter> tokensAndVoters = dataBase.getTokensAndVoters();
            voteAgainstAllDtoRequest.setTokensAndVoters(tokensAndVoters);
            assertEquals(0, voteAgainstAllDtoRequest.getTokensAndVoters().size());

            voteAgainstAllDtoRequest.validate();
            fail();
        } catch (VoterException ex) {
            assertEquals(VoterErrorCode.OFFLINE_TOKEN, ex.getVoterErrorCode());
        }
    }
}
