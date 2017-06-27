import java.awt.*;
import java.awt.event.*;
import java.awt.im.InputContext; // �ʱ� �Է¾�� �ѱ�
import java.io.*;
import java.net.URL;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;

@SuppressWarnings("serial")
public class GamePlayScreen extends JFrame {
	final private int width = 1080;
	final private int height = 720;
	final private int ENTER = 10;
	final private int ESC = 27;
	final private int SPACE = 32;

	static private enum Menu {
		PAUSE, GAME_OVER
	};

	boolean run_flag;
	boolean pause_flag;
	private Vector<WLabel> word; // ����ȭ�鿡 ������ �ܾ�

	// �÷��� �ð� ���� (���� �ð��� �ٸ���!)
	private long start_time;
	private long current_time;
	private int hit_cnt; // �� ���� ��
	private int err_cnt; // ��Ÿ��

	// ȭ���� ȣ���ϱ� ���� ����
	private JFrame pfv;
	private JPanel pcenter, psouth;
	private JTextField ptxtf;
	private JButton ppause;

	boolean correct_flag; // ���� ���� Ȯ�� �÷���
	boolean effect_flag; // ���� run �÷���
	
	public GamePlayScreen(GamePlayer p, GamePlay game, JFrame fv) {
		final Font f1i = new Font("����", Font.ITALIC, 14);
		final Font f1b = new Font("����", Font.BOLD, 14);
		final Font f2b = new Font("����", Font.BOLD, 16);
		final Font f4b = new Font("����", Font.BOLD, 30);

		run_flag = true;
		pause_flag = true; // ó������ �Ͻ����� ���� (Ready)
		correct_flag = true;
		effect_flag = false;
		pfv = fv;

		setTitle("ACIDRAIN");
		setResizable(false);
		setSize(width, height);
		setLocationRelativeTo(null);
		setVisible(true);
		setLayout(null);
		setVisible(true);

		// �ݱ� �ɼ�
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				fv.setVisible(true);
			}
		});

		class CenterPanel extends JPanel { // �ܾ �������� ȭ��
			public CenterPanel() {
				setLayout(null);
				setOpaque(false);
				setBorder(new BevelBorder(BevelBorder.LOWERED));

				/* �����ð� ������ */
				class TimerThread extends Thread {
					private JLabel remain_time;
					private int flick_flag; // ���� ��¦��

					public TimerThread() {
						flick_flag = 0;

						this.remain_time = new JLabel(String.format("%02d:%02d", (int) game.GetRemainTime(),
								(int) (game.GetRemainTime() * 100 % 100)));
						add(this.remain_time);
						this.remain_time.setFont(f4b);
						this.remain_time.setBounds(width / 2 - 95, 10, 110, 32);
					}

					public void run() {
						while (run_flag) {
							if (!pause_flag) {
								game.SetRemainTime(game.GetRemainTime() - 0.01);
								remain_time.setText(String.format("%02d:%02d", (int) game.GetRemainTime(),
										(int) (game.GetRemainTime() * 100 % 100)));

								// 5�� ���� ������ �ð� ��¦�̴� ȿ��
								if (game.GetRemainTime() < 10) {
									if (flick_flag > 40)
										this.remain_time.setForeground(Color.RED);
									else if (flick_flag > 20)
										this.remain_time.setForeground(Color.BLACK);
									flick_flag = (flick_flag + 1) % 60;
								} else
									this.remain_time.setForeground(Color.BLACK);

								if (game.GetRemainTime() <= 0) {// ���� �ð� 0�����̸�
																// ����Ŭ����
									game.SetRemainTime(0);
									remain_time.setText("00:00");

									game.SetStage(game.GetStage() + 1);
									game.InitGame(game.GetAcidity());

									while (!word.isEmpty()) {
										JLabel jlb = word.remove(0);
										game.RetWord(jlb.getText()); // game��ü��
																		// word��
																		// �ܾ� ����
										jlb.setVisible(false);
									}

									StageClear(p, game, pcenter); // ����Ŭ���� ��ȭ����
																	// ���� -->
																	// Ready()
																	// --> ���ӽ���
								}
							}

							try {
								sleep(10);
							} catch (InterruptedException e) {
								return;
							}
						}
					}
				}

				/* �ܾ� ������ */
				class WordThread extends Thread {
					final private int word_width = 90;
					final private int word_height = 22;

					public WordThread() {
						word = new Vector<WLabel>();
					}

					public void run() {
						while (run_flag) {
							if (!pause_flag) {
								int random_int = (int) (Math.random() * game.GetWordFreq());
								if (word.isEmpty())
									random_int = 0;

								switch (random_int) { // 1/n Ȯ���� ���� Math.random
														// 0~1 ���� ������ ��ȯ
								case 0:
									try { // �ܾ� ����: �ܾ��, ��Ʈ, ��ġ&ũ�� ����. word,
											// ����ȭ�鿡 �߰�
										int x = (int) (Math.random() * (width - 224) + 2);
										int y = 0;
										WLabel new_word;

										// �������� �������� ���� �ܾ�(red) ����
										switch ((int) (Math.random() * game.GetItemFreq())) {
										case 0:
											new_word = new WLabel(game.GetWord(),
													new ImageIcon(getClass().getResource("images/box/wordbox_red.jpg")), SwingConstants.CENTER,
													true);
											break;
										default:
											new_word = new WLabel(game.GetWord(),
													new ImageIcon(getClass().getResource("images/box/wordbox_blue.jpg")), SwingConstants.CENTER,
													false);
										}

										new_word.setHorizontalTextPosition(SwingConstants.CENTER);
										new_word.setVerticalTextPosition(SwingConstants.CENTER);
										new_word.setFont(f1b);
										new_word.setBounds(x, y, word_width, word_height);
										word.add(new_word);
										add(new_word);
										break;
									} catch (ArrayIndexOutOfBoundsException e) {
										;
									} // ���� �ܾ ������ �����Ѵ�.
								default:
									;
								}

								try { // ���� �� ��� �����͸� ���鼭 y��ǥ ����
									Iterator<WLabel> it = word.iterator(); // iterator
									while (it.hasNext()) {
										WLabel jlb = it.next();
										jlb.setLocation(jlb.getLocation().x, jlb.getLocation().y + word_height); // y��ǥ����

										if (jlb.getLocation().y >= height - 220) { // �ܾ�Ҹ�
											game.SetAcidity(game.GetAcidity() + 10);
											game.RetWord(jlb.getText()); // game��ü��
																			// word��
																			// �ܾ�
																			// ����
											word.remove(jlb);
										}
									}
								} catch (ConcurrentModificationException e) {
									;
								}
							}

							try {
								sleep(game.GetAvgDropSpeed());
							} catch (InterruptedException e) {
								return;
							}
						}
					}
				}

				// �����ð� ������ ����
				TimerThread tth = new TimerThread();
				tth.start();

				// �ܾ� ������ ����
				WordThread wth = new WordThread();
				wth.start();
			}

			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(new ImageIcon(getClass().getResource("images/bg/sea.jpg")).getImage(), 0, 0, getWidth(), getHeight(), this);
			}
		}

		class SouthPanel extends JPanel {
			public SouthPanel() {
				setLayout(null);

				class GamePlayerInfoPanel extends JPanel {
					public GamePlayerInfoPanel() {
						setLayout(null);

						class ScoreThread extends Thread {
							private JLabel stage_lab;
							private JLabel typing_speed_lab;
							private JLabel accuracy_lab;
							private JLabel p_score;
							private JLabel p_max_score;

							public ScoreThread() {
								stage_lab = new JLabel(game.GetStage() + " �ܰ�");
								add(stage_lab);
								stage_lab.setFont(f1i);
								stage_lab.setBounds(120, 10, 180, 22);

								current_time = System.currentTimeMillis();
								typing_speed_lab = new JLabel(
										"����Ÿ�� " + (hit_cnt * 60000 / (current_time - start_time)) + " ��/��");
								add(typing_speed_lab);
								typing_speed_lab.setFont(f1i);
								typing_speed_lab.setBounds(120, 32, 180, 22);

								int accuracy = (hit_cnt > 0) ? (hit_cnt - err_cnt) * 100 / hit_cnt : 0;
								accuracy_lab = new JLabel("��Ȯ�� " + accuracy + " %");
								add(accuracy_lab);
								accuracy_lab.setFont(f1i);
								accuracy_lab.setBounds(120, 54, 180, 22);

								p_score = new JLabel("�������� " + p.GetScore() + " ��");
								add(p_score);
								p_score.setFont(f1i);
								p_score.setBounds(120, 76, 180, 22);

								p_max_score = new JLabel("�ְ����� " + p.GetMaxScore() + " ��");
								add(p_max_score);
								p_max_score.setFont(f1i);
								p_max_score.setBounds(120, 98, 180, 22);
							}

							public void run() {
								while (run_flag) {
									if (!pause_flag) {
										stage_lab.setText(game.GetStage() + " �ܰ�");

										current_time = System.currentTimeMillis();
										typing_speed_lab.setText(
												"����Ÿ�� " + (hit_cnt * 60000 / (current_time - start_time)) + " ��/��");

										int accuracy = (hit_cnt > 0) ? (hit_cnt - err_cnt) * 100 / hit_cnt : 0;
										accuracy_lab.setText("��Ȯ�� " + accuracy + " %");

										p_score.setText("�������� " + p.GetScore() + " ��");
										p_max_score.setText("�ְ����� " + p.GetMaxScore() + " ��");
									}

									try {
										sleep(333);
									} catch (InterruptedException e) {
										return;
									}
								}
							}
						}

						// GamePlayer ���� �г�
						TitledBorder tb = new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), p.GetID() + " ��");
						tb.setTitleFont(f1i);
						setBorder(tb);

						// GamePlayer �̹���
						ImageIcon img = new ImageIcon(getClass().getResource("images/character/MushMom.png"));
						JLabel p_img = new JLabel() {
							public void paintComponent(Graphics g) {
								super.paintComponent(g);
								g.drawImage(img.getImage(), 0, 0, this.getWidth(), this.getHeight(), this);
							}
						};
						add(p_img);
						p_img.setBounds(10, 20, 100, 100);

						// ��������, Ÿ��, GamePlayer score, max score
						ScoreThread sth = new ScoreThread();
						sth.start();
					}
				}

				class AcidBarThread extends Thread {
					private double acidity;
					private JLabel acid_lab;
					private JProgressBar acid_bar;

					public AcidBarThread() {
						acidity = game.GetAcidity();

						// ����� �꼺�� ��
						acid_lab = new JLabel(String.format("%3d %%", (int) acidity));
						add(acid_lab);
						acid_lab.setBounds(339, 115, 60, 15);
						acid_lab.setFont(f2b);

						// �꼺�� �����
						acid_bar = new JProgressBar(JProgressBar.VERTICAL, 0, 100);
						add(acid_bar);
						acid_bar.setValue((int) acidity);
						acid_bar.setBounds(340, 10, 45, 100);
					}

					public void run() {
						while (run_flag) {
							if (!pause_flag) {
								game.SetAcidity(game.GetAcidity() + game.GetAciditySpeed());
								if (game.GetAcidity() <= 0)
									game.SetAcidity(0);

								if (acidity != game.GetAcidity()) {
									acidity = game.GetAcidity();

									// �꼺�� ����� �� ����
									if (acidity <= acid_bar.getMaximum()) {
										acid_bar.setValue((int) acidity);
										acid_lab.setText(String.format("%3d %%", (int) acidity));
									} else {
										acid_bar.setValue(acid_bar.getMaximum());
										acid_lab.setText("100 %");
									}

									// �꼺�� ����� �� ����
									if (acidity >= 60) {
										acid_bar.setForeground(Color.RED);
										acid_lab.setForeground(Color.RED);
									} else if (acidity >= 40 && acidity < 60) {
										acid_bar.setForeground(Color.YELLOW);
										acid_lab.setForeground(Color.DARK_GRAY);
									} else {
										acid_bar.setForeground(Color.GREEN);
										acid_lab.setForeground(Color.DARK_GRAY);
									}
								}

								// �꼺�� 100 �̻��̸� �й�
								if (game.GetAcidity() >= 100.0)
									Menu(p, game, Menu.GAME_OVER); // ���ӿ��� ��ȭ����
																	// ����
							}

							try {
								sleep(333);
							} catch (InterruptedException e) {
								return;
							}
						}
					}
				}

				// �÷��̾� ����
				GamePlayerInfoPanel p_info = new GamePlayerInfoPanel();
				add(p_info);
				p_info.setBounds(20, 5, 300, 130);

				// �꼺�� ����� ������ ����
				AcidBarThread acid_th = new AcidBarThread();
				acid_th.start();

				// �ؽ�Ʈ�Է�â
				JTextField txtf = new JTextField(15);
				ptxtf = txtf;
				add(txtf);
				txtf.setBounds(width / 2 - 90, 50, 180, 30);
				txtf.addFocusListener(new FocusAdapter() {
					public void focusGained(FocusEvent e) {
						InputContext default_lang = getInputContext();
						Character.Subset[] han_set = { Character.UnicodeBlock.HANGUL_SYLLABLES };
						default_lang.setCharacterSubsets(han_set);
					}
				});
				txtf.addKeyListener(new KeyAdapter() {
					public void keyPressed(KeyEvent e) {
						if (pause_flag) // �Ͻ����� �߿��� �ܾ� �Է� �Ұ�
							return;

						/* �ܾ� �Է� */
						int KeyCode = e.getKeyCode();
						if (KeyCode == ESC) {
							Menu(p, game, Menu.PAUSE);
							return;
						}

						if (KeyCode != SPACE && KeyCode != ENTER)
							return;

						// �Ͻ����� �� ��Ŀ�� �Ұ�
						if (pause_flag)
							txtf.setFocusable(false);
						else
							txtf.setFocusable(true);
						
						String str = txtf.getText().trim();
						hit_cnt += str.length();
						Iterator<WLabel> it = word.iterator();

						// �̽��� ����
						if (str.equals("^0^vv")) {
							game.SetStage(9);
							game.SetRemainTime(1);
							
							correct_flag = true;
							effect_flag = true;
							
							txtf.setText("");
							
							return;
						}
						
						while (it.hasNext()) {
							WLabel jlb = it.next();
							if (str.equals(jlb.getText())) {
								// �ܾ� ����: game��ü�� word�� �ܾ� ����
								correct_flag = true;
								effect_flag = true;
								game.RetWord(jlb.getText());
								jlb.setVisible(false);
								word.remove(jlb);

								// ������ �ߵ�
								if (jlb.hasItem())
									new Item().HideWord(word);

								// Ŭ���� �ð� & �꼺�� ����
								game.SetRemainTime(game.GetRemainTime() - 2);
								if (game.GetRemainTime() <= 0)
									game.SetRemainTime(0);
								game.SetAcidity(game.GetAcidity() - 2.5);
								if (game.GetAcidity() <= 0)
									game.SetAcidity(0);

								// ���� ���
								p.SetScore(p.GetScore() + 10);
								if (p.GetScore() > p.GetMaxScore())
									p.SetMaxScore(p.GetScore());

								txtf.setText("");
								return;
							}
						}
						correct_flag = false;
						effect_flag = true;
						err_cnt += str.length(); // ��Ÿ�� ���
						txtf.setText("");
					}
				});

				// �޴� ��ư
				JButton pause = new JButton("�޴� (ESC)");
				ppause = pause;
				add(pause);
				pause.setBounds(width - 220, 100, 95, 30);
				pause.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if (!pause_flag)
							Menu(p, game, Menu.PAUSE);
					}
				});
				pause.addKeyListener(new KeyAdapter() {
					public void keyPressed(KeyEvent e) {
						int KeyCode = e.getKeyCode();
						if (KeyCode == ESC && !pause_flag) {
							Menu(p, game, Menu.PAUSE);
							return;
						}
					}
				});
			}
		}

		JPanel bg = new JPanel() {
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(new ImageIcon(getClass().getResource("images/bg/bg_wood.jpg")).getImage(), 0, 0, getWidth(), getHeight(), this);
				setOpaque(false);
			}
		};

		bg.setLayout(null);
		bg.setBounds(0, 0, getWidth(), getHeight());
		setContentPane(bg);
		
		// ȿ����
		new PlayEffectSound().start();
		
		// ����ȭ�� ���
		pcenter = new CenterPanel();
		bg.add(pcenter);
		pcenter.setBounds((getWidth() - 950) / 2, 30, 950, 480);

		psouth = new SouthPanel();
		bg.add(psouth);
		psouth.setBounds((getWidth() - 980) / 2, 525, 980, 145);

		// �غ� !!
		Ready(p, game, pcenter);
	}

	public void Ready(GamePlayer p, GamePlay game, Container c) {
		class ReadyPanel extends JPanel {
			private ImageIcon bg_outer = new ImageIcon(getClass().getResource("images/menu/menu_wood.png"));
			private ImageIcon bg_inner = new ImageIcon(getClass().getResource("images/menu/menu_waterdrop.gif"));
			private ImageIcon start_img_clicked = new ImageIcon(getClass().getResource("images/menu/menu_start_clicked.png"));
			private ImageIcon start_img = new ImageIcon(getClass().getResource("images/menu/menu_start.png"));
			private ImageIcon start_img_entered = new ImageIcon(getClass().getResource("images/menu/menu_start_entered.png"));
			final private int lab_width = 160;
			final private int lab_height = 35;

			ReadyPanel() {
				setLayout(null);
				setOpaque(false);

				// n �ܰ�
				StageLab stg_lab = new StageLab(game.GetStage());
				add(stg_lab);
				stg_lab.setBounds(25, 10, 200, 40);

				// ���� ����
				TextPan txt_pan = new TextPan();
				add(txt_pan);
				txt_pan.setBounds(20, 60, 210, 120);

				// ���� ��ư
				StartButton start_lab = new StartButton(start_img);
				add(start_lab);
				start_lab.setBounds(58, 195, 125, 37);

				ptxtf.setFocusable(false);
				ppause.setFocusable(false);
				start_lab.setFocusable(true);
				start_lab.requestFocusInWindow();

				start_lab.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						setVisible(false);
						pause_flag = false;
						ptxtf.setFocusable(true);
						ptxtf.setEnabled(true);
						ptxtf.requestFocus();
						ppause.setFocusable(true);

						start_time = System.currentTimeMillis();
						hit_cnt = 0;
						err_cnt = 0;
					}
				});

				start_lab.addMouseListener(new MouseAdapter() {
					public void mousePressed(MouseEvent e) {
						start_lab.setImageIcon(start_img_clicked);
					}

					public void mouseReleased(MouseEvent e) {
						start_lab.setImageIcon(start_img);
					}

					public void mouseEntered(MouseEvent e) {
						start_lab.setImageIcon(start_img_entered);
					}

					public void mouseExited(MouseEvent e) {
						start_lab.setImageIcon(start_img);
					}
				});
			}

			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(bg_outer.getImage(), 0, 0, getWidth(), getHeight(), this);
			}

			class StageLab extends JLabel {
				StageLab(int stage) {
					setLayout(null);
					setText(stage + " �ܰ�");
					setForeground(Color.WHITE);
					setFont(new Font("�޸�����ü", Font.BOLD, 30));
					setHorizontalAlignment(SwingConstants.CENTER);
				}
			}

			class TextPan extends JPanel {
				TextPan() {
					setLayout(null);
					setOpaque(false);
					setBorder(new BevelBorder(BevelBorder.LOWERED));

					TextLab speed_lab = new TextLab("�ܾ��ϰ��ӵ�: " + game.GetAvgDropSpeed() + " ms");
					TextLab word_freq_lab = new TextLab("�ܾ������: " + 100 / game.GetWordFreq() + " %");
					TextLab item_freq_lab = new TextLab("�����ۻ�����: " + 100 / game.GetItemFreq() + " %");
					add(speed_lab);
					speed_lab.setBounds(40, 10, lab_width, lab_height);
					add(word_freq_lab);
					word_freq_lab.setBounds(40, 10 + lab_height, lab_width, lab_height);
					add(item_freq_lab);
					item_freq_lab.setBounds(40, 10 + lab_height * 2, lab_width, lab_height);
				}

				class TextLab extends JLabel {
					Font f = new Font("�޸�����ü", Font.BOLD, 16);

					TextLab(String text) {
						super(text);
						setFont(f);
						setForeground(Color.BLACK);
					}
				}

				public void paintComponent(Graphics g) {
					super.paintComponent(g);
					g.drawImage(bg_inner.getImage(), 0, 0, getWidth(), getHeight(), this);
				}
			}

			class StartButton extends JButton { // �ƹ�Ű�� �����ּ���
				private ImageIcon start_img;

				StartButton(ImageIcon start_img) {
					this.start_img = start_img;
					setBorderPainted(false);
					setContentAreaFilled(false);
					setFocusPainted(false);

					addFocusListener(new FocusAdapter() {
						public void focusLost(FocusEvent e) {
							requestFocusInWindow();
						}
					});
				}

				void setImageIcon(ImageIcon start_img) {
					this.start_img = start_img;
					repaint();
				}

				public void paintComponent(Graphics g) {
					super.paintComponent(g);
					g.drawImage(start_img.getImage(), 0, 0, getWidth(), getHeight(), this);
				}
			}
		}

		pause_flag = true;
		ReadyPanel rp = new ReadyPanel();
		c.add(rp);
		rp.setBounds((c.getWidth() - 250) / 2, 180, 250, 240);
	}

	public void StageClear(GamePlayer p, GamePlay game, Container c) { // ��������
																		// Ŭ����
																		// ��ȭ����
		class StageClearPanel extends JPanel {
			private ImageIcon bg_outer = new ImageIcon(getClass().getResource("images/menu/menu_oak.png"));
			private ImageIcon bg_inner = new ImageIcon(getClass().getResource("images/menu/menu_recycle_paper.png"));
			private ImageIcon stg_clr_img = new ImageIcon(getClass().getResource("images/menu/stage_clear.png"));
			private ImageIcon next_img_clicked = new ImageIcon(getClass().getResource("images/menu/menu_next_clicked.png"));
			private ImageIcon next_img = new ImageIcon(getClass().getResource("images/menu/menu_next.png"));
			private ImageIcon next_img_entered = new ImageIcon(getClass().getResource("images/menu/menu_next_entered.png"));
			final private int lab_width = 160;
			final private int lab_height = 35;

			StageClearPanel() {
				setLayout(null);
				setOpaque(false);

				// Stage Clear
				StgClrLab stg_clr_lab = new StgClrLab();
				add(stg_clr_lab);
				stg_clr_lab.setBounds(0, 0, 250, 70);

				// �÷��� ����
				TextPan txt_pan = new TextPan();
				add(txt_pan);
				txt_pan.setBounds(24, 70, 210, 120);

				// ���� ��ư
				NextButton next_lab = new NextButton(next_img);
				add(next_lab);
				next_lab.setBounds(66, 195, 125, 37);

				ptxtf.setFocusable(false);
				ppause.setFocusable(false);
				next_lab.setFocusable(true);
				next_lab.requestFocusInWindow();

				// ���� ��ư Ŭ�� �� Ready �Լ� ȣ��
				next_lab.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						setVisible(false);
						ptxtf.setFocusable(true);
						ptxtf.requestFocus();
						ppause.setFocusable(true);

						Ready(p, game, c);
					}
				});

				next_lab.addMouseListener(new MouseAdapter() {
					public void mousePressed(MouseEvent e) {
						next_lab.setImageIcon(next_img_clicked);
					}

					public void mouseReleased(MouseEvent e) {
						next_lab.setImageIcon(next_img);
					}

					public void mouseEntered(MouseEvent e) {
						next_lab.setImageIcon(next_img_entered);
					}

					public void mouseExited(MouseEvent e) {
						next_lab.setImageIcon(next_img);
					}
				});
			}

			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(bg_outer.getImage(), 0, 10, getWidth(), getHeight() - 10, this);
			}

			class StgClrLab extends JLabel {
				StgClrLab() {
					setLayout(null);
					setOpaque(false);
				}

				public void paintComponent(Graphics g) {
					super.paintComponent(g);
					g.drawImage(stg_clr_img.getImage(), 0, 0, getWidth(), getHeight(), this);
				}
			}

			class TextPan extends JPanel {
				TextPan() {
					setLayout(null);
					setOpaque(false);
					setBorder(new BevelBorder(BevelBorder.LOWERED));

					TextLab typing_speed_lab = new TextLab(
							"��� Ÿ��: " + (hit_cnt * 60000 / (current_time - start_time)) + " ��/��");
					add(typing_speed_lab);
					typing_speed_lab.setBounds(40, 10, lab_width, lab_height);

					int accuracy = (hit_cnt > 0) ? (hit_cnt - err_cnt) * 100 / hit_cnt : 0;
					TextLab accuracy_lab = new TextLab("��Ȯ��: " + accuracy + " %");
					add(accuracy_lab);
					accuracy_lab.setBounds(40, 10 + lab_height, lab_width, lab_height);
				}

				class TextLab extends JLabel {
					Font f = new Font("�޸�����ü", Font.BOLD, 16);

					TextLab(String text) {
						super(text);
						setFont(f);
						setForeground(Color.BLACK);
					}
				}

				public void paintComponent(Graphics g) {
					super.paintComponent(g);
					g.drawImage(bg_inner.getImage(), 0, 0, getWidth(), getHeight(), this);
				}
			}

			class NextButton extends JButton { // �ƹ�Ű�� �����ּ���
				private ImageIcon next_img;

				NextButton(ImageIcon next_img) {
					this.next_img = next_img;
					setBorderPainted(false);
					setContentAreaFilled(false);
					setFocusPainted(false);

					addFocusListener(new FocusAdapter() {
						public void focusLost(FocusEvent e) {
							requestFocusInWindow();
						}
					});
				}

				void setImageIcon(ImageIcon next_img) {
					this.next_img = next_img;
					repaint();
				}

				public void paintComponent(Graphics g) {
					super.paintComponent(g);
					g.drawImage(next_img.getImage(), 0, 0, getWidth(), getHeight(), this);
				}
			}
		}

		pause_flag = true;
		StageClearPanel scp = new StageClearPanel();
		c.add(scp);
		scp.setBounds((c.getWidth() - 250) / 2, 180, 250, 240);
	}

	public void Menu(GamePlayer p, GamePlay game, Menu type) {
		long pause_start_time = System.currentTimeMillis();

		class MenuPanel extends JPanel {
			final private URL continue_icon = getClass().getResource("images/icon/continue.png");
			final private URL retry_icon = getClass().getResource("images/icon/retry.png");
			final private URL back_icon = getClass().getResource("images/icon/back.png");
			final private URL exit_icon = getClass().getResource("images/icon/exit.png");
			JButton cntn, retry, back, exit;

			public MenuPanel(int width, int height) {
				class IconButton extends JButton {
					private ImageIcon img;

					IconButton(String caption, ImageIcon img) {
						super(caption);
						this.img = img;
					}

					public void paintComponent(Graphics g) {
						super.paintComponent(g);
						g.drawImage(img.getImage(), (getWidth() - 30) / 3, (getHeight() - 30) / 2, 30, 30, this);
					}
				}

				setLayout(null);

				cntn = new IconButton("              ����ϱ�", new ImageIcon(continue_icon));
				add(cntn);
				cntn.setBounds(0, 0, 200, 50);
				cntn.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						pause_flag = false;
						setVisible(false);

						// �Ͻ����� �ð��� ���Խ�Ű�� �ʴ´�.
						long pause_current_time = System.currentTimeMillis();
						start_time += (pause_current_time - pause_start_time);
					}
				});

				retry = new IconButton("              �ٽ��ϱ�", new ImageIcon(retry_icon));
				add(retry);
				retry.setBounds(0, 50, 200, 50);
				retry.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						p.SetScore(0);
						game.SetStage(1);
						game.InitGame(0);

						while (!word.isEmpty()) {
							JLabel jlb = word.remove(0);
							game.RetWord(jlb.getText()); // game��ü�� word�� �ܾ� ����
							jlb.setVisible(false);
						}

						Ready(p, game, pcenter);
						setVisible(false);
					}
				});

				back = new IconButton("              ���۸޴�", new ImageIcon(back_icon));
				add(back);
				back.setBounds(0, 100, 200, 50);
				back.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						run_flag = false;
						pfv.setVisible(true);
						dispose();
					}
				});

				exit = new IconButton("              �����ϱ�", new ImageIcon(exit_icon));
				add(exit);
				exit.setBounds(0, 150, 200, 50);
				exit.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						System.exit(0);
					}
				});
			}
		}

		pause_flag = true;

		MenuPanel mp = new MenuPanel(width, height);
		add(mp);
		mp.setBounds((width - 200) / 2, (height - 200) / 2, 200, 200);

		mp.retry.setEnabled(true);
		mp.back.setEnabled(true);
		mp.exit.setEnabled(true);

		switch (type) {
		case PAUSE:
			mp.cntn.setEnabled(true);
			break;
		case GAME_OVER:
			// ��� ����
			try {
				game.Save(p);
			} catch (IOException e) {
				System.err.println(e + " userinfo.bin ������ �߸� �Ǿ����ϴ�. \"bin\\data\\userinfo.bin\"�� �����ϰ� �ٽ� ��������ּ���. ");
				System.err.println("�÷��̾� ����� �������� ���Ͽ����ϴ�.");
			}

			mp.cntn.setEnabled(false);
			break;
		default:
			;
		}
	}
	
	class PlayEffectSound extends Thread {
		private URL correct_dir;
		private URL wrong_dir;
		
		PlayEffectSound() {
			correct_dir = getClass().getResource("sound/correct.wav");
			wrong_dir = getClass().getResource("sound/wrong.wav");
		}
		
		public void run() {
			while(run_flag) {
				if (effect_flag) {
					if (correct_flag)
						new PlaySound(correct_dir).PlayEffect();
					else
						new PlaySound(wrong_dir).PlayEffect();
					effect_flag = false;
				}
				
				try { sleep(100); }
				catch(InterruptedException e) { return; }
			}
		}
	}
}