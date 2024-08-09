import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import service.LogsReaderService;
import service.impl.LogsReaderImpl;

public class Main {

    public static void main(String[] args) {

        // Check if a filename was provided as an argument
        if (args.length < 2) {
            System.err.println("Please provide a filename and number of threads as a command-line argument.");
            System.exit(1);
        }

        String filePath = args[0];
        int numThreads = Integer.parseInt(args[1]);

        //String filePath = "/Users/jatinpatel/Library/Mobile Documents/com~apple~CloudDocs/Documents/code/Algorithms/src/flowLog/input_flow_logs.csv";  // Path to your input file
        //int numThreads = 4;
        LogsReaderService reader = new LogsReaderImpl(numThreads);
        reader.loadFile(filePath);

        System.out.println("Uploaded records: " + reader.getTotalUploadedRecords());

        Map<String, AtomicInteger> mapTagCounts = reader.getTagCounts();
        System.out.println("\n** Tag Counts: **");
        System.out.printf("| %-10s | %-5s |\n", "Tag", "Count");
        System.out.println("|------------|-------|");
        mapTagCounts.forEach((k, v) -> {
            System.out.printf("| %-10s | %-5s |\n", k, v);
        });

        Map<String, AtomicInteger> mapPortProtocolCounts = reader.getPortProtocolCounts();
        System.out.println("\n** Port/Protocol Combination Counts: **");
        System.out.printf("| %-10s | %-10s | %-5s |\n", "Port", "Protocol", "Count");
        System.out.println("|------------|------------|-------|");
        mapPortProtocolCounts.forEach((k, v) -> {
            String[] key = k.split("_");
            String port = key[0];
            String protocol = key[1];
            System.out.printf("| %-10s | %-10s | %-5s |\n", port, protocol, v);
        });
    }
}