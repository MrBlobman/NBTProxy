/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 MrBlobman
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.github.mrblobman.nbt;

import java.util.Set;
import java.util.UUID;

/**
 * An NBTCompoundTag is a mapping of String -&gt; NBTBaseTag. It is a special
 * {@link NBTBaseTag} in the sense that its value (returned by {@link NBTBaseTag#get()})
 * is itself. This is generally the top level tag for most operations.
 */
public abstract class NBTCompoundTag extends NBTBaseTag<NBTCompoundTag> {

    /**
     * See: {@link TagFactory#newCompoundTag()}
     * @param handle the nms tag to wrap
     */
    protected NBTCompoundTag(Object handle) {
        super(handle);
    }

    public final String prettyPrint() {
        StringBuilder out = new StringBuilder("{\n");
        this.prettyPrint(out, 0);
        out.append("\n}");

        return out.toString();
    }

    private void prettyPrint(StringBuilder out, int tabDepth) {
        boolean isFirst = true;
        for (String key : this.keys()) {
            NBTBaseTag tag = getTag(key);

            for (int i = 0; i < tabDepth; i++) out.append("  ");
            if (isFirst) {
                isFirst = false;
                out.append(",\n");
            }
            out.append('"').append(key).append(": ");

            if (tag.isCompound()) {
                ((NBTCompoundTag) tag).prettyPrint(out, tabDepth + 1);
            } else {
                out.append(tag.toString());
            }
        }
    }

    /**
     * Put a new mapping into this tag.
     * @param key the key of the mapping that can be used later to retrieve the
     *            {@code value} via one of the get methods.
     * @param value the value to put at the specified key
     */
    public abstract void put(String key, NBTBaseTag value);

    /**
     * Put all of the mappings from {@code tag} into this tag.
     * @param tag the
     */
    public abstract void putAll(NBTCompoundTag tag);

    /**
     * Retrieve a value from this map at the given key.
     * @param key the key that the value is located at.
     * @param type the expected type of the value tag.
     * @param <T> the return type of the value tag
     * @return the value represented by the tag at the key. null if the
     * mapping does not exist or is of a different type then specified.
     */
    public abstract <T> T get(String key, NBTType<T> type);

    /**
     * Retrieve a tag from this map at the given key.
     * @param key the key that the desired value is located at.
     * @return the tag at the given key or null if there is no mapping
     * for the given key.
     */
    public abstract NBTBaseTag getTag(String key);

    /**
     * Remove the mapping for the given key.
     * <b>Remove UUID tags with:</b> {@link #removeUUID(String)}.
     * @param key the key of the mapping to remove from this tag.
     */
    public abstract void remove(String key);

    /**
     * Check if the key is a mapping to a non-null value in this map.
     * @param key the key who's existence is being checked
     * @return true if the key exists in the map and {@link #getTag(String)}
     * will not return null for the checked key.
     */
    public abstract boolean hasKey(String key);

    /**
     * Check if the key is a mapping to a non-null value of the given
     * type.
     * @param key the key who's existence is being checked.
     * @param type the type that the key should point to
     * @return true if the key exists in the map and {@link #get(String, NBTType)}
     * will not return null for the checked key and type.
     */
    public abstract boolean hasKeyOfType(String key, NBTType type);

    /**
     * @return the number of mappings that exist in this map.
     */
    public abstract int size();

    /**
     * @return a set containing all of the keys that exist in this map
     */
    public abstract Set<String> keys();

    /**
     * Put a boolean in the map at the specified key. Note that
     * there is no boolean tag type, this is represented as a byte.
     * @param key the key pointing to the boolean
     * @param value the value to store
     */
    public abstract void putBoolean(String key, boolean value);

    /**
     * Get a boolean value from this tag at the given key.
     * @param key the key pointing to the boolean to retrieve.
     * @return the value in the map pointed to by {@code key}. This
     * will return false if the key doesn't exist in the map or points
     * to a tag of an incompatible type.
     */
    public abstract boolean getBoolean(String key);

    @Override
    public NBTType<NBTCompoundTag> type() {
        return NBTType.COMPOUND;
    }

    @Override
    public NBTCompoundTag get() {
        return this;
    }

    /**
     * Put a byte in the map that can be retrieved with {@link #getByte(String)}.
     * This will overwrite any existing mappings for the given key.
     * @param key the key at which to store the value
     * @param value the byte value to store.
     */
    public void putByte(String key, byte value) {
        put(key, TagFactory.get().newByteTag(value));
    }

    /**
     * Retrieve a byte value from the map. This is equivalent to calling
     * <pre>
     *     {@link #get(String, NBTType) get}(key, {@link NBTType#BYTE})
     * </pre>
     * but returning a 0 byte instead of null.
     * @param key the key that the byte value was put at.
     * @return the byte value stored at the given key or 0 if they key does not exist
     * or points to a value of a different type
     */
    public byte getByte(String key) {
        Byte value = get(key, NBTType.BYTE);
        return value == null ? 0 : value;
    }

    /**
     * Put a short in the map that can be retrieved with {@link #getShort(String)}.
     * This will overwrite any existing mappings for the given key.
     * @param key the key at which to store the value
     * @param value the short value to store.
     */
    public void putShort(String key, short value) {
        put(key, TagFactory.get().newShortTag(value));
    }

    /**
     * Retrieve a short value from the map. This is equivalent to calling
     * <pre>
     *     {@link #get(String, NBTType) get}(key, {@link NBTType#SHORT})
     * </pre>
     * but returning a 0 short instead of null.
     * @param key the key that the short value was put at.
     * @return the short value stored at the given key or 0 if they key does not exist
     * or points to a value of a different type
     */
    public short getShort(String key) {
        Short value = get(key, NBTType.SHORT);
        return value == null ? 0 : value;
    }

    /**
     * Put an int in the map that can be retrieved with {@link #getInt(String)}.
     * This will overwrite any existing mappings for the given key.
     * @param key the key at which to store the value
     * @param value the int value to store.
     */
    public void putInt(String key, int value) {
        put(key, TagFactory.get().newIntTag(value));
    }

    /**
     * Retrieve a int value from the map. This is equivalent to calling
     * <pre>
     *     {@link #get(String, NBTType) get}(key, {@link NBTType#INT})
     * </pre>
     * but returning a 0 int instead of null.
     * @param key the key that the int value was put at.
     * @return the int value stored at the given key or 0 if they key does not exist
     * or points to a value of a different type
     */
    public int getInt(String key) {
        Integer value = get(key, NBTType.INT);
        return value == null ? 0 : value;
    }

    /**
     * Put a long in the map that can be retrieved with {@link #getLong(String)}.
     * This will overwrite any existing mappings for the given key.
     * @param key the key at which to store the value
     * @param value the long value to store.
     */
    public void putLong(String key, long value) {
        put(key, TagFactory.get().newLongTag(value));
    }

    /**
     * Retrieve a long value from the map. This is equivalent to calling
     * <pre>
     *     {@link #get(String, NBTType) get}(key, {@link NBTType#LONG})
     * </pre>
     * but returning a 0 long instead of null.
     * @param key the key that the long value was put at.
     * @return the long value stored at the given key or 0 if they key does not exist
     * or points to a value of a different type
     */
    public long getLong(String key) {
        Long value = get(key, NBTType.LONG);
        return value == null ? 0 : value;
    }

    /**
     * Put a float in the map that can be retrieved with {@link #getFloat(String)}.
     * This will overwrite any existing mappings for the given key.
     * @param key the key at which to store the value
     * @param value the float value to store.
     */
    public void putFloat(String key, float value) {
        put(key, TagFactory.get().newFloatTag(value));
    }

    /**
     * Retrieve a float value from the map. This is equivalent to calling
     * <pre>
     *     {@link #get(String, NBTType) get}(key, {@link NBTType#FLOAT})
     * </pre>
     * but returning a 0 float instead of null.
     * @param key the key that the float value was put at.
     * @return the float value stored at the given key or 0 if they key does not exist
     * or points to a value of a different type
     */
    public float getFloat(String key) {
        Float value = get(key, NBTType.FLOAT);
        return value == null ? 0 : value;
    }

    /**
     * Put a double in the map that can be retrieved with {@link #getDouble(String)}.
     * This will overwrite any existing mappings for the given key.
     * @param key the key at which to store the value
     * @param value the double value to store.
     */
    public void putDouble(String key, double value) {
        put(key, TagFactory.get().newDoubleTag(value));
    }

    /**
     * Retrieve a double value from the map. This is equivalent to calling
     * <pre>
     *     {@link #get(String, NBTType) get}(key, {@link NBTType#DOUBLE})
     * </pre>
     * but returning a 0 double instead of null.
     * @param key the key that the double value was put at.
     * @return the double value stored at the given key or 0 if they key does not exist
     * or points to a value of a different type
     */
    public double getDouble(String key) {
        Double value = get(key, NBTType.DOUBLE);
        return value == null ? 0 : value;
    }

    /**
     * Put a byte[] in the map that can be retrieved with {@link #getByteArray(String)}.
     * This will overwrite any existing mappings for the given key.
     * @param key the key at which to store the value
     * @param value the byte[] value to store.
     */
    public void putByteArray(String key, byte[] value) {
        put(key, TagFactory.get().newByteArrayTag(value));
    }

    /**
     * Retrieve a byte[] value from the map. This is equivalent to calling
     * <pre>
     *     {@link #get(String, NBTType) get}(key, {@link NBTType#BYTE_ARRAY})
     * </pre>
     * but returning an empty byte array instead of null.
     * @param key the key that the byte[] value was put at.
     * @return the byte[] value stored at the given key or an empty byte[] if they key does
     * not exist or points to a value of a different type
     */
    public byte[] getByteArray(String key) {
        byte[] value = get(key, NBTType.BYTE_ARRAY);
        return value == null ? new byte[0] : value;
    }

    /**
     * Put a String in the map that can be retrieved with {@link #getString(String)}.
     * This will overwrite any existing mappings for the given key.
     * @param key the key at which to store the value
     * @param value the String value to store.
     */
    public void putString(String key, String value) {
        put(key, TagFactory.get().newStringTag(value));
    }

    /**
     * Retrieve a String value from the map. This is equivalent to calling
     * <pre>
     *     {@link #get(String, NBTType) get}(key, {@link NBTType#STRING})
     * </pre>
     * but returning an empty String instead of null. ({@code ""})
     * @param key the key that the String value was put at.
     * @return the String value stored at the given key or an empty String if they key does
     * not exist or points to a value of a different type
     */
    public String getString(String key) {
        String value = get(key, NBTType.STRING);
        return value == null ? "" : value;
    }

    /**
     * Put a list of tags in the map that can be retrieved with {@link #getList(String)}.
     * This will overwrite any existing mappings for the given key.
     * @param key the key at which to store the value
     * @param value the list value to store.
     */
    public void putList(String key, NBTListTag value) {
        put(key, value);
    }

    /**
     * Retrieve a list value from the map. This is equivalent to calling
     * <pre>
     *     {@link #get(String, NBTType) get}(key, {@link NBTType#LIST})
     * </pre>
     * but returning a new list instead of null.
     * @param key the key that the list value was put at.
     * @return the list value stored at the given key or a new list if they key does
     * not exist or points to a value of a different type
     */
    public NBTListTag getList(String key) {
        NBTListTag value = get(key, NBTType.LIST);
        return value == null ? NBTType.LIST.newTag().get() : value;
    }

    /**
     * Put a compound tag in the map that can be retrieved with {@link #getCompound(String)}.
     * This will overwrite any existing mappings for the given key.
     * @param key the key at which to store the value
     * @param value the compound value to store.
     */
    public void putCompound(String key, NBTCompoundTag value) {
        put(key, value);
    }

    /**
     * Retrieve a compound value from the map. This is equivalent to calling
     * <pre>
     *     {@link #get(String, NBTType) get}(key, {@link NBTType#COMPOUND})
     * </pre>
     * but returning a new compound instead of null.
     * @param key the key that the compound value was put at.
     * @return the compound value stored at the given key or a new compound if they key does
     * not exist or points to a value of a different type
     */
    public NBTCompoundTag getCompound(String key) {
        NBTCompoundTag value = get(key, NBTType.COMPOUND);
        return value == null ? NBTType.COMPOUND.newTag().get() : value;
    }

    /**
     * Put an int[] in the map that can be retrieved with {@link #getIntArray(String)}.
     * This will overwrite any existing mappings for the given key.
     * @param key the key at which to store the value
     * @param value the int[] value to store.
     */
    public void putIntArray(String key, int[] value) {
        put(key, TagFactory.get().newIntArrayTag(value));
    }

    /**
     * Retrieve a int[] value from the map. This is equivalent to calling
     * <pre>
     *     {@link #get(String, NBTType) get}(key, {@link NBTType#INT_ARRAY})
     * </pre>
     * but returning an empty int array instead of null.
     * @param key the key that the int[] value was put at.
     * @return the int[] value stored at the given key or an empty int[] if they key does
     * not exist or points to a value of a different type
     */
    public int[] getIntArray(String key) {
        int[] value = get(key, NBTType.INT_ARRAY);
        return value == null ? new int[0] : value;
    }

    /**
     * Put a UUID in the map that can be retrieved with {@link #getUUID(String)}.
     * This will overwrite any existing mappings for the given key. This is really
     * just putting 2 longs in the map that can be used to reconstruct the UUID.
     * @see #hasKeyOfUUID(String)
     * @param key the key at which to store the value
     * @param value the UUID value to store.
     */
    public void putUUID(String key, UUID value) {
        put(key + "Most", TagFactory.get().newLongTag(value.getMostSignificantBits()));
        put(key + "Least", TagFactory.get().newLongTag(value.getLeastSignificantBits()));
    }

    /**
     * Retrieve a UUID value from the map. This combines 2 {@link #getLong(String)}
     * calls to build the UUID. If parts of the serialized UUID are missing this method
     * will still return a UUID with 0 for the least significant bits, most significant
     * bits or both. Check the existence of a UUID with {@link #hasKeyOfUUID(String)} to be
     * safe.
     * @param key the key that the UUID value was put at.
     * @return the UUID value stored at the given key or a new list if they key does
     * not exist or points to a value of a different type
     */
    public UUID getUUID(String key) {
        return new UUID(getLong(key + "Most"), getLong(key + "Least"));
    }

    /**
     * Check if there is a UUID stored in this mapping at the given key.
     * @param key the key that the UUID was put into the map with.
     * @return true if there is a UUID in this tag at the given key, false otherwise.
     */
    public boolean hasKeyOfUUID(String key) {
        return hasKeyOfType(key + "Most", NBTType.LONG) && hasKeyOfType(key + "Least", NBTType.LONG);
    }

    /**
     * Remove the mapping for a UUID key. UUIDs are stored as 2
     * longs and so removal must remove multiple tags. Regular tag
     * removal can be accomplished with {@link #remove(String)}.
     * @param key the key of the mapping to remove from this tag.
     */
    public void removeUUID(String key) {
        remove(key + "Most");
        remove(key + "Least");
    }
}
