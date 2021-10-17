package com.code.afdn.xml;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlHelper {
    public static Iterable<Node> iterable(final NodeList nodeList) {
        return () -> new Iterator<Node>() {
    
            private int index = 0;
    
            @Override
            public boolean hasNext() {
                return index < nodeList.getLength();
            }
    
            @Override
            public Node next() {
                if (!hasNext())
                    throw new NoSuchElementException();
                return nodeList.item(index++); 
            }
        };
    }
}