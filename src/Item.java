import java.util.Vector;
import java.util.Iterator;

public class Item {
	private int n; // 아이템 개수 -> 확률에 사용됨
	
	public Item() { n = 1; }

	public int GetN() { return n; }
	public void SetN(int n) { this.n = n; }
	
	/* 함정 */
	synchronized public void HideWord(Vector<WLabel> word) {
		Iterator<WLabel> it = word.iterator();
		WLabel wb;		
		while (it.hasNext()) {
			wb = it.next();
			wb.setVisible(false);
		}
		
		HideWordThread hwth = new HideWordThread(word);
		hwth.start();
	}
	
	class HideWordThread extends Thread {
		Iterator<WLabel> it;
		WLabel wb;
		Vector<WLabel> word;
		
		HideWordThread(Vector<WLabel> word) {
			this.word = word;
		}
		
		public void run() {
			try { sleep(5000); }
			catch(InterruptedException e) { ; }
			
			it = word.iterator();
			while (it.hasNext()) {
				wb = it.next();
				wb.setVisible(true);
			}
		}
	};
}