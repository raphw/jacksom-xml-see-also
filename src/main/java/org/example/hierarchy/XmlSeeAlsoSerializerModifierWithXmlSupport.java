package org.example.hierarchy;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanSerializer;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;

import java.util.Map;
import java.util.function.Function;

class XmlSeeAlsoSerializerModifierWithXmlSupport extends BeanSerializerModifier {

    private final PropertyName property;

    private final boolean attribute;

    private final Function<PropertyName, String> resolver;

    XmlSeeAlsoSerializerModifierWithXmlSupport(PropertyName property, boolean attribute, Function<PropertyName, String> resolver) {
        this.property = property;
        this.attribute = attribute;
        this.resolver = resolver;
    }

    @Override
    public JsonSerializer<?> modifySerializer(SerializationConfig config, BeanDescription description, JsonSerializer<?> serializer) {
        if (serializer instanceof BeanSerializer) {
            Map<Class<?>, PropertyName> types = XmlSeeAlsoModule.typesToProperties(description);
            if (types != null) {
                serializer = XmlSeeAlsoSerializerWithXmlSupport.wrap((BeanSerializer) serializer, config, property, attribute, resolver, types);
            }
        }
        return serializer;
    }
}
