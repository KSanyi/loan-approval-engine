package hu.lae.util;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CalculationLog {

	public final Map<String, List<Object>> subResultsMap = new LinkedHashMap<>();

	public void put(String key, Object ... value) {
		subResultsMap.put(key, Stream.of(value).collect(Collectors.toList()));
	}
	
}
