package starbox;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class TestForm extends JFrame implements ActionListener {
	
	public TestForm() {
		setTitle("타이틀");
		
		// UI를 구성하는 코드
		
		
		
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
		// TODO Auto-generated method stub
		
	}
}
