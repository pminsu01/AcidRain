import javax.swing.ImageIcon;
import javax.swing.JLabel;

@SuppressWarnings("serial")
public class WLabel extends JLabel {
	private boolean item;
	
	public WLabel(String word, ImageIcon imageIcon, int center, boolean item) {
		super(word, imageIcon, center); // JLabel 생성자 실행
		this.item = item;
	}
	
	public boolean hasItem() { return item; }
}