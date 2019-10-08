package net.thumbtack.school.elections.request;

import net.thumbtack.school.elections.exception.VoterErrorCode;
import net.thumbtack.school.elections.exception.VoterException;

public class RegisterVoterDtoRequest {

    private int id;
    private String firstName;
    private String login;
    private String password;

    public RegisterVoterDtoRequest(int id, String firstName, String login, String password) {
        this.id = id;
        this.firstName = firstName;
        this.login = login;
        this.password = password;
    }

    public RegisterVoterDtoRequest(String firstName, String login, String password) {
        this(0, firstName, login, password);
    }

    public RegisterVoterDtoRequest(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public RegisterVoterDtoRequest() {

    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    protected boolean isEmpty(String value){
        return value == null || value.isEmpty();
    }

    private boolean isEmptyFirstname(){
        return isEmpty(firstName);
    }

    private boolean isEmptyPassword(){
        return isEmpty(password);
    }

    protected boolean isEmptyLogin(){
        return isEmpty(login);
    }

    private boolean isShortPassword(){
        int minPasswordLength = 8;
        return password.length() < minPasswordLength;
    }

    public boolean validate() throws VoterException {
        if(isEmptyFirstname()){
            throw new VoterException(VoterErrorCode.VOTER_WRONG_FIRSTNAME);
        }
        if(isEmptyLogin()){
            throw new VoterException(VoterErrorCode.VOTER_WRONG_LOGIN);
        }
        if(isEmptyPassword()){
            throw new VoterException(VoterErrorCode.VOTER_WRONG_PASSWORD);
        }
        if(isShortPassword()){
            throw new VoterException(VoterErrorCode.VOTER_SHORT_PASSWORD);
        }
        return true;
    }
}
