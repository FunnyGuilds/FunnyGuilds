package net.dzikoysk.funnyguilds.data.database.element;

import net.dzikoysk.funnyguilds.basic.Basic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class SQLTable<T extends Basic> {

    private final ArrayList<SQLElement<T>> sqlElements = new ArrayList<>();
    private final String name;
    private SQLGetAll<T> getAll;
    private int idPrimaryKey = 0;

    public SQLTable(String name) {
        this.name = name;
    }

    public void add(String key, SQLType type, SQLSave<T> sqlSave) {
        sqlElements.add(new SQLElement<>(key, type, -1, false, sqlSave));
    }

    public void add(String key, SQLType type, int size, SQLSave<T> sqlSave) {
        sqlElements.add(new SQLElement<>(key, type, size, false, sqlSave));
    }

    public void add(String key, SQLType type, boolean notNull, SQLSave<T> sqlSave) {
        sqlElements.add(new SQLElement<>(key, type, -1, notNull, sqlSave));
    }

    public void add(String key, SQLType type, int size, boolean notNull, SQLSave<T> sqlSave) {
        sqlElements.add(new SQLElement<>(key, type, size, notNull, sqlSave));
    }

    public void setPrimaryKey(String key) {
        for (int i = 0; i < sqlElements.size(); i++) {
            if (sqlElements.get(i).getKey().equalsIgnoreCase(key)) {
                this.setPrimaryKey(i);
                return;
            }
        }

        this.setPrimaryKey(0);
    }

    public void setPrimaryKey(int idPrimaryKey) {
        this.idPrimaryKey = idPrimaryKey;
    }

    public void setGetAll(SQLGetAll<T> getAll) {
        this.getAll = getAll;
    }

    public SQLElement<T> getPrimaryKey() {
        return sqlElements.get(idPrimaryKey);
    }

    public Collection<T> getGetAll() {
        return getAll.getAll();
    }

    public String getName() {
        return name;
    }

    public String getNameGraveAccent() {
        return "`" + name + "`";
    }

    public ArrayList<SQLElement<T>> getSqlElements() {
        return sqlElements;
    }

    public int getIndexElement(String key) {
        for (int index = 0; index < sqlElements.size(); index++) {
            if (!sqlElements.get(index).getKey().equalsIgnoreCase(key)) {
                continue;
            }

            return index;
        }

        return -1;
    }

    public SQLElement<T> getSQLElement(String key) {
        for (SQLElement<T> element : sqlElements) {
            if (element.getKey().equalsIgnoreCase(key)) {
                return element;
            }
        }

        return null;
    }

    public HashMap<String, Integer> getMapElementsKey() {
        HashMap<String, Integer> elementsMap = new HashMap<>();

        for (int i = 1; i < sqlElements.size() + 1; i++) {
            elementsMap.put(sqlElements.get(i - 1).getKey(), i);
        }

        return elementsMap;
    }
}
