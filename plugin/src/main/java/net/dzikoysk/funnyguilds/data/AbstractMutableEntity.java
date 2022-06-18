package net.dzikoysk.funnyguilds.data;

public abstract class AbstractMutableEntity implements MutableEntity {

    private boolean wasChanged = true;

    @Override
    public void markChanged() {
        this.wasChanged = true;
    }

    @Override
    public boolean wasChanged() {
        boolean changedState = this.wasChanged;

        if (changedState) {
            this.wasChanged = false;
        }

        return changedState;
    }

}
