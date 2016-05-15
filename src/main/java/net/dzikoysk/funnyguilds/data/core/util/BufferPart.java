package net.dzikoysk.funnyguilds.data.core.util;

public class BufferPart<T> {

    private final T object;
    private String[] fields;

    public BufferPart(T object, String... fields) {
        this.object = object;
        this.fields = fields;
    }

    public void add(String... fields) {
        String[] array = new String[this.fields.length + fields.length];
        System.arraycopy(this.fields, 0, array, 0, this.fields.length);
        System.arraycopy(fields, this.fields.length, array, this.fields.length, array.length - this.fields.length);
        this.fields = array;
    }

    public T getObject() {
        return this.object;
    }

    public String[] getFields() {
        return this.fields;
    }

    @Override
    public String toString() {
        return this.object.toString();
    }

}
