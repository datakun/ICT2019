package starbox;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class JoinForm extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	JButton btnOK; // 가입 완료
	JButton btnCancel; // 취소
	JTextField tfName; // 이름 입력 필드
	JTextField tfID; // 아이디 입력 필드
	JPasswordField pfPW; // 비밀번호 입력 필드
	JComboBox<String> comboYear;
	JComboBox<String> comboMonth;
	JComboBox<String> comboDay;
	
	public JoinForm() {
		setTitle("회원가입");

		setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

		// 이름 패널 생성
		JPanel panelName = new JPanel();
		panelName.setBorder(new EmptyBorder(10, 0, 0, 0));
		JLabel labelName = new JLabel("이름", JLabel.RIGHT);
		labelName.setPreferredSize(new Dimension(70, 20));
		tfName = new JTextField(20);
		tfName.setPreferredSize(new Dimension(160, 20));
		panelName.add(labelName);
		panelName.add(tfName);
		
		// 아이디 패널 생성
		JPanel panelID = new JPanel();
		panelID.setBorder(new EmptyBorder(10, 0, 0, 0));
		JLabel labelID = new JLabel("아이디", JLabel.RIGHT);
		labelID.setPreferredSize(new Dimension(70, 20));
		tfID = new JTextField(20);
		tfID.setPreferredSize(new Dimension(160, 20));
		panelID.add(labelID);
		panelID.add(tfID);
		
		// 비밀번호 패널 생성
		JPanel panelPW = new JPanel();
		panelPW.setBorder(new EmptyBorder(10, 0, 0, 0));
		JLabel labelPW = new JLabel("비밀번호", JLabel.RIGHT);
		labelPW.setPreferredSize(new Dimension(70, 20));
		pfPW = new JPasswordField(20);
		tfID.setPreferredSize(new Dimension(160, 20));
		panelPW.add(labelPW);
		panelPW.add(pfPW);

		// 생년월일 패널 생성
		JPanel panelBirthday = new JPanel();
		panelBirthday.setLayout(new BoxLayout(panelBirthday, BoxLayout.X_AXIS));
		panelBirthday.setBorder(new EmptyBorder(10, 10, 0, 10));

		JLabel labelBirthday = new JLabel("생년월일", JLabel.RIGHT);
		comboYear = new JComboBox<>();
		// 지문 2.1
		for (int i = 1900; i <= 2019; i++) {
			comboYear.addItem(String.format("%04d", i));
		}
		JLabel labelYear = new JLabel("년");
		comboMonth = new JComboBox<>();
		// 지문 2.2
		for (int i = 1; i <= 12; i++) {
			comboMonth.addItem(String.format("%02d", i));
		}
		JLabel labelMonth = new JLabel("월");
		comboDay = new JComboBox<>();
		// 1900년 1월의 일 만큼을 콤보박스에 넣기위함
		getDays(1900, 1);
		JLabel labelDay = new JLabel("일", JLabel.LEFT);
		
		panelBirthday.add(labelBirthday);
		panelBirthday.add(comboYear);
		panelBirthday.add(labelYear);
		panelBirthday.add(comboMonth);
		panelBirthday.add(labelMonth);
		panelBirthday.add(comboDay);
		panelBirthday.add(labelDay);

		// 하단 버튼 패널 생성
		JPanel panelButton = new JPanel();
		btnOK = new JButton("가입 완료");
		btnCancel = new JButton("취소");
		panelButton.add(btnOK);
		panelButton.add(btnCancel);

		add(panelName);
		add(panelID);
		add(panelPW);
		add(panelBirthday);
		add(panelButton);
		
		// 리스너 연결
		btnOK.addActionListener(this);
		btnCancel.addActionListener(this);
		comboYear.addActionListener(this);
		comboMonth.addActionListener(this);
		
		// pack() 메소드는 현재 프레임의 크기를 컴포넌트에 맞게 알아서 조절 하는 역할 
		pack();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
		// 화면 가운데에 폼이 띄워지게 하는 메소드
		setLocationRelativeTo(null);
		// 창 크기 변경 못화게 하는 메소드
		setResizable(false);
	}
	
	private void getDays(int year, int month) {
		comboDay.removeAllItems();
		// 년, 월에 따른 일 수를 구하기
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month - 1);
		
		int dayOfMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		for (int i = 1; i <= dayOfMonth; i++) {
			comboDay.addItem(String.format("%02d", i));
		}
	}
	
	private void join() {
		String name = tfName.getText();
		String id = tfID.getText();
		String pw = String.valueOf(pfPW.getPassword());
		String bd = comboYear.getSelectedItem().toString() + "-" + 
				comboMonth.getSelectedItem().toString() + "-" + 
				comboDay.getSelectedItem().toString();
		boolean isValid = false;
		
		if (name.length() == 0 || id.length() == 0 || pw.length() == 0 || bd.length() != 10) {
			// 지문 2.4.1
			// 누락된 항목이 있습니다메시지 띄우기
			Util.openDialog("누락된 항목이 있습니다.", "메시지", JOptionPane.ERROR_MESSAGE);
			
			return;
		}

		// 데이터 베이스 연결
		new DatabaseManager();
		
		// 이름, 아이디, 비밀번호, 생년월일을 가지고 user 테이블에 데이터 삽입 시도
		// 아이디가 중복될 경우 메소드는 false를 반환
		isValid = DatabaseManager.insertUser(name, id, pw, bd);
		
		// 데이터 베이스 연결 종료
		DatabaseManager.close();
		
		if (isValid == true) {
			// 지문 2.4.3
			Util.openDialog("가입완료 되었습니다.", "메시지", JOptionPane.INFORMATION_MESSAGE);
			
			setVisible(false);
			
			new LoginForm();
		} else {
			// 지문 2.4.2
			Util.openDialog("아이디가 중복되었습니다.", "메시지", JOptionPane.ERROR_MESSAGE);
			tfID.setText("");
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		
		// 년, 월을 선택했을 때, 해당 월의 일수를 구해서 콤보박스 재구성
		if (source.equals(comboYear) || source.equals(comboMonth)) {
			// 지문 2.3
			String strYear = ((String)comboYear.getSelectedItem());
			String strMonth = ((String)comboMonth.getSelectedItem());
			
			if (strYear == null || strYear.equals(""))
				return;
			
			if (strMonth == null || strMonth.equals(""))
				return;
			
			int year = Integer.parseInt(strYear);
			int month = Integer.parseInt(strMonth);
			
			getDays(year, month);
		} else if (source.equals(btnOK)) {
			join();
		} else if (source.equals(btnCancel)) {
			// 지문 2.5
			setVisible(false);
			
			new LoginForm();
		}
	}

}
