package banking;

public class Main {
    public static void main(String[] args) {
        String fileName = "";

        if (args.length != 0){
            if ("-fileName".equals(args[0])) {
                fileName = args[1];
            }
        } else {
            fileName = "card.s3db";
        }
        DataBase dataBase = new DataBase(fileName);
        dataBase.createNewDatabase(fileName);
        dataBase.createNewTable();
        Bank bank = new Bank(fileName);
        bank.init();
    }
}