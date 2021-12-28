package com.st.utils.io;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import static java.util.concurrent.TimeUnit.*;


/**
 * @author: st
 * @date: 2021/12/29 03:13
 * @version: 1.0
 * @description:
 */
public class TimerUtil {
	public static void runTask(String msg, Runnable task){
		long startTime = getTimeElapsed(0);
		task.run();
		System.out.printf("%s time taken: %s%n", msg, timeToString(getTimeElapsed(startTime)));
	}

	public static String timeToString(long nanos) {

		Optional<TimeUnit> first = Stream.of(DAYS, HOURS, MINUTES, SECONDS, MILLISECONDS,
						MICROSECONDS).filter(u -> u.convert(nanos, NANOSECONDS) > 0)
				.findFirst();
		TimeUnit unit = first.isPresent() ? first.get() : NANOSECONDS;

		double value = (double) nanos / NANOSECONDS.convert(1, unit);
		return String.format("%.4g %s", value, unit.name().toLowerCase());
	}

	private static long getTimeElapsed(long startTime) {
		return System.nanoTime() - startTime;
	}

}
