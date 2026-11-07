package ui;

import audio.MetronomeAudio;
import domain.*;
import service.*;

import javax.sound.sampled.LineUnavailableException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MetronomeUi extends javax.swing.JFrame {
    private final MetronomeSettings settings;
    private final MetronomeAudio audio;
    private final MetronomeService service;

    private JLabel bpmLabel;
    private JLabel statusLabel;
    private JButton startStopButton;
    private JButton increaseButton;
    private JButton decreaseButton;
    private JComboBox<String> timeSignatureCombo;
    private JPanel beatPanel;

    private boolean running=false;

    public MetronomeUi() throws LineUnavailableException {
        settings = new MetronomeSettings();
        audio = new MetronomeAudio();
        service=new MetronomeService(settings,beat->{
            audio.playBeat(beat);
            SwingUtilities.invokeLater(() -> updateBeatIndicator(beat));
        });
        initializeFrame();
        initializeComponents();
        layoutComponents();
        registerListeners();
    }

    private void initializeFrame(){
        setTitle("Accessible Metronome");
        setSize(500,550);
        setMinimumSize(new Dimension(500,550));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                service.stop();
                audio.close();
                dispose();
            }
        });
    }

    private void initializeComponents(){
        bpmLabel = new JLabel(settings.getBpm()+" bpm", SwingConstants.CENTER);
        bpmLabel.setFont(new Font("SansSerif", Font.BOLD, 42));

        statusLabel=new JLabel("Metronome stopped", SwingConstants.CENTER);
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));

        decreaseButton=new JButton("-");
        increaseButton=new JButton("+");

        startStopButton =new JButton("Start");
        startStopButton.setFont(new Font("SansSerif", Font.BOLD, 20));

        timeSignatureCombo=new JComboBox<>(new String[]{"2/4","3/4","4/4"});
        timeSignatureCombo.setSelectedItem("4/4");

        beatPanel=new JPanel(new FlowLayout(FlowLayout.CENTER,12,5));
        updateBeatIndicator(null);
    }

    private void layoutComponents(){
        JPanel mainPanel=new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(30,30,30,30));

        JLabel titleLabel=new JLabel("METRONOME",SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        bpmLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel tempoPanel=new JPanel(new FlowLayout(FlowLayout.CENTER,20,5));
        tempoPanel.add(decreaseButton);
        tempoPanel.add(bpmLabel);
        tempoPanel.add(increaseButton);

        JPanel signaturePanel=new JPanel(new FlowLayout(FlowLayout.CENTER,10,5));
        signaturePanel.add(new JLabel("Time Signature:"));
        signaturePanel.add(timeSignatureCombo);

        JPanel controlPanel=new JPanel(new FlowLayout(FlowLayout.CENTER));
        controlPanel.add(startStopButton);

        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(35));
        mainPanel.add(tempoPanel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(signaturePanel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(beatPanel);
        mainPanel.add(Box.createVerticalStrut(25));
        mainPanel.add(controlPanel);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(statusLabel);

        add(mainPanel);
    }

    private void registerListeners(){
        decreaseButton.addActionListener(e -> changeBpm(-1));
        increaseButton.addActionListener(e -> changeBpm(1));

        timeSignatureCombo.addActionListener(e -> {
            int selected=timeSignatureCombo.getSelectedIndex()+2;
            settings.setBeatsPerMeasure(selected);
            updateBeatIndicator(null);
        });

        startStopButton.addActionListener(e -> toggleMetronome());
    }

    private void changeBpm(int delta){
        int newBpm=settings.getBpm()+delta;
        if(newBpm<MetronomeSettings.MIN_BPM ||  newBpm>MetronomeSettings.MAX_BPM){ return; }
        settings.setBpm(newBpm);
        bpmLabel.setText(newBpm+" bpm");
        if(running){
            service.stop();
            service.start();
        }
    }

    private void toggleMetronome(){
        if(running){
            service.stop();
            running=false;
            startStopButton.setText("Start");
            statusLabel.setText("Metronome stopped");
            updateBeatIndicator(null);
        } else {
            service.start();
            running=true;
            startStopButton.setText("Stop");
            statusLabel.setText("Metronome running");
        }
    }

    private void updateBeatIndicator(Beat beat){
        beatPanel.removeAll();

        for(int i=1;i<=settings.getBeatsPerMeasure();i++){
            JLabel indicator=new JLabel("●");
            indicator.setFont(new Font("SansSerif", Font.BOLD, 26));

            boolean active = beat!=null && beat.getBeatNumber()==i;

            indicator.setForeground(active?Color.GREEN:Color.RED);

            beatPanel.add(indicator);
        }

        beatPanel.revalidate();
        beatPanel.repaint();
    }

}
