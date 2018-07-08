package ru.airiva.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * @author Ivan
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChannelDto {

    private String id;
    private String username;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
