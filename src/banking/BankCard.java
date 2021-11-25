package banking;

import java.security.SecureRandom;


public class BankCard {

    private String cardNumber;                //Зберігаємо номер карт
    private String cardPin;                   //Зберігаємо пін-коди
    private int cardBalance = 0;              //Зберігаємо баланс


    //Створюємо карту та пін-код у конструкторі класу
    public BankCard() {

        SecureRandom random = new SecureRandom();
        final String BIN = "400000";                    //Константа BIN

        //Рандом від 0 до 1000000000 и форматуємо в 9 цифр зберігаючи ведучі нулі
        cardNumber = BIN + //Для створення 9 цифр
                String.format("%09d", random.nextInt(1000000000));               //Номер карти
        cardNumber = cardNumber + checkSum(cardNumber); //Додаємо контрольну суму

        //Рандом від 0 до 10000 и форматуємо в 4 цифри зберігаючи ведучі нули
        cardPin = String.format("%04d", random.nextInt(10000));

    }

    //Вичисляємо контрольну суму алгоритму Луна
    public static String checkSum(String cardNumber) {
        int[] number = new int[cardNumber.length()];
        int sum = 0;
        for(int i = 0; i < cardNumber.length() ; i++){
            number[i] = Integer.parseInt(String.valueOf(cardNumber.charAt(i)));
            if (i % 2 == 0) {
                if (number[i] * 2 > 9) {
                    number[i] = number[i] * 2 - 9;
                } else {
                    number[i] = number[i] * 2;
                }
            }
            sum = sum + number[i];
        }
        return String.valueOf((10 - (sum % 10) == 10) ? 0 : (10 - (sum % 10)));
    }

    public String getCardNumber(){
        return cardNumber;
    }

    public String getCardPin() {
        return cardPin;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public void setCardPin(String cardPin) {
        this.cardPin = cardPin;
    }

    public void setCardBalance(int cardBalance) {
        this.cardBalance = cardBalance;
    }
}