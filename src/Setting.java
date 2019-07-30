import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Setting {
	static Connection conn = null;
	static String sql = "";
	static String db = "jdbc:mysql://localhost/";
	static Statement stmt = null;
	static String option = "?serverTimezone=Asia/Seoul&"
			+ "characterEncoding=utf8&"
			+ "jdbcCompliantTruncation=false";
	
	public Setting() {
		// JDBC 로드 및 데이터 베이스 연결
		System.out.println("Connecting...");
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection(db + option, "root", "rlawnsdn1");
			
			System.out.println("Connection completed.");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public static void existsDB() {
		if (sql.equals("") == false) 
			sql = "";
		
		try {
			// 쿼리문 수행하기 위한 Statement 객체 생성
			stmt = conn.createStatement();
			
			// 데이터 베이스 삭제
			sql = "DROP DATABASE IF EXISTS `coffee`";
			stmt.executeUpdate(sql);

			// 데이터 베이스 생성
			sql = "CREATE DATABASE `coffee`";
			stmt.executeUpdate(sql);

			// 데이터 베이스 주소 설정
			db = "jdbc:mysql://localhost/coffee";
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public static void createTable() {
		sql = "";
		
		try {
			stmt = conn.createStatement();

			sql = "CREATE TABLE IF NOT EXISTS `coffee`.`user` (\r\n" + 
					"  `u_no` INT NOT NULL AUTO_INCREMENT,\r\n" + 
					"  `u_id` VARCHAR(20) NULL,\r\n" + 
					"  `u_pw` VARCHAR(4) NULL,\r\n" + 
					"  `u_name` VARCHAR(5) NULL,\r\n" + 
					"  `u_bd` VARCHAR(14) NULL,\r\n" + 
					"  `u_point` INT NULL,\r\n" + 
					"  `u_grade` VARCHAR(10) NULL,\r\n" + 
					"  PRIMARY KEY (`u_no`))";
			stmt.executeUpdate(sql);

			sql = "CREATE TABLE IF NOT EXISTS `coffee`.`menu` (\r\n" + 
					"  `m_no` INT NOT NULL AUTO_INCREMENT,\r\n" + 
					"  `m_group` VARCHAR(10) NULL,\r\n" + 
					"  `m_name` VARCHAR(30) NULL,\r\n" + 
					"  `m_price` INT NULL,\r\n" + 
					"  PRIMARY KEY (`m_no`))";
			stmt.executeUpdate(sql);

			sql = "CREATE TABLE IF NOT EXISTS `coffee`.`orderlist` (\r\n" + 
					"  `o_no` INT NOT NULL AUTO_INCREMENT,\r\n" + 
					"  `o_date` DATE NULL,\r\n" + 
					"  `u_no` INT NOT NULL,\r\n" + 
					"  `m_no` INT NOT NULL,\r\n" + 
					"  `o_group` VARCHAR(10) NULL,\r\n" + 
					"  `o_size` VARCHAR(1) NULL,\r\n" + 
					"  `o_price` INT NULL,\r\n" + 
					"  `o_count` INT NULL,\r\n" + 
					"  `o_amount` INT NULL,\r\n" + 
					"  PRIMARY KEY (`o_no`),\r\n" + 
					"  INDEX `fk_orderlist_user_idx` (`u_no` ASC) VISIBLE,\r\n" + 
					"  INDEX `fk_orderlist_menu1_idx` (`m_no` ASC) VISIBLE,\r\n" + 
					"  CONSTRAINT `fk_orderlist_user`\r\n" + 
					"    FOREIGN KEY (`u_no`)\r\n" + 
					"    REFERENCES `coffee`.`user` (`u_no`)\r\n" + 
					"    ON DELETE NO ACTION\r\n" + 
					"    ON UPDATE NO ACTION,\r\n" + 
					"  CONSTRAINT `fk_orderlist_menu1`\r\n" + 
					"    FOREIGN KEY (`m_no`)\r\n" + 
					"    REFERENCES `coffee`.`menu` (`m_no`)\r\n" + 
					"    ON DELETE NO ACTION\r\n" + 
					"    ON UPDATE NO ACTION)";
			stmt.executeUpdate(sql);

			sql = "CREATE TABLE IF NOT EXISTS `coffee`.`shopping` (\r\n" + 
					"  `s_no` INT NOT NULL AUTO_INCREMENT,\r\n" + 
					"  `u_no` INT NOT NULL,\r\n" + 
					"  `m_no` INT NOT NULL,\r\n" + 
					"  `s_price` INT NULL,\r\n" + 
					"  `s_count` INT NULL,\r\n" + 
					"  `s_size` VARCHAR(1) NULL,\r\n" + 
					"  `s_amount` INT NULL,\r\n" + 
					"  PRIMARY KEY (`s_no`),\r\n" + 
					"  INDEX `fk_shopping_user_idx` (`u_no` ASC) VISIBLE,\r\n" +
					"  INDEX `fk_shopping_menu1_idx` (`m_no` ASC) VISIBLE,\r\n" +  
					"  CONSTRAINT `fk_shopping_user`\r\n" + 
					"    FOREIGN KEY (`u_no`)\r\n" + 
					"    REFERENCES `coffee`.`user` (`u_no`)\r\n" + 
					"    ON DELETE NO ACTION\r\n" + 
					"    ON UPDATE NO ACTION,\r\n" + 
					"  CONSTRAINT `fk_shopping_menu1`\r\n" + 
					"    FOREIGN KEY (`m_no`)\r\n" + 
					"    REFERENCES `coffee`.`menu` (`m_no`)\r\n" + 
					"    ON DELETE NO ACTION\r\n" + 
					"    ON UPDATE NO ACTION)";
			stmt.executeUpdate(sql);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public static void insertData(String tableName) {
		sql = "";
		
		try {	
			// 파일을 읽을거에요
			FileInputStream fi = new FileInputStream("C:\\WORK\\2과제\\DataFiles\\" + tableName + ".txt");
			InputStreamReader is = new InputStreamReader(fi, "UTF-8");
			BufferedReader br = new BufferedReader(is);
			
			String readRow = "";
			int rowCount = 1;
			stmt = conn.createStatement();

			while ((readRow = br.readLine()) != null) {
				String[] arr = readRow.split("\\t");
				
				if (rowCount != 1) {
					if (tableName.equals("menu")) {
						sql = "INSERT INTO coffee.menu VALUES("
								+ "'" + arr[0] + "','" + arr[1]
								+ "','" + arr[2] + "','" + arr[3] + "');";
					} else if (tableName.equals("orderlist")) {
						sql = "INSERT INTO coffee.orderlist VALUES("
								+ "'" + arr[0] + "',DATE '" + arr[1]
								+ "','" + arr[2] + "','" + arr[3]
								+ "','" + arr[4] + "','" + arr[5]
								+ "','" + arr[6] + "','" + arr[7]
								+ "','" + arr[8] + "');";
					} else if (tableName.equals("user")) {
						sql = "INSERT INTO coffee.user VALUES("
								+ "'" + arr[0] + "','" + arr[1]
								+ "','" + arr[2] + "','" + arr[3]
								+ "','" + arr[4] + "','" + arr[5]
								+ "','" + arr[6] + "');";
					}

					stmt.executeUpdate(sql);
				}

				rowCount++;
			}
			
			if (br != null)
				br.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public static void createUser() {
		sql = "";

		try {
			stmt = conn.createStatement();
			// user 계정 삭제 
			sql = "DROP USER IF EXISTS user@localhost";
			stmt.executeUpdate(sql);

			// user 계정 생성
			sql = "CREATE USER 'user'@localhost IDENTIFIED BY '1234'";
			stmt.executeUpdate(sql);

			// user 계정 권한 설정
			sql = "GRANT SELECT, INSERT, DELETE, UPDATE ON coffee.* TO user@localhost";
			stmt.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void close() {
		try {
			if (conn != null)
				conn.close();
			
			if (stmt != null)
				stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		// 데이터 베이스 삭제 생성용 연결
		new Setting();
		existsDB();
		
		// 테이블 생성용 연결
		new Setting();
		createTable();
		insertData("menu");
		insertData("user");
		insertData("orderlist");
		createUser();
		close();
	}

}
