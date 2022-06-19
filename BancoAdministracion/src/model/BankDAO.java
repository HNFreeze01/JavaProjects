package model;

import java.io.FileNotFoundException;
import java.io.IOException;

public interface BankDAO {
	public String checkBalance(String user) throws FileNotFoundException, IOException;
	public int withdrawMoney(double amount,String user) throws IOException;
	public int depositMoney(double amount,int walletSelect,String user) throws FileNotFoundException, IOException;
	public void saveInWallet(double amount,int walletSelect) throws FileNotFoundException, IOException;
	public String showWallets() throws FileNotFoundException, IOException;
	public String showWalletMoney(int walletSelect) throws FileNotFoundException, IOException;
	public String checkWallet(int walletSelect);
}
