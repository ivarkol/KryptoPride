package ru.airiva.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Ivan
 */
@Component
public class Timeouts {

    @Value("${auth.timeout}")
    public long auth;

    @Value("${auth.check.timeout}")
    public long authCheck;

    @Value("${code.check.timeout}")
    public long codeCheck;

    @Value("${logout.timeout}")
    public long logout;

    @Value("${code.resend.timeout}")
    public long codeResend;

    @Value("${chats.get.timeout}")
    public long getChats;
}
