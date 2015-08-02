package de.tobiaserthal.akgbensheim.data.provider.base;

import android.content.ContentValues;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Map;
import java.util.Set;

public class ContentValuesWrapper {
    private final ContentValues values;

    public ContentValuesWrapper(ContentValues values) {
        this.values = values;
    }

    public ContentValuesWrapper() {
        this.values = new ContentValues();
    }

    public ContentValues values() {
        return values;
    }

    /**
     * Adds a value to the set.
     *
     * @param key the name of the value to put
     * @param value the data for the value to put
     */
    public void put(String key, String value) {
        values.put(key, value);
    }

    /**
     * Adds all values from the passed in ContentValues.
     *
     * @param other the ContentValues from which to copy
     */
    public void putAll(ContentValues other) {
        values.putAll(other);
    }

    /**
     * Adds a value to the set.
     *
     * @param key the name of the value to put
     * @param value the data for the value to put
     */
    public void put(String key, Byte value) {
        values.put(key, value);
    }

    /**
     * Adds a value to the set.
     *
     * @param key the name of the value to put
     * @param value the data for the value to put
     */
    public void put(String key, Short value) {
       values.put(key, value);
    }

    /**
     * Adds a value to the set.
     *
     * @param key the name of the value to put
     * @param value the data for the value to put
     */
    public void put(String key, Integer value) {
        values.put(key, value);
    }

    /**
     * Adds a value to the set.
     *
     * @param key the name of the value to put
     * @param value the data for the value to put
     */
    public void put(String key, Long value) {
        values.put(key, value);
    }

    /**
     * Adds a value to the set.
     *
     * @param key the name of the value to put
     * @param value the data for the value to put
     */
    public void put(String key, Float value) {
        values.put(key, value);
    }

    /**
     * Adds a value to the set.
     *
     * @param key the name of the value to put
     * @param value the data for the value to put
     */
    public void put(String key, Double value) {
        values.put(key, value);
    }

    /**
     * Adds a value to the set.
     *
     * @param key the name of the value to put
     * @param value the data for the value to put
     */
    public void put(String key, Boolean value) {
        values.put(key, value);
    }

    /**
     * Adds a value to the set.
     *
     * @param key the name of the value to put
     * @param value the data for the value to put
     */
    public void put(String key, byte[] value) {
        values.put(key, value);
    }

    /**
     * Adds a null value to the set.
     *
     * @param key the name of the value to make null
     */
    public void putNull(String key) {
        values.putNull(key);
    }

    /**
     * Returns the number of values.
     *
     * @return the number of values
     */
    public int size() {
        return values.size();
    }

    /**
     * Remove a single value.
     *
     * @param key the name of the value to remove
     */
    public void remove(String key) {
        values.remove(key);
    }

    /**
     * Removes all values.
     */
    public void clear() {
        values.clear();
    }

    /**
     * Returns true if this object has the named value.
     *
     * @param key the value to check for
     * @return {@code true} if the value is present, {@code false} otherwise
     */
    public boolean containsKey(String key) {
        return values.containsKey(key);
    }

    /**
     * Gets a value. Valid value types are {@link String}, {@link Boolean}, and
     * {@link Number} implementations.
     *
     * @param key the value to get
     * @return the data for the value
     */
    public Object get(String key) {
        return values.get(key);
    }

    /**
     * Gets a value and converts it to a String.
     *
     * @param key the value to get
     * @return the String for the value
     */
    @Nullable
    public String getString(String key) {
        return values.getAsString(key);
    }

    /**
     * Gets a value and converts it to a String.
     *
     * @param key the value to get
     * @return the String for the value
     */
    @NonNull
    public String getStringOrThrow(String key) {
        String result = getString(key);
        if(result == null) {
            throw new IllegalStateException("column cannot be null. Set this value first before accessing it.");
        }

        return result;
    }

    /**
     * Gets a value and converts it to a Long.
     *
     * @param key the value to get
     * @return the Long value, or null if the value is missing or cannot be converted
     */
    @Nullable
    public Long getLong(String key) {
        return values.getAsLong(key);
    }

    /**
     * Gets a value and converts it to a Long.
     *
     * @param key the value to get
     * @return the Long value, or null if the value is missing or cannot be converted
     */
    @NonNull
    public Long getLongOrThrow(String key) {
        Long result = getLong(key);
        if(result == null) {
            throw new IllegalStateException("column cannot be null. Set this value first before accessing it.");
        }

        return result;
    }

    /**
     * Gets a value and converts it to an Integer.
     *
     * @param key the value to get
     * @return the Integer value, or null if the value is missing or cannot be converted
     */
    @Nullable
    public Integer getInteger(String key) {
        return values.getAsInteger(key);
    }

    /**
     * Gets a value and converts it to an Integer.
     *
     * @param key the value to get
     * @return the Integer value, or null if the value is missing or cannot be converted
     */
    @NonNull
    public Integer getIntegerOrThrow(String key) {
        Integer result = getInteger(key);
        if(result == null) {
            throw new IllegalStateException("column cannot be null. Set this value first before accessing it.");
        }

        return result;
    }

    /**
     * Gets a value and converts it to a Short.
     *
     * @param key the value to get
     * @return the Short value, or null if the value is missing or cannot be converted
     */
    @Nullable
    public Short getShort(String key) {
        return values.getAsShort(key);
    }

    /**
     * Gets a value and converts it to a Short.
     *
     * @param key the value to get
     * @return the Short value, or null if the value is missing or cannot be converted
     */
    @NonNull
    public Short getShortOrThrow(String key) {
        Short result = getShort(key);
        if(result == null) {
            throw new IllegalStateException("column cannot be null. Set this value first before accessing it.");
        }

        return result;
    }

    /**
     * Gets a value and converts it to a Byte.
     *
     * @param key the value to get
     * @return the Byte value, or null if the value is missing or cannot be converted
     */
    @Nullable
    public Byte getByte(String key) {
        return values.getAsByte(key);
    }

    /**
     * Gets a value and converts it to a Byte.
     *
     * @param key the value to get
     * @return the Byte value, or null if the value is missing or cannot be converted
     */
    @NonNull
    public Byte getByteOrThrow(String key) {
        Byte result = getByte(key);
        if(result == null) {
            throw new IllegalStateException("column cannot be null. Set this value first before accessing it.");
        }

        return result;
    }

    /**
     * Gets a value and converts it to a Double.
     *
     * @param key the value to get
     * @return the Double value, or null if the value is missing or cannot be converted
     */
    @Nullable
    public Double getDouble(String key) {
        return values.getAsDouble(key);
    }

    /**
     * Gets a value and converts it to a Double.
     *
     * @param key the value to get
     * @return the Double value, or null if the value is missing or cannot be converted
     */
    @NonNull
    public Double getDoubleOrThrow(String key) {
        Double result = getDouble(key);
        if(result == null) {
            throw new IllegalStateException("column cannot be null. Set this value first before accessing it.");
        }

        return result;
    }

    /**
     * Gets a value and converts it to a Float.
     *
     * @param key the value to get
     * @return the Float value, or null if the value is missing or cannot be converted
     */
    @Nullable
    public Float getFloat(String key) {
        return values.getAsFloat(key);
    }

    /**
     * Gets a value and converts it to a Float.
     *
     * @param key the value to get
     * @return the Float value, or null if the value is missing or cannot be converted
     */
    @NonNull
    public Float getFloatOrThrow(String key) {
        Float result = getFloat(key);
        if(result == null) {
            throw new IllegalStateException("column cannot be null. Set this value first before accessing it.");
        }

        return result;
    }

    /**
     * Gets a value and converts it to a Boolean.
     *
     * @param key the value to get
     * @return the Boolean value, or null if the value is missing or cannot be converted
     */
    @Nullable
    public Boolean getBoolean(String key) {
        return values.getAsBoolean(key);
    }

    /**
     * Gets a value and converts it to a Boolean.
     *
     * @param key the value to get
     * @return the Boolean value, or null if the value is missing or cannot be converted
     */
    @NonNull
    public Boolean getBooleanOrThrow(String key) {
        Boolean result = getBoolean(key);
        if(result == null) {
            throw new IllegalStateException("column cannot be null. Set this value first before accessing it.");
        }

        return result;
    }

    /**
     * Gets a value that is a byte array. Note that this method will not convert
     * any other types to byte arrays.
     *
     * @param key the value to get
     * @return the byte[] value, or null is the value is missing or not a byte[]
     */
    @Nullable
    public byte[] getByteArray(String key) {
        return values.getAsByteArray(key);
    }
    /**
     * Gets a value that is a byte array. Note that this method will not convert
     * any other types to byte arrays.
     *
     * @param key the value to get
     * @return the byte[] value, or null is the value is missing or not a byte[]
     */
    @NonNull
    public byte[] getByteArrayOrThrow(String key) {
        byte[] result = getByteArray(key);
        if(result == null) {
            throw new IllegalStateException("column cannot be null. Set this value first before accessing it.");
        }

        return result;
    }

    /**
     * Returns a set of all of the keys and values
     *
     * @return a set of all of the keys and values
     */
    public Set<Map.Entry<String, Object>> valueSet() {
        return values.valueSet();
    }

    /**
     * Returns a set of all of the keys
     *
     * @return a set of all of the keys
     */
    public Set<String> keySet() {
        return values.keySet();
    }
}
