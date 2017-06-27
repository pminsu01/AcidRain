import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URL;

import javax.swing.*;
import javax.swing.border.*;

@SuppressWarnings("serial")
public class StartFrame extends JFrame {
	private JPanel pnorth, pcenter;
	private int word_flag;
	
	private Boolean run_flag;
	
	public JButton current_wb;
	public ImageIcon wb_img = new ImageIcon(getClass().getResource("images/menu/normal.jpeg"));
	
	public StartFrame() {
		// ������ ����,�ݱ� �ɼ�
		setTitle("ACIDRAIN");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// ������ ��ġ,ũ��
		setSize(720, 480);
		setLocationRelativeTo(null);

		// ���ȭ��
		JPanel bg = new JPanel() {
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(new ImageIcon(getClass().getResource("images/bg/bg_start.jpg")).getImage(), 0, 0, getWidth(), getHeight(), this);
				setOpaque(false);
			}
		};
		bg.setLayout(null);
		bg.setSize(getWidth(), getHeight());
		setContentPane(bg);
		
		// JPanel ���� > bg�� �߰� > ��ġ,ũ�⼳��
		pnorth = new NorthPanel();
		pcenter = new CenterPanel();
		
		bg.add(pnorth);
		bg.add(pcenter);
		
		pnorth.setBounds(0, 0, getWidth(), 210);
		pcenter.setBounds((getWidth() - 230) / 2, 210, 230, 220);
		
		run_flag = true;
		new PlayBGM().start();
	}
	
	class NorthPanel extends JPanel { // Title
		ImageIcon title = new ImageIcon(getClass().getResource("images/bg/title.png"));
		
		NorthPanel() {
			setLayout(null);
			setOpaque(false);
		}
		
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.drawImage(title.getImage(), 35, 0, 650, 218, this);
		}
	}
	
	class CenterPanel extends JPanel { // ���ӽ���, �ܾ�����, ��������
		private ImageIcon start_img = new ImageIcon(getClass().getResource("images/start/start.png"));
		private ImageIcon edit_img = new ImageIcon(getClass().getResource("images/start/edit.png"));
		private ImageIcon exit_img = new ImageIcon(getClass().getResource("images/start/exit.png"));
		
		CenterPanel() {
			setLayout(new GridLayout(2, 2, 30, 20));
			setOpaque(false);
			
			MenuButton start_btn = new MenuButton() {
				public void paintComponent(Graphics g) {
					super.paintComponent(g);
					g.drawImage(start_img.getImage(), 0, 0, getWidth(), getHeight(), this);
				}
			};
			add(start_btn);
			start_btn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) { Start(); }
			});
			
			MenuButton manage_word = new MenuButton() {
				public void paintComponent(Graphics g) {
					super.paintComponent(g);
					g.drawImage(edit_img.getImage(), 0, 0, getWidth(), getHeight(), this);
				}
			};
			add(manage_word);
			manage_word.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) { ManageWordTxt(); }
			});
			
			// �ܾ� ����
			MenuButton change_word = new MenuButton("�ܾ��") {
				public void paintComponent(Graphics g) {
					super.paintComponent(g);
					g.drawImage(wb_img.getImage(), 0, 22, getWidth(), getHeight() - 22, this);
				}
			};
			add(change_word);
			change_word.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) { SelectWordSet(change_word); }
			});
						
			// �����ϱ�
			MenuButton close = new MenuButton() {
				public void paintComponent(Graphics g) {
					super.paintComponent(g);
					g.drawImage(exit_img.getImage(), 0, 0, getWidth(), getHeight(), this);
				}
			};
			add(close);
			close.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) { System.exit(0); }
			});
		}
	}

	class MenuButton extends JButton {
		private static final long serialVersionUID = 1L;
		
		public MenuButton() {
			setSize(100, 100);
			setBorder(new BevelBorder(BevelBorder.RAISED));
			setBackground(Color.DARK_GRAY);
			
			MenuButton wb = this;
			addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					wb.setBorder(new BevelBorder(BevelBorder.LOWERED));
				}
				public void mouseReleased(MouseEvent e) {
					wb.setBorder(new CompoundBorder(new LineBorder(Color.RED, 2), new BevelBorder(BevelBorder.LOWERED)));
				}
				public void mouseEntered(MouseEvent e) {
					wb.setBorder(new CompoundBorder(new LineBorder(Color.RED, 2), new BevelBorder(BevelBorder.LOWERED)));
				}
				public void mouseExited(MouseEvent e) {
					setBorder(new BevelBorder(BevelBorder.RAISED));
				}
			});
		}
		public MenuButton(String caption) {
			this();
			setText(caption);
			setVerticalAlignment(SwingConstants.TOP);
			setBackground(Color.LIGHT_GRAY);
			setForeground(Color.DARK_GRAY);
		}
	}
	
	/* �� ��ư�� ���� �޼ҵ�  */
	// 1. �����ϱ�
	public void Start() {
		class ID extends JDialog {
			JLabel ID_lab;
			JTextField txtf;
			JButton conf;
			
			ID(JFrame fv) {
				super(fv, "������̸��� �Է����ּ���.");
				setSize(300, 90);
				
				setLayout(null);
				setResizable(false);
				setLocationRelativeTo(fv);
				fv.setEnabled(false);
				setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				addWindowListener(new WindowAdapter() {
					public void windowClosing(WindowEvent e) {
						fv.setEnabled(true);
						fv.setVisible(true);
					}
				});

				class StartAction implements ActionListener {
					public void actionPerformed(ActionEvent e) {
						if (txtf.getText().equals(""))
							return;
						
						String ID = txtf.getText().trim(); // ������ ���� �𸣴� ���� ���� trim() ���
						GamePlayer p = new GamePlayer(ID); // id �Ѱ��ش�.
						GamePlay game = new GamePlay(word_flag); // word_flag �Ѱ��ֱ�

						if (game.GetNumOfWord() < 10) {
							new ErrMsgBox(fv, 0, "�ܾƮ�� �ܾ�� ��� 10�� �̻� �̾�� �մϴ�.");
							return;
						}
						
						try { game.Load(p);	}
						catch (IOException e1) { ; }

						new GamePlayScreen(p, game, fv);
						fv.setEnabled(true);
						fv.setVisible(false);
					}
				}
				
				ID_lab = new JLabel("����� �̸�:");
				ID_lab.setBounds(15, 20, 80, 20);
				add(ID_lab);

				txtf = new JTextField(15);
				add(txtf);
				txtf.setBounds(95, 19, 132, 22);
				txtf.addActionListener(new StartAction());
				txtf.addKeyListener(new KeyAdapter() {
					public void keyReleased(KeyEvent e) {
						if (txtf.getText().length() <= 0)
							conf.setEnabled(false);
						else
							conf.setEnabled(true);
					}
				});

				conf = new JButton("Ȯ��");
				add(conf);
				conf.setBounds(228, 19, 60, 22);
				conf.setEnabled(false);
				conf.addActionListener(new StartAction());
			}
		}
		
		new ID(this).setVisible(true);
	}

	// 2. �ܾ�����
	void ManageWordTxt() {
		new ManageWord(word_flag, this);
		this.setVisible(false);
	}

	// 4. �ܾ� ���� ����
	void SelectWordSet(JButton change_word) {
		new SelectWordSet(this, change_word);
	}
	
	public void SetWordFlag(int word_flag) { this.word_flag = word_flag; }

	// x. ������� ������
	class PlayBGM extends Thread {
		private URL bgm_dir;
		private PlaySound bgm;
		
		PlayBGM() {
			bgm_dir = getClass().getResource("sound/BGM.wav");
			bgm = new PlaySound(bgm_dir);
		}
		
		public void run() {
			while(run_flag) {
				if (!bgm.IsPlaying())
					bgm.PlayBGM();
				
				try { sleep(100); }
				catch(InterruptedException e) { return; }
			}
		}
	}
	
	public static void main(String[] args) {
		new StartFrame().setVisible(true);
	}
}