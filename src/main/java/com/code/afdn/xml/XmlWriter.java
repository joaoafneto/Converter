package com.code.afdn.xml;
import java.io.File;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import com.code.afdn.Automaton;
import com.code.afdn.State;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XmlWriter {

    public void createDocument(Automaton automaton, String fileName) {
        Random random = new Random(Calendar.getInstance().getTimeInMillis());

        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.newDocument();
            Element element = document.createElement("structure");

            document.appendChild(element);

            Element type = document.createElement("type");

            type.appendChild(document.createTextNode("fa"));
            element.appendChild(type);

            Element automatonElement = document.createElement("automaton");

            element.appendChild(automatonElement);

            createStates(automaton, random, document, automatonElement);
            createTransitions(automaton, document, automatonElement);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            String currentDirectory = new File(".").getCanonicalPath();

            new File(currentDirectory + "\\final").mkdirs();

            File destinationFile = new File(currentDirectory + "\\final\\" + fileName);
            destinationFile.createNewFile();

            DOMSource sourceXml = new DOMSource(document);
            StreamResult destinationXml = new StreamResult(destinationFile);

            transformer.transform(sourceXml, destinationXml);

            Logger.getLogger(XmlWriter.class.getName())
                    .info("Successfully wrote converted automaton to file with name: " + fileName);
        } catch (Exception e) {
            Logger.getLogger(XmlWriter.class.getName()).warning("Error writing converted automaton to file.");
        }
    }

    private void createTransitions(Automaton automaton, Document document, Element automatonElement) {
        for (Map.Entry<Integer, TreeMap<String, List<Integer>>> transition : automaton.transitions.entrySet()) {
            for (String transitionRead : transition.getValue().keySet()) {
                for (Integer transitionTo : transition.getValue().get(transitionRead)) {
                    Element transitionElement = document.createElement("transition");

                    Element from = document.createElement("from");
                    from.appendChild(document.createTextNode(Integer.toString(transition.getKey())));
                    transitionElement.appendChild(from);

                    Element to = document.createElement("to");
                    to.appendChild(document.createTextNode(Integer.toString(transitionTo)));
                    transitionElement.appendChild(to);

                    Element read = document.createElement("read");
                    read.appendChild(document.createTextNode(transitionRead));
                    transitionElement.appendChild(read);

                    automatonElement.appendChild(transitionElement);
                }
            }
        }
    }

    private void createStates(Automaton automaton, Random random, Document document, Element automatonElement) {
        for (State state : automaton.states.values()) {
            Element stateElement = document.createElement("state");
            Attr id = document.createAttribute("id");
            Attr name = document.createAttribute("name");

            id.setValue(Integer.toString(state.id));
            name.setValue("q" + Integer.toString(state.id));

            stateElement.setAttributeNode(id);
            stateElement.setAttributeNode(name);

            createAxis(random, document, state, stateElement);

            if (state.isFinal()) {
                Element finalState = document.createElement("final");
                stateElement.appendChild(finalState);
            }

            if (state.isInitial()) {
                Element initialState = document.createElement("initial");
                stateElement.appendChild(initialState);
            }

            automatonElement.appendChild(stateElement);
        }
    }

    private void createAxis(Random random, Document document, State state, Element stateElement) {
        Element xAxis = document.createElement("x");
        xAxis.appendChild(document.createTextNode(Integer.toString(random.nextInt(500) + 40)));
        stateElement.appendChild(xAxis);

        Element yAxis = document.createElement("y");
        yAxis.appendChild(document.createTextNode(Integer.toString(random.nextInt(200) + 40)));
        stateElement.appendChild(yAxis);

        Element label = document.createElement("label");
        label.appendChild(document.createTextNode(state.name.replaceAll("q", "")));
        stateElement.appendChild(label);
    }
}