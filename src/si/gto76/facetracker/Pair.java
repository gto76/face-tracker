package si.gto76.facetracker;

/**
 * A Pair class is simply a 2-tuple for use in this package. We might want to
 * think about adding something like this to a more central utility place, or
 * replace it by a common tuple class if one exists, or even rewrite the layout
 * classes using this Pair by a more dedicated data structure (so we don't have
 * to pass around generic signatures as is currently done, though at least the
 * construction is helped a bit by the {@link #of} factory method.
 *
 * @param <S> The type of the first value
 * @param <T> The type of the second value
 */
public class Pair<S,T> {
    private final S mFirst;
    private final T mSecond;
 
    // Use {@link Pair#of} factory instead since it infers generic types
    private Pair(S first, T second) {
        this.mFirst = first;
        this.mSecond = second;
    }
 
    /**
     * Return the first item in the pair
     *
     * @return the first item in the pair
     */
    public S getFirst() {
        return mFirst;
    }
 
    /**
     * Return the second item in the pair
     *
     * @return the second item in the pair
     */
    public T getSecond() {
        return mSecond;
    }
 
    /**
     * Constructs a new pair of the given two objects, inferring generic types.
     *
     * @param first the first item to store in the pair
     * @param second the second item to store in the pair
     * @param <S> the type of the first item
     * @param <T> the type of the second item
     * @return a new pair wrapping the two items
     */
    public static <S,T> Pair<S,T> of(S first, T second) {
        return new Pair<S,T>(first,second);
    }
 
    @Override
    public String toString() {
        return "Pair [first=" + mFirst + ", second=" + mSecond + "]";
    }
 
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((mFirst == null) ? 0 : mFirst.hashCode());
        result = prime * result + ((mSecond == null) ? 0 : mSecond.hashCode());
        return result;
    }
 
    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Pair other = (Pair) obj;
        if (mFirst == null) {
            if (other.mFirst != null)
                return false;
        } else if (!mFirst.equals(other.mFirst))
            return false;
        if (mSecond == null) {
            if (other.mSecond != null)
                return false;
        } else if (!mSecond.equals(other.mSecond))
            return false;
        return true;
    }
}