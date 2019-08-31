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
import Other.Strings;
import java.awt.AWTException;
import java.awt.CheckboxMenuItem;
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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
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
     * These integer fields hold the cell that is selected when the user left or right clicks in the JTable.
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

    boolean showingLoginData = false;
    boolean fileNotSaved = false;

    /**
     * The ExitWindow variable.
     */
    public ExitWindow ew;
    private SettingsWindow sw;
    private PasswordGenerator pg;

    private AES cipher;

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
        trayIcon = new TrayIcon(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource(Strings.ICON_NAME)));
        trayIcon.setImageAutoSize(true);
        trayIcon.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleVisibility();
            }
        });
        systemTrayMenu = new PopupMenu();
        updateSystemTrayMenu();
        trayIcon.setPopupMenu(systemTrayMenu);
        final SystemTray tray = SystemTray.getSystemTray();
        try {
            tray.add(trayIcon);
            trayIconSetupSuccessful = true;
        } catch (AWTException e) {
            showStatus(Strings.ErrorStrings.ERROR_COULD_NOT_INITIALIZE_TRAY_ICON);
            trayIconSetupSuccessful = false;
        }
    }

    private static MenuItem toggleMWVisibilityMenuItem;
    private static CheckboxMenuItem hideInformationMenuItem;
    private PopupMenu systemTrayMenu;

    public void updateSystemTrayMenu() {
        systemTrayMenu.removeAll();
        MenuItem exitItem = new MenuItem(Strings.MainWindowStrings.EXIT_STRING);
        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exitApp();
            }
        });
        toggleMWVisibilityMenuItem = new MenuItem(Strings.MainWindowStrings.HIDE_MAIN_WINDOW);
        toggleMWVisibilityMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleVisibility();
            }
        });
        MenuItem openPasswordGeneratorMenuItem = new MenuItem(Strings.MainWindowStrings.OPEN_PASSWORD_GENERATOR);
        openPasswordGeneratorMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopTableEditing();
                pg.showWindow(true);
            }
        });
        MenuItem settingsMenuItem = new MenuItem(Strings.MainWindowStrings.OPEN_SETTINGS);
        settingsMenuItem.addActionListener(settingsAction);
        hideInformationMenuItem = new CheckboxMenuItem(Strings.MainWindowStrings.HIDE_INFORMATION);
        hideInformationMenuItem.setState(!showingLoginData);
        hideInformationMenuItem.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                showPasswordsMenuItem.setState(hideInformationMenuItem.getState());
                hideInformationMenuItem.setState(hideInformationMenuItem.getState());
                stopTableEditing();
                new ShowPasswords().start();
            }
        });
        if (Settings.displayFavoritesInSystemTrayPopupMenu) {
            addFavorites();
        }
        systemTrayMenu.add(toggleMWVisibilityMenuItem);
        systemTrayMenu.addSeparator();
        systemTrayMenu.add(hideInformationMenuItem);
        systemTrayMenu.add(openPasswordGeneratorMenuItem);
        systemTrayMenu.add(settingsMenuItem);
        systemTrayMenu.addSeparator();
        systemTrayMenu.add(exitItem);
    }

    private void addFavorites() {
        if (systemTrayMenu == null) {
            return;
        }
        ArrayList<Login> favs = getFavorites();
        if (favs == null) {
            return;
        }
        for (Login l : favs) {
            addFavoriteToPopupMenu(l);
        }
        systemTrayMenu.addSeparator();
    }

    private void addFavoriteToPopupMenu(Login login) {
        PopupMenu loginMenu = null;
        if (Settings.hideSystemTrayPopupInformationWhenHidingInformation && !showingLoginData) {
            loginMenu = new PopupMenu(login.getId());
        } else {
            switch (Settings.favoriteDisplay) {
                case 0:
                    loginMenu = new PopupMenu(login.getId());
                    break;
                case 1:
                    loginMenu = new PopupMenu(login.getWebsite());
                    break;
                case 2:
                    loginMenu = new PopupMenu(login.getUsername());
                    break;
                case 3:
                    loginMenu = new PopupMenu(login.getOther().split(" ")[0]);
                    break;
                default:
                    loginMenu = new PopupMenu(login.getWebsite());
                    break;
            }
        }
        MenuItem copyUsernameMI = new MenuItem(Strings.MainWindowStrings.COPY_USERNAME);
        copyUsernameMI.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (Login.isHideUsername()) {
                    if (showingLoginData) {
                        copyStringToClipboard(login.getUsername(), Strings.StatusStrings.CLIPBOARD_COPY_USERNAME, null);
                    } else {
                        Thread t = new Thread() {
                            @Override
                            public void run() {
                                if (checkPassword()) {
                                    showPasswords();
                                    copyStringToClipboard(login.getUsername(), Strings.StatusStrings.CLIPBOARD_COPY_USERNAME, null);
                                } else {
                                    displayErrorWhenHidingInformation(Strings.ErrorStrings.HIDING_INFORMATION_COPY_USERNAME);
                                }
                            }
                        };
                        t.start();
                    }
                } else {
                    copyStringToClipboard(login.getUsername(), Strings.StatusStrings.CLIPBOARD_COPY_USERNAME, null);
                }
            }
        });
        MenuItem copyPasswordMI = new MenuItem(Strings.MainWindowStrings.COPY_PASSWORD);
        copyPasswordMI.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (showingLoginData) {
                    copyStringToClipboard(login.getPassword(), Strings.StatusStrings.CLIPBOARD_COPY_PASSWORD_FOR_WEBSITE, login.getWebsite());
                } else {
                    Thread t = new Thread() {
                        @Override
                        public void run() {
                            if (checkPassword()) {
                                showPasswords();
                                copyStringToClipboard(login.getPassword(), Strings.StatusStrings.CLIPBOARD_COPY_PASSWORD_FOR_WEBSITE, login.getWebsite());
                            } else {
                                displayErrorWhenHidingInformation(Strings.ErrorStrings.HIDING_INFORMATION_COPY_PASSWORD);
                            }
                        }
                    };
                    t.start();
                }
            }
        });
        MenuItem copyWebsiteMI = new MenuItem(Strings.MainWindowStrings.COPY_WEBSITE);
        copyWebsiteMI.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (Login.isHideWebsite()) {
                    if (showingLoginData) {
                        copyStringToClipboard(login.getWebsite(), Strings.StatusStrings.CLIPBOARD_COPY_WEBSITE, null);
                    } else {
                        Thread t = new Thread() {
                            @Override
                            public void run() {
                                if (checkPassword()) {
                                    showPasswords();
                                    copyStringToClipboard(login.getWebsite(), Strings.StatusStrings.CLIPBOARD_COPY_WEBSITE, null);
                                } else {
                                    displayErrorWhenHidingInformation(Strings.ErrorStrings.HIDING_INFORMATION_COPY_WEBSITE);
                                }
                            }
                        };
                        t.start();
                    }
                } else {
                    copyStringToClipboard(login.getWebsite(), Strings.StatusStrings.CLIPBOARD_COPY_WEBSITE, null);
                }
            }
        });
        MenuItem openWebsiteMI = new MenuItem(Strings.MainWindowStrings.OPEN_WEBSITE);
        openWebsiteMI.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (showingLoginData) {
                    openWebsite(login.getWebsite());
                } else {
                    Thread t = new Thread() {
                        @Override
                        public void run() {
                            if (checkPassword()) {
                                showPasswords();
                                openWebsite(login.getWebsite());
                            } else {
                                displayErrorWhenHidingInformation(Strings.ErrorStrings.HIDING_INFORMATION_OPEN_WEBSITE);
                            }
                        }
                    };
                    t.start();
                }
            }
        });
        loginMenu.add(copyUsernameMI);
        loginMenu.add(copyPasswordMI);
        loginMenu.add(copyWebsiteMI);
        loginMenu.addSeparator();
        loginMenu.add(openWebsiteMI);
        systemTrayMenu.add(loginMenu);
    }

    private class TryToOpenFiles extends Thread {

        @Override
        public void run() {
            File parent = new File(Settings.getDirectory());
            ArrayList<String> files = new ArrayList();
            int i = 0;
            for (File f : parent.listFiles()) {
                if (f.getPath().endsWith(Strings.FILE_EXTENSION)) {
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
                            MainWindow.showError(ex, Strings.ErrorStrings.EXCEPTION_INTERRUPTED);
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
            showPasswords();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tablePopupMenu = new javax.swing.JPopupMenu();
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
        filterTextField = new javax.swing.JTextField();
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
        tablePopupMenu.add(addLoginMenuItem1);

        deleteLoginMenuItem1.setAction(deleteLoginAction);
        deleteLoginMenuItem1.setText("Delete Login.");
        tablePopupMenu.add(deleteLoginMenuItem1);
        tablePopupMenu.add(jSeparator5);

        moveUpMenuItem.setAction(moveUpAction);
        moveUpMenuItem.setText("Move Up");
        tablePopupMenu.add(moveUpMenuItem);

        moveDownMenuItem.setAction(moveDownAction);
        moveDownMenuItem.setText("Move Down");
        tablePopupMenu.add(moveDownMenuItem);
        tablePopupMenu.add(jSeparator11);

        undoMenuItem1.setAction(undoAction);
        undoMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, java.awt.event.InputEvent.CTRL_MASK));
        undoMenuItem1.setText("Undo");
        tablePopupMenu.add(undoMenuItem1);

        redoMenuItem1.setAction(redoAction);
        redoMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Y, java.awt.event.InputEvent.CTRL_MASK));
        redoMenuItem1.setText("Redo");
        tablePopupMenu.add(redoMenuItem1);
        tablePopupMenu.add(jSeparator8);

        openLinkMenuItem1.setAction(openLinkAction);
        openLinkMenuItem1.setText("Open website in browser.");
        tablePopupMenu.add(openLinkMenuItem1);
        tablePopupMenu.add(jSeparator10);

        copyWebsiteMenuItem.setAction(copyWebsiteAction);
        copyWebsiteMenuItem.setText("Copy Website");
        tablePopupMenu.add(copyWebsiteMenuItem);

        copyUsernameMenuItem.setAction(copyUsernameAction);
        copyUsernameMenuItem.setText("Copy Username");
        tablePopupMenu.add(copyUsernameMenuItem);

        copyPasswordMenuItem.setAction(copyPasswordAction);
        copyPasswordMenuItem.setText("Copy Password");
        tablePopupMenu.add(copyPasswordMenuItem);
        tablePopupMenu.add(jSeparator12);

        pasteMenuItem.setAction(pasteAction);
        pasteMenuItem.setText("Paste");
        tablePopupMenu.add(pasteMenuItem);

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

        jScrollPane1.setComponentPopupMenu(tablePopupMenu);

        loginTable.setAutoCreateRowSorter(true);
        loginTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "Name/Website", "Username", "Password", "Other", "Favorite"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Boolean.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        loginTable.setCellEditor(new MyTableCellEditor());
        loginTable.setColumnSelectionAllowed(true);
        loginTable.setComponentPopupMenu(tablePopupMenu);
        jScrollPane1.setViewportView(loginTable);
        loginTable.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        if (loginTable.getColumnModel().getColumnCount() > 0) {
            loginTable.getColumnModel().getColumn(0).setPreferredWidth(30);
            loginTable.getColumnModel().getColumn(0).setMaxWidth(70);
        }

        filterTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        filterTextField.setText("Filter");
        filterTextField.setToolTipText(Strings.MainWindowStrings.FILTER_TOOLTIP);
        filterTextField.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                filterTextFieldMouseClicked(evt);
            }
        });
        filterTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterTextFieldActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 778, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(moveUpButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(moveDownButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addLoginButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(deleteLoginButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(filterTextField)
                .addGap(12, 12, 12)
                .addComponent(idleLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(moveUpButton)
                    .addComponent(moveDownButton)
                    .addComponent(addLoginButton)
                    .addComponent(deleteLoginButton)
                    .addComponent(filterTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(idleLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 422, Short.MAX_VALUE))
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

        showPasswordsMenuItem.setAction(hideInformationAction);
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

    private void filterTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterTextFieldActionPerformed
        
    }//GEN-LAST:event_filterTextFieldActionPerformed

    private void filterTextFieldMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_filterTextFieldMouseClicked
        if(filterTextField.hasFocus() && "Filter".equals(filterTextField.getText())){
            filterTextField.setText("");
        }
    }//GEN-LAST:event_filterTextFieldMouseClicked

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
            showError(ex, Strings.ErrorStrings.ERROR_COULD_NOT_INITIALIZE_THEME);
        }
        java.awt.EventQueue.invokeLater(() -> {
            mw = new MainWindow();
            mw.setVisible(true);
            if (Settings.minimizeToSystemTray && Settings.startPMMinimized && trayIconSetupSuccessful) {
                toggleVisibility();
            }
            mw.startIdleTimer();
        });
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
    private javax.swing.JTextField filterTextField;
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
    private javax.swing.JMenuItem redoMenuItem;
    private javax.swing.JMenuItem redoMenuItem1;
    private javax.swing.JMenuItem safariImportMenuItem;
    private javax.swing.JMenuItem saveAsMenuItem;
    private javax.swing.JMenuItem saveMenuItem;
    private javax.swing.JMenuItem settingsMenuItem;
    private javax.swing.JCheckBoxMenuItem showPasswordsMenuItem;
    private javax.swing.JPopupMenu tablePopupMenu;
    private javax.swing.JMenuItem undoMenuItem;
    private javax.swing.JMenuItem undoMenuItem1;
    private javax.swing.JMenu viewMenu;
    // End of variables declaration//GEN-END:variables

    private void initSettings() {
        if (getClass().getPackage().getImplementationVersion() != null) {
            Strings.APP_VERSION = getClass().getPackage().getImplementationVersion();
        }
        setTitle(Strings.getAppTitle());
        FileManagement.importSettingsFromFile();
        ew = new ExitWindow(MainWindow.this);
        pg = new PasswordGenerator();
        sw = new SettingsWindow(this);
        cipher = new AES();

        model = (DefaultTableModel) loginTable.getModel();

        loginList = new ArrayList();
        history = new History();
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource(Strings.ICON_NAME)));
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
        filterTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
//                String filterText = filterTextField.getText();
//                if(!"".equals(filterText)){
//                    filterLogins(filterText);
//                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                String filterText = filterTextField.getText();
                if(!"".equals(filterText)){
                    filterLogins(filterText);
                } else {
                    filtering = false;
                }
            }
        });
    }
    
    boolean filtering = false;
    private void filterLogins(String filter){
        System.out.println("Filtering logins " + filter);
        filtering = true;
        String[] filterWords = filter.split(" ");
        String filterU = null, filterO = null, filterW = null;
        for(String s: filterWords){
            if(s.startsWith("u:")){
                filterU = s.replace("u:", "");
            }
            if(s.startsWith("o:")){
                filterO = s.replace("o:", "");
            }
            if(s.startsWith("w:")){
                filterW = s.replace("w:", "");
            }
        }
        boolean appliedFiltering = false;
        for(Login l: loginList){
            if(filterU != null && !"".equals(filterU)){
                if(!l.getUsername().toLowerCase().contains(filterU.toLowerCase())){
                    System.out.println("Filtering login:" + l.toString());
                    l.filterHide = true;
                    appliedFiltering = true;
                } else {
                    l.filterHide = false;
                    appliedFiltering = true;
                }
            } else if(filterO != null && !"".equals(filterO)){
                if(!l.getOther().toLowerCase().contains(filterO.toLowerCase())){
                    System.out.println("Filtering login:" + l.toString());
                    l.filterHide = true;
                    appliedFiltering = true;
                } else {
                    l.filterHide = false;
                    appliedFiltering = true;
                }
            } else if(filterW != null && !"".equals(filterW)){
                if(!l.getWebsite().toLowerCase().contains(filterW.toLowerCase())){
                    System.out.println("Filtering login:" + l.toString());
                    l.filterHide = true;
                    appliedFiltering = true;
                } else {
                    l.filterHide = false;
                    appliedFiltering = true;
                }
            } else {
                l.filterHide = false;
                appliedFiltering = true;
            }
        }
        updateTable();
    }

    private void updateList() {
        if (bypassListChange) {
            return;
        }
        if (!showingLoginData) {
            updateTable();
            return;
        }
        fileNotSaved = true;
        ArrayList<Login> temp = loginList;
        loginList = new ArrayList();
        if(model.getRowCount() > 0) {
            for(int i = 0 ; i< model.getRowCount() ; i++){
                Login t = new Login();
                t.setId((String) model.getValueAt(i, 0));
                t.setWebsite((String) model.getValueAt(i, 1));
                t.setUsername((String) model.getValueAt(i, 2));
                if (showingLoginData) {
                    t.setPassword((String) model.getValueAt(i, 3));
                }
                t.setOther((String) model.getValueAt(i, 4));
                t.setFavorite((Boolean) model.getValueAt(i, 5));
                loginList.add(t);
                break;
            }
        } else {
            return;
        }
        if(filtering){
            for(Login l: temp){
                if(l.filterHide){
                    loginList.add(l);
                }
            }
        }
        loginList.sort(new LoginComparator());
        updateHistory();
        if (trayIconSetupSuccessful) {
            updateSystemTrayMenu();
        }
    }

    private ArrayList<Login> getFavorites() {
        if (loginList == null) {
            return null;
        }
        ArrayList<Login> favs = new ArrayList();
        for (Login l : loginList) {
            if (l.isFavorite()) {
                favs.add(l);
            }
        }
        if (favs.isEmpty()) {
            return null;
        }
        return favs;
    }

    /**
     * This method exits the program.
     */
    public void exitApp() {
        if (showingLoginData) {
            if (Settings.getWindowState() != JFrame.MAXIMIZED_BOTH) {
                Settings.setUserSize(getSize());
            }
            Settings.setWindowState(getExtendedState());
            FileManagement.saveSettingsToFile();
        }
        System.exit(0);
    }

    private final Action exitAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent ae) {
            stopTableEditing();
            exitApp();
        }
    };

    private final Action newAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent ae) {
            newFile();
        }
    };

    private void newFile() {
        if (fileNotSaved) {
            int result = JOptionPane.showConfirmDialog(null, Strings.MainWindowStrings.WARNING_FILE_UNSAVED_CONTENT, Strings.MainWindowStrings.WARNING_FILE_UNSAVED_TITLE, JOptionPane.OK_CANCEL_OPTION);
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
        showingLoginData = true;
        loginList = new ArrayList();
        updateTable();
        showPasswords();
        setTitle(Strings.getAppTitle());
    }

    private final Action openAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent ae) {
            new OpenFile().start();
        }
    };

    boolean openingFile = false;

    private class OpenFile extends Thread {

        @Override
        public void run() {
            openingFile = true;
            if (getFile()) {
                if (setPassword(true)) {
                    readFile();
                }
            }
            openingFile = false;
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
                setTitle(Strings.getAppFileTitle(file));
                fileNotSaved = false;
            } catch (IOException | NullPointerException ex) {
                MainWindow.showError(ex, Strings.ErrorStrings.ERROR_COULD_NOT_OPEN_FILE + file.getPath());
            } catch (InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException ex) {
                MainWindow.showError(null, Strings.ErrorStrings.ERROR_NULL_FILE);
            }
        } else {
            MainWindow.showError(null, Strings.ErrorStrings.ERROR_NULL_FILE);
        }
    }

    /**
     * Sets the encryption key for the file.
     * @param notFirstFileSave Boolean parameter that signifies if the encryption key has been set for this file.
     * @return 
     */
    private boolean setPassword(boolean notFirstFileSave) {
        PasswordFrame pf = new PasswordFrame(file.getPath(), 1, pg);
        while (true) {
            if (pf.done) {
                break;
            } else {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    MainWindow.showError(ex, Strings.ErrorStrings.EXCEPTION_INTERRUPTED);
                }
            }
        }
        if (pf.cancel) {
            return false;
        }
        encryptionKey = pf.getPassword();
        if (!notFirstFileSave) {
            if (encryptionKey.length() < 8) {
                int result = JOptionPane.showConfirmDialog(null, Strings.WarningStrings.WARNING_SMALL_ENCRYPTION_KEY, Strings.WarningStrings.WARNING, JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
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
            newFile();
            file = fc.getSelectedFile();
            if (!file.getPath().endsWith(Strings.FILE_EXTENSION) && !file.getPath().endsWith(".txt") && !openingFile) {
                file = new File(file.getPath() + Strings.FILE_EXTENSION);
            }
            return true;
        } else {
            return false;
        }
    }

    private final Action saveAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent ae) {
            saveFile();
        }
    };

    public boolean writingFile = false;

    /**
     * This method saves the file, provided that the user has selected the "Show Passwords" menu item.
     */
    public boolean saveFile() {
        if (!showingLoginData) {
            displayErrorWhenHidingInformation(Strings.ErrorStrings.HIDING_INFORMATION_SAVE_FILE);
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
                    setPassword(false);
                }
                saveLogins();
            }
        }
    }

    private final Action saveAsAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent ae) {
            saveAsFile();
        }
    };

    private void saveAsFile() {
        if (!showingLoginData) {
            Thread t = new Thread() {
                @Override
                public void run() {
                    if (!checkPassword()) {
                        displayErrorWhenHidingInformation(Strings.ErrorStrings.HIDING_INFORMATION_SAVE_AS_FILE);
                    } else {
                        writingFile = true;
                        new SaveAsFile().start();
                    }
                }
            };
            t.start();
        } else {
            writingFile = true;
            new SaveAsFile().start();
        }
    }

    private class SaveAsFile extends Thread {

        @Override
        public void run() {
            if (getFile()) {
                if (encryptionKey == null) {
                    setPassword(false);
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
                showError(ex, Strings.ErrorStrings.EXCEPTION_FILE_NOT_FOUND);
            } catch (IOException ex) {
                showError(ex, Strings.ErrorStrings.EXCEPTION_FILE_IOEXCEPTION);
            }
        }
        showStatus("Saved file: " + file.getPath());
        writingFile = false;
    }

    private final Action addLoginAction = new AbstractAction() {
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

    private final Action generatePasswordAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent ae) {
            stopTableEditing();
            pg.showWindow(true);
        }
    };

    private final Action deleteLoginAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent ae) {
            stopTableEditing();
            if (!showingLoginData) {
                Thread t = new Thread() {
                    @Override
                    public void run() {
                        if (!checkPassword()) {
                            displayErrorWhenHidingInformation(Strings.ErrorStrings.HIDING_INFORMATION_DELETE_LOGIN);
                        } else {
                            deleteLogin();
                        }
                    }
                };
                t.start();
            } else {
                deleteLogin();
            }
        }
    };

    private void deleteLogin() {
        try {
            String id = (String) model.getValueAt(loginTable.convertRowIndexToModel(row), 0);
            String website = (String) model.getValueAt(loginTable.convertRowIndexToModel(row), 1);
            String username = (String) model.getValueAt(loginTable.convertRowIndexToModel(row), 2);
            String password = (String) model.getValueAt(loginTable.convertRowIndexToModel(row), 3);
            String other = (String) model.getValueAt(loginTable.convertRowIndexToModel(row), 4);
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

    /**
     * Prompts the user for the encryption key and checks if it's the same.
     * @return true if the encryption key is the same. false if not.
     */
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
                    MainWindow.showError(ex, Strings.ErrorStrings.EXCEPTION_INTERRUPTED);
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

    private final Action hideInformationAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent ae) {
            stopTableEditing();
            new ShowPasswords().start();
        }
    };

    public class ShowPasswords extends Thread {

        @Override
        public void run() {
            if (showPasswordsMenuItem.getState()) {
                hidePasswords();
                return;
            }
            if (checkPassword()) {
                showPasswords();
            } else {
                showError(null, Strings.ErrorStrings.EXCEPTION_WRONG_ENCRYPTION_KEY);
                hidePasswords();
            }
        }
    }

    private void hidePasswords() {
        idleLabel.setText(Strings.StatusStrings.HIDING_INFORMATION);
        toggleHiddenInformationExtras(false);
        updateTable();
    }

    private void showPasswords() {
        toggleHiddenInformationExtras(true);
        updateTable();
    }

    private void toggleHiddenInformationExtras(boolean enable) {
        idleLabel.setEnabled(enable);
        showingLoginData = enable;
        showPasswordsMenuItem.setSelected(!enable);
        editMenu.setEnabled(enable);
        moveUpButton.setEnabled(enable);
        moveDownButton.setEnabled(enable);
        addLoginButton.setEnabled(enable);
        deleteLoginButton.setEnabled(enable);
        settingsMenuItem.setEnabled(enable);
        hideInformationMenuItem.setState(!enable);
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
            if(!filtering){
                if (showingLoginData) {
                    model.addRow(l.toObject());
                } else {
                    model.addRow(l.toObjectHidden());
                }
            } else {
                if (showingLoginData) {
                    if(!l.filterHide){
                        model.addRow(l.toObject());
                    }
                } else {
                    if(!l.filterHide){
                        model.addRow(l.toObjectHidden());
                    }
                }
            }
        }
        bypassListChange = false;
        if (trayIconSetupSuccessful) {
            updateSystemTrayMenu();
        }
    }

    private final Action undoAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!showingLoginData) {
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

    private final Action redoAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!showingLoginData) {
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

    private final Action settingsAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            stopTableEditing();
            if (showingLoginData) {
                sw.showWindow();
            } else {
                Thread t = new Thread() {
                    @Override
                    public void run() {
                        if (checkPassword()) {
                            showPasswords();
                            sw.showWindow();
                        } else {
                            displayErrorWhenHidingInformation(Strings.ErrorStrings.HIDING_INFORMATION_OPEN_SETTINGS);
                        }
                    }
                };
                t.start();
            }
        }
    };

    /**
     * This method begins the procedure in order to change the encryption key. It verifies the previous encryption key and if it is correct, asks for a new one.
     */
    public void changeEncryptionKey() {
        new ChangeEncryptionKey().start();
    }

    private class ChangeEncryptionKey extends Thread {

        @Override
        public void run() {
            if (checkPassword()) {
                setPassword(false);
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

    private final Action firefoxImportAction = new AbstractAction() {
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

    private final Action openLinkAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (showingLoginData) {
                openWebsite(getWebsite());
            } else {
                Thread t = new Thread() {
                    @Override
                    public void run() {
                        if (checkPassword()) {
                            showPasswords();
                            openWebsite(getWebsite());
                        } else {
                            displayErrorWhenHidingInformation(Strings.ErrorStrings.HIDING_INFORMATION_OPEN_WEBSITE);
                        }
                    }
                };
                t.start();
            }
        }
    };

    private String getWebsite() {
        String website = (String) model.getValueAt(loginTable.convertRowIndexToModel(row), 1);
        if (!website.startsWith("http://") && !website.startsWith("https://")) {
            if (website.startsWith("www.")) {
                website = "https://" + website;
            } else {
                website = "https://www." + website;
            }
        }
        return website;
    }

    private String getWebsite(String w) {
        String website = w.toString();
        if (!website.startsWith("http://") && !website.startsWith("https://")) {
            if (website.startsWith("www.")) {
                website = "https://" + website;
            } else {
                website = "https://www." + website;
            }
        }
        return website;
    }

    private final Action copyWebsiteAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (Login.isHideWebsite()) {
                if (showingLoginData) {
                    copyStringToClipboard(getWebsite(), Strings.StatusStrings.CLIPBOARD_COPY_WEBSITE, null);
                } else {
                    Thread t = new Thread() {
                        @Override
                        public void run() {
                            if (checkPassword()) {
                                showPasswords();
                                copyStringToClipboard(getWebsite(), Strings.StatusStrings.CLIPBOARD_COPY_WEBSITE, null);
                            } else {
                                displayErrorWhenHidingInformation(Strings.ErrorStrings.HIDING_INFORMATION_COPY_WEBSITE);
                            }
                        }
                    };
                    t.start();
                }
            } else {
                copyStringToClipboard(getWebsite(), Strings.StatusStrings.CLIPBOARD_COPY_WEBSITE, null);
            }
        }
    };

    private final Action copyUsernameAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (Login.isHideUsername()) {
                if (showingLoginData) {
                    copyStringToClipboard((String) model.getValueAt(loginTable.convertRowIndexToModel(row), 2), Strings.StatusStrings.CLIPBOARD_COPY_USERNAME, null);
                } else {
                    Thread t = new Thread() {
                        @Override
                        public void run() {
                            if (checkPassword()) {
                                showPasswords();
                                copyStringToClipboard((String) model.getValueAt(loginTable.convertRowIndexToModel(row), 2), Strings.StatusStrings.CLIPBOARD_COPY_USERNAME, null);
                            } else {
                                displayErrorWhenHidingInformation(Strings.ErrorStrings.HIDING_INFORMATION_COPY_USERNAME);
                            }
                        }
                    };
                    t.start();
                }
            } else {
                copyStringToClipboard((String) model.getValueAt(loginTable.convertRowIndexToModel(row), 2), Strings.StatusStrings.CLIPBOARD_COPY_USERNAME, null);
            }
        }
    };

    private final Action copyPasswordAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (showingLoginData) {
                copyStringToClipboard((String) model.getValueAt(loginTable.convertRowIndexToModel(row), 3), Strings.StatusStrings.CLIPBOARD_COPY_PASSWORD_FOR_WEBSITE, (String) model.getValueAt(loginTable.convertRowIndexToModel(row), 1));
            } else {
                Thread t = new Thread() {
                    @Override
                    public void run() {
                        if (checkPassword()) {
                            showPasswords();
                            copyStringToClipboard((String) model.getValueAt(loginTable.convertRowIndexToModel(row), 3), Strings.StatusStrings.CLIPBOARD_COPY_PASSWORD_FOR_WEBSITE, (String) model.getValueAt(loginTable.convertRowIndexToModel(row), 1));
                        } else {
                            displayErrorWhenHidingInformation(Strings.ErrorStrings.HIDING_INFORMATION_COPY_PASSWORD);
                        }
                    }
                };
                t.start();
            }
        }
    };

    public void startIdleTimer() {
        if (timer == null) {
            timer = new IdleTimer();
            timer.start();
        }
    }

    IdleTimer timer;

    private class IdleTimer extends Thread {

        @Override
        public void run() {
            while (true) {
                if (file != null) {
                    if (Settings.getUserIdleSeconds() > 0 && showingLoginData) {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException ex) {
                            MainWindow.showError(ex, Strings.ErrorStrings.EXCEPTION_INTERRUPTED);
                            break;
                        }
                        if (System.getProperty("os.name").contains("Windows")) {
                            idleLabel.setVisible(true);
                            idleLabel.setEnabled(true);
                            int idleTime = Win32IdleTime.getIdleTimeSeconds();
                            if (idleTime > Settings.getUserIdleSeconds()) {
                                if (Settings.getUserIdleSeconds() > 0 && showingLoginData) {
                                    hidePasswords();
                                }
                            } else {
                                idleLabel.setText("Idle for:" + idleTime + "/" + Settings.getUserIdleSeconds());
                            }
                        }
                    } else {
                        if (Settings.getUserIdleSeconds() > 0) {
                            idleLabel.setVisible(true);
                            idleLabel.setEnabled(false);
                            try {
                                Thread.sleep(1000);
                            } catch (Exception ex) {
                                MainWindow.showError(ex, Strings.ErrorStrings.EXCEPTION_INTERRUPTED);
                            }
                        } else {
                            try {
                                Thread.sleep(2000);
                            } catch (Exception ex) {
                                MainWindow.showError(ex, Strings.ErrorStrings.EXCEPTION_INTERRUPTED);
                            }
                            idleLabel.setVisible(false);
                            idleLabel.setEnabled(false);
                        }
                    }
                } else {
                    try {
                        Thread.sleep(2000);
                    } catch (Exception ex) {
                        MainWindow.showError(ex, Strings.ErrorStrings.EXCEPTION_INTERRUPTED);
                    }
                    idleLabel.setVisible(false);
                    idleLabel.setEnabled(false);
                }
            }
        }
    }

    private final Action pasteAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            try {
                model.setValueAt(clipboard.getData(DataFlavor.stringFlavor), row, col);
                showStatus(Strings.StatusStrings.PASTED_TEXT + clipboard.getData(DataFlavor.stringFlavor));
            } catch (UnsupportedFlavorException | IOException ex) {
                MainWindow.showError(ex, Strings.ErrorStrings.ERROR_PASTE);
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

    private final Action moveUpAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            moveUp();
        }
    };

    private void moveUp() {
        if (!showingLoginData) {
            return;
        }
        if (row == 0) {
            return;
        }
        int selectedId = new Integer((String) model.getValueAt(loginTable.convertRowIndexToModel(row), 0));
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

    private final Action moveDownAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            moveDown();
        }
    };

    private void moveDown() {
        if (!showingLoginData) {
            return;
        }
        if (row == model.getRowCount()) {
            return;
        }
        int selectedId = new Integer((String) model.getValueAt(loginTable.convertRowIndexToModel(row), 0));
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

    public void displayErrorWhenHidingInformation(String action) {
        showStatus("Cannot " + action + ". You need to click on 'Hide Information' first.");
    }

    public static void copyStringToClipboard(String string, String before, String websiteIfStringIsPassword) {
        StringSelection stringSelection = new StringSelection(string);
        Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
        clpbrd.setContents(stringSelection, null);
        if (websiteIfStringIsPassword != null) {
            showStatus(before + websiteIfStringIsPassword + Strings.StatusStrings.CLIPBOARD_COPY);
        } else {
            showStatus(before + string + Strings.StatusStrings.CLIPBOARD_COPY);
        }
    }

    public static void openWebsite(String website) {
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(new URI(website));
            } catch (URISyntaxException | IOException ex) {
                MainWindow.showError(ex, Strings.ErrorStrings.ERROR_COULD_NOT_OPEN_WEBSITE + website);
            }
        } else {
            showError(null, Strings.ErrorStrings.ERROR_COULD_NOT_OPEN_WEBSITE + website);
        }
    }

    public static void showStatus(String status) {
        trayIcon.displayMessage(Strings.getAppTitle(), status, TrayIcon.MessageType.NONE);
        System.out.println(status);
    }

    public static void showError(Throwable t, String errorDescription) {
        if (t != null) {
            LOG.log(Level.SEVERE, null, t);
            trayIcon.displayMessage(Strings.getAppTitle(), errorDescription + "\n" + t.getLocalizedMessage(), TrayIcon.MessageType.ERROR);
        } else {
            trayIcon.displayMessage(Strings.getAppTitle(), errorDescription, TrayIcon.MessageType.ERROR);
        }
    }

    public static void toggleVisibility() {
        if (mw.isVisible() && trayIconSetupSuccessful) {
            mw.setVisible(false);
            toggleMWVisibilityMenuItem.setLabel(Strings.MainWindowStrings.SHOW_MAIN_WINDOW);
        } else if (!mw.isVisible()) {
            if (trayIconSetupSuccessful) {
                toggleMWVisibilityMenuItem.setLabel(Strings.MainWindowStrings.HIDE_MAIN_WINDOW);
            }
            mw.setExtendedState(Settings.getWindowState());
            mw.setVisible(true);
            mw.toFront();
        }
    }

}
