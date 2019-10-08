package net.thumbtack.school.elections.response;

import net.thumbtack.school.elections.model.Offer;

import java.util.Set;

public class GetOffersSortedByAverageRatingsDtoResponse {

    private Set<Offer> offersSortedByAverageRatings;

    public GetOffersSortedByAverageRatingsDtoResponse(Set<Offer> offersSortedByAverageRatings) {
        this.offersSortedByAverageRatings = offersSortedByAverageRatings;
    }

    public GetOffersSortedByAverageRatingsDtoResponse() {

    }

    public Set<Offer> getOffersSortedByAverageRatings() {
        return offersSortedByAverageRatings;
    }

    public void setOffersSortedByAverageRatings(Set<Offer> offersSortedByAverageRatings) {
        this.offersSortedByAverageRatings = offersSortedByAverageRatings;
    }
}
