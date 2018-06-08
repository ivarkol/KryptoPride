package ru.airiva.dto;

/**
 * @author Ivan
 */
public class TlgChannelDto {

    private long id;
    private String title;

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public TlgChannelDto(long id, String title) {
        this.id = id;
        this.title = title;
    }

}
