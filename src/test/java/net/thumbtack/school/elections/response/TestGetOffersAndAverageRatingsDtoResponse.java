package net.thumbtack.school.elections.response;

import net.thumbtack.school.elections.database.DataBase;
import net.thumbtack.school.elections.exception.VoterException;
import net.thumbtack.school.elections.model.Offer;
import org.junit.Before;
import org.junit.Test;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class TestGetOffersAndAverageRatingsDtoResponse {

    private DataBase dataBase;

    @Before
    public void setUp(){
        dataBase = DataBase.getDataBase();
        dataBase.getVoters().clear();
    }

    @Test
    public void testGetOffersAndAverageRatingsDtoResponse() throws VoterException {
        GetOffersAndAverageRatingsDtoResponse offersSortedByAverageRatingsDtoResponse = new GetOffersAndAverageRatingsDtoResponse();
        Map<Offer, Integer> offersSortedByAverageRatings = dataBase.getOffersAndAverageRatings();
        offersSortedByAverageRatingsDtoResponse.setOffersSortedByAverageRatings(offersSortedByAverageRatings);
        assertEquals(0, offersSortedByAverageRatingsDtoResponse.getOffersSortedByAverageRatings().size());
    }
}
