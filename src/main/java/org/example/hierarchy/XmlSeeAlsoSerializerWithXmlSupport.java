package org.example.hierarchy;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.PropertyMetadata;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotationMap;
import com.fasterxml.jackson.databind.introspect.TypeResolutionContext;
import com.fasterxml.jackson.databind.introspect.VirtualAnnotatedMember;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializer;
import com.fasterxml.jackson.databind.util.SimpleBeanPropertyDefinition;
import com.fasterxml.jackson.dataformat.xml.ser.XmlBeanSerializer;
import com.fasterxml.jackson.dataformat.xml.ser.XmlBeanSerializerBase;
import com.fasterxml.jackson.dataformat.xml.util.XmlInfo;

import java.util.AbstractMap;
import java.util.Map;
import java.util.function.Function;

class XmlSeeAlsoSerializerWithXmlSupport extends XmlBeanSerializer {

    private final PropertyName property;

    private final boolean attribute;

    private final Function<PropertyName, String> resolver;

    private XmlSeeAlsoSerializerWithXmlSupport(BeanSerializer serializer, PropertyName property, boolean attribute, Function<PropertyName, String> resolver) {
        super(serializer);
        this.property = property;
        this.attribute = attribute;
        this.resolver = resolver;
    }

    static JsonSerializer<?> wrap(
            BeanSerializer serializer,
            MapperConfig<?> config,
            PropertyName property,
            boolean attribute,
            Function<PropertyName, String> resolver,
            Map<Class<?>, PropertyName> types
    ) {
        return new XmlSeeAlsoSerializerWithXmlSupport(serializer, property, attribute, resolver).doWrap(config, types);
    }

    JsonSerializer<?> doWrap(MapperConfig<?> config, Map<Class<?>, PropertyName> types) {
        BeanPropertyWriter[] properties = new BeanPropertyWriter[_props == null ? 1 : _props.length + 1];
        if (_props != null) {
            System.arraycopy(_props, 0, properties, 1, _props.length);
        }
        JavaType type = config.constructType(String.class);
        properties[0] = new XmlSeeAlsoWrapperWriter(SimpleBeanPropertyDefinition.construct(
                config,
                new VirtualAnnotatedMember(
                        new TypeResolutionContext.Empty(config.getTypeFactory()),
                        String.class,
                        property.getSimpleName(),
                        type
                ),
                property,
                PropertyMetadata.STD_OPTIONAL,
                JsonInclude.Include.NON_EMPTY
        ), new AnnotationMap(), type, resolver, types, new AbstractMap.SimpleEntry<>(
                XmlBeanSerializerBase.KEY_XML_INFO,
                new XmlInfo(
                        attribute,
                        property.getNamespace(),
                        false,
                        false
                )
        ));
        return withProperties(properties, _filteredProps);
    }
}
