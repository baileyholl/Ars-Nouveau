// SPDX-FileCopyrightText: 2023 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.hollingsworth.arsnouveau.common.book.client;

import com.hollingsworth.arsnouveau.common.book.Book;
import net.minecraft.network.chat.Component;

import java.util.List;

public interface BookScreenWithButtons {
    void setTooltip(List<Component> tooltip);

    Book getBook();
}
