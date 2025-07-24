package com.example;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class App extends Application {
    private ImageView cardView = new ImageView();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("ID Card & QR Generator");

        // UI Components
        TextField nameField = new TextField();
        nameField.setPromptText("Enter Name");

        TextField phoneField = new TextField();
        phoneField.setPromptText("Enter Phone Number");

        TextField emailField = new TextField();
        emailField.setPromptText("Enter Email");

        TextField statusField = new TextField();
        statusField.setPromptText("Enter Status");

        TextArea descriptionField = new TextArea();
        descriptionField.setPromptText("Enter Description");

        Button generateButton = new Button("Generate Card & QR Code");
        generateButton.setOnAction(e -> {
            String userData = "Name: " + nameField.getText() + "\n"
                    + "Phone: " + phoneField.getText() + "\n"
                    + "Email: " + emailField.getText() + "\n"
                    + "Status: " + statusField.getText() + "\n"
                    + "Description: " + descriptionField.getText();

            String cardPath = "user_card.png";
            String qrPath = "user_qr.png";

            try {
                generateCardWithQR(userData, cardPath, qrPath);
                cardView.setImage(new Image(new File(cardPath).toURI().toString()));
            } catch (WriterException | IOException ex) {
                ex.printStackTrace();
            }
        });

        cardView.setFitWidth(400);
        cardView.setFitHeight(300);

        // Layout
        VBox root = new VBox(10, nameField, phoneField, emailField, statusField, descriptionField, generateButton, cardView);
        root.setStyle("-fx-padding: 20; -fx-alignment: center;");

        Scene scene = new Scene(root, 450, 650);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void generateCardWithQR(String data, String cardPath, String qrPath) throws WriterException, IOException {
        int cardWidth = 500;
        int cardHeight = 300;

        // Create a blank ID card
        BufferedImage cardImage = new BufferedImage(cardWidth, cardHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = cardImage.createGraphics();

        // Background
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, cardWidth, cardHeight);

        // Border
        g2d.setColor(Color.BLACK);
        g2d.drawRect(10, 10, cardWidth-20, cardHeight-20);

        // Text settings
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));

        // Draw user details
        g2d.drawString("Name: " + data.split("\n")[0].split(": ")[1], 30, 50);
        g2d.drawString("Phone: " + data.split("\n")[1].split(": ")[1], 30, 80);
        g2d.drawString("Email: " + data.split("\n")[2].split(": ")[1], 30, 110);
        g2d.drawString("Status: " + data.split("\n")[3].split(": ")[1], 30, 140);
        g2d.drawString("Description:", 30, 170);

        // Multi-line description
        String[] descLines = data.split("\n")[4].split(": ")[1].split(" ");
        int y = 200;
        for (String word : descLines) {
            g2d.drawString(word,30, y);
            y += 20;
        }

        // Generate QR Code
        int qrSize = 200;
        BitMatrix matrix = new MultiFormatWriter().encode(data, BarcodeFormat.QR_CODE, qrSize, qrSize);
        Path qrPathFile = FileSystems.getDefault().getPath(qrPath);
        MatrixToImageWriter.writeToPath(matrix, "PNG", qrPathFile);

        // Load QR Code onto the card
        BufferedImage qrImage = ImageIO.read(new File(qrPath));
        g2d.drawImage(qrImage, cardWidth - qrSize - 20, cardHeight - qrSize - 20, null);

        // Save the final card
        g2d.dispose();
        ImageIO.write(cardImage, "PNG", new File(cardPath));
    }
}


