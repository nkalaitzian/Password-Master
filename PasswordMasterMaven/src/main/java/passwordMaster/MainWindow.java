/*
 * 	https://github.com/nikoskalai/Password-Master
 *
 * 	Copyright (c) 2018 Nikos Kalaitzian
 * 	Licensed under the WTFPL
 * 	You may obtain a copy of the License at
 *
 * 	http://www.wtfpl.net/about/
 *
 * 	Unless required by applicable law or agreed to in writing, software
 * 	distributed under the License is distributed on an "AS IS" BASIS,
 * 	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
package passwordMaster;

import Other.Win32IdleTime;
import Other.AES;
import Other.FileManagement;
import Other.History;
import Other.Settings;
import Other.RXTable;
import Other.MyTableCellEditor;
import Other.Login;
import java.awt.AWTException;
import java.awt.Desktop;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Nikos
 */
public class MainWindow extends javax.swing.JFrame {

    /**
     * These integer fields hold the cell that is selected when the user left or
     * right clicks in the JTable.
     */
    private int row, col;
    private DefaultTableModel model = null;

    /**
     * The file that the logins will be saved to.
     */
    private File file = null;
    /**
     * The encryption key that will be used to encrypt the file.
     *
     * @see <a href="MainWindow#file">File</a>
     */
    private String encryptionKey = null;

    private ArrayList<Login> loginList;

    private History history;

    boolean showHidden = false;
    boolean fileNotSaved = false;

    /**
     * The ExitWindow variable.
     */
    public ExitWindow ew;
    private SettingsWindow sw;
    private PasswordGenerator pg;

    private AES cipher;

    public static boolean idleTimer = false;

    private static final Logger LOG = Logger.getLogger(MainWindow.class.getName());

    /**
     * Creates new form MainWindow
     */
    public MainWindow() {
        initComponents();
        initSettings();
        initSystemTray();
        addListeners();
        new TryToOpenFiles().start();
    }

    public static TrayIcon trayIcon;
    public static boolean trayIconSetupSuccessful = false;

    private void initSystemTray() {
        if (!SystemTray.isSupported()) {
            trayIconSetupSuccessful = false;
            return;
        }
        trayIcon = new TrayIcon(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("ic_launcher.png")));
        trayIcon.setImageAutoSize(true);
        trayIcon.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleVisibility();
            }
        });
        PopupMenu popup = initPopupMenu();
        trayIcon.setPopupMenu(popup);
        final SystemTray tray = SystemTray.getSystemTray();
        try {
            tray.add(trayIcon);
            trayIconSetupSuccessful = true;
        } catch (AWTException e) {
            showStatus("Could not initialize tray icon.");
            trayIconSetupSuccessful = false;
        }
    }

    private static MenuItem toggleMWVisibilityMenuItem;

    private PopupMenu initPopupMenu() {
        PopupMenu popupMenu = new PopupMenu();
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exitApp();
            }
        });
        toggleMWVisibilityMenuItem = new MenuItem("Hide " + Settings.APP_NAME);
        toggleMWVisibilityMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleVisibility();
            }
        });
        MenuItem openPasswordGeneratorMenuItem = new MenuItem("Open Password Generator");
        openPasswordGeneratorMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopTableEditing();
                pg.showWindow(true);
            }
        });
        popupMenu.add(toggleMWVisibilityMenuItem);
        popupMenu.addSeparator();
        popupMenu.add(openPasswordGeneratorMenuItem);
        popupMenu.addSeparator();
        popupMenu.add(exitItem);
        return popupMenu;
    }

    private class TryToOpenFiles extends Thread {

        @Override
        public void run() {
            File parent;
            parent = new File(Settings.getDirectory());
            ArrayList<String> files = new ArrayList();
            int i = 0;
            for (File f : parent.listFiles()) {
                if (f.getPath().endsWith(".pmaster")) {
                    i++;
                    files.add(f.getPath());
                }
            }
            if (i > 0) {
                PasswordFrame pf = new PasswordFrame(files, pg);
                while (true) {
                    if (pf.done) {
                        break;
                    } else {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException ex) {
                            MainWindow.showError(ex, "Could not pause running thread.");
                        }
                    }
                }
                if (pf.cancel) {
                    fileNotSaved = false;
                    return;
                }
                encryptionKey = pf.getPassword();
                file = new File(pf.getFile());
                readFile();
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        popupMenu = new javax.swing.JPopupMenu();
        addLoginMenuItem1 = new javax.swing.JMenuItem();
        deleteLoginMenuItem1 = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        moveUpMenuItem = new javax.swing.JMenuItem();
        moveDownMenuItem = new javax.swing.JMenuItem();
        jSeparator11 = new javax.swing.JPopupMenu.Separator();
        undoMenuItem1 = new javax.swing.JMenuItem();
        redoMenuItem1 = new javax.swing.JMenuItem();
        jSeparator8 = new javax.swing.JPopupMenu.Separator();
        openLinkMenuItem1 = new javax.swing.JMenuItem();
        jSeparator10 = new javax.swing.JPopupMenu.Separator();
        copyWebsiteMenuItem = new javax.swing.JMenuItem();
        copyUsernameMenuItem = new javax.swing.JMenuItem();
        copyPasswordMenuItem = new javax.swing.JMenuItem();
        jSeparator12 = new javax.swing.JPopupMenu.Separator();
        pasteMenuItem = new javax.swing.JMenuItem();
        loginPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        addLoginButton = new javax.swing.JButton();
        deleteLoginButton = new javax.swing.JButton();
        idleLabel = new javax.swing.JLabel();
        moveUpButton = new javax.swing.JButton();
        moveDownButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        loginTable = new RXTable();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        openFileMenuItem = new javax.swing.JMenuItem();
        newFileMenuItem = new javax.swing.JMenuItem();
        jSeparator6 = new javax.swing.JPopupMenu.Separator();
        importMenu = new javax.swing.JMenu();
        firefoxImportMenuItem = new javax.swing.JMenuItem();
        chromeImportMenuItem = new javax.swing.JMenuItem();
        safariImportMenuItem = new javax.swing.JMenuItem();
        operaImportMenuItem = new javax.swing.JMenuItem();
        ieImportMenuItem = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        saveMenuItem = new javax.swing.JMenuItem();
        saveAsMenuItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        exitMenuItem = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();
        addLoginMenuItem = new javax.swing.JMenuItem();
        deleteLoginMenuItem = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        undoMenuItem = new javax.swing.JMenuItem();
        redoMenuItem = new javax.swing.JMenuItem();
        jSeparator7 = new javax.swing.JPopupMenu.Separator();
        openLinkMenuItem = new javax.swing.JMenuItem();
        jSeparator9 = new javax.swing.JPopupMenu.Separator();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        viewMenu = new javax.swing.JMenu();
        settingsMenuItem = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        showPasswordsMenuItem = new javax.swing.JCheckBoxMenuItem();
        helpMenu = new javax.swing.JMenu();
        generatePasswordMenuItem = new javax.swing.JMenuItem();

        addLoginMenuItem1.setAction(addLoginAction);
        addLoginMenuItem1.setText("Add Login.");
        popupMenu.add(addLoginMenuItem1);

        deleteLoginMenuItem1.setAction(deleteLoginAction);
        deleteLoginMenuItem1.setText("Delete Login.");
        popupMenu.add(deleteLoginMenuItem1);
        popupMenu.add(jSeparator5);

        moveUpMenuItem.setAction(moveUpAction);
        moveUpMenuItem.setText("Move Up");
        popupMenu.add(moveUpMenuItem);

        moveDownMenuItem.setAction(moveDownAction);
        moveDownMenuItem.setText("Move Down");
        popupMenu.add(moveDownMenuItem);
        popupMenu.add(jSeparator11);

        undoMenuItem1.setAction(undoAction);
        undoMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, java.awt.event.InputEvent.CTRL_MASK));
        undoMenuItem1.setText("Undo");
        popupMenu.add(undoMenuItem1);

        redoMenuItem1.setAction(redoAction);
        redoMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Y, java.awt.event.InputEvent.CTRL_MASK));
        redoMenuItem1.setText("Redo");
        popupMenu.add(redoMenuItem1);
        popupMenu.add(jSeparator8);

        openLinkMenuItem1.setAction(openLinkAction);
        openLinkMenuItem1.setText("Open website in browser.");
        popupMenu.add(openLinkMenuItem1);
        popupMenu.add(jSeparator10);

        copyWebsiteMenuItem.setAction(copyWebsiteAction);
        copyWebsiteMenuItem.setText("Copy Website");
        popupMenu.add(copyWebsiteMenuItem);

        copyUsernameMenuItem.setAction(copyUsernameAction);
        copyUsernameMenuItem.setText("Copy Username");
        popupMenu.add(copyUsernameMenuItem);

        copyPasswordMenuItem.setAction(copyPasswordAction);
        copyPasswordMenuItem.setText("Copy Password");
        popupMenu.add(copyPasswordMenuItem);
        popupMenu.add(jSeparator12);

        pasteMenuItem.setAction(pasteAction);
        pasteMenuItem.setText("Paste");
        popupMenu.add(pasteMenuItem);

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

        addLoginButton.setAction(addLoginAction);
        addLoginButton.setText("+");
        addLoginButton.setToolTipText("Add Login");

        deleteLoginButton.setAction(deleteLoginAction);
        deleteLoginButton.setText("-");
        deleteLoginButton.setToolTipText("Delete Login");

        idleLabel.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        idleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        idleLabel.setText("Idle for:");
        idleLabel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        idleLabel.setEnabled(false);

        moveUpButton.setText("Move Up");
        moveUpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moveUpButtonActionPerformed(evt);
            }
        });

        moveDownButton.setText("Move Down");
        moveDownButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moveDownButtonActionPerformed(evt);
            }
        });

        jScrollPane1.setComponentPopupMenu(popupMenu);

        loginTable.setAutoCreateRowSorter(true);
        loginTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "Name/Website", "Username", "Password", "Other"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        loginTable.setCellEditor(new MyTableCellEditor());
        loginTable.setColumnSelectionAllowed(true);
        loginTable.setComponentPopupMenu(popupMenu);
        jScrollPane1.setViewportView(loginTable);
        loginTable.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        if (loginTable.getColumnModel().getColumnCount() > 0) {
            loginTable.getColumnModel().getColumn(0).setPreferredWidth(30);
            loginTable.getColumnModel().getColumn(0).setMaxWidth(70);
        }

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(moveUpButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(moveDownButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addLoginButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(deleteLoginButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(idleLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 778, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(idleLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(deleteLoginButton)
                    .addComponent(addLoginButton)
                    .addComponent(moveDownButton)
                    .addComponent(moveUpButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1))
        );

        javax.swing.GroupLayout loginPanelLayout = new javax.swing.GroupLayout(loginPanel);
        loginPanel.setLayout(loginPanelLayout);
        loginPanelLayout.setHorizontalGroup(
            loginPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        loginPanelLayout.setVerticalGroup(
            loginPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        fileMenu.setText("File");

        openFileMenuItem.setAction(openAction);
        openFileMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        openFileMenuItem.setText("Open");
        fileMenu.add(openFileMenuItem);

        newFileMenuItem.setAction(newAction);
        newFileMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        newFileMenuItem.setText("New");
        fileMenu.add(newFileMenuItem);
        fileMenu.add(jSeparator6);

        importMenu.setText("Import Logins");

        firefoxImportMenuItem.setAction(firefoxImportAction);
        firefoxImportMenuItem.setText("From Mozilla Firefox");
        firefoxImportMenuItem.setEnabled(false);
        importMenu.add(firefoxImportMenuItem);

        chromeImportMenuItem.setText("From Google Chrome");
        chromeImportMenuItem.setEnabled(false);
        importMenu.add(chromeImportMenuItem);

        safariImportMenuItem.setText("From Safari");
        safariImportMenuItem.setEnabled(false);
        importMenu.add(safariImportMenuItem);

        operaImportMenuItem.setText("From Opera");
        operaImportMenuItem.setEnabled(false);
        importMenu.add(operaImportMenuItem);

        ieImportMenuItem.setText("From Internet Explorer");
        ieImportMenuItem.setEnabled(false);
        importMenu.add(ieImportMenuItem);

        fileMenu.add(importMenu);
        fileMenu.add(jSeparator3);

        saveMenuItem.setAction(saveAction);
        saveMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        saveMenuItem.setText("Save");
        fileMenu.add(saveMenuItem);

        saveAsMenuItem.setAction(saveAsAction);
        saveAsMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        saveAsMenuItem.setText("Save As");
        fileMenu.add(saveAsMenuItem);
        fileMenu.add(jSeparator1);

        exitMenuItem.setAction(exitAction);
        exitMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.ALT_MASK));
        exitMenuItem.setText("Exit");
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        editMenu.setText("Edit");
        editMenu.setToolTipText("Only if passwords are shown.");

        addLoginMenuItem.setAction(addLoginAction);
        addLoginMenuItem.setText("Add Login.");
        editMenu.add(addLoginMenuItem);

        deleteLoginMenuItem.setAction(deleteLoginAction);
        deleteLoginMenuItem.setText("Delete Login.");
        editMenu.add(deleteLoginMenuItem);
        editMenu.add(jSeparator4);

        undoMenuItem.setAction(undoAction);
        undoMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, java.awt.event.InputEvent.CTRL_MASK));
        undoMenuItem.setText("Undo");
        editMenu.add(undoMenuItem);

        redoMenuItem.setAction(redoAction);
        redoMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Y, java.awt.event.InputEvent.CTRL_MASK));
        redoMenuItem.setText("Redo");
        editMenu.add(redoMenuItem);
        editMenu.add(jSeparator7);

        openLinkMenuItem.setAction(openLinkAction);
        openLinkMenuItem.setText("Open website in browser.");
        editMenu.add(openLinkMenuItem);
        editMenu.add(jSeparator9);

        jMenuItem1.setAction(copyWebsiteAction);
        jMenuItem1.setText("Copy Website");
        editMenu.add(jMenuItem1);

        jMenuItem2.setAction(copyUsernameAction);
        jMenuItem2.setText("Copy Username");
        editMenu.add(jMenuItem2);

        jMenuItem3.setAction(copyPasswordAction);
        jMenuItem3.setText("Copy Password");
        editMenu.add(jMenuItem3);

        menuBar.add(editMenu);

        viewMenu.setText("View");

        settingsMenuItem.setAction(settingsAction);
        settingsMenuItem.setText("Settings");
        viewMenu.add(settingsMenuItem);
        viewMenu.add(jSeparator2);

        showPasswordsMenuItem.setAction(showPasswordsAction);
        showPasswordsMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_H, java.awt.event.InputEvent.CTRL_MASK));
        showPasswordsMenuItem.setText("Hide Information");
        viewMenu.add(showPasswordsMenuItem);

        menuBar.add(viewMenu);

        helpMenu.setText("Help");

        generatePasswordMenuItem.setAction(generatePasswordAction);
        generatePasswordMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        generatePasswordMenuItem.setText("Generate Password");
        helpMenu.add(generatePasswordMenuItem);

        menuBar.add(helpMenu);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(loginPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(loginPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void moveUpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moveUpButtonActionPerformed
        moveUp();
    }//GEN-LAST:event_moveUpButtonActionPerformed

    private void moveDownButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moveDownButtonActionPerformed
        moveDown();
    }//GEN-LAST:event_moveDownButtonActionPerformed

    static MainWindow mw;

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            MainWindow.showError(ex, "Could not initialize theme.");
        }
        java.awt.EventQueue.invokeLater(() -> {
            mw = new MainWindow();
            mw.setVisible(true);
            if (Settings.minimizeToSystemTray && Settings.startPMMinimized && trayIconSetupSuccessful) {
                toggleVisibility();
            }
        });
        if (trayIconSetupSuccessful) {
            if (mw.isVisible()) {
                toggleMWVisibilityMenuItem.setLabel("Hide " + Settings.APP_NAME);
            } else {
                toggleMWVisibilityMenuItem.setLabel("Show " + Settings.APP_NAME);
            }
        } else {
            if (toggleMWVisibilityMenuItem != null) {
                toggleMWVisibilityMenuItem.setEnabled(false);
            }
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addLoginButton;
    private javax.swing.JMenuItem addLoginMenuItem;
    private javax.swing.JMenuItem addLoginMenuItem1;
    private javax.swing.JMenuItem chromeImportMenuItem;
    private javax.swing.JMenuItem copyPasswordMenuItem;
    private javax.swing.JMenuItem copyUsernameMenuItem;
    private javax.swing.JMenuItem copyWebsiteMenuItem;
    private javax.swing.JButton deleteLoginButton;
    private javax.swing.JMenuItem deleteLoginMenuItem;
    private javax.swing.JMenuItem deleteLoginMenuItem1;
    private javax.swing.JMenu editMenu;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenuItem firefoxImportMenuItem;
    private javax.swing.JMenuItem generatePasswordMenuItem;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JLabel idleLabel;
    private javax.swing.JMenuItem ieImportMenuItem;
    private javax.swing.JMenu importMenu;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator10;
    private javax.swing.JPopupMenu.Separator jSeparator11;
    private javax.swing.JPopupMenu.Separator jSeparator12;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JPopupMenu.Separator jSeparator6;
    private javax.swing.JPopupMenu.Separator jSeparator7;
    private javax.swing.JPopupMenu.Separator jSeparator8;
    private javax.swing.JPopupMenu.Separator jSeparator9;
    private javax.swing.JPanel loginPanel;
    private javax.swing.JTable loginTable;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JButton moveDownButton;
    private javax.swing.JMenuItem moveDownMenuItem;
    private javax.swing.JButton moveUpButton;
    private javax.swing.JMenuItem moveUpMenuItem;
    private javax.swing.JMenuItem newFileMenuItem;
    private javax.swing.JMenuItem openFileMenuItem;
    private javax.swing.JMenuItem openLinkMenuItem;
    private javax.swing.JMenuItem openLinkMenuItem1;
    private javax.swing.JMenuItem operaImportMenuItem;
    private javax.swing.JMenuItem pasteMenuItem;
    private javax.swing.JPopupMenu popupMenu;
    private javax.swing.JMenuItem redoMenuItem;
    private javax.swing.JMenuItem redoMenuItem1;
    private javax.swing.JMenuItem safariImportMenuItem;
    private javax.swing.JMenuItem saveAsMenuItem;
    private javax.swing.JMenuItem saveMenuItem;
    private javax.swing.JMenuItem settingsMenuItem;
    private javax.swing.JCheckBoxMenuItem showPasswordsMenuItem;
    private javax.swing.JMenuItem undoMenuItem;
    private javax.swing.JMenuItem undoMenuItem1;
    private javax.swing.JMenu viewMenu;
    // End of variables declaration//GEN-END:variables

    private void initSettings() {
        if (getClass().getPackage().getImplementationVersion() != null) {
            Settings.APP_VERSION = getClass().getPackage().getImplementationVersion();
        }
        setTitle(Settings.APP_NAME + " v" + Settings.APP_VERSION);
        FileManagement.importSettingsFromFile();
        ew = new ExitWindow(MainWindow.this);
        pg = new PasswordGenerator();
        sw = new SettingsWindow(this);
        cipher = new AES();

        model = (DefaultTableModel) loginTable.getModel();

        loginList = new ArrayList();
        history = new History();
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("ic_launcher.png")));
    }

    private void addListeners() {
        WindowListener windowListener = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                ew.visible(true);
            }
        };
        addWindowListener(windowListener);
        ComponentListener componentListener = new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (getExtendedState() != JFrame.MAXIMIZED_BOTH) {
                    Settings.setUserSize(getSize());
                }
            }
        };
        WindowStateListener wsl = new WindowAdapter() {
            @Override
            public void windowStateChanged(WindowEvent e) {
                if (getExtendedState() == MainWindow.ICONIFIED) {
                    toggleVisibility();
                }
                Settings.setWindowState(getExtendedState());
            }
        };
        addWindowStateListener(wsl);
        addComponentListener(componentListener);
        loginTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                row = loginTable.rowAtPoint(evt.getPoint());
                col = loginTable.columnAtPoint(evt.getPoint());
            }

            @Override
            public void mousePressed(MouseEvent e) {
                Point mousePoint = e.getPoint();
                if (SwingUtilities.isLeftMouseButton(e)) {
                    row = loginTable.rowAtPoint(mousePoint);
                    col = loginTable.columnAtPoint(mousePoint);
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    row = loginTable.rowAtPoint(mousePoint);
                    col = loginTable.columnAtPoint(mousePoint);
                    loginTable.changeSelection(row, col, false, false);
                }
            }
        });

        model.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                updateList();
            }
        });
    }

    private void updateList() {
        if (bypassListChange) {
            return;
        }
        if (!showHidden) {
            updateTable();
            return;
        }
        fileNotSaved = true;
        int i = 0;
        ArrayList<Login> temp = loginList;
        loginList = new ArrayList();
        for (Login l : temp) {
            if (model.getRowCount() > 0) {
                Login t = new Login(new Integer(l.getId()), l.getWebsite(), l.getUsername(), l.getPassword(), l.getOther());
                t.setId((String) model.getValueAt(i, 0));
                t.setWebsite((String) model.getValueAt(i, 1));
                t.setUsername((String) model.getValueAt(i, 2));
                if (showHidden) {
                    t.setPassword((String) model.getValueAt(i, 3));
                }
                t.setOther((String) model.getValueAt(i, 4));
                loginList.add(t);
                i++;
            } else {
                break;
            }
        }
        loginList.sort(new LoginComparator());
        updateHistory();
    }

    /**
     * This method exits the program.
     */
    public void exitApp() {
        if (showHidden) {
            if (Settings.getWindowState() != JFrame.MAXIMIZED_BOTH) {
                Settings.setUserSize(getSize());
            }
            Settings.setWindowState(getExtendedState());
            FileManagement.saveSettingsToFile();
        }
        System.exit(0);
    }

    private final Action exitAction = new AbstractAction("") {
        @Override
        public void actionPerformed(ActionEvent ae) {
            stopTableEditing();
            exitApp();
        }
    };

    private final Action newAction = new AbstractAction("") {
        @Override
        public void actionPerformed(ActionEvent ae) {
            newFile();
        }
    };

    private void newFile() {
        if (fileNotSaved) {
            int result = JOptionPane.showConfirmDialog(null, "Do you want to save this file before making a new one?", "Warning! This file is unsaved.", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                saveFile();
            } else if (result == JOptionPane.CANCEL_OPTION) {
                clearFile();
            }
        } else {
            clearFile();
        }
    }

    private void clearFile() {
        model.setRowCount(0);
        fileNotSaved = false;
        file = null;
        encryptionKey = null;
        showPasswordsMenuItem.setSelected(false);
        showHidden = true;
        loginList = new ArrayList();
        updateTable();
        showPasswords();
    }

    private final Action openAction = new AbstractAction("") {
        @Override
        public void actionPerformed(ActionEvent ae) {
            openFile();
        }
    };

    private void openFile() {
        new OpenFile().start();
    }

    private class OpenFile extends Thread {

        @Override
        public void run() {
            newFile();
            if (getFile() && getPassword(true)) {
                model.setRowCount(0);
                loginList = new ArrayList();
                readFile();
            }
        }
    }

    private boolean bypassListChange = false;

    private void readFile() {
        if (file != null) {
            FileInputStream fis = null;
            byte[] result = new byte[(int) file.length()];
            try {
                fis = new FileInputStream(file);
                fis.read(result);
                String res = cipher.decrypt(result, encryptionKey).substring(16);
                fis.close();
                for (String l : res.split("--!--")) {
                    Login login = Login.fromString(l);
                    loginList.add(login);
                }
                updateHistory();
                updateTable();
                Settings.setDirectory(file.getParent());
                showPasswords();
                fileNotSaved = false;
            } catch (IOException | NullPointerException ex) {
                MainWindow.showError(ex, "Could not open file: " + file.getPath());
            } catch (InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage() + "\nPossible wrong password.", "Error!", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            MainWindow.showError(null, "Could not open file.");
        }
    }

    private boolean getPassword(boolean open) {
        PasswordFrame pf = new PasswordFrame(file.getPath(), 1, pg);
        while (true) {
            if (pf.done) {
                break;
            } else {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    MainWindow.showError(ex, "Could not pause running thread.");
                }
            }
        }
        if (pf.cancel) {
            return false;
        }
        encryptionKey = pf.getPassword();
        if (!open) {
            if (encryptionKey.length() < 8) {
                int result = JOptionPane.showConfirmDialog(null, "The password you have entered seems to be below 8 characters long.\n"
                        + "Are you sure you want to continue?", "Warning!", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    return true;
                } else {
                    encryptionKey = null;
                    return false;
                }
            }
        }
        return true;
    }

    private boolean getFile() {
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new File(Settings.getDirectory()));
        fc.setMultiSelectionEnabled(false);
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int returnVal = fc.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            file = fc.getSelectedFile();
            if (!file.getPath().endsWith(".pmaster") && !file.getPath().endsWith(".txt")) {
                file = new File(file.getPath() + ".pmaster");
            }
            setTitle(Settings.APP_NAME + " v" + Settings.APP_VERSION + " - File:" + file.getPath());
            return true;
        } else {
            return false;
        }
    }

    private final Action saveAction = new AbstractAction("") {
        @Override
        public void actionPerformed(ActionEvent ae) {
            saveFile();
        }
    };

    public boolean writingFile = false;

    /**
     * This method saves the file, provided that the user has selected the "Show
     * Passwords" menu item.
     */
    public boolean saveFile() {
        if (!showHidden) {
            showHidden("save");
            return false;
        }
        writingFile = true;
        new SaveFile().start();
        return true;
    }

    private class SaveFile extends Thread {

        @Override
        public void run() {
            boolean result = true;
            if (file == null) {
                result = getFile();
            }
            if (result) {
                if (encryptionKey == null) {
                    getPassword(false);
                }
                saveLogins();
            }
        }
    }

    private final Action saveAsAction = new AbstractAction("") {
        @Override
        public void actionPerformed(ActionEvent ae) {
            saveAsFile();
        }
    };

    private void saveAsFile() {
        if (!showHidden) {
            showHidden("save as");
            return;
        }
        writingFile = true;
        new SaveAsFile().start();
    }

    private class SaveAsFile extends Thread {

        @Override
        public void run() {
            if (getFile()) {
                if (encryptionKey == null) {
                    getPassword(false);
                }
                saveLogins();
            }
        }
    }

    private void saveLogins() {
        stopTableEditing();
        fileNotSaved = false;
        if (file != null) {
            try {
                String plaintext = "AAAAAAAAAAAAAAAA";
                FileOutputStream fos = new FileOutputStream(file);
                for (Login l : loginList) {
                    plaintext += l.toString();
                }
                fos.write(cipher.encrypt(plaintext, encryptionKey));
                fos.close();
            } catch (FileNotFoundException ex) {
                MainWindow.showError(ex, "File was not found.");
            } catch (IOException ex) {
                MainWindow.showError(ex, "Could not write logins file.");
            }
        }
        showStatus("Saved file: " + file.getPath());
        writingFile = false;
    }

    private final Action addLoginAction = new AbstractAction("") {
        @Override
        public void actionPerformed(ActionEvent ae) {
            stopTableEditing();
            Login newLogin = Login.fromString(Settings.STANDARD_LOGIN.toString().replace("--!--", ""));
            int rows = model.getRowCount();
            int lastId;
            if (rows > 0) {
                lastId = new Integer((String) model.getValueAt(rows - 1, 0));
            } else {
                lastId = 0;
            }
            newLogin.setId(++lastId);
            loginList.add(newLogin);
            updateHistory();
        }
    };

    private final Action generatePasswordAction = new AbstractAction("") {
        @Override
        public void actionPerformed(ActionEvent ae) {
            stopTableEditing();
            pg.showWindow(true);
        }
    };

    private final Action deleteLoginAction = new AbstractAction("") {
        @Override
        public void actionPerformed(ActionEvent ae) {
            stopTableEditing();
            if (!showHidden) {
                showHidden("delete logins");
                return;
            }
            try {
                String id = (String) model.getValueAt(row, 0);
                String website = (String) model.getValueAt(row, 1);
                String username = (String) model.getValueAt(row, 2);
                String password = (String) model.getValueAt(row, 3);
                String other = (String) model.getValueAt(row, 4);
                for (Login l : loginList) {
                    boolean idSame = id.equals(l.getId());
                    boolean websiteSame = website.equals(l.getWebsite());
                    boolean usernameSame = username.equals(l.getUsername());
                    boolean passwordSame = password.equals(l.getPassword());
                    boolean otherSame = other.equals(l.getOther());
                    if (idSame && websiteSame && usernameSame && passwordSame && otherSame) {
                        loginList.remove(l);
                        break;
                    }
                }
            } catch (ArrayIndexOutOfBoundsException ex) {
            }
            fileNotSaved = true;
            updateHistory();
        }
    };

    private boolean checkPassword() {
        if (encryptionKey == null) {
            return true;
        }
        PasswordFrame pf = new PasswordFrame(file.getPath(), 2, pg);
        while (true) {
            if (pf.done) {
                break;
            } else {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    MainWindow.showError(ex, "Could not pause running thread.");
                }
            }
        }
        if (pf.cancel) {
            return false;
        }
        String password = pf.getPassword();
        if (password.equals(encryptionKey)) {
            return true;
        } else {
            return false;
        }
    }

    private final Action showPasswordsAction = new AbstractAction("") {
        @Override
        public void actionPerformed(ActionEvent ae) {
            stopTableEditing();
            new ShowPasswords().start();
        }
    };

    public class ShowPasswords extends Thread {

        @Override
        public void run() {
            if (showPasswordsMenuItem.isSelected()) {
                hidePasswords();
                return;
            }
            if (checkPassword()) {
                showPasswords();
            } else {
                JOptionPane.showMessageDialog(null, "Wrong encryption key.", "Wrong Input.", JOptionPane.WARNING_MESSAGE);
                showPasswordsMenuItem.setSelected(false);
            }
        }
    }

    private void hidePasswords() {
        updateGUI(false);
        updateTable();
    }

    private void showPasswords() {
        updateGUI(true);
        updateTable();
        startIdleTimer();
    }

    private void updateGUI(boolean enable) {
        idleLabel.setEnabled(enable);
        idleTimer = enable;
        showHidden = enable;
        showPasswordsMenuItem.setSelected(!enable);
        editMenu.setEnabled(enable);
        moveUpButton.setEnabled(enable);
        moveDownButton.setEnabled(enable);
        addLoginButton.setEnabled(enable);
        deleteLoginButton.setEnabled(enable);
    }

    private void updateIDNumbers() {
        int maxID = 0;
        for (Login l : loginList) {
            if (l.getIntId() > maxID) {
                maxID = l.getIntId();
            }
        }
        int numberOfZeroes = (int) Math.log10(maxID);
        for (Login l : loginList) {
            l.setNumberOfZeroes(numberOfZeroes - (int) Math.log10(l.getIntId()));
        }
    }

    public void updateTable() {
        bypassListChange = true;
        model.setRowCount(0);
        updateIDNumbers();
        for (Login l : loginList) {
            if (showHidden) {
                model.addRow(l.toObject());
            } else {
                model.addRow(l.toObjectHidden());
            }
        }
        bypassListChange = false;
    }

    private final Action undoAction = new AbstractAction("") {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!showHidden) {
                return;
            }
            undo();
        }
    };

    private void undo() {
        stopTableEditing();
        loginList = history.undo();
        updateTable();
    }

    private final Action redoAction = new AbstractAction("") {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!showHidden) {
                return;
            }
            redo();
        }
    };

    private void redo() {
        stopTableEditing();
        loginList = history.redo();
        updateTable();
    }

    private void updateHistory() {
        fileNotSaved = true;
        history.insert(loginList);
        updateTable();
    }

    private final Action settingsAction = new AbstractAction("") {
        @Override
        public void actionPerformed(ActionEvent e) {
            stopTableEditing();
            sw.showWindow();
        }
    };

    /**
     * This method begins the procedure in order to change the encryption key.
     * It verifies the previous encryption key and if it is correct, asks for a
     * new one.
     */
    public void changeEncryptionKey() {
        new ChangeEncryptionKey().start();
    }

    private class ChangeEncryptionKey extends Thread {

        @Override
        public void run() {
            if (checkPassword()) {
                getPassword(false);
                saveFile();
                JOptionPane.showMessageDialog(null, "Encryption key changed and file saved successfully.", "Completed Action.", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void stopTableEditing() {
        TableCellEditor editor = loginTable.getCellEditor();
        if (editor != null) {
            editor.stopCellEditing();
        }
    }

    private final Action firefoxImportAction = new AbstractAction("") {
        @Override
        public void actionPerformed(ActionEvent e) {
            File profiles = new File(System.getProperty("user.home") + "\\AppData\\Roaming\\Mozilla\\Firefox\\Profiles\\");
            for (File profile : profiles.listFiles()) {
                File login = new File(profile.getPath() + "\\logins.json");
                if (login.exists()) {
                    importLoginFromFirefox(login);
                }
            }
        }
    };

    private void importLoginFromFirefox(File login) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(login));
            String logins = "";
            String temp;
            while ((temp = reader.readLine()) != null) {
                logins += temp;
            }
            reader.close();
            JSONObject json = new JSONObject(logins);
            JSONArray array = json.getJSONArray("logins");
            for (int i = 0; i < array.length(); i++) {
                addFirefoxJSONLogin(array.getJSONObject(i));
            }
        } catch (FileNotFoundException ex) {
            MainWindow.showError(ex, "Could not find file: " + login.getPath());
        } catch (IOException ex) {
            MainWindow.showError(ex, "Could not import logins from Firefox.");
        }
    }

    private void addFirefoxJSONLogin(JSONObject obj) {
        newFile();
        String encryptedUsername = obj.getString("encryptedUsername").substring(6);
        String encryptedPassword = obj.getString("encryptedPassword");
        String website = obj.getString("hostname");
        byte[] username = Base64.getDecoder().decode(encryptedUsername);
        byte[] password = Base64.getDecoder().decode(encryptedPassword);

        String un = new String(username);
        String pw = new String(password);
//        System.out.println("username: ");
//        System.out.println(un);
//        System.out.println("password: ");
//        System.out.println(pw);
//        System.out.println(website);

        String other = "imported from firefox";
    }

    private final Action openLinkAction = new AbstractAction("") {
        @Override
        public void actionPerformed(ActionEvent e) {
            String website = getWebsite();
            if (Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().browse(new URI(website));
                } catch (URISyntaxException | IOException ex) {
                    MainWindow.showError(ex, "Could not open websit: " + website);
                }
            } else {
                showError(null, "Cannot open website" + website + ".");
            }
        }
    };

    private String getWebsite() {
        String website = (String) model.getValueAt(row, 1);
        if (!website.startsWith("http://") && !website.startsWith("https://")) {
            if (website.startsWith("www.")) {
                website = "https://" + website;
            } else {
                website = "https://www." + website;
            }
        }
        return website;
    }

    private final Action copyWebsiteAction = new AbstractAction("") {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (Login.isHideWebsite()) {
                if (showHidden) {
                    StringSelection stringSelection = new StringSelection(getWebsite());
                    Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
                    clpbrd.setContents(stringSelection, null);
                    showStatus("Website: " + getWebsite() + " copied to clipboard.");
                } else {
                    showHidden("copy website");
                }
            } else {
                StringSelection stringSelection = new StringSelection(getWebsite());
                Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
                clpbrd.setContents(stringSelection, null);
                showStatus("Website: " + getWebsite() + " copied to clipboard.");
            }
        }
    };

    private final Action copyUsernameAction = new AbstractAction("") {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (Login.isHideUsername()) {
                if (showHidden) {
                    StringSelection stringSelection = new StringSelection((String) model.getValueAt(row, 2));
                    Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
                    clpbrd.setContents(stringSelection, null);
                    showStatus("Username: " + (String) model.getValueAt(row, 2) + " copied to clipboard.");
                } else {
                    showHidden("copy username");
                }
            } else {
                StringSelection stringSelection = new StringSelection((String) model.getValueAt(row, 2));
                Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
                clpbrd.setContents(stringSelection, null);
                showStatus("Username: " + (String) model.getValueAt(row, 2) + " copied to clipboard.");
            }
        }
    };

    private final Action copyPasswordAction = new AbstractAction("") {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (showHidden) {
                StringSelection stringSelection = new StringSelection((String) model.getValueAt(row, 3));
                Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
                clpbrd.setContents(stringSelection, null);
                showStatus("Password for website: " + (String) model.getValueAt(row, 1) + " copied to clipboard.");
            } else {
                showHidden("copy password");
            }
        }
    };

    public static void startStaticIdleTimer() {
        mw.startIdleTimer();
    }

    IdleTimer timer;

    private void startIdleTimer() {
        if (file == null) {
            idleLabel.setVisible(false);
            idleLabel.setEnabled(false);
            return;
        }
        if (System.getProperty("os.name").contains("Windows")) {
            if (Settings.getUserIdleSeconds() != 0) {
                idleLabel.setVisible(true);
                idleLabel.setEnabled(true);
                idleTimer = true;
                if (timer == null) {
                    timer = new IdleTimer();
                    timer.start();
                } else {
                    if (!timerRunning) {
                        timer = new IdleTimer();
                        timer.start();
                    }
                }
            } else {
                idleLabel.setVisible(false);
                idleLabel.setEnabled(false);
                idleTimer = false;
            }
        } else {
            showStatus("Cannot calculate user idle time on this OS.");
            idleLabel.setVisible(false);
            idleLabel.setEnabled(false);
            idleTimer = false;
        }
    }

    private boolean timerRunning = false;

    private class IdleTimer extends Thread {

        @Override
        public void run() {
            timerRunning = true;
            while (idleTimer) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    MainWindow.showError(ex, "Could pause running thread.");
                }
                int idleTime = Win32IdleTime.getIdleTimeSeconds();
                idleLabel.setText("Idle for:" + idleTime + "/" + Settings.getUserIdleSeconds());
                if (idleTime >= Settings.getUserIdleSeconds()) {
                    idleLabel.setText("Idle for:" + idleTime + "s");
                    if (idleTimer) {
                        hidePasswords();
                    }
                }
            }
            timerRunning = false;
        }
    }

    private final Action pasteAction = new AbstractAction("") {
        @Override
        public void actionPerformed(ActionEvent e) {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            try {
                model.setValueAt(clipboard.getData(DataFlavor.stringFlavor), row, col);
                showStatus("Pasted text: " + clipboard.getData(DataFlavor.stringFlavor));
            } catch (UnsupportedFlavorException | IOException ex) {
                MainWindow.showError(ex, "Could not paste.");
            }
        }
    };

    class LoginComparator implements Comparator {

        @Override
        public int compare(Object o1, Object o2) {
            Login l1 = (Login) o1;
            Login l2 = (Login) o2;
            int id1 = new Integer(l1.getId());
            int id2 = new Integer(l2.getId());
            if (id1 == id2) {
                return 0;
            } else if (id1 > id2) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    private final Action moveUpAction = new AbstractAction("") {
        @Override
        public void actionPerformed(ActionEvent e) {
            moveUp();
        }
    };

    private void moveUp() {
        if (!showHidden) {
            return;
        }
        if (row == 0) {
            return;
        }
        int selectedId = new Integer((String) model.getValueAt(row, 0));
        Login selectedLogin = null;
        Login aboveLogin = null;
        for (Login l : loginList) {
            if (l.getIntId() < selectedId) {
                aboveLogin = l;
            }
            if (l.getIntId() == selectedId) {
                selectedLogin = l;
                break;
            }
        }
        selectedLogin.setId(aboveLogin.getIntId());
        aboveLogin.setId(selectedLogin.getIntId() + 1);
        loginList.sort(new LoginComparator());
        updateHistory();
        fileNotSaved = true;
        row--;
    }

    private final Action moveDownAction = new AbstractAction("") {
        @Override
        public void actionPerformed(ActionEvent e) {
            moveDown();
        }
    };

    private void moveDown() {
        if (!showHidden) {
            return;
        }
        if (row == model.getRowCount()) {
            return;
        }
        int selectedId = new Integer((String) model.getValueAt(row, 0));
        Login selectedLogin = null;
        Login belowLogin = null;
        for (Login l : loginList) {
            if (l.getIntId() > selectedId) {
                belowLogin = l;
                break;
            }
            if (l.getIntId() == selectedId) {
                selectedLogin = l;
            }
        }
        selectedLogin.setId(belowLogin.getIntId());
        belowLogin.setId(selectedLogin.getIntId() - 1);
        loginList.sort(new LoginComparator());
        updateHistory();
        fileNotSaved = true;
        row++;
    }

    public void showHidden(String action) {
        showStatus("Cannot " + action + ". You need to click on 'Hide Information' first in order to deselect it.");
    }

    public static void showStatus(String status) {
        trayIcon.displayMessage(Settings.getAppTitle(), status, TrayIcon.MessageType.NONE);
        System.out.println(status);
    }

    public static void showError(Throwable t, String title) {
        LOG.log(Level.SEVERE, null, t);
//        JOptionPane.showMessageDialog(null, t.getMessage(), title, JOptionPane.ERROR_MESSAGE);
        trayIcon.displayMessage(Settings.getAppTitle(), title + "\n" + t.getLocalizedMessage(), TrayIcon.MessageType.ERROR);
    }

    public static void toggleVisibility() {
        if (mw.isVisible() && trayIconSetupSuccessful) {
            mw.setVisible(false);
            toggleMWVisibilityMenuItem.setLabel("Show " + Settings.APP_NAME);
        } else if (!mw.isVisible()) {
            if (trayIconSetupSuccessful) {
                toggleMWVisibilityMenuItem.setLabel("Hide " + Settings.APP_NAME);
            }
            mw.setExtendedState(Settings.getWindowState());
            mw.setVisible(true);
            mw.toFront();
        }
    }

}
