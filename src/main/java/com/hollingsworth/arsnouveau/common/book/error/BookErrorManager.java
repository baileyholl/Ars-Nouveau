/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.hollingsworth.arsnouveau.common.book.error;

import net.minecraft.resources.ResourceLocation;
import org.slf4j.helpers.MessageFormatter;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class BookErrorManager {
    private static final BookErrorManager instance = new BookErrorManager();

    private final ConcurrentMap<ResourceLocation, BookErrorHolder> booksErrors = new ConcurrentHashMap<>();
    private final BookErrorContextHelper contextHelper = new BookErrorContextHelper();
    private ResourceLocation currentBookId;
    private String currentContext;

    private BookErrorManager() {

    }

    public static BookErrorManager get() {
        return instance;
    }

    public void reset() {
        this.contextHelper.reset();
        this.booksErrors.clear();
        this.currentBookId = null;
        this.currentContext = null;
    }

    public BookErrorContextHelper getContextHelper() {
        return this.contextHelper;
    }

    public BookErrorHolder getErrors(ResourceLocation bookId) {
        return this.booksErrors.get(bookId);
    }

    public boolean hasErrors(ResourceLocation book) {
        var holder = this.booksErrors.get(book);
        return holder != null && !holder.getErrors().isEmpty();
    }

    public void error(String message) {
        this.error(new BookErrorInfo(message, null, this.currentContext));
    }

    public void error(String message, Exception exception) {
        this.error(new BookErrorInfo(message, exception, this.currentContext));
    }

    public void error(BookErrorInfo error) {
        this.error(this.currentBookId, error);
    }

    public void error(ResourceLocation book, String message) {
        this.error(book, new BookErrorInfo(message, null, this.currentContext));
    }

    public void error(ResourceLocation book, String message, Exception exception) {
        this.error(book, new BookErrorInfo(message, exception, this.currentContext));
    }

    public void error(ResourceLocation book, BookErrorInfo error) {
        if (book == null) {
//            Modonomicon.LOGGER.error("BookErrorManager.error() called with null book id with error: {}", error);
            return;
        }

        var holder = this.booksErrors.get(book);
        if (holder == null) {
            holder = new BookErrorHolder();
            this.booksErrors.put(book, holder);
        }
        holder.addError(error);

//        Modonomicon.LOGGER.warn("BookErrorManager.error() called for book: {} with error: {}", book, error);
    }

    /**
     * Gets the book id of the book currently being loaded. Used to add errors that happen where we don't have a direct
     * ref to the book.
     */
    public ResourceLocation getCurrentBookId() {
        return this.currentBookId;
    }

    /**
     * Sets the book id of the book currently being loaded. Used to add errors that happen where we don't have a direct
     * ref to the book.
     */
    public void setCurrentBookId(ResourceLocation id) {
        this.currentBookId = id;
    }

    /**
     * Set the context to add to all errors logged after this. Set to null to remove context. Uses
     * {@link MessageFormatter#format(String, Object)} to format the context.
     */
    public void setContext(String context, Object... args) {
        if (context != null) {
            this.currentContext = MessageFormatter.arrayFormat(context, args).getMessage();
        } else {
            this.currentContext = null;
        }
    }

    public String getContext() {
        if (this.currentContext != null) {
            return this.currentContext;
        }

        return this.contextHelper.toString();
    }
}
