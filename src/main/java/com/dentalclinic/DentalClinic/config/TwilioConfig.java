package com.dentalclinic.DentalClinic.config;

import com.twilio.Twilio;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TwilioConfig {

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    public TwilioConfig() {}

    @Value("${twilio.account.sid}")
    public void setAccountSid(String sid) {
        this.accountSid = sid;
        Twilio.init(accountSid, this.authToken);
    }

    @Value("${twilio.auth.token}")
    public void setAuthToken(String token) {
        this.authToken = token;
        Twilio.init(this.accountSid, authToken);
    }
}
