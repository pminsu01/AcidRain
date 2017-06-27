import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;

public class SelectWordSet extends JDialog { // 화면 구성
	private static final long serialVersionUID = 1L;
	private ImageIcon normal_icon = new ImageIcon(getClass().getResource("images/menu/normal.jpeg"));
	private ImageIcon spring_icon = new ImageIcon(getClass().getResource("images/menu/spring.png"));
	private ImageIcon winter_icon = new ImageIcon(getClass().getResource("images/menu/winter.jpg"));

	JPanel pcenter;
	JButton conf;
	
	private Vector<WordButton> wb_set;
	private int word_flag;

	public SelectWordSet(StartFrame fv, JButton change_word) {
		fv.setEnabled(false);
		setTitle("단어선택");
		setResizable(false);
		setSize(312, 160);
		setLocationRelativeTo(fv);
		setVisible(true);
		setLayout(null);
		setAlwaysOnTop(true);
		
		// 닫기 옵션
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) { fv.setEnabled(true); }
		});
		
		pcenter = new CenterPanel();
		add(pcenter);
		pcenter.setBounds(0, 0, getWidth(), 100);
		
		conf = new JButton("확인");
		add(conf);
		conf.setBounds((getWidth() - 60) / 2, 105, 60, 22);
		conf.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Confirm(fv, change_word);
			}
		});
	}

	void Confirm(StartFrame fv, JButton change_word) {
		fv.SetWordFlag(word_flag);
		ImageIcon img = normal_icon;
		switch(word_flag) {
		case 0: img = normal_icon; break;
		case 1: img = spring_icon; break;
		case 2: img = winter_icon; break;
		default:;
		}
		
		fv.wb_img = img;
		change_word.repaint();
		fv.setEnabled(true);
		dispose();
	}
	
	@SuppressWarnings("serial")
	class CenterPanel extends JPanel {
		CenterPanel() {
			setLayout(null);
			setOpaque(false);

			wb_set = new Vector<WordButton>();

			WordButton normal = new WordButton(normal_icon);
			add(normal);
			normal.setLocation(0, 0);
			normal.setBorder(new CompoundBorder(new LineBorder(Color.RED, 3), new BevelBorder(BevelBorder.LOWERED)));
			word_flag = 0;

			WordButton spring = new WordButton(spring_icon);
			add(spring);
			spring.setLocation(103, 0);

			WordButton winter = new WordButton(winter_icon);
			add(winter);
			winter.setLocation(206, 0);

			// 토글버튼을 벡터에 추가
			wb_set.add(normal);
			wb_set.add(spring);
			wb_set.add(winter);
		}
	}
	
	class WordButton extends JButton { // normal button
		private static final long serialVersionUID = 1L;
		private ImageIcon img;
		private Iterator<WordButton> it;
		
		public WordButton(ImageIcon img) {
			this.img = img;
			setSize(100, 100);
			
			WordButton wb = this;
			addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					wb.setBorder(new BevelBorder(BevelBorder.LOWERED));
				}
				public void mouseReleased(MouseEvent e) {
					wb.setBorder(new CompoundBorder(new LineBorder(Color.RED, 3), new BevelBorder(BevelBorder.LOWERED)));
					word_flag = wb_set.indexOf(wb);
					
					it = wb_set.iterator();
					WordButton pwb;
					while (it.hasNext()) {
						pwb = it.next();
						if (wb_set.indexOf(pwb) != word_flag)
						pwb.setBorder(null);
					}
				}
				public void mouseEntered(MouseEvent e) {
					if (wb_set.indexOf(wb) == word_flag) // isSelected()
						wb.setBorder(new CompoundBorder(new LineBorder(Color.RED, 3), new BevelBorder(BevelBorder.LOWERED)));
					else
						wb.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
				}
				public void mouseExited(MouseEvent e) {
					if (wb_set.indexOf(wb) == word_flag) // isSelected()
						wb.setBorder(new CompoundBorder(new LineBorder(Color.RED, 3), new BevelBorder(BevelBorder.LOWERED)));
					else
						wb.setBorder(null);
				}
			});
		}

		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			if (img == null)
				img = new ImageIcon("images/menu/no_btn.jpg");
			g.drawImage(img.getImage(), 0, 0, 100, 100, this);
		}
	}
}

