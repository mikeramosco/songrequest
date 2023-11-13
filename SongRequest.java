/*
 * File: SongRequest.java
 * 
 * This program allows people to enter song
 * requests which will add on to a text file,
 * and which can be read from
 * SongRequestView.java
 */

import acm.program.*;
import acm.util.*;
import acm.gui.*;
import acm.io.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class SongRequest extends Program {
	
/** width and height of the application */
	public static final int APPLICATION_WIDTH = 1100;
	public static final int APPLICATION_HEIGHT = 700;
	
/** the file containing all song requests */
	private static final String MAIN = "SongRequestList.txt";
	
/* init method */
	public void init() {
		setLayers();
		addActionListeners();
	}
	
/* run method */
	public void run() {
		while(true) {
			// refreshes program every five seconds
			pause(5000);
			showNumRequest();
		}
	}
	
/* creates a table layout and sets up each layer individually */
	private void setLayers() {
		temp = "";
		setLayout(new TableLayout(6, 6));
		setFirstLayer();
		blankLayer();
		blankLayer();
		setSecondLayer();
		blankLayer();
		setThirdLayer();
	}
	
/* sets up the first layer with the title */
	private void setFirstLayer() {
		JLabel songRequests = new JLabel("Song Requests:");
		songRequests.setFont(new Font("Arial", Font.BOLD, 40));
		add(new JLabel(""));
		add(new JLabel(""));
		add(songRequests);
		add(new JLabel(""));
		add(new JLabel(""));
		add(new JLabel(""));
	}
	
/* sets up a blank layer */
	private void blankLayer() {
		for(int i = 0; i < 6; i++) {
			JLabel lab = new JLabel(" ");
			lab.setFont(new Font("TimesNewRoman", Font.BOLD, 24));
			add(lab);
		}
	}
	
/* sets up second layer with text fields */
	private void setSecondLayer() {
		setFont();
		add(trackLabel);
		add(track);
		track.addActionListener(this);
		add(artistLabel);
		add(artist);
		artist.addActionListener(this);
		add(new JLabel("      "));
		add(request);
	}
	
/* sets font size and format of interactors */
	private void setFont() {
		trackLabel = new JLabel("TRACK: ");
		trackLabel.setFont(new Font("TimesNewRoman", Font.BOLD, 25));
		artistLabel = new JLabel("            ARTIST (optional): ");
		artistLabel.setFont(new Font("TimesNewRoman", Font.BOLD, 25));
		track.setFont(new Font("TimesNewRoman", Font.BOLD, 25));
		artist.setFont(new Font("TimesNewRoman", Font.BOLD, 25));
		request.setFont(new Font("TimesNewRoman", Font.BOLD, 25));
	}
	
/* sets up third layer */
	private void setThirdLayer() {
		countSongs();
		queue = new JLabel(setQueue());
		queue.setFont(new Font("TimesNewRoman", Font.ITALIC, 20));
		add(new JLabel(""));
		add(new JLabel(""));
		add(queue);
		add(new JLabel(""));
		add(new JLabel(""));
		add(new JLabel(""));
	}

/* returns number of songs in queue */
	private String setQueue() {
		String line = "           " + numSongs + " song(s) in queue.";
		return line;
	}
	
/* counts number of songs in queue */
	private void countSongs() {
		numSongs = 0;
		BufferedReader rd = openViewFile();
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
	
/* opens text file to read and count number of songs in queue */
	private BufferedReader openViewFile() {
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

/* action performed method */
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == track || e.getSource() == artist || e.getSource() == request) {
			if(formatText(track.getText()).length() < 4 || temp.equalsIgnoreCase(formatText(track.getText()).toUpperCase())) {
				// error appears if text entered is invalid
				resetTxt();
				showNumRequest();
				dialog.showErrorMessage("Track was NOT successfully requested.");
			} else {
				// if text is valid, song is requested
				temp = formatText(track.getText()).toUpperCase();
				requestSong();
				dialog.println("Track was successfully requested.");
			}
		}
	}
	
/* sets text fields to blank */
	private void resetTxt() {
		track.setText("");
		artist.setText("");
	}

/* refreshes number of songs in queue */
	private void showNumRequest() {
		countSongs();
		queue.setText(setQueue());
	}
	
/* adds song to queue */
	private void requestSong() {
		song = "[" + formatText(track.getText()) + "][" + formatText(artist.getText()) + "]";
		resetTxt();
		addSong();
		showNumRequest();
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
					result += ch;
				}
			}
			return result;
		}
	}
	
/* adds song to main text file */
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
	private int numSongs;
	private JTextField track = new JTextField(10);
	private JTextField artist = new JTextField(10);
	private JButton request = new JButton("REQUEST");
	private JLabel queue, trackLabel, artistLabel;
	private String song, result, temp;
	private IODialog dialog = getDialog();
}
