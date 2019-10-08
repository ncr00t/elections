package net.thumbtack.school.elections.daoimpl;

import net.thumbtack.school.elections.dao.CandidateDao;
import net.thumbtack.school.elections.database.DataBase;
import net.thumbtack.school.elections.exception.VoterException;
import net.thumbtack.school.elections.model.Candidate;
import net.thumbtack.school.elections.model.Offer;
import net.thumbtack.school.elections.model.Voter;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

public class CandidateDaoImpl implements CandidateDao {

    private DataBase dataBase;

    public CandidateDaoImpl() {
        dataBase = DataBase.getDataBase();
    }

    @Override
    public void remove(Candidate candidate) throws VoterException {
        dataBase.removeCandidate(candidate);
    }

    @Override
    public String getTokenByLogin(String login) {
        return dataBase.getTokenByCandidateLogin(login);
    }

    @Override
    public Candidate getCandidateByToken(String token) throws VoterException {
        return dataBase.getCandidateByToken(token);
    }

    @Override
    public Map<Integer, Candidate> getCandidates() {
        return dataBase.getCandidates();
    }

    @Override
    public Set<Candidate> getVotedCandidates() {
        return dataBase.getVotedCandidates();
    }

    @Override
    public SortedSet<Candidate> getRankedCandidates() {
        return dataBase.getRankedCandidates();
    }

    @Override
    public Candidate getCandidateById(int id) {
        return dataBase.getCandidateById(id);
    }

    @Override
    public void insert(Candidate candidate) throws VoterException {
        dataBase.insertCandidate(candidate);
    }

    @Override
    public void insertVotedCandidate(Candidate candidate) throws VoterException {
        dataBase.insertVotedCandidate(candidate);
    }

    @Override
    public void insertToRankedCandidates(Candidate candidate) {
        dataBase.insertToRankedCandidates(candidate);
    }

    @Override
    public Map<Candidate, Set<Offer>> getCandidatesAndPrograms() {
        return dataBase.getCandidatesAndPrograms();
    }

    @Override
    public Voter getCandidateByLogin(String login) throws VoterException {
        return dataBase.getCandidateByLogin(login);
    }
}
