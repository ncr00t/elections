package net.thumbtack.school.elections.service;

import java.util.UUID;

public class GenerateTokenService {
    public static String generateNewToken(){
        return UUID.randomUUID().toString();
    }
}
