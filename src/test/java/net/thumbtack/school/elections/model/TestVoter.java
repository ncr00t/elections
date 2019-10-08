package net.thumbtack.school.elections.model;

import net.thumbtack.school.elections.exception.VoterErrorCode;
import net.thumbtack.school.elections.exception.VoterException;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestVoter {

    @Test
    public void testVoter() {
        Voter voter = new Voter("ivan", "ivanov", "12345678");
        assertEquals("ivan", voter.getFirstName());
        assertEquals("ivanov", voter.getLogin());
        assertEquals("12345678", voter.getPassword());
        voter.setFirstName("petr");
        assertEquals("petr", voter.getFirstName());
        voter.setLogin("petrov");
        assertEquals("petrov", voter.getLogin());
        voter.setPassword("123456789");
        assertEquals("123456789", voter.getPassword());
    }

    @Test
    public void testEqualsVoter() {
        Voter voter1 = new Voter("ivan", "ivanov", "12345678");
        Voter voter2 = new Voter("ivan", "ivanov", "12345678");
        Voter voter3 = new Voter("petr", "petrov", "123456789");
        assertEquals(voter1, voter2);
        assertNotEquals(voter1, voter3);
    }

    @Test
    public void testAddOffer() throws VoterException {
        Voter voter1 = new Voter("petr","petrov","123456789");

        Offer offer = new Offer("build a bridge across the river");
        offer.setAuthorName(voter1.getLogin());
        offer.addVoterAndRating(voter1, 4 );
        assertEquals(4, offer.getRatingByVoter(voter1));
        assertEquals(0, voter1.getOffers().size());

        voter1.addOffer(offer);
        assertEquals(1, voter1.getOffers().size());
        assertTrue(voter1.getOffers().contains(offer));
    }

    @Test
    public void testChangeOfferRating() throws VoterException {
        Voter voter1 = new Voter("petr","petrov","123456789");

        Offer offer = new Offer("build a bridge across the river");
        offer.setAuthorName(voter1.getLogin());
        offer.addVoterAndRating(voter1, 4 );
        assertEquals(4, offer.getRatingByVoter(voter1));
        assertEquals(1, offer.getVotersAndRatings().size());

        voter1.addOffer(offer);
        assertEquals(1, voter1.getOffers().size());
        assertTrue(voter1.getOffers().contains(offer));

        Voter voter2 = new Voter("fedor","fedorov","123456789");

        Offer offerWithChangedRating = voter2.changeOfferRating(voter1, 2);
        assertEquals(2, offerWithChangedRating.getRatingByVoter(voter1));
        assertTrue(voter1.isContainsOffer(offerWithChangedRating));
    }

    @Test
    public void testRemoveOfferRating() throws VoterException {
        Voter voter1 = new Voter("petr","petrov","123456789");

        Offer offer = new Offer("build a bridge across the river");
        offer.setAuthorName(voter1.getLogin());
        offer.addVoterAndRating(voter1, 4 );
        assertEquals(4, offer.getRatingByVoter(voter1));
        assertEquals(1, offer.getVotersAndRatings().size());

        voter1.addOffer(offer);
        assertEquals(1, voter1.getOffers().size());
        assertTrue(voter1.getOffers().contains(offer));

        Voter voter2 = new Voter("fedor","fedorov","123456789");

        Offer offerWithRemovedRating = voter2.removeOfferRating(voter1);;
        assertEquals(0, offerWithRemovedRating.getVotersAndRatings().size());
        assertTrue(voter1.isContainsOffer(offerWithRemovedRating));
    }

    @Test
    public void testRemoveOfferRatingIfVoterRemoveYourselfRating() {
        try {
            Voter voter = new Voter("petr", "petrov", "123456789");
            Offer offer = new Offer("build a bridge across the river");
            offer.setAuthorName(voter.getLogin());
            offer.addVoterAndRating(voter, 4);
            assertEquals(4, offer.getRatingByVoter(voter));
            voter.removeOfferRating(voter);
            fail();
        }catch (VoterException ve){
            assertEquals(VoterErrorCode.VOTER_CANNOT_REMOVE_YOURSELF_RATING, ve.getVoterErrorCode());
        }
    }
}
