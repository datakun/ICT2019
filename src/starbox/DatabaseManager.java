package starbox;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
	static Connection conn = null;
	static String sql = "";
	static String db = "jdbc:mysql://localhost/";
	static Statement stmt = null;
	// 이전 버전의 mysql은 아래 값으로 데이터 베이스 주소 옵션 설정
	// String option = "?useSSL=false&characterEncoding=utf8";
	// 서버 시간 설정, 문자 인코딩 설정, 데이터 삽입 옵션 설정
	String option = "?serverTimezone=Asia/Seoul&characterEncoding=utf8&jdbcCompliantTruncation=false";

	public DatabaseManager() {
		try{
			// 데이터 베이스 연결
			// 이전 버전의 mysql은 아래 값으로 라이브러리를 불러 옴
			// Class.forName("com.mysql.jdbc.Driver");
			Class.forName("com.mysql.cj.jdbc.Driver");
			// Projects에서 모든 작업은 user 계정으로 이루어 짐
			conn = DriverManager.getConnection(db + option, "user", "1234");
		}catch(ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void existsDB () {
		if (sql != "")
			sql = "";

		try {
			stmt = conn.createStatement();
			// 데이터 베이스 삭제
			sql = "DROP DATABASE IF EXISTS `coffee`";
			stmt.executeUpdate(sql);

			// 데이터 베이스 생성
			sql = "CREATE DATABASE `coffee`";
			stmt.executeUpdate(sql);

			// 데이터 베이스 주소 설정
			db = "jdbc:mysql://localhost/coffee";
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}

	public static void createTable() {
		if (sql != "")
			sql = "";

		try {
			stmt = conn.createStatement();

			// menu 테이블
			sql = "CREATE TABLE IF NOT EXISTS `coffee`.`menu` (\r\n" + 
					"  `m_no` INT NOT NULL AUTO_INCREMENT,\r\n" + 
					"  `m_group` VARCHAR(10) NULL,\r\n" + 
					"  `m_name` VARCHAR(30) NULL,\r\n" + 
					"  `m_price` INT NULL,\r\n" + 
					"  PRIMARY KEY (`m_no`))";
			stmt.executeUpdate(sql);

			// user 테이블
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

			// orderlist 테이블
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

			// shopping 테이블
			sql = "CREATE TABLE IF NOT EXISTS `coffee`.`shopping` (\r\n" + 
					"  `s_no` INT NOT NULL AUTO_INCREMENT,\r\n" + 
					"  `m_no` INT NOT NULL,\r\n" + 
					"  `s_price` INT NULL,\r\n" + 
					"  `s_count` INT NULL,\r\n" + 
					"  `s_size` VARCHAR(1) NULL,\r\n" + 
					"  `s_amount` INT NULL,\r\n" + 
					"  PRIMARY KEY (`s_no`),\r\n" + 
					"  INDEX `fk_shopping_menu1_idx` (`m_no` ASC) VISIBLE,\r\n" + 
					"  CONSTRAINT `fk_shopping_menu1`\r\n" + 
					"    FOREIGN KEY (`m_no`)\r\n" + 
					"    REFERENCES `coffee`.`menu` (`m_no`)\r\n" + 
					"    ON DELETE NO ACTION\r\n" + 
					"    ON UPDATE NO ACTION)";
			stmt.executeUpdate(sql);

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void insertDate (String table) {
		if(sql != "")
			sql = "";

		try {
			// 데이터 파일 읽기
			FileInputStream fi = new FileInputStream("C:\\WORK\\2과제\\DataFiles\\" + table + ".txt");
			InputStreamReader is = new InputStreamReader(fi, "UTF-8");
			BufferedReader br = new BufferedReader(is);

			String readRow = "";
			int rowCount = 1; // 현재 로우 번호
			stmt = conn.createStatement();

			// 데이터 파일을 한 줄씩 읽어서 데이터 베이스 테이블에 데이터 삽입
			while ((readRow = br.readLine())!= null) {
				// 단어들이 탭으로 구분되어 있어서 문자열을 탭을 기준으로 단어를 분리시킴
				String[] arr = readRow.split("\\t");

				// 데이터 파일의 맨 첫 줄은 칼럼 이름이라 데이터 삽입 건너 뜀
				if (rowCount != 1) {
					if (table.equals("menu")) {
						// menu 테이블 데이터 입력
						sql = "INSERT INTO coffee.menu VALUES("
								+ "'" + arr[0] + "','" + arr[1] + "','" + arr[2] + "','" + arr[3] + "');";
					} else if (table.equals("orderlist")) {
						// orderlist 테이블 데이터 입력
						sql = "INSERT INTO coffee.orderlist VALUES("
								+ "'" + arr[0] + "',DATE '" + arr[1] + "','" + arr[2] + "','" + arr[3] + "','" + arr[4]
										+ "','" + arr[5] + "','" + arr[6] + "','" + arr[7] + "','" + arr[8] + "');";
					} else if (table.equals("user")) {
						// user 테이블 데이터 입력
						sql = "INSERT INTO coffee.user VALUES("
								+ "'" + arr[0] + "','" + arr[1] + "','" + arr[2] + "','" + arr[3] + "','" + arr[4]
										+ "','" + arr[5] + "','" + arr[6] + "');";
					}

					stmt.executeUpdate(sql);
				}

				rowCount++;
			}

			if(br != null)
				br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void createUser() {
		if(sql != "")
			sql = "";

		try {
			stmt = conn.createStatement();
			// user 계정 삭제 
			sql = "DROP USER IF EXISTS user@localhost";
			stmt.executeUpdate(sql);

			// user 계정 생성
			sql = "CREATE USER user@localhost IDENTIFIED BY '1234'";
			stmt.executeUpdate(sql);

			// user 계정 권한 설정
			sql = "GRANT SELECT, INSERT, DELETE, UPDATE ON coffee.* TO user@localhost";
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void close() {
		try {
			if (conn != null)
				conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		try {
			if (stmt != null)
				stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static boolean isValidUser(String id, String pw) {
		boolean isValid = false;

		sql = "";

		try {
			ResultSet srs;
			stmt = conn.createStatement();

			sql = "SELECT COUNT(*) FROM coffee.user WHERE u_id='" + id + "' AND u_pw='" + pw + "';";
			srs = stmt.executeQuery(sql);
			srs.next();
			int recordCount = srs.getInt(1);

			if (recordCount > 0)
				isValid = true;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return isValid;
	}

	public static boolean insertUser(String name, String id, String pw, String bd) {
		boolean isValid = false;

		sql = "";

		try {
			ResultSet srs;
			stmt = conn.createStatement();
			
			// 유저가 존재하는지 확인
			sql = "SELECT COUNT(*) FROM coffee.user WHERE u_id='" + id + "';";
			srs = stmt.executeQuery(sql);
			srs.next();
			int recordCount = srs.getInt(1);
			
			if (recordCount == 0) {
				// 유저가 없으면 유저 삽입
				sql = "INSERT INTO coffee.user (u_id, u_pw, u_name, u_bd, u_point, u_grade) "
						+ "VALUES('" + id + "','" + pw + "','" + name + "','" + bd + "', 0, '일반');";
				stmt.executeUpdate(sql);

				isValid = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return isValid;
	}

	public static ResultSet selectUser(String id) {
		ResultSet rs = null;
		sql = "";

		try {
			stmt = conn.createStatement();

			// 유저 정보 가져오기
			sql = "SELECT * FROM coffee.user WHERE u_id='" + id + "';";
			rs = stmt.executeQuery(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return rs;
	}

	public static boolean insertMenu(String group, String name, String price) {
		boolean isValid = false;

		sql = "";

		try {
			ResultSet srs;
			stmt = conn.createStatement();
			
			// 메뉴가 존재하는지 확인
			sql = "SELECT COUNT(*) FROM coffee.menu WHERE m_name='" + name + "';";
			srs = stmt.executeQuery(sql);
			srs.next();
			int recordCount = srs.getInt(1);
			
			if (recordCount == 0) {
				// 메뉴가 없으면 메뉴 삽입
				sql = "INSERT INTO coffee.menu (m_group, m_name, m_price) "
						+ "VALUES('" + group + "','" + name + "','" + price + "');";
				stmt.executeUpdate(sql);

				isValid = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return isValid;
	}

	public static ResultSet selectMenu(String group, String name) {
		ResultSet rs = null;
		sql = "";

		try {
			stmt = conn.createStatement();

			// SELECT 문 사용 시, WHERE 절에 LIKE를 사용하면 해당 단어를 포함한 모든 문자열 검색 가능
			sql = "SELECT m_group, m_name, m_price FROM coffee.menu WHERE m_name LIKE '%" + name + "%'";
			
			if (group.equals("전체") == false) {
				// 만약 분류가 '전체'가 아니라면 특정 분류만 검색
				sql = sql + " AND m_group='" + group + "';";
			} else {
				// 분류가 '전체'라면 모든 분류에서 검색
				sql = sql + ";";
			}
			
			rs = stmt.executeQuery(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return rs;
	}

	public static boolean deleteMenu(String name) {
		boolean isValid = false;

		sql = "";

		try {
			ResultSet srs;
			stmt = conn.createStatement();
			
			// 메뉴가 존재하는지 확인
			sql = "SELECT COUNT(*) FROM coffee.menu WHERE m_name='" + name + "';";
			srs = stmt.executeQuery(sql);
			srs.next();
			int recordCount = srs.getInt(1);
			
			if (recordCount > 0) {
				// 메뉴가 있으면 메뉴 삭제
				sql = "DELETE FROM coffee.menu WHERE m_name='" + name + "';";
				stmt.executeUpdate(sql);

				isValid = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return isValid;
	}

	public static boolean updateMenu(String oldName, String group, String name, String price) {
		boolean isValid = false;

		sql = "";

		try {
			stmt = conn.createStatement();
			
			// 지문 9.6.4
			if (oldName.equals(name)) {
				// 이전 이름과 현재 이름이 같다면 메뉴가 존재하는지 확인 안함
				sql = "UPDATE coffee.menu SET m_group='" + group + "', m_price='" + price + "' WHERE m_name='" + name + "';";
				stmt.executeUpdate(sql);

				isValid = true;
			} else {
				// 이전 이름과 현재 이름이 다르면 메뉴가 존재하는지 확인
				ResultSet srs;
				
				sql = "SELECT COUNT(*) FROM coffee.menu WHERE m_name='" + name + "';";
				srs = stmt.executeQuery(sql);
				srs.next();
				int recordCount = srs.getInt(1);
				
				if (recordCount != 0) {
					// 같은 이름의 메뉴가 있으면 수정 안 함
					isValid = false;
				} else {
					// 같은 이름의 메뉴가 없으면 수정
					// 기존 메뉴의 레코드 인덱스를 가져와서
					sql = "SELECT m_no FROM coffee.menu WHERE m_name='" + oldName + "';";
					srs = stmt.executeQuery(sql);
					srs.next();
					String m_no = String.valueOf(srs.getInt(1));

					// 해당 레코드의 데이터를 수정
					sql = "UPDATE coffee.menu SET m_group='" + group + "', m_name='" + name + "', m_price='" + price + "' WHERE m_no=" + m_no + ";";
					stmt.executeUpdate(sql);

					isValid = true;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return isValid;
	}

	public static boolean insertShopping(String userID, String menuName, String price, String count, String size, String amount) {
		boolean isValid = false;

		sql = "";

		try {
			ResultSet srs;
			stmt = conn.createStatement();
			
			// user 테이블에서 u_no 가져옴
			sql = "SELECT u_no FROM coffee.user WHERE u_id='" + userID + "';";
			srs = stmt.executeQuery(sql);
			srs.next();
			String u_no = String.valueOf(srs.getInt(1));
			
			// menu 테이블에서 m_no 가져옴
			sql = "SELECT m_no FROM coffee.menu WHERE m_name='" + menuName + "';";
			srs = stmt.executeQuery(sql);
			srs.next();
			String m_no = String.valueOf(srs.getInt(1));

			// 장바구니에 삽입
			sql = "INSERT INTO coffee.shopping (u_no, m_no, s_price, s_count, s_size, s_amount) "
					+ "VALUES(" + u_no + "," + m_no + "," + price + "," + count + ",'" + size + "'," + amount + ");";
			stmt.executeUpdate(sql);

			isValid = true;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return isValid;
	}

	public static boolean insertOrderlist(String userID, String menuName, String group, String size, String price, String count, String amount) {
		boolean isValid = false;

		sql = "";

		try {
			ResultSet srs;
			stmt = conn.createStatement();
			
			// user 테이블에서 u_no 가져옴
			sql = "SELECT u_no FROM coffee.user WHERE u_id='" + userID + "';";
			srs = stmt.executeQuery(sql);
			srs.next();
			String u_no = String.valueOf(srs.getInt(1));
			
			// menu 테이블에서 m_no 가져옴
			sql = "SELECT m_no FROM coffee.menu WHERE m_name='" + menuName + "';";
			srs = stmt.executeQuery(sql);
			srs.next();
			String m_no = String.valueOf(srs.getInt(1));

			// orderlist 테이블에 삽입
			sql = "INSERT INTO coffee.orderlist (o_date, u_no, m_no, o_group, o_size, o_price, o_count, o_amount) "
					+ "VALUES(NOW()," + u_no + "," + m_no + ",'" + group + "','" + size + "'," + price + "," + count + "," + amount + ");";
			stmt.executeUpdate(sql);

			isValid = true;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return isValid;
	}

	public static boolean insertOrderlist(String userID, String menuName, String size, String price, String count, String amount) {
		boolean isValid = false;

		sql = "";

		try {
			ResultSet srs;
			stmt = conn.createStatement();
			
			// user 테이블에서 u_no 가져옴
			sql = "SELECT u_no FROM coffee.user WHERE u_id='" + userID + "';";
			srs = stmt.executeQuery(sql);
			srs.next();
			String u_no = String.valueOf(srs.getInt(1));
			
			// menu 테이블에서 m_no 가져옴
			sql = "SELECT m_no, m_group FROM coffee.menu WHERE m_name='" + menuName + "';";
			srs = stmt.executeQuery(sql);
			srs.next();
			String m_no = String.valueOf(srs.getInt(1));
			String m_group = srs.getString(2);

			// orderlist 테이블에 삽입
			sql = "INSERT INTO coffee.orderlist (o_date, u_no, m_no, o_group, o_size, o_price, o_count, o_amount) "
					+ "VALUES(NOW()," + u_no + "," + m_no + ",'" + m_group + "','" + size + "'," + price + "," + count + "," + amount + ");";
			stmt.executeUpdate(sql);

			isValid = true;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return isValid;
	}

	public static boolean updateUserPoint(String userID, String point) {
		boolean isValid = false;

		sql = "";

		try {
			stmt = conn.createStatement();

			// 유저의 포인트 업데이트
			sql = "UPDATE coffee.user SET u_point=" + point + " WHERE u_id='" + userID + "';";
			stmt.executeUpdate(sql);

			isValid = true;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return isValid;
	}

	public static boolean updateUserGrade(String userID) {
		boolean isValid = false;

		sql = "";

		try {
			ResultSet srs;
			stmt = conn.createStatement();
			
			// user 테이블에서 u_no 가져옴
			sql = "SELECT u_no FROM coffee.user WHERE u_id='" + userID + "';";
			srs = stmt.executeQuery(sql);
			srs.next();
			String u_no = String.valueOf(srs.getInt(1));
			
			// orderlist 테이블에서 구매내역의 총금액 목록 가져옴
			sql = "SELECT o_amount FROM coffee.orderlist WHERE u_no=" + u_no + ";";
			srs = stmt.executeQuery(sql);
			
			int totalAmount = 0;
			while (srs.next()) {
				totalAmount += srs.getInt(1);
			}
			
			// 지문 3.15.3
			String grade = "";
			if (totalAmount > 300000) {
				grade = "Bronze";
			} else if (totalAmount > 500000) {
				grade = "Silver";
			} else if (totalAmount > 800000) {
				grade = "Gold";
			} else {
				grade = "일반";
			}

			// 유저의 등급 업데이트
			sql = "UPDATE coffee.user SET u_grade='" + grade + "' WHERE u_id='" + userID + "';";
			stmt.executeUpdate(sql);
			
			isValid = true;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return isValid;
	}

	public static ResultSet selectOrderlist(String userID) {
		ResultSet rs = null;
		sql = "";

		try {
			ResultSet srs = null;
			stmt = conn.createStatement();

			// user 테이블에서 u_no 가져옴
			sql = "SELECT u_no FROM coffee.user WHERE u_id='" + userID + "';";
			srs = stmt.executeQuery(sql);
			srs.next();
			String u_no = String.valueOf(srs.getInt(1));
			
			// orderlist와 menu 테이블을 내부 조인해서 필요한 구매내역 가져옴
			// JOIN 이론으로 필요합니다.
			sql = "SELECT coffee.orderlist.o_date, coffee.menu.m_name, coffee.menu.m_price, "
					+ "coffee.orderlist.o_size, coffee.orderlist.o_count, coffee.orderlist.o_amount "
					+ "FROM coffee.menu JOIN coffee.orderlist "
					+ "ON coffee.menu.m_no=coffee.orderlist.m_no "
					+ "WHERE coffee.orderlist.u_no=" + u_no + ";";
			rs = stmt.executeQuery(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return rs;
	}

	public static ResultSet selectOrderlistByTop5(String group) {
		ResultSet rs = null;
		sql = "";

		try {
			stmt = conn.createStatement();
			
			// orderlist와 menu 테이블을 내부 조인해서 필요한 구매내역 가져옴
			// JOIN 이론으로 필요합니다.
			sql = "SELECT coffee.menu.m_name, coffee.orderlist.o_count "
					+ "FROM coffee.menu JOIN coffee.orderlist "
					+ "ON coffee.menu.m_no=coffee.orderlist.m_no "
					+ "WHERE coffee.menu.m_group='" + group + "' "
					+ "ORDER BY coffee.menu.m_name DESC;";
			rs = stmt.executeQuery(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return rs;
	}

	public static ResultSet selectShopping(String userID) {
		ResultSet rs = null;
		sql = "";

		try {
			ResultSet srs = null;
			stmt = conn.createStatement();

			// user 테이블에서 u_no 가져옴
			sql = "SELECT u_no FROM coffee.user WHERE u_id='" + userID + "';";
			srs = stmt.executeQuery(sql);
			srs.next();
			String u_no = String.valueOf(srs.getInt(1));
			
			// shopping와 menu 테이블을 내부 조인해서 필요한 구매내역 가져옴
			// JOIN 이론으로 필요합니다.
			sql = "SELECT coffee.menu.m_name, coffee.menu.m_price, "
					+ "coffee.shopping.s_count, coffee.shopping.s_size, coffee.shopping.s_amount "
					+ "FROM coffee.menu JOIN coffee.shopping "
					+ "ON coffee.menu.m_no=coffee.shopping.m_no "
					+ "WHERE coffee.shopping.u_no=" + u_no + ";";
			rs = stmt.executeQuery(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return rs;
	}

	public static boolean deleteShoppingByMenu(String menuName) {
		boolean isValid = false;

		sql = "";

		try {
			ResultSet srs;
			stmt = conn.createStatement();

			// menu 테이블에서 m_no 가져옴
			sql = "SELECT m_no FROM coffee.menu WHERE m_name='" + menuName + "';";
			srs = stmt.executeQuery(sql);
			srs.next();
			String m_no = String.valueOf(srs.getInt(1));
			
			// 장바구니에서 메뉴 삭제
			sql = "DELETE FROM coffee.shopping WHERE m_no=" + m_no + ";";
			stmt.executeUpdate(sql);

			isValid = true;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return isValid;
	}

	public static boolean deleteShoppingByUser(String userID) {
		boolean isValid = false;

		sql = "";

		try {
			ResultSet srs;
			stmt = conn.createStatement();

			// user 테이블에서 u_no 가져옴
			sql = "SELECT u_no FROM coffee.user WHERE u_id='" + userID + "';";
			srs = stmt.executeQuery(sql);
			srs.next();
			String u_no = String.valueOf(srs.getInt(1));
			
			// 장바구니에서 메뉴 삭제
			sql = "DELETE FROM coffee.shopping WHERE u_no=" + u_no + ";";
			stmt.executeUpdate(sql);

			isValid = true;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return isValid;
	}

	/*
	public static void main(String[] args) {
		// 데이터 베이스 삭제 및 생성용 연결
		new DatabaseManager();
		existsDB();

		// 데이터 베이스 테이블 생성용 연결
		new DatabaseManager();
		createTable();
		insertDate("menu");
		insertDate("user");
		insertDate("orderlist");
		close();
	}
	*/
}