package net.dzikoysk.funnyguilds.ndb;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import net.dzikoysk.funnyguilds.ndb.query.DatabaseQuery;
import net.dzikoysk.funnyguilds.ndb.query.ReadQuery;
import net.dzikoysk.funnyguilds.ndb.query.WriteQuery;

public class DatabaseThread extends Thread {

    private static DatabaseThread instance;
    private final List<DatabaseQuery> queries = new CopyOnWriteArrayList<>();

    public DatabaseThread() {
        super("FunnyGuilds Database Thread");

        instance = this;
    }

    @Override
    public void run() {
        while (true) {
            synchronized (this.queries) {
                for (int i = 0; i < this.queries.size(); i++)
                    this.handleQuery(this.queries.get(i));
            }
        }
    }

    private void handleQuery(DatabaseQuery query) {
        Database database = null; // TODO

        if (query instanceof ReadQuery)
            database.read((ReadQuery)query);
        else if (query instanceof WriteQuery)
            database.write((WriteQuery)query);

        this.queries.remove(query);
    }

    public static void registerQuery(DatabaseQuery query) {
        instance.queries.add(query);
    }

    public static List<DatabaseQuery> getQueries() {
        return instance.queries;
    }
}
