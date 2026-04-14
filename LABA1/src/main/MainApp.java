package main;

import javax.swing.*;
import java.awt.*;
import ui.Lab1Frame;
import rgr.RGRStage1Frame;

public class MainApp {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("Системи моделювання", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        
        JLabel subtitleLabel = new JLabel("Виберіть модуль для запуску (Варіант 14)", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(subtitleLabel, BorderLayout.CENTER);

        String[] options = {"Лабораторна робота №1", "РГР (Етап 3)"};
        
        int choice = JOptionPane.showOptionDialog(null, panel, 
                "Головне меню | Примаченко В.", 
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, 
                null, options, options[0]);

        if (choice == 0) {
            EventQueue.invokeLater(() -> new Lab1Frame().setVisible(true));
        } else if (choice == 1) {
            EventQueue.invokeLater(() -> new RGRStage1Frame().setVisible(true));
        }
    }
}