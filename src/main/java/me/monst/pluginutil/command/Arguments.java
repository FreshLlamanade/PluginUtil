package me.monst.pluginutil.command;

import java.util.List;
import java.util.stream.Stream;

/**
 * Represents the arguments of a command, entered by the command sender.
 * Because the command sender can enter any number of arguments and an argument required
 * by a command may or may not be present, this class provides access to the argument strings
 * in the form of {@link Argument} objects, which can be used to handle arguments elegantly
 * regardless of whether they are present or not.
 * <p>
 * Methods are provided to access arguments by index or n-position, to get a sub-list of arguments, to
 * convert the arguments to a {@link List} or a {@link Stream} and to check whether the
 * arguments are empty.
 */
public interface Arguments extends Iterable<String> {
    
    /**
     * Returns whether the arguments are empty.
     * @return whether the arguments are empty.
     */
    boolean isEmpty();
    
    /**
     * Returns the number of arguments.
     * @return the number of arguments.
     */
    int size();
    
    /**
     * Returns the argument at the specified index.
     * @param index the index of the argument.
     * @return the argument at the specified index.
     */
    Argument<String> get(int index);
    
    /**
     * Returns the first argument.
     * @return the first argument.
     */
    default Argument<String> first() {
        return get(0);
    }
    
    /**
     * Returns the second argument.
     * @return the second argument.
     */
    default Argument<String> second() {
        return get(1);
    }
    
    /**
     * Returns the third argument.
     * @return the third argument.
     */
    default Argument<String> third() {
        return get(2);
    }
    
    /**
     * Returns the fourth argument.
     * @return the fourth argument.
     */
    default Argument<String> fourth() {
        return get(3);
    }
    
    /**
     * Returns the fifth argument.
     * @return the fifth argument.
     */
    default Argument<String> fifth() {
        return get(4);
    }
    
    /**
     * Returns the n-th argument.
     * This is equivalent to {@code get(n - 1)}.
     * @param n the n-th argument.
     * @return the n-th argument.
     */
    default Argument<String> nth(int n) {
        return get(n - 1);
    }
    
    /**
     * Returns the last argument.
     * @return the last argument.
     */
    default Argument<String> last() {
        return get(size() - 1);
    }
    
    /**
     * Returns a sub-list of arguments starting at the specified inclusive index.
     * @param fromIndex the index to start at.
     * @return a sub-list of arguments starting at the specified inclusive index.
     */
    default Arguments from(int fromIndex) {
        return between(fromIndex, size());
    }
    
    /**
     * Returns a sub-list of arguments ending at the specified exclusive index.
     * @param toIndex the index to end at.
     * @return a sub-list of arguments ending at the specified exclusive index.
     */
    default Arguments to(int toIndex) {
        return between(0, toIndex);
    }
    
    /**
     * Returns a sub-list of arguments between the specified inclusive and exclusive indices.
     * @param fromIndex the index to start at.
     * @param toIndex the index to end at.
     * @return a sub-list of arguments between the specified inclusive and exclusive indices.
     */
    Arguments between(int fromIndex, int toIndex);
    
    /**
     * Returns the arguments as a {@link List}.
     * @return the arguments as a {@link List}.
     */
    List<String> asList();
    
    /**
     * Returns the arguments as a {@link Stream}.
     * @return the arguments as a {@link Stream}.
     */
    Stream<String> stream();
    
    String join(CharSequence delimiter);
    
    String join(CharSequence delimiter, CharSequence prefix, CharSequence suffix);
    
}
