package org.example.hierarchy;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.deser.AbstractDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;

import java.util.Map;
import java.util.function.Function;

class XmlSeeAlsoDeserializerModifier extends BeanDeserializerModifier {

    private final PropertyName property;

    private final Function<String, PropertyName> resolver;

    public XmlSeeAlsoDeserializerModifier(PropertyName property, Function<String, PropertyName> resolver) {
        this.property = property;
        this.resolver = resolver;
    }

    @Override
    public JsonDeserializer<?> modifyDeserializer(DeserializationConfig config, BeanDescription description, JsonDeserializer<?> deserializer) {
        if (deserializer instanceof AbstractDeserializer) {
            Map<PropertyName, Class<?>> types = XmlSeeAlsoModule.propertiesToTypes(description);
            if (types != null) {
                deserializer = new XmlSeeAlsoDeserializer(description, property, resolver, types);
            }
        }
        return deserializer;
    }
}
