package endtoend.tests;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;


/**
 * A hamcrest matcher for comparing in order arrays.
 * 
 * @param <T>
 *            the type of the elements of the sequence.
 */
public class ArrayInOrderMatcher<T> extends TypeSafeMatcher<Iterable<T>> {
    /***/
    private final T[] arr;


    /**
     * @param arr
     */
    public ArrayInOrderMatcher(final T... arr) {
        this.arr = arr;
    }


    /**
     * @see org.hamcrest.SelfDescribing#describeTo(org.hamcrest.Description)
     */
    @Override
    public void describeTo(final Description desc) {
        desc.appendValue(arr);
    }


    /**
     * @see org.junit.matchers.TypeSafeMatcher#matchesSafely(java.lang.Object)
     */
    @Override
    public boolean matchesSafely(final Iterable<T> iter) {
        int i = 0;

        for (final T t : iter)
            if (!arr[i++].equals(t))
                return false;

        return true;
    }


    /**
     * @param arr
     * @return an {@link ArrayInOrderMatcher}.
     */
    public static <T> ArrayInOrderMatcher<T> contains(final T... arr) {
        return new ArrayInOrderMatcher<T>(arr);
    }
}
