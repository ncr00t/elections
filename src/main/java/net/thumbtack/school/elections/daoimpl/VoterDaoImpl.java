package net.thumbtack.school.elections.daoimpl;

import net.thumbtack.school.elections.database.DataBase;
import net.thumbtack.school.elections.dao.VoterDao;
import net.thumbtack.school.elections.exception.VoterException;
import net.thumbtack.school.elections.model.Voter;

import java.util.Map;
import java.util.Set;

public class VoterDaoImpl implements VoterDao {

    private DataBase dataBase;

    public VoterDaoImpl() {
        dataBase = DataBase.getDataBase();
    }

    @Override
    public void insert(Voter voter) throws VoterException {
        dataBase.insertVoter(voter);
    }

    @Override
    public String getTokenByLogin(String login) {
        return dataBase.getTokenByVoterLogin(login);
    }

    @Override
    public Set<Voter> getVotedVoters() {
        return dataBase.getVotedVoters();
    }

    @Override
    public Map<Integer, Voter> getVoters() {
        return dataBase.getVoters();
    }

    @Override
    public Set<Voter> getVotedAgainstAll() {
        return dataBase.getVotedAgainstAll();
    }

    @Override
    public Voter getVoterByToken(String token) throws VoterException {
        return dataBase.getVoterByToken(token);
    }

    @Override
    public void update(Voter voter) throws VoterException {
        dataBase.updateVoter(voter);
    }

    @Override
    public void insertVotedVoter(Voter voter) throws VoterException {
        dataBase.insertVotedVoter(voter);
    }

    @Override
    public <T extends Voter> void insertToVotedAgainstAll(T voter) {
        dataBase.insertToVotedAgainstAll(voter);
    }

    @Override
    public Map<String, Voter> getTokensAndVoters() {
        return dataBase.getTokensAndVoters();
    }

    @Override
    public Voter getVoterById(int id) {
        return dataBase.getVoterById(id);
    }

    @Override
    public Voter getVoterByLogin(String login) throws VoterException {
        return dataBase.getVoterByLogin(login);
    }

    @Override
    public void removeVoter(Voter voter) throws VoterException {
        dataBase.removeVoter(voter);
    }

    @Override
    public void removeTokenByLogin(String login) throws VoterException {
        dataBase.removeTokenByLogin(login);
    }
}

