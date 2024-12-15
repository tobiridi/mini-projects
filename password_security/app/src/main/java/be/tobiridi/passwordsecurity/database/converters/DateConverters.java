package be.tobiridi.passwordsecurity.database.converters;

import androidx.room.TypeConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateConverters {
    @TypeConverter
    public String localDateToString(LocalDate date) {
        return date.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }
    @TypeConverter
    public LocalDate stringToLocalDate(String date) {
        return LocalDate.parse(date);
    }

}
