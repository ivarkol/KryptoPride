package ru.airiva.client;

import java.util.List;

public interface TlgInteraction {

    void auth(String phone) throws Exception;

    void checkCode(String code) throws Exception;

    void logout();

    List<String> getChats();

}
