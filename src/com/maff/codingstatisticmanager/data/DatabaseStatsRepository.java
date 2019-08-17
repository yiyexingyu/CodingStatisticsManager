package com.maff.codingstatisticmanager.data;

import java.sql.*;

public class DatabaseStatsRepository {

    private static final String USER = "root";
    private static final String PASSWORD = "h0670131005";
    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/CodingCounter?useSSL=false";

    private String connectMessage;
    private Connection connection = null;

    public String getConnectMessage() {
        return connectMessage;
    }

    private static DatabaseStatsRepository instance = null;

    private DatabaseStatsRepository() {
        this.connectMessage = connectToDatabase();
    }

    public static void initDatabaseStatsRepository(String jdbc_device, String db_url, String user, String password){

    }

    public static DatabaseStatsRepository getInstance() {
        if (instance == null) {
            instance = new DatabaseStatsRepository();
        }
        return instance;
    }

    private String connectToDatabase() {
        String resultMessage = null;

        try {
            // 注册 JDBC 驱动
            Class.forName(JDBC_DRIVER);

            // 连接数据库
            this.connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            resultMessage = "数据库连接成功";

        } catch (ClassNotFoundException e) {
            // e.printStackTrace();
            resultMessage = "找不到数据库驱动程序： " + JDBC_DRIVER;
        } catch (SQLException e) {
            resultMessage = "连接数据库失败, user: " + USER + ", " + "database: " + DB_URL;
            e.printStackTrace();
        }
        return resultMessage;
    }

    public void disconnectDatabase() {
        try { if (this.connection != null) this.connection.close(); } catch (SQLException ignored) { }
    }

    private int calculateTotalCount(PeriodStats periodStats) {
        return 0;
    }

    private int calculateValidCount(PeriodStats periodStats) {
        return 0;
    }

    public void updateAllDataToDb(CodingStats codingStats) {
        this.updateDataToDb(Period.今天, codingStats.periods.get(Period.今天));
        this.updateDataToDb(Period.本周, codingStats.periods.get(Period.本周));
        this.updateDataToDb(Period.本月, codingStats.periods.get(Period.本月));
        this.updateLastEventTime(codingStats.lastEventTime);
    }

    public void insertDataToDb(Period period, PeriodStats periodStats) {
        PreparedStatement preparedStatement = null;
        try {
            String sql_string = SqlString.PERIOD_INSERT_SQL.get(period);
            preparedStatement = this.connection.prepareStatement(sql_string);

            // 计算数据
            int total_coding_count = this.calculateTotalCount(periodStats);
            int valid_coding_count = this.calculateValidCount(periodStats);

            // 设置数据
            preparedStatement.setObject(1, total_coding_count);
            preparedStatement.setObject(2, valid_coding_count);
            preparedStatement.setObject(3, periodStats.type);
            preparedStatement.setObject(4, periodStats.backDel);
            preparedStatement.setObject(5, periodStats.cut);
            preparedStatement.setObject(6, periodStats.paste);
            preparedStatement.setObject(7, periodStats.remove);
            preparedStatement.setObject(8, periodStats.insert);
            preparedStatement.setObject(9, new Date(System.currentTimeMillis()));
            // preparedStatement.setDate(9, new Date(System.currentTimeMillis()));

            // 执行插入语句
            preparedStatement.execute();

        } catch (SQLException ignored) { System.out.println(ignored); } finally {
            try { if (preparedStatement != null) preparedStatement.close(); } catch (SQLException ignored) { }
        }
    }

    private void updateDataToDb(Period period, PeriodStats periodStats) {
        System.out.println("准备更新数据：" + period.name());
        PreparedStatement preparedStatement = null;
        try {
            String sql_string = SqlString.PERIOD_UPDATE_SQl.get(period);
            preparedStatement = this.connection.prepareStatement(sql_string);

            // 计算数据
            int total_coding_count = this.calculateTotalCount(periodStats);
            int valid_coding_count = this.calculateValidCount(periodStats);

            preparedStatement.setObject(9, getLastId(period));
            preparedStatement.setObject(1, total_coding_count);
            preparedStatement.setObject(2, valid_coding_count);
            preparedStatement.setObject(3, periodStats.type);
            preparedStatement.setObject(4, periodStats.backDel);
            preparedStatement.setObject(5, periodStats.cut);
            preparedStatement.setObject(6, periodStats.paste);
            preparedStatement.setObject(7, periodStats.remove);
            preparedStatement.setObject(8, periodStats.insert);

            preparedStatement.executeUpdate();
        } catch (SQLException ignored) { } finally {
            try { if (preparedStatement != null) preparedStatement.close(); } catch (SQLException ignored) { }
        }
        System.out.println("更新数据完成：" + period.name());
    }

    private PeriodStats getLastPeriodStats(Period period) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        PeriodStats periodStats = new PeriodStats();

        try {
            preparedStatement = this.connection.prepareStatement(SqlString.PERIOD_LAST_STATS_SQl.get(period));
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int total_coding_count = resultSet.getInt("total_coding_count");
                int valid_coding_count = resultSet.getInt("valid_coding_count");
                periodStats.type = resultSet.getInt("type_coding_count");
                periodStats.backDel = resultSet.getInt("back_del_coding_count");
                periodStats.cut = resultSet.getInt("cut_coding_count");
                periodStats.paste = resultSet.getInt("paste_coding_count");
                periodStats.remove = resultSet.getInt("remove_coding_count");
                periodStats.insert = resultSet.getInt("insert_coding_count");
            } else {
                insertDataToDb(period, new PeriodStats());
                periodStats = getLastPeriodStats(period);
            }

        } catch (SQLException ignored) {} finally {
            try { if(resultSet != null) resultSet.close(); } catch (SQLException ignored){}
            try { if(preparedStatement != null) preparedStatement.close(); } catch (SQLException ignored){}
        }

        return periodStats;
    }

    private int getLastId(Period period){
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        int last_id = 0;

        try {
            preparedStatement = this.connection.prepareStatement(SqlString.PERIOD_LAST_STATS_SQl.get(period));
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                last_id = resultSet.getInt("id");
            } else {
                insertDataToDb(period, new PeriodStats());
                last_id = getLastId(period);
            }

        } catch (SQLException ignored) {} finally {
            try { if(resultSet != null) resultSet.close(); } catch (SQLException ignored){}
            try { if(preparedStatement != null) preparedStatement.close(); } catch (SQLException ignored){}
        }

        return last_id;
    }

    private long getLastEventTime() {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        long last_event_time = 0;

        try {
            preparedStatement = this.connection.prepareStatement(SqlString.PERIOD_LAST_STATS_SQl.get(Period.今天));
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                last_event_time = resultSet.getLong("last_event_time");
            }

        } catch (SQLException ignored) {} finally {
            try { if(resultSet != null) resultSet.close(); } catch (SQLException ignored){}
            try { if(preparedStatement != null) preparedStatement.close(); } catch (SQLException ignored){}
        }

        return last_event_time;
    }

    public void updateLastEventTime(long time){
        PreparedStatement preparedStatement = null;
        try {
            String sql_string = SqlString.UPDATE_LAST_EVENT_TIME;
            preparedStatement = this.connection.prepareStatement(sql_string);

            preparedStatement.setObject(1, time);
            preparedStatement.setObject(2, getLastId(Period.今天));

            preparedStatement.executeUpdate();
        } catch (SQLException ignored) {} finally {
            try { if (preparedStatement != null) preparedStatement.close(); } catch (SQLException ignored) { }
        }
    }

    public CodingStats loadLastCodingStats() {

        CodingStats codingStats = new CodingStats();
        codingStats.lastEventTime = this.getLastEventTime();
        codingStats.periods.put(Period.今天, this.getLastPeriodStats(Period.今天));
        codingStats.periods.put(Period.本周, this.getLastPeriodStats(Period.本周));
        codingStats.periods.put(Period.本月, this.getLastPeriodStats(Period.本月));
        return codingStats;
    }

}






















