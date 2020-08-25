import matchers.Sniffer;

// Args[0] is the project root folder
public class Main {
    public synchronized static void main(String[] args) throws Exception {
        if (args.length > 0) {
            Sniffer sniffer = new Sniffer(args[0]);
            sniffer.sniff();
        } else {
            throw new IllegalArgumentException("You must provide a correct file path with a java project!");
        }
    }
}
