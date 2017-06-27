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
	public String word_dir;		// public  설정
	final private String userinfo_dir = "userinfo.bin";
	
	private BufferedReader fin;
	private Vector<String> word;
	private double remain_time; // 남은시간(s)                     default: 60.00초
	private int word_freq; // 단어 생성 속도 (단어 하나 생성될 때 기존 단어 내려간 횟수)   default: 3 
	private int avg_drop_speed; // 단어 내려오는 평균속도              default: 20 (1픽셀 / 0.02초)
	private double acidity; // 산성도(%)                                default: 0.0%
	private double acidity_speed;
	private int item_freq;
	private int stage;
	private int word_flag;
	
	public GamePlay(int word_flag) {		// ver_flag 넘겨받는다.
		this.word_flag = word_flag;
		
		switch(this.word_flag) {
		case 0: word_dir = "word.txt";	break;
		case 1: word_dir = "Spring.txt"; break;
		case 2: word_dir = "Winter.txt"; break;
		default: word_dir = "word.txt";
		}
		
		stage = 1; // 스테이지1 부터 시작
		InitGame(0);
	}
	
	public void InitGame(double acidity) {
		// 파일입출력으로 단어 불러오기
		fin = null;
		word = new Vector<String>();
		
		try {
			fin = new BufferedReader(new FileReader(word_dir));
			
			String str = null;
			while ((str = fin.readLine()) != null)
				word.add(str);

		} catch (IOException e) { ; }

		// 게임 설정
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
	
	// 게임화면에 단어가 생성되면 word에서 삭제, 게임화면에서 단어가 사라지면 word에 다시 추가
	public String GetWord() { return word.remove((int)(Math.random() * word.size())); }
	public void RetWord(String str) { word.add(str); }
	
	public void Save(GamePlayer p) throws IOException { 
		HashMap<String, Integer> userinfo = Load();
		
		if (userinfo.containsKey(p.GetID()))
			userinfo.remove(p.GetID());			// 이전 데이터는 삭제
		userinfo.put(p.GetID(), p.GetMaxScore()); // 해시맵에 new 데이터 추가
		
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
		
		try { // 입력 스트림 생성 & 해시맵 로딩
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

	public void Load(GamePlayer p) throws IOException { // Load() 매개변수 오버로딩
		// 게임 시작 시 게임화면에 표시할 최고기록점수를 받아온다.
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
