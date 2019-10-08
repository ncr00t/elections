package net.thumbtack.school.elections.response;

import net.thumbtack.school.elections.service.GenerateTokenService;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestLogoutDtoResponse {

    @Test
    public void testAllCandidatesDtoRequest() {
        RegisterVoterDtoResponse registerVoterDtoResponse1 = new RegisterVoterDtoResponse(GenerateTokenService.generateNewToken());
        String token1 = registerVoterDtoResponse1.getToken();

        LogoutDtoResponse logoutDtoResponse = new LogoutDtoResponse(token1);
        assertEquals(token1, logoutDtoResponse.getToken());

        RegisterVoterDtoResponse registerVoterDtoResponse2 = new RegisterVoterDtoResponse(GenerateTokenService.generateNewToken());
        String token2 = registerVoterDtoResponse2.getToken();

        logoutDtoResponse.setToken(token2);
        assertEquals(token2, logoutDtoResponse.getToken());
    }
}
