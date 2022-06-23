package net.dzikoysk.funnyguilds.data;

public abstract class AbstractMutableEntity implements MutableEntity {

    private boolean wasChanged = true;

    @Override
    public void markChanged() {
        this.wasChanged = true;
    }

    @Override
    public void markUnchanged() {
        this.wasChanged = false;
    }

    @Override
    public boolean wasChanged() {
        return this.wasChanged;
    }

}
