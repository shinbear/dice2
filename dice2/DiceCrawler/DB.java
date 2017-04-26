/*    */ package DiceCrawler;
/*    */ 
/*    */ import java.io.PrintStream;
/*    */ import java.sql.Connection;
/*    */ import java.sql.DriverManager;
/*    */ import java.sql.ResultSet;
/*    */ import java.sql.SQLException;
/*    */ import java.sql.Statement;
/*    */ 
/*    */ public class DB
/*    */ {
/* 10 */   public Connection conn = null;
/*    */ 
/*    */   public DB() {
/*    */     try {
/* 14 */       Class.forName("com.mysql.jdbc.Driver");
/* 15 */       String url = "jdbc:mysql://localhost:3306/waverly";
/*    */ 
/* 17 */       this.conn = DriverManager.getConnection(url, "root", "197544");
/* 18 */       System.out.println("connection built");
/*    */     } catch (SQLException e) {
/* 20 */       e.printStackTrace();
/*    */     } catch (ClassNotFoundException e) {
/* 22 */       e.printStackTrace();
/*    */     }
/*    */   }
/*    */ 
/*    */   public ResultSet runSql(String sql) throws SQLException {
/* 27 */     Statement sta = this.conn.createStatement();
/* 28 */     return sta.executeQuery(sql);
/*    */   }
/*    */ 
/*    */   public boolean runSql2(String sql) throws SQLException {
/* 32 */     Statement sta = this.conn.createStatement();
/* 33 */     return sta.execute(sql);
/*    */   }
/*    */ 
/*    */   protected void finalize() throws Throwable
/*    */   {
/* 38 */     if ((this.conn != null) || (!this.conn.isClosed()))
/* 39 */       this.conn.close();
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\Desktop\Dice-Crawler_V5\Dice-Crawler_V5\Dice-Crawler_V5.jar
 * Qualified Name:     DiceCrawler.DB
 * JD-Core Version:    0.6.2
 */