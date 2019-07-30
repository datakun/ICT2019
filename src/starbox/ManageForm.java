package starbox;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

public class ManageForm extends JFrame implements ActionListener {
	JButton btnAdd; // 메뉴 등록
	JButton btnEdit; // 메뉴 관리
	JButton btnLogout; // 로그아웃
	
	public ManageForm() {
		setTitle("관리자 메뉴");
		
		setLayout(new GridLayout(3, 1));

		btnAdd = new JButton("메뉴 등록");
		btnAdd.setPreferredSize(new Dimension(250, 60));
		btnEdit = new JButton("메뉴 관리");
		btnEdit.setPreferredSize(new Dimension(250, 60));
		btnLogout = new JButton("로그아웃");
		btnLogout.setPreferredSize(new Dimension(250, 60));
		
		add(btnAdd);
		add(btnEdit);
		add(btnLogout);
		
		// 리스너 연결
		btnAdd.addActionListener(this);
		btnEdit.addActionListener(this);
		btnLogout.addActionListener(this);
		
		// pack() 메소드는 현재 프레임의 크기를 컴포넌트에 맞게 알아서 조절 하는 역할 
		pack();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
		// 화면 가운데에 폼이 띄워지게 하는 메소드
		setLocationRelativeTo(null);
		// 창 크기 변경 못화게 하는 메소드
		setResizable(false);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// 버튼을 눌렀을 때 처리하는 리스너
		Object source = e.getSource();
		
		if (source.equals(btnAdd)) {
			// 지문 7.1
			setVisible(false);
			
			new MenuAddForm();
		} else if (source.equals(btnEdit)) {
			// 지문 7.2
			setVisible(false);
			
			new MenuEditForm();
		} else if (source.equals(btnLogout)) {
			// 지문 7.3.4
			setVisible(false);
			
			new LoginForm();
		}
	}

}
