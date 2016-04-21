package SFTP_TCP;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Created by shubhamgoyal on 4/18/16.
 */
public class TCP_Server {


    public static void main(String[] args) {

        try {
            ServerSocket server = new ServerSocket(8090);
            System.out.println("Waiting for the Client....");

            Socket client = server.accept();
            System.out.println("Connection Established....");

            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));

//            int n = Integer.parseInt(in.readLine());
            int n = 1;

            String output = in.readLine();

            System.out.println("Output file name " + output);

            File file1 = new File(output);
            FileOutputStream fos = new FileOutputStream(file1);

            int size1 = Integer.parseInt(in.readLine());
            System.out.println("File size  = " + size1);

            if( n == 1)
            {

                int count = Integer.parseInt(in.readLine());
                System.out.println("Number of operations at server " + count);

                byte array1[];
                byte array2[];


                for(int i = 0; i < count; i++)
                {
                    int i1 = Integer.parseInt(in.readLine());
                    char[] arr = new char[i1];
                    in.read(arr, 0, i1);
                    String message = new String(arr);

                    System.out.println("message = " + message);
                    int size = message.length();
//                    System.out.println("size = " + size);
                    System.out.println("size = " +size);
                    System.out.println("Data sequences in chunk of 5s: ");
                    if(size <= 10 && size > 5)
                    {
                        array1 = new byte[5];
                        array2 = new byte[size - 5];
                        int j;
                        for( j = 0; j < 5 ; j++)
                        {
                            array1[j] = (byte) message.charAt(j);
                            System.out.print(message.charAt(j));
                        }
                        System.out.println();
                        for(int k = 0; k < size  - 5; k++,j++)
                        {
                            array2[k] = (byte) message.charAt(j);
                            System.out.print(message.charAt(j));
                        }
                        System.out.println();
                        fos.write(array1);
                        fos.write(array2);
                    }
                    else
                    {
                        array1 = new byte[size];
                        for(int j = 0; j < size ; j++)
                        {
                            array1[j]= (byte) message.charAt(j);
                            System.out.print(message.charAt(j));

                        }
                        fos.write(array1);
                    }
                    in.readLine();

                }
                System.out.println("Transfer complete");
                in.close();

                if(fos!=null)
                {
                    fos.close();
                }

                client.close();
                server.close();
            }



        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
