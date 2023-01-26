package org.example.hierarchy;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.deser.AbstractDeserializer;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.AnnotatedClassResolver;
import com.fasterxml.jackson.databind.introspect.BasicBeanDescription;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;

import java.io.IOException;
import java.util.List;
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
            JavaType constructed = context.getConfig().constructType(type);
            return context.getFactory().findValueInstantiator(context, new TypeInformationBeanDescription(
                    context.getConfig(),
                    constructed,
                    AnnotatedClassResolver.resolve(context.getConfig(), constructed, context.getConfig()),
                    description.findProperties()
            )).createUsingDefault(context);
        } else {
            return parser.getCodec().readValue(parser, type);
        }
    }

    private static class TypeInformationBeanDescription extends BasicBeanDescription {
        private TypeInformationBeanDescription(
                MapperConfig<?> config,
                JavaType type,
                AnnotatedClass classDef,
                List<BeanPropertyDefinition> props
        ) {
            super(config, type, classDef, props);
        }
    }
}
