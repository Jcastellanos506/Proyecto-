package com.scoreapp.ui;

import com.scoreapp.model.*;
import com.scoreapp.service.*;
import com.scoreapp.util.CsvPersistence;

import javax.swing.*;

// 👇 Imports específicos de AWT para evitar ambigüedad con java.util.List
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class MainWindow extends JFrame {
    private final UserService userSvc;
    private final AccountService accountSvc;
    private final ScoreService scoreSvc;
    private final AdvisorService advisorSvc;

    private User currentUser;
    private final DefaultListModel<String> accountsModel = new DefaultListModel<>();
    private final List<Integer> scoreHistory = new ArrayList<>();
    private final CsvPersistence persistence = new CsvPersistence(Path.of("data/scoreapp"));

    private ScoreChartPanel chartPanel;

    public MainWindow(UserService userSvc, AccountService accountSvc, ScoreService scoreSvc, AdvisorService advisorSvc){
        super("Score APP — Demo");
        this.userSvc = userSvc;
        this.accountSvc = accountSvc;
        this.scoreSvc = scoreSvc;
        this.advisorSvc = advisorSvc;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JTabbedPane tabs = new JTabbedPane();
        tabs.add("Perfil", buildProfilePanel());
        tabs.add("Cuentas", buildAccountsPanel());
        tabs.add("Score", buildScorePanel());
        tabs.add("Consejos", buildAdvicePanel());
        tabs.add("Histórico", buildHistoryPanel());
        add(tabs, BorderLayout.CENTER);

        // Barra superior con Guardar/Cargar
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton save = new JButton("Guardar Sesión");
        JButton load = new JButton("Cargar Sesión");
        top.add(save); top.add(load);
        add(top, BorderLayout.NORTH);

        save.addActionListener(e -> doSave());
        load.addActionListener(e -> doLoad());
    }

    private JPanel buildProfilePanel(){
        JPanel p = new JPanel(new GridLayout(6,2,8,8));
        p.setBorder(BorderFactory.createEmptyBorder(16,16,16,16));
        p.setBackground(new Color(245, 248, 255));

        JTextField name = new JTextField();
        JTextField email = new JTextField();
        JTextField income = new JTextField();

        JButton btn = new JButton("Crear/Actualizar Usuario");
        JLabel status = new JLabel(" ");

        p.add(new JLabel("Nombre completo:")); p.add(name);
        p.add(new JLabel("Email:")); p.add(email);
        p.add(new JLabel("Ingreso mensual:")); p.add(income);

        p.add(new JLabel(" ")); p.add(btn);
        p.add(new JLabel("Estado:")); p.add(status);

        btn.addActionListener(e -> {
            try{
                String n = name.getText();
                String em = email.getText();
                double inc = Double.parseDouble(income.getText());
                if(currentUser==null){
                    currentUser = userSvc.createUser(n, em, inc);
                }else{
                    currentUser.setFullName(n);
                    currentUser.setMonthlyIncome(inc);
                }
                status.setText("Usuario listo: " + (currentUser!=null? currentUser.getEmail() : ""));
            }catch(Exception ex){
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        return p;
    }

    private JPanel buildAccountsPanel(){
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createEmptyBorder(16,16,16,16));
        p.setBackground(new Color(240, 255, 245));

        JPanel form = new JPanel(new GridLayout(6,2,8,8));
        JTextField name = new JTextField();
        JTextField limit = new JTextField();
        JTextField rate = new JTextField();
        JTextField openedMonths = new JTextField("12");
        JButton addCard = new JButton("Agregar Tarjeta");
        JButton addLoan = new JButton("Agregar Préstamo");

        form.add(new JLabel("Nombre cuenta:")); form.add(name);
        form.add(new JLabel("Cupo / Principal:")); form.add(limit);
        form.add(new JLabel("Tasa anual (0-1):")); form.add(rate);
        form.add(new JLabel("Antigüedad (meses):")); form.add(openedMonths);
        form.add(addCard); form.add(addLoan);

        JList<String> list = new JList<>(accountsModel);
        list.setBorder(BorderFactory.createTitledBorder("Cuentas"));

        addCard.addActionListener(e -> {
            try{
                requireUser();
                String n = name.getText();
                double lim = Double.parseDouble(limit.getText());
                double r = Double.parseDouble(rate.getText());
                int m = Integer.parseInt(openedMonths.getText());
                accountSvc.addCreditCard(currentUser, n, lim, r, java.time.LocalDate.now().minusMonths(m));
                accountsModel.addElement("Tarjeta: " + n + " | cupo " + lim);
            }catch(Exception ex){
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        addLoan.addActionListener(e -> {
            try{
                requireUser();
                String n = name.getText();
                double principal = Double.parseDouble(limit.getText());
                double r = Double.parseDouble(rate.getText());
                int m = Integer.parseInt(openedMonths.getText());
                accountSvc.addLoan(currentUser, n, principal, r, 24, java.time.LocalDate.now().minusMonths(m));
                accountsModel.addElement("Préstamo: " + n + " | principal " + principal);
            }catch(Exception ex){
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        p.add(form, BorderLayout.NORTH);
        p.add(new JScrollPane(list), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildScorePanel(){
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createEmptyBorder(16,16,16,16));
        p.setBackground(new Color(255, 249, 240));

        JTextArea result = new JTextArea(8,40);
        result.setEditable(false);
        JButton compute = new JButton("Calcular Score");

        compute.addActionListener(e -> {
            try{
                requireUser();
                ScoreResult sr = scoreSvc.compute(currentUser);
                scoreHistory.add(sr.getScore());
                StringBuilder sb = new StringBuilder();
                sb.append("Score: ").append(sr.getScore()).append("\n");
                sb.append("Factores: ").append(sr.getFactorBreakdown()).append("\n");
                result.setText(sb.toString());
                if(chartPanel!=null) chartPanel.setData(scoreHistory);
            }catch(Exception ex){
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        p.add(compute, BorderLayout.NORTH);
        p.add(new JScrollPane(result), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildAdvicePanel(){
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createEmptyBorder(16,16,16,16));
        p.setBackground(new Color(245, 240, 255));

        JTextArea advice = new JTextArea(10,40);
        advice.setEditable(false);
        JButton btn = new JButton("Generar Consejos");

        btn.addActionListener(e -> {
            try{
                requireUser();
                ScoreResult sr = scoreSvc.compute(currentUser);
                List<String> tips = advisorSvc.aggregateAdvice(currentUser, sr);
                String text = String.join("\n - ", tips);
                if(!text.startsWith(" - ")) text = " - " + text;
                advice.setText(text);
            }catch(Exception ex){
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        p.add(btn, BorderLayout.NORTH);
        p.add(new JScrollPane(advice), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildHistoryPanel(){
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createEmptyBorder(16,16,16,16));
        p.setBackground(new Color(235, 235, 250));

        chartPanel = new ScoreChartPanel(scoreHistory);
        JButton saveHist = new JButton("Guardar Histórico");
        JButton loadHist = new JButton("Cargar Histórico");

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(saveHist); top.add(loadHist);

        saveHist.addActionListener(e -> {
            try{
                persistence.saveScoreHistory(scoreHistory);
                JOptionPane.showMessageDialog(this, "Histórico guardado en data/scoreapp/score_history.csv");
            }catch(Exception ex){
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        loadHist.addActionListener(e -> {
            try{
                List<Integer> loaded = persistence.loadScoreHistory();
                scoreHistory.clear();
                scoreHistory.addAll(loaded);
                chartPanel.setData(scoreHistory);
                JOptionPane.showMessageDialog(this, "Histórico cargado (" + loaded.size() + " puntos)");
            }catch(Exception ex){
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        p.add(top, BorderLayout.NORTH);
        p.add(chartPanel, BorderLayout.CENTER);
        return p;
    }

    private void doSave(){
        try{
            requireUser();
            persistence.saveUser(currentUser);
            persistence.saveAccounts(currentUser);
            JOptionPane.showMessageDialog(this, "Sesión guardada en carpeta data/scoreapp/");
        }catch(Exception ex){
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doLoad(){
        try{
            var opt = persistence.loadUser();
            if(opt.isEmpty()){
                JOptionPane.showMessageDialog(this, "No hay usuario guardado.", "Atención", JOptionPane.WARNING_MESSAGE);
                return;
            }
            currentUser = opt.get();
            persistence.loadAccounts(currentUser);
            accountsModel.clear();
            for(CreditAccount a : currentUser.getAccounts()){
                if(a instanceof CreditCardAccount){
                    accountsModel.addElement("Tarjeta: " + a.getName());
                }else{
                    accountsModel.addElement("Préstamo: " + a.getName());
                }
            }
            JOptionPane.showMessageDialog(this, "Sesión cargada.");
        }catch(Exception ex){
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void requireUser(){
        if(currentUser==null) throw new IllegalStateException("Primero crea el usuario en la pestaña Perfil.");
    }
}
