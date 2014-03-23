package blogspot.software_and_algorithms.stern_library.int_data_structure;

/* Copyright (c) 2012 Kevin L. Stern
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUint WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUint NOint LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENint SHALL THE
 * AUTHORS OR COPYRIGHint HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORint OR OTHERWISE, ARISING FROM,
 * OUint OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

/**
 * An <em>interval</em> is the subset of elements which fall between (with
 * respect to a total order) two endpoint elements of a set. An interval that
 * contains its endpoints is <em>closed</em>, an interval that contains one of
 * its endpoints but not the other is <em>half open</em> and an interval that
 * does not contain either of its endpoints is <em>open</em>. This class
 * encapsulates the concept of an interval and uses a class's natural order.
 * 
 * @author Kevin L. Stern
 */
public class IntervalInt implements
		Comparable<IntervalInt> {
	private int low, high;
	private boolean isClosedOnLow, isClosedOnHigh;
	private int hashCode = 0;
	public int value;
	/**
	 * Construct a new instance with the specified low and high endpoints.
	 * 
	 * @param low
	 *            the low endpoint.
	 * @param isClosedOnLow
	 *            true if this interval contains its low endpoint, false
	 *            otherwise.
	 * @param high
	 *            the high endpoint.
	 * @param isClosedOnHigh
	 *            true if this interval contains its high endpoint, false
	 *            otherwise.
	 */
	public IntervalInt(int low, boolean isClosedOnLow, int high, boolean isClosedOnHigh) {
		/*if (low == null) {
			throw new NullPointerException("low endpoint is null");
		} else if (high == null) {
			throw new NullPointerException("high endpoint is null");
		}*/
		this.low = low;
		this.isClosedOnLow = isClosedOnLow;
		this.high = high;
		this.isClosedOnHigh = isClosedOnHigh;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compareTo(IntervalInt o) {
		int result = low - o.low;
		if (result == 0) {
			if (isClosedOnLow != o.isClosedOnLow) {
				result = isClosedOnLow ? -1 : 1;
			} else {
				result = high - o.high;
				if (result == 0) {
					if (isClosedOnHigh != o.isClosedOnHigh) {
						result = isClosedOnHigh ? -1 : 1;
					}
				}
			}
		}
		return result;
	}

	/**
	 * Test whether or not this interval contains the specified interval. An
	 * interval is contained by another precisely when all of its values are
	 * contained by the other.
	 * 
	 * @param interval
	 *            the query interval, non-null.
	 * @return true if this interval contains the specified interval, false
	 *         otherwise.
	 */
	public boolean contains(IntervalInt interval) {
		boolean lowIsLowerBound = low == interval.low
				&& (isClosedOnLow || !interval.isClosedOnLow)
				|| low - interval.low < 0;
		boolean highIsUpperBound = high == interval.high
				&& (isClosedOnHigh || !interval.isClosedOnHigh)
				|| high - interval.high > 0;
		return lowIsLowerBound && highIsUpperBound;
	}

	/**
	 * Test whether or not this interval contains the specified value.
	 * 
	 * @param value
	 *            the query value, non-null.
	 * @return true if this interval contains the specified value, false
	 *         otherwise.
	 */
	public boolean contains(int value) {
		return value == low && isClosedOnLow || value == high
				&& isClosedOnHigh || low - value < 0
				&& value - high < 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IntervalInt other = (IntervalInt) obj;
		/*if (high == null) {
			if (other.high != null)
				return false;
		} else */
		if (!(high == other.high))
			return false;
		if (isClosedOnHigh != other.isClosedOnHigh)
			return false;
		/*if (low == null) {
			if (other.low != null)
				return false;
		} else */
		if (!(low == other.low))
			return false;
		if (isClosedOnLow != other.isClosedOnLow)
			return false;
		return true;
	}

	/**
	 * Get the high endpoint.
	 * 
	 * @return the high endpoint.
	 */
	public int getHigh() {
		return high;
	}

	/**
	 * Get the low endpoint.
	 * 
	 * @return the low endpoint.
	 */
	public int getLow() {
		return low;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		if (hashCode == 0) {
			final int prime = 31;
			int result = 1;
			result = prime * result + (
					//(high == null) ? 0 : 
						hashCode(high)
						);
			result = prime * result + (isClosedOnHigh ? 1231 : 1237);
			result = prime * result + (
					//(low == null) ? 0 : 
						hashCode(low)
							);
			result = prime * result + (isClosedOnLow ? 1231 : 1237);
			hashCode = result;
		}
		return hashCode;
	}

	private int hashCode(int i) {
		return i;
	}

	/**
	 * 
	 * @return true if this interval is closed at its high endpoint, false
	 *         otherwise.
	 */
	public boolean isClosedOnHigh() {
		return isClosedOnHigh;
	}

	/**
	 * 
	 * @return true if the interval is closed at its low endpoint, false
	 *         otherwise.
	 */
	public boolean isClosedOnLow() {
		return isClosedOnLow;
	}

	/**
	 * Test whether or not this interval and the specified interval overlap. Two
	 * intervals overlap precisely when their intersection is non-empty.
	 * 
	 * @param interval
	 *            the query interval.
	 * @return true if this interval and the specified interval overlap, false
	 *         otherwise.
	 */
	public boolean overlaps(IntervalInt interval) {
		if (interval.isClosedOnLow && contains(interval.low) || isClosedOnLow
				&& interval.contains(low)) {
			return true;
		}
		if (!interval.isClosedOnLow && low - interval.low <= 0
				&& interval.low - high < 0 || !isClosedOnLow
				&& interval.low - low <= 0
				&& low - interval.high < 0) {
			return true;
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		String format;
		if (isClosedOnLow) {
			if (isClosedOnHigh) {
				format = "[%s, %s]";
			} else {
				format = "[%s, %s)";
			}
		} else {
			if (isClosedOnHigh) {
				format = "(%s, %s]";
			} else {
				format = "(%s, %s)";
			}
		}
		return String.format(format, low, high);
	}
}