package SFTP_UDP_StopAndWait;

import java.io.*;
import java.util.*;
import java.util.zip.*;
import java.net.*;

/**
 * Created by shubhamgoyal on 5/28/16.
 */
public class UDPServer
{


    public static void main(String args[])
    {
        try
        {


            //Creating the UDP Server socket
            DatagramSocket ds = new DatagramSocket(3756);
            System.out.println("UDP Server Socket has been created");

            //Creating the packet to receive from the client
            DatagramPacket receivedPacket;

            //Creating the acknowledgement to send to the client
            DatagramPacket sendAcknowledgement;

            //Creating the packet array in which all the data will be present
            byte receivedDataPacket[] = new byte[21];

            //            //Making all the elements of the packet array as 0 in the beginning
            //            for (int i = 0; i < receivedDataPacket.length; i++)
            //            {
            //                receivedDataPacket[i] = 0;
            //            }


            //Creating a byte array to store only the data

            byte[] readDataBuffer = new byte[10];



            int counter = 1;

            //Creating a Checksum object
            Checksum cs;
            long serverChecksum = 0;
            byte clientChecksum[] = new byte[10];
            byte serverChecksumArray[];
            String serverChecksumString;


            //Creating an object of Random class
            Random r = new Random();
            int randomSequenceNumberAtServer;
            int sequenceNumFromClient;

            Scanner sc = new Scanner(System.in);
            System.out.println("Enter Output file name");
            String output = sc.next();
            sc.close();
            File outputFile = new File(output);
            FileOutputStream fos = new FileOutputStream(outputFile);

            System.out.println("Waiting for Client Connection");
            //Creating a byte array to send the acknowledgement to the client
            byte sendACKToClient[] = new byte[1];


            //            //Receiving the number of iterations from the client
            //            byte noOfIterationsArray[] = new byte[1];
            //            DatagramPacket receiveNoOfIterations = new DatagramPacket(noOfIterationsArray, noOfIterationsArray.length);
            //            ds.receive(receiveNoOfIterations);
            //
            //
            //            int numberOfIterations = noOfIterationsArray[0];
            //            System.out.println("The number of iterations received from the client are: " + numberOfIterations);

            int match = 0;
            byte[] receiveByteCount = new byte[1];
            receiveByteCount[0] = 10;

            //Running the loop according to the number of iterations
            while (receiveByteCount[0] == 10)
            {
                DatagramPacket receiveCount = new DatagramPacket(receiveByteCount, 1);
                ds.receive(receiveCount);


                boolean flag = true;

                System.out.println("Received byte count = " + receiveByteCount[0]);
                while (flag)
                {
                    byte readDataBuffer1[] = new byte[5];
                    byte readDataBuffer2[] = new byte[5];
                    //Receiving the fully loaded packet from the client side
                    receivedPacket = new DatagramPacket(receivedDataPacket, receivedDataPacket.length);
                    ds.receive(receivedPacket);

                    //Displaying the packet received from the client
                    System.out.println("Packet received : ");
                    for (int i = 0; i < receivedDataPacket.length; i++)
                    {
                        System.out.print(receivedDataPacket[i] + "\t");
                    }
                    System.out.println();

                    //Extracting the  byte array of the data from the packet
                    for (int i = 0; i < readDataBuffer1.length; i++)
                    {
                        readDataBuffer1[i] = receivedDataPacket[i + 11];
                        readDataBuffer[i] = receivedDataPacket[i + 11];
                    }
                    for (int i = 0; i < readDataBuffer2.length; i++)
                    {
                        readDataBuffer2[i] = receivedDataPacket[i + 16];
                        readDataBuffer[i + 5] = receivedDataPacket[i + 16];
                    }


                    //Extracting the checksum coming from the client side

                        clientChecksum = Arrays.copyOfRange(receivedDataPacket, 1 , 11);



                    //Recalculating the checksum for the data at the server side
                    cs = new CRC32();
                    cs.update(readDataBuffer, 0, readDataBuffer.length);
                    serverChecksum = cs.getValue();
                    System.out.println("Checksum Server: " + serverChecksum);
                    serverChecksumString = String.valueOf(serverChecksum);

                    int checkSumLength = serverChecksumString.length();
                    int diff = 10 - checkSumLength;
                    while (diff-- > 0)
                    {
                        serverChecksumString = '0' + serverChecksumString;
                    }
                    serverChecksumArray = serverChecksumString.getBytes();


                    //                    for(int i = 0 ; i < serverChecksumArray.length; i++)
                    //                    {
                    //                        System.out.print(serverChecksumArray[i] + " " + clientChecksum[i]);
                    //                    }
                    //Comparing the checksums of the client side and the server side
                    for (int i = 0; i < clientChecksum.length; i++)
                    {
                        //                        System.out.println("IN check sum " + i);
                        if (clientChecksum[i] == serverChecksumArray[i])
                        {
                            match++;
                            //System.out.println("CHECKSUM ELEMENT MATCHES");
                        }
                        else
                        {
                            System.out.println("CHECKSUM ELEMENT DOES NOT MATCH");
                            break;
                        }
                    }

                    System.out.println("Counter = " + counter);
//                    System.out.println("Match = " + match);

                    if (counter % 100 != 0 && match == clientChecksum.length)
                    {
                        System.out.println("CHECKSUM ELEMENTS MATCH");
                        //Generating a random number
                        randomSequenceNumberAtServer = r.nextInt(2);
                        System.out.println("Random Sequence Number at server = " + randomSequenceNumberAtServer);

                        //Extracting the sequence number sent by the client
                        sequenceNumFromClient = receivedDataPacket[0];


                        //Comparing the acknowledgement numbers of both the sides
                        if (sequenceNumFromClient == randomSequenceNumberAtServer)
                        {
                            System.out.println("Sequence Number Matched");
                            System.out.println("Packet successfully delivered");

                            sendACKToClient[0] = receivedDataPacket[0];

                            //Sending the same sequence number back to the client
                            sendAcknowledgement = new DatagramPacket(sendACKToClient, sendACKToClient.length, receivedPacket.getAddress(), receivedPacket.getPort());
                            ds.send(sendAcknowledgement);

                            System.out.println("Writing Data to file in chunks of 5 bytes");
                            fos.write(readDataBuffer1);
                            fos.write(readDataBuffer2);

                            //
                            //                        int size = receiveByteCount[0];
                            //                        if(size <= 10 && size >= 5)
                            //                        {
                            //                            fos.write(readDataBuffer1);
                            //                            size -= 5;
                            //                        }
                            //
                            //                        byte[] readDataBuffer3 = Arrays.copyOfRange(readDataBuffer, 5, 5 + size);
                            //                        fos.write(readDataBuffer3);

                            flag = false;

                            System.out.println("Server waiting for new packet");
                            System.out.println();
                        }
                        else
                        {
                            System.out.println("Sequence number mismatched.");
                            System.out.println("Packet got lost.");
                            System.out.println("Retransmission from client needs to be done");
                            System.out.println();
                        }

                    }

                    else
                    {
                        System.out.println("CHECKSUM ELEMENT DOES NOT MATCH");
                        System.out.println("Error bits Received");
                        System.out.println("Retransmission from client needs to be done");
                        System.out.println();
                    }
                    counter++;
                    match = 0;



                }

                for (int i = 0; i < receivedDataPacket.length; i++)
                {
                    receivedDataPacket[i] = 0;
                }

            }

            System.out.println("Server is closing");
            System.out.println("Uploading Successful");

            fos.close();
            ds.close();

        }
        catch (SocketException e)
        {
            System.out.println("Socket exception occured");
            System.out.println(e);
        }
        catch (IOException e)
        {
            System.out.println("IO exception encountered");
            System.out.println(e);
        }
    }


}
