package server.ws;

import javax.websocket.server.ServerEndpoint;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.websocket.*;

@ServerEndpoint("/serverendpoint")
public class WsServer 
{
	static ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();
	MatchScores ms = new MatchScores();
	Integer[] currentMatchDetails = new Integer[2];
	static int prevGoals = 0;
	static int prevMins = 0;
	
	private static final Set<Session> allSessions = Collections.synchronizedSet(new HashSet<Session>());
	
	@OnOpen
	public void onOpen(Session session)
	{
		allSessions.add(session);
		try
		{
			session.getBasicRemote().sendText("Mins: " + prevMins + " Goals: " + prevGoals);
		}
		catch(IOException ioe)
		{
			System.out.println(ioe.getMessage());
		}
		
		System.out.println("New Connection opened! " + allSessions.size());
		if(allSessions.size()==1)
		{
			currentMatchDetails = ms.getCurrentScore();
			prevGoals = currentMatchDetails[1];
			prevMins = currentMatchDetails[0];
			timer.scheduleAtFixedRate(()->retrieveScores(session), 0, 5, TimeUnit.SECONDS);
		}
	}
	
	public void retrieveScores(Session session)
	{
		String dataToBeSent;
		currentMatchDetails = ms.getCurrentScore();
		
		if(currentMatchDetails[0] != prevMins)
		{
			if(currentMatchDetails[1] != prevGoals)
			{
				dataToBeSent = "Mins: " + currentMatchDetails[0] + " Goals: " + currentMatchDetails[1];
			}
			else
				dataToBeSent = "Mins: " + currentMatchDetails[0];
		}
		else
		{
			if(currentMatchDetails[1] != prevGoals)
			{
				dataToBeSent = "Goals: " + currentMatchDetails[1];
			}
			else
				dataToBeSent = "No update!";
		}
		for(Session sess: allSessions)
		{
			try
			{
				sess.getBasicRemote().sendText(dataToBeSent);
			}
			catch(IOException ioe)
			{
				System.out.println(ioe.getMessage());
			}
		}
		prevGoals = currentMatchDetails[1];
		prevMins = currentMatchDetails[0];
	}
	
	@OnClose
	public void onClose(Session session)
	{
		allSessions.remove(session);
		System.out.println("Connection closed! Connections active: " + allSessions.size());
	}
	
	@OnMessage
	public void onMessage(String message)
	{
		System.out.println("Message received:" + message);
	}
	
	@OnError
	public void onError(Throwable e)
	{
		e.printStackTrace();
	}
}
