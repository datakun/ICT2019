package starbox;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class ShoppingForm extends JFrame implements ActionListener {
	UserInfo user;
	
	JTable tableShopping; // 장바구니 테이블
	DefaultTableModel modelShopping; // 테이블에서 사용하는 모델
	JButton btnBuy; // 구매
	JButton btnDelete; // 삭제
	JButton btnClose; // 닫기
	
	public ShoppingForm(UserInfo user) {
		setTitle("장바구니");
		
		this.user = user;

		setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		
		// 타이틀
		JLabel labelTitle = new JLabel(user.name + "회원님 장바구니");
		Font font = new Font("돋움체", Font.PLAIN, 20);
		labelTitle.setFont(font);
		labelTitle.setAlignmentY(Component.CENTER_ALIGNMENT);
		labelTitle.setBorder(new EmptyBorder(10, 10, 10, 10));
		
		JPanel panelTitle = new JPanel(new FlowLayout(FlowLayout.CENTER));
		panelTitle.add(labelTitle);

		// 테이블 생성
		JPanel panelTable = new JPanel();
		// 테이블 칼럼 이름
		String[] columns = { "메뉴명", "가격", "수량", "사이즈", "금액" };
		// 테이블에서 사용하는 모델 생성
		modelShopping = new DefaultTableModel(columns, 0);
		tableShopping = new JTable(modelShopping);
		JScrollPane scrollShopping = new JScrollPane(tableShopping);
		scrollShopping.setPreferredSize(new Dimension(700, 400));
		Border border = BorderFactory.createLineBorder(Color.BLACK);
		scrollShopping.setBorder(border);

		// 지문 4.1
		// 메뉴명 패널 생성
		JPanel panelButton = new JPanel(new FlowLayout(FlowLayout.CENTER));
		btnBuy = new JButton("구매");
		btnBuy.setPreferredSize(new Dimension(120, 30));
		btnDelete = new JButton("삭제");
		btnDelete.setPreferredSize(new Dimension(120, 30));
		btnClose = new JButton("닫기");
		btnClose.setPreferredSize(new Dimension(120, 30));
		panelButton.add(btnBuy);
		panelButton.add(btnDelete);
		panelButton.add(btnClose);
		
		add(panelTitle);
		add(scrollShopping);
		add(panelButton);
		
		// 리스너 연결
		btnBuy.addActionListener(this);
		btnDelete.addActionListener(this);
		btnClose.addActionListener(this);
		
		// 장바구니 조회
		updateShopping();
		
		// pack() 메소드는 현재 프레임의 크기를 컴포넌트에 맞게 알아서 조절 하는 역할 
		pack();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
		// 화면 가운데에 폼이 띄워지게 하는 메소드
		setLocationRelativeTo(null);
		// 창 크기 변경 못화게 하는 메소드
		setResizable(false);
	}
	
	private void searchShopping() {
		// 데이터 베이스 연결
		new DatabaseManager();

		// 메뉴 검색
		ResultSet rs = DatabaseManager.selectShopping(user.id);

		try {
			// 칼럼 정보 가져오기
			ResultSetMetaData rsMetaData = rs.getMetaData();

			// 데이터를 담을 오브젝트 생성
			Object [] tempObject = new Object[rsMetaData.getColumnCount()];

			// 모델의 초기화
			modelShopping.setRowCount(0);

			// 결과로 받은 메뉴 목록을 탐색
			while (rs.next()) {
				for (int i = 0; i < rsMetaData.getColumnCount(); i++) {
					tempObject[i] = rs.getString(i + 1);
				}

				// 모델에 메뉴 데이터 추가
				modelShopping.addRow(tempObject);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 데이터 베이스 연결 종료
		// NOTE:이번에는 ResultSet을 DatabaseManager 바깥에서 사용하기 때문에
		// 데이터 베이스 연결 종료를 맨 마지막에 함.
		// 만약 ResultSet을 사용하기 전에 데이터 베이스 연결을 끊으면 예외 발생.
		DatabaseManager.close();
	}
	
	private void updateShopping() {
		searchShopping();
		
		// 지문 5.1
		if (modelShopping.getRowCount() == 0) {
			btnBuy.setEnabled(false);
			btnDelete.setEnabled(false);
		} else {
			btnBuy.setEnabled(true);
			btnDelete.setEnabled(true);
		}
	}
	
	private void deleteCartItem() {
		int row = tableShopping.getSelectedRow();
		
		// 지문 5.4.1
		if (row < 0) {
			Util.openDialog("삭제할 메뉴를 선택해주세요.", "메시지", JOptionPane.ERROR_MESSAGE);
			
			return;
		}

		String menuName = modelShopping.getValueAt(row, 0).toString();
		
		boolean isValid = false;

		// 데이터 베이스 연결
		new DatabaseManager();

		// 메뉴명으로 장바구니 삭제
		isValid = DatabaseManager.deleteShoppingByMenu(menuName);

		// 데이터 베이스 연결 종료
		DatabaseManager.close();

		if (isValid == true) {
			// 지문 5.4.2
			// 삭제 후에 장바구니를 다시 검색
			updateShopping();
		}
	}

	public void buyItems(String totalAmount) {
		// 지문 3.15
		boolean isValid = false;
		
		int nTotalAmount = Integer.parseInt(totalAmount);
		if (nTotalAmount > user.point) {
			for (int i = 0; i < modelShopping.getRowCount(); i++) {
				String menuName = modelShopping.getValueAt(i, 0).toString();
				String price = modelShopping.getValueAt(i, 1).toString();
				String count = modelShopping.getValueAt(i, 2).toString();
				String size = modelShopping.getValueAt(i, 3).toString();
				String amount = modelShopping.getValueAt(i, 4).toString();
				
				isValid = payByCash(menuName, price, count, size, amount);
				
				if (isValid == false)
					break;
			}
			
			if (isValid == true) {
				Util.openDialog("구매되었습니다.", "메시지", JOptionPane.INFORMATION_MESSAGE);
			}
		} else {
			// 지문 3.15.2
			String message = "회원님의 총 포인트 : " + String.valueOf(user.point)
			+ "\n포인트로 결제하시겠습니까?"
			+ "\n(아니오를 클릭 시 현금결제가 됩니다)";
			int ret = Util.openConfirmDialog(message, "결제수단", JOptionPane.YES_NO_OPTION);

			if (ret == JOptionPane.YES_OPTION) {
				// 포인트로 결제
				isValid = payByPoint(totalAmount);
			} else {
				// 현금으로 결제
				for (int i = 0; i < modelShopping.getRowCount(); i++) {
					String menuName = modelShopping.getValueAt(i, 0).toString();
					String price = modelShopping.getValueAt(i, 1).toString();
					String count = modelShopping.getValueAt(i, 2).toString();
					String size = modelShopping.getValueAt(i, 3).toString();
					String amount = modelShopping.getValueAt(i, 4).toString();
					
					isValid = payByCash(menuName, price, count, size, amount);
					
					if (isValid == false)
						break;
				}
				
				if (isValid == true) {
					Util.openDialog("구매되었습니다.", "메시지", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		}
		
		if (isValid == false)
			return;
		
		// 데이터 베이스 연결
		new DatabaseManager();

		// 지문 5.3
		// shopping 테이블에 장바구니 제거
		isValid = DatabaseManager.deleteShoppingByUser(user.id);

		// 데이터 베이스 연결 종료
		DatabaseManager.close();
		
		// 지문 3.15.3
		updateGrade();
		
		setVisible(false);

		// Starbox 폼 시작
		new StarboxForm(user.id);
	}

	public boolean payByCash(String menuName, String price, String count, String size, String amount) {
		// 지문 3.15.2
		boolean isValid = false;

		// 데이터 베이스 연결
		new DatabaseManager();

		// orderlist 테이블에 구매내역 삽입
		isValid = DatabaseManager.insertOrderlist(user.id, menuName, size, price, count, amount);

		// 데이터 베이스 연결 종료
		DatabaseManager.close();

		if (isValid == true) {
			// 지문 3.15.1
			// 유저 포인트 누적
			int nPoint = user.point + (int) (Integer.parseInt(amount) * 0.05);

			// 데이터 베이스 연결
			new DatabaseManager();

			// 유저 포인트 업데이트
			isValid = DatabaseManager.updateUserPoint(user.id, String.valueOf(nPoint));

			// 데이터 베이스 연결 종료
			DatabaseManager.close();
		}
		
		return isValid;
	}

	public boolean payByPoint(String amount) {
		boolean isValid = false;

		user.point = user.point - Integer.parseInt(amount);

		// 데이터 베이스 연결
		new DatabaseManager();

		// 유저 포인트 업데이트
		isValid = DatabaseManager.updateUserPoint(user.id, String.valueOf(user.point));

		// 데이터 베이스 연결 종료
		DatabaseManager.close();

		if (isValid == true) {
			// 지문 3.15.2
			String message = "포인트로 결제 완료되었습니다."
					+ "\n남은 포인트 : " + String.valueOf(user.point);
			Util.openDialog(message, "메시지", JOptionPane.INFORMATION_MESSAGE);
		}
		
		return isValid;
	}

	public void updateGrade() {
		// 데이터 베이스 연결
		new DatabaseManager();

		// 유저 포인트 업데이트
		boolean isValid = DatabaseManager.updateUserGrade(user.id);

		// 데이터 베이스 연결 종료
		DatabaseManager.close();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// 버튼을 눌렀을 때 처리하는 리스너
		Object source = e.getSource();

		if (source.equals(btnBuy)) {
			// 지문 5.2
			int nTotalAmount = 0;
			for (int i = 0; i < modelShopping.getRowCount(); i++) {
				nTotalAmount += Integer.parseInt(modelShopping.getValueAt(i, 4).toString());
			}
			
			String totalAmount = String.valueOf(nTotalAmount);
			
			buyItems(totalAmount);
		} else if (source.equals(btnDelete)) {
			// 지문 5.4
			deleteCartItem();
		} else if (source.equals(btnClose)) {
			// 지문 5.5
			setVisible(false);

			new StarboxForm(user.id);
		}
	}

}
