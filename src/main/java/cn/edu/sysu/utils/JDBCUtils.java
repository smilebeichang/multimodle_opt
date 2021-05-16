package cn.edu.sysu.utils;




import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;



/**
 * @Author : song bei chang
 * @create 2021/5/2 7:24
 */
public class JDBCUtils {

    public static void main(String []args) {

        //insert(777,"'[a,d]'");
        //select();
    }


    /**
     * 查询，并返回list   limit4
     */
    public  ArrayList select() {
            ArrayList list = new ArrayList();
            Connection conn ;
            PreparedStatement ps ;
            ResultSet rs ;
            try {
                Class.forName("com.mysql.jdbc.Driver");
                System.out.println("驱动加载成功");
            } catch (Exception ex) {
                System.out.println("驱动加载失败");
                ex.printStackTrace();
            }

            try {
                conn =
                        DriverManager.getConnection("jdbc:mysql://localhost/sysu?"+"user=root&password=root");
                System.out.println("数据库连接成功");
                ps = conn.prepareStatement("select id,attributes from question order by id  limit 4;");
                rs = ps.executeQuery();
                System.out.println("id"+"\t\t"+"attributes");
                while(rs.next()) {
                    int id = rs.getInt("id");
                    String attributes = rs.getString("attributes");
                    System.out.print(id+"\t\t"+attributes);
                    System.out.println();
                    list.add(id+":"+attributes);
                }

            } catch (SQLException ex) {
                System.out.println("SQLException: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("VendorError: " + ex.getErrorCode());
            }
            System.out.println();
            return list;
    }

    /**
     * 新增到数据库
     */
    public  void insert(int id,String attributes) {
        Connection conn ;
        PreparedStatement ps ;

        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn =
                    DriverManager.getConnection("jdbc:mysql://localhost/sysu?"+"user=root&password=root");
            System.out.println("数据库连接成功");
            String sql = "insert into question values ("+id+","+ attributes +");";
            System.out.println(sql);
            ps = conn.prepareStatement(sql);
            ps.execute();

        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}



