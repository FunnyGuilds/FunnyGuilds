package net.dzikoysk.funnyguilds.basic;

public abstract class AbstractBasic implements Basic {

    private boolean changed = true;

    @Override
    public void changes() {
        this.changed = true;
    }

    @Override
    public boolean changed() {
        boolean changedState = this.changed;

        if (changedState) {
            this.changed = false;
        }

        return changedState;
    }

}
