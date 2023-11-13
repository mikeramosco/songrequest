/*
 * File: SongRequestView.java
 * 
 * This program shows a list of requested tracks
 * in order from most requested.
 */

import acm.graphics.*;
import acm.io.IODialog;
import acm.program.*;
import acm.util.*;
import java.io.*;
import java.awt.Font;
import java.awt.event.*;
import javax.swing.*;

public class SongRequestView extends Program {
	
/** width and height of the application */
	public static final int APPLICATION_WIDTH = 500;
	public static final int APPLICATION_HEIGHT = 600;
	
/** the file containing all song requests */
	private static final String MAIN = "SongRequestList.txt";
	
/* init method */
	public void init() {
		setNorthInteractors();
		setSouthInteractors();
		initObjects();
		addActionListeners();
	}
	
/* run method */
	public void run() {
		while(true) {
			// refreshes every five seconds
			pause(5000);
			refresh();
		}
	}
	
/* sets interactors in the north control bar */
	private void setNorthInteractors() {
		countSongs();
		queue = new JLabel(setQueue());
		queue.setFont(new Font("TimesNewRoman", Font.BOLD + Font.ITALIC, 12));
		add(clear, NORTH);
		add(queue, NORTH);
		add(canvas);
	}

/* reads the text file and counts how many tracks are requested */
	private void countSongs() {
		numSongs = 0;
		BufferedReader rd = openFile();
		try {
			while(true) {
				String line = rd.readLine();
				if(line == null || line == "") break;
				numSongs++;
			}
			rd.close();
		} catch (IOException ex) {
			throw new ErrorException (ex);
		}
	}
	
/* opens the main text file */
	private BufferedReader openFile() {
		BufferedReader rd = null;
		while(rd == null) {
			try { 
				rd = new BufferedReader(new FileReader(MAIN));
			} catch (IOException ex) {
				throw new ErrorException (ex);
			}
		}
		return rd;
	}
	
/* returns the string for number of tracks requested */
	private String setQueue() {
		String line = "      " + numSongs + " song(s) in queue.";
		return line;
	}
	
/* sets interactors in the south control bar */
	private void setSouthInteractors() {
		add(new JLabel("TRACK:"), SOUTH);
		add(track, SOUTH);
		track.addActionListener(this);
		add(new JLabel("  ARTIST:"), SOUTH);
		add(artist, SOUTH);
		artist.addActionListener(this);
		add(new JLabel(" "), SOUTH);
		add(addList, SOUTH);
	}
	
/* initializes action and mouse listeners refreshes canvas */
	private void initObjects() {
		clear.addActionListener(this);
		canvas.addMouseListener(this);
		canvas.displayRequests();
	}
	
/* mouse pressed function */
	public void mousePressed(MouseEvent e) {
		gobj = canvas.getElementAt(new GPoint(e.getPoint()));
		if(gobj != null && gobj != canvas.requestBox[0]) {
			// runs method when track box is clicked
			deleteBox();
		}
	}
	
/* dialog box pop up asks to remove track from screen */
	private void deleteBox() {
		String remove = "Remove this track from the queue?";
		if(dialog.readBoolean(remove, "YES", "NO")) {
			canvas.performAction(gobj);
			canvas.displayRequests();
			refresh();
		}
	}

/* refreshes program */
	private void refresh() {
		countSongs();
		queue.setText(setQueue());
		canvas.displayRequests();
	}
	
/* action performed method */
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == clear) {
			// removes all tracks from requests
			clearList();
			refresh();
		} else if(e.getSource() == track || e.getSource() == artist || e.getSource() == addList) {
			if(formatText(track.getText()).length() < 1) {
				// error comes up if nothing entered
				resetTxt();
				refresh();
				dialog.showErrorMessage("Track was NOT successfully requested.");
			} else {
				// adds track to requests
				addToList();
				refresh();
			}
		}
	}

/* removes all requests from main text file */
	private void clearList() {
		try {
			PrintWriter wr = new PrintWriter(new FileWriter(MAIN));
			wr.close();
		} catch (IOException ex) {
			throw new ErrorException (ex);
		}
	}
	
/* sets texts fields to blank */
	private void resetTxt() {
		track.setText("");
		artist.setText("");
	}
	
/* adds track to requests */
	private void addToList() {
		song = "[" + formatText(track.getText()) + "][" + formatText(artist.getText()) + "]";
		resetTxt();
		addSong();
	}

/* formats texts of entered tracks */
	private String formatText(String str) {
		if(str.equals("")) {
			return "";
		} else {
			result = "";
			for(int i = 0; i < str.length(); i++) {
				char ch = Character.toUpperCase(str.charAt(i));
				if(ch >= 'A' && ch <= 'Z') {
					// only reads character if letter
					result += ch;
				}
			}
			return result;
		}
	}
	
/* adds track to last line of request text file */
	private void addSong() {
	    BufferedWriter writer = null;
	    try {
	        writer = new BufferedWriter(new FileWriter(MAIN, true));
	        writer.append(song);
	        writer.newLine();
	        writer.close();
	    } catch (Exception e) {
	    }
	}
	
/* private instance variables */
	private JButton clear = new JButton("CLEAR LIST"), addList = new JButton("ADD TO QUEUE");
	private JLabel queue;
	private int numSongs;
	private GObject gobj;
	private String song, result;
	private JTextField track = new JTextField(10), artist = new JTextField(10);
	private SongRequestDisplay canvas = new SongRequestDisplay();
	private IODialog dialog = getDialog();
}
