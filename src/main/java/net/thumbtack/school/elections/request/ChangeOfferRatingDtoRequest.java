package net.thumbtack.school.elections.request;

import net.thumbtack.school.elections.exception.VoterErrorCode;
import net.thumbtack.school.elections.exception.VoterException;
import net.thumbtack.school.elections.model.Voter;
import java.util.Map;

public class ChangeOfferRatingDtoRequest{

    private String voterToken;
    private String authorToken;
    private Voter voter;
    private Voter author;
    private int rating;
    private Map<String, Voter> tokensAndVoters;

    public ChangeOfferRatingDtoRequest(String voterToken, Voter voter, String authorToken, Voter author, int rating) {
        this.voterToken = voterToken;
        this.authorToken = authorToken;
        this.rating = rating;
        this.voter = voter;
        this.author = author;
    }

    public Voter getVoter() {
        return voter;
    }

    public void setVoter(Voter voter) {
        this.voter = voter;
    }

    public Voter getAuthor() {
        return author;
    }

    public void setAuthor(Voter author) {
        this.author = author;
    }

    public String getVoterToken() {
        return voterToken;
    }

    public void setVoterToken(String voterToken) {
        this.voterToken = voterToken;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getAuthorToken() {
        return authorToken;
    }

    public void setAuthorToken(String authorToken) {
        this.authorToken = authorToken;
    }

    public Map<String, Voter> getTokensAndVoters() {
        return tokensAndVoters;
    }

    public void setTokensAndVoters(Map<String, Voter> tokensAndVoters) {
        this.tokensAndVoters = tokensAndVoters;
    }

    private boolean isEmpty(String value){
        return value == null || value.equals("");
    }

    public boolean validate() throws VoterException {
        if(isEmpty(voterToken) || isEmpty(authorToken)){
            throw new VoterException(VoterErrorCode.VOTER_WRONG_TOKEN);
        }
        if(rating <= 0 || rating > 5){
            throw new VoterException(VoterErrorCode.OFFER_WRONG_RATING);
        }
        if(!tokensAndVoters.containsKey(voterToken)){
            throw new VoterException(VoterErrorCode.OFFLINE_TOKEN);
        }
        return true;
    }
}
