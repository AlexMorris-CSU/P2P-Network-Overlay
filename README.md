# Routing Packets Within a Structured Peer-to-Peer (P2P) Network Overlay

Alex Morris, Routing Packets Within a Structured Peer-to-Peer (P2P) Network Overlay

1. Included Files:
All source files are within the following packages.
  * cs455/overlay/node
  * cs455/overlay/routing 
  * cs455/overlay/transport 
* cs455/overlay/util 
* cs455/overlay/wireformats 
* README.txt 
* build.gradle 

2. How to run:
    Use "gradle build" to build
    Run registry
        -cd build/libs
       -"java -cp Alex_Morris_HW1-1.0-SNAPSHOT.jar cs455.overlay.node.Registry <registry-port>"
    Run messaging nodes on lab machines
        -set registry IP and port in start-nodes.sh
        -"./start-nodes.sh" to run, machine-list is specified in /src/main/resources/machine-list
    Once all intended nodes are connected run "setup-overlay <number-of-routing-entrys>"
    Upon seeing "Registry now ready to initiate tasks." use "start <number-of-messages>"
    Wait for summary statistics to be gathered and displayed
    Wait time is only 60 seconds which may not be long enough considering, packet amount, routing table size, and current usage of ssh computers

3. Description of files:
  Registry(in cs455/overlay/node):
    -In charge of registering and deregistering Messaging Nodes.
    -Assigning random ID to Messaging Nodes(0-127)
    -Printing currently registered nodes and routing tables for each node if available
    -Initiate the sending of messages
    -Upon receipt of task completion from all nodes in overlay, will wait 90 seconds before gathering information
    -Gathering and printing final sums

  MessagingNode(in cs455/overlay/node):
    -Connecting and registering with the Registry via command line arguments
    -Upon receiving initiate task
      -Send messages to random nodes in overlay via their ID with random values(between min and max int)
      -Receive messages intended for it
      -Route messages to other nodes, in log(n) time
      -Track counts for Sent, Received and Relayed
      -Tracks sums for received and sent
      -Sending and relaying messages are added to queue to then be routed and sent
    -Notify the Registry when it has finished sending messages
    -Send the counts and sums to registry upon registry request

  InteractiveCommandParser(in cs455/overlay/util):
    -Listen for user input in console
    -Commands for registry
      -"list-messaging-nodes"
      -"setup-overlay <number-of-routing-table-entries>"
        -number of routing table entries will default to 3 if no argument is provided
      -"list-routing-tables"
      -"start <number-of-messages>"
    -Commands for MessagingNode
      -"print-counters-and-diagnostics"
      -"exit-overlay"

    wireformats:
      -This package contains the wireformats for communication between the Registry and MessagingNodes
      -Alos contains singleton class EventFactory, which is used to get events
    transport:
        -Handles all sockets and connection for registry and nodes
    routing:
        -Contains routingtable and routingentry to store routing tables information
