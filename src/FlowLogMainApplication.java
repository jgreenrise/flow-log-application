import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import service.LogsReaderService;
import service.impl.LogsReaderImpl;

public class FlowLogMainApplication {

    private static final Logger logger = Logger.getLogger(FlowLogMainApplication.class.getName());

    public static void main(String[] args) {
        processArgs(args);
    }

    public static void processArgs(String[] args) {

        if (args.length < 2) {
            logger.log(Level.SEVERE, "ERROR_CODE_1000: Please provide a filename and number of threads as a command-line argument.");
            logger.log(Level.SEVERE, "ERROR_CODE_1000: Please Retry. Example command: java Main input_flow_logs1.csv 4");
            System.exit(1);
        }

        String filePath = args[0];
        validateNumThreads(args[1]);
        int numThreads = Integer.parseInt(args[1]);

        LogsReaderService reader = new LogsReaderImpl(numThreads);
        try {
            reader.loadFile(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if(reader.getTotalUploadedRecords() != 0){
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
        }else{
            logger.log(Level.SEVERE, "ERROR_CODE_1002: Please provide data in the input file and try again. Refer ReadMe for further instructions");
            System.exit(1);
        }

    }

    public static void validateNumThreads(String arg) {

        int numThreads = -1;
        try{
            numThreads = Integer.parseInt(arg);
        }catch (Exception exception){
            logger.log(Level.SEVERE, "ERROR_CODE_1001: Invalid number of threads provided. Provided value:  {0}", numThreads);
            logger.log(Level.SEVERE, "ERROR_CODE_1001: Please Retry. Example command: java Main input_flow_logs1.csv 4");
            throw new IllegalArgumentException("ERROR_CODE_1001: Invalid number of threads provided. Provided value: " + numThreads);
        }

        if(numThreads <= 0){
            logger.log(Level.SEVERE, "ERROR_CODE_1001: Invalid number of threads provided. Provided value:  {0}", numThreads);
            logger.log(Level.SEVERE, "ERROR_CODE_1001: Please Retry. Example command: java Main input_flow_logs1.csv 4");
            throw new IllegalArgumentException("ERROR_CODE_1001: Invalid number of threads provided. Provided value: " + numThreads);
        }

    }
}