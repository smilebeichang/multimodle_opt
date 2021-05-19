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
                ps = conn.prepareStatement("select * from sysu.adi order by id  limit 62;");
                rs = ps.executeQuery();
//                System.out.println("id"+"\t\t"+"pattern");
                while(rs.next()) {
                    int id = rs.getInt("id");
                    String attributes = rs.getString("pattern");
//                    System.out.print(id+"\t\t"+attributes);
//                    System.out.println();
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
    public  void insert(int id,String pattern,Double base,String penalty,Double adi1_r,Double adi2_r,Double adi3_r,Double adi4_r,Double adi5_r) {
        Connection conn ;
        PreparedStatement ps ;

        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn =
                    DriverManager.getConnection("jdbc:mysql://localhost/sysu?"+"user=root&password=root");
            System.out.println("数据库连接成功");
            String sql = "INSERT INTO sysu.adi \n" +
                    "(id, pattern, base, penalty, adi1_r, adi2_r, adi3_r, adi4_r, adi5_r) \n" +
                    "VALUES("+id+",\""+pattern+"\","+base+",\""+penalty+"\","+adi1_r+","+adi2_r+","+adi3_r+","+adi4_r+","+adi5_r+");";
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


    /**
     * 新增到数据库
     */
    public  void updateDina(int id,double ps1,double pg,double adi1_d,double adi2_d,double adi3_d,double adi4_d,double  adi5_d) {
        Connection conn ;
        PreparedStatement ps ;

        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn =
                    DriverManager.getConnection("jdbc:mysql://localhost/sysu?"+"user=root&password=root");
            System.out.println("数据库连接成功");
            String sql = "UPDATE sysu.adi \n" +
                    "SET adi1_d="+adi1_d +", adi2_d=" +adi2_d+", " +
                    "adi3_d="+ adi3_d+", adi4_d="+adi4_d+", adi5_d="+adi5_d+", " +
                    "ps="+ps1+", pg="+pg+" where id="+id+" ;";
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



