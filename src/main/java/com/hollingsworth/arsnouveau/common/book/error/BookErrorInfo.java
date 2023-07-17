/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.hollingsworth.arsnouveau.common.book.error;

public class BookErrorInfo {
    private final String errorMessage;
    private final Exception exception;
    private final String context;

    public BookErrorInfo(String errorMessage, Exception exception, String context) {
        this.errorMessage = errorMessage;
        this.exception = exception;
        this.context = context;
    }

    @Override
    public String toString() {
        var errorMessage = this.errorMessage == null ? "" : this.errorMessage;
        var context = this.context == null ? "" : this.context;
        var exception = this.exception == null ? "" : this.exception.toString();
        return "BookErrorInfo{ " +
                "\nerrorMessage='" + errorMessage + "'" +
                ", \ncontext='" + context + "'" +
                ", \nexception='" + exception + "'" +
                "\n}";
    }
}
