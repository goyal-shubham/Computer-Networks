
#SFTP – Reliable Transfer over a Reliable Channel
This project consists of building an SFTP (Simple File Transfer Protocol). It consists of a client and a server that exchange one file using TCP. The file should be transferred from the client to the server.
The client accepts 4 arguments: the name of the two files (<input> and <output>), and the IP address (or Internet name) and the port number of the server.
 ftp_client <input_filename> <output_filename> <server_ip_address> <server_port#>
The server starts first and waits for a connection request.
The client requests a connection and then sends the name of the file <output> for output and the data in file <input> to the server, which saves the info received in the file <output>. The client reads the file and sends the data in chunks of 10 bytes. After sending the file, the client closes the connection and exits. The server receives the data and writes the file in chunks of 5 bytes.
The server needs to know when the transmission is over so that it can close the file. You need to come up with a mechanism for that! After executing, <input> and <output> should look the same. Your SFTP should be built on top of TCP.
The preferred language to be used is C/C++ and Linux (any Unix will do it!) but Java will also be accepted. You will need to use the socket library. The man pages in the Unix/Linux systems have a lot of useful information. Start with <man socket>. There is a man page for each function in the socket library.
Your SFTP should be able to transfer binary files as well as text files.
