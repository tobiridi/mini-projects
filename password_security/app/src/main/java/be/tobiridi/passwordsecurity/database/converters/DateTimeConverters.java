package be.tobiridi.passwordsecurity.database.converters;

import androidx.room.TypeConverter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeConverters {
    @TypeConverter
    public String localDateTimeToString(LocalDateTime date) {
        return date.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    @TypeConverter
    public LocalDateTime stringToLocalDateTime(String date) {
        return LocalDateTime.parse(date, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
