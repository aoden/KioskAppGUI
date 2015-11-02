package com.tdt.kioskapp.ui;

import com.tdt.kioskapp.dto.SlideDTO;
import com.tdt.kioskapp.service.BaseService;
import net.lingala.zip4j.exception.ZipException;
import org.apache.log4j.Logger;
import org.json.simple.parser.ParseException;
import org.springframework.context.ApplicationContext;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.videosurface.CanvasVideoSurface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Map;

public class KioskUI extends JFrame {

    protected MediaPlayerFactory mediaPlayerFactory = new MediaPlayerFactory();
    protected EmbeddedMediaPlayer mediaPlayer = mediaPlayerFactory.newEmbeddedMediaPlayer();
    protected ApplicationContext context;
    protected Canvas playerCanvas = new Canvas();
    protected CanvasVideoSurface videoSurface = mediaPlayerFactory.newVideoSurface(playerCanvas);
    protected JPanel initPanel = new JPanel();
    protected JLabel initLabel = new JLabel("Please enter a key to unlock: ");
    protected JTextField textField = new JTextField();
    protected JButton startBtn = new JButton("START");


    public KioskUI(ApplicationContext context) throws ParseException, ZipException, IOException {

        this.context = context;
        initUI();
        final BaseService baseService = context.getBean(BaseService.class);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        startBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                startSlideShow(baseService);
                System.exit(0);
            }
        });
    }

    private void startSlideShow(BaseService baseService) {
        try {
            Map<String, SlideDTO> manifest = baseService.readManifest(initLabel.getText());
            for (Map.Entry<String, SlideDTO> entry : manifest.entrySet()) {

                SlideDTO currentMedia = entry.getValue();
                refresh(currentMedia.getLocation(), currentMedia.getDuration() * 1000);
            }

        } catch (Exception ex) {
            Logger logger = Logger.getLogger(KioskUI.class);
            logger.error(ex.getStackTrace());
        }
    }

    private void initUI() {

        mediaPlayer.setVideoSurface(videoSurface);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        initPanel.add(initLabel);
        initPanel.add(textField);
        initPanel.add(startBtn);
        add(initPanel);
        setUndecorated(true);
        setVisible(true);
    }

    private void refresh(String location, int duration) {

        play(BaseService.TEMP_DIR + "/" + location, duration);
        KioskUI.this.getContentPane().removeAll();
        KioskUI.this.add(playerCanvas);
        KioskUI.this.revalidate();
        KioskUI.this.repaint();
    }

    private void play(String location, int duration) {
        try {
            mediaPlayer.playMedia(location);
            Thread.sleep(duration);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
    }
}
