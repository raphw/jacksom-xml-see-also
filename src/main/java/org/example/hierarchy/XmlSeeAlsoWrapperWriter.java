package org.example.hierarchy;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.ser.VirtualBeanPropertyWriter;
import com.fasterxml.jackson.databind.util.Annotations;

import java.util.Map;
import java.util.function.Function;

class XmlSeeAlsoWrapperWriter extends VirtualBeanPropertyWriter {

    private final Function<PropertyName, String> resolver;

    private final Map<Class<?>, PropertyName> types;

    XmlSeeAlsoWrapperWriter(
            BeanPropertyDefinition definition,
            Annotations annotations,
            JavaType type,
            Function<PropertyName, String> resolver,
            Map<Class<?>, PropertyName> types,
            Map.Entry<?, ?> attribute
    ) {
        super(definition, annotations, type);
        this.resolver = resolver;
        this.types = types;
        if (attribute != null) {
            setInternalSetting(attribute.getKey(), attribute.getValue());
        }
    }

    @Override
    protected Object value(Object bean, JsonGenerator generator, SerializerProvider provided) {
        PropertyName name = types.get(bean.getClass());
        if (name == null) {
            throw new IllegalStateException(bean.getClass() + " is not mapped within " + types.keySet());
        }
        return resolver.apply(name);
    }

    @Override
    public VirtualBeanPropertyWriter withConfig(MapperConfig<?> config, AnnotatedClass declaringClass, BeanPropertyDefinition propDef, JavaType type) {
        throw new UnsupportedOperationException();
    }
}
