package net.thumbtack.school.elections.exception;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class TestVoterErrorCode {

    @Test
    public void testVoterErrorCode1() {
        assertEquals("Firstname is null or empty",  VoterErrorCode.VOTER_WRONG_FIRSTNAME.getErrorString());
    }

    @Test
    public void testVoterErrorCode2() {
        assertEquals("Login is null or empty",  VoterErrorCode.VOTER_WRONG_LOGIN.getErrorString());
    }

    @Test
    public void testVoterErrorCode3() {
        assertEquals("Password is null or empty",  VoterErrorCode.VOTER_WRONG_PASSWORD.getErrorString());
    }

    @Test
    public void testVoterErrorCode4() {
        assertEquals("Minimum password length of 8 symbols",  VoterErrorCode.VOTER_SHORT_PASSWORD.getErrorString());
    }

    @Test
    public void testVoterErrorCode5() {
        assertEquals("Voter with same name already exist",  VoterErrorCode.DUPLICATE_VOTER_LOGIN.getErrorString());
    }

    @Test
    public void testVoterErrorCode6() {
        assertEquals("Voter token is null or empty",  VoterErrorCode.VOTER_WRONG_TOKEN.getErrorString());
    }

    @Test
    public void testVoterErrorCode7() {
        assertEquals("error",  VoterErrorCode.ERROR_RESPONSE.getErrorString());
    }

    @Test
    public void testVoterErrorCode8() {
        assertEquals("Need to restore registration",  VoterErrorCode.OFFLINE_TOKEN.getErrorString());
    }

    @Test
    public void testVoterErrorCode9() {
        assertEquals("User is not removed from list candidates",  VoterErrorCode.VOTER_IS_CANDIDATE.getErrorString());
    }

    @Test
    public void testVoterErrorCode10() {
        assertEquals("Token not found by login",  VoterErrorCode.TOKEN_NOT_FOUND_BY_LOGIN.getErrorString());
    }

    @Test
    public void testVoterErrorCode11() {
        assertEquals("Voter with login is not registered",  VoterErrorCode.VOTER_NOT_FOUND_BY_LOGIN.getErrorString());
    }

    @Test
    public void testVoterErrorCode12() {
        assertEquals("Voter with password is not registered",  VoterErrorCode.PASSWORD_NOT_FOUND.getErrorString());
    }

    @Test
    public void testVoterErrorCode13() {
        assertEquals("Voter not found by token",  VoterErrorCode.VOTER_NOT_FOUND_BY_TOKEN.getErrorString());
    }

    @Test
    public void testVoterErrorCode14() {
        assertEquals("Candidate not found",  VoterErrorCode.CANDIDATE_NOT_FOUND_BY_TOKEN.getErrorString());
    }

    @Test
    public void testVoterErrorCode15() {
        assertEquals("Offer description is null or empty",  VoterErrorCode.OFFER_WRONG_DESCRIPTION.getErrorString());
    }

    @Test
    public void testVoterErrorCode16() {
        assertEquals("Token is valid and is not in list of offline tokens",  VoterErrorCode.ACTIVE_TOKEN.getErrorString());
    }

    @Test
    public void testVoterErrorCode17() {
        assertEquals("Candidate token is null or empty",  VoterErrorCode.CANDIDATE_WRONG_TOKEN.getErrorString());
    }

    @Test
    public void testVoterErrorCode18() {
        assertEquals("Candidate cannot remove yourself offer", VoterErrorCode.CANDIDATE_CANNOT_REMOVE_YOURSELF_OFFER.getErrorString());
    }

    @Test
    public void testVoterErrorCode19() {
        assertEquals("Rating out of range (1-5)", VoterErrorCode.OFFER_WRONG_RATING.getErrorString());
    }

    @Test
    public void testVoterErrorCode20() {
        assertEquals("Voter cannot change yourself rating", VoterErrorCode.VOTER_CANNOT_CHANGE_YOURSELF_RATING.getErrorString());
    }

    @Test
    public void testVoterErrorCode21() {
        assertEquals("Voter cannot remove yourself rating", VoterErrorCode.VOTER_CANNOT_REMOVE_YOURSELF_RATING.getErrorString());
    }

    @Test
    public void testVoterErrorCode22() {
        assertEquals("Voter not setting rating for this offer", VoterErrorCode.VOTER_NOT_SET_RATING.getErrorString());
    }

    @Test
    public void testVoterErrorCode23() {
        assertEquals("Offer has no ratings", VoterErrorCode.OFFER_WITHOUT_RATINGS.getErrorString());
    }

    @Test
    public void testVoterErrorCode24() {
        assertEquals("This action is not allowed when voting started", VoterErrorCode. ACTION_NOT_ALLOWED_WHEN_VOTING_STARTED.getErrorString());
    }

    @Test
    public void testVoterErrorCode25() {
        assertEquals("Candidate cannot vote for yourself", VoterErrorCode.CANDIDATE_CANNOT_VOTE_FOR_YOURSELF.getErrorString());
    }

    @Test
    public void testVoterErrorCode26() {
        assertEquals("Candidate does not participate without election program", VoterErrorCode.CANDIDATE_WITHOUT_ELECTION_PROGRAM.getErrorString());
    }

    @Test
    public void testVoterErrorCode27() {
        assertEquals("Voter already voted and cannot vote twice", VoterErrorCode.VOTER_CANNOT_VOTE_TWICE.getErrorString());
    }

    @Test
    public void testVoterErrorCode28() {
        assertEquals("Candidate already voted and cannot vote twice", VoterErrorCode.CANDIDATE_CANNOT_VOTE_TWICE.getErrorString());
    }

    @Test
    public void testVoterErrorCode29() {
        assertEquals("Amount votes for candidate is less than or equal to amount votes against all", VoterErrorCode.ELECTIONS_DECLARED_INVALID.getErrorString());
    }

    @Test
    public void testVoterErrorCode30() {
        assertEquals("Voting has not started yet", VoterErrorCode.VOTING_HAS_NOT_STARTED.getErrorString());
    }

    @Test
    public void testVoterErrorCode31() {
        assertEquals("Server is not running yet", VoterErrorCode.SERVER_IS_NOT_RUNNING.getErrorString());
    }

    @Test
    public void testVoterErrorCode32() {
        assertEquals("Candidate not found by id", VoterErrorCode.CANDIDATE_NOT_FOUND_BY_ID.getErrorString());
    }

    @Test
    public void testVoterErrorCode33() {
        assertEquals("Voter not found by id", VoterErrorCode.VOTER_NOT_FOUND_BY_ID.getErrorString());
    }
}
