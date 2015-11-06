package com.tdt.kioskapp;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.tdt.kioskapp.config.AppConfig;
import com.tdt.kioskapp.ui.KioskUI;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

import java.awt.*;

public class Application {

    public static void main(String[] args) {

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {

                try {
                    String vlcHome = System.getenv("VLC_HOME");
                    NativeLibrary.addSearchPath(
                            RuntimeUtil.getLibVlcLibraryName(), vlcHome
                    );
                    Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);
                    new KioskUI(new AnnotationConfigApplicationContext(AppConfig.class));
                } catch (Exception e) {

                    Logger logger = Logger.getLogger(KioskUI.class);
                    logger.error(e.getCause());
                }
            }
        });
    }
}
