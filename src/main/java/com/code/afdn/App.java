package com.code.afdn;

import java.util.logging.Logger;

import com.code.afdn.xml.XmlLoader;
import com.code.afdn.xml.XmlWriter;

public class App {
    private final XmlLoader xmlLoader = new XmlLoader();
    private final XmlWriter xmlWriter = new XmlWriter();

    private Automaton getAutomaton(String filename) {
        return xmlLoader.loadAutomatonFromFile(filename);
    }

    private void runAutomaton(Automaton automaton, String sentence) {
        Logger.getLogger(App.class.getName()).info("Running automaton...");
        if (automaton.run(sentence)) {
            Logger.getLogger(App.class.getName()).info("The sentence is ACCEPTED by the Automaton!");
        } else {
            Logger.getLogger(App.class.getName()).info("The sentence is REFUSED by the Automaton!");
        }
    }

    private void convertAutomaton(Automaton automaton, String fileName) {
        Logger.getLogger(App.class.getName()).info("Converting automaton...");

        Automaton newAutomaton = automaton.convert();

        Logger.getLogger(App.class.getName())
                .info("Successfully converted automaton! Result: " + newAutomaton.toString());

        xmlWriter.generateDocument(newAutomaton, fileName);
    }

    public static void main(String[] args) {
        App app = new App();

        try {
            Logger.getLogger(App.class.getName()).info("Reading automaton from file...");

            Automaton automaton = app.getAutomaton(args[0]);

            Logger.getLogger(App.class.getName()).info("Automaton successfully read!\n " + automaton.toString());

            String parameter = args[1].replace("-", "");

            switch (parameter) {
                case "run":
                    String sentence = args[2];
                    app.runAutomaton(automaton, sentence);
                    break;

                case "convert":
                    String fileName = "newAutomaton.jff";

                    if (args.length >= 3)
                        fileName = args[2];

                    app.convertAutomaton(automaton, fileName);
                    break;

                default:
                    Logger.getLogger(App.class.getName()).warning(
                            "No valid option was specified. Please use --run with a sentence for input (--run 1000, for example), or --convert to convert to an FDA and save to 'newAutomaton.jff or pass a different name'");
                    break;
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            Logger.getLogger(App.class.getName()).warning(
                    "None or invalid arguments specified. Please provide a path for a file followed by a run option. Use --run with a sentence for input (--run 1000, for example), or --convert to convert to an FDA and save to 'newAutomaton.jff, or pass a different name'");
        } catch (Exception ex) {
            Logger.getLogger(App.class.getName())
                    .warning("An unhandled error occurred. Terminating program." + ex.getMessage());
        }
    }
}
