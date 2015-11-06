package com.tdt.kioskapp.ui;

import com.tdt.kioskapp.dto.SlideDTO;
import com.tdt.kioskapp.service.BaseService;
import net.lingala.zip4j.exception.ZipException;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
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
public class KioskUI extends JFrame implements Runnable {

    public static final Dimension TEXT_SIZE = new Dimension(120, 25);
    // this var is meant to prevent the second download
    public static volatile int playCount = 0;
    protected static volatile boolean play = true;
    protected EmbeddedMediaPlayerComponent mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
    protected ApplicationContext context;
    protected JPanel initPanel = new JPanel();
    protected JLabel initLabel = new JLabel("Please enter a key to unlock: ");
    protected JPasswordField textField = new JPasswordField();
    protected JButton startBtn = new JButton("START");
    Logger logger = Logger.getLogger(KioskUI.class);
    private BaseService baseService;
    private KeyAdapter keyListener = new KeyAdapter() {
        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public void keyPressed(KeyEvent e) {

            System.out.println(e);
            if (e.isAltDown() && e.isControlDown()) {

                System.exit(0);
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }
    };

    public KioskUI(ApplicationContext context) throws ZipException, IOException {

        this.context = context;
        final BaseService baseService = context.getBean(BaseService.class);
        this.baseService = baseService;
        initUI();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        if (baseService.registered()) {

            new Thread(KioskUI.this).start();
            playCount++;
        } else {

            startBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    new Thread(KioskUI.this).start();
                    playCount++;
                }
            });
        }

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                mediaPlayerComponent.release(true);
            }
        });
    }

    private void startSlideShow(BaseService baseService) {
        try {
            Map<String, SlideDTO> manifest = !baseService.registered() ?
                    baseService.readManifest(textField.getText())
                    : baseService.readManifest();
            for (Map.Entry<String, SlideDTO> entry : manifest.entrySet()) {

                SlideDTO currentValue = entry.getValue();
                File currentFile = baseService.readAllFiles(BaseService.TEMP_DIR + "/" + currentValue.getLocation());
                if (currentFile != null) {

                    refresh(currentValue.getLocation() + "/" + currentFile.getName(), currentValue.getSeconds() * 1000);
                }
            }
            playCount++;

        } catch (Exception ex) {
            logger.error(ex.getStackTrace());
            ex.printStackTrace();
            System.exit(1);
        }
    }

    private void initUI() {

        setLayout(new BorderLayout());
        if (!baseService.registered()) {

            textField.setPreferredSize(TEXT_SIZE);
            initPanel.add(initLabel);
            initPanel.add(textField);
            initPanel.add(startBtn);
            add(initPanel, BorderLayout.CENTER);
        }
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setVisible(true);

        startBtn.setDefaultCapable(true);
        EmbeddedMediaPlayer mediaPlayer = mediaPlayerComponent.getMediaPlayer();
        mediaPlayer.setFullScreen(true);
        mediaPlayer.setEnableKeyInputHandling(true);
        mediaPlayer.setEnableMouseInputHandling(true);
        mediaPlayerComponent.getVideoSurface().addKeyListener(keyListener);
    }

    private void refresh(String location, int duration) {

        play(BaseService.TEMP_DIR + "/" + location, duration);
    }

    private void play(String location, int duration) {
        try {

            EmbeddedMediaPlayer mediaPlayer = mediaPlayerComponent.getMediaPlayer();
            mediaPlayer.startMedia(location);
            mediaPlayer.parseMedia();
            mediaPlayerComponent.getVideoSurface().requestFocusInWindow();
            if ("image/jpeg".equals(Files.probeContentType(Paths.get(location))) || location == null) {

                Thread.sleep(duration);
            } else {

                Thread.sleep(mediaPlayer.getLength());
            }
        } catch (Exception e1) {

            logger.error(e1.getStackTrace());
        }
    }

    @Override
    public void run() {

        while (play) {

            if (KioskUI.this.getContentPane() != mediaPlayerComponent) {
                KioskUI.this.setContentPane(mediaPlayerComponent);
            }
            KioskUI.this.revalidate();
            KioskUI.this.repaint();
            startSlideShow(baseService);
        }
    }
}
