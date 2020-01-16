package com.gelerion.open.storage.api.ops;

import com.gelerion.open.storage.api.exceptions.StorageOperationException;

public class StorageOperations {

    public static <R> R exec(Exceptional<R> action) {
        try {
            return action.execute();
        }
        catch (StorageOperationException e) {
            throw e;
        }
        catch (Exception e) {
            throw new StorageOperationException("Something went wrong", e);
        }
    }

    public static OrElseLong execOpt(ExceptionalLong action) {
        return fallback -> {
            try {
                return action.execute();
            } catch (Exception e) {
                return fallback;
            }
        };
    }

    public static void run(VoidExceptional action) {
        try {
            action.execute();
        }
        catch (StorageOperationException e) {
            throw e;
        }
        catch (Exception e) {
            throw new StorageOperationException("Something went wrong", e);
        }
    }

    @FunctionalInterface
    public interface Exceptional<R> {
        R execute() throws Exception;
    }

    @FunctionalInterface
    public interface VoidExceptional {
        void execute() throws Exception;

        default void andThen(VoidExceptional after) throws Exception {
            execute();
            after.execute();
        }
    }


    //------------ Longs ------------
    @FunctionalInterface
    public interface ExceptionalLong {
        long execute() throws Exception;
    }

    @FunctionalInterface
    public interface OrElseLong {
        long orElse(long fallback);
    }
}
