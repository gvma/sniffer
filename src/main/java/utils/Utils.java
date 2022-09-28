package utils;

import org.w3c.dom.Node;

public class Utils {
    public static boolean isExpect(Node node) {
        return node.getNodeName().equals("expr") && node.getTextContent().contains("EXPECT_");
    }
}