package net.thumbtack.school.elections.request;

import net.thumbtack.school.elections.database.DataBase;
import net.thumbtack.school.elections.exception.VoterErrorCode;
import net.thumbtack.school.elections.exception.VoterException;
import net.thumbtack.school.elections.model.Voter;
import net.thumbtack.school.elections.response.RegisterVoterDtoResponse;
import org.junit.BeforeClass;
import org.junit.Test;
import java.util.Map;

import static org.junit.Assert.*;
import static org.junit.Assert.fail;

public class TestChangeOfferRatingDtoRequest {

    private static DataBase dataBase;

    @BeforeClass
    public static void setUpClass(){
        dataBase = DataBase.getDataBase();
    }
//
//    @Test
//    public void testChangeOfferRatingDtoRequest() {
//        String voterToken =  new RegisterVoterDtoResponse().getToken();
//        String authorToken =  new RegisterVoterDtoResponse().getToken();
//        ChangeOfferRatingDtoRequest changeOfferRatingDtoRequest = new ChangeOfferRatingDtoRequest(voterToken, 0, authorToken, 0, 3);
//        assertEquals(voterToken, changeOfferRatingDtoRequest.getVoterToken());
//        assertEquals(authorToken, changeOfferRatingDtoRequest.getAuthorToken());
//        assertEquals(3, changeOfferRatingDtoRequest.getRating());
//
//        changeOfferRatingDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
//        assertEquals(0, changeOfferRatingDtoRequest.getTokensAndVoters().size());
//
//        String newVoterToken = new RegisterVoterDtoResponse().getToken();
//        changeOfferRatingDtoRequest.setVoterToken(newVoterToken);
//        assertEquals(newVoterToken, changeOfferRatingDtoRequest.getVoterToken());
//
//        String newAuthorToken = new RegisterVoterDtoResponse().getToken();
//        changeOfferRatingDtoRequest.setAuthorToken(newAuthorToken);
//        assertEquals(newAuthorToken, changeOfferRatingDtoRequest.getAuthorToken());
//
//        changeOfferRatingDtoRequest.setRating(5);
//        assertEquals(5, changeOfferRatingDtoRequest.getRating());
//    }
//
//    @Test
//    public void testValidateWrongVoterToken() {
//        dataBase.getTokensAndVoters().clear();
//        String voterToken =  new RegisterVoterDtoResponse().getToken();
//        String authorToken =  new RegisterVoterDtoResponse().getToken();
//        try {
//            ChangeOfferRatingDtoRequest changeOfferRatingDtoRequest = new ChangeOfferRatingDtoRequest( "", 0, authorToken, 0,3);
//            changeOfferRatingDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
//            assertEquals(0, changeOfferRatingDtoRequest.getTokensAndVoters().size());
//
//            changeOfferRatingDtoRequest.validate();
//            fail();
//        } catch (VoterException ex) {
//            assertEquals(VoterErrorCode.VOTER_WRONG_TOKEN, ex.getVoterErrorCode());
//        }
//
//        try {
//            ChangeOfferRatingDtoRequest changeOfferRatingDtoRequest = new ChangeOfferRatingDtoRequest(null, 0, authorToken, 0, 3);
//            changeOfferRatingDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
//            assertEquals(0, changeOfferRatingDtoRequest.getTokensAndVoters().size());
//
//            changeOfferRatingDtoRequest.validate();
//            fail();
//        }  catch (VoterException ex) {
//            assertEquals(VoterErrorCode.VOTER_WRONG_TOKEN, ex.getVoterErrorCode());
//        }
//
//        try {
//            ChangeOfferRatingDtoRequest changeOfferRatingDtoRequest = new ChangeOfferRatingDtoRequest(voterToken, 0, authorToken, 0, 3);
//            changeOfferRatingDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
//            assertEquals(0, changeOfferRatingDtoRequest.getTokensAndVoters().size());
//            changeOfferRatingDtoRequest.setVoterToken("");
//
//            changeOfferRatingDtoRequest.validate();
//            fail();
//        }  catch (VoterException ex) {
//            assertEquals(VoterErrorCode.VOTER_WRONG_TOKEN, ex.getVoterErrorCode());
//        }
//
//        try {
//            ChangeOfferRatingDtoRequest changeOfferRatingDtoRequest = new ChangeOfferRatingDtoRequest(voterToken, 0, authorToken, 0, 3);
//            changeOfferRatingDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
//            assertEquals(0, changeOfferRatingDtoRequest.getTokensAndVoters().size());
//            changeOfferRatingDtoRequest.setVoterToken(null);
//
//            changeOfferRatingDtoRequest.validate();
//            fail();
//        }  catch (VoterException ex) {
//            assertEquals(VoterErrorCode.VOTER_WRONG_TOKEN, ex.getVoterErrorCode());
//        }
//    }
//
//    @Test
//    public void testValidateWrongAuthorToken() {
//        dataBase.getTokensAndVoters().clear();
//        String voterToken =  new RegisterVoterDtoResponse().getToken();
//        String authorToken =  new RegisterVoterDtoResponse().getToken();
//        try {
//            ChangeOfferRatingDtoRequest changeOfferRatingDtoRequest = new ChangeOfferRatingDtoRequest(voterToken, 0, "", 0, 3);
//            changeOfferRatingDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
//            assertEquals(0, changeOfferRatingDtoRequest.getTokensAndVoters().size());
//
//            changeOfferRatingDtoRequest.validate();
//            fail();
//        } catch (VoterException ex) {
//            assertEquals(VoterErrorCode.VOTER_WRONG_TOKEN, ex.getVoterErrorCode());
//        }
//
//        try {
//            ChangeOfferRatingDtoRequest changeOfferRatingDtoRequest = new ChangeOfferRatingDtoRequest(voterToken, 0, null, 0, 3);
//            changeOfferRatingDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
//            assertEquals(0, changeOfferRatingDtoRequest.getTokensAndVoters().size());
//
//            changeOfferRatingDtoRequest.validate();
//            fail();
//        }  catch (VoterException ex) {
//            assertEquals(VoterErrorCode.VOTER_WRONG_TOKEN, ex.getVoterErrorCode());
//        }
//
//        try {
//            ChangeOfferRatingDtoRequest changeOfferRatingDtoRequest = new ChangeOfferRatingDtoRequest(voterToken, 0, authorToken, 0, 3);
//            changeOfferRatingDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
//            assertEquals(0, changeOfferRatingDtoRequest.getTokensAndVoters().size());
//            changeOfferRatingDtoRequest.setAuthorToken("");
//
//            changeOfferRatingDtoRequest.validate();
//            fail();
//        }  catch (VoterException ex) {
//            assertEquals(VoterErrorCode.VOTER_WRONG_TOKEN, ex.getVoterErrorCode());
//        }
//
//        try {
//            ChangeOfferRatingDtoRequest changeOfferRatingDtoRequest = new ChangeOfferRatingDtoRequest(voterToken, 0, authorToken, 0, 3);
//            changeOfferRatingDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
//            assertEquals(0, changeOfferRatingDtoRequest.getTokensAndVoters().size());
//            changeOfferRatingDtoRequest.setAuthorToken(null);
//
//            changeOfferRatingDtoRequest.validate();
//            fail();
//        }  catch (VoterException ex) {
//            assertEquals(VoterErrorCode.VOTER_WRONG_TOKEN, ex.getVoterErrorCode());
//        }
//    }
//
//    @Test
//    public void testValidateWrongRating() {
//        dataBase.getTokensAndVoters().clear();
//        String voterToken =  new RegisterVoterDtoResponse().getToken();
//        String authorToken =  new RegisterVoterDtoResponse().getToken();
//        try {
//            ChangeOfferRatingDtoRequest changeOfferRatingDtoRequest = new ChangeOfferRatingDtoRequest(voterToken, 0, authorToken, 0, 0);
//            changeOfferRatingDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
//            assertEquals(0, changeOfferRatingDtoRequest.getTokensAndVoters().size());
//
//            changeOfferRatingDtoRequest.validate();
//            fail();
//        } catch (VoterException ex) {
//            assertEquals(VoterErrorCode.OFFER_WRONG_RATING, ex.getVoterErrorCode());
//        }
//
//        try {
//            ChangeOfferRatingDtoRequest changeOfferRatingDtoRequest = new ChangeOfferRatingDtoRequest(voterToken, 0, authorToken, 0,6);
//            changeOfferRatingDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
//            assertEquals(0, changeOfferRatingDtoRequest.getTokensAndVoters().size());
//
//            changeOfferRatingDtoRequest.validate();
//            fail();
//        }  catch (VoterException ex) {
//            assertEquals(VoterErrorCode.OFFER_WRONG_RATING, ex.getVoterErrorCode());
//        }
//
//        try {
//            ChangeOfferRatingDtoRequest changeOfferRatingDtoRequest = new ChangeOfferRatingDtoRequest(voterToken, 0, authorToken, 0,3);
//            changeOfferRatingDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
//            assertEquals(0, changeOfferRatingDtoRequest.getTokensAndVoters().size());
//            changeOfferRatingDtoRequest.setRating(0);
//
//            changeOfferRatingDtoRequest.validate();
//            fail();
//        }  catch (VoterException ex) {
//            assertEquals(VoterErrorCode.OFFER_WRONG_RATING, ex.getVoterErrorCode());
//        }
//
//        try {
//            ChangeOfferRatingDtoRequest changeOfferRatingDtoRequest = new ChangeOfferRatingDtoRequest(voterToken, 0, authorToken, 0,3);
//            changeOfferRatingDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
//            assertEquals(0, changeOfferRatingDtoRequest.getTokensAndVoters().size());
//            changeOfferRatingDtoRequest.setRating(6);
//
//            changeOfferRatingDtoRequest.validate();
//            fail();
//        }  catch (VoterException ex) {
//            assertEquals(VoterErrorCode.OFFER_WRONG_RATING, ex.getVoterErrorCode());
//        }
//    }
//
//    @Test
//    public void testValidateOfflineVoterToken() {
//        String voterToken =  new RegisterVoterDtoResponse().getToken();
//        String authorToken =  new RegisterVoterDtoResponse().getToken();
//        try {
//            ChangeOfferRatingDtoRequest changeOfferRatingDtoRequest = new ChangeOfferRatingDtoRequest(voterToken, 0, authorToken, 0,3);
//
//            Map<String, Voter> tokensAndVoters = dataBase.getTokensAndVoters();
//            tokensAndVoters.put(voterToken, new Voter("ivan", "ivanov", "123456789"));
//            changeOfferRatingDtoRequest.setTokensAndVoters(tokensAndVoters);
//            assertTrue(changeOfferRatingDtoRequest.getTokensAndVoters().containsKey(voterToken));
//
//            changeOfferRatingDtoRequest.validate();
//            fail();
//        } catch (VoterException ex) {
//            assertEquals(VoterErrorCode.OFFLINE_TOKEN, ex.getVoterErrorCode());
//        }
//    }
}
