package starbox;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import javax.imageio.ImageIO;
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
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

public class MenuEditForm extends JFrame implements ActionListener {
	JButton btnImageAdd; // 사진선택
	JButton btnDelete; // 삭제
	JButton btnEdit; // 수정
	JButton btnCancel; // 취소
	JComboBox<String> comboGroup; // 분류
	JTextField tfName; // 메뉴명
	JTextField tfPrice; // 가격
	JLabel labelImage; // 이미지 레이블
	String imageFilePath; // 이미지 파열 경로
	JComboBox<String> comboSearch; // 검색 분류
	JTable tableSearch; // 검색 메뉴 테이블
	DefaultTableModel modelMenu; // 테이블에서 사용하는 모델
	JTextField tfSearch; // 검색
	JButton btnSearch; // 찾기

	public MenuEditForm() {
		setTitle("메뉴 수정");

		setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

		JPanel panelSearch = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel labelSearch = new JLabel("검색");
		comboSearch = new JComboBox<>();
		// 지문 9.1
		comboSearch.addItem("전체");
		comboSearch.addItem("음료");
		comboSearch.addItem("푸드");
		comboSearch.addItem("상품");
		tfSearch = new JTextField(16);
		btnSearch = new JButton("찾기");
		panelSearch.add(labelSearch);
		panelSearch.add(comboSearch);
		panelSearch.add(tfSearch);
		panelSearch.add(btnSearch);

		// 테이블 생성
		JPanel panelLeftSide = new JPanel();
		// 테이블 칼럼 이름
		String[] columns = { "분류", "메뉴명", "가격" };
		// 테이블에서 사용하는 모델 생성
		modelMenu = new DefaultTableModel(columns, 0);
		tableSearch = new JTable(modelMenu);
		JScrollPane scrollSearch = new JScrollPane(tableSearch);
		scrollSearch.setPreferredSize(new Dimension(330, 200));
		panelLeftSide.add(scrollSearch);

		// 분류, 메뉴명, 가격 패널 생성
		JPanel panelInfo = new JPanel();
		panelInfo.setLayout(new BoxLayout(panelInfo, BoxLayout.Y_AXIS));

		// 분류 패널 생성
		JPanel panelGroup = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel labelGroup = new JLabel("분류");
		labelGroup.setPreferredSize(new Dimension(60, 20));
		comboGroup = new JComboBox<>();
		// 지문 9.2
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
		labelImage.setPreferredSize(new Dimension(120, 120));
		Border border = BorderFactory.createLineBorder(Color.BLACK);
		labelImage.setBorder(border);
		panelImage.add(labelImage);

		JPanel panelImageAdd = new JPanel();
		btnImageAdd = new JButton("사진선택");
		btnImageAdd.setPreferredSize(new Dimension(120, 25));
		panelImageAdd.add(btnImageAdd);

		JPanel panelImageInfo = new JPanel();
		panelImageInfo.setLayout(new BoxLayout(panelImageInfo, BoxLayout.Y_AXIS));

		panelImageInfo.add(panelImage);
		panelImageInfo.add(panelImageAdd);

		// 센터 패널 생성
		JPanel panelCenter = new JPanel();
		panelCenter.setLayout(new BoxLayout(panelCenter, BoxLayout.X_AXIS));

		panelCenter.add(panelInfo);
		panelCenter.add(panelImageInfo);

		// 하단 버튼 패널 생성
		JPanel panelButton = new JPanel();
		btnDelete = new JButton("삭제");
		btnEdit = new JButton("수정");
		btnCancel = new JButton("취소");
		panelButton.add(btnDelete);
		panelButton.add(btnEdit);
		panelButton.add(btnCancel);

		JPanel panelRightSide = new JPanel();
		panelRightSide.setLayout(new BoxLayout(panelRightSide, BoxLayout.Y_AXIS));

		panelRightSide.add(panelCenter);
		panelRightSide.add(panelButton);

		JPanel panelLower = new JPanel();
		panelLower.setLayout(new BoxLayout(panelLower, BoxLayout.X_AXIS));

		panelLower.add(panelLeftSide);
		panelLower.add(panelRightSide);

		add(panelSearch);
		add(panelLower);

		// 리스너 연결
		btnSearch.addActionListener(this);
		btnDelete.addActionListener(this);
		btnEdit.addActionListener(this);
		btnCancel.addActionListener(this);
		btnImageAdd.addActionListener(this);
		// 재활용하지 않늘 리스너는 무명 클래스로 처리하는 것이 편함
		tableSearch.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				// 지문 9.4
				int row = tableSearch.getSelectedRow();

				String group = modelMenu.getValueAt(row, 0).toString();
				String name = modelMenu.getValueAt(row, 1).toString();
				String price = modelMenu.getValueAt(row, 2).toString();

				// 메뉴 정보 표시
				comboGroup.setSelectedItem(group);
				tfName.setText(name);
				tfPrice.setText(price);

				imageFilePath = "이미지\\" + name + ".jpg";

				// 레이블 크기에 맞게 이미지 크기를 조정
				Image destImage = Util.getScaledImage(imageFilePath, labelImage.getWidth(), labelImage.getHeight());

				// 이미지 변환에 성공했다면
				if (destImage != null) {
					// 레이블에 이미지 표시
					labelImage.setIcon(new ImageIcon(destImage));
				} else {
					// 레이블의 이미지 제거
					labelImage.setIcon(null);
				}
			}
		});

		// pack() 메소드는 현재 프레임의 크기를 컴포넌트에 맞게 알아서 조절 하는 역할
		pack();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
		// 화면 가운데에 폼이 띄워지게 하는 메소드
		setLocationRelativeTo(null);
		// 창 크기 변경 못화게 하는 메소드
		setResizable(false);
	}

	private void searchMenu() {
		String group = comboSearch.getSelectedItem().toString();
		String name = tfSearch.getText();

		// 데이터 베이스 연결
		new DatabaseManager();

		// 메뉴 검색
		ResultSet rs = DatabaseManager.selectMenu(group, name);

		try {
			// 칼럼 정보 가져오기
			ResultSetMetaData rsMetaData = rs.getMetaData();

			// 데이터를 담을 오브젝트 생성
			Object [] tempObject = new Object[rsMetaData.getColumnCount()];

			// 모델의 초기화
			modelMenu.setRowCount(0);

			// 결과로 받은 메뉴 목록을 탐색
			while (rs.next()) {
				for (int i = 0; i < rsMetaData.getColumnCount(); i++) {
					tempObject[i] = rs.getString(i + 1);
				}

				// 모델에 메뉴 데이터 추가
				modelMenu.addRow(tempObject);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 데이터 베이스 연결 종료
		// NOTE:이번에는 ResultSet을 DatabaseManager 바깥에서 사용하기 때문에
		// 데이터 베이스 연결 종료를 맨 마지막에 함.
		// 만약 ResultSet을 사용하기 전에 데이터 베이스 연결을 끊으면 예외 발생.
		DatabaseManager.close();
		
		// 메뉴 상세정보 초기화
		comboGroup.setSelectedItem("음료");
		tfName.setText("");
		tfPrice.setText("");
		labelImage.setIcon(null);
	}

	private void selectImage() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new FileNameExtensionFilter("JPG image", "jpg"));
		int ret = fileChooser.showOpenDialog(null);
		if (ret == JFileChooser.APPROVE_OPTION) {
			try {
				// FileChooser에서 선택한 파일의 경로를 가져옴
				imageFilePath = fileChooser.getSelectedFile().getPath();

				// 레이블 크기에 맞게 이미지 크기를 조정
				BufferedImage srcImage = ImageIO.read(new File(imageFilePath));
				Image destImage = srcImage.getScaledInstance(labelImage.getWidth(), labelImage.getHeight(), Image.SCALE_SMOOTH);

				// 레이블에 이미지 적용
				labelImage.setIcon(new ImageIcon(destImage));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void deleteMenu() {
		int row = tableSearch.getSelectedRow();
		
		// 지문 9.5.1
		if (row < 0) {
			Util.openDialog("삭제할 메뉴를 선택해주세요.", "메시지", JOptionPane.ERROR_MESSAGE);
			
			return;
		}

		String name = modelMenu.getValueAt(row, 1).toString();
		
		boolean isValid = false;

		// 데이터 베이스 연결
		new DatabaseManager();

		// 메뉴명으로 메뉴 삭제
		isValid = DatabaseManager.deleteMenu(name);

		// 데이터 베이스 연결 종료
		DatabaseManager.close();

		if (isValid == true) {
			// 지문 9.5.2
			Util.openDialog("삭제되었습니다.", "메시지", JOptionPane.INFORMATION_MESSAGE);

			// 지문 9.5.2
			try {
				// 이미지 삭제
				String filepath = "이미지\\" + name + ".jpg";
				
				File oldFile = new File(filepath);
				oldFile.delete();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			// 삭제 후에 메뉴를 다시 검색
			searchMenu();
		}
	}

	private void updateMenu() {
		int row = tableSearch.getSelectedRow();
		
		// 지문 9.6.1
		if (row < 0) {
			Util.openDialog("수정할 메뉴를 선택해주세요.", "메시지", JOptionPane.ERROR_MESSAGE);
			
			return;
		}
		
		String oldName = modelMenu.getValueAt(row, 1).toString();
		
		String group = comboGroup.getSelectedItem().toString();
		String name = tfName.getText();
		String price = tfPrice.getText();
		boolean isValid = false;
		
		if (group.length() == 0 || name.length() == 0 || price.length() == 0) {
			// 지문 9.6.2
			// 빈 칸이 존재합니다 메시지 띄우기
			Util.openDialog("빈칸이 존재합니다.", "메시지", JOptionPane.ERROR_MESSAGE);
			
			return;
		}

		// 지문 9.6.3
		try {
			// 문자열을 정수형으로 변환
			// 문자열이 숫자가 아닐 때, 문자열을 숫자로 변환하면 예외가 발생하는 것을 이용
			int nPrice = Integer.parseInt(price);
		} catch (Exception e) {
			Util.openDialog("가격을 다시 입력해주세요.", "메시지", JOptionPane.ERROR_MESSAGE);
			
			return;
		}

		// 데이터 베이스 연결
		new DatabaseManager();
		
		// 이전 메뉴명, 분류, 메뉴명, 가격을 menu 테이블에서 데이터 수정 시도
		// 이전 메뉴명과 새로운 메뉴명이 다르지만
		// 새로운 메뉴명이 다른 메뉴명과 중복될 경우 메소드는 false를 반환
		isValid = DatabaseManager.updateMenu(oldName, group, name, price);
		
		// 데이터 베이스 연결 종료
		DatabaseManager.close();
		
		if (isValid == true) {
			// 지문 9.6.5
			Util.openDialog("수정되었습니다.", "메시지", JOptionPane.INFORMATION_MESSAGE);
			
			String destFilePath = "이미지\\" + name + ".jpg";

			// 실행파일과 같은 경로에 있는 '이미지' 폴더에 파일을 복사
			// 데이터베이스에 있는 메뉴 이름으로 이미지 파일 저장
			Util.copyFile(imageFilePath, destFilePath);

			// 이전 메뉴와 이름이 다르다면 이전 메뉴 이미지 삭제
			if (oldName.equals(name) == false) {
				String filepath = "이미지\\" + oldName + ".jpg";
				
				File oldFile = new File(filepath);
				oldFile.delete();
			}
			
			// 지문 9.6.5
			searchMenu();
		} else {
			// 지문 9.6.4
			Util.openDialog("이미 존재하는 메뉴명입니다.", "메시지", JOptionPane.ERROR_MESSAGE);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// 버튼을 눌렀을 때 처리하는 리스너
		Object source = e.getSource();

		if (source.equals(btnSearch)) {
			// 지문 9.3
			searchMenu();
		} else if (source.equals(btnImageAdd)) {
			selectImage();
		} else if (source.equals(btnDelete)) {
			// 지문 9.5
			deleteMenu();
		} else if (source.equals(btnEdit)) {
			// 지문 9.6
			updateMenu();
		} else if (source.equals(btnCancel)) {
			// 지문 9.6.7
			setVisible(false);

			new ManageForm();
		}
	}
}
