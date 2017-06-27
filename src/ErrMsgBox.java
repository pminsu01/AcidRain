import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

@SuppressWarnings("serial")
public class ErrMsgBox extends JDialog {
	private ImageIcon stop_img = new ImageIcon(getClass().getResource("images/icon/stop.png"));
	private JLabel err_img;
	private JButton conf;
	private int height;
	
	public ErrMsgBox(JFrame frame, int err_type, String errtxt) {
		super(frame, "에러메시지");
		frame.setEnabled(false);
		setLayout(null);
		setVisible(true);
		setLocationRelativeTo(frame);
		setAlwaysOnTop(true);
		setResizable(false);
		height = 100;
		setSize(300, height);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				frame.setVisible(true);
			}
		});
		
		err_img = new JLabel() {
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(stop_img.getImage(), 0, 0, getWidth(), getHeight(), this);
			}
		};
		add(err_img);
		err_img.setBounds(10, 15, 20, 20);
		
		String sub_errtxt;
		while (!errtxt.isEmpty()) {
			height += 20;
			int div_length = (errtxt.length() < 23) ? errtxt.length() : 23;
			sub_errtxt = errtxt.substring(0, div_length);
			errtxt = errtxt.substring(div_length);
			
			JLabel err_lab = new JLabel(sub_errtxt);
			add(err_lab);
			err_lab.setBounds(42, height - 112, getWidth(), 30);
		}
		setSize(300, height);
		
		conf = new JButton("확인");
		add(conf);
		conf.setBounds((getWidth() - 60) / 2, height - 75, 60, 30);
		conf.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				switch(err_type) {
				case -1: System.exit(1);
				default:;
				}
				
				frame.setEnabled(true);
				dispose();
			}
		});
	}
}