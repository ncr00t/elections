package net.thumbtack.school.elections.server;

import com.google.gson.Gson;
import net.thumbtack.school.elections.database.DataBase;
import net.thumbtack.school.elections.exception.VoterErrorCode;
import net.thumbtack.school.elections.exception.VoterException;
import net.thumbtack.school.elections.service.CandidateService;
import net.thumbtack.school.elections.service.ElectionService;
import net.thumbtack.school.elections.service.OfferService;
import net.thumbtack.school.elections.service.VoterService;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;

public class Server {

    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);
    private VoterService voterService;
    private CandidateService candidateService;
    private ElectionService electionService;
    private OfferService offerService;
    private boolean isStartedVoting = false;
    private static boolean isRunServer = false;

    public Server() {
        voterService = new VoterService();
        candidateService = new CandidateService();
        electionService = new ElectionService();
        offerService = new OfferService();
    }

    public static void main(String[] args)  {
        try {
            parseCommandLine(args);
        } catch (IOException ioe) {
            LOGGER.info("Can't parse command line {}", ioe);
        } catch (ParseException pe) {
            LOGGER.info("Can't parse command line {}", pe);
        }catch (VoterException ve) {
            LOGGER.info("Can't parse command line {}", ve);
        }
    }

    public static void parseCommandLine(String[] args) throws IOException, ParseException, VoterException {
        int amountMaxArguments = 3;
        if(args.length > amountMaxArguments){
            throw new ParseException("Amount arguments should not be more " + amountMaxArguments);
        }
        Options options = new Options();
        options.addOption("l",true, "File name for load data");
        options.addOption("s",true, "File name for save data");
        options.addOption("h",false, "Show all commands (helper)");

        CommandLineParser parser = new DefaultParser();
        CommandLine commandLine = parser.parse(options, args);
        if(commandLine.hasOption("l")){
            startServer(commandLine.getOptionValue("l"));
        }else {
            startServer(null);
        }
        if(commandLine.hasOption("s")){
            stopServer(commandLine.getOptionValue("s"));
        }else {
            stopServer(null);
        }
        if(commandLine.hasOption("h")){
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("CommandLineParameters", options);
        }
    }

    public static DataBase startServer(String savedDataFileName) throws IOException {
        isRunServer = true;
        DataBase dataBase;
        if(savedDataFileName != null){
            File file = new File(savedDataFileName);
            if(file.isDirectory()){
                throw new FileNotFoundException("File is directory");
            }
            try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(savedDataFileName)))){
               dataBase = new Gson().fromJson(reader.readLine(), DataBase.class);
            }
        }else {
               dataBase = DataBase.getDataBase();
               dataBase.getVoters().clear();
               dataBase.getCandidates().clear();
               dataBase.getTokensAndVoters().clear();
               dataBase.getRankedCandidates().clear();
               dataBase.getVotedAgainstAll().clear();
               dataBase.getVotedCandidates().clear();
               dataBase.getVotedVoters().clear();
        }
        return dataBase;
    }

    public static void stopServer(String savedDataFileName) throws IOException, VoterException {
        if(!isRunServer){
            throw new VoterException(VoterErrorCode.SERVER_IS_NOT_RUNNING);
        }
        if(savedDataFileName != null) {
            File file = new File(savedDataFileName);
            if(file.isDirectory()){
                throw new FileNotFoundException("File is directory");
            }
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(savedDataFileName)))) {
                DataBase dataBase = DataBase.getDataBase();
                String jsonDataBase = new Gson().toJson(dataBase);
                writer.write(jsonDataBase);
            } finally {
                isRunServer = false;
            }
        }
    }

    public String registerVoter(String requestJsonString) throws VoterException{
        if(!isRunServer){
            throw new VoterException(VoterErrorCode.SERVER_IS_NOT_RUNNING);
        }
        if(isStartedVoting){
            throw new VoterException(VoterErrorCode.ACTION_NOT_ALLOWED_WHEN_VOTING_STARTED);
        }
        return voterService.registerVoter(requestJsonString);
    }

    public String addCandidate(String requestJsonString) throws VoterException {
        if(!isRunServer){
            throw new VoterException(VoterErrorCode.SERVER_IS_NOT_RUNNING);
        }
        if(isStartedVoting){
            throw new VoterException(VoterErrorCode.ACTION_NOT_ALLOWED_WHEN_VOTING_STARTED);
        }
        return candidateService.addCandidate(requestJsonString);
    }

    public String removeCandidate(String requestJsonString) throws VoterException {
        if(!isRunServer){
            throw new VoterException(VoterErrorCode.SERVER_IS_NOT_RUNNING);
        }
        if(isStartedVoting){
            throw new VoterException(VoterErrorCode.ACTION_NOT_ALLOWED_WHEN_VOTING_STARTED);
        }
        return candidateService.removeCandidate(requestJsonString);
    }

    public String getCandidates(String requestJsonString) throws VoterException {
        if(!isRunServer){
            throw new VoterException(VoterErrorCode.SERVER_IS_NOT_RUNNING);
        }
        return candidateService.getCandidates(requestJsonString);
    }

    public String logout(String requestJsonString) throws VoterException {
        if(!isRunServer){
            throw new VoterException(VoterErrorCode.SERVER_IS_NOT_RUNNING);
        }
        return voterService.logout(requestJsonString);
    }

    public String login(String requestJsonString) throws VoterException {
        if(!isRunServer){
            throw new VoterException(VoterErrorCode.SERVER_IS_NOT_RUNNING);
        }
        if(isStartedVoting){
            throw new VoterException(VoterErrorCode.ACTION_NOT_ALLOWED_WHEN_VOTING_STARTED);
        }
        return voterService.login(requestJsonString);
    }

    public String addOffer(String requestJsonString) throws VoterException {
        if(!isRunServer){
            throw new VoterException(VoterErrorCode.SERVER_IS_NOT_RUNNING);
        }
        if(isStartedVoting){
            throw new VoterException(VoterErrorCode.ACTION_NOT_ALLOWED_WHEN_VOTING_STARTED);
        }
        return offerService.addOffer(requestJsonString);
    }

    public String addOfferToElectionProgram(String requestJsonString) throws VoterException {
        if(!isRunServer){
            throw new VoterException(VoterErrorCode.SERVER_IS_NOT_RUNNING);
        }
        if(isStartedVoting){
            throw new VoterException(VoterErrorCode.ACTION_NOT_ALLOWED_WHEN_VOTING_STARTED);
        }
        return offerService.addOfferToElectionProgram(requestJsonString);
    }

    public String removeOfferFromElectionProgram(String requestJsonString) throws VoterException {
        if(!isRunServer){
            throw new VoterException(VoterErrorCode.SERVER_IS_NOT_RUNNING);
        }
        if(isStartedVoting){
            throw new VoterException(VoterErrorCode.ACTION_NOT_ALLOWED_WHEN_VOTING_STARTED);
        }
        return offerService.removeOfferFromElectionProgram(requestJsonString);
    }

    public String changeOfferRating(String requestJsonString) throws VoterException {
        if(!isRunServer){
            throw new VoterException(VoterErrorCode.SERVER_IS_NOT_RUNNING);
        }
        if(isStartedVoting){
            throw new VoterException(VoterErrorCode.ACTION_NOT_ALLOWED_WHEN_VOTING_STARTED);
        }
        return offerService.changeOfferRating(requestJsonString);
    }

    public String removeOfferRating(String requestJsonString) throws VoterException {
        if(!isRunServer){
            throw new VoterException(VoterErrorCode.SERVER_IS_NOT_RUNNING);
        }
        if(isStartedVoting){
            throw new VoterException(VoterErrorCode.ACTION_NOT_ALLOWED_WHEN_VOTING_STARTED);
        }
        return offerService.removeOfferRating(requestJsonString);
    }

    public String getCandidatesAndPrograms(String requestJsonString) throws VoterException {
        if(!isRunServer){
            throw new VoterException(VoterErrorCode.SERVER_IS_NOT_RUNNING);
        }
        return candidateService.getCandidatesAndPrograms(requestJsonString);
    }

    public String getOffersAndAverageRatings(String requestJsonString) throws VoterException {
        if(!isRunServer){
            throw new VoterException(VoterErrorCode.SERVER_IS_NOT_RUNNING);
        }
        return offerService.getOffersAndAverageRatings(requestJsonString);
    }

    public String getOffersSortedByAverageRatings(String requestJsonString) throws VoterException {
        if(!isRunServer){
            throw new VoterException(VoterErrorCode.SERVER_IS_NOT_RUNNING);
        }
        return offerService.getOffersSortedByAverageRatings(requestJsonString);
    }

    public String voteForCandidate(String requestJsonString) throws VoterException {
        if(!isRunServer){
            throw new VoterException(VoterErrorCode.SERVER_IS_NOT_RUNNING);
        }
        if(!isStartedVoting) {
            isStartedVoting = true;
        }
        return electionService.voteForCandidate(requestJsonString);
    }

    public String voteAgainstAll(String requestJsonString) throws VoterException {
        if(!isRunServer){
            throw new VoterException(VoterErrorCode.SERVER_IS_NOT_RUNNING);
        }
        if(!isStartedVoting) {
            isStartedVoting = true;
        }
        return electionService.voteAgainstAll(requestJsonString);
    }

    public String getElectionResults(String requestJsonString) throws VoterException {
        if(!isRunServer){
            throw new VoterException(VoterErrorCode.SERVER_IS_NOT_RUNNING);
        }
        if(!isStartedVoting){
            throw new VoterException(VoterErrorCode.VOTING_HAS_NOT_STARTED);
        }
        String response = electionService.getElectionResults(requestJsonString);
        isStartedVoting = false;
        return response;
    }
}
