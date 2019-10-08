package net.thumbtack.school.elections.exception;

public enum VoterErrorCode {

    VOTER_WRONG_FIRSTNAME("Firstname is null or empty"),
    VOTER_WRONG_LOGIN("Login is null or empty"),
    VOTER_WRONG_PASSWORD("Password is null or empty"),
    VOTER_SHORT_PASSWORD("Minimum password length of 8 symbols"),
    VOTER_WRONG_TOKEN("Voter token is null or empty"),
    VOTER_CANNOT_CHANGE_YOURSELF_RATING("Voter cannot change yourself rating"),
    VOTER_CANNOT_REMOVE_YOURSELF_RATING("Voter cannot remove yourself rating"),
    VOTER_CANNOT_VOTE_TWICE("Voter already voted and cannot vote twice"),
    VOTER_NOT_SET_RATING("Voter not setting rating for this offer"),
    VOTER_NOT_FOUND_BY_ID("Voter not found by id"),
    ACTION_NOT_ALLOWED_WHEN_VOTING_STARTED("This action is not allowed when voting started"),
    OFFER_WRONG_DESCRIPTION("Offer description is null or empty"),
    OFFER_WRONG_RATING("Rating out of range (1-5)"),
    OFFER_WITHOUT_RATINGS("Offer has no ratings"),
    OFFLINE_TOKEN("Need to restore registration"),
    ERROR_RESPONSE("error"),
    VOTER_IS_CANDIDATE("User is not removed from list candidates"),
    ACTIVE_TOKEN("Token is valid and is not in list of offline tokens"),
    VOTER_NOT_FOUND_BY_TOKEN("Voter not found by token"),
    TOKEN_NOT_FOUND_BY_LOGIN("Token not found by login"),
    VOTER_NOT_FOUND_BY_LOGIN("Voter with login is not registered"),
    CANDIDATE_NOT_FOUND_BY_TOKEN("Candidate not found"),
    CANDIDATE_NOT_FOUND_BY_ID("Candidate not found by id"),
    CANDIDATE_WRONG_TOKEN("Candidate token is null or empty"),
    CANDIDATE_CANNOT_REMOVE_YOURSELF_OFFER("Candidate cannot remove yourself offer"),
    CANDIDATE_CANNOT_VOTE_FOR_YOURSELF("Candidate cannot vote for yourself"),
    CANDIDATE_WITHOUT_ELECTION_PROGRAM( "Candidate does not participate without election program"),
    CANDIDATE_CANNOT_VOTE_TWICE("Candidate already voted and cannot vote twice"),
    PASSWORD_NOT_FOUND("Voter with password is not registered"),
    DUPLICATE_VOTER_LOGIN("Voter with same name already exist"),
    ELECTIONS_DECLARED_INVALID("Amount votes for candidate is less than or equal to amount votes against all"),
    VOTING_HAS_NOT_STARTED("Voting has not started yet"),
    SERVER_IS_NOT_RUNNING("Server is not running yet");

    private String errorString;

    private VoterErrorCode(String errorString){
        this.errorString = errorString;
    }

    public String getErrorString() {
        return errorString;
    }
}
