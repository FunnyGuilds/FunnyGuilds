package net.dzikoysk.funnyguilds.util.telemetry;

import java.util.Objects;

public class FunnybinResponse {

    private String fullUrl;
    private String shortUrl;

    public FunnybinResponse() {
    }

    public FunnybinResponse(String fullUrl, String shortUrl) {
        this.fullUrl = fullUrl;
        this.shortUrl = shortUrl;
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
               Objects.equals(this.shortUrl, that.shortUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.fullUrl, this.shortUrl);
    }

    @Override
    public String toString() {
        return "FunnybinResponse{" +
               "fullUrl='" + this.fullUrl + '\'' +
               ", shortUrl='" + this.shortUrl + '\'' +
               '}';
    }
}
