import github.GithubRepositoryClone;
import matchers.Sniffer;
import utils.JUnit5Utilization;
import utils.OutputWriter;

public class Main {
    public synchronized static void main(String[] args) throws Exception {
        if (args.length > 0) {
            if (args[0].equals("-githubClone")) {
                new GithubRepositoryClone(args[1], args[2], args[3], args[4], args[5]);
            } else if (args[0].equals("-findJUnit5Utilization"))  {
                JUnit5Utilization jUnit5Utilization = new JUnit5Utilization(args[1]);
            } else {
                Sniffer sniffer = new Sniffer(args[0]);
                sniffer.sniff();
                OutputWriter.csvWriter.close();
            }
        } else {
            throw new IllegalArgumentException("You must provide a correct file path with a java project!");
        }
    }
}
