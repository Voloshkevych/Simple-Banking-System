package banking;

import java.util.Scanner;

import static banking.BankCard.checkSum;

public class Bank {
    Scanner scanner = new Scanner(System.in);
    DataBase dataBase;


    public Bank(String fileName) {
        this.dataBase = new DataBase(fileName);
    }

    //Головне меню
    public void init() {
        int choice;

        do {
            System.out.println("1. Create account");
            System.out.println("2. Log into account");
            System.out.println("0. Exit");
            choice = scanner.nextInt();
            switch (choice) {
                case 0:
                    //Вихід із програми
                    exit();
                    break;
                case 1:
                    //Створення пін-коду та карти дотримуючись правила Луна
                    createCard();
                    break;
                case 2:
                    //Вхід в обліковий запис
                    logIntoAccount();
                    break;
                default:
                    System.out.println("ERROR: Не верный номер.");
            }
        } while (choice != 0);
    }

    //Створення пін-коду та карти дотримуючись правила Луна
    public void createCard(){
        BankCard bankCard = new BankCard();

        System.out.println("\nYour card have been created");
        System.out.println("Your card number:\n" + bankCard.getCardNumber());
        System.out.println("Your card PIN:\n" + bankCard.getCardPin() + "\n");

        //Переносимо в базу даних
        dataBase.insert(bankCard.getCardNumber(),bankCard.getCardPin(),0);
    }

    //Логін в обліковий запис
    private void logIntoAccount() {
        String inputCardNumber;                       //Введений номер карти
        String inputCardPin;                          //Введений пін-код
        BankCard currentCard;                         //Отримана карта

        scanner.nextLine();  //Очищуємо сканер
        System.out.println("\nEnter your card number:");
        inputCardNumber = scanner.nextLine();
        System.out.println("Enter your PIN:");
        inputCardPin = scanner.nextLine();
        //Робимо запит у БД на отриману карту та пін-код
        currentCard = dataBase.selectCard(inputCardNumber, inputCardPin);
        //Перевіряємо чи дані відповідають дійсності
        if(currentCard.getCardNumber().equals(inputCardNumber) || currentCard.getCardPin().equals(inputCardPin)){
            System.out.println("\nYou have successfully logged in!\n");
            operationAcc(currentCard);
        } else {
            System.out.println("\nWrong card number or PIN!\n");
        }
    }

    //Меню в обліковому записі
    private void operationAcc(BankCard currentCard) {
        int choice;

        do {
            System.out.println("1. Balance");
            System.out.println("2. Add income");
            System.out.println("3. Do transfer");
            System.out.println("4. Close account");
            System.out.println("5. Log out");
            System.out.println("0. Exit");
            choice = scanner.nextInt();
            switch (choice) {
                case 0:
                    exit();
                    break;
                case 1:
                    //Запит балансу
                    viewBalance(currentCard);
                    break;
                case 2:
                    //Додати гроші на баланс
                    addDeposit(currentCard);
                    break;
                case 3:
                    //Перевід грошей на інший баланс
                    doTransfer(currentCard);
                    break;
                case 4:
                    //Закриття облікового запису
                    closeAccount(currentCard);
                    dataBase.selectAll();
                    return;
                case 5:
                    //Вихід із особистого запису
                    System.out.println("\nYou have successfully logged out!\n");
                    return;
                default:
                    System.out.println(".");
            }
        } while (choice != 0);

    }


    //Запит балансу
    private void viewBalance(BankCard currentCard) {
        dataBase.getBalance(currentCard);
        System.out.println("\nBalance: " + dataBase.getBalance(currentCard) + "\n");
    }

    //Додати гроші на баланс
    private void addDeposit(BankCard currentCard) {
        System.out.println("\nВносимо:");
        dataBase.addBalance(scanner.nextInt() + dataBase.getBalance(currentCard), currentCard);
    }

    //Перевід грошей на інший баланс
    private void doTransfer(BankCard currentCard) {
        String destCardNum;     //Номер карти отримувача
        String destCheckSum;    //Перевіряємо контрольну суму алгоритму Луна
        int transfer;           //Кошти для переведення
        BankCard destCard;      //Обєкт карти отримувача
        scanner.nextLine();     //Очищуємо сканер
        //Перевіримо карту на алгоритм Луна
        do {
            System.out.println("\nКарта отримувача:");
            destCardNum = scanner.nextLine();

            destCheckSum = checkSum(destCardNum.substring(0, destCardNum.length() - 1));

            if (!String.valueOf(destCardNum.charAt(15)).equals(destCheckSum)) {
                System.out.println("Probably you made mistake in card number. Please try again!\n");
            }

        } while (!String.valueOf(destCardNum.charAt(15)).equals(destCheckSum));

        //Перевірка наявності карти в БД
        if(!destCardNum.equals(dataBase.selectCardNum(destCardNum))){
            System.out.println("\nSuch a card does not exist.\n");
            return;
        }

        //Перевірка ідентичності карт
        if(destCardNum.equals(currentCard.getCardNumber())){
            System.out.println("\nYou can't transfer money to the same account!\n");
            return;
        }

        //Перевірка чи достатньо коштів на рахунку
        do{
            System.out.println("\nСкільки перераховуємо");
            transfer = scanner.nextInt();
            if(transfer > dataBase.getBalance(currentCard)) {
                System.out.println("\nНестача коштів на балансі!\n");
            }
        } while (transfer > dataBase.getBalance(currentCard));

        //Перевід грошей на інший баланс
        destCard = dataBase.selectDestCard(destCardNum);
        dataBase.addBalance(transfer + dataBase.getBalance(destCard), destCard);
        dataBase.addBalance(dataBase.getBalance(currentCard) - transfer, currentCard);
    }

    //Закриття облікового запису
    private void closeAccount(BankCard currentCard) {
        dataBase.closeAccount(currentCard);
        System.out.println("\nAccount " + currentCard.getCardNumber() + " is deleted...");
    }

    private void exit() {
        System.out.println("\nBye!");
        System.exit(0);
    }
}
