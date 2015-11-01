package com.tdt.kioskapp.ui;

import lombok.Data;
import org.springframework.context.ApplicationContext;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.videosurface.CanvasVideoSurface;

import javax.swing.*;
import java.awt.*;

@Data
public class KioskUI extends JFrame {

    MediaPlayerFactory mediaPlayerFactory = new MediaPlayerFactory();
    EmbeddedMediaPlayer mediaPlayer = mediaPlayerFactory.newEmbeddedMediaPlayer();
    private ApplicationContext context;
    private Canvas playerCanvas = new Canvas();
    CanvasVideoSurface videoSurface = mediaPlayerFactory.newVideoSurface(playerCanvas);

    public KioskUI(ApplicationContext context) {

        this.context = context;
        add(playerCanvas);
        mediaPlayer.setVideoSurface(videoSurface);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setVisible(true);
        mediaPlayer.playMedia("");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
}
