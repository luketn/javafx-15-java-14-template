package com.mycodefu.dashboard;

import com.mycodefu.dashboard.light.LightView;
import com.mycodefu.dashboard.tables.TablesView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import java.net.URL;
import java.time.LocalDate;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;

import static com.mycodefu.App.showModalView;

public class DashboardPresenter implements Initializable {
    @FXML
    Node root;

    @FXML
    Label message;

    @FXML
    Pane lightsBox;

    @Inject
    private LocalDate date;

    private String theEnd;

    private static Random random = new Random();
    private static AtomicInteger lightCounter = new AtomicInteger(0);

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        theEnd = rb.getString("theEnd");
        String dateLabel = rb.getString("date");
        message.setText(dateLabel + ": " + date + theEnd);

        root.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ESCAPE) {
                ((Stage) root.getScene().getWindow()).close();
            } else if (keyEvent.isControlDown()) {
                switch (keyEvent.getCode()) {
                    case NUMPAD1, DIGIT1 -> createLights(10);
                    case NUMPAD2, DIGIT2 -> createLights(20);
                    case NUMPAD3, DIGIT3 -> createLights(30);
                    case P -> lightsBox.getChildren().clear();
                }
            }
        });
        System.out.println("JavaFX initialize()");
    }

    @PostConstruct
    public void postConstruct() {
        System.out.println("Dashboard constructed.");
    }

    @PreDestroy
    public void preDestroy() {
        System.out.println("Dashboard about to be destroyed.");
    }


    public void createLights() {
        createLights(256);
    }

    public void createLights(int lightCount) {
        System.out.printf("Creating %d lights!\n", lightCount);

        for (int i = 0; i < lightCount; i++) {
            final int red = random.nextInt(255);
            final int green = random.nextInt(255);
            final int blue = random.nextInt(255);

            LightView view = new LightView(red, green, blue);
            view.getViewAsync(lightsBox.getChildren()::add);
            lightCounter.incrementAndGet();
        }
        if (lightCounter.get() > 2048) {
            Platform.runLater(() -> {
                int counterBefore = lightCounter.get();
                int remove = 0;
                if (counterBefore > 2048) {
                    remove = lightCounter.get() - 2048;
                    if (remove > 0) {
                        lightsBox.getChildren().remove(0, remove);
                        lightCounter.addAndGet(-remove);
                    }
                }

                System.out.printf("Removed %d. Count was %d, now %d.\n", remove, counterBefore, lightCounter.get());
            });
        }
    }

    public void launch() {
        TablesView tablesView = new TablesView();
        showModalView(tablesView);
        System.out.println("here");
    }
}
