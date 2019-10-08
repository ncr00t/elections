package net.thumbtack.school.elections.request;

import net.thumbtack.school.elections.exception.VoterErrorCode;
import net.thumbtack.school.elections.exception.VoterException;

public class AddOfferDtoRequest extends AddOfferToElectionProgramDtoRequest {

    private String voterToken;
    private int voterId;

    public AddOfferDtoRequest(String voterToken, int voterId, String candidateToken,int candidateId, String offerDescription) {
        super(candidateToken, candidateId, offerDescription);
        this.voterId = voterId;
        this.voterToken = voterToken;
    }

    public String getVoterToken() {
        return voterToken;
    }

    public void setVoterToken(String voterToken) {
        this.voterToken = voterToken;
    }

    public int getVoterId() {
        return voterId;
    }

    public void setVoterId(int voterId) {
        this.voterId = voterId;
    }

    public boolean validate() throws VoterException {
        if(isEmpty(voterToken)) {
            throw new VoterException(VoterErrorCode.VOTER_WRONG_TOKEN);
        }
        if(isEmpty(getCandidateToken())) {
            throw new VoterException(VoterErrorCode.CANDIDATE_WRONG_TOKEN);
        }
        if(!getTokensAndVoters().containsKey(voterToken) || !getTokensAndVoters().containsKey(getCandidateToken())){
            throw new VoterException(VoterErrorCode.OFFLINE_TOKEN);
        }
        if(isEmpty(getOfferDescription())){
            throw new VoterException(VoterErrorCode.OFFER_WRONG_DESCRIPTION);
        }
        return true;
    }
}
