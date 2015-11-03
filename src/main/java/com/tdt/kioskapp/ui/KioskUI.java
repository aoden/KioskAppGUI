package com.tdt.kioskapp.ui;

import com.tdt.kioskapp.dto.SlideDTO;
import com.tdt.kioskapp.service.BaseService;
import net.lingala.zip4j.exception.ZipException;
import org.apache.log4j.Logger;
import org.json.simple.parser.ParseException;
import org.springframework.context.ApplicationContext;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.videosurface.CanvasVideoSurface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

/**
 * Main UI of program
 *
 * @author aoden
 */
public class KioskUI extends JFrame {

    public static final Dimension TEXT_SIZE = new Dimension(120, 25);
    protected MediaPlayerFactory mediaPlayerFactory = new MediaPlayerFactory();
    protected EmbeddedMediaPlayerComponent mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
    protected ApplicationContext context;
    protected JPanel initPanel = new JPanel();
    protected JLabel initLabel = new JLabel("Please enter a key to unlock: ");
    protected JPasswordField textField = new JPasswordField();
    protected JButton startBtn = new JButton("START");

    protected static volatile boolean play = true;

    Logger logger = Logger.getLogger(KioskUI.class);
    private KeyListener keyListener;


    public KioskUI(ApplicationContext context) throws ParseException, ZipException, IOException {

        this.context = context;
        initUI();
        final BaseService baseService = context.getBean(BaseService.class);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        startBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                while (play) {

                    startSlideShow(baseService);
                }
            }
        });
    }

    private void startSlideShow(BaseService baseService) {
        try {
            Map<String, SlideDTO> manifest = baseService.readManifest(textField.getText());
            for (Map.Entry<String, SlideDTO> entry : manifest.entrySet()) {

                SlideDTO currentValue = entry.getValue();
                File currentFile = baseService.readAllFiles(BaseService.TEMP_DIR + "/" + currentValue.getLocation());
                if (currentFile != null) {

                    refresh(currentValue.getLocation() + "/" + currentFile.getName(), currentValue.getSeconds() * 1000);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex.getStackTrace());
        }
    }

    private void initUI() {

        setLayout(new BorderLayout());
        textField.setPreferredSize(TEXT_SIZE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        initPanel.add(initLabel);
        initPanel.add(textField);
        initPanel.add(startBtn);
        add(initPanel, BorderLayout.CENTER);
        setUndecorated(true);
        setVisible(true);
    }

    private void refresh(String location, int duration) {


        KioskUI.this.getContentPane().removeAll();
        KioskUI.this.add(mediaPlayerComponent);
        KioskUI.this.revalidate();
        KioskUI.this.repaint();
        keyListener = new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

                if (e.isControlDown() && e.isAltDown()) {

                    System.exit(0);
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        };
        mediaPlayerComponent.getVideoSurface().addKeyListener(keyListener);
        play(BaseService.TEMP_DIR + "/" + location, duration);
    }

    private void play(String location, int duration) {
        try {
            EmbeddedMediaPlayer mediaPlayer = mediaPlayerComponent.getMediaPlayer();
            mediaPlayer.attachVideoSurface();
            mediaPlayer.setFullScreen(true);
            mediaPlayer.setEnableKeyInputHandling(true);
            mediaPlayer.setEnableMouseInputHandling(false);
            mediaPlayer.playMedia(location);
            if ("image/jpeg".equals(Files.probeContentType(Paths.get(location))) || location == null) {

                Thread.sleep(duration);
            } else {

                mediaPlayer.parseMedia();
                Thread.sleep(mediaPlayer.getLength());

            }
        } catch (Exception e1) {

            logger.error(e1.getStackTrace());
        }
    }
}
