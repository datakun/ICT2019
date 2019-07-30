package starbox;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PopularForm extends JFrame{
	UserInfo user;

	GraphPanel panelGraph;

	public PopularForm(UserInfo user) {
		setTitle("인기상품 Top5");

		this.user = user;

		// 분류 패널 생성
		JPanel panelGroup = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JLabel labelGroup = new JLabel("인기상품 Top5");
		Font font = new Font("돋움체", Font.PLAIN, 16);
		labelGroup.setFont(font);
		labelGroup.setPreferredSize(new Dimension(120, 30));
		JComboBox<String> comboGroup = new JComboBox<>();
		// 지문 6.1
		comboGroup.addItem("음료");
		comboGroup.addItem("푸드");
		comboGroup.addItem("상품");
		panelGroup.add(comboGroup);
		panelGroup.add(labelGroup);

		panelGraph = new GraphPanel();
		panelGraph.setPreferredSize(new Dimension(500, 500));

		add(panelGroup, BorderLayout.NORTH);
		add(panelGraph, BorderLayout.CENTER);

		// 리스너 추가
		// 이벤트 리스너를 받는 컴포넌트가 하나라서 무명 클래스로 구현
		comboGroup.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JComboBox<String> comboGroup = (JComboBox<String>) e.getSource();
				// 지문 6.2
				calculateChart(comboGroup.getSelectedItem().toString());
			}
		});

		calculateChart("음료");

		// 지문 6.3
		// 닫기 버튼 누르면 Starbox 폼이 생성되도록
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				setVisible(false);

				new StarboxForm(user.id);
			}
		});

		// pack() 메소드는 현재 프레임의 크기를 컴포넌트에 맞게 알아서 조절 하는 역할 
		pack();
		setVisible(true);
		// 화면 가운데에 폼이 띄워지게 하는 메소드
		setLocationRelativeTo(null);
		// 창 크기 변경 못화게 하는 메소드
		setResizable(false);
	}

	private void calculateChart(String group) {
		String [] titleList = new String[5];
		int [] valueList = new int[5];
		HashMap<String, Integer> popularList = new HashMap<>();

		// 데이터 베이스 연결
		new DatabaseManager();

		// 메뉴 검색
		ResultSet rs = DatabaseManager.selectOrderlistByTop5(group);

		try {
			// 칼럼 정보 가져오기
			ResultSetMetaData rsMetaData = rs.getMetaData();

			// 데이터를 담을 오브젝트 생성
			Object [] tempObject = new Object[rsMetaData.getColumnCount()];

			// 결과로 받은 메뉴 목록을 탐색
			while (rs.next()) {
				for (int i = 0; i < rsMetaData.getColumnCount(); i++) {
					tempObject[i] = rs.getString(i + 1);
				}

				String title = tempObject[0].toString();
				int value = Integer.valueOf(tempObject[1].toString());

				if (popularList.containsKey(title) == false) {
					// 인기상품 HashMap에 상품(title)이 없으면 상품 이름과 구매 횟수를 HashMap에 추가
					popularList.put(title, value);
				} else {
					// 인기상품 HaspMap에 상품이 있으면 해당 상품의 구매횟수를 더해서 HashMap에 값 넣기
					int totalValue = popularList.get(title) + value;
					popularList.replace(title, totalValue);
				}
			}

			// 구매회수로 내림차순으로 정렬하고, 구매회수가 같으면 제품명 오름차순으로 정렬
			List<Map.Entry<String, Integer>> list = new LinkedList<>(popularList.entrySet());
			Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
				@Override
				public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
					int comparision = (o1.getValue() - o2.getValue()) * -1;
					return comparision == 0 ? o1.getKey().compareTo(o2.getKey()) : comparision;
				}
			});

			// 인기상품 리스트를 만듦
			int rowCount = 0;
			for(Iterator<Map.Entry<String, Integer>> iter = list.iterator(); iter.hasNext();){
				Map.Entry<String, Integer> entry = iter.next();
				
				titleList[rowCount] = entry.getKey() + " - " + entry.getValue() + "개";
				valueList[rowCount] = entry.getValue();
				
				rowCount++;
				if (rowCount >= 5)
					break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 데이터 베이스 연결 종료
		// NOTE:이번에는 ResultSet을 DatabaseManager 바깥에서 사용하기 때문에
		// 데이터 베이스 연결 종료를 맨 마지막에 함.
		// 만약 ResultSet을 사용하기 전에 데이터 베이스 연결을 끊으면 예외 발생.
		DatabaseManager.close();

		panelGraph.setTitleValue(titleList, valueList);
		panelGraph.repaint();
	}
}
