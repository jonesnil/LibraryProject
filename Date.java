
public class Date {

	private int year;
	private Month month;
	private int day;
	private int hour;
	private int minute;
	private int second;
	
	public int getYear(){ return year;}
	public Month getMonth(){ return month;}
	public int getDay(){ return day;}
	public int getHour(){ return hour;}
	public int getMinute(){ return minute;}
	public int getSecond(){ return second;}
	public void setYear(int n){ year = n;}
	public void setMonth(Month m){ month = m;}
	public void setDay(int n){ day = n;}
	public void setHour(int n){ hour = n;}
	public void setMinute(int n){ minute = n;}
	public void setSecond(int n){ second = n;}
	
	public Date(int y, Month m, int d, int h, int min, int s) {
		year = y;
		month = m;
		day = d;
		hour = h;
		minute = min;
		second = s;
	}
	
	
}