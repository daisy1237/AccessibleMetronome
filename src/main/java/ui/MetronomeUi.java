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
    private JLabel bpmUnitLabel;
    private JLabel statusLabel;
    private JButton startStopButton;
    private JButton increaseButton;
    private JButton decreaseButton;
    private JComboBox<String> timeSignatureCombo;
    private JComboBox<String> tempoPresetCombo;
    private JPanel beatPanel;
    private JButton muteButton;

    private boolean running=false;

    private static final String[] TIME_SIGNATURES = {"2/4","3/4","4/4","5/4","6/8","7/8","9/8","12/8"};
    private static final String[] TEMPO_PRESETS = {
            "Custom", "60", "80", "100", "120", "140", "160", "180"
    };

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
        setSize(500, 620);
        setMinimumSize(new Dimension(480, 580));
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
        bpmLabel = new JLabel(String.valueOf(settings.getBpm()), SwingConstants.CENTER);
        bpmLabel.setFont(Theme.BPM_FONT);
        bpmLabel.setForeground(Theme.TEXT);

        bpmUnitLabel=new JLabel("bpm", SwingConstants.CENTER);
        bpmUnitLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        bpmLabel.setForeground(Theme.TEXT_SECONDARY);

        statusLabel=new JLabel("Metronome stopped", SwingConstants.CENTER);
        statusLabel.setFont(Theme.BODY_FONT);
        statusLabel.setForeground(Theme.TEXT_SECONDARY);

        decreaseButton = new RoundedButton("−", Theme.CONTROL_BACKGROUND, Theme.PRIMARY);
        increaseButton = new RoundedButton("+", Theme.CONTROL_BACKGROUND, Theme.PRIMARY);

        startStopButton = new RoundedButton("Start", Theme.PRIMARY, Theme.BUTTON_TEXT);
        startStopButton.setFont(new Font("SansSerif", Font.BOLD, 18));
        startStopButton.setPreferredSize(new Dimension(170, 52));
        startStopButton.setFocusPainted(true);

        muteButton=new RoundedButton("", Theme.CONTROL_BACKGROUND, Theme.PRIMARY);
        muteButton.setIcon(new VolumeIcon(false,Theme.PRIMARY));
        muteButton.setPreferredSize(new Dimension(54, 52));
        muteButton.setToolTipText("Mute sound");

        timeSignatureCombo = new JComboBox<>(TIME_SIGNATURES);
        timeSignatureCombo.setSelectedItem("4/4");
        timeSignatureCombo.setFont(Theme.BODY_FONT);
        timeSignatureCombo.setBackground(Theme.CONTROL_BACKGROUND);
        timeSignatureCombo.setForeground(Theme.TEXT);
        timeSignatureCombo.setPreferredSize(new Dimension(110, 42));

        timeSignatureCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus
                );

                label.setFont(Theme.BODY_FONT);
                label.setOpaque(true);

                if (isSelected && index >= 0) {
                    label.setBackground(Theme.PRIMARY);
                    label.setForeground(Color.WHITE);
                } else {
                    label.setBackground(Theme.CONTROL_BACKGROUND);
                    label.setForeground(Theme.TEXT);
                }

                label.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
                return label;
            }
        });

        tempoPresetCombo = new JComboBox<>(TEMPO_PRESETS);
        tempoPresetCombo.setSelectedItem(
                String.valueOf(settings.getBpm())
        );
        tempoPresetCombo.setFont(Theme.BODY_FONT);
        tempoPresetCombo.setBackground(Theme.CONTROL_BACKGROUND);
        tempoPresetCombo.setForeground(Theme.TEXT);
        tempoPresetCombo.setPreferredSize(new Dimension(110, 42));
        tempoPresetCombo.setFocusable(true);
        tempoPresetCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus) {

                JLabel label = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus
                );

                if (isSelected && index >= 0) {
                    label.setBackground(Theme.PRIMARY);
                    label.setForeground(Color.WHITE);
                } else {
                    label.setBackground(Theme.CONTROL_BACKGROUND);
                    label.setForeground(Theme.TEXT);
                }

                label.setOpaque(true);
                label.setBorder(new EmptyBorder(8, 12, 8, 12));

                return label;
            }
        });

        beatPanel=new JPanel(new FlowLayout(FlowLayout.CENTER,12,5));
        updateBeatIndicator(null);
    }

    private void layoutComponents(){
        JPanel backgroundPanel=new JPanel(new GridLayout());
        backgroundPanel.setBackground(Theme.BACKGROUND);
        backgroundPanel.setBorder(new EmptyBorder(15,15,15,15));

        JPanel mainPanel=new RoundedPanel(30);
        mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));
        mainPanel.setBackground(Theme.SURFACE);
        mainPanel.setBorder(new EmptyBorder(25,25,25,25));

        JLabel titleLabel=new JLabel("METRONOME",SwingConstants.CENTER);
        titleLabel.setFont(Theme.TITLE_FONT);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setForeground(Theme.TEXT);

        bpmLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        bpmUnitLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel tempoPanel=new JPanel(new FlowLayout(FlowLayout.CENTER,20,5));
        tempoPanel.setBackground(Theme.SURFACE);

        JPanel bpmDisplayPanel=new JPanel();
        bpmDisplayPanel.setLayout(new BoxLayout(bpmDisplayPanel,BoxLayout.Y_AXIS));
        bpmDisplayPanel.setOpaque(false);
        bpmDisplayPanel.add(bpmLabel);
        bpmDisplayPanel.add(bpmUnitLabel);
        bpmDisplayPanel.setPreferredSize(new Dimension(130,95));

        tempoPanel.add(decreaseButton);
        tempoPanel.add(bpmDisplayPanel);
        tempoPanel.add(increaseButton);

        JPanel signaturePanel=new JPanel(new FlowLayout(FlowLayout.CENTER,10,5));
        signaturePanel.setBackground(Theme.SURFACE);
        JLabel signatureLabel = new JLabel("Time signature:");
        signatureLabel.setFont(Theme.BODY_FONT);
        signatureLabel.setForeground(Theme.TEXT_SECONDARY);
        signaturePanel.add(signatureLabel);
        signaturePanel.add(timeSignatureCombo);

        JPanel presetPanel=new JPanel(new FlowLayout(FlowLayout.CENTER,12,0));
        presetPanel.setOpaque(false);
        JLabel presetLabel = new JLabel("Tempo preset:");
        presetLabel.setFont(Theme.BODY_FONT);
        presetLabel.setForeground(Theme.TEXT_SECONDARY);
        presetPanel.add(presetLabel);
        presetPanel.add(tempoPresetCombo);

        JPanel controlPanel=new JPanel(new FlowLayout(FlowLayout.CENTER));
        controlPanel.setBackground(Theme.SURFACE);
        controlPanel.add(startStopButton);
        controlPanel.add(muteButton);

        beatPanel.setBackground(Theme.SURFACE);

        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(25));
        mainPanel.add(tempoPanel);
        mainPanel.add(Box.createVerticalStrut(12));
        mainPanel.add(presetPanel);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(signaturePanel);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(beatPanel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(controlPanel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(statusLabel);

        backgroundPanel.add(mainPanel);

        setContentPane(backgroundPanel);
    }

    private void registerListeners(){
        decreaseButton.addActionListener(e -> changeBpm(-1));
        increaseButton.addActionListener(e -> changeBpm(1));

        timeSignatureCombo.addActionListener(e -> {
            String selected = (String) timeSignatureCombo.getSelectedItem();

            if (selected == null) {
                return;
            }

            String[] parts = selected.split("/");

            int beats = Integer.parseInt(parts[0]);
            int unit = Integer.parseInt(parts[1]);

            settings.setBeatsPerMeasure(beats);
            settings.setBeatUnit(unit);

            service.resetBeat();

            updateBeatIndicator(null);
        });

        startStopButton.addActionListener(e -> toggleMetronome());

        muteButton.addActionListener(e -> toggleMute());

        tempoPresetCombo.addActionListener(e -> {
            String selected = (String) tempoPresetCombo.getSelectedItem();
            if (selected == null || selected.equals("Custom")) {return;}
            setBpm(Integer.parseInt(selected));
        });
    }

    private void changeBpm(int delta) {
        setBpm(settings.getBpm() + delta);
    }

    private void toggleMetronome(){
        if(running){
            service.stop();
            running=false;
            startStopButton.setText("Start");
            statusLabel.setText("Metronome stopped");
            updateBeatIndicator(null);
            ((RoundedButton) startStopButton)
                    .setButtonBackground(Theme.PRIMARY);
        } else {
            service.start();
            running=true;
            startStopButton.setText("Stop");
            statusLabel.setText("Metronome running");
            ((RoundedButton) startStopButton)
                    .setButtonBackground(Theme.STOP_BACKGROUND);
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

        beatPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 18, 5));
        beatPanel.setBackground(Theme.SURFACE);

        for(int i=1;i<=settings.getBeatsPerMeasure();i++){
            JPanel beatColumn=new JPanel();
            beatColumn.setLayout(new BoxLayout(beatColumn,BoxLayout.Y_AXIS));
            beatColumn.setOpaque(false);

            boolean active = beat!=null && beat.getBeatNumber()==i;

            JLabel arrow=new  JLabel(active?"▼" : " ");
            arrow.setFont(new Font("SansSerif", Font.BOLD, 15));
            arrow.setForeground(Theme.ACCENT);
            arrow.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel indicator=new JLabel("●");
            indicator.setFont(new Font("SansSerif", Font.BOLD, 26));
            indicator.setForeground(active
                    ? Theme.BEAT_ACTIVE
                    : Theme.BEAT_INACTIVE);
            indicator.setAlignmentX(Component.CENTER_ALIGNMENT);

            beatColumn.add(arrow);
            beatColumn.add(Box.createVerticalStrut(4));
            beatColumn.add(indicator);
            beatColumn.add(Box.createVerticalStrut(4));

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
        timeSignatureCombo.getAccessibleContext().setAccessibleDescription("Select a time signature. Use up and down arrow keys to change it.");

        bpmLabel.getAccessibleContext().setAccessibleName("Current tempo");
        bpmLabel.getAccessibleContext().setAccessibleDescription("Current tempo: "+settings.getBpm()+" beats per minute.");

        statusLabel.getAccessibleContext().setAccessibleName("Metronome status");
        statusLabel.getAccessibleContext().setAccessibleDescription("The metronome is currently stopped.");

        muteButton.getAccessibleContext().setAccessibleName("Mute sound");
        muteButton.getAccessibleContext().setAccessibleDescription(
                "Sound is enabled. Press M or activate this button to mute sound."
        );

        tempoPresetCombo.getAccessibleContext().setAccessibleName("Tempo preset");
        tempoPresetCombo.getAccessibleContext().setAccessibleDescription("Select a predefined tempo in beats per minute.");
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

        inputMap.put(KeyStroke.getKeyStroke("M"),"toggleMute");
        actionMap.put("toggleMute", new AbstractAction(){
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                toggleMute();
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
        //addFocusIndicator(decreaseButton);
        //addFocusIndicator(increaseButton);
        //addFocusIndicator(startStopButton);
        addFocusIndicator(timeSignatureCombo);
    }

    private void addFocusIndicator(JComponent component){
        Border originalBorder = component.getBorder();
        Border focusBorder = BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Theme.FOCUS,3), originalBorder);

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

    private void toggleMute(){
        boolean muted = !audio.isMuted();
        audio.setMuted(muted);
        muteButton.setIcon(new VolumeIcon(muted,Theme.PRIMARY));
        muteButton.setToolTipText(muted?"Unmute":"Mute");
        muteButton.getAccessibleContext().setAccessibleName(muted?"Unmute sound":"Mute sound");
        muteButton.getAccessibleContext().setAccessibleDescription(
                muted
                        ? "Sound is muted. Press M or activate this button to enable sound."
                        : "Sound is enabled. Press M or activate this button to mute sound."
        );
    }

    private void setBpm(int bpm) {
        if (bpm < 40 || bpm > 220) {return;}

        if (settings.getBpm() == bpm) {return;}

        settings.setBpm(bpm);
        bpmLabel.setText(String.valueOf(bpm));
        updateTempoPresetSelection();

        bpmLabel.getAccessibleContext().setAccessibleDescription(
                "Current tempo: " + bpm + " beats per minute."
        );

        if (running) {
            service.stop();
            service.start();
        }
    }

    private void updateTempoPresetSelection() {
        String currentBpm = String.valueOf(settings.getBpm());

        for (String preset : TEMPO_PRESETS) {
            if (preset.equals(currentBpm)) {
                tempoPresetCombo.setSelectedItem(currentBpm);
                return;
            }
        }

        tempoPresetCombo.setSelectedItem("Custom");
    }
}
