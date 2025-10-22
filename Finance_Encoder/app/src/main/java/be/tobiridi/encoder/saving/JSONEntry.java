package be.tobiridi.encoder.saving;

/**
 * Represent a JSON entry for a JSON.
 * <br/>
 * Each enum constant have a key name but its default value is {@code null}.
 * <br/>
 * You should set a value before manipulate the JSON entry.
 * @see JSONEntry#setValue(Object)
 */
public enum JSONEntry {
    CATEGORIES("Categories", String[].class),
    PAYMENT_METHODS("PaymentMethods", String[].class),
    NOTION_API_KEY("NOTION_API_KEY", String.class),
    NOTION_DB_ID("NOTION_DB_ID", String.class);

    private String keyName;
    private Class<?> valueType;
    private Object value;

    private JSONEntry(String keyName, Class<?> valueType) {
        this.keyName = keyName;
        this.valueType = valueType;
        this.value = null;
    }

    public String getKeyName() {
        return this.keyName;
    }

    /**
     * Set a value for this enum constant otherwise the default value is {@code null}.
     * The value is a JSON value associate with its JSON key name.
     * @param value The new value to set, assignable with the data type.
     * @throws IllegalArgumentException If the {@code value} is not assignable with the data type.
     * @see JSONEntry#getValueTypeClassName()
     */
    public void setValue(Object value) throws IllegalArgumentException {
        if (value.getClass().isAssignableFrom(this.valueType)) {
            this.value = this.valueType.cast(value);
        }
        else {
            throw new IllegalArgumentException("Cast error, expected data type : " + this.valueType.getTypeName());
        }
    }

    /**
     * Get the default class to cast the value of this enum constant into the right JSON format.
     * @return The class used for cast the value of this enum constant to the right JSON format.
     */
    public String getValueTypeClassName() {
        return this.valueType.getTypeName();
    }

    /**
     * Attempt to retrieve the casted value of this enum constant to the JSON right format.
     * @param castType Any {@code Class<?>} matches with the default casting class type, you should always use the data type from this enum constant.
     * @return The value of this enum constant cast to the right data type.
     * @see JSONEntry#getValueTypeClassName()
     * @throws IllegalArgumentException If the {@code castType} is not assignable with the data type.
     */
    public <T> T getValueAs(Class<T> castType) throws IllegalArgumentException {
        if(castType.isAssignableFrom(this.valueType)) {
            return castType.cast(this.value);
        }
        else {
            throw new IllegalArgumentException("Invalid cast format, expected cast type : " + this.valueType.getTypeName());
        }
    }
}
