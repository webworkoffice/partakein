package in.partake.model.dao.jpa.dictionary;

import java.sql.Types;

import org.apache.openjpa.jdbc.sql.PostgresDictionary;

public class PartakePostgresDictionary extends PostgresDictionary {

    private static final String TIMESTAMPTYPENAME = "timestamp";

    @Override
    public String getTypeName(int type) {
        if (type == Types.TIMESTAMP) {            
            return TIMESTAMPTYPENAME;
        } else {        
            return super.getTypeName(type);
        }
    }
}
