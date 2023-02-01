package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import org.example.hierarchy.XmlSeeAlsoModule;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.*;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.io.StringWriter;

public class WithSeeAlsoAsXml {
    public static void main(String[] args) throws Exception {
        ObjectMapper objectMapper = new XmlMapper()
                .registerModule(XmlSeeAlsoModule.ofXsi())
                .registerModule(new JaxbAnnotationModule());
        String xml = objectMapper.writeValueAsString(new Wrapper());
        System.out.println(xml);
        System.out.println(objectMapper.readValue(xml, Wrapper.class).getBase().getClass().getSimpleName());
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Wrapper {

        @XmlElement(name = "base")
        private Base base = new First();

        public Base getBase() {
            return base;
        }

        public void setBase(Base base) {
            this.base = base;
        }
    }

    @XmlSeeAlso({First.class, Second.class})
    public abstract static class Base { }

    @XmlType(name = "FirstType")
    public static class First extends Base { }

    @XmlType(name = "SecondType")
    public static class Second extends Base { }
}