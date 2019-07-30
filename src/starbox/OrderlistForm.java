package starbox;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class OrderlistForm extends JFrame implements ActionListener {
	UserInfo user;
	
	int totalAmount;

	JTable tableOrderlist; // 구매내역 테이블
	DefaultTableModel modelOrderlist; // 테이블에서 사용하는 모델
	JButton btnClose; // 닫기

	public OrderlistForm(UserInfo user) {
		setTitle("구매내역");
		
		this.user = user;

		totalAmount = 0;
		
		setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		
		// 타이틀
		JLabel labelTitle = new JLabel(user.name + "회원님 구매내역");
		Font font = new Font("돋움체", Font.PLAIN, 20);
		labelTitle.setFont(font);
		labelTitle.setAlignmentY(Component.CENTER_ALIGNMENT);
		labelTitle.setBorder(new EmptyBorder(10, 10, 10, 10));
		
		JPanel panelTitle = new JPanel(new FlowLayout(FlowLayout.CENTER));
		panelTitle.add(labelTitle);

		// 테이블 생성
		JPanel panelTable = new JPanel();
		// 테이블 칼럼 이름
		String[] columns = { "구매일자", "메뉴명", "가격", "사이즈", "수량", "총금액" };
		// 테이블에서 사용하는 모델 생성
		modelOrderlist = new DefaultTableModel(columns, 0);
		tableOrderlist = new JTable(modelOrderlist);
		JScrollPane scrollOrderlist = new JScrollPane(tableOrderlist);
		scrollOrderlist.setPreferredSize(new Dimension(800, 400));
		Border border = BorderFactory.createLineBorder(Color.BLACK);
		scrollOrderlist.setBorder(border);

		// 지문 4.1
		// 메뉴명 패널 생성
		JPanel panelTotalAmount = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JLabel labelName = new JLabel("총 결제 금액");
		JTextField tfTotalAmount = new JTextField(20);
		tfTotalAmount.setEnabled(false);
		tfTotalAmount.setHorizontalAlignment(JTextField.RIGHT);
		btnClose = new JButton("닫기");
		panelTotalAmount.add(labelName);
		panelTotalAmount.add(tfTotalAmount);
		panelTotalAmount.add(btnClose);
		
		add(panelTitle);
		add(scrollOrderlist);
		add(panelTotalAmount);
		
		// 리스너 연결
		btnClose.addActionListener(this);
		
		// 구매내역 조회
		searchOrderlist();
		
		// 총 결제 금액 표시
		// 3 자리씩 콤마 넣기
		String textAmount = NumberFormat.getInstance().format(totalAmount);
		tfTotalAmount.setText(textAmount);
		
		// pack() 메소드는 현재 프레임의 크기를 컴포넌트에 맞게 알아서 조절 하는 역할 
		pack();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
		// 화면 가운데에 폼이 띄워지게 하는 메소드
		setLocationRelativeTo(null);
		// 창 크기 변경 못화게 하는 메소드
		setResizable(false);
	}

	private void searchOrderlist() {
		// 데이터 베이스 연결
		new DatabaseManager();

		// 메뉴 검색
		ResultSet rs = DatabaseManager.selectOrderlist(user.id);

		try {
			// 칼럼 정보 가져오기
			ResultSetMetaData rsMetaData = rs.getMetaData();

			// 데이터를 담을 오브젝트 생성
			Object [] tempObject = new Object[rsMetaData.getColumnCount()];

			// 모델의 초기화
			modelOrderlist.setRowCount(0);
			
			// 총 결제 금액 초기화
			totalAmount = 0;

			// 결과로 받은 메뉴 목록을 탐색
			while (rs.next()) {
				for (int i = 0; i < rsMetaData.getColumnCount(); i++) {
					tempObject[i] = rs.getString(i + 1);
					
					if (i == 5) {
						// 총 결제 금액 계산
						totalAmount += rs.getInt(i + 1);
					}
				}

				// 모델에 메뉴 데이터 추가
				modelOrderlist.addRow(tempObject);
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

	@Override
	public void actionPerformed(ActionEvent e) {
		// 버튼을 눌렀을 때 처리하는 리스너
		Object source = e.getSource();

		if (source.equals(btnClose)) {
			// 지문 4.2
			setVisible(false);

			new StarboxForm(user.id);
		}
	}
}
