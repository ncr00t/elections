package net.thumbtack.school.elections.response;

import net.thumbtack.school.elections.model.Offer;
import java.util.Map;

public class GetOffersAndAverageRatingsDtoResponse {

    private Map<Offer, Integer> OffersSortedByAverageRatings;

    public GetOffersAndAverageRatingsDtoResponse(Map<Offer, Integer> OffersSortedByAverageRatings) {
        this.OffersSortedByAverageRatings = OffersSortedByAverageRatings;
    }

    public GetOffersAndAverageRatingsDtoResponse() {

    }

    public Map<Offer, Integer> getOffersSortedByAverageRatings() {
        return OffersSortedByAverageRatings;
    }

    public void setOffersSortedByAverageRatings(Map<Offer, Integer> offersSortedByAverageRatings) {
        OffersSortedByAverageRatings = offersSortedByAverageRatings;
    }
}
