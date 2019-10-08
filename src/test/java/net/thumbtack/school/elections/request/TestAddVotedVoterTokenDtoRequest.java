package net.thumbtack.school.elections.request;

import net.thumbtack.school.elections.database.DataBase;
import net.thumbtack.school.elections.exception.VoterErrorCode;
import net.thumbtack.school.elections.exception.VoterException;
import net.thumbtack.school.elections.model.Voter;
import net.thumbtack.school.elections.response.RegisterVoterDtoResponse;
import net.thumbtack.school.elections.service.GenerateTokenService;
import org.junit.BeforeClass;
import org.junit.Test;
import java.util.Map;

import static org.junit.Assert.*;

public class TestAddVotedVoterTokenDtoRequest {

    private static String voterToken;
    private static DataBase dataBase;

    @BeforeClass
    public static void setUpClass(){
        dataBase = DataBase.getDataBase();
        voterToken = GenerateTokenService.generateNewToken();
    }

    @Test
    public void testAddVotedVoterTokenDtoRequest() {
        dataBase.getTokensAndVoters().clear();
        AddVotedVoterTokenDtoRequest addVotedVoterTokenDtoRequest = new AddVotedVoterTokenDtoRequest(voterToken);
        assertEquals(voterToken, addVotedVoterTokenDtoRequest.getVoterToken());
        addVotedVoterTokenDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
        assertEquals(0, addVotedVoterTokenDtoRequest.getTokensAndVoters().size());

        String newVoterToken = GenerateTokenService.generateNewToken();
        addVotedVoterTokenDtoRequest.setVoterToken(newVoterToken);
        assertEquals(newVoterToken, addVotedVoterTokenDtoRequest.getVoterToken());
    }

    @Test
    public void testValidateWrongCandidateToken() {
        dataBase.getTokensAndVoters().clear();
        try {
            AddVotedVoterTokenDtoRequest addVotedVoterTokenDtoRequest = new AddVotedVoterTokenDtoRequest("");
            assertEquals("", addVotedVoterTokenDtoRequest.getVoterToken());
            addVotedVoterTokenDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(0, addVotedVoterTokenDtoRequest.getTokensAndVoters().size());

            addVotedVoterTokenDtoRequest.validate();
            fail();
        } catch (VoterException ex) {
            assertEquals(VoterErrorCode.VOTER_WRONG_TOKEN, ex.getVoterErrorCode());
        }

        try {
            AddVotedVoterTokenDtoRequest addVotedVoterTokenDtoRequest = new AddVotedVoterTokenDtoRequest(null);
            assertEquals(null, addVotedVoterTokenDtoRequest.getVoterToken());
            addVotedVoterTokenDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(0, addVotedVoterTokenDtoRequest.getTokensAndVoters().size());

            addVotedVoterTokenDtoRequest.validate();
            fail();
        }  catch (VoterException ex) {
            assertEquals(VoterErrorCode.VOTER_WRONG_TOKEN, ex.getVoterErrorCode());
        }

        try {
            AddVotedVoterTokenDtoRequest addVotedVoterTokenDtoRequest = new AddVotedVoterTokenDtoRequest(voterToken);
            assertEquals(voterToken, addVotedVoterTokenDtoRequest.getVoterToken());
            addVotedVoterTokenDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(0, addVotedVoterTokenDtoRequest.getTokensAndVoters().size());
            addVotedVoterTokenDtoRequest.setVoterToken("");

            addVotedVoterTokenDtoRequest.validate();
            fail();
        }  catch (VoterException ex) {
            assertEquals(VoterErrorCode.VOTER_WRONG_TOKEN, ex.getVoterErrorCode());
        }

        try {
            AddVotedVoterTokenDtoRequest addVotedVoterTokenDtoRequest = new AddVotedVoterTokenDtoRequest(voterToken);
            assertEquals(voterToken, addVotedVoterTokenDtoRequest.getVoterToken());
            addVotedVoterTokenDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(0, addVotedVoterTokenDtoRequest.getTokensAndVoters().size());
            addVotedVoterTokenDtoRequest.setVoterToken(null);

            addVotedVoterTokenDtoRequest.validate();
            fail();
        }  catch (VoterException ex) {
            assertEquals(VoterErrorCode.VOTER_WRONG_TOKEN, ex.getVoterErrorCode());
        }
    }

    @Test
    public void testValidateOfflineVoterToken() {
        try {
            AddVotedVoterTokenDtoRequest addVotedVoterTokenDtoRequest = new AddVotedVoterTokenDtoRequest(voterToken);

            Map<String, Voter> tokensAndVoters = dataBase.getTokensAndVoters();
            addVotedVoterTokenDtoRequest.setTokensAndVoters(tokensAndVoters);
            assertEquals(0, addVotedVoterTokenDtoRequest.getTokensAndVoters().size());

            addVotedVoterTokenDtoRequest.validate();
            fail();
        } catch (VoterException ex) {
            assertEquals(VoterErrorCode.OFFLINE_TOKEN, ex.getVoterErrorCode());
        }
    }
}
