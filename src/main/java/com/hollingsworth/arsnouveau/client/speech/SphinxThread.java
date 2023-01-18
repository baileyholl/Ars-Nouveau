package com.hollingsworth.arsnouveau.client.speech;

import com.hollingsworth.arsnouveau.ArsNouveau;
import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.SpeechResult;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sound.sampled.*;
import java.io.IOException;
import java.util.Arrays;

// Credit to SpeechToSpell https://github.com/Agent59/SpeechToSpell/blob/master/src/main/java/net/agent59/stp/speech/Sphinx4SpeechThread.java
public class SphinxThread implements Runnable{
    public static final Logger LOGGER = LogManager.getLogger(ArsNouveau.MODID + "_SPEECH");
    // other values for audio format won't work (https://cmusphinx.github.io/wiki/tutorialsphinx4/#streamspeechrecognizer)
    private final AudioFormat FORMAT = new AudioFormat(16000.0f, 16, 1, true, false);
    private final TargetDataLine mic = AudioSystem.getTargetDataLine(FORMAT);
    private final AudioInputStream inputStream = new AudioInputStream(mic);
    private CustomSpeechRecognizer recognizer;
    private volatile boolean listeningState = false; // used to check if the speech thread has reached a point, where it can be stopped
    private Player user = null;
    private static SphinxThread instance = null;

    private SphinxThread() throws LineUnavailableException {
    }

    public static SphinxThread getInstance() {
        if (instance == null) {
            try {
                instance = new SphinxThread();
            } catch (LineUnavailableException e) {
                throw new RuntimeException(e);
            }
        }
        return instance;
    }

    @Override
    public void run() {
        LOGGER.info("SPEECH THREAD STARTING");
        try {
            Configuration configuration = new Configuration();
            configuration.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
            configuration.setDictionaryPath("resource:/test/4793.dic");
            configuration.setLanguageModelPath("resource:/test/4793.lm");
            configuration.setUseGrammar(false);
            recognizer = new CustomSpeechRecognizer(configuration);
            mic.open();

            // This fixes the accumulating audio issue on some Linux systems
            mic.start();
            mic.stop();
            mic.flush();

            recognizer.startRecognition(inputStream);
            listeningState = true;

            SpeechResult speechResult;
            while ((speechResult = recognizer.getResult()) != null) {
                String voice_command = speechResult.getHypothesis();

                // voice_command is upperCase, so it has to be converted to every Word starting with upperCase
                // and the rest to lowercase
                String[] strings = voice_command.split(" ");
                String spellString = "";

                for (String string : strings) {

                    string = string.charAt(0) + string.substring(1).toLowerCase() + " ";
                    spellString = spellString.concat(string);
                }
                spellString = spellString.trim();

                user.sendSystemMessage(Component.literal(spellString));
//
//                //create the packet for the spell to send to the server
//                PacketByteBuf buf = PacketByteBufs.create();
//                buf.writeString(spellString);
//                //send the packaged spell to the server
//                ClientPlayNetworking.send(new Identifier(Main.MOD_ID, "spell"), buf);
            }

        } catch (LineUnavailableException | IOException e) {
            LOGGER.info("EXCEPTION " + Arrays.toString(e.getStackTrace()));
            throw new RuntimeException(e);
        } catch (NullPointerException e) {
            LOGGER.info("THE FOLLOWING EXCEPTION WAS CAUSED WHILE STOPPING THE SPEECH THREAD" +
                    " AND WAS PROBABLY INTENDED:\n\t" + Arrays.toString(e.getStackTrace()));
        }
        LOGGER.info("SPEECH THREAD ENDING");
    }

    public void end() {
        try {
            while (!listeningState) {
                Thread.onSpinWait();
            }
            inputStream.close();
            mic.stop();
            mic.flush();
            recognizer.cancelRecognition();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void pauseRecognition() {
        mic.stop();
    }

    public void resumeRecognition(Player player) {
        user = player;
        mic.flush();
        mic.start();
    }
}
