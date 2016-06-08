package SFTP_UDP_StopAndWait;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.*;
import java.util.Scanner;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

/**
 * Created by shubhamgoyal on 5/28/16.
 */
public class UDPClient
{
    public static final int TIMEOUT_TIME = 2000;
    static int PORT = 1111;

    public static void main(String[] args) throws InterruptedException
    {
        try
        {

            Scanner sc = new Scanner(System.in);

            //To count the packets and number of resends.
            int totalPackets = 0, resendCount = 0;

           // System.out.println("Enter the PORT number");

            System.out.println("Enter the name of input file");
            String input = sc.next();

            File file = new File(input);
            FileInputStream fis = new FileInputStream(file);


            //Creating a socket and connecting to the PORT and inetAddress
            DatagramSocket ds = new DatagramSocket();

            InetAddress ia = InetAddress.getLocalHost();
            ds.connect(ia, PORT);

            System.out.println("UDP Client Socket has been created");


            int file_size = fis.available();
            System.out.println("File size = " + file_size);

//            //Calculating the last remainaing size of packet
//            int lastIterationSize = file_size % 10;
//
//            int numberOfIterations = 0;
//
//
//            //Calculating the Number of Iterations
//            if (lastIterationSize == 0)
//            {
//                numberOfIterations = file_size / 10;
//            }
//            else
//            {
//                numberOfIterations = (file_size / 10) + 1;
//            }
//
////            byte[] noOfIterationsArray = new byte[1];
////            noOfIterationsArray[0] = (byte) numberOfIterations;
////
////            DatagramPacket sendNoOfIterations = new DatagramPacket(noOfIterationsArray, noOfIterationsArray.length);
////            ds.send(sendNoOfIterations);
////            System.out.println("The number of iterations are: " + noOfIterationsArray[0]);


            //Initializing the sequence number
            byte sequenceNumber = 0;

            //Buffer to read data from file
            byte[] myReadBuffer = new byte[10];

            //Counter for the bytesRead
            int bytesRead;

            while ((bytesRead = fis.read(myReadBuffer)) != -1)
            {
                //Sending a packet to inform server about bytes to send from client
                byte[] dataRead = new byte[1];
                dataRead[0] = (byte) bytesRead;

                System.out.println("Read " + bytesRead + " bytes from file");
                DatagramPacket sendByteCount = new DatagramPacket(dataRead, 1);
                ds.send(sendByteCount);

//                for (int i = 0; i < bytesRead; i++)
//                {
//                    System.out.print(myReadBuffer[i] + '\t');
//                }
//                System.out.println();


                //Buffer size is 21 ( 1 for sequence number , 10 for checksum, 10 for data chunk)
                byte[] dataToSend = new byte[21];

                //First bit is sequence number
                dataToSend[0] = sequenceNumber;

                //Saving file read buffer data to dataToSend Buffer
                for (int j = 11; j < dataToSend.length; j++)
                {
                    dataToSend[j] = myReadBuffer[j - 11];
                }


               //Using CRC32 class to compute the CRC-32 checksum of a data stream.
                Checksum cs = new CRC32();
                cs.update(myReadBuffer, 0, myReadBuffer.length);

                //Long value of checkSum
                long clientChecksum = cs.getValue();
                System.out.println("Client CheckSum: " + clientChecksum);

                String checksumString = String.valueOf(clientChecksum);


                //Updating Checksum if length is less than 10
                int checkSumLength = checksumString.length();
                int diff = 10 - checkSumLength;
                while(diff-- > 0)
                {
                    checksumString = '0' + checksumString;
                }
                byte checksumArray[] = checksumString.getBytes();

//                //System.out.println("The byte array of the checksum looks like follows:");
//                for (int ca = 0; ca < checksumArray.length; ca++)
//                {
//                    System.out.print(checksumArray[ca] + "\t");
//                }


                //Adding checksum value in dataToSend as header
                for (int i1 = 0; i1 < checksumArray.length; i1++)
                {
                    dataToSend[i1 + 1] = checksumArray[i1];
                }



                boolean flag = true;

                while (flag)
                {
                    //Printing the Datagram packet values

                    System.out.println("Packet to send:");

                    for (int pack = 0; pack < dataToSend.length; pack++)
                    {
                        System.out.print(dataToSend[pack] + "\t");
                    }

                    System.out.println();

                    //Sending the constructed packet to the server side
                    DatagramPacket packetToSend = new DatagramPacket(dataToSend, dataToSend.length);
                    ds.send(packetToSend);


                    try
                    {
                        //Setting the timer to receive the acknowledgement
                        ds.setSoTimeout(TIMEOUT_TIME);
//                        Thread.sleep(2000);

                        byte ackFromServer[] = new byte[1];
                        DatagramPacket receivedACK = new DatagramPacket(ackFromServer, ackFromServer.length);
                        ds.receive(receivedACK);

                        System.out.println("Packet Successfuly Delivered");
                        System.out.println("Sending next Packet");
                        System.out.println();
                        totalPackets++;
                        sequenceNumber =  (byte ) (sequenceNumber == 1 ? 0 : 1) ;
                        flag = false;
                    }

                    catch (SocketTimeoutException e)
                    {
                        System.out.println("**Timeout**");
                        System.out.println("Packet Either Lost or Error bits were sent");
                        System.out.println("No Acknowledgement from Server");
                        System.out.println("Retransmitting same Datagram Packet");
                        System.out.println();
                        resendCount++;

                    }

                    System.out.println("Total Packets send = " + totalPackets);
                    System.out.println("Resend count = " + resendCount);
                }

                flag = true;

                for(int i = 0; i < myReadBuffer.length; i++)
                {
                    myReadBuffer[i] = 0;
                }
                //reset the dataToSend Buffer
//                for (int i = 0; i < dataToSend.length; i++)
//                {
//                    dataToSend[i] = 0;
//                }
            }

            System.out.println("End of File");
            System.out.println("No more packets to send.");
            System.out.println("Total Packets send = " + totalPackets);
            System.out.println("Resend count = " + resendCount);

            //Closing the Scanner
            sc.close();

            //Closing the FileInputStream
            fis.close();

            //Closing the UDP socket
            ds.close();
        }
        catch (SocketException e)
        {
            System.out.println("Socket exception");
            System.out.println(e);
        }
        catch (UnknownHostException e)
        {
            System.out.println("Unknown host exception");
            System.out.println(e);
        }
        catch (IOException e)
        {
            System.out.println("IO exception");
            System.out.println(e);
        }

    }
}