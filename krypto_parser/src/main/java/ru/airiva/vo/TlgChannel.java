package ru.airiva.vo;

import java.util.Objects;

/**
 * @author Ivan
 */
public class TlgChannel implements Comparable<TlgChannel> {

    public final int id;
    private String title;

    public void setTitle(String title) {
        this.title = title != null ? title : "unknown";
    }

    public String getTitle() {
        return title;
    }

    public TlgChannel(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TlgChannel that = (TlgChannel) o;
        return id == that.id &&
                Objects.equals(title, that.title);
    }


    @Override
    public int compareTo(TlgChannel o) {
        if (!this.title.equals(o.title)) {
            return this.title.compareTo(o.title);
        }
        if (this.id != o.id) {
            return this.id < o.id ? -1 : 1;
        }
        return 0;
    }

}
