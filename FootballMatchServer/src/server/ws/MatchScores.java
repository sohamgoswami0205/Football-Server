package server.ws;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MatchScores 
{
	static ScheduledExecutorService timer_match = Executors.newSingleThreadScheduledExecutor();
	Integer[] arr_score = new Integer[2];
	int mins = 0;
	int goals = 0;
	
	public MatchScores()
	{
		timer_match.scheduleAtFixedRate(()->changeScores(), 0, 1, TimeUnit.SECONDS);
	}
	public void changeScores()
	{
		mins++;
		if(mins % 21 == 0)
		{
			goals++;
		}
	}
	
	public Integer[] getCurrentScore()
	{
		arr_score[0] = mins;
		arr_score[1] = goals;
		return arr_score;
	}
}
