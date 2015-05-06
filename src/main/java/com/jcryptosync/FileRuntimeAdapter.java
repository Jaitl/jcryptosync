package com.jcryptosync;

import com.google.gson.*;
import com.jcryptosync.container.domain.BaseFile;

import java.lang.reflect.Type;

public class FileRuntimeAdapter implements JsonSerializer<BaseFile>, JsonDeserializer<BaseFile> {
    @Override
    public JsonElement serialize(BaseFile src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.add("type", new JsonPrimitive(src.getClass().getSimpleName()));
        result.add("properties", context.serialize(src, src.getClass()));

        return result;
    }

    @Override
    public BaseFile deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

            JsonObject jsonObject = json.getAsJsonObject();
            String type = jsonObject.get("type").getAsString();
            JsonElement element = jsonObject.get("properties");

            try {
                return context.deserialize(element, Class.forName("com.jcryptosync.container.domain." + type));
            } catch (ClassNotFoundException cnfe) {
                throw new JsonParseException("Unknown element type: " + type, cnfe);
            }
    }
}
