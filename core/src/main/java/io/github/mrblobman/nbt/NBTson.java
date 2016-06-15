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

import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * See {@link NBTsonBuilder} for creating a new instance of this class.
 *
 * NBTson supports reading deserializable and writing serializable objects to
 * ItemStacks. ItemStacks support custom NBT tags where as Entities and TileEntities only
 * handle recognizable NBT tags.
 *
 * See the {@link #read(ItemStack, String, Class)} for reading a serialized object from
 * an item and {@link #write(ItemStack, String, Object)} for writing an object to an item.
 */
public class NBTson {
    private SerializationContext context;
    private NBTIODelegate<ItemStack> itemIODelegate;

    private NBTson(NBTIODelegate<ItemStack> itemIODelegate, SerializationContext context) {
        this.itemIODelegate = itemIODelegate;
        this.context = context;
    }

    /**
     * Read an object from the item at the given key.
     * @param item the ItemStack containing the object.
     * @param key the key that the object was originally written to.
     * @param type the class of the object to read.
     * @param <T> the type of the object being deserialized.
     * @return the deserialized object or null if either the object
     * cannot be deserialized or is not on the item.
     */
    public <T> T read(ItemStack item, String key, Class<T> type) {
        NBTCompoundTag itemTag = itemIODelegate.read(item);
        NBTBaseTag dataTag = itemTag.getTag(key);
        if (dataTag == null)
            return null;
        try {
            return context.deserialize(dataTag, type);
        } catch (NBTException e) {
            return null;
        }
    }

    /**
     * Write an object to the itemstack.
     * @param item the ItemStack containing the object.
     * @param key the key that the object is written to. It can be used to
     *            later retrieve the data with {@link #read(ItemStack, String, Class)}
     * @param data the object to write to the item.
     * @throws NBTSerializationException if the object cannot be serialized
     */
    public void write(ItemStack item, String key, Object data) {
        NBTBaseTag tag = context.serialize(data);
        NBTCompoundTag compoundTag = TagFactory.get().newCompoundTag();
        compoundTag.put(key, tag);
        itemIODelegate.append(item, compoundTag);
    }

    /**
     * Following the Builder pattern this class is designed for chaining registration calls
     * for all supported serializers and deserializers that will be required by the constructed
     * {@link NBTson} instance.
     */
    public static class NBTsonBuilder {
        private Map<Class, NBTSerializer> serializers;
        private Map<Class, NBTDeserializer> deserializers;
        private NBTIODelegate<ItemStack> itemIODelegate;

        public NBTsonBuilder() {
            this.serializers = new HashMap<>();
            this.deserializers = new HashMap<>();
        }

        public <T> NBTsonBuilder registerSerializer(Class<T> type, NBTSerializer<T> serializer) {
            serializers.put(type, serializer);
            return this;
        }

        public <T> NBTsonBuilder registerDeserializer(Class<T> type, NBTDeserializer<T> deserializer) {
            deserializers.put(type, deserializer);
            return this;
        }

        public <T> NBTsonBuilder register(Class<T> type, NBTSerializer<T> serializer, NBTDeserializer<T> deserializer) {
            serializers.put(type, serializer);
            deserializers.put(type, deserializer);
            return this;
        }

        public <T, U extends NBTSerializer<T> & NBTDeserializer<T>> NBTsonBuilder register(Class<T> type, U serializer) {
            serializers.put(type, serializer);
            deserializers.put(type, serializer);
            return this;
        }

        public NBTsonBuilder setItemIODelegate(NBTIODelegate<ItemStack> itemIODelegate) {
            this.itemIODelegate = itemIODelegate;
            return this;
        }

        public NBTson build() {
            SerializationContext context = new SerializationContext(serializers, deserializers);
            NBTIODelegate<ItemStack> itemIODelegate = this.itemIODelegate == null ?
                    TagFactory.get().getItemIODelegate() : this.itemIODelegate;
            return new NBTson(itemIODelegate, context);
        }
    }
}
