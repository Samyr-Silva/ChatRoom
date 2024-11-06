import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server {
    private final List<RunnableServer> lista = Collections.synchronizedList(new ArrayList<>());
    private String message;
    int portNumber = 8080;


    public Server(int portNumber) {
        this.portNumber = portNumber;
    }


    public static void main(String[] args) throws IOException {
        Server server = new Server(8080);
        server.start();
    }

    private class RunnableServer implements Runnable {
        private String name = "\033[36mAnonymous\033[m";
        private Socket socket;


        private RunnableServer(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {

            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                while (true) {
                    message = in.readLine();

                    if(message.toLowerCase().startsWith("/commands")){
                        send("\033[36mThese are the commands: \033[m");
                        send("\t\033[36m/name\033[m");
                        send("\t\033[36m/quit\033[m");
                        send("\t\033[36m/kick\033[m");
                        send("\t\033[36m/list\033[m");
                        send("\t\033[36m/whisper\033[m");
                        send("\t\033[36m/dadjoke\033[m");
                        send("\t\033[36m/commands\033[m");
                        continue;
                    }
                    if (message.toLowerCase().startsWith("/quit")) {
                        send("The " + this.name + " is  leaving the chat room");
                        quit(this);
                        continue;
                    }

                    if (message.toLowerCase().startsWith("/name")) {
                        String[] arr = message.split(" ");
                        name(arr[1]);
                        send(this.name + ": " + "This is my new name");
                        continue;
                    }

                    if (message.toLowerCase().startsWith("/kick")) {
                        String[] arr = message.split(" ");
                        send("\033[31mThe " + arr[1] + " is now out of the chat room\033[m");
                        kickSomeOne(arr[1]);
                        continue;
                    }

                    if (message.toLowerCase().startsWith("/list")){
                        send("\033[32mWe have this people on this chat room:\033[m");
                        for (RunnableServer name : lista){
                            send("\033[33mName: " + name.name);
                        }
                        continue;
                    }

                    if (message.toLowerCase().startsWith("/whisper")){
                        String[] arr = message.split(" ");
                        int conta = arr[1].length() + 9;
                        sendPrivate(arr[1], this.name + ": " + message.substring(conta));
                        continue;
                    }

                    if (message.toLowerCase().startsWith("/dadjoke")){
                        send(dadJoke());
                        continue;
                    }
                    send(this.name + ": " + message);
                }

            } catch (IOException e) {
                e.getMessage();
            } finally {
                quit(this); // GARANTE QUE THE CLIENT IS REMOVED
            }
        }

        public void send(String msg) {
            synchronized (lista) {
                for (RunnableServer re : lista) {
                    try {
                        PrintWriter out = new PrintWriter(re.socket.getOutputStream(), true);
                        out.println(msg);
                    } catch (Exception e) {
                        e.getMessage();
                    }
                }
                System.out.println("Enviando a mensagem");
            }
        }

        public void sendPrivate(String name, String msg) {
            synchronized (lista) {
                for (RunnableServer runnableServer : lista) {
                    if (runnableServer.name.equals(name)) {
                        try {
                            PrintWriter out = new PrintWriter(runnableServer.socket.getOutputStream(), true);
                            out.println(msg);
                        } catch (Exception e) {
                            e.getMessage();
                        }
                    }
                    System.out.println("Enviando mensagem privada");
                }
            }
        }


        public void name(String name) {
            this.name = name + "";
        }


        public void quit(RunnableServer r) {
            synchronized (lista) {
                try {
                    r.socket.close();
                    System.out.println("removendo o " + r.name);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                lista.remove(r);
            }
        }

        public void kickSomeOne(String name) {
            synchronized (lista) {
                for (RunnableServer l : lista) {
                    if (l.name.equals(name)) {
                        lista.remove(l);                // NAO SAQUEI MUITO BEM
                        quit(l);
                        break;
                    }
                }
            }
        }
    }
    public String dadJoke(){
        synchronized (lista) {
            List<String> jokes = new ArrayList<>();
            jokes.add("Why did the scarecrow win an award? Because he was outstanding in his field!");
            jokes.add("What do you call fake spaghetti? An impasta!");
            jokes.add("I only know 25 letters of the alphabet. I don’t know y!");
            jokes.add("How do you organize a space party? You planet!");
            int random = (int) Math.floor(Math.random() * jokes.size());
            String choise = jokes.get(random);

            return choise;
        }
    }
    public void start() {
        try {
            int port = 8080;

            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server socket created on port: " + port + ". :)");
            System.out.println("Waiting for a client connection...");

            while (true) {
                //waiting for a client connection: .accept()
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected");

                // CREATING UMA NOVA THREAD

                RunnableServer rs = new RunnableServer(clientSocket);
                Thread thread = new Thread(rs);
                thread.start();
                lista.add(rs);
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }
}

// KICK FUNCIONANDO E BEM
// COMO FAZER O COLOR E ACCHO QUR TA  BEM BOM