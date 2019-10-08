package net.thumbtack.school.elections.daoimpl;

import net.thumbtack.school.elections.database.DataBase;
import net.thumbtack.school.elections.exception.VoterErrorCode;
import net.thumbtack.school.elections.exception.VoterException;
import net.thumbtack.school.elections.model.Candidate;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.junit.Assert.assertNotEquals;

public class TestCandidateDaoImpl {

    private static DataBase dataBase;
    private static VoterDaoImpl voterDao;
    private static CandidateDaoImpl candidateDao;

    @BeforeClass
    public static void setUpClass(){
        dataBase = DataBase.getDataBase();
        voterDao = new VoterDaoImpl();
        candidateDao = new CandidateDaoImpl();
    }

    @Before
    public void setUp(){
        dataBase.getVoters().clear();
        dataBase.getCandidates().clear();
        dataBase.getTokensAndVoters().clear();
        candidateDao.getVotedCandidates().clear();
    }

    @Test
    public void testCandidateDaoImplAddCandidate1() throws VoterException {
        Candidate candidate1 = new Candidate("ivan","ivanov","123456789");
        candidateDao.insert(candidate1);
        assertEquals(1, dataBase.getCandidates().size());
        assertTrue(dataBase.getCandidates().containsValue(candidate1));
    }

    @Test
    public void testCandidateDaoImplAllCandidates() throws VoterException {
        Candidate candidate1 = new Candidate("ivan","ivanov","123456789");
        Candidate candidate2 = new Candidate("ivan","ivanov","123456789");

        candidateDao.insert(candidate1);
        assertEquals(1, dataBase.getCandidates().size());
        try{
            candidateDao.insert(candidate2);
            fail();
        }catch (VoterException ve){
            assertNotEquals(2, dataBase.getCandidates().size());
        }
    }

    @Test(expected = VoterException.class)
    public void testCandidateDaoImplRemoveCandidate1() throws VoterException {
        Candidate candidateNotContainsInCandidates = new Candidate("vasya","pupkin","123456789");
        candidateDao.remove(candidateNotContainsInCandidates);
    }

    @Test
    public void testCandidateDaoImplRemoveCandidate2() throws VoterException {
        Candidate candidate = new Candidate("vasya","pupkin","123456789");
        candidateDao.insert(candidate);
        assertEquals(1, dataBase.getCandidates().size());

        candidateDao.remove(candidate);
        assertEquals(0, dataBase.getCandidates().size());
    }

    @Test
    public void testCandidateDaoImplRemoveCandidate3() {
        try {
            Candidate candidateNotContainsInCandidates = new Candidate("vasya","pupkin","123456789");
            candidateDao.remove(candidateNotContainsInCandidates);
            fail();
        } catch (VoterException ex) {
            assertEquals(VoterErrorCode.CANDIDATE_NOT_FOUND_BY_ID, ex.getVoterErrorCode());
        }
    }

    @Test
    public void testVoterDaoImplInsertTokenVotedCandidate1() throws VoterException {
        Candidate candidate = new Candidate("ivan","ivanov","123456789");
        candidateDao.insert(candidate);
        assertEquals(1, candidateDao.getCandidates().size());

        candidateDao.insertVotedCandidate(candidate);
        assertFalse(candidateDao.getVotedCandidates().isEmpty());
        assertEquals(1, candidateDao.getVotedCandidates().size());
    }

    @Test
    public void testVoterDaoImplInsertTokenVotedCandidate2() {
        try{
            Candidate candidate = new Candidate("ivan","ivanov","123456789");
            candidateDao.insert(candidate);
            assertEquals(1, candidateDao.getCandidates().size());

            candidateDao.insertVotedCandidate(candidate);
            assertFalse(candidateDao.getVotedCandidates().isEmpty());
            assertEquals(1, candidateDao.getVotedCandidates().size());

            candidateDao.insertVotedCandidate(candidate);
            fail();
        } catch (VoterException ex) {
            assertEquals(VoterErrorCode.CANDIDATE_CANNOT_VOTE_TWICE, ex.getVoterErrorCode());
        }
    }

    @Test(expected = VoterException.class)
    public void testVoterDaoImplInsertTokenVotedCandidateShouldThrowException() throws VoterException {
        Candidate candidate = new Candidate("ivan","ivanov","123456789");
        candidateDao.insert(candidate);
        assertEquals(1, candidateDao.getCandidates().size());

        candidateDao.insertVotedCandidate(candidate);
        assertFalse(candidateDao.getVotedCandidates().isEmpty());
        assertEquals(1, candidateDao.getVotedCandidates().size());

        candidateDao.insertVotedCandidate(candidate);
    }
}
