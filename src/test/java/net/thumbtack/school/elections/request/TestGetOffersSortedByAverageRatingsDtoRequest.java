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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TestGetOffersSortedByAverageRatingsDtoRequest {

    private static DataBase dataBase;

    @BeforeClass
    public static void setUpClass(){
        dataBase = DataBase.getDataBase();
    }

    @Test
    public void testGetOffersSortedByAverageRatingsDtoRequest() {
        String token = GenerateTokenService.generateNewToken();
        GetOffersSortedByAverageRatingsDtoRequest offersSortedByAverageRatingsDtoRequest = new GetOffersSortedByAverageRatingsDtoRequest(token);
        assertEquals(token, offersSortedByAverageRatingsDtoRequest.getToken());

        String newToken = GenerateTokenService.generateNewToken();

        offersSortedByAverageRatingsDtoRequest.setToken(newToken);
        assertEquals(newToken, offersSortedByAverageRatingsDtoRequest.getToken());
    }

    @Test
    public void testValidateWrongToken() {
        dataBase.getTokensAndVoters().clear();
        try {
            GetOffersSortedByAverageRatingsDtoRequest offersSortedByAverageRatingsDtoRequest = new GetOffersSortedByAverageRatingsDtoRequest(null);
            assertEquals(null, offersSortedByAverageRatingsDtoRequest.getToken());
            dataBase.getTokensAndVoters().put(offersSortedByAverageRatingsDtoRequest.getToken(), new Voter("ivan", "ivanov", "123456789"));
            offersSortedByAverageRatingsDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(1, offersSortedByAverageRatingsDtoRequest.getTokensAndVoters().size());

            offersSortedByAverageRatingsDtoRequest.validate();
            fail();
        } catch (VoterException ex) {
            assertEquals(VoterErrorCode.VOTER_WRONG_TOKEN, ex.getVoterErrorCode());
        }

        dataBase.getTokensAndVoters().clear();
        try {
            GetOffersSortedByAverageRatingsDtoRequest offersSortedByAverageRatingsDtoRequest = new GetOffersSortedByAverageRatingsDtoRequest("");
            assertEquals("", offersSortedByAverageRatingsDtoRequest.getToken());
            dataBase.getTokensAndVoters().put(offersSortedByAverageRatingsDtoRequest.getToken(), new Voter("ivan", "ivanov", "123456789"));
            offersSortedByAverageRatingsDtoRequest.setTokensAndVoters(dataBase.getTokensAndVoters());
            assertEquals(1, offersSortedByAverageRatingsDtoRequest.getTokensAndVoters().size());

            offersSortedByAverageRatingsDtoRequest.validate();
            fail();
        }  catch (VoterException ex) {
            assertEquals(VoterErrorCode.VOTER_WRONG_TOKEN, ex.getVoterErrorCode());
        }
    }

    @Test
    public void testValidateOfflineToken() {
        dataBase.getTokensAndVoters().clear();
        try {
            String token1 = GenerateTokenService.generateNewToken();

            GetOffersSortedByAverageRatingsDtoRequest offersSortedByAverageRatingsDtoRequest = new GetOffersSortedByAverageRatingsDtoRequest(token1);
            assertEquals(token1, offersSortedByAverageRatingsDtoRequest.getToken());

            Map<String, Voter> tokensAndVoters = dataBase.getTokensAndVoters();
            offersSortedByAverageRatingsDtoRequest.setTokensAndVoters(tokensAndVoters);
            assertEquals(0, offersSortedByAverageRatingsDtoRequest.getTokensAndVoters().size());

            offersSortedByAverageRatingsDtoRequest.validate();
            fail();
        } catch (VoterException ex) {
            assertEquals(VoterErrorCode.OFFLINE_TOKEN, ex.getVoterErrorCode());
        }
    }
}
