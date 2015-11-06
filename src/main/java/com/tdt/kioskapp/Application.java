package com.tdt.kioskapp;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.tdt.kioskapp.ui.KioskUI;
import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

import java.awt.*;

@EnableAutoConfiguration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.tdt.kioskapp.repository")
@ComponentScan(basePackages = "com.tdt.kioskapp")
@EntityScan(basePackages = "com.tdt.kioskapp.model")
@SpringBootApplication
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
                    new KioskUI(SpringApplication.run(Application.class));
                } catch (Exception e) {

                    Logger logger = Logger.getLogger(KioskUI.class);
                    logger.error(e.getCause());
                }
            }
        });
    }
}
