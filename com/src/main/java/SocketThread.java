import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class SocketThread extends Thread {
    private InputStream in;
    private OutputStream out;
    private Socket socket;
    private SocketType socketType;
    private boolean enable;

    private SocketThread socketThread;

    public SocketThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            in = socket.getInputStream();
            out = socket.getOutputStream();
            while (true) {
                try {
                    int length = in.available();
                    if (length > 0) {
                        byte[] data = new byte[length];
                        in.read(data, 0, length);
                        String line = new String(data);

                        SocketDriver.getInstance().fireGotSocket(SocketThread
                        .this, line);

                        if (line.equalsIgnoreCase("QUIT")) {
                            socket.close();
                            return;
                        }

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }

            }



        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void sendCommand(String command) throws IOException {
        if (enable) {
            out.write(command.getBytes());
        }
    }

    public void close() throws IOException {
        in.close();
        out.close();
        socket.close();
    }

    public SocketThread getSocketThread() {
        return socketThread;
    }

    public void setSocketThread(SocketThread socketThread) {
        this.socketThread = socketThread;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    enum SocketType{
        Device,
        Client,
    }
}
