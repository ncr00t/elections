package net.thumbtack.school.elections.model;

import net.thumbtack.school.elections.exception.VoterErrorCode;
import net.thumbtack.school.elections.exception.VoterException;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestOffer {

    @Test
    public void testOffer() throws VoterException {
        Voter voter = new Voter("petr","petrov","123456789");
        Offer offer = new Offer("build a bridge across the river");
        offer.setAuthorName(voter.getLogin());
        offer.addVoterAndRating(voter, 1 );

        offer.setAuthorName("ivanov");
        assertEquals("ivanov", offer.getAuthorName());

        offer.setDescription("repair the road");
        assertEquals("repair the road", offer.getDescription());
    }

    @Test
    public void testEqualsOffer() throws VoterException {
        Voter voter1 = new Voter("ivan", "ivanov", "12345678");
        Voter voter2 = new Voter("ivan", "ivanov", "12345678");
        Voter voter3 = new Voter("petr", "petrov", "123456789");

        Offer offer1 = new Offer("build a bridge across the river");
        offer1.setAuthorName(voter1.getLogin());
        offer1.addVoterAndRating(voter1, 1 );

        Offer offer2 = new Offer("build a bridge across the river");
        offer2.setAuthorName(voter2.getLogin());
        offer2.addVoterAndRating(voter2, 1 );

        Offer offer3 = new Offer("repir the road");
        offer3.setAuthorName(voter3.getLogin());
        offer3.addVoterAndRating(voter3, 2 );

        assertEquals(offer1, offer2);
        assertNotEquals(offer1, offer3);
    }

    @Test
    public void testGetRatingByVoter() throws VoterException {
        Voter voter1 = new Voter("ivan", "ivanov", "12345678");
        Offer offer1 = new Offer("build a bridge across the river");
        offer1.setAuthorName(voter1.getLogin());
        offer1.addVoterAndRating(voter1, 1 );
        assertEquals(1 , offer1.getRatingByVoter(voter1));
    }

    @Test
    public void testGetRatingByVoterIfVoterNotSetRating() {
        try {
            Voter voter1 = new Voter("ivan", "ivanov", "12345678");
            Offer offer1 = new Offer("build a bridge across the river");
            offer1.setAuthorName(voter1.getLogin());
            assertEquals(1, offer1.getRatingByVoter(voter1));
            fail();
        }catch (VoterException ve){
            assertEquals(VoterErrorCode.VOTER_NOT_SET_RATING, ve.getVoterErrorCode());
        }
    }

    @Test
    public void testRemoveOfferRating() throws VoterException {
        Voter voter = new Voter("petr","petrov","123456789");
        Offer offer = new Offer("build a bridge across the river");
        offer.setAuthorName(voter.getLogin());
        offer.addVoterAndRating(voter, 4 );
        assertEquals(4, offer.getRatingByVoter(voter));
        assertEquals(1, offer.getVotersAndRatings().size());
        offer.removeOfferRating(voter);
        assertEquals(0, offer.getVotersAndRatings().size());
    }

    @Test
    public void testRemoveOfferRatingIfVoterNotSetRating() {
        try {
            Voter voter = new Voter("petr", "petrov", "123456789");
            Offer offer = new Offer("build a bridge across the river");
            offer.setAuthorName(voter.getLogin());
            assertEquals(0, offer.getVotersAndRatings().size());
            offer.removeOfferRating(voter);
        }catch (VoterException ve){
            assertEquals(VoterErrorCode.VOTER_NOT_SET_RATING, ve.getVoterErrorCode());
        }
    }

    @Test
    public void testAddVoterAndRating() throws VoterException {
        Voter voter = new Voter("petr","petrov","123456789");
        Offer offer = new Offer("build a bridge across the river");
        offer.setAuthorName(voter.getLogin());
        offer.addVoterAndRating(voter, 1 );
        assertEquals(1, offer.getRatingByVoter(voter));
    }

    @Test
    public void testAddVoterAndRatingWrongRating() {
        try {
            Voter voter = new Voter("petr","petrov","123456789");
            Offer offer = new Offer("build a bridge across the river");
            offer.setAuthorName(voter.getLogin());
            offer.addVoterAndRating(voter, 0 );
            fail();
        } catch (VoterException ve) {
            assertEquals(VoterErrorCode.OFFER_WRONG_RATING, ve.getVoterErrorCode());
        }

        try {
            Voter voter = new Voter("petr","petrov","123456789");
            Offer offer = new Offer("build a bridge across the river");
            offer.setAuthorName(voter.getLogin());
            offer.addVoterAndRating(voter, 6 );
            fail();
        } catch (VoterException ve) {
            assertEquals(VoterErrorCode.OFFER_WRONG_RATING, ve.getVoterErrorCode());
        }

        try {
            Voter voter = new Voter("petr","petrov","123456789");
            Offer offer = new Offer("build a bridge across the river");
            offer.setAuthorName(voter.getLogin());
            offer.addVoterAndRating(voter, -1 );
            fail();
        } catch (VoterException ve) {
            assertEquals(VoterErrorCode.OFFER_WRONG_RATING, ve.getVoterErrorCode());
        }
    }
}