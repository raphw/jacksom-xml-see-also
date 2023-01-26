package org.example.hierarchy;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.deser.BeanDeserializer;
import com.fasterxml.jackson.databind.ser.BeanSerializer;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.ser.std.BeanSerializerBase;

import java.util.Map;
import java.util.function.Function;

class XmlSeeAlsoSerializerModifier extends BeanSerializerModifier {

    private final PropertyName property;

    private final Function<PropertyName, String> resolver;

    XmlSeeAlsoSerializerModifier(PropertyName property, Function<PropertyName, String> resolver) {
        this.property = property;
        this.resolver = resolver;
    }

    @Override
    public JsonSerializer<?> modifySerializer(SerializationConfig config, BeanDescription description, JsonSerializer<?> serializer) {
        if (serializer instanceof BeanSerializer) {
            Map<Class<?>, PropertyName> types = XmlSeeAlsoModule.typesToProperties(description);
            if (types != null) {
                serializer = XmlSeeAlsoSerializer.wrap((BeanSerializer) serializer, config, property, resolver, types);
            }
        }
        return serializer;
    }
}
