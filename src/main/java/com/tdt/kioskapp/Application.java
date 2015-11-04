package com.tdt.kioskapp;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.tdt.kioskapp.config.AppConfig;
import com.tdt.kioskapp.ui.KioskUI;
import org.apache.log4j.Logger;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

import java.awt.*;

@SpringBootApplication
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.tdt.kioskapp.repository")
@ComponentScan(basePackages = {"com.tdt.kioskapp.config", "com.tdt.kioskapp.service"})
public class Application {

    public static void main(String[] args) {

        final ApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);
        String vlcHome = System.getenv("VLC_HOME");
        NativeLibrary.addSearchPath(
                RuntimeUtil.getLibVlcLibraryName(), vlcHome
        );
        Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {

                try {
                    new KioskUI(ctx);
                } catch (Exception e) {

                    Logger logger = Logger.getLogger(KioskUI.class);
                    logger.error(e.getStackTrace());
                }
            }
        });
    }
}
