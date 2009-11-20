package org.spantus.integration.skype.call;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.ServerSocket;
import java.net.Socket;
import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import org.apache.log4j.Logger;

public class CallStreamServer implements Runnable {

    public static final int outputPort = 9092;

    Logger log = Logger.getLogger(CallStreamServer.class);
//    private int inputPortNumber;
    private int outputPortNumber;
//    private ServerSocket incomeStream;
    private ServerSocket outcomeStream;

    public CallStreamServer(int outputPortNumber) {
       this.outputPortNumber = outputPortNumber;
        try {
//            incomeStream = new ServerSocket(getInputPortNumber());
            outcomeStream = new ServerSocket(getOutputPortNumber());
             log.debug("Created socket" + outcomeStream.toString());
        } catch (IOException e) {
            log.error(e);
        }
    }

    public void run() {
        
        while (true) {
            try {
//                Socket socket = incomeStream.accept();
                log.debug("Waiting for client message...");
                Socket outcomeStreamSocket = outcomeStream.accept();
                 log.debug("accepted message..." + outcomeStreamSocket.toString());
                new ConnectionHandler(outcomeStreamSocket);
            } catch (IOException ex) {
                log.error("error", ex);
            }
            log.debug("dying...");
        }
         
    }



//    public int getInputPortNumber() {
//        return inputPortNumber;
//    }
//
//    public void setInputPortNumber(int inputPortNumber) {
//        this.inputPortNumber = inputPortNumber;
//    }


    public int getOutputPortNumber() {
        return outputPortNumber;
    }

    public void setOutputPortNumber(int outputPortNumber) {
        this.outputPortNumber = outputPortNumber;
    }


    
    class ConnectionHandler implements Runnable {

        private Socket socket;

        public ConnectionHandler(Socket socket) {
            this.socket = socket;

            Thread t = new Thread(this);
            t.start();
        }

        public void run() {
            try {
                //
                // Read a message sent by client application
                //
                AudioFormat audioFormat = new AudioFormat((float) 16000.0, 16, 1, true, false);
                AudioInputStream  is = new AudioInputStream(
                        new DataInputStream(socket.getInputStream()), audioFormat, 2000);
                File file = new File("./",System.currentTimeMillis()+".wav");
                FileOutputStream fos = new FileOutputStream(file);
                AudioSystem.write(is, Type.WAVE, file);

                

                //
                // Send a response information to the client application
                //
////                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
//                log.debug("writing stream: " + socket);
//                int readBytes = 0;
//                char[] bytes = new char[2];
//                while (readBytes != -1) {
//                    readBytes = is.read(bytes);
////                    log.debug(readBytes);
//                    fos.write(readBytes);
////                    oos.write(bytes);
//                }
                is.close();
//                oos.close();
                socket.close();
                log.debug("closing stream: ");
            } catch (IOException e) {
                log.error("Error: ", e);
            }
        }
    }
}