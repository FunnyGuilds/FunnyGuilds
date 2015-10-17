package net.dzikoysk.funnyguilds.data.core.util;

public class BufforPart<T> {

    private final T object;
    private String[] fields;

    public BufforPart(T object, String... fields) {
        this.object = object;
        this.fields = fields;
    }

    public void add(String... fields) {
        String[] array = new String[this.fields.length + fields.length];
        for (int i = 0; i < this.fields.length; i++)
            array[i] = this.fields[i];
        for (int i = this.fields.length; i < array.length; i++)
            array[i] = fields[i];
        this.fields = array;
    }

    public T getObject() {
        return this.object;
    }

    public String[] getFields() {
        return this.fields;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((object == null) ? 0 : object.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        return obj.equals(this.object);
    }

    @Override
    public String toString() {
        return this.object.toString();
    }

}
