public class DateTime implements IDateTime {

    private final int year;
    private final int month;
    private final int day;
    private final int hour;
    private final int minute;

    public DateTime(int year, int month, int day, int hour, int minute) {
        if (year < 1
                || month < 1 || month > 12
                || day < 1 || day > daysInMonth(year, month)
                || hour < 0 || hour > 23
                || minute < 0 || minute > 59) {
            throw new IllegalArgumentException("Invalid date/time");
        }
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
    }

    public DateTime(String dateTime) {
        this(parse(dateTime));
    }

    private DateTime(int[] parts) {
        this(parts[2], parts[0], parts[1], parts[3], parts[4]);
    }

    private static int[] parse(String value) {
        if (value == null || !value.trim().matches("[0-9]{2}/[0-9]{2}/[0-9]{4,} [0-9]{2}:[0-9]{2}"))
            throw new IllegalArgumentException("Use MM/DD/YYYY HH:MM");
        String[] parts = value.trim().split("[/ :]");
        int[] numbers = new int[5];
        for (int i = 0; i < numbers.length; i++)
            numbers[i] = Integer.parseInt(parts[i]);
        return numbers;
    }

    private static int daysInMonth(int year, int month) {
        if (month == 2) {
            if (year % 400 == 0 || (year % 4 == 0 && year % 100 != 0))
                return 29;
            return 28;
        }
        if (month == 4 || month == 6 || month == 9 || month == 11)
            return 30;
        return 31;
    }

    @Override
    public int getYear() {
        return year;
    }

    @Override
    public int getMonth() {
        return month;
    }

    @Override
    public int getDay() {
        return day;
    }

    @Override
    public int getHour() {
        return hour;
    }

    @Override
    public int getMinute() {
        return minute;
    }

    @Override
    public String format() {
        return String.format("%02d/%02d/%04d %02d:%02d", month, day, year, hour, minute);
    }

    @Override
    public int compareTo(IDateTime other) {
        if (year != other.getYear())
            return Integer.compare(year, other.getYear());
        if (month != other.getMonth())
            return Integer.compare(month, other.getMonth());
        if (day != other.getDay())
            return Integer.compare(day, other.getDay());
        if (hour != other.getHour())
            return Integer.compare(hour, other.getHour());
        return Integer.compare(minute, other.getMinute());
    }

    @Override
    public String toString() {
        return format();
    }
}
