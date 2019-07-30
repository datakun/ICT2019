package starbox;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.filechooser.FileNameExtensionFilter;

public class MenuAddForm extends JFrame implements ActionListener {
	JButton btnImageAdd; // 사진등록
	JButton btnAdd; // 등록
	JButton btnCancel; // 취소
	JComboBox<String> comboGroup; // 분류
	JTextField tfName; // 메뉴명
	JTextField tfPrice; // 가격
	JLabel labelImage; // 이미지 레이블
	String imageFilePath; // 이미지 파열 경로
	
	public MenuAddForm() {
		setTitle("메뉴추가");
		
		setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		
		// 분류, 메뉴명, 가격 패널 생성
		JPanel panelInfo = new JPanel();
		panelInfo.setLayout(new BoxLayout(panelInfo, BoxLayout.Y_AXIS));
		
		// 분류 패널 생성
		JPanel panelGroup = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel labelGroup = new JLabel("분류");
		labelGroup.setPreferredSize(new Dimension(60, 20));
		comboGroup = new JComboBox<>();
		// 지문 8.1
		comboGroup.addItem("음료");
		comboGroup.addItem("푸드");
		comboGroup.addItem("상품");
		panelGroup.add(labelGroup);
		panelGroup.add(comboGroup);
		
		// 메뉴명 패널 생성
		JPanel panelName = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel labelName = new JLabel("메뉴명");
		labelName.setPreferredSize(new Dimension(60, 20));
		tfName = new JTextField(16);
		panelName.add(labelName);
		panelName.add(tfName);
		
		// 가격 패널 생성
		JPanel panelPrice = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel labelPrice = new JLabel("가격");
		labelPrice.setPreferredSize(new Dimension(60, 20));
		tfPrice = new JTextField(16);
		panelPrice.add(labelPrice);
		panelPrice.add(tfPrice);

		panelInfo.add(panelGroup);
		panelInfo.add(panelName);
		panelInfo.add(panelPrice);
		
		// 이미지 패널 생성
		JPanel panelImage = new JPanel();
		labelImage = new JLabel();
		labelImage.setPreferredSize(new Dimension(100, 100));
		Border border = BorderFactory.createLineBorder(Color.BLACK);
		labelImage.setBorder(border);
		panelImage.add(labelImage);

		// 센터 패널 생성
		JPanel panelCenter = new JPanel();
		panelCenter.setLayout(new BoxLayout(panelCenter, BoxLayout.X_AXIS));
		
		panelCenter.add(panelInfo);
		panelCenter.add(panelImage);
		
		JPanel panelImageAdd = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		btnImageAdd = new JButton("사진등록");
		btnImageAdd.setPreferredSize(new Dimension(100, 25));
		panelImageAdd.add(btnImageAdd);

		// 하단 버튼 패널 생성
		JPanel panelButton = new JPanel();
		btnAdd = new JButton("등록");
		btnCancel = new JButton("취소");
		panelButton.add(btnAdd);
		panelButton.add(btnCancel);

		add(panelCenter);
		add(panelImageAdd);
		add(panelButton);
		
		// 리스너 연결
		btnAdd.addActionListener(this);
		btnCancel.addActionListener(this);
		btnImageAdd.addActionListener(this);
		
		// pack() 메소드는 현재 프레임의 크기를 컴포넌트에 맞게 알아서 조절 하는 역할 
		pack();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
		// 화면 가운데에 폼이 띄워지게 하는 메소드
		setLocationRelativeTo(null);
		// 창 크기 변경 못화게 하는 메소드
		setResizable(false);
	}
	
	private void selectImage() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new FileNameExtensionFilter("JPG image", "jpg"));
		int ret = fileChooser.showOpenDialog(null);
		if (ret == JFileChooser.APPROVE_OPTION) {
			// 지문 8.3
			try {
				// FileChooser에서 선택한 파일의 경로를 가져옴
				imageFilePath = fileChooser.getSelectedFile().getPath();
				
				// 레이블 크기에 맞게 이미지 크기를 조정
				Image destImage = Util.getScaledImage(imageFilePath, labelImage.getWidth(), labelImage.getHeight());

				if (destImage != null) {
					// 레이블에 이미지 적용
					labelImage.setIcon(new ImageIcon(destImage));	
				} else {
					// 레이블의 이미지 제거
					labelImage.setIcon(null);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void insertMenu() {
		String group = comboGroup.getSelectedItem().toString();
		String name = tfName.getText();
		String price = tfPrice.getText();
		boolean isValid = false;
		
		if (group.length() == 0 || name.length() == 0 || price.length() == 0) {
			// 지문 8.4.1
			// 빈 칸이 존재합니다 메시지 띄우기
			Util.openDialog("빈칸이 존재합니다.", "메시지", JOptionPane.ERROR_MESSAGE);
			
			return;
		}

		// 지문 8.4.2
		try {
			// 문자열을 정수형으로 변환
			// 문자열이 숫자가 아닐 때, 문자열을 숫자로 변환하면 예외가 발생하는 것을 이용
			int nPrice = Integer.parseInt(price);
		} catch (Exception e) {
			Util.openDialog("가격은 숫자로 입력해주세요.", "메시지", JOptionPane.ERROR_MESSAGE);
			
			return;
		}

		// 데이터 베이스 연결
		new DatabaseManager();
		
		// 분류, 메뉴명, 가격을 menu 테이블에 데이터 삽입 시도
		// 메뉴명이 중복될 경우 메소드는 false를 반환
		isValid = DatabaseManager.insertMenu(group, name, price);
		
		// 데이터 베이스 연결 종료
		DatabaseManager.close();
		
		if (isValid == true) {
			// 지문 8.4.4
			Util.openDialog("메뉴가 등록되었습니다.", "메시지", JOptionPane.INFORMATION_MESSAGE);
			
			// 지문 8.4.5
			String destFilePath = "이미지\\" + name + ".jpg";

			// 실행파일과 같은 경로에 있는 '이미지' 폴더에 파일을 복사
			// 데이터베이스에 있는 메뉴 이름으로 이미지 파일 저장
			Util.copyFile(imageFilePath, destFilePath);
		} else {
			// 지문 8.4.3
			Util.openDialog("이미 존재하는 메뉴명입니다.", "메시지", JOptionPane.ERROR_MESSAGE);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// 버튼을 눌렀을 때 처리하는 리스너
		Object source = e.getSource();
		
		if (source.equals(btnImageAdd)) {
			// 지문 8.2
			selectImage();
		} else if (source.equals(btnAdd)) {
			// 지문 8.4
			insertMenu();
		} else if (source.equals(btnCancel)) {
			// 지문 8.5
			setVisible(false);
			
			new ManageForm();
		}
	}
}
