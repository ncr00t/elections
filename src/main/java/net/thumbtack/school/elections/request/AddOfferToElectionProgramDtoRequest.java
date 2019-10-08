package net.thumbtack.school.elections.request;

import net.thumbtack.school.elections.exception.VoterErrorCode;
import net.thumbtack.school.elections.exception.VoterException;
import net.thumbtack.school.elections.model.Voter;
import java.util.Map;

public class AddOfferToElectionProgramDtoRequest {

    private String candidateToken;
    private int candidateId;
    private String offerDescription;
    private Map<String, Voter> tokensAndVoters;

    public AddOfferToElectionProgramDtoRequest(String candidateToken, int candidateId, String offerDescription) {
        this.candidateToken = candidateToken;
        this.candidateId = candidateId;
        this.offerDescription = offerDescription;
    }

    public int getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(int candidateId) {
        this.candidateId = candidateId;
    }

    public String getCandidateToken() {
        return candidateToken;
    }

    public void setCandidateToken(String candidateToken) {
        this.candidateToken = candidateToken;
    }

    public String getOfferDescription() {
        return offerDescription;
    }

    public void setOfferDescription(String offerDescription) {
        this.offerDescription = offerDescription;
    }

    public Map<String, Voter> getTokensAndVoters() {
        return tokensAndVoters;
    }

    public void setTokensAndVoters(Map<String, Voter> tokensAndVoters) {
        this.tokensAndVoters = tokensAndVoters;
    }

    protected boolean isEmpty(String value){
        return value == null || value.equals("");
    }

    public boolean validate() throws VoterException {
        if(isEmpty(offerDescription)) {
            throw new VoterException(VoterErrorCode.OFFER_WRONG_DESCRIPTION);
        }
        if(isEmpty(candidateToken)){
            throw new VoterException(VoterErrorCode.CANDIDATE_WRONG_TOKEN);
        }
        if(!tokensAndVoters.containsKey(candidateToken)){
            throw new VoterException(VoterErrorCode.OFFLINE_TOKEN);
        }
        return true;
    }
}
