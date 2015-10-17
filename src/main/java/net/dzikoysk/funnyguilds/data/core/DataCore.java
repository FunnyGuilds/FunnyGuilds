package net.dzikoysk.funnyguilds.data.core;

import net.dzikoysk.funnyguilds.basic.Basic;
import net.dzikoysk.funnyguilds.data.core.database.Database;
import net.dzikoysk.funnyguilds.data.core.flat.Flat;

public class DataCore {

    private static DataCore instance;
    private DataType type;
    private Database database;
    private Flat flat;
    private boolean buffer;

    private DataCore(DataType type) {
        this.type = type;
    }

    public static DataCore getInstance() {
        if (instance == null)
            instance = new DataCore(DataType.FLAT);
        return instance;
    }

    public void load(DataType type) throws Exception {

    }

    public void save(Basic basic, String... fields) {/*
        switch(type){
		case FLAT:
			this.flat.save(basic, fields);
			break;
		case DATABASE:
			this.database.save(basic, fields);
			break;
		case DUO:
			this.flat.save(basic, fields);
			this.database.save(basic, fields);
			break;
		}
	*/
    }

    public void openBuffer() {
        this.buffer = true;
        switch (type) {
            case FLAT:
                this.flat.openBuffer();
                break;
            case DATABASE:
                this.database.openBuffer();
                break;
            case DUO:
                this.flat.openBuffer();
                this.database.openBuffer();
                break;
        }
    }

    public boolean isOpened() {
        return this.buffer;
    }

    public DataType getType() {
        return this.type;
    }

}
