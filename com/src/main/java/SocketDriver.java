import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class SocketDriver {
    private int port = 8080;
    private ServerSocket serverSocket;
    private List<SocketThread> waitingDeviceList;
    private static SocketDriver driver;
    private String dbName = "bms";

    private List<SocketThread> waitingSocketThreadList;

    public SocketDriver() {
//        socketThreadList = new ArrayList<SocketThread>();

        new Thread(new Runnable() {
            public void run() {
                try {
                    serverSocket = new ServerSocket(port);
                    startServer();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private void startServer() {
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                SocketThread socketThread = new SocketThread(socket);
                waitingSocketThreadList.add(socketThread);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public synchronized void fireGotSocket(SocketThread socketThread, String command) {
        MySqlDataBase db = null;//MySqlDataBase.getInstance();

        if (command.startsWith("+Device:")) {
            try {
                db.connect(dbName);

                String serialNumber = command.substring(5, 15);
                String password = command.substring(16, 26);

                ResultSet set = db.executeSelectCommand(String.format("select * form tbl_device where serialNumber = '%s' and " +
                        "password = '%s'", serialNumber, password));
                set.last();
                if (set.getRow() > 0) {
                    waitingSocketThreadList.add(socketThread);
                    waitingSocketThreadList.remove(socketThread);
                    db.executeUpdateCommand(String.format("update tbl_device set isOnline = %b", true));
                } else {
                    socketThread.sendCommand("Information is wrong.");
                    socketThread.close();
                    socketThread.interrupt();
                }

                db.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (command.startsWith("+Client:")) {

            try {
                db.connect("lawyer");

                ResultSet set = db.executeSelectCommand(String.format("select password form tbl_device where device_id = '{}'",12));
                if (set.absolute(0)) {
                    String password = set.getString("password");
                    waitingDeviceList.add(socketThread);
                    waitingSocketThreadList.remove(socketThread);
                } else {
                    socketThread.sendCommand("");
                }
                db.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public static SocketDriver getInstance() {
        if (driver == null) {
            driver = new SocketDriver();
        }
        return driver;
    }


    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

}
