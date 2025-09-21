package Dragon.server;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.Desktop;
import java.net.URI;

public class menu extends JFrame {

    public menu() {
        initComponents();
    }

    private void initComponents() {
        // Cài đặt màu nền gradient
        setContentPane(new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                Color color1 = new Color(45, 45, 45);
                Color color2 = new Color(0, 0, 0);
                GradientPaint gp = new GradientPaint(0, 0, color1, getWidth(), getHeight(), color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        });

        // Đặt icon logo riêng
        setIconImage(Toolkit.getDefaultToolkit().getImage("path/to/your/logo.png")); // Thay đường dẫn logo của bạn

        JButton jButton1 = new JButton("Bảo Trì");
        JButton jButton2 = new JButton("Kick All");
        JButton webButton = new JButton("Open Website");
        JLabel titleLabel = new JLabel("Server Control Panel");

        // Tùy chỉnh giao diện
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);

        jButton1.setPreferredSize(new Dimension(90, 35));
        jButton2.setPreferredSize(new Dimension(90, 35));
        webButton.setPreferredSize(new Dimension(120, 35));

        // Tùy chỉnh màu sắc và hiệu ứng hover cho các nút
        customizeButton(jButton1);
        customizeButton(jButton2);
        customizeButton(webButton);

        jButton1.addActionListener(evt -> jButton1ActionPerformed());
        jButton2.addActionListener(evt -> jButton2ActionPerformed());
        webButton.addActionListener(e -> openWebsite());

        setLayout(new BorderLayout());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setOpaque(false);
        buttonPanel.add(jButton1);
        buttonPanel.add(jButton2);
        buttonPanel.add(webButton);

        add(titleLabel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 200);
        setLocationRelativeTo(null);  // Căn giữa màn hình
    }

    private void customizeButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        button.setBackground(new Color(60, 60, 60));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(80, 80, 80));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(60, 60, 60));
            }
        });
    }

    private void jButton1ActionPerformed() {
        // Code bảo trì
    }

    private void jButton2ActionPerformed() {
        // Code Kick All
    }

    private void openWebsite() {
        try {
            Desktop.getDesktop().browse(new URI("https://yourwebsite.com"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new menu().setVisible(true));
    }
}
