package com.tyron.code.desktop.util;

import org.jetbrains.annotations.NotNull;

/**
 * General number parsing and other operations.
 *
 * @author Matt Coley
 */
public class NumberUtil {
	/**
	 * @param input
	 * 		Text input that represents a number.
	 * 		<ul>
	 * 		<li>Numbers ending with {@code F} are parsed as {@link Float}.</li>
	 * 		<li>Numbers ending with {@code D} are parsed as {@link Double}.</li>
	 * 		<li>Numbers ending with {@code L} are parsed as {@link Long}.</li>
	 * 		</ul>
	 *
	 * @return Number parsed from text.
	 * Can be an {@link Integer}, {@link Long}, {@link Float}, or {@link Double}.
	 *
	 * @throws NumberFormatException
	 * 		When the input cannot be parsed.
	 */
	@SuppressWarnings("")
	@NotNull
	public static Number parse(@NotNull String input) {
		String text = input.trim().toUpperCase();
		Number value;
		if (text.indexOf('.') > 0) {
			value = parseDecimal(text);
		} else {
			if (text.endsWith("L") && text.startsWith("0X"))
				value = Long.parseLong(text.substring(2, text.indexOf("L")), 16);
			else if (text.endsWith("L"))
				value = Long.parseLong(text.substring(0, text.indexOf("L")));
			else if (text.startsWith("0X"))
				value = Integer.parseInt(text.substring(2), 16);
			else if (text.endsWith("F"))
				value = Float.parseFloat(text.substring(0, text.indexOf("F")));
			else if (text.endsWith("D") || text.contains("."))
				value = Double.parseDouble(text.substring(0, text.indexOf("D")));
			else
				value = Integer.parseInt(text);
		}
		return value;
	}

	/**
	 * @param text
	 * 		Text input that represents a decimal number.
	 *
	 * @return Decimal number, either {@link Float} or {@link Double}.
	 */
	@NotNull
	private static Number parseDecimal(@NotNull String text) {
		if (text.endsWith("F"))
			return Float.parseFloat(text.substring(0, text.indexOf("F")));
		else if (text.endsWith("D") || text.contains("."))
			return Double.parseDouble(text.substring(0, text.indexOf("D")));
		else
			return Double.parseDouble(text);
	}

	/**
	 * Compare two numeric values, regardless of their type.
	 *
	 * @param right
	 * 		First value.
	 * @param left
	 * 		Second value.
	 *
	 * @return Comparison of {@code X.compare(left, right)}
	 */
	public static int cmp(@NotNull Number left, @NotNull Number right) {
		// Check for widest types first, go down the type list to narrower types until reaching int.
		if (right instanceof Double || left instanceof Double) {
			return Double.compare(left.doubleValue(), right.doubleValue());
		} else if (right instanceof Float || left instanceof Float) {
			return Float.compare(left.floatValue(), right.floatValue());
		} else if (right instanceof Long || left instanceof Long) {
			return Long.compare(left.longValue(), right.longValue());
		} else {
			return Integer.compare(left.intValue(), right.intValue());
		}
	}

	/**
	 * @param first
	 * 		First value.
	 * @param second
	 * 		Second value.
	 *
	 * @return Difference value.
	 */
	@NotNull
	public static Number sub(@NotNull Number first, @NotNull Number second) {
		// Check for widest types first, go down the type list to narrower types until reaching int.
		if (second instanceof Double || first instanceof Double) {
			return first.doubleValue() - second.doubleValue();
		} else if (second instanceof Float || first instanceof Float) {
			return first.floatValue() - second.floatValue();
		} else if (second instanceof Long || first instanceof Long) {
			return first.longValue() - second.longValue();
		} else {
			return first.intValue() - second.intValue();
		}
	}

	/**
	 * @param first
	 * 		First value.
	 * @param second
	 * 		Second value.
	 *
	 * @return Sum value.
	 */
	@NotNull
	public static Number add(@NotNull Number first, @NotNull Number second) {
		// Check for widest types first, go down the type list to narrower types until reaching int.
		if (second instanceof Double || first instanceof Double) {
			return first.doubleValue() + second.doubleValue();
		} else if (second instanceof Float || first instanceof Float) {
			return first.floatValue() + second.floatValue();
		} else if (second instanceof Long || first instanceof Long) {
			return first.longValue() + second.longValue();
		} else {
			return first.intValue() + second.intValue();
		}
	}

	/**
	 * @param first
	 * 		First value.
	 * @param second
	 * 		Second value.
	 *
	 * @return Product value.
	 */
	@NotNull
	public static Number mul(@NotNull Number first, @NotNull Number second) {
		// Check for widest types first, go down the type list to narrower types until reaching int.
		if (second instanceof Double || first instanceof Double) {
			return first.doubleValue() * second.doubleValue();
		} else if (second instanceof Float || first instanceof Float) {
			return first.floatValue() * second.floatValue();
		} else if (second instanceof Long || first instanceof Long) {
			return first.longValue() * second.longValue();
		} else {
			return first.intValue() * second.intValue();
		}
	}

	/**
	 * @param first
	 * 		First value.
	 * @param second
	 * 		Second value.
	 *
	 * @return Divided value.
	 */
	@NotNull
	public static Number div(@NotNull Number first, @NotNull Number second) {
		// Check for widest types first, go down the type list to narrower types until reaching int.
		if (second instanceof Double || first instanceof Double) {
			return first.doubleValue() / second.doubleValue();
		} else if (second instanceof Float || first instanceof Float) {
			return first.floatValue() / second.floatValue();
		} else if (second instanceof Long || first instanceof Long) {
			return first.longValue() / second.longValue();
		} else {
			return first.intValue() / second.intValue();
		}
	}

	/**
	 * @param first
	 * 		First value.
	 * @param second
	 * 		Second value.
	 *
	 * @return Remainder value.
	 */
	@NotNull
	public static Number rem(@NotNull Number first, @NotNull Number second) {
		// Check for widest types first, go down the type list to narrower types until reaching int.
		if (second instanceof Double || first instanceof Double) {
			return first.doubleValue() % second.doubleValue();
		} else if (second instanceof Float || first instanceof Float) {
			return first.floatValue() % second.floatValue();
		} else if (second instanceof Long || first instanceof Long) {
			return first.longValue() % second.longValue();
		} else {
			return first.intValue() % second.intValue();
		}
	}

	/**
	 * @param first
	 * 		First value.
	 * @param second
	 * 		Second value.
	 *
	 * @return Value where matching bits remain.
	 */
	@NotNull
	public static Number and(@NotNull Number first, @NotNull Number second) {
		// Check for widest types first, go down the type list to narrower types until reaching int.
		if (second instanceof Long || first instanceof Long) {
			return first.longValue() & second.longValue();
		} else {
			return first.intValue() & second.intValue();
		}
	}

	/**
	 * @param first
	 * 		First value.
	 * @param second
	 * 		Second value.
	 *
	 * @return Value where all active bits remain.
	 */
	@NotNull
	public static Number or(@NotNull Number first, @NotNull Number second) {
		// Check for widest types first, go down the type list to narrower types until reaching int.
		if (second instanceof Long || first instanceof Long) {
			return first.longValue() | second.longValue();
		} else {
			return first.intValue() | second.intValue();
		}
	}

	/**
	 * @param first
	 * 		First value.
	 * @param second
	 * 		Second value.
	 *
	 * @return Value where non-matching bits remain.
	 */
	@NotNull
	public static Number xor(@NotNull Number first, @NotNull Number second) {
		// Check for widest types first, go down the type list to narrower types until reaching int.
		if (second instanceof Long || first instanceof Long) {
			return first.longValue() ^ second.longValue();
		} else {
			return first.intValue() ^ second.intValue();
		}
	}

	/**
	 * @param value
	 * 		Numeric value.
	 *
	 * @return Negated value.
	 */
	@NotNull
	public static Number neg(@NotNull Number value) {
		// Check for widest types first, go down the type list to narrower types until reaching int.
		if (value instanceof Double) {
			return -value.doubleValue();
		} else if (value instanceof Float) {
			return -value.floatValue();
		} else if (value instanceof Long) {
			return -value.longValue();
		} else {
			return -value.intValue();
		}
	}

	/**
	 * @param value
	 * 		Numeric value.
	 * @param shift
	 * 		Value to shift by.
	 *
	 * @return Shifted value.
	 */
	@NotNull
	public static Number shiftLeft(@NotNull Number value, @NotNull Number shift) {
		// Check for widest types first, go down the type list to narrower types until reaching int.
		if (value instanceof Long) {
			return value.longValue() << shift.longValue();
		} else {
			return value.intValue() << shift.intValue();
		}
	}

	/**
	 * @param value
	 * 		Numeric value.
	 * @param shift
	 * 		Value to shift by.
	 *
	 * @return Shifted value.
	 */
	@NotNull
	public static Number shiftRight(@NotNull Number value, @NotNull Number shift) {
		// Check for widest types first, go down the type list to narrower types until reaching int.
		if (value instanceof Long) {
			return value.longValue() >> shift.longValue();
		} else {
			return value.intValue() >> shift.intValue();
		}
	}

	/**
	 * @param value
	 * 		Numeric value.
	 * @param shift
	 * 		Value to shift by.
	 *
	 * @return Shifted value.
	 */
	@NotNull
	public static Number shiftRightU(@NotNull Number value, @NotNull Number shift) {
		// Check for widest types first, go down the type list to narrower types until reaching int.
		if (value instanceof Long) {
			return value.longValue() >>> shift.longValue();
		} else {
			return value.intValue() >>> shift.intValue();
		}
	}

	/**
	 * Fast {@link Math#pow(double, double)} for {@code int}.
	 *
	 * @param base
	 * 		Base value.
	 * @param exp
	 * 		Exponent power.
	 *
	 * @return {@code base^exp}.
	 */
	public static int intPow(int base, int exp) {
		if (exp < 0) throw new IllegalArgumentException("Exponent must be positive");
		int result = 1;
		while (true) {
			if ((exp & 1) != 0) result *= base;
			if ((exp >>= 1) == 0) break;
			base *= base;
		}
		return result;
	}

	/**
	 * @param value
	 * 		Base value.
	 * @param min
	 * 		Clamp min.
	 * @param max
	 * 		Clamp max.
	 *
	 * @return Value, or min if value below min, or max if value above max.
	 */
	public static int intClamp(int value, int min, int max) {
		if (value > max)
			return max;
		if (value < min)
			return min;
		return value;
	}

	/**
	 * @param value
	 * 		Base value.
	 * @param min
	 * 		Clamp min.
	 * @param max
	 * 		Clamp max.
	 *
	 * @return Value, or min if value below min, or max if value above max.
	 */
	public static double doubleClamp(double value, double min, double max) {
		if (value > max)
			return max;
		if (value < min)
			return min;
		return value;
	}

	/**
	 * @param i
	 * 		Some value.
	 *
	 * @return {@code i != 0}
	 */
	public static boolean isNonZero(int i) {
		return i != 0;
	}

	/**
	 * @param i
	 * 		Some value.
	 *
	 * @return {@code i == 0}
	 */
	public static boolean isZero(int i) {
		return i == 0;
	}
}