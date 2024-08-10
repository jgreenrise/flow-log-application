package service;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public interface LogsReaderService {

    // Load the file from the given file path
    void loadFile(String filePath) throws IOException;

    // Get the total number of records uploaded
    int getTotalUploadedRecords();

    // Get the count of tags used
    Map<String, AtomicInteger> getTagCounts();

    // Get the count of port and protocol combinations
    Map<String, AtomicInteger> getPortProtocolCounts();
}

