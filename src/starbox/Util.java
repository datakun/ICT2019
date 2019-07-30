package starbox;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class Util {
	
	public static void openDialog(String message, String title, int option) {
		/*
		// option은 다음 값 중 하나로 설정 가능
		 * JOptionPane.ERROR_MESSAGE
		 * JOptionPane.INFORMATION_MESSAGE
		 * JOptionPane.WARNING_MESSAGE
		 * JOptionPane.QUESTION_MESSAGE
		 * JOptionPane.PLAIN_MESSAGE
		 */
		JOptionPane.showMessageDialog(null, message, title, option);
	}
	
	public static int openConfirmDialog(String message, String title, int option) {
		/*
		// option은 다음 값 중 하나로 설정 가능
		 * YES_NO_OPTION
		 * YES_NO_CANCEL_OPTION
		 * OK_CANCEL_OPTION
		 */
		return JOptionPane.showConfirmDialog(null, message, title, option);
	}
	
	public static Image getScaledImage(String filepath, int width, int height) {
		Image destImage = null;
		
		try {
			// 크기에 맞게 이미지 크기를 조정
			BufferedImage srcImage = ImageIO.read(new File(filepath));
			destImage = srcImage.getScaledInstance(width, height, Image.SCALE_FAST);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return destImage;
	}
	
	public static boolean copyFile(String srcFilepath, String destFilepath) {
		Path source = Paths.get(srcFilepath);
		Path target = Paths.get(destFilepath);
		
		// 사전체크
        if (source == null) {
            throw new IllegalArgumentException("source must be specified");
        }
        
        if (target == null) {
            throw new IllegalArgumentException("target must be specified");
        }
        
        // 소스파일이 실제로 존재하는지 체크
        if (!Files.exists(source, new LinkOption[] {})) {
            throw new IllegalArgumentException("Source file doesn't exist: " + source.toString());
        }
        
        try {
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);   // 파일복사
        } catch (IOException e) {
            e.printStackTrace();
            
            return false;
        }
        
    	// 파일이 정상적으로 생성이 되었다면 true 반환        
        if(Files.exists(target, new LinkOption[]{})){
            return true;
        } else {
            return false;
        }
	}
}
