package de.tobiaserthal.akgbensheim.data.model.base;

public interface BaseModelBuilder<T extends BaseModelBuilder<?>> {

    /**
     * Set the unique identifier of this model, e.g. in a database
     * representation.
     * @param id The id of this model as a long.
     */
    T putId(long id);
}
