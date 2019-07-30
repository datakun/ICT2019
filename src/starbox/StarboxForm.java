package starbox;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

public class StarboxForm extends JFrame implements ActionListener {
	UserInfo user; // 유저 정보

	JLabel labelUserInfo; // 사용자 정보
	JButton btnOrderlist; // 구매내역
	JButton btnCart; // 장바구니
	JButton btnPopular; // 인기상품 Top5
	JButton btnLogout; // 로그아웃
	JButton btnGroupDrink; // 음료
	JButton btnGroupFood; // 푸드
	JButton btnGroupItem; // 상품
	JPanel panelMenuList; // 메뉴 화면 패널
	DefaultListModel<CoffeeMenu> modelMenuList; // 메뉴 모델

	JPanel panelMenuDetail; // 메뉴 상세화면
	JTextField tfMenuName;
	JTextField tfMenuPrice;
	JTextField tfMenuAmount;
	JComboBox<String> comboMenuCount;
	JComboBox<String> comboMenuSize;
	JButton btnAddCart;
	JButton btnBuy;

	String currentGroup; // 현재 선택한 메뉴 분류

	Dimension originalWindowSize; // 원본 윈도우 크기

	public StarboxForm(String userID) {
		setTitle("STARBOX");
		
		user = new UserInfo();
		
		setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

		// 유저 정보
		// 왼쪽 정렬로 컴포넌트를 배치하고 싶을땐 FlowLayout.LEFT가 좋음
		JPanel panelUser = new JPanel(new FlowLayout(FlowLayout.LEFT));
		labelUserInfo = new JLabel();
		Font font = new Font("돋움체", Font.PLAIN, 14);
		labelUserInfo.setFont(font);
		labelUserInfo.setAlignmentY(Component.CENTER_ALIGNMENT);
		panelUser.add(labelUserInfo);

		// 유저 정보 가져오기
		// 지문 3.1
		getUserInfo(userID);

		// 상단 메뉴 버튼
		JPanel panelTopMenu = new JPanel(new FlowLayout(FlowLayout.LEFT));
		btnOrderlist = new JButton("구매내역");
		btnCart = new JButton("장바구니");
		btnPopular = new JButton("인기 상품 Top5");
		btnLogout = new JButton("Logout");
		panelTopMenu.add(btnOrderlist);
		panelTopMenu.add(btnCart);
		panelTopMenu.add(btnPopular);
		panelTopMenu.add(btnLogout);

		JPanel panelCenter = new JPanel();
		panelCenter.setLayout(new BoxLayout(panelCenter, BoxLayout.X_AXIS));

		JPanel panelSideMenu = new JPanel();
		panelSideMenu.setLayout(new BoxLayout(panelSideMenu, BoxLayout.Y_AXIS));
		btnGroupDrink = new JButton("음료");
		btnGroupDrink.setPreferredSize(new Dimension(70, 40));
		btnGroupFood = new JButton("푸드");
		btnGroupFood.setPreferredSize(new Dimension(70, 40));
		btnGroupItem = new JButton("상품");
		btnGroupItem.setPreferredSize(new Dimension(70, 40));
		panelSideMenu.add(btnGroupDrink);
		panelSideMenu.add(btnGroupFood);
		panelSideMenu.add(btnGroupItem);
		// BoxLayout 사용 시, 나머지 공간을 채우는 역할
		panelSideMenu.add(Box.createGlue());

		modelMenuList = new DefaultListModel<>();

		panelMenuList = new JPanel();

		// 메뉴 목록 가져오기
		// 지문 3.2
		updateMenuList("음료");

		// GridLayout 행 개수 구하기
		int rowCount = 1;
		if (modelMenuList.getSize() > 0) {
			rowCount = modelMenuList.getSize() / 3;

			if (modelMenuList.getSize() % 3 > 0)
				rowCount += 1;
		}

		JScrollPane scrollMenuItem = new JScrollPane(panelMenuList);
		scrollMenuItem.setPreferredSize(new Dimension(700, 500));

		//		panelMenuDetail = new JPanel(new FlowLayout(FlowLayout.CENTER));
		panelMenuDetail = new JPanel();
		panelMenuDetail.setLayout(new BoxLayout(panelMenuDetail, BoxLayout.Y_AXIS));
		panelMenuDetail.setBorder(new EmptyBorder(15, 15, 15, 15));

		panelCenter.add(panelSideMenu);
		panelCenter.add(scrollMenuItem);
		panelCenter.add(panelMenuDetail);
		panelCenter.add(Box.createGlue());

		add(panelUser);
		add(panelTopMenu);
		add(panelCenter);

		// 리스너 연결
		btnGroupDrink.addActionListener(this);
		btnGroupFood.addActionListener(this);
		btnGroupItem.addActionListener(this);
		btnOrderlist.addActionListener(this);
		btnCart.addActionListener(this);
		btnPopular.addActionListener(this);
		btnLogout.addActionListener(this);

		// pack() 메소드는 현재 프레임의 크기를 컴포넌트에 맞게 알아서 조절 하는 역할
		pack();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
		// 화면 가운데에 폼이 띄워지게 하는 메소드
		setLocationRelativeTo(null);
		// 창 크기 변경 못화게 하는 메소드
		setResizable(false);

		originalWindowSize = getContentPane().getSize();
	}

	private void getUserInfo(String userID) {
		// 데이터 베이스 연결
		new DatabaseManager();

		// 모든 메뉴 가져오기
		ResultSet rs = DatabaseManager.selectUser(userID);

		try {
			while (rs.next()) {
				// 유저 정보 가져오기
				user.id = rs.getString(2);
				user.name = rs.getString(4);
				user.birthday = rs.getString(5);
				user.point = rs.getInt(6);
				user.grade = rs.getString(7);
				if (user.grade == null || user.grade.equals(""))
					user.grade = "일반";

				// 유저 정보 정리
				String text = "회원명 : " + user.name +
						" / 회원등급 : " + user.grade +
						" / 총 누적 포인트 : " + String.valueOf(user.point);

				// 유저 정보 설정
				labelUserInfo.setText(text);

				// 반복문 탈출(유저는 한 명이기 때문에 필요없긴 함)
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 데이터 베이스 연결 종료
		DatabaseManager.close();
	}

	private void searchMenu(String group) {
		// 데이터 베이스 연결
		new DatabaseManager();

		// 모든 메뉴 가져오기
		ResultSet rs = DatabaseManager.selectMenu(group, "");

		try {
			// 모델의 초기화
			modelMenuList.setSize(0);

			// 결과로 받은 메뉴 목록을 탐색
			while (rs.next()) {
				// 데이터를 담을 오브젝트 생성
				CoffeeMenu menu = new CoffeeMenu();
				menu.group = rs.getString(1);
				menu.name = rs.getString(2);
				menu.price = rs.getString(3);

				// 모델에 메뉴 데이터 추가
				modelMenuList.addElement(menu);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 데이터 베이스 연결 종료
		DatabaseManager.close();
	}

	private void updateMenuList(String group) {
		// 현재 선택한 메뉴 분류 저장
		currentGroup = group;

		// 메뉴 목록 가져오기
		searchMenu(group);

		// GridLayout 행 개수 구하기
		int rowCount = 1;
		if (modelMenuList.getSize() > 0) {
			rowCount = modelMenuList.getSize() / 3;

			if (modelMenuList.getSize() % 3 > 0)
				rowCount += 1;
		}

		// 행 개수 x 3 크기의 GridLayout을 만들어 메뉴 목록을 작성
		GridLayout layoutMenuList = new GridLayout(rowCount, 3);
		panelMenuList.setLayout(layoutMenuList);

		// 기존의 메뉴 목록을 모두 삭제
		panelMenuList.removeAll();
		for (int i = 0; i < modelMenuList.getSize(); i++) {
			MenuPanel panelMenu = new MenuPanel(modelMenuList.getElementAt(i));

			panelMenuList.add(panelMenu);

			// 지문 3.8
			// 메뉴 아이템에 임시로 리스너를 추가하기때문에 무명 클래스로 리스너 구현
			panelMenu.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					super.mouseClicked(e);

					MenuPanel panelMenu = (MenuPanel) e.getSource();

					openMenuDetail(panelMenu.menu);
				}
			});
		}

		// GUI를 변경하고 나면(컴포넌트 추가 혹은 삭제/변경) 화면을 새로고침 해야 함
		validate();
	}

	private void openMenuDetail(CoffeeMenu menu) {
		panelMenuDetail.removeAll();

		// 지문 3.8
		// 선택한 메뉴 이미지
		JPanel panelMenuImage = new JPanel();
		JLabel labelMenuImage = new JLabel();
		labelMenuImage.setPreferredSize(new Dimension(180, 180));
		Border border = BorderFactory.createLineBorder(Color.BLACK);
		labelMenuImage.setBorder(border);
		String filepath = "이미지\\" + menu.name + ".jpg";
		Image image = Util.getScaledImage(filepath, 180, 180);
		if (image == null)
			labelMenuImage.setIcon(null);
		else
			labelMenuImage.setIcon(new ImageIcon(image));
		labelMenuImage.setAlignmentY(Component.TOP_ALIGNMENT);
		panelMenuImage.add(labelMenuImage);

		// 지문 3.9
		JLabel labelMenuName = new JLabel("주문메뉴 : ", JLabel.RIGHT);
		labelMenuName.setPreferredSize(new Dimension(70, 20));
		tfMenuName = new JTextField(menu.name, 16);
		tfMenuName.setPreferredSize(new Dimension(200, 20));
		tfMenuName.setEnabled(false);
		JPanel panelMenuName = new JPanel();
		panelMenuName.setLayout(new BoxLayout(panelMenuName, BoxLayout.X_AXIS));
		panelMenuName.add(labelMenuName);
		panelMenuName.add(tfMenuName);

		JLabel labelMenuPrice = new JLabel("가격 : ", JLabel.RIGHT);
		labelMenuPrice.setPreferredSize(new Dimension(70, 20));
		tfMenuPrice = new JTextField(menu.price, 16);
		tfMenuPrice.setPreferredSize(new Dimension(200, 20));
		tfMenuPrice.setEnabled(false);
		JPanel panelMenuPrice = new JPanel();
		panelMenuPrice.setLayout(new BoxLayout(panelMenuPrice, BoxLayout.X_AXIS));
		panelMenuPrice.add(labelMenuPrice);
		panelMenuPrice.add(tfMenuPrice);

		JLabel labelMenuAmount = new JLabel("총금액 : ", JLabel.RIGHT);
		labelMenuAmount.setPreferredSize(new Dimension(70, 20));
		tfMenuAmount = new JTextField("", 16);
		tfMenuAmount.setPreferredSize(new Dimension(200, 20));
		tfMenuAmount.setEnabled(false);
		JPanel panelMenuAmount = new JPanel();
		panelMenuAmount.setLayout(new BoxLayout(panelMenuAmount, BoxLayout.X_AXIS));
		panelMenuAmount.add(labelMenuAmount);
		panelMenuAmount.add(tfMenuAmount);

		// 지문 3.11
		JLabel labelMenuCount = new JLabel("수량 : ", JLabel.RIGHT);
		labelMenuCount.setPreferredSize(new Dimension(70, 20));
		comboMenuCount = new JComboBox<>();
		comboMenuCount.setPreferredSize(new Dimension(200, 20));
		for (int i = 1; i <= 10; i++) {
			comboMenuCount.addItem(String.valueOf(i));
		}
		JPanel panelMenuCount = new JPanel();
		panelMenuCount.setLayout(new BoxLayout(panelMenuCount, BoxLayout.X_AXIS));
		panelMenuCount.add(labelMenuCount);
		panelMenuCount.add(comboMenuCount);

		JLabel labelMenuSize = new JLabel("사이즈 : ", JLabel.RIGHT);
		labelMenuSize.setPreferredSize(new Dimension(70, 20));
		comboMenuSize = new JComboBox<>();
		comboMenuSize.setPreferredSize(new Dimension(200, 20));
		if (menu.group.equals("상품")) {
			// 지문 3.10
			comboMenuSize.setEnabled(false);
		} else {
			// 지문 3.12
			comboMenuSize.addItem("M");
			comboMenuSize.addItem("L");
		}
		JPanel panelMenuSize = new JPanel();
		panelMenuSize.setLayout(new BoxLayout(panelMenuSize, BoxLayout.X_AXIS));
		panelMenuSize.add(labelMenuSize);
		panelMenuSize.add(comboMenuSize);

		JPanel panelOption = new JPanel();
		GridLayout layoutOption = new GridLayout(5, 1);
		layoutOption.setVgap(10);
		panelOption.setLayout(layoutOption);
		panelOption.add(panelMenuName);
		panelOption.add(panelMenuPrice);
		panelOption.add(panelMenuCount);
		panelOption.add(panelMenuSize);
		panelOption.add(panelMenuAmount);

		JPanel panelInfo = new JPanel();
		panelInfo.setLayout(new BoxLayout(panelInfo, BoxLayout.X_AXIS));
		panelInfo.add(panelMenuImage);
		panelInfo.add(panelOption);

		// 지문 3.13
		// 콤보박스에 리스너 추가
		comboMenuCount.addActionListener(this);
		comboMenuSize.addActionListener(this);

		// 하단 버튼 패널 생성
		JPanel panelButton = new JPanel();
		btnAddCart = new JButton("장바구니에 담기");
		btnBuy = new JButton("구매하기");
		panelButton.add(btnAddCart);
		panelButton.add(btnBuy);

		// 위쪽에 공간을 주기 위해 Box.createGlue() 사용
		panelMenuDetail.add(Box.createGlue());
		panelMenuDetail.add(panelInfo);
		panelMenuDetail.add(panelButton);

		// 리스너 추가
		btnAddCart.addActionListener(this);
		btnBuy.addActionListener(this);

		// 메뉴 상세화면 컴포넌트가 추가된 만큼 크기 조절
		pack();

		// 총금액 계산
		calculateAmount();
	}

	private void closeMenuDetail() {
		panelMenuDetail.removeAll();

		// 메뉴 상세화면 컴포넌트가 삭제된 만큼 크기 조절
		pack();
	}

	private void calculateAmount() {
		int amount = 0;
		int price = Integer.parseInt(tfMenuPrice.getText());
		int count = Integer.parseInt(comboMenuCount.getSelectedItem().toString());
		boolean isLarge = false;
		if ("L".equals(comboMenuSize.getSelectedItem().toString()) == true)
			isLarge = true;

		// 지문 3.13.1
		amount = price * count;
		if (isLarge == true)
			amount += 1000;

		// 지문 3.13.2
		switch (user.grade) {
		case "Bronze":
			amount = (int) (amount * 0.97);
			break;
		case "Silver":
			amount = (int) (amount * 0.95);

			break;
		case "Gold":
			amount = (int) (amount * 0.90);

			break;
		default:
			break;
		}

		tfMenuAmount.setText(String.valueOf(amount));
	}

	private void addCart() {
		// 지문 3.14.2
		String menuName = tfMenuName.getText();
		String price = tfMenuPrice.getText();
		String count = comboMenuCount.getSelectedItem().toString();
		String size = comboMenuSize.getSelectedItem().toString();
		String amount = tfMenuAmount.getText();
		boolean isValid = false;

		// 데이터 베이스 연결
		new DatabaseManager();

		// 유저 아이디,메뉴 이름, 가격, 수량, 사이즈, 총금액을 shopping 테이블에 삽입
		isValid = DatabaseManager.insertShopping(user.id, menuName, price, count, size, amount);

		// 데이터 베이스 연결 종료
		DatabaseManager.close();

		if (isValid == true) {
			// 지문 3.14.1
			Util.openDialog("장바구니에 담았습니다.", "메시지", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	public void buyItems(String menuName, String group, String price, String count, String size, String amount) {
		// 지문 3.15
		
		int nAmount = Integer.parseInt(amount);
		if (nAmount > user.point) {
			// 지문 3.15.1
			payByCash(menuName, group, price, count, size, amount);
		} else {
			// 지문 3.15.2
			String message = "회원님의 총 포인트 : " + String.valueOf(user.point)
			+ "\n포인트로 결제하시겠습니까?"
			+ "\n(아니오를 클릭 시 현금결제가 됩니다)";
			int ret = Util.openConfirmDialog(message, "결제수단", JOptionPane.YES_NO_OPTION);

			if (ret == JOptionPane.YES_OPTION) {
				// 포인트로 결제
				payByPoint(amount);
			} else {
				// 현금으로 결제
				payByCash(menuName, group, price, count, size, amount);
			}
		}

		// 지문 3.15.3
		String beforeGrade = user.grade;
		updateGrade();

		// 유저 정보 업데이트
		getUserInfo(user.id);
		
		if (beforeGrade.equals(user.grade) == false) {
			// 지문 3.15.3
			String message = "축하합니다!"
					+ "\n회원님 등급이 " + user.grade + "로 승급하셨습니다.";
			Util.openDialog(message, "메시지", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	public void payByCash(String menuName, String group, String price, String count, String size, String amount) {
		// 지문 3.15.2
		boolean isValid = false;

		// 데이터 베이스 연결
		new DatabaseManager();

		// orderlist 테이블에 구매내역 삽입
		isValid = DatabaseManager.insertOrderlist(user.id, menuName, group, size, price, count, amount);

		// 데이터 베이스 연결 종료
		DatabaseManager.close();

		if (isValid == true) {
			// 지문 3.15.2
			Util.openDialog("구매되었습니다.", "메시지", JOptionPane.INFORMATION_MESSAGE);

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
	}

	public void payByPoint(String amount) {
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

		if (source.equals(btnGroupDrink)) {
			// 지문 3.8
			closeMenuDetail();
			// 지문 3.7
			updateMenuList("음료");
		} else if (source.equals(btnGroupFood)) {
			// 지문 3.8
			closeMenuDetail();
			// 지문 3.7
			updateMenuList("푸드");
		} else if (source.equals(btnGroupItem)) {
			// 지문 3.8
			closeMenuDetail();
			// 지문 3.7
			updateMenuList("상품");
		} else if (source.equals(btnOrderlist)) {
			// 지문 3.3
			setVisible(false);

			new OrderlistForm(user);
		} else if (source.equals(btnCart)) {
			// 지문 3.4
			setVisible(false);

			new ShoppingForm(user);
		} else if (source.equals(btnPopular)) {
			// 지문 3.5
			setVisible(false);

			new PopularForm(user);
		} else if (source.equals(btnLogout)) {
			// 지문 3.6
			setVisible(false);

			new LoginForm();
		} else if (source.equals(comboMenuCount) || source.equals(comboMenuSize)) {
			// 지문 3.13
			calculateAmount();
		} else if (source.equals(btnAddCart)) {
			// 지문 3.14
			addCart();
		} else if (source.equals(btnBuy)) {
			// 지문 3.15
			String menuName = tfMenuName.getText();
			String group = currentGroup;
			String price = tfMenuPrice.getText();
			String count = comboMenuCount.getSelectedItem().toString();
			String size = comboMenuSize.getSelectedItem().toString();
			String amount = tfMenuAmount.getText();
			
			buyItems(menuName, group, price, count, size, amount);
		}
	}
}
