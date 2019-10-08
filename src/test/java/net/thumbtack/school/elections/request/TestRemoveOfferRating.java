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

public class TestRemoveOfferRating {

    private static DataBase dataBase;

    @BeforeClass
    public static void setUpClass(){
        dataBase = DataBase.getDataBase();
    }

    @Test
    public void testRemoveOfferRatingDtoRequest() {
        dataBase.getTokensAndVoters().clear();
        String voterToken =  GenerateTokenService.generateNewToken();
        String authorToken =  GenerateTokenService.generateNewToken();

        Voter voter = new Voter("ivan", "ivanov", "123456789");
        Voter author = new Candidate("sergei", "sergeev", "12345678");

        RemoveOfferRatingDtoRequest removeOfferRatingDtoRequest = new RemoveOfferRatingDtoRequest(voterToken, authorToken, voter, author);
        assertEquals(voterToken, removeOfferRatingDtoRequest.getVoterToken());
        assertEquals(authorToken, removeOfferRatingDtoRequest.getAuthorToken());
        dataBase.getTokensAndVoters().put(removeOfferRatingDtoRequest.getVoterToken(), voter);
        removeOfferRatingDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
        assertEquals(1, removeOfferRatingDtoRequest.getTokensAndVoters().size());

        String newVoterToken = GenerateTokenService.generateNewToken();
        removeOfferRatingDtoRequest.setVoterToken(newVoterToken);
        assertEquals(newVoterToken, removeOfferRatingDtoRequest.getVoterToken());

        String newAuthorToken = GenerateTokenService.generateNewToken();
        removeOfferRatingDtoRequest.setAuthorToken(newAuthorToken);
        assertEquals(newAuthorToken, removeOfferRatingDtoRequest.getAuthorToken());
    }

    @Test
    public void testValidateWrongVoterToken() {
        dataBase.getTokensAndVoters().clear();
        String voterToken =  GenerateTokenService.generateNewToken();
        String authorToken = GenerateTokenService.generateNewToken();

        Voter voter = new Voter("ivan", "ivanov", "123456789");
        Voter author = new Candidate("sergei", "sergeev", "12345678");
        try {
            RemoveOfferRatingDtoRequest removeOfferRatingDtoRequest = new RemoveOfferRatingDtoRequest("", authorToken, voter, author);
            dataBase.getTokensAndVoters().put(removeOfferRatingDtoRequest.getVoterToken(), voter);
            removeOfferRatingDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(1, removeOfferRatingDtoRequest.getTokensAndVoters().size());

            removeOfferRatingDtoRequest.validate();
            fail();
        } catch (VoterException ex) {
            assertEquals(VoterErrorCode.VOTER_WRONG_TOKEN, ex.getVoterErrorCode());
        }

        dataBase.getTokensAndVoters().clear();
        try {
            RemoveOfferRatingDtoRequest removeOfferRatingDtoRequest = new RemoveOfferRatingDtoRequest(null, authorToken, voter, author);
            dataBase.getTokensAndVoters().put(removeOfferRatingDtoRequest.getVoterToken(), voter);
            removeOfferRatingDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(1, removeOfferRatingDtoRequest.getTokensAndVoters().size());

            removeOfferRatingDtoRequest.validate();
            fail();
        }  catch (VoterException ex) {
            assertEquals(VoterErrorCode.VOTER_WRONG_TOKEN, ex.getVoterErrorCode());
        }

        dataBase.getTokensAndVoters().clear();
        try {
            RemoveOfferRatingDtoRequest removeOfferRatingDtoRequest = new RemoveOfferRatingDtoRequest(voterToken, authorToken, voter, author);
            dataBase.getTokensAndVoters().put(removeOfferRatingDtoRequest.getVoterToken(), voter);
            removeOfferRatingDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(1, removeOfferRatingDtoRequest.getTokensAndVoters().size());
            removeOfferRatingDtoRequest.setVoterToken("");

            removeOfferRatingDtoRequest.validate();
            fail();
        }  catch (VoterException ex) {
            assertEquals(VoterErrorCode.VOTER_WRONG_TOKEN, ex.getVoterErrorCode());
        }

        dataBase.getTokensAndVoters().clear();
        try {
            RemoveOfferRatingDtoRequest removeOfferRatingDtoRequest = new RemoveOfferRatingDtoRequest(voterToken, authorToken, voter, author);
            dataBase.getTokensAndVoters().put(removeOfferRatingDtoRequest.getVoterToken(), voter);
            removeOfferRatingDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(1, removeOfferRatingDtoRequest.getTokensAndVoters().size());
            removeOfferRatingDtoRequest.setVoterToken(null);

            removeOfferRatingDtoRequest.validate();
            fail();
        }  catch (VoterException ex) {
            assertEquals(VoterErrorCode.VOTER_WRONG_TOKEN, ex.getVoterErrorCode());
        }
    }

    @Test
    public void testValidateWrongAuthorToken() {
        dataBase.getTokensAndVoters().clear();
        String voterToken =  GenerateTokenService.generateNewToken();
        String authorToken = GenerateTokenService.generateNewToken();

        Voter voter = new Voter("ivan", "ivanov", "123456789");
        Voter author = new Candidate("sergei", "sergeev", "12345678");
        try {
            RemoveOfferRatingDtoRequest removeOfferRatingDtoRequest = new RemoveOfferRatingDtoRequest(voterToken, "", voter, author);
            dataBase.getTokensAndVoters().put(removeOfferRatingDtoRequest.getVoterToken(), voter);
            removeOfferRatingDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(1, removeOfferRatingDtoRequest.getTokensAndVoters().size());

            removeOfferRatingDtoRequest.validate();
            fail();
        } catch (VoterException ex) {
            assertEquals(VoterErrorCode.VOTER_WRONG_TOKEN, ex.getVoterErrorCode());
        }

        dataBase.getTokensAndVoters().clear();
        try {
            RemoveOfferRatingDtoRequest removeOfferRatingDtoRequest = new RemoveOfferRatingDtoRequest(voterToken, null, voter, author);
            dataBase.getTokensAndVoters().put(removeOfferRatingDtoRequest.getVoterToken(), voter);
            removeOfferRatingDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(1, removeOfferRatingDtoRequest.getTokensAndVoters().size());

            removeOfferRatingDtoRequest.validate();
            fail();
        }  catch (VoterException ex) {
            assertEquals(VoterErrorCode.VOTER_WRONG_TOKEN, ex.getVoterErrorCode());
        }

        dataBase.getTokensAndVoters().clear();
        try {
            RemoveOfferRatingDtoRequest removeOfferRatingDtoRequest = new RemoveOfferRatingDtoRequest(voterToken, authorToken, voter, author);
            dataBase.getTokensAndVoters().put(removeOfferRatingDtoRequest.getVoterToken(), voter);
            removeOfferRatingDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(1, removeOfferRatingDtoRequest.getTokensAndVoters().size());
            removeOfferRatingDtoRequest.setAuthorToken("");

            removeOfferRatingDtoRequest.validate();
            fail();
        }  catch (VoterException ex) {
            assertEquals(VoterErrorCode.VOTER_WRONG_TOKEN, ex.getVoterErrorCode());
        }

        dataBase.getTokensAndVoters().clear();
        try {
            RemoveOfferRatingDtoRequest removeOfferRatingDtoRequest = new RemoveOfferRatingDtoRequest(voterToken, authorToken, voter, author);
            dataBase.getTokensAndVoters().put(removeOfferRatingDtoRequest.getVoterToken(), voter);
            removeOfferRatingDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(1, removeOfferRatingDtoRequest.getTokensAndVoters().size());
            removeOfferRatingDtoRequest.setAuthorToken(null);

            removeOfferRatingDtoRequest.validate();
            fail();
        }  catch (VoterException ex) {
            assertEquals(VoterErrorCode.VOTER_WRONG_TOKEN, ex.getVoterErrorCode());
        }
    }

    @Test
    public void testValidateOfflineVoterToken() {
        dataBase.getTokensAndVoters().clear();
        String voterToken =  GenerateTokenService.generateNewToken();
        String authorToken = GenerateTokenService.generateNewToken();

        Voter voter = new Voter("ivan", "ivanov", "123456789");
        Voter author = new Candidate("sergei", "sergeev", "12345678");
        try {
            RemoveOfferRatingDtoRequest removeOfferRatingDtoRequest = new RemoveOfferRatingDtoRequest(voterToken, authorToken, voter, author);

            Map<String, Voter> tokensAndVoters = dataBase.getTokensAndVoters();
            removeOfferRatingDtoRequest.setTokensAndVoters(tokensAndVoters);
            assertEquals(0, removeOfferRatingDtoRequest.getTokensAndVoters().size());

            removeOfferRatingDtoRequest.validate();
            fail();
        } catch (VoterException ex) {
            assertEquals(VoterErrorCode.OFFLINE_TOKEN, ex.getVoterErrorCode());
        }
    }
}
