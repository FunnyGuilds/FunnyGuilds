package net.dzikoysk.funnyguilds.telemetry;

import java.util.Objects;

public class FunnybinResponse {

    private String fullUrl;
    private String shortUrl;
    private String uuid;

    public FunnybinResponse() {
    }

    public FunnybinResponse(String fullUrl, String shortUrl, String uuid) {
        this.fullUrl = fullUrl;
        this.shortUrl = shortUrl;
        this.uuid = uuid;
    }

    public String getFullUrl() {
        return this.fullUrl;
    }

    public void setFullUrl(String fullUrl) {
        this.fullUrl = fullUrl;
    }

    public String getShortUrl() {
        return this.shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    public String getUuid() {
        return this.uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        FunnybinResponse that = (FunnybinResponse) o;
        return Objects.equals(this.fullUrl, that.fullUrl) &&
                Objects.equals(this.shortUrl, that.shortUrl) &&
                Objects.equals(this.uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.fullUrl, this.shortUrl, this.uuid);
    }


    @Override
    public String toString() {
        return "FunnybinResponse{" +
                "fullUrl='" + this.fullUrl + '\'' +
                ", shortUrl='" + this.shortUrl + '\'' +
                ", uuid='" + this.uuid + '\'' +
                '}';
    }
}
