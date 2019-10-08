package net.thumbtack.school.elections.database;

import net.thumbtack.school.elections.exception.VoterErrorCode;
import net.thumbtack.school.elections.exception.VoterException;
import net.thumbtack.school.elections.model.Candidate;
import net.thumbtack.school.elections.model.Offer;
import net.thumbtack.school.elections.model.Voter;
import net.thumbtack.school.elections.response.RegisterVoterDtoResponse;
import net.thumbtack.school.elections.service.GenerateTokenService;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.*;

public class TestDataBase {

    private static DataBase dataBase;

    @BeforeClass
    public static void setUpClass(){
        dataBase = DataBase.getDataBase();
    }

    @Before
    public void setUp(){
        dataBase.getVoters().clear();
        dataBase.getCandidates().clear();
        dataBase.getVotedVoters().clear();
        dataBase.getVotedCandidates().clear();
        dataBase.getVotedAgainstAll().clear();
        dataBase.getRankedCandidates().clear();
        dataBase.getTokensAndVoters().clear();
    }

    @Test(expected = VoterException.class)
    public void testInsertVoterShouldThrowException() throws VoterException {
        Voter voter1 = new Voter("ivan","ivanov","123456789");
        Voter voter2 = new Voter("ivan","ivanov","123456789");

        dataBase.insertVoter(voter1);
        assertEquals(1, dataBase.getVoters().size());

        dataBase.insertVoter(voter2);
    }

    @Test
    public void testInsertVoter1() throws VoterException {
        Voter voter1 = new Voter("ivan","ivanov","123456789");
        Voter voter2 = new Voter(1, "petr","petrov","123456789");

        dataBase.insertVoter(voter1);
        assertEquals(1, dataBase.getVoters().size());

        dataBase.insertVoter(voter2);
        assertEquals(2, dataBase.getVoters().size());
    }

    @Test
    public void testInsertVoter2() throws VoterException {
        Voter voter1 = new Voter();
        voter1.setFirstName("ivan");
        voter1.setLogin("ivanov");
        voter1.setPassword("123456789");
        voter1.setId(0);

        Voter voter2 = new Voter();
        voter2.setFirstName("petr");
        voter2.setLogin("petrov");
        voter2.setPassword("123456789");
        voter2.setId(1);

        dataBase.insertVoter(voter1);
        assertEquals(1, dataBase.getVoters().size());

        dataBase.insertVoter(voter2);
        assertEquals(2, dataBase.getVoters().size());
    }

    @Test
    public void testInsertVoter3() throws VoterException {
        Voter voter1 = new Voter("ivan","ivanov","123456789");
        Voter voter2 = new Voter("ivan","ivanov","123456789");

        dataBase.insertVoter(voter1);
        assertEquals(1, dataBase.getVoters().size());

        try{
            dataBase.insertVoter(voter2);
            fail();
        }catch (VoterException ve){
            assertNotEquals(2, dataBase.getVoters().size());
        }
    }

    @Test
    public void testInsertVoter4() throws VoterException {
        Voter voter1 = new Voter();
        voter1.setFirstName("ivan");
        voter1.setLogin("ivanov");
        voter1.setPassword("123456789");

        Voter voter2 = new Voter();
        voter2.setFirstName("ivan");
        voter2.setLogin("ivanov");
        voter2.setPassword("123456789");

        dataBase.insertVoter(voter1);
        assertEquals(1, dataBase.getVoters().size());

        try{
            dataBase.insertVoter(voter2);
            fail();
        }catch (VoterException ve){
            assertNotEquals(2, dataBase.getVoters().size());
        }
    }

    @Test
    public void testInsertVoter5() {
        try {
            Voter voter1 = new Voter("ivan", "ivanov", "12345678");
            Voter voter2 = new Voter("ivan", "ivanov", "12345678");

            dataBase.insertVoter(voter1);
            assertEquals(1, dataBase.getVoters().size());

            dataBase.insertVoter(voter2);
            fail();
        } catch (VoterException ex) {
            assertEquals(VoterErrorCode.DUPLICATE_VOTER_LOGIN, ex.getVoterErrorCode());
        }
    }

    @Test(expected = VoterException.class)
    public void testInsertCandidateShouldThrowException() throws VoterException {
        Candidate candidate1 = new Candidate("ivan","ivanov","123456789");

        dataBase.insertCandidate(candidate1);
        assertEquals(1, dataBase.getCandidates().size());

        dataBase.insertCandidate(candidate1);
    }

    @Test
    public void testInsertCandidate1() throws VoterException {
        Candidate candidate1 = new Candidate("ivan","ivanov","123456789");
        Candidate candidate2 = new Candidate(1, "petr","petrov","123456789");

        dataBase.insertCandidate(candidate1);
        assertEquals(1, dataBase.getCandidates().size());

        dataBase.insertCandidate(candidate2);
        assertEquals(2, dataBase.getCandidates().size());
    }

    @Test
    public void testInsertCandidate2() {
        try{
            Candidate candidate1 = new Candidate("ivan","ivanov","123456789");
            dataBase.insertCandidate(candidate1);
            assertEquals(1, dataBase.getCandidates().size());

            dataBase.insertCandidate(candidate1);
            fail();
        } catch (VoterException ex) {
            assertEquals(VoterErrorCode.DUPLICATE_VOTER_LOGIN, ex.getVoterErrorCode());
        }
    }

    @Test
    public void testInsertVotedVoter1() throws VoterException {
        Voter voter1 = new Voter("ivan","ivanov","123456789");

        dataBase.insertVoter(voter1);
        assertEquals(1, dataBase.getVoters().size());

        dataBase.insertVotedVoter(voter1);
        assertFalse(dataBase.getVotedVoters().isEmpty());
        assertEquals(1, dataBase.getVotedVoters().size());
    }

    @Test
    public void testInsertVotedVoter2() {
        try{
            Voter voter1 = new Voter("ivan","ivanov","123456789");

            dataBase.insertVoter(voter1);
            assertEquals(1, dataBase.getVoters().size());

            dataBase.insertVotedVoter(voter1);
            assertFalse(dataBase.getVotedVoters().isEmpty());
            assertEquals(1, dataBase.getVoters().size());

            dataBase.insertVotedVoter(voter1);
            fail();
        } catch (VoterException ex) {
            assertEquals(VoterErrorCode.VOTER_CANNOT_VOTE_TWICE, ex.getVoterErrorCode());
        }
    }

    @Test(expected = VoterException.class)
    public void testInsertVotedVoterShouldThrowException() throws VoterException {
        Voter voter1 = new Voter("ivan","ivanov","123456789");

        dataBase.insertVoter(voter1);
        assertEquals(1, dataBase.getVoters().size());

        dataBase.insertVotedVoter(voter1);
        assertFalse(dataBase.getVotedVoters().isEmpty());
        assertEquals(1, dataBase.getVoters().size());

        dataBase.insertVotedVoter(voter1);
    }

    @Test
    public void testInsertVotedCandidate1() throws VoterException {
        Candidate candidate1 = new Candidate("ivan","ivanov","123456789");
        dataBase.insertCandidate(candidate1);
        assertEquals(1, dataBase.getCandidates().size());

        dataBase.insertVotedCandidate(candidate1);
        assertFalse(dataBase.getVotedCandidates().isEmpty());
        assertEquals(1, dataBase.getVotedCandidates().size());
    }

    @Test
    public void testInsertVotedCandidate2() {
        try{
            Candidate candidate1 = new Candidate("ivan","ivanov","123456789");
            dataBase.insertCandidate(candidate1);
            assertEquals(1, dataBase.getCandidates().size());

            dataBase.insertVotedCandidate(candidate1);
            assertFalse(dataBase.getVotedCandidates().isEmpty());
            assertEquals(1, dataBase.getVotedCandidates().size());

            dataBase.insertVotedCandidate(candidate1);
            fail();
        } catch (VoterException ex) {
            assertEquals(VoterErrorCode.CANDIDATE_CANNOT_VOTE_TWICE, ex.getVoterErrorCode());
        }
    }

    @Test(expected = VoterException.class)
    public void testInsertVotedCandidateShouldThrowException() throws VoterException {
        Candidate candidate1 = new Candidate("ivan","ivanov","123456789");

        dataBase.insertCandidate(candidate1);
        assertEquals(1, dataBase.getCandidates().size());

        dataBase.insertVotedCandidate(candidate1);
        assertFalse(dataBase.getVotedCandidates().isEmpty());
        assertEquals(1, dataBase.getVotedCandidates().size());

        dataBase.insertVotedCandidate(candidate1);
    }

    @Test
    public void testInsertToVotedAgainstAll() throws VoterException {
        Voter voter1 = new Voter("ivan","ivanov","123456789");

        dataBase.insertVoter(voter1);
        assertEquals(1, dataBase.getVoters().size());

        dataBase.insertToVotedAgainstAll(voter1);
        assertFalse(dataBase.getVotedAgainstAll().isEmpty());
        assertEquals(1, dataBase.getVotedAgainstAll().size());
    }

    @Test
    public void testInsertToRankedCandidates() throws VoterException {
        Candidate candidate = new Candidate("ivan","ivanov","123456789");
        dataBase.insertCandidate(candidate);
        assertEquals(1, dataBase.getCandidates().size());

        dataBase.insertToRankedCandidates(candidate);
        assertFalse(dataBase.getRankedCandidates().isEmpty());
        assertEquals(1, dataBase.getRankedCandidates().size());
    }

    @Test
    public void testGetTokenByLogin1() throws VoterException {
        Voter voter1 = new Voter("ivan","ivanov","123456789");
        String voterToken1 = GenerateTokenService.generateNewToken();

        dataBase.insertVoter(voter1);
        assertEquals(1, dataBase.getVoters().size());
        dataBase.getTokensAndVoters().put(voterToken1, voter1);

        String expectedVoterToken = dataBase.getTokenByLogin(voter1.getLogin(), dataBase.getTokensAndVoters());
        assertEquals(voterToken1, expectedVoterToken);
    }

    @Test
    public void testGetTokenByLogin2() throws VoterException {
        Candidate candidate = new Candidate("ivan","ivanov","123456789");
        dataBase.insertCandidate(candidate);
        assertEquals(1, dataBase.getCandidates().size());

        String candidateToken = GenerateTokenService.generateNewToken();
        dataBase.getTokensAndVoters().put(candidateToken, candidate);
        String expectedCandidateToken = dataBase.getTokenByLogin(candidate.getLogin(), dataBase.getTokensAndVoters());
        assertEquals(candidateToken, expectedCandidateToken);
    }

    @Test
    public void testGeCandidates1() throws VoterException {
        Candidate candidate1 = new Candidate("ivan","ivanov","123456789");
        Candidate candidate2 = new Candidate(1, "petr","petrov","123456789");

        dataBase.insertCandidate(candidate1);
        assertEquals(1, dataBase.getCandidates().size());

        dataBase.insertCandidate(candidate2);
        assertEquals(2, dataBase.getCandidates().size());
    }

    @Test
    public void testGetCandidates2() {
        try{
            Candidate candidate1 = new Candidate("ivan","ivanov","123456789");
            Candidate candidate2 = new Candidate("ivan","ivanov","123456789");

            dataBase.insertCandidate(candidate1);
            assertEquals(1, dataBase.getCandidates().size());

            dataBase.insertCandidate(candidate2);
            fail();
        } catch (VoterException ex) {
            assertEquals(VoterErrorCode.DUPLICATE_VOTER_LOGIN, ex.getVoterErrorCode());
            assertNotEquals(2, dataBase.getCandidates().size());
        }
    }

    @Test
    public void testGeCandidatesAndPrograms1() throws VoterException {
        Candidate candidate1 = new Candidate("ivan","ivanov","123456789");
        Candidate candidate2 = new Candidate(1, "petr","petrov","123456789");

        dataBase.insertCandidate(candidate1);
        assertEquals(1, dataBase.getCandidates().size());

        dataBase.insertCandidate(candidate2);
        assertEquals(2, dataBase.getCandidates().size());

        assertFalse(dataBase.getCandidatesAndPrograms().isEmpty());
        assertTrue(dataBase.getCandidatesAndPrograms().containsKey(candidate1));
        assertTrue(dataBase.getCandidatesAndPrograms().containsKey(candidate2));
    }

    @Test
    public void testGeCandidatesAndPrograms2() {
        dataBase.getCandidates().clear();
        assertTrue(dataBase.getCandidates().isEmpty());
        assertTrue(dataBase.getCandidatesAndPrograms().isEmpty());
    }

    @Test(expected = VoterException.class)
    public void testRemoveCandidateShouldThrowException() throws VoterException {
        assertEquals(0, dataBase.getCandidates().size());
        dataBase.removeCandidate(new Candidate("vasya","pupkin","123456789"));
    }

    @Test
    public void testRemoveCandidateIfCandidateContainsInCandidates() throws VoterException {
        Candidate candidate = new Candidate("vasya","pupkin","123456789");
        dataBase.insertCandidate(candidate);
        assertEquals(1, dataBase.getCandidates().size());

        dataBase.removeCandidate(candidate);
        assertEquals(0, dataBase.getCandidates().size());
    }

    @Test
    public void testRemoveCandidateIfCandidateNotContainsInCandidates() {
        try {
            assertEquals(0, dataBase.getCandidates().size());
            dataBase.removeCandidate(new Candidate("vasya","pupkin","123456789"));
            fail();
        } catch (VoterException ex) {
            assertEquals(VoterErrorCode.CANDIDATE_NOT_FOUND_BY_ID, ex.getVoterErrorCode());
        }
    }

    @Test
    public void testInsertOfferInOffersVoter() throws VoterException {
        Voter voter = new Voter("ivan","ivanov","123456789");
        dataBase.insertVoter(voter);

        Offer offer = new Offer("build a bridge across the river");
        assertEquals("build a bridge across the river", offer.getDescription());
        offer.addVoterAndRating(voter, offer.getMAX_RATING());
        assertNotEquals(null, offer.getVotersAndRatings());

        dataBase.insertOfferToVoterOffers(voter.getId(), offer);
        assertTrue(voter.getOffers().contains(offer));
    }

    @Test
    public void testGetAllOffers() throws VoterException {
        Voter voter = new Voter("ivan","ivanov","123456789");
        dataBase.insertVoter(voter);

        Offer offer = new Offer("build a bridge across the river");
        assertEquals("build a bridge across the river", offer.getDescription());
        offer.addVoterAndRating(voter, offer.getMAX_RATING());
        assertNotEquals(null, offer.getVotersAndRatings());

        dataBase.insertOfferToVoterOffers(voter.getId(), offer);
        assertTrue(voter.getOffers().contains(offer));

        assertFalse(dataBase.getAllOffers().isEmpty());
        assertEquals(1, dataBase.getAllOffers().size());
    }

    @Test
    public void testGetOffersAndAverageRatings1() throws VoterException {
        Voter voter1 = new Voter("ivan","ivanov","123456789");
        dataBase.insertVoter(voter1);

        Offer offer1 = new Offer("build a bridge across the river");
        assertEquals("build a bridge across the river", offer1.getDescription());
        offer1.addVoterAndRating(voter1, offer1.getMAX_RATING());
        offer1.setAuthorName(voter1.getLogin());
        assertNotEquals(null, offer1.getVotersAndRatings());

        dataBase.insertOfferToVoterOffers(voter1.getId(), offer1);
        assertTrue(voter1.isContainsOffer(offer1));

        Voter voter2 = new Voter(1, "petr", "petrov", "123456789");
        dataBase.insertVoter(voter2);

        Offer offer2 = new Offer("repair the road");
        assertEquals("repair the road", offer2.getDescription());
        offer2.addVoterAndRating(voter2, offer2.getMAX_RATING());
        offer2.setAuthorName(voter2.getLogin());
        assertNotEquals(null, offer2.getVotersAndRatings());

        dataBase.insertOfferToVoterOffers(voter2.getId(), offer2);
        assertTrue(voter2.isContainsOffer(offer2));

        dataBase.changeOfferRating(voter2, voter1, 1);
        assertFalse(dataBase.getOffersAndAverageRatings().isEmpty());
        assertThat(dataBase.getOffersAndAverageRatings().values(), contains(5, 1));
    }

    @Test
    public void testGetOffersAndAverageRatings2() {
        try {
            Voter voter1 = new Voter("ivan", "ivanov", "123456789");
            dataBase.insertVoter(voter1);

            Offer offer = new Offer("build a bridge across the river");
            assertEquals("build a bridge across the river", offer.getDescription());
            offer.addVoterAndRating(voter1, offer.getMAX_RATING());
            offer.setAuthorName(voter1.getLogin());
            assertNotEquals(null, offer.getVotersAndRatings());

            dataBase.insertOfferToVoterOffers(voter1.getId(), offer);
            assertTrue(voter1.getOffers().contains(offer));

            Voter voter2 = new Voter(1, "petr", "petrov", "123456789");
            dataBase.insertVoter(voter2);

            dataBase.removeOfferRating(voter2, voter1);
            dataBase.getOffersAndAverageRatings();
            fail();
        }catch (VoterException ve){
            assertEquals(VoterErrorCode.OFFER_WITHOUT_RATINGS, ve.getVoterErrorCode());
        }
    }

    @Test(expected = VoterException.class)
    public void testGetOffersAndAverageRatingsShouldThrowException() throws VoterException {
        Voter voter1 = new Voter("ivan", "ivanov", "123456789");
        dataBase.insertVoter(voter1);

        Offer offer = new Offer("build a bridge across the river");
        assertEquals("build a bridge across the river", offer.getDescription());
        offer.addVoterAndRating(voter1, offer.getMAX_RATING());
        offer.setAuthorName(voter1.getLogin());
        assertNotEquals(null, offer.getVotersAndRatings());

        dataBase.insertOfferToVoterOffers(voter1.getId(), offer);
        assertTrue(voter1.getOffers().contains(offer));

        Voter voter2 = new Voter(1, "petr", "petrov", "123456789");

        dataBase.insertVoter(voter2);
        dataBase.removeOfferRating(voter2, voter1);
        dataBase.getOffersAndAverageRatings();
    }

    @Test
    public void testGetOffersSortedByAverageRatings() throws VoterException {
        Voter voter1 = new Voter("ivan","ivanov","123456789");
        dataBase.insertVoter(voter1);

        Offer offer1 = new Offer("build a bridge across the river");
        assertEquals("build a bridge across the river", offer1.getDescription());
        offer1.addVoterAndRating(voter1, offer1.getMAX_RATING());
        offer1.setAuthorName(voter1.getLogin());
        assertNotEquals(null, offer1.getVotersAndRatings());

        dataBase.insertOfferToVoterOffers(voter1.getId(), offer1);
        assertTrue(voter1.getOffers().contains(offer1));

        Voter voter2 = new Voter("petr", "petrov", "123456789");
        dataBase.insertVoter(voter2);

        Offer offer2 = new Offer("repair the road");
        assertEquals("repair the road", offer2.getDescription());
        offer2.addVoterAndRating(voter2, offer2.getMAX_RATING());
        offer2.setAuthorName(voter2.getLogin());
        assertNotEquals(null, offer2.getVotersAndRatings());

        dataBase.insertOfferToVoterOffers(voter2.getId(), offer2);
        assertTrue(voter2.getOffers().contains(offer2));

        dataBase.changeOfferRating(voter2, voter1, 1);
        assertFalse(dataBase.getOffersSortedByAverageRatings().isEmpty());
    }

    @Test
    public void testInsertOfferToElectionProgram() throws VoterException {
        Voter voter = new Voter("ivan","ivanov","123456789");
        dataBase.insertVoter(voter);

        Candidate candidate = new Candidate("ivan","ivanov","123456789");
        dataBase.insertCandidate(candidate);
        assertEquals(1, dataBase.getCandidates().size());

        Offer offer = new Offer("build a bridge across the river");
        assertEquals("build a bridge across the river", offer.getDescription());
        offer.addVoterAndRating(voter, offer.getMAX_RATING());
        assertNotEquals(null, offer.getVotersAndRatings());

        dataBase.insertOfferToElectionProgram(candidate.getId(), offer);
        assertTrue(candidate.getElectionProgram().contains(offer));
    }

    @Test
    public void testRemoveOfferFromElectionProgram1() throws VoterException {
        Candidate candidate = new Candidate("petr","petrov","123456789");
        dataBase.insertCandidate(candidate);
        assertEquals(1, dataBase.getCandidates().size());

        Voter voter = new Voter("ivan","ivanov","123456789");
        dataBase.insertVoter(voter);
        assertEquals(1, dataBase.getVoters().size());

        Offer offer = new Offer("build a bridge across the river");
        offer.addVoterAndRating(voter, offer.getMAX_RATING());
        offer.setAuthorName(voter.getLogin());

        dataBase.insertOfferToElectionProgram(candidate.getId(), offer);
        assertEquals(1, candidate.getElectionProgram().size());

        dataBase.removeOfferFromElectionProgram(candidate.getId(), offer);
        assertTrue(candidate.getElectionProgram().isEmpty());
        assertEquals(0, candidate.getElectionProgram().size());
    }

    @Test(expected = VoterException.class)
    public void testRemoveOfferFromElectionProgram2() throws VoterException {
        Candidate candidate = new Candidate("petr","petrov","123456789");
        dataBase.insertCandidate(candidate);
        assertEquals(1, dataBase.getCandidates().size());

        Voter voter2 = new Voter("ivan","ivanov","123456789");
        dataBase.insertVoter(voter2);
        assertEquals(1, dataBase.getVoters().size());

        Offer offer = new Offer("build a bridge across the river");
        offer.addVoterAndRating(voter2, offer.getMAX_RATING());
        offer.setAuthorName(voter2.getLogin());

        dataBase.insertOfferToElectionProgram(candidate.getId(), offer);
        assertEquals(1, candidate.getElectionProgram().size());

        int wrongCandidateId = 1;
        dataBase.removeOfferFromElectionProgram(wrongCandidateId, offer);
    }

    @Test
    public void testRemoveOfferFromElectionProgram3() {
        try{
            Voter voter1 = new Voter("petr","petrov","123456789");
            dataBase.insertVoter(voter1);
            assertEquals(1, dataBase.getVoters().size());

            Candidate candidate = new Candidate("petr","petrov","123456789");
            dataBase.insertCandidate(candidate);
            assertEquals(1, dataBase.getCandidates().size());

            Voter voter2 = new Voter(1, "ivan","ivanov","123456789");
            dataBase.insertVoter(voter2);
            assertEquals(2, dataBase.getVoters().size());

            Offer offer = new Offer("build a bridge across the river");
            offer.addVoterAndRating(voter2, offer.getMAX_RATING());
            offer.setAuthorName(voter2.getLogin());

            dataBase.insertOfferToElectionProgram(candidate.getId(), offer);
            assertEquals(1, candidate.getElectionProgram().size());

            int wrongCandidateId = 1;
            dataBase.removeOfferFromElectionProgram(wrongCandidateId, offer);
        }catch (VoterException ve){
            assertEquals(VoterErrorCode.CANDIDATE_NOT_FOUND_BY_TOKEN, ve.getVoterErrorCode());
        }
    }

    @Test(expected = VoterException.class)
    public void testRemoveOfferFromElectionProgram4() throws VoterException {
        Voter voter = new Voter("petr","petrov","123456789");
        dataBase.insertVoter(voter);
        assertEquals(1, dataBase.getVoters().size());

        Candidate candidate = new Candidate("petr","petrov","123456789");
        dataBase.insertCandidate(candidate);
        assertEquals(1, dataBase.getCandidates().size());

        Offer offer = new Offer("build a bridge across the river");
        offer.addVoterAndRating(voter, offer.getMAX_RATING());
        offer.setAuthorName(candidate.getLogin());

        dataBase.insertOfferToElectionProgram(candidate.getId(), offer);
        assertEquals(1, candidate.getElectionProgram().size());

        dataBase.removeOfferFromElectionProgram(candidate.getId(), offer);
    }

    @Test
    public void testRemoveOfferFromElectionProgram5() {
        try{
            Voter voter = new Voter("petr","petrov","123456789");

            dataBase.insertVoter(voter);
            assertEquals(1, dataBase.getVoters().size());

            Candidate candidate = new Candidate("petr","petrov","123456789");
            dataBase.insertCandidate(candidate);
            assertEquals(1, dataBase.getCandidates().size());

            Offer offer = new Offer("build a bridge across the river");
            offer.addVoterAndRating(voter, offer.getMAX_RATING());
            offer.setAuthorName(candidate.getLogin());

            dataBase.insertOfferToElectionProgram(candidate.getId(), offer);
            assertEquals(1, candidate.getElectionProgram().size());

            dataBase.removeOfferFromElectionProgram(candidate.getId(), offer);
        }catch (VoterException ve){
            assertEquals(VoterErrorCode.CANDIDATE_CANNOT_REMOVE_YOURSELF_OFFER, ve.getVoterErrorCode());
        }
    }

    @Test(expected = VoterException.class)
    public void testUpdateVoterShouldThrowException() throws VoterException {
        Voter voter = new Voter("ivan", "ivanov", "123456789");
        assertEquals("ivan", voter.getFirstName());
        assertEquals("ivanov", voter.getLogin());
        assertEquals("123456789", voter.getPassword());

        dataBase.insertVoter(voter);
        assertEquals(1, dataBase.getVoters().size());

        voter.setFirstName("petr");
        voter.setLogin("petrov");
        voter.setPassword("12345678");

        int wrongId = 1;
        voter.setId(wrongId);
        dataBase.updateVoter(voter);
    }

    @Test
    public void testUpdateVoter1() throws VoterException {
        Voter voter = new Voter("ivan","ivanov","123456789");
        assertEquals("ivan", voter.getFirstName());
        assertEquals("ivanov", voter.getLogin());
        assertEquals("123456789", voter.getPassword());

        dataBase.insertVoter(voter);
        assertEquals(1, dataBase.getVoters().size());

        String token = GenerateTokenService.generateNewToken();
        dataBase.getTokensAndVoters().put(token, voter);
        assertEquals(1, dataBase.getTokensAndVoters().size());

        voter.setFirstName("petr");
        voter.setLogin("petrov");
        voter.setPassword("12345678");
        dataBase.updateVoter(voter);

        Voter updatedVoter = dataBase.getVoterById(voter.getId());
        assertEquals(1, dataBase.getVoters().size());
        assertEquals("petr", updatedVoter.getFirstName());
        assertEquals("petrov", updatedVoter.getLogin());
        assertEquals("12345678", updatedVoter.getPassword());
        assertEquals(1, dataBase.getVoters().size());
    }

    @Test
    public void testUpdateVoter2() throws VoterException {
        try {
            Voter voter = new Voter("ivan", "ivanov", "123456789");
            assertEquals("ivan", voter.getFirstName());
            assertEquals("ivanov", voter.getLogin());
            assertEquals("123456789", voter.getPassword());

            dataBase.insertVoter(voter);
            assertEquals(1, dataBase.getVoters().size());

            voter.setFirstName("petr");
            voter.setLogin("petrov");
            voter.setPassword("12345678");

            int wrongId = 1;
            voter.setId(wrongId);
            dataBase.updateVoter(voter);
            fail();
        }catch (VoterException ve){
            assertEquals(VoterErrorCode.VOTER_NOT_FOUND_BY_ID, ve.getVoterErrorCode());
        }
    }

    @Test
    public void testChangeOfferRating1() throws VoterException {
        Candidate candidate = new Candidate("petr","petrov","123456789");
        dataBase.insertCandidate(candidate);
        candidate.getElectionProgram().clear();
        assertEquals(1, dataBase.getCandidates().size());

        Voter voter1 = new Voter("ivan","ivanov","123456789");
        dataBase.insertVoter(voter1);
        assertEquals(1, dataBase.getVoters().size());

        Offer offer = new Offer("build a bridge across the river");
        offer.addVoterAndRating(voter1, offer.getMAX_RATING());
        offer.setAuthorName(voter1.getLogin());
        dataBase.insertOfferToVoterOffers(voter1.getId(), offer);
        assertEquals(1, voter1.getOffers().size());
        assertTrue(voter1.getOffers().contains(offer));
        assertEquals(5, offer.getRatingByVoter(voter1));

        dataBase.insertOfferToElectionProgram(candidate.getId(), offer);
        assertEquals(1, candidate.getElectionProgram().size());

        Voter voter2 = new Voter(1, "alex","alexandrov","123456789");

        dataBase.insertVoter(voter2);
        assertEquals(2, dataBase.getVoters().size());

        Offer offerWithChangedRating = dataBase.changeOfferRating(voter2, voter1, 3);
        assertEquals(3, offerWithChangedRating.getRatingByVoter(voter1));
        assertEquals(1, candidate.getElectionProgram().size());
        assertEquals(1, voter1.getOffers().size());
        assertTrue(candidate.isContainsToElectionProgram(offerWithChangedRating));
        assertTrue(voter1.isContainsOffer(offerWithChangedRating));
    }

    @Test(expected = VoterException.class)
    public void testChangeOfferRating2() throws VoterException {
        Candidate candidate = new Candidate("petr", "petrov", "123456789");
        dataBase.insertCandidate(candidate);
        assertEquals(1, dataBase.getCandidates().size());

        Voter voter1 = new Voter("ivan", "ivanov", "123456789");
        dataBase.insertVoter(voter1);
        assertEquals(1, dataBase.getVoters().size());

        Offer offer = new Offer("build a bridge across the river");
        offer.addVoterAndRating(voter1, offer.getMAX_RATING());
        offer.setAuthorName(voter1.getLogin());
        dataBase.insertOfferToVoterOffers(voter1.getId(), offer);
        assertEquals(1, voter1.getOffers().size());
        assertTrue(voter1.getOffers().contains(offer));
        assertEquals(5, offer.getRatingByVoter(voter1));

        Voter voter2 = new Voter(1,"alex","alexandrov","123456789");
        dataBase.insertVoter(voter2);
        assertEquals(2, dataBase.getVoters().size());

        dataBase.insertOfferToElectionProgram(candidate.getId(), offer);
        assertEquals(1, candidate.getElectionProgram().size());

        dataBase.changeOfferRating(voter1, voter1, 3);
    }

    @Test
    public void testChangeOfferRating3() {
        try {
            Candidate candidate = new Candidate("petr", "petrov", "123456789");
            dataBase.insertCandidate(candidate);
            assertEquals(1, dataBase.getCandidates().size());

            Voter voter1 = new Voter("ivan", "ivanov", "123456789");
            dataBase.insertVoter(voter1);
            assertEquals(1, dataBase.getVoters().size());

            Offer offer = new Offer("build a bridge across the river");
            offer.addVoterAndRating(voter1, offer.getMAX_RATING());
            offer.setAuthorName(voter1.getLogin());
            dataBase.insertOfferToVoterOffers(voter1.getId(), offer);
            assertEquals(1, voter1.getOffers().size());
            assertTrue(voter1.getOffers().contains(offer));
            assertEquals(5, offer.getRatingByVoter(voter1));

            dataBase.insertOfferToElectionProgram(candidate.getId(), offer);
            assertEquals(1, candidate.getElectionProgram().size());

            dataBase.changeOfferRating(voter1, voter1,3);
            fail();
        }catch (VoterException ve){
            assertEquals(VoterErrorCode.VOTER_CANNOT_CHANGE_YOURSELF_RATING, ve.getVoterErrorCode());
        }
    }

    @Test
    public void testRemoveOfferRating1() throws VoterException {
        Candidate candidate = new Candidate("petr","petrov","123456789");
        dataBase.insertCandidate(candidate);
        assertEquals(1, dataBase.getCandidates().size());

        Voter voter1 = new Voter("ivan","ivanov","123456789");
        dataBase.insertVoter(voter1);
        assertEquals(1, dataBase.getVoters().size());

        Offer offer = new Offer("build a bridge across the river");
        offer.addVoterAndRating(voter1, offer.getMAX_RATING());
        assertEquals(1, offer.getVotersAndRatings().size());
        offer.setAuthorName(voter1.getLogin());

        dataBase.insertOfferToVoterOffers(voter1.getId(), offer);
        assertEquals(5, offer.getRatingByVoter(voter1));
        assertEquals(1, offer.getVotersAndRatings().size());
        assertEquals(1, voter1.getOffers().size());
        assertTrue(voter1.getOffers().contains(offer));

        dataBase.insertOfferToElectionProgram(candidate.getId(), offer);
        assertEquals(1, candidate.getElectionProgram().size());

        Voter voter2 = new Voter(1, "alex","alexandrov","123456789");
        dataBase.insertVoter(voter2);
        assertEquals(2, dataBase.getVoters().size());

        Offer offerWithRemovedRating = dataBase.removeOfferRating(voter2, voter1);
        assertEquals(0, offerWithRemovedRating.getVotersAndRatings().size());
        assertEquals(1, candidate.getElectionProgram().size());
        assertEquals(1, voter1.getOffers().size());
        assertTrue(voter1.isContainsOffer(offerWithRemovedRating));
        assertTrue(candidate.isContainsToElectionProgram(offerWithRemovedRating));
    }

    @Test(expected = VoterException.class)
    public void testRemoveOfferRating2() throws VoterException {
        Candidate candidate = new Candidate("petr", "petrov", "123456789");
        dataBase.insertCandidate(candidate);
        assertEquals(1, dataBase.getCandidates().size());

        Voter voter = new Voter("ivan", "ivanov", "123456789");
        dataBase.insertVoter(voter);
        assertEquals(1, dataBase.getVoters().size());

        Offer offer = new Offer("build a bridge across the river");
        offer.addVoterAndRating(voter, offer.getMAX_RATING());
        offer.setAuthorName(voter.getLogin());
        dataBase.insertOfferToVoterOffers(voter.getId(), offer);
        assertEquals(1, voter.getOffers().size());
        assertTrue(voter.getOffers().contains(offer));
        assertEquals(5, offer.getRatingByVoter(voter));

        dataBase.insertOfferToElectionProgram(candidate.getId(), offer);
        assertEquals(1, candidate.getElectionProgram().size());

        dataBase.removeOfferRating(voter, voter);
    }

    @Test
    public void testRemoveOfferRating3() {
        try {
            Candidate candidate = new Candidate("petr", "petrov", "123456789");
            dataBase.insertCandidate(candidate);
            assertEquals(1, dataBase.getCandidates().size());

            Voter voter = new Voter("ivan", "ivanov", "123456789");
            dataBase.insertVoter(voter);
            assertEquals(1, dataBase.getVoters().size());

            Offer offer = new Offer("build a bridge across the river");
            offer.addVoterAndRating(voter, offer.getMAX_RATING());
            offer.setAuthorName(voter.getLogin());
            dataBase.insertOfferToVoterOffers(voter.getId(), offer);
            assertEquals(1, voter.getOffers().size());
            assertTrue(voter.getOffers().contains(offer));
            assertEquals(5, offer.getRatingByVoter(voter));

            dataBase.insertOfferToElectionProgram(candidate.getId(), offer);
            assertEquals(1, candidate.getElectionProgram().size());

            dataBase.removeOfferRating(voter, voter);
            fail();
        }catch (VoterException ve){
            assertEquals(VoterErrorCode.VOTER_CANNOT_REMOVE_YOURSELF_RATING, ve.getVoterErrorCode());
        }
    }

    @Test
    public void testRemoveOfferRating4() {
        try {
            Candidate candidate = new Candidate("petr", "petrov", "123456789");
            dataBase.insertCandidate(candidate);
            assertEquals(1, dataBase.getCandidates().size());

            Voter voter1 = new Voter("ivan", "ivanov", "123456789");
            dataBase.insertVoter(voter1);
            assertEquals(1, dataBase.getVoters().size());

            Offer offer = new Offer("build a bridge across the river");
            offer.addVoterAndRating(voter1, offer.getMAX_RATING());
            assertEquals(1, offer.getVotersAndRatings().size());
            offer.setAuthorName(voter1.getLogin());

            dataBase.insertOfferToVoterOffers(voter1.getId(), offer);
            assertEquals(5, offer.getRatingByVoter(voter1));
            assertEquals(1, offer.getVotersAndRatings().size());
            assertEquals(1, voter1.getOffers().size());
            assertTrue(voter1.getOffers().contains(offer));

            dataBase.insertOfferToElectionProgram(candidate.getId(), offer);
            assertEquals(1, candidate.getElectionProgram().size());

            Voter voter2 = new Voter(1, "alex", "alexandrov", "123456789");
            dataBase.insertVoter(voter2);
            assertEquals(2, dataBase.getVoters().size());

            Offer offerWithRemovedRating = dataBase.removeOfferRating(voter2, voter1);
            assertEquals(1, candidate.getElectionProgram().size());
            assertEquals(1, voter1.getOffers().size());
            assertTrue(voter1.isContainsOffer(offerWithRemovedRating));
            assertTrue(candidate.isContainsToElectionProgram(offerWithRemovedRating));
            offer.getRatingByVoter(voter1);
            fail();
        }catch (VoterException ve){
            assertEquals(VoterErrorCode.VOTER_NOT_SET_RATING, ve.getVoterErrorCode());
        }
    }
}
