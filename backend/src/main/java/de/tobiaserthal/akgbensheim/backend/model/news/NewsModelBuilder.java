package de.tobiaserthal.akgbensheim.backend.model.news;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import de.tobiaserthal.akgbensheim.backend.model.base.BaseModelBuilder;


public interface NewsModelBuilder<T extends NewsModelBuilder<?>> extends BaseModelBuilder<T> {
    /**
     * The headline of this news post.
     * Cannot be {@code null}.
     */
    T putTitle(@NonNull String title);

    /**
     * The short summary of the article itself.
     * Cannot be {@code null}.
     */
    T putSnippet(@NonNull String snippet);

    /**
     * The article body text.
     * Cannot be {@code null}.
     */
    T putArticle(@NonNull String article);

    /**
     * The url the article was originally fetched from
     * Cannot be {@code null}.
     */
    T putArticleUrl(@NonNull String articleUrl);

    /**
     * The url for the header image.
     * Can be {@code null}.
     */
    T putImageUrl(@Nullable String imageUrl);

    /**
     * The header image description.
     * Can be {@code null}.
     */
    T putImageDesc(@Nullable String imageDesc);

    /**
     * Whether this news post was bookmarked by the user.
     */
    T putBookmarked(boolean bookmarked);
}
