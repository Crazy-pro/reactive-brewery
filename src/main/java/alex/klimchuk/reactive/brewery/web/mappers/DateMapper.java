package alex.klimchuk.reactive.brewery.web.mappers;

import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

/**
 * Copyright Alex Klimchuk (c) 2023.
 */
@Component
public class DateMapper {

    public OffsetDateTime asOffsetDateTime(Timestamp timestamp) {
        if (Objects.nonNull(timestamp)) {
            return OffsetDateTime.of(
                    timestamp.toLocalDateTime().getYear(),
                    timestamp.toLocalDateTime().getMonthValue(),
                    timestamp.toLocalDateTime().getDayOfMonth(),
                    timestamp.toLocalDateTime().getHour(),
                    timestamp.toLocalDateTime().getMinute(),
                    timestamp.toLocalDateTime().getSecond(),
                    timestamp.toLocalDateTime().getNano(),
                    ZoneOffset.UTC);
        } else {
            return null;
        }
    }

    public Timestamp asTimestamp(OffsetDateTime offsetDateTime) {
        return (Objects.nonNull(offsetDateTime))
                ? Timestamp.valueOf(offsetDateTime.atZoneSameInstant(ZoneOffset.UTC).toLocalDateTime())
                : null;
    }

}
