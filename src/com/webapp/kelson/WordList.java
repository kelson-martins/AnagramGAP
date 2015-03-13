package com.webapp.kelson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class WordList implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// the key to the anagram word
	@PrimaryKey
	@Persistent
	private Key id;
	
	@Persistent
	private List<String> wordList;

	// constructor
	public WordList(Key id) {
		this.id = id;
		wordList = new ArrayList<String>();
	}
	
	public int wordCount() {
		return wordList.size();
	}
	
	public String getWord(final int index) {
		return wordList.get(index);
	}
	
	public boolean addWord(final String word) {
		boolean added = false;
		
		if (!wordList.contains(word)) {
			wordList.add(word);
			added = true;
		}
		
		return added;
	}

	public List<String> getWordList() {
		return wordList;
	}
	
	
}
