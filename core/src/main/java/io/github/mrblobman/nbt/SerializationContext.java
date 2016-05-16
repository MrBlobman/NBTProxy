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

import java.util.Map;

/**
 * The SerializationContext contains serializers, deserializers, and the factory used
 * by them all. An {@link NBTSerializer} or {@link NBTDeserializer} can and should delegate
 * serialization of complex types to this context that will find the appropriate {@link NBTSerializer}
 * or {@link NBTDeserializer} for the job. Each should only be responsible for the name of the
 * key that the complex type is at and the primitive fields (including strings and lists of primitives).
 */
public class SerializationContext {
    private Map<Class, NBTSerializer> serializers;
    private Map<Class, NBTDeserializer> deserializers;
    private TagFactory factory;

    /**
     * See: {@link NBTson.NBTsonBuilder} for creating a new {@link NBTson} instance used
     * for serialization and deserialization.
     * @param serializers a mapping of types to their respective serializer
     * @param deserializers a mapping of types to their respective deserializer
     */
    protected SerializationContext(Map<Class, NBTSerializer> serializers, Map<Class, NBTDeserializer> deserializers) {
        this.serializers = serializers;
        this.deserializers = deserializers;
        this.factory = TagFactory.get();
    }

    /**
     * Deserialize the tag into an object of the given type.
     * @param tag the raw data tag
     * @param type the class of the serialized object
     * @param <T> the type of the object represented by the serialized tag
     * @return the deserializer object
     * @throws NBTDeserializationException if a deserializer cannot be found for the given type or
     * the deserializer throws an exception.
     */
    public <T> T deserialize(NBTBaseTag tag, Class<T> type) {
        NBTDeserializer<T> deserializer = deserializers.get(type);
        if (deserializer == null)
            throw new NBTDeserializationException(tag, "No deserializer for "+type.getSimpleName());

        try {
            return deserializer.deserialize(tag, this);
        } catch (NBTDeserializationException e) {
            throw e;
        } catch (Exception e) {
            throw new NBTDeserializationException(tag, "Exception encountered while deserializing a "+type.getSimpleName()+" with "+deserializer.getClass().getSimpleName(), e);
        }
    }

    /**
     * Serialize the given data as an NBTBaseTag.
     * @param data the data to serialize
     * @return the serialized form of the data
     * @throws NBTSerializationException if a serializer for the class of the data cannot be found
     * or an exception is raised during serialization.
     */
    public NBTBaseTag serialize(Object data) {
        NBTSerializer serializer = serializers.get(data.getClass());
        if (serializer == null)
            throw new NBTSerializationException("No serializer for "+data.getClass().getSimpleName());

        try {
            return serializer.serialize(data, this);
        } catch (NBTSerializationException e) {
            throw e;
        } catch (Exception e) {
            throw new NBTSerializationException("Exception encountered while serializing a "+data.getClass().getSimpleName()+" with "+serializer.getClass().getSimpleName(), e);
        }
    }

    /**
     * Serialize the given data as an {@link NBTBaseTag}
     * @param data the data to serialize
     * @param type the class that the serializer is able to serialize
     * @param <T> the type of the serializing data, think of this as the type
     *           to down cast to
     * @param <S> the type of the data, it must be T or a sub class of T
     * @return the serialized form of the data
     * @throws NBTSerializationException if a serializer for the given type is not found or
     * an exception is raised during serialization.
     */
    public <T, S extends T> NBTBaseTag serialize(S data, Class<T> type) {
        NBTSerializer<T> serializer = serializers.get(type);
        if (serializer == null)
            throw new NBTSerializationException("No serializer for "+type.getSimpleName());

        try {
            return serializer.serialize(data, this);
        } catch (NBTSerializationException e) {
            throw e;
        } catch (Exception e) {
            throw new NBTSerializationException("Exception encountered while serializing a "+data.getClass().getSimpleName()+" with "+serializer.getClass().getSimpleName(), e);
        }
    }

    /**
     * Check if this context is able to deserialize the given type.
     * @param type the class of the object to deserialize
     * @return true if this context can attempt to deserialize an object
     * of class {@code type}, false otherwise.
     */
    public boolean canDeserialize(Class type) {
        return deserializers.containsKey(type);
    }

    /**
     * Check if this context is able to serialize the given type.
     * @param type the class of the object to serialize
     * @return true if this context can attempt to serialize an object
     * of class {@code type}, false otherwise.
     */
    public boolean canSerialize(Class type) {
        return serializers.containsKey(type);
    }

    /**
     * Obtain the tag factory used in serialization and deserialization.
     * This method is designed for use by {@link NBTSerializer}s and {@link NBTDeserializer}s
     * as the context is always passed with a serialization or deserialization request.
     * @return the appropriate {@link TagFactory} for wrapping and instantiating tags.
     */
    public TagFactory factory() {
        return this.factory;
    }
}
