# MineHunter

MineHunter is a practical cryptomining traffic detection algorithm based on time series tracking. Instead of being deployed at the hosts, MineHunter detects the cryptomining traffic at the entrance of enterprise or campus networks. Minehunter has taken into account the challenges faced by the actual deployment environment, including extremely unbalanced datasets, controllable alarms, traffic confusion, and efficiency.

**This work is accepted by ACSAC 2021.**

# Files introduction

* **block\_collect\_script**: The script used for log processing of block collection software. For example, Monero's collection tool is monerod [1].
* **blok\_info**: The information of the Monero block creation time. And the time range is from 2020-10-21 to 2021-01-05, which is part of the input of the core detection code.
* **block\_raw\_log\_example**: Sample of collecting logs by the monerod.
* **java\_project**: The core detection code. The inputs include the original traffic data (due to the large-scale, you can download it in the data link below), and the block information in block\_info.

# How to start?

## Running Step 1——Getting Block Info

1：Use the monerod tool to join the monero blockchain network and collect block creation information at the same time. The running configuration file of the monerod tool is block\_collect\_script/monerod.conf. (Other cryptocurrencies can use similar tools).

2：Use block\_collect\_script/monerod\_log\_analysis.py to process the original logs collected by monerod to obtain block creation time information.

## Running Step 2——Running detection algorithm

1: Package the java source code in java\_project to generate a java binary run jar package.

2: Modify the config file in java\_project, including the path of the original traffic, the path of block information, etc.

## Running Step 3——Getting results

1: Run the command: java -jar ./main.jar

2: Result file description:

	The result file contains 4 columns [pos, num, flow_name ,sim]:
		(1) pos: Indicate the position of the detection block in the reference sequence. The same pos indicates the same detection result index.
		(2) num: The sorting results of different flows in the global detection table.
		(3) flow_name: Source_destination ip address.
		(4) sim: Global similarity. According to the experimental results in the paper, when the general sim>0.6, it can be determined that the flow is suspected to be cryptomining. 

## DATA Storage link

To be updated


## REFERENCES
[1] https://monerodocs.org/interacting/monerod-reference/