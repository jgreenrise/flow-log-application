import csv
import random

# Load flow log data from the text file
with open('flow_logs.txt', 'r') as f:
    flow_logs = [line.strip().split() for line in f.readlines()]

# Load tag mappings from the text file
with open('tag_mappings.txt', 'r') as f:
    tag_mappings = [line.strip().split(',') for line in f.readlines()]

# Define the number of records you want to generate
num_records = 1000

# Create a file to save the random flow log data
with open('../random_flow_logs.csv', 'w', newline='') as file:
    writer = csv.writer(file)
    # Write the header
    writer.writerow(['dstport', 'protocol', 'tag'])

    # Generate random records
    for _ in range(num_records):
        dstport, protocol = random.choice(flow_logs)

        # Get the possible tags for the selected dstport and protocol
        possible_tags = [mapping[2] for mapping in tag_mappings if mapping[0] == dstport and mapping[1] == protocol]

        # Randomly decide whether to assign a tag or leave it untagged
        if random.random() < 0.7:  # 70% chance to have a tag
            if possible_tags:  # Check if there are possible tags
                tag = random.choice(possible_tags)
            else:
                tag = ''  # No matching tag, leave untagged
        else:
            tag = ''  # No tag (untagged)

        # Write the row
        writer.writerow([dstport, protocol, tag])

print("Random flow log data generated as 'random_flow_logs.csv'")