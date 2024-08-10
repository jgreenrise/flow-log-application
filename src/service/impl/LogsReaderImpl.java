package service.impl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import service.LogsReaderService;

public class LogsReaderImpl implements LogsReaderService {

    private static final Logger logger = Logger.getLogger(LogsReaderImpl.class.getName());
    public int numThreads;
    ConcurrentHashMap<String, AtomicInteger> mapTagCounts;
    ConcurrentHashMap<String, AtomicInteger> mapPortProtocolCounts;
    public int totalRecords;

    public LogsReaderImpl(int numThreads) {
        this.numThreads = numThreads;
        this.mapTagCounts = new ConcurrentHashMap<String, AtomicInteger>();
        this.mapPortProtocolCounts = new ConcurrentHashMap<>();
        this.totalRecords = 0;
    }

    @Override
    public void loadFile(String filePath) throws IOException {
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String finalLine = line;
                executorService.submit(() -> readline(finalLine));
                totalRecords++;
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "ERROR_CODE_1003: Error reading file: {0}", e.getMessage());
            throw e;
        } finally {
            executorService.shutdown();
        }

        while (!executorService.isTerminated()) {
        }

    }

    @Override
    public int getTotalUploadedRecords() {
        return totalRecords;
    }

    @Override
    public Map<String, AtomicInteger> getTagCounts() {
        return mapTagCounts;
    }

    @Override
    public Map<String, AtomicInteger> getPortProtocolCounts() {
        return mapPortProtocolCounts;
    }

    private void readline(String line) {
        String[] parts = line.trim().split(",");
        if (parts.length != 0) {
            String dstport = parts[0].trim();
            String protocol = parts[1].trim();
            String tag = parts.length == 3 ? parts[2].trim() : "untagged";
            //System.out.println("DstPort: " + dstport + ", Protocol: " + protocol + ", Tag: " + tag);

            mapTagCounts.putIfAbsent(tag, new AtomicInteger((0)));
            mapTagCounts.get(tag).incrementAndGet();

            String key = dstport + "_" + protocol;
            mapPortProtocolCounts.putIfAbsent(key, new AtomicInteger(0));
            mapPortProtocolCounts.get(key).incrementAndGet();


        } else {
            System.err.println("Invalid line format: " + line);
        }
    }

}
