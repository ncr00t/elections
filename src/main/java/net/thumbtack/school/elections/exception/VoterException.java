package net.thumbtack.school.elections.exception;

public class VoterException extends Exception {

    private VoterErrorCode voterErrorCode;

    public VoterException(VoterErrorCode voterErrorCode) {
        this.voterErrorCode = voterErrorCode;
    }

    public VoterErrorCode getVoterErrorCode() {
        return voterErrorCode;
    }
}
