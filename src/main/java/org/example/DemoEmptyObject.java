package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;

public class DemoEmptyObject {

    public Bar bar = new Bar();

    public static void main(String[] args) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        DemoEmptyObject foo = objectMapper.readValue("{\"bar\":{}}", DemoEmptyObject.class);
        System.out.println("Creates empty object? " + (foo.bar != null));
    }

    public static class Bar { }
}
