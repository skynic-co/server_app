import java.sql.*;
import java.util.Random;

public class MySqlDataBase {

    private String dataBaseName;
    private String userName = "root";
    private String password = "";
    private Connection connection;

    public MySqlDataBase() {

    }

    public void connect(String dataBaseName, String... arg) throws Exception {
        if (arg.length > 2) {
            throw new Exception("Input of method is wrong.");
        }
        if (arg.length == 1) {
            userName = arg[0];
        }
        if (arg.length == 2) {
            password = arg[1];
        }
        connect(dataBaseName);
    }

    public void connect(String dataBaseName) throws Exception {
        this.dataBaseName = dataBaseName;
        String url = "jdbc:mysql://localhost:3306/" + dataBaseName;
        Class.forName("com.mysql.jdbc.Driver");
        connection = DriverManager.getConnection(url, userName, password);
    }

    public ResultSet executeSelectCommand(String query) throws SQLException {
        Statement statement = connection.createStatement();
        return statement.executeQuery(query);
    }

    public void executeUpdateCommand(String query) throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeUpdate(query);
    }

    public void disconnect() throws SQLException {
        connection.close();
    }


    static int[][] test = new int[2][4];

    static int co = 10;

    public static void main(String args[]){


        Thread[] threads = new Thread[4];
        for (int i = 0 ; i < threads.length ; i ++) {
            threads[i] = new Thread(new Runnable() {
                public void run() {
                    MySqlDataBase mySqlDataBase = new MySqlDataBase();
                    try{
                        while (true) {
                            int n = Integer.parseInt(Thread.currentThread().getName());
                            mySqlDataBase.connect("lawyer");

                            if (new Random().nextBoolean()) {
                                String s = String.format("select * from testtable ");
                                ResultSet rs = mySqlDataBase.executeSelectCommand(s);
                                System.out.println(s);
                                test[0][n]++;
                            } else {
                                String s = String.format("insert into testtable (name) values ('Ibrahim %d')", n);
                                mySqlDataBase.executeUpdateCommand(s);
                                test[1][n]++;
                            }

//                        mySqlDataBase.executeUpdateCommand("insert into testtable (name) values ('Ibrahim')");
//                            if (n % 2 == 0) {
//                                String s = String.format("select * from testtable ");
//                                ResultSet rs = mySqlDataBase.executeSelectCommand(s);
//                                while (rs.next()){
//                                    System.out.println((Thread.currentThread()).getName() + "   " + rs.getInt(1)+"  "+rs.getString(2)+"  "+rs.getString(3));
//                                }
//                            } else {
//                                String s = String.format("insert into testtable (name) values ('Ibrahim %d')", n);
//                                mySqlDataBase.executeUpdateCommand(s);
//                            }

                            mySqlDataBase.disconnect();
                            Thread.sleep(new Random().nextInt(900));
                            if (test[0][n] + test[1][n] == co)
                                break;
                        }

                    }catch(Exception e){
                        System.out.println(e);
                        try {
                            mySqlDataBase.disconnect();
                        } catch (SQLException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            });

            threads[i].setName(i + "");
            threads[i].start();
        }


        for (Thread thread : threads) {
            if (thread != null) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        for (int i = 0 ; i < 4 ; i ++) {
            int s = test[0][0];
        }
    }
}
