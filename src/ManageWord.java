import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

@SuppressWarnings("serial")
public class ManageWord extends JFrame {
	private String word_dir;
	private Vector<String> word;
	private JScrollPane r_sp, l_sp;
	private JList<String> r_list, l_list;
	private DefaultListModel<String> r_model, l_model;
	private JTextField txtf;
	private JButton conf, add, remove, save, close;
	private JLabel add_lab, remove_lab;
	
	public ManageWord(int word_flag, JFrame fv) {
		switch(word_flag) {
		case 0: word_dir = "word.txt";
				break;
		case 1: word_dir = "Spring.txt";
				break;
		case 2: word_dir = "Winter.txt";
				break;
		default:;
		}
		
		setTitle("ACIDRAIN: 단어 추가/삭제");
		setResizable(false);
		setSize(400, 400);
		setLocationRelativeTo(null);
		setLayout(null);
		setVisible(true);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				fv.setVisible(true);
			}
		});
		
		// 왼쪽 단어 리스트
		l_model = new DefaultListModel<String>();
		l_list = new JList<String>(l_model);
		l_list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		l_list.setSelectedIndex(0);
		l_list.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent e) {
				if (l_list.isSelectionEmpty())
					add.setEnabled(false);
				else
					add.setEnabled(true);
				remove.setEnabled(false);
			}
		});
		l_sp = new JScrollPane(l_list);
		add(l_sp);
		l_sp.setBounds(10, 10, 140, 270);
		
		// 단어 추가 입력
		txtf = new JTextField(15);
		add(txtf);
		txtf.setBounds(10, 282, 90, 23);
		txtf.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String a_str = txtf.getText();
				l_model.addElement(a_str);
				txtf.setText("");
			}
		});
		txtf.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if (txtf.getText().length() <= 0)
					conf.setEnabled(false);
				else
					conf.setEnabled(true);
			}
		});
		
		conf = new JButton("↑");
		add(conf);
		conf.setBounds(100, 282, 50, 22);
		conf.setEnabled(false);
		conf.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String a_str = txtf.getText();
				l_model.addElement(a_str);
			}
		});

		// 오른쪽 단어 리스트 (word.txt)
		r_model = new DefaultListModel<String>();
		word = Load();
		Iterator<String> it = word.iterator();
		while (it.hasNext()) {
			String str = it.next();
			r_model.addElement(str);
		}
		r_list = new JList<String>(r_model);
		r_list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		r_list.setSelectedIndex(0);
		r_list.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent e) {
				if (r_list.isSelectionEmpty())
					remove.setEnabled(false);
				else
					remove.setEnabled(true);
				add.setEnabled(false);
			}
		});
		r_sp = new JScrollPane(r_list);
		add(r_sp);
		r_sp.setBounds(230, 10, 140, 300);
		
		// 추가 버튼
		add = new JButton("추가");
		add(add);
		add.setBounds(160, 90, 60, 22);
		add.setEnabled(false);
		add.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					int index = l_list.getSelectedIndex();
					r_model.addElement(l_model.remove(index));
				} catch (ArrayIndexOutOfBoundsException e1) { ; }
			}
		});
		
		add_lab = new JLabel("→");
		add(add_lab);
		add_lab.setBounds(180, 120, 60, 22);
		
		remove_lab = new JLabel("←");
		add(remove_lab);
		remove_lab.setBounds(180, 170, 60, 22);
		
		// 제거 버튼
		remove = new JButton("제거");
		add(remove);
		remove.setBounds(160, 200, 60, 22);
		remove.setEnabled(false);
		remove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					int index = r_list.getSelectedIndex();
					l_model.addElement(r_model.remove(index));
				} catch (ArrayIndexOutOfBoundsException e1) { ; }
			}
		});
		
		save = new JButton("저장");
		add(save);
		save.setBounds(220, 320, 60, 25);
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Save(r_model);
				fv.setVisible(true);
				dispose();
			}
		});
		
		close = new JButton("끝내기");
		add(close);
		close.setBounds(280, 320, 80, 25);
		close.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fv.setVisible(true);
				dispose();
			}
		});
	}
	
	Vector<String> Load() {
		// 파일입출력으로 단어 불러오기
		BufferedReader fin = null;
		word = new Vector<String>();
		
		try {
			fin = new BufferedReader(new FileReader(word_dir));
			
			String str = null;
			while ((str = fin.readLine()) != null)
				word.add(str);

			fin.close();
		} catch (IOException e) {;}
		
		return word;
	}
	
	void Save(DefaultListModel<String> r_model) {
		BufferedWriter fout = null;
		String str;
		
		try {
			fout = new BufferedWriter(new FileWriter(word_dir));
			for (int i = 0; i < r_model.size(); i++) {
				str = r_model.getElementAt(i);
				fout.write(str + "\r\n");
			}
			fout.close();
		} catch (IOException e) {
			new ErrMsgBox(this, -1, e.getMessage());
		}
	}
}