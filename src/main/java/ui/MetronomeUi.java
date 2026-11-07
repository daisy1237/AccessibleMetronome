package ui;

import audio.MetronomeAudio;
import domain.*;
import service.*;

import javax.sound.sampled.LineUnavailableException;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.accessibility.*;

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
        configureAccessibility();
        configureKeyboardShortcuts();
        configureFocusIndicator();
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
        startStopButton.setFocusPainted(true);

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
        bpmLabel.getAccessibleContext().setAccessibleDescription("Current tempo: "+newBpm+" beats per minute.");
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

        startStopButton.getAccessibleContext().setAccessibleName(
                running ? "Stop metronome" : "Start metronome"
        );

        statusLabel.getAccessibleContext().setAccessibleDescription(
                running
                        ? "The metronome is currently running."
                        : "The metronome is currently stopped."
        );
    }

    private void updateBeatIndicator(Beat beat){
        beatPanel.removeAll();

        int beatsPerMeasure=settings.getBeatsPerMeasure();

        for(int i=1;i<=settings.getBeatsPerMeasure();i++){
            JPanel beatColumn=new JPanel();
            beatColumn.setLayout(new BoxLayout(beatColumn,BoxLayout.Y_AXIS));
            beatColumn.setOpaque(false);

            boolean active = beat!=null && beat.getBeatNumber()==i;

            JLabel arrow=new  JLabel(active?"▼" : " ");
            arrow.setFont(new Font("SansSerif", Font.BOLD, 15));
            arrow.setForeground(Color.BLACK);
            arrow.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel indicator=new JLabel("●");
            indicator.setFont(new Font("SansSerif", Font.BOLD, 26));
            indicator.setForeground(active
                    ? new Color(246, 20, 20)
                    : new Color(15, 188, 50, 255));
            indicator.setAlignmentX(Component.CENTER_ALIGNMENT);

            beatColumn.add(arrow);
            beatColumn.add(indicator);

            beatPanel.add(beatColumn);
        }

        beatPanel.revalidate();
        beatPanel.repaint();

        String description = beat==null ? "No active beat." : "Beat "+beat.getBeatNumber()+" of "+beatsPerMeasure+(beat.isAccented()?", accented.":".");

        beatPanel.getAccessibleContext().setAccessibleDescription(description);
    }

    private void configureAccessibility(){
        decreaseButton.getAccessibleContext().setAccessibleName("Decrease tempo");
        decreaseButton.getAccessibleContext().setAccessibleDescription("Decrease the tempo by one beat per minute.");

        increaseButton.getAccessibleContext().setAccessibleName("Increase tempo");
        increaseButton.getAccessibleContext().setAccessibleDescription("Increase the tempo by one beat per minute.");

        startStopButton.getAccessibleContext().setAccessibleName("Start metronome");
        startStopButton.getAccessibleContext().setAccessibleDescription("Start or stop the metronome playback.");

        timeSignatureCombo.getAccessibleContext().setAccessibleName("Time Signature");
        timeSignatureCombo.getAccessibleContext().setAccessibleDescription("Choose between two-four, three-four and four-four time signatures.");

        bpmLabel.getAccessibleContext().setAccessibleName("Current tempo");
        bpmLabel.getAccessibleContext().setAccessibleDescription("Current tempo: "+settings.getBpm()+" beats per minute.");

        statusLabel.getAccessibleContext().setAccessibleName("Metronome status");
        statusLabel.getAccessibleContext().setAccessibleDescription("The metronome is currently stopped.");
    }

    private void configureKeyboardShortcuts(){
        JRootPane root = getRootPane();

        InputMap inputMap = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = root.getActionMap();

        inputMap.put(KeyStroke.getKeyStroke("SPACE"),"toggleMetronome");
        actionMap.put("toggleMetronome", new AbstractAction(){
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                toggleMetronome();
            }
        });

        inputMap.put(KeyStroke.getKeyStroke("RIGHT"),"increaseTempo");
        actionMap.put("increaseTempo", new AbstractAction(){
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                changeBpm(1);
            }
        });

        inputMap.put(KeyStroke.getKeyStroke("LEFT"),"decreaseTempo");
        actionMap.put("decreaseTempo", new AbstractAction(){
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                changeBpm(-1);
            }
        });

        inputMap.put(KeyStroke.getKeyStroke("UP"),"previousTimeSignature");
        actionMap.put("previousTimeSignature", new AbstractAction(){
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                changeTimeSignature(-1);
            }
        });

        inputMap.put(KeyStroke.getKeyStroke("DOWN"),"nextTimeSignature");
        actionMap.put("nextTimeSignature", new AbstractAction(){
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                changeTimeSignature(1);
            }
        });
    }

    private void changeTimeSignature(int delta){
        int currentIndex=timeSignatureCombo.getSelectedIndex();
        int newIndex=currentIndex+delta;
        if(newIndex>=0 && newIndex<timeSignatureCombo.getItemCount()){
            timeSignatureCombo.setSelectedIndex(newIndex);
        }
    }

    private void configureFocusIndicator(){
        addFocusIndicator(decreaseButton);
        addFocusIndicator(increaseButton);
        addFocusIndicator(startStopButton);
        addFocusIndicator(timeSignatureCombo);
    }

    private void addFocusIndicator(JComponent component){
        Border originalBorder = component.getBorder();
        Border focusBorder = BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(4, 57, 227),3), originalBorder);

        component.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e){
                component.setBorder(focusBorder);
            }
            @Override
            public void focusLost(FocusEvent e){
                component.setBorder(originalBorder);
            }
        });
    }
}
