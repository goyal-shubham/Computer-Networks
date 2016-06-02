#  SFTP - Reliable Transfer over an Unreliable Channel with Bit Errors that can also loose packets

The project consists of building a Stop and Wait (S&W) reliable protocol. The S&W is built on top of UDP, and it is
supposed to provide a reliable transport service to the SFTP application.Messages are sent one at a time, and each
message needs to be acknowledged when received, before a new message can be sent.

The S&W consists of a client and a server. Communication is unidirectional, i.e., data flows from the client to the
server. The server starts first and waits for packets. The client starts the communication. Packets have sequence number
 0 or 1. Before sending each packet, a checksum is calculated and added to the S&W header. After sending each packet,
the client starts a timer (use alarm or sleep). When the timer goes off, the client tries to read a corresponding ACK
answer. If the corresponding ACK is not there, or it is not the corresponding ACK (or if the checksum does not match),
the packet is sent again and the timer is started again. If the corresponding ACK is there, the client changes the state
 and returns to the application which can now send one more packet. This means that the program blocks on writes.

The server, after receiving a packet, checks its checksum. If the packet is correct and has the right sequence number,
the server sends an ACK0 or ACK1 answer (according to the sequence number) to the client, changes state accordingly,
and deliver data to the application. If the message is not correct, the server repeats the last ACK message.

The protocol should deal properly with duplicate data packets and duplicate ACK messages.

The S&W packet contains the header and the application data. No reordering is necessary, since the S&W is sending the
exact data given by the application, one by one.

To verify MY protocol, I am using the result of a random function to decide to send or skip a message, to decide to
send or skip an ACK message, and to decide whether to send the right checksum or just zero. This is making fake packet
 error and loss of a packet effect.
