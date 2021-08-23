package com.kubeworks.watcher.ecosystem.kubernetes.serdes;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.PackageVersion;
import io.kubernetes.client.custom.IntOrString;

import java.io.IOException;

public final class IntOrStringModule extends SimpleModule {

    private static final long serialVersionUID = 1L;

    public IntOrStringModule() {
        super(PackageVersion.VERSION);
        addSerializer(IntOrString.class, IntOrStringSerializer.INSTANCE);
        addDeserializer(IntOrString.class, IntOrStringDeserializer.INSTANCE);
    }

    public static class IntOrStringSerializer extends JsonSerializer<IntOrString> {

        public static final IntOrStringSerializer INSTANCE = new IntOrStringSerializer();

        @Override
        public void serialize(IntOrString value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            if (value == null) {
                jgen.writeNull();
            } else {
                if (value.isInteger()) {
                    jgen.writeNumber(value.getIntValue());
                } else {
                    jgen.writeString(value.getStrValue());
                }
            }
        }

    }

    public static class IntOrStringDeserializer extends JsonDeserializer<IntOrString> {

        public static final IntOrStringDeserializer INSTANCE = new IntOrStringDeserializer();

        @Override
        public IntOrString deserialize(JsonParser jsonParser, DeserializationContext ctxt)
            throws IOException {
            ObjectCodec oc = jsonParser.getCodec();
            JsonNode node = oc.readTree(jsonParser);
            IntOrString intOrString;
            if (node.isInt()) {
                intOrString = new IntOrString(node.asInt());
            } else {
                intOrString = new IntOrString(node.asText());
            }
            return intOrString;
        }
    }
}
