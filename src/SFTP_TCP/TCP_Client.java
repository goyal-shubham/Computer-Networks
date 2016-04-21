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

            Socket socket = new Socket(ip, port);
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

//            System.out.println("Select 1 for text file & 2 for binary file");
            int num  = 1;
            if(num == 1)
            {


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
                while ((bytesRead = fis.read(myBuffer)) != -1)
                {
                    System.out.println("Transferring " + bytesRead + " bytes");
                    out.println(bytesRead);
                    if (bytesRead != 10)
                    {
                        System.out.println("first");
                        char[] remainingBytes = new char[bytesRead];
                        for (int i = 0; i < bytesRead; i++)
                        {
                            remainingBytes[i] = (char) myBuffer[i];
                        }
                        message = new String(remainingBytes);
                        System.out.println(message);
                        out.println(message);
                    }
                    else
                    {
                        System.out.println("second");

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
            }
            else
            {
                byte bArray[]=new byte[size];
                char cArray[]=new char[size];
                String message;

                int num1 = fis.read(bArray);
                System.out.println("The number of bytes being read are:");
                System.out.println(num1);
                for(int we = 0; we < bArray.length; we++)
                {
                    cArray[we] = (char) bArray[we];
                }

                message=new String(cArray);
                System.out.println("The size of the string is:");
                System.out.println(message.length());

                out.println(message);

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
