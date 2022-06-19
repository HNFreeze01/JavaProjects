package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import view.GUIBanco;

public class Bank implements BankDAO {
	private ArrayList<String> user_pass = new ArrayList<>();
	private double balance;
	private String user;

	public Bank(String user) {
		this.user = user;
	}

	public Bank() {

	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}
	
	public String getUser() {
		return user;
	}
	
	public void setUser(String user) {
		this.user = user;
	}
	
	public ArrayList<String> getUser_pass() {
		return user_pass;
	}
	
	public void setUser_pass(ArrayList<String> user_pass) {
		this.user_pass = user_pass;
	}

	@Override
	public String checkBalance(String user) throws FileNotFoundException, IOException {
		File user_balance = new File(user + ".txt");
		BufferedReader br = new BufferedReader(new FileReader(user_balance));
		br.readLine();
		String money = br.readLine();
		balance = Double.parseDouble(money);

		return money;
	}

	@Override
	public int withdrawMoney(double amount, String user) throws IOException {
		checkBalance(user);
		File user_file;
		int success = 1;
		if (amount > getBalance() || amount < 0) {
			success = -1;
		} else {
			success = 1;
			balance -= amount;
			user_file = new File(user + ".txt");
			FileWriter fw = new FileWriter(user_file);
			user = reverseCheckUser(user);
			fw.write(user + "\n" + balance);
			fw.close();

		}
		return success;
	}

	@Override
	public String showWallets() throws FileNotFoundException, IOException {
		String wallets = "";
		int count = 1;
		Path path = Paths.get(System.getProperty("user.dir"));
		String wallet = path + "\\" + "wallet";
		File folder = new File(wallet);
		File[] files = folder.listFiles();
		for (File file : files) {
			System.out.println( count + "." + file.getName());
			count++;
			wallets += file.getName();
		}

		return wallets;
	}

	@Override
	public String checkWallet(int walletSelect) {
		String wallet = "";
		if(walletSelect == 1) {
			wallet = "blackwallet";
		} else if(walletSelect == 2) {
			wallet = "bluewallet";
		}
		return wallet;
	}
	
	@Override
	public void saveInWallet(double amount, int walletSelect) throws FileNotFoundException, IOException {
		String wallet = "";
		if(walletSelect == 1) {
			wallet = "blackwallet";
		} else if(walletSelect == 2) {
			wallet = "bluewallet";
		}
		File walletFile;
		String moneyOnWallet = showWalletMoney(walletSelect);
		double parsedWallet = Double.parseDouble(moneyOnWallet);
		parsedWallet += amount;
		walletFile = new File("wallet/" + wallet + ".txt");
		FileWriter fw = new FileWriter(walletFile);
		String newWalletBalance = String.valueOf(parsedWallet);
		fw.write(newWalletBalance);
		fw.close();
	}

	@Override
	public String showWalletMoney(int walletSelect) throws FileNotFoundException, IOException {
		String wallet = "";
		if(walletSelect == 1) {
			wallet = "blackwallet";
		} else if(walletSelect == 2) {
			wallet = "bluewallet";
		}
		String availableMoney = "";
		File walletFile = new File("wallet/" + wallet + ".txt");
		BufferedReader br = new BufferedReader(new FileReader(walletFile));
		availableMoney = br.readLine();
		br.close();
		return availableMoney;
	}

	@Override
	public int depositMoney(double amount, int walletSelect, String user) throws FileNotFoundException, IOException {
		String wallet = "";
		if(walletSelect == 1) {
			wallet = "blackwallet";
		} else if(walletSelect == 2) {
			wallet = "bluewallet";
		}
		File walletFile;
		File user_file;
		String moneyOnWallet = showWalletMoney(walletSelect);
		double moneyParsed = Double.parseDouble(moneyOnWallet);
		int success;
		if (amount > moneyParsed || amount < 0) {
			success = -1;
		} else {
			moneyParsed -= amount;
			walletFile = new File("wallet/" + wallet + ".txt");
			FileWriter fw = new FileWriter(walletFile);
			String newWalletBalance = String.valueOf(moneyParsed);
			fw.write(newWalletBalance);
			
			balance += amount;
			user_file = new File(user + ".txt");
			FileWriter fw2 = new FileWriter(user_file);
			user = reverseCheckUser(user);
			fw2.write(user + "\n" + balance);
			fw.close();
			fw2.close();

			success = 1;

		}

		
		return success;
	}

	/**
	 * Allows to authenticate and check if the information is correct
	 * @param user
	 * @param password
	 * @return An ArrayList with both of user and pass to check in main
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws InvalidKeyException
	 * @throws ClassNotFoundException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	public ArrayList<String> authentication(String user, String password)
			throws FileNotFoundException, IOException, InvalidKeyException, ClassNotFoundException,
			NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		String picked_User = checkUser(user) + ".txt";
		File user_File = new File(picked_User);

		if (!user_File.exists()) {
			System.err.println("User not found!");
			// Registrar?
		}

		BufferedReader br = new BufferedReader(new FileReader(user_File));
		String line = br.readLine();
		if (!line.equals(user)) {
			user_pass.add("null");
			return user_pass;
		}
		user_pass.add(line);
		String pass = decrypted_Pass(user);
		if (!password.equals(pass)) {
			System.err.println("Invalid password!");
			user_pass.add("null");
		}
		user_pass.add(pass);

		return user_pass;
	}

	/**
	 * Allows to check which user is it
	 * @param user
	 * @return The name of the actual user
	 */
	public String checkUser(String user) {
		String name_to_return = "";
		String name_of_user[] = { "20921531J", "08234589K" };
		if (user.equals(name_of_user[0])) {
			name_to_return = "Sergio";

		} else if (user.equals(name_of_user[1])) {
			name_to_return = "Ana";
		}
		return name_to_return;

	}

	/**
	 * Allos to check which user is it with a reverse method 
	 * @param user
	 * @return The ID of the user
	 */
	public String reverseCheckUser(String user) {
		String name_to_return = "";
		String name_of_user[] = { "Sergio", "Ana" };
		if (user.equals(name_of_user[0])) {
			name_to_return = "20921531J";

		} else if (user.equals(name_of_user[1])) {
			name_to_return = "08234589K";
		}

		return name_to_return;

	}

	/**
	 * Not needed to run again
	 * 
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public void unique_Cipher(String username, String password) throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException, FileNotFoundException, IOException {
		String algorithm = "RSA";

		KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(algorithm);

		KeyPair keyPair = keyPairGen.generateKeyPair();
		PublicKey publicKey = keyPair.getPublic();
		PrivateKey privateKey = keyPair.getPrivate();

		// Cipher
		Cipher cipher = Cipher.getInstance(algorithm);
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		byte[] encrypted_password = cipher.doFinal(password.getBytes());
		// Now we save the publicKey, the private key and the cipher pass
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("keys/user_2/publicKey"));
		oos.writeObject(publicKey);
		oos.close();

		oos = new ObjectOutputStream(new FileOutputStream("keys/user_2/privateKey"));
		oos.writeObject(privateKey);
		oos.close();

		oos = new ObjectOutputStream(new FileOutputStream("keys/user_2/" + username + "_.txt"));
		oos.writeObject(encrypted_password);
		oos.close();

	}

	/**
	 * Allows us to decrypt the pass
	 * @param username
	 * @return Returns the decrypted pass
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	public String decrypted_Pass(String username)
			throws FileNotFoundException, IOException, ClassNotFoundException, NoSuchAlgorithmException,
			NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		
		String pass = "";
		if(username.equals("20921531J")) {
			// First, we save the private key in a variable
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream("keys/user_1/privateKey"));
			PrivateKey privateKey = (PrivateKey) ois.readObject();
			ois.close();

			// Secondly we pick up the encrypted pass
			ois = new ObjectInputStream(new FileInputStream("keys/user_1/" + username + "_.txt"));
			byte[] dec_Pass = (byte[]) ois.readObject();
			ois.close();

			// Now we instance the cipher to do the decrypt
			Cipher cipher = Cipher.getInstance(privateKey.getAlgorithm());
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			byte[] decrypted = cipher.doFinal(dec_Pass);
			pass = new String(decrypted);
			
		} else if(username.equals("08234589K")) {
			// First, we save the private key in a variable
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream("keys/user_2/privateKey"));
			PrivateKey privateKey = (PrivateKey) ois.readObject();
			ois.close();

			// Secondly we pick up the encrypted pass
			ois = new ObjectInputStream(new FileInputStream("keys/user_2/" + username + "_.txt"));
			byte[] dec_Pass = (byte[]) ois.readObject();
			ois.close();

			// Now we instance the cipher to do the decrypt
			Cipher cipher = Cipher.getInstance(privateKey.getAlgorithm());
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			byte[] decrypted = cipher.doFinal(dec_Pass);
			pass = new String(decrypted);

		}
		return pass;

		
		

		
	}

	

}
