package name.ulbricht.iconcreator.gui.util;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.function.Consumer;

import javax.swing.SwingWorker;

/// Defines an interface for running tasks that can produce intermediate results
/// and handle success or failure.
@FunctionalInterface
public interface TaskRunner {

    /// Define an functional interface for a task that can either run asynchronously
    /// or synchronously. The task can publish intermediate results using the
    /// provided [Consumer], and it can throw exceptions.
    /// 
    /// @param <T> The type of the final result produced by the task.
    /// @param <V> The type of intermediate results published by the task.
    @FunctionalInterface
    interface Task<T, V> {

        T run(Consumer<V> publisher) throws Exception;
    }

    /// Runs a task asynchronously, allowing it to publish intermediate results and
    /// handle success or failure.
    /// 
    /// @param <T>          The type of the final result produced by the task.
    /// @param <V>          The type of intermediate results published by the task.
    /// @param task         The task to be executed.
    /// @param intermediate A consumer that handles intermediate results published by
    ///                     the task.
    /// @param onSuccess    A consumer that handles the final result when the task
    ///                     completes successfully.
    /// @param onError      A consumer that handles any exceptions that occur during
    ///                     the execution of the task.
    <T, V> void run(Task<T, V> task, Consumer<List<V>> intermediate, Consumer<T> onSuccess,
            Consumer<Exception> onError);

    /// Runs a task asynchronously, allowing it to publish intermediate results and
    /// handle success or failure. This method fulfills the signature of the
    /// [TaskRunner] interface. Internally, a [SwingWorker] is used.
    /// 
    /// @param <T>          The type of the final result produced by the task.
    /// @param <V>          The type of intermediate results published by the task.
    /// @param task         The task to be executed.
    /// @param intermediate A consumer that handles intermediate results published by
    ///                     the task.
    /// @param onSuccess    A consumer that handles the final result when the task
    ///                     completes successfully.
    /// @param onError      A consumer that handles any exceptions that occur during
    ///                     the execution of the task.
    static <T, V> void async(final Task<T, V> task, final Consumer<List<V>> intermediate,
            final Consumer<T> onSuccess, final Consumer<Exception> onError) {

        requireNonNull(task);
        requireNonNull(intermediate);
        requireNonNull(onSuccess);
        requireNonNull(onError);

        // Use a SwingWorker to run the task in a background thread and publish
        // intermediate results to the event dispatch thread
        final var worker = new SwingWorker<T, V>() {

            @Override
            protected T doInBackground() throws Exception {
                // Run the task and use the SwingWorker to publish intermediate results
                return task.run(this::publish);
            }

            @Override
            protected void process(List<V> chunks) {
                // Forward the handling of intermediate results to the provided consumer
                intermediate.accept(chunks);
            }

            @Override
            protected void done() {
                try {
                    // Handle the successful completion of the task
                    onSuccess.accept(get());
                } catch (final Exception ex) {
                    // Handle any exceptions that occur during the execution of the task
                    onError.accept(ex);
                }
            }
        };

        // Start the worker asynchronously
        worker.execute();
    }

    /// Runs a task synchronously, allowing it to publish intermediate results and
    /// handle success or failure. It is intended to be used in tests. This method
    /// fulfills the signature of the [TaskRunner] interface. Internally, all
    /// executions are performed before this method returns.
    /// 
    /// @param <T>          The type of the final result produced by the task.
    /// @param <V>          The type of intermediate results published by the task.
    /// @param task         The task to be executed.
    /// @param intermediate A consumer that handles intermediate results published by
    ///                     the task.
    /// @param onSuccess    A consumer that handles the final result when the task
    ///                     completes successfully.
    /// @param onError      A consumer that handles any exceptions that occur during
    ///                     the execution of the task.
    static <T, V> void sync(final Task<T, V> task, final Consumer<List<V>> intermediate,
            final Consumer<T> onSuccess, final Consumer<Exception> onError) {

        requireNonNull(task);
        requireNonNull(intermediate);
        requireNonNull(onSuccess);
        requireNonNull(onError);

        try {
            // Deliver immediately (mimic SwingWorker.process)
            final Consumer<V> publisher = v -> intermediate.accept(List.of(v));

            // Run the task and use the provided consumer to publish intermediate results
            T result = task.run(publisher);

            // Handle the successful completion of the task
            onSuccess.accept(result);

        } catch (final Exception ex) {
            // Handle any exceptions that occur during the execution of the task
            onError.accept(ex);
        }
    }
}
