package com.example.xmlprocessor.service;

import org.springframework.stereotype.Service;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;
import tools.jackson.databind.json.JsonMapper;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class XmlJsonConverter {

    private final JsonMapper jsonMapper = JsonMapper.builder().build();

    public String convert(byte[] content) throws Exception {
        DocumentBuilderFactory factory =
                DocumentBuilderFactory.newInstance();

        factory.setNamespaceAware(true);
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        factory.setFeature(
                "http://apache.org/xml/features/disallow-doctype-decl",
                true);
        factory.setFeature(
                "http://xml.org/sax/features/external-general-entities",
                false);
        factory.setFeature(
                "http://xml.org/sax/features/external-parameter-entities",
                false);

        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        factory.setXIncludeAware(false);
        factory.setExpandEntityReferences(false);

        DocumentBuilder builder = factory.newDocumentBuilder();

        builder.setErrorHandler(new DefaultHandler() {
            @Override
            public void error(SAXParseException exception)
                    throws SAXException {
                throw exception;
            }

            @Override
            public void fatalError(SAXParseException exception)
                    throws SAXException {
                throw exception;
            }
        });

        Document document;

        try (ByteArrayInputStream input =
                     new ByteArrayInputStream(content)) {
            document = builder.parse(input);
        }

        Element root = document.getDocumentElement();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put(root.getTagName(), convertElement(root));

        return jsonMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(result);
    }

    private Object convertElement(Element element) {
        Map<String, Object> result = new LinkedHashMap<>();

        NamedNodeMap attributes = element.getAttributes();

        for (int i = 0; i < attributes.getLength(); i++) {
            Node attribute = attributes.item(i);
            result.put(
                    "@" + attribute.getNodeName(),
                    attribute.getNodeValue());
        }

        Map<String, List<Object>> children = new LinkedHashMap<>();
        StringBuilder text = new StringBuilder();

        NodeList nodes = element.getChildNodes();

        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);

            if (node instanceof Element child) {
                children.computeIfAbsent(
                        child.getTagName(),
                        key -> new ArrayList<>()
                ).add(convertElement(child));

            } else if (node.getNodeType() == Node.TEXT_NODE
                    || node.getNodeType() == Node.CDATA_SECTION_NODE) {
                text.append(node.getNodeValue());
            }
        }

        children.forEach((name, values) ->
                result.put(
                        name,
                        values.size() == 1 ? values.get(0) : values));

        String value = text.toString().trim();

        if (result.isEmpty()) {
            return value;
        }

        if (!value.isEmpty()) {
            result.put("#text", value);
        }

        return result;
    }
}