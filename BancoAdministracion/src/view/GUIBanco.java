package view;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import model.Bank;

public class GUIBanco {
	private static Scanner letterScanner = new Scanner(System.in);
	private static Scanner numericScanner = new Scanner(System.in);

	public static void main(String[] args) {
		System.out.println("Welcome to my Bank Administration mini - project. The available users are 20921531J & 08234589K, both of them with the password 1234, which is"
				+ "encrypted and if it is written wrong, it will fail, as well as the user of course.\n\n\n");
		int user_Selection;
		double amount_to_withdraw;
		String walletSelection;
		int walletSelect;
		ArrayList<String> user_pass = new ArrayList<>();

		// Ya hay usuario y contraseña anteriormente generado en un fichero
		boolean isNumber;
		boolean isLoged = false;
		boolean hasLoged = false;
		String user;
		String password;
		try {
			do {
				isLoged = false;
				System.out.print("Welcome to your online bank\nUser: ");
				user = letterScanner.nextLine();
				System.out.print("Password: ");
				password = letterScanner.nextLine();
				Bank b1 = new Bank(user);

				//b1.unique_Cipher(user, password);

				user_pass = b1.authentication(user, password);
				if (user_pass.get(0).equals("null") || user_pass.get(1).equals("null")) {
					System.err.println("Failure in the authentication");
				} else {
					user = b1.checkUser(user);
					while (!isLoged) {

						do {
							System.out.println("Welcome " + user
									+ ", what would you like to do?\n1.Check your balance\n2.Withdrawal\n3.Deposit\n4.Log out");
							if (numericScanner.hasNextInt()) {
								user_Selection = numericScanner.nextInt();
								isNumber = true;
								switch (user_Selection) {
								case 1:
									System.out.println("Your current balance is " + b1.checkBalance(user) + "€");
									break;

								case 2:
									int checkSuccess;
									System.out.print("Input the selected amount: ");
									amount_to_withdraw = numericScanner.nextDouble();
									checkSuccess = b1.withdrawMoney(amount_to_withdraw, user);
									if (checkSuccess == 1) {
										System.out.println("In which wallet do you want to save the money?");
										b1.showWallets();
										walletSelect = numericScanner.nextInt();
										b1.saveInWallet(amount_to_withdraw, walletSelect);
										
									} else {
										System.err.println("Invalid transaction!");
									}
									break;

								case 3:
									System.out.println("Which wallet do you want to use?");
									b1.showWallets();
									walletSelect = numericScanner.nextInt();
									System.out
											.println("Available money -> " + b1.showWalletMoney(walletSelect) + "€");

									System.out.println("How much money do you want to deposit into your account?");
									amount_to_withdraw = numericScanner.nextDouble();
									int success = b1.depositMoney(amount_to_withdraw, walletSelect, user);
									if (success == -1) {
										System.err.println("Failed to deposit money");
									}
									break;

								case 4:
									isLoged = true;
									break;

								default:
									System.err.println("That doesn't seem to be an available option");
									break;
								}
							} else {
								System.err.println("That doesn't seem to be an available option");
								isNumber = false;
								numericScanner.next();
							}

						} while (!isNumber);
					}
				}
			} while (!hasLoged);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}

	}

}
