package service;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public interface LogsReaderService {
    void loadFile(String filePath);

    int getTotalUploadedRecords();

    Map<String, AtomicInteger> getTagCounts();

    Map<String, AtomicInteger> getPortProtocolCounts();
}
