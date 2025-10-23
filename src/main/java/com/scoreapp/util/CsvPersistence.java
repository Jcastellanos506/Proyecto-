package com.scoreapp.util;

import com.scoreapp.model.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;

/**
 * Persistencia CSV simple para la demo.
 * Archivos:
 *   user.csv           -> fullName,email,monthlyIncome
 *   accounts.csv       -> type,id,name,balance,annualRate,openedAt,limitOrPrincipal,termMonths
 *   payments.csv       -> accountId,date,amount,onTime
 *   score_history.csv  -> score
 */
public final class CsvPersistence {
    private final Path dir;

    public CsvPersistence(Path dir){
        this.dir = dir;
    }

    public void saveUser(User u) throws IOException {
        Files.createDirectories(dir);
        try (BufferedWriter w = Files.newBufferedWriter(dir.resolve("user.csv"), StandardCharsets.UTF_8)) {
            w.write("fullName,email,monthlyIncome\n");
            w.write(escapeCsv(u.getFullName()) + "," + escapeCsv(u.getEmail()) + "," + u.getMonthlyIncome() + "\n");
        }
    }

    public Optional<User> loadUser() throws IOException {
        Path f = dir.resolve("user.csv");
        if (!Files.exists(f)) return Optional.empty();
        List<String> lines = Files.readAllLines(f, StandardCharsets.UTF_8);
        if (lines.size() < 2) return Optional.empty();
        String[] parts = splitCsvLine(lines.get(1));
        if (parts.length < 3) return Optional.empty();
        String fullName = unescapeCsv(parts[0]);
        String email = unescapeCsv(parts[1]);
        double income = Double.parseDouble(parts[2]);
        return Optional.of(new User(fullName, email, income));
    }

    public void saveAccounts(User u) throws IOException {
        Files.createDirectories(dir);
        // accounts.csv
        try (BufferedWriter w = Files.newBufferedWriter(dir.resolve("accounts.csv"), StandardCharsets.UTF_8)) {
            w.write("type,id,name,balance,annualRate,openedAt,limitOrPrincipal,termMonths\n");
            for (CreditAccount a : u.getAccounts()) {
                String type = (a instanceof CreditCardAccount) ? "CC" : "LN";
                String extra1 = "";
                String extra2 = "";
                if (a instanceof CreditCardAccount cc) {
                    extra1 = String.valueOf(cc.getCreditLimit());
                    extra2 = "0";
                } else if (a instanceof LoanAccount ln) {
                    extra1 = String.valueOf(ln.getPrincipal());
                    extra2 = String.valueOf(ln.getTermMonths());
                }
                String line = String.join(",",
                        type,
                        a.getId(),
                        escapeCsv(a.getName()),
                        String.valueOf(a.getBalance()),
                        String.valueOf(a.getAnnualRate()),
                        a.getOpenedAt().toString(),
                        extra1,
                        extra2
                );
                w.write(line);
                w.write("\n");
            }
        }

        // payments.csv
        try (BufferedWriter w = Files.newBufferedWriter(dir.resolve("payments.csv"), StandardCharsets.UTF_8)) {
            w.write("accountId,date,amount,onTime\n");
            for (CreditAccount a : u.getAccounts()) {
                for (PaymentRecord pr : a.getPayments()) {
                    w.write(String.join(",",
                            a.getId(),
                            pr.getDate().toString(),
                            String.valueOf(pr.getAmount()),
                            String.valueOf(pr.isOnTime())
                    ));
                    w.write("\n");
                }
            }
        }
    }

    public void loadAccounts(User u) throws IOException {
        // accounts.csv
        Path f = dir.resolve("accounts.csv");
        if (!Files.exists(f)) return;

        Map<String, CreditAccount> byId = new HashMap<>();
        for (String line : Files.readAllLines(f, StandardCharsets.UTF_8)) {
            if (line.isBlank() || line.startsWith("type")) continue;
            String[] p = splitCsvLine(line);
            String type = p[0];
            String id   = p[1];
            String name = unescapeCsv(p[2]);
            double balance = Double.parseDouble(p[3]);
            double rate    = Double.parseDouble(p[4]);
            LocalDate opened = LocalDate.parse(p[5]);

            if ("CC".equals(type)) {
                double limit = Double.parseDouble(p[6]);
                CreditCardAccount cc = new CreditCardAccount(id, name, balance, rate, opened, limit);
                u.addAccount(cc);
                byId.put(id, cc);
            } else {
                double principal = Double.parseDouble(p[6]);
                int term = Integer.parseInt(p[7]);
                LoanAccount ln = new LoanAccount(id, name, balance, rate, opened, principal, term);
                u.addAccount(ln);
                byId.put(id, ln);
            }
        }

        // payments.csv
        Path fp = dir.resolve("payments.csv");
        if (Files.exists(fp)) {
            for (String line : Files.readAllLines(fp, StandardCharsets.UTF_8)) {
                if (line.isBlank() || line.startsWith("accountId")) continue;
                String[] p = splitCsvLine(line);
                CreditAccount a = byId.get(p[0]);
                if (a == null) continue;
                LocalDate date = LocalDate.parse(p[1]);
                double amount  = Double.parseDouble(p[2]);
                boolean onTime = Boolean.parseBoolean(p[3]);
                a.addPayment(new PaymentRecord(date, amount, onTime));
            }
        }
    }

    public void saveScoreHistory(List<Integer> history) throws IOException {
        Files.createDirectories(dir);
        try (BufferedWriter w = Files.newBufferedWriter(dir.resolve("score_history.csv"), StandardCharsets.UTF_8)) {
            w.write("score\n");
            for (Integer s : history) {
                w.write(String.valueOf(s));
                w.write("\n");
            }
        }
    }

    public List<Integer> loadScoreHistory() throws IOException {
        List<Integer> list = new ArrayList<>();
        Path f = dir.resolve("score_history.csv");
        if (!Files.exists(f)) return list;
        for (String line : Files.readAllLines(f, StandardCharsets.UTF_8)) {
            if (line.isBlank() || line.startsWith("score")) continue;
            list.add(Integer.parseInt(line.trim()));
        }
        return list;
    }

    // ===== Utilidades CSV seguras (sin backslash raro) =====

    private static String escapeCsv(String s) {
        if (s == null) return "";
        boolean needsQuotes = s.contains(",") || s.contains("\"") || s.contains("\n") || s.contains("\r");
        String out = s.replace("\"", "\"\"");
        return needsQuotes ? "\"" + out + "\"" : out;
    }

    private static String unescapeCsv(String s) {
        if (s == null) return "";
        s = s.trim();
        if (s.startsWith("\"") && s.endsWith("\"")) {
            s = s.substring(1, s.length() - 1).replace("\"\"", "\"");
        }
        return s;
    }

    private static String[] splitCsvLine(String line) {
        List<String> parts = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean quoted = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                quoted = !quoted;
                cur.append(c); // conservamos comillas para unescape correcto
            } else if (c == ',' && !quoted) {
                parts.add(cur.toString());
                cur.setLength(0);
            } else {
                cur.append(c);
            }
        }
        parts.add(cur.toString());
        return parts.toArray(new String[0]);
    }
}
