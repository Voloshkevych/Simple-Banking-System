package banking;

import java.sql.*;

public class DataBase {

    private String url;

    public DataBase(String fileName){
        this.url = "jdbc:sqlite:" + fileName;
    }

    //Підключення до бази даних
    private Connection connect() {
        // SQLite's connection string

        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    //Створення нового бд файлу, якщо такого ще немає
    public void createNewDatabase(String fileName) {

        String url = "jdbc:sqlite:" + fileName;

        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn == null) {
                System.out.println("A new database NOT been created.");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    //Створення таблиці
    public void createNewTable() {

        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS card (\n"
                + "    id INTEGER PRIMARY KEY ASC,\n"
                + "    number TEXT,\n"
                + "    pin TEXT,\n"
                + "    balance INTEGER DEFAULT 0\n"
                + ");";

        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    //Вносимо дані в таблицю
    public void insert(String cardNumber, String cardPin, int balance) {

        String sql = "INSERT INTO card(number,pin,balance) VALUES(?,?,?)";

        try (Connection conn = this.connect();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, cardNumber);
            statement.setString(2, cardPin);
            statement.setInt(3, balance);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    //Перегляд обєктів бази даних
    public void selectAll() {
        String sql = "SELECT id, number, pin, balance FROM card";

        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                System.out.println(rs.getInt("id") + "\t" +
                        rs.getString("number") + "\t" +
                        rs.getString("pin") + "\t" +
                        rs.getInt("balance"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    //Отримуємо номер карти із бд по заданому номеру карти
    public String selectCardNum(String cardNumber) {

        String resultNumber = "";

        String sql = "SELECT number, pin, balance FROM card WHERE number=" + cardNumber;

        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                resultNumber = rs.getString("number");
                System.out.println("selectCard() = " + resultNumber);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return resultNumber;
    }

    //Запит на отримання обєкта номеру карти та пін-коду з бази даних
    public BankCard selectCard(String cardNumber, String cardPin) {
        BankCard result  = new BankCard();
        String resultNumber = "";
        String resultPin = "";

        String sql = "SELECT number, pin, balance FROM card WHERE number=" + cardNumber
                + " AND pin=" + cardPin;

        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                resultNumber = rs.getString("number");
                resultPin = rs.getString("pin");
                System.out.println("selectCard() = " + resultNumber + "\n pin=" + rs.getString("pin"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        result.setCardNumber(resultNumber);
        result.setCardPin(resultPin);
        return result;
    }

    //Запит на отримання обєкта номеру карти та балансу з бази даних
    public BankCard selectDestCard(String cardNumber) {
        BankCard result  = new BankCard();
        String resultNumber = "";
        int resultBalance = 0;

        String sql = "SELECT number, balance FROM card WHERE number=" + cardNumber;

        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                resultNumber = rs.getString("number");
                resultBalance = rs.getInt("balance");
                System.out.println("selectCard() = " + resultNumber + "\n bal=" + rs.getInt("balance"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        result.setCardNumber(resultNumber);
        result.setCardBalance(resultBalance);
        return result;
    }

    //Видалення даних
    public void closeAccount(BankCard bankCard){
        int delId = 0;
        String sql = "SELECT id FROM card WHERE number=" + bankCard.getCardNumber();
        System.out.println("запит на видалення: " + bankCard.getCardNumber());
        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                delId = rs.getInt("id");
                System.out.println("Найден id = " + delId);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        sql = "DELETE FROM card WHERE id = ?";
        System.out.println("try del " + bankCard.getCardNumber() + " id=" + delId);

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Задати відповідний параметр
            pstmt.setInt(1,delId);
            // видалити
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    //Вносимо зміни в баланс
    public void addBalance(int balance, BankCard bankCard) {
        int thisId = 0;
        String sql = "SELECT id FROM card WHERE number=" + bankCard.getCardNumber();
        System.out.println("Запит на перерахунок коштів: " + bankCard.getCardNumber());
        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                thisId = rs.getInt("id");
                System.out.println("Знайдений id = " + thisId);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        sql = "UPDATE card SET balance = ? WHERE id = ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Задавання необхідних параметрів
            pstmt.setInt(1, balance);
            pstmt.setInt(2, thisId);
            // Оновлення
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    //Отримуємо баланс по карті
    public int getBalance(BankCard currentCard) {
        int resultBalance = 0;
        String sql = "SELECT balance FROM card WHERE number=" + currentCard.getCardNumber();

        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                resultBalance = rs.getInt("balance");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return resultBalance;
    }
}
