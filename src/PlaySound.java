import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class PlaySound {
	private URL audioFile;
    private Clip clip;
    private AudioInputStream ais;
    
    public PlaySound(URL file) {
    	this.audioFile = file;
    }
    
    public void PlayEffect() {
    	try {
	    	ais = AudioSystem.getAudioInputStream(audioFile);
			clip = AudioSystem.getClip();
			clip.stop();
			clip.open(ais);
			clip.start();
	    } catch (Exception e) { ; }
    }
    
    public void PlayBGM() {
    	try {
	    	ais = AudioSystem.getAudioInputStream(audioFile);
			clip = AudioSystem.getClip();
			clip.open(ais);
			clip.start();
	    } catch (Exception e) { ; }
    }
    
    public void PauseBGM() {
    	clip.stop();
    }
    
    Boolean IsPlaying() {
    	if (clip == null)
    		return false;
    	return clip.isActive();
    }
}