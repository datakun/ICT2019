package starbox;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class LoginForm extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	JButton btnJoin; // 회원가입
	JButton btnExit; // 종료
	JButton btnLogin; // 로그인
	JTextField tfID; // 아이디 입력 필드
	JPasswordField pfPW; // 비밀번호 입력 필드

	public LoginForm() {
		setTitle("로그인");

		// 상단 레이블 생성 후, 마진 설정
		JLabel labelTitle = new JLabel("STARBOX",JLabel.CENTER);
		labelTitle.setBorder(new EmptyBorder(10, 0, 0, 0));
		
		// 글꼴 이름은 문제를 보고 적당히 비슷한 것 사용
		Font font = new Font("휴먼둥근헤드라인", Font.BOLD, 32);
		labelTitle.setFont(font);

		// 아이디, 비밀번호 입력 패널 생성
		JPanel panelTextField = new JPanel();
		panelTextField.setLayout(new BoxLayout(panelTextField, BoxLayout.Y_AXIS));
		
		// 아이디 패널 생성
		JPanel panelID = new JPanel();
		JLabel labelID = new JLabel("ID :", JLabel.RIGHT);
		labelID.setPreferredSize(new Dimension(70, 20));
		tfID = new JTextField(20);
		tfID.setPreferredSize(new Dimension(160, 20));
		panelID.add(labelID);
		panelID.add(tfID);
		
		// 비밀번호 패널 생성
		JPanel panelPW = new JPanel();
		JLabel labelPW = new JLabel("PW :", JLabel.RIGHT);
		labelPW.setPreferredSize(new Dimension(70, 20));
		// 지문 1.1
		pfPW = new JPasswordField(20);
		tfID.setPreferredSize(new Dimension(160, 20));
		panelPW.add(labelPW);
		panelPW.add(pfPW);

		panelTextField.add(panelID);
		panelTextField.add(panelPW);
		
		// 로그인 버튼 패널 생성
		JPanel panelLogin = new JPanel();
		btnLogin = new JButton("로그인");
		btnLogin.setPreferredSize(new Dimension(80, 60));
		panelLogin.add(btnLogin);

		// 센터 패널 생성
		JPanel panelCenter = new JPanel();
		panelCenter.setLayout(new BoxLayout(panelCenter, BoxLayout.X_AXIS));
		panelCenter.setBorder(new EmptyBorder(10, 10, 0, 10));
		
		panelCenter.add(panelTextField);
		panelCenter.add(panelLogin);

		// 하단 버튼 패널 생성
		JPanel panelButton = new JPanel(new FlowLayout(FlowLayout.CENTER));
		btnJoin = new JButton("회원가입");
		btnExit = new JButton("종료");
		panelButton.add(btnJoin);
		panelButton.add(btnExit);
		
		add(labelTitle, BorderLayout.NORTH);
		add(panelCenter, BorderLayout.CENTER);
		add(panelButton, BorderLayout.SOUTH);
		
		// 리스너 연결
		btnLogin.addActionListener(this);
		btnJoin.addActionListener(this);
		btnExit.addActionListener(this);
		
		// pack() 메소드는 현재 프레임의 크기를 컴포넌트에 맞게 알아서 조절 하는 역할 
		pack();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
		// 화면 가운데에 폼이 띄워지게 하는 메소드
		setLocationRelativeTo(null);
		// 창 크기 변경 못화게 하는 메소드
		setResizable(false);
	}
	
	private void login() {
		String id = tfID.getText();
		String pw = String.valueOf(pfPW.getPassword());
		boolean isValid = false;
		
		if (id.length() == 0 || pw.length() == 0) {
			// 지문 1.2.1
			// 빈 칸이 존재합니다 메시지 띄우기
			Util.openDialog("빈칸이 존재합니다.", "메시지", JOptionPane.ERROR_MESSAGE);
			
			return;
		}
		
		if (id.equals("admin")) {
			// 지문 1.2.4
			// 관리자 계정 처리
			isValid = pw.equals("1234");
			
			if (isValid == false) {
				// 지문 1.2.2
				// 회원정보 틀립니다 메시지 띄우기
				Util.openDialog("회원정보가 틀립니다. 다시입력해주세요.", "메시지", JOptionPane.ERROR_MESSAGE);
			} else {
				// 지문 1.2.4
				// 관리자 메뉴 폼 띄우기
				setVisible(false);
				
				new ManageForm();
			}
		} else {
			// 일반 계정 처리
			// 데이터 베이스 연결
			new DatabaseManager();
			
			// 아이디, 비밀번호를 가지고 user 테이블에서 해당 계정 정보가 존재하는지 확인
			isValid = DatabaseManager.isValidUser(id, pw);
			
			// 데이터 베이스 연결 종료
			DatabaseManager.close();
			
			if (isValid == false) {
				// 지문 1.2.2
				// 회원정보 틀립니다 메시지 띄우기
				Util.openDialog("회원정보가 틀립니다. 다시입력해주세요.", "메시지", JOptionPane.ERROR_MESSAGE);
			} else {
				// 지문 1.2.3
				// Starbox 폼 띄우기
				setVisible(false);
				
				new StarboxForm(id);
			}
		}
	}
	
	private void openJoinForm() {
		setVisible(false);
		
		new JoinForm();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// 버튼을 눌렀을 때 처리하는 리스너
		Object source = e.getSource();
		
		// source 객체와 btnLogin 객체가 같으면.
		if (source.equals(btnLogin)) {
			// 지문 1.2
			login();
		} else if (source.equals(btnJoin)) {
			// 지문 1.3
			openJoinForm();
		} else if (source.equals(btnExit)) {
			// 지문 1.4
			System.exit(0);
		}
	}
}
