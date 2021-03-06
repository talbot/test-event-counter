package im.tretyakov.test.eventcounter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Наивная реализация интерфейса для учета однотипных событий в системе
 * с использованием потокобезопасной кучи и Stream API.
 * <p>
 * Данная реализация обеспечивает регистрацию более 10.000 событий в секунду
 * даже при количестве поставщиков больше количества возможных одновременных потоков.
 * <p>
 * Created on 20.02.16.
 *
 * @author tretyakov (dmitry@tretyakov.im)
 */
public class EventCounterStreamImpl implements EventCounter {

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private final ConcurrentMap<Long, Long> events = new ConcurrentHashMap<>();

    /**
     * Учитывает событие
     */
    public synchronized void countEvent() {
        final long currentSecond = System.currentTimeMillis() / 1000L;
        this.events.merge(currentSecond, 1L, (a, b) -> a + b);
        this.events.entrySet().removeIf(entry -> entry.getKey() > currentSecond + 86400);
    }

    /**
     * Выдаёт число событий за последнюю минуту (60 секунд)
     *
     * @return число событий за последнюю минуту (60 секунд)
     */
    public long eventsByLastMinute() {
        return this.events.entrySet().stream().filter(
            entry -> entry.getKey() >= System.currentTimeMillis() / 1000L - 60
        ).mapToLong(Map.Entry::getValue).reduce(0, (a, b) -> a + b);
    }

    /**
     * Выдаёт число событий за последний час (60 минут)
     *
     * @return число событий за последний час (60 минут)
     */
    public long eventsByLastHour() {
        return this.events.entrySet().stream().filter(
            entry -> entry.getKey() >= System.currentTimeMillis() / 1000L - 3600
        ).mapToLong(Map.Entry::getValue).reduce(0, (a, b) -> a + b);
    }

    /**
     * Выдаёт число событий за последние сутки (24 часа)
     *
     * @return число событий за последние сутки (24 часа)
     */
    public long eventsByLastDay() {
        return this.events.entrySet().stream().filter(
            entry -> entry.getKey() >= System.currentTimeMillis() / 1000L - 86400
        ).mapToLong(Map.Entry::getValue).reduce(0, (a, b) -> a + b);
    }
}
