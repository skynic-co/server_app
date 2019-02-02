import java.util.Random;

public class Main {
    public static void main(String args[]) throws InterruptedException {
//        SocketDriver serverSocketDriver = new SocketDriver();

        Thread t[] = new Thread[10];
        for (int i = 0 ; i < t.length ; i ++) {
            t[i] = new Thread(new Runnable() {
                public void run() {
                    while (true) {

                        System.out.println("Thread is" + Thread.currentThread().getId() + " running.");
                    }
                }
            });
            t[i].start();

        }
        int y = 0;
        while (true) {
//            t[0].interrupt();

            int i = new Random().nextInt(10);
            if (t[i] != null) {
                t[i] = null;
            }
//            t[i].interrupt();
            if (y == 10)
                break;
//            Thread.sleep(4000);
        }

    }
}