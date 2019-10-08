package net.thumbtack.school.elections.daoimpl;

import net.thumbtack.school.elections.database.DataBase;
import net.thumbtack.school.elections.exception.VoterErrorCode;
import net.thumbtack.school.elections.exception.VoterException;
import net.thumbtack.school.elections.model.Candidate;
import net.thumbtack.school.elections.model.Voter;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestVoterDaoImpl {

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
        dataBase.getVotedVoters().clear();
        dataBase.getVotedAgainstAll().clear();

    }

    @Test(expected = VoterException.class)
    public void testVoterDaoImpl1() throws VoterException {
        Voter voter1 = new Voter("ivan","ivanov","123456789");
        Voter voter2 = new Voter("ivan","ivanov","123456789");

        voterDao.insert(voter1);
        assertEquals(1, dataBase.getVoters().size());

        voterDao.insert(voter2);
    }

    @Test
    public void testVoterDaoImpl3() throws VoterException {
        Voter voter1 = new Voter(0, "ivan","ivanov","123456789");
        Voter voter2 = new Voter(1, "petr","petrov","123456789");

        voterDao.insert(voter1);
        assertEquals(1, dataBase.getVoters().size());

        voterDao.insert(voter2);
        assertEquals(2, dataBase.getVoters().size());
    }

    @Test
    public void testVoterDaoImpl5() throws VoterException {
        Voter voter1 = new Voter("ivan","ivanov","123456789");
        Voter voter2 = new Voter("ivan","ivanov","123456789");

        voterDao.insert(voter1);
        assertEquals(1, dataBase.getVoters().size());

        try{
            voterDao.insert(voter2);
            fail();
        }catch (VoterException ve){
            assertNotEquals(2, dataBase.getVoters().size());
        }
    }

    @Test
    public void testVoterDaoImplInsertVotedVoter1() throws VoterException { ;
        Voter voter = new Voter("ivan","ivanov","123456789");
        voterDao.insertVotedVoter(voter);
        assertFalse(dataBase.getVotedVoters().isEmpty());
        assertEquals(1, voterDao.getVotedVoters().size());
    }

    @Test
    public void testVoterDaoImplInsertVotedVoter2() {
        voterDao.getVotedVoters().clear();
        try{
            Voter voter1 = new Voter("ivan","ivanov","123456789");
            voterDao.insertVotedVoter(voter1);
            assertFalse(voterDao.getVotedVoters().isEmpty());
            assertEquals(1, voterDao.getVotedVoters().size());

            voterDao.insertVotedVoter(voter1);
            fail();
        } catch (VoterException ex) {
            assertEquals(VoterErrorCode.VOTER_CANNOT_VOTE_TWICE, ex.getVoterErrorCode());
        }
    }

    @Test(expected = VoterException.class)
    public void testVoterDaoImplInsertVotedVoterShouldThrowException() throws VoterException {
        Voter voter = new Voter("ivan","ivanov","123456789");
        voterDao.insertVotedVoter(voter);
        assertFalse(dataBase.getVotedVoters().isEmpty());
        assertEquals(1, dataBase.getVotedVoters().size());

        voterDao.insertVotedVoter(voter);
    }

    @Test
    public void testVoterDaoImplInsertToVotedAgainstAll() throws VoterException {
        Voter voter1 = new Voter("ivan","ivanov","123456789");
        voterDao.insertToVotedAgainstAll(voter1);
        assertFalse(voterDao.getVotedAgainstAll().isEmpty());
        assertEquals(1, voterDao.getVotedAgainstAll().size());
    }

    @Test
    public void testVoterDaoImplInsertToRankedCandidates() throws VoterException {
        Candidate candidate1 = new Candidate("ivan","ivanov","123456789");
        candidateDao.insert(candidate1);
        assertEquals(1, candidateDao.getCandidates().size());
        candidate1.addVote();
        candidate1.addVote();

        Candidate candidate2 = new Candidate("alex","alexandrov","123456789");
        candidateDao.insert(candidate2);
        assertEquals(1, candidateDao.getCandidates().size());
        candidate2.addVote();

        candidateDao.insertToRankedCandidates(candidate1);
        assertFalse(dataBase.getRankedCandidates().isEmpty());
        assertEquals(1, candidateDao.getRankedCandidates().size());

        candidateDao.insertToRankedCandidates(candidate2);
        assertEquals(2, candidateDao.getRankedCandidates().size());

        assertEquals(2, candidateDao.getRankedCandidates().first().getAmountVotes());
        assertEquals(1, candidateDao.getRankedCandidates().last().getAmountVotes());
    }
}
