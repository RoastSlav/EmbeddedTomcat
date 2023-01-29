package Mappers;

import Models.Token;

public interface TokenMapper {
    Token getToken(String userName);

    int addToken(Token token);

    int deleteToken(String userName);
}

