import utils.OutputWriter;

import java.io.IOException;

// Args[0] is the project root folder
public class Main {
    public synchronized static void main(String[] args) throws IllegalArgumentException, IOException, InterruptedException {
        if (args.length > 0) {
            Matcher matcher = new Matcher(args[0]);
            matcher.match();
        } else {
            throw new IllegalArgumentException("You must provide a correct file path with a java project!");
        }
    }
}
