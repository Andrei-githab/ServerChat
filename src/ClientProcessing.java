import java.io.*;
import java.net.Socket;
import java.security.PublicKey;
import java.util.HashMap;

public class ClientProcessing implements Runnable {
    // экземпляр Loger'a
    MyLog myLog = new MyLog();
    // экземпляр PGP
    PGP pgp = new PGP();
    DB db = new DB();
    //сокет для общения
    private Socket socket = null;
    // экземпляр сервера
    private ChatServer server;
    // сокет, через который сервер общается с клиентом, кроме него - клиент и сервер никак не связаны
    private Socket socketDialog;
    // поток чтения из сокета
    private BufferedReader in;
    // поток завписи в сокет
    private BufferedWriter out;
    // пременая ключа
    private PublicKey publicKeyClient;
    // хронилище ключь + номер клиента
    private HashMap<Integer, PublicKey> hashMap = new HashMap<Integer, PublicKey>();
    // пременая для номера клиента
    private static int clients_count = 0;

    public ClientProcessing(Socket socket, ChatServer chatServer) throws IOException, ClassNotFoundException {
        this.socketDialog = socket;
        try {
            // создам поток для сериализации публичного ключа сервера и очищает буфер (сбрасываем его содержимое в выходной
            // поток (на клиент))
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(pgp.getPublicKey());
            objectOutputStream.flush();

            // принемаем ключ от клиента и записываем его в пременую publicKeyClient
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            publicKeyClient = (PublicKey)objectInputStream.readObject();
            //System.out.println("Ключ клиента: " + publicKeyClient);

            // создаем номер клиента
            clients_count++;
            // записываем номер клиента и его ключь в HashMap
            hashMap.put(clients_count, publicKeyClient);

            this.socket = socket;
            this.server = chatServer;

            //поток для отправки сообщений
            this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            //поток для приема сообщений
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // в поток вывода передаётся история чата из БД
            db.printChatStory();
            for (String vr : db.story){
                vr = pgp.encrypt(vr, publicKeyClient);
                out.write(vr + "\n");
                out.flush();
            }

        } catch (Exception exception) {
            System.out.println(exception);
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                if(in.ready()) {
                    // получаем зашифрованое сообщение от usera
                    String message = in.readLine();
                    System.out.println("prishlo ot usera: " + message);
                    // дишифруем сообщение от usera
                    message = pgp.decrypt(message);
                    // сообщения предаются в БД (в историю)
                    db.addDB(message);
                    System.out.println("Message from user: " + message);
                    myLog.printStory(message);
                    // отсылаем сообщение другим клиентам включая его самого
                    server.sendMessageToAllClients(message);
                }
            }

        } catch (Exception ignored) { }
        finally { /// попробвать убрать
            this.downService();
        }
    }

    /**
     * Метод отправки сообщений
     */
    public void sendMessage(String message)  {
        try {
            for (HashMap.Entry entry: hashMap.entrySet()) {
                //шифруем полученное сообщение и отправляем его клиентам
                message = pgp.encrypt(message, (PublicKey)entry.getValue());
                System.out.println("ushlo k useru: " + message);
                out.write(message);
                out.newLine();
                out.flush();
            }

        } catch (Exception ioException) {
            System.err.println(ioException);
        }
    }

    /**
     * Метод закрытия соединения клиента с сервером
     */
    private void downService() {
        try {
            if (!socket.isClosed()) {
                server.deleteThread(this);
                out.close();
                in.close();
                socket.close();
                clients_count--;
            }

        } catch (IOException e) {System.err.println(e);}
    }
}

