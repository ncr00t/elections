package net.thumbtack.school.elections.daoimpl;

import net.thumbtack.school.elections.database.DataBase;
import net.thumbtack.school.elections.exception.VoterErrorCode;
import net.thumbtack.school.elections.exception.VoterException;
import net.thumbtack.school.elections.model.Candidate;
import net.thumbtack.school.elections.model.Offer;
import net.thumbtack.school.elections.model.Voter;
import net.thumbtack.school.elections.response.RegisterVoterDtoResponse;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.*;

public class TestOfferDaoImpl {

    private static DataBase dataBase;
    private static VoterDaoImpl voterDao;
    private static OfferDaoImpl offerDao;
    private static CandidateDaoImpl candidateDao;

    @BeforeClass
    public static void setUpClass(){
        dataBase = DataBase.getDataBase();
        voterDao = new VoterDaoImpl();
        offerDao = new OfferDaoImpl();
        candidateDao = new CandidateDaoImpl();
    }

    @Before
    public void setUp(){
        dataBase.getVoters().clear();
        dataBase.getCandidates().clear();
    }

    @Test
    public void testOfferDaoImplInsertOfferToElectionProgram1() throws VoterException {
        Voter voter = new Voter("petr","petrov","123456789");
        voterDao.insert(voter);
        assertEquals(1, dataBase.getVoters().size());

        Candidate candidate = new Candidate("petr","petrov","123456789");
        candidateDao.insert(candidate);
        assertEquals(1, dataBase.getCandidates().size());

        Offer offer = new Offer("build a bridge across the river");
        offer.addVoterAndRating(voter, offer.getMAX_RATING());
        offerDao.insertOfferToElectionProgram(candidate.getId(), offer);
        assertEquals(1, candidate.getElectionProgram().size());
    }

    @Test(expected = VoterException.class)
    public void testOfferDaoImplInsertOfferToElectionProgram2() throws VoterException {
        Voter voter = new Voter("petr","petrov","123456789");
        voterDao.insert(voter);
        assertEquals(1, dataBase.getVoters().size());
        assertEquals(0, dataBase.getCandidates().size());

        Offer offer = new Offer("build a bridge across the river");
        offer.addVoterAndRating(voter, offer.getMAX_RATING());
        offerDao.insertOfferToElectionProgram(voter.getId(), offer);
    }

    @Test
    public void testOfferDaoImplInsertOfferToElectionProgram3() throws VoterException {
        try{
            Voter voter = new Voter("petr","petrov","123456789");
            voterDao.insert(voter);
            assertEquals(1, dataBase.getVoters().size());
            assertEquals(0, dataBase.getCandidates().size());

            Offer offer = new Offer("build a bridge across the river");
            offer.addVoterAndRating(voter, offer.getMAX_RATING());
            offerDao.insertOfferToElectionProgram(voter.getId(), offer);
        }catch (VoterException ve){
            assertEquals(VoterErrorCode.CANDIDATE_NOT_FOUND_BY_ID, ve.getVoterErrorCode());
        }
    }

    @Test
    public void testOfferDaoImplRemoveOfferFromElectionProgram1() throws VoterException {
        Candidate candidate = new Candidate("petr","petrov","123456789");
        candidateDao.insert(candidate);
        assertEquals(1, dataBase.getCandidates().size());

        Voter voter = new Voter("ivan","ivanov","123456789");

        Offer offer = new Offer("build a bridge across the river");
        offer.addVoterAndRating(voter, offer.getMAX_RATING());
        offer.setAuthorName(voter.getLogin());

        offerDao.insertOfferToElectionProgram(candidate.getId(), offer);
        assertEquals(1, candidate.getElectionProgram().size());

        offerDao.removeOfferFromElectionProgram(candidate.getId(), offer);
        assertTrue(candidate.getElectionProgram().isEmpty());
        assertEquals(0, candidate.getElectionProgram().size());
    }

    @Test
    public void testOfferDaoImplRemoveOfferFromElectionProgram4() {
        try{
            Voter voter = new Voter("petr","petrov","123456789");

            Candidate candidate = new Candidate("petr","petrov","123456789");
            candidateDao.insert(candidate);
            assertEquals(1, dataBase.getCandidates().size());

            Offer offer = new Offer("build a bridge across the river");
            offer.addVoterAndRating(voter, offer.getMAX_RATING());
            offer.setAuthorName(candidate.getLogin());

            offerDao.insertOfferToElectionProgram(candidate.getId(), offer);
            assertEquals(1, candidate.getElectionProgram().size());

            offerDao.removeOfferFromElectionProgram(candidate.getId(), offer);
        }catch (VoterException ve){
            assertEquals(VoterErrorCode.CANDIDATE_CANNOT_REMOVE_YOURSELF_OFFER, ve.getVoterErrorCode());
        }
    }

    @Test(expected = VoterException.class)
    public void testOfferDaoImplRemoveOfferFromElectionProgram5() throws VoterException {
        Voter voter = new Voter("petr","petrov","123456789");
        voterDao.insert(voter);
        assertEquals(1, dataBase.getVoters().size());

        Candidate candidate = new Candidate("petr","petrov","123456789");
        candidateDao.insert(candidate);
        assertEquals(1, dataBase.getCandidates().size());

        Offer offer = new Offer("build a bridge across the river");
        offer.addVoterAndRating(voter, offer.getMAX_RATING());
        offer.setAuthorName(candidate.getLogin());

        offerDao.insertOfferToElectionProgram(candidate.getId(), offer);
        assertEquals(1, candidate.getElectionProgram().size());

        offerDao.removeOfferFromElectionProgram(candidate.getId(), offer);
    }

    @Test
    public void testOfferDaoImplInsertOffer1() throws VoterException {
        Voter voter = new Voter("petr","petrov","123456789");
        voterDao.insert(voter);
        assertEquals(1, dataBase.getVoters().size());

        Offer offer = new Offer("build a bridge across the river");
        offer.addVoterAndRating(voter, offer.getMAX_RATING());
        offerDao.insertOffer(voter.getId(), offer);
        assertEquals(1, voter.getOffers().size());
        assertTrue(voter.getOffers().contains(offer));
    }

    @Test(expected = VoterException.class)
    public void testOfferDaoImplInsertOffer2() throws VoterException {
        Voter voter = new Voter("petr","petrov","123456789");
        assertEquals(0, dataBase.getVoters().size());

        Offer offer = new Offer("build a bridge across the river");
        offer.addVoterAndRating(voter, offer.getMAX_RATING());
        offerDao.insertOffer(voter.getId(), offer);
    }

    @Test
    public void testOfferDaoImplInsertOffer3() {
        try{
            Voter voter = new Voter("petr","petrov","123456789");
            assertEquals(0, dataBase.getVoters().size());

            Offer offer = new Offer("build a bridge across the river");
            offer.addVoterAndRating(voter, offer.getMAX_RATING());
            offerDao.insertOffer(voter.getId(), offer);
            fail();
        }catch (VoterException ve){
            assertEquals(VoterErrorCode.VOTER_NOT_FOUND_BY_ID, ve.getVoterErrorCode());
        }
    }

    @Test
    public void testOfferDaoImplInsertOffer4() throws VoterException {
        Voter voter = new Voter("petr","petrov","123456789");
        assertEquals(0, dataBase.getVoters().size());

        Offer offer = new Offer("build a bridge across the river");
        offer.addVoterAndRating(voter, offer.getMAX_RATING());
        try{
            offerDao.insertOffer(voter.getId(), offer);
            fail();
        }catch (VoterException ve){
            assertEquals(0, voter.getOffers().size());
            assertFalse(voter.getOffers().contains(offer));
        }
    }
    @Test
    public void testOfferDaoImplChangeOfferRating1() throws VoterException {
        Candidate candidate = new Candidate("petr","petrov","123456789");
        candidateDao.insert(candidate);
        assertEquals(1, dataBase.getCandidates().size());

        Voter voter1 = new Voter("ivan","ivanov","123456789");
        voterDao.insert(voter1);
        assertEquals(1, dataBase.getVoters().size());

        Offer offer = new Offer("build a bridge across the river");
        offer.addVoterAndRating(voter1, offer.getMAX_RATING());
        offer.setAuthorName(voter1.getLogin());

        offerDao.insertOffer(voter1.getId(), offer);
        assertEquals(1, voter1.getOffers().size());
        assertTrue(voter1.isContainsOffer(offer));
        assertEquals(5, offer.getRatingByVoter(voter1));

        offerDao.insertOfferToElectionProgram(candidate.getId(), offer);
        assertEquals(1, candidate.getElectionProgram().size());

        Voter voter2 = new Voter(1, "alex","alexandrov","123456789");
        voterDao.insert(voter2);
        assertEquals(2, dataBase.getVoters().size());

        Offer offerWithChangedRating = offerDao.changeOfferRating(voter2, voter1, 3);
        assertEquals(3, offerWithChangedRating.getRatingByVoter(voter1));
        assertEquals(1, candidate.getElectionProgram().size());
        assertEquals(1, voter1.getOffers().size());
        assertTrue(candidate.isContainsToElectionProgram(offerWithChangedRating));
        assertTrue(voter1.isContainsOffer(offerWithChangedRating));
    }

    @Test(expected = VoterException.class)
    public void testOfferDaoImplChangeOfferRating2() throws VoterException {
        Candidate candidate = new Candidate("petr","petrov","123456789");
        candidateDao.insert(candidate);
        assertEquals(1, dataBase.getCandidates().size());

        Voter voter1 = new Voter("ivan","ivanov","123456789");
        voterDao.insert(voter1);
        assertEquals(1, dataBase.getVoters().size());

        Offer offer = new Offer("build a bridge across the river");
        offer.addVoterAndRating(voter1, offer.getMAX_RATING());
        offer.setAuthorName(voter1.getLogin());
        offerDao.insertOffer(voter1.getId(), offer);
        assertEquals(1, voter1.getOffers().size());
        assertTrue(voter1.getOffers().contains(offer));
        assertEquals(5, offer.getRatingByVoter(voter1));

        offerDao.insertOfferToElectionProgram(candidate.getId(), offer);
        assertEquals(1, candidate.getElectionProgram().size());

        Voter voter2 = new Voter(1, "alex","alexandrov","123456789");
        dataBase.insertVoter(voter2);
        assertEquals(2, dataBase.getVoters().size());

        offerDao.changeOfferRating(voter1, voter1, 3);
    }

    @Test
    public void testOfferDaoImplChangeOfferRating3() {
        try {
            Candidate candidate = new Candidate("petr", "petrov", "123456789");
            candidateDao.insert(candidate);
            assertEquals(1, dataBase.getCandidates().size());

            Voter voter2 = new Voter("ivan", "ivanov", "123456789");
            voterDao.insert(voter2);
            assertEquals(1, dataBase.getVoters().size());

            Offer offer = new Offer("build a bridge across the river");
            offer.addVoterAndRating(voter2, offer.getMAX_RATING());
            offer.setAuthorName(voter2.getLogin());
            offerDao.insertOffer(voter2.getId(), offer);
            assertEquals(1, voter2.getOffers().size());
            assertTrue(voter2.getOffers().contains(offer));
            assertEquals(5, offer.getRatingByVoter(voter2));

            offerDao.insertOfferToElectionProgram(candidate.getId(), offer);
            assertEquals(1, candidate.getElectionProgram().size());

            offerDao.changeOfferRating(voter2, voter2, 3);
            fail();
        }catch (VoterException ve){
            assertEquals(VoterErrorCode.VOTER_CANNOT_CHANGE_YOURSELF_RATING, ve.getVoterErrorCode());
        }
    }

    @Test
    public void testOfferDaoImplRemoveOfferRating1() throws VoterException {
        Candidate candidate = new Candidate("petr","petrov","123456789");
        candidateDao.insert(candidate);
        assertEquals(1, dataBase.getCandidates().size());

        Voter voter1 = new Voter("ivan","ivanov","123456789");
        voterDao.insert(voter1);
        assertEquals(1, dataBase.getVoters().size());

        Offer offer = new Offer("build a bridge across the river");
        offer.addVoterAndRating(voter1, offer.getMAX_RATING());
        assertEquals(1, offer.getVotersAndRatings().size());
        offer.setAuthorName(voter1.getLogin());

        offerDao.insertOffer(voter1.getId(), offer);
        assertEquals(5, offer.getRatingByVoter(voter1));
        assertEquals(1, offer.getVotersAndRatings().size());
        assertEquals(1, voter1.getOffers().size());
        assertTrue(voter1.isContainsOffer(offer));

        offerDao.insertOfferToElectionProgram(candidate.getId(), offer);
        assertEquals(1, candidate.getElectionProgram().size());
        assertTrue(candidate.isContainsToElectionProgram(offer));

        Offer offerWithRemovedRating = offerDao.removeOfferRating(candidate, voter1);
        assertEquals(0, offerWithRemovedRating.getVotersAndRatings().size());
        assertEquals(1, candidate.getElectionProgram().size());
        assertEquals(1, voter1.getOffers().size());
        assertTrue(voter1.isContainsOffer(offerWithRemovedRating));
        assertTrue(candidate.isContainsToElectionProgram(offerWithRemovedRating));
    }

    @Test(expected = VoterException.class)
    public void testOfferDaoImplRemoveOfferRating2() throws VoterException {
        Candidate candidate = new Candidate("petr", "petrov", "123456789");
        candidateDao.insert(candidate);
        assertEquals(1, dataBase.getCandidates().size());

        Voter voter1 = new Voter("ivan", "ivanov", "123456789");
        voterDao.insert(voter1);
        assertEquals(1, dataBase.getVoters().size());

        Offer offer = new Offer("build a bridge across the river");
        offer.addVoterAndRating(voter1, offer.getMAX_RATING());
        offer.setAuthorName(voter1.getLogin());
        offerDao.insertOffer(voter1.getId(), offer);
        assertEquals(1, voter1.getOffers().size());
        assertTrue(voter1.getOffers().contains(offer));
        assertEquals(5, offer.getRatingByVoter(voter1));

        offerDao.insertOfferToElectionProgram(candidate.getId(), offer);
        assertEquals(1, candidate.getElectionProgram().size());

        Voter voter2 = new Voter(1,"alex","alexandrov","123456789");
        dataBase.insertVoter(voter2);
        assertEquals(2, dataBase.getVoters().size());

        offerDao.removeOfferRating(voter1, voter1);
    }

    @Test
    public void testOfferDaoImplRemoveOfferRating3() {
        try {
            Voter voter1 = new Voter("ivan", "ivanov", "123456789");
            voterDao.insert(voter1);
            assertEquals(1, dataBase.getVoters().size());

            Offer offer = new Offer("build a bridge across the river");
            offer.addVoterAndRating(voter1, offer.getMAX_RATING());
            offer.setAuthorName(voter1.getLogin());
            offerDao.insertOffer(voter1.getId(), offer);
            assertEquals(1, voter1.getOffers().size());
            assertTrue(voter1.isContainsOffer(offer));
            assertEquals(5, offer.getRatingByVoter(voter1));

            offerDao.removeOfferRating(voter1, voter1);
            fail();
        }catch (VoterException ve){
            assertEquals(VoterErrorCode.VOTER_CANNOT_REMOVE_YOURSELF_RATING, ve.getVoterErrorCode());
        }
    }

    @Test
    public void testOfferDaoImplRemoveOfferRating4() {
        try {
            Voter voter1 = new Voter("ivan", "ivanov", "123456789");
            voterDao.insert(voter1);
            assertEquals(1, dataBase.getVoters().size());

            Offer offer = new Offer("build a bridge across the river");
            offer.addVoterAndRating(voter1, offer.getMAX_RATING());
            assertEquals(1, offer.getVotersAndRatings().size());
            offer.setAuthorName(voter1.getLogin());

            offerDao.insertOffer(voter1.getId(), offer);
            assertEquals(5, offer.getRatingByVoter(voter1));
            assertEquals(1, offer.getVotersAndRatings().size());
            assertEquals(1, voter1.getOffers().size());
            assertTrue(voter1.isContainsOffer(offer));

            Voter voter2 = new Voter(1, "alex", "alexandrov", "123456789");
            voterDao.insert(voter2);
            assertEquals(2, dataBase.getVoters().size());

            Offer offerWithRemovedRating = dataBase.removeOfferRating(voter2, voter1);
            assertEquals(1, voter1.getOffers().size());
            assertTrue(voter1.isContainsOffer(offerWithRemovedRating));
            offer.getRatingByVoter(voter1);
            fail();
        }catch (VoterException ve){
            assertEquals(VoterErrorCode.VOTER_NOT_SET_RATING, ve.getVoterErrorCode());
        }
    }

    @Test
    public void testOfferDaoImplGetOffersAndAverageRatings1() throws VoterException {
        Voter voter1 = new Voter("ivan","ivanov","123456789");
        voterDao.insert(voter1);

        Offer offer1 = new Offer("build a bridge across the river");
        assertEquals("build a bridge across the river", offer1.getDescription());
        offer1.addVoterAndRating(voter1, offer1.getMAX_RATING());
        offer1.setAuthorName(voter1.getLogin());
        assertNotEquals(null, offer1.getVotersAndRatings());

        offerDao.insertOffer(voter1.getId(), offer1);
        assertTrue(voter1.getOffers().contains(offer1));

        Voter voter2 = new Voter(1, "petr", "petrov", "123456789");
        voterDao.insert(voter2);

        Offer offer2 = new Offer("repair the road");
        assertEquals("repair the road", offer2.getDescription());
        offer2.addVoterAndRating(voter2, offer2.getMAX_RATING());
        offer2.setAuthorName(voter2.getLogin());
        assertNotEquals(null, offer2.getVotersAndRatings());

        offerDao.insertOffer(voter2.getId(), offer2);
        assertTrue(voter2.getOffers().contains(offer2));

        offerDao.changeOfferRating(voter2, voter1, 1);
        assertFalse(offerDao.getOffersAndAverageRatings().isEmpty());
        assertThat(offerDao.getOffersAndAverageRatings().values(), contains(5, 1));
    }

    @Test
    public void testOfferDaoImplGetOffersAndAverageRatings2() {
        try {
            Voter voter1 = new Voter("ivan", "ivanov", "123456789");
            voterDao.insert(voter1);

            Offer offer = new Offer("build a bridge across the river");
            assertEquals("build a bridge across the river", offer.getDescription());
            offer.addVoterAndRating(voter1, offer.getMAX_RATING());
            offer.setAuthorName(voter1.getLogin());
            assertNotEquals(null, offer.getVotersAndRatings());

            offerDao.insertOffer(voter1.getId(), offer);
            assertTrue(voter1.getOffers().contains(offer));

            Voter voter2 = new Voter(1, "petr", "petrov", "123456789");
            voterDao.insert(voter2);

            offerDao.removeOfferRating(voter2, voter1);
            offerDao.getOffersAndAverageRatings();
            fail();
        }catch (VoterException ve){
            assertEquals(VoterErrorCode.OFFER_WITHOUT_RATINGS, ve.getVoterErrorCode());
        }
    }

    @Test(expected = VoterException.class)
    public void testOfferDaoImplGetOffersAndAverageRatingsShouldThrowException() throws VoterException {
        Voter voter1 = new Voter("ivan", "ivanov", "123456789");
        voterDao.insert(voter1);

        Offer offer = new Offer("build a bridge across the river");
        assertEquals("build a bridge across the river", offer.getDescription());
        offer.addVoterAndRating(voter1, offer.getMAX_RATING());
        offer.setAuthorName(voter1.getLogin());
        assertNotEquals(null, offer.getVotersAndRatings());

        offerDao.insertOffer(voter1.getId(), offer);
        assertTrue(voter1.isContainsOffer(offer));

        Voter voter2 = new Voter(1, "petr", "petrov", "123456789");
        voterDao.insert(voter2);

        offerDao.removeOfferRating(voter2, voter1);
        offerDao.getOffersAndAverageRatings();
    }

    @Test
    public void testOfferDaoImplGetOffersSortedByAverageRatings() throws VoterException {
        Voter voter1 = new Voter("ivan","ivanov","123456789");
        voterDao.insert(voter1);

        Offer offer1 = new Offer("build a bridge across the river");
        assertEquals("build a bridge across the river", offer1.getDescription());
        offer1.addVoterAndRating(voter1, offer1.getMAX_RATING());
        offer1.setAuthorName(voter1.getLogin());
        assertNotEquals(null, offer1.getVotersAndRatings());

        offerDao.insertOffer(voter1.getId(), offer1);
        assertTrue(voter1.getOffers().contains(offer1));

        Voter voter2 = new Voter("petr", "petrov", "123456789");
        voterDao.insert(voter2);

        Offer offer2 = new Offer("repair the road");
        assertEquals("repair the road", offer2.getDescription());
        offer2.addVoterAndRating(voter2, offer2.getMAX_RATING());
        offer2.setAuthorName(voter2.getLogin());
        assertNotEquals(null, offer2.getVotersAndRatings());

        offerDao.insertOffer(voter2.getId(), offer2);
        assertTrue(voter2.getOffers().contains(offer2));

        offerDao.changeOfferRating(voter2, voter1, 1);
        assertFalse(offerDao.getOffersSortedByAverageRatings().isEmpty());
    }
}
