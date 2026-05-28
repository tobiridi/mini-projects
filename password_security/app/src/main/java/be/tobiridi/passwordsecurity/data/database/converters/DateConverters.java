package be.tobiridi.passwordsecurity.data.database.converters;

import androidx.room.TypeConverter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateConverters {
    @TypeConverter
    public String localDateToString(LocalDate date) {
        return date == null ? null : date.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    @TypeConverter
    public LocalDate stringToLocalDate(String date) {
        return date == null ? null : LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
    }
}
