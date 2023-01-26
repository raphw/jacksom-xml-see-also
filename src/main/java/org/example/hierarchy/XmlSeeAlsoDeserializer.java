package org.example.hierarchy;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.PropertyMetadata;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.deser.AbstractDeserializer;
import com.fasterxml.jackson.databind.introspect.*;
import com.fasterxml.jackson.databind.util.SimpleBeanPropertyDefinition;

import java.io.IOException;
import java.util.Map;
import java.util.function.Function;

public class XmlSeeAlsoDeserializer extends AbstractDeserializer {

    private final BeanDescription description;

    private final PropertyName property;

    private final Function<String, PropertyName> resolver;

    private final Map<PropertyName, Class<?>> types;

    XmlSeeAlsoDeserializer(
            BeanDescription description,
            PropertyName property,
            Function<String, PropertyName> resolver,
            Map<PropertyName, Class<?>> types
    ) {
        super(description);
        this.description = description;
        this.property = property;
        this.resolver = resolver;
        this.types = types;
    }

    @Override
    public Object deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        if (!parser.nextFieldName().equals(property.getSimpleName())) {
            throw new IllegalStateException();
        }
        PropertyName resolved = resolver.apply(parser.nextTextValue());
        Class<?> type = types.get(resolved);
        if (type == null) {
            throw new IllegalStateException("No mapping found for " + resolved + " within " + types.keySet());
        }
        if (parser.nextToken().isStructEnd()) {
            return context.getFactory().findValueInstantiator(context, BasicBeanDescription.forOtherUse(
                    context.getConfig(),
                    context.constructType(type),
                    AnnotatedClassResolver.resolveWithoutSuperTypes(context.getConfig(), type)
            )).createUsingDefault(context);
        } else {
            return parser.getCodec().readValue(parser, type);
        }
    }
}
