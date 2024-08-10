# Flow Log Tagging Program

## Overview

This program parses flow log data from a specified input file and maps each entry to a corresponding tag based on a lookup table provided in CSV format. The lookup table consists of three columns: `dstport`, `protocol`, and `tag`. The combination of `dstport` and `protocol` determines the applicable tag for each flow log entry.

## Features

- **Input Files**:
    - A flow log file containing flow data (max size: 10 MB).
    - A CSV file serving as a lookup table (up to 10,000 mappings).

- **Tag Mapping**: Matches flow log entries to tags defined in the lookup table. Matching is case insensitive.

- **Output Generation**: Generates an output file summarizing:
    - **Tag Counts**: A count of how many times each tag was matched, including untagged records.
    - **Port/Protocol Combination Counts**: A count of matches for each unique combination of `dstport` and `protocol`.

### Example Output

**Tag Counts:**

| Tag       | Count |
|-----------|-------|
| Untagged  | 2     |
| sv_P2     | 2     |
| SV_P3     | 1     |
| sv_P1     | 2     |

**Port/Protocol Combination Counts:**

| Port | Protocol | Count |
|------|----------|-------|
| 23   | tcp      | 1     |
| 80   | tcp      | 1     |
| 68   | udp      | 1     |
| 25   | tcp      | 1     |
| 31   | udp      | 1     |
| 443  | tcp      | 1     |

## Assumptions

1. **Data Format**:
    - Each flow log entry is in the format `dstport,protocol,[tag]`, where the tag is optional.
    - Lines must be comma-separated without leading or trailing spaces.

2. **Port and Protocol Validity**:
    - Assumes that `dstport` values correspond to valid port numbers (0-65535).
    - Assumes that `protocol` values are either `tcp` or `udp`.

3. **Environment**:
    - Assumes that the Java Runtime Environment (JRE) is installed with a version of at least Java 8.
    - Assumes proper permissions for reading input files and writing output files.

4. **Error Handling**:
    - Invalid lines will be logged to the standard error output but will not disrupt the processing of other lines.
    - Missing or malformed data will be handled gracefully.

5. **Tag Mapping Consideration**: In the provided lookup table file, we noticed the following entry:
    - Provided input:
      ```
      23,tcp,sv_P1 x
      ```
      - I believe that the "x" at the end of this entry was added by mistake. Therefore, it has not been considered in our tag mapping logic, and only the valid entries will be processed. The valid lookup table file is structured as follows:
        ```
        dstport,protocol,tag
        25,tcp,sv_P1
        68,udp,sv_P2
        31,udp,SV_P3
        443,tcp,sv_P2
        ```

6. **Port Input Validity**: The dot after the port number is ignored. 
    - Provided input:
      ```
      Port.   Protocol. Count
      23.     tcp       1
      80      tcp       1
      68      udp      1
      25      tcp       1
      31      udp      1
      443.  tcp       1
      ```
   - We assume that the port input is always valid. For example, the port/protocol combination counts are structured as follows:
     ```
     Port    Protocol    Count
     23      tcp         1
     80      tcp         1
     68      udp         1
     25      tcp         1
     31      udp         1
     443     tcp         1
     ```



## Installation

To set up the project on your local machine, please follow these steps:

```bash
# 1. Clone the repository
git clone git@github.com:jgreenrise/flow-log-application.git
cd flow-log-application

# 2. Change to the src directory
cd src

# 3. Upload/Update Input file in src directory
# input_flow_logs.csv is already uploaded

# 3. Compile the Java program
javac service/*.java service/impl/*.java FlowLogMainApplication.java 

# 4. Execute the program
java FlowLogMainApplication <complete_file_path> <number_of_threads>
# number_of_threads helps us load the files concurrently. 
# - numThreads specifies the maximum number of threads that the thread pool can have active at any given time. 
# - This means that the pool will create and manage exactly numThreads threads to execute tasks concurrently. 
# - If there are more tasks than available threads, the additional tasks will wait in a queue until a thread becomes available.

# Example command
java FlowLogMainApplication "input_flow_logs.csv" 4
Uploaded records: 1000

** Tag Counts: **
| Tag        | Count |
|------------|-------|
| untagged   | 712   |
| sv_P2      | 199   |
| sv_P1      | 89    |

** Port/Protocol Combination Counts: **
| Port       | Protocol   | Count |
|------------|------------|-------|
| 3306       | tcp        | 65    |
| 4433       | tcp        | 58    |
| 68         | udp        | 70    |
| 123        | udp        | 68    |
| 53         | udp        | 75    |
| 23         | tcp        | 69    |
| 443        | tcp        | 65    |
| 25         | tcp        | 70    |
| 2049       | tcp        | 66    |
| 80         | tcp        | 79    |
| 67         | udp        | 72    |
| 4000       | tcp        | 73    |
| 22         | tcp        | 48    |
| 5000       | tcp        | 60    |
| 8080       | tcp        | 62    |

```

```
# 5. Test: Negative flow: File does not exist
java FlowLogMainApplication "input_flow_logs1.csv" 4

## Output
ERROR_CODE_1003: Error reading file: input_flow_logs1.csv (No such file or directory)
```

```
# 6. Test: Negative flow: File empty
java FlowLogMainApplication testInputFiles/test_empty_file.csv 2

## Output
ERROR_CODE_1002: Please provide data in the input file and try again. Refer ReadMe for further instructions
Uploaded records: 0
```

```
# 7. Test: Negative flow: Invalid number of threads provided
java FlowLogMainApplication "input_flow_logs.csv" "-2dd"

## Output
ERROR_CODE_1000: Invalid number of threads provided. Provided value: -1
ERROR_CODE_1000: Please Retry. Example command: java FlowLogMainApplication input_flow_logs1.csv 4: 
```

## Running Unit Tests

To run the unit tests for the project, follow these steps:

1. **Compile the main classes:**
   ```bash
   javac -d target -cp libs/junit-platform-console-standalone-1.10.0.jar src/service/impl/LogsReaderImpl.java src/service/LogsReaderService.java

2. Compile the unit test classes:
   ```bash
   javac -d target -cp target:libs/junit-platform-console-standalone-1.10.0.jar src/unitTests/LogsReaderImplTest.java

3. Run the unit tests:
    ```bash
   java -jar libs/junit-platform-console-standalone-1.10.0.jar --class-path target --scan-class-path

## Error Handling

- If the provided filename and number of threads are not specified, the program will log an error and exit.
- If the number of threads provided is invalid (not a number or less than or equal to zero), an exception will be thrown.
- If the file cannot be read, an error will be logged, and an exception will be thrown.
- If no data is provided in the input file, an error will be logged, and the program will exit.

## Future Consideration

* Make number_of_threads an optional parameter.
* Add a REST API to interact with the program.
* Send results via email notifications.
* Deploy the project as a temporary task based on needs.
* Save results in a database.
* Cache responses for the same input.