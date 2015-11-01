package com.tdt.kioskapp;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.tdt.kioskapp.config.AppConfig;
import com.tdt.kioskapp.ui.KioskUI;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

import java.awt.*;

public class Application {

    public static void main(String[] args) {

        final ApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);
        String vlcHome = "";
        NativeLibrary.addSearchPath(
                RuntimeUtil.getLibVlcLibraryName(), vlcHome
        );
        Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {

                new KioskUI(ctx);
            }
        });
    }
}
