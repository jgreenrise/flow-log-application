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
    - We believe that the "x" at the end of this entry was added by mistake. Therefore, it has not been considered in our tag mapping logic, and only the valid entries will be processed. The valid lookup table file is structured as follows:
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

To set up the project locally, follow these steps:

```bash
# Clone the repository
git clone https://github.com/your-repo.git
cd your-repo

# Compile the program
javac -d . service/*.java FlowLogsReaderMainApplication.java

# Run the program
java FlowLogsReaderMainApplication flow_logs.txt tag_mappings.csv output.csv