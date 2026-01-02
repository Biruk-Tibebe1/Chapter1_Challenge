package chapter2_challenge_applet;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BouncingTextApplet extends Applet implements Runnable {
    private Thread animator;
    private int x = 10;  // Starting x-position
    private String name = "Your Name Here";  // Replace with your name

    public void init() {
        setSize(400, 200);  // Applet size
        setBackground(Color.lightGray);  // Background color
    }

    public void start() {
        animator = new Thread(this);  // Create thread (this = Runnable)
        animator.start();  // Start animation
    }

    public void stop() {
        animator = null;  // Stop thread
    }

    public void run() {
        while (true) {  // Forever loop
            if (animator == null) return;  // Exit if stopped
            
            repaint();  // Request redraw
            
            x += 5;  // Move right (speed: adjust if wanted)
            if (x > getWidth() - 100) {  // Hit right edge? (approx text width)
                x = 0;  // Reset to left
            }
            
            try {
                Thread.sleep(100);  // Pause 100ms
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    public void paint(Graphics g) {
        g.drawString(name, x, 50);  // Draw name at (x, y=50)
    }
}