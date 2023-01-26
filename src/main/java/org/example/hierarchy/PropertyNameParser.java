package org.example.hierarchy;

import com.fasterxml.jackson.databind.PropertyName;

import java.util.function.Function;

public class PropertyNameParser implements Function<String, PropertyName> {

    @Override
    public PropertyName apply(String value) {
        String name, namespace;
        if (value.startsWith("{")) {
            int index = value.lastIndexOf('}');
            if (index == -1) {
                throw new IllegalStateException();
            }
            name = value.substring(index);
            namespace = value.substring(1, index);
        } else {
            name = value;
            namespace = null;
        }
        return new PropertyName(name, namespace);
    }
}
