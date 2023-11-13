/*
 * File: SongRequestDisplay.java
 * 
 * This program acts as a canvas for
 * SongRequestView.java and impliments
 * ComponentListener
 */

import acm.graphics.*;
import acm.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;

public class SongRequestDisplay extends GCanvas implements ComponentListener {
	
/** the names of the main and temporary text files */
	private static final String MAIN = "SongRequestList.txt";
	private static final String TEMP = "TempSongRequestList.txt";
	
/** height of the requests box */
	private static final int BOX_HEIGHT = 20;
	
/** max songs that can be showed onto the screen */
	private static final int MAX_SONGS = 1000;
	
/* SongRequestDisplay method adds component listener */
	public SongRequestDisplay() {
		addComponentListener(this);
	}
	
/* displays requests onto the screen */
	public void displayRequests() {
		removeAll();
		initValues();
		initArrays();
		getTracks();
		initVoteNum();
		displaySongs();
	}

/* initializes private instance variables values */
	private void initValues() {
		nVote = 0;
		nextBox = 0;
		songsEntered = 0;
		numIndSongs = 0;
		requests = "";
	}
	
/* initializes private instance variable arrays */
	private void initArrays() {
		track = new String[MAX_SONGS + 1];
		artist = new String[MAX_SONGS];
		vote = new int[MAX_SONGS];
		orderedVote = new int[MAX_SONGS];
		requestBox = new GCompound[MAX_SONGS + 1];
	}
	
/* gets tracks from a text file */
	private void getTracks() {
		nTrack = 0;
		BufferedReader rd = openFile(MAIN);
		try {
			while(nTrack < MAX_SONGS) {
				String line = rd.readLine();
				// if line is blank, stops getting tracks
				if(line == null || line == "") break;
				parseLine(line);
				nTrack++;
				songsEntered++;
			}
			rd.close();
		} catch (IOException ex) {
			throw new ErrorException (ex);
		}
	}

/* opens a file to read */
	private BufferedReader openFile(String file) {
		BufferedReader rd = null;
		while(rd == null) {
			try { 
				rd = new BufferedReader(new FileReader(file));
			} catch (IOException ex) {
				throw new ErrorException (ex);
			}
		}
		return rd;
	}
	
/* gets track and artist from string */
	private void parseLine(String line) {
		// gets the name of track
		int trackNameStart = line.indexOf("[") + 1;
		int trackNameEnd = line.indexOf("]");
		track[nTrack] = line.substring(trackNameStart, trackNameEnd);
		// gets the name of artist
		int artistNameStart = line.indexOf("[", trackNameEnd + 1) + 1;
		int artistNameEnd = line.indexOf("]", trackNameEnd + 1);
		artist[nTrack] = line.substring(artistNameStart, artistNameEnd);
	}
	
/* counts how many times each track was requested per track */
	private void initVoteNum() {
		for(int i = 0; i < songsEntered; i++) {
			vote[i] = 0;
			countTracks(i);
		}
	}

/* counts number of times track was requested */
	private void countTracks(int nVote) {
		for(int i = 0; i < songsEntered; i++) {
			if(track[i] == null) break;
			if(track[nVote].equalsIgnoreCase(track[i])) {
				vote[nVote]++;
			}
		}
	}
	
/* displays requests onto canvas */
	private void displaySongs() {
		add(requestBox("Track", "Artist", 0, Color.LIGHT_GRAY), 0, nextBox);
		nTrack = 0;
		while(track[nTrack] != null) {
			initOrderedVote();
		}
		orderVotes();
		nTrack = 0;
		requests = "";
		displayRequestBoxes();
	}

/* box made per each request */
	private GCompound requestBox(String label1, String label2, int num, Color color) {
		int nBox = getNBox(num);
		requestBox[nBox] = new GCompound();
		setBoxes();
		setBoxColor(color);
		GLabel labVote = new GLabel(numVote(num));
		GLabel lab1 = new GLabel(label1);
		GLabel lab2 = new GLabel(checkLab(label2));
		addToCompound(nBox, labVote, lab1, lab2);
		return requestBox[nBox];
	}

/* returns the number of which request box to add to screen */
	private int getNBox(int num) {
		if(num == 0) {
			return num;
		} else {
			return nTrack + 1;
		}
	}

/* sets each box in request box */
	private void setBoxes() {
		box1 = new GRect(BOX_HEIGHT, BOX_HEIGHT);
		box2 = new GRect((getWidth() - box1.getWidth() - 4) / 2, BOX_HEIGHT);
		box3 = new GRect((getWidth() - box1.getWidth() - 4) / 2, BOX_HEIGHT);
	}
	
/* colors the boxes accordingly */
	private void setBoxColor(Color color) {
		box1.setFilled(true);
		box2.setFilled(true);
		box3.setFilled(true);
		box1.setFillColor(color);
		box2.setFillColor(color);
		box3.setFillColor(color);
	}
	
/* returns what string to add to screen for
 * number of times track is requested
 */
	private String numVote(int num) {
		if(num == 0) {
			return "#";
		} else {
			return ("" + num);
		}
	}

/* if artist entered is blank, UNKNOWN will be
 * added onto screen
 */
	private String checkLab(String lab) {
		if(lab.equals("")) {
			lab = "UNKNOWN";
		}
		return lab;
	}
	
/* adds each object to entire request box compound */
	private void addToCompound(int nBox, GLabel labVote, GLabel lab1, GLabel lab2) {
		requestBox[nBox].add(box1, 2, 0);
		requestBox[nBox].add(box2, box1.getX() + box1.getWidth(), 0);
		requestBox[nBox].add(box3, box2.getX() + box2.getWidth(), 0);
		requestBox[nBox].add(lab1, box2.getX() + (box2.getWidth() - lab1.getWidth()) / 2, (box2.getHeight() + lab1.getAscent()) / 2);
		requestBox[nBox].add(lab2, box3.getX() + (box3.getWidth() - lab2.getWidth()) / 2, (box3.getHeight() + lab1.getAscent()) / 2);
		requestBox[nBox].add(labVote, box1.getX() + (box1.getWidth() - labVote.getWidth()) / 2, (box1.getHeight() + labVote.getAscent()) / 2);
	}
	
/* initializes values of ordered vote array to
 * equal values of vote array values
 */
	private void initOrderedVote() {
		String temp = track[nTrack];
		int index = requests.indexOf("[" + temp.toUpperCase() + "]");
		if(index == -1) {
			if(vote[nTrack] != 0) {
				orderedVote[nVote] = vote[nTrack];
				nVote++;
				numIndSongs++;
			}
		}
		requests += "[" + temp.toUpperCase() + "]";
		nTrack++;
	}
	
/* orders the ordered vote array from largest number */
	private void orderVotes() {
		for(int i = 0; i < numIndSongs; i++) {
			for(int j = i; j < numIndSongs; j++) {
				if(orderedVote[i] < orderedVote[j]) swapElements(i, j);
			}
		}
	}
	
/* swaps values with two variables */
	private void swapElements(int i, int j) {
		int temp = orderedVote[j];
		orderedVote[j] = orderedVote[i];
		orderedVote[i] = temp;
	}

/* displays requests boxes on screen */
	private void displayRequestBoxes() {
		for(int i = 0; i < numIndSongs; i++) {
			for(nTrack = 0; nTrack < songsEntered; nTrack++) {
				if(orderedVote[i] == vote[nTrack]) {
					String temp = track[nTrack];
					int index = requests.indexOf("[" + temp.toUpperCase() + "]");
					if(index == -1) {
						nextBox += BOX_HEIGHT;
						add(requestBox(track[nTrack], artist[nTrack], vote[nTrack], Color.WHITE), 0, nextBox);
					}
					requests += "[" + temp.toUpperCase() + "]";
				}
			}
		}
	}
	
/* removes individual track from requests */
	public void performAction(GObject gobj) {
		for(int i = 1; i < songsEntered + 1; i++) {
			if(gobj == requestBox[i]) {
				removeFromList(i);
				replaceMainFile();
				break;
			}
		}
	}
	
/* creates a temporary text file to recreate main file
 * without desired word to be removed
 */
	private void removeFromList(int i) {
		BufferedReader rd = openFile(MAIN);
		try {
			PrintWriter wr = new PrintWriter(new FileWriter("TempSongRequestList.txt"));
			while(true) {
				String line = rd.readLine();
				if(line == null || line == "") break;
				if(line.contains("[" + track[i - 1] + "]")) {
				} else {
					wr.println(line);
				}
			}
			rd.close();
			wr.close();
		} catch (IOException ex) {
			throw new ErrorException (ex);
		}
	}
	
/* overwrites the main file with the temporary file */
	private void replaceMainFile() {
		BufferedReader rd = openFile(TEMP);
		try {
			PrintWriter wr = new PrintWriter(new FileWriter(MAIN));
			while(true) {
				String line = rd.readLine();
				if(line == null || line == "") break;
				wr.println(line);
			}
			rd.close();
			wr.close();
		} catch (IOException ex) {
			throw new ErrorException (ex);
		}
	}
	
/* component methods */
	public void componentResized(ComponentEvent e) { displayRequests(); } // refreshes and resizes screen when component resized
	public void componentHidden(ComponentEvent e) { }
	public void componentMoved(ComponentEvent e) { }
	public void componentShown(ComponentEvent e) { }

/* private instance variables */
	private String requests;
	private String[] track, artist;
	private int nextBox, nTrack, songsEntered, nVote, numIndSongs;
	private int[] vote, orderedVote;
	private GRect box1, box2, box3;
	public GCompound[] requestBox;
}
