package com.code.afdn.xml;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.StreamSupport;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import com.code.afdn.Automaton;
import com.code.afdn.State;
import com.code.afdn.StateType;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlLoader {
    private StateType parseStateType(Element parentElement) {
        boolean isInitial = StreamSupport.stream(XmlHelper.iterable(parentElement.getChildNodes()).spliterator(), false)
                .anyMatch(childElement -> childElement.getNodeType() == Node.ELEMENT_NODE
                        && ((Element) childElement).getTagName() == "initial");

        boolean isFinal = StreamSupport.stream(XmlHelper.iterable(parentElement.getChildNodes()).spliterator(), false)
                .anyMatch(childElement -> childElement.getNodeType() == Node.ELEMENT_NODE
                        && ((Element) childElement).getTagName() == "final");

        return isInitial && isFinal ? StateType.Both
                : isInitial ? StateType.Initial : isFinal ? StateType.Final : StateType.Normal;
    }

    private HashMap<Integer, State> parseStateMap(NodeList nodeList) {
        HashMap<Integer, State> stateMap = new HashMap<Integer, State>();

        for (Node parentNode : XmlHelper.iterable(nodeList)) {
            if (parentNode.getNodeType() == Node.ELEMENT_NODE) {
                Element parentElement = (Element) parentNode;

                int id = Integer.parseInt(parentElement.getAttribute("id"));
                String name = parentElement.getAttribute("name");
                StateType type = parseStateType(parentElement);

                stateMap.put(id, new State(id, name, type));
            }
        }

        return stateMap;
    }

    private String getTransitionFrom(NodeList nodeList) {
        Node foundNode = StreamSupport.stream(XmlHelper.iterable(nodeList).spliterator(), false)
                .filter(node -> node.getNodeType() == Node.ELEMENT_NODE && ((Element) node).getTagName() == "from")
                .findFirst().orElse(null);

        return foundNode.getTextContent();
    }

    private String getTransitionTo(NodeList nodeList) {
        Node foundNode = StreamSupport.stream(XmlHelper.iterable(nodeList).spliterator(), false)
                .filter(node -> node.getNodeType() == Node.ELEMENT_NODE && ((Element) node).getTagName() == "to")
                .findFirst().orElse(null);

        return foundNode.getTextContent();
    }

    private String getTransitionRead(NodeList nodeList) {
        Node foundNode = StreamSupport.stream(XmlHelper.iterable(nodeList).spliterator(), false)
                .filter(node -> node.getNodeType() == Node.ELEMENT_NODE && ((Element) node).getTagName() == "read")
                .findFirst().orElse(null);

        return foundNode.getTextContent();
    }

    private Map<Integer, TreeMap<String, List<Integer>>> parseTransitionList(NodeList nodeList) {
        Map<Integer, TreeMap<String, List<Integer>>> transitionList = new TreeMap<Integer, TreeMap<String, List<Integer>>>();

        for (Node parentNode : XmlHelper.iterable(nodeList)) {
            if (parentNode.getNodeType() == Node.ELEMENT_NODE) {
                Element parentElement = (Element) parentNode;
                NodeList children = parentElement.getChildNodes();

                Integer from = Integer.parseInt(getTransitionFrom(children));
                Integer to = Integer.parseInt(getTransitionTo(children));
                String read = getTransitionRead(children);

                if (!transitionList.containsKey(from))
                    transitionList.put(from, new TreeMap<String, List<Integer>>());

                if (!transitionList.get(from).containsKey(read))
                    transitionList.get(from).put(read, new ArrayList<Integer>());

                transitionList.get(from).get(read).add(to);
            }
        }

        return transitionList;
    }

    private String getAlphabet(String text, String letter) {
        String[] textArray = text.split(",");

        if (Arrays.asList(textArray).contains(letter)) {
            return text;
        }

        if (text == "") {
            return letter;
        } else {
            return text + "," + letter;
        }
    }

    private String parseAlphabet(NodeList nodeList) {
        String alphabet = "";

        for (Node node : XmlHelper.iterable(nodeList)) {
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;

                alphabet = getAlphabet(alphabet, element.getTextContent());
            }
        }

        return alphabet;
    }

    public Automaton loadAutomatonFromFile(String filename) {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            Document document = documentBuilder.parse(filename);
            NodeList stateList = document.getElementsByTagName("state");
            NodeList transitionList = document.getElementsByTagName("transition");
            NodeList readList = document.getElementsByTagName("read");

            HashMap<Integer, State> states = parseStateMap(stateList);
            Map<Integer, TreeMap<String, List<Integer>>> transitions = parseTransitionList(transitionList);
            String alphabet = parseAlphabet(readList);

            return new Automaton(states, transitions, alphabet);
        } catch (Exception ex) {
            Logger.getLogger(XmlLoader.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
}
