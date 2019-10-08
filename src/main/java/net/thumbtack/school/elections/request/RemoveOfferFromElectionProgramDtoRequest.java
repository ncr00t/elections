package net.thumbtack.school.elections.request;

import net.thumbtack.school.elections.exception.VoterException;

public class RemoveOfferFromElectionProgramDtoRequest extends AddOfferToElectionProgramDtoRequest {

    public RemoveOfferFromElectionProgramDtoRequest(String candidateToken, int candidateId, String offerDescription) {
        super(candidateToken,candidateId, offerDescription);
    }

    @Override
    public boolean validate() throws VoterException {
        return super.validate();
    }
}
