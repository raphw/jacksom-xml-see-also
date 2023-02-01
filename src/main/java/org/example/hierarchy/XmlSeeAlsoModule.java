package org.example.hierarchy;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.module.SimpleModule;

import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class XmlSeeAlsoModule extends SimpleModule {

    private static final String DEFAULT = "##default";

    public static XmlSeeAlsoModule ofXsi() {
        return new XmlSeeAlsoModule("type", "http://www.w3.org/2001/XMLSchema-instance", true);
    }

    public static XmlSeeAlsoModule ofAtType() {
        return new XmlSeeAlsoModule("@type", null, false);
    }

    public XmlSeeAlsoModule(String name, String namespace, boolean attribute) {
        this(new PropertyName(name, namespace), attribute, PropertyName::toString, new PropertyNameParser());
    }

    public XmlSeeAlsoModule(
            PropertyName property,
            boolean attribute,
            Function<PropertyName, String> serializationResolver,
            Function<String, PropertyName> deserializationResolver
    ) {
        super(XmlSeeAlso.class.getName() + "Module");
        boolean supportsXml;
        try {
            Class.forName("com.fasterxml.jackson.dataformat.xml.ser.XmlBeanSerializer");
            supportsXml = true;
        } catch (ClassNotFoundException e) {
            supportsXml = false;
        }
        setSerializerModifier(supportsXml
                ? new XmlSeeAlsoSerializerModifierWithXmlSupport(property, attribute, serializationResolver)
                : new XmlSeeAlsoSerializerModifier(property, serializationResolver));
        setDeserializerModifier(new XmlSeeAlsoDeserializerModifier(property, deserializationResolver));
    }

    static Map<Class<?>, PropertyName> typesToProperties(BeanDescription description) {
        Map<Class<?>, PropertyName> types = new LinkedHashMap<>();
        if (types(description, types::put)) {
            return types;
        } else {
            return null;
        }
    }

    static Map<PropertyName, Class<?>> propertiesToTypes(BeanDescription description) {
        Map<PropertyName, Class<?>> types = new LinkedHashMap<>();
        if (types(description, (type, property) -> types.put(property, type))) {
            return types;
        } else {
            return null;
        }
    }

    private static boolean types(BeanDescription description, BiConsumer<Class<?>, PropertyName> consumer) {
        XmlSeeAlso annotation = description.getClassAnnotations().get(XmlSeeAlso.class);
        if (annotation == null) {
            return false;
        }
        for (Class<?> value : annotation.value()) {
            XmlType info = value.getAnnotation(XmlType.class);
            String namespace = null;
            if (info == null || info.namespace().equals(DEFAULT)) {
                Package location = value.getDeclaringClass().getPackage();
                if (location != null) {
                    XmlSchema schema = location.getAnnotation(XmlSchema.class);
                    if (schema != null && !schema.namespace().isEmpty()) {
                        namespace = schema.namespace();
                    }
                }
            } else {
                namespace = info.namespace();
            }
            consumer.accept(value, new PropertyName(info == null || info.name().equals(DEFAULT)
                    ? value.getName()
                    : info.name(), namespace));
        }
        return true;
    }
}
