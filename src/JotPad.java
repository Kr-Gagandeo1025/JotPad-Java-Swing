import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.undo.*;

public class JotPad extends JFrame{
    private JTextPane textPane;
    private JFileChooser fileChooser;
    private UndoManager undoManager;

    public JotPad(){
        setTitle("Jot Pad");
        setSize(800,600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        textPane = new JTextPane();
        fileChooser = new JFileChooser();
        undoManager = new UndoManager();

        StyledDocument doc = textPane.getStyledDocument();
        doc.addUndoableEditListener(e -> undoManager.addEdit(e.getEdit()));

        add(new JScrollPane(textPane),BorderLayout.CENTER);
        MenuBar();
        Toolbar();

        setVisible(true);
    }

    public void MenuBar(){
        JMenuBar menuBar = new JMenuBar();

//        file menu section - menu bar
        JMenu fileMenu = new JMenu("File");
        JMenuItem newFile = new JMenuItem("New - ctrl+n");
        JMenuItem openFile = new JMenuItem("Open - ctrl+o");
        JMenuItem saveFile = new JMenuItem("Save - ctrl+s");

        newFile.addActionListener(e -> textPane.setText(""));
        openFile.addActionListener(e -> openFile());
        saveFile.addActionListener(e -> saveFile());

        fileMenu.add(newFile);
        fileMenu.add(openFile);
        fileMenu.add(saveFile);

        menuBar.add(fileMenu);

//        edit menu section - menu bar
        JMenu editMenu = new JMenu("Edit");
        JMenuItem undo = new JMenuItem("Undo - ctrl+z");
        JMenuItem redo = new JMenuItem("Redo - ctrl+y");
        JMenuItem cut = new JMenuItem("Cut - ctrl+x");
        JMenuItem copy = new JMenuItem("Copy - ctrl+c");
        JMenuItem paste = new JMenuItem("Paste - ctrl+p");

        undo.addActionListener(e -> {
            if (undoManager.canUndo()){
                undoManager.undo();
            }
        });
        redo.addActionListener(e -> {
            if (undoManager.canRedo()){
                undoManager.redo();
            }
        });
        cut.addActionListener(e -> textPane.cut());
        copy.addActionListener(e -> textPane.copy());
        paste.addActionListener(e -> textPane.paste());

        editMenu.add(undo);
        editMenu.add(redo);
        editMenu.addSeparator();
        editMenu.add(cut);
        editMenu.add(copy);
        editMenu.add(paste);

        menuBar.add(editMenu);

//        setting the Menu Bar
        setJMenuBar(menuBar);

    }

    public void Toolbar(){
        JToolBar toolBar = new JToolBar();

        JButton boldButton = new JButton("B");
        JButton italicsButton = new JButton("I");
        JButton underlineButton = new JButton("U");
        JButton fontSizeButton = new JButton("Font Size");
        JButton fontColorButton = new JButton("Font Color");
        boldButton.addActionListener(e -> toggleStyle(StyleConstants.Bold));
        italicsButton.addActionListener(e -> toggleStyle(StyleConstants.Italic));
        underlineButton.addActionListener(e -> toggleStyle(StyleConstants.Underline));
        fontSizeButton.addActionListener(e -> setFontSize());
        fontColorButton.addActionListener(e -> setFontColor());

        toolBar.add(boldButton);
        toolBar.add(italicsButton);
        toolBar.add(underlineButton);
        toolBar.add(fontSizeButton);
        toolBar.add(fontColorButton);

        add(toolBar, BorderLayout.NORTH);
    }

    private void toggleStyle(Object style){
        StyledDocument doc = textPane.getStyledDocument();
        SimpleAttributeSet attr = new SimpleAttributeSet();
        StyleConstants.setBold(attr, style == StyleConstants.Bold);
        StyleConstants.setItalic(attr, style == StyleConstants.Italic);
        StyleConstants.setUnderline(attr, style == StyleConstants.Underline);
        doc.setCharacterAttributes(textPane.getSelectionStart(),
                textPane.getSelectionEnd() - textPane.getSelectionStart(),attr,false);

    }

    private void setFontSize(){
        String sizeStr = JOptionPane.showInputDialog("Enter Font Size:");
        if(sizeStr != null){
            try{
                int size = Integer.parseInt(sizeStr);
                SimpleAttributeSet attr = new SimpleAttributeSet();
                StyleConstants.setFontSize(attr, size);
                textPane.setCharacterAttributes(attr, false);
            }catch (NumberFormatException e){
                JOptionPane.showMessageDialog(this, "Invalid font size!");
            }
        }
    }

    private void setFontColor(){
        Color color = JColorChooser.showDialog(this, "Choose Font Color", Color.BLACK);
        if (color != null){
            SimpleAttributeSet attr = new SimpleAttributeSet();
            StyleConstants.setForeground(attr, color);
            textPane.setCharacterAttributes(attr, false);
        }
    }

    private void openFile(){
        int option = fileChooser.showOpenDialog(this);
        if(option == JFileChooser.APPROVE_OPTION){
            try(BufferedReader reader = new BufferedReader(new FileReader(fileChooser.getSelectedFile()))){
                textPane.read(reader, null);
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    private void saveFile(){
        int option = fileChooser.showSaveDialog(this);
        if(option == JFileChooser.APPROVE_OPTION){
            try(BufferedWriter writer = new BufferedWriter(new FileWriter(fileChooser.getSelectedFile()))){
                textPane.write(writer);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }


    public static void main(String[]args){
        SwingUtilities.invokeLater(JotPad::new);
    }
}