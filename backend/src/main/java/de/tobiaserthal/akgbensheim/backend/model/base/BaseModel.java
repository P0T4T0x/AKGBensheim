package de.tobiaserthal.akgbensheim.backend.model.base;

/**
 * @author tobiaserthal
 * A interface every model has to extend or implement.
 */
public interface BaseModel {

    /**
     * Get the unique identifier of this model, e.g. in a database
     * representation.
     * @return The id of this model as a long.
     */
    long getId();
}
