package be.tobiridi.passwordsecurity.data.database.converters;

import androidx.room.TypeConverter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeConverters {
    @TypeConverter
    public String localDateTimeToString(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    @TypeConverter
    public LocalDateTime stringToLocalDateTime(String dateTime) {
        return dateTime == null ? null : LocalDateTime.parse(dateTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
