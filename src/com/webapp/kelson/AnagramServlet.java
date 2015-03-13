package com.webapp.kelson;

import java.io.IOException;
import java.util.Arrays;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class AnagramServlet extends HttpServlet {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		// we are outputting html
		resp.setContentType("text/html");
		
		// we need to get access to the google user service
		UserService us = UserServiceFactory.getUserService();
		User u = us.getCurrentUser();
		
		PersistenceManager pm = null;
		WordList wordlist;
		MemcacheService ms = MemcacheServiceFactory.getMemcacheService();
		
		String user_input = req.getParameter("search_input");
		
		if (user_input.length() > 0) {
			
			Key key = KeyFactory.createKey("WordList", getStringOrdered(user_input));
			
			String cacheKey = "WordList:" + getStringOrdered(user_input);
			
			wordlist = (WordList) ms.get(cacheKey);
			
			if ( wordlist != null) {
				// in cache so just set the feedback
				req.setAttribute("feedback", "Anagrams with the given input were found.");
				req.setAttribute("list", wordlist.getWordList());				
			} else {
				
				// not in cache, add it if found
				try {
					pm = PMF.get().getPersistenceManager();
					wordlist = pm.getObjectById(WordList.class,key);
					
					req.setAttribute("feedback", "Anagrams with the given input were found.");
					req.setAttribute("list", wordlist.getWordList());
					ms.put(cacheKey, wordlist);
					pm.close();
					
				} catch(JDOObjectNotFoundException e) {
					req.setAttribute("feedback", "No Anagram Found with the given input !!!");
				}				
				
			}
			

			
		} else {
			req.setAttribute("feedback", "Invalid Anagram Search Input !!!");
		}
		
		
		req.setAttribute("user", u);
		
		RequestDispatcher rd = req.getRequestDispatcher("/WEB-INF/root.jsp");
		rd.forward(req, resp);		
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		PersistenceManager pm = null;
		WordList wordList;
		String user_input = req.getParameter("add_input");
		MemcacheService ms = MemcacheServiceFactory.getMemcacheService();
		
		//we need to get access to the google user service
		UserService us = UserServiceFactory.getUserService();
		User u = us.getCurrentUser();
				
		if (user_input.length() > 0) {
			
			Key key = KeyFactory.createKey("WordList", getStringOrdered(user_input));

			try {
				pm = PMF.get().getPersistenceManager();
				wordList = pm.getObjectById(WordList.class,key);
				
				if (wordList.addWord(user_input)) {
					pm.makePersistent(wordList);
					req.setAttribute("feedback", "Anagram: " + user_input + " was added to the list");	
					
					// updating the object adding a new word to the list so I delete the cache from the old object
					String cacheKey = "WordList:" + getStringOrdered(user_input);
					ms.delete(cacheKey);
				} else {
					req.setAttribute("feedback", "Anagram " + user_input +" is already on the list");
				}


			} catch(JDOObjectNotFoundException e) {
				
				// key do not exist. Create
				wordList = new WordList(key);
				wordList.addWord(user_input);
				pm.makePersistent(wordList);
				req.setAttribute("feedback", "New Anagram: " + user_input + " created");
			}
			
			pm.close();


		} else {
			req.setAttribute("feedback", "No Anagram was informed!!!");
		}
		
		RequestDispatcher rd = getServletContext().getRequestDispatcher("/WEB-INF/root.jsp");
		req.setAttribute("user", u);
		rd.forward(req, resp);
	}
	
	// function to order the key alphabetically
	public String getStringOrdered(String string) {
		
		char[] charArr = string.toCharArray(); 
		Arrays.sort(charArr); 
		String result = new String(charArr); 
		return result;
		
	}

}