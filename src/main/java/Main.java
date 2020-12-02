import github.GithubRepositoryClone;
import matchers.Sniffer;

public class Main {
    public synchronized static void main(String[] args) throws Exception {
        if (args.length > 0) {
            if (args[0].equals("-githubClone")) {
                new GithubRepositoryClone(args[1], args[2], args[3], args[4], args[5]);
            } else {
                Sniffer sniffer = new Sniffer(args[0]);
                sniffer.sniff();
            }
        } else {
            throw new IllegalArgumentException("You must provide a correct file path with a java project!");
        }
    }
}
