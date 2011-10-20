package org.mozilla.profiler.sps.gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.metal.MetalLookAndFeel;

import org.mozilla.profiler.sps.SampleLog;

public class MainWindow {
	private final SampleLog log;
	private JFrame topLevelWindow;
	private JDesktopPane desktop;
	private ActionListener actionListener;

	public MainWindow(final SampleLog log) {
		try {
			UIManager.setLookAndFeel(new MetalLookAndFeel());
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		} 
        JFrame.setDefaultLookAndFeelDecorated(true);
		this.log = log;
		topLevelWindow = new JFrame("Simple Profiler System - Main Window");
		actionListener = getMenuBarActionListener();
		
        //Make the big window be indented 50 pixels from each edge
        //of the screen.
        int inset = 100;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        topLevelWindow.setBounds(inset, inset,
                  screenSize.width  - inset*2,
                  screenSize.height - inset*2);
        
        //Set up the GUI.
        desktop = new JDesktopPane(); //a specialized layered pane
        createFrame(); //create first "window"
        topLevelWindow.setContentPane(desktop);
        topLevelWindow.setJMenuBar(createMenuBar());

        //Make dragging a little faster but perhaps uglier.
        desktop.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
        
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                //Make sure we have nice window decorations.

                //Create and set up the window.
                topLevelWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                
                //Display the window.
                topLevelWindow.setVisible(true);
				openView( new ViewList(log) );
            }
        });
	}
	
	private ActionListener getMenuBarActionListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equalsIgnoreCase("quit")) {
					System.exit(0);
				} else if (e.getActionCommand().equalsIgnoreCase("quit")) {
					openView( new ViewTree(log) );
				}
			}
		};
	}
	
	private void openView(JInternalFrame win) {
        int inset = 30;
        win.setBounds(inset, inset,
                  topLevelWindow.getWidth()  - inset*3 - 10,
                  topLevelWindow.getHeight() - inset*3 - 10);
        win.setVisible(true);
		desktop.add(win);        
		try {
            win.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {}
	}

	private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu;
        JMenuItem menuItem;
        
        //Set up the top level menu.
        menu = new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_F);
        menuBar.add(menu);
        
        //Set up the sub menu item.
        menuItem = new JMenuItem("Quit");
        menuItem.setMnemonic(KeyEvent.VK_Q);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_Q, ActionEvent.ALT_MASK));
        menuItem.setActionCommand("quit");
        menuItem.addActionListener(actionListener);
        menu.add(menuItem);

        //Set up the top level menu.
        menu = new JMenu("View");
        menu.setMnemonic(KeyEvent.VK_V);
        menuBar.add(menu);
        
        //Set up the sub menu item.
        menuItem = new JMenuItem("Tree");
        menuItem.setMnemonic(KeyEvent.VK_T);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_T, ActionEvent.ALT_MASK));
        menuItem.setActionCommand("tree");
        menuItem.addActionListener(actionListener);
        menu.add(menuItem);

        return menuBar;
	}

	private void createFrame() {
		
	}
}
