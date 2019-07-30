package starbox;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

public class MenuPanel extends JPanel {
	CoffeeMenu menu;
	
	public MenuPanel(CoffeeMenu menu) {
		// 커스텀 패널
		this.menu = menu;
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		// 메뉴 이미지
		JPanel panelImage = new JPanel();
		JLabel labelImage = new JLabel();
		labelImage.setPreferredSize(new Dimension(200, 200));
		Border border = BorderFactory.createLineBorder(Color.BLACK);
		labelImage.setBorder(border);
		String filepath = "이미지\\" + menu.name + ".jpg";
		Image image = Util.getScaledImage(filepath, 200, 200);
		if (image == null)
			labelImage.setIcon(null);
		else
			labelImage.setIcon(new ImageIcon(image));
		panelImage.add(labelImage);
		
		// 메뉴 이름
		JPanel panelTitle = new JPanel();
		JLabel labelTitle = new JLabel(menu.name);
		labelTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
		labelTitle.setAlignmentY(Component.TOP_ALIGNMENT);
		panelTitle.add(labelTitle);
		
		add(panelImage);
		add(labelTitle);
		// BoxLayout의 맨 마지막에 20만큼의 여백을 줌
		add(Box.createVerticalStrut(20));
		
		setVisible(true);
	}
}
