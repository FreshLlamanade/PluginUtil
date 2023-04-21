package me.monst.pluginutil.command;

import me.monst.pluginutil.command.exception.CommandExecutionException;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Represents an argument which may or may not have been specified by the command sender.
 * Helper methods are provided to deal with the possible presence or absence of a value.
 * @param <T> The type of the value.
 */
public class Argument<T> {
    
    /**
     * Common instance for empty arguments.
     */
    private static final Argument<?> EMPTY = new Argument<>();
    
    /**
     * Returns an empty argument instance. No value is present for this Argument.
     * @param <T> Type of the non-existent value.
     * @return An empty argument.
     */
    public static <T> Argument<T> empty() {
        @SuppressWarnings("unchecked")
        Argument<T> t = (Argument<T>) EMPTY;
        return t;
    }
    
    /**
     * If non-null, the value; if null, indicates no value is present.
     */
    private final T value;
    
    /**
     * Constructs an empty instance.
     * @implNote Generally only one empty instance, {@link #EMPTY}, should exist per VM.
     */
    private Argument() {
        this.value = null;
    }
    
    /**
     * Constructs an instance with the specified present non-null value.
     * @param value The value to be present, which must be non-null.
     * @throws NullPointerException If value is null.
     */
    private Argument(T value) {
        this.value = Objects.requireNonNull(value);
    }
    
    /**
     * Returns an argument with the specified value, if non-null, otherwise returns an empty argument.
     * @param value The value to be present, which may be null.
     * @param <T> The type of the value.
     * @return An argument with the value present if non-null, otherwise an empty argument.
     */
    public static <T> Argument<T> of(T value) {
        return value == null ? empty() : new Argument<>(value);
    }
    
    /**
     * Returns the value if present, otherwise throws {@code NoSuchElementException}.
     * @return The non-null value held by this argument.
     * @throws NoSuchElementException If there is no value present.
     */
    public T get() {
        if (value == null)
            throw new NoSuchElementException("No value present");
        return value;
    }
    
    /**
     * Return true if there is a value present, otherwise false.
     * @return true if there is a value present, otherwise false.
     */
    public boolean isPresent() {
        return value != null;
    }
    
    /**
     * If a value is present, invoke the specified consumer with the value, otherwise do nothing.
     * @param consumer block to be executed if a value is present.
     * @throws NullPointerException If value is present and {@code consumer} is null.
     */
    public void ifPresent(Consumer<T> consumer) {
        if (value != null)
            consumer.accept(value);
    }
    
    /**
     * If a value is present, and the value matches the given predicate, return an argument describing the value,
     * otherwise return an empty argument.
     * @param predicate a predicate to apply to the value, if present.
     * @return an argument describing the value of this argument, if a value is present and the value matches the given
     * predicate, otherwise an empty argument.
     * @throws NullPointerException If the predicate is null.
     */
    public Argument<T> filter(Predicate<T> predicate) {
        if (value == null || predicate.test(value))
            return this;
        return empty();
    }
    
    /**
     * If a value is present, assert that the value matches the given predicate, otherwise do nothing.
     * @param predicate a predicate to apply to the value, if present.
     *                  If the predicate returns false, a {@link CommandExecutionException} is thrown.
     * @param errorFormatter a function which takes the value and returns an error message.
     * @return this argument.
     * @throws CommandExecutionException if the value is present and the value does not match the given predicate.
     */
    public Argument<T> assertTrue(Predicate<T> predicate, Function<T, String> errorFormatter)
        throws CommandExecutionException {
        if (value != null && !predicate.test(value))
            throw new CommandExecutionException(errorFormatter.apply(value));
        return this;
    }
    
    /**
     * If a value is present, assert that the value does not match the given predicate, otherwise do nothing.
     * @param predicate a predicate to apply to the value, if present.
     *                  If the predicate returns true, a {@link CommandExecutionException} is thrown.
     * @param errorFormatter a function which takes the value and returns an error message.
     * @return this argument.
     * @throws CommandExecutionException if the value is present and the value matches the given predicate.
     */
    public Argument<T> assertFalse(Predicate<T> predicate, Function<T, String> errorFormatter)
        throws CommandExecutionException {
        if (value != null && predicate.test(value))
            throw new CommandExecutionException(errorFormatter.apply(value));
        return this;
    }
    
    /**
     * A function that can throw a CommandExecutionException.
     * @param <T> The type of the argument.
     * @param <R> The type of the result.
     * @param <X> The type of the exception.
     */
    public interface ThrowingFunction<T, R, X extends Exception> {
        R apply(T t) throws X;
    }
    
    /**
     * If a value is present, apply the provided mapping function to it, and if the result is non-null, return an argument
     * describing the result. Otherwise, return an empty argument.
     * @param mapper a mapping function to apply to the value, if present.
     * @return an argument describing the result of applying a mapping function to the value of this argument, if a value
     * is present, otherwise an empty argument.
     * @param <U> The type of the result of the mapping function.
     * @param <X> The type of the exception.
     * @throws X If the mapper throws an exception.
     */
    public <U, X extends Exception> Argument<U> map(
            ThrowingFunction<T, U, ? extends X> mapper)
            throws X {
        if (value == null)
            return empty();
        return Argument.of(mapper.apply(value));
    }
    
    /**
     * If a value is present, apply the provided {@code Optional}-bearing
     * mapping function to it, return that result, otherwise return an empty
     * {@code Argument}. This method is similar to {@link #map(ThrowingFunction)},
     * but the provided mapper is one whose result is already an {@code Optional},
     * and if invoked, {@code flatMap} will unwrap that {@code Optional} into
     * a value.
     *
     * @param <U> The type parameter to the {@code Argument} returned by
     * @param mapper a mapping function to apply to the value, if present
     * @return the result of applying an {@code Optional}-bearing mapping
     * function to the value of this {@code Argument}, if a value is present,
     * otherwise an empty {@code Argument}
     * @throws NullPointerException if the mapping function is null or returns
     * a null result
     * @param <X> The type of the exception.
     * @throws X If the mapper throws an exception.
     */
    public <U, X extends Exception> Argument<U> flatMap(
            ThrowingFunction<T, Optional<U>, ? extends X> mapper)
            throws X {
        if (value == null)
            return empty();
        return mapper.apply(value).map(Argument::of).orElse(empty());
    }
    
    /**
     * Return the value if present, otherwise return {@code other}.
     * @param other the value to be returned if there is no value present, may be null.
     * @return the value, if present, otherwise {@code other}.
     */
    public T orElse(T other) {
        return value != null ? value : other;
    }
    
    /**
     * Return the value if present, otherwise invoke {@code other} and return the result of that invocation.
     * @param other a {@code Supplier} whose result is returned if no value is present.
     * @return the value if present otherwise the result of {@code other.get()}
     * @throws NullPointerException if value is not present and {@code other} is null.
     */
    public T orElseGet(Supplier<T> other) {
        return value != null ? value : other.get();
    }
    
    /**
     * Return the contained value, if present, otherwise throw an exception to be created by the provided supplier.
     * @param supplier The supplier which will return the exception to be thrown.
     * @param <X> Type of the exception to be thrown.
     * @return the present value.
     * @throws X if there is no value present.
     * @throws NullPointerException if no value is present and {@code supplier} is null.
     */
    public <X extends CommandExecutionException> T orElseThrow(Supplier<? extends X> supplier) throws X {
        if (value != null)
            return value;
        throw supplier.get();
    }
    
    /**
     * Return the contained value, if present, otherwise throw a CommandExecutionException with the provided message.
     * @param errorMessage The error message to be used in the exception.
     * @return the present value.
     * @throws CommandExecutionException if there is no value present.
     */
    public T expect(String errorMessage) throws CommandExecutionException {
        if (value == null)
            throw new CommandExecutionException(errorMessage);
        return value;
    }
    
    /**
     * Return the contained value, if present, otherwise throw a CommandExecutionException with the message given by
     * the provided {@link Supplier<String>}.
     * @param errorMessage The supplier of the error message to be used in the exception.
     * @return the present value.
     * @throws CommandExecutionException if there is no value present.
     */
    public T expect(Supplier<String> errorMessage) throws CommandExecutionException {
        if (value == null)
            throw new CommandExecutionException(errorMessage.get());
        return value;
    }
    
    /**
     * Indicates whether some other object is "equal to" this Argument. The other object is considered equal if:
     * <ul>
     *     <li>it is also an Argument and;</li>
     *     <li>both instances have no value present or;</li>
     *     <li>the present values are "equal to" each other via {@link Object#equals(Object)}.</li>
     * </ul>
     * @param obj an object to be tested for equality.
     * @return {@code true} if the other object is "equal to" this object otherwise {@code false}.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Argument<?> that = (Argument<?>) obj;
        return Objects.equals(value, that.value);
    }
    
    /**
     * Returns the hash code value of the present value, if any, or 0 (zero) if no value is present.
     * @return hash code value of the present value or 0 if no value is present.
     */
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
    
    /**
     * Returns a non-empty string representation of this Argument suitable for debugging.
     * @return the string representation of this instance.
     */
    @Override
    public String toString() {
        return value != null
                ? String.format("Argument[%s]", value)
                : "Argument.empty";
    }
    
}
