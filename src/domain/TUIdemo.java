package domain;

import jexer.TAction;
import jexer.TApplication;
import jexer.TField;
import jexer.TText;
import jexer.TWindow;
import jexer.event.TMenuEvent;
import jexer.menu.TMenu;
import com.mybank.domain.Bank;
import com.mybank.domain.Customer;
import com.mybank.domain.Account;
import com.mybank.domain.CheckingAccount;
import com.mybank.domain.SavingsAccount;
import com.mybank.domain.*;
import com.mybank.data.DataSource;
import java.io.IOException;

/**
 *
 * @author Alexander 'Taurus' Babich
 */
public class TUIdemo extends TApplication {

    private static final int ABOUT_APP = 2000;
    private static final int CUST_INFO = 2010;

    public static void main(String[] args) throws Exception {
        TUIdemo tdemo = new TUIdemo();
        (new Thread(tdemo)).start();
    }

    public TUIdemo() throws Exception {
        super(BackendType.SWING);

        addToolMenu();
        //custom 'File' menu
        TMenu fileMenu = addMenu("&File");
        fileMenu.addItem(CUST_INFO, "&Customer Info");
        fileMenu.addDefaultItem(TMenu.MID_SHELL);
        fileMenu.addSeparator();
        fileMenu.addDefaultItem(TMenu.MID_EXIT);
        //end of 'File' menu  

        addWindowMenu();

        //custom 'Help' menu
        TMenu helpMenu = addMenu("&Help");
        helpMenu.addItem(ABOUT_APP, "&About...");
        //end of 'Help' menu 

        setFocusFollowsMouse(true);
        //Customer window
        ShowCustomerDetails();
    }

    @Override
    protected boolean onMenu(TMenuEvent menu) {
        if (menu.getId() == ABOUT_APP) {
            messageBox("About", "\t\t\t\t\t   Just a simple Jexer demo.\n\nCopyright \u00A9 2019 Alexander \'Taurus\' Babich").show();
            return true;
        }
        if (menu.getId() == CUST_INFO) {
            ShowCustomerDetails();
            return true;
        }
        return super.onMenu(menu);
    }

private void ShowCustomerDetails() {
    // Створення банку та додавання клієнтів
    Bank bank = Bank.getBank();
    
    Customer customer1 = new Customer("John", "Doe");
    customer1.addAccount(new CheckingAccount(200.00, 100.00)); // баланс і овердрафт
    bank.addCustomer("John", "Doe");

    Customer customer2 = new Customer("Jane", "Smith");
    customer2.addAccount(new SavingsAccount(500.00, 0.05)); // баланс і відсоткова ставка
    bank.addCustomer("Jane", "Smith");

    // Додаємо клієнтів вручну до банку, щоб зберегти їхні об'єкти
    bank.getCustomer(0).addAccount(customer1.getAccount(0));
    bank.getCustomer(1).addAccount(customer2.getAccount(0));

    TWindow custWin = addWindow("Customer Window", 2, 1, 40, 10, TWindow.NOZOOMBOX);
    custWin.newStatusBar("Enter valid customer number and press Show...");

    custWin.addLabel("Enter customer number: ", 2, 2);
    TField custNo = custWin.addField(24, 2, 3, false);
    TText details = custWin.addText("Owner Name: \nAccount Type: \nAccount Balance: ", 2, 4, 38, 8);

    custWin.addButton("&Show", 28, 2, new TAction() {
        @Override
        public void DO() {
            try {
                int custNum = Integer.parseInt(custNo.getText());
                
                if (custNum < 0 || custNum >= bank.getNumberOfCustomers()) {
                    throw new IndexOutOfBoundsException();
                }

                Customer customer = bank.getCustomer(custNum);
                Account account = customer.getAccount(0); // показуємо перший рахунок
                
                String accountType = account instanceof CheckingAccount ? "Checking" : 
                                     account instanceof SavingsAccount ? "Savings" : "Unknown";
                
                String text = "Owner Name: " + customer.getFirstName() + " " + customer.getLastName() +
                              " (id=" + custNum + ")\nAccount Type: '" + accountType + "'" +
                              "\nAccount Balance: $" + String.format("%.2f", account.getBalance());

                details.setText(text);

            } catch (Exception e) {
                messageBox("Error", "You must provide a valid customer number!").show();
            }
        }
    });
}




}
