/**
 * Copyright (c) 2019 The Antic Developers
 *
 * Distributed under the MIT software license, see the accompanying file
 * LICENSE or https://opensource.org/licenses/mit-license.php
 */
package org.antic.cli;

import java.io.File;
import java.io.IOException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

import org.antic.Kernel;
import org.antic.Launcher;
import org.antic.config.Config;
import org.antic.config.Constants;
import org.antic.config.exception.ConfigException;
import org.antic.core.BlockchainImpl;
import org.antic.core.Genesis;
import org.antic.core.Wallet;
import org.antic.core.exception.WalletLockedException;
import org.antic.crypto.Hex;
import org.antic.crypto.Key;
import org.antic.crypto.bip39.MnemonicGenerator;
import org.antic.db.DatabaseFactory;
import org.antic.db.LeveldbDatabase;
import org.antic.exception.LauncherException;
import org.antic.message.CliMessages;
import org.antic.net.filter.exception.IpFilterJsonParseException;
import org.antic.util.ConsoleUtil;
import org.antic.util.SystemUtil;
import org.antic.util.TimeUtil;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Antic command line interface.
 */
public class AnticCli extends Launcher {

    public static final boolean ENABLE_HD_WALLET_BY_DEFAULT = false;

    private static final Logger logger = LoggerFactory.getLogger(AnticCli.class);

    public static void main(String[] args, AnticCli cli) {
        try {
            // check jvm version
            if (SystemUtil.is32bitJvm()) {
                logger.error(CliMessages.get("Jvm32NotSupported"));
                SystemUtil.exit(SystemUtil.Code.JVM_32_NOT_SUPPORTED);
            }

            // system system prerequisites
            checkPrerequisite();

            // start CLI
            cli.setupLogger(args);
            cli.start(args);

        } catch (LauncherException | ConfigException | IpFilterJsonParseException | IOException exception) {
            logger.error(exception.getMessage());
        } catch (ParseException exception) {
            logger.error(CliMessages.get("ParsingFailed", exception.getMessage()));
        }
    }

    public static void main(String[] args) {
        main(args, new AnticCli());
    }

    /**
     * Creates a new Antic CLI instance.
     */
    public AnticCli() {
        SystemUtil.setLocale(getConfig().uiLocale());

        Option helpOption = Option.builder()
                .longOpt(AnticOption.HELP.toString())
                .desc(CliMessages.get("PrintHelp"))
                .build();
        addOption(helpOption);

        Option versionOption = Option.builder()
                .longOpt(AnticOption.VERSION.toString())
                .desc(CliMessages.get("ShowVersion"))
                .build();
        addOption(versionOption);

        Option accountOption = Option.builder()
                .longOpt(AnticOption.ACCOUNT.toString())
                .desc(CliMessages.get("ChooseAction"))
                .hasArg(true).numberOfArgs(1).optionalArg(false).argName("action").type(String.class)
                .build();
        addOption(accountOption);

        Option changePasswordOption = Option.builder()
                .longOpt(AnticOption.CHANGE_PASSWORD.toString()).desc(CliMessages.get("ChangeWalletPassword")).build();
        addOption(changePasswordOption);

        Option dumpPrivateKeyOption = Option.builder()
                .longOpt(AnticOption.DUMP_PRIVATE_KEY.toString())
                .desc(CliMessages.get("PrintHexKey"))
                .hasArg(true).optionalArg(false).argName("address").type(String.class)
                .build();
        addOption(dumpPrivateKeyOption);

        Option importPrivateKeyOption = Option.builder()
                .longOpt(AnticOption.IMPORT_PRIVATE_KEY.toString())
                .desc(CliMessages.get("ImportHexKey"))
                .hasArg(true).optionalArg(false).argName("key").type(String.class)
                .build();
        addOption(importPrivateKeyOption);

        Option reindexOption = Option.builder()
                .longOpt(AnticOption.REINDEX.toString())
                .desc(CliMessages.get("ReindexDescription"))
                .hasArg(true).optionalArg(true).argName("to").type(String.class)
                .build();
        addOption(reindexOption);
    }

    public void start(String[] args) throws ParseException, IOException {
        // parse common options
        CommandLine cmd = parseOptions(args);

        // parse remaining options
        if (cmd.hasOption(AnticOption.HELP.toString())) {
            printHelp();

        } else if (cmd.hasOption(AnticOption.VERSION.toString())) {
            printVersion();

        } else if (cmd.hasOption(AnticOption.ACCOUNT.toString())) {
            String action = cmd.getOptionValue(AnticOption.ACCOUNT.toString()).trim();
            if ("create".equals(action)) {
                createAccount();
            } else if ("list".equals(action)) {
                listAccounts();
            }

        } else if (cmd.hasOption(AnticOption.CHANGE_PASSWORD.toString())) {
            changePassword();

        } else if (cmd.hasOption(AnticOption.DUMP_PRIVATE_KEY.toString())) {
            dumpPrivateKey(cmd.getOptionValue(AnticOption.DUMP_PRIVATE_KEY.toString()).trim());

        } else if (cmd.hasOption(AnticOption.IMPORT_PRIVATE_KEY.toString())) {
            importPrivateKey(cmd.getOptionValue(AnticOption.IMPORT_PRIVATE_KEY.toString()).trim());

        } else if (cmd.hasOption(AnticOption.REINDEX.toString())) {
            reindex(cmd.getOptionValue(AnticOption.REINDEX.toString()));

        } else {
            start();
        }
    }

    protected void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.setWidth(200);
        formatter.printHelp("./antic-cli.sh [options]", getOptions());
    }

    protected void printVersion() {
        System.out.println(Constants.CLIENT_VERSION);
    }

    protected void reindex(String to) {
        Config config = getConfig();
        DatabaseFactory dbFactory = new LeveldbDatabase.LeveldbFactory(config.databaseDir());
        BlockchainImpl.upgrade(config, dbFactory, to == null ? Long.MAX_VALUE : Long.parseLong(to));
    }

    protected void start() throws IOException {
        // create/unlock wallet
        Wallet wallet = loadWallet().exists() ? loadAndUnlockWallet() : createNewWallet();
        if (wallet == null) {
            return;
        }

        // check file permissions
        if (SystemUtil.isPosix()) {
            if (!wallet.isPosixPermissionSecured()) {
                logger.warn(CliMessages.get("WarningWalletPosixPermission"));
            }
        }

        // check time drift
        long timeDrift = TimeUtil.getTimeOffsetFromNtp();
        if (Math.abs(timeDrift) > 5000L) {
            logger.warn(CliMessages.get("SystemTimeDrift"));
        }

        // in case HD wallet is enabled, make sure the seed is properly initialized.
        if (isHdWalletEnabled().orElse(ENABLE_HD_WALLET_BY_DEFAULT)) {
            if (!wallet.isHdWalletInitialized()) {
                initializedHdSeed(wallet);
            }
        }

        // create a new account if the wallet is empty
        List<Key> accounts = wallet.getAccounts();
        if (accounts.isEmpty()) {
            Key key;
            if (isHdWalletEnabled().orElse(ENABLE_HD_WALLET_BY_DEFAULT)) {
                key = wallet.addAccountWithNextHdKey();
            } else {
                key = wallet.addAccountRandom();
            }
            wallet.flush();

            accounts = wallet.getAccounts();
            logger.info(CliMessages.get("NewAccountCreatedForAddress", key.toAddressString()));
        }

        // check coinbase if the user specifies one
        int coinbase = getCoinbase() == null ? 0 : getCoinbase();
        if (coinbase < 0 || coinbase >= accounts.size()) {
            logger.error(CliMessages.get("CoinbaseDoesNotExist"));
            exit(SystemUtil.Code.ACCOUNT_NOT_EXIST);
            return;
        }

        // start kernel
        try {
            startKernel(getConfig(), wallet, wallet.getAccount(coinbase));
        } catch (Exception e) {
            logger.error("Uncaught exception during kernel startup.", e);
            exit(SystemUtil.Code.FAILED_TO_LAUNCH_KERNEL);
        }
    }

    /**
     * Starts the kernel.
     */
    protected Kernel startKernel(Config config, Wallet wallet, Key coinbase) {
        Kernel kernel = new Kernel(config, Genesis.load(config.network()), wallet, coinbase);
        kernel.start();

        return kernel;
    }

    protected void createAccount() {
        Wallet wallet = loadAndUnlockWallet();

        Key key;
        if (isHdWalletEnabled().orElse(ENABLE_HD_WALLET_BY_DEFAULT)) {
            key = wallet.addAccountWithNextHdKey();
        } else {
            key = wallet.addAccountRandom();
        }

        if (wallet.flush()) {
            logger.info(CliMessages.get("NewAccountCreatedForAddress", key.toAddressString()));
            logger.info(CliMessages.get("PublicKey", Hex.encode(key.getPublicKey())));
        }
    }

    protected void listAccounts() {
        Wallet wallet = loadAndUnlockWallet();

        List<Key> accounts = wallet.getAccounts();

        if (accounts.isEmpty()) {
            logger.info(CliMessages.get("AccountMissing"));
        } else {
            for (int i = 0; i < accounts.size(); i++) {
                logger.info(CliMessages.get("ListAccountItem", i, accounts.get(i).toString()));
            }
        }
    }

    protected void changePassword() {
        Wallet wallet = loadAndUnlockWallet();

        try {
            String newPassword = readNewPassword("EnterNewPassword", "ReEnterNewPassword");
            if (newPassword == null) {
                return;
            }

            wallet.changePassword(newPassword);
            boolean isFlushed = wallet.flush();
            if (!isFlushed) {
                logger.error(CliMessages.get("WalletFileCannotBeUpdated"));
                exit(SystemUtil.Code.FAILED_TO_WRITE_WALLET_FILE);
                return;
            }

            logger.info(CliMessages.get("PasswordChangedSuccessfully"));
        } catch (WalletLockedException exception) {
            logger.error(exception.getMessage());
        }
    }

    protected void exit(int code) {
        SystemUtil.exit(code);
    }

    protected String readPassword() {
        return ConsoleUtil.readPassword();
    }

    protected String readPassword(String prompt) {
        return ConsoleUtil.readPassword(prompt);
    }

    /**
     * Read a new password from input and require confirmation
     *
     * @return new password, or null if the confirmation failed
     */
    protected String readNewPassword(String newPasswordMessageKey, String reEnterNewPasswordMessageKey) {
        String newPassword = readPassword(CliMessages.get(newPasswordMessageKey));
        String newPasswordRe = readPassword(CliMessages.get(reEnterNewPasswordMessageKey));

        if (!newPassword.equals(newPasswordRe)) {
            logger.error(CliMessages.get("ReEnterNewPasswordIncorrect"));
            exit(SystemUtil.Code.PASSWORD_REPEAT_NOT_MATCH);
            return null;
        }

        return newPassword;
    }

    protected void dumpPrivateKey(String address) {
        Wallet wallet = loadAndUnlockWallet();

        byte[] addressBytes = Hex.decode0x(address);
        Key account = wallet.getAccount(addressBytes);
        if (account == null) {
            logger.error(CliMessages.get("AddressNotInWallet"));
            exit(SystemUtil.Code.ACCOUNT_NOT_EXIST);
        } else {
            System.out.println(CliMessages.get("PrivateKeyIs", Hex.encode(account.getPrivateKey())));
        }
    }

    protected void importPrivateKey(String key) {
        try {
            Wallet wallet = loadAndUnlockWallet();
            byte[] keyBytes = Hex.decode0x(key);
            Key account = new Key(keyBytes);

            boolean accountAdded = wallet.addAccount(account);
            if (!accountAdded) {
                logger.error(CliMessages.get("PrivateKeyAlreadyInWallet"));
                exit(SystemUtil.Code.ACCOUNT_ALREADY_EXISTS);
                return;
            }

            boolean walletFlushed = wallet.flush();
            if (!walletFlushed) {
                logger.error(CliMessages.get("WalletFileCannotBeUpdated"));
                exit(SystemUtil.Code.FAILED_TO_WRITE_WALLET_FILE);
                return;
            }

            logger.info(CliMessages.get("PrivateKeyImportedSuccessfully"));
            logger.info(CliMessages.get("Address", account.toAddressString()));
            logger.info(CliMessages.get("PublicKey", Hex.encode(account.getPublicKey())));
        } catch (InvalidKeySpecException exception) {
            logger.error(CliMessages.get("PrivateKeyCannotBeDecoded", exception.getMessage()));
            exit(SystemUtil.Code.INVALID_PRIVATE_KEY);
        } catch (WalletLockedException exception) {
            logger.error(exception.getMessage());
            exit(SystemUtil.Code.WALLET_LOCKED);
        }
    }

    protected Wallet loadAndUnlockWallet() {

        Wallet wallet = loadWallet();
        if (getPassword() == null) {
            if (wallet.unlock("")) {
                setPassword("");
            } else {
                setPassword(readPassword());
            }
        }

        if (!wallet.unlock(getPassword())) {
            logger.error("Invalid password");
            exit(SystemUtil.Code.FAILED_TO_UNLOCK_WALLET);
        }

        return wallet;
    }

    /**
     * Create a new wallet with a new password from input and save the wallet file
     * to disk
     *
     * @return created new wallet, or null if it failed to create the wallet
     */
    protected Wallet createNewWallet() {
        String newPassword = readNewPassword("EnterNewPassword", "ReEnterNewPassword");
        if (newPassword == null) {
            return null;
        }

        setPassword(newPassword);
        Wallet wallet = loadWallet();
        if (!wallet.unlock(newPassword) || !wallet.flush()) {
            logger.error("CreateNewWalletError");
            exit(SystemUtil.Code.FAILED_TO_WRITE_WALLET_FILE);
            return null;
        }

        return wallet;
    }

    protected Wallet loadWallet() {
        return new Wallet(new File(getDataDir(), "wallet.data"), getConfig().network());
    }

    protected void initializedHdSeed(Wallet wallet) {
        if (wallet.isUnlocked() && !wallet.isHdWalletInitialized()) {
            // HD Mnemonic
            System.out.println(CliMessages.get("HdWalletInitialize"));
            MnemonicGenerator generator = new MnemonicGenerator();
            String phrase = generator.getWordlist(Wallet.MNEMONIC_ENTROPY_LENGTH, Wallet.MNEMONIC_LANGUAGE);
            System.out.println(CliMessages.get("HdWalletMnemonic", phrase));

            String repeat = ConsoleUtil.readLine(CliMessages.get("HdWalletMnemonicRepeat"));
            repeat = String.join(" ", repeat.trim().split("\\s+"));

            if (!repeat.equals(phrase)) {
                logger.error(CliMessages.get("HdWalletInitializationFailure"));
                SystemUtil.exit(SystemUtil.Code.FAILED_TO_INIT_HD_WALLET);
                return;
            }

            wallet.initializeHdWallet(phrase);
            wallet.flush();
            logger.info(CliMessages.get("HdWalletInitializationSuccess"));
        }
    }
}
