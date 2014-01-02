package com.example.androidtest1;

public class Record {
	private long draw;
	private long[] redBalls = new long[6];
	private long blueBall;	
	private long openDay;
	private long winners;
	private long bonus;
	private long pools;

	public long getOpenDay() {
		return openDay;
	}
	public void setOpenDay(long openDay) {
		this.openDay = openDay;
	}
	public long getWinners() {
		return winners;
	}
	public void setWinners(long winners) {
		this.winners = winners;
	}
	public long getBonus() {
		return bonus;
	}
	public void setBonus(long bonus) {
		this.bonus = bonus;
	}
	public long getPools() {
		return pools;
	}
	public void setPools(long pools) {
		this.pools = pools;
	}
	public long getDraw() {
		return draw;
	}
	public void setDraw(long draw) {
		this.draw = draw;
	}
	public long[] getRedBalls() {
		return redBalls;
	}
	public void setRedBalls(long[] redBalls) {
		this.redBalls = redBalls;
	}
	public long getBlueBall() {
		return blueBall;
	}
	public void setBlueBall(long blueBall) {
		this.blueBall = blueBall;
	}
	
	public void setRedBall(int index, long redBall)
	{
		redBalls[index - 1] = redBall;
	}
	
	public long getRedBall(int index)
	{
		return redBalls[index - 1];
	}
	
}

