
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * @author Ryan, Amine, Sanaa
 * 
 * A class to handle playing the sound when a move is played.
 */
public class SoundPlayer {

    public boolean on; // whether or not sound is on

    public static final String capture = "Images_and_Sounds/capture.wav"; // path for the capture sound
    public static final String game_end = "Images_and_Sounds/game-end.wav"; // path for the game end sound
    public static final String game_start = "Images_and_Sounds/game-start.wav"; // path for the game start sound
    public static final String castle = "Images_and_Sounds/castle.wav"; // path for the castle sound
    public static final String check = "Images_and_Sounds/move-check.wav"; // path for the check sound
    public static final String move = "Images_and_Sounds/move-self.wav"; // path for the move sound
    public static final String promote = "Images_and_Sounds/promote.wav"; // path for the promote sound
    public static final String notify = "Images_and_Sounds/notify.wav"; // path for the notify sound

    /**
     * initialize the sound player and turn the sound on
     */
    public SoundPlayer(){
        on = true;
    }

    /**
     * turn off the sound
     */
    public void turnOff() { on = false; }
    /**
     * turn on the sound
     */
    public void turnOn() { on = true; }

    /**
     * play the capture sound
     */
    public void playCapture(){
        playSound(capture);
    }

    /**
     * play the game end sound
     */
    public void playGameEnd(){
        playSound(game_end);
    }

    /**
     * play the game start sound
     */
    public void playGameStart(){
        playSound(game_start);
    }

    /**
     * play the castle sound
     */
    public void playCastle(){
        playSound(castle);
    }

    /**
     * play the check sound
     */
    public void playCheck(){
        playSound(check);
    }

    /**
     * play the move sound
     */
    public void playMove(){
        playSound(move);
    }

    /**
     * play the promote sound
     */
    public void playPromote(){
        playSound(promote);
    }

    /**
     * play the notify sound
     */
    public void playNotify(){
        playSound(notify);
    }

    /**
     * @param audio - path of the sound file to be played
     * 
     * play the sound from the given audio file
     */
    public void playSound(String audio) {
        if(on){
            Thread thread = new Thread(new Runnable(){
                public void run(){
                    try {
                        //System.out.println("playing audio");
                        File f = new File(audio);
                        //System.out.println(f.getName());
                        AudioInputStream audioStream = AudioSystem.getAudioInputStream(f);
                        
                        Clip clip = AudioSystem.getClip();
                        clip.open(audioStream);
                        clip.start();
                    } catch (UnsupportedAudioFileException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (LineUnavailableException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        }
	}

}
