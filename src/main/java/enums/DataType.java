package enums;

public enum DataType {

    COLLECTION,
    STRING,
    NUMBER;

    @Override
    public String toString() {
        return name().toLowerCase();
    }

}
