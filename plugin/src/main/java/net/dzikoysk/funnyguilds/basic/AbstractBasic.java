package net.dzikoysk.funnyguilds.basic;

public abstract class AbstractBasic implements Basic {

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
