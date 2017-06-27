import java.io.*;
import java.util.*;

class GamePlayer {
	
	private String ID;
	private int score;
	private int max_score;

	public GamePlayer(String ID) {
		this.ID = ID;
		score = 0;
		max_score = 0;
	}

	public String GetID() { return ID; }
	public int GetScore() { return score; }
	public int GetMaxScore() { return max_score; }
	public void SetScore(int score) { this.score = score; }
	public void SetMaxScore(int max_score) { this.max_score = max_score; }
}

public class GamePlay {
	public String word_dir;		// public  ����
	final private String userinfo_dir = "userinfo.bin";
	
	private BufferedReader fin;
	private Vector<String> word;
	private double remain_time; // �����ð�(s)                     default: 60.00��
	private int word_freq; // �ܾ� ���� �ӵ� (�ܾ� �ϳ� ������ �� ���� �ܾ� ������ Ƚ��)   default: 3 
	private int avg_drop_speed; // �ܾ� �������� ��ռӵ�              default: 20 (1�ȼ� / 0.02��)
	private double acidity; // �꼺��(%)                                default: 0.0%
	private double acidity_speed;
	private int item_freq;
	private int stage;
	private int word_flag;
	
	public GamePlay(int word_flag) {		// ver_flag �Ѱܹ޴´�.
		this.word_flag = word_flag;
		
		switch(this.word_flag) {
		case 0: word_dir = "word.txt";	break;
		case 1: word_dir = "Spring.txt"; break;
		case 2: word_dir = "Winter.txt"; break;
		default: word_dir = "word.txt";
		}
		
		stage = 1; // ��������1 ���� ����
		InitGame(0);
	}
	
	public void InitGame(double acidity) {
		// ������������� �ܾ� �ҷ�����
		fin = null;
		word = new Vector<String>();
		
		try {
			fin = new BufferedReader(new FileReader(word_dir));
			
			String str = null;
			while ((str = fin.readLine()) != null)
				word.add(str);

		} catch (IOException e) { ; }

		// ���� ����
		remain_time = 90.00;
		this.acidity = acidity;
		
		switch(stage) {
		case 1:
			word_freq = 3;
			avg_drop_speed = 800;
			acidity_speed = 0.1;
			item_freq = 7;
			break;
		case 2:
			word_freq = 3;
			avg_drop_speed = 700;
			acidity_speed = 0.1;
			item_freq = 7;
			break;
		case 3:
			word_freq = 3;
			avg_drop_speed = 600;
			acidity_speed = 0.2;
			item_freq = 7;
			break;
		case 4:
			word_freq = 3;
			avg_drop_speed = 500;
			acidity_speed = 0.2;
			item_freq = 7;
			break;
		case 5:
			word_freq = 2;
			avg_drop_speed = 800;
			acidity_speed = 0.3;
			item_freq = 6;
			break;
		case 6:
			word_freq = 2;
			avg_drop_speed = 700;
			acidity_speed = 0.3;
			item_freq = 6;
			break;
		case 7:
			word_freq = 2;
			avg_drop_speed = 600;
			acidity_speed = 0.4;
			item_freq = 6;
			break;
		case 8:
			word_freq = 2;
			avg_drop_speed = 500;
			acidity_speed = 0.4;
			item_freq = 6;
			break;
		case 9:
			word_freq = 2;
			avg_drop_speed = 500;
			acidity_speed = 0.5;
			item_freq = 5;
			break;
		case 10:
			word_freq = 2;
			avg_drop_speed = 450;
			acidity_speed = 0.5;
			item_freq = 5;
			break;
		default:;
		}
	}
	
	// ����ȭ�鿡 �ܾ �����Ǹ� word���� ����, ����ȭ�鿡�� �ܾ ������� word�� �ٽ� �߰�
	public String GetWord() { return word.remove((int)(Math.random() * word.size())); }
	public void RetWord(String str) { word.add(str); }
	
	public void Save(GamePlayer p) throws IOException { 
		HashMap<String, Integer> userinfo = Load();
		
		if (userinfo.containsKey(p.GetID()))
			userinfo.remove(p.GetID());			// ���� �����ʹ� ����
		userinfo.put(p.GetID(), p.GetMaxScore()); // �ؽøʿ� new ������ �߰�
		
		File file = new File(userinfo_dir);
		
		FileOutputStream fout = new FileOutputStream(file);
		ObjectOutputStream oout = new ObjectOutputStream(fout);
		
		oout.writeObject(userinfo);
		
		oout.close();
		fout.close();
	}

	@SuppressWarnings("unchecked")
	public HashMap<String, Integer> Load() throws IOException {
		HashMap<String, Integer> userinfo = new HashMap<String, Integer>();
		
		File file = new File(userinfo_dir);
		
		try { // �Է� ��Ʈ�� ���� & �ؽø� �ε�
			FileInputStream fin = new FileInputStream(file);
			ObjectInputStream oin = new ObjectInputStream(fin);
			userinfo = (HashMap<String, Integer>)oin.readObject();
			
			oin.close();
			fin.close();
		}
		catch(FileNotFoundException e) {}
		catch(ClassNotFoundException e) {}
		
		return userinfo;
	}

	public void Load(GamePlayer p) throws IOException { // Load() �Ű����� �����ε�
		// ���� ���� �� ����ȭ�鿡 ǥ���� �ְ��������� �޾ƿ´�.
		HashMap<String, Integer> userinfo = Load();

		if (userinfo.containsKey(p.GetID())) {
			int max_score = userinfo.get(p.GetID());
			p.SetMaxScore(max_score);
		}
	}

	public int GetNumOfWord() { return word.size(); }
	public double GetRemainTime() { return remain_time; }
	public int GetWordFreq() { return word_freq; }
	public int GetAvgDropSpeed() { return avg_drop_speed; }
	public double GetAcidity() { return acidity; }
	public double GetAciditySpeed() { return acidity_speed; }
	public int GetItemFreq() { return item_freq; }
	public int GetStage() { return stage; }
	public int GetWordFlag() { return word_flag; }
	public void SetRemainTime(double remain_time) { this.remain_time = remain_time; }
	public void SetWordFreq(int word_freq) { this.word_freq = word_freq; } 
	public void SetAvgDropSpeed(int Drop_speed) { this.avg_drop_speed = Drop_speed; }
	public void SetAcidity(double acidity) { this.acidity = acidity; }
	public void SetAciditySpeed(double acidity_speed) { this.acidity_speed = acidity_speed; }
	public void SetItemFreq(int item_freq) { this.item_freq = item_freq; }
	public void SetStage(int stage) { this.stage = stage; }
	public void SetWordFlag(int word_flag) { this.word_flag = word_flag; } 
}
