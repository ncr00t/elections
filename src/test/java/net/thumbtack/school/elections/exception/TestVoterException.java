package net.thumbtack.school.elections.exception;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class TestVoterException {

    @Test
    public void testVoterException1() {
        VoterException voterException = new VoterException(VoterErrorCode.VOTER_WRONG_FIRSTNAME);
        assertEquals(VoterErrorCode.VOTER_WRONG_FIRSTNAME, voterException.getVoterErrorCode());
    }

    @Test
    public void testVoterException2() {
        VoterException voterException = new VoterException(VoterErrorCode.VOTER_WRONG_LOGIN);
        assertEquals(VoterErrorCode.VOTER_WRONG_LOGIN, voterException.getVoterErrorCode());
    }

    @Test
    public void testVoterException3() {
        VoterException voterException = new VoterException(VoterErrorCode.VOTER_WRONG_PASSWORD);
        assertEquals(VoterErrorCode.VOTER_WRONG_PASSWORD, voterException.getVoterErrorCode());
    }

    @Test
    public void testVoterException4() {
        VoterException voterException = new VoterException(VoterErrorCode.VOTER_SHORT_PASSWORD);
        assertEquals(VoterErrorCode.VOTER_SHORT_PASSWORD, voterException.getVoterErrorCode());
    }

    @Test
    public void testVoterException5() {
        VoterException voterException = new VoterException(VoterErrorCode.DUPLICATE_VOTER_LOGIN);
        assertEquals(VoterErrorCode.DUPLICATE_VOTER_LOGIN, voterException.getVoterErrorCode());
    }

    @Test
    public void testVoterException6() {
        VoterException voterException = new VoterException(VoterErrorCode.VOTER_WRONG_TOKEN);
        assertEquals(VoterErrorCode.VOTER_WRONG_TOKEN, voterException.getVoterErrorCode());
    }

    @Test
    public void testVoterException7() {
        VoterException voterException = new VoterException(VoterErrorCode.ERROR_RESPONSE);
        assertEquals(VoterErrorCode.ERROR_RESPONSE, voterException.getVoterErrorCode());
    }

    @Test
    public void testVoterException8() {
        VoterException voterException = new VoterException(VoterErrorCode.OFFLINE_TOKEN);
        assertEquals(VoterErrorCode.OFFLINE_TOKEN, voterException.getVoterErrorCode());
    }

    @Test
    public void testVoterException9() {
        VoterException voterException = new VoterException(VoterErrorCode.VOTER_IS_CANDIDATE);
        assertEquals(VoterErrorCode.VOTER_IS_CANDIDATE, voterException.getVoterErrorCode());
    }

    @Test
    public void testVoterException10() {
        VoterException voterException = new VoterException(VoterErrorCode.TOKEN_NOT_FOUND_BY_LOGIN);
        assertEquals(VoterErrorCode.TOKEN_NOT_FOUND_BY_LOGIN, voterException.getVoterErrorCode());
    }

    @Test
    public void testVoterException11() {
        VoterException voterException = new VoterException(VoterErrorCode.VOTER_NOT_FOUND_BY_LOGIN);
        assertEquals(VoterErrorCode.VOTER_NOT_FOUND_BY_LOGIN, voterException.getVoterErrorCode());
    }

    @Test
    public void testVoterException12() {
        VoterException voterException = new VoterException(VoterErrorCode.PASSWORD_NOT_FOUND);
        assertEquals(VoterErrorCode.PASSWORD_NOT_FOUND, voterException.getVoterErrorCode());
    }

    @Test
    public void testVoterException13() {
        VoterException voterException = new VoterException(VoterErrorCode.VOTER_NOT_FOUND_BY_TOKEN);
        assertEquals(VoterErrorCode.VOTER_NOT_FOUND_BY_TOKEN, voterException.getVoterErrorCode());
    }

    @Test
    public void testVoterException14() {
        VoterException voterException = new VoterException(VoterErrorCode.CANDIDATE_NOT_FOUND_BY_TOKEN);
        assertEquals(VoterErrorCode.CANDIDATE_NOT_FOUND_BY_TOKEN, voterException.getVoterErrorCode());
    }

    @Test
    public void testVoterException15() {
        VoterException voterException = new VoterException(VoterErrorCode.OFFER_WRONG_DESCRIPTION);
        assertEquals(VoterErrorCode.OFFER_WRONG_DESCRIPTION, voterException.getVoterErrorCode());
    }

    @Test
    public void testVoterException16() {
        VoterException voterException = new VoterException(VoterErrorCode.ACTIVE_TOKEN);
        assertEquals(VoterErrorCode.ACTIVE_TOKEN, voterException.getVoterErrorCode());
    }

    @Test
    public void testVoterException17() {
        VoterException voterException = new VoterException(VoterErrorCode.CANDIDATE_WRONG_TOKEN);
        assertEquals(VoterErrorCode.CANDIDATE_WRONG_TOKEN, voterException.getVoterErrorCode());
    }

    @Test
    public void testVoterException18() {
        VoterException voterException = new VoterException(VoterErrorCode.CANDIDATE_CANNOT_REMOVE_YOURSELF_OFFER);
        assertEquals(VoterErrorCode.CANDIDATE_CANNOT_REMOVE_YOURSELF_OFFER, voterException.getVoterErrorCode());
    }

    @Test
    public void testVoterException19() {
        VoterException voterException = new VoterException(VoterErrorCode.OFFER_WRONG_RATING);
        assertEquals(VoterErrorCode.OFFER_WRONG_RATING, voterException.getVoterErrorCode());
    }

    @Test
    public void testVoterException20() {
        VoterException voterException = new VoterException(VoterErrorCode.VOTER_CANNOT_CHANGE_YOURSELF_RATING);
        assertEquals(VoterErrorCode.VOTER_CANNOT_CHANGE_YOURSELF_RATING, voterException.getVoterErrorCode());
    }

    @Test
    public void testVoterException21() {
        VoterException voterException = new VoterException(VoterErrorCode.VOTER_CANNOT_REMOVE_YOURSELF_RATING);
        assertEquals(VoterErrorCode.VOTER_CANNOT_REMOVE_YOURSELF_RATING, voterException.getVoterErrorCode());
    }

    @Test
    public void testVoterException22() {
        VoterException voterException = new VoterException(VoterErrorCode.VOTER_NOT_SET_RATING);
        assertEquals(VoterErrorCode.VOTER_NOT_SET_RATING, voterException.getVoterErrorCode());
    }

    @Test
    public void testVoterException23() {
        VoterException voterException = new VoterException(VoterErrorCode.OFFER_WITHOUT_RATINGS);
        assertEquals(VoterErrorCode.OFFER_WITHOUT_RATINGS, voterException.getVoterErrorCode());
    }

    @Test
    public void testVoterException24() {
        VoterException voterException = new VoterException(VoterErrorCode.ACTION_NOT_ALLOWED_WHEN_VOTING_STARTED);
        assertEquals(VoterErrorCode.ACTION_NOT_ALLOWED_WHEN_VOTING_STARTED, voterException.getVoterErrorCode());
    }

    @Test
    public void testVoterException25() {
        VoterException voterException = new VoterException(VoterErrorCode.CANDIDATE_CANNOT_VOTE_FOR_YOURSELF);
        assertEquals(VoterErrorCode.CANDIDATE_CANNOT_VOTE_FOR_YOURSELF, voterException.getVoterErrorCode());
    }

    @Test
    public void testVoterException26() {
        VoterException voterException = new VoterException(VoterErrorCode.CANDIDATE_WITHOUT_ELECTION_PROGRAM);
        assertEquals(VoterErrorCode.CANDIDATE_WITHOUT_ELECTION_PROGRAM, voterException.getVoterErrorCode());
    }

    @Test
    public void testVoterException27() {
        VoterException voterException = new VoterException(VoterErrorCode.CANDIDATE_CANNOT_VOTE_TWICE);
        assertEquals(VoterErrorCode.CANDIDATE_CANNOT_VOTE_TWICE, voterException.getVoterErrorCode());
    }

    @Test
    public void testVoterException28() {
        VoterException voterException = new VoterException(VoterErrorCode.VOTER_CANNOT_VOTE_TWICE);
        assertEquals(VoterErrorCode.VOTER_CANNOT_VOTE_TWICE, voterException.getVoterErrorCode());
    }

    @Test
    public void testVoterException29() {
        VoterException voterException = new VoterException(VoterErrorCode.ELECTIONS_DECLARED_INVALID);
        assertEquals(VoterErrorCode.ELECTIONS_DECLARED_INVALID, voterException.getVoterErrorCode());
    }

    @Test
    public void testVoterException30() {
        VoterException voterException = new VoterException(VoterErrorCode.VOTING_HAS_NOT_STARTED);
        assertEquals(VoterErrorCode.VOTING_HAS_NOT_STARTED, voterException.getVoterErrorCode());
    }

    @Test
    public void testVoterException31() {
        VoterException voterException = new VoterException(VoterErrorCode.SERVER_IS_NOT_RUNNING);
        assertEquals(VoterErrorCode.SERVER_IS_NOT_RUNNING, voterException.getVoterErrorCode());
    }

    @Test
    public void testVoterException32() {
        VoterException voterException = new VoterException(VoterErrorCode.CANDIDATE_NOT_FOUND_BY_ID);
        assertEquals(VoterErrorCode.CANDIDATE_NOT_FOUND_BY_ID, voterException.getVoterErrorCode());
    }

    @Test
    public void testVoterException33() {
        VoterException voterException = new VoterException(VoterErrorCode.VOTER_NOT_FOUND_BY_ID);
        assertEquals(VoterErrorCode.VOTER_NOT_FOUND_BY_ID, voterException.getVoterErrorCode());
    }
}


