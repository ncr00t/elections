package net.thumbtack.school.elections.response;

import net.thumbtack.school.elections.service.GenerateTokenService;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestLoginDtoResponse {

    @Test
    public void testLoginDtoResponse() {
        String newToken = GenerateTokenService.generateNewToken();
        LoginDtoResponse loginDtoResponse = new LoginDtoResponse(newToken);
        assertEquals(newToken, loginDtoResponse.getToken());
    }
}
