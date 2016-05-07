package SFTP_TCP;

import java.util.*;
import java.io.*;
import java.net.*;


/**
 * Created by shubhamgoyal on 4/18/16.
 */
public class TCP_Client {


    public static void main(String[] args) {

        try {

            Scanner sc = new Scanner(System.in);

            System.out.println("Enter the port number");
            int port = sc.nextInt();

            System.out.println("Enter the Ip Address");
            String ip = sc.next();


            //Establishing connection with the specified port and IP
            Socket socket = new Socket(ip, port);

            //Setting input & output stream from the socket.
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintStream out = new PrintStream(socket.getOutputStream());

            System.out.println("Enter the name of input file");
            String input = sc.next();

            File file = new File(input);
            FileInputStream fis = new FileInputStream(file);

            System.out.println("Enter the name of output file");
            String output = sc.next();
            out.println(output);


            int size = fis.available();
            System.out.println("File size = " + size);
            out.println(size);

            //Calculating the iterations required to read file in chunks of 10 bytes.
            int count = size / 10;
            if (size % 10 != 0)
            {
                count++;
            }
            out.println(count);



            String message;
            byte[] myBuffer = new byte[10];
            char[] sendBuffer = new char[10];
            int bytesRead = 0;

            //Reading 10 bytes at a time and sending to the server untill EOF is reached.
            while ((bytesRead = fis.read(myBuffer)) != -1)
            {
                System.out.println("Transferring " + bytesRead + " bytes");
                out.println(bytesRead);

                //if bytes read is less than 10, then buffer size will be less.
                if (bytesRead != 10)
                {
                    char[] remainingBytes = new char[bytesRead];
                    for (int i = 0; i < bytesRead; i++)
                    {
                        remainingBytes[i] = (char) myBuffer[i];
                    }
                    message = new String();
                    System.out.println(message);
                    out.println(message);
                }
                else
                {
                    for (int i = 0; i < myBuffer.length; i++)
                    {
                        sendBuffer[i] = (char) myBuffer[i];
                    }

                    message = new String(sendBuffer);
                    out.println(message);
                    System.out.println(message);

                    for (int i = 0; i < 10; i++)
                    {
                        myBuffer[i] = 0;
                    }
                }
            }

            System.out.println("EOF.\nSend Succesful.\nClosing Connections....");

            in.close();
            out.close();

            if(fis != null)
            {
                fis.close();
            }

            socket.close();

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
