package starbox;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Map;

import javax.swing.JPanel;

public class GraphPanel extends JPanel {
	String [] titleList = null;
	int [] valueList = null;
	
	Color [] colorList = { Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.CYAN };
	
	public GraphPanel() {
		this.titleList = null;
		this.valueList = null;
	}
	
	public void setTitleValue(String [] titleList, int [] valueList) {
		this.titleList = titleList;
		this.valueList = valueList;
	}
	
	public void paint(Graphics g){
		// 그리기 이벤트(invaliDate, repaint 등등)
		if (titleList == null || valueList == null)
			return;
		
		// 그래프의 바의 최대 너비를 설정
		int maxWidth = getWidth() - 100;
		// 그래프의 바의 최대 값을 설정
		int maxValue = valueList[0];
		int startX = 50;
		int startY = 50;
		int height = 35;
		int offset = 35;
		
		// 사각형 영역만큼 이전에 그린 것들을 다 지워라
		g.clearRect(0, 0, getWidth(), getHeight());
		// 두 좌표를 잇는 선 그리기
		g.drawLine(startX, startY, startX, getHeight() - startY);
		
		for (int i = 1; i <= titleList.length; i++) {
			// 그래프 바의 세로 시작 좌표를 계산
			int y = startY + height * i + offset * (i - 1);
			// 그래프 바의 너비를 계산
			int width = (int) (maxWidth * (double) valueList[i - 1] / (double) maxValue);
			
			// 브러시 색 설정
			g.setColor(colorList[i - 1]);
			// 사각형 영역만큼 색칠
			g.fillRect(startX, y, width, height);
			// 브러시 색 설정
			g.setColor(Color.BLACK);
			// 사각형 영역의 테두리 그리기
			g.drawRect(startX, y, width, height);
			// x, y 좌표 위치에 글자 표시
			g.drawString(titleList[i - 1], startX + 5, y + height + 20);	
		}
	}
}
