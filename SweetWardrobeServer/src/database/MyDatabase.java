package database;

//import sun.awt.windows.WEmbeddedFrame;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyDatabase {

    private String driver;
    private String url;
    private String username;
    private String password;
    private Connection conn;
    private Statement stmt;
    private PreparedStatement pstmt;
    private ResultSet rs;

    MyDatabase(String m_driver, String m_url, String m_username, String m_password) {
        driver = m_driver;
        url = m_url;
        username = m_username;
        password = m_password;
        conn = null;
        stmt = null;
        pstmt = null;
    }

    MyDatabase() {
        driver = "com.mysql.jdbc.Driver";
        url = "jdbc:mysql://localhost:3306/mysql?useUnicode=true&characterEncoding=utf-8&useSSL=false";
        username = "root";
        //这里应该换成我的密码
        //password = "19980327cjh";
        password="1234";		
        conn = null;
        stmt = null;
        pstmt = null;
    }

    private List<Integer> transformClothesStringToInteger(String clothesString) {
        List<Integer> clothesList = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(clothesString);
        while (matcher.find()) {
            clothesList.add(Integer.parseInt(matcher.group(0)));
        }
        return clothesList;
    }

    //这个函数可能要修改----------------------------------------------------->
    //需要将字符串重新组装成weather
    private List<Weather> transformWeatherStringToWeather(String weatherString) {
        List<Weather> weatherList = new ArrayList<>();
        Pattern pattern = Pattern.compile("[A-Z]+");
        Matcher matcher = pattern.matcher(weatherString);
        while (matcher.find()) {
            weatherList.add(WeatherState.valueOf(matcher.group(0)));
        }
        return weatherList;
    }

    private List<Suit> transformSuitStringToSuit(String suitString) {
        List<Suit> suitList = new ArrayList<>();
        String[] suits = suitString.split(";");
        for(String clothesString : suits){
            List<Integer> clothesList = transformClothesStringToInteger(clothesString);
            Suit suit = new Suit(clothesList);
            suitList.add(suit);
        }

        return suitList;
    }

    private void closeDatabase() {
        if (rs != null) { //关闭结果集对象
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (stmt != null) { // 关闭数据库操作对象
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (conn != null) { // 关闭数据库连接对象
            try {
                if (!conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }   //关闭对象，回收数据库资源

    //初始化userlist表和clotheslist表
    public boolean initTable() {
        String sql;
        try {
            // 1、加载数据库驱动（ 成功加载后，会将Driver类的实例注册到DriverManager类中）
            Class.forName(driver);
            // 2、获取数据库连接
            conn = DriverManager.getConnection(url, username, password);
            // 3、获取数据库操作对象
            stmt = conn.createStatement();
            // 4、定义操作的SQL语句
            sql = "drop table if exists userlist";
            stmt.executeUpdate(sql);
            sql = "drop table if exists clotheslist";
            stmt.executeUpdate(sql);
            sql = "create table if not exists userlist(" +
                    "User_Id int not null, " +
                    "User_Name varchar (20), " +
                    "User_Password varchar (40), " +
                    "Clothes varchar (200), " +
                    "SuitHistories varchar (200), " +
                    "WeatherHistories varchar (200), " +
                    "HistoryDays int not null, " +
                    "primary key(User_Id))";
            stmt.executeUpdate(sql);
            System.out.println("创建数据表userlist成功");
            sql = "create table if not exists clotheslist(" +
                    "Clothes_Id int not null, " +
                    "Clothes_Class int not null, " +
                    "Clothes_Type int not null, " +
                    "Clothes_Color int not null, " +
                    "Clothes_DirtyDegree int not null, " +
                    "primary key(Clothes_Id))";
            stmt.executeUpdate(sql);
            System.out.println("创建数据表clotheslist成功");
        } catch (Exception e) {
            e.printStackTrace();
            closeDatabase();
            return false;
        } finally {
            // 7、关闭对象，回收数据库资源
            closeDatabase();
        }
        return true;
    }

    //插入新用户id, name, password，已存在则不插入
    public boolean insertUser(Integer userId, String userName, String userPassword) {

        String sql;
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, username, password);
            stmt = conn.createStatement();
            sql = "insert into userlist (User_Id, User_Name, User_Password, Clothes, SuitHistories, WeatherHistories, HistoryDays) " +
                    "values (?, ?, ?, ?, ?, ?, ?);";
          /*  sql = "insert into userlist (User_Id, User_Name, User_Password, Clothes, SuitHistories, WeatherHistories, HistoryDays) " +
                    "select * from (SELECT ?, ?, ?, ?, ?, ?, ?) as tmp " +
                    "where not exists( " +
                    "    select User_id FROM userlist WHERE User_id = ? " +
                    ") LIMIT 1;";                    */
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setString(2, userName);
            pstmt.setString(3, userPassword);
            pstmt.setString(4, ".");
            pstmt.setString(5, ".");
            pstmt.setString(6, ".");
            pstmt.setInt(7, 0);
            pstmt.executeUpdate();
            System.out.println("添加用户成功");
        } catch (Exception e) {
            e.printStackTrace();
            closeDatabase();
            return false;
        } finally {
            closeDatabase();
        }
        return true;
    }

    //删除用户，这个类似注销
    public boolean deleteUser(Integer userId) {
        String sql;
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, username, password);
            stmt = conn.createStatement();
            sql = "delete from userlist where User_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
            System.out.println("删除用户成功");
        } catch (Exception e) {
            e.printStackTrace();
            closeDatabase();
            return false;
        } finally {
            closeDatabase();
        }
        return true;
    }

    //插入衣物
    public boolean insertClothes(Integer userId, Clothes clothes) {
        String sql;
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, username, password);
            stmt = conn.createStatement();
            sql = "insert into clotheslist (Clothes_Id, Clothes_Class, Clothes_Type, Clothes_color, Clothes_DirtyDegree) " +
                    "values (?, ?, ?, ?, ?);";
         /*   sql = "insert into clotheslist (Clothes_Id, Clothes_Class, Clothes_Type, Clothes_color, Clothes_DirtyDegree) " +
                    "select * from (SELECT ?, ?, ?, ?, ?) as tmp " +
                    "where not exists( " +
                    "    select Clothes_Id FROM clotheslist WHERE Clothes_Id = ? " +
                    ") LIMIT 1;";*/
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, clothes.getClothesId());
            pstmt.setInt(2, clothes.getClothesClass());
            pstmt.setInt(3, clothes.getClothesType());
            pstmt.setInt(4, clothes.getClothesColor());
            pstmt.setInt(5, clothes.getClothesDirtyDegree());
            pstmt.executeUpdate();

            sql = "update userlist set Clothes = concat(Clothes, ?) where User_Id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, clothes.transformClothesToString());
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();

            System.out.println("插入衣物成功");

        } catch (Exception e) {
            e.printStackTrace();
            closeDatabase();
            return false;
        } finally {
            closeDatabase();
        }
        return true;
    }

    //插入穿衣历史和天气历史
    //这里也要修改---------------------------------------------------------------->
    public boolean insertHistory(Integer userId, Suit suit, Weather weather) {
        String sql;
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, username, password);
            stmt = conn.createStatement();
            sql = "select HistoryDays from userlist where User_Id = ?;";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();
            String suitHistory = suit.transformSuitToString();
            String weatherHistory = weather.name() + ";";
            while(rs.next()) {
                if (rs.getInt("HistoryDays") < 30) {
                    sql = "update userlist set SuitHistories = concat(SuitHistories, ?), WeatherHistories = concat(WeatherHistories, ?), HistoryDays = HistoryDays + 1 where User_Id = ?";
                    pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, suitHistory);
                    pstmt.setString(2, weatherHistory);
                    pstmt.setInt(3, userId);
                    pstmt.executeUpdate();
                } else {
                    sql = "select * from userlist where User_Id = ?";
                    pstmt = conn.prepareStatement(sql);
                    pstmt.setInt(1, userId);
                    ResultSet rs_history = pstmt.executeQuery();
                    while(rs_history.next()) {
                        String suitHistories = rs_history.getString("SuitHistories");
                        String weatherHistories = rs_history.getString("WeatherHistories");
                        int i = suitHistories.indexOf(';');
                        suitHistories = "." + suitHistories.substring(i + 1) + suitHistory;
                        i = weatherHistories.indexOf(';');
                        weatherHistories = "." + weatherHistories.substring(i + 1) + weatherHistory;

                        sql = "update userlist set SuitHistories = ?, WeatherHistories = ? where User_Id = ?";
                        pstmt = conn.prepareStatement(sql);
                        pstmt.setString(1, suitHistories);
                        pstmt.setString(2, weatherHistories);
                        pstmt.setInt(3, userId);
                        pstmt.executeUpdate();
                    }
                }
            }

            System.out.println("插入衣物和天气历史成功");

        } catch (Exception e) {
            e.printStackTrace();
            closeDatabase();
            return false;
        } finally {
            closeDatabase();
        }
        return true;
    }

    //得到用户信息
    public UserInfo getUserInfoById(Integer userId) {

        UserInfo userInfo = new UserInfo();
        String sql;
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, username, password);
            stmt = conn.createStatement();

            sql = "select * from userlist where User_Id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();

            while (rs.next()) {

                //userInfo.name
                String userName = rs.getString("User_Name");

                //userInfo.password
                String userPassword = rs.getString("User_PassWord");

                //userInfo.clothesInfo.suitHistory
                String suitString = rs.getString("SuitHistories");
                List<Suit> suitHistory = transformSuitStringToSuit(suitString);

                //userInfo.clothesInfo.weatherHistory
                String weatherString = rs.getString("WeatherHistories");
                List<Weather> weatherHistory = transformWeatherStringToWeather(weatherString);

                //userInfo.clothesInfo.clothesList
                String clothesString = rs.getString("Clothes");
                List<Clothes> userClothesList = new ArrayList<>();
                List<Integer> userClothesIdList = transformClothesStringToInteger(clothesString);
                for (Integer clothesId : userClothesIdList) {
                    sql = "select * from clotheslist where Clothes_Id = ?;";
                    pstmt = conn.prepareStatement(sql);
                    pstmt.setInt(1, clothesId);
                    ResultSet rs_clothes = pstmt.executeQuery();
                    while (rs_clothes.next()) {
                        List<Integer> features = new ArrayList<>(5);
                        features.add(rs_clothes.getInt("Clothes_Id"));
                        features.add(rs_clothes.getInt("Clothes_Class"));
                        features.add(rs_clothes.getInt("Clothes_Type"));
                        features.add(rs_clothes.getInt("Clothes_Color"));
                        features.add(rs_clothes.getInt("Clothes_DirtyDegree"));
                        Clothes clothes = new Clothes(features);
                        userClothesList.add(clothes);
                    }
                }

                //userInfo.clothesInfo
                ClothesInfo clothesInfo = new ClothesInfo(suitHistory, weatherHistory, userClothesList);

                userInfo.setInfo(userId, userName, userPassword, clothesInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
            closeDatabase();
            return null;
        } finally {
            closeDatabase();
        }
        System.out.println("导出用户信息成功");
        return userInfo;
    }
}
