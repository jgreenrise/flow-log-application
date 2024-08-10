package unitTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import service.impl.LogsReaderImpl;

import static org.junit.jupiter.api.Assertions.*;

class LogsReaderImplTest {

    private LogsReaderImpl logsReader;
    private static final String TEST_FILE_PATH = "test_flow_logs.csv";
    private static final String EMPTY_FILE_PATH = "empty_flow_logs.csv";
    private static final String NON_EXISTENT_FILE_PATH = "non_existent_file.csv";

    @BeforeEach
    void setUp() {
        logsReader = new LogsReaderImpl(4); // Set up with 4 threads
    }

    @Test
    void testLoadFile() throws IOException {
        // Prepare a test CSV file
        try (FileWriter writer = new FileWriter(TEST_FILE_PATH)) {
            writer.write("dstport,protocol,tag\n");
            writer.write("80,tcp,sv_P1\n");
            writer.write("25,tcp,sv_P2\n");
            writer.write("80,tcp,sv_P1\n");
            writer.write("25,tcp,\n");
        }

        logsReader.loadFile(TEST_FILE_PATH);

        // Check total records
        assertEquals(4, logsReader.getTotalUploadedRecords());

        // Check tag counts
        Map<String, AtomicInteger> tagCounts = logsReader.getTagCounts();
        assertEquals(3, tagCounts.size());
        assertEquals(2, tagCounts.get("sv_P1").get());
        assertEquals(1, tagCounts.get("sv_P2").get());
        assertEquals(1, tagCounts.get("untagged").get());

        // Check port/protocol counts
        Map<String, AtomicInteger> portProtocolCounts = logsReader.getPortProtocolCounts();
        assertEquals(2, portProtocolCounts.size());
        assertEquals(2, portProtocolCounts.get("80_tcp").get());
        assertEquals(2, portProtocolCounts.get("25_tcp").get());

        // Clean up the test file
        new File(TEST_FILE_PATH).delete();
    }

    @Test
    void testLoadFile_FileDoesNotExist() {
        Exception exception = assertThrows(IOException.class, () -> {
            logsReader.loadFile(NON_EXISTENT_FILE_PATH);
        });

        String expectedMessage = "No such file";
        String actualMessage = exception.getMessage();
        System.out.println(actualMessage);

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testLoadFile_EmptyFile() throws IOException {
        // Create an empty test CSV file
        new File(EMPTY_FILE_PATH).createNewFile();

        logsReader.loadFile(EMPTY_FILE_PATH);

        // Check total records
        assertEquals(0, logsReader.getTotalUploadedRecords());

        // Check tag counts
        Map<String, AtomicInteger> tagCounts = logsReader.getTagCounts();
        assertEquals(0, tagCounts.size());

        // Check port/protocol counts
        Map<String, AtomicInteger> portProtocolCounts = logsReader.getPortProtocolCounts();
        assertEquals(0, portProtocolCounts.size());

        // Clean up the empty file
        new File(EMPTY_FILE_PATH).delete();
    }

    @Test
    void testGetTotalUploadedRecords() {
        assertEquals(0, logsReader.getTotalUploadedRecords());
    }

    @Test
    void testGetTagCounts() {
        Map<String, AtomicInteger> tagCounts = logsReader.getTagCounts();
        assertNotNull(tagCounts);
        assertTrue(tagCounts.isEmpty());
    }

    @Test
    void testGetPortProtocolCounts() {
        Map<String, AtomicInteger> portProtocolCounts = logsReader.getPortProtocolCounts();
        assertNotNull(portProtocolCounts);
        assertTrue(portProtocolCounts.isEmpty());
    }
}
