package net.dzikoysk.funnyguilds.ndb;

public abstract class Database implements IDatabase {

    private final String url, username, password;

    public Database(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public String getConnectionURL() {
        return this.url;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }
}
