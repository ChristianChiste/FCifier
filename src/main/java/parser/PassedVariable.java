package parser;

import enums.DataType;

public class PassedVariable {

    private String identifier;

    private String type;

    private boolean parallel;

    public String getIdentifier() {
        return identifier;
    }

    public String getType() {
        if (this.type.startsWith("java.util.List") || this.type.startsWith("collection")) {
            return DataType.COLLECTION.toString();
        } else if (this.type.startsWith("java.lang.String")) {
            return DataType.STRING.toString();
        } else {
            return "object";
        }

    }

    public boolean isParallel() {
        return parallel;
    }

}
