package utils;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Utils {

  public static boolean isAssertEquals(Node node) {
    String textContent = node.getTextContent().trim();
    return node.getNodeName().equals("expr") && (textContent.startsWith("assertEquals") || textContent.startsWith("assertNotEquals"));
  }

  public static boolean isAssertsWith3Arguments(Node node) {
    String textContent = node.getTextContent().trim();
    return node.getNodeName().equals("expr") && (
            textContent.startsWith("assertArrayEquals") ||
                    textContent.startsWith("assertEquals") ||
                    textContent.startsWith("assertNotSame") ||
                    textContent.startsWith("assertSame") ||
                    textContent.startsWith("assertThrows") ||
                    textContent.startsWith("assertThat") ||
                    textContent.startsWith("assertNotEquals")
    );
  }

  public static boolean isAssertsWith2Arguments(Node node) {
    String textContent = node.getTextContent().trim();
    return node.getNodeName().equals("expr") && (
            textContent.startsWith("assertNotNull") ||
                    textContent.startsWith("assertNull") ||
                    textContent.startsWith("assertTrue")
    );
  }

  public static boolean isFail(Node node) {
    String textContent = node.getTextContent().trim();
    return node.getNodeName().equals("expr") && textContent.startsWith("fail");
  }

  public static boolean ispointFlutuant(String number) {
    try {
      Double.parseDouble(number);
      return true;
    } catch (Exception ignored) {
      return false;
    }
  }

  public static boolean isAssertEqualsWithDelta(Node node) {
    boolean isWithDelta = false;
    NodeList nodeChildren = node.getChildNodes();
    for (int i = 0; i < nodeChildren.getLength(); ++i) {
      Node child = nodeChildren.item(i);
      if (child.getNodeName().equals("argument_list")) {
        NodeList arguments = child.getChildNodes();
        int argumentPosition = 1;
        for (int j = 0; j < arguments.getLength(); ++j) {
          Node argument = arguments.item(j);
          if (argument.getNodeName().equals("argument")) {
            boolean isVariable = true;
            Node possibleLiteral = argument.getFirstChild().getFirstChild();
            if (possibleLiteral.hasAttributes()) {
              isVariable = false;
            }
            if (argumentPosition == 3 && (ispointFlutuant(argument.getTextContent()) || isVariable)) {
              isWithDelta = true;
            }
            argumentPosition += 1;
          }
        }
        return isWithDelta;
      }
      isWithDelta = isAssertEqualsWithDelta(child);
    }
    return isWithDelta;
  }

  public static int getNumbersOfArguments(Node assertRootNode) {
    int numberOfArguments = 0;
    NodeList nodeChildren = assertRootNode.getChildNodes();
    for (int i = 0; i < nodeChildren.getLength(); ++i) {
      Node child = nodeChildren.item(i);
      if (child.getNodeName().equals("argument_list")) {
        NodeList arguments = child.getChildNodes();
        for (int j = 0; j < arguments.getLength(); ++j) {
          Node argument = arguments.item(j);
          if (argument.getNodeName().equals("argument")) {
            numberOfArguments += 1;
          }
        }
        return numberOfArguments;
      }
      numberOfArguments = getNumbersOfArguments(child);
    }
    return numberOfArguments;
  }
}