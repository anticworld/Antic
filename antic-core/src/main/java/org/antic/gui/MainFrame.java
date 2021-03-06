/**
 * Copyright (c) 2019 The Antic Developers
 *
 * Distributed under the MIT software license, see the accompanying file
 * LICENSE or https://opensource.org/licenses/mit-license.php
 */
package org.antic.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import org.antic.Kernel;
import org.antic.core.Wallet;
import org.antic.gui.dialog.InputDialog;
import org.antic.gui.model.WalletModel;
import org.antic.gui.panel.*;
import org.antic.message.GuiMessages;
import org.antic.util.exception.UnreachableException;

public class MainFrame extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;

    private final transient Kernel kernel;
    private final transient WalletModel model;

    private final LockGlassPane lockGlassPane;

    private final HomePanel panelHome;
    private final SendPanel panelSend;
    private final ReceivePanel panelReceive;
    private final ContractPanel panelContract;
    private final TransactionsPanel panelTransactions;
    private final DelegatesPanel panelDelegates;
    private final StatusBar statusBar;

    private final JButton btnHome;
    private final JButton btnSend;
    private final JButton btnReceive;
    private final JButton btnContract;
    private final JButton btnTransactions;
    private final JButton btnDelegates;
    private final JButton btnLock;

    private final JPanel activePanel;

    private JButton activeButton;

    public MainFrame(AnticGui gui) {
        // ensure that all windows are released before it starts closing the Kernel
        this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // stop GUI threads
                gui.stop();

                // destroy all frames
                for (Frame frame : Frame.getFrames()) {
                    frame.setVisible(false);
                    frame.dispose();
                }

                // trigger the shutdown-hook of Kernel class then exits the process
                System.exit(0);
            }
        });
        this.model = gui.getModel();
        this.model.addListener(this);

        this.kernel = gui.getKernel();

        lockGlassPane = new LockGlassPane();
        lockGlassPane.setOpaque(false);
        this.setGlassPane(lockGlassPane);

        panelHome = new HomePanel(gui);
        panelSend = new SendPanel(gui, this);
        panelReceive = new ReceivePanel(gui);
        panelContract = new ContractPanel(gui, this);
        panelTransactions = new TransactionsPanel(gui, this);
        panelDelegates = new DelegatesPanel(gui, this);

        // setup frame properties
        this.setTitle(GuiMessages.get("AnticWallet"));
        this.setIconImage(SwingUtil.loadImage("logo", 128, 128).getImage());
        this.setMinimumSize(new Dimension(1100, 700));
        SwingUtil.alignFrameToMiddle(this, 1100, 700);

        // setup menu bar
        JMenuBar menuBar = new MenuBar(gui, this);
        this.setJMenuBar(menuBar);

        // setup tool bar
        JPanel toolBar = new JPanel();
        BorderLayout leftBoxLayout = new BorderLayout();
        toolBar.setLayout(leftBoxLayout);

        //add left tool
        JPanel leftPanel = new JPanel();
        BoxLayout leftLayout = new BoxLayout(leftPanel,BoxLayout.Y_AXIS);
        leftPanel.setLayout(leftLayout);
        toolBar.add(leftPanel,BorderLayout.WEST);

        //add right line
        JPanel rightLinePanel = new JPanel(new BorderLayout());
        rightLinePanel.add(Box.createRigidArea(new Dimension(20, this.getHeight())),BorderLayout.WEST);
        JSeparator sep = new JSeparator(SwingConstants.VERTICAL);
        sep.setPreferredSize(new Dimension(1,this.getHeight()));
        sep.setBackground(new Color(153,153,153));
        rightLinePanel.add(sep);

        toolBar.add(rightLinePanel,BorderLayout.EAST);

       /* layout.setVgap(0);
        layout.setHgap(0);
        layout.setAlignment(FlowLayout.LEADING);*/

        toolBar.setBorder(new EmptyBorder(15, 15, 15, 15));

        Dimension gap = new Dimension(0, 15);

        btnHome = createButton(GuiMessages.get("Home"), "home", Action.SHOW_HOME);
        btnHome.setMnemonic(KeyEvent.VK_H);
        leftPanel.add(btnHome);
        leftPanel.add(Box.createRigidArea(gap));

        btnReceive = createButton(GuiMessages.get("Receive"), "receive", Action.SHOW_RECEIVE);
        btnReceive.setMnemonic(KeyEvent.VK_R);
        leftPanel.add(btnReceive);
        leftPanel.add(Box.createRigidArea(gap));

        btnSend = createButton(GuiMessages.get("Send"), "send", Action.SHOW_SEND);
        btnSend.setMnemonic(KeyEvent.VK_S);
        leftPanel.add(btnSend);
        leftPanel.add(Box.createRigidArea(gap));

        btnContract = createButton(GuiMessages.get("Contract"), "contract", Action.SHOW_CONTRACT);
        btnContract.setMnemonic(KeyEvent.VK_C);
        leftPanel.add(btnContract);
        leftPanel.add(Box.createRigidArea(gap));


        btnTransactions = createButton(GuiMessages.get("Transactions"), "transactions", Action.SHOW_TRANSACTIONS);
        btnTransactions.setMnemonic(KeyEvent.VK_T);
        leftPanel.add(btnTransactions);
        leftPanel.add(Box.createRigidArea(gap));

        btnDelegates = createButton(GuiMessages.get("Delegates"), "delegates", Action.SHOW_DELEGATES);
        btnDelegates.setMnemonic(KeyEvent.VK_D);
        leftPanel.add(btnDelegates);
        leftPanel.add(Box.createRigidArea(gap));

        btnLock = createButton(GuiMessages.get("Lock"), "lock", Action.LOCK);
        btnLock.setMnemonic(KeyEvent.VK_L);
        leftPanel.add(btnLock);

        // setup tabs
        activePanel = new JPanel();
        activePanel.setBorder(new EmptyBorder(0, 15, 15, 15));
        activePanel.setLayout(new BorderLayout(0, 0));

        getContentPane().add(toolBar, BorderLayout.WEST);
        getContentPane().add(activePanel, BorderLayout.CENTER);

        // show the first tab
        activePanel.add(panelHome);
        select(panelHome, btnHome);

        // add status bar
        statusBar = new StatusBar(this);
        statusBar.setPeersNumber(model.getActivePeers().size());
        model.getSyncProgress().ifPresent(statusBar::setProgress);
        this.add(statusBar, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Action action = Action.valueOf(e.getActionCommand());

        switch (action) {
        case SHOW_HOME:
            select(panelHome, btnHome);
            break;
        case SHOW_SEND:
            select(panelSend, btnSend);
            break;
        case SHOW_RECEIVE:
            select(panelReceive, btnReceive);
            break;
        case SHOW_CONTRACT:
            select(panelContract, btnContract);
            break;
        case SHOW_TRANSACTIONS:
            select(panelTransactions, btnTransactions);
            break;
        case SHOW_DELEGATES:
            select(panelDelegates, btnDelegates);
            break;
        case LOCK:
            lock();
            break;
        case REFRESH:
            refresh();
            break;
        default:
            throw new UnreachableException();
        }
    }

    /**
     * Locks the wallet.
     */
    protected void lock() {
        Wallet w = kernel.getWallet();
        w.lock();

        lockGlassPane.setVisible(true);
        model.fireLockEvent();
        btnLock.setText(GuiMessages.get("Unlock"));
    }

    /**
     * Tries to unlock the wallet with the given password.
     */
    protected boolean unlock(String password) {
        Wallet w = kernel.getWallet();

        if (password != null && w.unlock(password)) {
            lockGlassPane.setVisible(false);
            btnLock.setText(GuiMessages.get("Lock"));
            return true;
        }

        return false;
    }

    /**
     * Event listener of ${@link Action#REFRESH}.
     */
    protected void refresh() {
        if (null == statusBar) {
            return;
        }
        // update status bar
        statusBar.setPeersNumber(model.getActivePeers().size());
        model.getSyncProgress().ifPresent(statusBar::setProgress);
    }

    private static final Border BORDER_NORMAL = new CompoundBorder(new LineBorder(new Color(180, 180, 180)),
            new EmptyBorder(0, 5, 0, 10));
    private static final Border BORDER_FOCUS = new CompoundBorder(new LineBorder(new Color(51, 153, 255)),
            new EmptyBorder(0, 5, 0, 10));

    /**
     * Selects an tabbed panel to display.
     *
     * @param panel
     * @param button
     */
    protected void select(JPanel panel, JButton button) {
        if (activeButton != null) {
            activeButton.setBorder(BORDER_NORMAL);
        }
        activeButton = button;
        activeButton.setBorder(BORDER_FOCUS);

        activePanel.removeAll();
        activePanel.add(panel);

        activePanel.revalidate();
        activePanel.repaint();
    }

    /**
     * Creates a button in the tool bar.
     *
     * @param name
     * @param icon
     * @param action
     * @return
     */
    protected JButton createButton(String name, String icon, Action action) {
        JButton btn = new JButton(name);
        btn.setActionCommand(action.name());
        btn.addActionListener(this);
        btn.setIcon(SwingUtil.loadImage(icon, 36, 36));
        btn.setFocusPainted(false);
        btn.setBorder(BORDER_NORMAL);
        btn.setContentAreaFilled(false);
        btn.setFont(btn.getFont().deriveFont(btn.getFont().getStyle() | Font.BOLD));

        Dimension preferredSize = new Dimension(130,60);//设置尺寸
        btn.setMaximumSize(preferredSize);
        btn.setPreferredSize(preferredSize );
        return btn;
    }

    /**
     * A gray overlay which shows on top of the GUI to prevent user actions.
     */
    protected class LockGlassPane extends JPanel {

        private static final long serialVersionUID = 1L;

        public LockGlassPane() {
            this.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    String pwd = new InputDialog(MainFrame.this, GuiMessages.get("EnterPassword") + ":", true)
                            .showAndGet();

                    if (pwd != null && !unlock(pwd)) {
                        JOptionPane.showMessageDialog(MainFrame.this, GuiMessages.get("IncorrectPassword"));
                    }
                }
            });
            this.addKeyListener(new KeyAdapter() {
                // eats all key events
            });
        }

        @Override
        public void paintComponent(Graphics g) {
            g.setColor(new Color(0, 0, 0, 96));
            g.fillRect(0, 0, this.getWidth(), this.getHeight());
        }
    }
}
