import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static PrintWriter out;
    private static BufferedReader in;
    private static Socket clientSocket;
    public static void main(String[] args) throws IOException {
        Scanner t = new Scanner(System.in);

        // STEP1: Get the host and the port from the command-line

        // STEP3: Setup input and output streams
        String hostName = "localHost";
        int portNumber = 8080;

        // STEP2: Open a client socket, blocking while connecting to the server
        clientSocket = new Socket(hostName, portNumber);

        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        // CREATING A NOVA THREAD
        Thread thread = new Thread(new RunnableClient());
        thread.start();

        // STEP4: Read from/write to the stream
        boolean sim = true;
        while (sim) {
            String message = t.nextLine();
            if(message != null) {
                out.println(message);
                if(message.startsWith("/quit")){
                    in.close();
                    out.close();
                    clientSocket.close();
                    break;
                }
            }
            else {
                in.close();
                out.close();
                clientSocket.close();
                break;
            }
        }
    }
    private static class RunnableClient implements Runnable{

        @Override
        public void run() {
            try{
                while (true) {
                    in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    String messagem = in.readLine();
                    if(messagem != null) {
                        System.out.println(messagem);
                    }
                    else {
                        in.close();
                        out.close();
                        clientSocket.close();
                        break;
                    }
                }
            }
            catch (IOException e){
                e.getMessage();
            }
        }
    }
}
