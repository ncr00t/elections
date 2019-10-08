package net.thumbtack.school.elections.request;

import net.thumbtack.school.elections.exception.VoterException;

public class GetOffersSortedByAverageRatingsDtoRequest extends GetOffersAndAverageRatingsDtoRequest {

    public GetOffersSortedByAverageRatingsDtoRequest(String token) {
        super(token);
    }

    @Override
    public boolean validate() throws VoterException {
        return super.validate();
    }
}
