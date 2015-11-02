package com.tdt.kioskapp.ui;

import com.tdt.kioskapp.service.BaseService;
import net.lingala.zip4j.exception.ZipException;
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
        BaseService baseService = context.getBean(BaseService.class);
        //baseService.readManifest("");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
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

        startBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                KioskUI.this.getContentPane().removeAll();
                KioskUI.this.add(playerCanvas);
                KioskUI.this.revalidate();
                KioskUI.this.repaint();
                mediaPlayer.playMedia("");
            }
        });
    }
}
