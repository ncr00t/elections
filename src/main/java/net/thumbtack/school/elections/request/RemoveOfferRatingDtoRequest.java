package net.thumbtack.school.elections.request;

import net.thumbtack.school.elections.exception.VoterErrorCode;
import net.thumbtack.school.elections.exception.VoterException;
import net.thumbtack.school.elections.model.Voter;
import java.util.Map;

public class RemoveOfferRatingDtoRequest {

    private String voterToken;
    private String authorToken;
    private Voter voter;
    private Voter author;
    private Map<String, Voter> tokensAndVoters;

    public RemoveOfferRatingDtoRequest(String voterToken, String authorToken, Voter voter, Voter author) {
        this.voterToken = voterToken;
        this.authorToken = authorToken;
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
        if(!tokensAndVoters.containsKey(voterToken)){
            throw new VoterException(VoterErrorCode.OFFLINE_TOKEN);
        }
        return true;
    }
}
