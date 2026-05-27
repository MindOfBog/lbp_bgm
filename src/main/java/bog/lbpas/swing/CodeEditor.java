package bog.lbpas.swing;

import bog.lbpas.Main;
import bog.lbpas.view3d.utils.TextIcon;
import bog.lbpas.view3d.utils.print;
import com.formdev.flatlaf.FlatLaf;
import org.fife.ui.rsyntaxtextarea.*;
import org.fife.ui.rsyntaxtextarea.parser.ParserNotice;
import org.fife.ui.rtextarea.*;
import org.joml.Math;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public abstract class CodeEditor {

    public static TextIcon errorOnLineIcon = new TextIcon(">", Color.RED, 14, 16, 16);

    private RSyntaxTextArea codeArea;
    private JPanel dialogPanel;
    private RTextScrollPane codeScrollPane;
    private JTextField findText;
    private JTextField replaceText;
    private JButton replaceNextButton;
    private JButton replaceAllButton;
    private JButton findAllButton;
    private JCheckBox matchCaseCheckBox;
    private JCheckBox matchWholeWordCheckBox;
    private JCheckBox regexCheckBox;
//    private JCheckBox findInSelectionCheckBox;
    private JButton findNextButton;
    private JPanel searchPanel;
    private JButton closeSearchPanelButton;
    private JButton findPrevButton;
    private JButton replacePrevButton;
    private JButton searchButton;
    private JButton undoButton;
    private JButton redoButton;
    private JPanel searchReplacePanel;
    private JButton gotoButton;
    private JPanel gotoPanel;
    private JSpinner goToLineSpinner;
    private JButton goToLine;
    private JButton closeGoToPanel;
    private JButton saveChangesButton;
    private JPanel errorPanel;
    private JTextArea errorTextArea;
    private JButton closeErrorPanelButton;

    SearchContext searchContext;
    private JFrame window;

    private boolean isDirty = false;
    private String baseTitle;

    public CodeEditor(String title, String content, String formatting) {
        this.baseTitle = title;
        SwingUtilities.invokeLater(() ->
        {
            searchContext = new SearchContext();
            searchContext.setSearchWrap(true);

            searchPanel.setVisible(false);
            gotoPanel.setVisible(false);
            errorPanel.setVisible(false);

            closeSearchPanelButton.setFocusPainted(false);
            closeSearchPanelButton.setBorderPainted(false);
            closeSearchPanelButton.setContentAreaFilled(false);
            closeSearchPanelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            closeSearchPanelButton.putClientProperty("FlatLaf.style", "font: 145%");
            closeSearchPanelButton.addActionListener(e -> searchPanel.setVisible(false));

            closeGoToPanel.setFocusPainted(false);
            closeGoToPanel.setBorderPainted(false);
            closeGoToPanel.setContentAreaFilled(false);
            closeGoToPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            closeGoToPanel.putClientProperty("FlatLaf.style", "font: 145%");
            closeGoToPanel.addActionListener(e -> gotoPanel.setVisible(false));

            closeErrorPanelButton.setFocusPainted(false);
            closeErrorPanelButton.setBorderPainted(false);
            closeErrorPanelButton.setContentAreaFilled(false);
            closeErrorPanelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            closeErrorPanelButton.putClientProperty("FlatLaf.style", "font: 145%");
            closeErrorPanelButton.addActionListener(e -> errorPanel.setVisible(false));

            searchButton.addActionListener(e -> showSearchDialog());
            gotoButton.addActionListener(e -> gotoPanel.setVisible(true));
            undoButton.addActionListener(e -> codeArea.undoLastAction());
            redoButton.addActionListener(e -> codeArea.redoLastAction());
            searchButton.putClientProperty("FlatLaf.style", "font: 175%");
            undoButton.putClientProperty("FlatLaf.style", "font: 175%");
            redoButton.putClientProperty("FlatLaf.style", "font: 175%");
            gotoButton.putClientProperty("FlatLaf.style", "font: 175%");

            saveChangesButton.putClientProperty("FlatLaf.style", "font: 175%");
            saveChangesButton.addActionListener(e -> saveChanges());

            findNextButton.addActionListener(e ->
            {
                if (findText.getText().isEmpty())
                    return;

                setupSearch();
                searchContext.setSearchForward(true);
                SearchEngine.find(codeArea, searchContext);
            });
            findPrevButton.addActionListener(e ->
            {
                if (findText.getText().isEmpty())
                    return;

                setupSearch();
                searchContext.setSearchForward(false);
                SearchEngine.find(codeArea, searchContext);
            });
            findAllButton.addActionListener(e ->
            {
                if (findText.getText().isEmpty())
                    return;

                setupSearch();
                searchContext.setMarkAll(true);
                SearchEngine.find(codeArea, searchContext);
            });
            replaceNextButton.addActionListener(e ->
            {
                if (findText.getText().isEmpty() || replaceText.getText().isEmpty())
                    return;

                setupSearch();
                searchContext.setSearchForward(true);
                SearchEngine.replace(codeArea, searchContext);
            });
            replacePrevButton.addActionListener(e ->
            {
                if (findText.getText().isEmpty() || replaceText.getText().isEmpty())
                    return;

                setupSearch();
                searchContext.setSearchForward(false);
                SearchEngine.replace(codeArea, searchContext);
            });
            replaceAllButton.addActionListener(e ->
            {
                if (findText.getText().isEmpty() || replaceText.getText().isEmpty())
                    return;

                setupSearch();
                searchContext.setMarkAll(true);
                SearchEngine.replaceAll(codeArea, searchContext);
            });

            findText.addActionListener(e ->
            {
                if (findText.getText().isEmpty())
                    return;

                setupSearch();
                searchContext.setSearchForward(true);
                SearchEngine.find(codeArea, searchContext);
            });

            replaceText.addActionListener(e ->
            {
                if (findText.getText().isEmpty() || replaceText.getText().isEmpty())
                    return;

                setupSearch();
                searchContext.setSearchForward(true);
                SearchEngine.replace(codeArea, searchContext);
            });

            goToLineSpinner.setValue(1);
            goToLineSpinner.addChangeListener(e ->
            {
                try {
                    goToLineSpinner.commitEdit();
                } catch (java.text.ParseException ignored) {}

                if (goToLineSpinner.getValue() == null)
                    return;

                try {
                    int line = Math.clamp(0, codeArea.getLineCount() - 1, ((SpinnerNumberModel) goToLineSpinner.getModel()).getNumber().intValue() - 1);
                    codeArea.setCaretPosition(codeArea.getLineStartOffset(line));
                } catch (BadLocationException ex) {print.stackTrace(ex);}
            });

            goToLine.addActionListener(e ->
            {
                try {
                    goToLineSpinner.commitEdit();
                } catch (java.text.ParseException ignored) {}

                if (goToLineSpinner.getValue() == null)
                    return;

                try {
                    int line = Math.clamp(0, codeArea.getLineCount() - 1, ((SpinnerNumberModel) goToLineSpinner.getModel()).getNumber().intValue() - 1);
                    codeArea.setCaretPosition(codeArea.getLineStartOffset(line));
                } catch (BadLocationException ex) {print.stackTrace(ex);}
            });

            codeScrollPane.setLineNumbersEnabled(true);
            codeScrollPane.setFoldIndicatorEnabled(true);
            codeScrollPane.setIconRowHeaderEnabled(true);

            codeArea.addKeyListener(new KeyAdapter()
            {
                @Override
                public void keyPressed(KeyEvent e)
                {
                    if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_F)
                        showSearchDialog();
                }
            });
            codeArea.addKeyListener(new KeyAdapter()
            {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_G)
                        gotoPanel.setVisible(true);
                }
            });
            codeArea.addKeyListener(new KeyAdapter()
            {
                @Override
                public void keyPressed(KeyEvent e)
                {
                    if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_S)
                        saveChanges();
                }
            });

            codeArea.setCodeFoldingEnabled(true);
            codeArea.setSyntaxEditingStyle(formatting);//SyntaxConstants.SYNTAX_STYLE_JSON
            codeArea.setText(content);
            codeArea.discardAllEdits();
            codeArea.setCaretPosition(0);
            Gutter gutter = RSyntaxUtilities.getGutter(codeArea);
            codeArea.addParser(new SyntaxParser(gutter));

            codeArea.addPropertyChangeListener(RSyntaxTextArea.PARSER_NOTICES_PROPERTY, e -> {
                if (gutter != null) {
                    gutter.removeAllTrackingIcons();
                    boolean hasErrors = false;
                    String errorMessages = "";

                    for (ParserNotice notice : codeArea.getParserNotices()) {
                        try {
                            gutter.addLineTrackingIcon(notice.getLine(), errorOnLineIcon);
                            hasErrors = true;
                            errorMessages += notice.getMessage();
                        } catch (BadLocationException ex) {
                        }
                    }

                    errorTextArea.setText(errorMessages);
                    errorPanel.setVisible(hasErrors);
                }
            });

            codeArea.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    setDirty(true);
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    setDirty(true);
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                }
            });

            findText.putClientProperty("JTextField.placeholderText", "Find...");
            replaceText.putClientProperty("JTextField.placeholderText", "Replace with...");
            goToLineSpinner.putClientProperty("JTextField.placeholderText", "GoTo Line...");

            try {
                boolean isDark = FlatLaf.isLafDark();

                String themePath = isDark
                        ? "/org/fife/ui/rsyntaxtextarea/themes/monokai.xml"
                        : "/org/fife/ui/rsyntaxtextarea/themes/eclipse.xml";

                Theme theme = Theme.load(getClass().getResourceAsStream(themePath));
                theme.apply(codeArea);
            } catch (Exception e) {
                print.stackTrace(e);
            }

            errorTextArea.setBackground(UIManager.getColor("TextArea.background"));

            this.window = new JFrame();
            this.window.setIconImages(Main.iconList);
            this.window.setMinimumSize(new Dimension(800, 500));
            this.window.setContentPane(this.dialogPanel);
            this.window.setTitle(title);
            this.window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            this.window.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    closeWindow();
                }
            });
            this.window.pack();

            Rectangle usableBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
            int maxWidth = (int) (usableBounds.width * 0.8);
            int maxHeight = (int) (usableBounds.height * 0.8);
            int finalWidth = Math.min(this.window.getWidth(), maxWidth);
            int finalHeight = Math.min(this.window.getHeight(), maxHeight);
            this.window.setSize(finalWidth, finalHeight);

            this.window.setLocationRelativeTo(null);
            this.window.setVisible(true);

            this.window.toFront();
            this.window.requestFocus();
        });
    }

    private void setDirty(boolean dirty)
    {
        this.isDirty = dirty;
        this.window.setTitle(baseTitle + (isDirty ? " *" : ""));
    }

    private void setupSearch()
    {
        searchContext.setSearchFor(findText.getText());
        searchContext.setReplaceWith(replaceText.getText());
        searchContext.setMatchCase(matchCaseCheckBox.isSelected());
        searchContext.setWholeWord(matchWholeWordCheckBox.isSelected());
        searchContext.setRegularExpression(regexCheckBox.isSelected());
//        context.setSearchSelectionOnly(findInSelectionCheckBox.isSelected());
    }

    private void showSearchDialog() {
        findText.setText(codeArea.getSelectedText());
        searchPanel.setVisible(true);
    }

    public void closeWindow() {
        if (!isDirty) {
            window.dispose();
            return;
        }

        int option = JOptionPane.showConfirmDialog(window,
                "Would you like to save \"" + baseTitle + "\" before closing?",
                "Unsaved Changes in \"" + baseTitle + "\"",
                JOptionPane.YES_NO_CANCEL_OPTION);

        if (option == JOptionPane.YES_OPTION)
        {
            saveChanges();
            setDirty(false);
            window.dispose();
        }
        else if (option == JOptionPane.NO_OPTION)
            window.dispose();
    }

    public void forceCloseWindow() {
            window.dispose();
    }

    private void saveChanges()
    {
        if(onSaveChanges(codeArea.getText()))
            setDirty(false);
    }

    public abstract boolean onSaveChanges(String content);
}
