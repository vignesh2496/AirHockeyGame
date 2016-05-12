package org.dyn4j;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;

public class form extends JFrame
{

    JLabel display;
    JTextField ip;
    JLabel jLabel1;
    JLabel jLabel2;
    JLabel jLabel3;
    JLabel jLabel4;
    JLabel jLabel5;
    
    JTextField name;
    JButton play;
    JTextField time;
    ImageIcon img = new ImageIcon(this.getClass().getResource("airhockey.jpg"));
  
    public form()
    {
        initComponents();
    }

    private void initComponents()
    {

        name = new JTextField();
        ip = new JTextField();
        time = new JTextField();
        jLabel1 = new JLabel();
        jLabel2 = new JLabel();
        jLabel3 = new JLabel();
        jLabel4 = new JLabel();
        jLabel5 = new JLabel();
        play = new JButton();
        display = new JLabel();

        setDefaultCloseOperation(EXIT_ON_CLOSE);

  
        jLabel1.setFont(new Font("Courier New", Font.BOLD, 15));
        jLabel1.setForeground(Color.YELLOW);
        jLabel1.setText("Player Name:");
        jLabel2.setFont(new Font("Courier New", Font.BOLD, 15));
        jLabel2.setForeground(Color.YELLOW);
        jLabel2.setText("Connect to (IP Address):");
        jLabel3.setFont(new Font("Courier New", Font.BOLD, 15));
        jLabel3.setForeground(Color.YELLOW);
        jLabel3.setText("Game Duration:");
        jLabel4.setIcon(img);
        jLabel4.setBounds(0, 0, 890, 510);
        
        
        jLabel5.setFont(new Font("Courier New", Font.BOLD, 35));
        jLabel5.setText("AIR HOCKEY 2K16");
        jLabel5.setBounds(265, 20, 415, 40);
        jLabel5.setForeground(Color.YELLOW);
        getContentPane().add(jLabel5);

        play.setText("PLAY");
        play.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                playActionPerformed(evt);
            }
        });

        //Layout
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(404, 404, 404)
                .addComponent(play)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGap(333, 333, 333)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel1, GroupLayout.PREFERRED_SIZE, 147, GroupLayout.PREFERRED_SIZE)
                    .addComponent(name)
                    .addComponent(ip)
                    .addComponent(time)
                    .addComponent(jLabel2, GroupLayout.PREFERRED_SIZE, 205, GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, GroupLayout.DEFAULT_SIZE, 217, Short.MAX_VALUE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 138, Short.MAX_VALUE)
                .addComponent(display, GroupLayout.PREFERRED_SIZE, 184, GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21))
        );
        
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(83, 83, 83)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(name, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(ip, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(display))
                .addGap(34, 34, 34)
                .addComponent(jLabel3)
                .addGap(30, 30, 30)
                .addComponent(time, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(56, 56, 56)
                .addComponent(play)
                .addContainerGap(95, Short.MAX_VALUE))
        );

        getContentPane().add(jLabel4);

        pack();
    }

    //Display Form
    private void playActionPerformed(java.awt.event.ActionEvent evt) 
    {
          double Time = Double.parseDouble(this.time.getText());
          String Name = this.name.getText();
          String IP = this.ip.getText();
          
          //Pass inputs to game and start game 
          AirHockey simulation = new AirHockey(Name, IP, Time);
          simulation.run();           
    }
    
    
    public static void main(String args[]) 
    {
    	form Form = new form();
        Form.setVisible(true);
    }
}
